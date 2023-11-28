CC := gcc
LD := $(CC)

JAVA_HOME=$(shell readlink -f /usr/bin/javac | sed "s:bin/javac::")

CFLAGS := -O2 -fPIC
LDLIBS := -shared -Wl,-soname,libffaudio.so -lavformat -lavcodec -lswresample
INCLUDES := -I$(CURDIR) -I$(CURDIR)/inc -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I/usr/include/ffmpeg

SRCDIR := $(CURDIR)
OBJDIR := $(CURDIR)
LIBDIR := $(CURDIR)

SRC_FILES := \
	libffaudio.c

OBJ_FILES := $(patsubst %.c,$(OBJDIR)/%.o,$(filter %.c, $(SRC_FILES)))

TARGET := $(LIBDIR)/libffaudio.so

all: $(TARGET)

$(TARGET): $(OBJ_FILES)
	$(LD) $(LDLIBS) -o $@ $^

$(OBJDIR)/%.o: $(SRCDIR)/%.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) $(INCLUDES) -c -o $@ $<

clean:
	rm -f $(OBJ_FILES) $(TARGET)

.PHONY: clean


