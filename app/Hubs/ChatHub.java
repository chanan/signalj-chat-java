package Hubs;

import signalJ.services.Hub;

public class ChatHub extends Hub<ChatPage> {

    @Override
    protected Class<ChatPage> getInterface() {
        return ChatPage.class;
    }
}