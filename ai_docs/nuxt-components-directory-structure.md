# components

The components/ directory is where you put all your Vue components.

Nuxt automatically imports any components in this directory (along with components that are registered by any modules you may be using).

Directory Structure

```bash
-| components/
---| AppHeader.vue
---| AppFooter.vue
```

app/app.vue

```html
<template>
  <div>
    <AppHeader />
    <NuxtPage />
    <AppFooter />
  </div>
</template>
```

## Component Names

If you have a component in nested directories such as:

Directory Structure

```bash
-| components/
---| base/
-----| foo/
-------| Button.vue
```

... then the component's name will be based on its own path directory and filename, with duplicate segments being removed. Therefore, the component's name will be:

```html
<BaseFooButton />
```

For clarity, we recommend that the component's filename matches its name. So, in the example above, you could rename `Button.vue` to be `BaseFooButton.vue`.

If you want to auto-import components based only on its name, not path, then you need to set `pathPrefix` option to `false` using extended form of the configuration object:

nuxt.config.ts

```ts
export default defineNuxtConfig({
  components: [
    {
      path: '~/components',
      pathPrefix: false    },
  ],
})
```

This registers the components using the same strategy as used in Nuxt 2. For example, `~/components/Some/MyComponent.vue` will be usable as `<MyComponent>` and not `<SomeMyComponent>`.

## Dynamic Components

If you want to use the Vue `<component :is="someComputedComponent">` syntax, you need to use the `resolveComponent` helper provided by Vue or import the component directly from `#components` and pass it into `is` prop.

For example:

app/pages/index.vue

```vue
<script setup lang="ts">
import { SomeComponent } from '#components'

const MyButton = resolveComponent('MyButton')
</script>

<template>
  <component :is="clickable ? MyButton : 'div'" />
  <component :is="SomeComponent" />
</template>
```

If you are using `resolveComponent` to handle dynamic components, make sure not to insert anything but the name of the component, which must be a literal string and not be or contain a variable. The string is statically analyzed at the compilation step.

Watch Daniel Roe's short video about resolveComponent()

Alternatively, though not recommended, you can register all your components globally, which will create async chunks for all your components and make them available throughout your application.

```diff
  export default defineNuxtConfig({
    components: {
+     global: true,
+     dirs: ['~/components']
    },
  })
```

You can also selectively register some components globally by placing them in a `~/components/global` directory, or by using a `.global.vue` suffix in the filename. As noted above, each global component is rendered in a separate chunk, so be careful not to overuse this feature.

The `global` option can also be set per component directory.

## Dynamic Imports

To dynamically import a component (also known as lazy-loading a component) all you need to do is add the `Lazy` prefix to the component's name. This is particularly useful if the component is not always needed.

By using the `Lazy` prefix you can delay loading the component code until the right moment, which can be helpful for optimizing your JavaScript bundle size.

app/pages/index.vue

```vue
<script setup lang="ts">
const show = ref(false)
</script>

<template>
  <div>
    <h1>Mountains</h1>
    <LazyMountainsList v-if="show" />
    <button
      v-if="!show"
      @click="show = true"
    >
      Show List
    </button>
  </div>
</template>
```

## Delayed (or Lazy) Hydration

Lazy components are great for controlling the chunk sizes in your app, but they don't always enhance runtime performance, as they still load eagerly unless conditionally rendered. In real-world applications, some pages may include a lot of content and a lot of components, and most of the time not all of them need to be interactive as soon as the page is loaded. Having them all load eagerly can negatively impact performance.

In order to optimize your app, you may want to delay the hydration of some components until they're visible, or until the browser is done with more important tasks.

Nuxt supports this using lazy (or delayed) hydration, allowing you to control when components become interactive.

### Hydration Strategies

Nuxt provides a range of built-in hydration strategies. Only one strategy can be used per lazy component.

Any prop change on a lazily hydrated component will trigger hydration immediately. (e.g., changing a prop on a component with `hydrate-never` will cause it to hydrate)

Currently Nuxt's built-in lazy hydration only works in single-file components (SFCs), and requires you to define the prop in the template (rather than spreading an object of props via `v-bind`). It also does not work with direct imports from `#components`.

#### `hydrate-on-visible`

Hydrates the component when it becomes visible in the viewport.

app/pages/index.vue

```vue
<template>
  <div>
    <LazyMyComponent hydrate-on-visible />
  </div>
</template>
```

