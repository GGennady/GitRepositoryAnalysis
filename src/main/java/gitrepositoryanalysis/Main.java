package gitrepositoryanalysis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gitrepositoryanalysis.model.RepoAnalysisModel;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java -jar repo-analyzer.jar <path-to-repo>");
            return;
        }

        Path repoPath = Path.of(args[0]);
        RepositoryAnalyzer analyzer = new RepositoryAnalyzer(repoPath);

        try {
            RepoAnalysisModel analysis = analyzer.analyze();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            System.out.println(gson.toJson(analysis));
        } catch (Exception e) {
            System.err.println("Error analyzing repository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}