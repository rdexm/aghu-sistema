package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.dominio.DominioTipoVisaoAnalise;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.SomatorioAnaliseCustosObjetosCustoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseOtimizacaoVO;
import br.gov.mec.aghu.sig.custos.vo.VisualizarAnaliseCustosObjetosCustoVO;


public class ManterAnaliseObjetosCustoPaginatorController extends ActionController implements ActionPaginator {

	private static final String MANTER_OBJETOS_CUSTO = "manterObjetosCusto";

	private static final String DETALHE_ANALISE_VISAO_OBJETO_CUSTO = "detalheAnaliseVisaoObjetoCusto";

	@Inject @Paginator
	private DynamicDataModel<VisualizarAnaliseCustosObjetosCustoVO> dataModel;

	private static final Log LOG = LogFactory.getLog(ManterAnaliseObjetosCustoPaginatorController.class);

	private static final long serialVersionUID = -4149810378759799176L;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private Integer seqProcessamentoCusto;
	private Integer codigoCentroCusto;

	private SigProcessamentoCusto competencia;
	private FccCentroCustos fccCentroCustos;
	private DominioTipoVisaoAnalise tipoVisaoAnaliseItens;
	private String nomeProdutoServico;
	private SigCentroProducao sigCentroProducao;

	private Integer seqObjetoCustoVersao;
	private Integer seqCentroCusto;
	private Integer numeroLinha;
	private Integer seqCompetencia;
	private SomatorioAnaliseCustosObjetosCustoVO somatorioAnaliseCustosObjetosCustoVO;
	
