<template>
  <basic-section-detail :section-detail="sectionDetail" :presentation-id="presentationId" :has-data="hasData"
                        @update-visualisation="updateVisualisation">
    <network-chart :nodes="nodes" :links="links">
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
            nodes:[],
            links:[]
        }
    },

    computed: {
      hasData() {
      return true;
      }
    },

    methods: {
      updateVisualisation({result, selections, extraData}) {
          let submission = extraData.first;
          let organisation = extraData.second;
          let myMap = new Map();
          let count = 0;
          let uniqueSubmission = Array.from(new Set(result.map(r => r[submission])));
          let uniqueOrganisation = Array.from(new Set(result.map(r => r[organisation])));
          uniqueOrganisation.forEach(s => {
            myMap.set(s,count);
            this.nodes.push({id: count, name: s});
            count++;
          });
          uniqueSubmission.forEach(d => {
                result.filter(i => i[submission] === d).forEach(s => {
                    result.filter(i => i[submission] === d).forEach(g => {
                            this.links.push({sid:myMap.get(s[organisation]) , tid:myMap.get(g[organisation])});
                    });
                });
          });
      },
    },

    components: {
      NetworkChart,
      BasicSectionDetail
    }
  }
</script>