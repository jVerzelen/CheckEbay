import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by school on 17-Dec-16.
 */
public class EbaySearch {
    private static List<EbaySearch> allSearches = new ArrayList<>();

    private String name;
    private Set<String> listingIds;
    private String link;
    private int nrOfNewListings = 0;

    public EbaySearch(String seller) {
        this(seller, "http://www.ebay.com/sch/m.html?_nkw=&_armrs=1&_ipg=&_from=&_ssn="+seller+"&_sop=10");
    }

    public EbaySearch(String name, String link) {
        this.name = name;
        this.link = link;
        listingIds = new HashSet<>();

        allSearches.add(this);
    }

    public void addNewIds(List<String> ids){
        for (String listingId : ids) {
            if (listingIds.add(listingId)){
                this.nrOfNewListings++;
            }else {
                listingIds.addAll(ids);
                break;
            }
        }
    }

    public void updateListings() throws IOException {
        List<String> ids = EbayHelper.getListingIdsForURL(getLink());
        addNewIds(ids);
    }

    public String getStatus(){
        if (hasNewListings()){
            return String.format("%-2d new ids for %s\n", getNrOfNewListings(), getName());
        }else {
            return String.format("no new ids for %s\n", getName());
        }
    }

    public static String getUpdateOfAllSearches(){
        StringBuilder messageBuilder = new StringBuilder();
        for (EbaySearch search : allSearches) {
            if (search.hasNewListings()){
                messageBuilder.append(search.getStatus());
            }
        }
        return messageBuilder.toString();
    }

    public static int getNrOfUpdatedSearches(){
        int nrOfUpdatedSearches = 0;
        for (EbaySearch search : allSearches) {
            if (search.hasNewListings()){
                nrOfUpdatedSearches++;
            }
        }
        return nrOfUpdatedSearches;
    }

    public boolean hasNewListings(){
        return nrOfNewListings > 0;
    }

    public int getNrOfNewListings(){
        return nrOfNewListings;
    }

    public void clearNewListingsNr(){
        nrOfNewListings = 0;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public static List<EbaySearch> getAllSearches() {
        return allSearches;
    }
}
