package br.gov.mec.aghu.util;

/**
 * Representa as colunas da AGH_PARAMETROS
 * 
 * @author luismoura
 * 
 */
public enum EmergenciaParametrosColunas {

	VLR_DATA("vlrData"), //
	VLR_NUMERICO("vlrNumerico"), //
	VLRTEXTO("vlrTexto"), //
	ROTINA_CONSISTENCIA("rotinaConsistencia"), //
	EXEMPLO_USO("exemploUso"), //
	TIPO_DADO("tipoDado"), //
	VLR_DATA_PADRAO("vlrDataPadrao"), //
	VLR_NUMERICO_PADRAO("vlrNumericoPadrao"), //
	VLR_TEXTO_PADRAO("vlrTextoPadrao"), //
	;

	private EmergenciaParametrosColunas(String nome) {
		this.nome = nome;
	}

	private String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return this.getNome();
	}
}
