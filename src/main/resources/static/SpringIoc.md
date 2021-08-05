# Spring容器创建源码分析

## Spring容器的refresh()

```java
	public void refresh() throws BeansException, IllegalStateException {
        synchronized(this.startupShutdownMonitor) {
            this.prepareRefresh();
            ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
            this.prepareBeanFactory(beanFactory);

            try {
                this.postProcessBeanFactory(beanFactory);
                this.invokeBeanFactoryPostProcessors(beanFactory);
                this.registerBeanPostProcessors(beanFactory);
                this.initMessageSource();
                this.initApplicationEventMulticaster();
                this.onRefresh();
                this.registerListeners();
                this.finishBeanFactoryInitialization(beanFactory);
                this.finishRefresh();
            } catch (BeansException var9) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + var9);
                }

                this.destroyBeans();
                this.cancelRefresh(var9);
                throw var9;
            } finally {
                this.resetCommonCaches();
            }

        }
    }
```

### 1.prepareRefresh()刷新前的预处理

```java
	protected void prepareRefresh() {
        this.startupDate = System.currentTimeMillis();
        this.closed.set(false);
        this.active.set(true);
        if (this.logger.isDebugEnabled()) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Refreshing " + this);
            } else {
                this.logger.debug("Refreshing " + this.getDisplayName());
            }
        }

        this.initPropertySources();
        this.getEnvironment().validateRequiredProperties();
        if (this.earlyApplicationListeners == null) {
            this.earlyApplicationListeners = new LinkedHashSet(this.applicationListeners);
        } else {
            this.applicationListeners.clear();
            this.applicationListeners.addAll(this.earlyApplicationListeners);
        }

        this.earlyApplicationEvents = new LinkedHashSet();
    }
```

​		1）initPropertySources()初始化一些属性设置；子类自定义个性化的属性设置方法

​		2）getEnvironment().validateRequiredProperties();检验属性的合法性等

​		3）earlyApplicationEvents = new LinkedHashSet<Application>();保存容器中的一些早期事件

### 2.obtainFreshBeanFactory();获取BeanFactory

```java
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
        this.refreshBeanFactory();
        return this.getBeanFactory();
}
```

​		1）refreshBeanFactory();刷新BeanFactory

```java
	protected final void refreshBeanFactory() throws BeansException {
        if (this.hasBeanFactory()) {
            this.destroyBeans();
            this.closeBeanFactory();
        }
        try {
            DefaultListableBeanFactory beanFactory = this.createBeanFactory();
            beanFactory.setSerializationId(this.getId());
            this.customizeBeanFactory(beanFactory);
            this.loadBeanDefinitions(beanFactory);
            this.beanFactory = beanFactory;
        } catch (IOException var2) {
            throw new ApplicationContextException("I/O error parsing bean definition source for " + this.getDisplayName(), var2);
        }
    }
```

​				创建了一个this.beanFactory = new DefaultListableBeanFactory();并设置序列化id

​		2）getBeanFactory();

```java
	public final ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }
```

​				返回GenericApplicationContext创建的BeanFactory[DefaultListableBeanFactory]对象

### 3.prepareBeanFactory(beanFactory);BeanFactory的预准备工作(对BeanFactory进行一些设置)

```java
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.setBeanClassLoader(this.getClassLoader());
        beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
        beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, this.getEnvironment()));
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
        beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
        beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
        beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
        beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
        beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
        beanFactory.registerResolvableDependency(ResourceLoader.class, this);
        beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
        beanFactory.registerResolvableDependency(ApplicationContext.class, this);
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
        if (beanFactory.containsBean("loadTimeWeaver")) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }

        if (!beanFactory.containsLocalBean("environment")) {
            beanFactory.registerSingleton("environment", this.getEnvironment());
        }

        if (!beanFactory.containsLocalBean("systemProperties")) {
            beanFactory.registerSingleton("systemProperties", this.getEnvironment().getSystemProperties());
        }

        if (!beanFactory.containsLocalBean("systemEnvironment")) {
            beanFactory.registerSingleton("systemEnvironment", this.getEnvironment().getSystemEnvironment());
        }

    }
```

​		1）设置 BeanFactory 的类加载器(BeanClassLoader)，支持表达式解析器(BeanExpressionResolver)，属性编辑注册器(PropertyEditorRegistrar)

​		2）添加部分前置处理器(BeanPostProcessor[ApplicationContextAwareProcessor])

​		3）设置忽略的自动装配的接口：EnvironmentAware、EmbeddedValueResolver、ResourceLoaderAware、ApplicationEventPublisherAware、MessageSourceAware、ApplicationContextAware

​		4）注册可以解析的自动装配，可以直接在任何组件中自动注入：

​			BeanFactory、ResouceLoader、ApplicationEventPublisher、ApplicationContext

​		5）添加部分前置处理器(BeanPostProcessor[ApplicationListenerDetector])

​		6）添加编译时的AspectJ

​		7）给BeanFactory中注册一些能用的组件：

​			**loadTimeWeaver**[ContextTypeMatchClassLoader]

​			**environment**[ConfigurableEnvironment]

​			**systemProperties**[Map<String, Object>]

​			**systemEnvironment**[Map<String, Object>]

#### 4.postProcessBeanFactory(beanFactory);BeanFactory准备工作完成后的后置处理工作

![image-20210802112212388](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20210802112212388.png)

