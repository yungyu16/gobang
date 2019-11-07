<template>
    <el-menu :default-active="activeIndex"
             @select="handleSelect"
             active-text-color="#ffd04b"
             background-color="#545c64"
             router
             mode="horizontal"
             text-color="#fff">
        <el-menu-item :route='{path:"/hall"}' index="1">大厅</el-menu-item>
        <el-menu-item :route='{path:"/history"}' disabled index="2">历史</el-menu-item>
        <el-menu-item :route='{path:"/history"}' disabled index="3">接口文档</el-menu-item>
    </el-menu>
</template>
<script>
    export default {
        computed: {
            activeIndex() {
                return this.$store.state.activeNavItemIdx
            }
        },
        methods: {
            handleSelect(index, indexPath) {
                console.log(index, indexPath);
            }
        }
    }
</script>