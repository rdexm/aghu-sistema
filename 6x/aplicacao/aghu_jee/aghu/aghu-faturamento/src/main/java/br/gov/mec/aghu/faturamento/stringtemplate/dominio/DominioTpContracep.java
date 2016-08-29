package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

@SuppressWarnings("ucd")
public enum DominioTpContracep implements
		DominioCodigoInt {

	NENHUM(0),
	LAM(1),
	OGINO_KNAUS(2),
	TEMP_BASAL(3),
	BILLINGS(4),
	CINTO_TERMICO(5),
	DIU(6),
	DIAFRAGMA(7),
	PRESERVATIVO(8),
	ESPERMICIDA(9),
	HORMONIO_ORAL(10),
	HORMONIO_INJETAVEL(11),
	COITO_INTERROMPIDO(12), ;

	private final int codigo;

	private DominioTpContracep(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}
}
