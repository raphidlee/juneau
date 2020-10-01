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
package org.apache.juneau.http.annotation;

import java.lang.annotation.*;

/**
 * A concrete implementation of the {@link HasQuery} annotation.
 */
public class HasQueryAnnotation implements HasQuery {

	private String name="", n="", value="";

	@Override /* Annotation */
	public Class<? extends Annotation> annotationType() {
		return HasQuery.class;
	}

	@Override /* HasQuery */
	public String name() {
		return name;
	}

	/**
	 * Sets the <c>name</c> property on this annotation.
	 *
	 * @param value The new value for this property.
	 * @return This object (for method chaining).
	 */
	public HasQueryAnnotation name(String value) {
		this.name = value;
		return this;
	}

	@Override /* HasQuery */
	public String n() {
		return n;
	}

	/**
	 * Sets the <c>n</c> property on this annotation.
	 *
	 * @param value The new value for this property.
	 * @return This object (for method chaining).
	 */
	public HasQueryAnnotation n(String value) {
		this.n = value;
		return this;
	}

	@Override /* HasQuery */
	public String value() {
		return value;
	}

	/**
	 * Sets the <c>value</c> property on this annotation.
	 *
	 * @param value The new value for this property.
	 * @return This object (for method chaining).
	 */
	public HasQueryAnnotation value(String value) {
		this.value = value;
		return this;
	}
}