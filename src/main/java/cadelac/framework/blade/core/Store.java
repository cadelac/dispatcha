package cadelac.framework.blade.core;

import java.util.HashMap;
import java.util.Map;

public interface Store {
	
	<T> T getValue(String key_);
	<T> void setValue(String key_, T value_);
	
	
	
	static Store create() {
		
		return new Store() {

			@SuppressWarnings("unchecked")
			@Override
			public <T> T getValue(String key_) {
				return (T) _map.get(key_);
			}

			@Override
			public <T> void setValue(String key_, T value_) {
				_map.put(key_, value_);
			}
			
			Map<String,Object> _map = new HashMap<String,Object>();
		};
	}

}
