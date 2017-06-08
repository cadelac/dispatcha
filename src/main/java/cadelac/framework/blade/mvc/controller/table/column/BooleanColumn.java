package cadelac.framework.blade.mvc.controller.table.column;

public class BooleanColumn<V> extends GTableColumn<Boolean,V> {
    public BooleanColumn(final String title_) {super(title_);}
    public BooleanColumn(final String title_, int columnIndex_) {super(title_, columnIndex_);}
    @Override 
    public Class<Boolean> getColumnClass() {return Boolean.class;}
}
