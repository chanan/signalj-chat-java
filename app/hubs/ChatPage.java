package hubs;

import java.util.Set;

public interface ChatPage {
    public void userJoined(String username);
    public void sendMessage(String username, String message);
    public void userList(Set<String> users);
    public void userLeftRoom(String username);
    public void userJoinedRoom(String username);
    public void roomList(Set<String> rooms);
}