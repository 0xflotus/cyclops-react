package com.aol.cyclops.lambda.tuple;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Seq;

import com.aol.cyclops.closures.mutable.Mutable;
import com.aol.cyclops.comprehensions.donotation.Doable;
import com.aol.cyclops.matcher2.Matchable;
import com.aol.cyclops.sequence.Monoid;
import com.aol.cyclops.sequence.streamable.ToStream;
import com.aol.cyclops.value.ValueObject;

import lombok.AllArgsConstructor;




public interface CachedValues extends Iterable, ValueObject, ToStream, Doable, Matchable, Comparable<CachedValues>{

	
	
	default Object getMatchable(){
		return getCachedValues();
	}
	public List<Object> getCachedValues();
	
	CachedValues withArity(int arity);
	@AllArgsConstructor
	public static class ConvertStep<T extends CachedValues>{
		private final T c;
		public <X> X to(Class<X> to){
			return (X)c.to(to);
		}
	}
	
	default <T extends CachedValues> ConvertStep<T> convert(){
		return new ConvertStep(this);
	}
	default <X> X to(Class<X> to){
		Constructor<X> cons = (Constructor)Stream.of(to.getConstructors())
							.filter(c -> c.getParameterCount()==arity())
							.findFirst()
							.get();
		try {
			
			return cons.newInstance(getCachedValues().toArray());
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			return new ParamMatcher().create(to, arity(), getCachedValues(),e);
			
		}
		
		
		
	}
	/**
	 * Wrap multiple reducers or monoids into a single reducer instance,
	 * that can be used to reduce a given steam to multiple different values simultanously
	 * <pre>
	 * {@code
	 * 
	 * Monoid<Integer> sum = Monoid.of(0,(a,b)->a+b);
	 * Monoid<Integer> mult = Monoid.of(1,(a,b)->a*b);
	   val result = tuple(sum,mult).<PTuple2<Integer,Integer>>asReducer()
											.mapReduce(Stream.of(1,2,3,4)); 
		 
		assertThat(result,equalTo(tuple(10,24)));
	 * 
	 * }
	 * </pre>
	 * 
	 * Or alternatively 
	 * 
	 * <pre>
	 * {@code
	 * 
	 * Monoid<String> concat = Monoid.of("",(a,b)->a+b);
	   Monoid<String> join = Monoid.of("",(a,b)->a+","+b);
	   Monoid<CachedValues> reducer = PowerTuples.tuple(concat,join).asReducer(); 
	 * 
	 * 
	 * assertThat(Stream.of("hello", "world", "woo!").map(CachedValues::of)
		                  .reduce(reducer.zero(),reducer.reducer())
		                  ,equalTo(tuple("helloworldwoo!",",hello,world,woo!")));
	 * }
	 * </pre>
	 * @return
	 */
	default <T extends CachedValues> Monoid<T> asReducer(){
		List<Monoid> reducers = (List)getCachedValues().stream().filter(c-> c instanceof Monoid).collect(Collectors.toList());
		return new Monoid(){
			public CachedValues zero(){
				return new TupleImpl(reducers.stream().map(r->r.zero()).collect(Collectors.toList()),arity());
			}
			public BiFunction<CachedValues,CachedValues,CachedValues> combiner(){
				return (c1,c2) -> { 
					List l= new ArrayList<>();
					
					
					for(int i=0;i<reducers.size();i++){
						l.add(reducers.get(i).combiner().apply(c1.getCachedValues().get(i),c2.getCachedValues().get(0)));
					}
					
					return new TupleImpl(l,l.size());
				};
			}
			
		
			public Stream mapToType(Stream stream){
				return (Stream) stream.map(CachedValues::of);
			}
		};
	}
	/**
	 * Wrap multiple collectors in a single Collector instance, so they can all run against a single Stream
	 * e.g 
	 * {@code
	 *  PTuple2<Set<Integer>,List<Integer>> res = Stream.of(1, 2, 2)
                       .collect(tuple(Collectors.toSet(),Collectors.toList()).asCollector());
	 * 
	 * }
	 * 
	 * Filters all non-Collector instances out of the Tuple
	 * 
	 * @return Collector
	 */
	default <T,A,R> Collector<T,A,R> asCollector(){
		
		List<Collector> collectors = (List)getCachedValues().stream().filter(c-> c instanceof Collector).collect(Collectors.toList());
		final Supplier supplier =  ()-> collectors.stream().map(c->c.supplier().get()).collect(Collectors.toList());
		final BiConsumer accumulator = (acc,next) -> {  Seq.seq(collectors.stream().iterator()).<Object,PTuple2<Collector,Object>>zip(Seq.seq((List)acc),(a,b)->PowerTuples.<Collector,Object>tuple(a,b))
													
													.forEach( t -> t.v1().accumulator().accept(t.v2(),next));
		};
		final BinaryOperator combiner = (t1,t2)-> new TupleImpl(collectors.stream().map(c->c.combiner().apply(t1,t2)).collect(Collectors.toList()),arity());
		
		return (Collector) Collector.of( supplier,
                accumulator ,
		 combiner,
		values-> new TupleImpl(Seq.seq(collectors.stream().iterator()).<Object,PTuple2<Collector,Object>>zip(Seq.seq((List)values),(a,b)->PowerTuples.<Collector,Object>tuple(a,b)).map(t->t.v1().finisher().apply(t.v2())).toList(),arity()));
	}


