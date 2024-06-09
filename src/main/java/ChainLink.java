import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
@XmlAccessorType(XmlAccessType.FIELD)
public class ChainLink {
    private String key;
    private String value;
    @XmlElement(name="locations")
    private List<String> locations;

    public ChainLink() {
        this.locations = new ArrayList<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void addLocation(String location) { this.locations.add(location); }

    public void removeLocation(String location) { this.locations.remove(location); }
}
