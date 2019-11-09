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
                                required/>
                        <van-field
                                v-model="rePassword"
                                type="password"
                                label="确认密码"
                                placeholder="请输入密码"
                                required
                                v-on:keyup.13="handleSignUp"
                        />
                    </van-cell-group>
                </van-col>
            </van-row>
        </div>
        <div style="margin-top:180px">
            <van-row type="flex" justify="center">
                <van-col span="18">
                    <van-button type="primary" size="large" @click="handleSignUp">注册</van-button>
                </van-col>
            </van-row>
        </div>
    </div>
</template>
<script>  export default {
    name: "signIn.vue",
    data() {
        return {
            userName: '',
            password: '',
            rePassword: '',
        };
    },
    methods: {
        handleSignUp() {
        }
    }
}
</script>
<style scoped>
</style>