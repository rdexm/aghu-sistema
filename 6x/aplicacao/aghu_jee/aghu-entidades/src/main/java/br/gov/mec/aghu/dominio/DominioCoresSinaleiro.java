package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCoresSinaleiro implements Dominio {
	VERMELHO, AMARELO, VERDE;
	//NÃO ALTERAR A ORDEM DAS CORES, porque irá alterar o getOrdinal
	
	/**
	 * A ordem das cores é importante para a apresentação 
	 dos registros na tela, estória #5344 - Tratar Ocorrências
	 */

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case VERMELHO:
			return "vermelho";
		case AMARELO:
			return "amarelo";
		case VERDE:
			return "verde";
		default:
			return "";
		}
	}
	
	
	public String getLegenda() {
		switch (this) {
		case VERMELHO:
			return "Não dispensado.";
		case AMARELO:
			return "Dispensado parcialmente.";
		case VERDE:
			return "Totalmente dispensado";
		default:
			return "";
		}
	}

}
