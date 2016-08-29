package br.gov.mec.aghu.exames.pesquisa.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelMetodo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterMetodoController extends ActionController {

	private static final long serialVersionUID = 4117168355378439712L;

	private static final String PESQUISA_METODO = "pesquisaMetodo";

	@EJB
	private IExamesFacade examesFacade;

	//Variáveis para controle de edição
	private AelMetodo metodo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(metodo != null && metodo.getSeq() != null) {
			metodo = examesFacade.obterAelMetodoId(metodo.getSeq());
			
			if(metodo == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			metodo = new AelMetodo();
			metodo.setSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}

	public String gravar() {
		try {
			examesFacade.inserirMetodo(metodo);

			//Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_METODO", metodo.getDescricao());
			
			return cancelar();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		metodo = null;
		return PESQUISA_METODO;
	}

	public AelMetodo getMetodo() {
		return metodo;
	}
	public void setMetodo(AelMetodo metodo) {
		this.metodo = metodo;
	}
}