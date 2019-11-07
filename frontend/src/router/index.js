import Vue from 'vue';
import Router from 'vue-router';
import game from '../template/page/game'
import pageContainer from '../template/common/pageContainer'
import store from '../store'

Vue.use(Router);
const router = new Router({
    routes: [
        {
            path: '/',
            component: pageContainer,
            meta: {title: 'index'},
            children: [
                {
                    path: '/game',
                    component: game,
                    meta: {title: '对局', navItemIdx: '2'}
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
