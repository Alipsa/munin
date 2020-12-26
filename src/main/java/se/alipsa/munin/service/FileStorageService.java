package se.alipsa.munin.service;

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

  public void store(MultipartFile file) throws FileStorageException {
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
        Path destPath = getPath(filename);
        LOG.trace("Copy content of {} to {}", file, destPath.toAbsolutePath());
        Files.copy(inputStream, destPath,
            StandardCopyOption.REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new FileStorageException("Failed to store file " + filename, e);
    }
  }

  /*
  public void save(MultipartFile file) throws FileStorageException {
    if (file == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be null");
    }
    String fileName = file.getOriginalFilename();
    if (fileName == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Original file name cannot be null");
    }
    try {
      Files.copy(file.getInputStream(), rootLocation.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      throw new FileStorageException("Could not store the file. Error: " + e.getMessage());
    }
  }
   */

  public OutputStream getOutputStream(String name) throws FileStorageException {
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
      return Files.newOutputStream(getPath(name));
    } catch (IOException e) {
      throw new FileStorageException("Failed to create store for file " + name, e);
    }
  }

  public Stream<Path> loadAll() throws FileStorageException {
    try {
      LOG.debug("Listing files in {}", rootLocation.toAbsolutePath());
      return Files.walk(rootLocation, 1)
          .filter(path -> !path.equals(rootLocation))
          .map(rootLocation::relativize);
    } catch (IOException e) {
      throw new FileStorageException("Failed to read stored files", e);
    }
  }

  public Path getPath(String filename) {
    Path path = rootLocation.resolve(filename);
    LOG.debug("File resolved to path {}",  path.toAbsolutePath());
    return path;
  }

  public Resource load(String filename) {
    try {
      Path file = rootLocation.resolve(filename);
      Resource resource = new UrlResource(file.toUri());

      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read the file!");
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error: " + e.getMessage());
    }
  }

  public InputStream getInputStream(String name) throws FileStorageException {
    try {
      return Files.newInputStream(getPath(name));
    } catch (IOException e) {
      throw new FileStorageException("Failed to read stored files", e);
    }
  }

  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  public void delete(String fileName) throws FileStorageException {
    try {
      Files.delete(rootLocation.resolve(fileName));
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete " + fileName, e);
    }
  }

  public void delete(String fileName,  Class<?> type) throws FileStorageException {
    try {
      Files.delete(rootLocation.resolve(toPath(type)).resolve(fileName));
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete " + fileName, e);
    }
  }

  private String toPath(Class<?> clazz) {
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
