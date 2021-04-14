import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.module.ModuleDescriptor.Version;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

record init(String name, Version version) {

  public static void main(String... args) {
    var version = Version.parse(args.length == 0 ? "17-ea-3" : args[0]);
    System.exit(new init("com.github.sormuras.bach", version).run());
  }

  public int run() {
    var jar = name + "@" + version + ".jar";
    System.out.printf("init (%s@%s)%n", name, version);
    try {
      var sor = "https://github.com/sormuras/";
      var tmp = Files.createTempDirectory("bach-init-");
      var bat = load(sor + "bach-init/raw/main/bach.bat", tmp.resolve("bach.bat"));
      var mod = load(sor + "bach/releases/download/" + version + "/" + jar, tmp.resolve(jar));
      var bin = createEmptyDirectory(Path.of(".bach", "bin"));
      Files.copy(bat, bin.resolve("bach.bat"));
      Files.copy(mod, bin.resolve(jar));
    } catch (Exception exception) {
      exception.printStackTrace(System.err);
      return 1;
    }
    return 0;
  }

  public Path load(String uri, Path file) throws Exception {
    System.out.printf("<< %s%n", uri);
    try (var stream = new URL(uri).openStream()) {
      var size = Files.copy(stream, file);
      System.out.printf("%,7d %s%n", size, file.getFileName());
    }
    return file;
  }

  public static Path createEmptyDirectory(Path directory) throws Exception {
    if (Files.notExists(directory)) return Files.createDirectories(directory);
    Files.newDirectoryStream(directory, Files::isRegularFile).forEach(init::delete);
    return directory;
  }

  private static void delete(Path path) {
    try {
      Files.delete(path);
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }
}
