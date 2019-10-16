<template>
  <div>
    <el-row class="addRowRightAlign" v-if="isNewPresentation">
      <el-alert
        title="Please create presentation before adding sections"
        type="info"
        show-icon>
      </el-alert>
    </el-row>
    <div v-loading="isLoadingDBMetaData || isLoadingSectionList" v-if="!isNewPresentation">
      <el-row class="addRowRightAlign" v-if="isLogin && isPresentationEditable">
        <el-select v-model="selectedConferenceName" placeholder="Conference Name" style="width: 300px"
                   filterable>
          <el-option
            v-for="item in conferenceNames"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
        <el-select v-model="selectedNewSection" placeholder="Please select a section to add" style="width: 300px"
                   filterable>
          <el-option-group
            v-for="group in predefinedSections"
            :key="group.label"
            :label="group.label">
            <el-option
              v-for="item in group.options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-option-group>
        </el-select>
        <el-button class="addButtonLeft" type="success" @click="addNewSection" :disabled="!isNewSectionTypeAddable">Add New Section</el-button>
      </el-row>
      <br/>
      <el-alert
        v-if="isSectionListApiError"
        :title="sectionListApiErrorMsg"
        type="error" show-icon>
      </el-alert>
      <abstract-section-detail class="presentation-section" v-for="section in orderedSectionList" :sectionDetail="section"
                               :key="section.id" :presentationId="presentationId" :moveSection="changeSectionOrder"/>
    </div>
  </div>
</template>

<script>
  import AbstractSectionDetail from "@/components/AbstractSectionDetail.vue"
  import {ID_NEW_PRESENTATION} from "@/common/const";
  import PredefinedQueries from "@/store/data/predefinedQueries"

  export default {
    props: {
      presentationId: String,
    },
    watch: {
      presentationId: 'fetchSectionList'
    },
    data() {
      return {
        selectedNewSection: '',
        selectedConferenceName: ''
      }
    },
    computed: {
      isLogin() {
        return this.$store.state.userInfo.isLogin
      },

      isPresentationEditable() {
        return this.$store.state.presentation.isPresentationEditable;
      },

      predefinedSections() {
        let sectionOptionsGroup = {};
        // grouping the predefined queries
        for (let key in PredefinedQueries) {
          if (!PredefinedQueries.hasOwnProperty(key)) {
            continue;
          }
          let groupName = PredefinedQueries[key].group;
          if (sectionOptionsGroup[groupName] === undefined) {
            sectionOptionsGroup[groupName] = [];
          }
          sectionOptionsGroup[groupName].push({
            value: key,
            label: PredefinedQueries[key].name,
          })
        }

        // generate to format that element ui requires
        let sectionOptions = [];
        for (let groupName in sectionOptionsGroup) {
          if (!sectionOptionsGroup.hasOwnProperty(groupName)) {
            continue;
          }
          sectionOptions.push({
            label: groupName,
            options: sectionOptionsGroup[groupName]
          })
        }
        return sectionOptions;
      },

      conferenceNames() {
        let conferenceNames = this.$store.state.dbMetaData.uniqueConferenceNames;
        
        conferenceNames = conferenceNames.map(res => {
          return { 
            value: res,
            label: res,
          }
        });
        return conferenceNames;
      },
 
      isNewPresentation() {
        return this.presentationId === ID_NEW_PRESENTATION
      },
      orderedSectionList() {
        return this._.orderBy(this.$store.state.section.sectionList, 'sectionIndex')
      },
      isLoadingSectionList() {
        return this.$store.state.section.sectionListStatus.isLoading
      },
      isSectionListApiError() {
        return this.$store.state.section.sectionListStatus.isApiError
      },
      sectionListApiErrorMsg() {
        return this.$store.state.section.sectionListStatus.apiErrorMsg
      },
      isLoadingDBMetaData() {
        return this.$store.state.dbMetaData.entitiesStatus.isLoading
      },
      isNewSectionTypeAddable() {
        return this.selectedNewSection.length !== 0
          && this.selectedConferenceName.length > 0;
      }
    },
    components: {
      AbstractSectionDetail
    },
    mounted() {
      this.fetchSectionList();
      this.$store.dispatch('fetchDBMetaDataEntities');
      this.$store.dispatch('fetchUniqueConferenceNames');
    },
    methods: {
      fetchSectionList() {
        if (this.isNewPresentation) {
          this.$store.commit('clearSectionList');
        } else {
          this.$store.dispatch('fetchSectionList', this.presentationId)
        }
      },

      addNewSection() {
        if (this.selectedNewSection.length === 0 && this.selectedConferenceName.length === 0) {
          return;
        }
        this.$store.dispatch('addSectionDetail', {
          presentationId: this.presentationId,
          selectedNewSection: this.selectedNewSection,
          dataSet: this.$store.state.userInfo.userEmail,
          conferenceName: this.selectedConferenceName,
          sectionListSize: this.orderedSectionList.length
        }).then(() => {
          this.selectedNewSection = ''
          this.selectedConferenceName = ''
        })
      },

      changeSectionOrder(sectionId, sectionIndex, direction) {
        var indexToSwap, idToSwap;

        if (direction === 'up') {
          // Get ID of section that is one index smaller
          indexToSwap = sectionIndex > 0 ? sectionIndex - 1 : sectionIndex
        } else {
          // Get ID of section that is one index greater
          indexToSwap = sectionIndex < this.orderedSectionList.length - 1 ? sectionIndex + 1 : sectionIndex
        }
        // Don't bother trying to swap if they are not swap-able
        if (indexToSwap == sectionIndex) {
          return
        }
        var sectionToSwap = this.orderedSectionList[indexToSwap]
        this.$store.dispatch('updateSectionIndex', {
          id: sectionId,
          presentationId: this.presentationId,
          sectionToSwap: sectionToSwap
        })
      }
    }
  }
</script>

<style scoped>
  .addButtonLeft {
    margin-left: 10px;
  }

  .addRowRightAlign {
    text-align: right;
  }
</style>