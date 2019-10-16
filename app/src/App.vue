<template>
  <div id="app">
    <navbar />
    <div class="container-fluid">
      <div class="row pt-md-2">
        <div class="col-md-6">
          <UserInputCard />
        </div>
        <div
          v-if="!resultsReady"
          class="col-md-6"
        >
          <SampleInput :siddhartha="false" />
        </div>
        <div class="col-md-6">
          <ResultsCard />
        </div>
      </div>
      <div class="row">
        <div
          v-if="resultsReady"
          class="col-md-12">
          <SampleInput :siddhartha="false" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import { EventBus } from './index'

import Navbar from './components/Navbar'
import UserInputCard from './components/UserInputCard'
import ResultsCard from './components/ResultsCard'
import SampleInput from './components/SampleInput'

export default {
  name: 'App',
  components: { SampleInput, ResultsCard, UserInputCard, Navbar },
  data () {
    return {
      resultsReady: false
    }
  },
  created () {
    EventBus.$on('resultDataReady_event', this.showResultsHandler)
    console.log('API Hostname: ' + this.$hostname)
  },
  methods: {
    showResultsHandler () {
      this.resultsReady = true
    }
  }
}
</script>

<style>

</style>
