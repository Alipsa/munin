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

/**
 * Service for storing files on the file system.
 * It provides methods to store, load, delete, and list files in a specified root location.
 */
@Service
public class FileStorageService {

  private final Path rootLocation;

  private static final Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

  /**
   * Constructor with autowired dependencies.
   *
   * @param properties the file storage properties
   */
  @Autowired
  public FileStorageService(FileStorageProperties properties) {
    this.rootLocation = Paths.get(properties.getLocation());
  }

  /**
   * Stores a file in the root location.
   *
   * @param file the file to store
   * @throws FileStorageException if an error occurs during storage
   */
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

  /**
   * Provides an OutputStream to write to a file with the given name.
   *
   * @param name the name of the file
   * @return an OutputStream to write to the file
   * @throws FileStorageException if an error occurs while creating the OutputStream
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

  /**
   * Loads all files in the root location.
   *
   * @return a Stream of Paths representing the files
   * @throws FileStorageException if an error occurs while reading the files
   */
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

  /**
   * Gets the Path for a given filename in the root location.
   *
   * @param filename the name of the file
   * @return the Path to the file
   */
  public Path getPath(String filename) {
    Path path = rootLocation.resolve(filename);
    LOG.debug("File resolved to path {}",  path.toAbsolutePath());
    return path;
  }

  /**
   * Loads a file as a Resource.
   *
   * @param filename the name of the file to load
   * @return the Resource representing the file
   * @throws RuntimeException if the file cannot be read
   */
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

  /**
   * Provides an InputStream to read from a file with the given name.
   *
   * @param name the name of the file
   * @return an InputStream to read from the file
   * @throws FileStorageException if an error occurs while creating the InputStream
   */
  public InputStream getInputStream(String name) throws FileStorageException {
    try {
      return Files.newInputStream(getPath(name));
    } catch (IOException e) {
      throw new FileStorageException("Failed to read stored files", e);
    }
  }

  /**
   * Deletes all files in the root location.
   */
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  /**
   * Deletes a file with the given name.
   *
   * @param fileName the name of the file to delete
   * @throws FileStorageException if an error occurs during deletion
   */
  public void delete(String fileName) throws FileStorageException {
    try {
      Files.delete(rootLocation.resolve(fileName));
    } catch (IOException e) {
      throw new FileStorageException("Failed to delete " + fileName, e);
    }
  }

  /**
   * Deletes a file with the given name in a subdirectory named after the class type.
   *
   * @param fileName the name of the file to delete
   * @param type the class type used to determine the subdirectory
   * @throws FileStorageException if an error occurs during deletion
   */
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

  /**
   * Initializes the storage by creating the root directory if it does not exist.
   *
   * @throws FileStorageException if an error occurs during initialization
   */
  public void init() throws FileStorageException {
    try {
      Path path = Files.createDirectories(rootLocation.toAbsolutePath());
      LOG.trace("FileSystemStorage rootLocation = {}", path );
    } catch (IOException e) {
      throw new FileStorageException("Could not initialize storage", e);
    }
  }
}
