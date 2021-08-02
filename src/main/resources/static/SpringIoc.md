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

​			1）获取BeanFactory

​			2）看容器中是否有id为messageSource且类型为MessageSource的组件，

​				有则赋值给messageSource，反之自己创建一个DelegatingMessageSource

​				MessageSource，取出国际化配置文件中的某个key值，能够按照区域信息获取

​			3）把创建好的MessageSource注册在容器中，以后获取国际化配置文件时，可自动注入MessageSource

​				beanFactory.registerSingleton("messageSource", this.messageSource);

​				String getMessage(String var1, @Nullable Object[] var2, Locale var3);

### 8.initApplicationEventMulticaster();初始化事件派发器

​			1）获取BeanFactory

​			2）从BeanFactory中获取applicationEventMulticaster的ApplicationEventMulticaster

​			3）如果上一步没有配置，则创建一个SimpleApplicationEventMulticaster

​			4）将创建的ApplicationEventMulticaster添加到BeanFactory中，以后其他组件直接注入

### 9.onRefresh();留给子容器(子类)

​			1）子类重写此方法，在容器刷新的适合可以自定义逻辑

### 10.registerListener();给容器中将所有项目里面的ApplicationListener注册进来

​			1）从容器中拿到所有的ApplicationListener

​			2）将每个监听器添加到事件派发器中

​				this.getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);

​			3）派发之前步骤产生的事件

### 11.this.finishBeanFactoryInitialization(beanFactory);初始化所有剩下的单实例bean

​			1）beanFactory.preInstantiateSingletons();初始化后剩下的单实例bean

​				1】获取容器中所有Bean，依次进行初始化和创建对象

​				2】获取Bean的定义信息，RootBeanDefinition

​				3】Bean不是抽象的，是单实例的，是懒加载的

​						①判断是否是FactoryBean：是否是实现FactoryBean接口的Bean

​						②不是FactoryBean，则利用getBean(beanName)创建对象

​								Ⅰ.getBean(beanName):ioc.getBean();

​								Ⅱ.doGetBean(name, (Class)null, (Object[])null, false);

​								Ⅲ.先获取缓存中保存的单实例Bean,如果能获取到说明此bean之前被创建过(所有创建过的bean都会被缓存起来)

```java
private final Map<String, Object> singletenObjects = new ConcurrentHashMap<>(256);
```

​								Ⅳ.缓存中获取不到, 开始Bean的创建对象流程

​								Ⅴ.标记当前bean已经被创建,防止多线程不安全情况

​								Ⅵ.获取Bean的定义信息

​								Ⅶ.获取当前Bean依赖的其他Bean;如果有按照getBean()把依赖的Bean先创建出来

​								Ⅷ.启动单实例Bean的创建流程:

​									1. createBean(beanName, mbd, args);

​									2 . Object bean = resolveBeforeInstantiation(beanName, mbdToUse);

​									让BeanPostProcessor先拦截返回代理对象

​										[InstantiationAwareBeanPostProcessor]提前执行,先触发postProcessorBeforeInstantiation()方法,如果有返回值再触发postProcessorAfterInstantiation();

​									3. 如果前面的InstantiationAwareBeanPostProcessor没有返回代理对象 调用👇

​									4. Object beanInstance = doCreateBean(beanName, mbdToUse, args);	

​											1)创建Bean实例:createBeanInstance(beanName, mbd, args);

​												利用工厂方法或对象的构造器创建除Bean实例

​											2)applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);

​												调用MergedBeanDefinitionPostProcessor的

​												bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName);

​											3)[Bean属性赋值]populateBean(beanName, mbd, beanWrapper);

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









































