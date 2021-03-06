package net.sf.anathema.character.equipment.item;

import net.sf.anathema.lib.gui.action.ActionConfiguration;
import net.sf.anathema.lib.gui.action.IActionConfiguration;
import net.sf.anathema.lib.gui.dialog.core.IDialogResult;
import net.sf.anathema.lib.gui.dialog.message.MessageUserDialogConfiguration;
import net.sf.anathema.lib.gui.dialog.userdialog.UserDialog;
import net.sf.anathema.lib.gui.dialog.userdialog.buttons.DialogButtonConfiguration;
import net.sf.anathema.lib.gui.list.veto.IVetor;
import net.sf.anathema.lib.gui.wizard.workflow.ICondition;
import net.sf.anathema.lib.message.IMessage;
import net.sf.anathema.lib.message.Message;
import net.sf.anathema.lib.message.MessageType;
import net.sf.anathema.lib.resources.IResources;

import java.awt.Component;

public class DiscardChangesVetor implements IVetor {

  private final ICondition preCondition;
  private final Component parentComponent;
  private final IResources resources;

  public DiscardChangesVetor(IResources resources, ICondition preCondition, Component parentComponent) {
    this.resources = resources;
    this.preCondition = preCondition;
    this.parentComponent = parentComponent;
  }

  @Override
  public boolean vetos() {
    if (!preCondition.isFulfilled()) {
      return false;
    }
    String messageText = resources.getString("Equipment.Creation.UnsavedChangesMessage.Text"); //$NON-NLS-1$
    IMessage message = new Message(messageText, MessageType.WARNING);
    MessageUserDialogConfiguration configuration = new MessageUserDialogConfiguration(
        message,
        new DialogButtonConfiguration() {
          @Override
          public IActionConfiguration getOkActionConfiguration() {
            return new ActionConfiguration(resources.getString("Equipment.Creation.UnsavedChangesMessage.OKButton")); //$NON-NLS-1$
          }
        });
    UserDialog userDialog = new UserDialog(parentComponent, configuration);
    IDialogResult result = userDialog.show();
    return result.isCanceled();
  }
}