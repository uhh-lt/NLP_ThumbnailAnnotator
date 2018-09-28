<template>
  <div class="card text-white bg-primary m-0 p-0">
    <div class="card-body">
      <caption-token
        v-bind:captionTokenInstance="crawlerResultObject.captionToken"
        v-bind:id="id"
      />

      <hr>

      <thumbnail v-if="highestPriorityThumbnail != null"
                 v-bind:key="highestPriorityThumbnail.id + '_' + id"
                 v-bind:id="highestPriorityThumbnail.id + '_' + id"
                 v-bind:hasHighestPriority="true"
                 v-bind:thumbnail="highestPriorityThumbnail"
      />

      <thumbnail v-for="thumbnail in lowPriorityThumbnails"
                 v-bind:key="thumbnail.id + '_' + id"
                 v-bind:id="thumbnail.id + '_' + id"
                 v-bind:hasHighestPriority="false"
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
        crawlerResultObject: null,
        highestPriorityIndex: -1
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
    },
    computed: {
      highestPriorityThumbnail: function () {
        let highestPrio = -100; //TODO bad style!
        let i = 0;
        for (i in this.crawlerResultObject.thumbnails) {
          if (this.crawlerResultObject.thumbnails[i].priority > highestPrio) {
            highestPrio = this.crawlerResultObject.thumbnails[i].priority;
            this.highestPriorityIndex = i;
          }
        }

        return this.crawlerResultObject.thumbnails[this.highestPriorityIndex];
      },

      lowPriorityThumbnails: function () {
        let thumbs = [];
        let i = 0;
        for (i in this.crawlerResultObject.thumbnails) {
          if (i !== this.highestPriorityIndex)
            thumbs.push(this.crawlerResultObject.thumbnails[i]);
        }

        return thumbs;
      }
    }
  }
</script>

<style scoped>

</style>
