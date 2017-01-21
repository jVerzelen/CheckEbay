import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by school on 17-Dec-16.
 */
public class CheckEbayMain {
    private static final int CHECK_SELLERS_EVERY_X_MINUTES = 1;


    public static void main(String[] args) {
        initializeSearches();

        while (true){
            try {
                for (EbaySearch search : EbaySearch.getAllSearches()) {
                    try {
                        search.updateListings();
                    } catch (IOException e) {
                        NotificationManager.showInfoNotification("Error getting new listings for " + search.getName());
                    }
                }
                NotificationManager.update();
                System.out.println("checked " + new SimpleDateFormat("HH:mm").format(new Date()));

                Thread.sleep(CHECK_SELLERS_EVERY_X_MINUTES * 60 * 1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    private static void initializeSearches(){
        new EbaySearch("altijd nieuw om te testen", "http://www.benl.ebay.be/sch/i.html?_from=R40&_sacat=0&_nkw=glas&_sop=10");

        for (EbaySearch search : EbaySearch.getAllSearches()) {
            try {
                search.updateListings();
            } catch (IOException e) {
                NotificationManager.showInfoNotification("Error getting new listings for " + search.getName());
            }
            search.clearNewListingsNr();
        }
    }
}
