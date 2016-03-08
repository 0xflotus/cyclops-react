package com.aol.cyclops.featuretoggle;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.aol.cyclops.control.FeatureToggle;
import com.aol.cyclops.control.FeatureToggle.Disabled;
import com.aol.cyclops.control.FeatureToggle.Enabled;

public class EnabledTest {

	Enabled<Integer> enabled;
	Object value = null;
	@Before
	public void setup(){
		value = null;
		enabled = FeatureToggle.enable(100);
	}
	
	
	@Test
	public void testPeek(){
		enabled.peek(it -> value=it);
		assertThat(value,is(100));
	}
	
	@Test
	public void testFlip(){
		assertThat(enabled.flip().isDisabled(),is(true));
	}
	@Test
	public void testFlipTwice(){
		assertThat(enabled.flip().flip().isDisabled(),is(false));
	}
	@Test
	public void testFilter(){
		assertThat(enabled.filter(i->i<200).map(i->i+1).get(),is(101));
	}
	@Test
	public void testFilterTrue(){
		assertThat(enabled.filter(i->i<200).map(i->i+1).isEnabled(),is(true));
	}
	@Test
	public void testFilterFalse(){
		assertThat(enabled.filter(i->i>200).map(i->i+1).isDisabled() ,is(true));
	}
	
	@Test
	public void testForEach(){
		enabled.forEach(i->value=i*100);
		assertThat(value,is(100*100));
	}
	@Test
	public void testFlatMap(){
		assertThat(enabled.flatMap(i->FeatureToggle.disable(100)).isEnabled(),is(false));
	}
	
	@Test
	public void testMap(){
		assertThat(enabled.map(i->i+1).get(),is(101));
	}
	@Test
	public void testEnabled() {
		assertThat(enabled.get(),is(100));
	}

	@Test
	public void testIsEnabled() {
		assertTrue(enabled.isEnabled());
	}

	@Test
	public void testIsDisabled() {
		assertFalse(enabled.isDisabled());
	}

}
