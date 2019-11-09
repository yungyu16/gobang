<template>
    <div>
        <van-tabs title-active-color="#1989fa"
                  color="#1989fa">
            <van-tab name="user" title="在线用户">
                <van-swipe-cell v-for="it in userList">
                    <van-cell :border="false" title="单元格" value="内容"/>
                    <template slot="right" scope="user">
                        <van-button square type="primary" text="聊天"/>
                        <van-button square type="primary" text="邀请"/>
                        <van-button square type="primary" text="围观"/>
                    </template>
                </van-swipe-cell>
            </van-tab>
            <van-tab name="game" title="在线对局">
                <van-swipe-cell v-for="it in gameList">
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
            return {
                userList: [1, 2, 3, 2, 3, 2, 3],
                gameList: [1, 2, 3, 2, 3, 2, 3],
            }
        },
        created() {
        },
        components: {floatBtn},
        methods: {

        }
    }
</script>
<style scoped>
</style>