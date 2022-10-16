# bach-init

Bach's Initialization Program

## Usage Example

Create an empty example directory for your project and change into it.

- `mkdir example && cd example`

Download Bach's `init.java` program into directory `.bach`. For example via `wget`:

- (_Linux_) `echo 'Files.copy(URI.create("https://init.java.bach.run").toURL().openStream(), Files.createDirectories(Path.of(".bach")).resolve("init.java"))' | jshell -`
- (Windows) `echo Files.copy(URI.create("https://init.java.bach.run").toURL().openStream(), Files.createDirectories(Path.of(".bach")).resolve("init.java")) | jshell -`

Initialize Bach in the current directory with the following command:

- `java .bach/init.java [<VERSION>]`

Valid values for the optional `VERSION` token include:

- `main` (_default_) resolves to <https://github.com/sormuras/bach/tree/main>
- `HEAD` resolves to <https://github.com/sormuras/bach/tree/HEAD>

Run Bach:

- `java .bach/bin/bach.java --version`
