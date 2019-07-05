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
        captionTokenObj: null,
        request_access_key: null
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
          axios.get(this.$hostname + "/getCaptionTokenById?captionTokenId=" + this.captionTokenObj.id +
            "&accessKey=" + this.request_access_key).then(response => {
            if (response.status === 200)
              this.captionTokenObj = response.data;
          }).catch(error => {
            console.log(error);
          });
        }
      },

      updatePriorities(ev) {
        // TODO find and fix bug (there is one for sure!)
        let numNonPrioritized = this.captionTokenObj.thumbnails.filter(x => x.priority === 0).length - 1;
        let numPrioritized = this.captionTokenObj.thumbnails.length - numNonPrioritized;

        // only update if the new position is somewhere in the range of the prioritized thumbnails
        if (ev.moved.newIndex <= numNonPrioritized) {
          // update the priority of the moved thumbnail for sure
          let newPriority = ev.moved.newIndex + 1;

          let updatedThumbnailId = ev.moved.element.id;
          this.setThumbnailPriority(updatedThumbnailId, newPriority, this.captionTokenObj.id);

          // check if other thumbnail priorities have to be adapted (in order to have the correct priority regarding their positions)
          let thumbIdx;
          let thumb;
          for (thumbIdx in this.captionTokenObj.thumbnails) {
            thumb = this.captionTokenObj.thumbnails[thumbIdx];
            if (thumb.id !== updatedThumbnailId && thumb.priority !== 0) {
              newPriority = numPrioritized - this.captionTokenObj.thumbnails.findIndex(t => t.id === thumb.id);
              this.setThumbnailPriority(thumb.id, newPriority, this.captionTokenObj.id);
            }
          }
        }
      },

      setThumbnailPriority(thumbnailId, priority, captionTokenId) {
        // get the AccessKey from the current user!
        EventBus.$emit("get_request_access_key");
        // wait 250ms
        setTimeout(() => {
          axios.put(this.$hostname + "/setThumbnailPriority?thumbnailId=" + thumbnailId +
            "&priority=" + priority +
            "&captionTokenId=" + captionTokenId +
            "&accessKey=" + this.request_access_key).then(response => {

            // update event for components that contain thumbnails
            EventBus.$emit("updatedThumbnail_event", response.data);
            // also update the captionToken to hold the new ordering
            this.updateCaptionToken(this.captionTokenObj.id);
          }).catch(error => {
            console.log(error);
          });
        }, 250);
      },

      updateRequestAccessKey(requestAccessKey) {
        if (requestAccessKey === null || requestAccessKey.length === 0) {
          if (!alert('ERROR GETTING ACCESS KEY OF CURRENT USER! REFRESHING PAGE AND LOGIN AGAIN!')) {
            window.location.reload();
          }
        }
        this.request_access_key = requestAccessKey;
      }

    },
    created() {
      this.captionTokenObj = this.captionToken;
      EventBus.$on("sent_request_access_key_event", this.updateRequestAccessKey);
      console.log("CaptionTokenDetails created");
    }
  }
</script>

<style scoped>

</style>
