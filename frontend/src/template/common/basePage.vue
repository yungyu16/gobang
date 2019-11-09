<template>
    <div>
        <van-nav-bar v-if="$route.meta.title"
                     :title="$route.meta.title"
                     left-arrow
                     @click-left="onClickLeft"
        ></van-nav-bar>
        <router-view></router-view>
    </div>
</template>
<script>
    export default {
        name: "basePage.vue",
        data() {
            return {};
        },
        methods: {
            onClickLeft() {
                this.$router.go(-1);
            }
        }
    }
</script>
<style>
</style>