[IntersectionObserver options](https://developer.mozilla.org/en-US/docs/Web/API/IntersectionObserver/IntersectionObserver) Read more about the options for `hydrate-on-visible`.

Under the hood, this uses Vue's built-in [`hydrateOnVisible` strategy](https://vuejs.org/guide/components/async#hydrate-on-visible).

#### `hydrate-on-idle`

Hydrates the component when the browser is idle. This is suitable if you need the component to load as soon as possible, but not block the critical rendering path.

You can also pass a number which serves as a max timeout.

app/pages/index.vue

```vue
<template>
  <div>
    <LazyMyComponent hydrate-on-idle />
  </div>
</template>
```

Under the hood, this uses Vue's built-in [`hydrateOnIdle` strategy](https://vuejs.org/guide/components/async#hydrate-on-idle).

#### `hydrate-on-interaction`

Hydrates the component after a specified interaction (e.g., click, mouseover).

app/pages/index.vue

```vue
<template>
  <div>
    <LazyMyComponent hydrate-on-interaction="mouseover" />
  </div>
</template>
```

If you do not pass an event or list of events, it defaults to hydrating on `pointerenter`, `click` and `focus`.

Under the hood, this uses Vue's built-in [`hydrateOnInteraction` strategy](https://vuejs.org/guide/components/async#hydrate-on-interaction).

#### `hydrate-on-media-query`

Hydrates the component when the window matches a media query.

app/pages/index.vue

```vue
<template>
  <div>
    <LazyMyComponent hydrate-on-media-query="(max-width: 768px)" />
  </div>
</template>
```

Under the hood, this uses Vue's built-in [`hydrateOnMediaQuery` strategy](https://vuejs.org/guide/components/async#hydrate-on-media-query).

#### `hydrate-after`

Hydrates the component after a specified delay (in milliseconds).

app/pages/index.vue

```vue
<template>
  <div>
    <LazyMyComponent :hydrate-after="2000" />
  </div>
</template>
```

#### `hydrate-when`

Hydrates the component based on a boolean condition.

app/pages/index.vue

```vue
<template>
  <div>
    <LazyMyComponent :hydrate-when="isReady" />
  </div>
</template>

<script setup lang="ts">
const isReady = ref(false)
function myFunction () {
  // trigger custom hydration strategy...
  isReady.value = true
}
</script>
```

#### `hydrate-never`

Never hydrates the component.

app/pages/index.vue

```vue
<template>
  <div>
    <LazyMyComponent hydrate-never />
  </div>
</template>
```

### Listening to Hydration Events

All delayed hydration components emit a `@hydrated` event when they are hydrated.

app/pages/index.vue

```vue
<template>
  <div>
    <LazyMyComponent
      hydrate-on-visible
      @hydrated="onHydrate"
    />
  </div>
</template>

<script setup lang="ts">
function onHydrate () {
  console.log('Component has been hydrated!')
}
</script>
```

### Caveats and Best Practices

Delayed hydration can offer performance benefits, but it's essential to use it correctly:

1. **Prioritize In-Viewport Content:** Avoid delayed hydration for critical, above-the-fold content. It's best suited for content that isn't immediately needed.
2. **Conditional Rendering:** When using `v-if="false"` on a lazy component, you might not need delayed hydration. You can just use a normal lazy component.
3. **Shared State:** Be mindful of shared state (`v-model`) across multiple components. Updating the model in one component can trigger hydration in all components bound to that model.
4. **Use Each Strategy's Intended Use Case:**Each strategy is optimized for a specific purpose.
   - `hydrate-when` is best for components that might not always need to be hydrated.
   - `hydrate-after` is for components that can wait a specific amount of time.
   - `hydrate-on-idle` is for components that can be hydrated when the browser is idle.
5. **Avoid `hydrate-never` on interactive components:** If a component requires user interaction, it should not be set to never hydrate.

## Direct Imports

You can also explicitly import components from `#components` if you want or need to bypass Nuxt's auto-importing functionality.

app/pages/index.vue

```vue
<script setup lang="ts">
import { LazyMountainsList, NuxtLink } from '#components'

const show = ref(false)
</script>

<template>
  <div>
    <h1>Mountains</h1>
    <LazyMountainsList v-if="show" />
    <button
      v-if="!show"
      @click="show = true"
    >
      Show List
    </button>
    <NuxtLink to="/">Home</NuxtLink>
  </div>
</template>
```

## Custom Directories

By default, only the `~/components` directory is scanned. If you want to add other directories, or change how the components are scanned within a subfolder of this directory, you can add additional directories to the configuration:

nuxt.config.ts

```ts
export default defineNuxtConfig({
  components: [
    // ~/calendar-module/components/event/Update.vue => <EventUpdate />
    { path: '~/calendar-module/components' },

    // ~/user-module/components/account/UserDeleteDialog.vue => <UserDeleteDialog />
    { path: '~/user-module/components', pathPrefix: false },

    // ~/components/special-components/Btn.vue => <SpecialBtn />
    { path: '~/components/special-components', prefix: 'Special' },

    // It's important that this comes last if you have overrides you wish to apply
    // to sub-directories of `~/components`.
    //
    // ~/components/Btn.vue => <Btn />
    // ~/components/base/Btn.vue => <BaseBtn />
    '~/components',
  ],
})
```

Any nested directories need to be added first as they are scanned in order.

## npm Packages

If you want to auto-import components from an npm package, you can use [`addComponent`](https://nuxt.com/docs/4.x/api/kit/components#addcomponent) in a [local module](https://nuxt.com/docs/4.x/directory-structure/modules) to register them.

~/modules/register-component.ts

```ts
import { addComponent, defineNuxtModule } from '@nuxt/kit'

export default defineNuxtModule({
  setup () {
    // import { MyComponent as MyAutoImportedComponent } from 'my-npm-package'
    addComponent({
      name: 'MyAutoImportedComponent',
      export: 'MyComponent',
      filePath: 'my-npm-package',
    })
  },
})
```

app/app.vue

```vue
<template>
  <div>
    <!--  the component uses the name we specified and is auto-imported  -->
    <MyAutoImportedComponent />
  </div>
</template>
```

## Component Extensions

By default, any file with an extension specified in the [extensions key of `nuxt.config.ts`](https://nuxt.com/docs/4.x/api/nuxt-config#extensions) is treated as a component.
If you need to restrict the file extensions that should be registered as components, you can use the extended form of the components directory declaration and its `extensions` key:

nuxt.config.ts

```ts
export default defineNuxtConfig({
  components: [
    {
      path: '~/components',
      extensions: ['.vue'],    },
  ],
})
```

## Client Components

If a component is meant to be rendered only client-side, you can add the `.client` suffix to your component.

Directory Structure

```bash
| components/
--| Comments.client.vue
```

app/pages/example.vue

```vue
<template>
  <div>
    <!-- this component will only be rendered on client side -->
    <Comments />
  </div>
</template>
```

This feature only works with Nuxt auto-imports and `#components` imports. Explicitly importing these components from their real paths does not convert them into client-only components.

`.client` components are rendered only after being mounted. To access the rendered template using `onMounted()`, add `await nextTick()` in the callback of the `onMounted()` hook.

[Docs > 4 X > API > Components > Client Only](https://nuxt.com/docs/4.x/api/components/client-only) You can also achieve a similar result with the `<ClientOnly>` component.

## Server Components

Server components allow server-rendering individual components within your client-side apps. It's possible to use server components within Nuxt, even if you are generating a static site. That makes it possible to build complex sites that mix dynamic components, server-rendered HTML and even static chunks of markup.

Server components can either be used on their own or paired with a [client component](https://nuxt.com/docs/4.x/directory-structure/app/components#paired-with-a-client-component).

Watch Learn Vue video about Nuxt Server Components

Read Daniel Roe's guide to Nuxt Server Components.

### Standalone server components

Standalone server components will always be rendered on the server, also known as Islands components.

When their props update, this will result in a network request that will update the rendered HTML in-place.

Server components are currently experimental and in order to use them, you need to enable the 'component islands' feature in your nuxt.config:

nuxt.config.ts

```ts
export default defineNuxtConfig({
  experimental: {
    componentIslands: true,
  },
})
```

Now you can register server-only components with the `.server` suffix and use them anywhere in your application automatically.

Directory Structure

```bash
-| components/
---| HighlightedMarkdown.server.vue
```

app/pages/example.vue

```vue
<template>
  <div>
    <!--
      this will automatically be rendered on the server, meaning your markdown parsing + highlighting
      libraries are not included in your client bundle.
     -->
    <HighlightedMarkdown markdown="# Headline" />
  </div>
</template>
```

Server-only components use [`<NuxtIsland>`](https://nuxt.com/docs/4.x/api/components/nuxt-island) under the hood, meaning that `lazy` prop and `#fallback` slot are both passed down to it.

Server components (and islands) must have a single root element. (HTML comments are considered elements as well.)

Props are passed to server components via URL query parameters, and are therefore limited by the possible length of a URL, so be careful not to pass enormous amounts of data to server components via props.

Be careful when nesting islands within other islands as each island adds some extra overhead.

Most features for server-only components and island components, such as slots and client components, are only available for single file components.

#### Client components within server components

This feature needs `experimental.componentIslands.selectiveClient` within your configuration to be true.

You can partially hydrate a component by setting a `nuxt-client` attribute on the component you wish to be loaded client-side.

app/components/ServerWithClient.vue

```vue
<template>
  <div>
    <HighlightedMarkdown markdown="# Headline" />
    <!-- Counter will be loaded and hydrated client-side -->
    <Counter
      nuxt-client
      :count="5"
    />
  </div>
</template>
```

This only works within a server component. Slots for client components are working only with `experimental.componentIsland.selectiveClient` set to `'deep'` and since they are rendered server-side, they are not interactive once client-side.

#### Server Component Context

When rendering a server-only or island component, `<NuxtIsland>` makes a fetch request which comes back with a `NuxtIslandResponse`. (This is an internal request if rendered on the server, or a request that you can see in the network tab if it's rendering on client-side navigation.)

This means:

- A new Vue app will be created server-side to create the `NuxtIslandResponse`.
- A new 'island context' will be created while rendering the component.
- You can't access the 'island context' from the rest of your app and you can't access the context of the rest of your app from the island component. In other words, the server component or island is _isolated_ from the rest of your app.
- Your plugins will run again when rendering the island, unless they have `env: { islands: false }` set (which you can do in an object-syntax plugin).

Route middleware does not run when rendering island components. Middleware is a routing concept that applies to pages, not components, and is not designed to control component rendering.

Within an island component, you can access its island context through `nuxtApp.ssrContext.islandContext`. Note that while island components are still marked as experimental, the format of this context may change.

Slots can be interactive and are wrapped within a `<div>` with `display: contents;`

### Paired with a Client component

In this case, the `.server` + `.client` components are two 'halves' of a component and can be used in advanced use cases for separate implementations of a component on server and client side.

Directory Structure

```bash
-| components/
---| Comments.client.vue
---| Comments.server.vue
```

app/pages/example.vue

```vue
<template>
  <div>
    <!-- this component will render Comments.server on the server then Comments.client once mounted in the browser -->
    <Comments />
  </div>
</template>
```

## Built-In Nuxt Components

There are a number of components that Nuxt provides, including `<ClientOnly>` and `<DevOnly>`. You can read more about them in the API documentation.

[Docs > 4 X > API](https://nuxt.com/docs/4.x/api) Read more in Docs > 4 X > API.

## Library Authors

Making Vue component libraries with automatic tree-shaking and component registration is super easy. ✨

You can use the [`addComponentsDir`](https://nuxt.com/docs/4.x/api/kit/components#addcomponentsdir) method provided from the `@nuxt/kit` to register your components directory in your Nuxt module.

Imagine a directory structure like this:

Directory Structure

```bash
-| node_modules/
---| awesome-ui/
-----| components/
-------| Alert.vue
-------| Button.vue
-----| nuxt.ts
-| pages/
---| index.vue
-| nuxt.config.ts
```

Then in `awesome-ui/nuxt.ts` you can use the `addComponentsDir` hook:

```ts
import { addComponentsDir, createResolver, defineNuxtModule } from '@nuxt/kit'

export default defineNuxtModule({
  setup () {
    const resolver = createResolver(import.meta.url)

    // Add ./components dir to the list
    addComponentsDir({
      path: resolver.resolve('./components'),
      prefix: 'awesome',
    })
  },
})
```

That's it! Now in your project, you can import your UI library as a Nuxt module in your `nuxt.config` file:

nuxt.config.ts

```ts
export default defineNuxtConfig({
  modules: ['awesome-ui/nuxt'],
})
```

... and directly use the module components (prefixed with `awesome-`) in our `app/pages/index.vue`:

```vue
<template>
  <div>
    My <AwesomeButton>UI button</AwesomeButton>!
    <awesome-alert>Here's an alert!</awesome-alert>
  </div>
</template>
```

It will automatically import the components only if used and also support HMR when updating your components in `node_modules/awesome-ui/components/`.

Read and edit a live example in [Docs > 4 X > Examples > Features > Auto Imports](https://nuxt.com/docs/4.x/examples/features/auto-imports).