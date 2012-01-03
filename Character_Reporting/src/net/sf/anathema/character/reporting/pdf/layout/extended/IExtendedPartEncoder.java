package net.sf.anathema.character.reporting.pdf.layout.extended;

import net.sf.anathema.character.generic.traits.types.OtherTraitType;
import net.sf.anathema.character.reporting.pdf.rendering.general.box.ContentEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.general.box.IVariableContentEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.page.PageEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.page.PdfPageConfiguration;

public interface IExtendedPartEncoder {

  ContentEncoder getAnimaEncoder();

  ContentEncoder getEssenceEncoder();

  ContentEncoder getDotsEncoder(OtherTraitType trait, int traitMax, String traitHeaderKey);

  ContentEncoder getGreatCurseEncoder();

  ContentEncoder getHealthEncoder();

  ContentEncoder getMovementEncoder();

  boolean hasMagicPage();

  IVariableContentEncoder[] getAdditionalFirstPageEncoders();

  PageEncoder[] getAdditionalPages(PdfPageConfiguration configuration);
}
