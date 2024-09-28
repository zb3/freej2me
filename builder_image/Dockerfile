FROM ubuntu:jammy

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update

RUN apt-get -y install openjdk-8-jdk-headless
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/

RUN apt-get -y install build-essential git python3 pkg-config wget mingw-w64 bzip2 libbz2-dev libz-mingw-w64-dev ant

# use posix version so c++ can use thread-related apis
# but this requires distributing winpthreads dll

RUN update-alternatives --set i686-w64-mingw32-g++ /usr/bin/i686-w64-mingw32-g++-posix && \
    update-alternatives --set i686-w64-mingw32-gcc /usr/bin/i686-w64-mingw32-gcc-posix && \
    update-alternatives --set x86_64-w64-mingw32-g++ /usr/bin/x86_64-w64-mingw32-g++-posix && \
    update-alternatives --set x86_64-w64-mingw32-gcc /usr/bin/x86_64-w64-mingw32-gcc-posix

RUN groupadd --gid 1000 zb3 \
    && useradd --uid 1000 --gid zb3 --shell /bin/bash --create-home zb3
RUN apt-get install -y sudo
RUN echo 'zb3:rocks' | chpasswd
RUN gpasswd --add zb3 sudo
RUN echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

# RUN dpkg --add-architecture i386 && apt-get update && apt-get -y install wine32 wine64
# RUN su zb3 -c "wine wineboot"

RUN apt-get install -y binutils-aarch64-linux-gnu gcc-aarch64-linux-gnu g++-aarch64-linux-gnu

# OSXCross for Mac Builds

RUN apt-get install -y clang cmake patch libssl-dev lzma-dev libxml2-dev xz-utils cpio zlib1g-dev

RUN git clone --depth=1 https://github.com/tpoechtrager/osxcross
RUN apt-get install -y clang && cd osxcross && \
    wget -nc https://github.com/phracker/MacOSX-SDKs/releases/download/11.3/MacOSX11.3.sdk.tar.xz && \
    mv MacOSX11.3.sdk.tar.xz tarballs/ && \
    UNATTENDED=yes ./build.sh

RUN wget https://github.com/indygreg/apple-platform-rs/releases/download/apple-codesign%2F0.26.0/apple-codesign-0.26.0-x86_64-unknown-linux-musl.tar.gz && tar xf apple-codesign* && rm apple-codesign*.gz && \
    cp apple-codesign*/rcodesign /usr/bin/ && rm -rf apple-codesign*

# need this to install aarch64 libraries

RUN sed -i -E 's/deb(-src)? (http|file|mirror)/deb\1 [arch=amd64,i386] \2/' /etc/apt/sources.list && \
    sed -E -e 's/deb(-src)? [^]]+]/deb\1 [arch=arm64]/' \
        -e 's| http://archive.ubuntu.com/ubuntu/ | http://ports.ubuntu.com/ubuntu-ports/ |g' \
        -e 's| http://security.ubuntu.com/ubuntu/ | http://ports.ubuntu.com/ubuntu-ports/ |g'  /etc/apt/sources.list > /etc/apt/sources.list.d/arm64.list && \
        dpkg --add-architecture arm64 && \
        apt-get update

RUN apt-get install -y --no-install-recommends zlib1g-dev:arm64

# dpkg --add-architecture i386
# apt-get install -y gcc-i686-linux-gnu g++-i686-linux-gnu libc6-dev-i386
# apt-get install -y zlib1g-dev:i386
