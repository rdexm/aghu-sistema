package br.gov.mec.aghu.estoque.action;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioGerarRelatoriosExcel;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoConsultaRelatorioEntrada;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.EntradaSaidaSemLicitacaoVO;
import br.gov.mec.aghu.estoque.vo.EntregaMateriaisMarcaDivergenteAFVO;
import br.gov.mec.aghu.estoque.vo.FiltroRelatorioEntradasServicosCentroCustoVO;
import br.gov.mec.aghu.estoque.vo.NotasRecebimentoGeradasMesVO;
import br.gov.mec.aghu.estoque.vo.TipoMovimentoVO;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

public class GeracaoRelatoriosExcelController extends ActionController {

	/**
	 * SERIAL ID
	 */
	private static final long serialVersionUID = -5209868508076127624L;
	
	private static final String EXTENSAO = ".csv";
	private static final String UNDERLINE = "_";
	//private static final String FORMATO_DDMMAAAA = "ddMMyyyy";
	
	private static final Log LOG = LogFactory.getLog(GeracaoRelatoriosExcelController.class);
	
	public static Log getLog() {
		return LOG;
	}

//	@EJB
//	private IComprasFacade comprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
//	@EJB
//	private ICentroCustoFacade centroCustoFacade;
	
//	@EJB
//	private IOrcamentoFacade orcamentoFacade;
	
	private DominioGerarRelatoriosExcel tipoRelatorio;
	
	private DominioGerarRelatoriosExcel relatorioSelecionado = null;
	
	private FiltroRelatorioEntradasServicosCentroCustoVO filtroES = new FiltroRelatorioEntradasServicosCentroCustoVO();
	
	private Boolean afsPendentes;
	
	private Integer[] listaNaturezaDespesa;
	
	private Map<String,Object> mapNaturezaDespesa = new LinkedHashMap<String,Object>();
	
	private DominioTipoConsultaRelatorioEntrada tipoEntrada;
	
	private DominioTipoConsultaRelatorioEntrada entradaSelecionada = null;
	
	private String fileName;
	
	private Boolean gerarArquivo = Boolean.FALSE;
	
	private String fileHeader;
	
	// Filtro MDAF #31467
	private EntregaMateriaisMarcaDivergenteAFVO filtroMD = new EntregaMateriaisMarcaDivergenteAFVO();
	
	// Filtro NRGM #34323
	private NotasRecebimentoGeradasMesVO filtroNR = new NotasRecebimentoGeradasMesVO();
	
	// Filtro NRGM #34163
	private EntradaSaidaSemLicitacaoVO filtroSL = new EntradaSaidaSemLicitacaoVO();
	