	private DominioTipoCentroProducaoCustos[] tiposCentroProducao;
	
	
	private VisualizarAnaliseOtimizacaoVO visualizarAnaliseOtimizacaoVO;
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
		if (this.tipoVisaoAnaliseItens == null) {	
			this.tipoVisaoAnaliseItens = DominioTipoVisaoAnalise.OBJETO_CUSTO;
			if (this.seqProcessamentoCusto != null) {
				this.competencia = this.custosSigProcessamentoFacade.obterProcessamentoCusto(this.seqProcessamentoCusto);
			}
			try {
				RapServidores servidorLogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
				if (this.codigoCentroCusto != null) {
					this.fccCentroCustos = this.centroCustoFacade.obterCentroCustoPorChavePrimaria(this.codigoCentroCusto);
				} else if (servidorLogado.getCentroCustoAtuacao() != null) { 
					this.fccCentroCustos = servidorLogado.getCentroCustoAtuacao(); 
				} else {
					this.fccCentroCustos = servidorLogado.getCentroCustoLotacao(); 
				}
			} catch (ApplicationBusinessException e) {
				this.fccCentroCustos = null; 
			}
			if (this.fccCentroCustos != null) { 
				this.sigCentroProducao = this.getFccCentroCustos().getCentroProducao(); 
			}
		}
	}

	public String detalheCusto() {
		return DETALHE_ANALISE_VISAO_OBJETO_CUSTO;
	}

	public String manterCusto() {
		return MANTER_OBJETOS_CUSTO;
	}
	
	public void carregarOtimizacao(){
		if(this.visualizarAnaliseOtimizacaoVO == null){
			this.visualizarAnaliseOtimizacaoVO = this.custosSigProcessamentoFacade.buscarCustosVisaoCentroCustosOtimizacao(this.competencia.getSeq(), this.fccCentroCustos,this.sigCentroProducao, tiposCentroProducao);
		}
	}
	
	@Override
	public Long recuperarCount() {
		if (this.tipoVisaoAnaliseItens == DominioTipoVisaoAnalise.OBJETO_CUSTO) {
			Integer codigoCentroCusto = this.fccCentroCustos != null ? fccCentroCustos.getCodigo() : null;
			somatorioAnaliseCustosObjetosCustoVO = this.custosSigProcessamentoFacade.obterSomatorioVisualizarObjetoCusto(this.competencia.getSeq(), codigoCentroCusto, this.nomeProdutoServico, this.sigCentroProducao, null, tiposCentroProducao);
			return this.custosSigProcessamentoFacade.buscarCustosVisaoObjetoCustosCount(this.competencia.getSeq(), codigoCentroCusto , this.nomeProdutoServico, this.sigCentroProducao, this.tiposCentroProducao);
		} else if (this.tipoVisaoAnaliseItens == DominioTipoVisaoAnalise.CENTRO_CUSTO) {
			this.carregarOtimizacao();
			somatorioAnaliseCustosObjetosCustoVO = visualizarAnaliseOtimizacaoVO.getSomatorio();
			return visualizarAnaliseOtimizacaoVO.contar();
			//somatorioAnaliseCustosObjetosCustoVO = this.custosSigProcessamentoFacade.obterSomatorioVisualizarCentroCusto(this.competencia.getSeq(), this.fccCentroCustos,this.sigCentroProducao, tiposCentroProducao);
			//return this.custosSigProcessamentoFacade.buscarCustosVisaoCentroCustosCount(this.competencia.getSeq(), this.fccCentroCustos, this.sigCentroProducao, this.tiposCentroProducao);
		}
		return 0L;
	}

	
	
	@Override
	public List<VisualizarAnaliseCustosObjetosCustoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		this.seqCompetencia = this.competencia.getSeq();
		if (this.tipoVisaoAnaliseItens == DominioTipoVisaoAnalise.OBJETO_CUSTO) {
			Integer codigoCentroCusto = this.fccCentroCustos != null ? fccCentroCustos.getCodigo() : null;
			return this.custosSigProcessamentoFacade.buscarCustosVisaoObjetoCustos(firstResult, maxResult, this.competencia.getSeq(), codigoCentroCusto, this.nomeProdutoServico, this.sigCentroProducao, tiposCentroProducao);
		} else if (this.tipoVisaoAnaliseItens == DominioTipoVisaoAnalise.CENTRO_CUSTO) {
			/*return this.custosSigProcessamentoFacade.buscarCustosVisaoCentroCustos(firstResult, maxResult, this.competencia.getSeq(), this.fccCentroCustos, this.sigCentroProducao, this.tiposCentroProducao);*/
			this.carregarOtimizacao();
			return visualizarAnaliseOtimizacaoVO.paginar(firstResult, maxResult);
		}
		return new ArrayList<VisualizarAnaliseCustosObjetosCustoVO>();
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		if (this.getSigCentroProducao() != null) {
			return this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, this.sigCentroProducao.getSeq(), null);
		} else {
			return this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
		}
	}
	
	public List<SigProcessamentoCusto> listarCompetencias(){
		return this.custosSigProcessamentoFacade.pesquisarCompetencia(DominioSituacaoProcessamentoCusto.P, DominioSituacaoProcessamentoCusto.F);
	}

	public void limparCentroCusto() {
		this.setFccCentroCustos(null);
	}
	
	public void pesquisar() {
		this.visualizarAnaliseOtimizacaoVO = null;
		this.dataModel.reiniciarPaginator();
	}

	public void limpar() {
		//this.iniciar();
		this.limparCentroCusto();
		this.setSigCentroProducao(null);
		this.setTiposCentroProducao(null);
		this.setNomeProdutoServico(null);
		this.setAtivo(false);
	}

	public boolean validaTipoVisao(){
		if(DominioTipoVisaoAnalise.CENTRO_CUSTO == tipoVisaoAnaliseItens){
			return true;
		}
		return false;
	}
	
	public List<SigCentroProducao> listarCentroProducao() {
		return this.custosSigCadastrosBasicosFacade.pesquisarCentroProducao();
	}
	
	// Getters and Setters
	public FccCentroCustos getFccCentroCustos() {
		return this.fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public SigProcessamentoCusto getCompetencia() {
		return this.competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}

	public DominioTipoVisaoAnalise getTipoVisaoAnaliseItens() {
		return tipoVisaoAnaliseItens;
	}

	public void setTipoVisaoAnaliseItens(DominioTipoVisaoAnalise tipoVisaoAnaliseItens) {
		this.tipoVisaoAnaliseItens = tipoVisaoAnaliseItens;
	}

	public String getNomeProdutoServico() {
		return nomeProdutoServico;
	}

	public void setNomeProdutoServico(String nomeProdutoServico) {
		this.nomeProdutoServico = nomeProdutoServico;
	}

	public SigCentroProducao getSigCentroProducao() {
		return sigCentroProducao;
	}

	public void setSigCentroProducao(SigCentroProducao sigCentroProducao) {
		this.sigCentroProducao = sigCentroProducao;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}

	public Integer getNumeroLinha() {
		return numeroLinha;
	}

	public void setNumeroLinha(Integer numeroLinha) {
		this.numeroLinha = numeroLinha;
	}

	public Integer getSeqCompetencia() {
		return seqCompetencia;
	}

	public void setSeqCompetencia(Integer seqCompetencia) {
		this.seqCompetencia = seqCompetencia;
	}

	public Integer getSeqCentroCusto() {
		return seqCentroCusto;
	}

	public void setSeqCentroCusto(Integer seqCentroCusto) {
		this.seqCentroCusto = seqCentroCusto;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public Integer getSeqProcessamentoCusto() {
		return seqProcessamentoCusto;
	}

	public void setSeqProcessamentoCusto(Integer seqProcessamentoCusto) {
		this.seqProcessamentoCusto = seqProcessamentoCusto;
	}
	
	public DynamicDataModel<VisualizarAnaliseCustosObjetosCustoVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<VisualizarAnaliseCustosObjetosCustoVO> dataModel) {
	 this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public SomatorioAnaliseCustosObjetosCustoVO getSomatorioAnaliseCustosObjetosCustoVO() {
		return somatorioAnaliseCustosObjetosCustoVO;
	}

	public void setSomatorioAnaliseCustosObjetosCustoVO(
			SomatorioAnaliseCustosObjetosCustoVO somatorioAnaliseCustosObjetosCustoVO) {
		this.somatorioAnaliseCustosObjetosCustoVO = somatorioAnaliseCustosObjetosCustoVO;
	}

	public DominioTipoCentroProducaoCustos[] getTiposCentroProducao() {
		return tiposCentroProducao;
	}

	public void setTiposCentroProducao(DominioTipoCentroProducaoCustos[] tiposCentroProducao) {
		this.tiposCentroProducao = tiposCentroProducao;
	}

}
