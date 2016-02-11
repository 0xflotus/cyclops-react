package com.aol.cyclops.value;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;

import org.junit.Test;

import com.aol.cyclops.Monoid;
import com.aol.cyclops.dynamic.As;
import com.aol.cyclops.matcher2.CheckValues;

public class AsTest {

/**
	@Test
	public void testAsMonoidFj() {
		
		fj.Monoid m = fj.Monoid.monoid((Integer a) -> (Integer b) -> a+b,0);
		Monoid<Integer> sum = As.asMonoid(m);
		
		assertThat(sum.reduce(Stream.of(1,2,3)),equalTo(6));
	}
	
	@AllArgsConstructor
	static class MyCase2 {
		int a;
		int b;
		int c;
	}
	@Test
	public void asMatchableTest(){
		
		
			assertThat(As.asMatchable(new MyCase2(1,2,3))
					.matches(
							c->c.hasValues(1,2,3).then(i->"hello"),
							c->c.hasValues(4,5,6).then(i->"goodbye")
							),equalTo("hello"));
			
	
	}
	@Test
	public void asMatchableTest2(){
		
		
			assertThat(As.asMatchable(new MyCase2(1,2,3))
					.mayMatch(
							c->c.hasValues(1,2,3).then(i->"hello"),
							c->c.hasValues(4,5,6).then(i->"goodbye")
							).orElse("default"),equalTo("hello"));
			
	
	}
	@Test
	public void testAsStreamableT() {
		val result = As.<Integer>asStreamableFromObject(Arrays.asList(1,2,3)).stream().map(i->i+2).collect(Collectors.toList());
		
		assertThat(result,equalTo(Arrays.asList(3,4,5)));
	}

	@Test
	public void testAsStreamableStreamOfT() {
		Stream<Integer> stream = Stream.of(1,2,3,4,5);
		val streamable = As.<Integer>asStreamable(stream);
		val result1 = streamable.stream().map(i->i+2).collect(Collectors.toList());
		val result2 = streamable.stream().map(i->i+2).collect(Collectors.toList());
		val result3 = streamable.stream().map(i->i+2).collect(Collectors.toList());
		
		assertThat(result1,equalTo(Arrays.asList(3,4,5,6,7)));
		assertThat(result1,equalTo(result2));
		assertThat(result1,equalTo(result3));
	}
	

	
	@Test
	public void asFunctor(){
		Object mappedStream = As.<Integer>asFunctor(Stream.of(1,2,3)).map( i->i*2).unwrap();
		assertThat(((Stream)mappedStream).collect(Collectors.toList()),equalTo(Arrays.asList(2,4,6)));
	}
	@Test
	public void testAsValueUnapply() {
		List list = new ArrayList();
		As.asValue(new Child(10,20)).unapply().forEach(i->list.add(i));
		assertThat(list,equalTo(Arrays.asList(10,20)));
	}
	@Test
	public void testAsValueMatch() {
		List list = new ArrayList();
		
		assertThat(As.asValue(new Child(10,20))
				.matches(c-> 
							c.isType((Child child) -> child.val ).anyValues()
						)
		,equalTo(10));
	}
	@Test
	public void testAsValue_Match() {
		List list = new ArrayList();
		
		assertThat(As.asValue(new Child(10,20)).<Parent,Integer>matches(c-> 
				    c.isType((Child r)-> r.nextVal)
				     .hasValues(10,20))
		,equalTo(20));
	}
	@Test
	public void testAsValue_MatchDefault() {
		
		
		assertThat(As.asValue(new Child(10,20)).<Child,Integer>mayMatch(c-> 
			c.hasValues(20,20).then(i->i.nextVal)).orElse(50)
		,equalTo(50));
	}
	@Test
	public void test() {
		assertThat(As.asDecomposable(new MyCase("key",10))
				.unapply(),equalTo(Arrays.asList("key",10)));
	}
	
	@Test
	public void testMap(){
		Map<String,?> map = As.asMappable(new MyEntity(10,"hello")).toMap();
		System.out.println(map);
		assertThat(map.get("num"),equalTo(10));
		assertThat(map.get("str"),equalTo("hello"));
	}
	
	@Test
	public void testAsSupplierObject() {
		assertThat(As.asSupplier(Optional.of("hello")).get(),equalTo("hello"));
	}

	@Test
	public void testAsSupplierObjectString() {
		assertThat(As.asSupplier(new Duck(),"quack").get(),equalTo("quack"));
	}
	@Test
	public void testAsStreamableValue() {
		double total = As.<Double>asStreamableValue(new BaseData(10.00,5.00,100.30))
									.stream().collect(Collectors.summingDouble(t->t));
		
		assertThat(total,equalTo(115.3));
	}
	@Test
	public void testAsStreamableValueDo() {
		
		Stream<Double> withBonus = As.<Double>asStreamableValue(new BaseData(10.00,5.00,100.30))
									.doWithThisAnd(d->As.<Double>asStreamableValue(new Bonus(2.0)))
									.yield((Double base)->(Double bonus)-> base*(1.0+bonus));
		
		
		//withBonus.forEach(System.out::println);
		val total = withBonus.collect(Collectors.summingDouble(t->t));
		
		assertThat(total,equalTo(345.9));
	}
	
	@Value
	static class BaseData{
		double salary;
		double pension;
		double socialClub;
	}
	@Value
	static class Bonus{
		double bonus;
		
	}

	static class Duck{
		
		public String quack(){
			return  "quack";
		}
	}
	
	@Value static class MyEntity { int num; String str;}
	@Value
	static class MyCase { String key; int value;}

	@AllArgsConstructor(access=AccessLevel.PACKAGE)
	static class Parent{
		int val;
	}
	@Value
	static class Child extends Parent{
		int nextVal;
		public Child(int val,int nextVal) { super(val); this.nextVal = nextVal;}
	}
	**/
}

