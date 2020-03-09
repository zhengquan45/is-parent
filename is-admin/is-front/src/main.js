import Vue from 'vue'
import App from './App.vue'
import ViewUI from 'view-design';
import VueCookies from 'vue-cookies';

import 'view-design/dist/styles/iview.css';

Vue.config.productionTip = false;

Vue.use(ViewUI);
Vue.use(VueCookies);

new Vue({
  render: h => h(App),
}).$mount('#app');
