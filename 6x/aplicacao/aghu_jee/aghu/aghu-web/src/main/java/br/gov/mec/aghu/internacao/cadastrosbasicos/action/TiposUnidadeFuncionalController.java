package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


/**
 * Classe responsável por controlar as ações do criação e edição de
 * tipo de unidade funcional.
 */



public class TiposUnidadeFuncionalController  extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1408451175566950412L;

	/*private enum TiposUnidadeFuncionalControllerExceptionCode implements NegocioExceptionCode {
		ERRO_PERSISTIR_TIPOSUNIDADEFUNCIONAL
	}*/

	

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Tipo de unidade funcional a ser criada/editada
	 */
	private AghTiposUnidadeFuncional aghTiposUnidadeFuncional;
	
	private final String PAGE_LIST_TIPO_UNIDADE = "tiposUnidadeFuncionalList";

	
	
	@PostConstruct
	public void init() {
		this.setAghTiposUnidadeFuncional(new AghTiposUnidadeFuncional());
	}
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * tipo de unidade funcional
	 */
	public String confirmar() {
		//reiniciarPaginator(TiposUnidadeFuncionalPaginatorController.class);
		
		try {
			boolean create = this.aghTiposUnidadeFuncional.getCodigo() == null;

			this.cadastrosBasicosInternacaoFacade
					.persistirTiposUnidadeFuncional(this.aghTiposUnidadeFuncional);

			if (create) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_TIPOUNIDADEFUNCIONAL",
						this.aghTiposUnidadeFuncional.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_TIPOUNIDADEFUNCIONAL",
						this.aghTiposUnidadeFuncional.getDescricao());
			}
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}


		return cancelar();
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * tipo de unidade funcional
	 */
	public String cancelar() {		
		this.setAghTiposUnidadeFuncional(new AghTiposUnidadeFuncional());
		return PAGE_LIST_TIPO_UNIDADE;
	}

	// ### GETs e SETs ###

	

	public AghTiposUnidadeFuncional getAghTiposUnidadeFuncional() {
		return aghTiposUnidadeFuncional;
	}

	public void setAghTiposUnidadeFuncional(
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional) {
		this.aghTiposUnidadeFuncional = aghTiposUnidadeFuncional;
	}

}
