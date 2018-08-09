package com.hb.pocket.clientv2.manager;

import com.hb.pocket.clientv2.Client;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hb on 07/08/2018.
 */
public class ClientThreadManager {

    private static String TAG = ClientThreadManager.class.getSimpleName();

    private Map<Client, Client> clientMap = new HashMap();

    private List<Client> clients = new ArrayList<>();

    public ClientThreadManager() {
        clientMap = new HashMap();
        clients = new ArrayList<>();
    }

    public boolean add() {
        Client client = new Client();
        client.init();
        if (client.connect()) {
            client.startLoop();
            clientMap.put(client, client);
            clients.add(client);

        } else {
            return false;
        }
        return true;
    }

    public Client remove(Client client) {
        if (clientMap.containsValue(client)) {
            clients.remove(client);
            return clientMap.remove(client);
        } else {
            return null;
        }
    }

    public void sendBroadMessage(String msg) {

    }

    public Map<Client, Client> getClientMap() {
        return clientMap;
    }

    public List<Client> getClients() {
        return clients;
    }
}
