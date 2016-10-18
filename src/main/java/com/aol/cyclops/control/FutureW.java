package com.aol.cyclops.control;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.reactivestreams.Publisher;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.Reducer;
import com.aol.cyclops.Semigroup;
import com.aol.cyclops.control.Matchable.CheckValue1;
import com.aol.cyclops.data.collections.extensions.CollectionX;
import com.aol.cyclops.data.collections.extensions.standard.ListX;
import com.aol.cyclops.types.ConvertableFunctor;
import com.aol.cyclops.types.Filterable;
import com.aol.cyclops.types.FlatMap;
import com.aol.cyclops.types.MonadicValue;
import com.aol.cyclops.types.MonadicValue1;
import com.aol.cyclops.types.Value;
import com.aol.cyclops.types.applicative.ApplicativeFunctor;
import com.aol.cyclops.types.stream.reactive.ValueSubscriber;
import com.aol.cyclops.util.ExceptionSoftener;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A Wrapper around CompletableFuture that implements cyclops-react interfaces and provides a more standard api
 * 
 * e.g.
 *   map instead of thenApply
 *   flatMap instead of thenCompose
 *   combine instead of thenCombine (applicative functor ap)
 * 
 * @author johnmcclean
 *
 * @param <T> Type of wrapped future value
 */
/**
 * @author johnmcclean
 *
 * @param <T>
 */
@AllArgsConstructor
@EqualsAndHashCode
public class FutureW<T> implements ConvertableFunctor<T>, ApplicativeFunctor<T>, MonadicValue1<T>, FlatMap<T>, Filterable<T> {

    /**
     * An empty FutureW
     * 
     * @return A FutureW that wraps a CompletableFuture with a null result
     */
    public static <T> FutureW<T> empty() {
        return new FutureW(
                           CompletableFuture.completedFuture(null));
    }

    public static <T> FutureW<T> fromPublisher(final Publisher<T> pub, final Executor ex) {
        final ValueSubscriber<T> sub = ValueSubscriber.subscriber();
        pub.subscribe(sub);
        return sub.toFutureWAsync(ex);
    }

    public static <T> FutureW<T> fromIterable(final Iterable<T> iterable, final Executor ex) {

        return FutureW.ofSupplier(() -> Eval.fromIterable(iterable))
                      .map(e -> e.get());
    }

    public static <T> FutureW<T> fromPublisher(final Publisher<T> pub) {
        final ValueSubscriber<T> sub = ValueSubscriber.subscriber();
        pub.subscribe(sub);
        return sub.toFutureW();
    }

    public static <T> FutureW<T> fromIterable(final Iterable<T> iterable) {
        iterable.iterator();
        return FutureW.ofResult(Eval.fromIterable(iterable))
                      .map(e -> e.get());
    }

    public static <T> FutureW<T> of(final CompletableFuture<T> f) {
        return new FutureW<>(
                             f);
    }

    public static <T, X extends Throwable> FutureW<T> fromTry(final Try<T, X> value, final Executor ex) {
        return FutureW.ofSupplier(value, ex);
    }

    public static <T> FutureW<T> schedule(final String cron, final ScheduledExecutorService ex, final Supplier<T> t) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        final FutureW<T> wrapped = FutureW.of(future);
        ReactiveSeq.generate(() -> {
            try {
                future.complete(t.get());
            } catch (final Throwable t1) {
                future.completeExceptionally(t1);
            }
            return 1;

        })
                   .limit(1)
                   .schedule(cron, ex);

