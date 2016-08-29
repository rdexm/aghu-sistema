package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que o tipo de pesquisa do lote na pesquisa de solicitação de exames em lote.
 * 
 * @author Filipe Hoffmeister
 * 
 */
public enum DominioSolicitacaoExameLote implements Dominio {
		/**
		 * Unidades
		 **/
		U,
		 /**
		 * Grupos
		 **/
		G,
		/**
		 * eSPECIALIDADES
		 **/
		E
		;

		@Override
		public int getCodigo() {
			return this.ordinal();
		}

		@Override
		public String getDescricao() {
			switch (this) {
			case U:
				return "Unidade";
				
			case G:
				return "Grupo";

			case E:
				return "Especialidade";
			
			default:
				return "";
			}
		}


}
