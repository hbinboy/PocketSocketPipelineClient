package com.hb.pocket.clientv2.commandline.command;

import com.hb.pocket.clientv2.Client;
import com.hb.pocket.clientv2.manager.ClientThreadManager;
import com.hb.pocket.parser.LongOpt;
import com.hb.utils.log.MyLog;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by hb on 05/08/2018.
 */
public class SendMessageCommand extends Command {

    private static String TAG = SendMessageCommand.class.getSimpleName();

    List<Client> clients;

    Map<Client, Client> clientMap;

    public SendMessageCommand(String commandName, List<LongOpt> longOptList, String commandLine, String optString, List<Client> clients, Map<Client, Client> clientMap) {
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
                case 'm' :
                    str = getopt.getOptarg();
                    if (str != null && !str.equals("") && clients != null && clients.size() > 0) {
                        int count = clients.size();
                        for (int i = 0; i < count; i++) {
                            try {
                                if (clients.get(i).isStart() == true) {
                                    clients.get(i).sendMessage(str);
                                } else {
                                    clientMap.remove(clients.remove(i));
                                    count--;
                                    i--;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
