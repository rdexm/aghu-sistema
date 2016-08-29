package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.internacao.vo.AinQuartosVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class QuartoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6311943654433755902L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Short codigoQuartoPesquisa = null;

	private AghClinicas clinicaPesquisa;

	private String descricaoQuartoPesquisa;

	private DominioSimNao excInfecPesquisa;
	
	private DominioSimNao consCliPesquisa;

	private AinQuartosVO quartoVO;
	
	private AinLeitos ainLeitos = null;
	
	@Inject @Paginator
	private DynamicDataModel<AinQuartos> dataModel;	
	
	private final String PAGE_CAD_QUARTO = "quartoCRUD";
	
	private boolean exibirPanelQuartoLeito;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);	
		quartoVO = new AinQuartosVO();
	}
	
	public String novo(){		
		exibirPanelQuartoLeito = false;
		return PAGE_CAD_QUARTO;
	}
	
	public String editar(){	
		exibirPanelQuartoLeito = false;
		return PAGE_CAD_QUARTO;
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.codigoQuartoPesquisa = null;
		this.clinicaPesquisa = null;
		this.excInfecPesquisa = null;
		this.consCliPesquisa = null;
		this.descricaoQuartoPesquisa = null;
		exibirPanelQuartoLeito = false;
		dataModel.limparPesquisa();
	}

	public void carregarQuarto(AinQuartosVO vo) {
		exibirPanelQuartoLeito = true;
		quartoVO = vo;
//		Collections.sort(this.cadastrosBasicosInternacaoFacade.pesquisaLeitosPorNroQuarto(this.quartoVO.getNumero()), COMPARATOR_LEITOS);
	} 

	@Override
	public Long recuperarCount() {		 
		return cadastrosBasicosInternacaoFacade.pesquisaQuartosCount(codigoQuartoPesquisa, clinicaPesquisa, excInfecPesquisa, consCliPesquisa, descricaoQuartoPesquisa);
	}
	
	@Override
	public List<AinQuartosVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		return this.cadastrosBasicosInternacaoFacade
				.pesquisaQuartosNew(firstResult, maxResults, orderProperty,
						asc, codigoQuartoPesquisa,
						clinicaPesquisa,
						excInfecPesquisa,
						consCliPesquisa,descricaoQuartoPesquisa);

	}
	
	public List<AinLeitosVO>  pesquisaLeitosPorNroQuarto(Short nroQuarto ){
		return this.cadastrosBasicosInternacaoFacade.pesquisaLeitosPorNroQuarto(nroQuarto);
	}
	
	
	/**
	 * Método que retorna uma coleções de clínicas p/ preencher a suggestion
	 * box, de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghClinicas> pesquisarClinicas(String paramPesquisa) {
		return this.aghuFacade.pesquisarClinicas(paramPesquisa == null ? null
				: paramPesquisa);
	}

	// ### GETs e SETs ###


	public Short getCodigoQuartoPesquisa() {
		return codigoQuartoPesquisa;
	}

	public void setCodigoQuartoPesquisa(Short codigoQuartoPesquisa) {
		this.codigoQuartoPesquisa = codigoQuartoPesquisa;
	}

	public AghClinicas getClinicaPesquisa() {
		return clinicaPesquisa;
	}

	public void setClinicaPesquisa(AghClinicas clinicaPesquisa) {
		this.clinicaPesquisa = clinicaPesquisa;
	}

	public DominioSimNao getExcInfecPesquisa() {
		return excInfecPesquisa;
	}

	public void setExcInfecPesquisa(DominioSimNao excInfecPesquisa) {
		this.excInfecPesquisa = excInfecPesquisa;
	}

	public DominioSimNao getConsCliPesquisa() {
		return consCliPesquisa;
	}

	public void setConsCliPesquisa(DominioSimNao consCliPesquisa) {
		this.consCliPesquisa = consCliPesquisa;
	}

	public AinLeitos getAinLeitos() {
		return ainLeitos;
	}

	public void setAinLeitos(AinLeitos ainLeitos) {
		this.ainLeitos = ainLeitos;
	}
	
	public String getDescricaoQuartoPesquisa() {
		return descricaoQuartoPesquisa;
	}

	public void setDescricaoQuartoPesquisa(String descricaoQuartoPesquisa) {
		this.descricaoQuartoPesquisa = descricaoQuartoPesquisa;
	}

	public DynamicDataModel<AinQuartos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AinQuartos> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isExibirPanelQuartoLeito() {
		return exibirPanelQuartoLeito;
	}

	public void setExibirPanelQuartoLeito(boolean exibirPanelQuartoLeito) {
		this.exibirPanelQuartoLeito = exibirPanelQuartoLeito;
	}

	public AinQuartosVO getQuartoVO() {
		return quartoVO;
	}

	public void setQuartoVO(AinQuartosVO quartoVO) {
		this.quartoVO = quartoVO;
	}
}
