package br.gov.mec.aghu.faturamento.stringtemplate.renderer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;

public class AttributeFormatPairRendererHelper {

	@SuppressWarnings("rawtypes")
	private Map<Class, ExtendedAttributeRenderer> classToRenderer;

	@SuppressWarnings("rawtypes")
	public AttributeFormatPairRendererHelper(final ExtendedAttributeRenderer... renderers) {

		super();

		this.classToRenderer = new HashMap<Class, ExtendedAttributeRenderer>();
		if (renderers != null) {
			for (ExtendedAttributeRenderer r : renderers) {
				for (Class c : r.getSupportedTypes()) {
					this.classToRenderer.put(c, r);
				}
			}
		}
	}

	public AttributeFormatPair toValue(final Object value, final String format) {

		AttributeFormatPair result = null;
		ExtendedAttributeRenderer renderer = null;

		if (value != null) {
			renderer = this.classToRenderer.get(value.getClass());
			if (renderer != null) {
				result = renderer.toValue(value, format);
			} else {
				throw new IllegalArgumentException("Argumento do tipo: " + value.getClass().getCanonicalName() + " nao suportado.");
			}
		} else {
			result = new AttributeFormatPair(value, format);
		}

		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void registrarTiposTratados(final StringTemplate st) {

		Map renderMap = null;
		List<Class> lstClass = null;

		if (st != null) {
			renderMap = new HashMap();
			for (ExtendedAttributeRenderer r : this.classToRenderer.values()) {
				lstClass = r.obterClassesTratadas();
				for (Class c : lstClass) {
					renderMap.put(c, r);
				}
			}
			st.setAttributeRenderers(renderMap);
		}
	}

}
