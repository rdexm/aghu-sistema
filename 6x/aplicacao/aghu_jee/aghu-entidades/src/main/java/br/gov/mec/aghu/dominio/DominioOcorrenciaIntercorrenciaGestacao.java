package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioOcorrenciaIntercorrenciaGestacao implements Dominio {

		A,

		P;

		@Override
		public int getCodigo() {
			return this.ordinal();
		}

		@Override
		public String getDescricao() {
			switch (this) {
			case A:
				return "A";
			case P:
				return "P";
			default:
				return "";
			}
		}
}