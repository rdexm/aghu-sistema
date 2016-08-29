package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.model.MptControleFreqSessao;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class ListarSessoesFisioPOLController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4214253319408555987L;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	private Integer trpSeqSessoesFisioterapia;
	
	@Inject @Paginator
	private DynamicDataModel<MptControleFreqSessao> dataModel;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;		

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		iniciar();
	}	
	
	public void iniciar() {
		if (itemPOL!=null){
			trpSeqSessoesFisioterapia=(Integer) itemPOL.getParametros().get("seqTratamentoTerapeutico"); 
		}		
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return prontuarioOnlineFacade.listarSessoesFisioterapiaCount(trpSeqSessoesFisioterapia);
	}

	@Override
	public List<MptControleFreqSessao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return prontuarioOnlineFacade.listarSessoesFisioterapia(trpSeqSessoesFisioterapia, firstResult, maxResult, orderProperty, asc);
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public Integer getTrpSeqSessoesFisioterapia() {
		return trpSeqSessoesFisioterapia;
	}

	public void setTrpSeqSessoesFisioterapia(Integer trpSeqSessoesFisioterapia) {
		this.trpSeqSessoesFisioterapia = trpSeqSessoesFisioterapia;
	} 


	public DynamicDataModel<MptControleFreqSessao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MptControleFreqSessao> dataModel) {
	 this.dataModel = dataModel;
	}
}