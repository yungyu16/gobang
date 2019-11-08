import Vue from 'vue';
import Router from 'vue-router';
import store from '../store'
import pageContainer from '../template/common/pageContainer'
import game from '../template/page/game'
import start from '../template/page/start'
import history from '../template/page/history'

Vue.use(Router);
const router = new Router({
    routes: [
        {
            path: '/',
            component: pageContainer,
            meta: {title: 'index'},
            children: [
                {
                    path: '',
                    component: start,
                    meta: {title: '开始', navItemIdx: '2'}
                },
                {
                    path: '/game',
                    component: game,
                    meta: {title: '对局', navItemIdx: '2'}
                }, {
                    path: '/history',
                    component: history,
                    meta: {title: '历史', navItemIdx: '2'}
                },
            ]
        },
    ]
});

router.beforeEach((to, from, next) => {
    document.title = `${to.meta.title} | 五子棋`;
    store.state.activeNavItemIdx = to.meta.navItemIdx;
    next();
});

export default router;
