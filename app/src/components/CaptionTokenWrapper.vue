<template>
  <span>
    <span class="badge badge-warning m-1 float-left">
      <a v-b-modal="captionToken_details_modal_target_id"
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
    </span>
  </span>
</template>

<script>
import CaptionToken from './CaptionToken'
import Thumbnail from './Thumbnail'
import CaptionTokenDetails from './CaptionTokenDetails'

export default {
  name: 'CaptionTokenWrapper',
  components: { CaptionTokenDetails, Thumbnail, CaptionToken },
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
