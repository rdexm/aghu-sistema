package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoUsoMedicamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2252232093303064152L;
	
	private final String REDIRECIONA_CADASTRAR_EDITAR_GRUPO_USO_MDTO = "grupoUsoMedicamentoCRUD";

	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;

	@Inject
	private Conversation conversation;
	
	@Inject
	private GrupoUsoMedicamentoController grupoUsoMedicamentoController;
	
	@Inject @Paginator
	private DynamicDataModel<AfaGrupoUsoMedicamento> dataModel;
	
	private AfaGrupoUsoMedicamento grupoUsoMedicamentoPesquisa;
	private AfaGrupoUsoMedicamento grupoUsoMedicamentoSelecionado;
	
	@PostConstruct
	public void init(){
		begin(conversation);
		grupoUsoMedicamentoPesquisa = new AfaGrupoUsoMedicamento();
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaApoioFacade
				.pesquisarGrupoUsoMedicamentoCount(grupoUsoMedicamentoPesquisa);
	}

	@Override
	public List<AfaGrupoUsoMedicamento> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.farmaciaApoioFacade.pesquisarGrupoUsoMedicamento(firstResult,
				maxResult, AfaGrupoUsoMedicamento.Fields.SEQ.toString(), true,
				grupoUsoMedicamentoPesquisa);
	}
	
	public void limparPesquisa(){
		grupoUsoMedicamentoPesquisa = new AfaGrupoUsoMedicamento();
		this.dataModel.limparPesquisa();
	}
	
	public void efetuarPesquisa(){
		this.dataModel.reiniciarPaginator();
	}

	public void excluir() {
		try {
			farmaciaApoioFacade.removerGrupoUsoMedicamento(grupoUsoMedicamentoSelecionado.getSeq());			
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_GRUPO_USO_MEDICAMENTOS", grupoUsoMedicamentoSelecionado.getDescricao());			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar(){
		grupoUsoMedicamentoController.setGrupoUsoMedicamento(grupoUsoMedicamentoSelecionado);
		return REDIRECIONA_CADASTRAR_EDITAR_GRUPO_USO_MDTO;
	}
	public String novo() {
		AfaGrupoUsoMedicamento agum = new AfaGrupoUsoMedicamento();
		agum.setIndSituacao(DominioSituacao.A);
		grupoUsoMedicamentoController.setGrupoUsoMedicamento(agum);
		return REDIRECIONA_CADASTRAR_EDITAR_GRUPO_USO_MDTO;
	}

	public DynamicDataModel<AfaGrupoUsoMedicamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaGrupoUsoMedicamento> dataModel) {
		this.dataModel = dataModel;
	}

	public AfaGrupoUsoMedicamento getGrupoUsoMedicamentoPesquisa() {
		return grupoUsoMedicamentoPesquisa;
	}

	public void setGrupoUsoMedicamentoPesquisa(
			AfaGrupoUsoMedicamento grupoUsoMedicamentoPesquisa) {
		this.grupoUsoMedicamentoPesquisa = grupoUsoMedicamentoPesquisa;
	}

	public AfaGrupoUsoMedicamento getGrupoUsoMedicamentoSelecionado() {
		return grupoUsoMedicamentoSelecionado;
	}

	public void setGrupoUsoMedicamentoSelecionado(
			AfaGrupoUsoMedicamento grupoUsoMedicamentoSelecionado) {
		this.grupoUsoMedicamentoSelecionado = grupoUsoMedicamentoSelecionado;
	}
}