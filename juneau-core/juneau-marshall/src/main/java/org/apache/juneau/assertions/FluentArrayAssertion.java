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
package org.apache.juneau.assertions;

import static org.apache.juneau.internal.ObjectUtils.*;

import java.lang.reflect.*;

import org.apache.juneau.internal.*;
import org.apache.juneau.marshall.*;

/**
 * Used for fluent assertion calls against array objects.
 *
 * @param <R> The return type.
 */
@FluentSetters(returns="FluentArrayAssertion<R>")
public class FluentArrayAssertion<R> extends FluentObjectAssertion<R> {

	private Object value;

	/**
	 * Constructor.
	 *
	 * @param contents The byte array being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentArrayAssertion(Object contents, R returns) {
		this(null, contents, returns);
	}

	/**
	 * Constructor.
	 *
	 * @param creator The assertion that created this assertion.
	 * @param contents The byte array being tested.
	 * @param returns The object to return after the test.
	 */
	public FluentArrayAssertion(Assertion creator, Object contents, R returns) {
		super(creator, contents, returns);
		this.value = contents;
	}

	/**
	 * Asserts that the collection exists and is empty.
	 *
	 * @return The object to return after the test.
	 * @throws AssertionError If assertion failed.
	 */
	public R isEmpty() throws AssertionError {
		exists();
		if (Array.getLength(value) != 0)
			throw error("Array was not empty.");
		return returns();
	}

	/**
	 * Asserts that the collection exists and is not empty.
	 *
	 * @return The object to return after the test.
	 * @throws AssertionError If assertion failed.
	 */
	public R isNotEmpty() throws AssertionError {
		exists();
		if (Array.getLength(value) == 0)
			throw error("Array was empty.");
		return returns();
	}

	/**
	 * Asserts that the collection exists and is the specified size.
	 *
	 * @param size The expected size.
	 * @return The object to return after the test.
	 * @throws AssertionError If assertion failed.
	 */
	public R isSize(int size) throws AssertionError {
		exists();
		if (Array.getLength(value) != size)
			throw error("Array did not have the expected size.  Expected={0}, Actual={1}.", size, Array.getLength(value));
		return returns();
	}

	/**
	 * Asserts that the array contains the expected value.
	 *
	 * @param value The value to check for.
	 * @return The object to return after the test.
	 * @throws AssertionError If assertion failed.
	 */
	public R contains(Object value) throws AssertionError {
		exists();
		for (int i = 0; i < Array.getLength(this.value); i++)
			if (eq(Array.get(this.value, i), value))
				return returns();
		throw error("Array did not contain expected value.\nContents: {0}\nExpected:{1}", SimpleJson.DEFAULT.toString(this.value), value);
	}

	/**
	 * Asserts that the array does not contain the expected value.
	 *
	 * @param value The value to check for.
	 * @return The object to return after the test.
	 * @throws AssertionError If assertion failed.
	 */
	public R doesNotContain(Object value) throws AssertionError {
		exists();
		for (int i = 0; i < Array.getLength(this.value); i++)
			if (eq(Array.get(this.value, i), value))
				throw error("Array contain unexpected value.\nContents: {0}\nUnexpected:{1}", SimpleJson.DEFAULT.toString(this.value), value);
		return returns();
	}

	/**
	 * Returns an object assertion on the item specified at the specified index.
	 *
	 * <p>
	 * If the array is <jk>null</jk> or the index is out-of-bounds, the returned assertion is a null assertion
	 * (meaning {@link FluentObjectAssertion#exists()} returns <jk>false</jk>).
	 *
	 * @param index The index of the item to retrieve from the array.
	 * @return A new assertion.
	 */
	public FluentObjectAssertion<R> item(int index) {
		return new FluentObjectAssertion<>(this, getItem(index), returns());
	}

	private Object getItem(int index) {
		if (value != null && Array.getLength(value) > index)
			return Array.get(value, index);
		return null;
	}

	// <FluentSetters>

	@Override /* GENERATED - Assertion */
	public FluentArrayAssertion<R> msg(String msg, Object...args) {
		super.msg(msg, args);
		return this;
	}

	@Override /* GENERATED - Assertion */
	public FluentArrayAssertion<R> stderr() {
		super.stderr();
		return this;
	}

	@Override /* GENERATED - Assertion */
	public FluentArrayAssertion<R> stdout() {
		super.stdout();
		return this;
	}

	// </FluentSetters>
}