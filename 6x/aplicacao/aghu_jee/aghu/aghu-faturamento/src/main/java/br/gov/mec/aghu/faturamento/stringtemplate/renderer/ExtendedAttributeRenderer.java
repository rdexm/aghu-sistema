package br.gov.mec.aghu.faturamento.stringtemplate.renderer;

import java.util.List;
import java.util.Set;

import org.antlr.stringtemplate.AttributeRenderer;

public abstract class ExtendedAttributeRenderer
		implements
			AttributeRenderer {

	@Override
	public String toString(final Object obj) {

		String result = null;
		AttributeFormatPair casted = null;

		if (obj instanceof AttributeFormatPair) {
			casted = (AttributeFormatPair) obj;
			result = this.toString(casted.getAttribute(), casted.getFormat());
		} else {
			result = String.valueOf(obj);
		}

		return result;
	}

	public abstract String toString(final Object valor, final String formatacao);

	@SuppressWarnings("rawtypes")
	public abstract Set<Class> getSupportedTypes();

	public abstract AttributeFormatPair toValue(final Object valor, final String formatacao);

	@SuppressWarnings("rawtypes")
	public abstract List<Class> obterClassesTratadas();

}
