<template>
  <svg aria-hidden="true" :class="customCss">
    <use :href="symbolId" />
  </svg>
</template>

<script setup>
import { defineProps, computed } from 'vue';

const props = defineProps({
  prefix: {
    type: String,
    default: 'icon',
  },
  name: {
    type: String,
    required: true,
  },
  customCss: {
    type: String,
    default: '',
  },
});

const symbolId = computed(() => `#${props.prefix}-${props.name}`);
</script>
