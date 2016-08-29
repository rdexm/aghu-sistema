package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoNecessidadesHumanasPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8377124220277489320L;
	
	private static final String PAGE_GRUPO_NECESSIDADES_HUMANAS_CRUD = "grupoNecessidadesHumanasCRUD";
	private static final String PAGE_SUG_GRUPO_NECESSIDADES_HUMANAS_LIST = "subGrupoNecessidadesHumanasList";

	@Inject @Paginator
	private DynamicDataModel<EpeGrupoNecesBasica> dataModel;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	private Short seqGrupoNecessidadesHumanas;

	private String descricaoGrupoNecessidadesHumanas;

	private DominioSituacao situacaoGrupoNecessidadesHumanas;

	private Short seqGrupoNecessidadesHumanasExclusao;

	private Boolean exibirBotaoIncluirGrupoNecessidadesHumanas;
	
	private EpeGrupoNecesBasica grupoNecessidadesHumanasSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {
	 

		if(!dataModel.getPesquisaAtiva()){
			situacaoGrupoNecessidadesHumanas = DominioSituacao.A;
		}
	
	}

	public String iniciarInclusao(){
		return PAGE_GRUPO_NECESSIDADES_HUMANAS_CRUD;
	}
	
	public String editar(){
		return PAGE_SUG_GRUPO_NECESSIDADES_HUMANAS_LIST;
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		exibirBotaoIncluirGrupoNecessidadesHumanas = true;
	}

	public void limparPesquisa() {
		seqGrupoNecessidadesHumanas = null;
		descricaoGrupoNecessidadesHumanas = null;
		situacaoGrupoNecessidadesHumanas = null;
		exibirBotaoIncluirGrupoNecessidadesHumanas = false;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		situacaoGrupoNecessidadesHumanas = DominioSituacao.A;
	}

	@Override
	public List<EpeGrupoNecesBasica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<EpeGrupoNecesBasica> result = prescricaoEnfermagemApoioFacade
				.pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacao(firstResult, maxResult, orderProperty, asc,
						seqGrupoNecessidadesHumanas, descricaoGrupoNecessidadesHumanas, situacaoGrupoNecessidadesHumanas);

		return result;
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemApoioFacade.pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacaoCount(
				seqGrupoNecessidadesHumanas, descricaoGrupoNecessidadesHumanas, situacaoGrupoNecessidadesHumanas);
	}

	public void excluir() {
		dataModel.reiniciarPaginator();
		
		try {
			if (grupoNecessidadesHumanasSelecionado != null) {
				this.prescricaoEnfermagemApoioFacade.removerGrupoNecessidadesHumanas(grupoNecessidadesHumanasSelecionado.getSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_GRUPO_NECESSIDADES_HUMANAS",
						grupoNecessidadesHumanasSelecionado.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_GRUPO_NECESSIDADES_HUMANAS_INVALIDA");
			}
			this.seqGrupoNecessidadesHumanasExclusao = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// ### Getters e Setters ###

	public Short getSeqGrupoNecessidadesHumanas() {
		return seqGrupoNecessidadesHumanas;
	}

	public void setSeqGrupoNecessidadesHumanas(Short seqGrupoNecessidadesHumanas) {
		this.seqGrupoNecessidadesHumanas = seqGrupoNecessidadesHumanas;
	}

	public String getDescricaoGrupoNecessidadesHumanas() {
		return descricaoGrupoNecessidadesHumanas;
	}

	public void setDescricaoGrupoNecessidadesHumanas(String descricaoGrupoNecessidadesHumanas) {
		this.descricaoGrupoNecessidadesHumanas = descricaoGrupoNecessidadesHumanas;
	}

	public DominioSituacao getSituacaoGrupoNecessidadesHumanas() {
		return situacaoGrupoNecessidadesHumanas;
	}

	public void setSituacaoGrupoNecessidadesHumanas(DominioSituacao situacaoGrupoNecessidadesHumanas) {
		this.situacaoGrupoNecessidadesHumanas = situacaoGrupoNecessidadesHumanas;
	}

	public Boolean getExibirBotaoIncluirGrupoNecessidadesHumanas() {
		return exibirBotaoIncluirGrupoNecessidadesHumanas;
	}

	public void setExibirBotaoIncluirGrupoNecessidadesHumanas(Boolean exibirBotaoIncluirGrupoNecessidadesHumanas) {
		this.exibirBotaoIncluirGrupoNecessidadesHumanas = exibirBotaoIncluirGrupoNecessidadesHumanas;
	}

	public Short getSeqGrupoNecessidadesHumanasExclusao() {
		return seqGrupoNecessidadesHumanasExclusao;
	}

	public void setSeqGrupoNecessidadesHumanasExclusao(Short seqGrupoNecessidadesHumanasExclusao) {
		this.seqGrupoNecessidadesHumanasExclusao = seqGrupoNecessidadesHumanasExclusao;
	}

	public DynamicDataModel<EpeGrupoNecesBasica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeGrupoNecesBasica> dataModel) {
		this.dataModel = dataModel;
	}

	public EpeGrupoNecesBasica getGrupoNecessidadesHumanasSelecionado() {
		return grupoNecessidadesHumanasSelecionado;
	}

	public void setGrupoNecessidadesHumanasSelecionado(EpeGrupoNecesBasica grupoNecessidadesHumanasSelecionado) {
		this.grupoNecessidadesHumanasSelecionado = grupoNecessidadesHumanasSelecionado;
	}
}
