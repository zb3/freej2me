SONAME := libffaudio

include $(COMMONDIR)/common.mk

FFMPEG_CFLAGS := $(shell $(PKG_CONFIG) --cflags libavformat libavcodec libswresample)
FFMPEG_LDLIBS := $(shell $(PKG_CONFIG) --libs libavformat libavcodec libswresample)

ifeq ($(TARGET_IS_WINDOWS),1)
	FFMPEG_LDLIBS := $(patsubst -lz,-l:libz.a,$(FFMPEG_LDLIBS))
endif

CFLAGS += $(FFMPEG_CFLAGS) 
LDFLAGS += -shared $(FFMPEG_LDLIBS) $(JAVA_LDLIBS) 

ifneq ($(TARGET_IS_MACOS),1)
	LDFLAGS += -Wl,-Bsymbolic
endif

INCLUDES += $(JAVA_INCLUDES)

SRC_FILES := libffaudio.c
OBJ_FILES := $(patsubst %.c,$(BUILDDIR)/%.o, $(SRC_FILES))

TARGET := $(BUILDDIR)/$(SONAME)

all: $(TARGET)

$(TARGET): $(OBJ_FILES)
	$(LD) -o $@  $^ $(LDFLAGS)
	$(STRIP) $@

$(BUILDDIR)/%.o: %.c
	$(CC) $(CFLAGS) $(INCLUDES) -c -o $@ $<

install: $(TARGET)
	install -m 755 $< $(DIST)

.PHONY: install
