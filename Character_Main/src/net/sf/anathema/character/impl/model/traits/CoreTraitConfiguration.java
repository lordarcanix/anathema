package net.sf.anathema.character.impl.model.traits;

import net.sf.anathema.character.generic.additionalrules.IAdditionalTraitRules;
import net.sf.anathema.character.generic.backgrounds.IBackgroundTemplate;
import net.sf.anathema.character.generic.caste.ICasteCollection;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ICharacterModelContext;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ITraitContext;
import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.generic.template.ITraitTemplateCollection;
import net.sf.anathema.character.generic.template.abilities.GroupedTraitType;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.generic.traits.groups.IIdentifiedCasteTraitTypeGroup;
import net.sf.anathema.character.generic.traits.groups.IIdentifiedTraitTypeGroup;
import net.sf.anathema.character.generic.traits.types.OtherTraitType;
import net.sf.anathema.character.generic.traits.types.VirtueType;
import net.sf.anathema.character.impl.model.traits.backgrounds.BackgroundArbitrator;
import net.sf.anathema.character.impl.model.traits.backgrounds.BackgroundConfiguration;
import net.sf.anathema.character.impl.model.traits.creation.AbilityTypeGroupFactory;
import net.sf.anathema.character.impl.model.traits.creation.AttributeTypeGroupFactory;
import net.sf.anathema.character.impl.model.traits.creation.DefaultTraitFactory;
import net.sf.anathema.character.impl.model.traits.creation.FavorableTraitFactory;
import net.sf.anathema.character.impl.model.traits.creation.FavoredIncrementChecker;
import net.sf.anathema.character.impl.model.traits.creation.YoziFavoredIncrementChecker;
import net.sf.anathema.character.impl.model.traits.creation.YoziTypeGroupFactory;
import net.sf.anathema.character.impl.model.traits.listening.WillpowerListening;
import net.sf.anathema.character.library.trait.AbstractTraitCollection;
import net.sf.anathema.character.library.trait.ITrait;
import net.sf.anathema.character.library.trait.TraitCollectionUtilities;
import net.sf.anathema.character.library.trait.favorable.IFavorableTrait;
import net.sf.anathema.character.library.trait.favorable.IIncrementChecker;
import net.sf.anathema.character.library.trait.specialties.ISpecialtiesConfiguration;
import net.sf.anathema.character.library.trait.specialties.SpecialtiesConfiguration;
import net.sf.anathema.character.library.trait.visitor.IDefaultTrait;
import net.sf.anathema.character.model.background.IBackgroundConfiguration;
import net.sf.anathema.character.model.traits.ICoreTraitConfiguration;
import net.sf.anathema.characterengine.persona.Persona;
import net.sf.anathema.characterengine.persona.QualityClosure;
import net.sf.anathema.characterengine.quality.Quality;
import net.sf.anathema.exaltedengine.ExaltedEngine;
import net.sf.anathema.exaltedengine.attributes.Attribute;
import net.sf.anathema.lib.registry.IIdentificateRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;

public class CoreTraitConfiguration extends AbstractTraitCollection implements ICoreTraitConfiguration {
  private static final boolean useGenericEngine = false;
  private final FavorableTraitFactory favorableTraitFactory;
  private final BackgroundConfiguration backgrounds;
  private final IIdentifiedCasteTraitTypeGroup[] abilityTraitGroups;
  private final IIdentifiedCasteTraitTypeGroup[] attributeTraitGroups;
  private final SpecialtiesConfiguration specialtyConfiguration;
  private final Persona persona = new ExaltedEngine().createCharacter();

