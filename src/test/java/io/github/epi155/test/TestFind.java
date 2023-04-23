package io.github.epi155.test;

import io.github.epi155.pm.lang.CustMsg;
import io.github.epi155.pm.lang.Hope;
import io.github.epi155.pm.lang.SearchResult;
import io.github.epi155.pm.lang.Some;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

@Slf4j
class TestFind {
    private static final CustMsg MY_FAULT = CustMsg.of("CA01", "Oop error !!");

    @Test
    void testFound1() {
        SearchResult<String> result = SearchResult.of("A");
        Assertions.assertDoesNotThrow(() ->
                result
                        .onFound(s -> log.info("Found {}", s))
                        .onNotFound(() -> log.info("Not found"))
                        .onFailure(e -> log.warn("Error {}", e.message())));
        if (result.isFound()) {
            val value = result.value();
            // action on found value
        } else if (result.isNotFound()) {
            // action on not found
        } else if (result.isFailure()) {
            val fault = result.failure();
            // action on failure
        }
        val a = result.<String>valueBuilder()
                .onFoundOf(t -> t)
                .onNotFound(() -> Hope.of("E"))
                .build();

        SearchResult<Integer> result2 = result.<Integer>resultBuilder()
                .onFound(s -> SearchResult.of(1))
                .onNotFound(() -> SearchResult.of(2))
                .build();
    }
    @Test
    void testNotFound1() {
        SearchResult<String> result = SearchResult.empty();
        Assertions.assertDoesNotThrow(() -> result
            .onFound(s -> log.info("Found {}", s))
            .onNotFound(() -> log.info("Not found"))
            .onFailure(e -> log.warn("Error {}", e.message())));
    }
    @Test
    void testError1() {
        SearchResult<String> result = SearchResult.capture(new NullPointerException());
        Assertions.assertDoesNotThrow(() -> result
            .onFound(s -> log.info("Found {}", s))
            .onNotFound(() -> log.info("Not found"))
            .onFailure(e -> log.warn("Error {}", e.message())));
    }

    @Test
    void testFound2() {
        SearchResult<String> result = SearchResult.of("A");
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(s -> Hope.of((int) s.charAt(0)))
            .onNotFound(() -> Hope.of(0))
            .build();
        Assertions.assertTrue(value.completeSuccess());
        Assertions.assertEquals(65, value.value());
    }
    @Test
    void testNotFound2() {
        SearchResult<String> result = SearchResult.empty();
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(s -> Hope.of((int) s.charAt(0)))
            .onNotFound(() -> Hope.of(0))
            .build();
        Assertions.assertTrue(value.completeSuccess());
        Assertions.assertEquals(0, value.value());
    }
    @Test
    void testError2() {
        SearchResult<String> result = SearchResult.capture(new NullPointerException());
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(s -> Hope.of((int) s.charAt(0)))
            .onNotFound(() -> Hope.of(0))
            .build();
        Assertions.assertFalse(value.completeSuccess());
    }

    @Test
    void testFound3() {
        SearchResult<String> result = SearchResult.of("A");
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFoundOf(s -> (int) s.charAt(0))
            .onNotFoundOf(() -> 0)
            .build();
        Assertions.assertTrue(value.completeSuccess());
        Assertions.assertEquals(65, value.value());
    }
    @Test
    void testNotFound3() {
        SearchResult<String> result = SearchResult.empty();
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFoundOf(s -> (int) s.charAt(0))
            .onNotFoundOf(() -> 0)
            .build();
        Assertions.assertTrue(value.completeSuccess());
        Assertions.assertEquals(0, value.value());
    }