	@Override
	default int compareTo(CachedValues o){
		if(o==null)
			return 1;
		if(o.getCachedValues()==null){
			if(getCachedValues()==null){
				return 0;
			}
			return 1;
		}
		else if(getCachedValues()==null)
			return -1;
		for(int i=0;i<getCachedValues().size();i++){
			if(i>=getCachedValues().size())
				return 1;
			int res = Objects.compare(getCachedValues().get(i), o.getCachedValues().get(i), (a,b) ->  a==null ? (b==null ? 0 : -1) : b==null? 1 : ((Comparable)a).compareTo(b) );
			if(res!=0)
				return res;
			
		}
			return 0;
	}
	
	default void forEach(Consumer c){
		getCachedValues().forEach(c);
	}
	
	default <T extends CachedValues> T filter(Predicate<PTuple2<Integer,Object>> p){
		Mutable<Integer> index = new Mutable(-1);
		List newList = getCachedValues().stream().map(v-> PowerTuples.tuple(index.set(index.get()+1).get(),v))
						.filter(p).map(PTuple2::v2).collect(Collectors.toList());
		return (T)new TupleImpl(newList,newList.size());
	}
	default <T> List<T> toList(){
		return (List)getCachedValues();
	}
	default <K,V> Map<K,V> toMap(){
		Map result = new HashMap<>();
		Iterator it = getCachedValues().iterator();
		if(arity()%2==0){
			for(int i=0;i+1<arity();i=i+2){
				result.put(getCachedValues().get(i), getCachedValues().get(i+1));
			}
		}
		
		return result;
	}
	public int arity();
	/**
	 * Will attempt to convert each element in the tuple into a flattened Stream
	 * 
	 * @return Flattened Stream
	 */
	default <T extends Stream<?>> T asFlattenedStream(){
		return (T)asStreams().flatMap(s->s);
	}
	/**
	 * Will attempt to convert each element in the tuple into a Stream
	 * 
	 * Collection::stream
	 * CharSequence to stream
	 * File to Stream
	 * URL to Stream
	 * 
	 * @return Stream of Streams
	 */
	default <T extends Stream<?>> Stream<T> asStreams(){
		//each value where stream can't be called, should just be an empty Stream
		return (Stream)getCachedValues().stream()
					.filter(o->o!=null)
					.map(o->DynamicInvoker.invokeStream(o.getClass(), o));
	}
	default Stream<String> asStreamOfStrings(){
		
		return (Stream)getCachedValues().stream()
					.filter(o->o!=null)
					.map(Object::toString);
					
	}
	@Override
	default Iterator iterator(){
		return getCachedValues().iterator();
	}
	
	default Stream stream(){
		return getCachedValues().stream();
	}
	
	default <T extends CachedValues,X> T append(X value){
		List list = new ArrayList(getCachedValues());
		list.add(value);
		return (T)new TupleImpl(list,list.size());
		
	}
	default <T extends CachedValues> T appendAll(CachedValues values){
		List list = new ArrayList(getCachedValues());
		list.addAll(values.getCachedValues());
		return (T)new TupleImpl(list,list.size());
		
	}
	default <T extends CachedValues, X extends CachedValues> T flatMap(Function<X,T> fn){
		return fn.apply((X)this);
	}
	
	default <T extends CachedValues> T map(Function<List,List> fn){
		List list = fn.apply(getCachedValues());
		return (T)new TupleImpl(list,list.size());
	}
	 
	public static <T> CachedValues of(T value){
		return new TupleImpl(Arrays.asList(value),1);
	}
	public static  CachedValues  of(Object... values){
		return new TupleImpl(Arrays.asList(values),values.length);
	}
}
