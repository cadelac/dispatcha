package cadelac.framework.blade.mvc.controller.table.column;

import javax.swing.table.AbstractTableModel;

public class GTableColumn<C,V> {
      
    public GTableColumn(final String title_, int tag_, int columnIndex_, int width_) {
        _title = title_;
        _columnIndex = columnIndex_;
        _width = width_;
        _tag = tag_;
        _comparator = null;
    }
    public GTableColumn(final String title_, int tag_) {
    	this(title_, tag_, -1, -1);
    }
    public GTableColumn(final String title_) {
    	this(title_, -1, -1, -1);        
    }
    
    public String getTitle() {
    	return _title;
    }
    public int getTag() {
    	return _tag;
    }
    
    public ColumnComparator<V> getComparator() {
    	return _comparator;
    }
    public void setComparator(final ColumnComparator<V> comparator_) {
    	_comparator = comparator_;
    }

    public int getColumnIndex() {
    	return _columnIndex;
    }
    public void setColumnIndex(int columnIndex_) {
    	_columnIndex = columnIndex_;
    }

    public int getWidth() {
    	return _width;
    }
    public void setWidth(int width_) {
    	_width = width_;
    }
    
    public Class<C> getColumnClass() {
    	return null;
    }
    
    public C getValue(final V model_) {
    	return null;
    }
    public void setValue(final AbstractTableModel state_, final V model_, final C value_) {
    }

    public C getDelta(final V model_) {
    	return null;
    }
    
    private final String  _title;
    private int _columnIndex;
    private int _width;
    private final int _tag;
    private ColumnComparator<V> _comparator;
}