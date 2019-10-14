<template>
  <div class="mt-5">
    <div class="card shadow-sm">
      <div class="card-header card-title text-md-center h5">
        User Input
      </div>
      <div class="card-body">
        <form
          v-if="!isSubmitted"
          class="form-group"
          novalidate
          @submit.prevent="crawlThumbnails"
        >
          <textarea
            id="additionalInfo"
            v-model.trim="authenticatedUserInputDto.value"
            :disabled="isSubmitted === true"
            :style="textAreaCursor"
            class="form-control"
            rows="14"
            name="user_input"
            title="user_input"></textarea>

          <div class="form-group">
            <button
              type="submit"
              class="btn btn-primary btn-block mt-md-2"
              :disabled="submitting"
              value="Crawl Thumbnails!"
            >
              <span v-if="submitting"><img
                id="loader"
                src="../../static/img/loader.svg"
              ></span>
              <span v-else>Crawl Thumbnails!</span>
            </button>
          </div>
        </form>
        <div v-else>
          <div
            v-if="!isError"
            @click="isSubmitted = !isSubmitted"
          >
            <div class="alert alert-success">
              <strong>Thumbnail Crawling Completed!</strong>
            </div>
            <div class="alert alert-info">
              <p><strong>Here is the input you sent:</strong></p>
              <code>
                {{ authenticatedUserInputDto }}
              </code>
            </div>
          </div>
          <div v-else>
            <div class="alert alert-danger">
              <strong>An error occurred during request to Thumbnail Crawler API!</strong>
            </div>
            <div class="alert alert-info">
              <p><strong>Error message:</strong></p>
              <code>
                {{ errorMessage }}
              </code>
            </div>
            <b-btn
              class="btn-primary w-100"
              @click="reload"
            >
              Retry
            </b-btn>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import { EventBus } from '../index'
import axios from 'axios'

export default {

  name: 'UserInputCard',

  data: function () {
    return {
      isSubmitted: false,
      isError: false,
      submitting: false,
      textAreaCursor: 'cursor: text',

      authenticatedUserInputDto: {
        value: null,
        accessKey: null
      },

      errorMessage: ''
    }
  },
  created () {
    EventBus.$on('sent_request_access_key_event', this.updateRequestAccessKey)
    console.log('UserInputCard created')
  },
  methods: {
    enableSubmitLoader () {
      this.submitting = true
      this.textAreaCursor = 'cursor: not-allowed'
    },
    disableSubmitLoader () {
      this.submitting = false
      this.textAreaCursor = 'cursor: text'
    },
    sleep (ms) {
      return new Promise(resolve => setTimeout(resolve, ms))
    },
    crawlThumbnails () {
      this.enableSubmitLoader()
      // get the AccessKey from the current user!
      EventBus.$emit('get_request_access_key')
      // wait for 250ms
      setTimeout(() => {
        const authInput = {
          accessKey: this.authenticatedUserInputDto.accessKey,
          userInput: {
            value: this.authenticatedUserInputDto.value
          }
        }

        axios.post(this.$hostname + '/crawlThumbnails/', authInput).then(response => {
          this.crawlSuccess(response)
          this.disableSubmitLoader()
        }).catch(error => {
          this.crawlError(error)
          this.disableSubmitLoader()
        })
      }, 250)
    },
    crawlSuccess (response) {
      if (response.status === 200) {
        this.isSubmitted = true
        EventBus.$emit('sendResultData_event', response.data)
        EventBus.$emit('sendUserInput_event', this.authenticatedUserInputDto.value)
      } else {
        this.isSubmitted = true
        this.isError = true
        this.errorMessage = response.status
      }
    },
    crawlError (error) {
      this.isSubmitted = true
      this.isError = true
      this.errorMessage = error
    },
    reload () {
      window.location = ''
    },
    updateRequestAccessKey (requestAccessKey) {
      if (requestAccessKey === null || requestAccessKey.length === 0) {
        if (!alert('ERROR GETTING ACCESS KEY OF CURRENT USER! REFRESHING PAGE AND LOGIN AGAIN!')) {
          window.location.reload()
        }
      }
      this.authenticatedUserInputDto.accessKey = requestAccessKey
    }
  }
}
</script>

<style scoped>
  #loader {
    max-height: 2rem;
    max-width: 2rem;
  }
</style>
