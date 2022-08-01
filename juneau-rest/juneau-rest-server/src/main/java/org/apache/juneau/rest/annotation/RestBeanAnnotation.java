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
package org.apache.juneau.rest.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

import org.apache.juneau.annotation.*;

/**
 * Utility classes and methods for the {@link RestBean RestBean} annotation.
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@source}
 * </ul>
 */
public class RestBeanAnnotation {

	//-----------------------------------------------------------------------------------------------------------------
	// Static
	//-----------------------------------------------------------------------------------------------------------------

	/** Default value */
	public static final RestBean DEFAULT = create().build();

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @return A new builder object.
	 */
	public static Builder create() {
		return new Builder();
	}

	/**
	 * Pulls the name/value attribute from a {@link RestBean} annotation.
	 *
	 * @param a The annotation to check.  Can be <jk>null</jk>.
	 * @return The annotation value, or an empty string if the annotation is <jk>null</jk>.
	 */
	public static String name(RestBean a) {
		if (a == null)
			return "";
		if (! a.name().isEmpty())
			return a.name();
		return a.value();
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Builder
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Builder class.
	 *
	 * <ul class='seealso'>
	 * 	<li class='jm'>{@link org.apache.juneau.BeanContext.Builder#annotations(Annotation...)}
	 * </ul>
	 */
	public static class Builder extends TargetedAnnotationMBuilder {

		String name, value;
		String[] methodScope;

		/**
		 * Constructor.
		 */
		protected Builder() {
			super(RestBean.class);
		}

		/**
		 * Instantiates a new {@link RestBean @RestBean} object initialized with this builder.
		 *
		 * @return A new {@link RestBean @RestBean} object.
		 */
		public RestBean build() {
			return new Impl(this);
		}

		/**
		 * Sets the {@link RestBean#name()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object.
		 */
		public Builder name(String value) {
			this.name = value;
			return this;
		}

		/**
		 * Sets the {@link RestBean#value()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object.
		 */
		public Builder value(String value) {
			this.value = value;
			return this;
		}

		/**
		 * Sets the {@link RestBean#methodScope()} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object.
		 */
		public Builder methodScope(String...value) {
			this.methodScope = value;
			return this;
		}

		// <FluentSetters>

		@Override /* GENERATED - TargetedAnnotationBuilder */
		public Builder on(String...values) {
			super.on(values);
			return this;
		}

		@Override /* GENERATED - TargetedAnnotationTMBuilder */
		public Builder on(java.lang.reflect.Method...value) {
			super.on(value);
			return this;
		}

		// </FluentSetters>
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Implementation
	//-----------------------------------------------------------------------------------------------------------------

	private static class Impl extends TargetedAnnotationImpl implements RestBean {

		private final String name, value;
		private final String[] methodScope;

		Impl(Builder b) {
			super(b);
			this.name = b.name;
			this.value = b.value;
			this.methodScope = b.methodScope;
			postConstruct();
		}

		@Override /* RestBean */
		public String name() {
			return name;
		}

		@Override /* RestBean */
		public String value() {
			return value;
		}

		@Override /* RestBean */
		public String[] methodScope() {
			return methodScope;
		}
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * A collection of {@link RestBean @RestBean annotations}.
	 */
	@Documented
	@Target({FIELD,METHOD,TYPE})
	@Retention(RUNTIME)
	@Inherited
	public static @interface Array {

		/**
		 * The child annotations.
		 *
		 * @return The annotation value.
		 */
		RestBean[] value();
	}
}
