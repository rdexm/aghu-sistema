package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

@SuppressWarnings("ucd")
public enum DominioCpfCnsCnpjCnes implements
		DominioCodigoInt {

	NAO_APLICAVEL(0),
	CPF(1),
	CNS(2),
	CNPJ(3),
	CNES(5), ;

	private final int codigo;

	private DominioCpfCnsCnpjCnes(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}
}
