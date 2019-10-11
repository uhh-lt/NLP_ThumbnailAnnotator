<template>
  <vue-tabs
    centered
    active-tab-color="yellow"
    type="pills"
  >
    <v-tab title="Login">
      <div class="card text-white bg-primary">
        <div class="card-body">
          <form
            v-if="!logged_in"
            novalidate
            @submit.prevent="login"
          >
            <div class="form-group">
              <input
                id="login_username_input"
                v-model.trim="userDto.username"
                name="username"
                class="form-control d-block mt-1"
                placeholder="Username"
                type="text"
              >
            </div>

            <div class="form-group">
              <input
                id="login_password_input"
                v-model.trim="userDto.password"
                name="password"
                class="form-control d-block mt-1"
                placeholder="Password"
                type="password"
              >
            </div>

            <div class="form-group">
              <button
                type="submit"
                class="btn btn-warning btn-block mt-md-2"
                value="Login"
              >
                <span>Login!</span>
              </button>
            </div>
          </form>

          <div
            v-if="logged_in && this.accessKeyDto.accessKey === null"
            class="badge-danger rounded text-center"
          >
            <h6>Already logged in! <br> Please logout first!</h6>
          </div>

          <div
            v-if="!this.logout_successful && this.accessKeyDto.accessKey !== null"
            class="badge-success rounded text-center"
          >
            <h4 class="m-0">
              Login successfull!
            </h4>
            <span>AccessKey: {{ accessKeyDto.accessKey }}</span>
          </div>
          <div
            v-if="this.login_failed"
            class="badge-danger rounded text-center"
          >
            <h6>Login Failed! Wrong username or password?!</h6>
          </div>
        </div>
      </div>
    </v-tab>

    <v-tab title="Register">
      <div class="card text-white bg-primary">
        <div class="card-body">
          <form
            novalidate
            @submit.prevent="register"
          >
            <div class="form-group">
              <input
                id="register_username_input"
                v-model.trim="userDto.username"
                name="username"
                class="form-control d-block mt-1"
                placeholder="Username"
                type="text"
              >
            </div>

            <div class="form-group">
              <input
                id="register_password_input"
                v-model.trim="userDto.password"
                name="password"
                class="form-control d-block mt-1"
                placeholder="Password"
                type="password"
              >
            </div>

            <div class="form-group">
              <button
                type="submit"
                class="btn btn-warning btn-block mt-md-2"
                value="Register"
              >
                <span>Register!</span>
              </button>
            </div>
          </form>

          <div
            v-if="this.register_successful && this.register_sent"
            class="badge-success rounded text-center"
          >
            <h6 class="m-0">
              Registered successfully! You can now login...
            </h6>
          </div>

          <div
            v-if="!this.register_successful && this.register_sent"
            class="badge-success rounded text-center"
          >
            <h6 class="m-0">
              Registering was not successful because the Username was already taken!
            </h6>
          </div>

          <div
            v-if="this.register_failed && !this.register_sent"
            class="badge-danger"
          >
            <h6>There was an error with communication to the Server!</h6>
          </div>
        </div>
      </div>
    </v-tab>

    <v-tab title="Logout">
      <div class="card text-white bg-primary">
        <div class="card-body">
          <form
            v-if="logged_in"
            novalidate
            @submit.prevent="logout"
          >
            <div class="form-group">
              <button
                type="submit"
                class="btn btn-warning btn-block mt-md-2"
                value="Logout"
              >
                <span>Logout!</span>
              </button>
            </div>
          </form>

          <div
            v-if="!logged_in && this.accessKeyDto.accessKey === null"
            class="badge-danger rounded text-center"
          >
            <h6>Not logged in! <br> Please login first!</h6>
          </div>

          <div
            v-if="this.logout_successful"
            class="badge-success rounded text-center"
          >
            <h6 class="m-0">
              Logged out successfully!
            </h6>
          </div>
        </div>
      </div>
    </v-tab>
  </vue-tabs>
</template>

<script>
import axios from 'axios'
import { EventBus } from '../index'
import { VueTabs, VTab } from 'vue-nav-tabs'

