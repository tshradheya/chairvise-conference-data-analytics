<template>
  <basic-section-detail :section-detail="sectionDetail" :presentation-id="presentationId" :has-data="hasData"
                        :extraFormItemsRules="{}"
                        @update-visualisation="updateVisualisation">
    <el-table
      :data="tableData"
      style="width: 100%">
      <el-table-column
        prop="type"
        label="Type">
      </el-table-column>
      <el-table-column
        prop="value"
        label="Value">
      </el-table-column>
    </el-table>
    <template slot="extraFormItems" slot-scope="slotProps">
      <el-form-item v-if="slotProps.isInAdvancedMode" v-bind:key="index" v-for="(input, index) in slotProps.extraData.types" :label="'Type ' + index">
        <el-input v-model="input.name" placeholder="Expression" style="width: 300px"></el-input>&nbsp;
        <el-select placeholder="type" v-model="input.find">
          <el-option label="Mode" value="mod"></el-option>
          <el-option label="Sum" value="sum"></el-option>
          <el-option label="Average" value="avg"></el-option>
          <el-option label="Count" value="count"></el-option>
          <el-option label="Unique Count" value="uniqueCount"></el-option>
          <el-option label="Min" value="min"></el-option>
          <el-option label="Max" value="max"></el-option>
          <el-option label="Median" value="median"></el-option>
          <el-option label="Standard Deviation" value="std"></el-option>
           <el-option label="Breakdown of values" value="breakDown"></el-option>
        </el-select>&nbsp;
        <el-button type="danger" icon="el-icon-delete" circle @click="removeType(index, slotProps.extraData.types)"></el-button>
      </el-form-item>
      <el-form-item v-if="slotProps.isInAdvancedMode">
       <el-button type="success" plain @click="addType(slotProps.extraData.types)" >Add Type</el-button>
      </el-form-item>
    </template>
  </basic-section-detail>
</template>

<script>
  import {max, mean, median, min, standardDeviation, sum, mode} from 'simple-statistics'
  import BasicSectionDetail from '@/components/sectionDetail/BasicSectionDetail.vue'

  export default {
    name: "OverviewSectionDetail",

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
        tableData: []
      }
    },

    computed: {
      hasData() {
        return this.tableData.length !== 0;
      }
    },

    methods: {
      updateVisualisation({result, extraData}) {
        this.tableData = [];
        if (result.length === 0) {
          return
        }

        extraData.types.forEach(t => {
          switch (t.find) {
            case 'avg':
              this.tableData.push({
                type: 'Average' + " of " + t.name,
                value: mean(result.map(r => r[t.name])).toFixed(2),
              });
              break;
            case 'count':
              this.tableData.push({
                type: 'Count of ' + t.name,
                value: (result.map(r => r[t.name])).length,
              });
              break;
            case 'mod':
              this.tableData.push({
                 type: 'Mode of ' + t.name,
                 value: mode((result.map(r => r[t.name]))),
              });
              break;
            case 'sum':
              this.tableData.push({
                 type: 'Sum of ' + t.name,
                 value: sum((result.map(r => r[t.name]))),
              });
              break;
            case 'uniqueCount': {
              let uniqueItems = Array.from(new Set(result.map(r => r[t.name])));
              this.tableData.push({
                 type: 'Unique count of ' + t.name,
                 value: uniqueItems.length,
              });
              break;
              }
            case 'min':
              this.tableData.push({
                type: 'Min of ' + t.name,
                value: min((result.map(r => r[t.name]))),
              });
              break;
            case 'max':
              this.tableData.push({
                type: 'Max of ' + t.name,
                value: max((result.map(r => r[t.name]))),
              });
              break;
            case 'median':
              this.tableData.push({
                type: 'Median of ' + t.name,
                value: median((result.map(r => r[t.name]))),
              });
              break;
            case 'std':
              this.tableData.push({
                type: 'Standard Deviation ' + t.name,
                value: standardDeviation((result.map(r => r[t.name]))).toFixed(2),
              });
            break;
            case 'breakDown': {
              let uniqueValues = Array.from(new Set(result.map(r => r[t.name])));
              let data = result.map(r => r[t.name]);
              uniqueValues.forEach( d => {
              this.tableData.push({
                type: 'Count of ' + t.name + ' with ' + d,
                value: (data.filter(i => i === d)).length,
                });
              });
            break;
            }
          }
        })
      },

      removeType(index,extraData) {
        extraData.splice(index, 1)
      },

      addType(extraData) {
        extraData.push({
            name: '',
            find: '',
        })
      },

    },

    components: {
      BasicSectionDetail,
    }
  }
</script>

<style scoped>

</style>