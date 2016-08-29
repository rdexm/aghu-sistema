package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioDividaHospitalar implements Dominio {

		/**
		 * Pagamentos Realizados no Periodo
		 */
		PP, 
		/**
		 * Títulos Bloqueados por BO
		 */
		TB,
		/**
		 * Dívida do Hospital por Natureza de Despesas
		 */
		DN;

		@Override
		public int getCodigo() {
			return this.ordinal();
		}

		@Override
		public String getDescricao() {
			switch (this) {
			case PP:
				return "PP - Pagamentos Realizados no Período";
			case TB:
				return "TB - Títulos Bloqueados por BO";
			case DN:
				return "DN - Dívida do Hospital por Natureza de Despesas";
			default:
				return "";
			}
		}

}
