/*
 * Created on 22 Jun 2007
 */
package uk.org.ponder.rsac.test;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.rsac.RSACBeanLocator;

/** A base class for deriving test fixtures which interact with an RSAC request cycle * */

public abstract class AbstractRSACTests {

  protected ConfigurableApplicationContext applicationContext;
  private RSACBeanLocator rsacbl;
  protected String[] configLocations = new String[] {};
  protected String[] requestConfigLocations = new String[] {};

  protected String[] getRequestConfigLocations() {
    return requestConfigLocations;
  }

  protected String[] getConfigLocations() {
    return configLocations;
  }

  public void contributeConfigLocations(String[] configLocations) {
    this.configLocations = (String[]) ArrayUtil.concat(this.configLocations,
        configLocations);
  }

  public void contributeRequestConfigLocations(String[] requestConfigLocations) {
    this.requestConfigLocations = (String[]) ArrayUtil.concat(
        this.requestConfigLocations, requestConfigLocations);
  }

  public void contributeConfigLocation(String configLocation) {
    this.configLocations = (String[]) ArrayUtil.append(this.configLocations,
        configLocation);
  }

  public void contributeRequestConfigLocation(String requestConfigLocation) {
    this.requestConfigLocations = (String[]) ArrayUtil.append(
        this.requestConfigLocations, requestConfigLocation);
  }

  private static boolean loggingInited = false;

  public AbstractRSACTests() {
    if (!loggingInited) {
      initLogging();
      loggingInited = true;
    }
  }

  protected void initLogging() {
    // These are being hardcoded due to various classpath issues with Maven 2 Testing
    // String log4jprops = "uk/org/ponder/rsac/test/log4j.test.properties";
    // URL url = this.getClass().getClassLoader().getResource(log4jprops);
    // PropertyConfigurator.configure(url);

    Properties props = new Properties();
    props.put("log4j.rootCategory", "warn");
    props.put("log4j.rootLogger", "warn, stdout");
    props.put("log4j.logger.org.springframework", "warn");
    props.put("log4j.logger.PonderUtilCore", "info");
    props.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
    props.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
    props.put("log4j.appender.stdout.layout.ConversionPattern", "%d %p (%F:%L) - <%m>%n");

    PropertyConfigurator.configure(props);
  }

  /**
   * Override this method to determine whether this test should consist of a single RSAC
   * cycle - if it returns <code>true</code>, the RSAC will be started and stopped
   * during setUp() and tearDown().
   */

  protected boolean isSingleShot() {
    return true;
  }

  protected ConfigurableApplicationContext loadContextLocations(String[] locations) {
    final LocalRSACResourceLocator resourceLocator = new LocalRSACResourceLocator();
    resourceLocator.setConfigLocations(getRequestConfigLocations());

    final GenericApplicationContext context = new GenericApplicationContext();
    context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {
		
		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	        resourceLocator.setApplicationContext(context);
	        beanFactory.registerSingleton("RSACResourceLocator", resourceLocator);
		}
	});
    
    new XmlBeanDefinitionReader(context).loadBeanDefinitions(locations);
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();

    return context;
  }

  public RSACBeanLocator getRSACBeanLocator() {
    return rsacbl;
  }

  /**
   * Locates a particular request bean, assuming that a request is currently active. That
   * is, this is either a singleshot test, or that getRequestLauncher() has been invoked
   * without disposing the current RSAC.
   */
  public Object locateRequestBean(String name) {
    return getRSACBeanLocator().getBeanLocator().locateBean(name);
  }

  protected void onSetUp() throws Exception {
    rsacbl = (RSACBeanLocator) applicationContext.getBean("RSACBeanLocator");
    if (isSingleShot()) {
      getRSACBeanLocator().startRequest();
    }
  }

  protected void onTearDown() throws Exception {
    if (isSingleShot()) {
      if (rsacbl != null) {
        rsacbl.endRequest();
      }
    }
  }
  
  @Before
  public final void setUp() throws Exception {
	  if (applicationContext == null) {
		  applicationContext = loadContextLocations(getConfigLocations());
	  }
	  onSetUp();
  }
  
  @After
  public final void tearDown() throws Exception {
	  onTearDown();
	  if (applicationContext != null) {
		  applicationContext.close();
		  applicationContext = null;
	  }
  }
}