export default {
  name: 'UserForm',
  components: { VueTabs, VTab },
  data () {
    return {
      userDto: {
        username: null,
        password: null
      },

      accessKeyDto: {
        accessKey: null
      },

      login_failed: false,
      logged_in: false,

      logout_successful: false,
      logout_failed: false,

      register_sent: false,
      register_successful: false,
      register_failed: false
    }
  },
  created () {
    this.resetFlags('all')
    EventBus.$on('sent_request_access_key_event', this.updateRequestAccessKey)
    EventBus.$on('sent_logged_in_flag', this.updateIsLoggedIn)
    // check if already logged in
    EventBus.$emit('is_logged_in_event')
    console.log('UserForm created')
  },
  methods: {
    resetFlags (which) {
      if (which === 'all' || which === 'login') {
        this.login_failed = false
        this.logged_in = false
      }

      if (which === 'all' || which === 'logout') {
        this.logout_successful = false
        this.logout_failed = false
      }

      if (which === 'all' || which === 'register') {
        this.register_sent = false
        this.register_successful = false
        this.register_failed = false
      }
    },
    login () {
      // check if already logged in
      EventBus.$emit('is_logged_in_event')
      // wait for 250ms
      setTimeout(() => {
        if (!this.logged_in) {
          axios.post(this.$hostname + '/login/', this.userDto).then(response => {
            this.loginSuccess(response)
          }).catch(error => {
            this.loginError(error)
          })
        } else { this.login_failed = false }
      }, 250)
    },
    loginSuccess (response) {
      if (response.status === 200) {
        this.login_failed = false
        this.accessKeyDto.accessKey = response.data.accessKey
        this.logged_in = true
      } else {
        this.login_failed = true
        this.accessKeyDto.accessKey = null
      }

      EventBus.$emit('user_logged_in_event', {
        userName: this.userDto.username,
        accessKey: this.accessKeyDto.accessKey
      })

      this.resetFlags('logout')
      this.resetFlags('register')
    },
    loginError (error) {
      this.login_failed = true
      this.accessKeyDto.accessKey = null
      this.resetFlags('logout')
      this.resetFlags('register')
    },

    register () {
      axios.put(this.$hostname + '/register/', this.userDto).then(response => {
        this.registerSuccess(response)
      }).catch(error => {
        this.registerError(error)
      })
    },
    registerSuccess (response) {
      this.register_sent = true
      if (response.status === 200 && response.data === true) {
        this.register_successful = true
        this.register_failed = false
      } else {
        this.register_successful = false
        this.register_failed = false
      }
    },
    registerError (error) {
      this.register_sent = false
      this.register_successful = false
      this.register_failed = true
    },

    logout () {
      // get the AccessKey from the current user!
      EventBus.$emit('get_request_access_key')
      // wait for 250ms
      setTimeout(() => {
        axios.post(this.$hostname + '/logout/', this.accessKeyDto).then(response => {
          this.logoutSuccess(response)
        }).catch(error => {
          this.logoutError(error)
        })
      }, 250)
    },
    logoutSuccess (response) {
      if (response.status === 200 && response.data === true) {
        this.logout_successful = true
        this.logged_in = false
      } else {
        this.logout_successful = false
        this.logout_failed = true
      }
      EventBus.$emit('user_logged_out_event')
      this.resetFlags('register')
      this.resetFlags('login')
    },
    logoutError (error) {
      this.logout_successful = false
      this.logout_failed = true
      this.resetFlags('register')
      this.resetFlags('login')
    },
    updateRequestAccessKey (requestAccessKey) {
      if (requestAccessKey === null || requestAccessKey.length === 0) {
        if (!alert('ERROR GETTING ACCESS KEY OF CURRENT USER! REFRESHING PAGE AND LOGIN AGAIN!')) {
          window.location.reload()
        }
      }
      this.accessKeyDto.accessKey = requestAccessKey
    },
    updateIsLoggedIn (flag) {
      this.logged_in = flag
    }
  }
}
</script>

<style scoped>

</style>
