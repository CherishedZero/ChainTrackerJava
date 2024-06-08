public class ChainLink {
    private String key;
    private String value;
    private String location;

    public ChainLink(String key, String value, String location) {
        this.key = key;
        this.value = value;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
