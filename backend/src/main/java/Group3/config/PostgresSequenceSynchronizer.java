package Group3.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Realigns PostgreSQL-backed identity sequences with the current max ID values.
 *
 * This prevents inserts from failing when rows were loaded with explicit IDs but
 * the underlying sequence was not advanced to match the existing data.
 */
@Configuration
public class PostgresSequenceSynchronizer {

    private static final Logger logger = LoggerFactory.getLogger(PostgresSequenceSynchronizer.class);

    private static final List<String> TABLES = List.of(
            "users",
            "foods",
            "meals",
            "meal_foods"
    );

    @Bean
    ApplicationRunner synchronizePostgresSequences(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            if (!isPostgreSql(dataSource)) {
                return;
            }

            for (String tableName : TABLES) {
                alignSequence(jdbcTemplate, tableName, "id");
            }
        };
    }

    private boolean isPostgreSql(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName();
            return productName != null && productName.toLowerCase().contains("postgresql");
        } catch (SQLException e) {
            logger.warn("Unable to determine database product name. Skipping sequence synchronization.", e);
            return false;
        }
    }

    private void alignSequence(JdbcTemplate jdbcTemplate, String tableName, String idColumn) {
        try {
            String sequenceName = jdbcTemplate.queryForObject(
                    "select pg_get_serial_sequence(?, ?)",
                    String.class,
                    tableName,
                    idColumn
            );

            if (sequenceName == null || sequenceName.isBlank()) {
                logger.debug("No sequence found for {}.{}", tableName, idColumn);
                return;
            }

            String sql = String.format(
                    "select setval('%s', coalesce((select max(%s) from %s), 0) + 1, false)",
                    sequenceName,
                    idColumn,
                    tableName
            );

            jdbcTemplate.queryForObject(sql, Long.class);
            logger.info("Synchronized sequence {} for {}.{}", sequenceName, tableName, idColumn);
        } catch (Exception e) {
            logger.warn("Failed to synchronize sequence for {}.{}", tableName, idColumn, e);
        }
    }
}