PKG_CONFIG ?= pkg-config

JAVA_HOME ?= /usr/lib/jvm/java
JAVA_LDLIBS := 
JAVA_INCLUDES := -I$(JAVA_HOME)/include -I$(PREFIX)/java_platform_include

ifneq (,$(findstring mingw,$(HOST)))
	TARGET_IS_WINDOWS = 1
	SOEXT = dll
	ZLIB_DEP = -l:libz.a
	
	LDFLAGS += -static-libgcc
	LDXXFLAGS += -static-libstdc++

	ifneq (,$(findstring i686,$(HOST)))
		JAVA_LDLIBS += -Wl,--add-stdcall-alias
	endif
else ifneq (,$(findstring apple,$(HOST)))
	TARGET_IS_MACOS = 1
	SOEXT = dylib
	ZLIB_DEP = -lz
else
	TARGET_IS_LINUX = 1
	SOEXT = so
	ZLIB_DEP = -lz
	
	LDFLAGS += -static-libgcc
	
	# this is for glibc 2.17 (rhel7) compatibility
	LDFLAGS += -L$(COMMONDIR)/linux-sysdep/$(HOST)
endif

INCLUDES := -I$(CURDIR)

CFLAGS += -O2 -fPIC

ifneq (,$(SONAME))
	SONAME := $(SONAME).$(SOEXT)

	ifneq ($(TARGET_IS_MACOS),1)
		LDFLAGS += -Wl,-soname,$(SONAME)
	endif
endif




