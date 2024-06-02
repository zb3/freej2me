package pl.zb3.freej2me;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This loader is tricky, there are so called "main" libraries (not all need to be loaded)
 * and "deps" (all of them need to be loaded).
 *
 * The loadLibrary function is given the "main" library name. If it was called
 * for the first time, it extracts all natives into a temporary directory,
 * then it loads all "deps" in alphabetical order, and then loads the "main"
 * library.
 *
 * This design was choosen because:
 * a) These libraries need to be loaded explicitly (I'll admit: I have no idea whether they would be without this design)
 * b) We can't modify java.library.path, not in an official, forward-compatible manner..
 */
public class NativeLoader {
    private static String TAG = "NativeLoader";
    private static String libraryDir = null;
    private static Set<String> loadedLibraries = new HashSet<>();

    public static void loadLibrary(String libraryName) {
        if (loadedLibraries.contains(libraryName)) {
            return;
        }

        if (libraryDir == null) {
            String javaOsName = System.getProperty("os.name");
            String osName = "linux";

            if (javaOsName.contains("indows")) {
                osName = "windows";
            } else if (javaOsName.contains("Mac OS")) {
                osName = "macos";
            }

            String arch = System.getProperty("os.arch");
            if (arch.equals("x86_64")) { // this happens on mac
                arch = "amd64";
            }
            if (arch.equals("aarch64")) {
                arch = "arm64";
            }

            String sourceLibraryDir = String.format("natives/%s-%s", osName, arch);
            File tempDir = createTempDirectory(osName.equals("windows"));

            System.out.println(TAG+": extracting native libraries to "+tempDir);

            extract(sourceLibraryDir, tempDir);

            File depsDir = new File(tempDir, "deps");
            File[] files = depsDir.listFiles();
            if (files != null) {
                /*
                 * yes, we rely on this
                 * the alternative is to append to java.library.path
                 * but that's troublesome on newer Java versions
                 */
                Arrays.sort(files);

                for (File file : files) {
                    if (!file.getName().endsWith(".json")) {
                        // System.out.println("loading dep library: " + file.getAbsolutePath());
                        System.load(file.getAbsolutePath());
                    }
                }
            }

            libraryDir = tempDir.getAbsolutePath();
        }

        String libraryFileName = System.mapLibraryName(libraryName);
        if (!libraryFileName.startsWith("lib")) {
            libraryFileName = "lib" + libraryFileName;
        }

        String libraryPath = libraryDir + "/" + libraryFileName;
        System.load(libraryPath);
        loadedLibraries.add(libraryName);
    }

    private static File createTempDirectory(boolean isWindows) {
        // on Windows, we can't delete the directory on exit because the dll files
        // are still loaded.. so we use a persistent location to avoid flooding the temp directory

        String dirName = isWindows ? "freej2me-natives-tmp" : "freej2me-" + System.nanoTime();

        File tempDir = new File(System.getProperty("java.io.tmpdir"), dirName);
        if (!tempDir.exists() && !tempDir.mkdir()) {
            throw new RuntimeException("Failed to create a temporary directory");
        }

        if (!isWindows) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    NativeLoader.deleteDirectory(tempDir);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }
        return tempDir;
    }

    private static void extract(String directoryToExtract, File tempDir) {
        if (!directoryToExtract.endsWith("/")) {
            directoryToExtract += "/";
        }

        File jarFile = null;

        try {
            jarFile = new File(NativeLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch(URISyntaxException e) {
            e.printStackTrace();
        }

        Path tempDirPath = tempDir.toPath();

        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();

                if (entryName.startsWith(directoryToExtract)) {
                    Path entryDest = tempDirPath.resolve(entryName.substring(directoryToExtract.length()));

                    if (entry.isDirectory()) {
                        Files.createDirectories(entryDest);
                    } else {
                        Files.createDirectories(entryDest.getParent());

                        try (InputStream is = jar.getInputStream(entry);
                             OutputStream os = Files.newOutputStream(entryDest)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                os.write(buffer, 0, bytesRead);
                            }
                        } catch (FileSystemException e) {
                            // on Windows this might fail when there's another
                            // instance of freej2me running.
                            // so if the file already exists, let it be.
                            if (!Files.exists(entryDest)) {
                                throw e;
                            } else {
                                System.out.println(TAG+": assuming another instance is running");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }

        if (!directory.delete()) {
            System.err.println("Failed to delete directory: " + directory);
        }
    }
}