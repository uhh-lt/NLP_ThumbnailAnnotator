<template>
  <vue-tabs centered activeTabColor="yellow" type="pills">
    <v-tab title="Login">
      <div class="card text-white bg-primary">
        <div class="card-body">
          <form @submit.prevent="login" novalidate>
            <div class="form-group">
              <input name="username" id="login_username_input" class="form-control d-block mt-1" placeholder="Username" type="text" v-model.trim="form.username"/>
            </div>

            <div class="form-group">
              <input name="password" id="login_password_input" class="form-control d-block mt-1" placeholder="Password" type="password" v-model.trim="form.password"/>
            </div>

            <div class="form-group">
              <button type="submit" class="btn btn-warning btn-block mt-md-2" value="Login">
                <span>Login!</span>
              </button>
            </div>
          </form>

          <div class="badge-success rounded text-center" v-if="this.access_key !== ''">
            <h4 class="m-0">Login successfull!</h4>
            <span>AccessKey: {{this.access_key}}</span>
          </div>
          <div v-if="this.login_failed" class="badge-danger">
            <h4>Login Failed! Wrong username or password?!</h4>
          </div>

        </div>
      </div>
    </v-tab>

    <v-tab title="Register">
      <div class="card text-white bg-primary">
        <div class="card-body">
          <form @submit.prevent="register" novalidate>
            <div class="form-group">
              <input name="username" id="register_username_input" class="form-control d-block mt-1" placeholder="Username" type="text" v-model.trim="form.username"/>
            </div>

            <div class="form-group">
              <input name="password" id="register_password_input" class="form-control d-block mt-1" placeholder="Password" type="password" v-model.trim="form.password"/>
            </div>

            <div class="form-group">
              <button type="submit" class="btn btn-warning btn-block mt-md-2" value="Register">
                <span>Register!</span>
              </button>
            </div>
          </form>

          <div class="badge-success rounded text-center" v-if="this.register_successful && this.register_sent">
            <h6 class="m-0">Registered successfully! You can now login...</h6>
          </div>

          <div class="badge-success rounded text-center" v-if="!this.register_successful && this.register_sent">
            <h6 class="m-0">Registering was not successful because the Username was already taken!</h6>
          </div>

          <div v-if="this.register_failed && !this.register_sent" class="badge-danger">
            <h6>There was an error with communication to the Server!</h6>
          </div>

        </div>
      </div>
    </v-tab>
  </vue-tabs>
</template>

<script>
  import axios from 'axios';
  import {VueTabs, VTab} from 'vue-nav-tabs'

  export default {
    name: "UserForm",
    components: {VueTabs, VTab},
    data() {
      return {
        form: {
          username: '',
          password: ''
        },

        access_key: '',
        login_failed: false,

        register_sent: false,
        register_successful: false,
        register_failed: false
      }
    },
    methods: {
      login() {
        axios.post(this.$hostname + "/login/", this.form).then(response => {
          this.loginSuccess(response);
        }).catch(error => {
          this.loginError(error);
        });
      },
      loginSuccess(response) {
        if (response.status === 200) {
          this.login_failed = false;
          this.access_key = response.data.accessKey;
        } else {
          this.login_failed = true;
          this.access_key = '';
        }
      },
      loginError(error) {
        this.login_failed = true;
        this.access_key = '';
      },
      register() {
        axios.put(this.$hostname + "/register/", this.form).then(response => {
          this.registerSuccess(response);
        }).catch(error => {
          this.registerError(error);
        });
      },
      registerSuccess(response) {
        this.register_sent = true;
        if (response.status === 200 && response.data === true) {
          this.register_successful = true;
          this.register_failed = true;
        } else {
          this.register_successful = false;
          this.register_failed = false;
        }
      },
      registerError(error) {
        this.register_sent = false;
        this.register_successful = false;
        this.register_failed = true;
      }
    }
  }
</script>

<style scoped>

</style>
