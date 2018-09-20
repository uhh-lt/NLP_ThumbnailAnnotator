import Vue from 'vue'
import App from './App.vue'

export const EventBus = new Vue();

import BootstrapVue from 'bootstrap-vue'

Vue.use(BootstrapVue);
// Vue.prototype.$hostname = 'http://api:8081'; // use with docker-compose
Vue.prototype.$hostname = 'http://localhost:8081'; // use for localhost

new Vue({
  el: '#app',
  render: h => h(App)
});
