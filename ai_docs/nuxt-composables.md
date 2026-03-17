# composables

Use the composables/ directory to auto-import your Vue composables into your application.

## Usage

**Method 1:** Using named export

app/composables/useFoo.ts

```ts
export const useFoo = () => {
  return useState('foo', () => 'bar')
}
```

**Method 2:** Using default export

app/composables/use-foo.ts or composables/useFoo.ts

```ts
// It will be available as useFoo() (camelCase of file name without extension)
export default function () {
  return useState('foo', () => 'bar')
}
```

**Usage:** You can now use auto imported composable in `.js`, `.ts` and `.vue` files

app/app.vue

```vue
<script setup lang="ts">
const foo = useFoo()
</script>

<template>
  <div>
    {{ foo }}
  </div>
</template>
```

The `app/composables/` directory in Nuxt does not provide any additional reactivity capabilities to your code. Instead, any reactivity within composables is achieved using Vue's Composition API mechanisms, such as ref and reactive. Note that reactive code is also not limited to the boundaries of the `app/composables/` directory. You are free to employ reactivity features wherever they're needed in your application.

[Docs > 4 X > Guide > Concepts > Auto Imports](https://nuxt.com/docs/4.x/guide/concepts/auto-imports) Read more in Docs > 4 X > Guide > Concepts > Auto Imports.

Read and edit a live example in [Docs > 4 X > Examples > Features > Auto Imports](https://nuxt.com/docs/4.x/examples/features/auto-imports).

## Types

Under the hood, Nuxt auto generates the file `.nuxt/imports.d.ts` to declare the types.

Be aware that you have to run [`nuxt prepare`](https://nuxt.com/docs/4.x/api/commands/prepare), [`nuxt dev`](https://nuxt.com/docs/4.x/api/commands/dev) or [`nuxt build`](https://nuxt.com/docs/4.x/api/commands/build) in order to let Nuxt generate the types.

If you create a composable without having the dev server running, TypeScript will throw an error, such as `Cannot find name 'useBar'.

## Examples

### Nested Composables

You can use a composable within another composable using auto imports:

app/composables/test.ts

```ts
export const useFoo = () => {
  const nuxtApp = useNuxtApp()
  const bar = useBar()
}
```

### Access plugin injections

You can access [plugin injections](https://nuxt.com/docs/4.x/directory-structure/app/plugins#providing-helpers) from composables:

app/composables/test.ts

```ts
export const useHello = () => {
  const nuxtApp = useNuxtApp()
  return nuxtApp.$hello
}
```

## How Files Are Scanned

Nuxt only scans files at the top level of the [`app/composables/` directory](https://nuxt.com/docs/4.x/directory-structure/app/composables), e.g.:

Directory Structure

```bash
-| composables/
---| index.ts     // scanned
---| useFoo.ts    // scanned
---| nested/
-----| utils.ts   // not scanned
```

Only `app/composables/index.ts` and `app/composables/useFoo.ts` would be searched for imports.

To get auto imports working for nested modules, you could either re-export them (recommended) or configure the scanner to include nested directories:

**Example:** Re-export the composables you need from the `app/composables/index.ts` file:

app/composables/index.ts

```ts
// Enables auto import for this export
export { utils } from './nested/utils.ts'
```

**Example:** Scan nested directories inside the `app/composables/` folder:

nuxt.config.ts

```ts
export default defineNuxtConfig({
  imports: {
    dirs: [\
      // Scan top-level composables\
      '~/composables',\
      // ... or scan composables nested one level deep with a specific name and file extension\
      '~/composables/*/index.{ts,js,mjs,mts}',\
      // ... or scan all composables within given directory\
      '~/composables/**',\
    ],
  },
})
```