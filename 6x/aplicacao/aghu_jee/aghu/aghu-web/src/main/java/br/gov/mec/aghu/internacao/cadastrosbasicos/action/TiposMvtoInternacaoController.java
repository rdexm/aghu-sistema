package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinTiposMvtoInternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de
 * tipo de movimento de internação.
 */


public class TiposMvtoInternacaoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2068739199127947790L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Tipo de movimento de internação a ser criada/editada
	 */
	private AinTiposMvtoInternacao ainTiposMvtoInternacao;
	
	private final String PAGE_PESQUISAR_TIPO_MVTO_INTERNACAO = "tiposMvtoInternacaoList";

	@PostConstruct
	public void init() {
		begin(conversation);
		ainTiposMvtoInternacao =new AinTiposMvtoInternacao();
	}
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * tipo de movimento de internação
	 */
	public String confirmar() {
		
		try {
			boolean update = this.ainTiposMvtoInternacao.getCodigo() != null;

			this.cadastrosBasicosInternacaoFacade
					.persistirTiposMvtoInternacao(this.ainTiposMvtoInternacao);
			if (!update) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_TIPOSMVTOINTERNACAO",
						this.ainTiposMvtoInternacao.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_TIPOSMVTOINTERNACAO",
						this.ainTiposMvtoInternacao.getDescricao());
			}
			ainTiposMvtoInternacao = new AinTiposMvtoInternacao();
			return PAGE_PESQUISAR_TIPO_MVTO_INTERNACAO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return "erro";
		}
	}

	public String cancelar() {
		ainTiposMvtoInternacao = new AinTiposMvtoInternacao();
		return PAGE_PESQUISAR_TIPO_MVTO_INTERNACAO;
	}
	
	
	
	// ### GETs e SETs ###


	public AinTiposMvtoInternacao getAinTiposMvtoInternacao() {
		return ainTiposMvtoInternacao;
	}

	public void setAinTiposMvtoInternacao(
			AinTiposMvtoInternacao ainTiposMvtoInternacao) {
		this.ainTiposMvtoInternacao = ainTiposMvtoInternacao;
	}

	}
