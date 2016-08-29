package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * 
 * @author twickert
 *
 */

public enum DominioFontes implements Dominio {
	
	ARIAL("Arial")
	,HELVETICA("Helvetica")
	,SANS_SERIF("sans-serif")
	,COMIC_SANS_MS("Comic Sans MS")
	,CURSIVE("cursive")
	,COURIER_NEW("Courier New")
	,COURIER("Courier")
	,MONOSPACE("monospace")
	,GEORGIA("Georgia")
	,SERIF("serif")
	,LUCIDA_SANS_UNICODE("Lucida Sans Unicode")
	,LUCIDA_GRANDE("Lucida Grande") 
	,TAHOMA("Tahoma")
	,GENEVA("Geneva")
	,TIMES_NEW_ROMAN("Times New Roman")
	,TIMES("Times")
	,TREBUCHET_MS("Trebuchet MS")
	,VERDANA("Verdana")
	;

	private String descricao;
	
	DominioFontes(String descricao){
		this.descricao = descricao;
	}
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return descricao;
	}
	
}
