import Vue from 'vue'
import App from './App.vue'

export const EventBus = new Vue();

import BootstrapVue from 'bootstrap-vue'
Vue.use(BootstrapVue);

// Vue.prototype.$hostname = 'http://api:8081'; // use with docker-compose
Vue.prototype.$hostname = 'http://localhost:8081/thumbnail-api'; // use for localhost

import {library} from '@fortawesome/fontawesome-svg-core'
import {faUser} from '@fortawesome/free-solid-svg-icons'
import {faGithub, faVuejs} from '@fortawesome/free-brands-svg-icons'
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'

library.add(faGithub, faUser);

Vue.component('font-awesome-icon', FontAwesomeIcon);

Vue.config.productionTip = false;

new Vue({
  el: '#app',
  render: h => h(App)
});
