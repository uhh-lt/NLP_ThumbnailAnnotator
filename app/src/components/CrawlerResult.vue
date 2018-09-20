<template>
  <span>
    <div class="badge badge-warning m-1 float-left">
      <a href="#" class="text-body" :id="thumbnail_carousel_popover_target_id" v-b-modal="crawler_result_details_modal_target_id">{{ this.crawlerResultObject.captionTokenValue }}</a>

      <b-modal centered hide-header hide-footer :body-class="'details-modal'" :id="crawler_result_details_modal_target_id" :title="crawlerResultObject.captionTokenValue">
        <crawler-result-details v-bind:key="id"
                                v-bind:crawler-result="crawlerResultObject"
                                v-bind:id="id"/>
      </b-modal>
    </div>


    <b-popover :target="thumbnail_carousel_popover_target_id" triggers="hover">
      <thumbnail-carousel v-bind:thumbnails="crawlerResultObject.thumbnails"
                          v-bind:id="id"/>
    </b-popover>
  </span>
</template>

<script>
  import CaptionToken from "./CaptionToken";
  import Thumbnail from "./Thumbnail";
  import ThumbnailCarousel from "./ThumbnailCarousel";
  import CrawlerResultDetails from "./CrawlerResultDetails";

  import {EventBus} from "../main";

  import axios from 'axios';

  export default {
    name: "CrawlerResult",
    components: {CrawlerResultDetails, ThumbnailCarousel, Thumbnail, CaptionToken},
    props: {
      crawlerResult: {
        type: Object,
        required: true
      },
      id: {
        required: true
      }
    },
    data() {
      return {
        crawler_result_details_modal_target_id: "crawler-result-details-modal-target-" + this.id,
        thumbnail_carousel_popover_target_id: "thumbnail-carousel-popover-target-" + this.id,
        crawlerResultObject: null
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
  a:link {
    text-decoration: none;
  }

  a:visited {
    text-decoration: none;
  }

  a:hover {
    text-decoration: none;
    font-style: italic;
  }

  a:active {
    text-decoration: none;
  }
</style>
