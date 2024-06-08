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

    public List<String> getValuesByKey(String key) {
        ArrayList<String> values = new ArrayList<>();
        for (ChainLink link: chain) {
            if(link.getKey().equals(key)) {
                values.add(link.getValue());
            }
        }
        return values;
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
}
