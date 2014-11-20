package actors;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import hubs.ChatHub;
import hubs.ChatPage;
import signalJ.GlobalHost;
import signalJ.services.HubContext;

public class Robot extends AbstractActor {
    public Robot() {
        receive(
                ReceiveBuilder.matchAny(x -> {
                    HubContext<ChatPage> hub = GlobalHost.getHub(ChatHub.class);
                    hub.clients().all.sendMessage("Robot", "I am alive!");
                }).build()
        );
    }
}