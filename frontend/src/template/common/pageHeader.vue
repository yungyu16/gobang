<template>
    <el-menu :default-active="activeIndex"
             @select="handleSelect"
             active-text-color="#ffd04b"
             background-color="#545c64"
             router
             mode="horizontal"
             text-color="#fff">
        <el-menu-item :route='{path:"/hall"}' index="1">大厅</el-menu-item>
        <el-menu-item :route='{path:"/game"}' index="2">对局</el-menu-item>
        <el-menu-item disabled index="3">消息中心</el-menu-item>
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