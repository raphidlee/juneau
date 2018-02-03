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

/**
 * Represents a simple child REST resource / path mapping.
 * 
 * <h5 class='section'>Example:</h5>
 * <p class='bcode'>
 * 	<jc>// Parent resource.</jc>
 * 	<jk>public class</jk> MyResource {
 * 		<jk>public</jk> MyResource(RestContextBuilder builder) <jk>throws</jk> Exception {
 * 			
 * 			<jc>// Register a child resource.</jc>
 * 			builder.children(<jk>new</jk> RestChild(<js>"/child"</js>, <jk>new</jk> MyChildResource());
 * 
 * 			<jc>// The above is equivalent to...</jc>
 * 			builder.child(<js>"/child"</js>, <jk>new</jk> MyChildResource());
 * 		}
 * 	}
 * </p>
 * 
 * <h5 class='section'>See Also:</h5>
 * <ul>
 * 	<li class='link'><a class="doclink" href="../../../../overview-summary.html#juneau-rest-server.Children">Overview &gt; juneau-rest-server &gt; Children</a>
 * </ul>
 */
public class RestChild {

	final String path;
	final Object resource;
	
	/**
	 * Constructor.
	 * 
	 * @param path The child resource path relative to the parent resource URI.
	 * @param resource 
	 * 	The child resource.
	 * 	<br>Can either be a Class (which will be instantiated using the registered {@link RestResourceResolver}) 
	 * 	or an already-instantiated object.
	 */
	public RestChild(String path, Object resource) {
		this.path = path;
		this.resource = resource;
	}
}
