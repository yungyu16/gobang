<template>
    <div>
        <van-notice-bar
                left-icon="volume-o"
                text="性感FZK,在线五子棋..."/>
        <van-divider/>
        <router-view></router-view>
    </div>
</template>
<script>
    import pageHeader from './pageHeader.vue';

    export default {
        name: "pageContainer.vue",
        data() {
            return {}
        },
        components: {pageHeader},
    }
</script>
<style>
</style>