​		1）子类通过重写此方法来在BeanFactory创建并预准备完成以后做进一步的设置，如 AbstractRefreshableWebApplicationContext

```java
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(this.servletContext, this.servletConfig));
        beanFactory.ignoreDependencyInterface(ServletContextAware.class);
        beanFactory.ignoreDependencyInterface(ServletConfigAware.class);
        WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, this.servletContext);
        WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, this.servletContext, this.servletConfig);
    }
```

**------------------------------------------以上是BeanFactory的创建以及预准备工作------------------------------------------**



### 5.invokeBeanFactoryPostProcessors(beanFactory);执行BeanFactoryPostProcessor；

```java
	protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        //封装了一层PostProcessorRegistrationDelegate
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, this.getBeanFactoryPostProcessors());
        if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean("loadTimeWeaver")) {
            beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
            beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
        }
    }
```

​		BeanFactoryPostProcessor：BeanFactory的后置处理器。在BeanFactory标准初始化之后执行

​		两个接口:BeanFactoryPostProcessor 和 BeanDefinitionRegistryPostProcessor

```java
	public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
        Set<String> processedBeans = new HashSet();
        ArrayList regularPostProcessors;
        ArrayList registryProcessors;
        int var9;
        ArrayList currentRegistryProcessors;
        String[] postProcessorNames;
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
            regularPostProcessors = new ArrayList();
            registryProcessors = new ArrayList();
            Iterator var6 = beanFactoryPostProcessors.iterator();

            //获取所有的BeanDefinitionRegistryPostProcessor
            while(var6.hasNext()) {
                BeanFactoryPostProcessor postProcessor = (BeanFactoryPostProcessor)var6.next();
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor)postProcessor;
                    registryProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryProcessors.add(registryProcessor);
                } else {
                    regularPostProcessors.add(postProcessor);
                }
            }

            currentRegistryProcessors = new ArrayList();
            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            String[] var16 = postProcessorNames;
            var9 = postProcessorNames.length;
            
            int var10;
            String ppName;
            //优先执行实现了PriorityOrdered接口的BeanDefinitionRegistryPostProcessor
            for(var10 = 0; var10 < var9; ++var10) {
                ppName = var16[var10];
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }

            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
            currentRegistryProcessors.clear();
            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            var16 = postProcessorNames;
            var9 = postProcessorNames.length;

            //再执行实现了Ordered接口的BeanDefinitionRegistryPostProcessor
            for(var10 = 0; var10 < var9; ++var10) {
                ppName = var16[var10];
                if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }
			//最后执行没有实现任何优先级或者是顺序接口的BeanDefinitionRegistryPostProcessor
            sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
            currentRegistryProcessors.clear();
            boolean reiterate = true;

            while(reiterate) {
                reiterate = false;
                postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                String[] var19 = postProcessorNames;
                var10 = postProcessorNames.length;

                for(int var26 = 0; var26 < var10; ++var26) {
                    String ppName = var19[var26];
                    if (!processedBeans.contains(ppName)) {
                        currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                        processedBeans.add(ppName);
                        reiterate = true;
                    }
                }

                sortPostProcessors(currentRegistryProcessors, beanFactory);
                registryProcessors.addAll(currentRegistryProcessors);
                invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
                currentRegistryProcessors.clear();
            }

            invokeBeanFactoryPostProcessors((Collection)registryProcessors, (ConfigurableListableBeanFactory)beanFactory);
            invokeBeanFactoryPostProcessors((Collection)regularPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        } else {
            invokeBeanFactoryPostProcessors((Collection)beanFactoryPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        }

        //获取所有的BeanFactoryPostProcessor
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
        regularPostProcessors = new ArrayList();
        registryProcessors = new ArrayList();
        currentRegistryProcessors = new ArrayList();
        postProcessorNames = postProcessorNames;
        int var20 = postProcessorNames.length;

        String ppName;
        for(var9 = 0; var9 < var20; ++var9) {
            ppName = postProcessorNames[var9];
            if (!processedBeans.contains(ppName)) {
                //优先执行实现了PriorityOrdered接口的BeanFactoryPostProcessor
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    regularPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
                //再执行实现了Ordered接口的BeanFactoryPostProcessor
                } else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                    registryProcessors.add(ppName);
                } else {
                    //最后执行没有实现任何优先级或者是顺序接口的BeanFactoryPostProcessor
                    currentRegistryProcessors.add(ppName);
                }
            }
        }

        sortPostProcessors(regularPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors((Collection)regularPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList(registryProcessors.size());
        Iterator var21 = registryProcessors.iterator();

        while(var21.hasNext()) {
            String postProcessorName = (String)var21.next();
            orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
        }

        sortPostProcessors(orderedPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors((Collection)orderedPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList(currentRegistryProcessors.size());
        Iterator var24 = currentRegistryProcessors.iterator();

        while(var24.hasNext()) {
            ppName = (String)var24.next();
            nonOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
        }

        invokeBeanFactoryPostProcessors((Collection)nonOrderedPostProcessors, (ConfigurableListableBeanFactory)beanFactory);
        beanFactory.clearMetadataCache();
    }
```

​	**执行BeanFactoryPostProcessor 的方法**：

​		**先执行BeanDefinitionRegistryPostProcessor**

​			1）获取所有的BeanDefinitionRegistryPostProcessor；

