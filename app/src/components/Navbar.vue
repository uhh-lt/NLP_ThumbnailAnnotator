<template>
  <div>
    <nav class="navbar navbar-nav-scroll navbar-dark bg-dark">
      <span class="navbar-brand mb-0 h1">Thumbnail Annotator</span>

      <ul class="navbar-nav bd-navbar-nav flex-row">
        <li class="nav-item">
          <a class="nav-link" href="https://github.com/uhh-lt/NLP_ThumbnailAnnotation/" target="_blank"
             rel="noopener"
             aria-label="GitHub">
            <font-awesome-icon size="2x" :icon="{ prefix: 'fab', iconName: 'github' }"/>
          </a>
        </li>
        <li class="nav-item ml-2">
          <a class="nav-link" href="#" rel="noopener" id="user_login_button" aria-label="Login" v-b-modal="user_login_modal">
            <font-awesome-icon size="2x" :icon="{ prefix: 'fas', iconName: 'user' }"/>
          </a>
        </li>
      </ul>
    </nav>

    <b-modal centered hide-header hide-footer :id="user_login_modal" title="a">
      <login-form/>
    </b-modal>
  </div>
</template>

<script>

  import LoginForm from "./LoginForm";

  export default {
    name: "navbar",
    components: {LoginForm},
    data() {
      return {
        user_login_modal: "user_login_modal"
      }
    }
  }
</script>

<style scoped>

</style>
