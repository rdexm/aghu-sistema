package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

public enum DominioSaidaUtineo implements
		DominioCodigoInt {

	NAO_APLICAVEL(0),
	ALTA_UTI(1),
	OBITO_UTI(2),
	TRANSF_UTI(3), ;

	private final int codigo;

	private DominioSaidaUtineo(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}

	public static DominioSaidaUtineo valueOf(final int codigo) {

		DominioSaidaUtineo result = null;

		for (DominioSaidaUtineo d : DominioSaidaUtineo.values()) {
			if (d.getCodigo() == codigo) {
				result = d;
				break;
			}
		}

		return result;
	}
}
