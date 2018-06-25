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
package org.apache.juneau.httppart.oapi;

import org.apache.juneau.*;
import org.apache.juneau.httppart.*;
import org.apache.juneau.httppart.uon.*;
import org.apache.juneau.internal.*;

/**
 * Serializes POJOs to values suitable for transmission as HTTP headers, query/form-data parameters, and path variables.
 * 
 * <p>
 * This serializer uses UON notation for all parts by default.  This allows for arbitrary POJOs to be losslessly
 * serialized as any of the specified HTTP types.
 */
public class OapiPartSerializer extends UonPartSerializer {

	//-------------------------------------------------------------------------------------------------------------------
	// Configurable properties
	//-------------------------------------------------------------------------------------------------------------------

	private static final String PREFIX = "OapiPartSerializer.";

	/**
	 * Configuration property:  OpenAPI schema description.
	 * 
	 * <h5 class='section'>Property:</h5>
	 * <ul>
	 * 	<li><b>Name:</b>  <js>"OapiPartSerializer.schema"</js>
	 * 	<li><b>Data type:</b>  <code>HttpPartSchema</code>
	 * 	<li><b>Default:</b>  <jk>false</jk>
	 * 	<li><b>Session-overridable:</b>  <jk>false</jk>
	 * 	<li><b>Methods:</b> 
	 * 		<ul>
	 * 			<li class='jm'>{@link OapiPartSerializerBuilder#schema(HttpPartSchema)}
	 * 		</ul>
	 * </ul>
	 * 
	 * <h5 class='section'>Description:</h5>
	 * <p>
	 * Defines the OpenAPI schema for this part serializer.
	 */
	public static final String OAPI_schema = PREFIX + "schema.o";


	//-------------------------------------------------------------------------------------------------------------------
	// Predefined instances
	//-------------------------------------------------------------------------------------------------------------------

	/** Reusable instance of {@link OapiPartSerializer}, all default settings. */
	public static final OapiPartSerializer DEFAULT = new OapiPartSerializer(PropertyStore.DEFAULT);


	//-------------------------------------------------------------------------------------------------------------------
	// Instance
	//-------------------------------------------------------------------------------------------------------------------

	final HttpPartSchema schema;

	/**
	 * Constructor.
	 * 
	 * @param ps
	 * 	The property store containing all the settings for this object.
	 */
	public OapiPartSerializer(PropertyStore ps) {
		super(
			ps.builder()
				.set(UON_encoding, false)
				.build() 
		);
		this.schema = getProperty(OAPI_schema, HttpPartSchema.class, HttpPartSchema.DEFAULT);
	}

	@Override /* Context */
	public UonPartSerializerBuilder builder() {
		return new UonPartSerializerBuilder(getPropertyStore());
	}

	/**
	 * Instantiates a new clean-slate {@link UonPartSerializerBuilder} object.
	 * 
	 * <p>
	 * Note that this method creates a builder initialized to all default settings, whereas {@link #builder()} copies 
	 * the settings of the object called on.
	 * 
	 * @return A new {@link UonPartSerializerBuilder} object.
	 */
	public static UonPartSerializerBuilder create() {
		return new UonPartSerializerBuilder();
	}

	//--------------------------------------------------------------------------------
	// Entry point methods
	//--------------------------------------------------------------------------------

	@Override /* PartSerializer */
	public String serialize(HttpPartType type, HttpPartSchema schema, Object value) {
		schema = ObjectUtils.firstNonNull(schema, this.schema, HttpPartSchema.DEFAULT);
		return super.serialize(type, schema, value);
	}
}
