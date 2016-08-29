package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.CotacaoPrecoVO;
import br.gov.mec.aghu.compras.vo.FiltroConsSCSSVO;
import br.gov.mec.aghu.compras.vo.ItensSCSSVO;
import br.gov.mec.aghu.dominio.DominioMaterialSC;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;


public class CotarPrecoController extends ActionController {

	private static final String IMPRIMIR_PROPOSTA_FORNECEDOR_PDF = "imprimirPropostaFornecedorPdf";

	private static final Log LOG = LogFactory.getLog(CotarPrecoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6311943654533755944L;

	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private ImprimirPropostaFornecedorController imprimirPropostaFornecedorController;

	private ScoMaterial material;

	private ScoSolicitacaoDeCompra solicitacaoCompra;

	private boolean suggestionSolicitacaoCompra;

	private boolean suggestionMaterial;

	private DominioMaterialSC tipoSolicitacao;

	private boolean preencherQuantidade;

	private boolean ativo;

	private CotacaoPrecoVO vo;

	private List<CotacaoPrecoVO> listaCotacaoPreco;
	
	private boolean recarregar = true;
	
	private FccCentroCustos centroCustoSolicitante;	
	private FccCentroCustos centroCustoAplicacao;
	private ScoPontoParadaSolicitacao pontoParadaProxima;
	
	private Date dataInicioGeracao;
	private Date dataFimGeracao;
	
	private FiltroConsSCSSVO filtro = new FiltroConsSCSSVO();
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

	 

		if (recarregar){
			ativo = false;
			suggestionSolicitacaoCompra = false;
			suggestionMaterial = true;
			tipoSolicitacao = DominioMaterialSC.MT;
			listaCotacaoPreco = new ArrayList<CotacaoPrecoVO>();
			//this.//setIgnoreInitPageConfig(true);
		}
		
		
	
	}
	

	public void posSelecaoMaterial() {
		montarVO(true, getMaterial().getUnidadeMedida(), null, null, getMaterial());

	}

	public void posSelecaoSolicitacaoCompra() {
			montarVO(false, getSolicitacaoCompra().getUnidadeMedida(), getSolicitacaoCompra().getQtdeSolicitada(), getSolicitacaoCompra().getNumero(), getSolicitacaoCompra().getMaterial());
	}
	
	public void posDelete() {
		setVo(new CotacaoPrecoVO());
	}
	
	public boolean renderedAdicionarTodos(){
		return (vo != null && vo.getMaterial() == null &&	solicitacaoCompra == null && suggestionSolicitacaoCompra && 
				(centroCustoSolicitante != null || centroCustoAplicacao != null || 
				 pontoParadaProxima != null || dataInicioGeracao != null  || dataFimGeracao != null));
				
	}

	private void montarVO(final boolean preencherQuantidade, final ScoUnidadeMedida unidade, final Long quantidade, final Integer numero, final ScoMaterial material) {
		vo = new CotacaoPrecoVO();
		setPreencherQuantidade(preencherQuantidade);
		getVo().setSolicitacaoCompra(numero);
		if (getListaCotacaoPreco() == null) {
			getVo().setSeq(1);
		} else {
			getVo().setSeq(getListaCotacaoPreco().size() + 1);
		}
		
		if (unidade != null) {
			getVo().setUnidade(unidade.getCodigo());
			if (unidade.getDescricao() != null) {
				getVo().setUnidadeDescricao(unidade.getDescricao());
			}

		}
		
		if (quantidade != null) {
			getVo().setQuantidade(quantidade);
		}
	
		if (material != null) {
			if (StringUtils.isNotBlank(material.getDescricao())) {
				getVo().setMaterial(material.getNome() + "-" + material.getDescricao());
				getVo().setMaterialDescricao(material.getDescricao());
			} else {
				getVo().setMaterial(material.getNome());
				
			}
			
			if (getVo().getMaterial().length() > 50) {
				getVo().setMaterialTruncado(getVo().getMaterial().substring(0, 47)+"...");
			}else{
				getVo().setMaterialTruncado(getVo().getMaterial());
			}
			getVo().setMaterialLabel(material.getNome());
			getVo().setCodigoMaterial(material.getCodigo());
		}
	}

	public void novaCotacao() {
		limpar();	
		suggestionMaterial = true;
		suggestionSolicitacaoCompra = false;
		ativo = false;
		preencherQuantidade = false;
		tipoSolicitacao = null;
		listaCotacaoPreco = new ArrayList<CotacaoPrecoVO>();
		
		
	}

	public String imprimir() throws DocumentException {
		imprimirPropostaFornecedorController.setListaVO(listaCotacaoPreco);
		try {
			imprimirPropostaFornecedorController.print();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.error(e.getMessage(),e);
		} catch (SystemException e) {
			LOG.error(e.getMessage(),e);
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
		}
		return IMPRIMIR_PROPOSTA_FORNECEDOR_PDF;
	}

	public void adicionar() {
		if (getVo() != null) {
			if (getVo().getQuantidade() == null && isPreencherQuantidade()) {
				this.apresentarMsgNegocio("tfQuantidade", Severity.ERROR, "CAMPO_OBRIGATORIO");
			} else {
				listaCotacaoPreco.add(getVo());
				ativo = true;
				limpar();
			}
		}
	}
	
