package br.gov.mec.aghu.estoque.action;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.EntradaMateriasDiaVO;
import br.gov.mec.aghu.estoque.vo.RelatorioDiarioMateriaisComSaldoAteVinteDiasVO;
import br.gov.mec.aghu.estoque.vo.RotinaDiariaMateriaisExceptionCode;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

import com.itextpdf.text.DocumentException;


public class RotinaDiariaMateriaisController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RotinaDiariaMateriaisController.class);

	private static final long serialVersionUID = -5590739332664571751L;
	
	private static final String EXTENSAO=".csv";
	private static final String SIGLA = "MS";
	private static final String NOME_ARQUIVO ="Rotina_Diaria_Materiais";
	private static final String ENCODE="ISO-8859-1";
	
	private static final String SUBREPORT_DIR = "SUBREPORT_DIR";
	private static final String ORADB_CONSUMO_MAT_MES = "ORADB_CONSUMO_MAT_MES";
	private static final String ORADB_CONSUMO_MAT_DIA = "ORADB_CONSUMO_MAT_DIA";
	private static final String ORADB_ENTR_ACUM_PATR_MES = "ORADB_ENTR_ACUM_PATR_MES";
	private static final String ORADB_ENTR_ACUM_SERV_MES = "ORADB_ENTR_ACUM_SERV_MES";
	private static final String ORADB_ENTR_ACUM_CONSUMO_MES = "ORADB_ENTR_ACUM_CONSUMO_MES";
	private static final String ORADB_ENTR_SERV_DIA = "ORADB_ENTR_SERV_DIA";
	private static final String ORADB_ENTR_CONSUMO_DIA = "ORADB_ENTR_CONSUMO_DIA";
	private static final String ORADB_TOT_GERAL_DIFERENCA = "ORADB_TOT_GERAL_DIFERENCA";
	private static final String ORADB_TOT_GERAL_DEVOLUCOES = "ORADB_TOT_GERAL_DEVOLUCOES";
	private static final String ORADB_TOT_GERAL_COMPRAS = "ORADB_TOT_GERAL_COMPRAS";
	private static final String P_DT_GERACAO = "P_DT_GERACAO";
	private static final String P_HOSPITAL_RAZAO_SOCIAL = "P_HOSPITAL_RAZAO_SOCIAL";

	private Date dataInicio;
	
	private DominioNomeRelatorio relatorio;
	
	private String fileName;
	
	private String origem;
	
	private Boolean gerouArquivo;
	
	private String titlePdfView;
	
	private Map<String, Object> parametrosEspecificos;
	
	private String nomeRelatorio;
	
	private String nomeRelatorioRodape;
	
	private String nomeArquivoRelatorio;
	
	private List<?> dadosRelatorio = null;
	
	private Integer duracaoEstoque;
	
	private Double percentualAjuste = null;

	private Boolean btImprimirEntradaMaterias;
	
	private Boolean btDesabilitarExportarCsv;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;	
	
	private boolean rederizaBtCsv;

	/**
	 * 
	 * @author aghu
	 *
	 */
	public enum EnumTargetRotinaDiariaMateriais {
		TITLE_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS,
		CABECALHO_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS,
		CABECALHO_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS_COM_ESTQ_SEGURANCA,
		LABEL_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS,
		RODAPE_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS,		
		ROTINA_DIARIA_MATERIAIS_VISUALIZACAO_RELATORIO,
		MENSAGEM_SUCESSO_IMPRESSAO,
		TITLE_RELATORIO_ENTRADA_MATERIAIS_NO_DIA;
	}
	
	/**
	 * 
	 * @author aghu
	 *
	 */
	private enum RotinaDiariaMateriaisControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS;
	}
	
	/**
	 * 
	 */
	public void selecionarRelatorio() {
		if(getRelatorio() != null && getRelatorio().equals(DominioNomeRelatorio.RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS)) {
			try {
				setDuracaoEstoque(recuperarLimiteDiasDuracaoEstoque());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		if(getRelatorio() != null && getRelatorio().equals(DominioNomeRelatorio.RELATORIO_ENTRADA_MATERIAIS_NO_DIA)){
			this.rederizaBtCsv = true;
		}else{
			this.rederizaBtCsv = false;
		}
	}

	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return getNomeArquivoRelatorio();
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return dadosRelatorio;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			

			if(relatorio != null) {
				switch(relatorio){
					case RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS:
						String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
						params.put("nomeInstituicao", hospital);
						params.put("nomeRelatorio", recuperarCabecalhoRelatorioMateriaisSaldoAteDias());
						params.put("nomeRelatorioRodape", WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.RODAPE_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS.toString(), null, (Object[])null));
						break;
					case RELATORIO_ENTRADA_MATERIAIS_NO_DIA:	
						AghParametros nomeHospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
						params.put(P_HOSPITAL_RAZAO_SOCIAL, nomeHospital.getVlrTexto());						
						params.put(P_DT_GERACAO, dataInicio);
						params.put(ORADB_TOT_GERAL_COMPRAS, estoqueFacade.obterTotalGeralComprasDia(dataInicio));
						params.put(ORADB_TOT_GERAL_DEVOLUCOES, estoqueFacade.obterTotalGeralDevolucoesDia(dataInicio));
						params.put(ORADB_TOT_GERAL_DIFERENCA, estoqueFacade.obterTotalDiferencaFormula(dataInicio));
						params.put(ORADB_ENTR_CONSUMO_DIA, estoqueFacade.obterQuantidadeMateriaisConsumidosDia(dataInicio));
						params.put(ORADB_ENTR_SERV_DIA, estoqueFacade.obterQuantidadeServicosDia(dataInicio));
						params.put(ORADB_ENTR_ACUM_CONSUMO_MES, estoqueFacade.obterQuantidadeMateriaisConsumoEntradaDia(dataInicio));
						params.put(ORADB_ENTR_ACUM_SERV_MES, estoqueFacade.obterQuantidadeServicosMes(dataInicio));
						params.put(ORADB_ENTR_ACUM_PATR_MES, estoqueFacade.obterQuantidadeServicosEntradaMes(dataInicio));
						params.put(ORADB_CONSUMO_MAT_DIA, estoqueFacade.consumoMatDiaFormula(dataInicio));
						params.put(ORADB_CONSUMO_MAT_MES, estoqueFacade.consumoMatMesFormula(dataInicio));						
						break;
				}
			}
					
			if (getParametrosEspecificos() != null) {
				params.putAll(getParametrosEspecificos());
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		return params;
	}	
	
	public String recuperarLabelRelatorioMateriaisSaldoAteDias() {
		String label = "";
		try {
			if(getDuracaoEstoque() == null || getDuracaoEstoque().intValue() == 0) {
				label = WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.LABEL_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS.toString(),
						null, recuperarLimiteDiasDuracaoEstoque());	
			} else {
				label = WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.LABEL_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS.toString(),
						null, getDuracaoEstoque());	
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return label;
	}
	
	private String recuperarTituloRelatorioMateriaisSaldoAteDias() {
		String titulo = "";
		try {
			if(getDuracaoEstoque() == null || getDuracaoEstoque().intValue() == 0) {
				titulo = WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.TITLE_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS.toString(),
						null, recuperarLimiteDiasDuracaoEstoque());						
			} else {
				titulo = WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.TITLE_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS.toString(),
						null, getDuracaoEstoque());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return titulo;
	}
	
//	private String recuperarMessages(String key, Object... params) {
//		final StatusMessage message = new StatusMessage(null, key, null, null, null);
//		if (!message.isEmpty()) {
//			message.interpolate(params);
//		}
//		return message.getSummary();
//	}
	
	public String recuperarCabecalhoRelatorioMateriaisSaldoAteDias() throws ApplicationBusinessException {
		String titulo = "";
		try {
			//obtém parâmetro para saber se o relatório possui ou não percentual de ajuste
			if(percentualAjuste == null){
				percentualAjuste = recuperarPercentualAjuste();
			}
			if(getDuracaoEstoque() == null || getDuracaoEstoque().intValue() == 0) {
				if(percentualAjuste.equals(0d)){
					titulo = WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.CABECALHO_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS.toString(),
							null, recuperarLimiteDiasDuracaoEstoque());	
				}
				else{
					titulo =  WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.CABECALHO_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS_COM_ESTQ_SEGURANCA.toString(),
							null, percentualAjuste);

				}
			} else {
				if(percentualAjuste.equals(0d)){
					titulo = WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.CABECALHO_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS.toString(),
							null, getDuracaoEstoque());
				}
				else{
					titulo = WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.CABECALHO_RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS_COM_ESTQ_SEGURANCA.toString(),
							null, getDuracaoEstoque(),percentualAjuste);							
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return titulo;
	}
	
	/**
	 * Recupera o percentual de ajuste conforme melhoria #20596
	 * @return
	 * @throws ApplicationBusinessException
	 * @author bruno.mourao
	 * @since 01/10/2012
	 */
	private Double recuperarPercentualAjuste() throws ApplicationBusinessException {
			AghParametros paramPercAjuste = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PERCENTUAL_AJUSTE_SEGURANCA);
			Double percAjuste = 0d;
			if(paramPercAjuste != null && paramPercAjuste.getVlrNumerico() != null){
				if(paramPercAjuste.getVlrNumerico().compareTo(BigDecimal.ZERO) >= 0 && paramPercAjuste.getVlrNumerico().compareTo(new BigDecimal(100)) < 0 ){
					percAjuste = paramPercAjuste.getVlrNumerico().doubleValue();
				}
				else{
					if(paramPercAjuste.getVlrNumericoPadrao() != null){
						percAjuste = paramPercAjuste.getVlrNumericoPadrao().doubleValue();
					}
					else{
						percAjuste = 0d;
					}
				}
			}
		return percAjuste;
	}
	
	private Integer recuperarLimiteDiasDuracaoEstoque() throws ApplicationBusinessException {
		return parametroFacade.buscarAghParametro(AghuParametrosEnum.P_LIMITE_DIAS_DURACAO_ESTOQUE).getVlrNumerico().intValue();
	}
	
	/**
	 * Retorna os relatórios a serem apresentados na tela
	 * @return
	 */
	public DominioNomeRelatorio[] listarRelatorios() {
		return new DominioNomeRelatorio[] {
			DominioNomeRelatorio.RELATORIO_SOLICITACOES_COMPRA_MATERIAL_ESTOCAVEL,
			DominioNomeRelatorio.RELATORIO_MOVIMENTO_MATERIAIS,
			DominioNomeRelatorio.RELATORIO_AFS_NAO_EFETIVADAS,
			
			//TODO
			// verificar como será feito
			DominioNomeRelatorio.RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS,
			//
			
			DominioNomeRelatorio.RELATORIO_MATERIAIS_COM_CONTRATO_SALDO_ATE_VINTE_DIAS,
			DominioNomeRelatorio.RELATORIO_MATERIAIS_ALMOXARIFADO_SALDO_ATE_VINTE_DIAS,
			DominioNomeRelatorio.RELATORIO_ENTRADA_MATERIAIS_NO_DIA,
			DominioNomeRelatorio.RELATORIO_PRODUCAO_INTERNA_MATERIAIS,
			DominioNomeRelatorio.RELATORIO_AFS_SEM_EMPENHO_EMP_PARCIAL_COM_TOTAL,
			DominioNomeRelatorio.RELATORIO_SCS_PLANEJAMENTO_COMPRAS,
			DominioNomeRelatorio.RELATORIO_GERACAO_SOLICITACOES_COMPRAS_MATERIAL_ESTOCAVEL,
			DominioNomeRelatorio.RELATORIO_GERACAO_SOLICITACOES_COMPRAS_MATERIAL_DIRETOS,
			DominioNomeRelatorio.RELATORIO_EFETIVACOES_NO_DIA,
			DominioNomeRelatorio.RELATORIO_AFS_SEM_EMPENHO_EMP_PARCIAL,
			DominioNomeRelatorio.RELATORIO_AFS_GERADAS_ALTERADAS_EXCLUIDAS,
			DominioNomeRelatorio.RELATORIO_MATERIAIS_GRAFICA_EM_PONTO_PEDIDO,
			DominioNomeRelatorio.RELATORIO_TITULOS_EXTORNADOS,
			DominioNomeRelatorio.RELATORIO_CONSUMO_MATERIAIS_NO_DIA,
			DominioNomeRelatorio.RELATORIO_EMPENHOS_GERADOS_ALTERADOS,
			DominioNomeRelatorio.RELATORIO_MATERIAIS_EM_CONSIGNADO_COM_ESTOQUE_ATE_VINTE_DIAS
		};
	}
	

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório.
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 */
	public String print() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		btImprimirEntradaMaterias = false;
		
		if(relatorio == null){
			apresentarMsgNegocio(Severity.ERROR, RotinaDiariaMateriaisExceptionCode.REPORT_SELECIONADO.toString());
		
		
		return null;
		} 
		String retorno = null; 
		
		if(relatorio != null) {
			switch(relatorio){
				case RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS:
					dadosRelatorio = pesquisarMateriaisComSaldoAteVinteDias();
					break;
				case RELATORIO_ENTRADA_MATERIAIS_NO_DIA:
					btImprimirEntradaMaterias = true;
					try {
						List<EntradaMateriasDiaVO> dados = pesquisarEntradaMateriaisNoDia();
						dadosRelatorio = dados;
						if (dados != null && dados.get(0).getListaDadosEntradaMateriasDia().isEmpty() || dados.get(0).getListaGrupoEntradaMateriasDia().isEmpty()){
							exibirMensagemPesquisaSemDados();
							return null;
						}
							
					}catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
						return null;
					}
					break;
			}
		}
		
		if(dadosRelatorio == null || dadosRelatorio.isEmpty()) {
			exibirMensagemPesquisaSemDados();			
		} else {			
			DocumentoJasper documento = gerarDocumento();
			retorno = "rotinaDiariaMateriaisPdf";
			
			if (relatorio.equals(DominioNomeRelatorio.RELATORIO_ENTRADA_MATERIAIS_NO_DIA)){				
				this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
			}else{
				media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));				
			}
		}
		
		return retorno;
	}

	/**
	 * Renderiza o PDF
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws JRException
	 * @throws DocumentException
	 * @throws ApplicationBusinessException 
	 * @throws BaseException
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/**
	 * 
	 */
	/**
	 * Realiza a impressão direta do relatório
	 */
	public void impressaoDireta(){
		if(relatorio == null){
			apresentarMsgNegocio(Severity.ERROR, RotinaDiariaMateriaisExceptionCode.REPORT_SELECIONADO.toString());
			return;
		}
		try {
			if(print() != null) {
				DocumentoJasper documento = gerarDocumento();
				sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, EnumTargetRotinaDiariaMateriais.MENSAGEM_SUCESSO_IMPRESSAO.toString());
			}				
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
		}
	}
	
	/**
	 * Gera o arquivo CSV para o relatório
	 */
	public void gerarCSV() {
		if(relatorio == null){
			apresentarMsgNegocio(Severity.ERROR, RotinaDiariaMateriaisExceptionCode.REPORT_SELECIONADO.toString());
			return;
		}
		try {
			switch(relatorio){
				case RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS:
					List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> dados = pesquisarMateriaisComSaldoAteVinteDias();
					fileName = estoqueFacade.gerarCSVRelatorioMateriaisComSaldoAteVinteDias(dados);
					break;
			}
			setGerouArquivo(Boolean.TRUE);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}
	
	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload(){ 
		if(fileName != null){
			try {
				
				final FacesContext fc = FacesContext.getCurrentInstance();
				final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition","attachment;filename=" + montarNomeArquivo() + EXTENSAO);
				response.getCharacterEncoding();
				
				final OutputStream out = response.getOutputStream();				
				
				String arquivoString = estoqueFacade.efetuarDownloadCSVRelatorioMateriaisComSaldoAteVinteDias(fileName);				
				
				final Scanner scanner = new Scanner(arquivoString);
				
				while (scanner.hasNextLine()){
					out.write(scanner.nextLine().getBytes(ENCODE));
					out.write(System.getProperty("line.separator").getBytes(ENCODE));
				}
				scanner.close();
				out.flush();
				out.close();
				fc.responseComplete();				
				
				setGerouArquivo(Boolean.FALSE);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
	
	
	private String montarNomeArquivo() {
		String dataFormatada = DateFormatUtil.formataDiaMesAnoParaNomeArquivo(Calendar.getInstance().getTime());
		 LOG.info(dataFormatada);
		return NOME_ARQUIVO + "_" + SIGLA + "_CPE_" + dataFormatada;
	}
	
	
	/**
	 * Adiciona msg 
	 */
	private void exibirMensagemPesquisaSemDados(){		
		apresentarMsgNegocio(RotinaDiariaMateriaisControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
	}
	
	/**
	 * 
	 */
	public void limparCampos() {
		setDataInicio(null);
		setRelatorio(null);
		setGerouArquivo(Boolean.FALSE);
		setDuracaoEstoque(null);
		this.rederizaBtCsv = false; 
		relatorio = null; 
		dataInicio = null;
	}

	/**
	 * Controla habilitar/desabilitar do campo dataInicio
	 * @return
	 */
	public Boolean isRelatorioMateriaisSaldo(){
		if(getRelatorio() != null && getRelatorio().equals(DominioNomeRelatorio.RELATORIO_MATERIAIS_SALDO_ATE_VINTE_DIAS)) {
			setDuracaoEstoque(null);
			return Boolean.TRUE;			
		}
		return Boolean.FALSE;
	}
	

	/**
	 * Obtém dados para relatório "Materiais com Saldo até 20 dias"
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> pesquisarMateriaisComSaldoAteVinteDias() throws ApplicationBusinessException {
		List<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO> dados = new ArrayList<RelatorioDiarioMateriaisComSaldoAteVinteDiasVO>();
		HashMap<String, Object> params = new HashMap<String, Object>(); 
		setTitlePdfView(recuperarTituloRelatorioMateriaisSaldoAteDias());
		setNomeRelatorio(recuperarCabecalhoRelatorioMateriaisSaldoAteDias());
		setNomeArquivoRelatorio("br/gov/mec/aghu/estoque/report/relatorioDiarioMateriaisComSaldoAteVinteDias.jasper");
		setParametrosEspecificos(params);
		try {
			if(percentualAjuste == null){
				percentualAjuste = recuperarPercentualAjuste();
			}
			dados = estoqueFacade.pesquisarMateriaisComSaldoAteVinteDias(getDuracaoEstoque(),percentualAjuste/100);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return dados;
	}
	
	public void gerarSolicitacaoCompraMatEstocaveis(){
		try {	
			
			List<String> msgs = solicitacaoComprasFacade.gerarSolicitacaoCompraAlmox(dataInicio);
			for(String msg : msgs){
				this.apresentarMsgNegocio(Severity.WARN, msg, new Object[]{});
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	
	/**
	 * Obtém dados para relatório "Materiais com Saldo até 20 dias"
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<EntradaMateriasDiaVO> pesquisarEntradaMateriaisNoDia() throws ApplicationBusinessException {
		EntradaMateriasDiaVO dados = new EntradaMateriasDiaVO();		
		HashMap<String, Object> params = new HashMap<String, Object>(); 
		
			dados = estoqueFacade.pesquisarEntradaMateriasDia(dataInicio);		
			titlePdfView = recuperarTituloRelatorioEntradaMateriaisNoDia();
			setNomeArquivoRelatorio("");
			setNomeArquivoRelatorio("br/gov/mec/aghu/estoque/report/entradaMateriaisDia.jasper");
			params.put(SUBREPORT_DIR, "br/gov/mec/aghu/estoque/report/");
			setParametrosEspecificos(params);
			
			List<EntradaMateriasDiaVO> saidaDados = new ArrayList<EntradaMateriasDiaVO>();
			
			saidaDados.add(dados);
			
			return saidaDados;
	}
	
	
	private String recuperarTituloRelatorioEntradaMateriaisNoDia() {
		String titulo = "";
		titulo = WebUtil.initLocalizedMessage(EnumTargetRotinaDiariaMateriais.TITLE_RELATORIO_ENTRADA_MATERIAIS_NO_DIA.toString(),
					null, null);
		return titulo;
	}
	
	
	public Boolean isDesabilitarBotaoCsv(){		
		if(getRelatorio() != null && getRelatorio().equals(DominioNomeRelatorio.RELATORIO_ENTRADA_MATERIAIS_NO_DIA)) {
			return Boolean.TRUE;			
		}
		return Boolean.FALSE;
	}

	public DominioNomeRelatorio getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(DominioNomeRelatorio relatorio) {
		this.relatorio = relatorio;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getTitlePdfView() {
		return titlePdfView;
	}

	public void setTitlePdfView(String titlePdfView) {
		this.titlePdfView = titlePdfView;
	}

	public Map<String, Object> getParametrosEspecificos() {
		return parametrosEspecificos;
	}

	public void setParametrosEspecificos(Map<String, Object> parametrosEspecificos) {
		this.parametrosEspecificos = parametrosEspecificos;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public String getNomeRelatorioRodape() {
		return nomeRelatorioRodape;
	}

	public void setNomeRelatorioRodape(String nomeRelatorioRodape) {
		this.nomeRelatorioRodape = nomeRelatorioRodape;
	}

	public String getNomeArquivoRelatorio() {
		return nomeArquivoRelatorio;
	}

	public void setNomeArquivoRelatorio(String nomeArquivoRelatorio) {
		this.nomeArquivoRelatorio = nomeArquivoRelatorio;
	}

	public List<?> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(List<?> dados) {
		this.dadosRelatorio = dados;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getOrigem() {
		return origem;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date data) {
		this.dataInicio = data;
	}

	public Integer getDuracaoEstoque() {
		return duracaoEstoque;
	}

	public void setDuracaoEstoque(Integer duracaoEstoque) {
		this.duracaoEstoque = duracaoEstoque;
	}

	public Boolean getBtImprimirEntradaMaterias() {
		return btImprimirEntradaMaterias;
	}

	public void setBtImprimirEntradaMaterias(Boolean btImprimirEntradaMaterias) {
		this.btImprimirEntradaMaterias = btImprimirEntradaMaterias;
	}

	public Boolean getBtDesabilitarExportarCsv() {
		return btDesabilitarExportarCsv;
	}

	public void setBtDesabilitarExportarCsv(Boolean btDesabilitarExportarCsv) {
		this.btDesabilitarExportarCsv = btDesabilitarExportarCsv;
	}

	public boolean isRederizaBtCsv() {
		return rederizaBtCsv;
	}

	public void setRederizaBtCsv(boolean rederizaBtCsv) {
		this.rederizaBtCsv = rederizaBtCsv;
	}
}