    @Test
    void testError3() {
        SearchResult<String> result = SearchResult.fault(MY_FAULT);
        Some<Integer> value = result
                .<Integer>valueBuilder()
                .onFound(s -> Hope.of((int) s.charAt(0)))
                .onNotFound(() -> Hope.of(0))
                .build();
        Assertions.assertFalse(value.completeSuccess());
    }
    @Test
    void testFound4() {
        SearchResult<String> result = SearchResult.of("A");
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFoundSetError(MY_FAULT)
            .onNotFoundSetError(MY_FAULT)
            .build();
        Assertions.assertFalse(value.completeSuccess());
    }
    @Test
    void testNotFound4() {
        SearchResult<String> result = SearchResult.empty();
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFoundSetError(MY_FAULT)
            .onNotFoundSetError(MY_FAULT)
            .build();
        Assertions.assertFalse(value.completeSuccess());
    }

    @Test
    void testFoundR1() {
        SearchResult<String> result = SearchResult.of("A");
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFound(s -> SearchResult.of(1))
            .onNotFound(() -> SearchResult.of(0))
            .build();
        Assertions.assertFalse(value.isFailure());
        Assertions.assertTrue(value.isFound());
        Assertions.assertFalse(value.isNotFound());
        Assertions.assertEquals(1, value.value());
    }
    @Test
    void testNotFoundR1() {
        SearchResult<String> result = SearchResult.empty();
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFound(s -> SearchResult.of(1))
            .onNotFound(() -> SearchResult.of(0))
            .build();
        Assertions.assertFalse(value.isFailure());
        Assertions.assertTrue(value.isFound());
        Assertions.assertFalse(value.isNotFound());
        Assertions.assertEquals(0, value.value());
        Assertions.assertThrows(NoSuchElementException.class, value::failure);
    }
    @Test
    void testErrorR1() {
        SearchResult<String> result = SearchResult.capture(new NullPointerException());
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFound(s -> SearchResult.of(1))
            .onNotFound(() -> SearchResult.of(0))
            .build();
        Assertions.assertTrue(value.isFailure());
        Assertions.assertFalse(value.isFound());
        Assertions.assertFalse(value.isNotFound());
        Assertions.assertThrows(NoSuchElementException.class, value::value);
        Assertions.assertDoesNotThrow(value::failure);
    }
    @Test
    void testFoundR2() {
        SearchResult<String> result = SearchResult.of("A");
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFound(s -> SearchResult.empty())
            .onNotFound(() -> SearchResult.of(0))
            .build();
        Assertions.assertFalse(value.isFailure());
        Assertions.assertFalse(value.isFound());
        Assertions.assertTrue(value.isNotFound());
        Assertions.assertThrows(NoSuchElementException.class, value::value);
    }
    @Test
    void testNotFoundR2() {
        SearchResult<String> result = SearchResult.empty();
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFound(s -> SearchResult.of(1))
            .onNotFound(SearchResult::empty)
            .build();
        Assertions.assertFalse(value.isFailure());
        Assertions.assertFalse(value.isFound());
        Assertions.assertTrue(value.isNotFound());
        Assertions.assertThrows(NoSuchElementException.class, value::value);
    }
    @Test
    void testFoundR3() {
        SearchResult<String> result = SearchResult.of("A");
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFoundSetError(MY_FAULT)
            .onNotFoundSetError(MY_FAULT)
            .build();
        Assertions.assertTrue(value.isFailure());
        Assertions.assertFalse(value.isFound());
        Assertions.assertFalse(value.isNotFound());
        Assertions.assertThrows(NoSuchElementException.class, value::value);
        Assertions.assertDoesNotThrow(value::failure);
    }

    @Test
    void testNotFoundR3() {
        SearchResult<String> result = SearchResult.empty();
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFoundSetError(MY_FAULT)
            .onNotFoundSetError(MY_FAULT)
            .build();
        Assertions.assertTrue(value.isFailure());
        Assertions.assertFalse(value.isFound());
        Assertions.assertFalse(value.isNotFound());
        Assertions.assertThrows(NoSuchElementException.class, value::value);
        Assertions.assertDoesNotThrow(value::failure);
    }
}