​			2）按优先级排序，优先执行实现了PriorityOrdered接口的BeanDefinitionRegistryPostProcessor、	postProcessor.postProcessBeanDefinitionRegistry(registry)

​			3）再执行实现了Ordered接口的BeanDefinitionRegistryPostProcessor、postProcessor.postProcessBeanDefinitionRegistry(registry)

​			4）最后执行没有实现任何优先级或者是顺序接口的BeanDefinitionRegistryPostProcessor、postProcessor.postProcessBeanDefinitionRegistry(registry)

​		**再执行BeanFactoryPostProcessor 的方法**

​			1）获取所有的BeanFactoryPostProcessor

​			2）按优先级排序，优先执行实现了PriorityOrdered接口的BeanFactoryPostProcessor、	postProcessor.postProcessBeanFactory()

​			3）再执行实现了Ordered接口的BeanFactoryPostProcessor、postProcessor.postProcessBeanFactory()

​			4）最后执行没有实现任何优先级或者是顺序接口的BeanFactoryPostProcessor、postProcessor.postProcessBeanFactory()

### 6.registerBeanPostProcessors(beanFactory);注册BeanPostProcessor(Bean的后置处理器)[intercept bean creation]

```java
	public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
        int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
        beanFactory.addBeanPostProcessor(new PostProcessorRegistrationDelegate.BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
        List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList();
        List<BeanPostProcessor> internalPostProcessors = new ArrayList();
        List<String> orderedPostProcessorNames = new ArrayList();
        List<String> nonOrderedPostProcessorNames = new ArrayList();
        String[] var8 = postProcessorNames;
        int var9 = postProcessorNames.length;

        String ppName;
        BeanPostProcessor pp;
        for(int var10 = 0; var10 < var9; ++var10) {
            ppName = var8[var10];
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                pp = (BeanPostProcessor)beanFactory.getBean(ppName, BeanPostProcessor.class);
                priorityOrderedPostProcessors.add(pp);
                if (pp instanceof MergedBeanDefinitionPostProcessor) {
                    internalPostProcessors.add(pp);
                }
            } else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
            } else {
                nonOrderedPostProcessorNames.add(ppName);
            }
        }

        sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, (List)priorityOrderedPostProcessors);
        List<BeanPostProcessor> orderedPostProcessors = new ArrayList(orderedPostProcessorNames.size());
        Iterator var14 = orderedPostProcessorNames.iterator();

        while(var14.hasNext()) {
            String ppName = (String)var14.next();
            BeanPostProcessor pp = (BeanPostProcessor)beanFactory.getBean(ppName, BeanPostProcessor.class);
            orderedPostProcessors.add(pp);
            if (pp instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp);
            }
        }

        sortPostProcessors(orderedPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, (List)orderedPostProcessors);
        List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList(nonOrderedPostProcessorNames.size());
        Iterator var17 = nonOrderedPostProcessorNames.iterator();

        while(var17.hasNext()) {
            ppName = (String)var17.next();
            pp = (BeanPostProcessor)beanFactory.getBean(ppName, BeanPostProcessor.class);
            nonOrderedPostProcessors.add(pp);
            if (pp instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp);
            }
        }

        registerBeanPostProcessors(beanFactory, (List)nonOrderedPostProcessors);
        sortPostProcessors(internalPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, (List)internalPostProcessors);
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
    }
```

​	不同接口类型的 BeanPostProcessor 在Bean创建前后的执行实际是不一样的

​	**BeanPostProcessor**

​	**DestructionAwareBeanPostProcessor**

​	**InstantiationAwareBeanPostProcessor**

​	**SmartInstantiationAwareBeanPostProcessor**

​	**MergedBeanDefinitionPostProcessor**[internalPostProcessors]

​			1）获取所有的BeanPostProcessor；

​				后置处理器都默认可以通过PriorityOrdered、Ordered接口指定优先级

​			2）先注册PriorityOrdered优先级接口的BeanPostProcessor

​				把每一个BeanPostProcessor添加到BeanFactory中

​				beanFactory.addBeanPostProcessor(postProcessor);

​			3）再注册Ordered接口的

​			4）然后注册没有实现任何优先级排序的接口

​			5）最终注册MergedBeanDefinitionPostProcessor

​			6）注册一个ApplicationListenerDetector用来在Bean创建完成后检查是否为ApplicationListener

​				如果是：this.applicationContext.addApplicationListener((ApplicationListener)bean);

### 7.initMessageSource() 初始化MessageSource组件(国际化功能，消息绑定，消息解析)

```java
	protected void initMessageSource() {
        //获取BeanFactory
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        //看容器中是否有id为messageSource且类型为MessageSource的组件
        if (beanFactory.containsLocalBean("messageSource")) {
            //有则赋值给messageSource
            this.messageSource = (MessageSource)beanFactory.getBean("messageSource", MessageSource.class);
            if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
                HierarchicalMessageSource hms = (HierarchicalMessageSource)this.messageSource;
                if (hms.getParentMessageSource() == null) {
                    hms.setParentMessageSource(this.getInternalParentMessageSource());
                }
            }

            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Using MessageSource [" + this.messageSource + "]");
            }
        } else {
            //反之自己创建一个DelegatingMessageSource
            DelegatingMessageSource dms = new DelegatingMessageSource();
            //把创建好的MessageSource注册在容器中，以后获取国际化配置文件时，可自动注入MessageSource
            dms.setParentMessageSource(this.getInternalParentMessageSource());
            this.messageSource = dms;
            beanFactory.registerSingleton("messageSource", this.messageSource);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No 'messageSource' bean, using [" + this.messageSource + "]");
            }
        }

    }
```

