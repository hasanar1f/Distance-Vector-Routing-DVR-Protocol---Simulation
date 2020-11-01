

import java.util.ArrayList;
import java.util.Random;

public class ServerThread implements Runnable {

    NetworkUtility networkUtility;
    EndDevice endDevice;
    String info = "",route_info = "",report="";

    double total = 0,success = 0;

    ServerThread(NetworkUtility networkUtility, EndDevice endDevice) {
        this.networkUtility = networkUtility;
        this.endDevice = endDevice;
        networkUtility.write( (Object) endDevice );
        System.out.println("Server Ready for client " + NetworkLayerServer.clientCount);
        NetworkLayerServer.clientCount++;
        new Thread(this).start();
    }

    @Override
    public void run() {
        /**
         * Synchronize actions with client.
         */
        
        /*
        Tasks:
        1. Upon receiving a packet and recipient, call deliverPacket(packet)
        2. If the packet contains "SHOW_ROUTE" request, then fetch the required information
                and send back to client
        3. Either send acknowledgement with number of hops or send failure message back to client
        */

        while (true) {

            Object obj = networkUtility.read();

            if(obj instanceof Packet) {

                Packet newPacket = (Packet) obj;
                Random rand = new Random();
                int random_integer = rand.nextInt(NetworkLayerServer.endDevices.size() );

                EndDevice random_active_client = NetworkLayerServer.endDevices.get(random_integer);
                newPacket.setDestinationIP( random_active_client.getIpAddress() );

                if(newPacket.getSpecialMessage().equalsIgnoreCase("SHOW_REPORT")) {

                    report = "Report : Total sent : "+total+" success : "+success+" avg : "+(success/total);
                    networkUtility.write( report );

                }

                boolean isSuccess = deliverPacket(newPacket);
                total++;
                if(isSuccess) {
                    success++;
                    networkUtility.write( info + route_info );

                }
                else {
                    networkUtility.write("Packet sending failed!");

                }




            }

        }




    }


    public Boolean deliverPacket(Packet p) {


//        1. Find the router s which has an interface
//                such that the interface and source end device have same network address.

        Router s = null;

        p.hopcount = 0;

        for (Router r : NetworkLayerServer.routers) {

            IPAddress rip = r.getInterfaceAddresses().get(0);

            if(p.getSourceIP()==null) return  false;

            if ((rip.getBytes()[0].intValue() == p.getSourceIP().getBytes()[0].intValue()) &&
                    (rip.getBytes()[1].intValue() == p.getSourceIP().getBytes()[1].intValue()) &&
                    (rip.getBytes()[2].intValue() == p.getSourceIP().getBytes()[2].intValue())) {
                s = r;
            }


        }


//        2. Find the router d which has an interface
//                such that the interface and destination end device have same network address.

        Router d = null;

        for (Router r : NetworkLayerServer.routers) {

            IPAddress rip = r.getInterfaceAddresses().get(0);

            if(p.getDestinationIP()==null) return false;

            if ((rip.getBytes()[0].intValue() == p.getDestinationIP().getBytes()[0].intValue()) &&
                    (rip.getBytes()[1].intValue() == p.getDestinationIP().getBytes()[1].intValue()) &&
                    (rip.getBytes()[2].intValue() == p.getDestinationIP().getBytes()[2].intValue())) {
                d = r;
                break;
            }



        }



//        3. Implement forwarding, i.e., s forwards to its gateway router x considering d as the destination.
//                similarly, x forwards to the next gateway router y considering d as the destination,
//                and eventually the packet reaches to destination router d.



      //  System.out.println(s.getRouterId()+" to "+d.getRouterId());

        Router current_router = s;
        Router away = s;

        while (true) {


            p.hopcount++;

            current_router = away;

            for (RoutingTableEntry e : current_router.getRoutingTable()) {

                if (e.getRouterId() == d.getRouterId()) {

                    away = NetworkLayerServer.routers.get(e.getRouterId()-1);

                    if (away.getState() == false) {

                        e.setDistance(Constants.INFINITY);
                        RouterStateChanger.islocked = true;
                        NetworkLayerServer.DVR(current_router.getRouterId());
                        RouterStateChanger.islocked = false;

                        return false;
                    }


                    if (e.getDistance() == Constants.INFINITY) {
                        e.setDistance(1);
                        RouterStateChanger.islocked = true;
                        NetworkLayerServer.DVR(away.getRouterId());
                        RouterStateChanger.islocked = false;

                    }


                    break;

                }

            }


            if (current_router.getRouterId() == d.getRouterId()) {

                info = "Packet sent! Total hops : "+p.hopcount;
                route_info = "";
                if(p.getSpecialMessage().equalsIgnoreCase("SHOW_ROUTE")){
                    route_info = "\n";

                    for(Router rr : NetworkLayerServer.routers) {
                        route_info += rr.strRoutingTable();
                    }
                }


                return true;
            }

        }

    }


//
//            3(a) If, while forwarding, any gateway x, found from routingTable of router r is in down state[x.state==FALSE]
//                    (i) Drop packet
//                    (ii) Update the entry with distance Constants.INFTY
//                    (iii) Block NetworkLayerServer.stateChanger.t
//                    (iv) Apply DVR starting from router r.
//                    (v) Resume NetworkLayerServer.stateChanger.t
//
//            3(b) If, while forwarding, a router x receives the packet from router y,
//                    but routingTableEntry shows Constants.INFTY distance from x to y,
//                    (i) Update the entry with distance 1
//                    (ii) Block NetworkLayerServer.stateChanger.t
//                    (iii) Apply DVR starting from router x.
//                    (iv) Resume NetworkLayerServer.stateChanger.t
//
//        4. If 3(a) occurs at any stage, packet will be dropped,
//            otherwise successfully sent to the destination router



    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }
}
