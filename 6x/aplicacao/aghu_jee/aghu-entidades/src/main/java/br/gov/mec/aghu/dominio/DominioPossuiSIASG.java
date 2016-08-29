package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioPossuiSIASG implements Dominio {

		/**
		 * Pendente
		 */	     
		 P, 
		 /**
		 * Existente
		 */
		 E; 
	 
		@Override
		public int getCodigo() {
			return this.ordinal();
		}
		
		@Override
		public String getDescricao(){
			switch (this) {
			case P:
				return "Pendente";

			case E:
				return "Existente";				
				
			default:
				return "";
			}
		}
}
