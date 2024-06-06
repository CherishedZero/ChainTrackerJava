import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Chain {
    @XmlElement(name="Chain")
    private List<ChainLink> chain = new ArrayList<>();

    public List<ChainLink> getChain() {
        return chain;
    }
}
