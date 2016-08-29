package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

public enum DominioModIntern implements
		DominioCodigoInt {

	NAO_APLICAVEL(0),
	HOSPITALAR(2),
	HOSPITAL_DIA(3),
	INTERNACAO_DOMICILIAR(4), ;

	private final int codigo;

	private DominioModIntern(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}

	public static DominioModIntern valueOf(final int codigo) {

		DominioModIntern result = null;

		for (DominioModIntern d : DominioModIntern.values()) {
			if (d.getCodigo() == codigo) {
				result = d;
				break;
			}
		}

		return result;
	}
}