​			1）获取BeanFactory

​			2）看容器中是否有id为messageSource且类型为MessageSource的组件，

​				有则赋值给messageSource，反之自己创建一个DelegatingMessageSource

​				MessageSource，取出国际化配置文件中的某个key值，能够按照区域信息获取

​			3）把创建好的MessageSource注册在容器中，以后获取国际化配置文件时，可自动注入MessageSource

​				beanFactory.registerSingleton("messageSource", this.messageSource);

​				String getMessage(String var1, @Nullable Object[] var2, Locale var3);

### 8.initApplicationEventMulticaster();初始化事件派发器

```java
	protected void initApplicationEventMulticaster() {
        //获取BeanFactory
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        //从BeanFactory中获取applicationEventMulticaster的ApplicationEventMulticaster
        if (beanFactory.containsLocalBean("applicationEventMulticaster")) {
            this.applicationEventMulticaster = (ApplicationEventMulticaster)beanFactory.getBean("applicationEventMulticaster", ApplicationEventMulticaster.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
            }
        } else {
            //如果上一步没有配置，则创建一个SimpleApplicationEventMulticaster
            this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
            //将创建的ApplicationEventMulticaster添加到BeanFactory中，以后其他组件直接注入
            beanFactory.registerSingleton("applicationEventMulticaster", this.applicationEventMulticaster);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No 'applicationEventMulticaster' bean, using [" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
            }
        }

    }
```



​			1）获取BeanFactory

​			2）从BeanFactory中获取applicationEventMulticaster的ApplicationEventMulticaster

​			3）如果上一步没有配置，则创建一个SimpleApplicationEventMulticaster

​			4）将创建的ApplicationEventMulticaster添加到BeanFactory中，以后其他组件直接注入

### 9.onRefresh();留给子容器(子类)

```java
	protected void onRefresh() throws BeansException {
    }
```

​			1）子类重写此方法，在容器刷新的适合可以自定义逻辑

### 10.registerListener();给容器中将所有项目里面的ApplicationListener注册进来

```java
	protected void registerListeners() {
        //通过迭代器从容器中拿到所有的ApplicationListener
        Iterator var1 = this.getApplicationListeners().iterator();

        while(var1.hasNext()) {
            ApplicationListener<?> listener = (ApplicationListener)var1.next();
            this.getApplicationEventMulticaster().addApplicationListener(listener);
        }

        String[] listenerBeanNames = this.getBeanNamesForType(ApplicationListener.class, true, false);
        String[] var7 = listenerBeanNames;
        int var3 = listenerBeanNames.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String listenerBeanName = var7[var4];
          //将每个监听器添加到事件派发器中
      this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
        }

        Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
        this.earlyApplicationEvents = null;
        //派发之前步骤产生的事件
        if (!CollectionUtils.isEmpty(earlyEventsToProcess)) {
            Iterator var9 = earlyEventsToProcess.iterator();

            while(var9.hasNext()) {
                ApplicationEvent earlyEvent = (ApplicationEvent)var9.next();
                this.getApplicationEventMulticaster().multicastEvent(earlyEvent);
            }
        }

    }
```

​			1）从容器中拿到所有的ApplicationListener

​			2）将每个监听器添加到事件派发器中

​				this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);

​			3）派发之前步骤产生的事件

### 11.this.finishBeanFactoryInitialization(beanFactory);初始化所有剩下的单实例bean

```java
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory.containsBean("conversionService") && beanFactory.isTypeMatch("conversionService", ConversionService.class)) {
            beanFactory.setConversionService((ConversionService)beanFactory.getBean("conversionService", ConversionService.class));
        }

        if (!beanFactory.hasEmbeddedValueResolver()) {
            beanFactory.addEmbeddedValueResolver((strVal) -> {
                return this.getEnvironment().resolvePlaceholders(strVal);
            });
        }

        String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
        String[] var3 = weaverAwareNames;
        int var4 = weaverAwareNames.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String weaverAwareName = var3[var5];
            this.getBean(weaverAwareName);
        }

        beanFactory.setTempClassLoader((ClassLoader)null);
        beanFactory.freezeConfiguration();
        beanFactory.preInstantiateSingletons();
    }
```

​			1）beanFactory.preInstantiateSingletons();初始化后剩下的单实例bean

