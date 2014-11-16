package hubs;

import play.Logger;
import signalJ.services.Hub;

import java.util.*;

public class ChatHub extends Hub<ChatPage> {
    private final static Map<String, Set<String>> users = new HashMap<>();
    private final static Map<UUID, String> connectionsToUsernames = new HashMap<>();

    public boolean login(String username) {
        if(connectionsToUsernames.containsValue(username)) return false;
        clients().callerState.put("username", username);
        joinRoom("Lobby");
        clients().caller.roomList(getRoomList());
        clients().othersInGroup("Lobby").userJoined(username);
        clients().caller.userList(getUserList("Lobby"));
        connectionsToUsernames.putIfAbsent(context().connectionId, clients().callerState.get("username"));
        return true;
    }

    public void logout() {
        connectionsToUsernames.remove(context().connectionId);
        removeUserFromRoom(clients().callerState.get("username"));
        clients().callerState.put("username", "");
    }

    public void joinRoom(String room) {
        if(users.containsKey(room)) joinRoom(room, false);
        else createRoom(room);
        clients().group(room).userList(getUserList(room));
    }

    public void createRoom(String room) {
        users.putIfAbsent(room, new HashSet<>());
        joinRoom(room, true);
    }

    public void sendMessage(String room, String message) {
        clients().othersInGroup(room).sendMessage(clients().callerState.get("username"), message);
    }

    private void joinRoom(String room, boolean fromCreate) {
        boolean changed = removeUserFromRoom(clients().callerState.get("username"));
        addUserToRoom(clients().callerState.get("username"), room);
        if(fromCreate || changed) clients().all.roomList(getRoomList());
    }

    private void addUserToRoom(String username, String room) {
        users.get(room).add(username);
        groups().add(context().connectionId, room);
        clients().othersInGroup(room).userJoinedRoom(username);
    }

    private boolean removeUserFromRoom(String username) {
        String room = null;
        String removekey = null;
        for(String key : users.keySet()) {
            if(users.get(key).remove(username)) {
                room = key;
                clients().othersInGroup(key).userLeftRoom(username);
                clients().othersInGroup(key).userList(getUserList(key));
                if(users.get(key).size() == 0) removekey = key;
                break;
            }
        }
        if (room != null) groups().remove(context().connectionId, room);
        if(removekey != null) {
            users.remove(removekey);
            return true;
        }
        return false;
    }

    private Set<String> getUserList(String room) {
        return new HashSet<String>(users.get(room));
    }

    private Set<String> getRoomList() {
        return new HashSet<String>(users.keySet());
    }

    @Override
    protected Class<ChatPage> getInterface() {
        return ChatPage.class;
    }

    @Override
    public void onDisconnected() {
        final String username = connectionsToUsernames.remove(context().connectionId);
        Logger.debug("Disconnect: " + username);
        removeUserFromRoom(username);
    }
}