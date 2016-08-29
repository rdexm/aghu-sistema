package br.gov.mec.aghu.core.saucer;

import org.xhtmlrenderer.extend.FontContext;
import org.xhtmlrenderer.pdf.ITextFSFontMetrics;
import org.xhtmlrenderer.pdf.ITextTextRenderer;
import org.xhtmlrenderer.render.FSFont;
import org.xhtmlrenderer.render.FSFontMetrics;

import br.gov.mec.aghu.core.saucer.AGHUITextFontResolver.FontDescription;

import com.lowagie.text.pdf.BaseFont;

public class AGHUITextTextRenderer extends ITextTextRenderer{
	private static final float TEXT_MEASURING_DELTA = 0.01f;
	
	@Override
	public FSFontMetrics getFSFontMetrics(FontContext context, FSFont font, String string) {
		 FontDescription descr = ((AGHUITextFSFont)font).getFontDescription();
	        BaseFont bf = descr.getFont();
	        float size = font.getSize2D();
	        ITextFSFontMetrics result = new ITextFSFontMetrics();
	        result.setAscent(bf.getFontDescriptor(BaseFont.BBOXURY, size));
	        result.setDescent(-bf.getFontDescriptor(BaseFont.BBOXLLY, size));
	        
	        result.setStrikethroughOffset(-descr.getYStrikeoutPosition() / 1000f * size);
	        if (descr.getYStrikeoutSize() != 0) {
	            result.setStrikethroughThickness(descr.getYStrikeoutSize() / 1000f * size);
	        } else {
	            result.setStrikethroughThickness(size / 12.0f);
	        }
	        
	        result.setUnderlineOffset(-descr.getUnderlinePosition() / 1000f * size);
	        result.setUnderlineThickness(descr.getUnderlineThickness() / 1000f * size);
	        
	        return result;
	}
	
	@Override
	public int getWidth(FontContext context, FSFont font, String string) {
		BaseFont bf = ((AGHUITextFSFont)font).getFontDescription().getFont();
		float result = bf.getWidthPoint(string, font.getSize2D());
		if (result - Math.floor(result) < TEXT_MEASURING_DELTA) {
		    return (int)result;
		} else {
		    return (int)Math.ceil(result); 
		}
    }
}
