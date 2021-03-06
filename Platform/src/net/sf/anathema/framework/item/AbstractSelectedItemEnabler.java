package net.sf.anathema.framework.item;

import net.sf.anathema.framework.presenter.IItemManagementModel;
import net.sf.anathema.framework.presenter.IItemManagementModelListener;
import net.sf.anathema.framework.repository.IItem;
import net.sf.anathema.lib.exception.AnathemaException;

import javax.swing.Action;

public abstract class AbstractSelectedItemEnabler {

  private final Action action;

  public AbstractSelectedItemEnabler(final IItemManagementModel model, Action action) {
    this.action = action;
    model.addListener(new IItemManagementModelListener() {
      @Override
      public void itemAdded(IItem item) throws AnathemaException {
        adjustEnabled(model.getSelectedItem());
      }

      @Override
      public void itemSelected(IItem item) {
        adjustEnabled(model.getSelectedItem());
      }

      @Override
      public void itemRemoved(IItem item) {
        adjustEnabled(model.getSelectedItem());
      }
    });
    action.setEnabled(isEnabled(model.getSelectedItem()));
  }

  private void adjustEnabled(IItem selectedItem) {
    action.setEnabled(isEnabled(selectedItem));
  }

  protected abstract boolean isEnabled(IItem selectedItem);
}
