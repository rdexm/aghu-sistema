package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinTiposMvtoInternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de tipo de movimento de internação.
 */


public class TiposMvtoInternacaoPaginatorController extends ActionController implements ActionPaginator {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7154666087595689651L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private AinTiposMvtoInternacao tipoMovimentoInternacao;
	
	private AinTiposMvtoInternacao tipoMovimentoInternacaoSelecionado;

	@Inject @Paginator
	private DynamicDataModel<AinTiposMvtoInternacao> dataModel;
	
	private final String PAGE_TIPO_MVTO_INTERNACAO_CRUD = "tiposMvtoInternacaoCRUD";

	@PostConstruct
	public void init() {
		begin(conversation, true);
		tipoMovimentoInternacao = new AinTiposMvtoInternacao();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.setTipoMovimentoInternacao(new AinTiposMvtoInternacao());
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Tipo de movimento de internação.
	 */
	public void excluir() {
		try{
			if(this.getTipoMovimentoInternacaoSelecionado()!=null){
				this.cadastrosBasicosInternacaoFacade.removerTiposMvtoInternacao(this.getTipoMovimentoInternacaoSelecionado().getCodigo());
						apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_TIPOSMVTOINTERNACAO",
						this.getTipoMovimentoInternacaoSelecionado().getDescricao());
			} else {
						apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_TIPOSMVTOINTERNACAO_INVALIDA");
			}
			this.setTipoMovimentoInternacaoSelecionado(null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
			
	}
	
	@Override
	public Long recuperarCount() {
		Long count = cadastrosBasicosInternacaoFacade.pesquisaTiposMvtoInternacaoCount(
				this.getTipoMovimentoInternacao().getCodigo(),
				this.getTipoMovimentoInternacao().getDescricao());

		return count;
	}

	@Override
	public List<AinTiposMvtoInternacao> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<AinTiposMvtoInternacao> result = this.cadastrosBasicosInternacaoFacade
				.pesquisaTiposMvtoInternacao(firstResult, maxResults, orderProperty,
						asc, this.getTipoMovimentoInternacao().getCodigo(),
						this.getTipoMovimentoInternacao().getDescricao());

		if (result == null) {
			result = new ArrayList<AinTiposMvtoInternacao>();
		}

		return result;
	}
	
	public String editar() {
		return PAGE_TIPO_MVTO_INTERNACAO_CRUD;
	}

	public AinTiposMvtoInternacao getTipoMovimentoInternacao() {
		return tipoMovimentoInternacao;
	}

	public void setTipoMovimentoInternacao(AinTiposMvtoInternacao tipoMovimentoInternacao) {
		this.tipoMovimentoInternacao = tipoMovimentoInternacao;
	}

	public AinTiposMvtoInternacao getTipoMovimentoInternacaoSelecionado() {
		return tipoMovimentoInternacaoSelecionado;
	}

	public void setTipoMovimentoInternacaoSelecionado(AinTiposMvtoInternacao tipoMovimentoInternacaoSelecionado) {
		this.tipoMovimentoInternacaoSelecionado = tipoMovimentoInternacaoSelecionado;
	}

	public DynamicDataModel<AinTiposMvtoInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinTiposMvtoInternacao> dataModel) {
		this.dataModel = dataModel;
	}

	
}
