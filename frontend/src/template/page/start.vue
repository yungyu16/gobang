<template>
    <van-dialog
            v-model="dialogShow"
            title="注册"
            @click-right-icon="confirmUserName"
            show-cancel-button>
        <van-field v-model="username"
                   required
                   clearable
                   label="用户名"
                   placeholder="请输入用户名"/>
    </van-dialog>
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
            let gobangUserId = sessionStorage.getItem('gobangUserId');
            if (!gobangUserId) {
                Notify({type: 'danger', message: '请输入用户名'});
                return;
            }
            apis.account.validate(gobangUserId,
                ok => {
                },
                error => {
                });
        },
        methods: {
            confirmUserName() {
                if (!this.userName) {

                }
            }
        }
    }
</script>
<style scoped>
</style>