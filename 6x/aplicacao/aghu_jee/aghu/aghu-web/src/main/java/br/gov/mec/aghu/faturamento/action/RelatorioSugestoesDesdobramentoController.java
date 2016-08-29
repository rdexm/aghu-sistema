package br.gov.mec.aghu.faturamento.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrigemSugestoesDesdobramento;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.SugestoesDesdobramentoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelatorioSugestoesDesdobramentoController extends ActionReport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3084854700660728219L;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final String EXTENSAO=".csv";
	private static final String EXCECAO_CAPTURADA="Exceção capturada: ";
	private static final String RELATORIO = "relatorioSugestoesDesdobramento";
	private static final String RELATORIO_PDF = "relatorioSugestoesDesdobramentoPdf";
	private static final Log LOG = LogFactory.getLog(RelatorioSugestoesDesdobramentoController.class);
	
	private Log getLog() {
		return LOG;
	}
	
	private String inicialPac;
	private DominioOrigemSugestoesDesdobramento origem;

	private String fileName;
	private Boolean gerouArquivo = Boolean.FALSE;
	private StreamedContent media;
	

	public enum RelatorioSugestoesDesdobramentoControllerExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_SUGESTOES_DESDO_INVALIDAS;
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation, true);
	}
	
	@Override
	protected Collection<SugestoesDesdobramentoVO> recuperarColecao() throws ApplicationBusinessException {
		return faturamentoFacade.pesquisarSugestoesDesdobramento(this.origem, this.inicialPac);
	}
	
	@Override
	public String recuperarArquivoRelatorio()  {
		return "br/gov/mec/aghu/faturamento/report/relatorioSugestoesDesdobramento.jasper";
	}
	
	/**
	 * Imprimimr relatorio
	 * 
	 * @return
	 */
	public String imprimirRelatorio() {
		try {
			if (CoreUtil.validaIniciaisPaciente(this.inicialPac)) {
				
				directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				return null;
					
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioSugestoesDesdobramentoControllerExceptionCode.INICIAS_RELATORIO_SUGESTOES_DESDO_INVALIDAS.toString());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}

	/**
	 * Visualizar Relatorio
	 * 
	 * @return
	 */
	public String visualizarRelatorio() {
		
		try {
			if (CoreUtil.validaIniciaisPaciente(inicialPac)) {
				
				DocumentoJasper documento = gerarDocumento();
				this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
				return RELATORIO_PDF;
				
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioSugestoesDesdobramentoControllerExceptionCode.INICIAS_RELATORIO_SUGESTOES_DESDO_INVALIDAS.toString());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException | IOException | DocumentException e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}
	

	/**
	 * Gerar CSV
	 */
	public void gerarCSV() {
		
		try {
			if (CoreUtil.validaIniciaisPaciente(inicialPac)) {

				fileName = faturamentoFacade.gerarCSV(this.origem, this.inicialPac);
				gerouArquivo = true;
				execultarDownload();
				
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioSugestoesDesdobramentoControllerExceptionCode.INICIAS_RELATORIO_SUGESTOES_DESDO_INVALIDAS.toString());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (IOException e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
					e.getLocalizedMessage()));
		} 
	}
	
	/**
	 * Execultar download
	 */
	public void execultarDownload() {
		
		if (fileName != null) {
			try {
				String header = DominioNomeRelatorio.RELATORIO_SUGESTOES_DESDOBRAMENTO.getDescricao()+"_"+ DateUtil.obterDataFormatada(new Date(), "ddMMyyyy") + EXTENSAO;
				download(fileName, header);
				gerouArquivo = false;
				fileName = null;
			} catch (IOException e) {
				getLog().error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
						e.getLocalizedMessage()));
			}
		}
	}
	
	/**
	 * Limpar tela e campos
	 */
	public void limpar() {
		
		this.origem = null;
		this.inicialPac = null;
		this.gerouArquivo = false;
		this.fileName = null;
	}
	
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		
		try {			
			AghParametros nomeHospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("nomeHospital", nomeHospital.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		if (origem != null) {
			
			params.put("origem", origem.getDescricao());
		}

		return params;
	}

	
	public String voltar() {
		return RELATORIO;
	}

	
	//Getters and Setters
	/**
	 * @return the inicialPac
	 */
	public String getInicialPac() {
		return inicialPac;
	}

	/**
	 * @param inicialPac the inicialPac to set
	 */
	public void setInicialPac(String inicialPac) {
		this.inicialPac = inicialPac;
	}

	/**
	 * @return the origem
	 */
	public DominioOrigemSugestoesDesdobramento getOrigem() {
		return origem;
	}

	/**
	 * @param origem the origem to set
	 */
	public void setOrigem(DominioOrigemSugestoesDesdobramento origem) {
		this.origem = origem;
	}

	/**
	 * @return the gerouArquivo
	 */
	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	/**
	 * @param gerouArquivo the gerouArquivo to set
	 */
	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the media
	 */
	public StreamedContent getMedia() {
		return media;
	}

	/**
	 * @param media the media to set
	 */
	public void setMedia(StreamedContent media) {
		this.media = media;
	}

}
