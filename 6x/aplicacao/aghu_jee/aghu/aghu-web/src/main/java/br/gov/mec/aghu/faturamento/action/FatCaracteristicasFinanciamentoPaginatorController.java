package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class FatCaracteristicasFinanciamentoPaginatorController extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4815156130398961773L;
	
	private static final String PAGE_CADASTRO_CARACTERISTICAS_FINANCIAMENTO = "caracteristicasFinanciamentoCRUD";

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	@Inject @Paginator
	private DynamicDataModel<FatCaractFinanciamento> dataModel;
	
	private FatCaractFinanciamento filtro = new FatCaractFinanciamento();
	
	@PostConstruct
	public void inicializar() {
		begin(conversation);
	}

	public void pesquisar(){
		atualizarDataModel();
	}
	
	public void limparPesquisa(){
		filtro = new FatCaractFinanciamento();
		dataModel.limparPesquisa();
	}
	
	
	public void atualizarDataModel(){
		dataModel.reiniciarPaginator();	
	}
	
	@Override
	public List<FatCaractFinanciamento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.faturamentoFacade.pesquisarCaracteristicasFinanciamento(firstResult, maxResult, orderProperty, asc, this.filtro);
	}

	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade.pesquisarCaractFinanciamentosCount(filtro);
	}
	
	public String incluir(){
		return PAGE_CADASTRO_CARACTERISTICAS_FINANCIAMENTO;
	}
		
	public void ativarCaracteristica(FatCaractFinanciamento entidade) throws ApplicationBusinessException{
		entidade.setIndSituacao(DominioSituacao.A);
		alterarSituacao(entidade);
	}
	
	public void desativarCaracteristica(FatCaractFinanciamento entidade) throws ApplicationBusinessException{
		entidade.setIndSituacao(DominioSituacao.I);
		alterarSituacao(entidade);		
	}
	
	private void alterarSituacao(FatCaractFinanciamento entidade) throws ApplicationBusinessException{
		faturamentoFacade.alterarSituacaoCaracteristicaFinanciamento(entidade);
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_FAT_CARACT_FINANCIAMENTO", entidade.getDescricao());
		atualizarDataModel();
	}
	
	/**
	 * Métodos Getters and Setters 
	 */

	public DynamicDataModel<FatCaractFinanciamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatCaractFinanciamento> dataModel) {
		this.dataModel = dataModel;
	}

	public FatCaractFinanciamento getFiltro() {
		return filtro;
	}

	public void setFiltro(FatCaractFinanciamento filtro) {
		this.filtro = filtro;
	}

}
