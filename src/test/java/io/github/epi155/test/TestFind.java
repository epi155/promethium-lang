package io.github.epi155.test;

import io.github.epi155.pm.lang.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

@Slf4j
public class TestFind {
    private static final MsgError MY_FAULT = MsgError.of("CA01", "Oop error !!");

    @Test
    public void testFound1() {
        SearchResult<String> result = SearchResult.of("A");
        Assertions.assertDoesNotThrow(() -> result
            .onFound(s -> log.info("Found {}", s))
            .onNotFound(() -> log.info("Not found"))
            .onFailure(e -> log.warn("Error {}", e.message())));
    }
    @Test
    public void testNotFound1() {
        SearchResult<String> result = SearchResult.empty();
        Assertions.assertDoesNotThrow(() -> result
            .onFound(s -> log.info("Found {}", s))
            .onNotFound(() -> log.info("Not found"))
            .onFailure(e -> log.warn("Error {}", e.message())));
    }
    @Test
    public void testError1() {
        SearchResult<String> result = SearchResult.capture(new NullPointerException());
        Assertions.assertDoesNotThrow(() -> result
            .onFound(s -> log.info("Found {}", s))
            .onNotFound(() -> log.info("Not found"))
            .onFailure(e -> log.warn("Error {}", e.message())));
    }

    @Test
    public void testFound2() {
        SearchResult<String> result = SearchResult.of("A");
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(s -> Hope.of((int) s.charAt(0)))
            .onNotFound(() -> Hope.of(0))
            .build();
        Assertions.assertTrue(value.isSuccess());
        Assertions.assertEquals(65, value.value());
    }
    @Test
    public void testNotFound2() {
        SearchResult<String> result = SearchResult.empty();
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(s -> Hope.of((int) s.charAt(0)))
            .onNotFound(() -> Hope.of(0))
            .build();
        Assertions.assertTrue(value.isSuccess());
        Assertions.assertEquals(0, value.value());
    }
    @Test
    public void testError2() {
        SearchResult<String> result = SearchResult.capture(new NullPointerException());
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(s -> Hope.of((int) s.charAt(0)))
            .onNotFound(() -> Hope.of(0))
            .build();
        Assertions.assertFalse(value.isSuccess());
    }

    @Test
    public void testFound3() {
        SearchResult<String> result = SearchResult.of("A");
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFoundOf(s -> (int) s.charAt(0))
            .onNotFoundOf(() -> 0)
            .build();
        Assertions.assertTrue(value.isSuccess());
        Assertions.assertEquals(65, value.value());
    }
    @Test
    public void testNotFound3() {
        SearchResult<String> result = SearchResult.empty();
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFoundOf(s -> (int) s.charAt(0))
            .onNotFoundOf(() -> 0)
            .build();
        Assertions.assertTrue(value.isSuccess());
        Assertions.assertEquals(0, value.value());
    }

    @Test
    public void testError3() {
        SearchResult<String> result = SearchResult.failure(MY_FAULT);
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(s -> Hope.of((int) s.charAt(0)))
            .onNotFound(() -> Hope.of(0))
            .build();
        Assertions.assertFalse(value.isSuccess());
    }
    @Test
    public void testFound4() {
        SearchResult<String> result = SearchResult.of("A");
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(Failure.of(MY_FAULT))
            .onNotFound(Failure.of(MY_FAULT))
            .build();
        Assertions.assertFalse(value.isSuccess());
    }
    @Test
    public void testNotFound4() {
        SearchResult<String> result = SearchResult.empty();
        Some<Integer> value = result
            .<Integer>valueBuilder()
            .onFound(Failure.of(MY_FAULT))
            .onNotFound(Failure.of(MY_FAULT))
            .build();
        Assertions.assertFalse(value.isSuccess());
    }

    @Test
    public void testFoundR1() {
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
    public void testNotFoundR1() {
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
        Assertions.assertThrows(NoSuchElementException.class, value::fault);
    }
    @Test
    public void testErrorR1() {
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
        Assertions.assertDoesNotThrow(value::fault);
    }
    @Test
    public void testFoundR2() {
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
    public void testNotFoundR2() {
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
    public void testFoundR3() {
        SearchResult<String> result = SearchResult.of("A");
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFound(Failure.of(MY_FAULT))
            .onNotFound(Failure.of(MY_FAULT))
            .build();
        Assertions.assertTrue(value.isFailure());
        Assertions.assertFalse(value.isFound());
        Assertions.assertFalse(value.isNotFound());
        Assertions.assertThrows(NoSuchElementException.class, value::value);
        Assertions.assertDoesNotThrow(value::fault);
    }
    @Test
    public void testNotFoundR3() {
        SearchResult<String> result = SearchResult.empty();
        @NotNull SearchResult<Integer> value = result
            .<Integer>resultBuilder()
            .onFound(Failure.of(MY_FAULT))
            .onNotFound(Failure.of(MY_FAULT))
            .build();
        Assertions.assertTrue(value.isFailure());
        Assertions.assertFalse(value.isFound());
        Assertions.assertFalse(value.isNotFound());
        Assertions.assertThrows(NoSuchElementException.class, value::value);
        Assertions.assertDoesNotThrow(value::fault);
    }
}
