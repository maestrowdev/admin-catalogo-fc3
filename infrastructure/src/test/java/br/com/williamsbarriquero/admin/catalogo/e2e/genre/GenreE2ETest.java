package br.com.williamsbarriquero.admin.catalogo.e2e.genre;

import br.com.williamsbarriquero.admin.catalogo.E2ETest;
import br.com.williamsbarriquero.admin.catalogo.domain.category.CategoryID;
import br.com.williamsbarriquero.admin.catalogo.domain.genre.GenreID;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.configuration.json.Json;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import br.com.williamsbarriquero.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
class GenreE2ETest {

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("adm_videos");
    @Autowired
    private MockMvc mvc;
    @Autowired
    private GenreRepository genreRepository;

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithValidValues() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, genreRepository.count());

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        final var actualId = givenAGenre(expectedName, expectedIsActive, expectedCategories);

        final var actualGenre = genreRepository.findById(actualId.getValue()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategories().size()
                        && expectedCategories.containsAll(actualGenre.getCategoryIDs())
        );
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    void asACatalogAdminIShouldBeAbleToCreateANewGenreWithCategories() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, genreRepository.count());

        final var movies = givenACategory("Filmes", "Filmes em geral", true);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(movies);

        final var actualId = givenAGenre(expectedName, expectedIsActive, expectedCategories);

        final var actualGenre = genreRepository.findById(actualId.getValue()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategories().size()
                        && expectedCategories.containsAll(actualGenre.getCategoryIDs())
        );
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    private GenreID givenAGenre(
            final String aName,
            final boolean isActive,
            final List<CategoryID> categories
    ) throws Exception {
        final var aRequestBody = new CreateGenreRequest(aName, mapTo(categories, CategoryID::getValue), isActive);

        final var aRequest = post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        final var actualId = this.mvc.perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("/genres/", "");

        return GenreID.from(actualId);
    }

    private CategoryID givenACategory(
            final String aName,
            final String aDescription,
            final boolean isActive
    ) throws Exception {
        final var aRequestBody = new CreateCategoryRequest(aName, aDescription, isActive);

        final var aRequest = post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        final var actualId = this.mvc.perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("/categories/", "");

        return CategoryID.from(actualId);
    }

    private <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream()
                .map(mapper)
                .toList();
    }
}
