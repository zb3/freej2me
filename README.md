# freej2me

Experimental fork of [https://github.com/hex007/freej2me](https://github.com/hex007/freej2me)

The idea is to bring some features from other open source emulators like J2ME-Loader.

Currently it is unstable, but it has:
* m3g support (the Nokia implementation with J2ME-Loader modifications)
* Mascot Capsule V3 support (from JL-Mod)
* various fixes where files were replaced with J2ME-Loader implementations
* more audio formats support via the ffmpeg library

It still lacks many features, especially the UI.

## DISCLAIMER/TODO
While the J2ME API is fairly limited, any JAR loaded into this emulator can do **everything** on your system, and those distributing various J2ME JARs today are well aware of the fact that emulators exist.
That's why it's highly recommended to run this in a sandboxed environment, the emulator itself provides no sandbox at this time, sorry.
(TODO: linux docker container setup)


## Let's play
You can download prebuilt JARs with dependencies from the [Releases page](https://github.com/zb3/freej2me/releases)

Have fun :)

## Building

Since this fork has incorporatied m3g and Mascot Capsule V3 support, native dependencies had to be introduced and this unfortunately made freej2me less portable. Additional audio format support was also enabled by integrating the ffmpeg library, but that wasn't free either - freej2me now needs a build system to preserve its cross-platform nature.

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

