<template>
  <div>
    <span class="badge badge-warning">{{ this.captionTokenInstance.value }}</span>
    <span class="badge badge-dark card-title">[{{ this.captionTokenInstance.beginPosition }}, {{ this.captionTokenInstance.endPosition }}] </span>
    <span class="badge badge-dark">{{ this.captionTokenInstance.type }}</span>
  </div>
</template>

<script>
  export default {
    name: "CaptionToken",
    data: function () {
      return {}
    },
    props: {
      captionTokenInstance: {
        type: Object,
        required: true
      }
    },
    methods: {}
  }
</script>

<style scoped>

</style>
