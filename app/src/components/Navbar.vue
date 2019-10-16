<template>
  <div>
    <nav class="navbar navbar-nav-scroll navbar-dark bg-dark">
      <span class="navbar-brand mb-0 h1">Thumbnail Annotator</span>

      <ul class="navbar-nav bd-navbar-nav flex-row">
        <li class="nav-item">
          <a
            class="nav-link"
            href="https://github.com/uhh-lt/NLP_ThumbnailAnnotator/"
            target="_blank"
            rel="noopener"
            aria-label="GitHub"
          >
            <font-awesome-icon
              size="2x"
              :icon="{ prefix: 'fab', iconName: 'github' }"
            />
          </a>
        </li>
        <li class="nav-item ml-2">
          <a
            id="user_login_button"
            v-b-modal="user_login_modal"
            class="nav-link"
            href="#"
            rel="noopener"
            aria-label="Login"
          >
            <font-awesome-icon
              size="2x"
              :icon="{ prefix: 'fas', iconName: 'user' }"
            />
          </a>
        </li>
        <li class="nav-item ml-2 my-auto">
          <span class="badge badge-warning">
            Logged in as: <br>
            <h6
              v-if="current_user.userName !== null"
              class="m-0 "
            >{{ current_user.userName }}</h6>
            <h6
              v-else="current_user.userName"
              class="m-0 "
            >NO USER</h6>
          </span>
        </li>
      </ul>
    </nav>

    <b-modal
      :id="user_login_modal"
      centered
      hide-header
      hide-footer
      title="a"
    >
      <user-form />
    </b-modal>
  </div>
</template>

<script>

import UserForm from './UserForm'
import { EventBus } from '../index'
import CaptionToken from './CaptionToken'

export default {
  name: 'Navbar',
  components: { UserForm },
  data () {
    return {
      user_login_modal: 'user_login_modal',

      current_user: {
        userName: null,
        accessKey: null
      }
    }
  },
  created () {
    EventBus.$on('user_logged_in_event', this.updateCurrentUser)
    EventBus.$on('user_logged_out_event', this.resetCurrentUser)
    EventBus.$on('get_request_access_key', this.sendAccessKey)
    EventBus.$on('is_logged_in_event', this.sendLoggedInFlag)
  },
  methods: {
    updateCurrentUser (userData) {
      this.current_user = userData
    },
    resetCurrentUser () {
      this.current_user.accessKey = null
      this.current_user.userName = null
    },
    sendAccessKey () {
      EventBus.$emit('sent_request_access_key_event', this.current_user.accessKey)
    },
    sendLoggedInFlag () {
      EventBus.$emit('sent_logged_in_flag', this.current_user.accessKey !== null)
    }
  }
}
</script>

<style scoped>

</style>
