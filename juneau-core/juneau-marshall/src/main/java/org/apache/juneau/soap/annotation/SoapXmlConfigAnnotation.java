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
package org.apache.juneau.soap.annotation;

import org.apache.juneau.*;
import org.apache.juneau.reflect.*;
import org.apache.juneau.soap.*;
import org.apache.juneau.svl.*;

/**
 * Utility classes and methods for the {@link SoapXmlConfig @SoapXmlConfig} annotation.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jm.SoapXmlDetails}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
public class SoapXmlConfigAnnotation {

	/**
	 * Applies {@link SoapXmlConfig} annotations to a {@link org.apache.juneau.soap.SoapXmlSerializer.Builder}.
	 */
	public static class SerializerApply extends AnnotationApplier<SoapXmlConfig,SoapXmlSerializer.Builder> {

		/**
		 * Constructor.
		 *
		 * @param vr The resolver for resolving values in annotations.
		 */
		public SerializerApply(VarResolverSession vr) {
			super(SoapXmlConfig.class, SoapXmlSerializer.Builder.class, vr);
		}

		@Override
		public void apply(AnnotationInfo<SoapXmlConfig> ai, SoapXmlSerializer.Builder b) {
			SoapXmlConfig a = ai.getAnnotation();

			string(a.soapAction()).ifPresent(x -> b.soapAction(x));
		}
	}
}