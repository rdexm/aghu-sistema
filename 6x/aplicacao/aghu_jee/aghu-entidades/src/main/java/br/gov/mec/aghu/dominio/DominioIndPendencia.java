package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndPendencia implements Dominio {

		 /**
		 * Item não utilizado
		 */
	     X, 
		 /**
		 * Pendente
		 */	     
		 P, 
		 /**
		 * Validado
		 */
		 V, 
		 /**
		 * Alteração não validada
		 */
		 A, 
		 /**
		 * Exclusão não validada
		 */
		 E, 
		 /**
		 * Rascunho
		 */
		 R, 
		 /**
		 * Excluído após validação
		 */
		 C;
	 
	 
		@Override
		public int getCodigo() {
			return this.ordinal();
		}
		
		public String getDescricaoBanco(){
			switch (this) {
			case X:
				return "ITEM NÃO UTILIZADO";
				
			case P:
				return "PENDENTE";

			case V:
				return "VALIDADO";				
				
			case A:
				return "ALTERAÇÃO NA VALIDADA";

			case E:
				return "EXCLUSÃO NÃO VALIDADA";
				
			case R:
				return "RASCUNHO";
				
			case C:	
				return "EXCLUÍDO APÓS VALIDAÇÃO";
				
			default:
				return "";
			}
		}

		@Override
		public String getDescricao() {
			switch (this) {
			case X:
				return "ITEM NÃO UTILIZADO";
				
			case P:
				return "PENDENTE";

			case V:
				return "VALIDADO";				
				
			case A:
				return "ALTERAÇÃO NA VALIDADA";

			case E:
				return "EXCLUSÃO NAO VALIDADA";
				
			case R:
				return "RASCUNHO";
				
			case C:	
				return "EXCLUÍDO APÓS VALIDAÇÃO";
				
			default:
				return "";
			}
		}

}
