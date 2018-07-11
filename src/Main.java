import com.hb.pocket.client.Client;
import com.hb.pocket.client.manager.ClientThreadManager;
import com.hb.pocket.client.thread.ClientThreadStatus;
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

        MyLog.d(TAG, "Hello Pocket Socket Pipeline Client!");

        List<ClientThreadManager> clients = new ArrayList<>();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()) {
            String str = sc.next();
            if (str.equals("Exit".toLowerCase())) {
                break;
            } else if (str.equals("Add".toLowerCase())) {
                for (int i = 0; i < 200;i++) {
                    ClientThreadManager c = new ClientThreadManager();
                    clients.add(c);
                }
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).isStart() == false) {
                        clients.get(i).connect();
                    }
                }
            } else if (str.equals("SendMsg".toLowerCase())) {
                for (int n = 0; n < 10; n++) {
                    for (int i = 0; i < clients.size(); i++) {
                        clients.get(i).sendMessage("Hello prokcet socket");
                    }
                }
            }
        }

        if (clients != null && clients.size() > 0) {
            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).close();
            }
            clients.clear();
        }

        MyLog.i(TAG, "Main exit.");


    }
}
