/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.orm.jpa.vendor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.boot.cfgxml.internal.CfgXmlAccessServiceInitiator;
import org.hibernate.boot.internal.DefaultSessionFactoryBuilderInitiator;
import org.hibernate.boot.registry.StandardServiceInitiator;
import org.hibernate.bytecode.internal.BytecodeProviderInitiator;
import org.hibernate.bytecode.internal.ProxyFactoryFactoryInitiator;
import org.hibernate.bytecode.spi.BytecodeProvider;
import org.hibernate.cache.internal.RegionFactoryInitiator;
import org.hibernate.engine.config.internal.ConfigurationServiceInitiator;
import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
import org.hibernate.engine.jdbc.connections.internal.ConnectionProviderInitiator;
import org.hibernate.engine.jdbc.connections.internal.MultiTenantConnectionProviderInitiator;
import org.hibernate.engine.jdbc.cursor.internal.RefCursorSupportInitiator;
import org.hibernate.engine.jdbc.dialect.internal.DialectFactoryInitiator;
import org.hibernate.engine.jdbc.dialect.internal.DialectResolverInitiator;
import org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator;
import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
import org.hibernate.engine.jndi.internal.JndiServiceInitiator;
import org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformInitiator;
import org.hibernate.engine.transaction.jta.platform.internal.JtaPlatformResolverInitiator;
import org.hibernate.event.internal.EntityCopyObserverFactoryInitiator;
import org.hibernate.persister.internal.PersisterClassResolverInitiator;
import org.hibernate.persister.internal.PersisterFactoryInitiator;
import org.hibernate.property.access.internal.PropertyAccessStrategyResolverInitiator;
import org.hibernate.resource.beans.spi.ManagedBeanRegistryInitiator;
import org.hibernate.resource.transaction.internal.TransactionCoordinatorBuilderInitiator;
import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.internal.SchemaManagementToolInitiator;

import org.springframework.core.NativeDetector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Alternative to {@link org.hibernate.service.StandardServiceInitiators} which avoids
 * making ByteBuddy reachable on native images. Can also be used for other Hibernate
 * infrastructure optimizations. Supports both Hibernate 5.6.x and Hibernate 6.x.
 *
 * @author Sebastien Deleuze
 * @since 6.1
 */
abstract class SpringServiceInitiators {

	static final String SSI_FIELD_NAME = "INSTANCE";

	// Hibernate 5.6.x
	static final String JSI_CLASS_NAME = "org.hibernate.jmx.internal.JmxServiceInitiator";
	static final String QTFI_CLASS_NAME = "org.hibernate.hql.internal.QueryTranslatorFactoryInitiator";
	static final String MIGFI_CLASS_NAME = "org.hibernate.id.factory.internal.MutableIdentifierGeneratorFactoryInitiator";

	// Hibernate 6.x
	static final String SIGFI_CLASS_NAME = "org.hibernate.id.factory.internal.StandardIdentifierGeneratorFactoryInitiator";
	static final String SSEI_CLASS_NAME = "org.hibernate.tool.schema.internal.script.SqlScriptExtractorInitiator";
	static final String ISCEI_CLASS_NAME = "org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractorInitiator";
	static final String MESI_CLASS_NAME = "org.hibernate.engine.jdbc.mutation.internal.MutationExecutorServiceInitiator";
	static final String SSLI_CLASS_NAME = "org.hibernate.engine.jdbc.internal.SqlStatementLoggerInitiator";
	static final String JVMPPI_CLASS_NAME = "org.hibernate.sql.results.jdbc.internal.JdbcValuesMappingProducerProviderInitiator";
	static final String SMTMSPI_CLASS_NAME = "org.hibernate.query.sqm.mutation.internal.SqmMultiTableMutationStrategyProviderInitiator";
	static final String PMSI_CLASS_NAME = "org.hibernate.sql.ast.internal.ParameterMarkerStrategyInitiator";
	static final String BLFI_CLASS_NAME = "org.hibernate.loader.ast.internal.BatchLoaderFactoryInitiator";

	@Nullable
	private static final StandardServiceInitiator<?> standardIdentifierGeneratorFactoryInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> sqlScriptExtractorInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> importSqlCommandExtractorInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> jmxServiceInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> mutationExecutorServiceInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> sqlStatementLoggerInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> queryTranslatorFactoryInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> mutableIdentifierGeneratorFactoryInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> jdbcValuesMappingProducerProviderInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> sqmMultiTableMutationStrategyProviderInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> parameterMarkerStrategyInitiator;

	@Nullable
	private static final StandardServiceInitiator<?> batchLoaderFactoryInitiator;

	static {
		standardIdentifierGeneratorFactoryInitiator = instantiateServiceInitiator(SIGFI_CLASS_NAME);
		sqlScriptExtractorInitiator = instantiateServiceInitiator(SSEI_CLASS_NAME);
		importSqlCommandExtractorInitiator = instantiateServiceInitiator(ISCEI_CLASS_NAME);
		jmxServiceInitiator = instantiateServiceInitiator(JSI_CLASS_NAME);
		mutationExecutorServiceInitiator = instantiateServiceInitiator(MESI_CLASS_NAME);
		sqlStatementLoggerInitiator = instantiateServiceInitiator(SSLI_CLASS_NAME);
		queryTranslatorFactoryInitiator = instantiateServiceInitiator(QTFI_CLASS_NAME);
		mutableIdentifierGeneratorFactoryInitiator = instantiateServiceInitiator(MIGFI_CLASS_NAME);
		jdbcValuesMappingProducerProviderInitiator = instantiateServiceInitiator(JVMPPI_CLASS_NAME);
		sqmMultiTableMutationStrategyProviderInitiator = instantiateServiceInitiator(SMTMSPI_CLASS_NAME);
		parameterMarkerStrategyInitiator = instantiateServiceInitiator(PMSI_CLASS_NAME);
		batchLoaderFactoryInitiator = instantiateServiceInitiator(BLFI_CLASS_NAME);
	}

