<template>
  <li style="list-style: none">
    <div
      :id="thumbnail_details_popover_target_id"
      class="float-left"
    >
      <img
        :id="thumbnail_large_image_popover_target_id"
        :src="thumbnailObj.url"
        class="img-thumbnail"
        :alt="thumbnailObj.url"
      >
    </div>

    <b-popover
      triggers="click blur"
      placement="left"
      :target="thumbnail_details_popover_target_id"
      :show.sync="popoverShow"
    >
      <b-btn
        class="close"
        aria-label="Close"
        @click="popoverShow = false"
      >
        &times;
      </b-btn>
      <thumbnail-details-panel
        :thumbnail="thumbnailObj"
        :caption-token-id="captionTokenId"
      />
    </b-popover>

    <b-popover
      :target="thumbnail_large_image_popover_target_id"
      triggers="hover"
      placement="top"
    >
      <img
        :src="thumbnailObj.url"
        :alt="thumbnailObj.url"
      >
    </b-popover>
  </li>
</template>

<script>
import ThumbnailDetailsPanel from './ThumbnailDetailsPanel'
import { EventBus } from '../index'

export default {
  name: 'Thumbnail',
  components: { ThumbnailDetailsPanel },
  props: {
    thumbnail: {
      type: Object,
      required: true
    },
    id: {
      required: true
    },
    captionTokenId: {
      required: true
    }
  },
  data: function () {
    return {
      thumbnail_details_popover_target_id: 'thumbail-details-popver-target-' + this.id,
      thumbnail_large_image_popover_target_id: 'thumbail-large-image-popver-target' + this.id,
      popoverShow: false,
      thumbnailObj: null
    }
  },
  created () {
    this.thumbnailObj = this.thumbnail
    EventBus.$on('updatedThumbnail_event', this.updateThumbnail)
    console.log('Thumbnail created')
  },
  methods: {
    updateThumbnail (updatedThumbnail) {
      if (this.thumbnailObj.id === updatedThumbnail.id) {
        this.thumbnailObj = updatedThumbnail
      }
    }
  }
}
</script>

<style scoped>
  .img-thumbnail {
    width: 55px;
    height: 55px;
    margin: 1px;
    padding: 0;
  }

  img {
    max-width: 250px;
    max-height: 250px;
  }
</style>
