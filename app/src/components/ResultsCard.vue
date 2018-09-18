<template>
  <div v-if="resultsReceived" class="col-md-6 mt-5">
    <div class="card shadow-sm">
      <div class="card-header card-title text-md-center h5">Thumbnail-annotated text</div>
      <div class="card-body">
        <template v-for="node, n in nodes">
          <crawler-result v-if="n % 2"
                          v-bind:key="nodeResultMap[node].id + '_' + n"
                          v-bind:id="nodeResultMap[node].id+ '_' + n"
                          v-bind:crawler-result="nodeResultMap[node]"
          />
          <template v-else>
            {{ node }}
          </template>
        </template>
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
        resultsReceived: false,
        userInput: 'noInput',
        nodes: null,
        nodeResultMap: null
      }
    },
    methods: {
      generateNodes() {
        // sort the results by length to enable nested caption tokens
        let sortedResults = this.resultsData;

        function compare(a, b) {
          if (a.captionTokenValue.length < b.captionTokenValue.length)
            return 1;
          if (a.captionTokenValue.length > b.captionTokenValue.length)
            return -1;
          return 0;
        }

        sortedResults.sort(compare);

        // encapsulate each CaptionToken occurrence with ~ ..... #
        let input = ' ' + this.userInput;
        let cr;
        for (cr in sortedResults)
          input = input.replace(new RegExp(' ' + sortedResults[cr].captionTokenValue, 'gm'), ' ~' + sortedResults[cr].captionTokenValue + '#');


        // remove nested occurrence marks
        input = input.replace(new RegExp(/##/g), '#');
        let reg = new RegExp(/~[\w ,'\-]+~/, 'g');
        if (reg.test(input))
          input = input.substr(0, reg.lastIndex - 1) + input.substr(reg.lastIndex);

        // split the input with occurrence marks -> each second element in the array holds a CaptionToken
        this.nodes = input.split(/~([\w ,'-]+)#/);
      },

      generateResultMap() {
        let resMap = {};
        let cr;
        for (cr in this.resultsData)
          resMap[this.resultsData[cr].captionTokenValue] = this.resultsData[cr];
        this.nodeResultMap = resMap;
      },

      receiveResultData(data) {
        this.resultsData = data;

        this.generateResultMap();

        EventBus.$emit("resultDataReady_event");
        this.resultsReceived = true;
      },
      receiveUserInput(userInput) {
        this.userInput = userInput;
        this.generateNodes();
      }
    },
    created() {
      EventBus.$on('sendResultData_event', this.receiveResultData);
      EventBus.$on('sendUserInput_event', this.receiveUserInput);
    }
  }

</script>

<style scoped>
  .card-body {
    column-count: 1;
    column-fill: balance;
  }

  .card-body span {
    display: inline-flex;
    white-space: pre-wrap;
    overflow-wrap: break-word;
  }

</style>
