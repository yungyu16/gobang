<template>
    <div>
        <div style="width:100%;height:190px;"></div>
        <div style="margin-top:20px">
            <van-row type="flex" justify="center">
                <van-col span="20">
                    <van-cell-group style="border-radius:10px;">
                        <van-field
                                v-model="userName"
                                required
                                clearable
                                label="手机号"
                                placeholder="请输入手机号码"/>
                        <van-field
                                v-model="password"
                                type="password"
                                label="密码"
                                placeholder="请输入密码"
                                required
                                v-on:keyup.13="handleSignIn"
                        />
                    </van-cell-group>
                </van-col>
            </van-row>
        </div>
        <div style="margin-top:180px">
            <van-row type="flex" justify="center">
                <van-col span="18">
                    <van-button type="primary" size="large" @click="handleSignIn">登陆</van-button>
                </van-col>
            </van-row>
        </div>
        <van-divider />
        <van-row type="flex" justify="center">
            <van-col span="8">
                <router-link to="/sign-up">没有账号,前往注册</router-link>
            </van-col>
        </van-row>
    </div>
</template>
<script>  export default {
    name: "signIn.vue",
    data() {
        return {
            userName: '',
            password: '',
        };
    },
    methods: {
        handleSignIn() {
        }
    }
}
</script>
<style scoped>
</style>