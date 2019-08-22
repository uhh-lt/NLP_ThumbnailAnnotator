<template>
  <div id="app">
    <navbar/>
    <div class="container-fluid">
      <div class="row pt-md-2">
        <div class="col-md-6">
          <UserInputCard/>
        </div>
        <div v-if="!resultsReady" class="col-md-6">
          <SampleInput v-bind:siddhartha="false"/>
          <SampleInput v-bind:siddhartha="true"/>
        </div>
        <div v-if="resultsReady" class="col-md-6">
          <ResultsCard/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import 'bootstrap/dist/css/bootstrap.css'
  import 'bootstrap-vue/dist/bootstrap-vue.css'

  import {EventBus} from "./main";

  import Navbar from "./components/Navbar";
  import UserInputCard from "./components/UserInputCard"
  import ResultsCard from "./components/ResultsCard";
  import SampleInput from "./components/SampleInput";


  export default {
    name: 'app',
    components: {SampleInput, ResultsCard, UserInputCard, Navbar},
    data() {
      return {
        resultsReady: false
      }
    },
    methods: {
      showResultsHandler() {
        this.resultsReady = true;
      }
    },
    created() {
      EventBus.$on('resultDataReady_event', this.showResultsHandler);
      console.log('API Hostname: ' + this.$hostname)
    }
  }
</script>

<style>

</style>
