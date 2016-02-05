package com.aol.cyclops.comprehensions;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.function.BiFunction;

import org.junit.Ignore;
import org.junit.Test;

import com.aol.cyclops.comprehensions.donotation.UntypedDo;

import fj.data.Either;
import fj.data.Option;

public class DoFJOptionTest {
	@Test
	public void optionTest(){
		Option<Integer> one = Option.some(1);
		Option<Integer> empty = Option.none();
		BiFunction<Integer,Integer,Integer> f2 = (a,b) -> a *b; 
		
		Option result =  UntypedDo.add(one).add(empty).yield( (Integer a)->(Integer b) -> f2.apply(a,b));
		
		System.out.println(result);
		assertTrue(result.isNone());

	}
	@Test
	public void optionPositiveTest(){
		Option<Integer> one = Option.some(1);
		Option<Integer> empty = Option.some(3);
		BiFunction<Integer,Integer,Integer> f2 = (a,b) -> a *b; 
		
		Option result =  UntypedDo.add(one).add(empty).yield( (Integer a)->(Integer b) -> f2.apply(a,b));
		
		System.out.println(result);
		assertEquals(result.some(),3);

	}
	@Test @Ignore //fj.Either needs a specific comprehender
	public void eitherTest(){
		Either<Exception,Integer> one = Either.right(1);
		Either<Exception,Integer> empty = Either.left(new IOException());
		BiFunction<Integer,Integer,Integer> f2 = (a,b) -> a *b; 
		
		Either result =  UntypedDo.add(one).add(empty).yield( (Integer a)->(Integer b) -> f2.apply(a,b));
		
		System.out.println(result);
		assertTrue(result.isLeft());

	}
}
