package br.gov.mec.aghu.ambulatorio.action;


import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de grupo para a condição de atendimento.
 */
public class ManterCondicaoAtendimentoController extends ActionController {

	private static final long serialVersionUID = -2493364795992419928L;

	private static final String PAGE_MANTER_CONDICAO_ATENDIMENTO_LIST = "manterCondicaoAtendimentoList";
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private AacCondicaoAtendimento condicaoAtendimento;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

	 

		if(condicaoAtendimento == null){
			condicaoAtendimento = new AacCondicaoAtendimento();
		}
	
	}
	
	
	public String confirmar() {
		try {
			boolean create = this.condicaoAtendimento.getSeq() == null;
			
			if (create){
				this.ambulatorioFacade.persistirCondicaoAtendimento(condicaoAtendimento);
				
			} else {
				this.ambulatorioFacade.atualizarCondicaoAtendimento(condicaoAtendimento);
			}
			
			if (create) {
				apresentarMsgNegocio(Severity.INFO, "MSG_CONDICAO_ATENDIMENTO_GRAVADO_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MSG_CONDICAO_ATENDIMENTO_ALTERADO_SUCESSO");
			}
			
			return cancelar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null; 
		}
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro da condição de atendimento.
	 */
	public String cancelar() {
		this.condicaoAtendimento = new AacCondicaoAtendimento();
		return PAGE_MANTER_CONDICAO_ATENDIMENTO_LIST;
	}
	
	public AacCondicaoAtendimento getCondicaoAtendimento() {
		return condicaoAtendimento;
	}

	public void setCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) {
		this.condicaoAtendimento = condicaoAtendimento;
	}
}
