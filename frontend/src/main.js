import App from './App'
import Vue from 'vue';
import router from './router';
import store from './store';
import Vant from 'vant';
import 'vant/lib/index.css';
import util from './util'
import VConsole from 'vconsole'

const vConsole = new VConsole();
console.log(vConsole.version);

Vue.use(Vant);
Vue.config.productionTip = false;

Vue.prototype.getSessionToken = util.getSessionToken;
Vue.prototype.setSessionToken = util.setSessionToken;
Vue.prototype.removeSessionToken = util.removeSessionToken;

new Vue({
    router,
    render: h => h(App),
    store,
}).$mount('#app');