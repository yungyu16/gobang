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
    export default {
        name: "basePage.vue",
        data() {
            return {
                userWebSocket: '',
                pingInterval: ''
            };
        },
        created() {
            let wsUri = "ws://47.102.103.194:8099/ws/user";
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
                console.log('ws 连接完毕...');
                let pingMsg = {
                    msgType: 'ping',
                    data: this.getSessionToken() || ''
                };
                this.userWebSocket.send(JSON.stringify(pingMsg));
                this.pingInterval = setInterval(() => {
                    console.log("开始发送心跳消息...");
                    this.userWebSocket.send(JSON.stringify(pingMsg));
                }, 5000);
            },
            onWsMessage(msg) {
                console.log('收到ws消息', msg);
                let msgData = JSON.parse(msg.data);
                let msgType = msgData.msgType;
                switch (msgType) {
                    case 'welcome':
                        console.log("连接成功：", msgData.data);
                        break;
                    case 'userList':
                        console.log("收到用户列表", msgData.data);
                        this.$store.state.onlineUserList = msgData.data;
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