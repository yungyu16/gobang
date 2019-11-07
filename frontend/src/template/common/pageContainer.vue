<template>
    <div>
        <pageHeader/>
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