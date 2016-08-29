package br.gov.mec.aghu.sig.custos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.core.action.ActionController;


public class DetalheAnaliseVisaoObjetoCustoController extends ActionController {

	private static final String PESQUISAR_ANALISE_OBJETOS_CUSTO = "pesquisarAnaliseObjetosCusto";
	
	private static final Log LOG = LogFactory.getLog(DetalheAnaliseVisaoObjetoCustoController.class);

	private static final long serialVersionUID = -8346981833567307418L;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@Inject
	private CustoVisaoGeralPaginatorController custoVisaoGeralPaginatorController;

	@Inject
	private CustoPessoalPaginatorController custoPessoalPaginatorController;

	@Inject
	private CustoInsumoPaginatorController custoInsumoPaginatorController;

	@Inject
	private CustoEquipamentoPaginatorController custoEquipamentoPaginatorController;

	@Inject
	private CustoServicoPaginatorController custoServicoPaginatorController;
	
	@Inject
	private CustoIndiretoPaginatorController custoIndiretoPaginatorController;

	private String tabSelecionada;

	private Short seqPagador;
	private Integer seqObjetoCustoVersao;
	private Integer seqCentroCusto;
	private FccCentroCustos fccCentroCustos;
	private Integer seqCompetencia;
	private DominioTipoVisaoAnalise tipoVisaoAnaliseItens;

	private SigProcessamentoCusto competencia;
	private VisualizarAnaliseCustosObjetosCustoVO visualizarAnaliseCustosObjetosCustoVO;
	
	private Integer firstResult;
	
	private Boolean paginaIniciada;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.competencia = this.custosSigProcessamentoFacade.obterProcessamentoCusto(this.seqCompetencia);
		this.atualizarFirstResult(); 
		
		if (this.seqCentroCusto != null && this.seqCentroCusto != 0) {
			this.fccCentroCustos = this.centroCustoFacade.obterCentroCustoPorChavePrimaria(this.seqCentroCusto);
		}
		
		if (this.seqObjetoCustoVersao != null && this.seqObjetoCustoVersao != 0 && this.seqCentroCusto != null) {
			this.setTipoVisaoAnaliseItens(DominioTipoVisaoAnalise.OBJETO_CUSTO);
			this.visualizarAnaliseCustosObjetosCustoVO = this.custosSigCadastrosBasicosFacade.obterDetalheVisualizacaoAnaliseOC(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto, this.seqPagador);
			this.iniciarAbasVisaoObjeto();
		} else {
			this.setTipoVisaoAnaliseItens(DominioTipoVisaoAnalise.CENTRO_CUSTO);
			this.visualizarAnaliseCustosObjetosCustoVO = this.custosSigCadastrosBasicosFacade.obterDetalheVisualizacaoAnaliseCC(this.seqCompetencia, this.fccCentroCustos);
			this.iniciarAbasVisaoCentro();
		}
	
	}

	private void atualizarFirstResult(){
		if(firstResult != null && tabSelecionada != null){
			if(tabSelecionada.equals("abaGeral")){
				this.custoVisaoGeralPaginatorController.iniciarAbaVisaoGeral(this.seqCompetencia, this.seqObjetoCustoVersao, this.fccCentroCustos);
			}
//			TODO MIGRAÇÃO verificar como setar o first result			
//			else if(tabSelecionada.equals("abaInsumo")){
//				this.custoPessoalPaginatorController.setFirstResult(this.firstResult);
//			}
//			else if(tabSelecionada.equals("abaPessoal")){
//				this.custoInsumoPaginatorController.setFirstResult(this.firstResult);
//			}
//			else if(tabSelecionada.equals("abaEquipamento")){
//				this.custoEquipamentoPaginatorController.setFirstResult(this.firstResult);
//			}
//			else if(tabSelecionada.equals("abaServico")){
//				this.custoServicoPaginatorController.setFirstResult(this.firstResult);
//			}
//			else if(tabSelecionada.equals("abaIndiretos")){
//				this.custoIndiretoPaginatorController.setFirstResult(this.firstResult);
//			}
		}
	}
	
	private void iniciarAbasVisaoObjeto() {
		this.paginaIniciada = true;
		this.custoVisaoGeralPaginatorController.iniciarAbaVisaoGeral(this.seqCompetencia, this.seqObjetoCustoVersao, this.fccCentroCustos);
		this.custoPessoalPaginatorController.iniciarAbaObjetoCusto(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto);
		this.custoInsumoPaginatorController.iniciarAbaObjetoCusto(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto);
		this.custoEquipamentoPaginatorController.iniciarAbaObjetoCusto(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto);
		this.custoServicoPaginatorController.iniciarAbaObjetoCusto(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto);
		this.custoIndiretoPaginatorController.iniciarAbaObjetoCusto(this.seqCompetencia, this.seqObjetoCustoVersao, this.seqCentroCusto);
	}

	private void iniciarAbasVisaoCentro() {
		this.paginaIniciada = true;
		this.custoVisaoGeralPaginatorController.iniciarAbaVisaoGeralCentroCusto(this.seqCompetencia, this.fccCentroCustos);
		this.custoPessoalPaginatorController.iniciarAbaCentroCusto(this.seqCompetencia, this.seqCentroCusto);
		this.custoInsumoPaginatorController.iniciarAbaCentroCusto(this.seqCompetencia, this.seqCentroCusto);
		this.custoEquipamentoPaginatorController.iniciarAbaCentroCusto(this.seqCompetencia, this.seqCentroCusto);
		this.custoServicoPaginatorController.iniciarAbaCentroCusto(this.seqCompetencia, this.seqCentroCusto);
		this.custoIndiretoPaginatorController.iniciarAbaCentroCusto(this.seqCompetencia, this.seqCentroCusto);
	}

	public String voltar() {
		return PESQUISAR_ANALISE_OBJETOS_CUSTO;
	}

	public String getTabSelecionada() {
		return tabSelecionada;
	}

	public void setTabSelecionada(String tabSelecionada) {
		this.tabSelecionada = tabSelecionada;
	}

	public Integer getSeqCompetencia() {
		return seqCompetencia;
	}

	public void setSeqCompetencia(Integer seqCompetencia) {
		this.seqCompetencia = seqCompetencia;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}

	public VisualizarAnaliseCustosObjetosCustoVO getVisualizarAnaliseCustosObjetosCustoVO() {
		return visualizarAnaliseCustosObjetosCustoVO;
	}

	public void setVisualizarAnaliseCustosObjetosCustoVO(VisualizarAnaliseCustosObjetosCustoVO visualizarAnaliseCustosObjetosCustoVO) {
		this.visualizarAnaliseCustosObjetosCustoVO = visualizarAnaliseCustosObjetosCustoVO;
	}

	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}

	public Integer getSeqCentroCusto() {
		return seqCentroCusto;
	}

	public void setSeqCentroCusto(Integer seqCentroCusto) {
		this.seqCentroCusto = seqCentroCusto;
	}

	public DominioTipoVisaoAnalise getTipoVisaoAnaliseItens() {
		return tipoVisaoAnaliseItens;
	}

	public void setTipoVisaoAnaliseItens(DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		this.tipoVisaoAnaliseItens = tipoVisaoAnaliseItens;
	}

	public Integer getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	public Boolean getPaginaIniciada() {
		return paginaIniciada;
	}

	public void setPaginaIniciada(Boolean paginaIniciada) {
		this.paginaIniciada = paginaIniciada;
	}

	public Short getSeqPagador() {
		return seqPagador;
	}

	public void setSeqPagador(Short seqPagador) {
		this.seqPagador = seqPagador;
	}
}
