package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de observação alta
 * paciente.
 * 
 * @author david.laks
 */

public class ObservacoesPacAltaController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8332080868930075362L;

	private enum ObservacoesPacAltaControllerExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_OBSERVACOESPACALTA
	}

	private final String PAGE_PESQUISAR_OBSERVACOES_PAC_ALTA = "observacoesPacAltaList";
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Observação alta paciente a ser criada/editada
	 */
	private AinObservacoesPacAlta ainObservacoesPacAlta;

	@PostConstruct
	public void init() {
		begin(conversation);
		ainObservacoesPacAlta=new AinObservacoesPacAlta();
	}
	
	
	public String confirmar() {
		boolean update = getAinObservacoesPacAlta().getCodigo()!=null;
		try {
			
			this.cadastrosBasicosInternacaoFacade.persistirObservacoesPacAlta(this.ainObservacoesPacAlta);

			if (!update) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_OBSERVACOESPACALTA", this.ainObservacoesPacAlta.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_OBSERVACOESPACALTA", this.ainObservacoesPacAlta.getDescricao());
			}
			
			return cancelar();
		} 
		catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (Exception e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(ObservacoesPacAltaControllerExceptionCode.ERRO_PERSISTIR_OBSERVACOESPACALTA));
			return null;
		}
	} 

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Observação alta paciente
	 */
	public String cancelar() {
		ainObservacoesPacAlta = new AinObservacoesPacAlta();
		return PAGE_PESQUISAR_OBSERVACOES_PAC_ALTA;
	}
	

	// ### GETs e SETs ###

	public AinObservacoesPacAlta getAinObservacoesPacAlta() {
		return ainObservacoesPacAlta;
	}

	public void setAinObservacoesPacAlta(AinObservacoesPacAlta ainObservacoesPacAlta) {
		this.ainObservacoesPacAlta = ainObservacoesPacAlta;
	}

}
