package net.sf.anathema.initialization;

import net.sf.anathema.framework.IApplicationModel;
import net.sf.anathema.framework.initialization.IReportFactory;
import net.sf.anathema.framework.messaging.IMessageContainer;
import net.sf.anathema.framework.module.AnathemaCoreMenu;
import net.sf.anathema.framework.module.IItemTypeConfiguration;
import net.sf.anathema.framework.module.PreferencesElementsExtensionPoint;
import net.sf.anathema.framework.presenter.action.preferences.IPreferencesElement;
import net.sf.anathema.framework.view.ApplicationView;
import net.sf.anathema.lib.control.IChangeListener;
import net.sf.anathema.lib.resources.IResources;

import java.util.Collection;

public class AnathemaPresenter {

  private final IApplicationModel model;
  private final ApplicationView view;
  private final IResources resources;
  private final Collection<IItemTypeConfiguration> itemTypeConfigurations;
  private final Instantiater instantiater;

  public AnathemaPresenter(IApplicationModel model, ApplicationView view, IResources resources,
                           Collection<IItemTypeConfiguration> itemTypeConfigurations, Instantiater instantiater) {
    this.instantiater = instantiater;
    this.model = model;
    this.view = view;
    this.resources = resources;
    this.itemTypeConfigurations = itemTypeConfigurations;
  }

  public void initPresentation() throws InitializationException {
    for (IItemTypeConfiguration configuration : itemTypeConfigurations) {
      configuration.fillPresentationExtensionPoints(model.getExtensionPointRegistry(), resources, model, view);
    }
    initializePreferences();
    for (IItemTypeConfiguration configuration : itemTypeConfigurations) {
      configuration.registerViewFactory(model, resources);
    }
    runBootJobs();
    new AnathemaCoreMenu().add(resources, model, view.getMenuBar());
    initializeReports();
    IMessageContainer messageContainer = model.getMessageContainer();
    init(messageContainer);
  }

  private void init(final IMessageContainer messageContainer) {
    messageContainer.addChangeListener(new IChangeListener() {
      @Override
      public void changeOccurred() {
        showLatestMessage(messageContainer);
      }
    });
    showLatestMessage(messageContainer);
  }

  private void showLatestMessage(IMessageContainer messageContainer) {
    view.getStatusBar().setLatestMessage(messageContainer.getLatestMessage());
  }

  private void runBootJobs() throws InitializationException {
    Collection<IBootJob> jobs = instantiater.instantiateOrdered(BootJob.class);
    for (IBootJob bootJob : jobs) {
      bootJob.run(resources, model);
    }
  }

  private void initializePreferences() throws InitializationException {
    PreferencesElementsExtensionPoint extensionPoint =
            (PreferencesElementsExtensionPoint) model.getExtensionPointRegistry().get(PreferencesElementsExtensionPoint.ID);
    Collection<IPreferencesElement> elements = instantiater.instantiateOrdered(PreferenceElement.class);
    for (IPreferencesElement element : elements) {
      extensionPoint.addPreferencesElement(element);
    }
  }

  private void initializeReports() throws InitializationException {
    Collection<IReportFactory> factories = instantiater.instantiateOrdered(ReportFactoryAutoCollector.class);
    for (IReportFactory factory : factories) {
      model.getReportRegistry().addReports(factory.createReport(resources, model));
    }
  }
}