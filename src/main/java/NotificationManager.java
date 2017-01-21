import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * Created by school on 17-Dec-16.
 */
public abstract class NotificationManager {
    private static Image image;
    private static TrayIcon trayIcon;
    private static boolean muted = false;
    private static boolean trayIconActive = false;
    private static String lastMessage = getNoNewMessagesString();

    static{
        try {
            image = ImageIO.read(new File("icon_grey.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showInfoNotification(String message){
        if (trayIcon != null){
            trayIcon.displayMessage("Info", message, TrayIcon.MessageType.INFO);
        }
    }

    private static void showNotification(String message, int nrOfUpdatedSearches) throws AWTException {
        lastMessage = message;
        SystemTray tray = SystemTray.getSystemTray();

        if (trayIcon == null){
            PopupMenu popupMenu = new PopupMenu();

            final MenuItem muteItem = new MenuItem("mute");
            muteItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (muteItem.getLabel().equals("mute")){
                        muted = true;
                        muteItem.setLabel("unmute");
                    }else {
                        muted = false;
                        muteItem.setLabel("mute");
                        try {
                            int nrOfUpdatedSearches = EbaySearch.getNrOfUpdatedSearches();
                            showNotification(EbaySearch.getUpdateOfAllSearches(), nrOfUpdatedSearches);
                        } catch (AWTException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });

            final MenuItem quitItem = new MenuItem("QUIT");
            quitItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(8);
                }
            });
            popupMenu.add(quitItem);
            popupMenu.add(muteItem);


            trayIcon = new TrayIcon(image, "Ebay update", popupMenu);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Ebay update");
            tray.add(trayIcon);

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1){
                        openURLS();
                    }
                }
            });
        }

        setTrayIconImage(true);

        if (!muted){
            trayIcon.displayMessage(String.format("%s searches updated", nrOfUpdatedSearches), message, TrayIcon.MessageType.INFO);
        }
    }

    private static void openURLS() {
        lastMessage = getNoNewMessagesString();
        Set<String> urlsToOpen = new LinkedHashSet<>();

        if(Desktop.isDesktopSupported()) {
            List<EbaySearch> searchesToClear = new ArrayList<>();
            for (EbaySearch search : EbaySearch.getAllSearches()) {
                if (search.hasNewListings()){
                    urlsToOpen.add(search.getLink());
                    searchesToClear.add(search);
                }
            }
            Desktop desktop = Desktop.getDesktop();

            for (String url : urlsToOpen) {
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            for (EbaySearch search : searchesToClear) {
                try {
                    search.updateListings();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                search.clearNewListingsNr();
            }
        }
        setTrayIconImage(false);
    }

    private static void setTrayIconImage(boolean active){
        String imageLink = active ? "icon.png" : "icon_grey.png";

        try{
            trayIcon.setImage(ImageIO.read(new File(imageLink)));
        }catch (IOException e) {
            e.printStackTrace();
        }

        trayIconActive = active;
    }

    public static void update() {
        if (lastMessage.equals(getNoNewMessagesString())){
            try {
                int nrOfUpdatedSearches = EbaySearch.getNrOfUpdatedSearches();
                if (nrOfUpdatedSearches > 0){
                    showNotification(EbaySearch.getUpdateOfAllSearches(), nrOfUpdatedSearches);
                }
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }else {
            lastMessage = EbaySearch.getUpdateOfAllSearches();
        }
    }

    public static String getNoNewMessagesString(){
        return "no new messages";
    }
}
