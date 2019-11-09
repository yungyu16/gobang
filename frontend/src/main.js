import App from './App'
import Vue from 'vue';
import router from './router';
import store from './store';
import Vant from 'vant';
import 'vant/lib/index.css';

Vue.use(Vant);
Vue.config.productionTip = false;

Vue.prototype.getUserToken = () => localStorage.getItem('gobangUserToken');
Vue.prototype.setUserToken = userToken => localStorage.setItem('gobangUserToken', userToken);

new Vue({
    router,
    render: h => h(App),
    store,
}).$mount('#app');