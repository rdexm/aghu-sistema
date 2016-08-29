package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

public enum DominioStMudaproc implements
		DominioCodigoInt {

	SIM(1),
	NAO(2),
	;

	private final int codigo;

	private DominioStMudaproc(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}
}
