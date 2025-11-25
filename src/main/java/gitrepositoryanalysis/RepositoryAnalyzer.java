package gitrepositoryanalysis;

import gitrepositoryanalysis.model.RepoAnalysisModel;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class RepositoryAnalyzer {
    private final Path root;

    public RepositoryAnalyzer(Path root) {
        this.root = root;
    }

    public RepoAnalysisModel analyze() throws IOException {
        RepoAnalysisModel result = new RepoAnalysisModel();

        result.language = detectLanguage();
        result.buildTool = detectBuildTool();
        result.hasDockerfile = detectDockerfile();
        result.hasTests = detectTests();
        result.infra = detectInfra();
        result.detectedPaths = detectPaths();

        return result;
    }

    private String detectLanguage() throws IOException {
        Map<String, Integer> extCount = new HashMap<>();
        try (var stream = Files.walk(root)) {
            for (Path p : stream.toList()) {
                if (Files.isRegularFile(p)) {
                    String filename = p.getFileName().toString();
                    if (filename.endsWith(".java")) extCount.put("java", extCount.getOrDefault("java", 0) + 1);
                    else if (filename.endsWith(".kt")) extCount.put("kotlin", extCount.getOrDefault("kotlin", 0) + 1);
                    else if (filename.endsWith(".py")) extCount.put("python", extCount.getOrDefault("python", 0) + 1);
                    else if (filename.endsWith(".js")) extCount.put("javascript", extCount.getOrDefault("javascript", 0) + 1);
                }
            }
        }
        return extCount
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    private String detectBuildTool() {
        if (Files.exists(root.resolve("pom.xml"))) return "maven";
        if (Files.exists(root.resolve("build.gradle")) || Files.exists(root.resolve("build.gradle.kts"))) return "gradle";
        if (Files.exists(root.resolve("package.json"))) return "npm";
        return null;
    }

    private boolean detectDockerfile() {
        return Files.exists(root.resolve("Dockerfile"));
    }

    private boolean detectTests() throws IOException {
        final boolean[] hasTests = {false};
        try (var stream = Files.walk(root)) {
            stream.filter(Files::isRegularFile).forEach(p -> {
                String name = p.getFileName().toString().toLowerCase();
                if (name.contains("test")) hasTests[0] = true;
            });
        }
        return hasTests[0];
    }

    private Infra detectInfra() {
        boolean k8s = Files.exists(root.resolve("k8s"));
        boolean helm = Files.exists(root.resolve("Chart.yaml"));
        return new Infra(k8s, helm);
    }

    private List<String> detectPaths() throws IOException {
        Set<String> paths = new TreeSet<>();
        try (var stream = Files.walk(root)) {
            for (Path p : stream.toList()) {
                if (Files.isDirectory(p)) {
                    String rel = root.relativize(p).toString().replace("\\", "/");
                    if (rel.startsWith("src/main")) paths.add("src/main");
                    if (rel.startsWith("src/test")) paths.add("src/test");
                    if (rel.startsWith("k8s")) paths.add("k8s/");
                }
            }
        }
        return new ArrayList<>(paths);
    }
}