	public void adicionarTodos() {
		filtro.setCentroCustoSolicitante(centroCustoSolicitante);
		filtro.setCentroCustoAplicacao(centroCustoAplicacao);
		filtro.setEfetivada(DominioSimNao.N);
		filtro.setIndExclusao(false);
		filtro.setPontoParadaAtual(pontoParadaProxima);
		filtro.setDataInicioSolicitacao(dataInicioGeracao);
		filtro.setDataFimSolicitacao(dataFimGeracao);
		
		List<ItensSCSSVO> listaSC = this.comprasFacade.listarSCItensSCSSVO(0, 2000, null, false, this.filtro);
		
		for(ItensSCSSVO itemSC: listaSC){
			ScoSolicitacaoDeCompra sc = this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(itemSC.getNumero());		
			ScoUnidadeMedida unidadeMedida = this.comprasCadastrosBasicosFacade.obterUnidadeMedida(sc.getUnidadeMedida().getCodigo());
			ScoMaterial material = this.comprasFacade.obterMaterialPorId(sc.getMaterial().getCodigo());
			montarVO(false, unidadeMedida, sc.getQtdeSolicitada(), sc.getNumero(), material);
			listaCotacaoPreco.add(getVo());
		}
		
		ativo = true;
		limpar();
	}
	
	

	public void limpar() {
		vo = new CotacaoPrecoVO();
		material  = null;
		solicitacaoCompra = null;
		recarregar = true;
		centroCustoSolicitante = null;	
		centroCustoAplicacao = null;
		pontoParadaProxima = null;
		dataInicioGeracao = null;
		dataFimGeracao = null;
	}

	public void excluir(CotacaoPrecoVO vo) {
		listaCotacaoPreco.remove(vo);
		List<CotacaoPrecoVO> listaAux = new LinkedList<CotacaoPrecoVO>();
		for (CotacaoPrecoVO cotacao : listaCotacaoPreco) {
			cotacao.setSeq(listaCotacaoPreco.indexOf(cotacao)+1 );
			listaAux.add(cotacao);
		}
		listaCotacaoPreco = listaAux;
		if (listaCotacaoPreco.size() == 0) {
			setAtivo(false);
		}
	}	
		
	public void carregarSuggestion() {
		// Material
		if (getTipoSolicitacao() != null && DominioMaterialSC.MT.equals(getTipoSolicitacao())) {
			setSuggestionMaterial(true);
			setSuggestionSolicitacaoCompra(false);
		} else {
			setSuggestionMaterial(false);
			setSuggestionSolicitacaoCompra(true);
			setPreencherQuantidade(false);
		}
	}
	
	public List<FccCentroCustos> listarCentroCustos(String filter) {

		String srtPesquisa = (String) filter;
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(srtPesquisa),listarCentroCustosCount(filter));
	}

	public Long listarCentroCustosCount(String filter) {

		String srtPesquisa = (String) filter;
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount(srtPesquisa);
	}

	//Método para carregar suggestion Ponto Parada
	public List<ScoPontoParadaSolicitacao> pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao(String pontoParadaSolic) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricao((String) pontoParadaSolic),pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount(pontoParadaSolic));
	}

	//Método para carregar suggestion ponto parada count
	public Long pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount(String pontoParadaSolic) {
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoCount((String) pontoParadaSolic);
	}

	public void setTipoSolicitacao(DominioMaterialSC tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public DominioMaterialSC getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setSuggestionSolicitacaoCompra(
			boolean suggestionSolicitacaoCompra) {
		this.suggestionSolicitacaoCompra = suggestionSolicitacaoCompra;
	}

	public boolean isSuggestionSolicitacaoCompra() {
		return suggestionSolicitacaoCompra;
	}

	public void setSuggestionMaterial(boolean suggestionMaterial) {
		this.suggestionMaterial = suggestionMaterial;
	}

	public boolean isSuggestionMaterial() {
		return suggestionMaterial;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	public CotacaoPrecoVO getVo() {
		return vo;
	}

	public void setVo(CotacaoPrecoVO vo) {
		this.vo = vo;
	}

	public List<CotacaoPrecoVO> getListaCotacaoPreco() {
		return listaCotacaoPreco;
	}

	public void setListaCotacaoPreco(List<CotacaoPrecoVO> listaCotacaoPreco) {
		this.listaCotacaoPreco = listaCotacaoPreco;
	}

	public void setPreencherQuantidade(boolean preencherQuantidade) {
		this.preencherQuantidade = preencherQuantidade;
	}

	public boolean isPreencherQuantidade() {
		return preencherQuantidade;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public List<ScoSolicitacaoDeCompra> listarSolicitacaoCompra(String filter) {
		return this.pacFacade.obterSolicitacoesCompraPorNumero(filter);
	}

	public List<ScoMaterial> listarMaterial(String filter) {
		return this.comprasFacade.listarScoMateriaisAtivos(filter);
	}

	public boolean isRecarregar() {
		return recarregar;
	}

	public void setRecarregar(boolean recarregar) {
		this.recarregar = recarregar;
	}

	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitante;
	}

	public void setCentroCustoSolicitante(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitante = centroCustoSolicitante;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public ScoPontoParadaSolicitacao getPontoParadaProxima() {
		return pontoParadaProxima;
	}

	public void setPontoParadaProxima(ScoPontoParadaSolicitacao pontoParadaProxima) {
		this.pontoParadaProxima = pontoParadaProxima;
	}

	public Date getDataInicioGeracao() {
		return dataInicioGeracao;
	}

	public void setDataInicioGeracao(Date dataInicioGeracao) {
		this.dataInicioGeracao = dataInicioGeracao;
	}

	public Date getDataFimGeracao() {
		return dataFimGeracao;
	}

	public void setDataFimGeracao(Date dataFimGeracao) {
		this.dataFimGeracao = dataFimGeracao;
	}

	public FiltroConsSCSSVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroConsSCSSVO filtro) {
		this.filtro = filtro;
	}

}
