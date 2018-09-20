<template>
  <div class="col-md-6 mt-5">
    <div class="card shadow-sm">
      <div class="card-header card-title text-md-center h5">User Input</div>
      <div class="card-body">
        <form class="form-group" v-if="!isSubmitted" @submit.prevent="submit" novalidate>
          <textarea
            class="form-control"
            rows="14"
            name="user_input"
            title="user_input"
            id="additionalInfo"
            v-model.trim="form.value">
          </textarea>

          <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block mt-md-2" :disabled="submitting" value="Get Thumbnails!">
              <span v-if="submitting">{{ form.submitting }} <img id="loader" src="../assets/loader.svg"/></span>
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
                {{ form }}
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
        form: {
          value: ''
        },
        errorMessage: ''
      }
    },
    methods: {
      submit() {
        this.crawlThumbnails();
      },
      enableSubmitLoader() {
        this.submitting = true;
      },
      disableSubmitLoader() {
        this.submitting = false;
      },
      crawlThumbnails() {
        this.enableSubmitLoader();
        axios.post("http://localhost:8081/api/crawlThumbnails", this.form).then(response => {
          this.submitSuccess(response);
          this.disableSubmitLoader();
        }).catch(error => {
          this.submitError(error);
          this.disableSubmitLoader();
        });
      },
      submitSuccess(response) {
        if (response.status === 200) {
          this.isSubmitted = true;
          EventBus.$emit("sendResultData_event", response.data);
          EventBus.$emit("sendUserInput_event", this.form.value)
        } else {
          this.isSubmitted = true;
          this.isError = true;
          this.errorMessage = response.status
        }
      },
      submitError(error) {
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
    max-height: 1rem;
    max-width: 1rem;
  }
</style>
