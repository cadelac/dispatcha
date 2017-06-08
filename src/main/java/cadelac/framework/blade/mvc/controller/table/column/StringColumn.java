package cadelac.framework.blade.mvc.controller.table.column;

public class StringColumn<V> extends GTableColumn<String,V>  {
    public StringColumn(final String title_) {super(title_);}
    public StringColumn(final String title_, int columnIndex_) {super(title_, columnIndex_);}
    @Override 
    public Class<String> getColumnClass() {return String.class;}
}