	private enum GeracaoRelatoriosExcelControllerExceptionCode implements BusinessExceptionCode {
		SELECIONE_UM_RELATORIO_EXCEL, NENHUM_REGISTRO_ENCONTRADO_EXCEL, PERIODO_INICIAL_MAIOR_PERIODO_FINAL_EXCEL;
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation, true);
	}
	
	/**
	 * Retorna os tipos de relatórios a serem apresentados na tela para a seleção.
	 * 
	 * @return Coleção de {@link DominioGerarRelatoriosExcel}
	*/
	public DominioGerarRelatoriosExcel[] listarTipoRelatorio() {
		 return new DominioGerarRelatoriosExcel[] {
			DominioGerarRelatoriosExcel.ESMP,
			DominioGerarRelatoriosExcel.ESCC,
			DominioGerarRelatoriosExcel.ESSL,
			DominioGerarRelatoriosExcel.MDAF,
			DominioGerarRelatoriosExcel.NRGM
		 };
	}
	
	/**
	 * Seleciona o tipo de relatório que será gerado e apresenta os filtros equivalentes.
	 */
	public void selecionarTipoRelatorio() {
		switch (tipoRelatorio) {
		case ESMP:
			this.resetarFiltroESMP();
			//this.carregarMapNaturezaDespesa();
			this.relatorioSelecionado = DominioGerarRelatoriosExcel.ESMP;
			break;
		case ESCC:
			this.resetarFiltroESCC();
			this.relatorioSelecionado = DominioGerarRelatoriosExcel.ESCC;
			break;
		case ESSL:
			this.filtroSL = new EntradaSaidaSemLicitacaoVO();
			this.carregarMapTipoMovimento();
			this.relatorioSelecionado = DominioGerarRelatoriosExcel.ESSL;
			break;
		case MDAF:
			this.filtroMD = new EntregaMateriaisMarcaDivergenteAFVO();
			this.relatorioSelecionado = DominioGerarRelatoriosExcel.MDAF;
			break;
		case NRGM:
			this.filtroNR = new NotasRecebimentoGeradasMesVO();
			this.relatorioSelecionado = DominioGerarRelatoriosExcel.NRGM;
			break;
		default:
			this.relatorioSelecionado = null;
			break;
		}
	}

	/**
	 * Limpa os campos de filtro do Relatorio ES - Entrada de Serviços no Periodo.
	 * #35689
	 */
	private void resetarFiltroESMP() {
		this.entradaSelecionada = null;
		this.afsPendentes = false;
		this.listaNaturezaDespesa = null;
		this.filtroES = new FiltroRelatorioEntradasServicosCentroCustoVO();
	}

	/**
	 * Limpa os campos de filtro do Relatorio Entradas de Serviços por Centro de Custo.
	 * #35688
	 */
	private void resetarFiltroESCC() {
		this.entradaSelecionada = null;
		this.filtroES = new FiltroRelatorioEntradasServicosCentroCustoVO();
	}
	
	/**
	 * Seleciona o tipo de entrada (serviço ou material) e exibe o suggestion box equivalente.
	 * #35688, #35689
	 */
	public void selecionarServicoMaterial() {
		if (this.filtroES != null) {
			switch (this.filtroES.getTipoEntrada()) {
			case C:
				resetarSuggestionBoxServico();
				this.entradaSelecionada = DominioTipoConsultaRelatorioEntrada.C;
				break;
			case S:
				resetarSuggestionBoxMaterial();
				this.entradaSelecionada = DominioTipoConsultaRelatorioEntrada.S;
				break;
			default:
				this.entradaSelecionada = null;
				break;
			}
		}
	}

	/**
	 * Limpa a suggestion box de {@link ScoServico}.
	 * #35688, #35689
	 */
	private void resetarSuggestionBoxServico() {
		if (this.filtroES != null) {
			this.filtroES.setScoServico(null);
		}
	}

	/**
	 * Limpa a suggestion box de {@link ScoMaterial}.
	 * #35688, #35689
	 */
	private void resetarSuggestionBoxMaterial() {
		if (this.filtroES != null) {
			this.filtroES.setScoMaterial(null);
		}
	}

	/**
	 * Ação do botão Download CSV.
	 * 
	 * @throws BaseException 
	 * @throws IOException 
	 * @throws ApplicationBusinessException
	 */
	public void gerarCSV() throws BaseException, ApplicationBusinessException {
		
		try{
			if (this.tipoRelatorio == null) {
				apresentarMsgNegocio(GeracaoRelatoriosExcelControllerExceptionCode.SELECIONE_UM_RELATORIO_EXCEL.toString());
			} else {
				this.fileName = "";
				this.fileHeader = "";
				
				if (this.relatorioSelecionado == DominioGerarRelatoriosExcel.ESMP) {
					gerarRelatorioExcelESMP();
				}
				
				if (this.relatorioSelecionado == DominioGerarRelatoriosExcel.ESCC) {
					gerarRelatorioExcelESCC();
				}
				
				if (this.relatorioSelecionado == DominioGerarRelatoriosExcel.ESSL) {
					gerarRelatorioExcelESSL();
				}
				
//				if (this.tipoRelatorio == DominioGerarRelatoriosExcel.MDAF || this.tipoRelatorio == DominioGerarRelatoriosExcel.NRGM) {
//					
//					if (this.tipoRelatorio == DominioGerarRelatoriosExcel.MDAF){
//					
//						this.fileName = estoqueFacade.gerarRelatoriosExcelMDAF(this.filtroMD);
//						this.fileHeader = DominioNomeRelatorio.RELATORIO_CSV_NRS_MARCAS_DIVERGENTES.getDescricao() + UNDERLINE + DateUtil.obterDataFormatada(new Date(), "ddMMyyyy") + EXTENSAO;
//						
//					} else if (this.tipoRelatorio == DominioGerarRelatoriosExcel.NRGM) {
//						
//						this.fileName = estoqueFacade.gerarRelatoriosExcelNRGM(this.filtroNR);
//						String mesAnoCompetencia = (filtroNR != null && filtroNR.getMesAnoFiltro() != null ? DateUtil.obterDataFormatada(filtroNR.getMesAnoFiltro(), "MMyyyy") : DateUtil.obterDataFormatada(new Date(), "MMyyyy")); 
//						this.fileHeader = DominioNomeRelatorio.RELATORIO_CSV_NRS_MES.getDescricao() + UNDERLINE + mesAnoCompetencia + EXTENSAO;
//						
//					}
//					if(this.fileName == null){
//						apresentarMsgNegocio(GeracaoRelatoriosExcelControllerExceptionCode.NENHUM_REGISTRO_ENCONTRADO_EXCEL.toString());
//						return;
//					} else {
//						this.gerarArquivo = true;
//					}
//					
//				}
			} 
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
					e.getLocalizedMessage()));
		}
	}
	
	/**
	 * Gera relatório de Entradas de Serviços por Centro de Custo.
	 * #35688
	 * @throws IOException
	 * @throws BaseException
	 */
	private void gerarRelatorioExcelESCC() throws IOException, BaseException {
		
//		if (!this.estoqueFacade.validarDatasFiltroRelatorioExcel(this.filtroES)) {
//			apresentarMsgNegocio(GeracaoRelatoriosExcelControllerExceptionCode.PERIODO_INICIAL_MAIOR_PERIODO_FINAL_EXCEL.toString());
//		} else {
//		
//			this.fileName = this.estoqueFacade.gerarRelatorioExcelESCC(this.filtroES);
//			
//			if (this.fileName == null) {
//				apresentarMsgNegocio(GeracaoRelatoriosExcelControllerExceptionCode.NENHUM_REGISTRO_ENCONTRADO_EXCEL.toString());
//			} else {
//				
//				this.fileHeader = DominioNomeRelatorio.ARQUIVO_DADOS_SCE_ES.getDescricao() + UNDERLINE 
//						+ DateUtil.obterDataFormatada(this.filtroES.getPeriodoInicial(), FORMATO_DDMMAAAA) + UNDERLINE 
//						+ DateUtil.obterDataFormatada(this.filtroES.getPeriodoFinal(), FORMATO_DDMMAAAA) + EXTENSAO;
//				
//				this.gerarArquivo = true;
//			}
//		}
	}
	
	/**
	 * Gera relatório de Entrada de Serviços no Período.
	 * #35689
	 * @throws IOException
	 * @throws BaseException
	 */
	private void gerarRelatorioExcelESMP() throws IOException, BaseException {
		
//		if (!this.estoqueFacade.validarDatasFiltroRelatorioExcel(this.filtroES)) {
//			apresentarMsgNegocio(GeracaoRelatoriosExcelControllerExceptionCode.PERIODO_INICIAL_MAIOR_PERIODO_FINAL_EXCEL.toString());
//		} else {
//			
//			this.fileName = this.estoqueFacade.gerarRelatorioExcelESMP(this.filtroES, this.listaNaturezaDespesa, this.afsPendentes);
//			
//			if (this.fileName == null) {
//				apresentarMsgNegocio(GeracaoRelatoriosExcelControllerExceptionCode.NENHUM_REGISTRO_ENCONTRADO_EXCEL.toString());
//			} else {
//				
//				this.fileHeader = DominioNomeRelatorio.ARQUIVO_DADOS_SCE_ES.getDescricao() + UNDERLINE 
//						+ DateUtil.obterDataFormatada(this.filtroES.getPeriodoInicial(), FORMATO_DDMMAAAA) + UNDERLINE 
//						+ DateUtil.obterDataFormatada(this.filtroES.getPeriodoFinal(), FORMATO_DDMMAAAA) + EXTENSAO;
//				
//				this.gerarArquivo = true;
//			}
//		}
	}
	
	
	/**
	 * Gera relatório de Entrada e Saida Sem Licitação.
	 * #35689
	 * @throws IOException
	 * @throws BaseException
	 */
	private void gerarRelatorioExcelESSL() throws IOException, BaseException {
		
		if (this.filtroSL != null) {
			
			try {
				this.estoqueFacade.validaRegras(this.filtroSL);	
				this.fileName = this.estoqueFacade.gerarRelatoriosExcelESSL(this.filtroSL);
				
				if (this.fileName == null) {
					apresentarMsgNegocio(GeracaoRelatoriosExcelControllerExceptionCode.NENHUM_REGISTRO_ENCONTRADO_EXCEL.toString());
				} else {
					
					if(this.filtroSL.getMmtDataComp() != null){
						this.fileHeader = DominioNomeRelatorio.RELATORIO_CSV_ESSL.getDescricao() + UNDERLINE 
								+ DateUtil.obterDataFormatada(this.filtroSL.getMmtDataComp(), "MMyyyy") + EXTENSAO;
					}else{
						this.fileHeader = DominioNomeRelatorio.RELATORIO_CSV_ESSL.getDescricao() + UNDERLINE 
								+ DateUtil.obterDataFormatada(this.filtroSL.getDataInicial(), "ddMMyy") + UNDERLINE 
								+ DateUtil.obterDataFormatada(this.filtroSL.getDataFinal(), "ddMMyy") + EXTENSAO;
					}
					this.gerarArquivo = true;
				}
			} catch (ApplicationBusinessException e) {			
				apresentarExcecaoNegocio(e);
			}
		}
		
		this.filtroSL = new EntradaSaidaSemLicitacaoVO();
		carregarMapTipoMovimento();
	}
	
	/**
	 * Faz Downlaod do Arquivo CSV
	 */
	public void dispararDownload(){
		
		try {
			download(this.fileName, this.fileHeader);
			setGerarArquivo(Boolean.FALSE);
			this.fileName = null;
			this.fileHeader = null;
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
		}
		
	}

	/**
	 * Ação do botão Limpar.
	 */
	public void limpar() {
		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		
		while (componentes.hasNext()) {
			
			limparValoresSubmetidos(componentes.next());
		}
		
		this.tipoRelatorio = null;
		this.relatorioSelecionado = null;
		this.filtroMD = null;
		this.entradaSelecionada = null;
		this.filtroES = null;
		this.filtroNR = null;
		this.filtroSL = null;
		this.listaNaturezaDespesa = null;
		this.afsPendentes = false;
	}
	
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	/**
	 * Método para carregar {@link Map} com descrição e código de {@link FsoGrupoNaturezaDespesa}.
	 * #35689
	 */
