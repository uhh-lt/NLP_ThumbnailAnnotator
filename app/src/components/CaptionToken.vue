<template>
  <div class="clearfix">
    <h4><span class="badge badge-warning text-monospace float-left">{{ this.captionTokenInstance.value }}</span></h4>
    <h4><span class="badge badge-dark text-monospace float-right">{{ this.captionTokenInstance.type }}</span></h4>
    <br/>
    <hr>
    <code>{{this.captionTokenInstance.posTags}}</code>
    <code>{{this.captionTokenInstance.tokens}}</code>
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
  code {
    font-weight: bold;
    text-align: left;
    font-size: medium;
    display: block;
  }

</style>
