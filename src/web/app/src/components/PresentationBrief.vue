<template>
  <el-alert v-if="isNewPresentation && !isLogin" title="Please login to create new presentation" type="error" show-icon
            class="errorMsg"/>
  <el-form v-else label-position="right" ref="presentationForm" label-width="120px" :rules="rules"
           :model="presentationForm" v-loading="isLoading">
    <el-alert v-if="isError" :title="apiErrorMsg" type="error" show-icon class="errorMsg"/>
    <el-alert v-if="hasDuplicateName" :title="duplicatePresentationErrorMsg" type="error" show-icon class="errorMsg"/>
    <el-form-item label="Name" :prop=" isInEditMode ? 'name' : ''">
      <div v-if="!isInEditMode">{{ presentationForm.name }}</div>
      <el-input v-model="presentationFormName" v-if="isInEditMode"/>
    </el-form-item>
    <el-form-item label="Access Control" v-if="!isNewPresentation">
      <el-tag>Created by {{ presentationForm.creatorIdentifier }}</el-tag>
      <el-button type="success" size="small" class="share_button_left_margin" icon="el-icon-view"
                 @click="openAccessControlPanel()" v-if="isLogin && isPresentationEditable">SHARE
      </el-button>
    </el-form-item>
    <el-dialog title="Share with other users:" :visible.sync="isAccessControlDialogVisible" width="70%"
               :close-on-click-modal="false">
      <access-control-panel :presentationId="id"></access-control-panel>
    </el-dialog>
    <el-form-item label="Description">
      <div v-if="!isInEditMode" id="presentation-description">{{ presentationForm.description }}</div>
      <el-input v-model="presentationFormDescription" v-if="isInEditMode"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="downloadPDF(false)" v-if="!isInEditMode && !isNewPresentation">Download as PDF
      </el-button>
      <el-button type="primary" @click="downloadPDF(true)" v-if="!isInEditMode && !isNewPresentation">Download in Presentation Format
      </el-button>
      <el-button type="primary" @click="changeEditMode(true)" v-if="!isInEditMode && isPresentationEditable">Edit
      </el-button>
      <el-button type="primary" @click="deleteSectionsWithoutData()" v-if="!isInEditMode && isPresentationEditable">Remove Sections without data
      </el-button>
      <el-button type="primary" @click="addPresentation()" v-if="isInEditMode && !hasDuplicateName">Save</el-button>
      <el-button type="primary" plain disabled @click="addPresentation()" v-if="isInEditMode && hasDuplicateName">Save</el-button>
      <el-button type="info" @click="changeEditMode(false)" v-if="isInEditMode && !isNewPresentation">Cancel</el-button>
      <delete-modal
        v-if="!isNewPresentation && isLogin && isPresentationEditable"
        typeOfDelete="presentation"
        :deleteFunction="this.deletePresentation">
      </delete-modal>
    </el-form-item>
  </el-form>
</template>