//	public void carregarMapNaturezaDespesa() {
//		this.mapNaturezaDespesa = new LinkedHashMap<String, Object>();
//		for (FsoGrupoNaturezaDespesa naturezaDespesa : this.orcamentoFacade.obterNaturezasDespesaAtivas()) {
//			this.mapNaturezaDespesa.put(naturezaDespesa.getCodigo() + " - " + naturezaDespesa.getDescricao(), 
//					naturezaDespesa.getCodigo());
//		}
//	}
	
	/**
	 * Método para carregar {@link Map} com descrição e código de {@link FsoGrupoNaturezaDespesa}.
	 * #34348
	 */
	public void carregarMapTipoMovimento() {
		this.filtroSL.setMapTipoMovimento(new LinkedHashMap<String, Object>());
		for (TipoMovimentoVO tipoMovimento : this.estoqueFacade.obterTipoMovimentoListBox()) {
			this.filtroSL.getMapTipoMovimento().put(tipoMovimento.getSigla(), tipoMovimento.getSeq());
		}
	}
	
	/**
	 * Método para carregar sugestionBox de {@link ScoMaterial}.
	 * #35688, #35689
	 * @param parametro - O que foi digitado no sugestionBox que será utilizado para consulta.
	 * @return Lista de Materiais de acordo com valor digitado.
	 */
