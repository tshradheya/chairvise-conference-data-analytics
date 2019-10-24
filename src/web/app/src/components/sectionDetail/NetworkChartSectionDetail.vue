<template>
  <basic-section-detail :section-detail="sectionDetail" :presentation-id="presentationId" :has-data="hasData"
                        :extraFormItemsRules="{}"
                        @update-visualisation="updateVisualisation">
    <network-chart :nodes="nodes" :links="links" :dataSet="dataSet" ></network-chart>
     <template slot="extraFormItems" slot-scope="slotProps">
          <el-form-item label="Links by" v-if="slotProps.isInAdvancedMode">
            <el-input v-model="slotProps.extraData.first" placeholder="Expression" style="width: 300px"></el-input>&nbsp;
          </el-form-item>
          <el-form-item label="Nodes" v-if="slotProps.isInAdvancedMode">
            <el-input v-model="slotProps.extraData.second" placeholder="Expression" style="width: 300px"></el-input>&nbsp;
          </el-form-item>
          <el-form-item label="Force" prop="extraData.force" v-if="slotProps.isInAdvancedMode">
            <el-slider v-model="slotProps.extraData.force" :min="400" :max="3000"></el-slider>
          </el-form-item>
           <el-form-item label="Canvas Height" prop="extraData.canvasSize" v-if="slotProps.isInAdvancedMode">
             <el-slider v-model="slotProps.extraData.canvasSize" :min="400" :max="3000"></el-slider>
           </el-form-item>
         <el-form-item label="Labels" prop="extraData.nodeLabels" v-if="slotProps.isInAdvancedMode">
              <el-switch
                v-model="slotProps.extraData.nodeLabels"
                active-text="Labels on"
                inactive-text="Labels off">
              </el-switch>
         </el-form-item>
     </template>
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
            links:[],
            dataSet:{},
        }
    },

    computed: {
      hasData() {
      return this.nodes.length!= 0;
      }
    },

    methods: {
      updateVisualisation({result, extraData}) {
          this.dataSet = {};
          this.dataSet.nodeLabels = extraData.nodeLabels;
          this.dataSet.force = extraData.force;
          this.dataSet.canvasSize = extraData.canvasSize;
          let submission = extraData.first;
          let organisation = extraData.second;
          let myMap = new Map();
          let count = 0;
          this.nodes = [];
          this.links = [];
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