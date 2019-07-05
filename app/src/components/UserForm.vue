<template>
  <vue-tabs centered activeTabColor="yellow" type="pills">
    <v-tab title="Login">
      <div class="card text-white bg-primary">
        <div class="card-body">
          <form @submit.prevent="login" novalidate>
            <div class="form-group">
              <input name="username" id="login_username_input" class="form-control d-block mt-1" placeholder="Username" type="text" v-model.trim="userDto.username"/>
            </div>

            <div class="form-group">
              <input name="password" id="login_password_input" class="form-control d-block mt-1" placeholder="Password" type="password" v-model.trim="userDto.password"/>
            </div>

            <div class="form-group">
              <button type="submit" class="btn btn-warning btn-block mt-md-2" value="Login">
                <span>Login!</span>
              </button>
            </div>
          </form>

          <div class="badge-success rounded text-center" v-if="this.accessKeyDto.accessKey !== ''">
            <h4 class="m-0">Login successfull!</h4>
            <span>AccessKey: {{accessKeyDto.accessKey}}</span>
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
              <input name="username" id="register_username_input" class="form-control d-block mt-1" placeholder="Username" type="text" v-model.trim="userDto.username"/>
            </div>

            <div class="form-group">
              <input name="password" id="register_password_input" class="form-control d-block mt-1" placeholder="Password" type="password" v-model.trim="userDto.password"/>
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

    <v-tab title="Logout">
      <div class="card text-white bg-primary">
        <div class="card-body">
          <form @submit.prevent="logout" novalidate>
            <div class="form-group">
              <input name="access_key" id="logout_access_key" class="form-control d-block mt-1" placeholder="Access Key" type="text" v-model.trim="accessKeyDto.accessKey"/>
            </div>

            <div class="form-group">
              <button type="submit" class="btn btn-warning btn-block mt-md-2" value="Logout">
                <span>Logout!</span>
              </button>
            </div>
          </form>

          <div class="badge-success rounded text-center" v-if="this.logout_successful">
            <h6 class="m-0">Logged out successfully!</h6>
          </div>

          <div class="badge-success rounded text-center" v-if="!this.logout_successful && this.logout_failed">
            <h6 class="m-0">Error while logging out! Wrong Access Key?!</h6>
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
        userDto: {
          username: '',
          password: ''
        },

        accessKeyDto: {
          accessKey: ''
        },

        login_failed: false,

        logout_successful: false,
        logout_failed: false,

        register_sent: false,
        register_successful: false,
        register_failed: false
      }
    },
    methods: {
      resetFlags(which) {
        if(which === "all" || which === "login")
          this.login_failed = false;

        if(which === "all" || which === "logout") {
          this.logout_successful = false;
          this.logout_failed = false;
        }

        if(which === "all" || which === "register") {
          this.register_sent = false;
          this.register_successful = false;
          this.register_failed = false;
        }
      },
      login() {
        axios.post(this.$hostname + "/login/", this.userDto).then(response => {
          this.loginSuccess(response);
        }).catch(error => {
          this.loginError(error);
        });
      },
      loginSuccess(response) {
        if (response.status === 200) {
          this.login_failed = false;
          this.accessKeyDto.accessKey = response.data.accessKey;
        } else {
          this.login_failed = true;
          this.accessKeyDto.accessKey = '';
        }
        this.resetFlags("logout");
        this.resetFlags("register");
      },
      loginError(error) {
        this.login_failed = true;
        this.accessKeyDto.accessKey = '';
        this.resetFlags("logout");
        this.resetFlags("register");
      },

      register() {
        axios.put(this.$hostname + "/register/", this.userDto).then(response => {
          this.registerSuccess(response);
        }).catch(error => {
          this.registerError(error);
        });
      },
      registerSuccess(response) {
        this.register_sent = true;
        if (response.status === 200 && response.data === true) {
          this.register_successful = true;
          this.register_failed = false;
        } else {
          this.register_successful = false;
          this.register_failed = false;
        }
        this.resetFlags("logout");
        this.resetFlags("login");
      },
      registerError(error) {
        this.register_sent = false;
        this.register_successful = false;
        this.register_failed = true;
        this.resetFlags("logout");
        this.resetFlags("login");
      },

      logout() {
        console.log(this.accessKeyDto);
        axios.post(this.$hostname + "/logout/", this.accessKeyDto).then(response => {
          this.logoutSuccess(response);
        }).catch(error => {
          this.logoutError(error);
        });
      },
      logoutSuccess(response) {
        if (response.status === 200 && response.data === true) {
          this.logout_successful = true;
        } else {
          this.logout_successful = false;
          this.logout_failed = true;
        }
          this.resetFlags("register");
        this.resetFlags("login");
      },
      logoutError(error) {
        this.logout_successful = false;
        this.logout_failed = true;
        this.resetFlags("register");
        this.resetFlags("login");
      }
    },
    created() {
      this.resetFlags("all");
    }
  }
</script>

<style scoped>

</style>
