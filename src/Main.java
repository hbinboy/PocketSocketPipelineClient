import com.hb.pocket.client.manager.ClientThreadManager;
//import com.hb.pocket.client.commandline.CommandLine;
import com.hb.pocket.clientv2.commandline.CommandLine;
import com.hb.utils.log.MyLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by hb on 11/07/2018.
 */
public class Main {

    private static String TAG = Main.class.getSimpleName();

    public static void main(String[] args) {
        startClientV2();
    }

    private static void startClient() {
        MyLog.d(TAG, "Hello Pocket Socket Pipeline Client!");

        List<ClientThreadManager> clients = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        com.hb.pocket.client.commandline.CommandLine commandLine = new com.hb.pocket.client.commandline.CommandLine(clients);
        while (sc.hasNext()) {
            String str = sc.next();
            if (commandLine.excute(str)) {
                break;
            }
        }

        MyLog.i(TAG, "Main exit.");
    }

    private static void startClientV2() {
        MyLog.d(TAG, "Hello Pocket Socket Pipeline ClientV2!");

        List<ClientThreadManager> clients = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        sc.useDelimiter("\n");
        CommandLine commandLine = new CommandLine();
        while (sc.hasNext()) {
            String str = sc.next();
            if (commandLine.excute(str)) {
                break;
            }
        }

        MyLog.i(TAG, "Main exit.");
    }

}
