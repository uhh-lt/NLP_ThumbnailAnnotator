<template>
  <div class="card text-white bg-primary">
    <div class="card-body">
      <caption-token
        v-bind:captionTokenInstance="crawlerResult.captionToken"
        v-bind:id="id"
      />
      <hr>
      <thumbnail v-for="thumbnail in crawlerResult.thumbnails"
                 v-bind:key="thumbnail.id + '_' + id"
                 v-bind:id="thumbnail.id + '_' + id"
                 v-bind:thumbnail="thumbnail"
      />
    </div>
  </div>
</template>

<script>
  import {EventBus} from "../main";

  import CaptionToken from "./CaptionToken";
  import Thumbnail from "./Thumbnail";

  export default {
    name: "CrawlerResultDetails",
    components: {CaptionToken, Thumbnail},
    props: {
      crawlerResult: {
        type: Object,
        required: true
      },
      id: {
        required: true
      }
    },
    methods: {
      sortThumbnailList() {
        function compare(a, b) {
          if (a.priority < b.priority)
            return 1;
          if (a.priority > b.priority)
            return -1;
          return 0;
        }

        this.crawlerResult.thumbnails.sort(compare);
        this.$forceUpdate();
      }
    },
    created() {
      EventBus.$on('thumbnailPriorityChanged_event', this.sortThumbnailList);
    }
  }
</script>

<style scoped>

</style>
