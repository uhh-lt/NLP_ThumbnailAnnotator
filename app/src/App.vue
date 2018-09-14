<template>
  <div id="app">
    <navbar/>
    <div class="container-fluid">
      <div class="row pt-md-2">
        <UserInputCard/>
        <SampleInput v-if="!resultsReady"/>
        <ResultsCard />
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
        console.log("App::resultsReady")
        this.resultsReady = true;
      }
    },
    created() {
      EventBus.$on('resultDataReady_event', this.showResultsHandler);
    }
  }
</script>

<style>

</style>
