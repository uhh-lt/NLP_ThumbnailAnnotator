import Vue from 'vue'
import App from './App.vue'

// Vue.config.crawlThumbnailUrl = "localhost:8080/api/crawlThumbnails";
// Vue.config.incrementThumbnailUrl = "localhost:8080/api/decrementThumbnailPriority/";
// Vue.config.decrementThumbnailUrl = "localhost:8080/api/incrementThumbnailPriority";

export const EventBus = new Vue();

import BootstrapVue from 'bootstrap-vue'

Vue.use(BootstrapVue);

new Vue({
  el: '#app',
  render: h => h(App)
});
