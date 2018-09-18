<template>
  <div>
    <div class='float-left' :id="thumbnail_details_popover_target_id">
      <img :src="thumbnail.url" class='img-thumbnail' :alt="thumbnail.url"
           :id="thumbnail_large_image_popover_target_id">
    </div>

    <b-popover :target="thumbnail_details_popover_target_id" triggers="click focus" placement="right">
      <thumbnail-priority-panel
        v-bind:thumbnail="thumbnail"/>
    </b-popover>

    <b-popover :target="thumbnail_large_image_popover_target_id" triggers="hover" placement="top">
      <img :src="thumbnail.url" :alt="thumbnail.url">
    </b-popover>
  </div>
</template>

<script>
  import ThumbnailPriorityPanel from "./ThumbnailPriorityPanel";

  export default {
    name: "Thumbnail",
    data: function () {
      return {
        thumbnail_details_popover_target_id: "thumbail-details-popver-target-" + this.id,
        thumbnail_large_image_popover_target_id: "thumbail-large-image-popver-target" + this.id
      }
    },
    components: {ThumbnailPriorityPanel},
    props: {
      thumbnail: {
        type: Object,
        required: true
      },
      id: {
        required: true
      }
    },
    methods: {}
  }
</script>

<style scoped>
  .img-thumbnail {
    width: 50px;
    height: 50px;
    margin: 1px;
    padding: 0;
  }

  img {
    max-width: 250px;
    max-height: 250px;
  }
</style>
