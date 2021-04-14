# bach-init

Bach's Initialization Program

## Usage

`jshell https://git.io/bach-init`

Initializes Bach in the current working directory by installing a default version.

> <https://git.io/bach-init> expands to <https://github.com/sormuras/bach-init/raw/main/default.jshell>.
> 
> Created via `curl -i https://git.io -F "url=https://github.com/sormuras/bach-init/raw/main/default.jshell" -F "code=bach-init"`
> 
> See <https://github.blog/2011-11-10-git-io-github-url-shortener> for details.

In order to initialize a specific version of Bach in the current working directory, launch `jshell` with an explicit and valid `VERSION` token like this:

`jshell -R-Dbach-version=$VERSION https://git.io/bach-init`
