/*
 * Created on 5 Jun 2007
 */
package uk.org.ponder.rsac.support;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;

import uk.org.ponder.beanutil.BeanLocator;

/**
 * Adapts the per-request RSAC container to a standard Spring BeanFactory,
 * primarily for the purpose of beans which report themselves BeanFactoryAware.
 * 
 * Note that any bean requiring ApplicationContextAware will actually receive
 * the main application-scope ApplicationContext rather than a request-scope
 * container.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class RSACBeanFactory implements BeanFactory {
  private BeanLocator locator;
  private RSACBeanLocatorImpl rsacbl;

  public RSACBeanFactory(RSACBeanLocatorImpl rsacbl, BeanLocator bl) {
    this.locator = bl;
    this.rsacbl = rsacbl;
  }

  public boolean containsBean(String name) {
    return rsacbl.getBlankContext().containsBean(name);
  }

  public String[] getAliases(String name) throws NoSuchBeanDefinitionException {
    // Aliases are not supported in RSAC
    return null;
  }

  public Object getBean(String name) throws BeansException {
    return locator.locateBean(name);
  }
  
  // This method appeared in Spring 3.0
  public Object getBean(Class requiredType) throws BeansException {
    throw new NoSuchBeanDefinitionException("Unsupported method getBean(Class) for RSACBeanFactory");
  }

  public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    Object bean = locator.locateBean(name);
    if (requiredType != null && !requiredType.isAssignableFrom(bean.getClass())) {
      throw new BeanNotOfRequiredTypeException(name, requiredType, bean
          .getClass());
    }
    return (T) bean;
  }

  // This peculiar method appeared in Spring 2.0
  @Override
  public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
    return isTypeMatch(name, ResolvableType.forRawClass(typeToMatch));
  }

  @Override
  public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
    Object bean = getBean(name);
    return typeToMatch.isAssignableFrom(bean.getClass());
  }

  @Override
  public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
    return getType(name, true);
  }

  @Override
  public Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException {
    Class staticclazz = rsacbl.getBeanClass(name);
    if (staticclazz == null) {
      Object bean = getBean(name);
      return bean.getClass();
    } else
      return staticclazz;
  }

  public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    RSACBeanInfo rbi = (RSACBeanInfo) rsacbl.getRBIMap().get(name);
    if (rbi == null) {
      throw new NoSuchBeanDefinitionException(name, "No such bean");
    }
    else
      return rbi.issingleton;
  }

  public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
    return !isSingleton(name);
  }

  // This method was introduced in Spring 2.5
  public Object getBean(String name, Object... args) throws BeansException {
    if (args == null) {
      return getBean(name);
    }
    RSACBeanInfo rbi = (RSACBeanInfo) rsacbl.getRBIMap().get(name);
    if (rbi.issingleton) {
      throw new BeanDefinitionStoreException("Bean with name " + name + 
          " is a singleton and may not accept arguments");
    }
    else {
      throw new UnsupportedOperationException(
          "RSAC does not support argument prototyping in this release");
    }
  }

  @Override
  // this method appeared in spring 4.1
  public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
	throw new NoSuchBeanDefinitionException("Unsupported method getBean(Class) for RSACBeanFactory");
  }

  @Override
  public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
    return getBeanProvider(ResolvableType.forRawClass(requiredType));
  }

  @Override
  // this method appear in spring 5
  public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
    throw new NoSuchBeanDefinitionException("Unsupported method getBeanProvider(ResolvableType) for RSACBeanFactory");
  }

}
