SONAME = libjavam3g
include $(COMMONDIR)/common.mk

WRANGLE_CFLAGS := $(shell $(PKG_CONFIG) --cflags wrangle)
WRANGLE_LDLIBS := $(shell $(PKG_CONFIG) --libs wrangle)

LD := $(CXX)

CFLAGS += -DM3G_TARGET_LINUX -DM3G_GL_ES_1_1 -DIS_WRANGLE  $(WRANGLE_CFLAGS)
LDFLAGS += -shared $(WRANGLE_LDLIBS) $(ZLIB_DEP) $(JAVA_LDLIBS)

INCLUDES += -I$(CURDIR)/inc $(JAVA_INCLUDES)

SRCDIR := src

SRC_FILES := \
	CSynchronization.cpp \
	m3g_linux_java_api.cpp \
	m3g_core.c \
	m3g_linux.cpp \
	m3g_linux_gl.cpp

OBJ_FILES := $(patsubst %.c,$(BUILDDIR)/%.o,$(filter %.c, $(SRC_FILES))) \
            $(patsubst %.cpp,$(BUILDDIR)/%.o,$(filter %.cpp, $(SRC_FILES)))

TARGET := $(BUILDDIR)/$(SONAME)

all: $(TARGET)

$(TARGET): $(OBJ_FILES)
	$(LD) -o $@ $^ $(LDFLAGS) $(LDXXFLAGS)

$(BUILDDIR)/%.o: $(SRCDIR)/%.c
	$(CC) $(CFLAGS) $(INCLUDES) -c -o $@ $<

$(BUILDDIR)/%.o: $(SRCDIR)/%.cpp
	$(CXX) $(CFLAGS) $(CXXFLAGS) $(INCLUDES) -c -o $@ $<

install: $(TARGET)
	install -m 755 $< $(DIST)

.PHONY: install

