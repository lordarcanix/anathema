package net.sf.anathema.character.abyssal.reporting;

import net.sf.anathema.character.reporting.pdf.layout.simple.AbstractFirstEditionExaltPdfPartEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.general.box.IBoxContentEncoder;
import net.sf.anathema.lib.resources.IResources;

public class Simple1stEditionAbyssalPartEncoder extends AbstractFirstEditionExaltPdfPartEncoder {

  public Simple1stEditionAbyssalPartEncoder(IResources resources) {
    super(resources);
  }

  public IBoxContentEncoder getGreatCurseEncoder() {
    return new AbyssalResonanceEncoder();
  }

  @Override
  public IBoxContentEncoder getAnimaEncoder() {
    return new AbyssalAnimaEncoderFactory(getResources()).createAnimaEncoder();
  }
}
