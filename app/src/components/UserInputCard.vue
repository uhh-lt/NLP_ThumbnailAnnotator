<template>
  <div class="col-md-6 mt-5">
    <div class="card shadow-sm">
      <div class="card-header card-title text-md-center h5">User Input</div>
      <div class="card-body">
        <form class="form-group" v-if="!isSubmitted" @submit.prevent="crawlThumbnails" novalidate>
          <textarea
            :disabled="isSubmitted === true"
            :style="textAreaCursor"
            class="form-control"
            rows="14"
            name="user_input"
            title="user_input"
            id="additionalInfo"
            v-model.trim="authenticatedUserInputDto.value">
          </textarea>

          <div class="form-group">
            <input name="access_key" id="access_key_input" class="form-control d-block mt-1" placeholder="User Access Key" type="text" v-model.trim="authenticatedUserInputDto.accessKey"/>
          </div>


          <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block mt-md-2" :disabled="submitting" value="Get Thumbnails!">
              <span v-if="submitting"><img id="loader" src="../assets/loader.svg"/></span>
              <span v-else>Crawl Thumbnails!</span>
            </button>
          </div>
        </form>
        <div v-else>
          <div v-if="!isError" @click="isSubmitted = !isSubmitted">
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
            <b-btn class="btn-primary w-100" @click="reload">Retry</b-btn>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>

  import {EventBus} from "../main";
  import axios from 'axios';

  export default {

    name: "UserInputCard",

    data: function () {
      return {
        isSubmitted: false,
        isError: false,
        submitting: false,
        textAreaCursor: 'cursor: text',

        authenticatedUserInputDto: {
          value: '',
          accessKey: ''
        },

        errorMessage: ''
      }
    },
    methods: {
      enableSubmitLoader() {
        this.submitting = true;
        this.textAreaCursor = 'cursor: not-allowed';
      },
      disableSubmitLoader() {
        this.submitting = false;
        this.textAreaCursor = 'cursor: text';
      },
      crawlThumbnails() {
        this.enableSubmitLoader();
        let authInput = {
          "accessKey": this.authenticatedUserInputDto.accessKey,
          "userInput": {
            "value": this.authenticatedUserInputDto.value
          }
        };
        axios.post(this.$hostname + "/crawlThumbnails/", authInput).then(response => {
          this.crawlSuccess(response);
          this.disableSubmitLoader();
        }).catch(error => {
          this.crawlError(error);
          this.disableSubmitLoader();
        });
      },
      crawlSuccess(response) {
        if (response.status === 200) {
          this.isSubmitted = true;
          EventBus.$emit("sendResultData_event", response.data);
          EventBus.$emit("sendUserInput_event", this.authenticatedUserInputDto.value)
        } else {
          this.isSubmitted = true;
          this.isError = true;
          this.errorMessage = response.status
        }
      },
      crawlError(error) {
        this.isSubmitted = true;
        this.isError = true;
        this.errorMessage = error;
      },
      reload() {
        window.location = ''
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