```java
	public void preInstantiateSingletons() throws BeansException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Pre-instantiating singletons in " + this);
        }
		//获取容器中所有Bean，依次进行初始化和创建对象
        List<String> beanNames = new ArrayList(this.beanDefinitionNames);
        Iterator var2 = beanNames.iterator();

        while(true) {
            String beanName;
            Object bean;
            //判断是否是FactoryBean：是否是实现FactoryBean接口的Bean
            do {
                while(true) {
                    //获取Bean的定义信息
                    RootBeanDefinition bd;
                    //Bean不是抽象的，是单实例的，是懒加载的
                    do {
                        do {
                            do {
                                if (!var2.hasNext()) {
                                    var2 = beanNames.iterator();

                                    while(var2.hasNext()) {
                                        beanName = (String)var2.next();
                                        Object singletonInstance = this.getSingleton(beanName);
                                        if (singletonInstance instanceof SmartInitializingSingleton) {
                                            SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton)singletonInstance;
                                            if (System.getSecurityManager() != null) {
                                                AccessController.doPrivileged(() -> {
                                                    smartSingleton.afterSingletonsInstantiated();
                                                    return null;
                                                }, this.getAccessControlContext());
                                            } else {
                                                smartSingleton.afterSingletonsInstantiated();
                                            }
                                        }
                                    }

                                    return;
                                }

                                beanName = (String)var2.next();
                                bd = this.getMergedLocalBeanDefinition(beanName);
                            } while(bd.isAbstract());
                        } while(!bd.isSingleton());
                    } while(bd.isLazyInit());

                    if (this.isFactoryBean(beanName)) {
                        bean = this.getBean("&" + beanName);
                        break;
                    }
					//不是FactoryBean，则利用getBean(beanName)创建对象
                    this.getBean(beanName);
                }
            } while(!(bean instanceof FactoryBean));

            FactoryBean<?> factory = (FactoryBean)bean;
            boolean isEagerInit;
            if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                SmartFactoryBean var10000 = (SmartFactoryBean)factory;
                ((SmartFactoryBean)factory).getClass();
                isEagerInit = (Boolean)AccessController.doPrivileged(var10000::isEagerInit, this.getAccessControlContext());
            } else {
                isEagerInit = factory instanceof SmartFactoryBean && ((SmartFactoryBean)factory).isEagerInit();
            }

            if (isEagerInit) {
                this.getBean(beanName);
            }
        }
    }
```

​				1】获取容器中所有Bean，依次进行初始化和创建对象

​				2】获取Bean的定义信息，RootBeanDefinition

​				3】Bean不是抽象的，是单实例的，是懒加载的

​						①判断是否是FactoryBean：是否是实现FactoryBean接口的Bean

​						②不是FactoryBean，则利用getBean(beanName)创建对象

​								Ⅰ.getBean(beanName):ioc.getBean();

```java
	public Object getBean(String name) throws BeansException {
        return this.doGetBean(name, (Class)null, (Object[])null, false);
    }
```

​								Ⅱ.**doGetBean**(name, (Class)null, (Object[])null, false);

```java
	protected <T> T doGetBean(String name, @Nullable Class<T> requiredType, @Nullable Object[] args, boolean typeCheckOnly) throws BeansException {
        // 获取beanName
        String beanName = this.transformedBeanName(name);
        //先获取缓存中保存的单实例Bean
        Object sharedInstance = this.getSingleton(beanName);
        Object bean;
        if (sharedInstance != null && args == null) {
            if (this.logger.isTraceEnabled()) {
                if (this.isSingletonCurrentlyInCreation(beanName)) {
                    this.logger.trace("Returning eagerly cached instance of singleton bean '" + beanName + "' that is not fully initialized yet - a consequence of a circular reference");
                } else {
                    this.logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
                }
            }

            bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, (RootBeanDefinition)null);
        } else {
            //缓存中获取不到, 开始Bean的创建对象流程
            //标记当前bean已经被创建,防止多线程不安全情况
            if (this.isPrototypeCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(beanName);
            }

            //获取Bean的定义信息
            BeanFactory parentBeanFactory = this.getParentBeanFactory();
            if (parentBeanFactory != null && !this.containsBeanDefinition(beanName)) {
                String nameToLookup = this.originalBeanName(name);
                if (parentBeanFactory instanceof AbstractBeanFactory) {
                    return ((AbstractBeanFactory)parentBeanFactory).doGetBean(nameToLookup, requiredType, args, typeCheckOnly);
                }

                if (args != null) {
                    return parentBeanFactory.getBean(nameToLookup, args);
                }

                if (requiredType != null) {
                    return parentBeanFactory.getBean(nameToLookup, requiredType);
                }

                return parentBeanFactory.getBean(nameToLookup);
            }

            if (!typeCheckOnly) {
                this.markBeanAsCreated(beanName);
            }

            //获取当前Bean依赖的其他Bean;如果有按照getBean()把依赖的Bean先创建出来
            try {
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                this.checkMergedBeanDefinition(mbd, beanName, args);
                String[] dependsOn = mbd.getDependsOn();
                String[] var11;
                if (dependsOn != null) {
                    var11 = dependsOn;
                    int var12 = dependsOn.length;

                    for(int var13 = 0; var13 < var12; ++var13) {
                        String dep = var11[var13];
                        if (this.isDependent(beanName, dep)) {
                            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                        }

                        this.registerDependentBean(dep, beanName);

                        try {
                            this.getBean(dep);
                        } catch (NoSuchBeanDefinitionException var24) {
                            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "'" + beanName + "' depends on missing bean '" + dep + "'", var24);
                        }
                    }
                }

                //启动单实例Bean的创建流程
                if (mbd.isSingleton()) {
                    sharedInstance = this.getSingleton(beanName, () -> {
                        try {
                            return this.createBean(beanName, mbd, args);
                        } catch (BeansException var5) {
                            this.destroySingleton(beanName);
                            throw var5;
                        }
                    });
                    bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                } else if (mbd.isPrototype()) {
                    var11 = null;

                    Object prototypeInstance;
                    try {
                        this.beforePrototypeCreation(beanName);
                        prototypeInstance = this.createBean(beanName, mbd, args);
                    } finally {
                        this.afterPrototypeCreation(beanName);
                    }

                    bean = this.getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
                } else {
                    String scopeName = mbd.getScope();
                    if (!StringUtils.hasLength(scopeName)) {
                        throw new IllegalStateException("No scope name defined for bean ´" + beanName + "'");
                    }

                    Scope scope = (Scope)this.scopes.get(scopeName);
                    if (scope == null) {
                        throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
                    }

                    try {
                        Object scopedInstance = scope.get(beanName, () -> {
                            this.beforePrototypeCreation(beanName);

                            Object var4;
                            try {
                                var4 = this.createBean(beanName, mbd, args);
                            } finally {
                                this.afterPrototypeCreation(beanName);
                            }

                            return var4;
                        });
                        bean = this.getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
                    } catch (IllegalStateException var23) {
                        throw new BeanCreationException(beanName, "Scope '" + scopeName + "' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton", var23);
                    }
                }
            } catch (BeansException var26) {
                this.cleanupAfterBeanCreationFailure(beanName);
                throw var26;
            }
        }

        if (requiredType != null && !requiredType.isInstance(bean)) {
            try {
                T convertedBean = this.getTypeConverter().convertIfNecessary(bean, requiredType);
                if (convertedBean == null) {
                    throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
                } else {
                    return convertedBean;
                }
            } catch (TypeMismatchException var25) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Failed to convert bean '" + name + "' to required type '" + ClassUtils.getQualifiedName(requiredType) + "'", var25);
                }

                throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
            }
        } else {
            return bean;
        }
    }
```

