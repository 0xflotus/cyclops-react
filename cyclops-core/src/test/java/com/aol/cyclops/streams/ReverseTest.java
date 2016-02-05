package com.aol.cyclops.streams;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.aol.cyclops.sequence.SequenceM;

public class ReverseTest {
	@Test
	public void limitRange() throws InterruptedException{
		
		assertThat(SequenceM.range(0,Integer.MAX_VALUE)
				 .limit(100)
				 .count(),equalTo(100L));
	}
	@Test
	public void limitList() throws InterruptedException{
		
		List<Integer> list= new ArrayList<>();
		for(int i=0;i<1000;i++)
			list.add(i);
		assertThat(SequenceM.fromList(list)
				 .limit(100)
				 .count(),equalTo(100L));
		
	}
	@Test
	public void limitArray() throws InterruptedException{
		
		List<Integer> list= new ArrayList<>();
		for(int i=0;i<1000;i++)
			list.add(i);
		assertThat(SequenceM.of(list.toArray())
				 .limit(100)
				 .count(),equalTo(100L));
		
	}
	@Test
	public void skipArray() throws InterruptedException{
		
		List<Integer> list= new ArrayList<>();
		for(int i=0;i<1000;i++)
			list.add(i);
		assertThat(SequenceM.of(list.toArray())
				 .skip(100)
				 .count(),equalTo(900L));
		
	}
	@Test
	public void skipRange() throws InterruptedException{
		
		assertThat(SequenceM.range(0,1000)
				 .skip(100)
				 .count(),equalTo(900L));
	}
	@Test
	public void skipRangeReversed() throws InterruptedException{
		
		assertThat(SequenceM.range(0,1000)
				 .skip(100).reverse()
				 .count(),equalTo(900L));
	}
	@Test
	public void skipList() throws InterruptedException{
		
		List<Integer> list= new ArrayList<>();
		for(int i=0;i<1000;i++)
			list.add(i);
		assertThat(SequenceM.fromList(list)
				 .skip(100)
				 .count(),equalTo(900L));
		
	}
	@Test
	public void reversedOfArray() throws InterruptedException{
		List<Integer> list= new ArrayList<>();
		list.add(1);
		list.add(2);
		
		assertThat(SequenceM.reversedOf(1,2)
							.toList(),
							equalTo(Arrays.asList(2,1)));
		
	}
	@Test
	public void reversedOfList() throws InterruptedException{
		List<Integer> list= new ArrayList<>();
		list.add(1);
		list.add(2);
		
		assertThat(SequenceM.reversedListOf(list)
							.toList(),
							equalTo(Arrays.asList(2,1)));
		
	}
	
}
