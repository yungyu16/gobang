import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

let store = new Vuex.Store({
    state: {
        currentUserId: '',
        onlineUserList: [],
        onlineGameList: [],
    },
    getters: {},
    actions: {},
    mutations: {}
});

export default store;