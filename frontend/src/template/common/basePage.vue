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
                websock: ''
            };
        },
        created() {
            this.initWebSocket();
        },
        methods: {
            initWebSocket() {
                const wsUri = "ws://47.102.103.194:8099/ws/game";
                let websock = new WebSocket(wsUri);
                websock.onmessage = this.onWebsocketMessage;
                websock.onopen = this.onWebsocketOpen;
                websock.onerror = this.onWebsocketError;
                websock.onclose = this.onWebsocketClose;
                this.websock = websock;
            },
            onWebsocketOpen() {
                console.log("ws open");
                let actions = {"test": "12345"};
                this.websocketSend(JSON.stringify(actions));
            },
            onWebsocketError() {
                this.initWebSocket();
            },
            onWebsocketMessage(e) {
                console.log(e)
            },
            websocketSend(Data) {
                this.websock.send(Data);
            },
            onWebsocketClose(e) {
                Notify({type: 'danger', message: '连接中断,请刷新页面'});
            },
            onClickLeft() {
                this.$router.go(-1);
            }
        }
    }
</script>
<style>
</style>