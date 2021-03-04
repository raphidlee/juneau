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
package org.apache.juneau.rest.args;

import org.apache.juneau.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.httppart.bean.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.rest.*;
import org.apache.juneau.rest.annotation.*;

/**
 * Resolves method parameters annotated with {@link Request} on {@link RestOp}-annotated Java methods.
 *
 * <p>
 * The parameter value is resolved using <c><jv>call</jv>.{@link RestCall#getRestRequest() getRestRequest}().{@link RestRequest#getRequest(RequestBeanMeta) getRequest}(<jv>requestBeanMeta</jv>)</c>
 * with a {@link RequestBeanMeta meta} derived from the {@link Request} annotation and context configuration.
 */
public class RequestBeanArg implements RestOperationArg {
	private final RequestBeanMeta meta;

	/**
	 * Static creator.
	 *
	 * @param paramInfo The Java method parameter being resolved.
	 * @param cp The configuration properties of the {@link RestContext}.
	 * @return A new {@link RequestBeanArg}, or <jk>null</jk> if the parameter is not annotated with {@link Request}.
	 */
	public static RequestBeanArg create(ParamInfo paramInfo, ContextProperties cp) {
		if (paramInfo.hasAnnotation(Request.class))
			return new RequestBeanArg(paramInfo, cp);
		return null;
	}

	/**
	 * Constructor.
	 *
	 * @param paramInfo The Java method parameter being resolved.
	 * @param cp The configuration properties of the {@link RestContext}.
	 */
	protected RequestBeanArg(ParamInfo paramInfo, ContextProperties cp) {
		this.meta = RequestBeanMeta.create(paramInfo, cp);
	}

	@Override /* RestOperationArg */
	public Object resolve(RestCall call) throws Exception {
		return call.getRestRequest().getRequest(meta);
	}
}