//	public List<ScoMaterial> listarScoMaterial(String parametro) {
//		return returnSGWithCount(this.comprasFacade.obterMateriaisPorCodigoOuNome(parametro), 
//								 this.comprasFacade.obterMateriaisPorCodigoOuNomeCount(parametro));
//	}

	/**
	 * Método para carregar sugestionBox de {@link ScoServico}.
	 * #35688, #35689
	 * @param parametro - O que foi digitado no sugestionBox que será utilizado para consulta.
	 * @return Lista de Serviços de acordo com valor digitado.
	 */
//	public List<ScoServico> listarScoServico(String parametro) {
//		return returnSGWithCount(this.comprasFacade.obterServicosPorCodigoOuNome(parametro), 
//								 this.comprasFacade.obterServicosPorCodigoOuNomeCount(parametro));
//	}
	
	/**
	 * Método para carregar sugestionBox de {@link FccCentroCustos}.
	 * #35688, #35689
	 * @param parametro - O que foi digitado no sugestionBox que será utilizado para consulta.
	 * @return Lista de Centros de Custos de acordo com valor digitado.
	 */
//	public List<FccCentroCustos> listarFccCentroCustos(String parametro) {
//		return returnSGWithCount(this.centroCustoFacade.obterCentrosCustosPorCodigoOuDescricao(parametro), 
//								 this.centroCustoFacade.obterCentrosCustosPorCodigoOuDescricaoCount(parametro));
//	}

	/**
	 * Método para carregar sugestionBox de de {@link ScoMarcaComercial}.
	 * #31476
	 * @param parametro
	 * @return Lista de Marca Comercial de acordo com valor digitado.
	 */
