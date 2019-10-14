<template>
  <div class="card text-white bg-primary container-fluid">
    <div class="card-body">
      <caption-token
        :id="id"
        :caption-token-instance="captionTokenObj"
      />
      <hr>

      <div class="card bg-info mb-0">
        <div class="card-header text-center p-0">
          Training Area
        </div>
        <div class="card-body p-1 text-center">
          <span
            v-if="trainingList.length === 0 && !training"
            class="small"
          >Drag n' Drop a Thumbnail to train your model!</span>

          <draggable
            tag="ul"
            :list="trainingList"
            group="group1"
            @change="train"
          >
            <thumbnail
              v-for="thumbnail in trainingList"
              :id="thumbnail.id + '_' + id"
              :key="thumbnail.id + '_' + id"
              :thumbnail="thumbnail"
              :caption-token-id="captionTokenObj.id"
            />
          </draggable>
        </div>
        <div
          v-if="training"
          class="card-footer p-0 text-center bg-success"
        >
          <span class="h4">Training in progress...</span>
        </div>
      </div>
    </div>
    <hr>

    <draggable
      tag="ul"
      class="p-0"
      :list="captionTokenObj.thumbnails"
      group="group1"
    >
      <thumbnail
        v-for="thumbnail in captionTokenObj.thumbnails"
        :id="thumbnail.id + '_' + id"
        :key="thumbnail.id + '_' + id"
        :thumbnail="thumbnail"
        :caption-token-id="captionTokenObj.id"
      />
    </draggable>

    <form>
      <form
        v-if="!crawlingThumbnails"
        class="form-group"
        novalidate
        @submit.prevent="crawlThumbnails"
      >
        <div class="form-group">
          <button
            type="submit"
            class="btn btn-secondary btn-block mt-md-2"
            :disabled="crawlingThumbnails"
            value="Crawl Thumbnails!"
          >
              <span v-if="crawlingThumbnails"><img
                id="loader"
                src="../../static/img/loader.svg"
              ></span>
            <span v-else>Crawl new Thumbnails!</span>
          </button>
        </div>
      </form>
    </form>

  </div>
</template>

<script>

import { EventBus } from '../index'

import CaptionToken from './CaptionToken'
import Thumbnail from './Thumbnail'
import draggable from 'vuedraggable'

import axios from 'axios'

export default {
  name: 'CaptionTokenDetails',
  components: { CaptionToken, Thumbnail, draggable },
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
      captionTokenObj: null,
      request_access_key: null,
      crawlingThumbnails: false,
      training: false,
      trainingList: []
    }
  },
  created () {
    this.captionTokenObj = this.captionToken
    EventBus.$on('sent_request_access_key_event', this.updateRequestAccessKey)
    console.log('CaptionTokenDetails created')
  },
  methods: {
    updateCaptionToken (captionTokenId) {
      if (this.captionTokenObj.id === captionTokenId) {
        axios.get(this.$hostname + '/getCaptionTokenById?captionTokenId=' + this.captionTokenObj.id +
            '&accessKey=' + this.request_access_key).then(response => {
          if (response.status === 200) { this.captionTokenObj = response.data }
        }).catch(error => {
          console.log(error)
        })
      }
    },

    trainModel (thumbnailId, captionTokenId) {
      // get the AccessKey from the current user!
      EventBus.$emit('get_request_access_key')
      // wait 250ms
      setTimeout(() => {
        axios.post(this.$hostname + '/trainModel?thumbnailId=' + thumbnailId +
            '&captionTokenId=' + captionTokenId +
            '&accessKey=' + this.request_access_key).then(response => {
          // start the prediction if there was no error
          EventBus.$emit('start_prediction_event', this.captionTokenObj.id)
        }).catch(error => {
          this.training = false
          console.log(error)
        })
      }, 250)
    },

    updateRequestAccessKey (requestAccessKey) {
      if (requestAccessKey === null || requestAccessKey.length === 0) {
        if (!alert('ERROR GETTING ACCESS KEY OF CURRENT USER! REFRESHING PAGE AND LOGIN AGAIN!')) {
          window.location.reload()
        }
      }
      this.request_access_key = requestAccessKey
    },

    train: function (evt) {
      this.training = true
      const thumbnailId = evt.added.element.id
      const captionTokenId = this.captionTokenObj.id
      this.trainModel(thumbnailId, captionTokenId)
      setTimeout(() => {
        this.training = false
        this.trainingList = []
      }, 1000)
    },

    enableSubmitLoader () {
      this.submitting = true
      this.textAreaCursor = 'cursor: not-allowed'
    },
    disableSubmitLoader () {
      this.submitting = false
      this.textAreaCursor = 'cursor: text'
    },

    crawlThumbnails () {
      this.enableSubmitLoader()

      // get the AccessKey from the current user!
      EventBus.$emit('get_request_access_key')
      // wait 250ms
      setTimeout(() => {
        const captionTokenId = this.captionTokenObj.id
        axios.get(this.$hostname + '/crawlNewThumbnails?captionTokenId=' + captionTokenId
          + '&accessKey=' + this.request_access_key).then(response => {
          this.crawlSuccess(response)
          this.disableSubmitLoader()
        }).catch(error => {
          console.log(error)
          this.disableSubmitLoader()
        })
      }, 250)
    },
    crawlSuccess (response) {
      if (response.status === 200) {
        this.captionTokenObj.thumbnails = response.data.thumbnails
      } else {
        this.crawlingThumbnails = true
      }
    },
  }
}
</script>

<style scoped>

</style>
