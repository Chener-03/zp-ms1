/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.chener.zp.sentinelAdapter.feign;

import com.alibaba.csp.sentinel.SphU;
import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import xyz.chener.zp.sentinelAdapter.sphu.SphUDefault;


@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ SphU.class, Feign.class })
@AutoConfigureBefore(name = "com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration")
@Import(SphUDefault.class)
public class SentinelFeignCustomAutoConfiguration {

	@Bean
	@Scope("prototype")
	@ConditionalOnMissingBean
	public Feign.Builder feignSentinelBuilder() {
		return SentinelFeign.builder();
	}

}
