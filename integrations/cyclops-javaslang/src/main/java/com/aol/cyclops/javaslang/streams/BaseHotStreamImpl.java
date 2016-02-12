package com.aol.cyclops.javaslang.streams;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import javaslang.collection.Stream;
import uk.co.real_logic.agrona.concurrent.OneToOneConcurrentArrayQueue;

import com.aol.cyclops.internal.stream.IteratorHotStream;
import com.aol.cyclops.internal.stream.spliterators.ClosingSpliterator;
import com.aol.cyclops.javaslang.FromJDK;
import com.aol.cyclops.sequence.SequenceM;

public abstract class BaseHotStreamImpl<T> extends IteratorHotStream<T> implements JavaslangHotStream<T>{
	
	protected final Stream<T> stream;
	
	public BaseHotStreamImpl(Stream<T> stream){
		this.stream = stream;	
	}
	
	public JavaslangHotStream<T> paused(Executor exec){
		pause();
		return init(exec);
	}
	public abstract JavaslangHotStream<T> init(Executor exec);
	
	public JavaslangHotStream<T> schedule(String cron,ScheduledExecutorService ex){
		final Iterator<T> it = stream.iterator();
		 scheduleInternal(it,cron,ex);
		 return this;
	}
	
	
	public JavaslangHotStream<T> scheduleFixedDelay(long delay,ScheduledExecutorService ex){
		final Iterator<T> it = stream.iterator();
		scheduleFixedDelayInternal(it,delay,ex);
		return this;
		
	}
	public JavaslangHotStream<T> scheduleFixedRate(long rate,ScheduledExecutorService ex){
		final Iterator<T> it = stream.iterator();
		scheduleFixedRate(it,rate,ex);
		return this;
	}
	


	@Override
	public Stream<T> connect() {
		unpause();
		return connect(new OneToOneConcurrentArrayQueue<T>(256));
	}

	@Override
	public Stream<T> connect(Queue<T> queue) {
		unpause();
		connections.getAndSet(connected, queue);
		connected++;
		return FromJDK.stream(SequenceM.fromStream(StreamSupport.stream(
                new ClosingSpliterator(Long.MAX_VALUE, queue,open), false)));
	}

	@Override
	public <R extends Stream<T>> R connectTo(Queue<T> queue, Function<Stream<T>, R> to) {
		return to.apply(connect(queue));
	}
	
}
