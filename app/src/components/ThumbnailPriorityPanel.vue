<template>
  <div class="card text-white bg-primary d-inline-block m-1 clearfix">
    <div class="card-header card-title text-md-center">Edit Priority</div>
    <div class="card-body justify-content-center">
      <a class="float-left" href="#" v-on:click="incPrio">
        <span class="badge badge-dark badge-pill" title="Increment priority">
            <i class="fa fa-minus-square" aria-hidden="true">+</i>
        </span>
      </a>
      <span class="badge badge-danger badge-pill float-left" data-toggle="tooltip" data-placement="top" title="Priority">
        {{ this.thumbnailObj.priority }}
      </span>
      <a href="#" v-on:click="decPrio">
        <span class="badge badge-dark badge-pill" title="Decrement priority">
            <i class="fa fa-plus-square" aria-hidden="true">-</i>
        </span>
      </a>
    </div>
  </div>
</template>

<script>
  import {EventBus} from "../main";

  import axios from 'axios';

  export default {
    name: "ThumbnailPriorityPanel",
    props: {
      thumbnail: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        thumbnailObj: null
      }
    },
    methods: {
      incPrio() {
        axios.put(this.$hostname + "/incrementThumbnailPriority/" + this.thumbnail.id).then(response => {
          this.submitSuccess(response);
        }).catch(error => {
          this.submitError(error);
          console.log(error);
        });
      },
      decPrio() {
        axios.put(this.$hostname + "/decrementThumbnailPriority/" + this.thumbnailObj.id).then(response => {
          this.submitSuccess(response);
        }).catch(error => {
          console.log(error);
        });
      },
      submitSuccess(response) {
        if (response.status === 200)
          this.thumbnailObj = response.data;
        EventBus.$emit('thumbnailPriorityChanged_event')
      }
    },
    created() {
      this.thumbnailObj = this.thumbnail;
    }
  }
</script>

<style scoped>

</style>
