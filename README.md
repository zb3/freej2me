# freej2me

An experimental fork of [https://github.com/hex007/freej2me](https://github.com/hex007/freej2me), a free cross-platform desktop J2ME emulator.

The idea is to bring some features from Android-only open source emulators like J2ME-Loader and make them available on the desktop.

Currently it has:
* m3g support (the Nokia implementation with J2ME-Loader modifications)
* Mascot Capsule V3 support (from JL-Mod)
* various fixes where files were replaced with J2ME-Loader implementations
* more audio formats support via the ffmpeg library
* very basic UI controls support (just enough to make game settings work)

## DISCLAIMER/TODO
While the J2ME API is fairly limited, any JAR loaded into this emulator can do **everything** on your system, and those distributing various J2ME JARs today are well aware of the fact that emulators exist.
That's why it's highly recommended to run this in a sandboxed environment, the emulator itself provides no sandbox at this time, sorry.
(TODO: linux docker container setup)


## Let's play!

At least Java 8 is required, but it should also work with newer JREs. Since freej2me uses native libraries, you need to download the proper JAR for your system and architecture:

| Linux | Windows | macOS |
|---|---|---|
| <h3><a href="https://github.com/zb3/freej2me/releases/latest/download/freej2me-linux-amd64.jar">Linux x86_64</a></h3>  <h3><a href="https://github.com/zb3/freej2me/releases/latest/download/freej2me-linux-arm64.jar">Linux aarch64</a></h3> <br> | <h3><a href="https://github.com/zb3/freej2me/releases/latest/download/freej2me-windows-amd64.jar">Windows 64-bit</a></h3>  <h3><a href="https://github.com/zb3/freej2me/releases/latest/download/freej2me-windows-x86.jar">Windows 32-bit</a></h3> <br> | <h3><a href="https://github.com/zb3/freej2me/releases/latest/download/freej2me-macos-amd64.jar">macOS Intel</a></h3>  <h3><a href="https://github.com/zb3/freej2me/releases/latest/download/freej2me-macos-arm64.jar">macOS Apple Silicon</a></h3> <br> |
| (requires glibc from RHEL8 or later) | (requires Windows 7 or later) | (requires Catalina or later) |

for more information including system requirements, see the [Releases page](https://github.com/zb3/freej2me/releases).


### Usage
You can either run the JAR directly (if your system supports it) where a file picker will be shown, or you can run the JAR from the command line.

Both JAR and JAD files are supported, albeit some games might not work without a JAD file containing the necessary properties.

To run from the command line just pass the file name as an argument:
```
java -jar path_to_freej2me.jar path_to_game_jar_or_jad
```

In most cases, that should be it. Have fun :)


### Basic keybindings


| **Key** | **Functions As** |
| :------------: | :--------------: |
| <kbd>Esc</kbd> | Enter/exit freej2me options |
| <kbd>F1</kbd> or <kbd>Q</kbd> | Left soft key |
| <kbd>F2</kbd> or <kbd>W</kbd> | Right soft key |
| <kbd>0</kbd> to <kbd>9</kbd> | Keypad Numbers |
| Numpad keys | Numbers with keys 123 and 789 swaped |
| <kbd>E</kbd> | * |
| <kbd>R</kbd> | # |
| <kbd>↑</kbd> | Up |
| <kbd>↓</kbd> | Down |
| <kbd>←</kbd> | Left |
| <kbd>→</kbd> | Right |
| <kbd>⏎ Enter</kbd> | Action key (OK button) |
| <kbd>+</kbd> | Scale up (nearest integer factor) |
| <kbd>-</kbd> | Scale down (nearest integer factor) |

#### Phone types and key mappings
Keys like left/right soft, arrows and the action key have different vendor-specific mappings. By default, freej2me uses the most common **Nokia** mapping, but this can be changed in settings by changing the `Phone type`. Note that in the `Standard` phone, arrow keys are mapped to 2, 4, 6, 8 and the enter key is mapped to 5.

When using the numpad keys, the 123 and 789 rows are swapped so as to resemble the key layout on a mobile phone.

## Game crashed?
If a game doesn't work, first try changing the settings. Press the <kbd>Esc</kbd> key, change some settings and then restart the game. Try changing these:
* display size
* compatibility flags
* sound (turn off)

If it still doesn't work you can get more information by looking at the console. Note however that **not every game will work with this emulator**. You can report a bug though.

### Using a custom resolution
Some games might only work with a specific resolution that is not listed on the settings screen. You can set it via the commandline using the `-w` and `-h` parameters:
```
java -jar freej2me.jar -w 100 -h 200 game.jar
```
However you might also need to force the game canvas to operate in fullscreen mode (no commands bar) using the `-ff` argument:
```
java -jar freej2me.jar -ff -w 100 -h 200 game.jar
```

### Specifying a custom system/app property
Both system properties and app properties (these are Java/J2ME concepts) can be specified on the command line. For example, sometimes a given game might expect a particular value of the `microedition.platform` system property. These can be defined using the `-sp` argument:


```
java -jar freej2me.jar -sp microedition.platform=J2ME game.jar
```

Similarily, an app property (normally present in the manifest or JAD files) can be specified using the `-ap` argument:

```
java -jar freej2me.jar -ap License-Key=zb3forever game.jar
```
(you can pass more than one `-sp` and `-ap` arguments)


### No main class? Try this
In rare cases where there's no manifest, no JAD file and FreeJ2ME was not able to find the main class automatically, you can specify it manually using the `-m` argument:

```
java -jar freej2me.jar -m a.a.a.a.a.z game.jar
```
(btw the `a.a.a.a.a.z` class was the only value used by me in practice so you might as well try it if you don't know the class)


## Building

Since this fork has incorporated m3g and Mascot Capsule V3 support, native dependencies had to be introduced and this unfortunately made freej2me less portable. Additional audio format support was also enabled by integrating the ffmpeg library, but that wasn't free either - freej2me now needs a build system to preserve its cross-platform nature.

GLES1 and GLES2 are supported via the ANGLE library, which isn't built from source but copied from a Chromium build. FFMpeg, m3g and MC V3 are built from source.

Currently natives can only be built in a **x86_64 Linux** docker container (tested on x86_64 linux host) and there we can build natives for both linux, windows and even macOS + for different architectures.

On the host, you need to have docker installed. Currently the image assumes a linux host (the `1000` uid).

These targets are supported: (which doesn't mean they're tested)
* linux-amd64
* linux-arm64
* windows-x86
* windows-amd64
* macos-amd64
* macos-arm64

For linux, it needs at least glibc 2.17 (which corresponds to RHEL 7), but in order for the graphics to run at a reasonable speed, the system needs to support GLES 3.0, that's because angle implements GLES1 support via GLES 3.0.

For windows, at least Windows 7 is needed, however again, without a proper D3D 11 driver, software rendering will be used.

For macOS, at least 10.15 is needed and this is a requirement imposed by the particular ANGLE library build - it's posible to ease this requirement if you happen to have ANGLE binaries which work on older macs.

So let's say you want to build for the `windows-amd64` target.
First we need to build the builder image - let me call it `bobthebuilder`:

```
git clone https://github.com/zb3/freej2me
cd freej2me
docker build builder_image -t bobthebuilder
```
(TODO: the above hardcodes the non-privileged uid to 1000, but it should be a build argument)

Then we need to switch user to `zb3` (uid `1000`), mount the source directory as `/app` and move there:
```
docker run --rm -it -uzb3 -w /app -v`pwd`:/app bobthebuilder
```

Now we can build the natives: (inside the docker container, of course)
```
bash natives/build.sh windows-amd64 build
```
(`clean` can be used instead of build to.. clean artifacts)

After natives were build successfully (unlikely), we can build the Java part which will also copy all built libraries from the `natives/dist` directory into the JAR:
```
ant -Dvariant=windows-amd64
```

If everything worked well (again, unlikely), you should now have the jar inside the `build/` directory.
So we can exit the container:
```
exit
```

and then finally we can play some games:
```
# not for linux
java -jar build/freej2me-windows-amd64.jar
```
... umm no, naturally the above won't work unless you are using windows (hopefully not), but you can repeat the process above substituting `windows` with `linux` and run:
```
# for linux
java -jar build/freej2me-linux-amd64.jar
```
... but be prepared to see a brand new error message and please kindly report it here :)