<script>
  import AccessControlPanel from '@/components/AccessControlPanel';
  import DeleteModal from '@/components/common/DeleteModal';
  import {download, downloadAsPresentation} from '@/store/helpers/pdfDownloader';
  import {AccessLevel, ID_NEW_PRESENTATION, SPECIAL_IDENTIFIER_PUBLIC} from '@/common/const';
  import {deepCopy} from '@/common/utility';

  export default {
    name: 'PresentationBrief',
    props: {
      id: String,
    },
    mounted() {
      this.updatePresentationForm();
    },
    watch: {
      'id'() {
        this.updatePresentationForm();
      },
    },
    computed: {
      isLogin() {
        return this.$store.state.userInfo.isLogin;
      },
      isPresentationEditable() {
        return this.$store.state.presentation.isPresentationEditable;
      },

      presentationForm() {
        return {
          name: this.presentationFormName,
          creatorIdentifier: this.presentationFormCreatorIdentifier,
          description: this.presentationFormDescription,
        };
      },
      presentationFormName: {
        get() {
          return this.$store.state.presentation.presentationForm.name;
        },
        set(value) {
          this.$store.commit('setPresentationFormField', {
            field: 'name',
            value,
          });
        },
      },
      presentationFormCreatorIdentifier() {
        return this.$store.state.presentation.presentationForm.creatorIdentifier;
      },
      presentationFormDescription: {
        get() {
          return this.$store.state.presentation.presentationForm.description;
        },
        set(value) {
          this.$store.commit('setPresentationFormField', {
            field: 'description',
            value,
          });
        },
      },
      isNewPresentation() {
        return this.id === ID_NEW_PRESENTATION;
      },
      isInEditMode() {
        return this.isEditing || this.isNewPresentation;
      },
      isLoading() {
        return this.$store.state.presentation.presentationFormStatus.isLoading;
      },
      isError() {
        return this.$store.state.presentation.presentationFormStatus.isApiError;
      },
      apiErrorMsg() {
        return this.$store.state.presentation.presentationFormStatus.apiErrorMsg;
      },
      duplicatePresentationErrorMsg() {
        return 'Cannot have duplicate presentation names';
      },
      hasDuplicateName() {
          var presentation;
          for (presentation of this.$store.state.presentation.sharedPresentationList) {
              if (presentation.id == this.id) {
                  return false;
              }
          }
          for (presentation of this.$store.state.presentation.presentationList) {
              if (presentation.name === this.presentationFormName && presentation.id != this.id) {
                  return true;
              }
          }
          return false;
      },
    },
    data() {
      return {
        isEditing: false,
        isAccessControlDialogVisible: false,
        rules: {
          name: [
            {required: true, message: 'Please enter presentation name', trigger: 'blur'},
            {min: 3, message: 'The length should be more than 3 character', trigger: 'blur'},
          ],
        },
      };
    },
    methods: {
      changeEditMode(isEditing) {
        if (isEditing === false) {
          this.updatePresentationForm();
        }
        this.isEditing = isEditing;
      },

      openAccessControlPanel() {
        this.isAccessControlDialogVisible = true;
      },

      addPresentation() {
        this.$refs['presentationForm'].validate((valid) => {
          if (!valid) {
            return;
          }
          this.$refs['presentationForm'].clearValidate();
          if (this.isNewPresentation) {
            // add
            this.$store.dispatch('savePresentation')
              .then(() => {
                if (this.isError) {
                  return;
                }
                // redirect to the newly added presentation
                this.$router.push({
                  name: 'analyze',
                  params: {
                    id: this.$store.state.presentation.presentationForm.id,
                  },
                });
              });
          } else {
            // edit
            this.$store.dispatch('updatePresentation')
              .then(() => {
                if (this.isError) {
                  return;
                }
                this.isEditing = false;
              });
          }
        });
      },
      deletePresentation() {
        this.$store.dispatch('deletePresentation', this.id)
          .then(() => {
            if (this.isError) {
              return;
            }
            this.$router.replace({
              name: 'analyze',
              params: {
                id: ID_NEW_PRESENTATION,
              },
            });
            this.isEditing = false;
          });
      },
      updatePresentationForm() {
        if (this.$refs['presentationForm']) {
          this.$refs['presentationForm'].clearValidate();
        }
        this.$store.commit('resetPresentationForm');
        if (this.id !== ID_NEW_PRESENTATION) {
          this.$store.dispatch('getPresentation', this.id)
            .then(() => {
              // check writable or not
              this.$store.dispatch('fetchAccessControlList', this.id)
                .then(() => {
                  const currentUser = this.$store.state.userInfo.userEmail;
                  const accessControlList = this.$store.state.accessControl.accessControlList;
                  const isPresentationEditable =
                    currentUser === this.presentationFormCreatorIdentifier
                    || accessControlList.some(acl => acl.userIdentifier === currentUser && acl.accessLevel === AccessLevel.CAN_WRITE)
                    || accessControlList.some(acl => acl.userIdentifier === SPECIAL_IDENTIFIER_PUBLIC && acl.accessLevel === AccessLevel.CAN_WRITE);
                  this.$store.commit('setIsPresentationEditable', isPresentationEditable);
                });
            });
        }
      },
      downloadPDF(asPresentation) {
        const vm = this;
        const wasPresentationEditable = deepCopy(vm.isPresentationEditable);
        vm.$store.commit('setIsPresentationEditable', false);
        vm.$store.commit('setPageLoadingStatus', true);

        let downloadFunc = download;
        if (asPresentation) {
          downloadFunc = downloadAsPresentation;
        }

        this.$nextTick(() => {
          downloadFunc(vm.presentationFormName).then(() => {
            vm.$store.commit('setIsPresentationEditable', wasPresentationEditable);
            vm.$store.commit('setPageLoadingStatus', false);
          });
        });
      },
      deleteSectionsWithoutData() {
       const section_list = this.$store.state.section.sectionList;
          for (const p in section_list) {
          if (!section_list[p].hasData) {
              this.$store.dispatch('deleteSectionDetail', {
                  id: section_list[p].id,
                  presentationId: this.id,
              });
          }
        }
      },
    },

    components: {
      AccessControlPanel,
      DeleteModal,
    },
  };
</script>

<style scoped>
  .share_button_left_margin {
    margin-left: 10px;
  }

  .errorMsg {
    margin-bottom: 18px;
  }
</style>
