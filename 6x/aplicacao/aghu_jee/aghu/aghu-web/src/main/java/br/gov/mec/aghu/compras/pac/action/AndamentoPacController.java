package br.gov.mec.aghu.compras.pac.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller de Manutenção do Andamento do PAC
 * 
 * @author mlcruz
 */

public class AndamentoPacController extends ActionController {

	private static final long serialVersionUID = 7183122074355959168L;

	@EJB
	private IPacFacade pacFacade;

	@Inject
	private AndamentoPacPaginatorController andamentoPacPaginatorController;

	/** Andamento do PAC */
	private ScoAndamentoProcessoCompra andamento;

	/** Flag de Update */
	private Boolean isUpdate;

	@PostConstruct
	protected void inicializar() {		
		this.begin(conversation);
	}

	/**
	 * Obtem localizações.
	 * 
	 * @return Localizações.
	 */
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacoes(String filtro) {
		return pacFacade.pesquisarScoLocalizacaoProcesso(filtro, 100);
	}

	/**
	 * Salva andamento do PAC.
	 */
	public void salvar() {
		String message;
		andamento.setDtEntrada(new Date());

		if (Boolean.TRUE.equals(isUpdate)) {
			pacFacade.alterar(andamento);
			message = "MESSAGE_ALTERACAO_ANDAMENTO_PAC";
		} else {
			try {
				pacFacade.incluir(andamento);
				message = "MESSAGE_INCLUSAO_ANDAMENTO_PAC";
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return;
			}
		}

		apresentarMsgNegocio(Severity.INFO, message, andamento.getSeq().toString(), andamento.getLocalizacaoProcesso()
				.getDescricao());

		andamentoPacPaginatorController.pesquisa();
	}

	/**
	 * Cancela edição do andamento do PAC.
	 */
	public void cancelar() {
		andamentoPacPaginatorController.getDataModel().reiniciarPaginator();
	}

	// Getters/Setters

	public ScoAndamentoProcessoCompra getAndamento() {
		return andamento;
	}

	public void setAndamento(ScoAndamentoProcessoCompra andamento) {
		this.andamento = andamento;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
}
