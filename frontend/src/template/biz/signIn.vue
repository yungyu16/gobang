<template>
    <div>
        <img src="../../../public/favicon.png"/>
        <div style="margin-top:20px">
            <van-row type="flex" justify="center">
                <van-col span="20">
                    <van-cell-group style="border-radius:10px;">
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
                                placeholder="请输入密码"
                                required
                                v-on:keyup.13="handleSignIn"
                        />
                    </van-cell-group>
                </van-col>
            </van-row>
        </div>
        <div style="margin-top:100px">
            <van-row type="flex" justify="center">
                <van-col span="18">
                    <van-button type="primary" size="large" @click="handleSignIn">登陆</van-button>
                </van-col>
            </van-row>
        </div>
        <van-divider/>
        <van-row type="flex" justify="center">
            <van-col span="6">
                <router-link to="/sign-up">前往注册</router-link>
            </van-col>
        </van-row>
    </div>
</template>
<script>
    import apis from '../../apis'

    export default {
        name: "signIn.vue",
        data() {
            return {
                mobile: '',
                password: '',
            };
        },
        methods: {
            handleSignIn() {
                if (!this.mobile) {
                    this.$toast("请输入手机号");
                    return;
                }
                if (!this.password) {
                    this.$toast("请输入密码");
                    return;
                }
                apis.user
                    .signIn({
                        mobile: this.mobile,
                        password: this.password,
                    })
                    .then(it => {
                        this.setSessionToken(it);
                        this.$toast("登陆成功");
                        this.$router.push('/tab/start');
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