//	public List<ScoMarcaComercial> listaScoMarcaComercial(String parametro) {
//		
//		return  this.returnSGWithCount(this.comprasFacade.obterMarcaComercialPorCodigoDescricao(parametro),this.comprasFacade.obterMarcaComercialPorCodigoDescricaoCount(parametro));
//	}
	
	/**
	 * Método para carregar sugestionBox de {@link ScoMaterial}.
	 * #31476 
	 * @param parametro - O que foi digitado no sugestionBox que será utilizado para consulta.
	 * @return Lista de Materiais de acordo com valor digitado.
	 */
//	public List<MaterialMDAFVO> listarScoMaterialCodigoNomeDescricao(String parametro) {
//
//		return returnSGWithCount(this.comprasFacade.obterScoMaterialPorCodigoNomeDescricao(parametro), this.comprasFacade.obterScoMaterialPorCodigoNomeDescricaoCount(parametro));
//	}

	public Boolean getGerarArquivo() {
		return gerarArquivo;
	}

	public void setGerarArquivo(Boolean gerarArquivo) {
		this.gerarArquivo = gerarArquivo;
	}

	/**
	 * Método para carregar sugestionBox de de {@link ScoMarcaComercial}.
	 * #31476 
	 * @param parametro
	 * @return Lista de Grupo Material de acordo com valor digitado.
	 */
