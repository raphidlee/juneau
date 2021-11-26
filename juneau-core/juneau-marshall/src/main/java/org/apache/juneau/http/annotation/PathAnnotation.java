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

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import static org.apache.juneau.internal.StringUtils.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.annotation.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.svl.*;

/**
 * Utility classes and methods for the {@link Path @Path} annotation.
 */
public class PathAnnotation {

	//-----------------------------------------------------------------------------------------------------------------
	// Static
	//-----------------------------------------------------------------------------------------------------------------

	/** Default value */
	public static final Path DEFAULT = create().build();

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @return A new builder object.
	 */
	public static Builder create() {
		return new Builder();
	}

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @param on The targets this annotation applies to.
	 * @return A new builder object.
	 */
	public static Builder create(Class<?>...on) {
		return create().on(on);
	}

	/**
	 * Instantiates a new builder for this class.
	 *
	 * @param on The targets this annotation applies to.
	 * @return A new builder object.
	 */
	public static Builder create(String...on) {
		return create().on(on);
	}

	/**
	 * Returns <jk>true</jk> if the specified annotation contains all default values.
	 *
	 * @param a The annotation to check.
	 * @return <jk>true</jk> if the specified annotation contains all default values.
	 */
	public static boolean empty(Path a) {
		return a == null || DEFAULT.equals(a);
	}

	/**
	 * Finds the name from the specified lists of annotations.
	 *
	 * <p>
	 * The last matching name found is returned.
	 *
	 * @param lists The lists to search.
	 * @return The last matching name, or {@link Optional#empty()} if not found.
	 */
	@SafeVarargs
	public static Optional<String> findName(List<Path>...lists) {
		String n = null;
		for (List<Path> l : lists)
			for (Path h : l)
				n = firstNonEmpty(h.name(), h.value(), n);
		return Optional.ofNullable(n);
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
	public static class Builder extends TargetedAnnotationTMFBuilder {

		Class<? extends HttpPartParser> parser = HttpPartParser.Null.class;
		Class<? extends HttpPartSerializer> serializer = HttpPartSerializer.Null.class;
		Schema schema = SchemaAnnotation.DEFAULT;
		String name="", value="";

		/**
		 * Constructor.
		 */
		protected Builder() {
			super(Path.class);
		}

		/**
		 * Instantiates a new {@link Path @Path} object initialized with this builder.
		 *
		 * @return A new {@link Path @Path} object.
		 */
		public Path build() {
			return new Impl(this);
		}

		/**
		 * Sets the {@link Path#name} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object.
		 */
		public Builder name(String value) {
			this.name = value;
			return this;
		}

		/**
		 * Sets the {@link Path#parser} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object.
		 */
		public Builder parser(Class<? extends HttpPartParser> value) {
			this.parser = value;
			return this;
		}

		/**
		 * Sets the {@link Path#schema} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object.
		 */
		public Builder schema(Schema value) {
			this.schema = value;
			return this;
		}

		/**
		 * Sets the {@link Path#serializer} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object.
		 */
		public Builder serializer(Class<? extends HttpPartSerializer> value) {
			this.serializer = value;
			return this;
		}

		/**
		 * Sets the {@link Path#value} property on this annotation.
		 *
		 * @param value The new value for this property.
		 * @return This object.
		 */
		public Builder value(String value) {
			this.value = value;
			return this;
		}

		// <FluentSetters>

		@Override /* GENERATED - TargetedAnnotationBuilder */
		public Builder on(String...values) {
			super.on(values);
			return this;
		}

		@Override /* GENERATED - TargetedAnnotationTBuilder */
		public Builder on(java.lang.Class<?>...value) {
			super.on(value);
			return this;
		}

		@Override /* GENERATED - TargetedAnnotationTBuilder */
		public Builder onClass(java.lang.Class<?>...value) {
			super.onClass(value);
			return this;
		}

		@Override /* GENERATED - TargetedAnnotationTMFBuilder */
		public Builder on(Field...value) {
			super.on(value);
			return this;
		}

		@Override /* GENERATED - TargetedAnnotationTMFBuilder */
		public Builder on(Method...value) {
			super.on(value);
			return this;
		}

		// </FluentSetters>
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Implementation
	//-----------------------------------------------------------------------------------------------------------------

	private static class Impl extends TargetedAnnotationTImpl implements Path {

		private final Class<? extends HttpPartParser> parser;
		private final Class<? extends HttpPartSerializer> serializer;
		private final String  name, value;
		private final Schema schema;

		Impl(Builder b) {
			super(b);
			this.name = b.name;
			this.parser = b.parser;
			this.schema = b.schema;
			this.serializer = b.serializer;
			this.value = b.value;
			postConstruct();
		}

		@Override /* Path */
		public String name() {
			return name;
		}

		@Override /* Path */
		public Class<? extends HttpPartParser> parser() {
			return parser;
		}

		@Override /* Path */
		public Schema schema() {
			return schema;
		}

		@Override /* Path */
		public Class<? extends HttpPartSerializer> serializer() {
			return serializer;
		}

		@Override /* Path */
		public String value() {
			return value;
		}
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Appliers
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Applies targeted {@link Path} annotations to a {@link org.apache.juneau.BeanContext.Builder}.
	 */
	public static class Applier extends AnnotationApplier<Path,BeanContext.Builder> {

		/**
		 * Constructor.
		 *
		 * @param vr The resolver for resolving values in annotations.
		 */
		public Applier(VarResolverSession vr) {
			super(Path.class, BeanContext.Builder.class, vr);
		}

		@Override
		public void apply(AnnotationInfo<Path> ai, BeanContext.Builder b) {
			Path a = ai.getAnnotation();

			if (isEmpty(a.on()) && isEmpty(a.onClass()))
				return;

			b.annotations(a);
		}
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Other
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * A collection of {@link Path @Path annotations}.
	 */
	@Documented
	@Target({METHOD,TYPE})
	@Retention(RUNTIME)
	@Inherited
	public static @interface Array {

		/**
		 * The child annotations.
		 */
		Path[] value();
	}
}