        return wrapped;
    }

    public static <T> FutureW<T> schedule(final long delay, final ScheduledExecutorService ex, final Supplier<T> t) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        final FutureW<T> wrapped = FutureW.of(future);

        ReactiveSeq.generate(() -> {
            try {
                future.complete(t.get());
            } catch (final Throwable t1) {
                future.completeExceptionally(t1);
            }
            return 1;

        })
                   .limit(1)
                   .scheduleFixedDelay(delay, ex);

        return wrapped;
    }

    public static <T> FutureW<ListX<T>> sequence(final CollectionX<FutureW<T>> fts) {
        return sequence(fts.stream()).map(s -> s.toListX());

    }

    public static <T> FutureW<ReactiveSeq<T>> sequence(final Stream<FutureW<T>> fts) {
        return AnyM.sequence(fts.map(f -> AnyM.fromFutureW(f)), () -> AnyM.fromFutureW(FutureW.ofResult(Stream.<T> empty())))
                   .map(s -> ReactiveSeq.fromStream(s))
                   .unwrap();

    }

    public static <T, R> FutureW<R> accumulateSuccess(final CollectionX<FutureW<T>> fts, final Reducer<R> reducer) {

        final FutureW<ListX<T>> sequenced = AnyM.sequence(fts.map(f -> AnyM.fromFutureW(f)))
                                                .unwrap();
        return sequenced.map(s -> s.mapReduce(reducer));
    }

    public static <T, R> FutureW<R> accumulate(final CollectionX<FutureW<T>> fts, final Reducer<R> reducer) {
        return sequence(fts).map(s -> s.mapReduce(reducer));
    }

    public static <T, R> FutureW<R> accumulate(final CollectionX<FutureW<T>> fts, final Function<? super T, R> mapper, final Semigroup<R> reducer) {
        return sequence(fts).map(s -> s.map(mapper)
                                       .reduce(reducer.reducer())
                                       .get());
    }

    public static <T> FutureW<T> accumulate(final CollectionX<FutureW<T>> fts, final Semigroup<T> reducer) {
        return sequence(fts).map(s -> s.reduce(reducer.reducer())
                                       .get());
    }

    public <R> Eval<R> matches(final Function<CheckValue1<T, R>, CheckValue1<T, R>> secondary,
            final Function<CheckValue1<Throwable, R>, CheckValue1<Throwable, R>> primary, final Supplier<? extends R> otherwise) {
        return toXor().swap()
                      .matches(secondary, primary, otherwise);
    }

    @Getter
    private final CompletableFuture<T> future;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.MonadicValue#coflatMap(java.util.function.Function)
     */
    @Override
    public <R> FutureW<R> coflatMap(final Function<? super MonadicValue<T>, R> mapper) {
        return (FutureW<R>) MonadicValue1.super.coflatMap(mapper);
    }

    /*
     * cojoin (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue#nest()
     */
    @Override
    public FutureW<MonadicValue<T>> nest() {
        return (FutureW<MonadicValue<T>>) MonadicValue1.super.nest();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.MonadicValue2#combine(com.aol.cyclops.Monoid,
     * com.aol.cyclops.types.MonadicValue2)
     */
    @Override
    public FutureW<T> combineEager(final Monoid<T> monoid, final MonadicValue<? extends T> v2) {
        return (FutureW<T>) MonadicValue1.super.combineEager(monoid, v2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.ConvertableFunctor#map(java.util.function.Function)
     */
    @Override
    public <R> FutureW<R> map(final Function<? super T, ? extends R> fn) {
        return new FutureW<R>(
                              future.thenApply(fn));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Functor#patternMatch(java.util.function.Function,
     * java.util.function.Supplier)
     */
    @Override
    public <R> FutureW<R> patternMatch(final Function<CheckValue1<T, R>, CheckValue1<T, R>> case1, final Supplier<? extends R> otherwise) {

        return (FutureW<R>) ApplicativeFunctor.super.patternMatch(case1, otherwise);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.function.Supplier#get()
     */
    @Override
    public T get() {
        try {
            return future.join();
        } catch (final Throwable t) {
            throw ExceptionSoftener.throwSoftenedException(t.getCause());
        }
    }

    /**
     * @return true if this FutureW is both complete, and completed without an
     *         Exception
     */
    public boolean isSuccess() {
        return future.isDone() && !future.isCompletedExceptionally();
    }

    /**
     * @return true if this FutureW is complete, but completed with an Exception
     */
    public boolean isFailed() {
        return future.isCompletedExceptionally();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Value#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return toStream().iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.lambda.monads.Unit#unit(java.lang.Object)
     */
    @Override
    public <T> FutureW<T> unit(final T unit) {
        return new FutureW<T>(
                              CompletableFuture.completedFuture(unit));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Value#stream()
     */
    @Override
    public ReactiveSeq<T> stream() {
        return ReactiveSeq.generate(() -> Try.withCatch(() -> get()))
                          .limit(1)
                          .filter(t -> t.isSuccess())
                          .map(Value::get);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.FlatMap#flatten()
     */
    @Override
    public <R> FutureW<R> flatten() {
        return FutureW.of(AnyM.fromCompletableFuture(future)
                              .flatten()
                              .unwrap());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.MonadicValue1#flatMap(java.util.function.Function)
     */
    @Override
    public <R> FutureW<R> flatMap(final Function<? super T, ? extends MonadicValue<? extends R>> mapper) {
        return FutureW.<R> of(future.<R> thenCompose(t -> (CompletionStage<R>) mapper.apply(t)
                                                                                     .toFutureW()
                                                                                     .getFuture()));
    }

    /**
     * A flatMap operation that accepts a CompleteableFuture CompletionStage as
     * the return type
     * 
     * @param mapper
     *            Mapping function
     * @return FlatMapped FutureW
     */
    public <R> FutureW<R> flatMapCf(final Function<? super T, ? extends CompletionStage<? extends R>> mapper) {
        return FutureW.<R> of(future.<R> thenCompose(t -> (CompletionStage<R>) mapper.apply(t)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Value#toXor()
     */
    @Override
    public Xor<Throwable, T> toXor() {
        try {
            return Xor.primary(future.join());
        } catch (final Throwable t) {
            return Xor.<Throwable, T> secondary(t.getCause());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Value#toIor()
     */
    @Override
    public Ior<Throwable, T> toIor() {
        try {
            return Ior.primary(future.join());
        } catch (final Throwable t) {
            return Ior.<Throwable, T> secondary(t.getCause());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.closures.Convertable#toFutureW()
     */
    @Override
    public FutureW<T> toFutureW() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.closures.Convertable#toCompletableFuture()
     */
    @Override
    public CompletableFuture<T> toCompletableFuture() {
        return this.future;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.closures.Convertable#toCompletableFutureAsync()
     */
    @Override
    public CompletableFuture<T> toCompletableFutureAsync() {
        return this.future;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.closures.Convertable#toCompletableFutureAsync(java.util.
     * concurrent.Executor)
     */
    @Override
    public CompletableFuture<T> toCompletableFutureAsync(final Executor exec) {
        return this.future;
    }

    /**
     * Returns a new FutureW that, when this FutureW completes exceptionally is
     * executed with this FutureW exception as the argument to the supplied
     * function. Otherwise, if this FutureW completes normally, then the
     * returned FutureW also completes normally with the same value.
     * 
     * @param fn
     *            the function to use to compute the value of the returned
     *            FutureW if this FutureW completed exceptionally
     * @return the new FutureW
     */
    public FutureW<T> recover(final Function<Throwable, ? extends T> fn) {
        return FutureW.of(toCompletableFuture().exceptionally(fn));
    }

    /**
     * Map this FutureW differently depending on whether the previous stage
     * completed successfully or failed
     * 
     * @param success
     *            Mapping function for successful outcomes
     * @param failure
     *            Mapping function for failed outcomes
     * @return New futureW mapped to a new state
     */
    public <R> FutureW<R> map(final Function<? super T, R> success, final Function<Throwable, R> failure) {
        return FutureW.of(future.thenApply(success)
                                .exceptionally(failure));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.lambda.monads.Functor#cast(java.lang.Class)
     */
    @Override
    public <U> FutureW<U> cast(final Class<? extends U> type) {

        return (FutureW<U>) ApplicativeFunctor.super.cast(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.lambda.monads.Functor#peek(java.util.function.Consumer)
     */
    @Override
    public FutureW<T> peek(final Consumer<? super T> c) {

        return (FutureW<T>) ApplicativeFunctor.super.peek(c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.lambda.monads.Functor#trampoline(java.util.function.
     * Function)
     */
    @Override
    public <R> FutureW<R> trampoline(final Function<? super T, ? extends Trampoline<? extends R>> mapper) {

        return (FutureW<R>) ApplicativeFunctor.super.trampoline(mapper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return mkString();
    }

    /**
     * Construct a successfully completed FutureW from the given value
     * 
     * @param result
     *            To wrap inside a FutureW
     * @return FutureW containing supplied result
     */
    public static <T> FutureW<T> ofResult(final T result) {
        return FutureW.of(CompletableFuture.completedFuture(result));
    }

    /**
     * Construct a completed-with-error FutureW from the given Exception
     * 
     * @param error
     *            To wrap inside a FutureW
     * @return FutureW containing supplied error
     */
    public static <T> FutureW<T> ofError(final Throwable error) {
        final CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(error);

        return FutureW.<T> of(cf);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Convertable#isPresent()
     */
    @Override
    public boolean isPresent() {
        return !this.future.isCompletedExceptionally();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Value#mkString()
     */
    @Override
    public String mkString() {
        return "FutureW[" + future.toString() + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Filterable#filter(java.util.function.Predicate)
     */
    @Override
    public Maybe<T> filter(final Predicate<? super T> fn) {
        return toMaybe().filter(fn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Filterable#ofType(java.lang.Class)
     */
    @Override
    public <U> Maybe<U> ofType(final Class<? extends U> type) {

        return (Maybe<U>) Filterable.super.ofType(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Filterable#filterNot(java.util.function.Predicate)
     */
    @Override
    public Maybe<T> filterNot(final Predicate<? super T> fn) {

        return (Maybe<T>) Filterable.super.filterNot(fn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Filterable#notNull()
     */
    @Override
    public Maybe<T> notNull() {

        return (Maybe<T>) Filterable.super.notNull();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Convertable#toOptional()
     */
    @Override
    public Optional<T> toOptional() {
        if (future.isDone() && future.isCompletedExceptionally())
            return Optional.empty();

        try {
            return Optional.ofNullable(get());
        } catch (final Throwable t) {
            return Optional.empty();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Convertable#toFutureWAsync()
     */
    @Override
    public FutureW<T> toFutureWAsync() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.Convertable#toFutureWAsync(java.util.concurrent.
     * Executor)
     */
    @Override
    public FutureW<T> toFutureWAsync(final Executor ex) {
        return this;
    }

    /*
     * Apply a function across two values at once. (non-Javadoc)
     * 
     * @see
     * com.aol.cyclops.types.applicative.ApplicativeFunctor#combine(com.aol.
     * cyclops.types.Value, java.util.function.BiFunction)
     */
    @Override
    public <T2, R> FutureW<R> combine(final Value<? extends T2> app, final BiFunction<? super T, ? super T2, ? extends R> fn) {
        if (app instanceof FutureW) {
            return FutureW.of(future.thenCombine(((FutureW<T2>) app).getFuture(), fn));
        }
        return (FutureW<R>) ApplicativeFunctor.super.zip(app, fn);
    }

    /*
     * Equivalent to combine, but accepts an Iterable and takes the first value
     * only from that iterable. (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.lang.Iterable,
     * java.util.function.BiFunction)
     */
    @Override
    public <T2, R> FutureW<R> zip(final Iterable<? extends T2> app, final BiFunction<? super T, ? super T2, ? extends R> fn) {

        return (FutureW<R>) ApplicativeFunctor.super.zip(app, fn);
    }

    /*
     * Equivalent to combine, but accepts a Publisher and takes the first value
     * only from that publisher.
     * 
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.util.function.BiFunction,
     * org.reactivestreams.Publisher)
     */
    @Override
    public <T2, R> FutureW<R> zip(final BiFunction<? super T, ? super T2, ? extends R> fn, final Publisher<? extends T2> app) {
        return (FutureW<R>) ApplicativeFunctor.super.zip(fn, app);

    }

    /**
     * Create a FutureW object that asyncrhonously populates using the Common
     * ForkJoinPool from the user provided Supplier
     * 
     * @param s
     *            Supplier to asynchronously populate results from
     * @return FutureW asynchronously populated from the Supplier
     */
    public static <T> FutureW<T> ofSupplier(final Supplier<T> s) {
        return FutureW.of(CompletableFuture.supplyAsync(s));
    }

    /**
     * Create a FutureW object that asyncrhonously populates using the provided
     * Executor and Supplier
     * 
     * @param s
     *            Supplier to asynchronously populate results from
     * @param ex
     *            Executro to asynchronously populate results with
     * @return FutureW asynchronously populated from the Supplier
     */
    public static <T> FutureW<T> ofSupplier(final Supplier<T> s, final Executor ex) {
        return FutureW.of(CompletableFuture.supplyAsync(s, ex));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(org.jooq.lambda.Seq,
     * java.util.function.BiFunction)
     */
    @Override
    public <U, R> FutureW<R> zip(final Seq<? extends U> other, final BiFunction<? super T, ? super U, ? extends R> zipper) {
        return (FutureW<R>) ApplicativeFunctor.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.util.stream.Stream,
     * java.util.function.BiFunction)
     */
    @Override
    public <U, R> FutureW<R> zip(final Stream<? extends U> other, final BiFunction<? super T, ? super U, ? extends R> zipper) {
        return (FutureW<R>) ApplicativeFunctor.super.zip(other, zipper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.util.stream.Stream)
     */
    @Override
    public <U> FutureW<Tuple2<T, U>> zip(final Stream<? extends U> other) {
        return (FutureW) ApplicativeFunctor.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(org.jooq.lambda.Seq)
     */
    @Override
    public <U> FutureW<Tuple2<T, U>> zip(final Seq<? extends U> other) {
        return (FutureW) ApplicativeFunctor.super.zip(other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.aol.cyclops.types.Zippable#zip(java.lang.Iterable)
     */
    @Override
    public <U> FutureW<Tuple2<T, U>> zip(final Iterable<? extends U> other) {
        return (FutureW) ApplicativeFunctor.super.zip(other);
    }

    /**
     * Flat map the wrapped Streamable and return the first element
     *
     * @param mapper FlatMap function with Iterable type returned value
     * @return Future typed the first element returned after the flatMap function is applied
     */
    @Override
    public <R> FutureW<R> flatMapIterable(final Function<? super T, ? extends Iterable<? extends R>> mapper) {
        return (FutureW<R>) MonadicValue1.super.flatMapIterable(mapper);
    }

    /**
     * Flat map the wrapped Streamable and return the element published
     *
     * @param mapper FlatMap function with Publisher type returned value
     * @return FutureW typed value subscribed from publisher after the flatMap function is applied
     */
    @Override
    public <R> FutureW<R> flatMapPublisher(final Function<? super T, ? extends Publisher<? extends R>> mapper) {
        return (FutureW<R>) MonadicValue1.super.flatMapPublisher(mapper);
    }

}
