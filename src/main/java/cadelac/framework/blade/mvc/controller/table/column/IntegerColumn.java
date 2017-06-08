package cadelac.framework.blade.mvc.controller.table.column;

public class IntegerColumn<V> extends GTableColumn<Integer,V> {
    public IntegerColumn(final String title_) {super(title_);}
    public IntegerColumn(final String title_, int columnIndex_) {super(title_, columnIndex_);}
    @Override 
    public Class<Integer> getColumnClass() {return Integer.class;}
}