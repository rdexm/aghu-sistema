package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioMascaraData implements Dominio {

	DIA_MES_ANO("dd/MM/yyyy", "99/99/9999"), MES_ANO("MM/yyyy", "99/9999"), ANO("yyyy","9999");
	private String	exibicao;
	private String	mascara;

	DominioMascaraData(final String exibicao, final String mascara) {
		this.exibicao = exibicao;
		this.mascara = mascara;
	}

	/**
	 * Retorna o código da enumeração solicitada.
	 */
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	/**
	 * Retorna a descrição da enumeração solicitada.
	 */
	@Override
	public String getDescricao() {
		return getExibicao();
	}
	
	public static DominioMascaraData valorPorMascara(final String mascara) {
		if(mascara == null){
			return null;
		}
		for(DominioMascaraData dominioMascaraData: DominioMascaraData.values()){
			if(mascara.equals(dominioMascaraData.mascara)) {
				return dominioMascaraData;
			}
		}
		return null;
	}
	
	public static DominioMascaraData valorPorMascaraExibicaoIgnoraCase(final String mascara) {
		if(mascara == null){
			return null;
		}
		for(DominioMascaraData dominioMascaraData: DominioMascaraData.values()){
			if(mascara.equalsIgnoreCase(dominioMascaraData.exibicao)) {
				return dominioMascaraData;
			}
		}
		return null;
	}

	public void setExibicao(String exibicao) {
		this.exibicao = exibicao;
	}

	public String getExibicao() {
		return exibicao;
	}

	public void setMascara(String mascara) {
		this.mascara = mascara;
	}

	public String getMascara() {
		return mascara;
	}
}
