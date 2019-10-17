import axios from 'axios'

export default {
  state: {
    entities: [],
    uniqueConferenceNames: [],
    entitiesStatus: {
      isLoading: false,
      isApiError: false,
      apiErrorMsg: '',
    }
  },

  mutations: {
    setDBMetaDataEntitiesLoading(state, isLoading) {
      state.entitiesStatus.isApiError = false;
      state.entitiesStatus.isLoading = isLoading;
    },

    setDBMetaDataEntitiesApiError(state, payload) {
      state.entitiesStatus.isApiError = true;
      state.entitiesStatus.apiErrorMsg = payload;
    },

    setDBMetaDataEntities(state, payload) {
      state.entities = payload;
    },

    setUniqueConferenceNames(state, payload) {
      state.uniqueConferenceNames = payload;
    }

  },

  actions: {
    async fetchDBMetaDataEntities({commit}) {
      commit('setDBMetaDataEntitiesLoading', true);

      await axios.get('/api/db/entity')
        .then(response => {
          commit('setDBMetaDataEntities', response.data);
        })
        .catch(e => {
          commit('setDBMetaDataEntitiesApiError', e.toString());
        })
        .finally(() => {
          commit('setDBMetaDataEntitiesLoading', false);
        })
    },

    async fetchUniqueConferenceNames({commit}) {
      commit('setDBMetaDataEntitiesLoading', true);

      await axios.get('/api/allConferenceNames')
        .then(response => {
          commit('setUniqueConferenceNames', response.data)
        }).finally(() => {
          commit('setDBMetaDataEntitiesLoading', false);
        })
    }

  }
}