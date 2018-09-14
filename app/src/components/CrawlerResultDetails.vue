<template>
  <div class="card text-white bg-primary d-inline-block p-1 m-1">
    <div class="card-body">
      <caption-token
        v-bind:captionTokenInstance="crawlerResult.captionToken"
      />
      <thumbnail v-for="thumbnail in crawlerResult.thumbnails"
                 v-bind:key="thumbnail.id"
                 v-bind:thumbnail="thumbnail"
      />
    </div>
  </div>
</template>

<script>
  import CaptionToken from "./CaptionToken";
  import Thumbnail from "./Thumbnail";

  export default {
    name: "CrawlerResultDetails",
    components: {CaptionToken, Thumbnail},
    props: {
      crawlerResult: {
        type: Object,
        required: true
      }
    }
  }
</script>

<style scoped>

</style>
