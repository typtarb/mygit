package login;

/**
 * Created by yapeng.tian on 2016/5/19.
 */
public class PersonItem {
    public String name;
    public String headUrl;
    public String password;
    public String updatedAtDate;
    public String recharge;
    public boolean isSelected = false;

    public PersonItem() {
    }

    public PersonItem(String name, String recharge) {
        this.name = name;
        this.recharge = recharge;
    }
}
