SONAME = libwrangle

include $(COMMONDIR)/common.mk

ANGLE_CFLAGS := $(shell $(PKG_CONFIG) --cflags angle)
ANGLE_LDLIBS := $(shell $(PKG_CONFIG) --libs angle)

CFLAGS += -DWRANGLE_EXPORT -I$(CURDIR)/include $(ANGLE_CFLAGS)
LDFLAGS += -shared $(ANGLE_LDLIBS)

SRC_FILES := wrangle.c
OBJ_FILES := $(patsubst %.c,$(BUILDDIR)/%.o,$(filter %.c, $(SRC_FILES)))

TARGET := $(BUILDDIR)/$(SONAME)

all: $(TARGET)

$(TARGET): $(OBJ_FILES)
	$(LD) -o $@  $^ $(LDFLAGS)
	$(STRIP) $@

$(BUILDDIR)/%.o: %.c
	$(CC) $(CFLAGS) -c -o $@ $<

install: $(TARGET)
	install -m 755 $< $(PREFIX)/lib
	cp -r include $(PREFIX)

.PHONY: install

