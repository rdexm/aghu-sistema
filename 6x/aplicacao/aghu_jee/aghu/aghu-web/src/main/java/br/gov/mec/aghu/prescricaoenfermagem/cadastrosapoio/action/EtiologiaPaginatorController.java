package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class EtiologiaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8377124220277489320L;
	
	private static final String PAGE_ETIOLOGIA_CRUD = "etiologiaCRUD";

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;
	
	@Inject @Paginator
	private DynamicDataModel<EpeFatRelacionado> dataModel;

	private Short seqEtiologia;

	private String descricaoEtiologia;

	private Boolean exibirBotaoIncluirEtiologia;
	
	private EpeFatRelacionado etiologiaSelecionado;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciarInclusao(){
		return PAGE_ETIOLOGIA_CRUD;
	}
	
	public String editar(){
		return PAGE_ETIOLOGIA_CRUD;
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		exibirBotaoIncluirEtiologia = true;
	}

	public void limparPesquisa() {
		this.seqEtiologia = null;
		this.descricaoEtiologia = null;
		this.exibirBotaoIncluirEtiologia = false;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	@Override
	public List<EpeFatRelacionado> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<EpeFatRelacionado> result = prescricaoEnfermagemApoioFacade.pesquisarEtiologiasPorSeqDescricao(firstResult,
				maxResult, orderProperty, asc, seqEtiologia, descricaoEtiologia);

		return result;
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemApoioFacade.pesquisarEtiologiasPorSeqDescricaoCount(seqEtiologia, descricaoEtiologia);
	}

	public void excluir() {
		dataModel.reiniciarPaginator();
		try {
			if (etiologiaSelecionado != null) {
				this.prescricaoEnfermagemApoioFacade.removerEtiologia(etiologiaSelecionado.getSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_ETIOLOGIA", etiologiaSelecionado.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_ETIOLOGIA_INVALIDA");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// ### Getters e Setters ###

	public Short getSeqEtiologia() {
		return seqEtiologia;
	}

	public void setSeqEtiologia(Short seqEtiologia) {
		this.seqEtiologia = seqEtiologia;
	}

	public String getDescricaoEtiologia() {
		return descricaoEtiologia;
	}

	public void setDescricaoEtiologia(String descricaoEtiologia) {
		this.descricaoEtiologia = descricaoEtiologia;
	}

	public Boolean getExibirBotaoIncluirEtiologia() {
		return exibirBotaoIncluirEtiologia;
	}

	public void setExibirBotaoIncluirEtiologia(Boolean exibirBotaoIncluirEtiologia) {
		this.exibirBotaoIncluirEtiologia = exibirBotaoIncluirEtiologia;
	}

	public DynamicDataModel<EpeFatRelacionado> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeFatRelacionado> dataModel) {
		this.dataModel = dataModel;
	}

	public EpeFatRelacionado getEtiologiaSelecionado() {
		return etiologiaSelecionado;
	}

	public void setEtiologiaSelecionado(EpeFatRelacionado etiologiaSelecionado) {
		this.etiologiaSelecionado = etiologiaSelecionado;
	}
}
