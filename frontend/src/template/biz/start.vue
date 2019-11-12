<template>
    <div>
        <div v-if="!onlineUserList || onlineUserList.length==0">
            <van-divider>暂时没有在线用户哦~</van-divider>
        </div>
        <div v-if="onlineUserList" v-for="it in onlineUserList">
            <van-divider/>
            <van-row justify="center" type="flex">
                <van-col offset='2' span="8"><strong>{{it.userName}}</strong></van-col>
                <van-col span="4" :class="{'text-green': it.status === -1 || it.status === -3 }">
                    {{statusStr(it.status)}}
                </van-col>
                <van-col offset='4' span="6">
                    <a @click="inviteUser(it)" href="#" v-if="it.status == -1 || it.status == -3">邀请</a>
                    <a @click="watchGame(it)" href="#" v-if="it.status === -1 || it.status == -2">观战</a>
                    <a href="#" v-if="it.status === 0">-</a>
                </van-col>
            </van-row>
        </div>
    </div>
</template>
<script>
    import floatBtn from '../common/floatBtn'
    import {Dialog, Notify, Toast} from 'vant';
    import apis from '../../apis'

    export default {
        data() {
            return {}
        },
        computed: {
            onlineUserList() {
                return this.$store.state.onlineUserList;
            },
        },
        created() {
        },
        components: {floatBtn},
        methods: {
            statusStr(status) {
                if (status === 0) {
                    return '离线';
                }
                if (status === -1) {
                    return '观战中';
                }
                if (status === -2) {
                    return '对局中';
                }
                return '空闲';
            },
            inviteUser(user) {
                Dialog.confirm({
                    title: '确认邀请',
                    message: '确认邀请' + user.userName + '与您对局？'
                }).then(() => {
                    apis.game.createAndInvite({userId: user.userId})
                        .then(gameId => {
                            this.$router.push({
                                path: '/game',
                                query: {
                                    gameId: gameId
                                }
                            })
                        });
                });
            },
            watchGame(user) {
                Dialog.confirm({
                    title: '确认围观比赛',
                }).then(() => {
                    this.$router.push({
                        path: '/game',
                        query: {
                            gameId: user.gameId
                        }
                    })
                });
            }
        },
    }
</script>
<style scoped>
    .text-green {
        color: green;
    }
</style>