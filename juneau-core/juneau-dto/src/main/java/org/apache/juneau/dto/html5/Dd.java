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
package org.apache.juneau.dto.html5;

import org.apache.juneau.annotation.*;

/**
 * DTO for an HTML {@doc ext.HTML5.grouping-content#the-dd-element <dd>}
 * element.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jd.Html5}
 * </ul>
 */
@Bean(typeName="dd")
public class Dd extends HtmlElementMixed {

	/**
	 * Creates an empty {@link Dd} element.
	 */
	public Dd() {}

	/**
	 * Creates a {@link Dd} element with the specified child nodes.
	 *
	 * @param children The child nodes.
	 */
	public Dd(Object...children) {
		children(children);
	}

	//-----------------------------------------------------------------------------------------------------------------
	// Overridden methods
	//-----------------------------------------------------------------------------------------------------------------

	@Override /* HtmlElement */
	public final Dd _class(String _class) {
		super._class(_class);
		return this;
	}

	@Override /* HtmlElement */
	public final Dd id(String id) {
		super.id(id);
		return this;
	}

	@Override /* HtmlElement */
	public final Dd style(String style) {
		super.style(style);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Dd children(Object...children) {
		super.children(children);
		return this;
	}

	@Override /* HtmlElementMixed */
	public Dd child(Object child) {
		super.child(child);
		return this;
	}
}
