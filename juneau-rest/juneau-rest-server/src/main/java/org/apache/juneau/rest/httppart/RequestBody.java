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
package org.apache.juneau.rest.httppart;

import static org.apache.juneau.internal.IOUtils.*;
import static org.apache.juneau.internal.StringUtils.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import javax.servlet.*;

import org.apache.juneau.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.encoders.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.http.header.*;
import org.apache.juneau.http.response.*;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.util.*;

/**
 * Contains the body of the HTTP request.
 *
 * <p>
 * 	The {@link RequestBody} object is the API for accessing the body of an HTTP request.
 * 	It can be accessed by passing it as a parameter on your REST Java method:
 * </p>
 * <p class='bcode w800'>
 * 	<ja>@RestPost</ja>(...)
 * 	<jk>public</jk> Object myMethod(RequestBody <jv>body</jv>) {...}
 * </p>
 *
 * <h5 class='figure'>Example:</h5>
 * <p class='bcode w800'>
 * 	<ja>@RestPost</ja>(...)
 * 	<jk>public void</jk> doPost(RequestBody <jv>body</jv>) {
 * 		<jc>// Convert body to a linked list of Person objects.</jc>
 * 		List&lt;Person&gt; <jv>list</jv> = <jv>body</jv>.as(LinkedList.<jk>class</jk>, Person.<jk>class</jk>);
 * 		...
 * 	}
 * </p>
 *
 * <p>
 * 	Some important methods on this class are:
 * </p>
 * <ul class='javatree'>
 * 	<li class='jc'>{@link RequestBody}
 * 	<ul class='spaced-list'>
 * 		<li>Methods for accessing the raw contents of the request body:
 * 		<ul class='javatreec'>
 * 			<li class='jm'>{@link RequestBody#asBytes() asBytes()}
 * 			<li class='jm'>{@link RequestBody#asHex() asHex()}
 * 			<li class='jm'>{@link RequestBody#asSpacedHex() asSpacedHex()}
 * 			<li class='jm'>{@link RequestBody#asString() asString()}
 * 			<li class='jm'>{@link RequestBody#getInputStream() getInputStream()}
 * 			<li class='jm'>{@link RequestBody#getReader() getReader()}
 * 		</ul>
 * 		<li>Methods for parsing the contents of the request body:
 * 		<ul class='javatreec'>
 * 			<li class='jm'>{@link RequestBody#as(Class) as(Class)}
 * 			<li class='jm'>{@link RequestBody#as(Type, Type...) as(Type, Type...)}
 * 			<li class='jm'>{@link RequestBody#setSchema(HttpPartSchema) setSchema(HttpPartSchema)}
 * 		</ul>
 * 		<li>Other methods:
 * 		<ul class='javatreec'>
 * 			<li class='jm'>{@link RequestBody#cache() cache()}
 * 			<li class='jm'>{@link RequestBody#getParserMatch() getParserMatch()}
 * 		</ul>
 * 	</ul>
 * </ul>
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jrs.HttpParts}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@SuppressWarnings("unchecked")
public class RequestBody {

	private byte[] body;
	private final RestRequest req;
	private EncoderSet encoders;
	private Encoder encoder;
	private ParserSet parsers;
	private long maxInput;
	private int contentLength = 0;
	private MediaType mediaType;
	private Parser parser;
	private HttpPartSchema schema;

	/**
	 * Constructor.
	 *
	 * @param req The request creating this bean.
	 */
	public RequestBody(RestRequest req) {
		this.req = req;
	}

	/**
	 * Sets the encoders to use for decoding this body.
	 *
	 * @param value The new value for this setting.
	 * @return This object.
	 */
	public RequestBody encoders(EncoderSet value) {
		this.encoders = value;
		return this;
	}

	/**
	 * Sets the parsers to use for parsing this body.
	 *
	 * @param value The new value for this setting.
	 * @return This object.
	 */
	public RequestBody parsers(ParserSet value) {
		this.parsers = value;
		return this;
	}

	/**
	 * Sets the schema for this body.
	 *
	 * @param schema The new schema for this body.
	 * @return This object.
	 */
	public RequestBody setSchema(HttpPartSchema schema) {
		this.schema = schema;
		return this;
	}

	/**
	 * Sets the max input value for this body.
	 *
	 * @param value The new value for this setting.
	 * @return This object.
	 */
	public RequestBody maxInput(long value) {
		this.maxInput = value;
		return this;
	}

	/**
	 * Sets the media type of this body.
	 *
	 * @param value The new value for this setting.
	 * @return This object.
	 */
	public RequestBody mediaType(MediaType value) {
		this.mediaType = value;
		return this;
	}

	/**
	 * Sets the parser to use for this body.
	 *
	 * @param value The new value for this setting.
	 * @return This object.
	 */
	public RequestBody parser(Parser value) {
		this.parser = value;
		return this;
	}

	/**
	 * Sets the contents of this body.
	 *
	 * @param value The new value for this setting.
	 * @return This object.
	 */
	public RequestBody body(byte[] value) {
		this.body = value;
		return this;
	}

	boolean isLoaded() {
		return body != null;
	}

	/**
	 * Reads the input from the HTTP request parsed into a POJO.
	 *
	 * <p>
	 * The parser used is determined by the matching <c>Content-Type</c> header on the request.
	 *
	 * <p>
	 * If type is <jk>null</jk> or <code>Object.<jk>class</jk></code>, then the actual type will be determined
	 * automatically based on the following input:
	 * <table class='styled'>
	 * 	<tr><th>Type</th><th>JSON input</th><th>XML input</th><th>Return type</th></tr>
	 * 	<tr>
	 * 		<td>object</td>
	 * 		<td><js>"{...}"</js></td>
	 * 		<td><code><xt>&lt;object&gt;</xt>...<xt>&lt;/object&gt;</xt></code><br><code><xt>&lt;x</xt> <xa>type</xa>=<xs>'object'</xs><xt>&gt;</xt>...<xt>&lt;/x&gt;</xt></code></td>
	 * 		<td>{@link OMap}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>array</td>
	 * 		<td><js>"[...]"</js></td>
	 * 		<td><code><xt>&lt;array&gt;</xt>...<xt>&lt;/array&gt;</xt></code><br><code><xt>&lt;x</xt> <xa>type</xa>=<xs>'array'</xs><xt>&gt;</xt>...<xt>&lt;/x&gt;</xt></code></td>
	 * 		<td>{@link OList}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>string</td>
	 * 		<td><js>"'...'"</js></td>
	 * 		<td><code><xt>&lt;string&gt;</xt>...<xt>&lt;/string&gt;</xt></code><br><code><xt>&lt;x</xt> <xa>type</xa>=<xs>'string'</xs><xt>&gt;</xt>...<xt>&lt;/x&gt;</xt></code></td>
	 * 		<td>{@link String}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>number</td>
	 * 		<td><c>123</c></td>
	 * 		<td><code><xt>&lt;number&gt;</xt>123<xt>&lt;/number&gt;</xt></code><br><code><xt>&lt;x</xt> <xa>type</xa>=<xs>'number'</xs><xt>&gt;</xt>...<xt>&lt;/x&gt;</xt></code></td>
	 * 		<td>{@link Number}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>boolean</td>
	 * 		<td><jk>true</jk></td>
	 * 		<td><code><xt>&lt;boolean&gt;</xt>true<xt>&lt;/boolean&gt;</xt></code><br><code><xt>&lt;x</xt> <xa>type</xa>=<xs>'boolean'</xs><xt>&gt;</xt>...<xt>&lt;/x&gt;</xt></code></td>
	 * 		<td>{@link Boolean}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>null</td>
	 * 		<td><jk>null</jk> or blank</td>
	 * 		<td><code><xt>&lt;null/&gt;</xt></code> or blank<br><code><xt>&lt;x</xt> <xa>type</xa>=<xs>'null'</xs><xt>/&gt;</xt></code></td>
	 * 		<td><jk>null</jk></td>
	 * 	</tr>
	 * </table>
	 *
	 * <p>
	 * Refer to {@doc jm.PojoCategories} for a complete definition of supported POJOs.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Parse into an integer.</jc>
	 * 	<jk>int</jk> body = req.getBody().as(<jk>int</jk>.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into an int array.</jc>
	 * 	<jk>int</jk>[] body = req.getBody().as(<jk>int</jk>[].<jk>class</jk>);

	 * 	<jc>// Parse into a bean.</jc>
	 * 	MyBean body = req.getBody().as(MyBean.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a linked-list of objects.</jc>
	 * 	List body = req.getBody().as(LinkedList.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a map of object keys/values.</jc>
	 * 	Map body = req.getBody().as(TreeMap.<jk>class</jk>);
	 * </p>
	 *
	 * <ul class='notes'>
	 * 	<li>
	 * 		If {@code allowHeaderParams} init parameter is true, then first looks for {@code &body=xxx} in the URL query string.
	 * </ul>
	 *
	 * @param type The class type to instantiate.
	 * @param <T> The class type to instantiate.
	 * @return The input parsed to a POJO.
	 * @throws BadRequest Thrown if input could not be parsed or fails schema validation.
	 * @throws UnsupportedMediaType Thrown if the Content-Type header value is not supported by one of the parsers.
	 * @throws InternalServerError Thrown if an {@link IOException} occurs.
	 */
	public <T> T as(Class<T> type) throws BadRequest, UnsupportedMediaType, InternalServerError {
		return getInner(getClassMeta(type));
	}

	/**
	 * Reads the input from the HTTP request parsed into a POJO.
	 *
	 * <p>
	 * This is similar to {@link #as(Class)} but allows for complex collections of POJOs to be created.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Parse into a linked-list of strings.</jc>
	 * 	List&lt;String&gt; body = req.getBody().as(LinkedList.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a linked-list of linked-lists of strings.</jc>
	 * 	List&lt;List&lt;String&gt;&gt; body = req.getBody().as(LinkedList.<jk>class</jk>, LinkedList.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a map of string keys/values.</jc>
	 * 	Map&lt;String,String&gt; body = req.getBody().as(TreeMap.<jk>class</jk>, String.<jk>class</jk>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a map containing string keys and values of lists containing beans.</jc>
	 * 	Map&lt;String,List&lt;MyBean&gt;&gt; body = req.getBody().as(TreeMap.<jk>class</jk>, String.<jk>class</jk>, List.<jk>class</jk>, MyBean.<jk>class</jk>);
	 * </p>
	 *
	 * <ul class='notes'>
	 * 	<li>
	 * 		<c>Collections</c> must be followed by zero or one parameter representing the value type.
	 * 	<li>
	 * 		<c>Maps</c> must be followed by zero or two parameters representing the key and value types.
	 * 	<li>
	 * 		If {@code allowHeaderParams} init parameter is true, then first looks for {@code &body=xxx} in the URL query string.
	 * </ul>
	 *
	 * @param type
	 * 	The type of object to create.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * @param args
	 * 	The type arguments of the class if it's a collection or map.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * 	<br>Ignored if the main type is not a map or collection.
	 * @param <T> The class type to instantiate.
	 * @return The input parsed to a POJO.
	 * @throws BadRequest Thrown if input could not be parsed or fails schema validation.
	 * @throws UnsupportedMediaType Thrown if the Content-Type header value is not supported by one of the parsers.
	 * @throws InternalServerError Thrown if an {@link IOException} occurs.
	 */
	public <T> T as(Type type, Type...args) throws BadRequest, UnsupportedMediaType, InternalServerError {
		return getInner(this.<T>getClassMeta(type, args));
	}

	/**
	 * Returns the HTTP body content as a plain string.
	 *
	 * <ul class='notes'>
	 * 	<li>
	 * 		If {@code allowHeaderParams} init parameter is true, then first looks for {@code &body=xxx} in the URL query string.
	 * </ul>
	 *
	 * @return The incoming input from the connection as a plain string.
	 * @throws IOException If a problem occurred trying to read from the reader.
	 */
	public String asString() throws IOException {
		cache();
		return new String(body, UTF8);
	}

	/**
	 * Returns the HTTP body content as a plain string.
	 *
	 * <ul class='notes'>
	 * 	<li>
	 * 		If {@code allowHeaderParams} init parameter is true, then first looks for {@code &body=xxx} in the URL query string.
	 * </ul>
	 *
	 * @return The incoming input from the connection as a plain string.
	 * @throws IOException If a problem occurred trying to read from the reader.
	 */
	public byte[] asBytes() throws IOException {
		cache();
		return body;
	}

	/**
	 * Returns the HTTP body content as a simple hexadecimal character string.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	0123456789ABCDEF
	 * </p>
	 *
	 * @return The incoming input from the connection as a plain string.
	 * @throws IOException If a problem occurred trying to read from the reader.
	 */
	public String asHex() throws IOException {
		cache();
		return toHex(body);
	}

	/**
	 * Returns the HTTP body content as a simple space-delimited hexadecimal character string.
	 *
	 * <h5 class='section'>Example:</h5>
	 * <p class='bcode w800'>
	 * 	01 23 45 67 89 AB CD EF
	 * </p>
	 *
	 * @return The incoming input from the connection as a plain string.
	 * @throws IOException If a problem occurred trying to read from the reader.
	 */
	public String asSpacedHex() throws IOException {
		cache();
		return toSpacedHex(body);
	}

	/**
	 * Returns the HTTP body content as a {@link Reader}.
	 *
	 * <ul class='notes'>
	 * 	<li>
	 * 		If {@code allowHeaderParams} init parameter is true, then first looks for {@code &body=xxx} in the URL query string.
	 * 	<li>
	 * 		Automatically handles GZipped input streams.
	 * </ul>
	 *
	 * @return The body contents as a reader.
	 * @throws IOException Thrown by underlying stream.
	 */
	public BufferedReader getReader() throws IOException {
		Reader r = getUnbufferedReader();
		if (r instanceof BufferedReader)
			return (BufferedReader)r;
		int len = req.getHttpServletRequest().getContentLength();
		int buffSize = len <= 0 ? 8192 : Math.max(len, 8192);
		return new BufferedReader(r, buffSize);
	}

	/**
	 * Same as {@link #getReader()}, but doesn't encapsulate the result in a {@link BufferedReader};
	 *
	 * @return An unbuffered reader.
	 * @throws IOException Thrown by underlying stream.
	 */
	protected Reader getUnbufferedReader() throws IOException {
		if (body != null)
			return new CharSequenceReader(new String(body, UTF8));
		return new InputStreamReader(getInputStream(), req.getCharset());
	}

	/**
	 * Returns the HTTP body content as an {@link InputStream}.
	 *
	 * @return The negotiated input stream.
	 * @throws IOException If any error occurred while trying to get the input stream or wrap it in the GZIP wrapper.
	 */
	public ServletInputStream getInputStream() throws IOException {

		if (body != null)
			return new BoundedServletInputStream(body);

		Encoder enc = getEncoder();

		InputStream is = req.getHttpServletRequest().getInputStream();

		if (enc == null)
			return new BoundedServletInputStream(is, maxInput);

		return new BoundedServletInputStream(enc.getInputStream(is), maxInput);
	}

	/**
	 * Returns the parser and media type matching the request <c>Content-Type</c> header.
	 *
	 * @return
	 * 	The parser matching the request <c>Content-Type</c> header, or {@link Optional#empty()} if no matching parser was
	 * 	found.
	 * 	Includes the matching media type.
	 */
	public Optional<ParserMatch> getParserMatch() {
		if (mediaType != null && parser != null)
			return Optional.of(new ParserMatch(mediaType, parser));
		MediaType mt = getMediaType();
		return Optional.ofNullable(mt == null ? null : parsers.getParserMatch(mt));
	}

	private MediaType getMediaType() {
		if (mediaType != null)
			return mediaType;
		Optional<ContentType> ct = req.getHeader(ContentType.class);
		if (!ct.isPresent() && body != null)
			return MediaType.UON;
		return ct.isPresent() ? ct.get().asMediaType().orElse(null) : null;
	}

	private <T> T getInner(ClassMeta<T> cm) throws BadRequest, UnsupportedMediaType, InternalServerError {
		try {
			return parse(cm);
		} catch (UnsupportedMediaType e) {
			throw e;
		} catch (SchemaValidationException e) {
			throw new BadRequest("Validation failed on request body. " + e.getLocalizedMessage());
		} catch (ParseException e) {
			throw new BadRequest(e, "Could not convert request body content to class type ''{0}''.", cm);
		} catch (IOException e) {
			throw new InternalServerError(e, "I/O exception occurred while parsing request body.");
		} catch (Exception e) {
			throw new InternalServerError(e, "Exception occurred while parsing request body.");
		}
	}

	/* Workhorse method */
	private <T> T parse(ClassMeta<T> cm) throws SchemaValidationException, ParseException, UnsupportedMediaType, IOException {

		if (cm.isReader())
			return (T)getReader();

		if (cm.isInputStream())
			return (T)getInputStream();

		Optional<TimeZone> timeZone = req.getTimeZone();
		Locale locale = req.getLocale();
		ParserMatch pm = getParserMatch().orElse(null);

		if (schema == null)
			schema = HttpPartSchema.DEFAULT;

		if (pm != null) {
			Parser p = pm.getParser();
			MediaType mediaType = pm.getMediaType();
			ParserSession session = p
				.createSession()
				.properties(req.getAttributes().asMap())
				.javaMethod(req.getOpContext().getJavaMethod())
				.locale(locale)
				.timeZone(timeZone.orElse(null))
				.mediaType(mediaType)
				.apply(ReaderParser.Builder.class, x -> x.streamCharset(req.getCharset()))
				.schema(schema)
				.debug(req.isDebug() ? true : null)
				.outer(req.getContext().getResource())
				.build();
			;
			try (Closeable in = session.isReaderParser() ? getUnbufferedReader() : getInputStream()) {
				T o = session.parse(in, cm);
				if (schema != null)
					schema.validateOutput(o, cm.getBeanContext());
				return o;
			}
		}

		if (cm.hasReaderMutater())
			return cm.getReaderMutater().mutate(getReader());

		if (cm.hasInputStreamMutater())
			return cm.getInputStreamMutater().mutate(getInputStream());

		MediaType mt = getMediaType();

		if ((isEmpty(mt) || mt.toString().startsWith("text/plain")) && cm.hasStringMutater())
			return cm.getStringMutater().mutate(asString());

		Optional<ContentType> ct = req.getHeader(ContentType.class);
		throw new UnsupportedMediaType(
			"Unsupported media-type in request header ''Content-Type'': ''{0}''\n\tSupported media-types: {1}",
			ct.isPresent() ? ct.get().asMediaType().orElse(null) : "not-specified", json(req.getOpContext().getParsers().getSupportedMediaTypes())
		);
	}

	private Encoder getEncoder() throws UnsupportedMediaType {
		if (encoder == null) {
			String ce = req.getHeader("content-encoding").orElse(null);
			if (isNotEmpty(ce)) {
				ce = ce.trim();
				encoder = encoders.getEncoder(ce);
				if (encoder == null)
					throw new UnsupportedMediaType(
						"Unsupported encoding in request header ''Content-Encoding'': ''{0}''\n\tSupported codings: {1}",
						req.getHeader("content-encoding").orElse(null), json(encoders.getSupportedEncodings())
					);
			}

			if (encoder != null)
				contentLength = -1;
		}
		// Note that if this is the identity encoder, we want to return null
		// so that we don't needlessly wrap the input stream.
		if (encoder == IdentityEncoder.INSTANCE)
			return null;
		return encoder;
	}

	/**
	 * Returns the content length of the body.
	 *
	 * @return The content length of the body in bytes.
	 */
	public int getContentLength() {
		return contentLength == 0 ? req.getHttpServletRequest().getContentLength() : contentLength;
	}

	/**
	 * Caches the body in memory for reuse.
	 *
	 * @return This object.
	 * @throws IOException If error occurs while reading stream.
	 */
	public RequestBody cache() throws IOException {
		if (body == null)
			body = readBytes(getInputStream());
		return this;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Helper methods
	//-----------------------------------------------------------------------------------------------------------------

	private <T> ClassMeta<T> getClassMeta(Type type, Type...args) {
		return req.getBeanSession().getClassMeta(type, args);
	}

	private <T> ClassMeta<T> getClassMeta(Class<T> type) {
		return req.getBeanSession().getClassMeta(type);
	}
}