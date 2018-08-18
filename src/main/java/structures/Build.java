package structures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Build {

    @Expose
    @SerializedName("id")
    private int buildNumber;

    public Build(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }
}
