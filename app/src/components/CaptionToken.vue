<template>
  <div class="clearfix mb-0">
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
        <li v-for="(sense, n) in this.captionTokenInstance.wordNetSenses" class="mb-1">
          <code :id="sense_collapse_id + n" class="text-warning sense">{{sense}}</code>
          <b-tooltip :target="sense_collapse_id + n">
            {{sense}}
          </b-tooltip>
        </li>
      </ul>
    </b-collapse>
    <hr/>


    <div class="row mt-2">
      <div class="col-4 text-center">
        <h6>Thumbnail</h6>
      </div>
      <div class="col-8 text-center">
        <h6>Predicted Categories</h6>
      </div>
    </div>

    <div v-if="predictionReady && predictedThumbnailUrl !== null" class="row">
      <div class="col-4 text-center sense">
        <img :src="predictedThumbnailUrl" class="img-thumbnail" :alt="predictedThumbnailUrl">
      </div>

      <div class="col-8 text-left">
        <div v-for="(val, cat, i) in predictedCategories" :id="cat + '_popover_target'">
          <div class="badge-group w-100 mb-2" v-if="i === 0">
            <span v-if="i === 0" class="badge badge-success text-left w-50">
              {{cat}}
            </span>
            <span class="badge badge-success text-center w-50">
              {{val.toPrecision(8)}}
            </span>
          </div>
          <div class="badge-group w-100 mb-0" v-else>
            <span class="badge badge-info text-left w-50">
              {{cat}}
            </span>
            <span class="badge badge-info text-center w-50">
              {{val.toPrecision(8)}}
            </span>
          </div>

          <b-popover :target="cat + '_popover_target'" triggers="hover" placement="right">
            <div v-for="(feat) in predictedInfluentialFeatures[cat]" class="row">
                <div class="badge badge-dark text-left col-9">
                  {{Object.keys(feat)[0]}}
                </div>
                <div class="badge badge-dark col-3">
                  {{Object.values(feat)[0].toPrecision(5)}}
                </div>
            </div>
          </b-popover>


        </div>
      </div>
    </div>
    <div v-else class="row">
      <div class="col-12">
          <span class="badge badge-danger text-center w-100">
            MODEL NOT TRAINED YET
          </span>
      </div>
    </div>
  </div>
</template>

<script>
    import axios from 'axios';
    import {EventBus} from "../main";

    export default {
        name: "CaptionToken",
        data: function () {
            return {
                context_collapse_id: "context_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value,
                details_collapse_id: "details_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value,
                sense_collapse_id: "sense_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value,
                predictionReady: false,
                predictedCategories: null,
                predictedInfluentialFeatures: null,
                predictedThumbnailUrl: null,
                request_access_key: null
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
                // get the AccessKey from the current user!
                EventBus.$emit("get_request_access_key");
                // wait 250ms
                setTimeout(() => {
                    axios.get(this.$hostname + "/predict?captionTokenId=" + captionTokenId + "&accessKey=" + this.request_access_key).then(response => {
                        // handle empty prediction which indicates that the model is not trained yet
                        if (response.data === null || response.data.length === 0) {
                            this.predictionReady = false;
                            return;
                        }

                        let predictedThumbnailUrl = Object.keys(response.data)[0];
                        let categoryPrediction = Object.values(response.data)[0];

                        this.predictedCategories = categoryPrediction.classProbabilities;
                        this.predictedInfluentialFeatures = categoryPrediction.influentialFeatures;
                        this.predictedThumbnailUrl = predictedThumbnailUrl;
                        this.predictionReady = true;
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
            EventBus.$on("sent_request_access_key_event", this.updateRequestAccessKey);
            EventBus.$on("start_prediction_event", this.predict);
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
