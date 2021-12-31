package com.agiletec.apsadmin.system.dispatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.views.xslt.AdapterFactory;
import org.apache.struts2.views.xslt.ServletURIResolver;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Classe definita per risolvere vulnerabilit&agrave;
 * https://cwiki.apache.org/confluence/display/WW/S2-031. Mai testata a livello
 * di codice sul portale (non avendo result type xslt nelle action).
 */
public class XSLTResult
  implements Result
{
  private static final long serialVersionUID = 6424691441777176763L;
  private static final Logger LOG = LoggerFactory.getLogger(XSLTResult.class);
  public static final String DEFAULT_PARAM = "stylesheetLocation";
  private static final Map<String, Templates> templatesCache = new HashMap();
  protected boolean noCache;
  private String stylesheetLocation;
  private String matchingPattern;
  private String excludingPattern;
  private String exposedValue;
  private int status = 200;
  private String encoding = "UTF-8";
  private boolean parse;
  private AdapterFactory adapterFactory;
  
  public XSLTResult() {}
  
  public XSLTResult(String stylesheetLocation)
  {
    this();
    setStylesheetLocation(stylesheetLocation);
  }
  
  @Inject("struts.xslt.nocache")
  public void setNoCache(String val)
  {
    this.noCache = "true".equals(val);
  }
  
  /**
   * @deprecated
   */
  public void setLocation(String location)
  {
    setStylesheetLocation(location);
  }
  
  public void setStylesheetLocation(String location)
  {
    if (location == null) {
      throw new IllegalArgumentException("Null location");
    }
    this.stylesheetLocation = location;
  }
  
  public String getStylesheetLocation()
  {
    return this.stylesheetLocation;
  }
  
  public String getExposedValue()
  {
    return this.exposedValue;
  }
  
  public void setExposedValue(String exposedValue)
  {
    this.exposedValue = exposedValue;
  }
  
  /**
   * @deprecated
   */
  public String getMatchingPattern()
  {
    return this.matchingPattern;
  }
  
  /**
   * @deprecated
   */
  public void setMatchingPattern(String matchingPattern)
  {
    this.matchingPattern = matchingPattern;
  }
  
  /**
   * @deprecated
   */
  public String getExcludingPattern()
  {
    return this.excludingPattern;
  }
  
  /**
   * @deprecated
   */
  public void setExcludingPattern(String excludingPattern)
  {
    this.excludingPattern = excludingPattern;
  }
  
  public String getStatus()
  {
    return String.valueOf(this.status);
  }
  
  public void setStatus(String status)
  {
    try
    {
      this.status = Integer.valueOf(status).intValue();
    }
    catch (NumberFormatException e)
    {
      throw new IllegalArgumentException("Status value not number " + e.getMessage(), e);
    }
  }
  
  public String getEncoding()
  {
    return this.encoding;
  }
  
  public void setEncoding(String encoding)
  {
    this.encoding = encoding;
  }
  
  public void setParse(boolean parse)
  {
    this.parse = parse;
  }
  
  public void execute(ActionInvocation invocation)
    throws Exception
  {
    long startTime = System.currentTimeMillis();
    String location = getStylesheetLocation();
    if (this.parse)
    {
      ValueStack stack = ActionContext.getContext().getValueStack();
      location = TextParseUtil.translateVariables(location, stack);
    }
    try
    {
      HttpServletResponse response = ServletActionContext.getResponse();
      response.setStatus(this.status);
      response.setCharacterEncoding(this.encoding);
      PrintWriter writer = response.getWriter();
      
      Templates templates = null;
      Transformer transformer;
      if (location != null)
      {
        templates = getTemplates(location);
        transformer = templates.newTransformer();
      }
      else
      {
        transformer = TransformerFactory.newInstance().newTransformer();
      }
      transformer.setURIResolver(getURIResolver());
      transformer.setErrorListener(new ErrorListener()
      {
        public void error(TransformerException exception)
          throws TransformerException
        {
          throw new StrutsException("Error transforming result", exception);
        }
        
        public void fatalError(TransformerException exception)
          throws TransformerException
        {
          throw new StrutsException("Fatal error transforming result", exception);
        }
        
        public void warning(TransformerException exception)
          throws TransformerException
        {
          if (XSLTResult.LOG.isWarnEnabled()) {
            XSLTResult.LOG.warn(exception.getMessage(), exception, new String[0]);
          }
        }
      });
      String mimeType;
      if (templates == null) {
        mimeType = "text/xml";
      } else {
        mimeType = templates.getOutputProperties().getProperty("media-type");
      }
      if (mimeType == null) {
        mimeType = "text/html";
      }
      response.setContentType(mimeType);
      
      Object result = invocation.getAction();
      if (this.exposedValue != null)
      {
        ValueStack stack = invocation.getStack();
        result = stack.findValue(this.exposedValue);
      }
      Source xmlSource = getDOMSourceForStack(result);
      if (LOG.isDebugEnabled()) {
        LOG.debug("xmlSource = " + xmlSource, new String[0]);
      }
      transformer.transform(xmlSource, new StreamResult(writer));
      
      writer.flush();
      if (LOG.isDebugEnabled()) {
        LOG.debug("Time:" + (System.currentTimeMillis() - startTime) + "ms", new String[0]);
      }
    }
    catch (Exception e)
    {
      if (LOG.isErrorEnabled()) {
        LOG.error("Unable to render XSLT Template, '#0'", e, new String[] { location });
      }
      throw e;
    }
  }
  
  protected AdapterFactory getAdapterFactory()
  {
    if (this.adapterFactory == null) {
      this.adapterFactory = new AdapterFactory();
    }
    return this.adapterFactory;
  }
  
  protected void setAdapterFactory(AdapterFactory adapterFactory)
  {
    this.adapterFactory = adapterFactory;
  }
  
  protected URIResolver getURIResolver()
  {
		return new ServletURIResolver(ServletActionContext.getServletContext());
  }
  
  protected Templates getTemplates(String path)
    throws TransformerException, IOException
  {
    if (path == null) {
      throw new TransformerException("Stylesheet path is null");
    }
    Templates templates = (Templates)templatesCache.get(path);
    if ((this.noCache) || (templates == null)) {
      synchronized (templatesCache)
      {
        URL resource = ServletActionContext.getServletContext().getResource(path);
        if (resource == null) {
          throw new TransformerException("Stylesheet " + path + " not found in resources.");
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("Preparing XSLT stylesheet templates: " + path, new String[0]);
        }
        TransformerFactory factory = TransformerFactory.newInstance();
        factory.setURIResolver(getURIResolver());
        templates = factory.newTemplates(new StreamSource(resource.openStream()));
        templatesCache.put(path, templates);
      }
    }
    return templates;
  }
  
  protected Source getDOMSourceForStack(Object value)
    throws IllegalAccessException, InstantiationException
  {
    return new DOMSource(getAdapterFactory().adaptDocument("result", value));
  }
}
