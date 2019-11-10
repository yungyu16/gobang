<template>
    <div>
        <van-divider/>
        <van-skeleton v-if="!userName" title avatar :row="2"/>
        <van-cell-group v-else>
            <van-row type="flex" justify="center">
                <van-col span="12">
                    <van-row type="flex" justify="center">
                        <van-col span="8">
                            <avatar :username="userName"
                                    backgroundColor="#03a9f4"
                                    :size="70"
                                    round>
                            </avatar>
                        </van-col>
                    </van-row>
                </van-col>
                <van-col span="12">
                    <van-row type="flex" justify="left">
                        <van-col span="12">
                            <strong>{{userName}}</strong>
                        </van-col>
                    </van-row>
                    <van-divider/>
                    <van-row type="flex" justify="left">
                        <van-col span="12">
                            {{mobile}}
                        </van-col>
                    </van-row>
                </van-col>
            </van-row>
        </van-cell-group>
        <div style="margin-top:50px">
            <van-cell-group>
                <van-cell title="对战记录" :is-link="true" to="/history"/>
                <van-cell title="人机对战" :is-link="true"/>
            </van-cell-group>
        </div>
        <div style="margin-top:250px">
            <van-row type="flex" justify="center">
                <van-col span="18">
                    <van-button type="primary" size="large" @click="signOut">退出登录</van-button>
                </van-col>
            </van-row>
        </div>

    </div>
</template>
<script>
    import Avatar from 'vue-avatar'
    import apis from '../../apis'

    export default {
        name: "mine.vue",
        components: {
            Avatar
        },
        data() {
            return {
                userName: '',
                mobile: ''
            };
        },
        created() {
            apis.user
                .detail()
                .then(it => {
                    this.userName = it.userName;
                    this.mobile = it.mobile
                });
        },
        methods: {
            signOut() {
                apis.user.signOut()
                    .then(() => {
                        this.removeSessionToken();
                        this.$router.push('/tab/start');
                    })
            }
        }
    }
</script>
<style scoped>
</style>