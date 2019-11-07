<template>
    <canvas @click="whenClickBoard" height="480px" id="gobang_board" width="480px"></canvas>
</template>

<script>
    export default {
        name: "game.vue",
        data() {
            let boardSize = 480;
            let boardPadding = 30;

            let gobangBoardBox = [];
            for (let i = 0; i < 15; i++) {
                gobangBoardBox[i] = [];
                for (let j = 0; j < 15; j++) {
                    gobangBoardBox[i][j] = {
                        checked: false,
                        x: i * 30 + boardPadding,
                        y: j * 30 + boardPadding,
                        aroundHere(thisX, thisY) {
                            let xFlag = thisX > this.x - 15 && thisX < this.x + 15;
                            let yFlag = thisY > this.y - 15 && thisY < this.y + 15;
                            return xFlag && yFlag;
                        },
                    };
                }
            }
            console.log(gobangBoardBox);
            return {
                latestChessmanColor: '',
                boardContext: '',
                boardSize: boardSize,
                boardPadding: boardPadding,
                gobangBoardBox: gobangBoardBox
            };
        },
        created() {

        },
        methods: {
            whenClickBoard(e) {
                let x = e.offsetX;
                let y = e.offsetY;
                let box = this.gobangBoardBox;
                for (let idx_i in box) {
                    for (let idx_j in box[idx_i]) {
                        let cell = box[idx_i][idx_j];
                        if (cell.aroundHere(x, y)) {
                            let thisTimeColor = this.thisTimeColor();
                            this.drawChessman(thisTimeColor, cell.x, cell.y);
                        }
                    }
                }
            },
            char2Index(input) {
                return input.charCodeAt(0) - 65;
            },
            index2Char(input) {
                return String.fromCharCode(65 + input);
            },
            thisTimeColor() {
                if (this.latestChessmanColor) {
                    if (this.latestChessmanColor === 'B') {
                        this.latestChessmanColor = 'W'
                    } else {
                        this.latestChessmanColor = 'B'
                    }
                } else {
                    this.latestChessmanColor = 'B';
                }
                return this.latestChessmanColor;
            },
            putByIndex(color, x, y) {
                if (!color) {
                    console.log("没有颜色");
                    return
                }
                x = this.char2Index(x);
                x = this.boardPadding + x * 30;
                y = this.boardPadding + y * 30;
                console.log("棋子坐标:", x, y);
                this.drawChessman(color, x, y);

            },
            drawChessman(color, x, y) {
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
            drawChessBoard(context) {
                console.log("开始初始化棋盘...");
                this.boardContext = context;
                let boardSize = this.boardSize;
                let boardPadding = this.boardPadding;

                this.boardContext.fillStyle = "#ffdc99";
                this.boardContext.fillRect(0, 0, boardSize, boardSize);

                this.boardContext.strokeStyle = "#2a2a2a";
                this.boardContext.fillStyle = "black";
                this.boardContext.font = "15px '微软雅黑'";
                this.boardContext.textAlign = "center";

                for (let i = 0; i < 15; i++) {
                    this.boardContext.fillText(i + '', boardPadding + i * 30, boardPadding - 15);
                    this.boardContext.moveTo(boardPadding + i * 30, boardPadding);
                    this.boardContext.lineTo(boardPadding + i * 30, boardSize - boardPadding);
                    this.boardContext.stroke();
                    this.boardContext.fillText(this.index2Char(i), boardPadding - 20, boardPadding + i * 30 + 4);
                    this.boardContext.moveTo(boardPadding, boardPadding + i * 30);
                    this.boardContext.lineTo(boardSize - boardPadding, boardPadding + i * 30);
                    this.boardContext.stroke();
                }
                console.log("初始化棋盘完成...");
            }
        },
        mounted() {
            let board = document.getElementById("gobang_board");
            let context = board.getContext('2d');
            this.drawChessBoard(context);//绘制棋盘

            board.οnclick = function (e) {

            };
        }
    }
</script>

<style scoped>
    canvas {
        display: block;
        margin: 50px auto;
        box-shadow: -2px -2px 2px #F3F2F2, 5px 5px 5px #6F6767;
    }
</style>