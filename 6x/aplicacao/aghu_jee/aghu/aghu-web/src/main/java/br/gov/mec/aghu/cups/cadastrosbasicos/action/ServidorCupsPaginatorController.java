package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpServidorCups;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;



public class ServidorCupsPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ImpServidorCups> dataModel;

	//private static final Log LOG = LogFactory.getLog(ServidorCupsPaginatorController.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -5939113940971283157L;

	private boolean exibirBotaoIncluirServidorCups;

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	private ImpServidorCups impServidorCups;

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosCupsFacade.pesquisarServidorCupsCount(
				impServidorCups.getIpServidor(), impServidorCups.getNomeCups(),
				impServidorCups.getDescricao());
	}

	@Override
	public List<ImpServidorCups> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		List<ImpServidorCups> retorno = cadastrosBasicosCupsFacade
				.pesquisarServidorCups(firstResult, maxResult, orderProperty,
						asc, impServidorCups.getIpServidor(),
						impServidorCups.getNomeCups(),
						impServidorCups.getDescricao());

		return retorno;
	}

	public boolean isExibirBotaoIncluirServidorCups() {
		return exibirBotaoIncluirServidorCups;
	}

	public void setExibirBotaoIncluirServidorCups(
			boolean exibirBotaoIncluirServidorCups) {
		this.exibirBotaoIncluirServidorCups = exibirBotaoIncluirServidorCups;

	}

	// Get's and Set's
	// ===============

	/**
	 * @return
	 */
	public ImpServidorCups getImpServidorCups() {
		return impServidorCups;
	}

	public void setImpServidorCups(ImpServidorCups impServidorCups) {
		this.impServidorCups = impServidorCups;
	}
 


	public DynamicDataModel<ImpServidorCups> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ImpServidorCups> dataModel) {
	 this.dataModel = dataModel;
	}
}
