<template>
    <div>
        <img src="../../../public/favicon.png"/>
        <div style="margin-top:20px">
            <van-row type="flex" justify="center">
                <van-col span="20">
                    <van-cell-group style="border-radius:10px;">
                        <van-field
                                v-model="userName"
                                required
                                clearable
                                label="用户名"
                                placeholder="6位长度内汉字字母数字组合"/>
                        <van-field
                                v-model="mobile"
                                required
                                clearable
                                label="手机号"
                                placeholder="请输入手机号码"/>
                        <van-field
                                v-model="password"
                                type="password"
                                label="密码"
                                placeholder="5到10位字母数字组合"
                                required/>
                        <van-field
                                v-model="rePassword"
                                type="password"
                                label="确认密码"
                                placeholder="5到10位字母数字组合"
                                required
                                v-on:keyup.13="handleSignUp"
                        />
                    </van-cell-group>
                </van-col>
            </van-row>
        </div>
        <div style="margin-top:100px">
            <van-row type="flex" justify="center">
                <van-col span="18">
                    <van-button type="primary" size="large" @click="handleSignUp">注册</van-button>
                </van-col>
            </van-row>
        </div>
    </div>
</template>
<script>
    import apis from '../../apis'

    export default {
        name: "signIn.vue",
        data() {
            return {
                mobile: '',
                userName: '',
                password: '',
                rePassword: '',
            };
        },
        methods: {
            handleSignUp() {
                if (!this.mobile) {
                    this.$toast("请输入手机号");
                    return;
                }
                if (!this.userName) {
                    this.$toast("请输入用户名");
                    return;
                }
                if (!this.password) {
                    this.$toast("请输入密码");
                    return;
                }
                if (!this.rePassword) {
                    this.$toast("请输入确认密码");
                    return;
                }
                if (this.password !== this.rePassword) {
                    this.$toast("两次密码不一致");
                    return;
                }
                apis.user
                    .signUp({
                        mobile: this.mobile,
                        userName: this.userName,
                        password: this.password
                    })
                    .then(() => {
                        this.$toast("注册成功");
                        this.$router.push('/sign-in');
                    })
            }
        }
    }
</script>
<style scoped>
    img {
        display: block;
        margin: 0px auto;
    }
</style>