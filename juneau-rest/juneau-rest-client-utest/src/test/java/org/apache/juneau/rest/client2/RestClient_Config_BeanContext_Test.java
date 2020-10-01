// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.rest.client2;

import static org.apache.juneau.assertions.Assertions.*;
import static org.junit.Assert.*;
import static org.junit.runners.MethodSorters.*;

import java.io.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.http.*;
import org.apache.juneau.json.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.rest.client2.RestResponse;
import org.apache.juneau.rest.mock2.*;
import org.apache.juneau.transform.*;
import org.junit.*;

@FixMethodOrder(NAME_ASCENDING)
public class RestClient_Config_BeanContext_Test {

	@Rest
	public static class A extends BasicRest {
		@RestMethod(path="/echoBody")
		public Reader postEchoBody(org.apache.juneau.rest.RestRequest req) throws IOException {
			return req.getBody().getReader();
		}
		@RestMethod(path="/checkHeader")
		public String[] getHeader(org.apache.juneau.rest.RestRequest req) {
			return req.getHeaders().get(req.getHeader("Check"));
		}
		@RestMethod(path="/checkQuery")
		public Reader getQuery(org.apache.juneau.rest.RestRequest req) {
			return new StringReader(req.getQuery().asQueryString());
		}
		@RestMethod(path="/checkFormData")
		public Reader postFormData(org.apache.juneau.rest.RestRequest req) {
			return new StringReader(req.getFormData().asQueryString());
		}
	}


	protected static class A1 {
		public int f = 1;
		@Override
		public String toString() {
			return "O1";
		}
	}

	@Test
	public void a01_beanClassVisibility() throws Exception {
		RestClient x1 = client().build();
		RestClient x2 = client(A.class).beanClassVisibility(Visibility.PROTECTED).build();
		x1.post("/echoBody",new A1()).run().assertBody().is("'O1'");
		x2.post("/echoBody",new A1()).run().assertBody().is("{f:1}");
		x1.get("/checkQuery").query("foo",new A1()).run().assertBody().is("foo=O1");
		x2.get("/checkQuery").query("foo",new A1()).run().assertBody().is("foo=f%3D1").assertBody().urlDecode().is("foo=f=1");
		x1.formPost("/checkFormData").formData("foo",new A1()).run().assertBody().is("foo=O1");
		x2.formPost("/checkFormData").formData("foo",new A1()).run().assertBody().is("foo=f%3D1").assertBody().urlDecode().is("foo=f=1");
		x1.get("/checkHeader").header("foo",new A1()).header("Check","foo").run().assertBody().is("['O1']");
		x2.get("/checkHeader").header("foo",new A1()).header("Check","foo").run().assertBody().is("['f=1']");
	}

	public static class A2a {
		private int f;
		protected A2a(int f) {
			this.f = f;
		}
		public int toInt() {
			return f;
		}
	}

	@Rest
	public static class A2b extends BasicRest {
		@RestMethod
		public Reader postTest(org.apache.juneau.rest.RestRequest req,org.apache.juneau.rest.RestResponse res) throws IOException {
			res.setHeader("X",req.getHeaders().getString("X"));
			return req.getBody().getReader();
		}
	}

	@Test
	public void a02_beanConstructorVisibility() throws Exception {
		RestResponse x = client(A2b.class).beanConstructorVisibility(Visibility.PROTECTED).build().post("/test",new A2a(1)).header("X",new A2a(1)).run().cacheBody().assertBody().is("1").assertStringHeader("X").is("1");
		assertEquals(1,x.getBody().as(A2a.class).f);
		assertEquals(1,x.getHeader("X").as(A2a.class).f);
	}

	public static class A3 {
		public int f1;
		protected int f2;
		static A3 get() {
			A3 x = new A3();
			x.f1 = 1;
			x.f2 = 2;
			return x;
		}
		@Override
		public String toString() {
			return f1 + "/" + f2;
		}
	}

