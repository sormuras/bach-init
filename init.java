import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.spi.ToolProvider;

record init(String module, String version) {

  private static final Path GIT_IGNORE_FILE = Path.of(".bach", ".gitignore");
  private static final Path VERSION_FILE = Path.of(".bach", "bach-init.version");

  public static void main(String... args) throws Exception {
    var slug = args.length == 1 ? args[0] : readVersionFromFileOrElseReturnMain();
    var init = new init("com.github.sormuras.bach", slug);
    System.exit(init.run());
  }

  static String readVersionFromFileOrElseReturnMain() throws Exception {
    return Files.exists(VERSION_FILE) ? Files.readString(VERSION_FILE) : "main";
  }

  int run() {
    System.out.printf("Initializing %s @ %s...%n", module, version);

    try {
      var sor = "https://github.com/sormuras/";
      var tmp = Files.createTempDirectory("bach-init-");
      var bsh = load(sor + "bach-init/raw/main/bach", tmp.resolve("bach"));
      var bat = load(sor + "bach-init/raw/main/bach.bat", tmp.resolve("bach.bat"));
      var jsh = load(sor + "bach-init/raw/main/bach.jshell", tmp.resolve("bach.jshell"));
      var zip = load(sor + "bach/archive/" + version + ".zip", tmp.resolve(version + ".zip"));

      var mod = compile(extract(zip));
      var bin = refresh(Path.of(".bach", "bin"));

      Files.copy(bsh, bin.resolve("bach")).toFile().setExecutable(true);
      Files.copy(bat, bin.resolve("bach.bat"));
      Files.copy(jsh, bin.resolve("bach.jshell"));
      Files.copy(mod, bin.resolve(module + ".jar"));

      if (Files.notExists(GIT_IGNORE_FILE)) Files.writeString(GIT_IGNORE_FILE, generateGitIgnore());
      Files.writeString(VERSION_FILE, version);
    } catch (Exception exception) {
      exception.printStackTrace(System.err);
      return 1;
    }
    System.out.print(generateNextStepsMessage());
    return 0;
  }

  String generateGitIgnore() {
    return """
           out/
           *.jar
           """;
  }

  String generateNextStepsMessage() {
    return """

           Bach successfully initialized in %s

           Next steps?
             - Print project information   -> %2$s info
             - Re-initialize (update) Bach -> %2$s init main (HEAD,${BRANCH},${TAG})
             - Print usage help message    -> %2$s --help

           Have fun!
           """
        .formatted(Path.of("").toAbsolutePath().toUri(), Path.of(".bach/bin/bach"));
  }

  Path load(String uri, Path file) throws Exception {
    System.out.printf("<< %s%n", uri);
    try (var stream = new URL(uri).openStream()) {
      var size = Files.copy(stream, file);
      System.out.printf(">> %,7d %s%n", size, file.getFileName());
    }
    return file;
  }

  Path extract(Path zip) throws Exception {
    var temp = zip.getParent();
    var process =
        new ProcessBuilder("jar", "--extract", "--file", zip.getFileName().toString())
            .directory(temp.toFile())
            .inheritIO()
            .start();
    if (process.waitFor() != 0) throw new Error("Extraction failed!");
    try (var stream = Files.find(temp, 2, (p, a) -> p.endsWith(module) && a.isDirectory())) {
      return stream.findFirst().orElseThrow().getParent();
    }
  }

  Path compile(Path root) throws Exception {
    var temp = root.getParent();
    var classes = temp.resolve("classes");
    run(
        "javac",
        "--module",
        module,
        "--module-source-path",
        root + File.separator + String.join(File.separator, "*", "src", "main", "java"),
        "-g",
        "-parameters",
        "-Werror",
        "-Xlint",
        "-encoding",
        "UTF-8",
        "-d",
        classes.toString());

    var file = temp.resolve(module + ".jar");
    var read = Files.readString(root.resolve("VERSION"));
    run(
        "jar",
        "--create",
        "--file=" + file,
        "--module-version=" + (version.equals(read) ? read : read + "+" + version),
        "--main-class",
        module + ".Main",
        "-C",
        classes.resolve(module).toString(),
        ".",
        "-C",
        root.resolve(module).resolve("src/main/java").toString(),
        ".");
    System.out.println("<< " + Files.size(file));
    return file;
  }

  void run(String name, String... args) {
    System.out.println(">> " + name + " " + String.join(" ", args));
    var tool = ToolProvider.findFirst(name).orElseThrow();
    var code = tool.run(System.out, System.err, args);
    if (code != 0) throw new Error("Non-zero exit code: " + code);
  }

  Path refresh(Path directory) throws Exception {
    if (Files.notExists(directory)) return Files.createDirectories(directory);
    try (var stream = Files.newDirectoryStream(directory, Files::isRegularFile)) {
      stream.forEach(this::remove);
    }
    return directory;
  }

  void remove(Path path) {
    try {
      Files.delete(path);
    } catch (Exception exception) {
      throw new Error(exception);
    }
  }
}
