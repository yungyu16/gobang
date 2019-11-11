<template>
    <div>
        <van-row justify="center" type="flex">
            <van-col span="6">
                <strong>对局时间:</strong>
            </van-col>
            <van-col span="6">
                <van-count-down :time="5000000"/>
            </van-col>
        </van-row>
        <van-divider/>
        <van-row justify="center" type="flex">
            <van-col span="6">
                <avatar :size="50"
                        :username="thatUser.userName"
                        backgroundColor="#03a9f4"
                        round>
                </avatar>
            </van-col>
            <van-col span="6">
                <strong>{{thatUser.userName}}</strong>
                <br/>
                <strong>{{thatUserColor}}</strong>
            </van-col>
        </van-row>
        <van-divider/>
        <div id="canvas_container">
        </div>
        <van-divider/>
        <div>
            <van-row justify="center" type="flex">
                <van-col span="6">
                    <avatar :size="50"
                            :username="thisUser.userName"
                            backgroundColor="#03a9f4"
                            round>
                    </avatar>
                </van-col>
                <van-col span="6">
                    <strong>{{thisUser.userName}}</strong>
                    <br/>
                    <strong>{{thisUserColor}}</strong>
                </van-col>
            </van-row>
        </div>
    </div>
</template>
<script>
    import Avatar from 'vue-avatar'
    import {Dialog, Notify} from 'vant';
    import config from '../../config'
    import apis from '../../apis'
    import util from '../../util'


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
        components: {
            Avatar
        },
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
                checkAudio: new Audio("https://www.cosumi.net/sound/stone.ogg"),
                gameWebSocket: null,
                gameStartFlag: false,
                notifyGameStartFlag: false,
                isGameWatcher: false,
                latestCheckCell: {status: 2},
                thisUser: {
                    userName: '等待中...',
                    color: 0
                },
                thatUser: {
                    userName: '等待中...',
                    color: 0
                },
            };
        },
        created() {
            apis.user.validate({});

            let that = this;

            function drawPreCheck() {
                console.log("预选", this.xIndex, this.yIndex);
                that.boardContext.beginPath();
                that.boardContext.arc(this.x, this.y, scale(13), 0, 2 * Math.PI);
                that.boardContext.fillStyle = that.thisUser.color === 1 ? "#0A0A0A" : "#D1D1D1";
                that.boardContext.stroke();
                that.boardContext.closePath();
                this.status = 0;
            }

            function drawCheck(color) {
                console.log("确认", this.xIndex, this.yIndex);
                that.boardContext.beginPath();
                that.boardContext.arc(this.x, this.y, scale(13), 0, 2 * Math.PI);
                let style = that.boardContext.createRadialGradient(this.x, this.y, scale(13), this.x, this.y, 0);
                switch (color) {
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
                this.status = color;
                that.checkAudio.play();
            }

            gobangPointList.forEach(it => {
                it.drawPreCheck = drawPreCheck;
                it.drawCheck = drawCheck;
            });
            this.initWebSocket();
        },
        methods: {
            initWebSocket() {
                console.log("开始新建game ws连接...");
                let wsUri = `ws://${config.apiHost}/ws/game`;
                this.gameWebSocket = new WebSocket(wsUri);
                this.gameWebSocket.onmessage = this.onWsMessage;
                this.gameWebSocket.onopen = this.onWsOpen;
                this.gameWebSocket.onerror = e => {
                    console.log('game ws 连接错误...', wsUri, e);
                };
                this.gameWebSocket.onclose = () => {
                    console.log('game ws 连接关闭...', wsUri);
                }
            },
            onWsOpen() {
                console.log('game ws 连接完毕...');
                let pingMsg = {
                    msgType: 'enterGame',
                    gameId: this.$route.query.gameId,
                    sessionToken: this.getSessionToken(),
                    data: this.$route.query.gameId
                };
                console.log("game ws发送 enterGame消息：", pingMsg);
                this.gameWebSocket.send(JSON.stringify(pingMsg));
            },
            onWsMessage(msg) {
                let msgData = JSON.parse(msg.data);
                console.log("game ws收到消息：", msgData);
                let msgType = msgData.msgType;
                let data = msgData.data;
                switch (msgType) {
                    case 'welcome':
                        console.log("game ws连接成功：", data);
                        break;
                    case 'initGame':
                        this.initGame(data);
                        break;
                    case 'startGame':
                        this.gameStartFlag = true;
                        break;
                    case 'checkBoard':
                        this.checkBoard(data);
                        break;
                    case 'gameOver':
                        util.mobileShock();
                        let msg = '对局结束喽~';
                        if (!this.isGameWatcher) {
                            if (this.thisUser.color === data) {
                                msg = msg + '恭喜您获得了胜利~';
                            } else {
                                msg = msg + '很遗憾您输掉了比赛,下次继续努力~';
                            }
                        }
                        Dialog.confirm({
                            title: '对局结束',
                            message: msg
                        }).finally(() => {
                            this.$router.push({path: '/tab/start'});
                        });
                        break;
                    case 'toast':
                        if (data) {
                            this.$toast(data);
                        }
                        break;
                    case 'error':
                        if (data) {
                            this.$notify(data);
                        }
                        setTimeout(() => {
                            window.location.reload();
                        }, 1000);
                        break;
                }
            },
            initGame(data) {
                this.isGameWatcher = data.isGameWatcher;
                this.thisUser.userName = data.thisUser.userName;
                this.thisUser.color = data.thisUser.color;
                let thatUser = data.thatUser;
                if (thatUser) {
                    this.thatUser.userName = thatUser.userName;
                    this.thatUser.color = thatUser.color;
                }
            },
            checkBoard(data) {
                let boardCell = this.findClickedCellByIndex(data.x, data.y);
                if (!boardCell) {
                    console.log("没有找到坐标:", data);
                    return;
                }
                boardCell.drawCheck(data.color);
                this.latestCheckCell = boardCell;
                console.log(this.latestCheckCell.color)
            },
            whenClickBoard(e) {
                if (!this.notifyGameStartFlag || this.isGameWatcher) {
                    return;
                }

                if (this.latestCheckCell.status === this.thisUser.color) {
                    return;
                }

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
                        let pointMsg = {
                            msgType: 'checkBoard',
                            sessionToken: this.getSessionToken(),
                            gameId: this.$route.query.gameId,
                            data: {
                                x: thisCell.xIndex,
                                y: thisCell.yIndex
                            }
                        };
                        this.gameWebSocket.send(JSON.stringify(pointMsg));
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
            findClickedCellByIndex(xIndex, yIndex) {
                let box = this.gobangPointList;
                for (let idx_i in box) {
                    let cell = box[idx_i];
                    if (cell.id === 'x' + xIndex + ':y' + yIndex) {
                        return cell;
                    }
                }
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
        computed: {
            thisUserColor() {
                if (this.thisUser.color === 0) {
                    return '未知';
                }
                return this.thisUser.color === 1 ? "黑方" : "白方";
            },
            thatUserColor() {
                if (this.thatUser.color === 0) {
                    return '未知';
                }
                return this.thatUser.color === 1 ? "黑方" : "白方";
            },
        },
        watch: {
            gameStartFlag: function (newFlag, oldFlag) {
                if (this.isGameWatcher) {
                    return;
                }
                util.mobileShock();
                let msg = '对局开始喽~';
                if (this.thisUser.color === 1) {
                    msg = msg + '您方是先手,请开始游戏~';
                }
                Dialog.confirm({
                    title: '对局开始',
                    message: msg
                }).then(() => {
                    this.notifyGameStartFlag = true;
                });
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