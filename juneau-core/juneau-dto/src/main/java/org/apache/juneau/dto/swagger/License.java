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
package org.apache.juneau.dto.swagger;

import static org.apache.juneau.internal.StringUtils.*;
import static org.apache.juneau.internal.ConverterUtils.*;

import java.net.*;
import java.net.URI;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.collections.*;
import org.apache.juneau.internal.*;

/**
 * License information for the exposed API.
 *
 * <h5 class='section'>Example:</h5>
 * <p class='bcode w800'>
 * 	<jc>// Construct using SwaggerBuilder.</jc>
 * 	License <jv>license</jv> = <jsm>license</jsm>(<js>"Apache 2.0"</js>, <js>"http://www.apache.org/licenses/LICENSE-2.0.html"</js>);
 *
 * 	<jc>// Serialize using JsonSerializer.</jc>
 *
 * 	<jc>// Or just use toString() which does the same as above.</jc>
 * 	<jv>json</jv> = <jv>license</jv>.toString();
 * </p>
 * <p class='bcode w800'>
 * 	<jc>// Output</jc>
 * 	{
 * 		<js>"name"</js>: <js>"Apache 2.0"</js>,
 * 		<js>"url"</js>: <js>"http://www.apache.org/licenses/LICENSE-2.0.html"</js>
 * 	}
 * </p>
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jd.Swagger}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
@Bean(properties="name,url,*")
public class License extends SwaggerElement {

	private String name;
	private URI url;

	/**
	 * Default constructor.
	 */
	public License() {}

	/**
	 * Copy constructor.
	 *
	 * @param copyFrom The object to copy.
	 */
	public License(License copyFrom) {
		super(copyFrom);

		this.name = copyFrom.name;
		this.url = copyFrom.url;
	}

	/**
	 * Make a deep copy of this object.
	 *
	 * @return A deep copy of this object.
	 */
	public License copy() {
		return new License(this);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// name
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Bean property getter:  <property>name</property>.
	 *
	 * <p>
	 * The license name used for the API.
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Bean property setter:  <property>name</property>.
	 *
	 * <p>
	 * The license name used for the API.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Property value is required.
	 */
	public void setName(String value) {
		name = value;
	}

	/**
	 * Bean property fluent getter:  <property>name</property>.
	 *
	 * <p>
	 * The license name used for the API.
	 *
	 * @return The property value as an {@link Optional}.  Never <jk>null</jk>.
	 */
	public Optional<String> name() {
		return Optional.ofNullable(getName());
	}

	/**
	 * Bean property fluent setter:  <property>name</property>.
	 *
	 * <p>
	 * The license name used for the API.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public License name(String value) {
		setName(value);
		return this;
	}

	//-----------------------------------------------------------------------------------------------------------------
	// url
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Bean property getter:  <property>url</property>.
	 *
	 * <p>
	 * A URL to the license used for the API.
	 *
	 * @return The property value, or <jk>null</jk> if it is not set.
	 */
	public URI getUrl() {
		return url;
	}

	/**
	 * Bean property setter:  <property>url</property>.
	 *
	 * <p>
	 * A URL to the license used for the API.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>URIs defined by {@link UriResolver} can be used for values.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 */
	public void setUrl(URI value) {
		url = value;
	}

	/**
	 * Bean property fluent getter:  <property>url</property>.
	 *
	 * <p>
	 * A URL to the license used for the API.
	 *
	 * @return The property value as an {@link Optional}.  Never <jk>null</jk>.
	 */
	public Optional<URI> url() {
		return Optional.ofNullable(getUrl());
	}

	/**
	 * Bean property fluent setter:  <property>url</property>.
	 *
	 * <p>
	 * A URL to the license used for the API.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public License url(URI value) {
		setUrl(value);
		return this;
	}

	/**
	 * Bean property fluent setter:  <property>url</property>.
	 *
	 * <p>
	 * A URL to the license used for the API.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public License url(URL value) {
		setUrl(StringUtils.toURI(value));
		return this;
	}

	/**
	 * Bean property fluent setter:  <property>url</property>.
	 *
	 * <p>
	 * A URL to the license used for the API.
	 *
	 * @param value
	 * 	The new value for this property.
	 * 	<br>Can be <jk>null</jk> to unset the property.
	 * @return This object.
	 */
	public License url(String value) {
		setUrl(StringUtils.toURI(value));
		return this;
	}

	@Override /* SwaggerElement */
	public <T> T get(String property, Class<T> type) {
		if (property == null)
			return null;
		switch (property) {
			case "name": return toType(getName(), type);
			case "url": return toType(getUrl(), type);
			default: return super.get(property, type);
		}
	}

	@Override /* SwaggerElement */
	public License set(String property, Object value) {
		if (property == null)
			return this;
		switch (property) {
			case "name": return name(stringify(value));
			case "url": return url(StringUtils.toURI(value));
			default:
				super.set(property, value);
				return this;
		}
	}

	@Override /* SwaggerElement */
	public Set<String> keySet() {
		ASet<String> s = ASet.<String>of()
			.appendIf(name != null, "name")
			.appendIf(url != null, "url");
		return new MultiSet<>(s, super.keySet());
	}
}
