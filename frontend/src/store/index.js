import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

let store = new Vuex.Store({
    state: {
        activeNavItemIdx: '1'
    },
    getters: {},
    actions: {},
    mutations: {}
});

export default store;