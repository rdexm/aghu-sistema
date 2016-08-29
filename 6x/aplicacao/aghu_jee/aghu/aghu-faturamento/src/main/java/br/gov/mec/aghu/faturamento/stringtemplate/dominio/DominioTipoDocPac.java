package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

public enum DominioTipoDocPac implements
		DominioCodigoInt {

	PIS_PASEP(1),
	IDENTIDADE(2),
	REGISTRO_NASC(3),
	CPF(4),
	IGNORADO(5),
	MAT_CERTIDAO_NASC(6), ;

	private final int codigo;

	private DominioTipoDocPac(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}

	public static DominioTipoDocPac valueOf(final int codigo) {

		DominioTipoDocPac result = null;

		for (DominioTipoDocPac d : DominioTipoDocPac.values()) {
			if (d.getCodigo() == codigo) {
				result = d;
				break;
			}
		}

		return result;
	}
}
