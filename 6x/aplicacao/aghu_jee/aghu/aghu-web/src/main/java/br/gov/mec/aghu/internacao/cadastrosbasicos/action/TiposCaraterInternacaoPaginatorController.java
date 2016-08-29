package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de tipos de caráter de internação.
 */


public class TiposCaraterInternacaoPaginatorController extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -5134206360040747148L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AinTiposCaraterInternacao> dataModel;	

	
	private AinTiposCaraterInternacao tiposCaraterInternacao;
	
	private AinTiposCaraterInternacao tiposCaraterInternacaoSelecionado;
	
	private final String PAGE_CADASTRAR_TIPO_CARATER_INTERNACAO = "tiposCaraterInternacaoCRUD";
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		tiposCaraterInternacao = new AinTiposCaraterInternacao();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.setTiposCaraterInternacao(new AinTiposCaraterInternacao());
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Tipo de caráter de internação.
	 */
	public void excluir() {
		try {
			if (tiposCaraterInternacao != null) {
				this.cadastrosBasicosInternacaoFacade.removerTiposCaraterInternacao(tiposCaraterInternacaoSelecionado.getCodigo());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_TIPOSCARATERINTERNACAO",
						tiposCaraterInternacaoSelecionado.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_TIPOSCARATERINTERNACAO_INVALIDA");
			}

			this.setTiposCaraterInternacaoSelecionado(null);
		}  catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		Long count = cadastrosBasicosInternacaoFacade.pesquisaTiposCaraterInternacaoCount(
				this.getTiposCaraterInternacao().getCodigo(),
				this.getTiposCaraterInternacao().getDescricao(),
				this.getTiposCaraterInternacao().getCodSus(),
				this.getTiposCaraterInternacao().getIndCaraterCidSec());
		return count;
	}

	@Override
	public List<AinTiposCaraterInternacao> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<AinTiposCaraterInternacao> result = this.cadastrosBasicosInternacaoFacade
				.pesquisaTiposCaraterInternacao(firstResult, maxResults, orderProperty,
						asc, this.getTiposCaraterInternacao().getCodigo(),
						this.getTiposCaraterInternacao().getDescricao(),
						this.getTiposCaraterInternacao().getCodSus(),
						this.getTiposCaraterInternacao().getIndCaraterCidSec());

		if (result == null) {
			result = new ArrayList<AinTiposCaraterInternacao>();
		}

		return result;
	}
	
	public String editar() {
		return PAGE_CADASTRAR_TIPO_CARATER_INTERNACAO;
	}

	public DynamicDataModel<AinTiposCaraterInternacao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinTiposCaraterInternacao> dataModel) {
		this.dataModel = dataModel;
	}

	public AinTiposCaraterInternacao getTiposCaraterInternacao() {
		return tiposCaraterInternacao;
	}

	public void setTiposCaraterInternacao(AinTiposCaraterInternacao tiposCaraterInternacao) {
		this.tiposCaraterInternacao = tiposCaraterInternacao;
	}

	public AinTiposCaraterInternacao getTiposCaraterInternacaoSelecionado() {
		return tiposCaraterInternacaoSelecionado;
	}

	public void setTiposCaraterInternacaoSelecionado(AinTiposCaraterInternacao tiposCaraterInternacaoSelecionado) {
		this.tiposCaraterInternacaoSelecionado = tiposCaraterInternacaoSelecionado;
	}


}
