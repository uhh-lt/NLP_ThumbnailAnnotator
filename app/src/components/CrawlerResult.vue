<template>
  <div>

    <div class="badge badge-warning m-1 float-left">
     <a href="#" class="text-body" :id="thumbnail_carousel_popover_target_id" v-b-toggle="crawler_result_details_collapse_target_id">{{ this.crawlerResult.captionTokenValue }}</a>

      <b-collapse :id="crawler_result_details_collapse_target_id">
        <crawler-result-details v-bind:key="crawlerResult.id"
                                v-bind:crawler-result="crawlerResult"/>
      </b-collapse>
    </div>


    <b-popover :target="thumbnail_carousel_popover_target_id" triggers="hover">
      <template slot="title"></template>
      <thumbnail-carousel v-bind:thumbnails="crawlerResult.thumbnails"
                          v-bind:id="crawlerResult.id"/>
    </b-popover>
  </div>
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
      }
    },
    data: function () {
      return {
        crawler_result_details_collapse_target_id: "crawler-result-details-collapse-target-" + this.crawlerResult.id,
        thumbnail_carousel_popover_target_id: "thumbnail-carousel-popover-target-" + this.crawlerResult.id
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
