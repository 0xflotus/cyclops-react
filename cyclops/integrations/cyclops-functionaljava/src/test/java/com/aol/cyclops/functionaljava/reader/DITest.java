package com.aol.cyclops.functionaljava.reader;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

//Reader Monad for Dependency Injection converted to Java8
//http://blog.originate.com/blog/2013/10/21/reader-monad-for-dependency-injection/
public class DITest {

	@SuppressWarnings("serial")
	Map<String,String> map = new HashMap<String,String> (){{
									put("fullname","bob");
									put("email","bob@user.com");
									put("boss","boss");
								}};
	@Test
	public void test(){
		assertThat(new Application().userInfo("bob"), equalTo(map));
	}
}

/** NOTES : See UserInfo class where current main compromise occurs.
UserRepository type info temporarily lost & locally restored. This could be solved
by a custom For Comprehension builder for FJ. Would also make construction of For Comphrensions
a little less clunky**/