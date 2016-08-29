package br.gov.mec.aghu.faturamento.stringtemplate.impl;

import java.util.HashMap;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPair;
import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPairRendererHelper;

@SuppressWarnings("PMD.SuspiciousConstantFieldName")
public abstract class AbstractRegistroAih
		implements
			RegistroAih {

	public static boolean DEFAULT_NULL_TO_ZERO_FLAG = false;
	private final NomeStringTemplateEnum nomeStringTemplate;
	private final Map<NomeAtributoEnum, String> atributoParaFormato;
	private final AttributeFormatPairRendererHelper rendererHelper;
	private boolean nullToZeroed = DEFAULT_NULL_TO_ZERO_FLAG;

	protected AbstractRegistroAih(
			final NomeStringTemplateEnum nomeStringTemplate,
			final AttributeFormatPairRendererHelper rendererHelper) {

		super();

		//check args
		if (nomeStringTemplate == null) {
			throw new IllegalArgumentException("Parametro nomeStringTemplate nao informado!!!");
		}
		if (rendererHelper == null) {
			throw new IllegalArgumentException("Parametro rendererHelper nao informado!!!");
		}
		//algo
		this.nomeStringTemplate = nomeStringTemplate;
		this.rendererHelper = rendererHelper;
		this.atributoParaFormato = new HashMap<RegistroAih.NomeAtributoEnum, String>();
	}

	@Override
	public NomeStringTemplateEnum getNomeStringTemplate() {

		return this.nomeStringTemplate;
	}

	@Override
	public void ajustarAtributos(final StringTemplate st) {

		AttributeFormatPair pair = null;

		//check args
		if (st == null) {
			throw new IllegalArgumentException("Parametro st nao informado!!!");
		}
		//algo
		this.rendererHelper.registrarTiposTratados(st);
		for (NomeAtributoEnum a : this.getAtributos()) {
			pair = this.getPair(a);
			st.setAttribute(a.getNome(), pair);
		}
	}

	@Override
	public StringTemplate obterStringTemplateComAtributos(final StringTemplateGroup stg) {

		StringTemplate result = null;

		//check args
		if (stg == null) {
			throw new IllegalArgumentException("Parametro stg nao informado!!!");
		}
		//algo
		result = stg.getInstanceOf(this.getNomeStringTemplate().getNome());
		this.ajustarAtributos(result);

		return result;
	}

	@Override
	public String setFormat(final NomeAtributoEnum atributo, final String format) {

		String result = null;

		if ((atributo != null) && (format != null)) {
			result = this.atributoParaFormato.put(atributo, format);
		}

		return result;
	}

	@Override
	public String getFormat(final NomeAtributoEnum atributo) {

		String result = null;

		result = this.atributoParaFormato.get(atributo);

		return result;
	}

	protected abstract Object getAtributo(NomeAtributoEnum atributo);

	@Override
	public AttributeFormatPair getPair(final NomeAtributoEnum atributo) {

		AttributeFormatPair result = null;
		Object valor = null;
		String format = null;

		valor = this.getAtributo(atributo);
		format = this.getFormat(atributo);
		result = this.rendererHelper.toValue(valor, format);

		return result;
	}

	public final boolean isNullToZeroed() {

		return this.nullToZeroed;
	}

	public final void setNullToZeroed(final boolean nullToZeroed) {

		this.nullToZeroed = nullToZeroed;
	}

	@Override
	public String toString() {

		return this.getDescricao();
	}
}
