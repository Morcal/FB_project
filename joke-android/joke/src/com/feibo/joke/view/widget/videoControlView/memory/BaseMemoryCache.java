/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.feibo.joke.view.widget.videoControlView.memory;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class BaseMemoryCache<T>{

	/** Stores not strong references to objects */
	private final Map<String, Reference<T>> softMap = Collections.synchronizedMap(new HashMap<String, Reference<T>>());

	public T get(String key) {
		T result = null;
		Reference<T> reference = softMap.get(key);
		if (reference != null) {
			result = reference.get();
		}
		return result;
	}

	public boolean put(String key, T value) {
		softMap.put(key, createReference(value));
		return true;
	}

	public T remove(String key) {
		Reference<T> bmpRef = softMap.remove(key);
		return bmpRef == null ? null : bmpRef.get();
	}

	public Collection<String> keys() {
		synchronized (softMap) {
			return new HashSet<String>(softMap.keySet());
		}
	}

	public void clear() {
		softMap.clear();
	}

	/** Creates {@linkplain Reference not strong} reference of value */
	protected abstract Reference<T> createReference(T value);
}
