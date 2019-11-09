import Vue from 'vue';
import Router from 'vue-router';
import store from '../store'
import tabPage from '../template/common/tabPage'
import game from '../template/page/game'
import start from '../template/page/start'
import history from '../template/page/history'
import mine from '../template/page/mine'

Vue.use(Router);
const router = new Router({
    routes: [
        {
            path: '',
            redirect: '/tab'
        },
        {
            path: '/tab',
            component: tabPage,
            meta: {title: 'index'},
            children: [
                {
                    path: '',
                    redirect: 'start'
                },
                {
                    path: 'start',
                    component: start,
                    meta: {
                        showNotice: false,
                        icon: 'wap-home-o',
                        title: '开始',
                        tabName: '首页'
                    }
                },
                {
                    path: 'mine',
                    component: mine,
                    meta: {
                        showNotice: true,
                        icon: 'manager-o',
                        title: '我的',
                        tabName: '我的'
                    }
                },
            ]
        },
        {
            path: '/game',
            component: game,
            meta: {title: '对局'}
        }, {
            path: '/history',
            component: history,
            meta: {title: '历史'}
        },
    ]
});

router.beforeEach((to, from, next) => {
    document.title = `五子棋`;
    store.state.activeNavItemIdx = to.meta.navItemIdx;
    next();
});

export default router;
