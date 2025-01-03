package se.alipsa.munin.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

  /**
   * Gets a reference to a file or folder in the classpath. Useful for getting test resources and
   * other similar artifacts.
   *
   * @param name        the name of the resource, use / to separate path entities.
   *                    Do NOT lead with a "/" unless you know what you are doing.
   * @param encodingOpt optional encoding if something other than UTF-8 is needed.
   * @return The resource as a file.
   * @throws FileNotFoundException if the resource cannot be found.
   */
  public static File getResource(String name, String... encodingOpt) throws FileNotFoundException {
    File file;
    try {
      String path = getResourcePath(name, encodingOpt);
      file = new File(path);
    } catch (UnsupportedEncodingException e) {
      throw new FileNotFoundException("Failed to find resource " + name);
    }
    return file;
  }

  public static String getResourcePath(String name, String... encodingOpt) throws UnsupportedEncodingException {
    String encoding = encodingOpt.length > 0 ? encodingOpt[0] : "UTF-8";
    URL url = getResourceUrl(name);
    return URLDecoder.decode(url.getFile(), encoding);
  }

  /**
   * Find a resource using available class loaders.
   * It will also load resources/files from the
   * absolute path of the file system (not only the classpath's).
   * @param resource the path the resource
   * @return a URL to the resource
   */
  public static URL getResourceUrl(String resource) {
    final List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
    classLoaders.add(Thread.currentThread().getContextClassLoader());
    classLoaders.add(FileUtils.class.getClassLoader());

    for (ClassLoader classLoader : classLoaders) {
      final URL url = getResourceWith(classLoader, resource);
      if (url != null) {
        return url;
      }
    }

    final URL systemResource = ClassLoader.getSystemResource(resource);
    if (systemResource != null) {
      return systemResource;
    } else {
      try {
        return new File(resource).toURI().toURL();
      } catch (MalformedURLException e) {
        return null;
      }
    }
  }

  private static URL getResourceWith(ClassLoader classLoader, String resource) {
    if (classLoader != null) {
      return classLoader.getResource(resource);
    }
    return null;
  }


  /**
   * fetch a list of files for the dir specified and the extension specified
   * extension in not case sensitive
   * @param dir the folder to search in
   * @param ext the extension to filter for
   * @return a list of files matching the extension
   */
  public static List<File> findFilesWithExt(File dir, String ext) {
    if (dir == null || ext == null) {
      return null;
    }
    File[] files = dir.listFiles();
    if (files == null) {
      return null;
    }
    return Arrays.stream(files)
        .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(ext.toLowerCase()))
        .collect(Collectors.toList());
  }

  /**
   * Copy a file to a dir.
   *
   * @param from  the file to copy
   * @param toDir the destination dir
   * @return the copied file
   * @throws IOException if an IO issue occurs
   */
  public static File copy(File from, File toDir) throws IOException {
    File destFile = new File(toDir, from.getName());
    if (!from.exists()) {
      throw new IOException("File " + from.getAbsolutePath() + " does not exist");
    }
    if (toDir.exists() && toDir.isFile()) {
      throw new IllegalArgumentException("Target must be a directory");
    }

    if (destFile.exists()) {
      System.out.println("File " + destFile.getAbsolutePath() + " already exists");
      return destFile;
    }
    if (!toDir.exists() && !toDir.mkdirs()) {
      throw new IOException("Failed to create directory " + toDir);
    }
    if (!destFile.createNewFile()) {
      throw new IOException("Failed to create destination file " + destFile);
    }
    Files.copy(from.toPath(), new FileOutputStream(destFile));
    return destFile;
  }

  public static long copy(String resourcePath, File toDir) throws IOException {
    URL url = getResourceUrl(resourcePath);
    if (url == null) {
      throw new FileNotFoundException("Failed to find file " + resourcePath);
    }

    String fileName = FilenameUtils.getName(url.getPath());
    return Files.copy(url.openStream(), toDir.toPath().resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * Copy a file to a dir.
   *
   * @param from         the file to copy
   * @param toFile       the destination file
   * @param overwriteOpt whether to overwrite existing file or not, default true
   * @return the copied file
   * @throws IOException if an IO issue occurs
   */
  public static File copyFile(File from, File toFile, boolean... overwriteOpt) throws IOException {
    if (!from.exists()) {
      throw new IOException("File " + from.getAbsolutePath() + " does not exist");
    }
    boolean overwrite = overwriteOpt.length <= 0 || overwriteOpt[0];
    if (!overwrite && toFile.exists()) {
      throw new IOException("File " + toFile.getAbsolutePath() + " already exists");
    }
    Files.copy(from.toPath(), new FileOutputStream(toFile));
    return toFile;
  }

  /**
   * Find the first file matching the pattern.
   *
   * @param dir    the dir to search
   * @param prefix the pattern to match
   * @param suffix the pattern to match
   * @return the first file matching the pattern
   */
  public static File findFirst(File dir, String prefix, String suffix) {
    if (dir == null || !dir.exists()) {
      return null;
    }
    File[] files = dir.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isFile() && file.getName().startsWith(prefix) && file.getName().endsWith(suffix)) {
          return file;
        }
      }
    }
    return null;
  }

  /**
   * recursive delete on exit.
   * @param dir the folder to delete
   * @throws IOException if some problem occurred when deleting the content
   */
  public static void deleteOnExit(File dir) throws IOException {
    if (dir == null) {
      return;
    }
    dir.deleteOnExit();
    Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        file.toFile().deleteOnExit();
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        dir.toFile().deleteOnExit();
        return FileVisitResult.CONTINUE;
      }
    });

  }

  /**
   * @param file    the file to write to
   * @param content the content to write
   * @throws FileNotFoundException If the given file object does not denote an existing, writable regular file
   *                               and a new regular file of that name cannot be created,
   *                               or if some other error occurs while opening or creating the file
   * @throws UnsupportedEncodingException If the JVM does not support UTF-8
   */
  public static void writeToFile(File file, String content) throws FileNotFoundException, UnsupportedEncodingException {
    try (PrintStream out = new PrintStream(file, StandardCharsets.UTF_8.name())) {
      out.print(content);
    }
  }


  /**
   * @param file    the file to write to
   * @param lines the content to write
   * @throws IOException If the given file object does not denote an existing, writable regular file
   *                               and a new regular file of that name cannot be created,
   *                               or if some other error occurs while opening, creating or writing to the file
   */
  public static void writeToFile(File file, List<String> lines) throws IOException {
    writeToFile(file, lines, "\n", StandardCharsets.UTF_8);
  }

  /**
   * @param file    the file to write to
   * @param lines the content to write
   * @param lineEnding the character to use at the end of each line.
   * @param charset the charset to use when writing the file
   * @throws IOException If the given file object does not denote an existing, writable regular file
   *                               and a new regular file of that name cannot be created,
   *                               or if some other error occurs while opening, creating or writing to the file
   */
  public static void writeToFile(File file, List<String> lines, String lineEnding, Charset charset) throws IOException {
    try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), charset)) {
      for (String line : lines) {
        writer.write(line + lineEnding);
      }
    }
  }

  public static void appendToOrCreateFile(File file, String content) throws IOException {
    if (!file.exists() && !file.createNewFile()) {
      throw new IOException("Failed to create file " + file);
    }
    try(Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);) {
      writer.write(content);
    }
  }

  public static String readContent(File file, Charset... charsetOpt) throws IOException {
    Charset charset = charsetOpt.length > 0 ? charsetOpt[0] : StandardCharsets.UTF_8;
    StringBuilder str = new StringBuilder();
    Stream<String> stream = Files.lines( file.toPath(), charset);
    stream.forEach(s -> str.append(s).append("\n"));
    return str.toString();
  }

  public static String readContent(String resource, Charset... charsetOpt) throws IOException {
    URL url = getResourceUrl(resource);
    if (url == null) {
      throw new FileNotFoundException("Failed to read resource " + resource);
    }
    Charset charset = charsetOpt.length > 0 ? charsetOpt[0] : StandardCharsets.UTF_8;
    return IOUtils.toString(url, charset);
  }

  @SuppressFBWarnings("ENV_USE_PROPERTY_INSTEAD_OF_ENV")
  public static File getUserHome() {
    String userHome = System.getProperty("user.home");
    if (userHome == null) {
      userHome = System.getenv("user.home");
      if (userHome == null) {
        userHome = System.getenv("USERPROFILE");
        if (userHome == null) {
          userHome = System.getenv("HOME");
        }
      }
    }

    if (userHome == null) {
      System.out.println("Failed to find user home property");
      throw new RuntimeException("Failed to find user home property");
    }
    File homeDir = new File(userHome);
    if(!homeDir.exists()) {
      System.err.printf("User home dir %s does not exist, something is not right%n", homeDir);
      throw new RuntimeException("User home dir " + homeDir + " does not exist, something is not right");
    }
    return homeDir;
  }

}
