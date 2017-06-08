package cadelac.framework.blade.mvc.controller.table;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import cadelac.framework.blade.mvc.controller.table.column.GTableColumn;


/**
 * improved double cell renderer
 * @author cadelac
 *
 */
public class DblCellRenderer<V> extends JLabel implements TableCellRenderer {
	
	public DblCellRenderer() {
		setHorizontalAlignment(SwingConstants.RIGHT);
		setOpaque(true);
		decimalFormat = new DecimalFormat("#.##");
    	uptickBackground = new Color(0, 102, 0);
    	uptickForeground = Color.WHITE;
    	downtickBackground = new Color(153, 0, 0);
    	downtickForeground = Color.WHITE;
	}
	
	
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	
    	setText(decimalFormat.format((Double)value));

    	if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
    	}
    	else {
        	Color backgroundColor = table.getBackground();
        	Color foregroundColor = table.getForeground();

        	final TableModel controller = table.getModel(); // TableModel is really a Controller in the MVC paradigm
            if (controller instanceof TableController) {
            	TableController<?,?,?> tableController = (TableController<?,?,?>) controller;

            	@SuppressWarnings("unchecked")
    			GTableColumn<Double,V> vtc = (GTableColumn<Double,V>) tableController.getColumn(column);
            	if (vtc != null) {
            		@SuppressWarnings("unchecked")
					V rowData = (V) tableController.getElementAtRow(row); // row is equivalent to symbolId
                    if (rowData != null) {
                    	Double delta = vtc.getDelta(rowData);
                    	if (delta != null) {
                        	if (delta>0.0) {
                        		backgroundColor = uptickBackground;
                        		foregroundColor = uptickForeground;
                            }
                            else if (delta<0.0) {
                            	backgroundColor = downtickBackground;
                            	foregroundColor = downtickForeground;
                        	}
                    	}
                    }
            	}
            }
            setBackground(backgroundColor);
            setForeground(foregroundColor);
    	}
        return this;
    }
    
    private final DecimalFormat decimalFormat;
	private final Color uptickBackground;
	private final Color uptickForeground;
	private final Color downtickBackground;
	private final Color downtickForeground;
}