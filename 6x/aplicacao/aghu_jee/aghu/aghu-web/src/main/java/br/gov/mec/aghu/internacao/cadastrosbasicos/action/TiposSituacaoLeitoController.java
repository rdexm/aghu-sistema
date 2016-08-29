package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de tipos de situação de leito.
 * 
 * @author david.laks
 */

public class TiposSituacaoLeitoController  extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6392715223731968405L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private final String PAGE_PESQUISAR_TIPO_SITUACAO_LEITO = "tiposSituacaoLeitoList";

	/**
	 * Tipo de situação de leito a ser criada/editada
	 */
	private AinTiposMovimentoLeito tipoMovimentoLeito;
	
	private boolean update;

	
	@PostConstruct
	public void init() {
		begin(conversation);
		tipoMovimentoLeito = new AinTiposMovimentoLeito();
	}
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * tipos de situação de leito
	 */
	
	public String confirmar() {
		try {
			if (!update) {
				this.cadastrosBasicosInternacaoFacade.criarTipoSituacaoLeito(this.tipoMovimentoLeito);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_TIPO_SITUACAO_LEITO",this.getTipoMovimentoLeito().getDescricao());
			} else {
				this.cadastrosBasicosInternacaoFacade.alterarTipoSituacaoLeito(this.tipoMovimentoLeito);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EDICAO_TIPO_SITUACAO_LEITO",this.getTipoMovimentoLeito().getDescricao());
			}
			tipoMovimentoLeito=new AinTiposMovimentoLeito();
			update = false;
			return PAGE_PESQUISAR_TIPO_SITUACAO_LEITO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	} 

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * tipos de situação de leito
	 */
	public String cancelar() {
		tipoMovimentoLeito=new AinTiposMovimentoLeito();
		update = false;
		return PAGE_PESQUISAR_TIPO_SITUACAO_LEITO;
	}
	
	
	public AinTiposMovimentoLeito getTipoMovimentoLeito() {
		return tipoMovimentoLeito;
	}
	public void setTipoMovimentoLeito(AinTiposMovimentoLeito tipoMovimentoLeito) {
		this.tipoMovimentoLeito = tipoMovimentoLeito;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}



}
