package com.example.gobang.game;

import com.example.gobang.GobangApplication;
import com.example.gobang.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * Description: 表示一个游戏房间
 * User: cbiltps
 * Date: 2023-05-05
 * Time: 10:24
 */
@Data
public class Room {
    // 使用字符串类型来表示, 方便生成唯一值
    private String roomId;
    private User player1;
    private User player2;
    private int whiteUser; // 先手玩家的玩家id

    private static final int MAX_ROW = 15;
    private static final int MAX_COL = 15;

    /**
     * 表示棋盘
     * 0: 表示未落子
     * 1: 表示 玩家1 的落子位置
     * 2: 表示 玩家2 的落子位置
     */
    private int[][] board = new int[MAX_ROW][MAX_COL];

    private ObjectMapper objectMapper = new ObjectMapper();

    private OnlineUserManager onlineUserManager;

    private RoomManager roomManager;

    public Room() {
        // 构造 Room 的时候生成一个唯一的字符串表示房间 id, 使用 UUID 来作为房间 id~~
        roomId = UUID.randomUUID().toString();

        // 通过入口类中记录的 context 来手动获取前面的 RoomManger 和 OnlineUserManger
        onlineUserManager = GobangApplication.context.getBean(OnlineUserManager.class);
        roomManager = GobangApplication.context.getBean(RoomManager.class);
    }

    /**
     * 处理落子操作
     * @param reqJsonString
     * @throws IOException
     */
    public void putChess(String reqJsonString) throws IOException {
        GameRequest request = objectMapper.readValue(reqJsonString, GameRequest.class);
        GameResponse response = new GameResponse();

        // 1. 记录当前落子的位置
        int chess = request.getUserId() == player1.getUserId() ? 1 : 2; // 判断是 玩家1 落子, 还是 玩家2 落子.
        int row = request.getRow();
        int col = request.getCol();
        if (board[row][col] != 0) {
            // 在客户端已经针对重复落子进行过判定了. 此处为了程序更加稳健, 在服务器再判定一次.
            System.out.println("当前位置 (" + row + ", " + col + ") 已经有子了!");
            return;
        }
        board[row][col] = chess;

        // 2. 打印当前的棋盘信息(方便来观察局势, 也方便后面判定胜负关系)
        printBoard();
        // 3. 进行胜负判定
        int winner = checkWinner(row, col, chess);

        // 4. 给房间中的所有客户端都返回响应
        response.setMessage("putChess");
        response.setUserId(request.getUserId());
        response.setRow(row);
        response.setCol(col);
        response.setWinner(winner);

        // 要想给用户发送 WebSocket 数据, 就需要获取到这个用户的 WebSocketSession
        WebSocketSession session1 = onlineUserManager.getSessionStatusFromGameRoom(player1.getUserId());
        WebSocketSession session2 = onlineUserManager.getSessionStatusFromGameRoom(player2.getUserId());

        // 万一当前查到的会话为空(玩家已经下线了), 特殊处理一下(另一个玩家获胜)
        if (session1 == null) {
            // 玩家1 已经下线了, 直接认为玩家2 获胜
            response.setWinner(player2.getUserId());
            System.out.println("玩家1掉线!");
        }
        if (session2 == null) {
            // 玩家2 已经下线, 直接认为玩家1 获胜
            response.setWinner(player1.getUserId());
            System.out.println("玩家2掉线!");
        }

        // 把响应构造成 JSON 字符串, 通过 session 进行传输
        String respJsonStr = objectMapper.writeValueAsString(response);
        session1.sendMessage(new TextMessage(respJsonStr));
        session2.sendMessage(new TextMessage(respJsonStr));


        // 5. 如果当前胜负已分, 此时这个房间就失去存在的意义了, 就可以直接销毁房间(把房间从房间管理器中给移除)
        if (response.getWinner() != 0) {
            // 胜负已分
            System.out.println("游戏结束~房间即将销毁~ roomId=" + roomId + " 获胜方为: " + response.getWinner());

            // TODO 更新获胜方和失败方的信息


            // 销毁房间
            roomManager.removeRoom(roomId, player1.getUserId(), player2.getUserId());
        }
    }

    private int checkWinner(int row, int col, int chess) {
        // 1. 检查所有的行(先遍历五种情况)
        for (int c = col-4; c <= col; c++) {
            // 判定五个子是否连在一起(颜色也是一致的)
            try {
                if (board[row][c] == chess
                        && board[row][c + 1] == chess
                        && board[row][c + 2] == chess
                        && board[row][c + 3] == chess
                        && board[row][c + 4] == chess) {
                    // 胜负已分, 构成五子连珠
                    return chess == 1 ? player1.getUserId() : player2.getUserId();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                // 出现数组下标越界, 直接忽略异常就可以
                continue;
            }
        }
        return 0;
    }

    /**
     * 打印棋盘
     */
    private void printBoard() {
        System.out.println("[打印棋盘信息] " + roomId);
        System.out.println("=================================");
        for (int r = 0; r < MAX_ROW; r++) {
            for (int c = 0; c < MAX_COL; c++) {
                System.out.print(board[r][c] + " ");
            }
            // 每次遍历完一行之后, 再打印换行
            System.out.println();
        }
        System.out.println("=================================");
    }
}
