package net.sf.anathema.character.lunar.reporting.rendering;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import net.sf.anathema.character.generic.character.IGenericTraitCollection;
import net.sf.anathema.character.generic.impl.traits.EssenceTemplate;
import net.sf.anathema.character.generic.template.abilities.IGroupedTraitType;
import net.sf.anathema.character.generic.traits.ITraitType;
import net.sf.anathema.character.generic.traits.types.AttributeGroupType;
import net.sf.anathema.character.generic.traits.types.AttributeType;
import net.sf.anathema.character.lunar.beastform.BeastformTemplate;
import net.sf.anathema.character.lunar.beastform.model.SecondEditionBeastformModel;
import net.sf.anathema.character.reporting.pdf.content.ReportContent;
import net.sf.anathema.character.reporting.pdf.rendering.Bounds;
import net.sf.anathema.character.reporting.pdf.rendering.Position;
import net.sf.anathema.character.reporting.pdf.rendering.general.box.IBoxContentEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.general.traits.PdfTraitEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.graphics.SheetGraphics;
import net.sf.anathema.lib.resources.IResources;

import static net.sf.anathema.character.reporting.pdf.rendering.page.IVoidStateFormatConstants.LINE_HEIGHT;

public class SecondEditionLunarSpiritFormEncoder implements IBoxContentEncoder {

  private final IResources resources;
  private final PdfTraitEncoder smallTraitEncoder = PdfTraitEncoder.createSmallTraitEncoder();

  public SecondEditionLunarSpiritFormEncoder(IResources resources) {
    this.resources = resources;
  }

  public String getHeaderKey(ReportContent content) {
    return "Lunar.SpiritForm"; //$NON-NLS-1$
  }

  public void encode(SheetGraphics graphics, ReportContent reportContent, Bounds bounds) {
    IGroupedTraitType[] attributeGroups = reportContent.getCharacter().getTemplate().getAttributeGroups();
    SecondEditionBeastformModel additionalModel =
      (SecondEditionBeastformModel) reportContent.getCharacter().getAdditionalModel(BeastformTemplate.TEMPLATE_ID);
    IGenericTraitCollection traitCollection = additionalModel.getSpiritTraitCollection();
    encodeAttributes(graphics, bounds, attributeGroups, traitCollection);
    encodeForm(graphics, bounds, additionalModel.getSpiritForm());
  }

  private void encodeForm(SheetGraphics graphics, Bounds bounds, String form) {
    Font font = graphics.createTableFont();
    Bounds newBounds = new Bounds(bounds.x, bounds.y, bounds.width, bounds.height - 50);
    String text = resources.getString("Sheet.Header.Lunar.SpiritForm") + ": " + form;
    try {
      graphics.createSimpleColumn(newBounds).withLeading(LINE_HEIGHT - 2).andTextPart(new Phrase(text, font)).encode();
    } catch (DocumentException e) {
    }
  }

  private void encodeAttributes(SheetGraphics graphics, Bounds contentBounds, IGroupedTraitType[] attributeGroups,
    IGenericTraitCollection traitCollection) {
    float groupSpacing = smallTraitEncoder.getTraitHeight() / 2;
    float y = contentBounds.getMaxY() - 2 * groupSpacing;
    int maximum = EssenceTemplate.SYSTEM_ESSENCE_MAX;
    float width = contentBounds.getWidth();
    for (IGroupedTraitType groupedTraitType : attributeGroups) {
      if (!groupedTraitType.getGroupId().equals(AttributeGroupType.Physical.name()) &&
        !groupedTraitType.getTraitType().getId().equals(AttributeType.Appearance.name())) {
        continue;
      }

      ITraitType traitType = groupedTraitType.getTraitType();
      String traitLabel = resources.getString("AttributeType.Name." + traitType.getId()); //$NON-NLS-1$
      int value = traitCollection.getTrait(traitType).getCurrentValue();
      Position position = new Position(contentBounds.x, y);
      y -= smallTraitEncoder.encodeWithText(graphics, traitLabel, position, width, value, maximum);
    }
  }

  public boolean hasContent(ReportContent content) {
    return true;
  }
}
