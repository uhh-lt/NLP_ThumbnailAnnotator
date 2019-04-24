<template>
  <div class="card text-white bg-primary">
    <div class="card-body">
      <caption-token
        v-bind:captionTokenInstance="captionTokenObj"
        v-bind:id="id"
      />

      <hr>
      <draggable v-model="captionTokenObj.thumbnails" @change="updatePriorities">
        <thumbnail v-for="thumbnail in captionTokenObj.thumbnails"
                   v-bind:key="thumbnail.id + '_' + id"
                   v-bind:id="thumbnail.id + '_' + id"
                   v-bind:thumbnail="thumbnail"
                   v-bind:captionTokenId="captionTokenObj.id"
        />
      </draggable>


    </div>
  </div>
</template>

<script>

  import {EventBus} from "../main";

  import CaptionToken from "./CaptionToken";
  import Thumbnail from "./Thumbnail";
  import draggable from 'vuedraggable'

  import axios from 'axios';

  export default {
    name: "CaptionTokenDetails",
    components: {CaptionToken, Thumbnail, draggable},
    data() {
      return {
        captionTokenObj: null
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
    computed: {
      idToIdxMap: function () {
        let map = {}

        let i = 0;
        let t = null;
        for (t in this.thumbnails)
          map[t.id] = i++;

        return map
      }
    },
    methods: {
      updateCaptionToken(captionTokenId) {
        if (this.captionTokenObj.id === captionTokenId) {
          axios.get(this.$hostname + "/getCaptionToken/" + this.captionTokenObj.id).then(response => {
            if (response.status === 200)
              this.captionTokenObj = response.data;
          }).catch(error => {
            console.log(error);
          });
        }
      },
      updatePriorities(ev) {
        let nonPrioritized = this.captionTokenObj.thumbnails.filter(x => x.priority === 0).length - 1;

        // update the priority of the moved thumbnail for sure
        let updatedThumbnailId = ev.moved.element.id;
        let newPriority = this.captionTokenObj.thumbnails.length - ev.moved.newIndex - nonPrioritized;
        this.setThumbnailPriority(updatedThumbnailId, newPriority, this.captionTokenObj.id);


        // check if other thumbnail priorities have to be adapted (in order to have the correct priority regarding their positions)
        let thumb = null;
        for (thumb in this.captionTokenObj.thumbnails) {
          if (this.captionTokenObj.thumbnails[thumb].id !== updatedThumbnailId && this.captionTokenObj.thumbnails[thumb].priority !== 0) {
            let newPriority = this.captionTokenObj.thumbnails.length - this.captionTokenObj.thumbnails.findIndex(t => t.id === this.captionTokenObj.thumbnails[thumb].id) - nonPrioritized;
            this.setThumbnailPriority(this.captionTokenObj.thumbnails[thumb].id, newPriority, this.captionTokenObj.id);
          }
        }
      },
      setThumbnailPriority(thumbnailId, priority, captionTokenId) {
        axios.put(this.$hostname + "/setThumbnailPriority?id=" + thumbnailId + "&priority=" + priority + "&captionTokenId=" + captionTokenId).then(response => {
          // update event for components that contain thumbnails
          EventBus.$emit("updatedThumbnail_event", response.data);
          // also update the captionToken to hold the new ordering
          this.updateCaptionToken(this.captionTokenObj.id);
        }).catch(error => {
          console.log(error);
        });
      },
    },
    created() {
      this.captionTokenObj = this.captionToken;
    }
  }
</script>

<style scoped>

</style>
