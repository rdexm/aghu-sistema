package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class TipoApresentacaoMedicamentoPaginatorController  extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4585283557574668585L;

	private final String REDIRECIONA_CADASTRAR_EDITAR_TIPO_APRES_MDTO = "tipoApresentacaoMedicamentoCRUD";
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@Inject
	private TipoApresentacaoMedicamentoController tipoApresentacaoMedicamentoController;

	@Inject @Paginator
	private DynamicDataModel<AfaTipoApresentacaoMedicamento> dataModel;
	
	private AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoPesquisa;
	private AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoSelecionado;
	
	@PostConstruct
	public void init(){
		begin(conversation);
		tipoApresentacaoMedicamentoPesquisa = new AfaTipoApresentacaoMedicamento();
	}
	
	@Override
	public Long recuperarCount() {
		return this.farmaciaApoioFacade
				.pesquisarTipoApresentacaoMedicamentoCount(tipoApresentacaoMedicamentoPesquisa);
	}

	@Override
	public List<AfaTipoApresentacaoMedicamento> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.farmaciaApoioFacade.pesquisarTipoApresentacaoMedicamento(
				firstResult, maxResult,
				AfaTipoApresentacaoMedicamento.Fields.DESCRICAO.toString(),
				true, tipoApresentacaoMedicamentoPesquisa);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		tipoApresentacaoMedicamentoPesquisa = new AfaTipoApresentacaoMedicamento();
		this.dataModel.limparPesquisa();
	}
	
	public void excluir() {
		try {
			farmaciaApoioFacade.removerTipoApresentacaoMedicamento(tipoApresentacaoMedicamentoSelecionado.getSigla());			
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_TIPO_APRESENTACAO_MEDICAMENTOS", tipoApresentacaoMedicamentoSelecionado.getDescricao());			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar(){
		tipoApresentacaoMedicamentoController.setTipoApresentacaoMedicamento(tipoApresentacaoMedicamentoSelecionado);
		tipoApresentacaoMedicamentoController.setNovoRegistro(Boolean.FALSE);
		return REDIRECIONA_CADASTRAR_EDITAR_TIPO_APRES_MDTO;
	}
	public String novo() {
		AfaTipoApresentacaoMedicamento atam = new AfaTipoApresentacaoMedicamento();
		atam.setSituacao(DominioSituacao.A);
		tipoApresentacaoMedicamentoController.setTipoApresentacaoMedicamento(atam);
		tipoApresentacaoMedicamentoController.setNovoRegistro(Boolean.TRUE);
		return REDIRECIONA_CADASTRAR_EDITAR_TIPO_APRES_MDTO;
	}

	public AfaTipoApresentacaoMedicamento getTipoApresentacaoMedicamentoPesquisa() {
		return tipoApresentacaoMedicamentoPesquisa;
	}

	public void setTipoApresentacaoMedicamentoPesquisa(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoPesquisa) {
		this.tipoApresentacaoMedicamentoPesquisa = tipoApresentacaoMedicamentoPesquisa;
	}

	public AfaTipoApresentacaoMedicamento getTipoApresentacaoMedicamentoSelecionado() {
		return tipoApresentacaoMedicamentoSelecionado;
	}

	public void setTipoApresentacaoMedicamentoSelecionado(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamentoSelecionado) {
		this.tipoApresentacaoMedicamentoSelecionado = tipoApresentacaoMedicamentoSelecionado;
	}

	public DynamicDataModel<AfaTipoApresentacaoMedicamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AfaTipoApresentacaoMedicamento> dataModel) {
		this.dataModel = dataModel;
	}

}