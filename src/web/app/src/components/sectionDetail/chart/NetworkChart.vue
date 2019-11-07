<template>
  <d3-network ref='net' :net-nodes="nodes" :net-links="links" :options="options" />
</template>
<script>
  import D3Network from 'vue-d3-network';
  export default {
    name: 'NetworkChart',
     props: {
        links: {
          type: Array,
          required: true,
        },
        nodes: {
            type: Array,
            required: true,
            },
        dataSet: {
            type: Object,
            required: true,
            },
     },

     data(){
      return {
          force:this.dataSet.force,
          fX:1,
          fY:1,
          fMb:true,
          fC:false,
          nodeSize:this.dataSet.nodeSize,
          canvas:true,
          nodeLabels: this.dataSet.nodeLabels,
          fontSize: this.dataSet.fontSize,
      };
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
         size:{w:1300, h:this.dataSet.canvasSize},
         nodeSize: this.dataSet.nodeSize,
         nodeLabels: this.dataSet.nodeLabels,
         fontSize: this.dataSet.fontSize,
         linkLabels:true,
         canvas: true,
        };
      },
    },

    components: {
        D3Network,
    },
  };
</script>

<style scoped>

</style>
