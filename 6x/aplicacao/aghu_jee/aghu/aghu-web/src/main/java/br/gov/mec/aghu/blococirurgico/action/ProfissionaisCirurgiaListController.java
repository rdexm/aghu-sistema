package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.core.action.ActionController;


public class ProfissionaisCirurgiaListController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = -9080940970337693983L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;		
	
	private Integer crgSeq;
	
	private List<MbcProfCirurgias> listaProfCirurgias;	
			
	
	public void inicializar(Integer crgSeq) {			
		this.crgSeq = crgSeq;
		listaProfCirurgias = blocoCirurgicoFacade.pesquisarMbcProfCirurgiasByCirurgia(this.crgSeq);				
	}
		
	public String editarBoolean(Boolean boleano) {
		String retorno = null;
		if (boleano != null){
			retorno = boleano ? "Sim" : "Não"; 
		}
		return retorno;
	}	
	
	
	// Getters e Setters	

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public List<MbcProfCirurgias> getListaProfCirurgias() {
		return listaProfCirurgias;
	}

	public void setListaProfCirurgias(List<MbcProfCirurgias> listaProfCirurgias) {
		this.listaProfCirurgias = listaProfCirurgias;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	
}