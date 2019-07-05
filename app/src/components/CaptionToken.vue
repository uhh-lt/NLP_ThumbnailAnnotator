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
          <b-btn v-b-toggle="context_collapse_id" class="badge badge-secondary text-monospace pointer">UDContext</b-btn>
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
</style>
