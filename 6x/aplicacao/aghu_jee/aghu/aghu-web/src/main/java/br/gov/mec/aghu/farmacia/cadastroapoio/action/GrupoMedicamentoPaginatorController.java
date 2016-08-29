package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoMedicamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4918991518425230843L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private GrupoMedicamentoController grupoMedicamentoController;
	
	private AfaGrupoMedicamento grupoMedicamentoSelecionado;
	private Short filtroSeq;
	private String filtroDescricao;
	private DominioSimNao filtroMedicamentoMesmoSal;
	private DominioSimNao filtroUsoFichaAnestesia;
	private DominioSituacao filtroSituacao;
	@Inject @Paginator
	private DynamicDataModel<AfaGrupoMedicamento> dataModel;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	private static final Comparator<AfaItemGrupoMedicamento> COMPARATOR_ITEM_GRUPO_MEDICAMENTO = new Comparator<AfaItemGrupoMedicamento>() {
		@Override
		public int compare(AfaItemGrupoMedicamento o1, AfaItemGrupoMedicamento o2) {
			return o1.getMedicamento().getDescricao().toUpperCase().compareTo(o2.getMedicamento().getDescricao().toUpperCase());
		}
	};

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public String iniciarInclusao() {
		grupoMedicamentoController.setGrupoMedicamento(new AfaGrupoMedicamento());
		// Valor padrão para esta tela.
		grupoMedicamentoController.getGrupoMedicamento().setUsoSumarioPrescricao(Boolean.FALSE);
		return "grupoMedicamentoCRUD";
	}
	
	public void limparPesquisa() {
		this.filtroSeq = null;
		this.filtroDescricao = null;
		this.filtroMedicamentoMesmoSal = null;
		this.filtroUsoFichaAnestesia = null;
		this.filtroSituacao = null;
		dataModel.limparPesquisa();
	}

	public String editar() {
		grupoMedicamentoController.setGrupoMedicamento(grupoMedicamentoSelecionado);
		return "grupoMedicamentoCRUD";
	}
	
	public void excluir() {
		dataModel.reiniciarPaginator();
		try {
			this.farmaciaFacade.removerAfaGrupoMedicamento(grupoMedicamentoSelecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_GRUPO_MEDICAMENTO", grupoMedicamentoSelecionado.getDescricao());
			grupoMedicamentoSelecionado = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void exibirItensGrupoMedicamento(AfaGrupoMedicamento grupo) {
		grupoMedicamentoSelecionado = grupo;
		Collections.sort(grupoMedicamentoSelecionado.getItensGruposMedicamento(), COMPARATOR_ITEM_GRUPO_MEDICAMENTO);
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaFacade.pesquisaAfaGrupoMedicamentoCount(
				this.filtroSeq,
				this.filtroDescricao,
				this.filtroMedicamentoMesmoSal != null ? this.filtroMedicamentoMesmoSal.isSim() : null,
				this.filtroUsoFichaAnestesia != null ? this.filtroUsoFichaAnestesia.isSim() : null,
				this.filtroSituacao);
	}

	@Override
	public List<AfaGrupoMedicamento> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		List<AfaGrupoMedicamento> result = this.farmaciaFacade.pesquisaAfaGrupoMedicamento(
				firstResult,
				maxResults,
				orderProperty,
				asc,
				this.filtroSeq,
				this.filtroDescricao,
				this.filtroMedicamentoMesmoSal != null ? this.filtroMedicamentoMesmoSal.isSim() : null,
				this.filtroUsoFichaAnestesia != null ? this.filtroUsoFichaAnestesia.isSim() : null, 
				this.filtroSituacao);
if (result == null) {
	result = new ArrayList<AfaGrupoMedicamento>();
}

return result;
	}

	public Short getFiltroSeq() {
		return filtroSeq;
	}

	public void setFiltroSeq(Short filtroSeq) {
		this.filtroSeq = filtroSeq;
	}

	public String getFiltroDescricao() {
		return filtroDescricao;
	}

	public void setFiltroDescricao(String filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}

	public DominioSimNao getFiltroMedicamentoMesmoSal() {
		return filtroMedicamentoMesmoSal;
	}

	public void setFiltroMedicamentoMesmoSal(
			DominioSimNao filtroMedicamentoMesmoSal) {
		this.filtroMedicamentoMesmoSal = filtroMedicamentoMesmoSal;
	}

	public DominioSimNao getFiltroUsoFichaAnestesia() {
		return filtroUsoFichaAnestesia;
	}

	public void setFiltroUsoFichaAnestesia(DominioSimNao filtroUsoFichaAnestesia) {
		this.filtroUsoFichaAnestesia = filtroUsoFichaAnestesia;
	}

	public DominioSituacao getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(DominioSituacao filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public DynamicDataModel<AfaGrupoMedicamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaGrupoMedicamento> dataModel) {
		this.dataModel = dataModel;
	}

	public AfaGrupoMedicamento getGrupoMedicamentoSelecionado() {
		return grupoMedicamentoSelecionado;
	}

	public void setGrupoMedicamentoSelecionado(
			AfaGrupoMedicamento grupoMedicamentoSelecionado) {
		this.grupoMedicamentoSelecionado = grupoMedicamentoSelecionado;
	}
}
