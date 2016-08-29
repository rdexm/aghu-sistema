package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOperacaoAghControleExecucao implements Dominio {
	
	ENCERRAMENTO_CONTAS_AUTOMATICO;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
			case ENCERRAMENTO_CONTAS_AUTOMATICO: return "Encerramento Automático de Contas Hospitalares";
			default: return null;
		}
	}
}