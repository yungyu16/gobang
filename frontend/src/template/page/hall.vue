<template>
    <el-row>
        <el-col :span="8">
            <el-card class="box-card">
                <div class="clearfix" slot="header">
                    <span>在线用户</span>
                </div>
                <el-table :data="tableData"
                          height="600px"
                          style="width: 100%">
                    <el-table-column
                            label="用户名"
                            prop="date"
                            width="180">
                    </el-table-column>
                    <el-table-column
                            label="积分"
                            prop="name"
                            width="180">
                    </el-table-column>
                    <el-table-column
                            label="胜"
                            prop="address">
                    </el-table-column>
                    <el-table-column
                            label="负"
                            prop="address">
                    </el-table-column>
                    <el-table-column
                            label="操作"
                            prop="address">
                    </el-table-column>
                </el-table>
            </el-card>
        </el-col>
        <el-col :span="8">
            <el-card class="box-card">
                <div class="clearfix" slot="header">
                    <span>在线对局室</span>
                </div>
                <el-table :data="tableData"
                          height="600px"
                          style="width: 100%">
                    <el-table-column
                            label="用户名"
                            prop="date"
                            width="180">
                    </el-table-column>
                    <el-table-column
                            label="积分"
                            prop="name"
                            width="180">
                    </el-table-column>
                    <el-table-column
                            label="胜"
                            prop="address">
                    </el-table-column>
                    <el-table-column
                            label="负"
                            prop="address">
                    </el-table-column>
                    <el-table-column
                            label="操作"
                            prop="address">
                    </el-table-column>
                </el-table>
            </el-card>
        </el-col>
        <el-col :span="8">
            <el-card class="box-card">
                <div class="clearfix" slot="header">
                    <span>聊天室</span>
                </div>
                <el-table :data="tableData"
                          style="width: 100%">
                    <el-table-column
                            label="用户名"
                            prop="date"
                            width="180">
                    </el-table-column>
                    <el-table-column
                            label="积分"
                            prop="name"
                            width="180">
                    </el-table-column>
                    <el-table-column
                            label="胜"
                            prop="address">
                    </el-table-column>
                    <el-table-column
                            label="负"
                            prop="address">
                    </el-table-column>
                    <el-table-column
                            label="操作"
                            prop="address">
                    </el-table-column>
                </el-table>
            </el-card>
        </el-col>
    </el-row>
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