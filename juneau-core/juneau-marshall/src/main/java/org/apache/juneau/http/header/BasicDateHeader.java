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
package org.apache.juneau.http.header;

import static java.time.format.DateTimeFormatter.*;
import static java.time.temporal.ChronoUnit.*;
import static org.apache.juneau.internal.ThrowableUtils.*;
import static org.apache.juneau.internal.StringUtils.*;
import static java.util.Optional.*;

import java.time.*;
import java.util.*;
import java.util.function.*;

import org.apache.juneau.assertions.*;

/**
 * Category of headers that consist of a single HTTP-date.
 *
 * <p>
 * <h5 class='figure'>Example</h5>
 * <p class='bcode w800'>
 * 	If-Modified-Since: Sat, 29 Oct 1994 19:43:31 GMT
 * </p>
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc TODO}
 * 	<li class='extlink'>{@doc ext.RFC2616}
 * 	<li class='extlink'>{@source}
 * </ul>
 *
 * @serial exclude
 */
public class BasicDateHeader extends BasicHeader {

	//-----------------------------------------------------------------------------------------------------------------
	// Static
	//-----------------------------------------------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	/**
	 * Static creator.
	 *
	 * @param name The header name.
	 * @param value
	 * 	The header value.
	 * 	<br>Must be an RFC-1123 formated string (e.g. <js>"Sat, 29 Oct 1994 19:43:31 GMT"</js>).
	 * 	<br>Can be <jk>null</jk>.
	 * @return A new header bean, or <jk>null</jk> if the name is <jk>null</jk> or empty or the value is <jk>null</jk>.
	 */
	public static BasicDateHeader of(String name, String value) {
		return value == null || isEmpty(name) ? null : new BasicDateHeader(name, value);
	}

	/**
	 * Static creator.
	 *
	 * @param name The header name.
	 * @param value
	 * 	The header value.
	 * 	<br>Can be <jk>null</jk>.
	 * @return A new header bean, or <jk>null</jk> if the name is <jk>null</jk> or empty or the value is <jk>null</jk>.
	 */
	public static BasicDateHeader of(String name, ZonedDateTime value) {
		return value == null || isEmpty(name) ? null : new BasicDateHeader(name, value);
	}

	/**
	 * Static creator with delayed value.
	 *
	 * <p>
	 * Header value is re-evaluated on each call to {@link #getValue()}.
	 *
	 * @param name The header name.
	 * @param value
	 * 	The supplier of the header value.
	 * 	<br>Can be <jk>null</jk>.
	 * @return A new header bean, or <jk>null</jk> if the name is <jk>null</jk> or empty or the value is <jk>null</jk>.
	 */
	public static BasicDateHeader of(String name, Supplier<ZonedDateTime> value) {
		return value == null || isEmpty(name) ? null : new BasicDateHeader(name, value);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Instance
	//-----------------------------------------------------------------------------------------------------------------

	private final ZonedDateTime value;
	private final Supplier<ZonedDateTime> supplier;

	/**
	 * Constructor.
	 *
	 * @param name The header name.
	 * @param value
	 * 	The header value.
	 * 	<br>Must be an RFC-1123 formated string (e.g. <js>"Sat, 29 Oct 1994 19:43:31 GMT"</js>).
	 * 	<br>Can be <jk>null</jk>.
	 */
	public BasicDateHeader(String name, String value) {
		super(name, value);
		this.value = parse(value);
		this.supplier = null;
	}

	/**
	 * Constructor.
	 *
	 * @param name The header name.
	 * @param value
	 * 	The header value.
	 * 	<br>Can be <jk>null</jk>.
	 */
	public BasicDateHeader(String name, ZonedDateTime value) {
		super(name, serialize(value));
		this.value = value;
		this.supplier = null;
	}

	/**
	 * Constructor with delayed value.
	 *
	 * <p>
	 * Header value is re-evaluated on each call to {@link #getValue()}.
	 *
	 * @param name The header name.
	 * @param value
	 * 	The supplier of the header value.
	 * 	<br>Can be <jk>null</jk>.
	 */
	public BasicDateHeader(String name, Supplier<ZonedDateTime> value) {
		super(name, null);
		this.value = null;
		this.supplier = value;
	}

	@Override /* Header */
	public String getValue() {
		if (supplier != null)
			return serialize(supplier.get());
		return super.getValue();
	}

	/**
	 * Returns this header value as a {@link ZonedDateTime}.
	 *
	 * @return This header value as a {@link ZonedDateTime}, or {@link Optional#empty()} if the header could not be parsed.
	 */
	public Optional<ZonedDateTime> asZonedDateTime() {
		if (supplier != null)
			return ofNullable(supplier.get());
		return ofNullable(value);
	}

	/**
	 * Provides the ability to perform fluent-style assertions on this header.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bcode w800'>
	 * 	<jc>// Validates the response body content is not expired.</jc>
	 * 	client
	 * 		.get(<jsf>URL</jsf>)
	 * 		.run()
	 * 		.getDateHeader(<js>"Expires"</js>).assertThat().isLessThan(<jk>new</jk> Date());
	 * </p>
	 *
	 * @return A new fluent assertion object.
	 * @throws AssertionError If assertion failed.
	 */
	public FluentZonedDateTimeAssertion<BasicDateHeader> assertZonedDateTime() {
		return new FluentZonedDateTimeAssertion<>(asZonedDateTime().orElse(null), this);
	}

	private static String serialize(ZonedDateTime value) {
		return value == null ? null : RFC_1123_DATE_TIME.format(value);
	}

	private static ZonedDateTime parse(String value) {
		try {
			return value == null ? null : ZonedDateTime.from(RFC_1123_DATE_TIME.parse(value.toString())).truncatedTo(SECONDS);
		} catch (NumberFormatException e) {
			throw runtimeException("Value ''{0}'' could not be parsed as a zoned date-time.");
		}
	}
}
