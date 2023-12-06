. $COMMONDIR/common.sh

set -e

make
make install

mkdir -p $PREFIX/lib/pkgconfig

sed -e "s/{{SOEXT}}/$soext/" -e "s/{{PREFIX}}/${PREFIX//\//\\/}/" <wrangle.pc.in >$PREFIX/lib/pkgconfig/wrangle.pc
