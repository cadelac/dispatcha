package cadelac.framework.blade.mvc.controller.table.column;

import java.util.Comparator;

public abstract class ColumnComparator<E> implements Comparator<E> {
	public abstract int compare(final E e1_, final E e2_);
}
