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
     },
     data () {
      return {
          force:1000,
          fX:1,
          fY:1,
          fMb:true,
          fC:false,
          nNodes:500,
          nodeSize:5,
          canvas:true
      }
    },
    computed:{
      options(){
        return{
        force: this.force,
        forces:{
             X:this.fX,
             Y:this.fY,
             ManyBody:this.fMb,
             Center: this.fC,
         },
         size:{w:1300, h:480},
         nodeSize: this.nodeSize,
         nodeLabels: true,
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