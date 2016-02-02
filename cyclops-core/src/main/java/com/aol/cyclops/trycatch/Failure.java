package com.aol.cyclops.trycatch;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.aol.cyclops.invokedynamic.ExceptionSoftener;
import com.aol.cyclops.monad.AnyM;

/**
 * Class that represents the Failure of a Try
 * 
 * @author johnmcclean
 *
 * @param <T> Value type
 * @param <X> Error type
 */
@RequiredArgsConstructor @ToString @EqualsAndHashCode
public class Failure<T,X extends Throwable> implements Try<T,X> {

	@Override
	public <T extends Iterable<?>> T unapply() {
		return (T)Arrays.asList(error);
	}
	private final X error;
	
	/**
	 * @return This monad, wrapped as AnyM of Success
	 */
	public AnyM<T> anyM(){
		return this.anyMSuccess();
	}
	/**
	 * @return This monad, wrapped as AnyM of Failure
	 */
	public AnyM<X> anyMFailure(){
		return AnyM.ofMonad(this);
	}
	/**
	 * @return This monad, wrapped as AnyM of Success
	 */
	public AnyM<T> anyMSuccess(){
		return AnyM.fromOptional(Optional.empty());
	}
	/**
	 * Construct a Failure instance from a throwable
	 * 
	 * @param error for Failure
	 * @return new Failure with error
	 */
	public static <T,X extends Throwable> Failure<T,X> of(X error){
		return new Failure<>(error);
	}
	/**
	 * Construct a Failure instance from a throwable
	 * 
	 * @param error for Failure
	 * @return new Failure with error
	 */
	public static <T,X extends Throwable> AnyM<X> anyMOf(X error){
		return new Failure<>(error).anyMFailure();
	}
	/* 
	 *	@return throws an Exception
	 * @see com.aol.cyclops.trycatch.Try#get()
	 */
	public T get(){
		ExceptionSoftener.throwSoftenedException((Throwable)error);
		return null;
	}

	/* 
	 *	@return this
	 * @see com.aol.cyclops.trycatch.Try#map(java.util.function.Function)
	 */
	@Override
	public <R> Try<R,X> map(Function<T, R> fn) {
		return (Failure)this;
	}

	/* 
	 *	@return this
	 * @see com.aol.cyclops.trycatch.Try#flatMap(java.util.function.Function)
	 */
	@Override
	public <R> Try<R,X> flatMap(Function<T, Try<R,X>> fn) {
		return (Try)this;
	}
	
	/* 
	 *	@return Empty optional
	 * @see com.aol.cyclops.trycatch.Try#filter(java.util.function.Predicate)
	 */
	@Override
	public Optional<T> filter(Predicate<T> p) {
		return Optional.empty();
	}
	
	/* 
	 * FlatMap recovery function if exception is of specified type
	 * 
	 * @param t Type of exception to match against
	 * @param fn Recovery FlatMap function. Map from a failure to a Success
	 * @return Success from recovery function
	 * @see com.aol.cyclops.trycatch.Try#recoverWithFor(java.lang.Class, java.util.function.Function)
	 */
	@Override
	public Try<T,X> recoverWithFor(Class<? extends X> t,Function<X, Success<T,X>> fn){
		if(t.isAssignableFrom(error.getClass()))
			return recoverWith(fn);
		return this;
	}
	
	
	/* 
	 * Recover if exception is of specified type
	 * @param t Type of exception to match against
	 * @param fn Recovery function
	 * @return New Success
	 * @see com.aol.cyclops.trycatch.Try#recoverFor(java.lang.Class, java.util.function.Function)
	 */
	@Override
	public Try<T,X> recoverFor(Class<? extends X> t,Function<X, T> fn){
		if(t.isAssignableFrom(error.getClass()))
			return recover(fn);
		return this;
	}
	
	/* 
	 * @param fn Recovery function - map from a failure to a Success.
	 * @return new Success
	 * @see com.aol.cyclops.trycatch.Try#recover(java.util.function.Function)
	 */
	@Override
	public Success<T,X> recover(Function<X, T> fn) {
		return Success.of(fn.apply(error));
	}
	
