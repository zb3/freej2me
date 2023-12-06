variant="$VARIANT"
project="$PROJECT"

export DIST=$(realpath dist/$variant)
export PREFIX=$(realpath temp/$variant/prefix)
export SRCDIR=$(realpath temp/src)
export BUILDDIR=$(realpath temp/$variant/build/$project)

mkdir -p $BUILDDIR
cd projects/$PROJECT

# deliberately do NOT use mingw32-pkg-config, env variable is enough
export PKG_CONFIG=pkg-config


if [ "$variant" = "linux-amd64" ]; then
    export HOST=x86_64-linux-gnu
    export PATH="$PREFIX/bin":$PATH
    export PKG_CONFIG_PATH=/usr/lib/x86_64-linux-gnu/pkgconfig/:$PREFIX/lib/pkgconfig
    export CPPFLAGS="-I$PREFIX/include"
    export LDFLAGS="-L$PREFIX/lib"

    host_arch=x86_64
    host_os=linux

elif [ "$variant" = "linux-arm64" ]; then
    export HOST=aarch64-linux-gnu
    export CROSS_COMPILE=/usr/bin/$HOST-

    export PATH=/usr/$HOST/bin:"$PREFIX/bin":$PATH
    export PKG_CONFIG_PATH=$PREFIX/lib/pkgconfig
    export CPPFLAGS="-I/usr/$HOST/include -I$PREFIX/include"
    export LDFLAGS="-L/usr/$HOST/lib -L$PREFIX/lib"

    host_arch=aarch64
    host_os=linux

elif [ "$variant" = "windows-x86" ]; then
    export HOST=i686-w64-mingw32
    export CROSS_COMPILE=/usr/bin/$HOST-

    export PATH=/usr/$HOST/bin:"$PREFIX/bin":$PATH
    export PKG_CONFIG_PATH=/usr/$HOST/lib/pkgconfig:$PREFIX/lib/pkgconfig
    export CPPFLAGS="-I/usr/$HOST/include -I$PREFIX/include"
    export LDFLAGS="-L/usr/$HOST/lib -L$PREFIX/lib"
    export SYSROOT=/usr/$HOST/

    host_arch=i386
    host_os=mingw64

elif [ "$variant" = "windows-amd64" ]; then
    export HOST=x86_64-w64-mingw32
    export CROSS_COMPILE=/usr/bin/$HOST-

    export PATH=/usr/$HOST/bin:"$PREFIX/bin":$PATH
    export PKG_CONFIG_PATH=/usr/$HOST/lib/pkgconfig:$PREFIX/lib/pkgconfig
    export CPPFLAGS="-I/usr/$HOST/include -I$PREFIX/include"
    export LDFLAGS="-L/usr/$HOST/lib -L$PREFIX/lib"
    export SYSROOT=/usr/$HOST/


    host_arch=x86_64
    host_os=mingw64

elif [[ $variant == "macos-"* ]]; then
    DARWIN_VERSION=darwin20.4
    OSXCROSS_PATH=/osxcross/target

    export PATH="${OSXCROSS_PATH}/bin":$PATH
    export PKG_CONFIG_PATH=$PREFIX/lib/pkgconfig

    host_os=darwin

    if [ "$variant" = "macos-amd64" ]; then
        export HOST=x86_64-apple-${DARWIN_VERSION}
        export CROSS_COMPILE=/osxcross/target/bin/$HOST-

        # let's not set CPPFLAGS.. will ffmpeg build?
        host_arch=x86_64
        

    elif [ "$variant" = "macos-arm64" ]; then
        export HOST=aarch64-apple-${DARWIN_VERSION}
        export CROSS_COMPILE=/osxcross/target/bin/$HOST-

        host_arch=aarch64
    fi

fi

if [[ $variant == "macos-"* ]]; then
    export CC="$CROSS_COMPILE"clang
    export CXX="$CROSS_COMPILE"clang++
    export LD="$CXX"
    export STRIP="${CROSS_COMPILE}strip -x"
else
    export CC="$CROSS_COMPILE"gcc
    export CXX="$CROSS_COMPILE"g++
    export LD="$CXX"
    export STRIP="$CROSS_COMPILE"strip
fi

[[ $variant == "linux-"* ]] && soext=so
[[ $variant == "windows-"* ]] && soext=dll
[[ $variant == "macos-"* ]] && soext=dylib
