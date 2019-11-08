<template>
    <div>
        <van-row>
            <van-col offset='8' span="8">
                <van-button type="primary">创建对局</van-button>
            </van-col>
        </van-row>
        <van-dialog
                v-model="dialogShow"
                title="注册"
                :showConfirmButton="false"
                :showCancelButton="false"
                show-cancel-button>
            <div width="300px" height="200px">
                <van-divider/>
                <van-cell-group>
                    <van-field v-model="userName"
                               required
                               clearable
                               label="用户名"
                               placeholder="请输入用户名"/>
                </van-cell-group>
                <van-divider/>
                <van-row>
                    <van-col offset='10'>
                        <van-button type="primary" size="normal" @click="confirmUserName">确认</van-button>
                    </van-col>
                </van-row>
            </div>

        </van-dialog>
    </div>
</template>
<script>
    import {Notify} from 'vant';
    import apis from '../../apis'

    export default {
        data() {
            return {
                userName: '',
                dialogShow: false
            }
        },
        created() {
            let userToken = this.getUserToken();
            if (!userToken) {
                this.dialogShow = true;
                return;
            }
            apis.account.validate(userToken)
                .then(payload => {
                    if (payload.code !== 0) {
                        this.dialogShow = true;
                    }
                });
        },
        methods: {
            confirmUserName() {
                if (!this.userName) {
                    Notify({type: 'danger', message: '请输入用户名'});
                    return;
                }
                apis.account.signUp(this.userName)
                    .then(payload => {
                        this.dialogShow = false;
                        this.setUserToken(payload.token);
                    })
            }
        }
    }
</script>
<style scoped>
</style>