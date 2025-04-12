package com.yarr.userservices.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.SessionBuilderConfigurer;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.cql.keyspace.CreateKeyspaceSpecification;
import org.springframework.data.cassandra.core.cql.keyspace.KeyspaceOption;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.yarr.userservices.converters.FormDataToUdtValueConverter;
import com.yarr.userservices.converters.OrderLogsToUdtValueConverter;
import com.yarr.userservices.converters.QueryLogsToUdtValueConverter;
import com.yarr.userservices.converters.StudentDataToUdtValueConverter;
import com.yarr.userservices.converters.UdtValueToFormDataConverter;
import com.yarr.userservices.converters.UdtValueToOrderLogsConverter;
import com.yarr.userservices.converters.UdtValueToQueryLogsConverter;
import com.yarr.userservices.converters.UdtValueToStudentDataConverter;
import com.yarr.userservices.converters.UdtValueToUserFormRequestConverter;
import com.yarr.userservices.converters.UserFormRequestToUdtValueConverter;

@Configuration
@EnableCassandraRepositories(basePackages = "com.yarr.userservices.repository")
public class CassandraConfig extends AbstractCassandraConfiguration {
  @Value("${spring.cassandra.local-datacenter}")
  private String datacenter;

  @Value("${spring.cassandra.contact-points}")
  private String contactPoints;

  @Value("${spring.cassandra.port}")
  private int port;

  @Value("${spring.cassandra.keyspace-name}")
  private String keyspaceName;

  @Override
  protected String getContactPoints() {
    return contactPoints;
  }

  @Override
  protected String getKeyspaceName() {
    return keyspaceName;
  }

  @Override
  protected int getPort() {
    return port;
  }

  @Override
  protected String getLocalDataCenter() {
    return datacenter;
  }

  @Override
  public SchemaAction getSchemaAction() {
    return SchemaAction.CREATE_IF_NOT_EXISTS;
  }

  @Override
  protected List<CreateKeyspaceSpecification> getKeyspaceCreations() {
    CreateKeyspaceSpecification specification = CreateKeyspaceSpecification
        .createKeyspace(keyspaceName)
        .ifNotExists()
        .with(KeyspaceOption.DURABLE_WRITES, true)
        .withSimpleReplication(1);

    return Arrays.asList(specification);
  }

  @Bean(name = "cassandraScript")
  public String cassandraScript() {
    try {
      CqlSession session = cassandraSession().getObject();
      ClassPathResource resource = new ClassPathResource("cassandra-schema.cql");
      String schema = new String(resource.getInputStream().readAllBytes());

      // Split the script by semicolons and execute each statement
      for (String statement : schema.split(";")) {
        if (!statement.trim().isEmpty()) {
          session.execute(statement.trim());
        }
      }
      return "Schema created successfully";
    } catch (Exception e) {
      e.printStackTrace();
      return "Error creating schema: " + e.getMessage();
    }
  }

  @Override
  @DependsOn("cassandraScript")
  public CassandraCustomConversions customConversions() {
    List<Object> converters = new ArrayList<>();

    try {
      CqlSession session = cassandraSession().getObject();
      if (session != null) {
        // Get the UDT definitions with error handling
        try {
          UserDefinedType studentDataType = getUserDefinedType(session, "student_data");
          FormDataToUdtValueConverter formDataToUdtValueConverter = new FormDataToUdtValueConverter(
              getUserDefinedType(session, "form_data"));
          converters.add(new StudentDataToUdtValueConverter(studentDataType, formDataToUdtValueConverter));
          UdtValueToFormDataConverter udtValueToFormDataConverter = new UdtValueToFormDataConverter();
          converters.add(new UdtValueToStudentDataConverter(udtValueToFormDataConverter));
        } catch (Exception e) {
          System.err.println("Failed to initialize student_data converters: " + e.getMessage());
        }

        try {
          UserDefinedType queryLogsType = getUserDefinedType(session, "query_logs");
          converters.add(new QueryLogsToUdtValueConverter(queryLogsType));
          converters.add(new UdtValueToQueryLogsConverter());
        } catch (Exception e) {
          System.err.println("Failed to initialize query_logs converters: " + e.getMessage());
        }

        try {
          UserDefinedType orderLogsType = getUserDefinedType(session, "order_logs");
          converters.add(new OrderLogsToUdtValueConverter(orderLogsType));
          converters.add(new UdtValueToOrderLogsConverter());
        } catch (Exception e) {
          System.err.println("Failed to initialize order_logs converters: " + e.getMessage());
        }

        try {
          UserDefinedType formDataType = getUserDefinedType(session, "form_data");
          converters.add(new FormDataToUdtValueConverter(formDataType));
          converters.add(new UdtValueToFormDataConverter());
        } catch (Exception e) {
          System.err.println("Failed to initialize form_data converters: " + e.getMessage());
        }

        try {
          UserDefinedType userFormRequestType = getUserDefinedType(session, "user_form_request");
          converters.add(new UserFormRequestToUdtValueConverter(userFormRequestType));
          converters.add(new UdtValueToUserFormRequestConverter());
        } catch (Exception e) {
          System.err.println("Failed to initialize user_form_request converters: " + e.getMessage());
        }
      }
    } catch (Exception e) {
      System.err.println("Error initializing custom conversions: " + e.getMessage());
    }

    return new CassandraCustomConversions(converters);
  }

  private UserDefinedType getUserDefinedType(CqlSession session, String typeName) {
    return session.getKeyspace()
        .flatMap(ks -> session.getMetadata().getKeyspace(ks))
        .flatMap(ksmd -> ksmd.getUserDefinedType(typeName))
        .orElseThrow(() -> new IllegalStateException(typeName + " UDT not found"));
  }

  @Bean
  @Override
  public CqlSessionFactoryBean cassandraSession() {
    CqlSessionFactoryBean cassandraSession = super.cassandraSession();
    cassandraSession.setPort(port);
    cassandraSession.setLocalDatacenter(datacenter);
    cassandraSession.setKeyspaceName(keyspaceName);
    return cassandraSession;
  }

  @Override
  protected SessionBuilderConfigurer getSessionBuilderConfigurer() {
    return new SessionBuilderConfigurer() {
      @Override
      public CqlSessionBuilder configure(CqlSessionBuilder cqlSessionBuilder) {
        return cqlSessionBuilder
            .withConfigLoader(DriverConfigLoader.programmaticBuilder()
                .withDuration(DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT, Duration.ofMillis(60000))
                .withDuration(DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofMillis(60000))
                .withDuration(DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofMillis(15000))
                .build());
      }
    };
  }

  protected boolean getMetricsEnabled() {
    return false;
  }

  @Override
  public String[] getEntityBasePackages() {
    return new String[] { "com.yarr.userservices.entity" };
  }
}