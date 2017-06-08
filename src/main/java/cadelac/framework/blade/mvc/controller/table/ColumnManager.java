package cadelac.framework.blade.mvc.controller.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cadelac.framework.blade.mvc.controller.table.column.GTableColumn;

public class ColumnManager<E> {
	public ColumnManager() {
		_columns = new ArrayList<GTableColumn<?,E>>();
		_map = new HashMap<Integer,GTableColumn<?,E>>();
	}
	public GTableColumn<?,E> getColumn(final int index_) {
    	return (index_<0 || getColumnCount()-1<index_) ? null : _columns.get(index_);
	}
	public GTableColumn<?,E> getColumnByTag(final int tag_) {
    	return _map.get(tag_);
	}

	public int getColumnCount() {
		return _columns.size();
	}
	public String getColumnTitle(final int index_) {
		final GTableColumn<?,E> column = getColumn(index_);
		if (column==null)
			return "";
		return column.getTitle();
	}
    public Class<?> getColumnClass(final int index_) {
		final GTableColumn<?,E> column = getColumn(index_);
		if (column==null)
			return null;
		return column.getColumnClass();
    }
	public void addColumn(final GTableColumn<?,E> column_) {
		column_.setColumnIndex(_columns.size());
		_columns.add(column_);
		final int tag = column_.getTag();
		if (tag!=-1) {
			_map.put(tag, column_);
		}
	}
	/**
	 * access columns by index
	 */
	private final List<GTableColumn<?,E>> _columns;
	/**
	 * access columns by tag
	 */
	private final Map<Integer,GTableColumn<?,E>> _map;
}
