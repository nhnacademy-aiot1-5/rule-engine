package live.ioteatime.ruleengine.repository.impl;

import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class InfluxQueryRepositoryImplTest {
    @Mock
    QueryApi queryApi;
    @InjectMocks
    InfluxQueryRepositoryImpl repository;
    @TempDir
    File tempDir;
    List<String> queries;

    @BeforeEach
    void setUp() throws IOException {
        repository = new InfluxQueryRepositoryImpl(queryApi) {

            @Override
            protected BufferedReader createBufferedReader(String queryPath) {
                return new BufferedReader(new StringReader("query1\nquery2\nquery3\n" + queryPath));
            }
        };
        queries = new ArrayList<>(List.of("test1", "test2"));
        ReflectionTestUtils.setField(repository, "queries", queries);
        ReflectionTestUtils.setField(repository, "queryPath", tempDir.getAbsolutePath());
    }

    @Test
    void queryTest() {
        String query = "test";
        List<FluxTable> tables = List.of(new FluxTable(), new FluxTable());

        when(queryApi.query(query)).thenReturn(tables);

        repository.query(query);

        verify(queryApi).query(query);
    }

    @Test
    void getQuerySizeTest() {
        int querySize = repository.getQuerySize();

        assertEquals(queries.size(), querySize);
    }

    @Test
    void getQueryTest() {
        String query = repository.getQuery(0);

        assertEquals(queries.get(0), query);
    }

    @Test
    void updateQueryTest() {
        repository.updateQuery();

        assertEquals(4, repository.getQuerySize());
    }

    @Test
    void writeQueryTest() throws IOException {
        String query = "test";

        repository.writeQuery(query);

        File file = new File(tempDir.getAbsolutePath());
        assertTrue(file.exists());

        BufferedReader bufferedReader = repository.createBufferedReader(query);
        assertEquals("query1", bufferedReader.readLine());
        assertEquals("query2", bufferedReader.readLine());
        assertEquals("query3", bufferedReader.readLine());
        assertEquals("test", bufferedReader.readLine());
        bufferedReader.close();
    }

    @Test
    void modifyQueryTest() throws IOException {
        try (MockedStatic<Files> files = Mockito.mockStatic(Files.class)) {
            String modifyQuery = "modify";

            repository.modifyQuery();

            File file = new File(tempDir.getAbsolutePath());
            assertTrue(file.exists());

            files.verify(() -> Files.delete(Path.of(tempDir.getAbsolutePath())));
            BufferedReader bufferedReader = repository.createBufferedReader(modifyQuery);
            assertEquals("query1", bufferedReader.readLine());
            assertEquals("query2", bufferedReader.readLine());
            assertEquals("query3", bufferedReader.readLine());
            assertEquals("modify", bufferedReader.readLine());
        }
    }

    @Test
    void removeQueryTest() {
        repository.removeQuery(1);

        assertEquals(1, repository.getQuerySize());
    }

    @Test
    void getQueryPathTest() {
        assertEquals(tempDir.getAbsolutePath(), repository.getQueryPath());
    }

}