package br.gov.mec.aghu.cups.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;



public class ClasseImpressaoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ImpClasseImpressao> dataModel;

	//private static final Log LOG = LogFactory.getLog(ClasseImpressaoPaginatorController.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -3355255059869829260L;

	private boolean exibirBotaoIncluirClasseImpressao;

	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	private ImpClasseImpressao impClasseImpressao;

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosCupsFacade.pesquisarClasseImpressaoCount(
				impClasseImpressao.getClasseImpressao(),
				impClasseImpressao.getTipoImpressora(),
				impClasseImpressao.getDescricao(),
				impClasseImpressao.getTipoCups());

	}

	@Override
	public List<ImpClasseImpressao> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		List<ImpClasseImpressao> retorno = cadastrosBasicosCupsFacade
				.pesquisarClasseImpressao(firstResult, maxResult,
						orderProperty, asc,
						impClasseImpressao.getClasseImpressao(),
						impClasseImpressao.getTipoImpressora(),
						impClasseImpressao.getDescricao(),
						impClasseImpressao.getTipoCups());

		return retorno;
	}

	public boolean isExibirBotaoIncluirClasseImpressao() {
		return exibirBotaoIncluirClasseImpressao;
	}

	public void setExibirBotaoIncluirClasseImpressao(
			boolean exibirBotaoIncluirClasseImpressao) {
		this.exibirBotaoIncluirClasseImpressao = exibirBotaoIncluirClasseImpressao;

	}

	// Get's and Set's
	public ImpClasseImpressao getImpClasseImpressao() {
		return impClasseImpressao;
	}

	public void setImpClasseImpressao(ImpClasseImpressao impClasseImpressao) {
		this.impClasseImpressao = impClasseImpressao;
	}
 


	public DynamicDataModel<ImpClasseImpressao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ImpClasseImpressao> dataModel) {
	 this.dataModel = dataModel;
	}
}
