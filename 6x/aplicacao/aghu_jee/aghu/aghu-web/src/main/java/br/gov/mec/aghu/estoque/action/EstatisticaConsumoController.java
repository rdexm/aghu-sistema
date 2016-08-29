package br.gov.mec.aghu.estoque.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.VScoComprMaterialVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.EstatisticaEstoqueAlmoxarifadoVO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class EstatisticaConsumoController extends ActionController {
	
	private static final String ULTIMAS_COMPRAS = "compras-ultimasCompras";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private static final Log LOG = LogFactory.getLog(EstatisticaConsumoController.class);

	private static final long serialVersionUID = 2045794245553047684L;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private Integer codigoMaterial;
	private SceAlmoxarifado almoxarifadoFiltro;
	private SceAlmoxarifado almoxLocalEstoque;
	private ScoFornecedor fornecedor;
	private ScoMaterial material;
	private EstatisticaEstoqueAlmoxarifadoVO estatistica;
	private String grupo;
	private MovimentoMaterialVO mvtodataCompetencia;
	private String origemConsumo;
	private Boolean mostrarComprasWeb;
	private AghParametros paramClassificacaoComprasWeb;
	private VScoComprMaterialVO dadosUltimaEntrega;

	/**
	 * Almoxarifado do historico de consumo
	 */
	private Short almSeqConsumo;

	private boolean pesquisou;
	private boolean filtroAberto = true;
	private boolean panelHistoricoAberto = true;
	private boolean panelEstatisticaAberto = true;
	private boolean panelUltimaEntregaAberto = true;

	private String voltarPara = null;
	
	private String banco = null;
	private String urlBaseWebForms = null;

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void iniciar() {
	 

	 

		
		if (this.voltarPara == null && !this.pesquisou && this.estatistica != null) {
			limparPesquisa();
		}
		
		try {
			this.paramClassificacaoComprasWeb = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRUPO_CLASSIF_COMPRAS_WEB);
		} catch (ApplicationBusinessException e) {
			getLog().error("parametro compras web nao cadastrado.");
		}

		// Quando o material for passado por parâmetro...
		if (this.codigoMaterial != null) {
			this.material = comprasFacade.obterScoMaterial(this.codigoMaterial);
			this.mostrarComprasWeb = comprasFacade.verificarComprasWeb(this.paramClassificacaoComprasWeb, this.material);
			this.filtroAberto = false;
			pesquisar();
		}
		this.popularParametros();
	}
	
	private void popularParametros() {
		try {
			if (aghuFacade.isHCPA()){
				AghParametros aghParametroUrlBaseWebForms = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS);
				if (aghParametroUrlBaseWebForms != null) {
					setUrlBaseWebForms(aghParametroUrlBaseWebForms.getVlrTexto());
					//setUrlBaseWebForms("http://10.10.30.25:9001/forms/frmservlet");
				}
				AghParametros aghParametrosBanco = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
				if (aghParametrosBanco != null) {
					setBanco(aghParametrosBanco.getVlrTexto());
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public Boolean validarUrlBaseWebFormsBanco(){
		return StringUtils.isBlank(urlBaseWebForms) || StringUtils.isBlank(banco);
	}
	
	public String obterTokenUsuarioLogadoAnestesia() {
		return super.obterTokenUsuarioLogado().toString();
	}

	
	public String ultimasCompras(){
		return ULTIMAS_COMPRAS;
	}
	

	/**
	 * Pesquisa principal
	 */
	public void pesquisar() {

		try {
			this.estatistica = null;

			if (getAlmoxarifadoFiltro() == null) {
				origemConsumo = "Consumo Geral Hospital";
				this.almoxLocalEstoque = this.material.getAlmoxarifado();
				this.almSeqConsumo = null;
			} else {
				origemConsumo = "Consumo almoxarifado: " + this.almoxarifadoFiltro.getSeq() + " " + this.almoxarifadoFiltro.getDescricao();
				this.almSeqConsumo = this.almoxarifadoFiltro.getSeq();
				this.almoxLocalEstoque = this.almoxarifadoFiltro;
			}

			Date dtComp = null;

			if (mvtodataCompetencia == null) {
				dtComp = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA).getVlrData();
				List<MovimentoMaterialVO> movimentos = pesquisarDatasCompetencias(new SimpleDateFormat("MM/yyyy").format(dtComp));

				if (movimentos != null && movimentos.size() > 0) {
					mvtodataCompetencia = movimentos.get(0);
				}

			} else {
				dtComp = mvtodataCompetencia.getCompetencia();
			}

			estatistica = this.estoqueFacade.obterEstatisticasAlmoxarifadoPorMaterialAlmoxDataComp(this.almoxLocalEstoque.getSeq(), almSeqConsumo, this.material.getCodigo(), dtComp);
			setDadosUltimaEntrega(this.comprasFacade.pesquisaUltimaEntrega(this.material.getCodigo()));
			this.filtroAberto = true;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.pesquisou = true;
		}
	}

	public String voltar() {
		String retorno = this.voltarPara;
		this.limparParametros();
		return retorno;
	}

	private void limparParametros() {
		this.codigoMaterial = null;
		this.voltarPara = null;
		this.filtroAberto = true;
		limparPesquisa();
	}

	protected Long recuperarCount() {
		Short almoxSeq = null;
		Integer numeroFornecedor = null;
		Integer codMaterial = null;

		if (this.getAlmoxarifadoFiltro() != null) {
			almoxSeq = this.getAlmoxarifadoFiltro().getSeq();
		}

		if (this.getFornecedor() != null) {
			numeroFornecedor = getFornecedor().getNumero();
		}

		if (this.getMaterial() != null) {
			codMaterial = this.getMaterial().getCodigo();
		}
		return this.estoqueFacade.pesquisarEstoqueMaterialPorAlmoxarifadoCount(almoxSeq, numeroFornecedor, codMaterial);
	}

	/**
	 * Limpa os filtros da pesquisa principal
	 */
	public void limparPesquisa() {
		this.mostrarComprasWeb = false;
		this.material = null;
		this.fornecedor = null;
		this.almoxarifadoFiltro = null;
		this.almoxLocalEstoque = null;
		this.fornecedor = null;
		this.origemConsumo = null;
		this.estatistica = null;
		this.mvtodataCompetencia = null;
		this.filtroAberto = true;
		this.almSeqConsumo = null;
		this.codigoMaterial = null;
		this.pesquisou = false;
		this.dadosUltimaEntrega = null;
	}

	// Metodo para pesquisa na suggestion box de almoxarifado
	public List<SceAlmoxarifado> obterSceAlmoxarifado(String objPesquisa) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(objPesquisa);
	}

	public String getEstocavel() {

		try {
			Short almCentral = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ALMOX_CENTRAL).getVlrNumerico().shortValue();

			SceEstoqueAlmoxarifado eal = this.estoqueFacade.obterEstoqueAlmoxarifadoEstocavelPorMaterialAlmoxarifadoFornecedor((
					this.almoxarifadoFiltro!=null?this.almoxarifadoFiltro.getSeq():almCentral), 
					this.material!=null?this.material.getCodigo():null, null);
			if (eal != null) {
				return DominioSimNao.getInstance(eal.getIndEstocavel()).getDescricao();
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return "";
	}

	// Metodo para pesquisa na suggestion box de material
	public List<ScoMaterial> listaEstoqueMaterialPorAlmoxarifado(String paramPesq) throws ApplicationBusinessException {
		Short almoSeq = (this.getAlmoxarifadoFiltro() != null) ? this.getAlmoxarifadoFiltro().getSeq() : null;
		return this.comprasFacade.pesquisaMateriaisPorParamAlmox(almoSeq, paramPesq);
	}

	/**
	 * Método que realiza a pesquisa de competencias de estoque geral, por mes e ano.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<MovimentoMaterialVO> pesquisarDatasCompetencias(String paramPesquisa) {
		List<MovimentoMaterialVO> lista = null;
		try {
			lista = estoqueFacade.pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(paramPesquisa);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return lista;
	}

	/**
	 * Pesquisas para suggestion box
	 */

	/**
	 * Obtem lista para sugestion box de fornecedores
	 * 
	 * @param param
	 * @return
	 */
	public List<ScoFornecedor> obterFornecedores(String param) {
		return comprasFacade.obterFornecedor(param);
	}

	/**
	 * Obtem lista para sugestion box de fornecedores eventuais
	 * 
	 * @param param
	 * @return
	 */
	public List<SceFornecedorEventual> obterFornecedoresEventuais(Object param) {
		return estoqueFacade.obterFornecedorEventual(param);
	}
	
	public void verificarComprasWeb() {
		if (this.material != null) {
			this.mostrarComprasWeb = comprasFacade.verificarComprasWeb(this.paramClassificacaoComprasWeb, this.material);
		} else {
			this.mostrarComprasWeb = false;
		}
	}
	
	public Boolean isHcpa() {
		return aghuFacade.isHCPA();
	}

	/*
	 * Getters e setters
	 */

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public boolean isFiltroAberto() {
		return filtroAberto;
	}

	public void setFiltroAberto(boolean filtroAberto) {
		this.filtroAberto = filtroAberto;
	}

	public boolean isPanelHistoricoAberto() {
		return panelHistoricoAberto;
	}

	public void setPanelHistoricoAberto(boolean panelHistoricoAberto) {
		this.panelHistoricoAberto = panelHistoricoAberto;
	}

	public boolean isPanelEstatisticaAberto() {
		return panelEstatisticaAberto;
	}

	public void setPanelEstatisticaAberto(boolean panelEstatisticaAberto) {
		this.panelEstatisticaAberto = panelEstatisticaAberto;
	}

	public EstatisticaEstoqueAlmoxarifadoVO getEstatistica() {
		return estatistica;
	}

	public void setEstatistica(EstatisticaEstoqueAlmoxarifadoVO estatistica) {
		this.estatistica = estatistica;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public MovimentoMaterialVO getMvtodataCompetencia() {
		return mvtodataCompetencia;
	}

	public void setMvtodataCompetencia(MovimentoMaterialVO mvtodataCompetencia) {
		this.mvtodataCompetencia = mvtodataCompetencia;
	}

	public String getOrigemConsumo() {
		return origemConsumo;
	}

	public void setOrigemConsumo(String origemConsumo) {
		this.origemConsumo = origemConsumo;
	}

	public Short getAlmSeqConsumo() {
		return almSeqConsumo;
	}

	public void setAlmSeqConsumo(Short almSeqConsumo) {
		this.almSeqConsumo = almSeqConsumo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public boolean isPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public SceAlmoxarifado getAlmoxarifadoFiltro() {
		return almoxarifadoFiltro;
	}

	public void setAlmoxarifadoFiltro(SceAlmoxarifado almoxarifadoFiltro) {
		this.almoxarifadoFiltro = almoxarifadoFiltro;
	}

	public SceAlmoxarifado getAlmoxLocalEstoque() {
		return almoxLocalEstoque;
	}

	public void setAlmoxLocalEstoque(SceAlmoxarifado almoxLocalEstoque) {
		this.almoxLocalEstoque = almoxLocalEstoque;
	}

	public Boolean getMostrarComprasWeb() {
		return mostrarComprasWeb;
	}

	public void setMostrarComprasWeb(Boolean mostrarComprasWeb) {
		this.mostrarComprasWeb = mostrarComprasWeb;
	}

	public AghParametros getParamClassificacaoComprasWeb() {
		return paramClassificacaoComprasWeb;
	}

	public void setParamClassificacaoComprasWeb(
			AghParametros paramClassificacaoComprasWeb) {
		this.paramClassificacaoComprasWeb = paramClassificacaoComprasWeb;
	}

	public VScoComprMaterialVO getDadosUltimaEntrega() {
		return dadosUltimaEntrega;
	}

	public void setDadosUltimaEntrega(VScoComprMaterialVO dadosUltimaEntrega) {
		this.dadosUltimaEntrega = dadosUltimaEntrega;
	}

	public boolean isPanelUltimaEntregaAberto() {
		return panelUltimaEntregaAberto;
	}

	public void setPanelUltimaEntregaAberto(boolean panelUltimaEntregaAberto) {
		this.panelUltimaEntregaAberto = panelUltimaEntregaAberto;
	}

	public static Log getLog() {
		return LOG;
	}
	
	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getUrlBaseWebForms() {
		return urlBaseWebForms;
	}

	public void setUrlBaseWebForms(String urlBaseWebForms) {
		this.urlBaseWebForms = urlBaseWebForms;
	}

}