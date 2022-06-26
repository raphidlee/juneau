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
package org.apache.juneau.marshall;

import java.lang.reflect.*;

import org.apache.juneau.*;
import org.apache.juneau.parser.*;
import org.apache.juneau.serializer.*;

/**
 * A subclass of {@link Marshall} for character-based serializers and parsers.
 *
 * <ul class='seealso'>
 * 	<li class='link'>{@doc jm.Marshalls}
 * 	<li class='extlink'>{@source}
 * </ul>
 */
public class CharMarshall extends Marshall {

	private final ReaderParser p;
	private final WriterSerializer s;

	/**
	 * Constructor.
	 *
	 * @param s
	 * 	The serializer to use for serializing output.
	 * 	<br>Must not be <jk>null</jk>.
	 * @param p
	 * 	The parser to use for parsing input.
	 * 	<br>Must not be <jk>null</jk>.
	 */
	public CharMarshall(WriterSerializer s, ReaderParser p) {
		super(s, p);
		this.s = s;
		this.p = p;
	}

	/**
	 * Same as {@link #read(Object,Class)} but reads from a string and thus doesn't throw an <c>IOException</c>.
	 *
	 * <p>
	 * This is the preferred parse method for simple types since you don't need to cast the results.
	 *
	 * <h5 class='section'>Examples:</h5>
	 * <p class='bjava'>
	 * 	Marshall <jv>marshall</jv> = Json.<jsf>DEFAULT</jsf>;
	 *
	 * 	<jc>// Parse into a string.</jc>
	 * 	String <jv>string</jv> = <jv>marshall</jv>.read(<jv>json</jv>, String.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a bean.</jc>
	 * 	MyBean <jv>bean</jv> = <jv>marshall</jv>.read(<jv>json</jv>, MyBean.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a bean array.</jc>
	 * 	MyBean[] <jv>beanArray</jv> = <jv>marshall</jv>.read(<jv>json</jv>, MyBean[].<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a linked-list of objects.</jc>
	 * 	List <jv>list</jv> = <jv>marshall</jv>.read(<jv>json</jv>, LinkedList.<jk>class</jk>);
	 *
	 * 	<jc>// Parse into a map of object keys/values.</jc>
	 * 	Map <jv>map</jv> = <jv>marshall</jv>.read(<jv>json</jv>, TreeMap.<jk>class</jk>);
	 * </p>
	 *
	 * @param <T> The class type of the object being created.
	 * @param input The input.
	 * @param type The object type to create.
	 * @return The parsed object.
	 * @throws ParseException Malformed input encountered.
	 */
	public final <T> T read(String input, Class<T> type) throws ParseException {
		return p.parse(input, type);
	}

	/**
	 * Same as {@link #read(Object,Type,Type...)} but reads from a string and thus doesn't throw an <c>IOException</c>.
	 *
	 * @param <T> The class type of the object to create.
	 * @param input The input.
	 * @param type
	 * 	The object type to create.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * @param args
	 * 	The type arguments of the class if it's a collection or map.
	 * 	<br>Can be any of the following: {@link ClassMeta}, {@link Class}, {@link ParameterizedType}, {@link GenericArrayType}
	 * 	<br>Ignored if the main type is not a map or collection.
	 * @return The parsed object.
	 * @throws ParseException Malformed input encountered.
	 * @see BeanSession#getClassMeta(Type,Type...) for argument syntax for maps and collections.
	 */
	public final <T> T read(String input, Type type, Type...args) throws ParseException {
		return p.parse(input, type, args);
	}

	/**
	 * Serializes a POJO directly to a <c>String</c>.
	 *
	 * @param object The object to serialize.
	 * @return
	 * 	The serialized object.
	 * @throws SerializeException If a problem occurred trying to convert the output.
	 */
	public final String write(Object object) throws SerializeException {
		return s.serializeToString(object);
	}
}