	@Test
	public void a03_beanFieldVisibility() throws Exception {
		RestResponse x = client(A2b.class).beanFieldVisibility(Visibility.PROTECTED).build().post("/test",A3.get()).header("X",A3.get()).run().cacheBody().assertBody().is("{f1:1,f2:2}").assertStringHeader("X").is("f1=1,f2=2");
		assertEquals(2,x.getBody().as(A3.class).f2);
		assertEquals(2,x.getHeader("X").as(A3.class).f2);
	}

	public static interface A4a {
		int getF3();
		void setF3(int f3);
	}

	public static class A4b implements A4a {
		public int f1, f2;
		private int f3;
		@Override
		public int getF3() {
			return f3;
		}
		@Override
		public void setF3(int f3) {
			this.f3 = f3;
		}
		static A4b get() {
			A4b x = new A4b();
			x.f1 = 1;
			x.f2 = 2;
			x.f3 = 3;
			return x;
		}
		@Override
		public String toString() {
			return f1 + "/" + f2;
		}
	}

	@Test
	public void a04_beanFilters() throws Exception {
		RestResponse x = client(A2b.class).bpi(A4b.class,"f1").build().post("/test",A4b.get()).header("X",A4b.get()).run().cacheBody().assertBody().is("{f1:1}").assertStringHeader("X").is("f1=1");
		assertEquals(0,x.getBody().as(A4b.class).f2);
		assertEquals(0,x.getHeader("X").as(A4b.class).f2);

		x = client(A2b.class).bpi(A4b.class,"f1").build().post("/test",A4b.get()).header("X",A4b.get()).run().cacheBody().assertBody().is("{f1:1}").assertStringHeader("X").is("f1=1");
		assertEquals(0,x.getBody().as(A4b.class).f2);
		assertEquals(0,x.getHeader("X").as(A4b.class).f2);

		x = client(A2b.class).bpi(A4b.class,"f1").build().post("/test",A4b.get()).header("X",A4b.get()).run().cacheBody().assertBody().is("{f1:1}").assertStringHeader("X").is("f1=1");
		assertEquals(0,x.getBody().as(A4b.class).f2);
		assertEquals(0,x.getHeader("X").as(A4b.class).f2);

		x = client(A2b.class).bpi(A4b.class,"f1").build().post("/test",A4b.get()).header("X",A4b.get()).run().cacheBody().assertBody().is("{f1:1}").assertStringHeader("X").is("f1=1");
		assertEquals(0,x.getBody().as(A4b.class).f2);
		assertEquals(0,x.getHeader("X").as(A4b.class).f2);

		x = client(A2b.class).interfaces(A4a.class).build().post("/test",A4b.get()).header("X",A4b.get()).run().cacheBody().assertBody().is("{f3:3}").assertStringHeader("X").is("f3=3");
		assertEquals(3,x.getBody().as(A4b.class).f3);
		assertEquals(3,x.getHeader("X").as(A4b.class).f3);
	}

	public static class A5  {
		private int f1, f2;
		public int getF1() {
			return f1;
		}
		public void setF1(int f1) {
			this.f1 = f1;
		}
		protected int getF2() {
			return f2;
		}
		protected void setF2(int f2) {
			this.f2 = f2;
		}
		static A5 get() {
			A5 x = new A5();
			x.f1 = 1;
			x.f2 = 2;
			return x;
		}
		@Override
		public String toString() {
			return f1 + "/" + f2;
		}
	}

	@Test
	public void a05_beanMethodVisibility() throws Exception {
		RestResponse x = client(A2b.class).beanMethodVisibility(Visibility.PROTECTED).build().post("/test",A5.get()).header("X",A5.get()).run().cacheBody().assertBody().is("{f1:1,f2:2}").assertStringHeader("X").is("f1=1,f2=2");
		assertEquals(2,x.getBody().as(A5.class).f2);
		assertEquals(2,x.getHeader("X").as(A5.class).f2);
	}

	public static class A6 {}

	@Test
	public void a06_beansDontRequireSomeProperties() throws Exception {
		client().beansDontRequireSomeProperties().build().post("/echoBody",new A6()).run().assertBody().is("{}");
	}

