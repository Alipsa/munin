package se.alipsa.renjin.webreports.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileStorageService {

  private final Path rootLocation;

  private static final Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

  @Autowired
  public FileStorageService(FileStorageProperties properties) {
    this.rootLocation = Paths.get(properties.getLocation());
  }

  public void store(MultipartFile file, Class<?> type) throws FileStorageException {
    if (file == null) {
      LOG.warn("File is null, nothing to store");
      return;
    }
    String orgFileName = file.getOriginalFilename();
    if (orgFileName == null ) {
      orgFileName = "";
    }
    String filename = StringUtils.cleanPath(orgFileName);
    LOG.trace("Storing file {}", filename);
    try {
      if (file.isEmpty()) {
        throw new FileStorageException("Failed to store empty file " + filename);
      }
      if (filename.contains("..")) {
        // This is a security check
        throw new FileStorageException(
            "Cannot store file with relative path outside current directory "
            + filename);
      }
      try (InputStream inputStream = file.getInputStream()) {
        Path destPath = load(filename, type);
        LOG.trace("Copy content of {} to {}, type is {}", file, destPath.toAbsolutePath(), type.getSimpleName());
        Files.copy(inputStream, destPath,
            StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new FileStorageException("Failed to store file " + filename, e);
    }
  }

  public OutputStream getOutputStream(String name, Class<?> type) throws FileStorageException {
    try {
      if (name.isEmpty()) {
        throw new FileStorageException("Failed to create store for empty file name " + name);
      }
      if (name.contains("..")) {
        // This is a security check
        throw new FileStorageException(
            "Cannot store file with relative path outside current directory "
            + name);
      }
      return Files.newOutputStream(load(name, type));
    } catch (IOException e) {
      throw new FileStorageException("Failed to create store for file " + name, e);
    }
  }

  public Stream<Path> loadAll(Class<?> type) throws FileStorageException {
    try {
      ensureDir(type);
      Path dir = rootLocation.resolve(toPath(type));
      LOG.debug("Listing files in {}", dir.toAbsolutePath());
      return Files.walk(dir, 1)
          .filter(path -> !path.equals(dir))
          .map(dir::relativize);
    } catch (IOException e) {
      throw new FileStorageException("Failed to read stored files", e);
    }
  }

  public Path load(String filename,  Class<?> type) throws FileStorageException {
    ensureDir(type);
    Path path = rootLocation.resolve(toPath(type)).resolve(filename);
    LOG.debug("File resolved to path {}",  path.toAbsolutePath());
    return path;
  }

  public InputStream getInputStream(String name,  Class<?> type) throws FileStorageException {
    try {
      return Files.newInputStream(load(name, type));
    } catch (IOException e) {
      throw new FileStorageException("Failed to read stored files", e);
    }
  }

  public Resource loadAsResource(String filename, Class<?> type) throws FileStorageException {
    try {
      Path file = load(filename, type);
      LOG.debug("resource is {}", file.toAbsolutePath());
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new FileStorageException(
            "Could not read file: " + filename);

      }
    } catch (MalformedURLException | FileStorageException e) {
      throw new FileStorageException("Could not read file: " + filename, e);
    }
  }

  public void deleteAll( Class<?> type) {
    FileSystemUtils.deleteRecursively(rootLocation.resolve(toPath(type)).toFile());
  }

  public void delete(String fileName,  Class<?> type) throws FileStorageException {
    try {
      Files.delete(rootLocation.resolve(toPath(type)).resolve(fileName));
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete " + fileName, e);
    }
  }

  private void ensureDir(Class<?> type) throws FileStorageException {
    Path path = rootLocation.toAbsolutePath();
    Path subdir = path.resolve(toPath(type));
    if (!Files.exists(subdir)) {
      try {
        LOG.debug("Creating subdir {}", subdir);
        Files.createDirectories(subdir);
      } catch (IOException e) {
        throw new FileStorageException("Failed to create storage directory " + subdir, e);
      }
    } else {
      LOG.trace("dir {} already exists", subdir);
    }
  }

  private String toPath(Class clazz) {
    return clazz.getSimpleName() + "/";
  }

  public void init() throws FileStorageException {
    try {
      Path path = Files.createDirectories(rootLocation.toAbsolutePath());
      LOG.trace("FileSystemStorage rootLocation = {}", path );
    } catch (IOException e) {
      throw new FileStorageException("Could not initialize storage", e);
    }
  }
}
