package br.gov.mec.aghu.faturamento.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class ManterCidContaHospitalarPaginatorController extends ActionController implements ActionPaginator  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5364282298929735067L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private FatCidContaHospitalar fatCidContaHospitalarSelecionado;

	// FILTRO
	private Integer cthSeq;

	public void pesquisar() {
		if (cthSeq != null) {
			this.dataModel.reiniciarPaginator();		
		}
	}

	@Override
	public Long recuperarCount() {		
		return this.faturamentoFacade.pesquisarCidContaHospitalarCount(cthSeq);
	}

	@Override
	public List<FatCidContaHospitalar> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<FatCidContaHospitalar> lista = this.faturamentoFacade.pesquisarCidContaHospitalar(firstResult, maxResult, orderProperty, asc, cthSeq);
		Collections.sort(lista, new CidComparator());
		return lista;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}
	
	/**
	 * Ordena por Prioridade de CID
	 * 
	 */
	class CidComparator implements
			Comparator<FatCidContaHospitalar> {

		@Override
		public int compare(FatCidContaHospitalar o1, FatCidContaHospitalar o2) {

			DominioPrioridadeCid cid1 = (o1).getId().getPrioridadeCid();

			if (cid1.equals(DominioPrioridadeCid.P)) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}

	public FatCidContaHospitalar getFatCidContaHospitalarSelecionado() {
		return fatCidContaHospitalarSelecionado;
	}

	public void setFatCidContaHospitalarSelecionado(
			FatCidContaHospitalar fatCidContaHospitalarSelecionado) {
		this.fatCidContaHospitalarSelecionado = fatCidContaHospitalarSelecionado;
	}	
}
