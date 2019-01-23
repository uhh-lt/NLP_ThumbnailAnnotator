<template>
  <div class="card text-white bg-primary">
    <div class="card-body">
      <form @submit.prevent="submit" novalidate>
        <div class="form-group">
          <input name="username" id="username_input" class="form-control d-block mt-1" placeholder="Username" type="text" v-model.trim="form.username"/>
        </div>

        <div class="form-group">
          <input name="password" id="password_input" class="form-control d-block mt-1" placeholder="Password" type="password" v-model.trim="form.password"/>
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
        <h4>Login Failed!</h4>
      </div>

    </div>
  </div>
</template>

<script>
  import axios from 'axios';

  export default {
    name: "LoginForm",
    data() {
      return {
        form: {
          username: '',
          password: ''
        },
        access_key: '',
        login_failed: false
      }
    },
    methods: {
      submit() {
        console.log("submit");
        console.log(this.form);
        axios.post(this.$hostname + "/login/", this.form).then(response => {
          this.submitSuccess(response);
        }).catch(error => {
          this.submitError(error);
        });
      },
      submitSuccess(response) {
        console.log("asd");
        console.log(response.data.accessKey);
        if (response.status === 200) {
          this.login_failed = false;
          this.access_key = response.data.accessKey;
        } else {
          this.login_failed = true;
          this.access_key = '';
        }
      },
      submitError(error) {
        this.login_failed = true;
        this.access_key = '';
      }
    }
  }
</script>

<style scoped>

</style>
