package com.example.reactive.core;

import io.zonky.test.db.postgres.embedded.*;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Configuration
public class TestConfig {

    @Bean
    public DataSource dataSource() throws SQLException {

        DatabasePreparer preparer = LiquibasePreparer.forClasspathLocation("db/changelog/changelog.xml");

        List<Consumer<EmbeddedPostgres.Builder>> builderCustomizers = new CopyOnWriteArrayList<>();

        PreparedDbProvider provider = PreparedDbProvider.forPreparer(preparer, builderCustomizers);
        ConnectionInfo connectionInfo = provider.createNewDatabase();
        return provider.createDataSourceFromConnectionInfo(connectionInfo);
    }

    /**
     * translate embedded postgres properties to R2dbcProperties
     */
    @Primary
    @Bean
    public R2dbcProperties getR2dbcProperties(DataSource dataSource) throws SQLException {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        String newUrl = metaData.getURL().replace("jdbc", "r2dbc");

        R2dbcProperties r2dbcProperties = new R2dbcProperties();
        r2dbcProperties.setUrl(newUrl);
        r2dbcProperties.setUsername(metaData.getUserName());
        return r2dbcProperties;
    }
}
