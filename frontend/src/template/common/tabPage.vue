<template>
    <div>
        <van-notice-bar v-if="$route.meta.showNotice"
                        left-icon="volume-o"
                        text="性感FZK,在线五子棋..."/>
        <router-view/>
        <van-tabbar route>
            <van-tabbar-item
                    v-for='tab in tabs'
                    replace
                    :to="'/tab/'+tab.path"
                    :icon="tab.meta.icon">
                {{tab.meta.tabName}}
            </van-tabbar-item>
        </van-tabbar>
    </div>
</template>
<script>
    export default {
        name: "tabPage.vue",
        data() {
            let tabs = [];
            let routes = this.$router.options.routes;
            console.log(routes)
            let tabParentRoute = routes[0].children.filter(it => it.path === '/tab')[0];
            if (tabParentRoute) {
                tabs = tabParentRoute.children.filter(it => it.path)
            }
            if (!tabs || tabs.length <= 0) {
                console.log("没有tab路由...")
            }
            return {
                tabs
            };
        },
    }
</script>
<style>
</style>