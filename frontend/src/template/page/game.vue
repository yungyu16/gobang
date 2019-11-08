<template>
    <div id="canvas_container">
    </div>
</template>
<script>
    import {Notify} from 'vant';

    let boardSize = 480;
    let boardPadding = 30;
    let boardDelta = 30;

    let screenWidth = window.screen.availWidth;
    let scaleRate = screenWidth / boardSize;

    function scale(input) {
        return input * scaleRate;
    }

    function index2Char(input) {
        return String.fromCharCode(65 + input);
    }

    boardSize = scale(boardSize);
    boardPadding = scale(boardPadding);
    boardDelta = scale(boardDelta);

    console.log("boardSize", boardSize);
    console.log("boardPadding", boardPadding);
    console.log("boardDelta", boardDelta);

    let lines = [];
    let gobangPointMap = [];
    let gobangPointList = [];
    for (let i = 0; i < 15; i++) {
        let line = {
            h: {
                charInfo: {
                    char: i + '',
                    x: boardPadding + i * boardDelta,
                    y: boardPadding - scale(15)
                },
                fromX: boardPadding + i * boardDelta,
                fromY: boardPadding,
                toX: boardPadding + i * boardDelta,
                toY: boardSize - boardPadding
            },
            v: {
                charInfo: {
                    char: index2Char(i),
                    x: boardPadding - scale(20),
                    y: boardPadding + i * boardDelta + scale(4)
                },
                fromX: boardPadding,
                fromY: boardPadding + i * boardDelta,
                toX: boardSize - boardPadding,
                toY: boardPadding + i * boardDelta
            }
        };
        lines.push(line);
        //
        gobangPointMap[i] = [];
        for (let j = 0; j < 15; j++) {
            let xIndex = i;
            let yIndex = j;
            gobangPointMap[i][j] = {
                id: 'x' + xIndex + ':y' + yIndex,
                //-1空 0预选 1黑色 2白色
                status: -1,
                xIndex: xIndex,
                yIndex: yIndex,
                x: i * boardDelta + boardPadding,
                y: j * boardDelta + boardPadding,
                aroundHere(thisX, thisY) {
                    let checkFlag = this.status < 1;
                    if (!checkFlag) {
                        return false;
                    }
                    let xFlag = thisX > this.x - scale(12) && thisX < this.x + scale(12);
                    if (!xFlag) {
                        return false;
                    }
                    return thisY > this.y - scale(12) && thisY < this.y + scale(12);
                },
            };
            gobangPointList.push(gobangPointMap[i][j]);
        }
    }
    export default {
        name: "game.vue",
        data() {
            return {
                boardSize: boardSize,
                boardContext: '',
                boardLines: lines,
                gobangPointMap: gobangPointMap,
                gobangPointList: gobangPointList,
                latestClickCell: null,
                latestBoardSnapshot: null,
                //1黑 2白
                currentUserColor: 1,
            };
        },
        created() {
            let that = this;
            function drawPreCheck() {
                console.log("预选", this.xIndex, this.yIndex);
                that.boardContext.beginPath();
                that.boardContext.arc(this.x, this.y, scale(13), 0, 2 * Math.PI);
                that.boardContext.fillStyle = that.currentUserColor === 1 ? "#0A0A0A" : "#D1D1D1";
                that.boardContext.stroke();
                that.boardContext.closePath();
                this.status = 0;
            }

            function drawCheck() {
                console.log("确认", this.xIndex, this.yIndex);
                that.boardContext.beginPath();
                that.boardContext.arc(this.x, this.y, scale(13), 0, 2 * Math.PI);
                let style = that.boardContext.createRadialGradient(this.x, this.y, scale(13), this.x, this.y, 0);
                switch (that.currentUserColor) {
                    case 1:
                        style.addColorStop(0, '#0A0A0A');
                        style.addColorStop(1, '#636766');
                        break;
                    case 2:
                        style.addColorStop(0, '#D1D1D1');
                        style.addColorStop(1, '#F9F9F9');
                        break;
                }
                that.boardContext.fillStyle = style;
                that.boardContext.fill();
                that.boardContext.closePath();
                this.status = that.currentUserColor;
            }

            gobangPointList.forEach(it => {
                it.drawPreCheck = drawPreCheck;
                it.drawCheck = drawCheck;
            })
        },
        methods: {
            playSound(id) {
                let node = document.getElementById(id);
                if (node != null) {
                    node.Play();
                }
            },
            whenClickBoard(e) {
                let x = e.offsetX;
                let y = e.offsetY;
                let thisCell = this.findClickedCellByOffset(x, y);
                let latestCell = this.latestClickCell;

                if (thisCell) {
                    console.log("thisCell", thisCell.xIndex, thisCell.yIndex);
                } else {
                    console.log("未选中thisCell");
                }
                if (latestCell) {
                    console.log("latestCell", latestCell.xIndex, latestCell.yIndex);
                } else {
                    console.log("未选中latestCell");
                }

                if (!thisCell && !latestCell) {
                    return;
                }
                if (thisCell && latestCell) {
                    if (thisCell.id === latestCell.id) {
                        console.log("确认");
                        //提交后台
                        thisCell.drawCheck();
                        this.latestClickCell = null;
                    } else {
                        console.log("重选");
                        this.undo();
                        thisCell.drawPreCheck();
                        latestCell.status = -1;
                        this.latestClickCell = thisCell;
                    }
                    return;
                }
                if (!thisCell) {
                    return;
                }
                this.save();
                thisCell.drawPreCheck();
                this.latestClickCell = thisCell;
            },
            save() {
                console.log("save");
                this.latestBoardSnapshot = this.boardContext.getImageData(0, 0, boardSize, boardSize);
            },
            undo() {
                console.log("undo");
                this.boardContext.putImageData(this.latestBoardSnapshot, 0, 0);
            },
            findClickedCellByOffset(x, y) {
                let box = this.gobangPointList;
                for (let idx_i in box) {
                    let cell = box[idx_i];
                    if (cell.status < 1 && cell.aroundHere(x, y)) {
                        return cell;
                    }
                }
            },
            findClickedCellByIndex(x, y) {
                let box = this.gobangPointList;
                for (let idx_i in box) {
                    let cell = box[idx_i];
                    if (cell.status < 1 && cell.aroundHere(x, y)) {
                        return cell;
                    }
                }
            },
            initWebSocket() {
                const wsUri = "ws://47.102.103.194:8099/ws/game-room";
                this.websock = new WebSocket(wsUri);
                this.websock.onmessage = this.onWebsocketMessage();
                this.websock.onopen = this.onWebsocketOpen();
                this.websock.onerror = this.onWebsocketError();
                this.websock.onclose = this.onWebsocketClose();
            },
            onWebsocketOpen() {
                let actions = {"test": "12345"};
                this.websocketSend(JSON.stringify(actions));
            },
            onWebsocketError() {
                this.initWebSocket();
            },
            onWebsocketMessage(e) {
                const redata = JSON.parse(e.data);
            },
            websocketSend(Data) {
                this.websock.send(Data);
            },
            onWebsocketClose(e) {
                Notify({type: 'danger', message: '连接中断,请刷新页面'});
            },
            initBoard() {
                console.log("开始初始化棋盘...");
                //棋盘背景色
                this.boardContext.fillStyle = "#ffdc99";
                this.boardContext.fillRect(0, 0, boardSize, boardSize);
                console.log('boardSize', boardSize);
                this.gobangPointList.forEach(it => it.status = -1);

                let that = this;

                function fillLine(line) {
                    let charInfo = line.charInfo;
                    //坐标文字
                    that.boardContext.fillStyle = "black";
                    that.boardContext.font = "15px '微软雅黑'";
                    that.boardContext.textAlign = "center";
                    that.boardContext.fillText(charInfo.char, charInfo.x, charInfo.y);
                    //线
                    that.boardContext.strokeStyle = "#2a2a2a";
                    that.boardContext.lineWidth = 0.5;
                    that.boardContext.moveTo(line.fromX, line.fromY);
                    that.boardContext.lineTo(line.toX, line.toY);
                    that.boardContext.stroke();
                }

                let lines = this.boardLines;
                lines.forEach(it => {
                    let hLine = it.h;
                    let vLine = it.v;
                    fillLine(hLine);
                    fillLine(vLine);
                });
                console.log("初始化棋盘完成...");
            }
        },
        mounted() {
            let canvas = document.createElement('canvas');
            canvas.id = 'gobang_board';
            canvas.width = screenWidth;
            canvas.height = screenWidth;
            canvas.addEventListener('click', e => this.whenClickBoard(e));
            document.getElementById('canvas_container').appendChild(canvas);
            this.boardContext = canvas.getContext('2d');
            this.initBoard();
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
        margin: auto auto;
        box-shadow: -2px -2px 2px #F3F2F2, 5px 5px 5px #6F6767;
    }
</style>