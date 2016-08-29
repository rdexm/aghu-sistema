package br.gov.mec.aghu.internacao.leitos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoTransferencia;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class SolicitacoesTransferenciaPacientePaginatorController  extends ActionController implements ActionPaginator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6019600544772822031L;

	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;
	
	DominioSituacaoSolicitacaoTransferencia situacao;
	private String voltarPara; 
	@Inject @Paginator
	private DynamicDataModel<AinSolicTransfPacientes> dataModel;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {
	 

		dataModel.reiniciarPaginator();
		this.situacao = DominioSituacaoSolicitacaoTransferencia.P;
	
	}
	
	@Override
	public Long recuperarCount() {
		Long count = this.leitosInternacaoFacade.pesquisarSolicitacaoTransferenciaPacienteCount(
				this.situacao, null, null, null, null, null
		);
		return count;
	}
	
	@Override
	public List<AinSolicTransfPacientes> recuperarListaPaginada(
			Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc
	) {
		return this.leitosInternacaoFacade.pesquisarSolicitacaoTransferenciaPaciente(
				firstResult, maxResult, orderProperty, asc, 
				this.situacao, null, null, null, null, null
		);
	}
	
	public String actionVoltar() {
		if (voltarPara != null) {
			if(voltarPara.equalsIgnoreCase("pesquisarDisponibilidadeQuarto")){
				return "internacao-pesquisarDisponibilidadeQuarto";
			}
			return voltarPara;
		} else {
			return "cancelar";
		}
	}
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public DynamicDataModel<AinSolicTransfPacientes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinSolicTransfPacientes> dataModel) {
		this.dataModel = dataModel;
	}
	
}
