package com.aol.cyclops.matcher;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.matcher.builders.Matching;
import com.aol.cyclops.matcher.builders.PatternMatcher;


public class MatchManyTest {

	int found = 0;
	@Test
	public void matchManyFromStream(){
		
		found = 0;
		List data = new PatternMatcher().inCaseOfType((String s) -> s.trim())
				.inCaseOfType((Integer i) -> i+100)
				.inCaseOfValue(100, i->"jackpot")
				.matchManyFromStream(Stream.of(100,200,300,100," hello "))
				.collect(Collectors.toList());
		
		assertThat((List<String>)data, hasItem("jackpot"));
		assertThat((List<Integer>)data,hasItem(200));
		assertThat((List<String>)data, hasItem("hello"));
		data.forEach(next -> { if("jackpot".equals(next)){ found++; } } );
		found++;
	}
	
	
	@Test
	public void matchMany(){
		
		List data = new PatternMatcher().inCaseOfType((String s) -> s.trim())
				.inCaseOfType((Integer i) -> i+100)
				.inCaseOfValue(100, i->"jackpot")
				.matchMany(100)
				.collect(Collectors.toList());
		
		assertThat(data.size(),is(2));
		assertThat((List<String>)data, hasItem("jackpot"));
		assertThat((List<Integer>)data,hasItem(200));
		
	}
	@Test
	public void optionalFlatMap(){
		System.out.println(Maybe.of(100).flatMap(Matching.when().isValue(100)
																	.thenApply(i->i+10)).get());
	}
	@Test
	public void unwrapped(){
		Integer num = Stream.of(1)
							.map(Matching.<Integer>when().isValue(1)
													.thenApply(i->i+10)
										.asUnwrappedFunction())
							.findFirst()
							.get();
		
		assertThat(num,is(11));
	}
	@Test(expected=NoSuchElementException.class)
	public void unwrappedMissing(){
		Integer num = Stream.of(10)
							.map(Matching.when().isValue(1).thenApply(i->i+10).asUnwrappedFunction())
							.findFirst()
							.get();
		
		fail("unreachable");
	}
	@Test
	public void streamMap(){
		Integer num = Stream.of(1)
							.map(Matching.when().isValue(1).thenApply(i->i+10).asUnwrappedFunction())
							.findFirst()
							.get();
							
		
		assertThat(num,is(11));
	}
	@Test
	public void stream(){
		Integer num = Stream.of(1)
							.flatMap(Matching.when().isValue(1).thenApply(i->i+10).asStreamFunction())
							.findFirst()
							.get();
		
		assertThat(num,is(11));
	}
	@Test(expected=NoSuchElementException.class)
	public void streamdMissing(){
		
		
		
		Integer num = Stream.of(10)
							.flatMap(Matching.when().isValue(1)
									.thenApply(i->i+10).asStreamFunction())
							.findFirst()
							.get();
							
		
		fail("unreachable");
	}
	@Test
	public void matchFromStream(){
		
	}
	
}