	public static class A7  {
		public String f1;
		public A7(String i) {
			f1 = i;
		}
		@Override
		public String toString() {
			return f1;
		}
	}

	@Test
	public void a07_beansRequireDefaultConstructor() throws Exception {
		client(A2b.class).build().post("/test",new A7("1")).header("X",new A7("1")).run().assertBody().is("{f1:'1'}").assertStringHeader("X").is("f1=1");
		client(A2b.class).beansRequireDefaultConstructor().build().post("/test",new A7("1")).header("X",new A7("1")).run().assertBody().is("'1'").assertStringHeader("X").is("1");
	}

	@Test
	public void a08_beansRequireSerializable() throws Exception {
		client(A2b.class).build().post("/test",new A7("1")).header("X",new A7("1")).run().assertBody().is("{f1:'1'}").assertStringHeader("X").is("f1=1");
		client(A2b.class).beansRequireSerializable().build().post("/test",new A7("1")).header("X",new A7("1")).run().assertBody().is("'1'").assertStringHeader("X").is("1");
	}

	public static class A9 {
		private int f1, f2;
		public int getF1() {
			return f1;
		}
		public void setF1(int f1) {
			this.f1 = f1;
		}
		public int getF2() {
			return f2;
		}
		static A9 get() {
			A9 x = new A9();
			x.f1 = 1;
			x.f2 = 2;
			return x;
		}
		@Override
		public String toString() {
			return f1 + "/" + f2;
		}
	}

