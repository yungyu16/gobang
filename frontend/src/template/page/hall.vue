<template>
    <div>
        <el-divider></el-divider>
        <el-row>
            <el-col :offset="1" :span="2">
                <el-button type="primary">创建对局</el-button>
            </el-col>
        </el-row>
        <el-divider></el-divider>
        <el-row>
            <el-col :span="5">
                <el-card class="box-card">
                    <div class="clearfix" slot="header">
                        <span>在线对局</span>
                    </div>
                    <el-table :data="tableData"
                              height="600px"
                              style="width: 100%">
                        <el-table-column
                                label="房主"
                                prop="date">
                        </el-table-column>
                        <el-table-column
                                label="状态"
                                prop="name">
                        </el-table-column>
                        <el-table-column
                                label="操作"
                                prop="address">
                            <el-button type="text">加入/观战</el-button>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
            <el-col :offset="1" :span="5">
                <el-card class="box-card">
                    <div class="clearfix" slot="header">
                        <span>聊天室</span>
                    </div>
                    <el-table :data="tableData"
                              height="600px"
                              style="width: 100%">
                        <el-table-column
                                label="房主"
                                prop="date">
                        </el-table-column>
                        <el-table-column
                                label="状态"
                                prop="name">
                        </el-table-column>
                        <el-table-column
                                label="操作"
                                prop="address">
                            <el-button type="text">加入/观战</el-button>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>
<script>
    export default {
        name: "hall.vue",
        data() {
            return {
                tableData: [{
                    date: '2016-05-02',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1518 弄'
                }, {
                    date: '2016-05-04',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1517 弄'
                }, {
                    date: '2016-05-01',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1519 弄'
                }, {
                    date: '2016-05-03',
                    name: '王小虎',
                    address: '上海市普陀区金沙江路 1516 弄'
                }]
            }
        },
        created() {
        },
    }
</script>
<style scoped>

</style>