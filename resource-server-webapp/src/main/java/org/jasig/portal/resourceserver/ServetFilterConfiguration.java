/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.resourceserver;

import net.sf.ehcache.CacheManager;
import org.jasig.resourceserver.aggr.ResourcesDao;
import org.jasig.resourceserver.aggr.ResourcesDaoImpl;
import org.jasig.resourceserver.utils.aggr.ResourcesElementsProvider;
import org.jasig.resourceserver.utils.aggr.ResourcesElementsProviderImpl;
import org.jasig.resourceserver.utils.cache.ConfigurablePageCachingFilter;
import org.jasig.resourceserver.utils.filter.CacheExpirationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServetFilterConfiguration {

    private static final String PAGE_CACHE_NAME = "SimplePageCachingFilter";

    @Autowired
    private CacheManager cacheManager;

    @Bean
    public ResourcesDao resourcesDao() {
        return new ResourcesDaoImpl();
    }

    @Bean
    public ResourcesElementsProvider resourcesElementsProvider() {
        final ResourcesElementsProviderImpl rslt = new ResourcesElementsProviderImpl();
        rslt.setResourcesDao(resourcesDao());
        return rslt;
    }

    @Bean
    public FilterRegistrationBean pageCachingFilter() {
        final ConfigurablePageCachingFilter filter = new ConfigurablePageCachingFilter(cacheManager, PAGE_CACHE_NAME);
        filter.setResourcesElementsProvider(resourcesElementsProvider());

        FilterRegistrationBean rslt = new FilterRegistrationBean();
        rslt.setFilter(filter);
        rslt.addUrlPatterns("*.js", "*.css");
        rslt.addInitParameter("targetFilterLifecycle", "true");
        rslt.setName("pageCachingFilter");
        return rslt;
    }

    @Bean
    public FilterRegistrationBean cacheExpirationFilter() {
        final CacheExpirationFilter filter = new CacheExpirationFilter();

        FilterRegistrationBean rslt = new FilterRegistrationBean();
        rslt.setFilter(filter);
        rslt.addUrlPatterns("/rs/*");
        rslt.setName("CacheExpiresFilter");
        return rslt;
    }

}
