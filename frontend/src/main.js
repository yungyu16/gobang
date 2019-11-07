import App from './App'
import Vue from 'vue';
import router from './router';
import store from './store';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css'; // 默认主题
import VueCookies from 'vue-cookies'

Vue.use(VueCookies);
Vue.config.productionTip = false;
Vue.use(ElementUI, {
    size: 'small'
});

new Vue({
    router,
    render: h => h(App),
    store,
}).$mount('#app');