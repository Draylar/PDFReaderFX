import java.io.File;
import java.util.prefs.Preferences;

public class PreferenceManager {

    private Preferences pref;
    private boolean isResettingPreferences = false;

    PreferenceManager() {
        pref = Preferences.userRoot().node(this.getClass().getName());
    }


    /**
     * Sets the current PDF directory in preferences.
     * @param file the PDF to save
     */
    public void setPDFDirectory(File file) {
        pref.put("PDFDirectory", file.toString());
    }


    /**
     * Retrieves the current PDF directory from preferences.
     * @return returns the PDF saved in preferences. Defaults to "noDirectory" if it doesn't exist.
     */
    public File getPDFDirectory() {
        return new File(pref.get("PDFDirectory", "noDirectory"));
    }


    /**
     * Resets the user preferences. Useful for debugging purposes.
     */
    public void resetPreferences() {
        pref.remove("PDFDirectory");
        pref.remove("CurrentPage");
        setResettingPreferences(true);
    }


    /**
     * Sets the current page in the preferences so it can be accessed later.
     * @param currentPage the page the PDF was changed to
     */
    public void setCurrentPage(int currentPage) {
        pref.putInt("CurrentPage", currentPage);
    }


    /**
     * Retrieves the current page stored in preferences.
     * @return returns the latest page to be stored
     */
    public int getCurrentPage() {
        return pref.getInt("CurrentPage", 0);
    }


    /**
     * Checks to see if the user chose to reset their preferences. Mostly for debugging purposes.
     * @return true if the user chose to reset their preferences
     */
    public boolean isResettingPreferences() {
        return isResettingPreferences;
    }


    /**
     * Sets whether or not the user wants to reset their preferences. Mostly for debugging purposes.
     * @param resettingPreferences true if the user wants to reset
     */
    public void setResettingPreferences(boolean resettingPreferences) {
        isResettingPreferences = resettingPreferences;
    }
}
