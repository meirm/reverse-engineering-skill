# Getting Started

## Overview

This plugin is compatible with `pinia>=2.0.0`, make sure you have [Pinia installed](https://pinia.vuejs.org/getting-started.html) before proceeding. `pinia-plugin-persistedstate` comes with many features to make persistence of Pinia stores effortless and configurable with:

- An API similar to [`vuex-persistedstate`](https://github.com/robinvdvleuten/vuex-persistedstate).
- Per-store configuration.
- Custom storage and custom data serializer.
- Pre/post persistence/hydration hooks.
- Multiple configurations per store.

USING NUXT ?

This package exports a module for better integration with Nuxt and out of the box SSR support. Learn more about it in its [documentation](https://prazdevs.github.io/pinia-plugin-persistedstate/frameworks/nuxt.html).

## Installation

1. Install the dependency with your favorite package manager:

```bash
pnpm add pinia-plugin-persistedstate
```

```bash
npm i pinia-plugin-persistedstate
```

```bash
yarn add pinia-plugin-persistedstate
```

2. Add the plugin to your pinia instance:

```typescript
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
```

## Usage

When declaring your store, set the new `persist` option to `true`.

### setup syntax

```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useStore = defineStore(
  'main',
  () => {
    const someState = ref('hello pinia')
    return { someState }
  },
  {
    persist: true,
  },
)
```

### option syntax

```typescript
import { defineStore } from 'pinia'

export const useStore = defineStore('main', {
  state: () => {
    return {
      someState: 'hello pinia',
    }
  },
  persist: true,
})
```

Your whole store will now be saved with the [default persistence settings](https://prazdevs.github.io/pinia-plugin-persistedstate/guide/config.html).