	/* 
	 * flatMap recovery
	 * 
	 * @param fn Recovery FlatMap function. Map from a failure to a Success
	 * @return Success from recovery function
	 * @see com.aol.cyclops.trycatch.Try#recoverWith(java.util.function.Function)
	 */
	@Override
	public  Success<T,X> recoverWith(Function<X,Success<T,X>> fn){
		return fn.apply(error);
	}
	/* 
	 * Flatten a nested Try Structure
	 * @return Lowest nested Try
	 * @see com.aol.cyclops.trycatch.Try#flatten()
	 */
	@Override
	public Try<T,X> flatten() {
		return this;
	}
	/* 
	 *  @param value Return value supplied 
	 * @return  supplied value
	 * @see com.aol.cyclops.trycatch.Try#orElse(java.lang.Object)
	 */
	@Override
	public T orElse(T value) {
		return value;
	}
	/* 
	 * @param value from supplied Supplier 
	 * @return value from supplier
	 * @see com.aol.cyclops.trycatch.Try#orElseGet(java.util.function.Supplier)
	 */
	@Override
	public T orElseGet(Supplier<? extends T> value) {
		return value.get();
	}
	/* 
	 *	@return Optional.empty()
	 * @see com.aol.cyclops.trycatch.Try#toOptional()
	 */
	@Override
	public Optional<T> toOptional() {
		return Optional.empty();
	}
	/* 
	 *	@return empty Stream
	 * @see com.aol.cyclops.trycatch.Try#toStream()
	 */
	@Override
	public Stream<T> stream() {
		return Stream.of();
	}
	/* 
	 *	@return false
	 * @see com.aol.cyclops.trycatch.Try#isSuccess()
	 */
	@Override
	public boolean isSuccess() {
		return false;
	}
	/*  
	 *	@return true
	 * @see com.aol.cyclops.trycatch.Try#isFailure()
	 */
	@Override
	public boolean isFailure() {
		return true;
	}
	/* 
	 *	does nothing
	 * @see com.aol.cyclops.trycatch.Try#foreach(java.util.function.Consumer)
	 */
	@Override
	public void foreach(Consumer<T> consumer) {
		
		
	}
	/* 
	 *	@param consumer is passed error
	 *	@return this
	 * @see com.aol.cyclops.trycatch.Try#onFail(java.util.function.Consumer)
	 */
	@Override
	public Try<T,X> onFail(Consumer<X> consumer) {
		consumer.accept(error);
		return this;
	}
	/* 
	 * @param t Class type of match Exception against
	 * @param consumer Accept Exception if present
	 * @return this
	 * @see com.aol.cyclops.trycatch.Try#onFail(java.lang.Class, java.util.function.Consumer)
	 */
	@Override
	public Try<T, X> onFail(Class<? extends X> t, Consumer<X> consumer) {
		if(t.isAssignableFrom(error.getClass()))
			consumer.accept(error);
		return this;
	}
	/* 
	 *	
	 * @see com.aol.cyclops.trycatch.Try#throwException()
	 */
	@Override
	public void throwException() {
		ExceptionSoftener.throwSoftenedException(error);
		
	}
	/* 
	 * @return Optional containing error
	 * @see com.aol.cyclops.trycatch.Try#toFailedOptional()
	 */
	@Override
	public Optional<X> toFailedOptional() {
		
		return Optional.of(error);
	}
	/* 
	 *	@return Stream containing error
	 * @see com.aol.cyclops.trycatch.Try#toFailedStream()
	 */
	@Override
	public Stream<X> toFailedStream() {
		return Stream.of(error);
	}
	/* 
	 * @param consumer that will accept error
	 * @see com.aol.cyclops.trycatch.Try#foreachFailed(java.util.function.Consumer)
	 */
	@Override
	public void foreachFailed(Consumer<X> consumer) {
		consumer.accept(error);
		
	}
}
