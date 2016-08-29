package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class TipoInformacoesController extends ActionController {

	private static final long serialVersionUID = 6166836168733273443L;
	
	private static final String PESQUISAR_TIPO_INFORMACOES = "pesquisarTipoInformacoes";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapTipoInformacoes rapTipoInformacao;
	private boolean alteracao;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		try {
			if (rapTipoInformacao != null && rapTipoInformacao.getSeq() != null) {
				rapTipoInformacao = cadastrosBasicosFacade.obterTipoInformacoes(rapTipoInformacao.getSeq());
				alteracao = true;

				if(rapTipoInformacao == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}
			} else {
				rapTipoInformacao = new RapTipoInformacoes();
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	
	}
	
	public String salvar() {
		try {

			cadastrosBasicosFacade.persistirTipoInformacoes(rapTipoInformacao);
			
			if (alteracao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZACAO_TIPO_INFORMACAO");
				
			} else{
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TIPO_INFORMACAO");
			}
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	
	// Força refazer a pesquisa, para não ficar os valores modificados nesta edição (que foi cancelada)
	public String cancelar() {
		rapTipoInformacao = null;
		return PESQUISAR_TIPO_INFORMACOES;
	}

	public RapTipoInformacoes getRapTipoInformacao() {
		return rapTipoInformacao;
	}

	public void setRapTipoInformacao(RapTipoInformacoes rapTipoInformacao) {
		this.rapTipoInformacao = rapTipoInformacao;
	}

	public boolean isAlteracao() {
		return alteracao;
	}

	public void setAlteracao(boolean alteracao) {
		this.alteracao = alteracao;
	}
}