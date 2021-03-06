package net.sf.anathema.character.impl.costs;

import net.sf.anathema.character.dummy.trait.DummyCoreTraitConfiguration;
import net.sf.anathema.character.dummy.trait.DummyTraitContext;
import net.sf.anathema.character.generic.IBasicCharacterData;
import net.sf.anathema.character.generic.dummy.DummyCasteType;
import net.sf.anathema.character.generic.dummy.DummyExaltCharacterType;
import net.sf.anathema.character.generic.framework.additionaltemplate.listening.ICharacterChangeListener;
import net.sf.anathema.character.generic.framework.additionaltemplate.model.ICharacterListening;
import net.sf.anathema.character.generic.impl.additional.NullAdditionalRules;
import net.sf.anathema.character.generic.impl.traits.DummyTraitTemplateFactory;
import net.sf.anathema.character.generic.impl.traits.TraitTemplateCollection;
import net.sf.anathema.character.generic.template.ITemplateType;
import net.sf.anathema.character.generic.template.TemplateType;
import net.sf.anathema.character.generic.traits.types.AbilityType;
import net.sf.anathema.character.generic.traits.types.OtherTraitType;
import net.sf.anathema.character.generic.type.ICharacterType;
import net.sf.anathema.character.impl.model.traits.creation.DefaultTraitFactory;
import net.sf.anathema.character.impl.model.traits.creation.FavorableTraitFactory;
import net.sf.anathema.character.library.trait.favorable.FriendlyIncrementChecker;
import net.sf.anathema.character.library.trait.favorable.IIncrementChecker;

public abstract class AbstractBonusPointTestCase {

  protected static void addAbilityAndEssence(DummyCoreTraitConfiguration coreTraits) {
    NullAdditionalRules additionalRules = new NullAdditionalRules();
    TraitTemplateCollection templateCollection = new TraitTemplateCollection(new DummyTraitTemplateFactory());
    DummyTraitContext traitContext = new DummyTraitContext(coreTraits);
    DefaultTraitFactory traitFactory = new DefaultTraitFactory(traitContext, templateCollection, additionalRules);
    coreTraits.addTestTrait(traitFactory.createTrait(OtherTraitType.Essence));
    FavorableTraitFactory favorableTraitFactory =
            new FavorableTraitFactory(traitContext, templateCollection, new NullAdditionalRules(), new IBasicCharacterData() {
              @Override
              public DummyCasteType getCasteType() {
                return new DummyCasteType();
              }

              @Override
              public ICharacterType getCharacterType() {
                return getTemplateType().getCharacterType();
              }

              @Override
              public boolean isExperienced() {
                return false;
              }

              @Override
              public ITemplateType getTemplateType() {
                return new TemplateType(new DummyExaltCharacterType());
              }
            }, new ICharacterListening() {
              @Override
              public void addChangeListener(ICharacterChangeListener changeListener) {
                // Nothing to do
              }
            }
            );
    IIncrementChecker friendlyIncrementChecker = new FriendlyIncrementChecker();
    for (AbilityType traitType : AbilityType.values()) {
      DummyCasteType[] casteType = {new DummyCasteType()};
      coreTraits.addTestTrait(favorableTraitFactory.createTrait(traitType, casteType, friendlyIncrementChecker));
      coreTraits.addAbilityTypeToGroup(traitType, casteType[0].getId());
    }
  }
}