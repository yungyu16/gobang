import Vue from 'vue';
import Router from 'vue-router';
import store from '../store'

import heartBeat from '../template/common/heartBeat'
import tabPage from '../template/common/tabPage'
import signUp from '../template/page/signUp'
import signIn from '../template/page/signIn'
import game from '../template/page/game'
import start from '../template/page/start'
import history from '../template/page/history'
import mine from '../template/page/mine'
import chat from '../template/page/chat'
import notFound from '../template/page/notFound'

Vue.use(Router);
const router = new Router({
    routes: [
        {
            path: '/',
            component: heartBeat,
            meta: {title: 'index'},
            children: [
                {
                    path: '',
                    redirect: '/tab'
                },
                {
                    path: '/sign-up',
                    component: signUp,
                    meta: {title: '注册'}
                },
                {
                    path: '/sign-in',
                    component: signIn,
                    meta: {title: '登陆'}
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
                            path: 'chat',
                            component: chat,
                            meta: {
                                showNotice: true,
                                icon: 'chat-o',
                                title: '聊天',
                                tabName: '聊天'
                            }
                        },
                        {
                            path: 'mine',
                            component: mine,
                            meta: {
                                showNotice: true,
                                icon: 'manager-o',
                                title: '我',
                                tabName: '我'
                            }
                        },
                    ]
                },
            ]
        }, {
            name: 'notFound',
            path: '/404',
            component: notFound
        },
        {
            path: '*',
            redirect: '/404'
        }
    ]
});

router.beforeEach((to, from, next) => {
    document.title = `五子棋`;
    store.state.activeNavItemIdx = to.meta.navItemIdx;
    next();
});

export default router;
