<template>
  <span>
    <el-button v-if="isPresentation" class="delete" type="danger" @click="openDeleteModal()">Delete</el-button>
    <el-button v-if="!isPresentation" class="delete" type="danger" icon="el-icon-delete" circle @click="openDeleteModal()"></el-button>
  </span>
</template>

<script>
  export default {
    name: 'DeleteModal',
    props: {
      typeOfDelete: String,
      deleteFunction: {
        type: Function,
      },
    },
    computed: {
      isPresentation () {
        return this.typeOfDelete == 'presentation';
      },
    },
    methods: {
      openDeleteModal() {
        const message = `This will permanently delete the ${  this.typeOfDelete  } . Continue?`;
        this.$confirm(message, 'Warning', {
          confirmButtonText: 'OK',
          cancelButtonText: 'Cancel',
          type: 'warning',
          center: true,
        }).then(() => {
          this.deleteFunction();
          this.$message({
            type: 'success',
            message: 'Delete completed',
          });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: 'Delete canceled',
          });
        });
      },
    },
  };
</script>

<style scoped>
  .delete {
    margin-left: 10px;
  }
</style>
