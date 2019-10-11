<template>
  <span>
    <div class="badge badge-warning m-1 float-left">
      <a
        :id="thumbnail_carousel_popover_target_id"
        v-b-modal="captionToken_details_modal_target_id"
        href="#"
        class="text-body"
      >{{ this.captionTokenObj.value }}</a>

      <b-modal
        :id="captionToken_details_modal_target_id"
        centered
        hide-header
        hide-footer
        :body-class="'details-modal'"
        :title="captionTokenObj.value"
      >
        <caption-token-details
          :id="id"
          :key="id"
          :caption-token="captionTokenObj"
        />
      </b-modal>
    </div>

    <b-popover
      :target="thumbnail_carousel_popover_target_id"
      triggers="hover"
    >
      <thumbnail-carousel
        :id="id"
        :thumbnails="captionTokenObj.thumbnails"
      />
    </b-popover>
  </span>
</template>

<script>
import CaptionToken from './CaptionToken'
import Thumbnail from './Thumbnail'
import ThumbnailCarousel from './ThumbnailCarousel'
import CaptionTokenDetails from './CaptionTokenDetails'

export default {
  name: 'CaptionTokenWrapper',
  components: { CaptionTokenDetails, ThumbnailCarousel, Thumbnail, CaptionToken },
  props: {
    captionToken: {
      type: Object,
      required: true
    },
    id: {
      required: true
    }
  },
  data () {
    return {
      captionToken_details_modal_target_id: 'caption-token-details-modal-target-' + this.id,
      thumbnail_carousel_popover_target_id: 'thumbnail-carousel-popover-target-' + this.id,
      captionTokenObj: null
    }
  },
  created () {
    console.log('CaptionTokenWrapper created')
    this.captionTokenObj = this.captionToken
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
