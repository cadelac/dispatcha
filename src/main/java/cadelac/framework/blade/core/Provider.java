package cadelac.framework.blade.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Provider<K,V> {
	
	public Provider(final Map<K,V> store_) {
		_store = store_;
	}
	
	public Provider() {
		_store = new HashMap<K,V>();
	}
	
	public V getElement(final K key_) {
		return _store.get(key_);
	}
	
	public V remove(final K key_) {
		return _store.remove(key_);
	}
	
	public List<V> getAllElements() {
		return new ArrayList<V>(_store.values());
	}
	
	public List<V> getFilteredElements(final Predicate<V> filter_) {
		final List<V> elements = new ArrayList<V>();
		for (V element : _store.values()) {
			if (filter_.test(element))
				elements.add(element);
		}
		return elements;
	}
	
	public Map<K,V> getRows() {
		return _store;
	}
	
	public void setRows(final Map<K,V> store_) {
		_store = store_;
	}
	/**
	 * Adds element_ if key_ is not already in the map
	 * @param key_
	 * @param element_
	 */
	public void safeAdd(final K key_, final V element_) {
		if (!_store.containsKey(key_)) {
			_store.put(key_, element_);
		}
	}
	
	/**
	 * Adds element_ in the map. If key_ is already in the map, old element is replaced.
	 * @param key_
	 * @param element_
	 */
	public void unsafeAdd(final K key_, final V element_) {
		_store.put(key_, element_);
	}
	
	public int getSize() {
		return _store.size();
	}
	
	private Map<K,V> _store;
}
