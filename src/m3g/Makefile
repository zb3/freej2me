CC := gcc
CXX := g++
LD := $(CXX)

JAVA_HOME ?= /usr/lib/jvm/java

CFLAGS := -O2 -DM3G_TARGET_LINUX -DM3G_GL_ES_1_1  -fPIC
CXXFLAGS := $(CFLAGS)
LDLIBS := -shared -Wl,-soname,libjavam3g.so -lEGL -lGL -lz
INCLUDES := -I$(CURDIR) -I$(CURDIR)/inc -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux

SRCDIR := $(CURDIR)/src
OBJDIR := $(CURDIR)/obj
LIBDIR := $(CURDIR)

SRC_FILES := \
	CSynchronization.cpp \
	m3g_linux_java_api.cpp \
	m3g_core.c \
	m3g_linux.cpp \
	m3g_linux_gl.cpp

OBJ_FILES := $(patsubst %.c,$(OBJDIR)/%.o,$(filter %.c, $(SRC_FILES))) \
            $(patsubst %.cpp,$(OBJDIR)/%.o,$(filter %.cpp, $(SRC_FILES)))

TARGET := $(LIBDIR)/libjavam3g.so

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


