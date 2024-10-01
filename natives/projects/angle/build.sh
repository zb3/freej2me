. $COMMONDIR/common.sh

mkdir -p $PREFIX/lib
cp -a "prebuilts/$variant"/* $PREFIX/lib
cp -a -r "prebuilts/$variant"/* $DIST/deps

mkdir -p $PREFIX/include/angle
cp -ar include/* $PREFIX/include/angle

mkdir -p $PREFIX/lib/pkgconfig

[[ $variant == "linux-"* ]] && soext=so
[[ $variant == "windows-"* ]] && soext=dll
[[ $variant == "macos-"* ]] && soext=dylib

sed -e "s/{{SOEXT}}/$soext/" -e "s/{{PREFIX}}/${PREFIX//\//\\/}/" <angle.pc.in >$PREFIX/lib/pkgconfig/angle.pc