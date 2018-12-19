package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it.

public class OrderCache {

    //En liste af produkterne
    private ArrayList<Order> orders;

    //Tiden som vores cache burde kunne køre
    private long timelimit;

    //Herefter sætter vi tiden hvor vores cache blev oprettet
    private long create;

    public OrderCache() { this.timelimit = Config.getOrderTimelimit(); }
    public ArrayList<Order> getProducts(Boolean forceUpdate)
    {
        //Hvis vi her clearer vores cache, kan vi altså forcere opdateringen
        //Evt. kan vi se på "alderen" af vores cache og dermed finde ud af om vi skal opdatere
        //Hvis det er således at listen er tom kan vi også tjekke efter nye produkter

        if (forceUpdate
              || ((this.create + this.timelimit) >= (System.currentTimeMillis() / 1000L))
              || this.orders.isEmpty()) {

            //Da vi gerne vil opdatere så getter vi vores produkter fra vores controller
            ArrayList<Order> products = OrderController.getOrders();

            //Nu setter vi produkterne for instancen og setter samtidig også vores oprettede timestamp
            this.orders = orders;
            this.create = System.currentTimeMillis() / 1000L;
        }

        //Nu returnerer vi vores dokumenter
        return this.orders;  


    }

}
