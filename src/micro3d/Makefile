CC := gcc
CXX := g++
LD := $(CXX)

JAVA_HOME ?= /usr/lib/jvm/java

CFLAGS := -ggdb -DM3G_TARGET_LINUX -fPIC -Wall
CXXFLAGS := $(CFLAGS)
LDLIBS := -shared -Wl,-soname,libmicro3d.so -lEGL -lGLESv2 -lz
INCLUDES := -I$(CURDIR) -I$(CURDIR)/inc -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux

SRCDIR := $(CURDIR)/src
OBJDIR := $(CURDIR)/obj
LIBDIR := $(CURDIR)

SRC_FILES := \
	utils.cpp \
	program.c \
	render.c \
	texture.c 

OBJ_FILES := $(patsubst %.c,$(OBJDIR)/%.o,$(filter %.c, $(SRC_FILES))) \
            $(patsubst %.cpp,$(OBJDIR)/%.o,$(filter %.cpp, $(SRC_FILES)))

TARGET := $(LIBDIR)/libmicro3d.so

all: $(TARGET)

$(TARGET): $(OBJ_FILES)
	$(LD) $(LDLIBS) -o $@ $^

$(OBJDIR)/%.o: $(SRCDIR)/%.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) $(INCLUDES) -c -o $@ $<

$(OBJDIR)/%.o: $(SRCDIR)/%.cpp
	@mkdir -p $(dir $@)
	$(CXX) $(CXXFLAGS) $(INCLUDES) -c -o $@ $<

clean:
	rm -f $(OBJ_FILES) $(TARGET)

.PHONY: clean


