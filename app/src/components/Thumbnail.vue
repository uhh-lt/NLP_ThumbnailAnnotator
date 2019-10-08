<template>
  <li style="list-style: none">
    <div class="float-left" :id="thumbnail_details_popover_target_id">
      <img :src="thumbnailObj.url" class="img-thumbnail" :alt="thumbnailObj.url"
           :id="thumbnail_large_image_popover_target_id">
    </div>

    <b-popover triggers="click blur" placement="left"
               :target="thumbnail_details_popover_target_id"
               :show.sync="popoverShow">
      <b-btn @click="popoverShow = false" class="close" aria-label="Close">
        &times;
      </b-btn>
      <thumbnail-details-panel
        v-bind:thumbnail="thumbnailObj"
        v-bind:captionTokenId="captionTokenId"
      />
    </b-popover>

    <b-popover :target="thumbnail_large_image_popover_target_id" triggers="hover" placement="top">
      <img :src="thumbnailObj.url" :alt="thumbnailObj.url">
      <code class="badge badge-danger badge-pill">
        {{ this.thumbnailObj.priority }}
      </code>
    </b-popover>
  </li>
</template>

<script>
  import ThumbnailDetailsPanel from "./ThumbnailDetailsPanel";
  import {EventBus} from "../main";

  export default {
    name: "Thumbnail",
    data: function () {
      return {
        thumbnail_details_popover_target_id: "thumbail-details-popver-target-" + this.id,
        thumbnail_large_image_popover_target_id: "thumbail-large-image-popver-target" + this.id,
        popoverShow: false,
        thumbnailObj: null
      }
    },
    components: {ThumbnailDetailsPanel},
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
    methods: {
      updateThumbnail(updatedThumbnail) {
        if (this.thumbnailObj.id === updatedThumbnail.id) {
          this.thumbnailObj = updatedThumbnail;
        }
      }
    },
    created() {
      this.thumbnailObj = this.thumbnail;
      EventBus.$on('updatedThumbnail_event', this.updateThumbnail)
      console.log("Thumbnail created");
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
