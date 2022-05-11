# bach-init

Bach's Initialization Program

## Usage

Initialize Bach in the current working directory by installing a default version.

`jshell https://git.io/bach-init`

Once initialized successfully subsequent runs are started by using the following command:

- `.bach/bin/bach init VERSION` (Linux, Mac)
- `.bach\bin\bach init VERSION` (Linux, Mac)

Examples for valid `VERSION` tokens include:

- `main` (_default_) resolves to <https://github.com/sormuras/bach/tree/main>
- `HEAD` resolves to <https://github.com/sormuras/bach/tree/HEAD>
- `17` (_soon_) resolves to <https://github.com/sormuras/bach/tree/17>

## Remarks

> <https://git.io/bach-init> expands to <https://github.com/sormuras/bach-init/raw/main/default.jshell>.
>
> Created via `curl -i https://git.io -F "url=https://github.com/sormuras/bach-init/raw/main/default.jshell" -F "code=bach-init"`
>
> See <https://github.blog/2011-11-10-git-io-github-url-shortener> for details.
