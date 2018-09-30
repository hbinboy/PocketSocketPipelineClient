package com.hb.pocket.clientv2.commandline.command;

import com.hb.pocket.clientv2.Client;
import com.hb.pocket.clientv2.manager.ClientThreadManager;
import com.hb.pocket.parser.LongOpt;
import com.hb.utils.Utils;
import com.hb.utils.log.MyLog;
import java.util.List;
import java.util.Map;

/**
 * Created by hb on 05/08/2018.
 */
public class AddCommand extends Command {

    private static String TAG = AddCommand.class.getSimpleName();

    List<Client> clients;

    Map<Client, Client> clientMap;

    public AddCommand(String commandName, List<LongOpt> longOptList, String commandLine, String optString, List<Client> clients, Map<Client, Client> clientMap) {
        super(commandName, longOptList, commandLine, optString);
        this.clients = clients;
        this.clientMap = clientMap;
    }

    @Override
    public boolean excute() {
        String str = "";
        int ch;
        while ((ch = getopt.getopt()) != -1) {
            switch (ch) {
                case 'n' :
                    str = getopt.getOptarg();
                    if (str != null && !str.equals("")) {
                        if (Utils.isNumeric(str)) {
                            int number = Integer.parseInt(str);
                            for (int i = 0; i < number; i++) {
                                Client c = new Client();
                                if (!c.init() || !c.connect()) {
                                    MyLog.i(TAG, "Init the client failed.");
                                    return false;
                                }
                                c.startLoop();
                                clients.add(c);
                                clientMap.put(c,c);
                            }
                            /*for (int i = 0; i < clients.size(); i++) {
                                if (clients.get(i).isStart() == false) {
                                    clients.get(i).connect();
                                }
                            }*/
                        }
                    }
                    break;
                case ':':
                    MyLog.i(TAG, "Need a paramer.");
                    return false;
                case '?':
                    MyLog.i(TAG,"Please help.");
                    return false;
                default:
                    return false;
            }
        }
        return false;
    }
}
