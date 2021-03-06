package net.sf.anathema.character.presenter.magic.detail;

import net.sf.anathema.lib.gui.IView;
import net.sf.anathema.lib.gui.Presenter;

public interface MagicDetailPresenter extends Presenter {

  MagicDetailModel getModel();

  String getDetailTitle();

  IView getView();
}