​								Ⅲ.先获取缓存中保存的单实例Bean,如果能获取到说明此bean之前被创建过(所有创建过的bean都会被缓存起来)

```java
private final Map<String, Object> singletenObjects = new ConcurrentHashMap<>(256);
```

​								Ⅳ.缓存中获取不到, 开始Bean的创建对象流程

​								Ⅴ.标记当前bean已经被创建,防止多线程不安全情况

​								Ⅵ.获取Bean的定义信息

​								Ⅶ.获取当前Bean依赖的其他Bean;如果有按照getBean()把依赖的Bean先创建出来

​								Ⅷ.启动单实例Bean的创建流程:

```java
	protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Creating instance of bean '" + beanName + "'");
        }

        RootBeanDefinition mbdToUse = mbd;
        Class<?> resolvedClass = this.resolveBeanClass(mbd, beanName, new Class[0]);
        if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
            mbdToUse = new RootBeanDefinition(mbd);
            mbdToUse.setBeanClass(resolvedClass);
        }

        try {
            mbdToUse.prepareMethodOverrides();
        } catch (BeanDefinitionValidationException var9) {
            throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(), beanName, "Validation of method overrides failed", var9);
        }

        Object beanInstance;
        try {
            //拦截返回代理对象
            beanInstance = this.resolveBeforeInstantiation(beanName, mbdToUse);
            if (beanInstance != null) {
                return beanInstance;
            }
        } catch (Throwable var10) {
            throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "BeanPostProcessor before instantiation of bean failed", var10);
        }

        try {
            beanInstance = this.doCreateBean(beanName, mbdToUse, args);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Finished creating instance of bean '" + beanName + "'");
            }
			//返回新创建的bean
            return beanInstance;
        } catch (ImplicitlyAppearedSingletonException | BeanCreationException var7) {
            throw var7;
        } catch (Throwable var8) {
            throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", var8);
        }
    }
```

​									1. createBean(beanName, mbd, args);

​									2 . Object bean = resolveBeforeInstantiation(beanName, mbdToUse);

```java
	protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
        Object bean = null;
        if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
            if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
                Class<?> targetType = this.determineTargetType(beanName, mbd);
                if (targetType != null) {
                    bean = this.applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
                    if (bean != null) {
                        bean = this.applyBeanPostProcessorsAfterInitialization(bean, beanName);
                    }
                }
            }

            mbd.beforeInstantiationResolved = bean != null;
        }
        return bean;
    }
```

​									让BeanPostProcessor先拦截返回代理对象

​										[InstantiationAwareBeanPostProcessor]提前执行,先触发postProcessorBeforeInstantiation()方法,如果有返回值再触发postProcessorAfterInstantiation();

​									3. 如果前面的InstantiationAwareBeanPostProcessor没有返回代理对象 调用👇

​									4. Object beanInstance = doCreateBean(beanName, mbdToUse, args);

