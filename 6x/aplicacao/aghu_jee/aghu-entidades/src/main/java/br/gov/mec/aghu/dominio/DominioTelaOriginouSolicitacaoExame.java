package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para mapear onde se originou a chamada da solicitação do exame. 
 * 
 */
public enum DominioTelaOriginouSolicitacaoExame implements Dominio {
	
	TELA_PESQUISA_SOLICITACAO_EXAME("voltaParaPadrao"),
	TELA_ATENDIMENTO_EXTERNO("atendimentoExternoCRUD"),
	TELA_PACIENTES_INTERNADOS("prescricaomedica-pesquisarListaPacientesInternados"),
	TELA_SUMARIO_ALTA("prescricaomedica-manterSumarioAlta"),
	TELA_AMBULATORIO("atenderPacientesAgendados"),
	TELA_ATENDIMENTOS_DIVERSOS("exames-atendimentoDiversoList"),
	TELA_PESQUISAR_EXAMES("exames-solicitacaoExameList"),
	TELA_BLOCO_CIRURGICO("blococirurgico-listaCirurgias");
	
	private String retorno;
	
	
	private DominioTelaOriginouSolicitacaoExame(String retorno) {
		this.retorno = retorno;
	}
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		return retorno;
	}
	
	public String getRetorno() {
		return retorno;
	}
	
	public static DominioTelaOriginouSolicitacaoExame obterPorRetorno(String retorno) {
		for(DominioTelaOriginouSolicitacaoExame tela : values()) {
			if(tela.getRetorno().equals(retorno)) {
				return tela;
			}
		}
		
		return null;
	}
	
}
