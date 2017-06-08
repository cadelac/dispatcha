package cadelac.framework.blade.mvc.controller.table.column;

public class DoubleColumn<V> extends GTableColumn<Double,V> {
	public DoubleColumn(final String title_) {super(title_);}
    public DoubleColumn(final String title_, int columnIndex_) {super(title_, columnIndex_);}
    @Override
    public Class<Double> getColumnClass() {return Double.class;}
}
