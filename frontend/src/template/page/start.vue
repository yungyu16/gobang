<template>
    <div>
        <van-nav-bar
                :title="$route.meta.title"
                :left-arrow="false"
                title-active-color="#1989fa"
                color="#1989fa"
                @click-right="onClickNavRight">
            <div slot="right">
                <van-icon name="plus"/>
            </div>
        </van-nav-bar>
        <van-tabs v-model="activeTab">
            <van-tab title="在线用户">在线用户</van-tab>
            <van-tab title="在线对局">在线对局</van-tab>
        </van-tabs>
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
        <van-action-sheet :round="false" v-model="action.addActionsShow" :actions="action.addActions" @select="onSelectAddAction"/>
    </div>
</template>
<script>
    import {Notify, Toast} from 'vant';
    import apis from '../../apis'

    export default {
        data() {
            return {
                activeTab: '1',
                userName: '',
                dialogShow: false,
                action: {
                    addActionsShow: false,
                    addActions: [
                        {name: '好友对战'},
                        {name: '人机对战'}
                    ]
                }
            }
        },
        created() {
            this.validateUserToken()
        },
        methods: {
            onClickNavRight() {
                this.action.addActionsShow = true;
            },
            onSelectAddAction() {

            },
            validateUserToken() {
                let userToken = this.getUserToken();
                if (!userToken) {
                    this.dialogShow = true;
                    return;
                }
                apis.account.validate({userToken: userToken})
                    .then(payload => {
                        if (payload.bizCode !== 0) {
                            this.dialogShow = true;
                        }
                    });
            },
            confirmUserName() {
                if (!this.userName) {
                    Notify({type: 'danger', message: '请输入用户名'});
                    return;
                }
                apis.account.signUp(this.userName)
                    .then(payload => {
                        this.dialogShow = false;
                        this.setUserToken(payload.data);
                    });
                this.validateUserToken();
            }
        }
    }
</script>
<style scoped>
</style>