	@Test
	public void a09_beansRequireSettersForGetters() throws Exception {
		client(A2b.class).build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f1:1,f2:2}").assertStringHeader("X").is("f1=1,f2=2");
		client(A2b.class).beansRequireSettersForGetters().build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f1:1}").assertStringHeader("X").is("f1=1");
	}

	@Test
	public void a10_bpi() throws Exception {
		client(A2b.class).bpi(OMap.of("A9","f2")).build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f2:2}").assertStringHeader("X").is("f2=2");
		client(A2b.class).bpi(A9.class,"f2").build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f2:2}").assertStringHeader("X").is("f2=2");
		client(A2b.class).bpi("A9","f2").build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f2:2}").assertStringHeader("X").is("f2=2");
		client(A2b.class).bpi(A9.class.getName(),"f2").build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f2:2}").assertStringHeader("X").is("f2=2");
	}

	@Test
	public void a11_bpro() throws Exception {
		RestResponse x = null;

		x = client(A2b.class).bpro(OMap.of("09","f2")).build().post("/test",A9.get()).header("X",A9.get()).run().cacheBody().assertBody().is("{f1:1,f2:2}").assertStringHeader("X").is("f1=1,f2=2");
		assertEquals("1/0",x.getBody().as(A9.class).toString());
		assertEquals("1/0",x.getHeader("X").as(A9.class).toString());

		x = client(A2b.class).bpro(A9.class,"f2").build().post("/test",A9.get()).header("X",A9.get()).run().cacheBody().assertBody().is("{f1:1,f2:2}").assertStringHeader("X").is("f1=1,f2=2");
		assertEquals("1/0",x.getBody().as(A9.class).toString());
		assertEquals("1/0",x.getHeader("X").as(A9.class).toString());

		x = client(A2b.class).bpro("O9","f2").build().post("/test",A9.get()).header("X",A9.get()).run().cacheBody().assertBody().is("{f1:1,f2:2}").assertStringHeader("X").is("f1=1,f2=2");
		assertEquals("1/0",x.getBody().as(A9.class).toString());
		assertEquals("1/0",x.getHeader("X").as(A9.class).toString());
	}

	@Test
	public void a12_bpwo() throws Exception {
		RestResponse x = null;

		x = client(A2b.class).bpwo(OMap.of("A9","f2")).build().post("/test",A9.get()).header("X",A9.get()).run().cacheBody().assertBody().is("{f1:1}").assertStringHeader("X").is("f1=1");
		assertEquals("1/0",x.getBody().as(A9.class).toString());
		assertEquals("1/0",x.getHeader("X").as(A9.class).toString());

		x = client(A2b.class).bpwo(A9.class,"f2").build().post("/test",A9.get()).header("X",A9.get()).run().cacheBody().assertBody().is("{f1:1}").assertStringHeader("X").is("f1=1");
		assertEquals("1/0",x.getBody().as(A9.class).toString());
		assertEquals("1/0",x.getHeader("X").as(A9.class).toString());

		x = client(A2b.class).bpwo("A9","f2").build().post("/test",A9.get()).header("X",A9.get()).run().cacheBody().assertBody().is("{f1:1}").assertStringHeader("X").is("f1=1");
		assertEquals("1/0",x.getBody().as(A9.class).toString());
		assertEquals("1/0",x.getHeader("X").as(A9.class).toString());
	}

	@Test
	public void a13_bpx() throws Exception {
		client(A2b.class).bpx(OMap.of("A9","f1")).build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f2:2}").assertStringHeader("X").is("f2=2");
		client(A2b.class).bpx(A9.class,"f1").build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f2:2}").assertStringHeader("X").is("f2=2");
		client(A2b.class).bpx("A9","f1").build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f2:2}").assertStringHeader("X").is("f2=2");
		client(A2b.class).bpx(A9.class.getName(),"f1").build().post("/test",A9.get()).header("X",A9.get()).run().assertBody().is("{f2:2}").assertStringHeader("X").is("f2=2");
	}

	public static class A14 {
		public Object f;
	}

	@Test
	public void a14_debug() throws Exception {
		A14 x = new A14();
		x.f = x;
		assertThrown(()->client().debug().build().post("/echo",x).run()).contains("Recursion occurred");
	}

	@org.apache.juneau.annotation.Bean(typeName="foo")
	public static class A15a {
		public String foo;
		static A15a get() {
			A15a x = new A15a();
			x.foo = "1";
			return x;
		}
	}

	@org.apache.juneau.annotation.Bean(typeName="bar")
	public static class A15b {
		public String foo;
		static A15b get() {
			A15b x = new A15b();
			x.foo = "2";
			return x;
		}
	}

	public static class A15c {
		public Object foo;
		static A15c get() {
			A15c x = new A15c();
			x.foo = A15a.get();
			return x;
		}
	}

	@Test
	public void a15_dictionary() throws Exception {
		Object o = client().dictionary(A15a.class,A15b.class).addRootType().addBeanTypes().build().post("/echoBody",A15a.get()).run().cacheBody().assertBody().contains("{_type:'foo',foo:'1'}").getBody().as(Object.class);;
		assertTrue(o instanceof A15a);

		OMap m = OMap.of("x",A15a.get(),"y",A15b.get());
		m = client().dictionary(A15a.class,A15b.class).addRootType().addBeanTypes().build().post("/echoBody",m).run().cacheBody().assertBody().is("{x:{_type:'foo',foo:'1'},y:{_type:'bar',foo:'2'}}").getBody().as(OMap.class);;
		assertTrue(m.get("x") instanceof A15a);
		assertTrue(m.get("y") instanceof A15b);

		A15c x = client().dictionaryOn(A15c.class,A15a.class,A15b.class).addRootType().addBeanTypes().build().post("/echoBody",A15c.get()).run().cacheBody().assertBody().is("{foo:{_type:'foo',foo:'1'}}").getBody().as(A15c.class);;
		assertTrue(x.foo instanceof A15a);
	}

	public static class A16 {
		private String foo;
		public String getFoo() {
			return foo;
		}
		static A16 get() {
			A16 x = new A16();
			x.foo = "foo";
			return x;
		}
	}

	@Test
	public void a16_dontIgnorePropertiesWithoutSetters() throws Exception {
		A16 x = client().build().post("/echoBody",A16.get()).run().cacheBody().assertBody().contains("{foo:'foo'}").getBody().as(A16.class);
		assertNull(x.foo);
		assertThrown(()->client().dontIgnorePropertiesWithoutSetters().build().post("/echoBody",A16.get()).run().cacheBody().assertBody().contains("{foo:'foo'}").getBody().as(A16.class)).contains("Setter or public field not defined");
	}

	public static class A17 {
		public String foo;
		public transient String bar;
		static A17 get() {
			A17 x = new A17();
			x.foo = "1";
			x.bar = "2";
			return x;
		}
	}

	@Test
	public void a17_dontIgnoreTransientFields() throws Exception {
		A17 x = client().build().post("/echoBody",A17.get()).run().cacheBody().assertBody().contains("{foo:'1'}").getBody().as(A17.class);;
		assertNull(x.bar);
		x = client().dontIgnoreTransientFields().build().post("/echoBody",A17.get()).run().cacheBody().assertBody().contains("{bar:'2',foo:'1'}").getBody().as(A17.class);
		assertEquals("2",x.bar);
	}

	public static class A18 {
		public String foo;
	}

	@Test
	public void a18_dontIgnoreUnknownNullBeanProperties() throws Exception {
		client().build().post("/echoBody",new StringReader("{foo:'1',bar:null}")).run().cacheBody().assertBody().contains("{foo:'1',bar:null}").getBody().as(A18.class);;
		assertThrown(()->client().dontIgnoreUnknownNullBeanProperties().build().post("/echoBody",new StringReader("{foo:'1',bar:null}")).run().cacheBody().assertBody().contains("{foo:'1',bar:null}").getBody().as(A18.class)).contains("Unknown property 'bar'");
	}

	public static interface A19 {
		public String getFoo();
		public void setFoo(String foo);
	}

	@Test
	public void a19_dontUseInterfaceProxies() throws Exception {
		A19 x = client().build().post("/echoBody",new StringReader("{foo:'1'}")).run().cacheBody().assertBody().contains("{foo:'1'}").getBody().as(A19.class);;
		assertEquals("1",x.getFoo());
		assertThrown(()->client().dontUseInterfaceProxies().build().post("/echoBody",new StringReader("{foo:'1'}")).run().cacheBody().assertBody().contains("{foo:'1'}").getBody().as(A19.class)).contains("could not be instantiated");
	}

	public static class A20 {
		private String foo;
		public String getFoo() {
			return foo;
		}
		public A20 foo(String foo) {
			this.foo = foo;
			return this;
		}
	}

	@Test
	public void a20_fluentSetters() throws Exception {
		A20 x = client().fluentSetters().build().post("/echoBody",new StringReader("{foo:'1'}")).run().cacheBody().assertBody().contains("{foo:'1'}").getBody().as(A20.class);;
		assertEquals("1",x.getFoo());
		x = client().fluentSetters(A20.class).build().post("/echoBody",new StringReader("{foo:'1'}")).run().cacheBody().assertBody().contains("{foo:'1'}").getBody().as(A20.class);;
		assertEquals("1",x.getFoo());
	}

	public static class A21 {
		@SuppressWarnings("unused")
		private String foo, bar;
		public String getFoo() {
			return foo;
		}
		public void setFoo(String foo) {
			this.foo = foo;
		}
		public String getBar() {
			throw new RuntimeException("xxx");
		}
		static A21 get() {
			A21 x = new A21();
			x.foo = "1";
			x.bar = "2";
			return x;
		}
	}

	@Test
	public void a21_ignoreInvocationExceptionsOnGetters() throws Exception {
		assertThrown(()->client().build().post("/echoBody",A21.get()).run()).contains("Could not call getValue() on property 'bar'");
		A21 x = client().ignoreInvocationExceptionsOnGetters().build().post("/echoBody",A21.get()).run().cacheBody().assertBody().contains("{foo:'1'}").getBody().as(A21.class);;
		assertEquals("1",x.getFoo());
	}

	public static class A22 {
		@SuppressWarnings("unused")
		private String foo, bar;
		public String getFoo() {
			return foo;
		}
		public void setFoo(String foo) {
			this.foo = foo;
		}
		public String getBar() {
			return bar;
		}
		public void setBar(String bar) {
			throw new RuntimeException("xxx");
		}
		static A22 get() {
			A22 x = new A22();
			x.foo = "1";
			x.bar = "2";
			return x;
		}
	}

	@Test
	public void a22_ignoreInvocationExceptionsOnSetters() throws Exception {
		assertThrown(()->client().build().post("/echoBody",A22.get()).run().getBody().as(A22.class)).contains("Error occurred trying to set property 'bar'");
		A22 x = client().ignoreInvocationExceptionsOnSetters().build().post("/echoBody",A22.get()).run().cacheBody().getBody().as(A22.class);;
		assertEquals("1",x.getFoo());
	}

	public static class A23 {
		public String foo;
	}

	@Test
	public void a23_ignoreUnknownBeanProperties() throws Exception {
		assertThrown(()->client().build().post("/echoBody",new StringReader("{foo:'1',bar:'2'}")).run().getBody().as(A23.class)).contains("Unknown property 'bar' encountered");
		A23 x = client().ignoreUnknownBeanProperties().build().post("/echoBody",new StringReader("{foo:'1',bar:'2'}")).run().cacheBody().getBody().as(A23.class);;
		assertEquals("1",x.foo);
	}

	public static interface A24a {
		void setFoo(int foo);
		int getFoo();
	}

	public static class A24b implements A24a {
		private int foo;
		@Override
		public int getFoo() {
			return foo;
		}
		@Override
		public void setFoo(int foo) {
			this.foo = foo;
		}
	}

	@Test
	public void a24_implClass() throws Exception {
		A24a x = client().implClass(A24a.class,A24b.class).build().post("/echoBody",new StringReader("{foo:1}")).run().getBody().as(A24a.class);
		assertEquals(1,x.getFoo());
		assertTrue(x instanceof A24b);

		x = client().implClasses(AMap.of(A24a.class,A24b.class)).build().post("/echoBody",new StringReader("{foo:1}")).run().getBody().as(A24a.class);
		assertEquals(1,x.getFoo());
		assertTrue(x instanceof A24b);
	}

	public static interface A25a {
		void setFoo(int foo);
		int getFoo();
	}

	public static class A25b implements A25a {
		private int foo, bar;
		@Override
		public int getFoo() { return foo; }
		@Override
		public void setFoo(int foo) { this.foo = foo; }
		public int getBar() { return bar; }  // Not executed
		public void setBar(int bar) { this.bar = bar; }  // Not executed

		static A25b get() {
			A25b x = new A25b();
			x.foo = 1;
			x.bar = 2;
			return x;
		}
	}

	@Test
	public void a25_interfaceClass() throws Exception {
		A25a x = client().interfaceClass(A25b.class,A25a.class).build().post("/echoBody",A25b.get()).run().cacheBody().assertBody().is("{foo:1}").getBody().as(A25b.class);
		assertEquals(1,x.getFoo());
		x = client().interfaces(A25a.class).build().post("/echoBody",A25b.get()).run().assertBody().is("{foo:1}").getBody().as(A25b.class);
		assertEquals(1,x.getFoo());
	}

	public static class A26 {
		public int foo;
		static A26 get() {
			A26 x = new A26();
			x.foo = 1;
			return x;
		}
	}

	@Test
	public void a26_locale() throws Exception {
		A26 x = client().locale(Locale.UK).build().post("/echoBody",A26.get()).run().cacheBody().assertBody().is("{foo:1}").getBody().as(A26.class);
		assertEquals(1,x.foo);
	}

	@Test
	public void a27_mediaType() throws Exception {
		A26 x = client().mediaType(MediaType.JSON).build().post("/echoBody",A26.get()).run().cacheBody().assertBody().is("{foo:1}").getBody().as(A26.class);
		assertEquals(1,x.foo);
	}

	public static class A28 {
		public int foo;
		static A28 get() {
			A28 x = new A28();
			x.foo = 1;
			return x;
		}
		@Override
		public String toString() {
			return String.valueOf(foo);
		}
		public static A28 fromString(String foo) throws ParseException {
			A28 x = new A28();
			x.foo = JsonParser.DEFAULT.parse(foo,int.class);
			return x;
		}
	}

	@Test
	public void a28_notBeanClasses() throws Exception {
		A28 x = client().notBeanClasses(A28.class).build().post("/echoBody",A28.get()).run().cacheBody().assertBody().is("'1'").getBody().as(A28.class);
		assertEquals(1,x.foo);
	}

	@Test
	public void a29_notBeanPackages() throws Exception {
		A28 x = client().notBeanPackages(A28.class.getPackage()).build().post("/echoBody",A28.get()).run().cacheBody().assertBody().is("'1'").getBody().as(A28.class);
		assertEquals(1,x.foo);
	}

	public static class A30a {
		private String foo;
		public String getFoo() { return foo; }
		public void setFoo(String foo) { this.foo = foo; }
		static A30a get() {
			A30a x = new A30a();
			x.foo = "foo";
			return x;
		}
	}

	public static class A30b extends BeanInterceptor<A30a> {
		static boolean getterCalled,setterCalled;
		@Override
		public Object readProperty(A30a bean,String name,Object value) {
			getterCalled = true;
			return "x" + value;
		}
		@Override
		public Object writeProperty(A30a bean,String name,Object value) {
			setterCalled = true;
			return value.toString().substring(1);
		}
	}

	@Test
	public void a30_beanInterceptor() throws Exception {
		A30a x = client().beanInterceptor(A30a.class,A30b.class).build().post("/echoBody",A30a.get()).run().cacheBody().assertBody().is("{foo:'xfoo'}").getBody().as(A30a.class);
		assertEquals("foo",x.foo);
		assertTrue(A30b.getterCalled);
		assertTrue(A30b.setterCalled);
	}

	public static class A31 {
		private String fooBar;
		public String getFooBar() { return fooBar; }
		public void setFooBar(String fooBar) { this.fooBar = fooBar; }
		static A31 get() {
			A31 x = new A31();
			x.fooBar = "fooBar";
			return x;
		}
	}

	@Test
	public void a31_propertyNamer() throws Exception {
		A31 x = client().propertyNamer(PropertyNamerDLC.class).build().post("/echoBody",A31.get()).run().cacheBody().assertBody().is("{'foo-bar':'fooBar'}").getBody().as(A31.class);
		assertEquals("fooBar",x.fooBar);
		x = client().propertyNamer(A31.class,PropertyNamerDLC.class).build().post("/echoBody",A31.get()).run().cacheBody().assertBody().is("{'foo-bar':'fooBar'}").getBody().as(A31.class);
		assertEquals("fooBar",x.fooBar);
	}

	public static class A32 {
		public int foo, bar, baz;
		static A32 get() {
			A32 x = new A32();
			x.foo = 1;
			x.bar = 2;
			x.baz = 3;
			return x;
		}
	}

	@Test
	public void a32_sortProperties() throws Exception {
		A32 x = client().sortProperties().build().post("/echoBody",A32.get()).run().cacheBody().assertBody().is("{bar:2,baz:3,foo:1}").getBody().as(A32.class);
		assertEquals(1,x.foo);
		x = client().sortProperties(A32.class).build().post("/echoBody",A32.get()).run().cacheBody().assertBody().is("{bar:2,baz:3,foo:1}").getBody().as(A32.class);
		assertEquals(1,x.foo);
	}

	public static class A33a {
		public int foo;
	}

	public static class A33b extends A33a {
		public int bar;
		static A33b get() {
			A33b x = new A33b();
			x.foo = 1;
			x.bar = 2;
			return x;
		}
	}

	@Test
	public void a33_stopClass() throws Exception {
		A33b x = client().stopClass(A33b.class,A33a.class).build().post("/echoBody",A33b.get()).run().cacheBody().assertBody().is("{bar:2}").getBody().as(A33b.class);
		assertEquals(0,x.foo);
		assertEquals(2,x.bar);
	}

	public static class A34a {
		public int foo;
		static A34a get() {
			A34a x = new A34a();
			x.foo = 1;
			return x;
		}
	}

	public static class A34b extends PojoSwap<A34a,Integer> {
		@Override
		public Integer swap(BeanSession session,A34a o) { return o.foo; }
		@Override
		public A34a unswap(BeanSession session,Integer f,ClassMeta<?> hint) {return A34a.get(); }
	}

	@Test
	public void a34_swaps() throws Exception {
		A34a x = client().swaps(A34b.class).build().post("/echoBody",A34a.get()).run().cacheBody().assertBody().is("1").getBody().as(A34a.class);
		assertEquals(1,x.foo);
	}

	public static class A35 {
		public int foo;
		static A35 get() {
			A35 x = new A35();
			x.foo = 1;
			return x;
		}
	}

	@Test
	public void a35_timeZone() throws Exception {
		A35 x = client().timeZone(TimeZone.getTimeZone("Z")).build().post("/echoBody",A35.get()).run().cacheBody().assertBody().is("{foo:1}").getBody().as(A35.class);
		assertEquals(1,x.foo);
	}

	public static class A36 {
		public int foo;
		static A36 get() {
			A36 x = new A36();
			x.foo = 1;
			return x;
		}
	}

	@Test
	public void a36_typeName() throws Exception {
		A36 x = client().typeName(A36.class,"foo").addRootType().build().post("/echoBody",A36.get()).run().cacheBody().assertBody().is("{_type:'foo',foo:1}").getBody().as(A36.class);
		assertEquals(1,x.foo);
	}

	@Test
	public void a37_typePropertyName() throws Exception {
		A36 x = client().typeName(A36.class,"foo").typePropertyName("X").addRootType().build().post("/echoBody",A36.get()).run().cacheBody().assertBody().is("{X:'foo',foo:1}").getBody().as(A36.class);
		assertEquals(1,x.foo);
		x = client().typeName(A36.class,"foo").typePropertyName(A36.class,"X").addRootType().build().post("/echoBody",A36.get()).run().cacheBody().assertBody().is("{X:'foo',foo:1}").getBody().as(A36.class);
		assertEquals(1,x.foo);
	}

	public static enum A38a {
		ONE(1),TWO(2);
		private int value;
		A38a(int value) {
			this.value = value;
		}
		@Override
		public String toString() {
			return String.valueOf(value);  // Not executed
		}
	}

	public static class A38b {
		public A38a foo;
		static A38b get() {
			A38b x = new A38b();
			x.foo = A38a.ONE;
			return x;
		}
	}

	@Test
	public void a38_useEnumNames() throws Exception {
		A38b x = client().useEnumNames().build().post("/echoBody",A38b.get()).run().cacheBody().assertBody().is("{foo:'ONE'}").getBody().as(A38b.class);
		assertEquals(A38a.ONE,x.foo);
	}

	public static class A39 {
		private int foo;
		public int bar;
		public int getFoo() {
			return foo;
		}
		public void setFoo(int foo) {
			this.foo = foo;
		}
		static A39 get() {
			A39 x = new A39();
			x.foo = 1;
			x.bar = 2;
			return x;
		}
	}

	@Test
	public void a39_useJavaIntrospector() throws Exception {
		A39 x = client().useJavaBeanIntrospector().build().post("/echoBody",A39.get()).run().cacheBody().assertBody().is("{foo:1}").getBody().as(A39.class);
		assertEquals(1,x.foo);
	}


	//------------------------------------------------------------------------------------------------------------------
	// Helper methods.
	//------------------------------------------------------------------------------------------------------------------

	private static RestClientBuilder client() {
		return MockRestClient.create(A.class).simpleJson();
	}

	private static RestClientBuilder client(Class<?> c) {
		return MockRestClient.create(c).simpleJson();
	}
}