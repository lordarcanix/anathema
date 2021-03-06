package net.sf.anathema.cascades.presenter;

import net.sf.anathema.character.generic.impl.magic.MartialArtsUtilities;
import net.sf.anathema.character.generic.impl.magic.charm.CharmTree;
import net.sf.anathema.character.generic.impl.magic.charm.MartialArtsCharmTree;
import net.sf.anathema.character.generic.magic.charms.ICharmGroup;
import net.sf.anathema.character.generic.magic.charms.ICharmTree;
import net.sf.anathema.character.generic.magic.charms.MartialArtsLevel;
import net.sf.anathema.character.generic.template.ICharacterTemplate;
import net.sf.anathema.character.generic.template.ITemplateRegistry;
import net.sf.anathema.character.generic.template.magic.ICharmTemplate;
import net.sf.anathema.character.generic.template.magic.IUniqueCharmType;
import net.sf.anathema.character.generic.type.CharacterTypes;
import net.sf.anathema.character.generic.type.ICharacterType;
import net.sf.anathema.charmtree.presenter.CharmGroupCollection;
import net.sf.anathema.lib.util.Identified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CascadeGroupCollection implements CharmGroupCollection {
  private final CharacterTypes characterTypes;
  private ITemplateRegistry templateRegistry;
  private CharmTreeIdentificateMap treeIdentificateMap;

  public CascadeGroupCollection(CharacterTypes characterTypes, ITemplateRegistry templateRegistry, CharmTreeIdentificateMap treeIdentificateMap) {
    this.templateRegistry = templateRegistry;
    this.treeIdentificateMap = treeIdentificateMap;
    this.characterTypes = characterTypes;
  }

  @Override
  public ICharmGroup[] getCharmGroups() {
    List<ICharmGroup> allCharmGroups = new ArrayList<>();
    initCharacterTypeCharms(allCharmGroups);
    initMartialArtsCharms(allCharmGroups);
    return allCharmGroups.toArray(new ICharmGroup[allCharmGroups.size()]);
  }

  private void initCharacterTypeCharms(List<ICharmGroup> allCharmGroups) {
    for (ICharacterType type : characterTypes.findAll()) {
      ICharacterTemplate template = templateRegistry.getDefaultTemplate(type);
      if (template == null) {
        continue;
      }
      ICharmTemplate charmTemplate = template.getMagicTemplate().getCharmTemplate();
      if (charmTemplate.canLearnCharms()) {
        registerTypeCharms(allCharmGroups, type, template);
        registerUniqueCharms(allCharmGroups, charmTemplate);
      }
    }
  }

  private void initMartialArtsCharms(List<ICharmGroup> allCharmGroups) {
    ICharmTemplate charmTemplate = findCharmTemplateOfCharacterTypeMostProficientWithMartialArts();
    ICharmTree martialArtsTree = new MartialArtsCharmTree(charmTemplate);
    treeIdentificateMap.put(MartialArtsUtilities.MARTIAL_ARTS, martialArtsTree);
    allCharmGroups.addAll(Arrays.asList(martialArtsTree.getAllCharmGroups()));
  }

  private ICharmTemplate findCharmTemplateOfCharacterTypeMostProficientWithMartialArts() {
    ICharmTemplate currentFavoriteTemplate = new NullCharmTemplate();
    for (ICharacterType type : characterTypes.findAll()) {
      ICharmTemplate charmTemplate = templateRegistry.getDefaultTemplate(type).getMagicTemplate().getCharmTemplate();
      MartialArtsLevel martialArtsLevel = charmTemplate.getMartialArtsRules().getStandardLevel();
      MartialArtsLevel highestLevelSoFar = currentFavoriteTemplate.getMartialArtsRules().getStandardLevel();
      if (martialArtsLevel.compareTo(highestLevelSoFar) > 0) {
        currentFavoriteTemplate = charmTemplate;
      }
    }
    return currentFavoriteTemplate;
  }

  private void registerUniqueCharms(List<ICharmGroup> allCharmGroups, ICharmTemplate charmTemplate) {
    if (!charmTemplate.hasUniqueCharms()) {
      return;
    }
    IUniqueCharmType uniqueType = charmTemplate.getUniqueCharmType();
    ICharmTree uniqueTree = new CharmTree(charmTemplate.getUniqueCharms());
    registerGroups(allCharmGroups, uniqueType.getId(), uniqueTree);
  }

  private void registerTypeCharms(List<ICharmGroup> allCharmGroups, ICharacterType type,
                                  ICharacterTemplate defaultTemplate) {
    ICharmTree typeTree = new CharmTree(defaultTemplate.getMagicTemplate().getCharmTemplate());
    registerGroups(allCharmGroups, type, typeTree);
  }

  private void registerGroups(List<ICharmGroup> allCharmGroups, Identified typeId, ICharmTree charmTree) {
    ICharmGroup[] groups = charmTree.getAllCharmGroups();
    if (groups.length != 0) {
      treeIdentificateMap.put(typeId, charmTree);
      allCharmGroups.addAll(Arrays.asList(groups));
    }
  }
}