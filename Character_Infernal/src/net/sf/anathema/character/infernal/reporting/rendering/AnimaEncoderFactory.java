package net.sf.anathema.character.infernal.reporting.rendering;

import net.sf.anathema.character.infernal.InfernalCharacterModule;
import net.sf.anathema.character.reporting.pdf.content.BasicContent;
import net.sf.anathema.character.reporting.pdf.content.stats.anima.AnimaTableRangeProvider;
import net.sf.anathema.character.reporting.pdf.rendering.boxes.RegisteredEncoderFactory;
import net.sf.anathema.character.reporting.pdf.rendering.boxes.anima.AbstractAnimaEncoderFactory;
import net.sf.anathema.character.reporting.pdf.rendering.boxes.anima.AnimaTableEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.general.table.ITableEncoder;
import net.sf.anathema.lib.resources.IResources;

@RegisteredEncoderFactory
public class AnimaEncoderFactory extends AbstractAnimaEncoderFactory {

  @Override
  protected ITableEncoder getAnimaTableEncoder(IResources resources) {
    return new AnimaTableEncoder(resources, getFontSize(), new AnimaTableRangeProvider());
  }

  @Override
  public boolean supports(BasicContent content) {
    return content.isOfType(InfernalCharacterModule.type);
  }
}