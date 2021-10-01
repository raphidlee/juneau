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
package org.apache.juneau.serializer;

import static org.apache.juneau.internal.SystemEnv.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.http.header.*;
import org.apache.juneau.internal.*;

/**
 * Builder class for building instances of serializers.
 * {@review}
 */
@FluentSetters
public abstract class SerializerBuilder extends BeanTraverseBuilder {

	boolean addBeanTypes, addRootType, keepNullProperties, sortCollections, sortMaps, trimEmptyCollections,
		trimEmptyMaps, trimStrings;
	String produces, accept;
	UriContext uriContext;
	UriRelativity uriRelativity;
	UriResolution uriResolution;
	Class<? extends SerializerListener> listener;

	/**
	 * Constructor, default settings.
	 */
	protected SerializerBuilder() {
		super();
		produces = null;
		accept = null;
		addBeanTypes = env("Serializer.addBeanTypes", false);
		addRootType = env("Serializer.addRootType", false);
		keepNullProperties = env("Serializer.keepNullProperties", false);
		sortCollections = env("Serializer.sortCollections", false);
		sortMaps = env("Serializer.sortMaps", false);
		trimEmptyCollections = env("Serializer.trimEmptyCollections", false);
		trimEmptyMaps = env("Serializer.trimEmptyMaps", false);
		trimStrings = env("Serializer.trimStrings", false);
		uriContext = UriContext.DEFAULT;
		uriRelativity = UriRelativity.RESOURCE;
		uriResolution = UriResolution.NONE;
		listener = null;
	}

	/**
	 * Copy constructor.
	 *
	 * @param copyFrom The bean to copy from.
	 */
	protected SerializerBuilder(Serializer copyFrom) {
		super(copyFrom);
		produces = copyFrom.produces;
		accept = copyFrom.accept;
		addBeanTypes = copyFrom.addBeanTypes;
		addRootType = copyFrom.addRootType;
		keepNullProperties = copyFrom.keepNullProperties;
		sortCollections = copyFrom.sortCollections;
		sortMaps = copyFrom.sortMaps;
		trimEmptyCollections = copyFrom.trimEmptyCollections;
		trimEmptyMaps = copyFrom.trimEmptyMaps;
		trimStrings = copyFrom.trimStrings;
		uriContext = copyFrom.uriContext;
		uriRelativity = copyFrom.uriRelativity;
		uriResolution = copyFrom.uriResolution;
		listener = copyFrom.listener;
	}

	/**
	 * Copy constructor.
	 *
	 * @param copyFrom The builder to copy from.
	 */
	protected SerializerBuilder(SerializerBuilder copyFrom) {
		super(copyFrom);
		produces = copyFrom.produces;
		accept = copyFrom.accept;
		addBeanTypes = copyFrom.addBeanTypes;
		addRootType = copyFrom.addRootType;
		keepNullProperties = copyFrom.keepNullProperties;
		sortCollections = copyFrom.sortCollections;
		sortMaps = copyFrom.sortMaps;
		trimEmptyCollections = copyFrom.trimEmptyCollections;
		trimEmptyMaps = copyFrom.trimEmptyMaps;
		trimStrings = copyFrom.trimStrings;
		uriContext = copyFrom.uriContext;
		uriRelativity = copyFrom.uriRelativity;
		uriResolution = copyFrom.uriResolution;
		listener = copyFrom.listener;
	}

	@Override /* ContextBuilder */
	public abstract SerializerBuilder copy();

