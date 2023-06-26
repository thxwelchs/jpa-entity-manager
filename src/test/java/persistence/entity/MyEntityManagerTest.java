package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.H2DbDialect;
import persistence.sql.ddl.DdlQueryBuilder;
import persistence.sql.ddl.JavaToSqlColumnParser;
import persistence.sql.dml.DmlQueryBuilder;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MyEntityManagerTest {
    private final DatabaseServer server = new H2();
    private JdbcTemplate jdbcTemplate;

    MyEntityManagerTest() throws SQLException {
    }

    @BeforeEach
    void setUp() throws SQLException {
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        createTable();
    }

    @AfterEach
    void tearDown() {
        dropTable();
        server.stop();
    }

    @DisplayName("entityManager 의 findById 메서드 테스트")
    @Test
    void findByIdTest() {

        final DmlQueryBuilder<Person> dmlQueryBuilder = new DmlQueryBuilder<>(Person.class);
        final Person jeongwon = new Person(1L, "정원", 15, "a@a.com", 1);
        final String insertSql = dmlQueryBuilder.insert(jeongwon);
        jdbcTemplate.execute(insertSql);

        final MyEntityManager myEntityManager = new MyEntityManager(jdbcTemplate);
        final Person person = myEntityManager.find(Person.class, 1L);

        assertAll(
                () -> assertThat(person.getName()).isEqualTo(jeongwon.getName()),
                () -> assertThat(person.getAge()).isEqualTo(jeongwon.getAge()),
                () -> assertThat(person.getId()).isEqualTo(jeongwon.getId()),
                () -> assertThat(person.getEmail()).isEqualTo(jeongwon.getEmail()),
                () -> assertThat(person.getIndex()).isNull()
        );
    }

    @DisplayName("entityManager 의 persist 메서드 테스트")
    @Test
    void persistTest() {
        final Person jeongwon = new Person(
                1L,
                "정원",
                15,
                "a@a.com",
                1
        );

        final MyEntityManager myEntityManager = new MyEntityManager(jdbcTemplate);
        myEntityManager.persist(jeongwon);

        final Person person = myEntityManager.find(Person.class, 1L);

        assertAll(
                () -> assertThat(person.getName()).isEqualTo(jeongwon.getName()),
                () -> assertThat(person.getAge()).isEqualTo(jeongwon.getAge()),
                () -> assertThat(person.getId()).isEqualTo(jeongwon.getId()),
                () -> assertThat(person.getEmail()).isEqualTo(jeongwon.getEmail()),
                () -> assertThat(person.getIndex()).isEqualTo(jeongwon.getIndex())
        );
    }

    @DisplayName("entityManager 의 delete 메서드 테스트")
    @Test
    void deleteTest() {
        final Person jeongwon = new Person(
                1L,
                "정원",
                15,
                "a@a.com",
                1
        );

        final MyEntityManager myEntityManager = new MyEntityManager(jdbcTemplate);
        myEntityManager.persist(jeongwon);

        final Person person = myEntityManager.find(Person.class, 1L);

        myEntityManager.remove(person);
        assertThat(myEntityManager.find(Person.class, 1L)).isNull();
    }

    private void createTable() {
        final JavaToSqlColumnParser javaToSqlColumnParser = new JavaToSqlColumnParser(new H2DbDialect());
        final DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(javaToSqlColumnParser, Person.class);
        final String createTableSql = ddlQueryBuilder.createTable();
        jdbcTemplate.execute(createTableSql);
    }

    private void dropTable() {
        final JavaToSqlColumnParser javaToSqlColumnParser = new JavaToSqlColumnParser(new H2DbDialect());
        final DdlQueryBuilder ddlQueryBuilder = new DdlQueryBuilder(javaToSqlColumnParser, Person.class);
        final String dropTableSql = ddlQueryBuilder.dropTable();
        jdbcTemplate.execute(dropTableSql);
    }

}