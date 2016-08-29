package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;



public class ImpressoraPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ImpImpressora> dataModel;

	//private static final Log LOG = LogFactory.getLog(ImpressoraPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8023100778733012485L;

	private boolean exibirBotaoIncluirImpressora;

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	private ImpImpressora impImpressora;

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosCupsFacade
				.pesquisarImpressoraCount(impImpressora.getFilaImpressora(),
						impImpressora.getTipoImpressora(),
						impImpressora.getDescricao());
	}

	@Override
	public List<ImpImpressora> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		List<ImpImpressora> retorno = cadastrosBasicosCupsFacade
				.pesquisarImpressora(firstResult, maxResult, orderProperty,
						asc, impImpressora.getFilaImpressora(),
						impImpressora.getTipoImpressora(),
						impImpressora.getDescricao());

		if (retorno == null) {
			retorno = new ArrayList<ImpImpressora>();
		}

		return retorno;
	}

	public boolean isExibirBotaoIncluirImpressora() {
		return exibirBotaoIncluirImpressora;
	}

	public void setExibirBotaoIncluirImpressora(
			boolean exibirBotaoIncluirImpressora) {
		this.exibirBotaoIncluirImpressora = exibirBotaoIncluirImpressora;
	}

	// Get's and Set's
	public ImpImpressora getImpImpressora() {
		return impImpressora;
	}

	public void setImpImpressora(ImpImpressora impImpressora) {
		this.impImpressora = impImpressora;
	} 


	public DynamicDataModel<ImpImpressora> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ImpImpressora> dataModel) {
	 this.dataModel = dataModel;
	}
}
