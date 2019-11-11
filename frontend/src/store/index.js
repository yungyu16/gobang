import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

let store = new Vuex.Store({
    state: {
        onlineUserList: [],
    },
    getters: {},
    actions: {},
    mutations: {}
});

export default store;