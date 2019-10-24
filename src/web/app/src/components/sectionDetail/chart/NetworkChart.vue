<template>
  <d3-network ref='net' :net-nodes="nodes" :net-links="links" :options="options" />
</template>
<script>
  import D3Network from 'vue-d3-network';
  export default {
    name: "NetworkChart",
     props: {
        links: {
          type: Array,
          required: true
        },
        nodes: {
            type: Array,
            required: true
            },
        dataSet: {
            type: Object,
            required: true
            },
     },

     data(){
      return {
          force:this.dataSet.force,
          fX:1,
          fY:1,
          fMb:true,
          fC:false,
          nodeSize:5,
          canvas:true,
          nodeLabels: this.dataSet.nodeLabels
      }
    },

    computed:{
      options(){
        return{
        force: this.dataSet.force,
        forces:{
             X:this.fX,
             Y:this.fY,
             ManyBody:this.fMb,
             Center: this.fC,
         },
         size:{w:1300, h:480},
         nodeSize: this.nodeSize,
         nodeLabels:this.dataSet.nodeLabels,
         linkLabels:true,
         canvas: true
        }
      }
    },

    components: {
        D3Network
    },
  }
</script>

<style scoped>

</style>