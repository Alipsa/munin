package se.alipsa.munin.service;

import freemarker.cache.TemplateCache;
import freemarker.cache.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.alipsa.munin.controller.ReportNotFoundException;
import se.alipsa.munin.model.Report;
import se.alipsa.munin.repo.ReportRepo;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class JournoTemplateLoader implements TemplateLoader {

  private static final Logger LOG = LoggerFactory.getLogger(JournoTemplateLoader.class);
  ReportRepo reportRepo;

  @Autowired
  JournoTemplateLoader(ReportRepo reportRepo) {
    this.reportRepo = reportRepo;
  }

  /**
   * Finds the template in the backing storage and returns an object that identifies the storage location where the
   * template can be loaded from. See the return value for more information.
   *
   * @param name
   *            The name (template root directory relative path) of the template, already localized and normalized by
   *            the {@link freemarker.cache.TemplateCache cache}. It is completely up to the loader implementation to
   *            interpret the name, however it should expect to receive hierarchical paths where path components are
   *            separated by a slash (not backslash). Backslashes (or any other OS specific separator character) are
   *            not considered as separators by FreeMarker, and thus they will not be replaced with slash before
   *            passing to this method, so it's up to the template loader to handle them (say, by throwing an
   *            exception that tells the user that the path (s)he has entered is invalid, as (s)he must use slash --
   *            typical mistake of Windows users). The passed names are always considered relative to some
   *            loader-defined root location (often referred as the "template root directory"), and will never start
   *            with a slash, nor will they contain a path component consisting of either a single or a double dot --
   *            these are all resolved by the template cache before passing the name to the loader. As a side effect,
   *            paths that trivially reach outside template root directory, such as {@code ../my.ftl}, will be
   *            rejected by the template cache, so they never reach the template loader. Note again, that if the path
   *            uses backslash as path separator instead of slash as (the template loader should not accept that), the
   *            normalization will not properly happen, as FreeMarker (the cache) recognizes only the slashes as
   *            separators.
   *
   * @return An object representing the template source, which can be supplied in subsequent calls to
   *         {@link #getLastModified(Object)} and {@link #getReader(Object, String)}, when those are called on the
   *         same {@link TemplateLoader}. {@code null} must be returned if the source for the template doesn't exist;
   *         don't throw exception then! The exact type of this object is up to the {@link TemplateLoader}
   *         implementation. As this object is possibly used as hash key in caches, and is surly compared with another
   *         template source for equality, <b>it must have a proper {@link Object#equals(Object)} and
   *         {@link Object#hashCode()}) implementation</b>. Especially, template sources that refer to the same
   *         physical source must be equivalent, otherwise template caching can become inefficient. This is only
   *         expected from {@link Object#equals(Object)} when the compared template sources came from the same
   *         {@link TemplateLoader} instance. Also, it must not influence the equality if the source is open or
   *         closed ({@link #closeTemplateSource(Object)}).
   *
   * @throws IOException
   *             When an error occurs that makes it impossible to find out if the template exists, or to access the
   *             existing template. Don't throw exception if the template doesn't exist, instead return with
   *             {@code null} then!
   */
  @Override
  public Object findTemplateSource(String name) throws IOException {
    //LOG.info("Finding template source {}", name);
    try {
      Optional<Report> report = reportRepo.findById(name);
      if (report.isPresent()) {
        return report.get();
      }
      // The template engine is doing some localization attempts, appending _en_US, and _en in subsequent attempts
      // This is dependent on config, so should not be needed for Journo as it is taken care of there
      LOG.warn("Could not find template source {}, trying to find a similar one", name);
      for (String r : reportRepo.getJournoReports()) {
        if (name.startsWith(r)) {
          LOG.info("Found template source {}", name);
          return reportRepo.loadReport(r);
        }
      }
      return null;
    } catch (ReportNotFoundException e) {
      return null;
    }
  }

  /**
   * Returns the time of last modification of the specified template source. This method is called after
   * <code>findTemplateSource()</code>.
   *
   * @param templateSource
   *            an object representing a template source (the template file), obtained through a prior call to
   *            {@link #findTemplateSource(String)}. This must be an object on which
   *            {@link TemplateLoader#closeTemplateSource(Object)} wasn't applied yet.
   * @return The time of last modification for the specified template source, or -1 if the time is not known. This
   *            value meant to be milliseconds since the epoch, but in fact FreeMarker doesn't care what it means, it
   *            only cares if it changes, in which case the template needs to be reloaded (even if the value has
   *            decreased). -1 is not special in that regard either; if you keep returning it, FreeMarker won't
   *            reload the template (as far as it's not evicted from the cache from some other
   *            reason). Note that {@link Long#MIN_VALUE} is reserved for internal use.
   */
  @Override
  public long getLastModified(Object templateSource) {
    if (templateSource == null) {
      throw new IllegalArgumentException("templateSource cannot be null");
    }
    if (templateSource instanceof Report report) {
      return report.getUpdatedAt().toEpochSecond(ZoneOffset.ofTotalSeconds(0));
    }
    throw new IllegalArgumentException("templateSource is not a Report but a " + templateSource.getClass());
  }

  /**
   * Returns the character stream of a template represented by the specified template source. This method is possibly
   * called for multiple times for the same template source object, and it must always return a {@link Reader} that
   * reads the template from its beginning. Before this method is called for the second time (or later), its caller
   * must close the previously returned {@link Reader}, and it must not use it anymore. That is, this method is not
   * required to support multiple concurrent readers for the same source {@code templateSource} object.
   *
   * <p>
   * Typically, this method is called if the template is missing from the cache, or if after calling
   * {@link #findTemplateSource(String)} and {@link #getLastModified(Object)} it was determined that the cached copy
   * of the template is stale. Then, if it turns out that the {@code encoding} parameter used doesn't match the actual
   * template content (based on the {@code #ftl encoding=...} header), this method will be called for a second time
   * with the correct {@code encoding} parameter value.
   *
   * <p>
   * Unlike {@link #findTemplateSource(String)}, this method must not tolerate if the template is not found, and
   * must throw {@link IOException} in that case.
   *
   * @param templateSource
   *            an object representing a template source, obtained through a prior call to
   *            {@link #findTemplateSource(String)}. This must be an object on which
   *            {@link TemplateLoader#closeTemplateSource(Object)} wasn't applied yet.
   * @param encoding
   *            the character encoding used to translate source bytes to characters. Some loaders may not have access
   *            to the byte representation of the template stream, and instead directly obtain a character stream.
   *            These loaders should ignore the encoding parameter.
   *
   * @return A {@link Reader} representing the template character stream; not {@code null}. It's the responsibility of
   *         the caller (which is {@link TemplateCache} usually) to {@code close()} it. The {@link Reader} is not
   *         required to work after the {@code templateSource} was closed ({@link #closeTemplateSource(Object)}).
   *
   * @throws IOException
   *             if an I/O error occurs while accessing the stream.
   */
  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException {
    if (templateSource == null) {
      throw new IllegalArgumentException("templateSource cannot be null");
    }
    if (templateSource instanceof Report report) {
      return new StringReader(report.getTemplate());
    }
    throw new IllegalArgumentException("templateSource is not a Report but a " + templateSource.getClass());
  }

  /**
   * Closes the template source, releasing any resources held that are only required for reading the template and/or
   * its metadata. This is the last method that is called by the {@link TemplateCache} for a template source, except
   * that {@link Object#equals(Object)} might be called later too. {@link TemplateCache} ensures that this method will
   * be called on every object that is returned from {@link #findTemplateSource(String)}.
   *
   * @param templateSource
   *            the template source that should be closed.
   */
  @Override
  public void closeTemplateSource(Object templateSource) throws IOException {
    // Nothing to do here
  }
}
