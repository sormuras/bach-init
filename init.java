import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.spi.ToolProvider;

record init(String module, String slug) {

  public static void main(String... args) {
    var slug = args.length == 0 ? "HEAD" : args[0];
    System.exit(new init("com.github.sormuras.bach", slug).run());
  }

  public int run() {
    var jar = module + ".jar";
    System.out.printf("init %s @ %s%n", module, slug);

    try {
      var sor = "https://github.com/sormuras/";
      var tmp = Files.createTempDirectory("bach-init-");
      var bsh = load(sor + "bach-init/raw/main/bach", tmp.resolve("bach"));
      var bat = load(sor + "bach-init/raw/main/bach.bat", tmp.resolve("bach.bat"));
      var jsh = load(sor + "bach-init/raw/main/bach.jshell", tmp.resolve("bach.jshell"));
      var zip = load(sor + "bach/archive/" + slug + ".zip", tmp.resolve(slug + ".zip"));

      var mod = compile(extract(zip));
      var bin = refresh(Path.of(".bach", "bin"));

      Files.copy(bsh, bin.resolve("bach")).toFile().setExecutable(true);
      Files.copy(bat, bin.resolve("bach.bat"));
      Files.copy(jsh, bin.resolve("bach.jshell"));
      Files.copy(mod, bin.resolve(jar));
    } catch (Exception exception) {
      exception.printStackTrace(System.err);
      return 1;
    }
    return 0;
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
    var version = Files.readString(root.resolve("VERSION")) + "+" + slug;
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

    var jar = temp.resolve(module + ".jar");
    run(
        "jar",
        "--create",
        "--file=" + jar,
        "--module-version=" + version + "+" + Instant.now().truncatedTo(ChronoUnit.SECONDS),
        "--main-class",
        module + ".Main",
        "-C",
        classes.resolve(module).toString(),
        ".",
        "-C",
        root.resolve(module).resolve("src/main/java").toString(),
        ".");
    System.out.println("<< " + Files.size(jar));
    return jar;
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
