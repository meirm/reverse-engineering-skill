package dev.rebuild.arena

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

private enum class Screen {
    MENU,
    PLAYING,
    PAUSED
}

private data class Target(var x: Float, var direction: Float, var health: Int = 3)
private data class Shot(var x: Float, var y: Float, val vx: Float, val vy: Float)

class ArenaView(context: Context) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var screen = Screen.MENU
    private var logicalHeight = 480f
    private var scale = 1f
    private var lastFrameMs = 0L

    private var playerX = 170f
    private var playerY = 0f
    private var velocityY = 0f
    private var aimX = 760f
    private var aimY = 260f
    private var movePointer = INVALID_POINTER
    private var firePointer = INVALID_POINTER
    private var jumpPointer = INVALID_POINTER
    private var aimPointer = INVALID_POINTER
    private var moveOriginX = 0f
    private var moveOriginY = 0f
    private var moveTouchX = 0f
    private var moveTouchY = 0f
    private var jumpQueued = false
    private var fireCooldown = 0f
    private var score = 0
    private var targets = mutableListOf<Target>()
    private val shots = mutableListOf<Shot>()

    init {
        isFocusable = true
        contentDescription = "Arena Trainer game"
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val heightAboveGround = groundY - playerY
        scale = width.coerceAtLeast(1) / LOGICAL_WIDTH
        logicalHeight = height / scale
        playerY = if (screen == Screen.PLAYING) {
            groundY - heightAboveGround.coerceAtLeast(PLAYER_RADIUS)
        } else {
            groundY - PLAYER_RADIUS
        }
        aimX = LOGICAL_WIDTH * 0.76f
        aimY = groundY - 80f
        clearPointers()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        val frameTime = SystemClock.uptimeMillis()
        val delta = if (lastFrameMs == 0L) 0f else
            ((frameTime - lastFrameMs) / 1000f).coerceIn(0f, MAX_FRAME_DELTA)
        lastFrameMs = frameTime

        canvas.save()
        canvas.scale(scale, scale)
        when (screen) {
            Screen.MENU -> drawMenu(canvas)
            Screen.PLAYING -> {
                updateGame(delta)
                drawArena(canvas)
            }
            Screen.PAUSED -> {
                drawArena(canvas)
                drawPausePanel(canvas)
            }
        }
        canvas.restore()

        if (screen == Screen.PLAYING) postInvalidateOnAnimation()
    }

    fun handleBack(): Boolean {
        when (screen) {
            Screen.MENU -> return false
            Screen.PLAYING -> screen = Screen.PAUSED
            Screen.PAUSED -> screen = Screen.PLAYING
        }
        lastFrameMs = 0L
        invalidate()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.actionIndex
                handlePointerDown(
                    event.getPointerId(index),
                    event.getX(index) / scale,
                    event.getY(index) / scale
                )
            }
            MotionEvent.ACTION_MOVE -> {
                for (index in 0 until event.pointerCount) {
                    handlePointerMove(
                        event.getPointerId(index),
                        event.getX(index) / scale,
                        event.getY(index) / scale
                    )
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                handlePointerUp(event.getPointerId(event.actionIndex))
                if (event.actionMasked == MotionEvent.ACTION_UP) performClick()
            }
            MotionEvent.ACTION_CANCEL -> clearPointers()
        }
        invalidate()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun handlePointerDown(id: Int, x: Float, y: Float) {
        when (screen) {
            Screen.MENU -> {
                if (playButton.contains(x, y, logicalHeight)) startGame()
            }
            Screen.PAUSED -> {
                if (resumeButton.contains(x, y, logicalHeight)) {
                    screen = Screen.PLAYING
                    lastFrameMs = 0L
                } else if (menuButton.contains(x, y, logicalHeight)) {
                    screen = Screen.MENU
                    lastFrameMs = 0L
                }
            }
            Screen.PLAYING -> {
                if (x > LOGICAL_WIDTH - 86f && y < 62f) {
                    screen = Screen.PAUSED
                } else if (x < LOGICAL_WIDTH * 0.45f && y > logicalHeight * 0.48f &&
                    movePointer == INVALID_POINTER
                ) {
                    movePointer = id
                    moveOriginX = x
                    moveOriginY = y
                    moveTouchX = x
                    moveTouchY = y
                } else if (fireButton.contains(x, y, logicalHeight) &&
                    firePointer == INVALID_POINTER
                ) {
                    firePointer = id
                } else if (jumpButton.contains(x, y, logicalHeight) &&
                    jumpPointer == INVALID_POINTER
                ) {
                    jumpPointer = id
                    jumpQueued = true
                } else if (x >= LOGICAL_WIDTH * 0.45f && aimPointer == INVALID_POINTER) {
                    aimPointer = id
                    aimX = x
                    aimY = y
                }
            }
        }
    }

    private fun handlePointerMove(id: Int, x: Float, y: Float) {
        when (id) {
            movePointer -> {
                moveTouchX = x
                moveTouchY = y
            }
            aimPointer -> {
                aimX = x
                aimY = y
            }
        }
    }

    private fun handlePointerUp(id: Int) {
        if (movePointer == id) movePointer = INVALID_POINTER
        if (firePointer == id) firePointer = INVALID_POINTER
        if (jumpPointer == id) jumpPointer = INVALID_POINTER
        if (aimPointer == id) aimPointer = INVALID_POINTER
    }

    private fun clearPointers() {
        movePointer = INVALID_POINTER
        firePointer = INVALID_POINTER
        jumpPointer = INVALID_POINTER
        aimPointer = INVALID_POINTER
        jumpQueued = false
    }

    private fun startGame() {
        screen = Screen.PLAYING
        score = 0
        playerX = 170f
        playerY = groundY - PLAYER_RADIUS
        velocityY = 0f
        shots.clear()
        targets = mutableListOf(
            Target(LOGICAL_WIDTH * 0.61f, 1f),
            Target(LOGICAL_WIDTH * 0.77f, -1f),
            Target(LOGICAL_WIDTH * 0.90f, 1f)
        )
        clearPointers()
        lastFrameMs = 0L
    }

    private fun updateGame(delta: Float) {
        if (delta <= 0f) return
        val ground = groundY
        val stickRadius = 54f
        val movement = if (movePointer == INVALID_POINTER) 0f
        else ((moveTouchX - moveOriginX) / stickRadius).coerceIn(-1f, 1f)
        playerX = (playerX + movement * PLAYER_SPEED * delta)
            .coerceIn(PLAYER_RADIUS + 8f, LOGICAL_WIDTH - PLAYER_RADIUS - 8f)

        if (jumpQueued && playerY >= ground - PLAYER_RADIUS - 1f) {
            velocityY = -JUMP_SPEED
        }
        jumpQueued = false
        velocityY = (velocityY + GRAVITY * delta).coerceAtMost(MAX_FALL_SPEED)
        playerY += velocityY * delta
        if (playerY > ground - PLAYER_RADIUS) {
            playerY = ground - PLAYER_RADIUS
            velocityY = 0f
        }

        for (target in targets) {
            target.x += target.direction * TARGET_SPEED * delta
            if (target.x < LOGICAL_WIDTH * 0.56f || target.x > LOGICAL_WIDTH * 0.94f) {
                target.direction *= -1f
            }
        }

        fireCooldown -= delta
        if (firePointer != INVALID_POINTER && fireCooldown <= 0f) {
            fireShot()
            fireCooldown = FIRE_INTERVAL
        }

        val iterator = shots.iterator()
        while (iterator.hasNext()) {
            val shot = iterator.next()
            shot.x += shot.vx * delta
            shot.y += shot.vy * delta
            if (shot.x < 0f || shot.x > LOGICAL_WIDTH || shot.y < 0f || shot.y > logicalHeight) {
                iterator.remove()
                continue
            }
            val hit = targets.firstOrNull {
                it.health > 0 && hypot(shot.x - it.x, shot.y - (ground - 35f)) < 30f
            }
            if (hit != null) {
                hit.health--
                iterator.remove()
                if (hit.health <= 0) {
                    score++
                    hit.health = 3
                    hit.x = (LOGICAL_WIDTH * 0.58f) +
                        (score % 4) * (LOGICAL_WIDTH * 0.09f)
                }
            }
        }
    }

    private fun fireShot() {
        val originX = playerX + 19f
        val originY = playerY - 4f
        val angle = atan2(aimY - originY, aimX - originX)
        shots += Shot(
            originX,
            originY,
            cos(angle) * SHOT_SPEED,
            sin(angle) * SHOT_SPEED
        )
    }

    private fun drawMenu(canvas: Canvas) {
        drawBackdrop(canvas)
        drawText(canvas, "ARENA TRAINER", LOGICAL_WIDTH / 2f, logicalHeight * 0.30f, 38f, COLOR_WHITE, bold = true)
        drawText(canvas, "OFFLINE TRAINING", LOGICAL_WIDTH / 2f, logicalHeight * 0.39f, 16f, COLOR_MUTED)
        drawButton(canvas, playButton.rect(logicalHeight), "START TRAINING", COLOR_CYAN)
        drawText(
            canvas,
            "Move, jump, aim and practise your shots",
            LOGICAL_WIDTH / 2f,
            logicalHeight * 0.77f,
            15f,
            COLOR_MUTED
        )
    }

    private fun drawArena(canvas: Canvas) {
        drawBackdrop(canvas)
        val ground = groundY

        paint.color = Color.rgb(59, 89, 86)
        canvas.drawRoundRect(RectF(95f, ground - 110f, 290f, ground - 98f), 5f, 5f, paint)
        canvas.drawRoundRect(
            RectF(395f, ground - 165f, 555f, ground - 153f),
            5f,
            5f,
            paint
        )
        paint.color = Color.rgb(70, 112, 90)
        canvas.drawRect(0f, ground, LOGICAL_WIDTH, logicalHeight, paint)
        paint.color = Color.rgb(113, 162, 103)
        canvas.drawRect(0f, ground, LOGICAL_WIDTH, ground + 5f, paint)

        targets.forEach { drawTarget(canvas, it, ground) }
        shots.forEach { shot ->
            paint.color = COLOR_YELLOW
            canvas.drawCircle(shot.x, shot.y, 4f, paint)
        }
        drawPlayer(canvas)
        drawHud(canvas)
        drawControls(canvas)
    }

    private fun drawBackdrop(canvas: Canvas) {
        canvas.drawColor(Color.rgb(15, 29, 48))
        paint.color = Color.rgb(29, 51, 69)
        val hill = android.graphics.Path().apply {
            moveTo(0f, logicalHeight * 0.68f)
            lineTo(150f, logicalHeight * 0.38f)
            lineTo(300f, logicalHeight * 0.67f)
            lineTo(520f, logicalHeight * 0.34f)
            lineTo(740f, logicalHeight * 0.67f)
            lineTo(900f, logicalHeight * 0.43f)
            lineTo(LOGICAL_WIDTH, logicalHeight * 0.61f)
            lineTo(LOGICAL_WIDTH, logicalHeight)
            lineTo(0f, logicalHeight)
            close()
        }
        canvas.drawPath(hill, paint)
        paint.color = Color.rgb(194, 217, 229)
        canvas.drawCircle(LOGICAL_WIDTH - 100f, 58f, 18f, paint)
        for (index in 0 until 14) {
            val x = (index * 83f + 31f) % LOGICAL_WIDTH
            val y = 35f + (index * 37f % max(45f, logicalHeight * 0.30f))
            paint.color = Color.argb(110 + index % 100, 199, 221, 232)
            canvas.drawCircle(x, y, 1.5f, paint)
        }
    }

    private fun drawPlayer(canvas: Canvas) {
        val bodyTop = playerY - PLAYER_RADIUS * 0.52f
        val angle = atan2(aimY - playerY, aimX - playerX)
        paint.color = COLOR_CYAN
        canvas.drawRoundRect(
            RectF(playerX - 12f, bodyTop, playerX + 12f, playerY + 19f),
            9f,
            9f,
            paint
        )
        paint.color = Color.rgb(237, 201, 151)
        canvas.drawCircle(playerX, playerY - PLAYER_RADIUS, 11f, paint)
        paint.color = Color.rgb(35, 47, 59)
        canvas.drawRoundRect(
            RectF(playerX - 19f, playerY + 12f, playerX - 4f, playerY + 27f),
            4f,
            4f,
            paint
        )
        canvas.drawRoundRect(
            RectF(playerX + 3f, playerY + 12f, playerX + 18f, playerY + 27f),
            4f,
            4f,
            paint
        )
        paint.color = Color.rgb(224, 231, 238)
        canvas.drawLine(
            playerX + 7f,
            playerY - 3f,
            playerX + 7f + cos(angle) * 30f,
            playerY - 3f + sin(angle) * 30f,
            paint
        )
    }

    private fun drawTarget(canvas: Canvas, target: Target, ground: Float) {
        val y = ground - 36f
        paint.color = Color.rgb(237, 105, 88)
        canvas.drawCircle(target.x, y - 20f, 10f, paint)
        canvas.drawRoundRect(RectF(target.x - 10f, y - 10f, target.x + 10f, y + 16f), 5f, 5f, paint)
        paint.color = Color.rgb(234, 95, 79)
        canvas.drawRect(target.x - 7f, y + 14f, target.x - 2f, y + 25f, paint)
        canvas.drawRect(target.x + 2f, y + 14f, target.x + 7f, y + 25f, paint)
        paint.color = Color.rgb(51, 62, 76)
        canvas.drawRoundRect(RectF(target.x - 17f, y - 37f, target.x + 17f, y - 32f), 2f, 2f, paint)
        paint.color = COLOR_GREEN
        canvas.drawRect(target.x - 16f, y - 36f, target.x - 16f + 32f * target.health / 3f, y - 33f, paint)
    }

    private fun drawHud(canvas: Canvas) {
        drawText(canvas, "TRAINING  •  HITS $score", 24f, 32f, 17f, COLOR_WHITE, Paint.Align.LEFT, true)
        paint.color = Color.argb(185, 10, 18, 28)
        canvas.drawRoundRect(RectF(LOGICAL_WIDTH - 80f, 10f, LOGICAL_WIDTH - 8f, 57f), 14f, 14f, paint)
        drawText(canvas, "Ⅱ", LOGICAL_WIDTH - 44f, 42f, 24f, COLOR_WHITE, bold = true)
    }

    private fun drawControls(canvas: Canvas) {
        val baseX = if (movePointer == INVALID_POINTER) 105f else moveOriginX
        val baseY = if (movePointer == INVALID_POINTER) logicalHeight - 84f else moveOriginY
        paint.color = Color.argb(90, 220, 239, 249)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawCircle(baseX, baseY, 45f, paint)
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(135, 89, 168, 195)
        val knobX = if (movePointer == INVALID_POINTER) baseX else
            moveOriginX + (moveTouchX - moveOriginX).coerceIn(-45f, 45f)
        val knobY = if (movePointer == INVALID_POINTER) baseY else
            moveOriginY + (moveTouchY - moveOriginY).coerceIn(-45f, 45f)
        canvas.drawCircle(knobX, knobY, 19f, paint)
        drawText(canvas, "MOVE", baseX, baseY + 65f, 12f, COLOR_WHITE, bold = true)

        drawActionButton(canvas, jumpButton.rect(logicalHeight), "JUMP", jumpPointer != INVALID_POINTER)
        drawActionButton(canvas, fireButton.rect(logicalHeight), "FIRE", firePointer != INVALID_POINTER)
        if (aimPointer == INVALID_POINTER) drawAimMarker(canvas)
    }

    private fun drawAimMarker(canvas: Canvas) {
        paint.color = Color.argb(185, 255, 230, 138)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawCircle(aimX, aimY, 11f, paint)
        canvas.drawLine(aimX - 16f, aimY, aimX + 16f, aimY, paint)
        canvas.drawLine(aimX, aimY - 16f, aimX, aimY + 16f, paint)
        paint.style = Paint.Style.FILL
    }

    private fun drawPausePanel(canvas: Canvas) {
        paint.color = Color.argb(170, 3, 10, 18)
        canvas.drawRect(0f, 0f, LOGICAL_WIDTH, logicalHeight, paint)
        drawText(canvas, "PAUSED", LOGICAL_WIDTH / 2f, logicalHeight * 0.38f, 32f, COLOR_WHITE, bold = true)
        drawButton(canvas, resumeButton.rect(logicalHeight), "RESUME", COLOR_CYAN)
        drawButton(canvas, menuButton.rect(logicalHeight), "MAIN MENU", Color.rgb(91, 112, 133))
    }

    private fun drawButton(canvas: Canvas, rect: RectF, label: String, color: Int) {
        paint.color = color
        canvas.drawRoundRect(rect, 15f, 15f, paint)
        drawText(canvas, label, rect.centerX(), rect.centerY() + 6f, 18f, COLOR_WHITE, bold = true)
    }

    private fun drawActionButton(canvas: Canvas, rect: RectF, label: String, pressed: Boolean) {
        paint.color = if (pressed) Color.argb(220, 233, 111, 85)
        else Color.argb(150, 32, 46, 62)
        canvas.drawRoundRect(rect, 30f, 30f, paint)
        paint.color = Color.argb(210, 239, 244, 248)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRoundRect(rect, 30f, 30f, paint)
        paint.style = Paint.Style.FILL
        drawText(canvas, label, rect.centerX(), rect.centerY() + 5f, 13f, COLOR_WHITE, bold = true)
    }

    private fun drawText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        size: Float,
        color: Int,
        align: Paint.Align = Paint.Align.CENTER,
        bold: Boolean = false
    ) {
        paint.color = color
        paint.textSize = size
        paint.textAlign = align
        paint.typeface = if (bold) Typeface.create("sans-serif", Typeface.BOLD)
        else Typeface.create("sans-serif", Typeface.NORMAL)
        paint.style = Paint.Style.FILL
        canvas.drawText(text, x, y, paint)
    }

    private val groundY: Float
        get() = logicalHeight * 0.79f

    private val playButton = ButtonLayout(500f, 0.52f, 590f, 64f)
    private val resumeButton = ButtonLayout(500f, 0.49f, 400f, 56f)
    private val menuButton = ButtonLayout(500f, 0.64f, 400f, 56f)
    private val fireButton = ButtonLayout(870f, 0.80f, 72f, 72f)
    private val jumpButton = ButtonLayout(758f, 0.80f, 72f, 72f)

    private data class ButtonLayout(
        val centerX: Float,
        val centerYFraction: Float,
        val buttonWidth: Float,
        val buttonHeight: Float
    ) {
        fun rect(height: Float) = RectF(
            centerX - buttonWidth / 2f,
            height * centerYFraction - buttonHeight / 2f,
            centerX + buttonWidth / 2f,
            height * centerYFraction + buttonHeight / 2f
        )

        fun contains(x: Float, y: Float, height: Float) = rect(height).contains(x, y)
    }

    private companion object {
        const val LOGICAL_WIDTH = 1000f
        const val INVALID_POINTER = -1
        const val PLAYER_RADIUS = 30f
        const val PLAYER_SPEED = 250f
        const val JUMP_SPEED = 510f
        const val GRAVITY = 1250f
        const val MAX_FALL_SPEED = 850f
        const val TARGET_SPEED = 38f
        const val SHOT_SPEED = 780f
        const val FIRE_INTERVAL = 0.22f
        const val MAX_FRAME_DELTA = 0.05f
        val COLOR_WHITE = Color.rgb(244, 247, 250)
        val COLOR_MUTED = Color.rgb(174, 195, 209)
        val COLOR_CYAN = Color.rgb(59, 185, 210)
        val COLOR_GREEN = Color.rgb(118, 224, 143)
        val COLOR_YELLOW = Color.rgb(255, 228, 119)
    }
}
