package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do listagem de tipos de situação de
 * leito.
 * 
 * @author david.laks
 */

public class TiposSituacaoLeitoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5505971972985192086L;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AinAcomodacoes> dataModel;	
	
	private AinTiposMovimentoLeito tipoMovimentoLeito;
	
	private AinTiposMovimentoLeito tipoMovimentoLeitoSelecionado;
	
	private final String PAGE_CADASTRAR_TIPO_SITUACAO_LEITO = "tiposSituacaoLeitoCRUD";
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		tipoMovimentoLeito = new AinTiposMovimentoLeito();
	}
	
	//ordenar por codigo
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.setTipoMovimentoLeito(new AinTiposMovimentoLeito());
		this.dataModel.limparPesquisa();
	}
	
	public String editar() {
		return PAGE_CADASTRAR_TIPO_SITUACAO_LEITO;
	}
	
	public void excluir() {
		try {
			if (this.getTipoMovimentoLeitoSelecionado() != null) {
				this.cadastrosBasicosInternacaoFacade.removerTipoSituacaoLeito(tipoMovimentoLeitoSelecionado.getCodigo());
				apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_REMOCAO_TIPO_SITUACAO_LEITO",	tipoMovimentoLeitoSelecionado.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_ERRO_REMOCAO_TIPO_SITUACAO_LEITO_INVALIDA");
			}
			this.setTipoMovimentoLeitoSelecionado(null);
		}  catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Long count = cadastrosBasicosInternacaoFacade.pesquisaTipoSituacaoLeitoCount(
				this.getTipoMovimentoLeito().getCodigo(),
				this.getTipoMovimentoLeito().getDescricao(),
				this.getTipoMovimentoLeito().getGrupoMvtoLeito(),
				this.getTipoMovimentoLeito().getIndNecessitaLimpeza(),
				this.getTipoMovimentoLeito().getIndExigeJustificativa(),
				this.getTipoMovimentoLeito().getIndBloqueioPaciente(),
				this.getTipoMovimentoLeito().getIndExigeJustLiberacao());

		return count;
	}

	@Override
	public List<AinTiposMovimentoLeito> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		List<AinTiposMovimentoLeito> result = cadastrosBasicosInternacaoFacade.pesquisaTipoSituacaoLeito(
				firstResult, maxResults, orderProperty, asc,
				this.getTipoMovimentoLeito().getCodigo(),
				this.getTipoMovimentoLeito().getDescricao(),
				this.getTipoMovimentoLeito().getGrupoMvtoLeito(),
				this.getTipoMovimentoLeito().getIndNecessitaLimpeza(),
				this.getTipoMovimentoLeito().getIndExigeJustificativa(),
				this.getTipoMovimentoLeito().getIndBloqueioPaciente(),
				this.getTipoMovimentoLeito().getIndExigeJustLiberacao());

		if (result == null) {
			result = new ArrayList<AinTiposMovimentoLeito>();
		}

		return result;
	}

	public AinTiposMovimentoLeito getTipoMovimentoLeito() {
		return tipoMovimentoLeito;
	}

	public void setTipoMovimentoLeito(AinTiposMovimentoLeito tipoMovimentoLeito) {
		this.tipoMovimentoLeito = tipoMovimentoLeito;
	}

	public AinTiposMovimentoLeito getTipoMovimentoLeitoSelecionado() {
		return tipoMovimentoLeitoSelecionado;
	}

	public void setTipoMovimentoLeitoSelecionado(AinTiposMovimentoLeito tipoMovimentoLeitoSelecionado) {
		this.tipoMovimentoLeitoSelecionado = tipoMovimentoLeitoSelecionado;
	}

	public DynamicDataModel<AinAcomodacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinAcomodacoes> dataModel) {
		this.dataModel = dataModel;
	}

	
}
