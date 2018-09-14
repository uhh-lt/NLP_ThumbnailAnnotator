<template>
  <div v-if="resultsReceived" class="col-md-6 mt-5">
    <div class="card shadow-sm">
      <div class="card-header card-title text-md-center h5">Thumbnail-annotated text</div>
      <div class="card-body">
        <crawler-result
          v-for="result in this.resultsData"
          v-bind:key="result.id"
          v-bind:crawler-result="result"
        />
      </div>
    </div>
  </div>
</template>

<script>

  import {EventBus} from "../main";
  import CrawlerResult from "./CrawlerResult";

  export default {
    name: "ResultsCard",
    components: {CrawlerResult},
    data() {
      return {
        resultsData: 'noData',
        resultsReceived: false
      }
    },
    methods: {
      receiveResultData(data) {
        console.log("ResultsCard::receiveResultData")
        this.resultsData = data;
        this.resultsReceived = true;
        EventBus.$emit("resultDataReady_event");
      }
    },
    created() {
      EventBus.$on('resultDataReceived_event', this.receiveResultData);
    }
  }

</script>

<style scoped>

</style>
