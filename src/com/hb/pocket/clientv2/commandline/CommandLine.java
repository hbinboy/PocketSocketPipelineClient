package com.hb.pocket.clientv2.commandline;

import com.hb.pocket.clientv2.Client;
import com.hb.pocket.clientv2.commandline.command.AddCommand;
import com.hb.pocket.clientv2.commandline.command.SendMessageCommand;
import com.hb.pocket.clientv2.manager.ClientThreadManager;
import com.hb.pocket.parser.LongOpt;
import com.hb.utils.log.MyLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hb on 07/08/2018.
 */
public class CommandLine {

    private static String TAG = CommandLine.class.getSimpleName();

    private ClientThreadManager clientThreadManager;

    private Map<String, List<LongOpt>> mainCommandMap = new HashMap<>();

    public CommandLine() {
        clientThreadManager = new ClientThreadManager();
        initMainCommand();
    }

    private void initMainCommand() {
        mainCommandMap = new HashMap<>();
        mainCommandMap.put("Exit".toLowerCase(), null);
        List<LongOpt> addList = new ArrayList<>();
        addList.add(new LongOpt("number", LongOpt.OPTIONAL_ARGUMENT, 'n',"Create a new clinet and connection to the server."));
        addList.add(new LongOpt("ip", LongOpt.OPTIONAL_ARGUMENT, 'i',"The connection to the server's IP."));
        addList.add(new LongOpt("port", LongOpt.OPTIONAL_ARGUMENT, 'p',"The connection to the server's port."));
        mainCommandMap.put("Add".toLowerCase(), addList);
        List<LongOpt> sendMessageList = new ArrayList<>();
        sendMessageList.add(new LongOpt("message", LongOpt.OPTIONAL_ARGUMENT, 'm', "Send a message to the server."));
        mainCommandMap.put("SendMessage".toLowerCase(), sendMessageList);
        List<LongOpt> delList = new ArrayList<>();
        delList.add(new LongOpt("number", LongOpt.OPTIONAL_ARGUMENT, 'n', "Delete a client and disconnection."));
        mainCommandMap.put("Del".toLowerCase(), delList);
        mainCommandMap.put("Help".toLowerCase(), null);
        mainCommandMap.put("Version".toLowerCase(), null);
        mainCommandMap.put("ClearClient".toLowerCase(), null);
    }

    private String[] string2Array(String commandLine) {
        if (commandLine == null || commandLine.trim().equals("")) {
            return null;
        }
        String[] tmpResult = commandLine.trim().split(" ");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tmpResult.length; i++) {
            if (tmpResult[i] != null && !tmpResult[i].trim().equals("")) {
                if (tmpResult[i].startsWith("-") || tmpResult[i].startsWith("--")) {
                    String[] tmp = tmpResult[i].split("=");
                    if (tmp != null) {
                        for (int j = 0; j < tmp.length; j++) {
                            list.add(tmp[j]);
                        }
                    }
                } else {
                    list.add(tmpResult[i].trim());
                }
            }
        }
        String[] result = list.toArray(new String[list.size()]);
        return result;
    }

    public boolean excute(String commandLine) {
        String[] args = string2Array(commandLine);
        if (args == null || args.length == 0) {
            return false;
        }
        if (args[0].toLowerCase().startsWith("Exit".toLowerCase())) {
            if (clientThreadManager.getClients() != null && clientThreadManager.getClients().size() > 0) {
                for (int i = 0; i < clientThreadManager.getClients().size(); i++) {
                    try {
                        clientThreadManager.getClients().get(i).close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                clientThreadManager.getClients().clear();
                clientThreadManager.getClientMap().clear();
            } else {
                MyLog.i(TAG, "Not support this command!");
            }
            return true;
        } else if (args[0].toLowerCase().startsWith("Add".toLowerCase())) {
            if (mainCommandMap.get(args[0].toLowerCase()) != null && mainCommandMap.get(args[0].toLowerCase()).size() != 0) {
                AddCommand addCommand = new AddCommand("Add", mainCommandMap.get(args[0].toLowerCase()), commandLine, "-:n",
                        clientThreadManager.getClients(), clientThreadManager.getClientMap());
                addCommand.excute();
            } else {
                MyLog.i(TAG, "Not support this command!");
            }
        } else if (args[0].toLowerCase().startsWith("SendMessage".toLowerCase())) {
            if (mainCommandMap.get(args[0].toLowerCase()) != null && mainCommandMap.get(args[0].toLowerCase()).size() != 0) {
                SendMessageCommand sendMessage = new SendMessageCommand("SendMessage", mainCommandMap.get(args[0].toLowerCase()), commandLine, "-:m",
                        clientThreadManager.getClients(), clientThreadManager.getClientMap());
                sendMessage.excute();
            } else {
                MyLog.i(TAG, "Not support this command!");
            }
        } else if (args[0].toLowerCase().startsWith("Del".toLowerCase())) {
            if (clientThreadManager.getClients() != null && clientThreadManager.getClients().size() > 0) {
                try {
                    Client client = clientThreadManager.getClientMap().remove(clientThreadManager.getClients().remove(0));
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                MyLog.i(TAG, "Not support this command!");
            }
        } else if (args[0].toLowerCase().startsWith("Version".toLowerCase())) {
            MyLog.i(TAG, "1.0.0 version.");
        } else if (args[0].toLowerCase().startsWith("Help".toLowerCase())) {
        } else if (args[0].toLowerCase().startsWith("ClearClient".toLowerCase())) {
            if (clientThreadManager.getClients() != null && clientThreadManager.getClients().size() > 0) {
                try {
                    while (clientThreadManager.getClients().size() > 0){
                        Client client = clientThreadManager.getClientMap().remove(clientThreadManager.getClients().remove(0));
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                MyLog.i(TAG, "Not support this command!");
            }
        } else {
            MyLog.i(TAG, "Not support this command!");
        }
        return false;
    }
}
