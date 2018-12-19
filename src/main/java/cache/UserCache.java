package cache;

import controllers.UserController;
import model.User;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it. - FIXED
public class UserCache {

    //Produktliste
    private ArrayList<User> users;

    //Tiden som vores cache bør kunne overleve i
    private long ttl;

    //Bliver sat når vores cache oprettes
    private long created;

    public UserCache() {
        this.ttl = Config.getUserTtl(); }

    public ArrayList<User> getUsers(Boolean forceUpdate)
    {

        //Hvis vi her clearer vores cache, kan vi altså forcere opdateringen
        //Evt. kan vi se på "alderen" af vores cache og dermed finde ud af om vi skal opdatere
        //Hvis det er således at listen er tom kan vi også tjekke efter nye produkter

        if (forceUpdate
                || ((this.created + this.ttl) >= (System.currentTimeMillis() / 1000L ))
                || this.users.isEmpty()) {
            //Vi getter vores produkter fra vores controller, da vi ønsker at opdatere
            ArrayList<User> users = UserController.getUsers();

            //Setter her produkterne for instancerne og for created timestamp
            this.users = users;
            this.created = System.currentTimeMillis() / 1000L;

        }

        //Returnerer dokumenter
        return this.users;

        }
    }



