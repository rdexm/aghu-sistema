package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisarSituacaoLeitosVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisarSituacaoLeitosPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6534228449734868969L;

	@EJB
	protected IAghuFacade aghuFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	private AghClinicas clinica;

	@Inject @Paginator
	private DynamicDataModel<PesquisarSituacaoLeitosVO> dataModel;	
	
	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() { 		
		dataModel.reiniciarPaginator(); 
	}

	public void limparPesquisa() {
		this.clinica = null;
		this.dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return this.pesquisaInternacaoFacade
				.pesquisaSituacaoLeitosCount(this.clinica);
	}

	@Override
	public List<PesquisarSituacaoLeitosVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		List<PesquisarSituacaoLeitosVO> result = this.pesquisaInternacaoFacade
				.pesquisaSituacaoLeitos(this.clinica, firstResult, maxResult,
						orderProperty, asc);

		if (result == null) {
			result = new ArrayList<PesquisarSituacaoLeitosVO>();
		}

		return result;
	}

	public List<AghClinicas> pesquisarClinicas(String paramPesquisa) {
		return this.aghuFacade
				.obterClinicaCapacidadeReferencial((String) paramPesquisa);
	}

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	public void setPesquisaInternacaoFacade(
			IPesquisaInternacaoFacade pesquisaInternacaoFacade) {
		this.pesquisaInternacaoFacade = pesquisaInternacaoFacade;
	}

	public DynamicDataModel<PesquisarSituacaoLeitosVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PesquisarSituacaoLeitosVO> dataModel) {
		this.dataModel = dataModel;
	}

}
