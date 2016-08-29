package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;



public class ComputadorPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ImpComputador> dataModel;

	//private static final Log LOG = LogFactory.getLog(ComputadorPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -7406509053063106453L;

	private boolean exibirBotaoIncluirComputador;

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	private ImpComputador impComputador;

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosCupsFacade
				.pesquisarComputadorCount(impComputador.getIpComputador(),
						impComputador.getNomeComputador(),
						impComputador.getDescricao());

	}

	@Override
	public List<ImpComputador> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		List<ImpComputador> retorno = cadastrosBasicosCupsFacade
				.pesquisarComputador(firstResult, maxResult, orderProperty,
						asc, impComputador.getIpComputador(),
						impComputador.getNomeComputador(),
						impComputador.getDescricao());

		return retorno;
	}

	public boolean isExibirBotaoIncluirComputador() {
		return exibirBotaoIncluirComputador;
	}

	public void setExibirBotaoIncluirComputador(
			boolean exibirBotaoIncluirComputador) {
		this.exibirBotaoIncluirComputador = exibirBotaoIncluirComputador;

	}

	// Get's and Set's
	public ImpComputador getImpComputador() {
		return impComputador;
	}

	public void setImpComputador(ImpComputador impComputador) {
		this.impComputador = impComputador;
	}
 


	public DynamicDataModel<ImpComputador> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ImpComputador> dataModel) {
	 this.dataModel = dataModel;
	}
}
