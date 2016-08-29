package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



public enum DominioPeriodoRegistroControlePaciente implements Dominio {
	
	HORA1,
	HORAS6,
	HORAS12,
	HORAS24,
	HORAS48,
	DIAS7,
	DIAS15;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case HORA1:
			return "Última 1 hora";
		case HORAS6:
			return "Últimas 6 horas";
		case HORAS12:
			return "Últimas 12 horas";
		case HORAS24:
			return "Últimas 24 horas";
		case HORAS48:
			return "Últimas 48 horas";		
		case DIAS7:
			return "Últimos 7 dias";		
		case DIAS15:
			return "Últimos 15 dias";		
		default:
			return "";
		}
	}
	
	public long getValorEmMilisegundos() {
		switch (this) {
		case HORA1:
			return 3600000; //60*60*1000
		case HORAS6:
			return 21600000; //6*60*60*1000
		case HORAS12:
			return 43200000; //12*60*60*1000;
		case HORAS24:
			return 86400000; //24*60*60*1000;
		case HORAS48:
			return 172800000; //2*24*60*60*1000;		
		case DIAS7:
			return 604800000; //7*24*60*60*1000;		
		case DIAS15:
			return 1296000000 ; //15*24*60*60*1000;		
		default:
			return 0;
		}
	}
	
	public int getValorEmDias() {
		switch (this) {
		case HORAS24:
			return 1; 
		case HORAS48:
			return 2; 		
		case DIAS7:
			return 7; 		
		case DIAS15:
			return 15;		
		default:
			return 0;
		}		
	}

}