  public CoreTraitConfiguration(ICharacterTemplate template, ICharacterModelContext modelContext,
                                IIdentificateRegistry<IBackgroundTemplate> backgroundRegistry) {
    ICasteCollection casteCollection = template.getCasteCollection();
    this.abilityTraitGroups = new AbilityTypeGroupFactory().createTraitGroups(casteCollection, template.getAbilityGroups());
    this.attributeTraitGroups = new AttributeTypeGroupFactory().createTraitGroups(casteCollection, template.getAttributeGroups());
    ITraitContext traitContext = modelContext.getTraitContext();
    ITraitTemplateCollection traitTemplateCollection = template.getTraitTemplateCollection();
    IAdditionalTraitRules additionalTraitRules = template.getAdditionalRules().getAdditionalTraitRules();
    DefaultTraitFactory traitFactory = new DefaultTraitFactory(traitContext, traitTemplateCollection, additionalTraitRules);
    this.favorableTraitFactory =
            new FavorableTraitFactory(traitContext, traitTemplateCollection, additionalTraitRules, modelContext.getBasicCharacterContext(),
                    modelContext.getCharacterListening());
    addTrait(traitFactory.createTrait(OtherTraitType.Essence));
    addTraits(traitFactory.createTraits(VirtueType.values()));
    addTrait(traitFactory.createTrait(OtherTraitType.Willpower));
    addAttributes(template);
    addYozis(template);
    IDefaultTrait willpower = TraitCollectionUtilities.getWillpower(this);
    IDefaultTrait[] virtues = TraitCollectionUtilities.getVirtues(this);
    if (additionalTraitRules.isWillpowerVirtueBased()) {
      new WillpowerListening().initListening(willpower, virtues);
    } else {
      willpower.setModifiedCreationRange(5, 10);
    }
    addAbilities(template);
    this.backgrounds = new BackgroundConfiguration(new BackgroundArbitrator(template), traitTemplateCollection, traitContext, backgroundRegistry);
    this.specialtyConfiguration = new SpecialtiesConfiguration(this, abilityTraitGroups, modelContext);
    getTrait(OtherTraitType.Essence).addCurrentValueListener(new EssenceLimitationListener(new AllTraits(), modelContext));
  }

  private void addAttributes(ICharacterTemplate template) {
    IIncrementChecker incrementChecker = FavoredIncrementChecker.createFavoredAttributeIncrementChecker(template, this);
    if (useGenericEngine) {
      persona.doForEachDisregardingRules(ExaltedEngine.ATTRIBUTE, new AddTraitBasedOnQuality());
    } else {
      addFavorableTraits(attributeTraitGroups, incrementChecker);
    }
  }

  private void addAbilities(ICharacterTemplate template) {
    IIncrementChecker incrementChecker = FavoredIncrementChecker.createFavoredAbilityIncrementChecker(template, this);
    addFavorableTraits(abilityTraitGroups, incrementChecker);
  }

  private void addYozis(ICharacterTemplate template) {
    ICasteCollection casteCollection = template.getCasteCollection();
    GroupedTraitType[] yoziGroups = template.getYoziGroups();
    IIdentifiedCasteTraitTypeGroup[] yoziTraitGroups = new YoziTypeGroupFactory().createTraitGroups(casteCollection, yoziGroups);
    IIncrementChecker incrementChecker = YoziFavoredIncrementChecker.create(this);
    addFavorableTraits(yoziTraitGroups, incrementChecker);
  }

  private void addFavorableTraits(IIdentifiedCasteTraitTypeGroup[] traitGroups, IIncrementChecker incrementChecker) {
    for (IIdentifiedCasteTraitTypeGroup traitGroup : traitGroups) {
      addTraits(favorableTraitFactory.createTraits(traitGroup, incrementChecker));
    }
  }

  @Override
  public ITrait getTrait(ITraitType traitType) {
    if (contains(traitType)) {
      return super.getTrait(traitType);
    }
    if (traitType instanceof IBackgroundTemplate) {
      return getBackgrounds().getBackgroundByTemplate((IBackgroundTemplate) traitType);
    }
    throw new UnsupportedOperationException("Unsupported trait type " + traitType); //$NON-NLS-1$
  }

  @Override
  public IBackgroundConfiguration getBackgrounds() {
    return backgrounds;
  }

  @Override
  public IIdentifiedTraitTypeGroup[] getAbilityTypeGroups() {
    return abilityTraitGroups;
  }

  @Override
  public final IIdentifiedCasteTraitTypeGroup[] getAttributeTypeGroups() {
    return attributeTraitGroups;
  }

  @Override
  public IFavorableTrait[] getAllAbilities() {
    List<ITraitType> abilityTypes = new ArrayList<>();
    for (IIdentifiedTraitTypeGroup group : getAbilityTypeGroups()) {
      Collections.addAll(abilityTypes, group.getAllGroupTypes());
    }
    return getFavorableTraits(abilityTypes.toArray(new ITraitType[abilityTypes.size()]));
  }

  @Override
  public ISpecialtiesConfiguration getSpecialtyConfiguration() {
    return specialtyConfiguration;
  }

  private class AllTraits implements TraitProvider {
    @Override
    public Iterator<ITrait> iterator() {
      return asList(getAllTraits()).iterator();
    }
  }

  private class AddTraitBasedOnQuality implements QualityClosure {
    @Override
    public void execute(Quality quality) {
      addTrait(new FavorableQualityTrait(persona, (Attribute) quality));
    }
  }
}