# promethium-lang

## 1. Introduction

In some cases we need to read a large amount of data, read it, one by one, and produce a result for each individual
piece of data.
Some input data may generate an error, this must be handled. In this case the relevant result is not produced, but the
error is reported in a final report.

### 1.1 Iterable

Let's take as an example that the data source is an `Iterable`. The data is iterated by undergoing two transformations,
in case of error specific exceptions are thrown. The first transformation will be:

```java
class Step1 {
    TempData firstStep(CustomInput input) throws FirstException;
}
```

and the second:

```java
class Step2 {
    CustomOutput secondStep(TempData tempData) throws SecondException;
}
```

if there are no errors the result is saved. Otherwise you have to catch exceptions and accumulate errors.

The code could be something like:

```java
class Demo1 {
    void example1(CustomReader rd, CustomWriter wr) {
        List<CustomError> errors = new ArrayList<>();
        Iterable<CustomInput> iterable = rd.Iterable();
        for (CustomInput input : iterable) {
            try {
                TempData tempData = firstStep(input);
                try {
                    CustomOutput output = secondStep(tempData);
                    wr.write(output);
                } catch (SecondException e) {
                    errors.add(CustomError.of(tempData, e));
                }
            } catch (FirstException e) {
                errors.add(CustomError.of(input, e));
            }
        }
        report(errors);
    }
}
```

The **promethium-lang** library provides classes that can handle a result or, alternatively, an error, or a list of
errors.
Intermediate processing methods should return one of these classes instead of throwing an exception. They will become
something like:

```java
class Demo2 {
    Hope<TempData> firstStep(CustomInput input);

    Hope<CustomOutput> secondStep(TempData tempData);
}
```

The `Hope` class handles a value or, alternatively, a single error. The class has methods for knowing whether the value
or error is present, and how to get the value or error. But it also has methods that allow for more advanced management;
these methods use `Consumer` to handle the value or error.
Using the `onSuccess`/`onFailure` methods we can rewrite the code as:

```java
class Demo3 {
    void example2(CustomReader rd, CustomWriter wr) {
        val bld = None.builder();
        Iterable<CustomInput> iterable = rd.Iterable();
        for (CustomInput input : iterable) {
            firstStep(input)
                    .onSuccess(temp -> secondStep(temp)
                            .onSuccess(wr::write)
                            .onFailure(bld::add))
                    .onFailure(bld::add);
        }
        None none = bld.build();
        report(none.errors());
    }
}
```

If the error handling is limited to accumulating the error, it is possible to use other methods that accumulate the
errors automatically.
Using `ergo`/`implies` we can rewrite this as:

```java
class Demo4 {
    void example3(CustomReader rd, CustomWriter wr) {
        Iterable<CustomInput> iterable = rd.Iterable();
        None none = None.iterableOf(iterable)
                .forEach(input -> firstStep(input)
                        .ergo(temp -> secondStep(temp)
                                .implies(wr::write)));
        report(none.errors());
    }
}
```

The `Consumer` of the `ergo` method is executed only if `firstStep` finishes without errors, otherwise the error
of `firstStep` is accumulated;
similarly the `Consumer` of the `implies` method is executed if `secondStep` finishes without errors, otherwise
the `secondStep` error is accumulated.

Written this way the `Consumer` of the `implies` method has visibility of the `temp` field.
If this visibility is not needed, you can use the `map` method and you can rewrite everything as:

```java
class Demo5 {
    void example4(CustomReader rd, CustomWriter wr) {
        Iterable<CustomInput> iterable = rd.Iterable();
        None none = None.iterableOf(iterable)
                .forEach(input -> firstStep(input)
                        .map(this::secondStep)
                        .implies(wr::write));
        report(none.errors());
    }
}
```

example with `Stream`/`collect`

```java
class Demo6 {
    void example5(CustomReader rd, CustomWriter wr) {
        Stream<CustomInput> stream = rd.stream();
        None none = stream
                .map(input -> firstStep(input)
                        .map(this::secondStep)
                        .implies(wr::write))
                .collect(None.collect());
        report(none.errors());
    }
}
```