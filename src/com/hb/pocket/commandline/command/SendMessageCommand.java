package com.hb.pocket.commandline.command;

import com.hb.pocket.client.manager.ClientThreadManager;
import com.hb.pocket.commandline.parser.LongOpt;
import com.hb.utils.log.MyLog;
import java.util.List;

/**
 * Created by hb on 05/08/2018.
 */
public class SendMessageCommand extends Command{

    private static String TAG = SendMessageCommand.class.getSimpleName();

    private List<ClientThreadManager> clients;

    public SendMessageCommand(String commandName, List<LongOpt> longOptList, String commandLine, String optString, List<ClientThreadManager> clients) {
        super(commandName, longOptList, commandLine, optString);
        this.clients = clients;
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
                        for (int i = 0; i < clients.size(); i++) {
                            clients.get(i).sendMessage(str);
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
