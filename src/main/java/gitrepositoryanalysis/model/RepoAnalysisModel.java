package gitrepositoryanalysis.model;

import com.google.gson.annotations.SerializedName;
import gitrepositoryanalysis.Infra;

import java.util.List;

public class RepoAnalysisModel {
    public String language;

    @SerializedName("build_tool")
    public String buildTool;

    @SerializedName("has_dockerfile")
    public boolean hasDockerfile;

    @SerializedName("has_tests")
    public boolean hasTests;

    public Infra infra;

    @SerializedName("detected_paths")
    public List<String> detectedPaths;
}
