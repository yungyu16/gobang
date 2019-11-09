import App from './App'
import Vue from 'vue';
import router from './router';
import store from './store';
import Vant from 'vant';
import 'vant/lib/index.css';
import util from './util'

Vue.use(Vant);
Vue.config.productionTip = false;

Vue.prototype.getUserToken = util.getUserToken;
Vue.prototype.setUserToken = util.setUserToken;

new Vue({
    router,
    render: h => h(App),
    store,
}).$mount('#app');