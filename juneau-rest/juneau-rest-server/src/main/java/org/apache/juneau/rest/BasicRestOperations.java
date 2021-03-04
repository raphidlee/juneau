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
package org.apache.juneau.rest;

import static org.apache.juneau.http.HttpMethod.*;

import java.util.*;

import org.apache.juneau.annotation.*;
import org.apache.juneau.dto.swagger.Swagger;
import org.apache.juneau.dto.swagger.ui.*;
import org.apache.juneau.html.annotation.*;
import org.apache.juneau.http.*;
import org.apache.juneau.http.annotation.*;
import org.apache.juneau.jsonschema.annotation.*;
import org.apache.juneau.rest.annotation.*;

/**
 * Basic REST operation methods.
 */
@HtmlDocConfig(
	// Basic page navigation links.
	navlinks={
		"up: request:/..",
		"api: servlet:/api",
		"stats: servlet:/stats"
	}
)
@JsonSchemaConfig(
	// Add descriptions to the following types when not specified:
	addDescriptionsTo="bean,collection,array,map,enum",
	// Add example to the following types:
	addExamplesTo="bean,collection,array,map",
	// Don't generate schema information on the Swagger bean itself or HTML beans.
	ignoreTypes="Swagger,org.apache.juneau.dto.html5.*",
	// Use $ref references for bean definitions to reduce duplication in Swagger.
	useBeanDefs="true"
)
public interface BasicRestOperations {

	/**
	 * [GET /api] - Show resource options.
	 *
	 * @param req The HTTP request.
	 * @return A bean containing the contents for the OPTIONS page.
	 */
	@RestGet(
		path="/api/*",
		summary="Swagger documentation",
		description="Swagger documentation for this resource."
	)
	@HtmlDocConfig(
		// Should override config annotations defined on class.
		rank=10,
		// Override the nav links for the swagger page.
		navlinks={
			"back: servlet:/",
			"json: servlet:/?Accept=text/json&plainText=true"
		},
		// Never show aside contents of page inherited from class.
		aside="NONE"
	)
	@BeanConfig(
		// POJO swaps to apply to all serializers/parsers on this method.
		swaps={
			// Use the SwaggerUI swap when rendering Swagger beans.
			// This is a per-media-type swap that only applies to text/html requests.
			SwaggerUI.class
		}
	)
	public Swagger getSwagger(RestRequest req);

	/**
	 * [GET /htdocs/*] - Retrieve static file.
	 *
	 * @param path The path to retrieve.
	 * @param locale The locale of the HTTP request.
	 * @return An HTTP resource representing the static file.
	 */
	@RestGet(
		path="/htdocs/*",
		summary="Static files",
		description="Static file retrieval."
	)
	public HttpResource getHtdoc(@Path String path, Locale locale);

	/**
	 * [GET favicon.ico] - Retrieve favorites icon image.
	 *
	 * @return A bean containing the contents for the OPTIONS page.
	 */
	@RestGet(
		path="favicon.ico",
		summary="Favorites icon.",
		description="Favorites icon."
	)
	public HttpResource getFavIcon();

	/**
	 * [* /error] - Error occurred.
	 *
	 * <p>
	 * Servlet chains will often automatically redirect to <js>"/error"</js> when any sort of error condition occurs
	 * (such as failed authentication) and will set appropriate response parameters (such as an <c>WWW-Authenticate</c>
	 * response header).
	 *
	 * <p>
	 * These responses should be left as-is without any additional processing.
	 */
	@RestOp(
		method=ANY,
		path="/error",
		summary="Error occurred",
		description="An error occurred during handling of the request."
	)
	public void error();

	/**
	 * [GET /stats] - Timing statistics.
	 *
	 * <p>
	 * Timing statistics for method invocations on this resource.
	 *
	 * @param req The HTTP request.
	 * @return A collection of timing statistics for each annotated method on this resource.
	 */
	@RestGet(
		path="/stats",
		summary="Timing statistics",
		description="Timing statistics for method invocations on this resource."
	)
	@HtmlDocConfig(
		// Should override config annotations defined on class.
		rank=10,
		// Override the nav links for the swagger page.
		navlinks={
			"back: servlet:/",
			"json: servlet:/stats?Accept=text/json&plainText=true"
		},
		// Never show aside contents of page inherited from class.
		aside="NONE"
	)
	public RestContextStats getStats(RestRequest req);

}