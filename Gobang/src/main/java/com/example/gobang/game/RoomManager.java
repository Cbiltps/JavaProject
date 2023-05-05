package com.example.gobang.game;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description: 房间管理器(希望是唯一实例)
 * User: cbiltps
 * Date: 2023-05-05
 * Time: 10:34
 */
@Component
public class RoomManager {
    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> playerIdAndRoomId = new ConcurrentHashMap<>();

    public void addRoom(Room room, int playerId1, int playerId2) {
        rooms.put(room.getRoomId(), room);
        playerIdAndRoomId.put(playerId1, room.getRoomId());
        playerIdAndRoomId.put(playerId2, room.getRoomId());
    }

    public void removeRoom(String roomId, int playerId1, int playerId2) {
        rooms.remove(roomId);
        playerIdAndRoomId.remove(playerId1);
        playerIdAndRoomId.remove(playerId2);
    }

    public Room getRoomByRoomId(String roomId) {
        return rooms.get(roomId);
    }

    public Room getRoomByPlayer(int player) {
        String roomId = playerIdAndRoomId.get(player);
        if (roomId == null) {
            // userId -> roomId 的映射关系不存在, 直接返回 null
            return null;
        }
        return getRoomByRoomId(roomId);
    }
}