//	public List<ScoGrupoMaterial> listaScoGrupoMaterial(String grupoMaterial) {
//		
//		return  this.returnSGWithCount(this.comprasFacade.obterGrupoMaterialPorSeqDescricaoOrderDescricao(grupoMaterial),this.comprasFacade.obterGrupoMaterialPorSeqDescricaoOrderDescricaoCount(grupoMaterial));
//	}
	
	
	/**
	 * Lista SuggestionBox Fornecedor
	 * #31476 
	 * @param parametro
	 * @return
	 */
//	public List<FornecedorVO> listaFornecedor(String fornecedor) {
//		
//		return  this.returnSGWithCount(this.comprasFacade.listarFornecedoresPorNumCnpjCpfRazaoSoc(fornecedor),this.comprasFacade.listarFornecedoresPorNumCnpjCpfRazaoSocCount(fornecedor));
//	}

	/**
	 * 
	 * GETs and SETs
	 * 
	 */
	public DominioGerarRelatoriosExcel getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(DominioGerarRelatoriosExcel tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public DominioGerarRelatoriosExcel getRelatorioSelecionado() {
		return relatorioSelecionado;
	}

	public void setRelatorioSelecionado(DominioGerarRelatoriosExcel relatorioSelecionado) {
		this.relatorioSelecionado = relatorioSelecionado;
	}

	public DominioTipoConsultaRelatorioEntrada getTipoEntrada() {
		return tipoEntrada;
	}

	public void setTipoEntrada(DominioTipoConsultaRelatorioEntrada tipoEntrada) {
		this.tipoEntrada = tipoEntrada;
	}

	public DominioTipoConsultaRelatorioEntrada getEntradaSelecionada() {
		return entradaSelecionada;
	}

	public void setEntradaSelecionada(DominioTipoConsultaRelatorioEntrada entradaSelecionada) {
		this.entradaSelecionada = entradaSelecionada;
	}

	public EntregaMateriaisMarcaDivergenteAFVO getFiltroMD() {
		return filtroMD;
	}

	public void setFiltroMD(EntregaMateriaisMarcaDivergenteAFVO filtroMD) {
		this.filtroMD = filtroMD;
	}

	public FiltroRelatorioEntradasServicosCentroCustoVO getFiltroES() {
		return filtroES;
	}

	public void setFiltroES(FiltroRelatorioEntradasServicosCentroCustoVO filtroES) {
		this.filtroES = filtroES;
	}

	public String getFileHeader() {
		return fileHeader;
	}

	public void setFileHeader(String fileHeader) {
		this.fileHeader = fileHeader;
	}

	public Map<String,Object> getMapNaturezaDespesa() {
		return mapNaturezaDespesa;
	}

	public void setMapNaturezaDespesa(Map<String,Object> mapNaturezaDespesa) {
		this.mapNaturezaDespesa = mapNaturezaDespesa;
	}

	public Integer[] getListaNaturezaDespesa() {
		return listaNaturezaDespesa;
	}

	public void setListaNaturezaDespesa(Integer[] listaNaturezaDespesa) {
		this.listaNaturezaDespesa = listaNaturezaDespesa;
	}

	public Boolean getAfsPendentes() {
		return afsPendentes;
	}

	public void setAfsPendentes(Boolean afsPendentes) {
		this.afsPendentes = afsPendentes;
	}
	
	public NotasRecebimentoGeradasMesVO getFiltroNR() {
		return filtroNR;
	}

	public void setFiltroNR(NotasRecebimentoGeradasMesVO filtroNR) {
		this.filtroNR = filtroNR;
	}

	public EntradaSaidaSemLicitacaoVO getFiltroSL() {
		return filtroSL;
	}

	public void setFiltroSL(EntradaSaidaSemLicitacaoVO filtroSL) {
		this.filtroSL = filtroSL;
	}
	
	
}
