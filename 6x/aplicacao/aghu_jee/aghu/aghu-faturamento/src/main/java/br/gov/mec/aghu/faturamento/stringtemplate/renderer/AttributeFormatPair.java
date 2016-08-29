package br.gov.mec.aghu.faturamento.stringtemplate.renderer;


public class AttributeFormatPair {

	private final Object attribute;
	private final String format;

	public AttributeFormatPair(final Object attribute, final String format) {

		super();

		this.attribute = attribute;
		this.format = format;
	}

	public Object getAttribute() {

		return this.attribute;
	}

	public String getFormat() {

		return this.format;
	}

	@Override
	public String toString() {

		return String.valueOf(this.getAttribute());
	}
}