```java
	protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
        BeanWrapper instanceWrapper = null;
        //如果是单实例，则从缓存中移除该bean
        if (mbd.isSingleton()) {
            instanceWrapper = (BeanWrapper)this.factoryBeanInstanceCache.remove(beanName);
        }

        //创建Bean实例
        if (instanceWrapper == null) {
            instanceWrapper = this.createBeanInstance(beanName, mbd, args);
        }

        Object bean = instanceWrapper.getWrappedInstance();
        Class<?> beanType = instanceWrapper.getWrappedClass();
        if (beanType != NullBean.class) {
            mbd.resolvedTargetType = beanType;
        }

        synchronized(mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                try {
                    this.applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                } catch (Throwable var17) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Post-processing of merged bean definition failed", var17);
                }

                mbd.postProcessed = true;
            }
        }
        //单实例 && 允许循环引用 && 当前Bean创建时允许单实例
        boolean earlySingletonExposure = mbd.isSingleton() && this.allowCircularReferences && this.isSingletonCurrentlyInCreation(beanName);
        if (earlySingletonExposure) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");
            }

            this.addSingletonFactory(beanName, () -> {
                return this.getEarlyBeanReference(beanName, mbd, bean);
            });
        }

        Object exposedObject = bean;

        try {
            //Bean属性赋值
            this.populateBean(beanName, mbd, instanceWrapper);
            //Bean初始化
            exposedObject = this.initializeBean(beanName, exposedObject, mbd);
        } catch (Throwable var18) {
            if (var18 instanceof BeanCreationException && beanName.equals(((BeanCreationException)var18).getBeanName())) {
                throw (BeanCreationException)var18;
            }

            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", var18);
        }

        //单实例预处理
        if (earlySingletonExposure) {
            Object earlySingletonReference = this.getSingleton(beanName, false);
            if (earlySingletonReference != null) {
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
                } else if (!this.allowRawInjectionDespiteWrapping && this.hasDependentBean(beanName)) {
                    String[] dependentBeans = this.getDependentBeans(beanName);
                    Set<String> actualDependentBeans = new LinkedHashSet(dependentBeans.length);
                    String[] var12 = dependentBeans;
                    int var13 = dependentBeans.length;

                    for(int var14 = 0; var14 < var13; ++var14) {
                        String dependentBean = var12[var14];
                        if (!this.removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                            actualDependentBeans.add(dependentBean);
                        }
                    }

                    if (!actualDependentBeans.isEmpty()) {
                        throw new BeanCurrentlyInCreationException(beanName, "Bean with name '" + beanName + "' has been injected into other beans [" + StringUtils.collectionToCommaDelimitedString(actualDependentBeans) + "] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the bean. This is often the result of over-eager type matching - consider using 'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
                    }
                }
            }
        }

        try {
            this.registerDisposableBeanIfNecessary(beanName, bean, mbd);
            return exposedObject;
        } catch (BeanDefinitionValidationException var16) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", var16);
        }
    }
```

​											1)创建Bean实例:createBeanInstance(beanName, mbd, args);

​												利用工厂方法或对象的构造器创建除Bean实例

​											2)applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);

​												调用MergedBeanDefinitionPostProcessor的

​												bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName);

​											3)[Bean属性赋值]populateBean(beanName, mbd, beanWrapper);

```java
	protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
        if (bw == null) {
            if (mbd.hasPropertyValues()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
            }
        } else {
            if (!mbd.isSynthetic() && this.hasInstantiationAwareBeanPostProcessors()) {
                Iterator var4 = this.getBeanPostProcessors().iterator();

                while(var4.hasNext()) {
                    BeanPostProcessor bp = (BeanPostProcessor)var4.next();
                    if (bp instanceof InstantiationAwareBeanPostProcessor) {
                        InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                        if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                            return;
                        }
                    }
                }
            }

            PropertyValues pvs = mbd.hasPropertyValues() ? mbd.getPropertyValues() : null;
            int resolvedAutowireMode = mbd.getResolvedAutowireMode();
            if (resolvedAutowireMode == 1 || resolvedAutowireMode == 2) {
                MutablePropertyValues newPvs = new MutablePropertyValues((PropertyValues)pvs);
                if (resolvedAutowireMode == 1) {
                    this.autowireByName(beanName, mbd, bw, newPvs);
                }

                if (resolvedAutowireMode == 2) {
                    this.autowireByType(beanName, mbd, bw, newPvs);
                }

                pvs = newPvs;
            }

            boolean hasInstAwareBpps = this.hasInstantiationAwareBeanPostProcessors();
            boolean needsDepCheck = mbd.getDependencyCheck() != 0;
            PropertyDescriptor[] filteredPds = null;
            if (hasInstAwareBpps) {
                if (pvs == null) {
                    pvs = mbd.getPropertyValues();
                }

                Iterator var9 = this.getBeanPostProcessors().iterator();

                while(var9.hasNext()) {
                    BeanPostProcessor bp = (BeanPostProcessor)var9.next();
                    if (bp instanceof InstantiationAwareBeanPostProcessor) {
                        InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor)bp;
                        PropertyValues pvsToUse = ibp.postProcessProperties((PropertyValues)pvs, bw.getWrappedInstance(), beanName);
                        if (pvsToUse == null) {
                            if (filteredPds == null) {
                                filteredPds = this.filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                            }

                            pvsToUse = ibp.postProcessPropertyValues((PropertyValues)pvs, filteredPds, bw.getWrappedInstance(), beanName);
                            if (pvsToUse == null) {
                                return;
                            }
                        }

                        pvs = pvsToUse;
                    }
                }
            }

            if (needsDepCheck) {
                if (filteredPds == null) {
                    filteredPds = this.filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
                }

                this.checkDependencies(beanName, mbd, filteredPds, (PropertyValues)pvs);
            }

            if (pvs != null) {
                this.applyPropertyValues(beanName, mbd, bw, (PropertyValues)pvs);
            }

        }
    }
```

​													**赋值之前**

​													(1)拿到InstantiationAwareBeanPostProcessor后置处理器

​															postProcessAfterInstantiation();

