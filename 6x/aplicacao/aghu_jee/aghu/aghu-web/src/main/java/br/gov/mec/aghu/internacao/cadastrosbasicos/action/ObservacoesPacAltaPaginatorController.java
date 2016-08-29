package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de observações de alta do paciente.
 */


public class ObservacoesPacAltaPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 863158441866537875L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject @Paginator
	private DynamicDataModel<AinObservacoesPacAlta> dataModel;	
	
	private AinObservacoesPacAlta observacoesPacAlta;
	
	private AinObservacoesPacAlta observacoesPacAltaSelecionada;
	
	private final String PAGE_CADASTRAR_OBSERVACOES_PAC_ALTA = "observacoesPacAltaCRUD";
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		observacoesPacAlta = new AinObservacoesPacAlta();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.setObservacoesPacAlta(new AinObservacoesPacAlta());
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Observação alta paciente.
	 */
	public void excluir() {
			try {
			if (this.getObservacoesPacAltaSelecionada() != null) {
				this.cadastrosBasicosInternacaoFacade.removerObservacoesPacAlta(observacoesPacAltaSelecionada.getCodigo());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_OBSERVACOESPACALTA",
						observacoesPacAlta.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_OBSERVACOESPACALTA_INVALIDA");
			}
			this.setObservacoesPacAltaSelecionada(null);
		}  catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Long count = cadastrosBasicosInternacaoFacade.pesquisaObservacoesPacAltaCount(
				this.getObservacoesPacAlta().getCodigo(),
				this.getObservacoesPacAlta().getDescricao(),
				this.getObservacoesPacAlta().getIndSituacao());

		return count;
	}

	@Override
	public List<AinObservacoesPacAlta> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<AinObservacoesPacAlta> result = this.cadastrosBasicosInternacaoFacade
				.pesquisaObservacoesPacAlta(firstResult, maxResults, orderProperty,
						asc, this.getObservacoesPacAlta().getCodigo(),
						this.getObservacoesPacAlta().getDescricao(),
						this.getObservacoesPacAlta().getIndSituacao());

		if (result == null) {
			result = new ArrayList<AinObservacoesPacAlta>();
		}

		return result;
	}

	public String editar() {
		return PAGE_CADASTRAR_OBSERVACOES_PAC_ALTA;
	}
	public AinObservacoesPacAlta getObservacoesPacAlta() {
		return observacoesPacAlta;
	}

	public void setObservacoesPacAlta(AinObservacoesPacAlta observacoesPacAlta) {
		this.observacoesPacAlta = observacoesPacAlta;
	}

	public AinObservacoesPacAlta getObservacoesPacAltaSelecionada() {
		return observacoesPacAltaSelecionada;
	}

	public void setObservacoesPacAltaSelecionada(AinObservacoesPacAlta observacoesPacAltaSelecionada) {
		this.observacoesPacAltaSelecionada = observacoesPacAltaSelecionada;
	}

	public DynamicDataModel<AinObservacoesPacAlta> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinObservacoesPacAlta> dataModel) {
		this.dataModel = dataModel;
	}

	
}
