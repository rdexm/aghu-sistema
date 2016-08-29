package br.gov.mec.aghu.registrocolaborador.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CentroCustoAtuacaoController extends ActionController {

	private static final long serialVersionUID = -7271924139684909555L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private RapServidores servidor = new RapServidores();

	private static final String PESQUISAR_CENTRO_CUSTO_ATUACAO = "pesquisarCentroCustoAtuacao";
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String inicio() {
		if(servidor.getId() != null){
			servidor = registroColaboradorFacade.obterServidor(servidor.getId());

			if(servidor == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		}
		
		return null;
	}

	public String salvar() {
		try {
			registroColaboradorFacade.salvar(servidor);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CENTRO_CUSTO_ALTERADO_SUCESSO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		return cancelar();
	}

	private String cancelar() {
		servidor = null;
		return PESQUISAR_CENTRO_CUSTO_ATUACAO;
	}
	
	public List<FccCentroCustos> pesquisarCentroCustos(String param){
		return registroColaboradorFacade.pesquisarCentroCustosAtivosOrdemDescricao(param);
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidor() {
		return servidor;
	}
}