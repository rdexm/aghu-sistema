package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para campos de tela com opção de sim ou não.
 * 
 * @author gmneto
 * 
 * OBS: Conforme definição do Checklist do Desenvolvedor (desde Abril/2010):
 * https://apus.hcpa.ufrgs.br/projects/aghu/wiki/CheckList_do_Desenvolvedor
 * NÃO mais utilizar esta enum para mapear colunas que usem somente estes dois
 * valores, 'Sim' e 'Não', e usar BooleanUserType no lugar.
 */
public enum DominioSimNao implements Dominio {
	/**
	 * Sim
	 */
	S,
	/**
	 * Não
	 */
	N;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case S:
			return "Sim";
		case N:
			return "Não";
		default:
			return "";
		}
	}

	/**
	 * Método criado para ajudar os mapeamentos sintéticos para boolean
	 * 
	 * @return
	 */
	public boolean isSim() {
		switch (this) {
		case S:
			return Boolean.TRUE;
		case N:
			return Boolean.FALSE;
		default:
			return Boolean.FALSE;
		}
	}

	public static DominioSimNao getInstance(boolean valor) {
		if (valor) {
			return DominioSimNao.S;
		} else {
			return DominioSimNao.N;
		}
	}
	
	public static Boolean getBooleanInstance(DominioSimNao valor){
		return valor != null ? valor.isSim() : null;
	}
	
}
