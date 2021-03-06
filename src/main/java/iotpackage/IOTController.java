package iotpackage;


import org.springframework.web.bind.annotation.*;
import webSocket.objects.User;
import webSocket.WebSocketController;
import webSocket.WebSocketMessageClientEndpoint;
import webSocket.WebSocketUserClientEndpoint;

import java.net.URISyntaxException;

@RestController
public class IOTController {

    private WebSocketMessageClientEndpoint webSocket;
    private String serverID = "1";

    public IOTController() {
        try {
            this.webSocket = new WebSocketController().setDevicesWebSocket();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/lampAction", method = RequestMethod.GET)
    public String lamp(@RequestParam(value = "deviceId", defaultValue = "") String lampID,
                       @RequestParam(value = "userName", defaultValue = "") String userName,
                       @RequestParam(value = "userToken", defaultValue = "") String token,
                       @RequestParam(value = "lampAction", defaultValue = "") String action,
                       @RequestParam(value = "newDeviceDescription", defaultValue = "") String newDeviceDescription) throws Exception {

        while (webSocket.userSession == null) {
            this.webSocket = new WebSocketController().setDevicesWebSocket();
        }

        Lamp lamp = new Lamp();
        lamp.setWebSocket(webSocket);
        return lamp.lampAction(serverID, lampID, userName, token, action, newDeviceDescription);
    }

    @RequestMapping(value = "/remoteAction", method = RequestMethod.GET)
    public String remote(@RequestParam(value = "action", defaultValue = "") String action,
                         @RequestParam(value = "deviceId", defaultValue = "") String deviceID,
                         @RequestParam(value = "userName", defaultValue = "") String userName,
                         @RequestParam(value = "userToken", defaultValue = "") String token,
                         @RequestParam(value = "remoteOption", defaultValue = "0") int remoteOption) throws Exception {
        //todo a use of while loop here might cause an issue of infinite loop, consider changing this
        while (webSocket.userSession == null) {
            this.webSocket = new WebSocketController().setDevicesWebSocket();
        }
        Remote remote = new Remote(action, serverID, deviceID, userName, token, remoteOption);
        remote.setWebSocket(webSocket);
        return remote.remoteAction();
    }

    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    public @ResponseBody
    String registerUser(@RequestBody User user) throws URISyntaxException {
        WebSocketUserClientEndpoint userWebSocket = new WebSocketController().setUsersWebSocket();
        return new UserActions(userWebSocket).registerUser(user);
    }

    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public @ResponseBody
    String userLogin(@RequestBody User user) throws URISyntaxException {
        WebSocketUserClientEndpoint userWebSocket = new WebSocketController().setUsersWebSocket();
        return new UserActions(userWebSocket).userLogin(user);
    }
}