<template>
  <div class="card text-white bg-primary m-0 p-0">
    <div class="card-body">
      <caption-token
        v-bind:captionTokenInstance="captionTokenObject"
        v-bind:id="id"
      />

      <hr>

      <thumbnail v-if="highestPriorityThumbnail != null"
                 v-bind:key="highestPriorityThumbnail.id + '_' + id"
                 v-bind:id="highestPriorityThumbnail.id + '_' + id"
                 v-bind:hasHighestPriority="true"
                 v-bind:thumbnail="highestPriorityThumbnail"
      />

      <thumbnail v-for="thumbnail in lowPriorityThumbnails"
                 v-bind:key="thumbnail.id + '_' + id"
                 v-bind:id="thumbnail.id + '_' + id"
                 v-bind:hasHighestPriority="false"
                 v-bind:thumbnail="thumbnail"
      />
    </div>
  </div>
</template>

<script>

  import CaptionToken from "./CaptionToken";
  import Thumbnail from "./Thumbnail";

  import axios from 'axios';
  import {EventBus} from "../main";

  export default {
    name: "CaptionTokenDetails",
    components: {CaptionToken, Thumbnail},
    data() {
      return {
        captionTokenObject: null,
        highestPriorityIndex: -1
      }
    },
    props: {
      captionToken: {
        type: Object,
        required: true
      },
      id: {
        required: true
      }
    },
    methods: {
      updateCaptionToken() {
        axios.get(this.$hostname + "/getCaptionToken/" + this.captionTokenObject.id).then(response => {
          this.submitSuccess(response);
        }).catch(error => {
          console.log(error);
        });
      },
      submitSuccess(response) {
        if (response.status === 200)
          this.captionTokenObject = response.data;
      }
    },
    created() {
      this.captionTokenObject = this.captionToken;
      EventBus.$on('thumbnailPriorityChanged_event', this.updateCaptionToken)
    },
    computed: {
      highestPriorityThumbnail: function () {
        let highestPrio = -100; //TODO bad style!
        let i = 0;
        for (i in this.captionTokenObject.thumbnails) {
          if (this.captionTokenObject.thumbnails[i].priority > highestPrio) {
            highestPrio = this.captionTokenObject.thumbnails[i].priority;
            this.highestPriorityIndex = i;
          }
        }

        return this.captionTokenObject.thumbnails[this.highestPriorityIndex];
      },

      lowPriorityThumbnails: function () {
        let thumbs = [];
        let i = 0;
        for (i in this.captionTokenObject.thumbnails) {
          if (i !== this.highestPriorityIndex)
            thumbs.push(this.captionTokenObject.thumbnails[i]);
        }

        return thumbs;
      }
    }
  }
</script>

<style scoped>

</style>
