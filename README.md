# bach-init

Bach's Initialization Program

## Usage

`jshell https://git.io/bach-init`

Initializes Bach in the current working directory by delegating to a default version.
Find the current default version in file [default.jshell](default.jshell), at the end of the line starting with `/open ...`.

> <https://git.io/bach-init> expands to <https://github.com/sormuras/bach-init/raw/main/default.jshell>.
> 
> Created via `curl -i https://git.io -F "url=https://github.com/sormuras/bach-init/raw/main/default.jshell" -F "code=bach-init"`
> 
> See <https://github.blog/2011-11-10-git-io-github-url-shortener> for details.

In order to initialize a specific version of Bach in the current working directory, launch `jshell` with an explicit URL ending with valid `$VERSION.jshell` token like this:

`jshell https://github.com/sormuras/bach-init/raw/main/$VERSION.jshell`