	@Override /* ContextBuilder */
	public Serializer build() {
		return (Serializer)super.build();
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Specifies the media type that this serializer produces.
	 *
	 * @param value The value for this setting.
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder produces(String value) {
		this.produces = value;
		return this;
	}

	/**
	 * Returns the current value for the 'produces' property.
	 *
	 * @return The current value for the 'produces' property.
	 */
	public String getProduces() {
		return produces;
	}

	/**
	 * 	Specifies the accept media types that the serializer can handle.
	 *
	 * 	<p>
	 * 	Can contain meta-characters per the <c>media-type</c> specification of {@doc ExtRFC2616.section14.1}
	 * 	<p>
	 * 	If empty, then assumes the only media type supported is <c>produces</c>.
	 * 	<p>
	 * 	For example, if this serializer produces <js>"application/json"</js> but should handle media types of
	 * 	<js>"application/json"</js> and <js>"text/json"</js>, then the arguments should be:
	 * 	<p class='bcode w800'>
	 * 	<jk>super</jk>(ps, <js>"application/json"</js>, <js>"application/json,text/json"</js>);
	 * 	</p>
	 * 	<br>...or...
	 * 	<p class='bcode w800'>
	 * 	<jk>super</jk>(ps, <js>"application/json"</js>, <js>"*&#8203;/json"</js>);
	 * 	</p>
	 * <p>
	 * The accept value can also contain q-values.
	 *
	 * @param value The value for this setting.
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder accept(String value) {
		this.accept = value;
		return this;
	}

	/**
	 * Returns the current value for the 'accept' property.
	 *
	 * @return The current value for the 'accept' property.
	 */
	public String getAccept() {
		return accept;
	}

	/**
	 * Add <js>"_type"</js> properties when needed.
	 *
	 * <p>
	 * When enabled, <js>"_type"</js> properties will be added to beans if their type cannot be inferred
	 * through reflection.
	 *
	 * <p>
	 * This is used to recreate the correct objects during parsing if the object types cannot be inferred.
	 * <br>For example, when serializing a <c>Map&lt;String,Object&gt;</c> field where the bean class cannot be determined from
	 * the type of the values.
	 *
	 * <p>
	 * Note the differences between the following settings:
	 * <ul class='javatree'>
	 * 	<li class='jf'>{@link #addRootType()} - Affects whether <js>'_type'</js> is added to root node.
	 * 	<li class='jf'>{@link #addBeanTypes()} - Affects whether <js>'_type'</js> is added to any nodes.
	 * </ul>
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a serializer that adds _type to nodes.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.addBeanTypes()
	 * 		.build();
	 *
	 * 	<jc>// Our map of beans to serialize.</jc>
	 * 	<ja>@Bean</ja>(typeName=<js>"mybean"</js>)
	 * 	<jk>public class</jk> MyBean {
	 * 		<jk>public</jk> String <jf>foo</jf> = <js>"bar"</js>;
	 * 	}
	 * 	OMap <jv>myMap</jv> = OMap.of(<js>"foo"</js>, <jk>new</jk> MyBean());
	 *
	 * 	<jc>// Will contain:  {"foo":{"_type":"mybean","foo":"bar"}}</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.serialize(<jv>myMapp</jv>);
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder addBeanTypes() {
		return addBeanTypes(true);
	}

	/**
	 * Same as {@link #addBeanTypes()} but allows you to explicitly specify the value.
	 *
	 * @param value The value for this setting.
	 * @return This object.
	 */
	@FluentSetter
	public SerializerBuilder addBeanTypes(boolean value) {
		addBeanTypes = value;
		return this;
	}

	/**
	 * Add type attribute to root nodes.
	 *
	 * <p>
	 * When enabled, <js>"_type"</js> properties will be added to top-level beans.
	 *
	 * <p>
	 * When disabled, it is assumed that the parser knows the exact Java POJO type being parsed, and therefore top-level
	 * type information that might normally be included to determine the data type will not be serialized.
	 *
	 * <p>
	 * For example, when serializing a top-level POJO with a {@link Bean#typeName() @Bean(typeName)} value, a
	 * <js>'_type'</js> attribute will only be added when this setting is enabled.
	 *
	 * <p>
	 * Note the differences between the following settings:
	 * <ul class='javatree'>
	 * 	<li class='jf'>{@link #addRootType()} - Affects whether <js>'_type'</js> is added to root node.
	 * 	<li class='jf'>{@link #addBeanTypes()} - Affects whether <js>'_type'</js> is added to any nodes.
	 * </ul>
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a serializer that adds _type to root node.</jc>
	 * 	WriterSerializer <jv>serializer</jv>= JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.addRootType()
	 * 		.build();
	 *
	 * 	<jc>// Our bean to serialize.</jc>
	 * 	<ja>@Bean</ja>(typeName=<js>"mybean"</js>)
	 * 	<jk>public class</jk> MyBean {
	 * 		<jk>public</jk> String <jf>foo</jf> = <js>"bar"</js>;
	 * 	}
	 *
	 * 	<jc>// Will contain:  {"_type":"mybean","foo":"bar"}</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.serialize(<jk>new</jk> MyBean());
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder addRootType() {
		return addRootType(true);
	}

	/**
	 * Same as {@link #addRootType()} but allows you to explicitly specify the value.
	 *
	 * @param value The value for this setting.
	 * @return This object.
	 */
	@FluentSetter
	public SerializerBuilder addRootType(boolean value) {
		addRootType = value;
		return this;
	}

	/**
	 * Don't trim null bean property values.
	 *
	 * <p>
	 * When enabled, null bean values will be serialized to the output.
	 *
	 * <ul class='notes'>
	 * 	<li>Not enabling this setting will cause <c>Map</c>s with <jk>null</jk> values to be lost during parsing.
	 * </ul>
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a serializer that serializes null properties.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.keepNullProperties()
	 * 		.build();
	 *
	 * 	<jc>// Our bean to serialize.</jc>
	 * 	<jk>public class</jk> MyBean {
	 * 		<jk>public</jk> String <jf>foo</jf> = <jk>null</jk>;
	 * 	}
	 *
	 * 	<jc>// Will contain "{foo:null}".</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.serialize(<jk>new</jk> MyBean());
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder keepNullProperties() {
		return keepNullProperties(true);
	}

	/**
	 * Same as {@link #keepNullProperties()} but allows you to explicitly specify the value.
	 *
	 * @param value The value for this setting.
	 * @return This object.
	 */
	@FluentSetter
	public SerializerBuilder keepNullProperties(boolean value) {
		keepNullProperties = value;
		return this;
	}

	/**
	 * Serializer listener.
	 *
	 * <p>
	 * Class used to listen for errors and warnings that occur during serialization.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Define our serializer listener.</jc>
	 * 	<jc>// Simply captures all errors.</jc>
	 * 	<jk>public class</jk> MySerializerListener <jk>extends</jk> SerializerListener {
	 *
	 * 		<jc>// A simple property to store our events.</jc>
	 * 		<jk>public</jk> List&lt;String&gt; <jf>events</jf> = <jk>new</jk> LinkedList&lt;&gt;();
	 *
	 * 		<ja>@Override</ja>
	 * 		<jk>public</jk> &lt;T&gt; <jk>void</jk> onError(SerializerSession <jv>session</jv>, Throwable <jv>throwable</jv>, String <jv>msg</jv>) {
	 * 			<jf>events</jf>.add(<jv>session</jv>.getLastLocation() + <js>","</js> + <jv>msg</jv> + <js>","</js> + <jv>throwable</jv>);
	 * 		}
	 * 	}
	 *
	 * 	<jc>// Create a serializer using our listener.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.listener(MySerializerListener.<jk>class</jk>)
	 * 		.build();
	 *
	 * 	<jc>// Create a session object.</jc>
	 * 	<jc>// Needed because listeners are created per-session.</jc>
	 * 	<jk>try</jk> (WriterSerializerSession <jv>session</jv> = <jv>serializer</jv>.createSession()) {
	 *
	 * 		<jc>// Serialize a bean.</jc>
	 * 		String <jv>json</jv> = <jv>session</jv>.serialize(<jk>new</jk> MyBean());
	 *
	 * 		<jc>// Get the listener.</jc>
	 * 		MySerializerListener <jv>listener</jv> = <jv>session</jv>.getListener(MySerializerListener.<jk>class</jk>);
	 *
	 * 		<jc>// Dump the results to the console.</jc>
	 * 		SimpleJson.<jsf>DEFAULT</jsf>.println(<jv>listener</jv>.<jf>events</jf>);
	 * 	}
	 * </p>
	 *
	 * @param value
	 * 	The new value for this property.
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder listener(Class<? extends SerializerListener> value) {
		listener = value;
		return this;
	}

	/**
	 * Sort arrays and collections alphabetically.
	 *
	 * <p>
	 * When enabled, copies and sorts the contents of arrays and collections before serializing them.
	 *
	 * <p>
	 * Note that this introduces a performance penalty since it requires copying the existing collection.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a serializer that sorts arrays and collections before serialization.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.sortCollections()
	 * 		.build();
	 *
	 * 	<jc>// An unsorted array</jc>
	 * 	String[] <jv>myArray</jv> = {<js>"foo"</js>,<js>"bar"</js>,<js>"baz"</js>}
	 *
	 * 	<jc>// Produces ["bar","baz","foo"]</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.serialize(<jv>myArray</jv>);
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder sortCollections() {
		return sortCollections(true);
	}

	/**
	 * Same as {@link #sortCollections()} but allows you to explicitly specify the value.
	 *
	 * @param value The value for this setting.
	 * @return This object.
	 */
	@FluentSetter
	public SerializerBuilder sortCollections(boolean value) {
		sortCollections = value;
		return this;
	}

	/**
	 * Sort maps alphabetically.
	 *
	 * <p>
	 * When enabled, copies and sorts the contents of maps by their keys before serializing them.
	 *
	 * <p>
	 * Note that this introduces a performance penalty.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a serializer that sorts maps before serialization.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.sortMaps()
	 * 		.build();
	 *
	 * 	<jc>// An unsorted map.</jc>
	 * 	OMap <jv>myMap</jv> = OMap.<jsm>of</jsm>(<js>"foo"</js>,1,<js>"bar"</js>,2,<js>"baz"</js>,3);
	 *
	 * 	<jc>// Produces {"bar":2,"baz":3,"foo":1}</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.serialize(<jv>myMap</jv>);
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder sortMaps() {
		return sortMaps(true);
	}

	/**
	 * Same as {@link #sortMaps()} but allows you to explicitly specify the value.
	 *
	 * @param value The value for this setting.
	 * @return This object.
	 */
	@FluentSetter
	public SerializerBuilder sortMaps(boolean value) {
		sortMaps = value;
		return this;
	}

	/**
	 * Trim empty lists and arrays.
	 *
	 * <p>
	 * When enabled, empty lists and arrays will not be serialized.
	 *
	 * <p>
	 * Note that enabling this setting has the following effects on parsing:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Map entries with empty list values will be lost.
	 * 	<li>
	 * 		Bean properties with empty list values will not be set.
	 * </ul>
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a serializer that skips empty arrays and collections.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.trimEmptyCollections()
	 * 		.build();
	 *
	 * 	<jc>// A bean with a field with an empty array.</jc>
	 * 	<jk>public class</jk> MyBean {
	 * 		<jk>public</jk> String[] <jf>foo</jf> = {};
	 * 	}
	 *
	 * 	<jc>// Produces {}</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.serialize(<jk>new</jk> MyBean());
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder trimEmptyCollections() {
		return trimEmptyCollections(true);
	}

	/**
	 * Same as {@link #trimEmptyCollections()} but allows you to explicitly specify the value.
	 *
	 * @param value The value for this setting.
	 * @return This object.
	 */
	@FluentSetter
	public SerializerBuilder trimEmptyCollections(boolean value) {
		trimEmptyCollections = value;
		return this;
	}

	/**
	 * Trim empty maps.
	 *
	 * <p>
	 * When enabled, empty map values will not be serialized to the output.
	 *
	 * <p>
	 * Note that enabling this setting has the following effects on parsing:
	 * <ul class='spaced-list'>
	 * 	<li>
	 * 		Bean properties with empty map values will not be set.
	 * </ul>
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a serializer that skips empty maps.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.trimEmptyMaps()
	 * 		.build();
	 *
	 * 	<jc>// A bean with a field with an empty map.</jc>
	 * 	<jk>public class</jk> MyBean {
	 * 		<jk>public</jk> OMap <jf>foo</jf> = OMap.<jsm>of</jsm>();
	 * 	}
	 *
	 * 	<jc>// Produces {}</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.serialize(<jk>new</jk> MyBean());
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder trimEmptyMaps() {
		return trimEmptyMaps(true);
	}

	/**
	 * Same as {@link #trimEmptyMaps()} but allows you to explicitly specify the value.
	 *
	 * @param value The value for this setting.
	 * @return This object.
	 */
	@FluentSetter
	public SerializerBuilder trimEmptyMaps(boolean value) {
		trimEmptyMaps = value;
		return this;
	}

	/**
	 * Trim strings.
	 *
	 * <p>
	 * When enabled, string values will be trimmed of whitespace using {@link String#trim()} before being serialized.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Create a serializer that trims strings before serialization.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.trimStrings()
	 * 		.build();
	 *
	 *	<jc>// A map with space-padded keys/values</jc>
	 * 	OMap <jv>myMap</jv> = OMap.<jsm>of</jsm>(<js>" foo "</js>, <js>" bar "</js>);
	 *
	 * 	<jc>// Produces "{foo:'bar'}"</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.toString(<jv>myMap</jv>);
	 * </p>
	 *
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder trimStrings() {
		return trimStrings(true);
	}

	/**
	 * Same as {@link #trimStrings()} but allows you to explicitly specify the value.
	 *
	 * @param value The value for this setting.
	 * @return This object.
	 */
	@FluentSetter
	public SerializerBuilder trimStrings(boolean value) {
		trimStrings = value;
		return this;
	}

	/**
	 * URI context bean.
	 *
	 * <p>
	 * Bean used for resolution of URIs to absolute or root-relative form.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Our URI contextual information.</jc>
	 * 	String <jv>authority</jv> = <js>"http://localhost:10000"</js>;
	 * 	String <jv>contextRoot</jv> = <js>"/myContext"</js>;
	 * 	String <jv>servletPath</jv> = <js>"/myServlet"</js>;
	 * 	String <jv>pathInfo</jv> = <js>"/foo"</js>;
	 *
	 * 	<jc>// Create a UriContext object.</jc>
	 * 	UriContext <jv>uriContext</jv> = <jk>new</jk> UriContext(<jv>authority</jv>, <jv>contextRoot</jv>, <jv>servletPath</jv>, <jv>pathInfo</jv>);
	 *
	 * 	<jc>// Associate it with our serializer.</jc>
	 * 	WriterSerializer <jv>serializer</jv> = JsonSerializer
	 * 		.<jsm>create</jsm>()
	 * 		.uriContext(<jv>uriContext</jv>)
	 * 		.uriRelativity(<jsf>RESOURCE</jsf>)  <jc>// Assume relative paths are relative to servlet.</jc>
	 * 		.uriResolution(<jsf>ABSOLUTE</jsf>)  <jc>// Serialize URLs as absolute paths.</jc>
	 * 		.build();
	 *
	 * 	<jc>// A relative URL</jc>
	 * 	URL <jv>myUrl</jv> = <jk>new</jk> URL(<js>"bar"</js>);
	 *
	 * 	<jc>// Produces "http://localhost:10000/myContext/myServlet/foo/bar"</jc>
	 * 	String <jv>json</jv> = <jv>serializer</jv>.toString(<jv>myUrl</jv>);
	 * </p>
	 *
	 * <ul class='seealso'>
	 * 	<li class='link'>{@doc MarshallingUris}
	 * </ul>
	 *
	 * @param value The new value for this property.
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder uriContext(UriContext value) {
		uriContext = value;
		return this;
	}

	/**
	 * URI relativity.
	 *
	 * <p>
	 * Defines what relative URIs are relative to when serializing any of the following:
	 * <ul>
	 * 	<li>{@link java.net.URI}
	 * 	<li>{@link java.net.URL}
	 * 	<li>Properties and classes annotated with {@link Uri @Uri}
	 * </ul>
	 *
	 * <p>
	 * Possible values are:
	 * <ul class='javatree'>
	 * 	<li class='jf'>{@link org.apache.juneau.UriRelativity#RESOURCE}
	 * 		- Relative URIs should be considered relative to the servlet URI.
	 * 	<li class='jf'>{@link org.apache.juneau.UriRelativity#PATH_INFO}
	 * 		- Relative URIs should be considered relative to the request URI.
	 * </ul>
	 *
	 * <p>
	 * See {@link #uriContext(UriContext)} for examples.
	 *
	 * <ul class='seealso'>
	 * 	<li class='link'>{@doc MarshallingUris}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>The default is {@link UriRelativity#RESOURCE}
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder uriRelativity(UriRelativity value) {
		uriRelativity = value;
		return this;
	}

	/**
	 * URI resolution.
	 *
	 * <p>
	 * Defines the resolution level for URIs when serializing any of the following:
	 * <ul>
	 * 	<li>{@link java.net.URI}
	 * 	<li>{@link java.net.URL}
	 * 	<li>Properties and classes annotated with {@link Uri @Uri}
	 * </ul>
	 *
	 * <p>
	 * Possible values are:
	 * <ul>
	 * 	<li class='jf'>{@link UriResolution#ABSOLUTE}
	 * 		- Resolve to an absolute URL (e.g. <js>"http://host:port/context-root/servlet-path/path-info"</js>).
	 * 	<li class='jf'>{@link UriResolution#ROOT_RELATIVE}
	 * 		- Resolve to a root-relative URL (e.g. <js>"/context-root/servlet-path/path-info"</js>).
	 * 	<li class='jf'>{@link UriResolution#NONE}
	 * 		- Don't do any URL resolution.
	 * </ul>
	 *
	 * <p>
	 * See {@link #uriContext(UriContext)} for examples.
	 *
	 * <ul class='seealso'>
	 * 	<li class='link'>{@doc MarshallingUris}
	 * </ul>
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>The default is {@link UriResolution#NONE}
	 * @return This object (for method chaining).
	 */
	@FluentSetter
	public SerializerBuilder uriResolution(UriResolution value) {
		uriResolution = value;
		return this;
	}

	// <FluentSetters>

	@Override /* GENERATED - ContextBuilder */
	public SerializerBuilder applyAnnotations(java.lang.Class<?>...fromClasses) {
		super.applyAnnotations(fromClasses);
		return this;
	}

	@Override /* GENERATED - ContextBuilder */
	public SerializerBuilder applyAnnotations(Method...fromMethods) {
		super.applyAnnotations(fromMethods);
		return this;
	}

	@Override /* GENERATED - ContextBuilder */
	public SerializerBuilder apply(AnnotationWorkList work) {
		super.apply(work);
		return this;
	}

	@Override /* GENERATED - ContextBuilder */
	public SerializerBuilder debug() {
		super.debug();
		return this;
	}

	@Override /* GENERATED - ContextBuilder */
	public SerializerBuilder locale(Locale value) {
		super.locale(value);
		return this;
	}

	@Override /* GENERATED - ContextBuilder */
	public SerializerBuilder mediaType(MediaType value) {
		super.mediaType(value);
		return this;
	}

	@Override /* GENERATED - ContextBuilder */
	public SerializerBuilder timeZone(TimeZone value) {
		super.timeZone(value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder annotations(Annotation...values) {
		super.annotations(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanClassVisibility(Visibility value) {
		super.beanClassVisibility(value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanConstructorVisibility(Visibility value) {
		super.beanConstructorVisibility(value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanFieldVisibility(Visibility value) {
		super.beanFieldVisibility(value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanInterceptor(Class<?> on, Class<? extends org.apache.juneau.transform.BeanInterceptor<?>> value) {
		super.beanInterceptor(on, value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanMapPutReturnsOldValue() {
		super.beanMapPutReturnsOldValue();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanMethodVisibility(Visibility value) {
		super.beanMethodVisibility(value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanProperties(Map<String,Object> values) {
		super.beanProperties(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanProperties(Class<?> beanClass, String properties) {
		super.beanProperties(beanClass, properties);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanProperties(String beanClassName, String properties) {
		super.beanProperties(beanClassName, properties);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesExcludes(Map<String,Object> values) {
		super.beanPropertiesExcludes(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesExcludes(Class<?> beanClass, String properties) {
		super.beanPropertiesExcludes(beanClass, properties);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesExcludes(String beanClassName, String properties) {
		super.beanPropertiesExcludes(beanClassName, properties);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesReadOnly(Map<String,Object> values) {
		super.beanPropertiesReadOnly(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesReadOnly(Class<?> beanClass, String properties) {
		super.beanPropertiesReadOnly(beanClass, properties);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesReadOnly(String beanClassName, String properties) {
		super.beanPropertiesReadOnly(beanClassName, properties);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesWriteOnly(Map<String,Object> values) {
		super.beanPropertiesWriteOnly(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesWriteOnly(Class<?> beanClass, String properties) {
		super.beanPropertiesWriteOnly(beanClass, properties);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanPropertiesWriteOnly(String beanClassName, String properties) {
		super.beanPropertiesWriteOnly(beanClassName, properties);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beansRequireDefaultConstructor() {
		super.beansRequireDefaultConstructor();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beansRequireSerializable() {
		super.beansRequireSerializable();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beansRequireSettersForGetters() {
		super.beansRequireSettersForGetters();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder beanDictionary(Class<?>...values) {
		super.beanDictionary(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder dictionaryOn(Class<?> on, java.lang.Class<?>...values) {
		super.dictionaryOn(on, values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder disableBeansRequireSomeProperties() {
		super.disableBeansRequireSomeProperties();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder disableIgnoreMissingSetters() {
		super.disableIgnoreMissingSetters();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder disableIgnoreTransientFields() {
		super.disableIgnoreTransientFields();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder disableIgnoreUnknownNullBeanProperties() {
		super.disableIgnoreUnknownNullBeanProperties();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder disableInterfaceProxies() {
		super.disableInterfaceProxies();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public <T> SerializerBuilder example(Class<T> pojoClass, T o) {
		super.example(pojoClass, o);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public <T> SerializerBuilder example(Class<T> pojoClass, String json) {
		super.example(pojoClass, json);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder findFluentSetters() {
		super.findFluentSetters();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder findFluentSetters(Class<?> on) {
		super.findFluentSetters(on);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder ignoreInvocationExceptionsOnGetters() {
		super.ignoreInvocationExceptionsOnGetters();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder ignoreInvocationExceptionsOnSetters() {
		super.ignoreInvocationExceptionsOnSetters();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder ignoreUnknownBeanProperties() {
		super.ignoreUnknownBeanProperties();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder implClass(Class<?> interfaceClass, Class<?> implClass) {
		super.implClass(interfaceClass, implClass);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder implClasses(Map<Class<?>,Class<?>> values) {
		super.implClasses(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder interfaceClass(Class<?> on, Class<?> value) {
		super.interfaceClass(on, value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder interfaces(java.lang.Class<?>...value) {
		super.interfaces(value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder notBeanClasses(Class<?>...values) {
		super.notBeanClasses(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder notBeanPackages(String...values) {
		super.notBeanPackages(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder propertyNamer(Class<? extends org.apache.juneau.PropertyNamer> value) {
		super.propertyNamer(value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder propertyNamer(Class<?> on, Class<? extends org.apache.juneau.PropertyNamer> value) {
		super.propertyNamer(on, value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder sortProperties() {
		super.sortProperties();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder sortProperties(java.lang.Class<?>...on) {
		super.sortProperties(on);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder stopClass(Class<?> on, Class<?> value) {
		super.stopClass(on, value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder swaps(Class<?>...values) {
		super.swaps(values);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder typeName(Class<?> on, String value) {
		super.typeName(on, value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder typePropertyName(String value) {
		super.typePropertyName(value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder typePropertyName(Class<?> on, String value) {
		super.typePropertyName(on, value);
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder useEnumNames() {
		super.useEnumNames();
		return this;
	}

	@Override /* GENERATED - BeanContextBuilder */
	public SerializerBuilder useJavaBeanIntrospector() {
		super.useJavaBeanIntrospector();
		return this;
	}

	@Override /* GENERATED - BeanTraverseBuilder */
	public SerializerBuilder detectRecursions() {
		super.detectRecursions();
		return this;
	}

	@Override /* GENERATED - BeanTraverseBuilder */
	public SerializerBuilder ignoreRecursions() {
		super.ignoreRecursions();
		return this;
	}

	@Override /* GENERATED - BeanTraverseBuilder */
	public SerializerBuilder initialDepth(int value) {
		super.initialDepth(value);
		return this;
	}

	@Override /* GENERATED - BeanTraverseBuilder */
	public SerializerBuilder maxDepth(int value) {
		super.maxDepth(value);
		return this;
	}

	// </FluentSetters>
}