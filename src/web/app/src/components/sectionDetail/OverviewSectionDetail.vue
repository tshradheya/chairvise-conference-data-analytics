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
      <el-form-item v-if="slotProps.isInAdvancedMode" v-for="(input, index) in slotProps.extraData.types" :label="'Type ' + index">
        <el-input v-model="input.name" placeholder="Expression" style="width: 300px"></el-input>&nbsp;
        <el-select placeholder="type" v-model="input.find">
          <el-option label="Mode" value="mod"></el-option>
          <el-option label="Sum" value="sum"></el-option>
          <el-option label="Average" value="avg"></el-option>
          <el-option label="Count" value="count"></el-option>
        </el-select>&nbsp;
        <el-button type="danger" icon="el-icon-delete" circle @click="removeType(index, slotProps.extraData.types)"></el-button>
      </el-form-item>
       <el-button type="success" plain @click="addType(slotProps.extraData.types)" v-if="slotProps.isInAdvancedMode">Add Type</el-button>
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
      updateVisualisation({result, selections, extraData}) {
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
            case 'mode':
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