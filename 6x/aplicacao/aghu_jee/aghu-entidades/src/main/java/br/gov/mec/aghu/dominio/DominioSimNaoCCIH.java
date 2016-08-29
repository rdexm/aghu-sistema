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
public enum DominioSimNaoCCIH implements Dominio {
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

	public static DominioSimNaoCCIH getInstance(boolean valor) {
		if (valor) {
			return DominioSimNaoCCIH.S;
		} else {
			return DominioSimNaoCCIH.N;
		}
	}
	
	public static Boolean getBooleanInstance(DominioSimNaoCCIH valor){
		return valor != null ? valor.isSim() : null;
	}
	
}
