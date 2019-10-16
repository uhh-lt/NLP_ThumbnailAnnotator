import 'es6-promise/auto'
import 'babel-polyfill'

import Vue from 'vue'
import App from './App.vue'
import BootstrapVue from 'bootstrap-vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import { faUser } from '@fortawesome/free-solid-svg-icons'
import { faGithub } from '@fortawesome/free-brands-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

export const EventBus = new Vue()

Vue.use(BootstrapVue)

// Vue.prototype.$hostname = 'http://api:8081'; // use with docker-compose
Vue.prototype.$hostname = 'http://localhost:8081/thumbnail-api' // use for localhost

library.add(faGithub, faUser)

Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.config.productionTip = false

/* eslint-disable-next-line no-new */
new Vue({
  el: '#app',
  render: h => h(App)
})
