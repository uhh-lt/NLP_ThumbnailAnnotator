<template>
  <div class="card text-white bg-primary d-inline-block p-1 m-1 clearfix">
    <div class="card-body">
      <a class="float-left" href="#">
        <span class="badge badge-dark badge-pill" title="Increment priority">
            <i class="fa fa-minus-square" aria-hidden="true"></i>
        </span>
      </a>
      <span class="badge badge-danger badge-pill float-left" data-toggle="tooltip" data-placement="top" title="Priority">
            {{ this.thumbnail.priority }}
      </span>
      <a href="#">
        <span class="badge badge-dark badge-pill" title="Decrement priority">
            <i class="fa fa-plus-square" aria-hidden="true"></i>
        </span>
      </a>
      <br>
      <span class="badge badge-success">{{ thumbnail.url }}</span>
    </div>
  </div>
</template>

<script>
  export default {
    name: "ThumbnailDetails",
    props: {
      thumbnail: {
        type: Object,
        required: true
      }
    },
    methods: {
      incPrio() {
        axios.put("http://localhost:8081/api/crawlThumbnails", this.form).then(response => {
          this.submitSuccess(response);
          this.disableSubmitLoader();
        }).catch(error => {
          this.submitError(error);
          this.disableSubmitLoader();
        });
      },
      decPrio() {
        axios.post("http://localhost:8081/api/crawlThumbnails", this.form).then(response => {
          this.submitSuccess(response);
          this.disableSubmitLoader();
        }).catch(error => {
          this.submitError(error);
          this.disableSubmitLoader();
        });
      },
      submitSuccess(response) {
        console.log(response.data);
        if (response.status === 200) {
          this.isSubmitted = true;
          EventBus.$emit("resultDataReady_event", response.data)
        } else {
          console.log(response.data.errors);
          this.isSubmitted = true;
        }
      },
      submitError(error) {
        console.log(error);
        this.isSubmitted = true;
      }
    }
  }
</script>

<style scoped>

</style>
