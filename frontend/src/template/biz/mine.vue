<template>
    <div>                    <van-divider/>

        <van-cell-group>
            <van-row type="flex" justify="center">
                <van-col span="12">
                    <van-row type="flex" justify="center">
                        <van-col span="8">
                            <avatar :username="userName"
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
        <van-cell-group>
            <van-cell title="对战记录" :is-link="true" to="/history"/>
            <van-cell title="人机对战" :is-link="true"/>
        </van-cell-group>
    </div>
</template>
<script>
    import Avatar from 'vue-avatar'

    export default {
        name: "mine.vue",
        components: {
            Avatar
        },
        data() {
            return {
                userName: '宋加林',
                mobile: '15156684305'
            };
        },
    }
</script>
<style scoped>
</style>