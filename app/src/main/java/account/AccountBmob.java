package account;

import cn.bmob.v3.BmobObject;

/**
 * Created by yapeng.tian on 2016/5/21.
 */
public class AccountBmob extends BmobObject {
    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getPersonnel() {
        return personnel;
    }

    public void setPersonnel(String personnel) {
        this.personnel = personnel;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getCoster() {
        return coster;
    }

    public void setCoster(String coster) {
        this.coster = coster;
    }

    private int type;
    private float cost;
    private String restaurant;
    private String personnel;
    private String coster;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
