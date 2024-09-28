#!/bin/bash

cd "$(realpath "$(dirname "$0")")"
export COMMONDIR=$(realpath common)

variants=({linux,macos}-{amd,arm}64 windows-{x86,amd64} linux-x86 windows-xp-x86)
cmds=(build clean)

variant="$1"
[ -z "$variant" ] && echo need variant && exit 1
[[ " ${variants[@]} " != *" $variant "* ]] && echo "unrecognized variant, supported variants: ${variants[@]}" && exit 1


cmd=build
[ -n "$2" ] && cmd=$2
[[ " ${cmds[@]} " != *" $cmd "* ]] && echo "unrecognized command, supported commands: build, clean" && exit 1

one_project="$3"

# only clear dist if we're building / clearing everything
[ -z "$one_project" ] && rm -rf "dist/$variant"

mkdir -p temp/$variant/prefix/lib
mkdir -p temp/src
mkdir -p temp/$variant/build
mkdir -p "dist/$variant/deps"


projects=(ffmpeg ffaudio angle wrangle m3g micro3d)
[ -n "$one_project" ] && projects=($one_project)

set -e

if [ "$cmd" = "clean" ]; then
    for proj in "${projects[@]}"; do
        rm -rf temp/$variant/build/$proj
    done

else
    jni_platform=linux
    [[ $variant == "windows-"* ]] && jni_platform=win32
    [[ $variant == "macos-"* ]] && jni_platform=darwin

    mkdir -p temp/$variant/prefix/java_platform_include
    cp projects/jni_headers/$jni_platform/* temp/$variant/prefix/java_platform_include

    for proj in "${projects[@]}"; do
        echo building $proj

        PROJECT=$proj VARIANT=$variant bash projects/$proj/build.sh
    done

    # per-project build.sh moves the "main" library into dist (not all main libraries need to be loaded)
    # but every other artifact present in the prefix is moved to deps (all of them need to be loaded)
    # the tricky part is that deps need to have proper names as they're loaded in alphabetical order

    shopt -s nullglob
    deps_to_copy=(temp/$variant/prefix/lib/*.{so,dll,dylib})

    [ -n "$deps_to_copy" ] && cp -r "${deps_to_copy[@]}" dist/$variant/deps

    # HACK: this works because winpthreads are only needed by "main" libraries
    # and wrangle, so it works just because wi < wr... kinda hacky
    if [[ $variant == "windows-"* ]]; then
        [ "$variant" = "windows-amd64" ] && libdir=/usr/x86_64-w64-mingw32/lib/ || libdir=/usr/i686-w64-mingw32/lib/

        cp $libdir/libwinpthread-1.dll dist/$variant/deps
    fi

    # ad-hoc signing for macOS libraries
    if [[ $variant == "macos-"* ]]; then
        find dist/$variant -name '*.dylib' | xargs -n1 rcodesign sign
    fi
fi

