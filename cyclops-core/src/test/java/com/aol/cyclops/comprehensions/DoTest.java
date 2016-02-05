package com.aol.cyclops.comprehensions;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.val;

import org.junit.Test;

import com.aol.cyclops.comprehensions.donotation.UntypedDo;
public class DoTest {
	
	
	@Test
	public void do2(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.yield((Double base)->(Double bonus)-> base*(1.0+bonus));
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(345.9));
	}
	@Test
	public void do1(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.yield((Double base)-> base+10);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(145.3));
	}
	
	
	@Test
	public void do3(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.yield((Double base)->(Double bonus)->(Double woot) -> base*(1.0+bonus)*woot);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(3459.0));
	}
	@Test
	public void do4(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									base*(1.0+bonus)*woot*f);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(34590.0));
	}
	@Test
	public void do5(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> Stream.of(10.0) )
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									(Double g)->
									base*(1.0+bonus)*woot*f*g);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(345900.0));
	}
	@Test
	public void do6(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)->
											Stream.of(10.0) )
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									(Double g)->(Double h)->
									base*(1.0+bonus)*woot*f*g*h);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(3459000.0));
	}
	@Test
	public void do7(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)->
											Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)-> (Double i) ->
											Stream.of(10.0) )
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									(Double g)->(Double h)->(Double i)->
									base*(1.0+bonus)*woot*f*g*h*i);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(34590000.0));
	}
	@Test
	public void do9(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)->
											Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)-> (Double i) ->
											Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)-> (Double i) -> (Double j) ->
											Stream.of(10.0) )
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									(Double g)->(Double h)->(Double i)->(Double j)->
									base*(1.0+bonus)*woot*f*g*h*i*j);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(345900000.0));
	}
	
	
	
	@Test
	public void do2Just(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.add(()->Stream.of(2.0))
						.yield((Double base)->(Double bonus)-> base*(1.0+bonus));
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(345.9));
	}
	
	
	@Test
	public void do3Just(){
		Stream<Double> s = UntypedDo.add(()->Stream.of(10.00,5.00,100.30))
						.add(()->Stream.of(2.0))
						.add(()->Stream.of(10.0))
						.yield((Double base)->(Double bonus)->(Double woot) -> base*(1.0+bonus)*woot);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(3459.0));
	}
	@Test
	public void do4Just(){
		Stream<Double> s = UntypedDo.add(()->Stream.of(10.00,5.00,100.30))
						.add(()->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.add(()->Stream.of(10.0))
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									base*(1.0+bonus)*woot*f);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(34590.0));
	}
	@Test
	public void do5Just(){
		Stream<Double> s = UntypedDo.add(Stream.of(10.00,5.00,100.30))
						.add(()->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.add(()->Stream.of(10.0) )
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									(Double g)->
									base*(1.0+bonus)*woot*f*g);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(345900.0));
	}
	@Test
	public void do6Just(){
		Stream<Double> s = UntypedDo.add(()->Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> Stream.of(10.0) )
						.add(()->Stream.of(10.0) )
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									(Double g)->(Double h)->
									base*(1.0+bonus)*woot*f*g*h);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(3459000.0));
	}
	@Test
	public void do7Just(){
		Stream<Double> s = UntypedDo.add(()->Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)->
											Stream.of(10.0) )
						.add(()->Stream.of(10.0) )
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									(Double g)->(Double h)->(Double i)->
									base*(1.0+bonus)*woot*f*g*h*i);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(34590000.0));
	}
	@Test
	public void do9Just(){
		Stream<Double> s = UntypedDo.add(()->Stream.of(10.00,5.00,100.30))
						.with((Double d)->Stream.of(2.0))
						.with((Double d)->(Double e)->Stream.of(10.0))
						.with((Double d)->(Double e)->(Double f)->Stream.of(10.0))
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)->
											Stream.of(10.0) )
						.with( (Double d)->(Double e)->(Double f)-> (Double g)-> (Double h)-> (Double i) ->
											Stream.of(10.0) )
						.add(()->Stream.of(10.0) )
						.yield((Double base)->(Double bonus)->(Double woot) -> (Double f)->
									(Double g)->(Double h)->(Double i)->(Double j)->
									base*(1.0+bonus)*woot*f*g*h*i*j);
		
		val total = s.collect(Collectors.summingDouble(t->t));
		assertThat(total,equalTo(345900000.0));
	}
}
