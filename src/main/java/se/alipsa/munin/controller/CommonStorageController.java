package se.alipsa.munin.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.alipsa.munin.service.FileStorageException;
import se.alipsa.munin.service.FileStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This controller handles common content such as
 * images and css files that are uploaded and meant to be shared
 * across multiple reports.
 */
@Controller
public class CommonStorageController {

  private final FileStorageService fileStorageService;

  /**
   * Constructor with autowired dependencies.
   *
   * @param fileStorageService the file storage service
   */
  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public CommonStorageController(FileStorageService fileStorageService) {
    this.fileStorageService = fileStorageService;
  }

  /**
   * Displays the common resources page with a list of uploaded files.
   *
   * @param message an optional message to display on the page
   * @return a ModelAndView object for the common resources page
   */
  @GetMapping("/common/resources")
  public ModelAndView commonIndex(@RequestParam Optional<String> message) {
    ModelAndView mav = new ModelAndView();
    List<String> files = new ArrayList<>();
    message.ifPresent(m -> mav.addObject("message", m));
    try {
      fileStorageService.loadAll().forEach(p -> files.add(p.getFileName().toString()));
      mav.addObject("files", files);
    } catch (FileStorageException e) {
      e.printStackTrace();
    }
    mav.setViewName("common.html");
    return mav;
  }

  /**
   * Retrieves a file by its name and returns it as a downloadable resource.
   *
   * @param fileName the name of the file to retrieve
   * @return a ResponseEntity containing the file resource
   */
  @GetMapping("/common/resources/{fileName}")
  public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
    Resource file = fileStorageService.load(fileName);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
  }

  /**
   * Handles file upload and stores the uploaded file.
   *
   * @param file the uploaded file
   * @param redirectAttributes attributes for flash messages
   * @return a redirect to the common resources page
   */
  @PostMapping("/common/resources/upload")
  public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
    try {
      fileStorageService.store(file);
      redirectAttributes.addFlashAttribute("message","Uploaded the file successfully: " + file.getOriginalFilename());

    } catch (FileStorageException e) {
      redirectAttributes.addFlashAttribute("message","Could not upload the file: " + file.getOriginalFilename() + "!");
    }
    return "redirect:/common/resources";
  }

  /**
   * Deletes a file by its name.
   *
   * @param fileName the name of the file to delete
   * @return a message indicating the result of the deletion
   */
  @DeleteMapping(value = "/common/resources/{fileName}", produces = MediaType.TEXT_PLAIN_VALUE)
  public @ResponseBody String deleteFile(@PathVariable String fileName) {
    try {
      fileStorageService.delete(fileName);
      return fileName + " deleted successfully!";
    } catch (FileStorageException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete " + fileName + ", " + e);
    }
  }
}
