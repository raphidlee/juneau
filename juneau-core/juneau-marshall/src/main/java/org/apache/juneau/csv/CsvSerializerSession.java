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
package org.apache.juneau.csv;

import static org.apache.juneau.internal.CollectionUtils.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;
import org.apache.juneau.*;
import org.apache.juneau.http.header.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.serializer.*;
import org.apache.juneau.svl.*;

/**
 * Session object that lives for the duration of a single use of {@link CsvSerializer}.
 *
 * <ul class='notes'>
 * 	<li class='warn'>This class is not thread safe and is typically discarded after one use.
 * </ul>
 *
 * <ul class='seealso'>
 * 	<li class='extlink'>{@source}
 * </ul>
 */
public final class CsvSerializerSession extends WriterSerializerSession {

	//-----------------------------------------------------------------------------------------------------------------
	// Static
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a new builder for this object.
	 *
	 * @param ctx The context creating this session.
	 * @return A new builder.
	 */
	public static Builder create(CsvSerializer ctx) {
		return new Builder(ctx);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Builder
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Builder class.
	 */
	@FluentSetters
	public static class Builder extends WriterSerializerSession.Builder {

		CsvSerializer ctx;

		/**
		 * Constructor
		 *
		 * @param ctx The context creating this session.
		 */
		protected Builder(CsvSerializer ctx) {
			super(ctx);
			this.ctx = ctx;
		}

		@Override
		public CsvSerializerSession build() {
			return new CsvSerializerSession(this);
		}

		// <FluentSetters>

		@Override /* GENERATED - org.apache.juneau.ContextSession.Builder */
		public <T> Builder apply(Class<T> type, Consumer<T> apply) {
			super.apply(type, apply);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.ContextSession.Builder */
		public Builder debug(Boolean value) {
			super.debug(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.ContextSession.Builder */
		public Builder properties(Map<String,Object> value) {
			super.properties(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.ContextSession.Builder */
		public Builder property(String key, Object value) {
			super.property(key, value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.ContextSession.Builder */
		public Builder unmodifiable() {
			super.unmodifiable();
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.BeanSession.Builder */
		public Builder locale(Locale value) {
			super.locale(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.BeanSession.Builder */
		public Builder localeDefault(Locale value) {
			super.localeDefault(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.BeanSession.Builder */
		public Builder mediaType(MediaType value) {
			super.mediaType(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.BeanSession.Builder */
		public Builder mediaTypeDefault(MediaType value) {
			super.mediaTypeDefault(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.BeanSession.Builder */
		public Builder timeZone(TimeZone value) {
			super.timeZone(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.BeanSession.Builder */
		public Builder timeZoneDefault(TimeZone value) {
			super.timeZoneDefault(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.serializer.SerializerSession.Builder */
		public Builder javaMethod(Method value) {
			super.javaMethod(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.serializer.SerializerSession.Builder */
		public Builder resolver(VarResolverSession value) {
			super.resolver(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.serializer.SerializerSession.Builder */
		public Builder schema(HttpPartSchema value) {
			super.schema(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.serializer.SerializerSession.Builder */
		public Builder schemaDefault(HttpPartSchema value) {
			super.schemaDefault(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.serializer.SerializerSession.Builder */
		public Builder uriContext(UriContext value) {
			super.uriContext(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.serializer.WriterSerializerSession.Builder */
		public Builder fileCharset(Charset value) {
			super.fileCharset(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.serializer.WriterSerializerSession.Builder */
		public Builder streamCharset(Charset value) {
			super.streamCharset(value);
			return this;
		}

		@Override /* GENERATED - org.apache.juneau.serializer.WriterSerializerSession.Builder */
		public Builder useWhitespace(Boolean value) {
			super.useWhitespace(value);
			return this;
		}

		// </FluentSetters>
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Instance
	//-----------------------------------------------------------------------------------------------------------------

	/**
	 * Constructor.
	 *
	 * @param builder The builder for this object.
	 */
	protected CsvSerializerSession(Builder builder) {
		super(builder);
	}

	@SuppressWarnings("rawtypes")
	@Override /* SerializerSession */
	protected final void doSerialize(SerializerPipe pipe, Object o) throws IOException, SerializeException {

		try (CsvWriter w = getCsvWriter(pipe)) {
			ClassMeta<?> cm = getClassMetaForObject(o);
			Collection<?> l = null;
			if (cm.isArray()) {
				l = alist((Object[])o);
			} else if (cm.isCollection()) {
				l = (Collection<?>)o;
			} else {
				l = Collections.singleton(o);
			}

			// TODO - Doesn't support DynaBeans.
			if (l.size() > 0) {
				ClassMeta<?> entryType = getClassMetaForObject(l.iterator().next());
				if (entryType.isBean()) {
					BeanMeta<?> bm = entryType.getBeanMeta();
					Flag addComma = Flag.create();
					bm.forEachProperty(x -> x.canRead(), x -> {
						addComma.ifSet(() -> w.w(',')).set();
						w.writeEntry(x.getName());
					});
					w.append('\n');
					l.forEach(x -> {
						Flag addComma2 = Flag.create();
						BeanMap<?> bean = toBeanMap(x);
						bm.forEachProperty(y -> y.canRead(), y -> {
							addComma2.ifSet(() -> w.w(',')).set();
							w.writeEntry(y.get(bean, y.getName()));
						});
						w.w('\n');
					});
				} else if (entryType.isMap()) {
					Flag addComma = Flag.create();
					Map first = (Map)l.iterator().next();
					first.keySet().forEach(x -> {
						addComma.ifSet(() -> w.w(',')).set();
						w.writeEntry(x);
					});
					w.append('\n');
					l.stream().forEach(x -> {
						Flag addComma2 = Flag.create();
						Map map = (Map)x;
						map.values().forEach(y -> {
							addComma2.ifSet(() -> w.w(',')).set();
							w.writeEntry(y);
						});
						w.w('\n');
					});
				} else {
					w.writeEntry("value");
					w.append('\n');
					l.stream().forEach(x -> {
						w.writeEntry(x);
						w.w('\n');
					});
				}
			}
		}
	}

	final CsvWriter getCsvWriter(SerializerPipe out) {
		Object output = out.getRawOutput();
		if (output instanceof CsvWriter)
			return (CsvWriter)output;
		CsvWriter w = new CsvWriter(out.getWriter(), isUseWhitespace(), getMaxIndent(), getQuoteChar(), isTrimStrings(), getUriResolver());
		out.setWriter(w);
		return w;
	}
}
