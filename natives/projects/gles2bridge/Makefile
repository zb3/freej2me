SONAME = libgles2bridge
include $(COMMONDIR)/common.mk

WRANGLE_CFLAGS := $(shell $(PKG_CONFIG) --cflags wrangle)
WRANGLE_LDLIBS := $(shell $(PKG_CONFIG) --libs wrangle)

CFLAGS += -g -DIS_WRANGLE $(WRANGLE_CFLAGS)
LDFLAGS += -shared $(WRANGLE_LDLIBS) $(JAVA_LDLIBS)
INCLUDES += -I$(CURDIR)/inc $(JAVA_INCLUDES)

SRCDIR := .
SRC_FILES := bridge.c

OBJ_FILES := $(patsubst %.c,$(BUILDDIR)/%.o,$(filter %.c, $(SRC_FILES)))

TARGET := $(BUILDDIR)/$(SONAME)

all: $(TARGET)

$(TARGET): $(OBJ_FILES)
	$(LD) -o $@ $^ $(LDFLAGS) $(LDXXFLAGS)

$(BUILDDIR)/%.o: $(SRCDIR)/%.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) $(INCLUDES) -c -o $@ $<

install: $(TARGET)
	install -m 755 $< $(DIST)

.PHONY: install


