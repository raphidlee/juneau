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
package org.apache.juneau.httppart;

import java.lang.reflect.*;

import org.apache.juneau.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.parser.*;

/**
 * Session object that lives for the duration of a single use of {@link SimplePartParser}.
 *
 * <p>
 * This class is NOT thread safe.
 * It is typically discarded after one-time use although it can be reused within the same thread.
 */
public class SimplePartParserSession extends BaseHttpPartParserSession {

	@Override /* HttpPartParserSession */
	public <T> T parse(HttpPartType partType, HttpPartSchema schema, String in, ClassMeta<T> toType) throws ParseException, SchemaValidationException {
		return ClassUtils.fromString(toType.getInnerClass(), in);
	}

	@Override /* HttpPartParserSession */
	public <T> T parse(HttpPartType partType, HttpPartSchema schema, String in, Class<T> toType) throws ParseException, SchemaValidationException {
		return ClassUtils.fromString(toType, in);
	}

	@SuppressWarnings("unchecked")
	@Override /* HttpPartParserSession */
	public <T> T parse(HttpPartType partType, HttpPartSchema schema, String in, Type toType, Type...toTypeArgs) throws ParseException, SchemaValidationException {
		if (toType instanceof Class)
			return (T)ClassUtils.fromString((Class<?>)toType, in);
		return null;
	}
}
