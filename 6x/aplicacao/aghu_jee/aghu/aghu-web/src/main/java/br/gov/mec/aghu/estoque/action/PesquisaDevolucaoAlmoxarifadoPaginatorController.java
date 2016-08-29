package br.gov.mec.aghu.estoque.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Controller para pesquisa de devoluções ao almoxarifado.
 * 
 * @author diego.pacheco
 *
 */

public class PesquisaDevolucaoAlmoxarifadoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<SceAlmoxarifado> dataModel;

	//private static final Log LOG = LogFactory.getLog(PesquisaDevolucaoAlmoxarifadoPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -275605630170986234L;
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	private Integer numeroDa;
	private List<SceDevolucaoAlmoxarifado> listaDevolucaoAlmoxarifado;
	private SceAlmoxarifado almoxarifado;
	private FccCentroCustos centroCusto;
	
	private boolean atualizarPesquisa;
	
	public void inicio(){
	 

	 

		if(this.atualizarPesquisa){
			dataModel.reiniciarPaginator();
			this.atualizarPesquisa = false;
		}
	
	}
	
	/**
	 * Atributo utilizado para controlar a exibicao do botao
	 */
	private boolean exibirBotaoIncluir;
	
	public boolean isExibirBotaoIncluir() {
		return exibirBotaoIncluir;
	}

	public void setExibirBotaoIncluir(boolean exibirBotaoIncluir) {
		this.exibirBotaoIncluir = exibirBotaoIncluir;
	}

	@Override
	public Long recuperarCount() {
		
		Short almoxarifadoSeq = null;
		Integer cctCodigo = null;
		
		if (almoxarifado != null) {
			almoxarifadoSeq = almoxarifado.getSeq();
		}
		
		if (centroCusto != null) {
			cctCodigo = centroCusto.getCodigo();
		}
		
		return estoqueFacade.pesquisarDevolucaoAlmoxarifadoCount(numeroDa, almoxarifadoSeq, cctCodigo, 
				Boolean.FALSE, Boolean.TRUE, null, null);
	}

	@Override
	public List<SceDevolucaoAlmoxarifado> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		Short almoxarifadoSeq = null;
		Integer cctCodigo = null;
		
		if (almoxarifado != null) {
			almoxarifadoSeq = almoxarifado.getSeq();
		}
		
		if (centroCusto != null) {
			cctCodigo = centroCusto.getCodigo();
		}

		listaDevolucaoAlmoxarifado = estoqueFacade.pesquisarDevolucaoAlmoxarifado(
				firstResult, maxResult, SceDevolucaoAlmoxarifado.Fields.DT_GERACAO.toString(), false, 
				numeroDa, almoxarifadoSeq, cctCodigo, Boolean.FALSE, Boolean.TRUE, null, null);
		
		return listaDevolucaoAlmoxarifado;
	}
	
	public void pesquisar(){
		dataModel.reiniciarPaginator();
		this.exibirBotaoIncluir = true;
	}
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null){
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public void limparPesquisa() {
		this.numeroDa = null;
		this.almoxarifado = null;
		this.centroCusto = null;
		this.listaDevolucaoAlmoxarifado = null;
		this.exibirBotaoIncluir = false;
		dataModel.setPesquisaAtiva(false);
	}
	
	public String criarDevolucaoAlmoxarifado() {
		return "manterDevolucaoAlmoxarifado";
	}
	
	public String editar(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) {
		return "manterDevolucaoAlmoxarifado";
	}
	
	public List<SceAlmoxarifado> pesquisarAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.pesquisarAlmoxarifadosAtivosPorCodigoDescricao(objPesquisa);
	}
	
	public List<FccCentroCustos> pesquisarCentroCusto(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosAtivosPorCodigoOuDescricao(objPesquisa);
	}

	public Integer getNumeroDa() {
		return numeroDa;
	}

	public void setNumeroDa(Integer numeroDa) {
		this.numeroDa = numeroDa;
	}

	public List<SceDevolucaoAlmoxarifado> getListaDevolucaoAlmoxarifado() {
		return listaDevolucaoAlmoxarifado;
	}

	public void setListaDevolucaoAlmoxarifado(
			List<SceDevolucaoAlmoxarifado> listaDevolucaoAlmoxarifado) {
		this.listaDevolucaoAlmoxarifado = listaDevolucaoAlmoxarifado;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public boolean isAtualizarPesquisa() {
		return atualizarPesquisa;
	}

	public void setAtualizarPesquisa(boolean atualizarPesquisa) {
		this.atualizarPesquisa = atualizarPesquisa;
	} 


	public DynamicDataModel<SceAlmoxarifado> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceAlmoxarifado> dataModel) {
	 this.dataModel = dataModel;
	}
}
