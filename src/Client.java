import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;

//Work needed
public class Client {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        NetworkUtility networkUtility = new NetworkUtility("127.0.0.1", 5555);
        System.out.println("Connected to server");
        /**
         * Tasks
         */

        EndDevice clientInfo;

//        1. Receive EndDevice configuration from server
        while (true) {
            Object obj = networkUtility.read();
            if(obj instanceof EndDevice) {
                clientInfo = (EndDevice) obj;
                break;
            }
        }

        System.out.println("Your device id : " +clientInfo.getDeviceID() );
//        2. Receive active client list from server


//        3. for(int i=0;i<100;i++)
//        4. {
//        5.      Generate a random message
//        6.      Assign a random receiver from active client list
//        7.      if(i==20)
//        8.      {
//        9.            Send the message and recipient IP address to server and a special request "SHOW_ROUTE"
//        10.           Display routing path, hop count and routing table of each router [You need to receive
//                            all the required info from the server in response to "SHOW_ROUTE" request]
//        11.     }
//        12.     else
//        13.     {
//        14.           Simply send the message and recipient IP address to server.
//        15.     }
//        16.     If server can successfully send the message, client will get an acknowledgement along with hop count
//                    Otherwise, client will get a failure message [dropped packet]
//        17. }



        System.out.println("\nDo u want to start sending packets?\n");
        String s = scanner.nextLine();



        for(int i=0;i<100;i++) {
            String random_msg = "Hello from "+clientInfo.getDeviceID();


            Packet newPacket = new Packet();
            newPacket.setMessage(random_msg);
            newPacket.setSourceIP(clientInfo.getIpAddress());


            if(i==50) {
                newPacket.setSpecialMessage("SHOW_ROUTE");
                networkUtility.write( (Object) newPacket );
            }
            else {
                networkUtility.write( (Object) newPacket );
            }



            while (true) {
                String server_msg = (String) networkUtility.read();
                if (server_msg.length() > 0) {
                    System.out.println(server_msg);
                    break;
                }
            }

        }

        //        18. Report average number of hops and drop rate

        Packet newPacket = new Packet();
        newPacket.setSpecialMessage("SHOW_REPORT");
        networkUtility.write( (Object) newPacket );

        while (true) {
            String server_msg = (String) networkUtility.read();
            if (server_msg.length() > 0) {
                System.out.println(server_msg);
                break;
            }
        }


        while (true) { }




    }
}
