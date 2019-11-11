<template>
    <div>
        <van-tabs title-active-color="#1989fa"
                  color="#1989fa">
            <van-tab name="user" title="在线用户">
                <div v-if="!onlineUserList || onlineUserList.length==0">
                    <van-divider>暂时没有用户哦~</van-divider>
                </div>
                <div v-if="onlineUserList">
                    <van-divider/>
                    <van-row justify="center" type="flex" v-for="it in onlineUserList">
                        <van-col offset='4' span="3"><strong>{{it.userName}}</strong></van-col>
                        <van-col offset='4' span="3">{{it.status}}</van-col>
                        <van-col offset='4' span="8">
                            <a @click="inviteUser(it)" href="#" v-if="it.status !=='对战中'">邀请</a>
                            <a @click="watchGame(it)" href="#" v-else>观战</a>
                        </van-col>
                    </van-row>
                </div>
            </van-tab>
            <van-tab name="game" title="在线对局">
                <div v-if="!onlineGameList || onlineGameList.length==0">
                    <van-divider> 暂时没有对局哦~</van-divider>
                </div>
            </van-tab>
        </van-tabs>
    </div>
</template>
<script>
    import floatBtn from '../common/floatBtn'
    import {Notify, Toast} from 'vant';
    import apis from '../../apis'

    export default {
        data() {
            return {}
        },
        computed: {
            onlineUserList() {
                return this.$store.state.onlineUserList;
            },
            onlineGameList() {
                return this.$store.state.onlineGameList;
            }
        },
        created() {
        },
        components: {floatBtn},
        methods: {
            inviteUser(user) {
                apis.game.createAndInvite({userId: user.userId})
                    .then(gameId => {
                        this.$router.push({
                            path: '/game',
                            query: {
                                gameId: gameId
                            }
                        })
                    });
                console.log(user)
            },
            watchGame(user) {
                console.log(user)
            }
        }
    }
</script>
<style scoped>
</style>