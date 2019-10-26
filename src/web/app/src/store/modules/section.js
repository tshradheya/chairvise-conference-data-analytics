import axios from 'axios';
import PredefinedQueries from '@/store/data/predefinedQueries';

export default {
  state: {
    sectionList: [],
    sectionListStatus: {
      isLoading: false,
      isApiError: false,
      apiErrorMsg: '',
    },
  },
  mutations: {
    setSectionListLoading(state, isLoading) {
      if (isLoading) {
        state.sectionListStatus.isApiError = false;
      }
      state.sectionListStatus.isLoading = isLoading;
    },

    setSectionListApiError(state, msg) {
      state.sectionListStatus.isApiError = true;
      state.sectionListStatus.apiErrorMsg = msg;
    },

    clearSectionList(state) {
      state.sectionList = [];
    },

    addSectionDetail(state, payload) {
      state.sectionList.push(Object.assign({
        result: [],
        previewResult: [],
        status: {
          isLoading: false,
          isApiError: false,
          apiErrorMsg: '',
          apiErrorMsgDetail: '',
        },
      }, payload));
    },

    deleteSectionDetail(state, payload) {
      const index = state.sectionList.findIndex(s => s.id === payload);
      state.sectionList.splice(index, 1);
    },

    updateSectionDetail(state, {id, title, description, dataSet, conferenceName, selections, involvedRecords, filters, joiners, groupers, sectionIndex, sorters, extraData}) {
      const section = findSectionDetailById(state.sectionList, id);

      section.title = title;
      section.description = description;
      section.dataSet = dataSet;
      section.conferenceName = conferenceName;
      section.selections = selections;
      section.involvedRecords = involvedRecords;
      section.filters = filters;
      section.joiners = joiners;
      section.groupers = groupers;
      section.sectionIndex = sectionIndex;
      section.sorters = sorters;
      section.extraData = extraData;
    },

    updateSectionIndices(state, {sections}) {
      // Update each section that has been swapped with new index.
      sections.forEach((section) => {
        const sectionToUpdate = findSectionDetailById(state.sectionList, section.id);
        sectionToUpdate.sectionIndex = section.sectionIndex;
      });
    },

    setSectionDetailLoading(state, {id, isLoading}) {
      const section = findSectionDetailById(state.sectionList, id);
      if (isLoading) {
        section.status.isApiError = false;
      }
      section.status.isLoading = isLoading;
    },

    setSectionDetailApiError(state, {id, msg, msgDetail}) {
      const section = findSectionDetailById(state.sectionList, id);
      section.status.isApiError = true;
      section.status.apiErrorMsg = msg;
      section.status.apiErrorMsgDetail = msgDetail;
    },

    updateSectionAnalysisResult(state, {id, result}) {
      const section = findSectionDetailById(state.sectionList, id);
      section.result = result;
    },

    updateSectionAnalysisPreviewResult(state, {id, result}) {
      const section = findSectionDetailById(state.sectionList, id);
      section.previewResult = result;
    },
  },
  actions: {
    async fetchSectionList({commit}, presentationId) {
      commit('setSectionListLoading', true);

      await axios.get(`/api/presentations/${presentationId}/sections`)
        .then(response => {
          commit('clearSectionList');
          response.data.forEach(s => {
            commit('addSectionDetail', s);
          });
        })
        .catch(e => {
          commit('setSectionListApiError', e.toString());
        })
        .finally(() => {
          commit('setSectionListLoading', false);
        });
    },

    async addSectionDetail({commit}, {presentationId, selectedNewSection, dataSet, conferenceName, sectionListSize}) {
      commit('setSectionListLoading', true);

      let newSection = PredefinedQueries[selectedNewSection].data;
      newSection = JSON.parse(JSON.stringify(newSection).replace(/\${PLACEHOLDER_DATA_SET}/g, dataSet).replace(/\${PLACEHOLDER_CONFERENCE_NAME}/g, conferenceName));

      // Assign the new section its length as index
      // as that would give an index one higher than the current list's indices.
      newSection['sectionIndex'] = sectionListSize;

      await axios.post(`/api/presentations/${presentationId}/sections`, newSection)
        .then(response => {
          commit('addSectionDetail', response.data);
        })
        .catch(e => {
          commit('setSectionListApiError', e.toString());
        })
        .finally(() => {
          commit('setSectionListLoading', false);
        });
    },

    async saveSectionDetail({commit}, {id, presentationId, title, description, dataSet, conferenceName, selections, involvedRecords, filters, joiners, groupers, sectionIndex, sorters, extraData}) {
      commit('setSectionDetailLoading', {id, isLoading: true});

      await axios.put(`/api/presentations/${presentationId}/sections/${id}`, {
        title,
        description,
        dataSet,
        conferenceName,
        selections,
        involvedRecords,
        filters,
        joiners,
        groupers,
        sectionIndex,
        sorters,
        extraData,
      })
        .then(response => {
          const section = response.data;
          commit('updateSectionDetail', {
            id: section.id,
            title: section.title,
            description: section.description,
            dataSet: section.dataSet,
            conferenceName: section.conferenceName,
            selections: section.selections,
            involvedRecords: section.involvedRecords,
            filters: section.filters,
            joiners: section.joiners,
            groupers: section.groupers,
            sectionIndex: section.sectionIndex,
            sorters: section.sorters,
            extraData: section.extraData,
          });
        })
        .catch(e => {
          commit('setSectionDetailApiError', {id, msg: e.toString(), msgDetail: JSON.stringify(e.response)});
        })
        .finally(() => {
          commit('setSectionDetailLoading', {id, isLoading: false});
        });
    },

    async updateSectionIndex({commit}, {id, presentationId, sectionToSwap}) {
      commit('setSectionDetailLoading', {id, isLoading: true});

      await axios.patch(`/api/presentations/${presentationId}/sections/${id}`, sectionToSwap)
      .then(response => {
        const sections = response.data;
        commit('updateSectionIndices', {
          sections: sections,
        });
      })
      .catch(e => {
        commit('setSectionDetailApiError', {id, msg: e.toString(), msgDetail: JSON.stringify(e.response)});
      })
      .finally(() => {
        commit('setSectionDetailLoading', {id, isLoading: false});
      });
    },

    async deleteSectionDetail({commit}, {id, presentationId}) {
      commit('setSectionDetailLoading', {id, isLoading: true});

      await axios.delete(`/api/presentations/${presentationId}/sections/${id}`)
        .then(() => {
          commit('deleteSectionDetail', id);
        })
        .catch(e => {
          commit('setSectionDetailApiError', {id, msg: e.toString(), msgDetail: JSON.stringify(e.response)});
          commit('setSectionDetailLoading', {id, isLoading: false});
        });
    },

    async sendPreviewAnalysisRequest({commit}, {presentationId, id, dataSet, conferenceName, selections, involvedRecords, filters, joiners, groupers, sorters}) {
      commit('setSectionDetailLoading', {id, isLoading: true});

      await axios.post(`/api/presentations/${presentationId}/analysis`, {
        dataSet,
        conferenceName,
        selections,
        involvedRecords,
        filters,
        joiners,
        groupers,
        sorters,
      })
        .then(response => {
          commit('updateSectionAnalysisPreviewResult', {id, result: response.data});
        })
        .catch(e => {
          commit('setSectionDetailApiError', {id, msg: e.toString(), msgDetail: JSON.stringify(e.response)});
        })
        .finally(() => {
          commit('setSectionDetailLoading', {id, isLoading: false});
        });
    },

    async sendAnalysisRequest({state, commit}, {id, presentationId}) {
      const sectionToAnalysis = findSectionDetailById(state.sectionList, id);
      commit('setSectionDetailLoading', {id: sectionToAnalysis.id, isLoading: true});

      await axios.post(`/api/presentations/${presentationId}/analysis`, {
        dataSet: sectionToAnalysis.dataSet,
        conferenceName: sectionToAnalysis.conferenceName,
        selections: sectionToAnalysis.selections,
        involvedRecords: sectionToAnalysis.involvedRecords,
        filters: sectionToAnalysis.filters,
        joiners: sectionToAnalysis.joiners,
        groupers: sectionToAnalysis.groupers,
        sorters: sectionToAnalysis.sorters,
      })
        .then(response => {
          commit('updateSectionAnalysisResult', {id: sectionToAnalysis.id, result: response.data});
        })
        .catch(e => {
          commit('setSectionDetailApiError',
            {id: sectionToAnalysis.id, msg: e.toString(), msgDetail: JSON.stringify(e.response)});
        })
        .finally(() => {
          commit('setSectionDetailLoading', {id: sectionToAnalysis.id, isLoading: false});
        });
    },
  },
};

function findSectionDetailById(sectionList, id) {
  return sectionList.find(element => element.id === id);
}
