package com.aol.cyclops.internal.stream;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import com.aol.cyclops.types.stream.HotStream;
import com.aol.cyclops.types.stream.PausableHotStream;

public class PausableHotStreamImpl<T> extends BaseHotStreamImpl<T> implements PausableHotStream<T>  {
	public PausableHotStreamImpl(Stream<T> stream) {
		super(stream);
	}

	public PausableHotStream<T> init(Executor exec){
		CompletableFuture.runAsync( ()-> {
			
			stream.forEach(a->{
					pause.get().join();
					int local = connected;
					
					for(int i=0;i<local;i++){
					
						connections.get(i).offer(a);
					}
					
					
				});
				
				open.set(false); 
				
					
		},exec);
		return this;
	}
	public PausableHotStream<T> paused(Executor exec){
		 super.paused(exec);
		 return this;
	}
	public void unpause(){
		super.unpause();
	}
	public void pause(){
		super.pause();
	}
}
