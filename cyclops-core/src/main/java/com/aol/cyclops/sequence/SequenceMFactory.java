package com.aol.cyclops.sequence;

import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Stream;

import com.aol.cyclops.streams.spliterators.ReversableSpliterator;

import lombok.extern.java.Log;



public interface SequenceMFactory {

	public <T> SequenceM<T> sequenceM(Stream<T> s, ReversableSpliterator reversable);
	
	public final static SequenceMFactory instance = MetaFactory.get();
	@Log
	static class MetaFactory{
		static  SequenceMFactory get(){
			try {
				return (SequenceMFactory)Class.forName("com.aol.cyclops.sequence.SequenceMFactoryImpl").newInstance();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				log.log(Level.WARNING,"Failed to find SequenceM Factory on the classpath - please add cyclops-streams to use SequenceM");
				return null;
			}
		}
	}
}
