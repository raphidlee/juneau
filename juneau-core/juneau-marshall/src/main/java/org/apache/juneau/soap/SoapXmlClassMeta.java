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
package org.apache.juneau.soap;

import org.apache.juneau.*;
import org.apache.juneau.soap.annotation.*;

/**
 * Metadata on classes specific to the SOAP/XML serializers and parsers pulled from the {@link SoapXml @SoapXml} annotation on
 * the class.
 */
public class SoapXmlClassMeta extends ExtendedClassMeta {

	private final SoapXml soapXml;

	/**
	 * Constructor.
	 *
	 * @param cm The class that this annotation is defined on.
	 * @param mp SOAP/XML metadata provider (for finding information about other artifacts).
	 */
	public SoapXmlClassMeta(ClassMeta<?> cm, SoapXmlMetaProvider mp) {
		super(cm);
		this.soapXml = cm.getAnnotation(SoapXml.class);
	}

	/**
	 * Returns the {@link SoapXml @SoapXml} annotation defined on the class.
	 *
	 * @return The value of the annotation, or <jk>null</jk> if not specified.
	 */
	protected SoapXml getAnnotation() {
		return soapXml;
	}
}
