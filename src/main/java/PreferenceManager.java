import java.io.File;
import java.util.prefs.Preferences;

public class PreferenceManager {


    private Preferences pref;
    public boolean isResettingPreferences = false;

    public PreferenceManager() {
        pref = Preferences.userRoot().node(this.getClass().getName());
    }

    public void setPDFDirectory(File file) {
        pref.put("PDFDirectory", file.toString());
    }

    public File getPDFDirectory() {
        File file = new File(pref.get("PDFDirectory", "noDirectory"));
        return file;
    }

    public void resetPreferences() {
        pref.remove("PDFDirectory");
        pref.remove("CurrentPage");
        setResettingPreferences(true);
    }

    public void setCurrentPage(int currentPage) {
        pref.putInt("CurrentPage", currentPage);
    }

    public int getCurrentPage() {
        return pref.getInt("CurrentPage", 0);
    }

    public boolean isResettingPreferences() {
        return isResettingPreferences;
    }

    public void setResettingPreferences(boolean resettingPreferences) {
        isResettingPreferences = resettingPreferences;
    }
}
