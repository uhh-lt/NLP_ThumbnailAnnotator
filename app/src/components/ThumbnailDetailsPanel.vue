<template>
  <div class="card text-white bg-primary d-inline-flex clearfix border-warning bo">
    <div class="card-header card-title text-md-center m-1">
      <a :href="thumbnailObj.url" target="_blank">
        <img v-bind:src="thumbnailObj.url"
             v-bind:alt="thumbnailObj.url"
             class="rounded" style="max-width: 224px; margin: 0; padding: 0;">
      </a>
    </div>
    <div class="card-body p-1 text-center">
      <h6>
        <code class="badge badge-danger badge-pill">
          Priority: {{ this.thumbnailObj.priority }}
        </code>
      </h6>
      <hr>
      <code class="text-warning">{{this.thumbnailObj.description}}</code>
      <hr>
      <div>
        <span v-for="c in this.thumbnailObj.categories" class="badge badge-info ml-1">
          {{c.name}}
        </span>
      </div>
      <hr>
      <div>
        <span v-for="k in this.thumbnailObj.keywords" class="badge badge-info ml-1">
          {{k}}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
  import {EventBus} from "../main";

  export default {
    name: "ThumbnailDetailsPanel",
    props: {
      thumbnail: {
        type: Object,
        required: true
      },
      captionTokenId: {
        required: true
      }
    },
    data() {
      return {
        thumbnailObj: null
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
    }
  }
</script>

<style scoped>
</style>
