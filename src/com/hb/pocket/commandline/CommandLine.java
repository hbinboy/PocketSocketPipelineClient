package com.hb.pocket.commandline;

import com.hb.pocket.client.manager.ClientThreadManager;
import com.hb.pocket.commandline.command.AddCommand;
import com.hb.pocket.commandline.command.SendMessageCommand;
import com.hb.pocket.commandline.parser.LongOpt;
import com.hb.utils.log.MyLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hb on 05/08/2018.
 */
public class CommandLine {

    private static String TAG = CommandLine.class.getSimpleName();

    private List<ClientThreadManager> clients;

    private Map<String, List<LongOpt>> mainCommandMap = new HashMap<>();

    public CommandLine(List<ClientThreadManager> clients) {
        this.clients = clients;
        initMainCommand();
    }

    private void initMainCommand() {
        mainCommandMap = new HashMap<>();
        mainCommandMap.put("Exit".toLowerCase(), null);
        List<LongOpt> addList = new ArrayList<>();
        addList.add(new LongOpt("number", LongOpt.OPTIONAL_ARGUMENT, 'n',"Create a new clinet and connection to the server."));
        mainCommandMap.put("Add".toLowerCase(), addList);
        List<LongOpt> sendMessageList = new ArrayList<>();
        sendMessageList.add(new LongOpt("message", LongOpt.OPTIONAL_ARGUMENT, 'm', "Send a message to the server."));
        mainCommandMap.put("SendMessage".toLowerCase(), sendMessageList);
        List<LongOpt> delList = new ArrayList<>();
        delList.add(new LongOpt("number", LongOpt.OPTIONAL_ARGUMENT, 'n', "Delete a client and disconnection."));
        mainCommandMap.put("Del".toLowerCase(), delList);
        mainCommandMap.put("Help".toLowerCase(), null);
        mainCommandMap.put("Version".toLowerCase(), null);
    }

    private String[] string2Array(String commandLine) {
        if (commandLine == null || commandLine.trim().equals("")) {
            return null;
        }
        String[] tmp = commandLine.trim().split(" ");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] != null && !tmp[i].trim().equals("")) {
                list.add(tmp[i].trim());
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
            if (clients != null && clients.size() > 0) {
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).close();
                }
                clients.clear();
            }
            return true;
        }
        if (args[0].toLowerCase().startsWith("Add".toLowerCase())) {
            AddCommand addCommand = new AddCommand("Add", mainCommandMap.get(args[0].toLowerCase()),commandLine, "-:n", clients);
            addCommand.excute();
        }
        if (args[0].toLowerCase().startsWith("SendMessage".toLowerCase())) {
            SendMessageCommand sendMessage = new SendMessageCommand("SendMessage", mainCommandMap.get(args[0].toLowerCase()), commandLine, "-:m", clients);
            sendMessage.excute();
        }
        if (args[0].toLowerCase().startsWith("Del".toLowerCase())) {
            if (clients != null && clients.size() > 0) {
                clients.remove(0).close();
            }
        }
        if (args[0].toLowerCase().startsWith("Version".toLowerCase())) {
            MyLog.i(TAG, "1.0.0 version.");
        }
        if (args[0].toLowerCase().startsWith("Help".toLowerCase())) {
        }
        return false;
    }

}
