import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by school on 17-Dec-16.
 */
public abstract class EbayHelper {
    public static List<String> getListingIdsForURL(String urlString) throws IOException {
        List<String> listToReturn = new ArrayList<String>();
        URL url = new URL(urlString);
        InputStream is = null;
        BufferedReader br;
        String line;
        StringBuilder stringBuilder = new StringBuilder();

        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");

        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = br.readLine()) != null) {
            stringBuilder.append(line);
        }

        Pattern pattern = Pattern.compile("listingId=\"\\d+?\"");
        Matcher matcher = pattern.matcher(stringBuilder.toString());

        while (matcher.find()) {
            Pattern listingNrPattern = Pattern.compile("\\d+");
            Matcher listingNrMatcher = listingNrPattern.matcher(matcher.group());

            if (listingNrMatcher.find()){
                listToReturn.add(String.valueOf(listingNrMatcher.group()));
            }
        }

        return listToReturn;
    }
}
