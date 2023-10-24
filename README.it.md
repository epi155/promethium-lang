# promethium-lang

## 1. Introduzione
In alcuni casi dobbiamo leggere una grande quantità di dati, leggerli, uno per uno, e produrre un risultato per ogni singolo dato.
Alcuni dati in ingresso possono generare un errore, questo deve essere gestito. In questo caso non viene prodotto il relativo risultato, ma viene segnalato l'errore in un rapporto finale.

### 1.1 Iterable
Prendiamo come esempio che l'origine dati sia un `Iterable`. I dati vengono iterati subendo due trasformazioni, in caso di errore vengono lanciate delle eccezioni specifiche. La prima trasformazione sarà:
```java
class Step1 {
    TempData firstStep(CustomInput input) throws FirstException;
}
```
e la seconda:
```java
class Step2 {
    CustomOutput secondStep(TempData tempData) throws SecondException;
}
```
se non ci sono errori il risultato viene salvato. Altrimenti bisogna catturare le eccezioni ed accumulare gli errori.

Il codice potrebbe essere qualcosa del tipo:
```java
class Demo1 {
    void example1(CustomReader rd, CustomWriter wr) {
        List<CustomError> errors = new ArrayList<>();
        Iterable<CustomInput> iterable = rd.Iterable();
        for(CustomInput input: iterable) {
            try {
                TempData tempData =  firstStep(input);
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
La libreria **promethium-lang** mette a disposizione delle classi che possono gestire un risultato o, in alternativa, un errore, o una lista di errori.
I metodi di elaborazione intermedia dovranno restituire una  di queste classi invece di lanciare una accezione. Diventeranno qualcosa del tipo:
```java
class Demo2 {
    Hope<TempData> firstStep(CustomInput input);
    Hope<CustomOutput> secondStep(TempData tempData);
}
```
La classe `Hope` gestisce un valore o, in alternativa, un singolo errore. La classe ha dei metodi per sapere se è presente il valore o l'errore, e come prendere il valore o l'errore. Ma ha anche dei metodi che permettono una gestione più evoluta; questi metodi usano `Consumer` per gestire il valore o l'errore.
Usando i metodi `onSuccess`/`onFailure` possiamo riscrivere il codice come:
```java
class Demo3 {
    void example2(CustomReader rd, CustomWriter wr) {
        val bld = None.builder();
        Iterable<CustomInput> iterable = rd.Iterable();
        for(CustomInput input: iterable) {
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
Se la gestione dell'errore si limita ad accumulare l'errore è possibile usare altri metodi che accumulano gli errori
automaticamente. Usando `ergo`/`implies` possiamo riscrivere il tutto come:
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

Il `Consumer` del metodo `ergo` viene eseguito solo se `firstStep` termina senza errori, altrimenti viene accumulato
l'errore di `firstStep`; analogamente il `Consumer` del metodo `implies` viene eseguito se `secondStep` termina senza
errori, altrimenti viene accumulato l'errore di `secondStep`.

Scritto in questo modo il `Consumer` del metodo `implies` ha visibilità del campo `temp`. Se questa visibilità non è
necessaria, è possibile usare il metodo `map` e si può riscrivere il tutto con come:
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

esempio con `Stream`/`collect`
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

## 2. Some, None, Hope, Nope, SearchResult

Nella sezione precendente abbiamo visto gli oggetti `Hope` e `None`.
Il primo contiene un valore o un errore, l'altro un elenco di errori.

Vediamo in dettagli tutti gli oggetti messi a disposizione dalla libreria che possono essere utili come risposta di un
metodo

`Some`
: questo oggetto può contenere un valore, un valore e dei warning, degli errori, degli errori e warning.

`None`
: questo oggetto è simile al `Some`, ma non contiene nessun valore; come il `Some` può contenere errori e warning.

`Hope`
: questo oggetto può contenere un valore o un errore.

`Nope`
: questo oggetto è simile a `Hope`, ma senza il valore;

La classe ha i metodi

* `completeSuccess()` che indica che è presente un valore, nessun warning, nessun errore.
* `completeWarning`() che indica che è presente un valore e alcuni warning, nessun errore.
* `completeWithErrors()` che indica che sono presenti alcuni errori, potrebbero essere presenti dei warning, non è
  presente nessun valore.
* `completeWithoutErrors()` che indica che è presente un valore, potrebbero essere presenti dei warning, nessun errore.

## 3. Failure, Warning
