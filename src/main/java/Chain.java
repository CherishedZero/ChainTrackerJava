import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Chain {
    @XmlElement(name="Chain")
    private List<ChainLink> chain = new ArrayList<>();

    public List<ChainLink> getChain() {
        return chain;
    }

    public List<String> getKeys() {
        ArrayList<String> keys = new ArrayList<>();
        for (ChainLink link: chain) {
            keys.add(link.getKey());
        }
        return keys;
    }

    public List<String> getValues() {
        ArrayList<String> values = new ArrayList<>();
        for (ChainLink link: chain) {
            values.add(link.getValue());
        }
        return values;
    }

    public List<String> getLocations() {
        ArrayList<String> locations = new ArrayList<>();
        for (ChainLink link: chain) {
            for (String location: link.getLocations()) {
                if (!locations.contains(location) && !location.equals("N/A") && !location.isEmpty()) {
                    locations.add(location);
                }
            }
        }
        return locations;
    }

    public List<String> getLocationsByKey(String key) {
        List<String> locations = new ArrayList<>();
        for (ChainLink link: chain) {
            if(link.getKey().equals(key)) {
                locations = link.getLocations();
            }
        }
        return locations;
    }

    public String getValueByKey(String key) {
        String value = "No Value";
        for (ChainLink link: chain) {
            if(link.getKey().equals(key)) {
                value = link.getValue();
            }
        }
        return value;
    }

    public List<String> getKeysByValue(String value) {
        ArrayList<String> keys = new ArrayList<>();
        for (ChainLink link: chain) {
            if (link.getValue().equals(value)) {
                keys.add(link.getKey());
            }
        }
        return keys;
    }

    public ChainLink getLinkByKey(String key) {
        ChainLink outputLink = new ChainLink();
        boolean foundLink = false;
        for (ChainLink link: chain) {
            if(link.getKey().equals(key)) {
                outputLink = link;
                foundLink = true;
            }
        }
        if (!foundLink) {
            outputLink.setKey("No Link");
        }
        return (outputLink);
    }
}
