// 初始化信息对象
let gameInfo = {
    roomId: null,
    thisUserId: null,
    thatUserId: null,
    isWhite: true
}

// 设定界面显示相关操作
function setScreenText(me) {
    let screen = document.querySelector('#screen');
    if (me) {
        screen.innerHTML = "轮到你落子了!";
    } else {
        screen.innerHTML = "轮到对方落子了!";
    }
}

// 初始化 websocket(此处的路径写作 /game, 不要写作 /game/ )
let websocket = new WebSocket("ws://127.0.0.1:8080/game");
websocket.onopen = function () {
    console.log("连接有房间成功!");
}

websocket.close = function () {
    console.log("和游戏服务器断开连接!");
}

websocket.onerror = function () {
    console.log("和游戏服务器的连接出现异常!")
}

websocket.onbeforeunload = function () {
    websocket.close();
}

// 处理服务器建立连接返回的数据响应
websocket.onmessage = function (event) {
    console.log("[handlerGameReady]" + event.data);
    let resp = JSON.parse(event.data);
    if (resp.message != 'gameReady') {
        console.log("响应类型错误!");
        return;
    }

    if (!resp.ok) {
        alert("连接游戏失败! reason: " + resp.reason);
        // 连接游戏失败, 返回游戏大厅
        location.assign("/game_hall.html");
        return;
    }

    // 得到的是正确的响应, 响应数据赋值给 gameInfo 对象
    gameInfo.roomId = resp.roomId;
    gameInfo.thatUserId = resp.thatUserId;
    gameInfo.thisUserId = resp.thisUserId;
    gameInfo.isWhite = resp.isWhite;

    // 初始化棋盘
    initGame();
    // 设置显示区域的内容
    setScreenText(gameInfo.isWhite);
}

//////////////////////////////////////////////////
// 初始化一局游戏
//////////////////////////////////////////////////
function initGame() {
    // 是我下还是对方下. 根据服务器分配的先后手情况决定
    let me = gameInfo.isWhite;
    // 游戏是否结束
    let over = false;
    let chessBoard = [];
    //初始化chessBord数组(表示棋盘的数组)
    for (let i = 0; i < 15; i++) {
        chessBoard[i] = [];
        for (let j = 0; j < 15; j++) {
            chessBoard[i][j] = 0;
        }
    }
    let chess = document.querySelector('#chess');
    let context = chess.getContext('2d');
    context.strokeStyle = "#BFBFBF";
    // 背景图片
    let logo = new Image();
    logo.src = "image/sky.jpeg";
    logo.onload = function () {
        context.drawImage(logo, 0, 0, 450, 450);
        initChessBoard();
    }

    // 绘制棋盘网格
    function initChessBoard() {
        for (let i = 0; i < 15; i++) {
            context.moveTo(15 + i * 30, 15);
            context.lineTo(15 + i * 30, 430);
            context.stroke();
            context.moveTo(15, 15 + i * 30);
            context.lineTo(435, 15 + i * 30);
            context.stroke();
        }
    }

    // 绘制一个棋子, me 为 true
    function oneStep(i, j, isWhite) {
        context.beginPath();
        context.arc(15 + i * 30, 15 + j * 30, 13, 0, 2 * Math.PI);
        context.closePath();
        var gradient = context.createRadialGradient(15 + i * 30 + 2, 15 + j * 30 - 2, 13, 15 + i * 30 + 2, 15 + j * 30 - 2, 0);
        if (!isWhite) {
            gradient.addColorStop(0, "#0A0A0A");
            gradient.addColorStop(1, "#636766");
        } else {
            gradient.addColorStop(0, "#D1D1D1");
            gradient.addColorStop(1, "#F9F9F9");
        }
        context.fillStyle = gradient;
        context.fill();
    }

    chess.onclick = function (e) {
        if (over) {
            return;
        }
        if (!me) {
            return;
        }
        let x = e.offsetX;
        let y = e.offsetY;
        // 注意, 横坐标是列, 纵坐标是行
        let col = Math.floor(x / 30);
        let row = Math.floor(y / 30);
        if (chessBoard[row][col] == 0) {
            // TODO 发送坐标给服务器, 服务器要返回结果

            oneStep(col, row, gameInfo.isWhite);
            chessBoard[row][col] = 1;
        }
    }
}

