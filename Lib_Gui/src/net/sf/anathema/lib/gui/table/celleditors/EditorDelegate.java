package net.sf.anathema.lib.gui.table.celleditors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;

public class EditorDelegate implements ActionListener, ItemListener, Serializable {

  private final AbstractDelegatingCellEditor editor;
  private static final int CLICK_COUNT_TO_START = 2;
  private Object value;


  public EditorDelegate(AbstractDelegatingCellEditor editor) {
    this.editor = editor;
  }
  public Object getCellEditorValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public boolean isCellEditable(EventObject anEvent) {
    if (anEvent instanceof MouseEvent) {
      return ((MouseEvent) anEvent).getClickCount() >= CLICK_COUNT_TO_START;
    }
    return true;
  }

  public boolean stopCellEditing() {
    editor.fireEditingStopped();
    return true;
  }

  public void cancelCellEditing() {
    editor.fireEditingCanceled();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    this.editor.stopCellEditing();
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    this.editor.stopCellEditing();
  }
}