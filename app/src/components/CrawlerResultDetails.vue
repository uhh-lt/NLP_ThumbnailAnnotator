<template>
  <div class="card text-white bg-primary m-0 p-0">
    <div class="card-body">
      <caption-token
        v-bind:captionTokenInstance="crawlerResultObject.captionToken"
        v-bind:id="id"
      />
      <hr>
      <thumbnail v-for="thumbnail in crawlerResultObject.thumbnails"
                 v-bind:key="thumbnail.id + '_' + id"
                 v-bind:id="thumbnail.id + '_' + id"
                 v-bind:thumbnail="thumbnail"
      />
    </div>
  </div>
</template>

<script>

  import CaptionToken from "./CaptionToken";
  import Thumbnail from "./Thumbnail";

  import axios from 'axios';
  import {EventBus} from "../main";

  export default {
    name: "CrawlerResultDetails",
    components: {CaptionToken, Thumbnail},
    data() {
      return {
        crawlerResultObject: null
      }
    },
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
      updateCrawlerResult() {
        axios.get(this.$hostname + "/getCrawlerResult/" + this.crawlerResultObject.id).then(response => {
          this.submitSuccess(response);
        }).catch(error => {
          console.log(error);
        });
      },
      submitSuccess(response) {
        if (response.status === 200)
          this.crawlerResultObject = response.data;
      }
    },
    created() {
      this.crawlerResultObject = this.crawlerResult;
      EventBus.$on('thumbnailPriorityChanged_event', this.updateCrawlerResult)
    }
  }
</script>

<style scoped>

</style>
