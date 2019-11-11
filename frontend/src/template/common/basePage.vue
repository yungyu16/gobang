<template>
    <div>
        <van-nav-bar v-if="$route.meta.title"
                     :title="$route.meta.title"
                     left-arrow
                     @click-left="onClickLeft"
        ></van-nav-bar>
        <router-view></router-view>
    </div>
</template>
<script>
    import {Dialog} from 'vant';
    import config from '../../config'

    export default {
        name: "basePage.vue",
        data() {
            return {
                userWebSocket: '',
                pingInterval: ''
            };
        },
        created() {
            let wsUri = `ws://${config.apiHost}/ws/user`;
            this.userWebSocket = new WebSocket(wsUri);
            this.userWebSocket.onmessage = this.onWsMessage;
            this.userWebSocket.onopen = this.onWsOpen;
            this.userWebSocket.onerror = e => {
                console.log('ws 连接错误...', wsUri, e);
                clearInterval(this.pingInterval)
            };
            this.userWebSocket.onclose = () => {
                console.log('ws 连接关闭...', wsUri);
                clearInterval(this.pingInterval)
            }
        },
        beforeDestroy() {
            this.userWebSocket.close();
        },
        methods: {
            onWsOpen() {
                console.log('user ws连接完毕...');
                let pingMsg = {
                    msgType: 'ping',
                    sessionToken: this.getSessionToken()
                };
                this.userWebSocket.send(JSON.stringify(pingMsg));
                this.pingInterval = setInterval(() => {
                    console.log("user ws 发送心跳消息...");
                    pingMsg = {
                        msgType: 'ping',
                        sessionToken: this.getSessionToken()
                    };
                    this.userWebSocket.send(JSON.stringify(pingMsg));
                }, 5000);
            },
            onWsMessage(msg) {
                let msgData = JSON.parse(msg.data);
                // console.log("user ws收到消息：", msgData);
                let msgType = msgData.msgType;
                let data = msgData.data;
                switch (msgType) {
                    case 'welcome':
                        console.log("user ws连接成功：", data);
                        break;
                    case 'toast':
                        if (data) {
                            this.$toast(data);
                        }
                        break;
                    case 'error':
                        if (data) {
                            this.$notify(data);
                        }
                        setTimeout(() => {
                            window.location.reload();
                        }, 1000);
                        break;
                    case 'userList':
                        // console.log("收到用户列表", data);
                        this.$store.state.onlineUserList = data;
                        break;
                    case 'inviteGame':
                        Dialog.confirm({
                            title: '邀请对战',
                            message: `${data.userName} 邀您对战`
                        }).then(() => {
                            this.$router.push({
                                path: '/game',
                                query: {
                                    gameId: data.gameId
                                }
                            })
                        });
                        break;
                }
            },
            onClickLeft() {
                this.$router.go(-1);
            }
        }
    }
</script>
<style>
</style>