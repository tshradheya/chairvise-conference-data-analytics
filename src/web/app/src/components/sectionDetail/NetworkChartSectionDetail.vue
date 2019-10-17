<template>
  <basic-section-detail :section-detail="sectionDetail" :presentation-id="presentationId" :has-data="hasData"
                        :edit-form-selections-rule="editFormSelectionsRule"
                        :edit-form-involved-records-rule="editFormInvolvedRecordsRule"
                        :edit-form-filters-rule="editFormFiltersRule"
                        :extraFormItemsRules="extraFormItemsRules"
                        @update-visualisation="updateVisualisation">
    <network-chart>
    </network-chart>
  </basic-section-detail>
</template>

<script>
  import NetworkChart from '@/components/sectionDetail/chart/NetworkChart.vue'
  import BasicSectionDetail from '@/components/sectionDetail/BasicSectionDetail.vue'

  export default {
    props: {
      sectionDetail: {
        type: Object,
        required: true
      },
      presentationId: {
        type: String,
        required: true
      }
    },

    data() {
      return {
        }
    },

    computed: {
      hasData() {
      return true;
      }
    },

    methods: {
      updateVisualisation({result, selections, extraData}) {

      },

    },

    components: {
      NetworkChart,
      BasicSectionDetail
    }
  }
</script>