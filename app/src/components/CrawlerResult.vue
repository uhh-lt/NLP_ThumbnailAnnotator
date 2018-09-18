<template>
  <span>
    <div class="badge badge-warning m-1 float-left">
      <a href="#" class="text-body" :id="thumbnail_carousel_popover_target_id" v-b-modal="crawler_result_details_collapse_target_id">{{ this.crawlerResult.captionTokenValue }}</a>

      <b-modal centered hide-header hide-footer :id="crawler_result_details_collapse_target_id" :title="this.crawlerResult.captionTokenValue">
        <crawler-result-details v-bind:key="id"
                                v-bind:crawler-result="crawlerResult"
                                v-bind:id="id"/>
      </b-modal>
    </div>


    <b-popover :target="thumbnail_carousel_popover_target_id" triggers="hover">
      <thumbnail-carousel v-bind:thumbnails="crawlerResult.thumbnails"
                          v-bind:id="id"/>
    </b-popover>
  </span>
</template>

<script>
  import CaptionToken from "./CaptionToken";
  import Thumbnail from "./Thumbnail";
  import ThumbnailCarousel from "./ThumbnailCarousel";
  import CrawlerResultDetails from "./CrawlerResultDetails";


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
    data: function () {
      return {
        crawler_result_details_collapse_target_id: "crawler-result-details-collapse-target-" + this.id,
        thumbnail_carousel_popover_target_id: "thumbnail-carousel-popover-target-" + this.id
      }
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
