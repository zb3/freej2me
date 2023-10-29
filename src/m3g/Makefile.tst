CC := gcc
CXX := g++
LD := g++

JAVA_HOME ?= /usr/lib/jvm/java

CFLAGS := -O3 -DM3G_TARGET_LINUX -DM3G_GL_ES_1_1
CXXFLAGS := $(CFLAGS)
LDLIBS := -shared -Wl,-soname,libtest.so -lEGL -lGL -lz
INCLUDES := -I$(CURDIR)/inc -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux

SOURCES := extest.c

TARGET := libtest.so

$(TARGET): exttest.o
	$(LD) $(LDFLAGS) exttest.o $(LDLIBS) -o $@

exttest.o: exttest.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) $(INCLUDES) -fPIC -c $< -o $@

clean:
	rm -rf exttest.o libtest.so

.PHONY: clean

all: $(TARGET)
