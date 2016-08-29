package br.gov.mec.aghu.faturamento.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.RateioValoresSadtPorPontosVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelatorioRateioValoresSadtPorPontosController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6102508939734694471L;
	
	private static final String CAMINHO_ARQUIVO_RELATORIO = "br/gov/mec/aghu/faturamento/report/relatorioRateioValoresSadtPorPontos.jasper";
	
	private static final String PAGINA_PESQUISA_RELATORIO = "faturamento-relatorioRateioValoresSadtPorPontos";
	
	private static final String PAGINA_RELATORIO_PDF = "faturamento-relatorioRateioValoresSadtPorPontosPdf";
	
	private static final String EXTENSAO=".csv";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private Integer ano;

	private Integer mes;

	private Date dataHoraInicio;

	private StreamedContent media;
	
	private List<RateioValoresSadtPorPontosVO> colecao = new ArrayList<RateioValoresSadtPorPontosVO>();
	
	private FatCompetencia competencia;

	@PostConstruct
	protected void inicializar() {
		begin(conversation, true);
	}
	
	/**
	 * Ação do botão Visualizar Impressão.
	 * 
	 * @return
	 * @throws ApplicationBusinessException 
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 */
	public String visualizarImpressao() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		
		this.inicializarReport();
		
		this.colecao = recuperarColecao();
		
		if (this.colecao == null || this.colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_RETORNADO");
			return null;
		} else {
			DocumentoJasper documento = gerarDocumento();
			this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
		}
		
		return PAGINA_RELATORIO_PDF;
	}
	
	/**
	 * Ação do botão Imprimir.
	 * 
	 * @throws ApplicationBusinessException 
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 */
	public void imprimir() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		
		this.inicializarReport();
		
		this.colecao = recuperarColecao();
		
		if (this.colecao == null || this.colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_RETORNADO");
		} else {
			this.directPrint();
		}
	}

	/**
	 * Atribui valores dos atributos para consulta.
	 * 
	 * @param isDirectPrint
	 * @throws ApplicationBusinessException
	 * @throws JRException
	 * @throws IOException
	 * @throws DocumentException
	 */
	private void inicializarReport() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		
		if (this.competencia != null && this.competencia.getId() != null) {
			this.setDataHoraInicio(this.competencia.getId().getDtHrInicio());
			this.setMes(this.competencia.getId().getMes());
			this.setAno(this.competencia.getId().getAno());
		} else {
			this.setDataHoraInicio(null);
			this.setMes(null);
			this.setAno(null);
		}
	}
	
	/**
	 * Ação do botão Exportar.
	 */
	public void exportarArquivoCSV() {
		
		try {
			String fileName = this.faturamentoFacade.gerarCSVRelatorioRateioValoresSadtPorPontos(this.competencia);
			if (fileName == null) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_RETORNADO");
			} else {
				String header = DominioNomeRelatorio.REL_RATEIO_VALORES_SADT_POR_PONTOS.toString() 
						+ "_" + DateUtil.obterDataFormatada(new Date(), "yyyyMMdd") + EXTENSAO;
				download(fileName, header);
			}
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Implementação do método directPrint da abstração.
	 */
	@Override
	protected void directPrint() throws ApplicationBusinessException {
		
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Obtém os parametros para o relatório.
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		
		final Map<String, Object> parametros = new HashMap<String, Object>();

		try {
			AghParametros nomeHospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			parametros.put("NM_HOSPITAL", nomeHospital.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		parametros.put("NM_MES", CoreUtil.obterMesPorExtenso(this.mes).toUpperCase());
		parametros.put("ANO", this.ano);
		
		BigDecimal indice = this.faturamentoFacade.obterFatorMultiplicacaoParaValorRateado(this.dataHoraInicio, this.ano, this.mes);
		
		parametros.put("INDICE_RN5", indice);

		return parametros;
	}

	/**
	 * Ação do botão Voltar.
	 * 
	 * @return
	 */
	public String voltar() {
		
		this.ano = null;
		this.mes = null;
		this.dataHoraInicio = null;
		
		return PAGINA_PESQUISA_RELATORIO;
	}
	
	/**
	 * Ação do botão Limpar.
	 */
	public void limpar() {
		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		
		while (componentes.hasNext()) {
			
			limparValoresSubmetidos(componentes.next());
		}
		
		this.competencia = null;
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
	 * Consulta principal para carregamento da Suggestion Box de {@link FatCompetencia}
	 * 
	 * @param objPesquisa {@link String}
	 * @return {@link List} de {@link FatCompetencia}
	 */
	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		
		try {
			return returnSGWithCount(this.faturamentoFacade.listarCompetenciaModuloParaSuggestionBox(this.faturamentoFacade.getCompetenciaId(objPesquisa)),pesquisarCompetenciasCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<FatCompetencia>();
	}

	/**
	 * Consulta count para carregamento da Suggestion Box de {@link FatCompetencia}
	 * 
	 * @param objPesquisa {@link String}
	 * @return {@link Long}
	 */
	public Long pesquisarCompetenciasCount(String objPesquisa) {
		
		try {
			return this.faturamentoFacade.listarCompetenciaModuloParaSuggestionBoxCount(this.faturamentoFacade.getCompetenciaId(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0L;
	}
	
	/**
	 * Realiza consulta principal para corpo do relatorio.
	 */
	@Override
	public List<RateioValoresSadtPorPontosVO> recuperarColecao() throws ApplicationBusinessException {
		return this.faturamentoFacade.obterRateioValoresSadtPorPontos(this.dataHoraInicio, this.ano, this.mes);
	}

	/**
	 * Retorna o caminho/diretorio do relatorio jasper.
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return CAMINHO_ARQUIVO_RELATORIO;
	}

	/**
	 * 
	 * GET's and SET's
	 * 
	 */
	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Date getDataHoraInicio() {
		return dataHoraInicio;
	}

	public void setDataHoraInicio(Date dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public List<RateioValoresSadtPorPontosVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RateioValoresSadtPorPontosVO> colecao) {
		this.colecao = colecao;
	}
	
	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}
}