​													(2)拿到InstantiationAwareBeanPostProcessor后置处理器

​															postprocessPropertyValues();

​													**开始赋值**

​													(3)应用Bean属性的值,为属性利用setter方法等进行赋值

​															applyPropertyValues(beanName, mbd, bw, pvs);

​											4)[Bean初始化] initializeBean(beanName, exposedObject, mbd);

​													(1)invokeAwareMethods(beanName, bean);执行xxxAware接口的方法

```java
	protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged(() -> {
                this.invokeAwareMethods(beanName, bean);
                return null;
            }, this.getAccessControlContext());
        } else {
            //执行xxxAware接口的方法
            this.invokeAwareMethods(beanName, bean);
        }

        Object wrappedBean = bean;
        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        }

        try {
            //执行初始化方法
            this.invokeInitMethods(beanName, wrappedBean, mbd);
        } catch (Throwable var6) {
            throw new BeanCreationException(mbd != null ? mbd.getResourceDescription() : null, beanName, "Invocation of init method failed", var6);
        }

        if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = this.applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }

        return wrappedBean;
    }
```

### 12.this.finishRefresh()声明完成BeanFactory的初始化创建工作，此时IOC容器就创建完成

```java
protected void finishRefresh() {
	//清除资源缓存
    this.clearResourceCaches();
    //初始化生命周期处理器
    this.initLifecycleProcessor();
    //调用生命周期处理器的onfresh()
    this.getLifecycleProcessor().onRefresh();
    //发布事件
    this.publishEvent((ApplicationEvent)(new ContextRefreshedEvent(this)));
    LiveBeansView.registerApplicationContext(this);
}
```

1.this.clearResourceCaches();

```java
private final Map<Class<?>, Map<Resource, ?>> resourceCaches = new ConcurrentHashMap(4);
public void clearResourceCaches() {
	this.resourceCaches.clear();
}
```

2.this.initLifecycleProcessor()；初始化和生命周期有关的后置处理器；

​		默认从容器中找是否有lifecycleProcessor组件[LifecycleProcessor]，有的话加入到容器

```java
	protected void initLifecycleProcessor() {
        //获取beanFactory
        ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
        //如果beanFactory存在lifecycleProcessor则直接赋值lifecycleProcessor
        if (beanFactory.containsLocalBean("lifecycleProcessor")) {
            this.lifecycleProcessor = (LifecycleProcessor)beanFactory.getBean("lifecycleProcessor", LifecycleProcessor.class);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
            }
        } else {
            //否则新建一个DefaultLifecycleProcessor并注册至beanFactory
            DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
            defaultProcessor.setBeanFactory(beanFactory);
            this.lifecycleProcessor = defaultProcessor;
            beanFactory.registerSingleton("lifecycleProcessor", this.lifecycleProcessor);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No 'lifecycleProcessor' bean, using [" + this.lifecycleProcessor.getClass().getSimpleName() + "]");
            }
        }
    }
```

3.this.getLifecycleProcessor().onRefresh();

​	拿到之前定义的生命周期处理器(BeanFactory)，回调onRefresh

```java
public void onRefresh() {
    this.startBeans(true);
    this.running = true;
}
```

4.publishEvent((ApplicationEvent)(new ContextRefreshedEvent(this)));发布容器刷新完成事件

```java
protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
        Assert.notNull(event, "Event must not be null");
        Object applicationEvent;
    	//如果当前event是ApplicationEvent，直接赋值
        if (event instanceof ApplicationEvent) {
            applicationEvent = (ApplicationEvent)event;
        } else {
            //否则创建PayloadApplicationEvent实例
            applicationEvent = new PayloadApplicationEvent(this, event);
            //如果eventType空，通过applicationEvent.getResolvableType()赋值
            if (eventType == null) {
                eventType = ((PayloadApplicationEvent)applicationEvent).getResolvableType();
            }
        }

        if (this.earlyApplicationEvents != null) {
            this.earlyApplicationEvents.add(applicationEvent);
        } else {
            this.getApplicationEventMulticaster().multicastEvent((ApplicationEvent)applicationEvent, eventType);
        }

        if (this.parent != null) {
            if (this.parent instanceof AbstractApplicationContext) {
                ((AbstractApplicationContext)this.parent).publishEvent(event, eventType);
            } else {
                this.parent.publishEvent(event);
            }
        }

    }
```

至此，Spring容器的refresh()执行完成

## 总结

1.Spring容器在启动的时候，会先保存所有注册好的Bean的定义信息

​			1) xml注册bean<bean>

​			2) 通过注解注册Bean:@Service、@Component、@Bean、xxx

2.Spring容器会在合适的时机创建这些Bean

​			1) 用到某bean的时候，利用getBean创建bean，创建好后保存在容器中

​			2) 统一创建剩下的所有bean，finishBeanFactoryInitialization(beanFactory)

3.后置处理器

​			1) 没一个bean创建完成，都会使用各种后置处理器进行处理，来增强bean的功能

​					AutowiredAnnotationBeanPostProcessor:处理自动注入

​					AnnotationAwareAspectJAutoProxyCreator:AOP功能

​					xxx.....

​					增强的功能注解:

​					AsyncAnnotationBeanPostProcessor...

4.事件驱动模型

​			ApplicationListener 事件监听

​			ApplicationEventMulticaster:事件派发