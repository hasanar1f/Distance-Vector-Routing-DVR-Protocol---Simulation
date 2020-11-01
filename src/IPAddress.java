import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

//Done!
public class IPAddress implements Serializable {

    private Short bytes[];
    private String string;

    public IPAddress(String string) {
        bytes = new Short[4];
        this.string = string;
        String[] temp = string.split("\\.");
        for (int i = 0; i < 4; i++) {
            bytes[i] = Short.parseShort(temp[i]);
        }

    }

    public Short[] getBytes()
    {
        return bytes;
    }

    public String getString()
    {
        return string;
    }

    @Override
    public String toString() { return string; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPAddress ipAddress = (IPAddress) o;
        return Arrays.equals(bytes, ipAddress.bytes) &&
                Objects.equals(string, ipAddress.string);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(string);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }
}
