<template>
    <div>
        <van-tabs title-active-color="#1989fa"
                  color="#1989fa">
            <van-tab name="user" title="在线用户">
                <div v-if="!onlineUserList || onlineUserList.length==0">
                    暂时没有用户哦~
                </div>
                <div v-if="onlineUserList">
                    <van-swipe-cell v-for="it in onlineUserList">
                        <van-cell :border="false" :title="it.userName" :value='it.status'/>
                        <template slot="right" scope="user">
                            <van-button square type="primary" text="聊天"/>
                            <van-button square type="primary" text="邀请"/>
                            <van-button square type="primary" text="围观"/>
                        </template>
                    </van-swipe-cell>
                </div>
            </van-tab>
            <van-tab name="game" title="在线对局">
                <van-swipe-cell v-for="it in onlineGameList">
                    <van-cell :border="false" title="单元格" value="内容"/>
                    <template slot="right" scope="game">
                        <van-button square type="primary" text="加入"/>
                        <van-button square type="primary" text="围观"/>
                    </template>
                </van-swipe-cell>
            </van-tab>
        </van-tabs>
    </div>
</template>
<script>
    import floatBtn from '../common/floatBtn'
    import {Notify, Toast} from 'vant';

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
        methods: {}
    }
</script>
<style scoped>
</style>