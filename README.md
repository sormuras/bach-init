# bach-init

Bach's Initialization Program

## Usage

Download Bach's `init.java` program into directory `.bach`. For example via `wget`:

- `wget --directory-prefix .bach https://raw.githubusercontent.com/sormuras/bach-init/main/init.java`

Initialize Bach with the following command:

- `java .bach/init.java [<VERSION>]`

Examples for the optional `VERSION` tokens include:

- `main` (_default_) resolves to <https://github.com/sormuras/bach/tree/main>
- `HEAD` resolves to <https://github.com/sormuras/bach/tree/HEAD>

Run Bach:

- `java .bach/bin/bach.java --version`
