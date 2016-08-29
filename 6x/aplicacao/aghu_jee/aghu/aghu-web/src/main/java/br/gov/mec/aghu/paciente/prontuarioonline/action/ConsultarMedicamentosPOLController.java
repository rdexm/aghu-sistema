package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.model.VAipPolMdtosBase;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ConsultarMedicamentosPOLController extends ActionController implements ActionPaginator  {

	private static final long serialVersionUID = 7137082321232271674L;
	
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;	
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;	

	private Date dataInicio;
	private String tipo;
	private Integer codPaciente;

	private List<VAipPolMdtosBase> listaMedicamentos;
	
	@Inject @Paginator
	private DynamicDataModel<AipPaises> dataModel;
	
	private Boolean isHistorico=false;
	
	private Long sizeListaMedicamentos;
	
	/**
	 * Aba corrente
	 */
	private Integer currentTab;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}	
	
	public void inicio() {
		if (itemPOL!=null){
			codPaciente=(Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE);
			if (itemPOL.getParametros().containsKey(NodoPOLVO.IS_HISTORICO)){
				isHistorico=(Boolean) itemPOL.getParametros().get(NodoPOLVO.IS_HISTORICO);
			}	
			if ("antibioticos".equals(itemPOL.getTipo())){
				currentTab = 0;
			}else if ("quimioterapicos".equals(itemPOL.getTipo())){
				currentTab = 1;
			}else if ("tuberculostaticos".equals(itemPOL.getTipo())){				
				currentTab = 2;
			}
			if (itemPOL.getParametros().containsKey("data")){
				dataInicio = (Date) itemPOL.getParametros().get("data");
			}
		}
		if (currentTab==null || currentTab.equals("medicamento")){
			currentTab=0;
		}	
		
		pesquisarMedicamentos();
	}
	
    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getClientId();
        
		if ("antibioticos".equals(tab)){
			currentTab = 0;
		}else if ("quimioterapicos".equals(tab)){
			currentTab = 1;
		}else if ("tuberculostaticos".equals(tab)){				
			currentTab = 2;
		}        
        
        pesquisarMedicamentos();
        
    }
	
	private void pesquisarMedicamentos() {		
		String tab=null;
		if (currentTab.equals(0)){
			tab="antibioticos";
		} else if (currentTab.equals(1)){
			tab="quimioterapicos";
		} else if (currentTab.equals(2)){
			tab="tuberculostaticos";			
		}

		dataModel.reiniciarPaginator();
		
		if(isHistorico){
			this.listaMedicamentos = new ArrayList<VAipPolMdtosBase>(prontuarioOnlineFacade.obterMedicamentosHist(codPaciente, tab, dataInicio));
		}else{
			this.listaMedicamentos = new ArrayList<VAipPolMdtosBase>(prontuarioOnlineFacade.obterMedicamentos(codPaciente, tab, dataInicio));
		}
		sizeListaMedicamentos = Long.valueOf(listaMedicamentos.size());
	}
	
		
	@Override
	public List<VAipPolMdtosBase> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(listaMedicamentos == null || listaMedicamentos.size() != sizeListaMedicamentos.intValue()){//Se true, usuário está paginando
			pesquisarMedicamentos();
		}        
		
		Integer lastResult = (firstResult+maxResult)>listaMedicamentos.size()?listaMedicamentos.size():(firstResult+maxResult);
		listaMedicamentos= listaMedicamentos.subList(firstResult, lastResult);
		return listaMedicamentos;
	}

	@Override
	public Long recuperarCount() {
		return sizeListaMedicamentos;
	}
	
	public Integer getQtdMdto(Integer atdSeq, String medicamento, VAipPolMdtosBase polMdto) {
		Integer qtdMdto = 0;
		if (atdSeq == null || atdSeq==0){
			qtdMdto = polMdto.getQtdeDiasPrcr();
		}else{
			qtdMdto = prontuarioOnlineFacade.obterQuantidadeMedicamentos(atdSeq, medicamento);
		}
		return qtdMdto;
	}
	
	
	// GETTERS AND SETTERS
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Boolean getIsHistorico() {
		return isHistorico;
	}

	public void setIsHistorico(Boolean isHistorico) {
		this.isHistorico = isHistorico;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public List<VAipPolMdtosBase> getListaMedicamentos() {
		return listaMedicamentos;
	}

	public void setListaMedicamentos(List<VAipPolMdtosBase> listaMedicamentos) {
		this.listaMedicamentos = listaMedicamentos;
	}

	public Integer getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(Integer currentTab) {
		this.currentTab = currentTab;
	}

	public Long getSizeListaMedicamentos() {
		return sizeListaMedicamentos;
	}

	public void setSizeListaMedicamentos(Long sizeListaMedicamentos) {
		this.sizeListaMedicamentos = sizeListaMedicamentos;
	}

	public DynamicDataModel<AipPaises> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipPaises> dataModel) {
		this.dataModel = dataModel;
	}
}