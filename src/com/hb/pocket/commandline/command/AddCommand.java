package com.hb.pocket.commandline.command;

import com.hb.pocket.client.manager.ClientThreadManager;
import com.hb.pocket.commandline.parser.LongOpt;
import com.hb.utils.Utils;
import com.hb.utils.log.MyLog;

import java.util.List;

/**
 * Created by hb on 05/08/2018.
 */
public class AddCommand extends Command {

    private static String TAG = AddCommand.class.getSimpleName();


    List<ClientThreadManager> clients;

    public AddCommand(String commandName, List<LongOpt> longOptList, String commandLine, String optString, List<ClientThreadManager> clients) {
        super(commandName, longOptList, commandLine, optString);
        this.clients = clients;
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
                                ClientThreadManager c = new ClientThreadManager();
                                clients.add(c);
                            }
                            for (int i = 0; i < clients.size(); i++) {
                                if (clients.get(i).isStart() == false) {
                                    clients.get(i).connect();
                                }
                            }
                        }
                    } else {
                        ClientThreadManager c = new ClientThreadManager();
                        clients.add(c);
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
