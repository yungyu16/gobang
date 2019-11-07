<template>
    <canvas @click="whenClickBoard" height="620px" id="gobang_board" width="620px"></canvas>
</template>

<script>
    export default {
        name: "game.vue",
        data() {
            let boardSize = 620;
            let boardPadding = 30;
            let boardDelta = 40;

            let gobangBoardBox = [];
            for (let i = 0; i < 15; i++) {
                gobangBoardBox[i] = [];
                for (let j = 0; j < 15; j++) {
                    gobangBoardBox[i][j] = {
                        checked: false,
                        xIndex: i,
                        yIndex: this.index2Char(j),
                        x: i * boardDelta + boardPadding,
                        y: j * boardDelta + boardPadding,
                        aroundHere(thisX, thisY) {
                            let xFlag = thisX > this.x - 12 && thisX < this.x + 12;
                            let yFlag = thisY > this.y - 12 && thisY < this.y + 12;
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
                boardDelta: boardDelta,
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
                x = this.boardPadding + x * this.boardDelta;
                y = this.boardPadding + y * this.boardDelta;
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

                for (let i = 0; i < 15; i++) {
                    this.boardContext.fillText(i + '', boardPadding + i * this.boardDelta, boardPadding - 15);
                    this.boardContext.moveTo(boardPadding + i * this.boardDelta, boardPadding);
                    this.boardContext.lineTo(boardPadding + i * this.boardDelta, boardSize - boardPadding);
                    this.boardContext.stroke();
                    this.boardContext.fillText(this.index2Char(i), boardPadding - 20, boardPadding + i * this.boardDelta + 4);
                    this.boardContext.moveTo(boardPadding, boardPadding + i * this.boardDelta);
                    this.boardContext.lineTo(boardSize - boardPadding, boardPadding + i * this.boardDelta);
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
    canvas {
        display: block;
        margin: 50px auto;
        box-shadow: -2px -2px 2px #F3F2F2, 5px 5px 5px #6F6767;
    }
</style>