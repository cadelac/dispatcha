package cadelac.framework.blade.mvc.controller.table;

import java.awt.event.MouseListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

import cadelac.framework.blade.mvc.controller.table.column.GTableColumn;
import cadelac.lib.primitive.KeyedElement;
import cadelac.lib.primitive.Provider;
import cadelac.lib.primitive.concept.Identified;




public class TableController <K,E,A extends KeyedElement<K,E>> extends AbstractTableModel implements Identified {

	public TableController(final String id_, final Provider<K,E> provider_, final A adapter_) {
		_adapter = adapter_;
		_id = id_;
		_columnManager = new ColumnManager<E>();
		_rowManager = new RowManager<K,E,A>(adapter_);
		_rowManager.setProvider(provider_);
		_rowManager.selectAllRows();
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public int getColumnCount() {
		return _columnManager.getColumnCount();
	}

	@Override
    public String getColumnName(int column_) {
		return _columnManager.getColumnTitle(column_);
	}
	
	@Override
	public Class<?> getColumnClass(int column_) {
		return _columnManager.getColumnClass(column_);
	}
	
	@Override
	public int getRowCount() {
		return _rowManager.getRowCount();
	}

	@Override
	public Object getValueAt(int rowIndex_, int columnIndex_) {
		final GTableColumn<?,E> column = _columnManager.getColumn(columnIndex_);
		final E element = _rowManager.getElementAtRow(rowIndex_);
		if (column != null && element != null) {
			return column.getValue(element);
		}
		return null;
	}

	@Override
    public boolean isCellEditable(int row, int col) {
		return false;
	}
	
    public void setTableHeader(JTableHeader tableHeader_) {
        _tableHeader = tableHeader_;
        if (_tableHeader != null && _tableHeaderMouseListener != null) {
            _tableHeader.addMouseListener(_tableHeaderMouseListener);
        }
    }	
	
	public void addColumn(final GTableColumn<?,E> column_) {
		_columnManager.addColumn(column_);
	}
	public void addRow(final E element_) {
		_rowManager.addRow(element_);
		fireTableStructureChanged();
	}
	
	public GTableColumn<?,E> getColumn(final int column_) {
		return _columnManager.getColumn(column_);
	}
	public E getElementAtRow(final int row_) {
		return _rowManager.getElementAtRow(row_);
	}
	
	public void selectAllRows() {
		_rowManager.selectAllRows();
	}
	
    protected final ColumnManager<E> _columnManager;
    protected final RowManager<K,E,A> _rowManager;
    protected final A _adapter;
    protected /* not final */ MouseListener _tableHeaderMouseListener;
    
    private final String _id;
 	private JTableHeader _tableHeader;
}
