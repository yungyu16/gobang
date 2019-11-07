<template>
    <div>
        <canvas @click="whenClickBoard" id="gobang_board" height="480px" width="480px"></canvas>
        <van-dialog v-model="signUpData.showFlag" title="注册" @confirm="confirmUserName">
            <van-cell-group>
                <van-field
                        v-model="signUpData.userName"
                        clearable
                        label="用户名"
                        placeholder="请输入用户名">
                </van-field>
            </van-cell-group>
        </van-dialog>
    </div>
</template>

<script>import {Notify} from 'vant';

export default {
    name: "game.vue",
    data() {
        let boardSize = 480;
        let boardPadding = 30;
        let boardDelta = 30;

        let gobangBoardBoxMap = [];
        let gobangBoardBoxList = [];
        for (let i = 0; i < 15; i++) {
            gobangBoardBoxMap[i] = [];
            for (let j = 0; j < 15; j++) {
                let xIndex = i;
                let yIndex = this.index2Char(j);
                gobangBoardBoxMap[i][j] = {
                    id: xIndex + yIndex,
                    checkedBy: -1,
                    xIndex: xIndex,
                    yIndex: yIndex,
                    x: i * boardDelta + boardPadding,
                    y: j * boardDelta + boardPadding,
                    aroundHere(thisX, thisY) {
                        let xFlag = thisX > this.x - 12 && thisX < this.x + 12;
                        let yFlag = thisY > this.y - 12 && thisY < this.y + 12;
                        return xFlag && yFlag;
                    },
                };
                gobangBoardBoxList.push(gobangBoardBoxMap[i][j]);
            }
        }
        return {
            signUpData: {
                showFlag: false,
                userName: ''
            },
            boardContext: '',
            boardSize: boardSize,
            boardPadding: boardPadding,
            boardDelta: boardDelta,
            gobangBoardBoxMap: gobangBoardBoxMap,
            gobangBoardBoxList: gobangBoardBoxList,
            latestClickedCell: ''
        };
    },
    created() {
        let userId = this.$cookies.get('userId');
        if (!userId) {
            this.signUp();
            return;
        }
        let roomId = this.$route.params.roomId;
        if (!roomId) {

        }
    },
    methods: {
        signUp() {
            this.signUpData.showFlag = true;
        },
        confirmUserName() {
            let userName = this.signUpData.userName;
            if (!userName) {
                Notify({type: 'danger', message: '注册失败,用户名为空'});
                return;
            }
        },
        whenClickBoard(e) {
            let x = e.offsetX;
            let y = e.offsetY;
            let thisCell = this.findClickedCell(x, y);
            let latestCell = this.latestClickedCell;

            if (!thisCell && !latestCell) {
                return;
            }
            if (thisCell && latestCell) {
                if (thisCell.id === latestCell.id) {
                    this.drawCheck('AAAA', thisCell.x, thisCell.y);
                } else {
                    this.clearPreCheck(latestCell.x, latestCell.y);
                    this.drawPreCheck("aaa", thisCell.x, thisCell.y);
                    this.latestClickedCell = thisCell;
                }
                return;
            }
            if (!thisCell) {
                return;
            }
            if (!latestCell) {
                return;
            }
            if (!clickedCell) {
                if (this.latestClickedCell) {
                    this.clearPreCheck(this.latestClickedCell.x, this.latestClickedCell.y)
                }
                return;
            }
            if (this.latestClickedCell) {
                if (this.latestClickedCell.id == clickedCell.id) {
                    this.drawChessman('W', clickedCell.x, clickedCell.y)
                } else {
                    this.clearPreCheck(this.latestClickedCell.x, this.latestClickedCell.y)
                }
            } else {
                this.drawPreCheck('W', this.latestClickedCell.x, this.latestClickedCell.y)
            }
        },
        findClickedCell(x, y) {
            let box = this.gobangBoardBoxList;
            for (let idx_i in box) {
                let cell = box[idx_i];
                if (cell.checkedBy < 1 && cell.aroundHere(x, y)) {
                    return cell;
                }
            }
        },
        char2Index(input) {
            return input.charCodeAt(0) - 65;
        },
        index2Char(input) {
            return String.fromCharCode(65 + input);
        },
        putByIndex(color, x, y) {
            if (!color) {
                console.log("没有颜色");
                return
            }
            x = this.char2Index(x);
            x = this.boardPadding + x * this.boardDelta;
            y = this.boardPadding + y * this.boardDelta;
            console.log("棋子坐标:", x, y);
            this.drawChessman(color, x, y);

        },
        drawCheck(color, x, y) {
            this.boardContext.beginPath();
            this.boardContext.arc(x, y, 13, 0, 2 * Math.PI);
            let style = this.boardContext.createRadialGradient(x, y, 13, x, y, 0);
            switch (color) {
                case 'W':
                    style.addColorStop(0, '#D1D1D1');
                    style.addColorStop(1, '#F9F9F9');
                    break;
                case 'B':
                    style.addColorStop(0, '#0A0A0A');
                    style.addColorStop(1, '#636766');
                    break;
            }
            this.boardContext.fillStyle = style;
            this.boardContext.fill();
            this.boardContext.closePath();
        },
        drawPreCheck(color, x, y) {
            this.boardContext.beginPath();
            this.boardContext.arc(x, y, 13, 0, 2 * Math.PI);
            let style = this.boardContext.createRadialGradient(x, y, 13, x, y, 0);
            switch (color) {
                case 'W':
                    style.addColorStop(0, '#D1D1D1');
                    style.addColorStop(1, '#F9F9F9');
                    break;
                case 'B':
                    style.addColorStop(0, '#0A0A0A');
                    style.addColorStop(1, '#636766');
                    break;
            }
            this.boardContext.fillStyle = style;
            this.boardContext.fill();
            this.boardContext.closePath();
        },
        clearPreCheck(x, y) {
            this.boardContext.beginPath();
            this.boardContext.arc(x, y, 13, 0, 2 * Math.PI);
            let style = this.boardContext.createRadialGradient(x, y, 13, x, y, 0);
            switch (color) {
                case 'W':
                    style.addColorStop(0, '#D1D1D1');
                    style.addColorStop(1, '#F9F9F9');
                    break;
                case 'B':
                    style.addColorStop(0, '#0A0A0A');
                    style.addColorStop(1, '#636766');
                    break;
            }
            this.boardContext.fillStyle = style;
            this.boardContext.fill();
            this.boardContext.closePath();
        },
        drawChessBoard() {
            console.log("开始初始化棋盘...");
            let boardSize = this.boardSize;
            let boardPadding = this.boardPadding;

            this.boardContext.fillStyle = "#ffdc99";
            this.boardContext.fillRect(0, 0, boardSize, boardSize);

            this.boardContext.strokeStyle = "#2a2a2a";
            this.boardContext.fillStyle = "black";
            this.boardContext.font = "15px '微软雅黑'";
            this.boardContext.textAlign = "center";
            this.boardContext.lineWidth = 0.5;

            for (let i = 0; i < 15; i++) {
                this.boardContext.fillText(i + '', boardPadding + i * this.boardDelta, boardPadding - 15);
                this.boardContext.moveTo(boardPadding + i * this.boardDelta, boardPadding);
                this.boardContext.lineTo(boardPadding + i * this.boardDelta, boardSize - boardPadding);
                this.boardContext.strokeStyle = "#2a2a2a";
                this.boardContext.stroke();
                this.boardContext.fillText(this.index2Char(i), boardPadding - 20, boardPadding + i * this.boardDelta + 4);
                this.boardContext.moveTo(boardPadding, boardPadding + i * this.boardDelta);
                this.boardContext.lineTo(boardSize - boardPadding, boardPadding + i * this.boardDelta);
                this.boardContext.strokeStyle = "#2a2a2a";
                this.boardContext.stroke();
            }
            console.log("初始化棋盘完成...");
        }
    },
    mounted() {
        let board = document.getElementById("gobang_board");
        this.boardContext = board.getContext('2d');
        this.drawChessBoard();
    }
}
</script>

<style scoped>
    * {
        margin: 0;
        padding: 0;
    }

    html, body {
        height: 100%;
        width: 100%;
    }

    canvas {
        display: block;
        margin: 50px auto;
        box-shadow: -2px -2px 2px #F3F2F2, 5px 5px 5px #6F6767;
    }
</style>