	@SuppressWarnings("rawtypes")
	public static List<StandardServiceInitiator> buildInitiatorList() {
		final ArrayList<StandardServiceInitiator<?>> serviceInitiators = new ArrayList<>();
		serviceInitiators.add(DefaultSessionFactoryBuilderInitiator.INSTANCE);
		if (standardIdentifierGeneratorFactoryInitiator != null) {
			serviceInitiators.add(standardIdentifierGeneratorFactoryInitiator);
		}
		if (NativeDetector.inNativeImage()) {
			serviceInitiators.add(new NoneBytecodeProviderInitiator());
		}
		else {
			serviceInitiators.add(BytecodeProviderInitiator.INSTANCE);
		}
		serviceInitiators.add(ProxyFactoryFactoryInitiator.INSTANCE);
		serviceInitiators.add(CfgXmlAccessServiceInitiator.INSTANCE);
		serviceInitiators.add(ConfigurationServiceInitiator.INSTANCE);
		serviceInitiators.add(PropertyAccessStrategyResolverInitiator.INSTANCE);
		if (sqlScriptExtractorInitiator != null) {
			serviceInitiators.add(sqlScriptExtractorInitiator);
		}
		else {
			Assert.notNull(importSqlCommandExtractorInitiator,
					"Either org.hibernate.tool.schema.internal.script.SqlScriptExtractorInitiator or " +
					"org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractorInitiator are expected to be available " +
					"in the classpath, none has been found.");
			serviceInitiators.add(importSqlCommandExtractorInitiator);
		}
		serviceInitiators.add(SchemaManagementToolInitiator.INSTANCE);
		serviceInitiators.add(JdbcEnvironmentInitiator.INSTANCE);
		serviceInitiators.add(JndiServiceInitiator.INSTANCE);
		if (jmxServiceInitiator != null) {
			serviceInitiators.add(jmxServiceInitiator);
		}
		serviceInitiators.add(PersisterClassResolverInitiator.INSTANCE);
		serviceInitiators.add(PersisterFactoryInitiator.INSTANCE);
		serviceInitiators.add(ConnectionProviderInitiator.INSTANCE);
		serviceInitiators.add(MultiTenantConnectionProviderInitiator.INSTANCE);
		serviceInitiators.add(DialectResolverInitiator.INSTANCE);
		serviceInitiators.add(DialectFactoryInitiator.INSTANCE);
		if (mutationExecutorServiceInitiator != null) {
			serviceInitiators.add(mutationExecutorServiceInitiator);
		}
		serviceInitiators.add(BatchBuilderInitiator.INSTANCE);
		if (sqlStatementLoggerInitiator != null) {
			serviceInitiators.add(sqlStatementLoggerInitiator);
		}
		serviceInitiators.add(JdbcServicesInitiator.INSTANCE);
		serviceInitiators.add(RefCursorSupportInitiator.INSTANCE);
		if (queryTranslatorFactoryInitiator != null) {
			serviceInitiators.add(queryTranslatorFactoryInitiator);
		}
		if (mutableIdentifierGeneratorFactoryInitiator != null) {
			serviceInitiators.add(mutableIdentifierGeneratorFactoryInitiator);
		}
		serviceInitiators.add(JtaPlatformResolverInitiator.INSTANCE);
		serviceInitiators.add(JtaPlatformInitiator.INSTANCE);
		serviceInitiators.add(SessionFactoryServiceRegistryFactoryInitiator.INSTANCE);
		serviceInitiators.add(RegionFactoryInitiator.INSTANCE);
		serviceInitiators.add(TransactionCoordinatorBuilderInitiator.INSTANCE);
		serviceInitiators.add(ManagedBeanRegistryInitiator.INSTANCE);
		serviceInitiators.add(EntityCopyObserverFactoryInitiator.INSTANCE);
		if (jdbcValuesMappingProducerProviderInitiator != null) {
			serviceInitiators.add(jdbcValuesMappingProducerProviderInitiator);
		}
		if (sqmMultiTableMutationStrategyProviderInitiator != null) {
			serviceInitiators.add(sqmMultiTableMutationStrategyProviderInitiator);
		}
		if (parameterMarkerStrategyInitiator != null) {
			serviceInitiators.add(parameterMarkerStrategyInitiator);
		}
		if (batchLoaderFactoryInitiator != null) {
			serviceInitiators.add(batchLoaderFactoryInitiator);
		}
		serviceInitiators.trimToSize();
		return Collections.unmodifiableList(serviceInitiators);
	}

	@Nullable
	private static StandardServiceInitiator<?> instantiateServiceInitiator(String className) {
		ClassLoader classLoader = SpringServiceInitiators.class.getClassLoader();
		try {
			Class<?> clazz = ClassUtils.forName(className, classLoader);
			return (StandardServiceInitiator<?>) clazz.getField(SSI_FIELD_NAME).get(null);
		}
		catch (Throwable ex) {
			return null;
		}
	}

	private static class NoneBytecodeProviderInitiator implements StandardServiceInitiator<BytecodeProvider> {

		@Override
		@SuppressWarnings("rawtypes")
		public BytecodeProvider initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
			return new org.hibernate.bytecode.internal.none.BytecodeProviderImpl();
		}

		@Override
		public Class<BytecodeProvider> getServiceInitiated() {
			return BytecodeProvider.class;
		}

	}

}
