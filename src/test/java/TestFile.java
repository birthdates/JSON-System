import com.birthdates.jsonsystem.JSONFile;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TestFile extends JSONFile {

    public static class Data {
        @SerializedName("Test")
        private String test;
    }

    public TestFile(Plugin plugin) {
        super(Bukkit.getPluginManager().getPlugins()[0], "test.json");
        Validate.isTrue(getData(Data.class) != null, "Test failed: data null");
    }
}
