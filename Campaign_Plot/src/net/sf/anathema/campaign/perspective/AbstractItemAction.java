package net.sf.anathema.campaign.perspective;

import net.sf.anathema.framework.IApplicationModel;
import net.sf.anathema.framework.item.IItemType;
import net.sf.anathema.framework.item.IItemTypeRegistry;
import net.sf.anathema.lib.gui.action.SmartAction;
import net.sf.anathema.lib.gui.dialog.core.IDialogResult;
import net.sf.anathema.lib.gui.dialog.core.ISwingFrameOrDialog;
import net.sf.anathema.lib.gui.dialog.wizard.WizardDialog;
import net.sf.anathema.lib.gui.swing.GuiUtilities;
import net.sf.anathema.lib.gui.wizard.AnathemaWizardDialog;
import net.sf.anathema.lib.gui.wizard.IAnathemaWizardPage;
import net.sf.anathema.lib.resources.IResources;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import static net.sf.anathema.campaign.module.NoteTypeConfiguration.NOTE_ITEM_TYPE_ID;
import static net.sf.anathema.campaign.module.SeriesTypeConfiguration.SERIES_ITEM_TYPE_ID;

public abstract class AbstractItemAction extends SmartAction {

  private final IResources resources;
  private final IApplicationModel anathemaModel;

  public AbstractItemAction(IApplicationModel anathemaModel, IResources resources) {
    this.anathemaModel = anathemaModel;
    this.resources = resources;
  }

  protected final boolean showDialog(Component parentComponent, IAnathemaWizardPage startPage) {
    WizardDialog dialog = new AnathemaWizardDialog(parentComponent, startPage);
    ISwingFrameOrDialog configuredDialog = dialog.getConfiguredDialog();
    configuredDialog.setResizable(false);
    GuiUtilities.centerToParent(configuredDialog.getWindow());
    IDialogResult result = dialog.show();
    return result.isCanceled();
  }

  protected IApplicationModel getAnathemaModel() {
    return anathemaModel;
  }

  protected IResources getResources() {
    return resources;
  }

  protected static IItemType[] collectItemTypes(IApplicationModel model) {
    List<IItemType> types = new ArrayList<>();
    IItemTypeRegistry itemTypeRegistry = model.getItemTypeRegistry();
    types.add(itemTypeRegistry.getById(NOTE_ITEM_TYPE_ID));
    types.add(itemTypeRegistry.getById(SERIES_ITEM_TYPE_ID));
    return types.toArray(new IItemType[types.size()]);
  }
}