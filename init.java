import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

record init(String version) {

  private static final Path GIT_IGNORE_FILE = Path.of(".bach", ".gitignore");
  private static final Path INIT_VERSION_FILE = Path.of(".bach", "init.version");

  public static void main(String... args) throws Exception {
    var version = args.length == 1 ? args[0] : readVersionFromFileOrElseReturnMain();
    var init = new init(version);
    System.exit(init.run());
  }

  static String readVersionFromFileOrElseReturnMain() throws Exception {
    return Files.exists(INIT_VERSION_FILE) ? Files.readString(INIT_VERSION_FILE) : "main";
  }

  int run() {
    System.out.printf("Initializing Bach %s...%n", version);

    try {
      var sor = "https://github.com/sormuras/";
      var tmp = Files.createTempDirectory("bach-init-");
      var zip = load(sor + "bach/archive/" + version + ".zip", tmp.resolve("bach-archive-" + version + ".zip"));

      extract(zip);

      //noinspection ResultOfMethodCallIgnored
      Path.of(".bach/bin/bach").toFile().setExecutable(true, true);

      Files.createDirectories(GIT_IGNORE_FILE.getParent());
      if (Files.notExists(GIT_IGNORE_FILE)) Files.writeString(GIT_IGNORE_FILE, generateGitIgnore());
      Files.writeString(INIT_VERSION_FILE, version);
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

  void extract(Path zip) throws Exception {
    try (var fs = FileSystems.newFileSystem(zip)) {
      for (var root : fs.getRootDirectories()) {
        try (var stream = Files.walk(root)) {
          var list = stream
                  .filter(Files::isRegularFile).toList();
          for (var file : list) {
            var string = file.toString();
            if (string.contains(".bach/bin") || string.contains(".bach/src/run.bach")) {
              var target = Path.of(file.subpath(1, file.getNameCount()).toString());
              Files.createDirectories(target.getParent());
              Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
            }
          }
        }
      }
    }
  }
}
