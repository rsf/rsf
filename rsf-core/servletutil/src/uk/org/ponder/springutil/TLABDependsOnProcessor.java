package uk.org.ponder.springutil;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class TLABDependsOnProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinition loaderDef = beanFactory.getBeanDefinition("TLABPostProcessorLoader");
		String[] tlabs = beanFactory.getBeanNamesForType(TargetListAggregatingBean.class, false, false);
		loaderDef.setDependsOn(tlabs);
	}
}
