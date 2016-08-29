package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioCoresPacientesTriagem implements Dominio {
	CINZA, ROXO, BRANCO, AMARELO, MARROM, AZUL, VERMELHO;
	//NÃO ALTERAR A ORDEM DAS CORES, porque irá alterar o getOrdinal
	
	/**
	 * A ordem das cores é importante para a apresentação 
	 dos registros na tela, estória #5299 - Pesquisar Pacientes para Triagem e Dispensação
	 */
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case CINZA:
			return "#e6e6e6 !important";
		case AMARELO:
			return "#fcf2b8 !important";
		case VERMELHO:
			return "#ff9999 !important";
		case AZUL:
			return "#d1e1ff !important";
		case ROXO:
			return "#ded1f1 !important";
		case BRANCO:
			return "#ffffff !important";
		case MARROM:
			return "#ead0bb !important";
		default:
			return "";
		}
	}
	
	public String getLegenda(){
		switch (this) {
		case CINZA:
			return "Prescrição Médica por Triar";
		case AMARELO:
			return "Prescrição Médica com Alterações";
		case VERMELHO:
			return "Paciente com Alta";
		case AZUL:
			return "Prescrição Médica Pronta";
		case ROXO:
			return "Prescrição Médica Triada a Dispensar";
		case BRANCO:
			return "Prescrição Médica Triada com Pendências";
		case MARROM:
			return "Prescrição Médica Dispensada com Ocorrências";
		default:
			return "";
		}
	}

}
