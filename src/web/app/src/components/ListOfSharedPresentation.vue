<template>
  <div>
    <h4>Presentations Shared with me</h4>
    <el-menu :default-active="$route.path" v-loading="isLoading" router>
      <li v-for="presentation in sharedPresentations" :key="presentation.id">
        <el-menu-item :index="`/analyze/${presentation.id}`">
          <i class="el-icon-document"></i>
          <span slot="title">
          {{ presentation.name }}
        </span>
        </el-menu-item>
      </li>
    </el-menu>
  </div>
</template>
<script>
  export default {
    name: 'ListOfSharedPresentation',
    data() {
      return {}
    },
    watch: {
      'isError'() {
        if (!this.isError) {
          return
        }
        this.$notify.error({
          title: 'Shared Presentation list API request fail',
          message: this.$store.state.presentation.sharedPresentationListStatus.apiErrorMsg,
          duration: 0
        });
      }
    },
    computed: {
      isLoading() {
        return this.$store.state.presentation.presentationListStatus.isLoading
          || this.$store.state.presentation.presentationFormStatus.isLoading
          || this.$store.state.section.sectionListStatus.isLoading
          || this.$store.state.section.sectionList.some(s => s.status.isLoading)
      },
      sharedPresentations() {
        return this.$store.state.presentation.sharedPresentationList
      },
      isError() {
        return this.$store.state.presentation.presentationListStatus.isApiError
      },
    },
    mounted() {
      this.$store.dispatch('getSharedPresentationList')
    }
  }
</script>