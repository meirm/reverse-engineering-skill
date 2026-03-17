# Lifecycle Hooks | Vue.js

Each Vue component instance goes through a series of initialization steps when it's created - for example, it needs to set up data observation, compile the template, mount the instance to the DOM, and update the DOM when data changes. Along the way, it also runs functions called lifecycle hooks, giving users the opportunity to add their own code at specific stages.

## Registering Lifecycle Hooks

For example, the `onMounted` `mounted` hook can be used to run code after the component has finished the initial rendering and created the DOM nodes:

```vue
<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  console.log(`the component is now mounted.`)
})
</script>
```

```js
export default {
  mounted() {
    console.log(`the component is now mounted.`)
  }
}
```

There are also other hooks which will be called at different stages of the instance's lifecycle, with the most commonly used being [`onMounted`](https://vuejs.org/api/composition-api-lifecycle#onmounted), [`onUpdated`](https://vuejs.org/api/composition-api-lifecycle#onupdated), and [`onUnmounted`](https://vuejs.org/api/composition-api-lifecycle#onunmounted).[`mounted`](https://vuejs.org/api/options-lifecycle#mounted), [`updated`](https://vuejs.org/api/options-lifecycle#updated), and [`unmounted`](https://vuejs.org/api/options-lifecycle#unmounted).

All lifecycle hooks are called with their `this` context pointing to the current active instance invoking it. Note this means you should avoid using arrow functions when declaring lifecycle hooks, as you won't be able to access the component instance via `this` if you do so.

When calling `onMounted`, Vue automatically associates the registered callback function with the current active component instance. This requires these hooks to be registered **synchronously** during component setup. For example, do not do this:

```js
setTimeout(() => {
  onMounted(() => {
    // this won't work.
  })
}, 100)
```

Do note this doesn't mean that the call must be placed lexically inside `setup()` or `<script setup>`. `onMounted()` can be called in an external function as long as the call stack is synchronous and originates from within `setup()`.

## Lifecycle Diagram

Below is a diagram for the instance lifecycle. You don't need to fully understand everything going on right now, but as you learn and build more, it will be a useful reference.

![Component lifecycle diagram](https://vuejs.org/assets/lifecycle.MuZLBFAS.png)

Consult the [Lifecycle Hooks API reference](https://vuejs.org/api/composition-api-lifecycle)[Lifecycle Hooks API reference](https://vuejs.org/api/options-lifecycle) for details on all lifecycle hooks and their respective use cases.