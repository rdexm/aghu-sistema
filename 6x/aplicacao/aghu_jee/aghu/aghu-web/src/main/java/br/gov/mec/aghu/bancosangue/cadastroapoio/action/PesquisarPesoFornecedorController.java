package br.gov.mec.aghu.bancosangue.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedor;
import br.gov.mec.aghu.model.AbsComponentePesoFornecedorId;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsFornecedorBolsas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarPesoFornecedorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = 4282877415523802264L;

	private static final String MANTER_COMPONENTES_SANGUINEOS = "prescricaomedica-manterComponentesSanguineos";

	private static final String MANTER_PESO_FORNECEDOR = "manterPesoFornecedor";
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;	
	
	private AbsComponenteSanguineo componenteSanguineo;
	private String componenteSanguineoCodigo;
	private AbsComponentePesoFornecedor absComponentePesoFornecedor;
	private AbsFornecedorBolsas absFornecedorBolsas;
	private Short peso;
	
	@Inject @Paginator
	private DynamicDataModel<AbsComponentePesoFornecedor> dataModel;
	
	private AbsComponentePesoFornecedor selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	public void iniciar(){
	 

	 

		if(componenteSanguineoCodigo != null){
			componenteSanguineo = bancoDeSangueFacade.obterComponenteSanguineosPorCodigo(componenteSanguineoCodigo);
			pesquisar();
		} else {
			componenteSanguineo = new AbsComponenteSanguineo();
		}
	
	}
	
	
	public List<AbsComponenteSanguineo> pesquisarComponenteSanguineo(String param) {
		return  this.returnSGWithCount(bancoDeSangueFacade.obterComponenteSanguineos(param),pesquisarComponenteSanguineoCount(param));
	}
	
	public Long pesquisarComponenteSanguineoCount(String param) {
		return bancoDeSangueFacade.obterComponenteSanguineosCount(param);
	}
	
	public List<AbsFornecedorBolsas> pesquisarFornecedorBolsa(String param) {
		return  this.returnSGWithCount(bancoDeSangueFacade.pesquisarFornecedor(param),pesquisarFornecedorBolsaCount(param));  
	}
	
	public Long pesquisarFornecedorBolsaCount(String param) {
		return bancoDeSangueFacade.pesquisarFornecedorCount(param); 
	}
	
	public String voltarManterComponentesSanguineos(){
		return MANTER_COMPONENTES_SANGUINEOS;
	}
	
	public void pesquisar() {
		absComponentePesoFornecedor = new AbsComponentePesoFornecedor(); 
		absComponentePesoFornecedor.setComponenteSanguineo(componenteSanguineo); 
		absComponentePesoFornecedor.setFornecedorBolsas(absFornecedorBolsas);
		absComponentePesoFornecedor.setPeso(peso);
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		dataModel.limparPesquisa();
		absComponentePesoFornecedor = null; 
		absFornecedorBolsas = null; 
		peso = null; 
	}

	public void alterarIndSugestao(AbsComponentePesoFornecedorId id) {
		AbsComponentePesoFornecedor absComponentePesoFornecedor = bancoDeSangueFacade.obterAbsComponentePesoFornecedorPorChavePrimaria(id, false);
		try {
			bancoDeSangueFacade.alterarIndSugestao(absComponentePesoFornecedor);
			apresentarMsgNegocio(Severity.INFO,"PESO_FORNECEDOR_MENSAGEM_SUCESSO_ATUALIZAR_IND_SUGESTAO");
			pesquisar(); 
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar(){
		return MANTER_PESO_FORNECEDOR;
	}
	
	public String inserir() {
		return MANTER_PESO_FORNECEDOR;
	}

	public void excluir() {
		try {
			bancoDeSangueFacade.excluirAbsComponentePesoFornecedor(selecionado.getId());
			apresentarMsgNegocio(Severity.INFO,"PESO_FORNECEDOR_MENSAGEM_SUCESSO_EXCLUIR_PESO_FORNECEDOR");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public List<AbsComponentePesoFornecedor> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return bancoDeSangueFacade.listaPesoFornecedor(firstResult, maxResult, orderProperty, true, absComponentePesoFornecedor); 
	}

	@Override
	public Long recuperarCount() {
		return bancoDeSangueFacade.pesquisarComponentePesoFornecedorCount(absComponentePesoFornecedor); 
	}
	
	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	public String getComponenteSanguineoCodigo() {
		return componenteSanguineoCodigo;
	}

	public void setComponenteSanguineoCodigo(String componenteSanguineoCodigo) {
		this.componenteSanguineoCodigo = componenteSanguineoCodigo;
	}

	public AbsComponentePesoFornecedor getAbsComponentePesoFornecedor() {
		return absComponentePesoFornecedor;
	}

	public void setAbsComponentePesoFornecedor(
			AbsComponentePesoFornecedor absComponentePesoFornecedor) {
		this.absComponentePesoFornecedor = absComponentePesoFornecedor;
	}

	public AbsFornecedorBolsas getAbsFornecedorBolsas() {
		return absFornecedorBolsas;
	}

	public void setAbsFornecedorBolsas(AbsFornecedorBolsas absFornecedorBolsas) {
		this.absFornecedorBolsas = absFornecedorBolsas;
	}

	public Short getPeso() {
		return peso;
	}

	public void setPeso(Short peso) {
		this.peso = peso;
	} 

	public DynamicDataModel<AbsComponentePesoFornecedor> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AbsComponentePesoFornecedor> dataModel) {
		this.dataModel = dataModel;
	}

	public AbsComponentePesoFornecedor getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AbsComponentePesoFornecedor selecionado) {
		this.selecionado = selecionado;
	}
}