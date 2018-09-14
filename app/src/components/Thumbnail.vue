<template>
  <div>
    <div class='float-lef m-1' :id="thumbnail_details_popover_target_id">
      <img :src="thumbnail.url" class='img-thumbnail float-md-left' width="50" height="50" :alt="thumbnail.url"
           :id="thumbnail_large_image_popover_target_id">
    </div>

    <b-popover :target="thumbnail_details_popover_target_id" triggers="click focus">
      <thumbnail-details
        v-bind:thumbnail="thumbnail"/>
    </b-popover>

    <b-popover :target="thumbnail_large_image_popover_target_id" triggers="hover focus">
      <img :src="thumbnail.url" width="250" height="250" :alt="thumbnail.url">
    </b-popover>
  </div>
</template>

<script>
  import ThumbnailDetails from "./ThumbnailDetails";

  export default {
    name: "Thumbnail",
    data: function () {
      return {
        thumbnail_details_popover_target_id: "thumbail-details-popver-target-" + this.thumbnail.id,
        thumbnail_large_image_popover_target_id: "thumbail-large-image-popver-target" + this.thumbnail.id
      }
    },
    components: {ThumbnailDetails},
    props: {
      thumbnail: {
        type: Object,
        required: true
      }
    },
    methods: {}
  }
</script>

<style scoped>

</style>
