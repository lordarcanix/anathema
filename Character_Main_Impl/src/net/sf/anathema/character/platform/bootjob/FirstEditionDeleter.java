package net.sf.anathema.character.platform.bootjob;

import net.sf.anathema.framework.IApplicationModel;

import java.io.File;

public class FirstEditionDeleter extends CharacterChanger {

  public FirstEditionDeleter(IApplicationModel model) {
    super(model);
  }

  @Override
  protected void actWithCharacter(File character) {
    if (isSecondEditionCharacter(character)) {
      return;
    }
    character.delete();
  }

  private boolean isSecondEditionCharacter(File file) {
    String xmlString = getCharacterAsString(file);
    return xmlString.contains("<RuleSet name=\"SecondEdition\"/>");
  }
}