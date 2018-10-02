<template>
  <div class="clearfix">
    <div class="row">
      <div class="col-6 text-left">
        <h5><span class="badge badge-warning text-monospace">{{ this.captionTokenInstance.value }}</span></h5>
      </div>
      <div class="col-2">
        <h6>
          <b-btn v-b-toggle="context_collapse_id" class="badge badge-secondary text-monospace pointer">Context</b-btn>
        </h6>
      </div>
      <div class="col-2">
        <h6>
          <b-btn v-b-toggle="details_collapse_id" class="badge badge-secondary text-monospace pointer">Details</b-btn>
        </h6>

      </div>
      <div class="col-2">
        <h6>
          <b-btn v-b-toggle="sense_collapse_id" class="badge badge-secondary text-monospace pointer">Sense</b-btn>
        </h6>

      </div>
    </div>

    <br/>

    <b-collapse :id="details_collapse_id" class="text-left text-warning">
      <hr>
      <code class="text-warning">{{this.captionTokenInstance.type}}</code>
      <code class="text-warning">{{this.captionTokenInstance.posTags}}</code>
      <code class="text-warning">{{this.captionTokenInstance.tokens}}</code>
    </b-collapse>


    <b-collapse :id="context_collapse_id" class="text-left text-warning">
      <hr>
      <ul class="list-unstyled">
        <li v-for="c in this.captionTokenInstance.udContext"><code class="text-warning">{{c.type}}({{c.governor}},{{c.dependent}})</code></li>
      </ul>
    </b-collapse>


    <b-collapse :id="sense_collapse_id" class="text-warning">
      <hr>
      <p class="sense">{{this.captionTokenInstance.wordNetSense}}</p>
    </b-collapse>
  </div>
</template>

<script>
  export default {
    name: "CaptionToken",
    data: function () {
      return {
        context_collapse_id: "context_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value,
        details_collapse_id: "details_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value,
        sense_collapse_id: "sense_collapse" + "_" + this.id + "_" + this.captionTokenInstance.value
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
    }
  }
</script>

<style scoped>
  code {
    font-weight: bold;
    text-align: left;
    font-size: medium;
    display: block;
  }

  .pointer {
    cursor: pointer;
  }

  .sense {
    /*overflow-wrap: break-word !important;*/

    /*word-wrap:break-word !important;*/
    /*word-break:break-word !important;*/

    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    border: 1px solid red;
    /*overflow: scroll;*/
  }
</style>
