package com.aol.cyclops.monad;

import java.util.logging.Level;

import lombok.extern.java.Log;



public interface AnyMFactory {

	public <T> AnyM<T> of(Object o);
	public <T> AnyM<T> monad(Object o);
	public AnyMFunctions anyMonads();
	
	public final static AnyMFactory instance = MetaFactory.get();
	@Log
	static class MetaFactory{
		static  AnyMFactory get(){
			try {
				return (AnyMFactory)Class.forName("com.aol.cyclops.lambda.api.AnyMFactoryImpl").newInstance();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				log.log(Level.WARNING,"Failed to find AnyM Factory on the classpath - please add cyclops-monad-api to use AnyM");
				return null;
			}
		}
	}
}
