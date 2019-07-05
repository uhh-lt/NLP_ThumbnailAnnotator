<template>
  <div class="clearfix">
    <div class="row">

      <div class="col-6 text-left">
        <h5><span class="badge badge-warning text-monospace">{{ this.captionTokenInstance.value }}</span></h5>
      </div>

      <div class="col-2">
        <h6>
          <b-btn v-b-toggle="details_collapse_id" class="badge badge-secondary text-monospace pointer">Details</b-btn>
        </h6>
      </div>

      <div class="col-2">
        <h6>
          <b-btn v-b-toggle="context_collapse_id" class="badge badge-secondary text-monospace pointer">UniDeps</b-btn>
        </h6>
      </div>

      <div class="col-2">
        <h6>
          <b-btn v-b-toggle="sense_collapse_id" class="badge badge-secondary text-monospace pointer">WordNet</b-btn>
        </h6>
      </div>
    </div>

    <b-collapse :id="details_collapse_id" class="text-left text-warning">
      <hr>
      <code class="text-warning">
        <span class="w-25 d-inline-flex font-italic mb-1">Type: </span>
        <span class="w-75 text-left">
          {{this.captionTokenInstance.type}}
        </span>
      </code>
      <code class="text-warning">
        <span class="w-25 d-inline-flex font-italic mb-1">POSTags: </span>
        <span class="w-75 text-left">
          {{this.captionTokenInstance.posTags}}
        </span>
      </code>
      <code class="text-warning">
        <span class="w-25 d-inline-flex font-italic mb-1">Surface: </span>
        <span class="w-75 text-left">
          {{this.captionTokenInstance.tokens}}
        </span>
      </code>
      <code class="text-warning">
        <span class="w-25 d-inline-flex font-italic mb-1">Lemmata: </span>
        <span class="w-75 text-left">
          {{this.captionTokenInstance.lemmata}}
        </span>
      </code>
    </b-collapse>

    <b-collapse :id="context_collapse_id" class="text-left text-warning">
      <hr>
      <ul class="list-unstyled">
        <li v-for="c in this.captionTokenInstance.udContext"><code class="text-warning">{{c.type}}({{c.governor}},{{c.dependent}})</code></li>
      </ul>
    </b-collapse>

    <b-collapse :id="sense_collapse_id" class="text-left text-warning">
      <hr>
      <ul class="list-unstyled">
        <li v-for="sense, n in this.captionTokenInstance.wordNetSenses" class="mb-1">
          <code :id="sense_collapse_id + n" class="text-warning sense">{{sense}}</code>
          <b-tooltip :target="sense_collapse_id + n">
            {{sense}}
          </b-tooltip>
        </li>
      </ul>
    </b-collapse>
    <hr/>

    <!--   START PREDICTION -->
    <div v-if="predictionReady" class="row mt-2">
      <div class="col-6 text-center">
        <h6>Predicted Thumbnail</h6>
      </div>
      <div class="col-6 text-center">
        <h6>Predicted Categories</h6>
      </div>
    </div>
    <div v-if="predictionReady" class="row">
      <div class="col-6 text-center sense">
        <!--        <img :src="captionTokenInstance.thumbnails[0].url" class="img-thumbnail" :alt="captionTokenInstance.thumbnails[0].url">-->
        <img :src="predictedThumbnailUrl" class="img-thumbnail" :alt="predictedThumbnailUrl">
      </div>

      <div class="col-6 text-left">
        <div class="h6 m-0" v-for="k, v, i in this.predictedCategory">

          <div class="badge-group w-100" v-if="i === 0">
            <span v-if="i === 0" class="badge badge-success mb-1 text-left w-50">
              {{v}}
            </span>
            <span class="badge badge-success mb-1 text-right w-50">
              {{k.toPrecision(3)}}
            </span>
          </div>
          <div class="badge-group w-100" v-else>
            <span class="badge badge-info text-left w-50">
              {{v}}
            </span>
            <span class="badge badge-info text-right w-50">
              {{k.toPrecision(3)}}
            </span>
          </div>
        </div>
      </div>
    </div>
    <!--    END PREDICTION -->
  </div>
</template>

<script>
  import {EventBus} from "../main";
  import axios from 'axios';

  export default {
    name: "CaptionToken",
    data: function () {
      return {
        context_collapse_id: "context_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value,
        details_collapse_id: "details_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value,
        sense_collapse_id: "sense_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value,
        predictionReady: false,
        predictedCategory: null,
        predictedThumbnailUrl: null,
      }
    },
    props: {
      captionTokenInstance: {
        type: Object,
        required: true
      },
      id: {
        required: true
      }
    },
    methods: {
      predict(captionTokenId) {
        axios.get(this.$hostname + "/predict?captionTokenId=" + captionTokenId).then(response => {
          let firstPredictedThumbnailId = Object.keys(response.data[0])[0];
          let firstPrediction = Object.values(response.data[0])[0];

          let predictedCategories = firstPrediction.classProbabilities;

          console.log("predictedCategory: " + predictedCategories);
          console.log("predictedThumbnailId: " + firstPredictedThumbnailId);

          this.predictedCategory = predictedCategories;
          this.getThumbnailUrl(firstPredictedThumbnailId);

        }).catch(error => {
          console.log(error);
        });
      },

      getThumbnailUrl(thumbnailId) {
        axios.get(this.$hostname + "/getThumbnailById?thumbnailId=" + thumbnailId).then(response => {
          this.predictedThumbnailUrl = response.data.url;
          this.predictionReady = true;
        }).catch(error => {
          console.log(error);
        });
      }
    },
    created() {
      this.predict(this.captionTokenInstance.id);
      console.log("CaptionToken created");
    }
  }
</script>

<style scoped>
  code {
    font-weight: bold;
    text-align: left;
    display: block;
    font-size: small;
  }

  .pointer {
    cursor: pointer;
  }

  .sense {
    text-overflow: ellipsis;
    overflow: hidden;
  }


  .badge-group {
    position: relative;
    display: inline-flex;
    vertical-align: middle;
  }

  .badge-group > .badge {
    flex: 0 1 auto;
  }

  .badge-group > .badge:first-child {
    margin-left: 0;
  }

  .badge-group > .badge:not(:last-child) {
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
  }

  .badge-group > .badge:not(:first-child) {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
  }
</style>
