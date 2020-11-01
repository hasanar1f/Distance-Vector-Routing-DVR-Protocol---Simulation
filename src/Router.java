//Work needed
import java.util.*;

public class Router {

    private int routerId;
    private int numberOfInterfaces;
    private ArrayList<IPAddress> interfaceAddresses;//list of IP address of all interfaces of the router
    private ArrayList<RoutingTableEntry> routingTable;//used to implement DVR
    private ArrayList<Integer> neighborRouterIDs;//Contains both "UP" and "DOWN" state routers
    private Boolean state;//true represents "UP" state and false is for "DOWN" state
    private Map<Integer, IPAddress> gatewayIDtoIP;

    public Router() {
        interfaceAddresses = new ArrayList<>();
        routingTable = new ArrayList<>();
        neighborRouterIDs = new ArrayList<>();

        /**
         * 80% Probability that the router is up
         */
        Random random = new Random();
        double p = random.nextDouble();
        if(p < 0.80) state = true;
        else state = false;

        numberOfInterfaces = 0;
    }

    public Router(int routerId, ArrayList<Integer> neighborRouters, ArrayList<IPAddress> interfaceAddresses, Map<Integer, IPAddress> gatewayIDtoIP) {
        this.routerId = routerId;
        this.interfaceAddresses = interfaceAddresses;
        this.neighborRouterIDs = neighborRouters;
        this.gatewayIDtoIP = gatewayIDtoIP;
        routingTable = new ArrayList<>();



        /**
         * 80% Probability that the router is up
         */
        Random random = new Random();
        double p = random.nextDouble();
        if(p < 0.80) state = true;
        else state = false;

        numberOfInterfaces = interfaceAddresses.size();
    }

    @Override
    public String toString() {
        String string = "";
        string += "Router ID: " + routerId + "\n" + "Interfaces: \n";
        for (int i = 0; i < numberOfInterfaces; i++) {
            string += interfaceAddresses.get(i).getString() + "\t";
        }
        string += "\n" + "Neighbors: \n";
        for(int i = 0; i < neighborRouterIDs.size(); i++) {
            string += neighborRouterIDs.get(i) + "\t";
        }
        return string;
    }



    /**
     * Initialize the distance(hop count) for each router.
     * for itself, distance=0; for any connected router with state=true, distance=1; otherwise distance=Constants.INFTY;
     */
    public void initiateRoutingTable() {

        clearRoutingTable();

        for(Router r : NetworkLayerServer.routers) {
            RoutingTableEntry newEntry = new RoutingTableEntry(r.routerId,Constants.INFINITY,-1);

            for(int i : neighborRouterIDs) {
                if(i == r.routerId && NetworkLayerServer.routerMap.get(i).getState())
                    newEntry.setDistance(1);
            }

            if(routerId == r.routerId)
                newEntry.setDistance(0);

            routingTable.add(newEntry);
        }


     }

    /**
     * Delete all the routingTableEntry
     */
    public void clearRoutingTable() {
        routingTable.clear();
    }

    /**
     * Update the routing table for this router using the entries of Router neighbor
     * @param neighbor
     */
    public boolean sfupdateRoutingTable(Router neighbor) {


        boolean flag = false;

        int N = NetworkLayerServer.routers.size();


        for(int i=0;i<N;i++) {

            if(routingTable.size()==0 || neighbor.routingTable.size()==0 ) return false;
            if(state==false || neighbor.state == false)  return  false;

            if( routingTable.get(i).getGatewayRouterId() == neighbor.getRouterId() || // force update
                    ( ( ( neighbor.routingTable.get(i).getDistance() + 1.0 ) < routingTable.get(i).getDistance() ) &&
                        (neighbor.routingTable.get(i).getGatewayRouterId() != getRouterId() ) ) // split horizon applied
              ) {
                routingTable.get(i).setDistance( neighbor.routingTable.get(i).getDistance() + 1.0 );
                routingTable.get(i).setGatewayRouterId( neighbor.routerId );
                //System.out.println("updated!");
                flag = true;
            }


        }



        return  flag;

    }

    public boolean updateRoutingTable(Router neighbor) {

        boolean flag = false;

        int N = routingTable.size();

        for(int i=0;i<N;i++) {

            if ( ( neighbor.routingTable.get(i).getDistance() + 1.0 ) < routingTable.get(i).getDistance() ) {
                routingTable.get(i).setDistance( neighbor.routingTable.get(i).getDistance() + 1.0 );
                routingTable.get(i).setGatewayRouterId( neighbor.routerId );
                //System.out.println(" simply updated!");
                flag = true;
            }


        }


        return  flag;
    }

    /**
     * If the state was up, down it; if state was down, up it
     */
    public void revertState() {
        state = !state;
        if(state) { initiateRoutingTable(); }
        else { clearRoutingTable(); }
    }

    public int getRouterId() {
        return routerId;
    }

    public void setRouterId(int routerId) {
        this.routerId = routerId;
    }

    public int getNumberOfInterfaces() {
        return numberOfInterfaces;
    }

    public void setNumberOfInterfaces(int numberOfInterfaces) {
        this.numberOfInterfaces = numberOfInterfaces;
    }

    public ArrayList<IPAddress> getInterfaceAddresses() {
        return interfaceAddresses;
    }

    public void setInterfaceAddresses(ArrayList<IPAddress> interfaceAddresses) {
        this.interfaceAddresses = interfaceAddresses;
        numberOfInterfaces = interfaceAddresses.size();
    }

    public ArrayList<RoutingTableEntry> getRoutingTable() {
        return routingTable;
    }

    public void addRoutingTableEntry(RoutingTableEntry entry) {
        this.routingTable.add(entry);
    }

    public ArrayList<Integer> getNeighborRouterIDs() {
        return neighborRouterIDs;
    }

    public void setNeighborRouterIDs(ArrayList<Integer> neighborRouterIDs) { this.neighborRouterIDs = neighborRouterIDs; }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Map<Integer, IPAddress> getGatewayIDtoIP() { return gatewayIDtoIP; }

    public void printRoutingTable() {
        System.out.println("Router " + routerId);
        System.out.println("DestID\t Distance\t Nexthop");
        for (RoutingTableEntry routingTableEntry : routingTable) {
            System.out.println(routingTableEntry.getRouterId() + "\t " + routingTableEntry.getDistance() + "\t " + routingTableEntry.getGatewayRouterId());
        }
        System.out.println("-----------------------");
    }
    public String strRoutingTable() {
        String string = "Router" + routerId + "\n";
        string += "DestID\t Distance\t Nexthop\n";
        for (RoutingTableEntry routingTableEntry : routingTable) {
            string += routingTableEntry.getRouterId() + "\t " + routingTableEntry.getDistance() + "\t " + routingTableEntry.getGatewayRouterId() + "\n";
        }

        string += "-----------------------\n";
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Router router = (Router) o;
        return routerId == router.routerId &&
                numberOfInterfaces == router.numberOfInterfaces &&
                Objects.equals(interfaceAddresses, router.interfaceAddresses) &&
                Objects.equals(routingTable, router.routingTable) &&
                Objects.equals(neighborRouterIDs, router.neighborRouterIDs) &&
                Objects.equals(state, router.state) &&
                Objects.equals(gatewayIDtoIP, router.gatewayIDtoIP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routerId, numberOfInterfaces, interfaceAddresses, routingTable, neighborRouterIDs, state, gatewayIDtoIP);
    }
}
