import Vue from 'vue'
import VueLodash from 'vue-lodash'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import store from './store'
import './plugins/element.js'

const options = { name: 'lodash' }

Vue.use(ElementUI);
Vue.use(VueLodash, options)

Vue.config.productionTip = false;

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');

// change title of page
router.afterEach((to) => {
  document.title = to.meta.title;
});
