<template>
  <b-carousel
    :id="thumbnail_carousel_id"
    :interval="500"
    img-width="200"
    img-height="200"
  >
    <b-carousel-slide
      v-for="thumbnail in thumbnails"
      :key="thumbnail.id"
    >
      <img
        slot="img"
        class="d-block img-fluid w-100"
        :src="thumbnail.url"
        width="200"
        height="200"
        :alt="thumbnail.url"
      >
    </b-carousel-slide>
  </b-carousel>
</template>

<script>
export default {
  name: 'ThumbnailCarousel',
  props: {
    thumbnails: {
      type: Array,
      required: true
    },
    id: {
      type: String,
      required: true
    }
  },
  data: function () {
    return {
      thumbnail_carousel_id: 'thumbnail-carousel-' + this.id
    }
  }
}
</script>

<style scoped>

</style>
