package br.gov.mec.aghu.faturamento.action;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ClinicaPorProcedimentoVO;
import br.gov.mec.aghu.faturamento.vo.TotalGeralClinicaPorProcedimentoVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioClinicaPorProcedimentoController extends ActionReport {

	private static final long serialVersionUID = 569212671414148750L;
	
	private static final String MSG_REL_CLIN_POR_PROCED_EXCECAO_CAPTURADA = "MSG_REL_CLIN_POR_PROCED_EXCECAO_CAPTURADA";
	
	private static final String ERRO_GERAR_RELATORIO = "ERRO_GERAR_RELATORIO";
	
	private static final Log LOG = LogFactory.getLog(RelatorioClinicaPorProcedimentoController.class);
	private static final String CONTENT_TYPE_ZIP = "application/zip";

	private Log getLog() {
		return LOG;
	}
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
    private IParametroFacade parametroFacade;
	
	private FatCompetencia competencia;
	private List<ClinicaPorProcedimentoVO> colecao = new LinkedList<ClinicaPorProcedimentoVO>();
	private Boolean gerouArquivo;
	private String fileName;
	private String extensaoArquivo;

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	public void inicio() {
		recarregarTela();
	}
	
	private void recarregarTela(){
		fileName = null;
		gerouArquivo = Boolean.FALSE;
	}
	
	public void limpar() {
		competencia = null;
	}
	
	public List<FatCompetencia> pesquisarCompetencias(String strPesquisa) {
		
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId((String)strPesquisa, DominioModuloCompetencia.INT));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return new ArrayList<FatCompetencia>();
	}

	@Override
	protected Collection<ClinicaPorProcedimentoVO> recuperarColecao() throws ApplicationBusinessException {
		try {
			return faturamentoFacade.recupearColecaoRelatorioClinicaPorProcedimento(competencia);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return colecao;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioClinicaPorProcedimento.jasper";
	}
	
	public void renderPdf(OutputStream out, Object data){
		DocumentoJasper documento = null;
		
		try {
			documento = gerarDocumento();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		try {	
			out.write(documento.getPdfByteArray(Boolean.TRUE));
		} catch (IOException e) {
			getLog().error(getBundle().getString(MSG_REL_CLIN_POR_PROCED_EXCECAO_CAPTURADA), e);
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (JRException e) {
			getLog().error(getBundle().getString(MSG_REL_CLIN_POR_PROCED_EXCECAO_CAPTURADA), e);
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (DocumentException e) {
			getLog().error(getBundle().getString(MSG_REL_CLIN_POR_PROCED_EXCECAO_CAPTURADA), e);
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		}
	}
	
	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		// label header
		params.put("headerClinica", getBundle().getString("HEADER_REL_CLIN_POR_PROCED_CLINICA"));
		params.put("headerProcedimento", getBundle().getString("HEADER_REL_CLIN_POR_PROCEDIMENTO"));
		params.put("headerQtd", getBundle().getString("HEADER_REL_CLIN_POR_PROCED_QTD"));
		params.put("headerVlrSH", getBundle().getString("HEADER_REL_CLIN_POR_PROCED_SERV_HOSP"));
		params.put("headerVlrSP", getBundle().getString("HEADER_REL_CLIN_POR_PROCED_SERV_PROF"));
		params.put("headerVlrSADT", getBundle().getString("HEADER_REL_CLIN_POR_PROCED_SADT"));
		params.put("headerTotal", getBundle().getString("HEADER_REL_CLIN_POR_PROCED_TOTAL"));
		params.put("labelAihClinica", getBundle().getString("LABEL_REL_CLIN_POR_PROCED_AIH_CLINICA"));
		params.put("labelProcedDiaEsp", getBundle().getString("LABEL_REL_CLIN_POR_PROCED_DIARIAS_ESP"));
		params.put("labelTotalClinica", getBundle().getString("LABEL_REL_CLIN_POR_PROCED_TOT_CLI"));
		params.put("labelAihGeral", getBundle().getString("LABEL_REL_CLIN_POR_PROCED_AIH_GERAL"));
		params.put("labelProcedDiaEspGeral", getBundle().getString("LABEL_REL_CLIN_POR_PROCED_PROD_DIA_ESP_HEM_GERAL"));
		params.put("labelDiariaAcompanhate", getBundle().getString("LABEL_REL_CLIN_POR_PROCED_DIARIAS_ACOMP"));
		params.put("labelDiariaUtil", getBundle().getString("LABEL_REL_CLIN_POR_PROCED_DIARIAS_UTI"));
		params.put("labelTotalGeral", getBundle().getString("LABEL_REL_CLIN_POR_PROCED_TOTAL_GERAL"));
		
		// dados header
		final String local = obterNomeHospital();
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("competencia", String.format("%02d", competencia.getId().getMes()) + "/" + competencia.getId().getAno());
		params.put("nomeTituloRelatorio", getBundle().getString("TITLE_REL_CLIN_POR_PROCED"));
		params.put("nomeRelatorio", getBundle().getString("PDF_REL_CLIN_POR_PROCED_NOME"));
		params.put("mes", CoreUtil.obterMesPorExtenso(competencia.getId().getMes()).toUpperCase());
		params.put("ano", competencia.getId().getAno());
		
		params.put("totalGeralClinicaPorProcedimento", obterTotalGeralClinicaPorProcedimento());
		
		return params;
	}
	

	private TotalGeralClinicaPorProcedimentoVO obterTotalGeralClinicaPorProcedimento(){
		try {
			return faturamentoFacade.obterTotalGeralClinicaPorProcedimento(competencia);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private String obterNomeHospital() {
		try {
			return parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return "";
		}
	}
	
	public void directPrint() {
		try {
			super.directPrint();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error(getBundle().getString(MSG_REL_CLIN_POR_PROCED_EXCECAO_CAPTURADA), e);
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		}
	}
	
	public void gerarCSV() {
		try {
			fileName = faturamentoFacade.gerarCSVClinicaPorProcedimento(competencia);			
			gerouArquivo = Boolean.TRUE;
		} catch (BaseException e) {
			gerouArquivo = Boolean.FALSE;
			apresentarExcecaoNegocio(e);
		}	
	}

	public void dispararDownload() {
		if (fileName != null) {
			try {
				download(new File(fileName), CONTENT_TYPE_ZIP);
				gerouArquivo = Boolean.FALSE;
				fileName = null;
				
			} catch (IOException e) {
				getLog().error(getBundle().getString(MSG_REL_CLIN_POR_PROCED_EXCECAO_CAPTURADA), e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e));
			}
		}
	}
	
	public String voltar(){
		return "relatorioClinicaPorProcedimento";
	}
	
	public String visualizarRelatorio() {
		return "relatorioClinicaPorProcedimentoPdf";
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public List<ClinicaPorProcedimentoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ClinicaPorProcedimentoVO> colecao) {
		this.colecao = colecao;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExtensaoArquivo() {
		return extensaoArquivo;
	}

	public void setExtensaoArquivo(String extensaoArquivo) {
		this.extensaoArquivo = extensaoArquivo;
	}

}
