<template>
    <div>
        <van-tabs title-active-color="#1989fa"
                  color="#1989fa">
            <van-tab name="user" title="在线用户">
                <van-list :finished="true">
                    <van-cell
                            v-for="item in userList"
                            :key="item"
                            :title="item"
                    />
                </van-list>
            </van-tab>
            <van-tab name="game" title="在线对局">
                <van-list :finished="true">
                    <van-cell
                            v-for="item in gameList"
                            :key="item"
                            :title="item"
                    />
                </van-list>
            </van-tab>
        </van-tabs>
        <van-action-sheet :round="false" v-model="action.addActionsShow" :actions="action.addActions"
                          @select="onSelectAddAction"/>
    </div>
</template>
<script>
    import floatBtn from '../common/floatBtn'
    import {Notify, Toast} from 'vant';
    import apis from '../../apis'

    export default {
        data() {
            return {
                userList: [1, 2, 3],
                gameList: [1, 2, 3],
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
        components: {floatBtn},
        methods: {
            onFloatBtnClick() {
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