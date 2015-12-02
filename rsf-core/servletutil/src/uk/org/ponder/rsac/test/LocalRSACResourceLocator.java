/*
 * Created on Dec 25, 2006
 */
package uk.org.ponder.rsac.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import uk.org.ponder.rsac.RSACResourceLocator;

public class LocalRSACResourceLocator implements ApplicationContextAware, RSACResourceLocator, BeanFactoryPostProcessor, BeanDefinitionRegistryPostProcessor {
  private ApplicationContext applicationContext;
  private String[] configLocations;

  public void setConfigLocations(String[] configLocations) {
    this.configLocations = configLocations;
  }

  public ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  public String[] getConfigLocations() {
    return configLocations;
  }

  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	  
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    BeanDefinition definition = new RootBeanDefinition(RSACResourceLocator.class);
    registry.registerBeanDefinition("RSACResourceLocator", definition);
  }

}
