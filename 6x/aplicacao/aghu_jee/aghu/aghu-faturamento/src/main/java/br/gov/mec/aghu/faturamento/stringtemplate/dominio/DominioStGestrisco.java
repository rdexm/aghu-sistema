package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

@SuppressWarnings("ucd")
public enum DominioStGestrisco implements
		DominioCodigoInt {

	SIM(0),
	NAO(1),
	;

	private final int codigo;

	private DominioStGestrisco(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}
}
