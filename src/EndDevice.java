import java.io.Serializable;
import java.util.Objects;

//Done!
public class EndDevice implements Serializable {

    private IPAddress ipAddress;
    private IPAddress gateway;
    private Integer deviceID;


    public EndDevice(IPAddress ipAddress, IPAddress gateway, Integer deviceID) {
        this.ipAddress = ipAddress;
        this.gateway = gateway;
        this.deviceID = deviceID;
    }

    public IPAddress getIpAddress() {
        return ipAddress;
    }

    public IPAddress getGateway() { return gateway; }

    public Integer getDeviceID() { return deviceID; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndDevice endDevice = (EndDevice) o;
        return Objects.equals(ipAddress, endDevice.ipAddress) &&
                Objects.equals(gateway, endDevice.gateway) &&
                Objects.equals(deviceID, endDevice.deviceID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ipAddress, gateway, deviceID);
    }
}
