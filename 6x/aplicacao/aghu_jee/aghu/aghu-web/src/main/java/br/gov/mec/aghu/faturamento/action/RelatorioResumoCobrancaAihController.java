package br.gov.mec.aghu.faturamento.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.action.RelatorioResumoAIHEmLotePdfController.RelatorioResumoAIHEmLotePdfExceptionCode;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioResumoCobrancaAihController extends ActionReport {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2098474065313437675L;

	private static final Log LOG = LogFactory.getLog(RelatorioResumoCobrancaAihController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Integer cthSeq;

	private Boolean previa;
	
	private List<ResumoCobrancaAihVO> colecao = new ArrayList<ResumoCobrancaAihVO>(0);

	private Date dtInicial;

	private Date dtFinal;

	private Boolean autorizada;

	private String iniciaisPaciente;

	private Boolean reapresentada;

	private String origem;

	private Boolean directPrint = false;
	
	private Boolean visualizarPDF = false;
	
	private File tempFile = null;
	
	// Dados do Paciente
	private Integer pacCodigo;
	private String pacNome;
	private Integer pacProntuario;
	
	@PostConstruct
	public void inicializar() {
		begin(conversation);
	}
	
	public String inicio() {

		this.definirValorPrevia();
		if (directPrint) {
			try {
				directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				getLog().error(EXCECAO_CAPTURADA, e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		} 
		else if(visualizarPDF){
			this.criarArquivoPdf();
			this.dispararDownload();
		}

		return null;
	}
	
	public void definirValorPrevia(){
		if (previa == null) {
			previa = Boolean.FALSE;
		}
		if (cthSeq != null) {
			FatContasHospitalares conta = faturamentoFacade.obterContaHospitalar(cthSeq);
			if (conta != null && (DominioSituacaoConta.E.equals(conta.getIndSituacao()) ||
					DominioSituacaoConta.O.equals(conta.getIndSituacao()))) {
				previa = false;
			} else {
				previa = true;
			}
		}
	}
	
	public enum RelatorioResumoCobrancaAihExceptionCode implements BusinessExceptionCode {
		ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_COBRANCA_AIH,
		WARN_SEM_DADOS_CONTA_INFORMADA_PDF_RELATORIO;
	}

	public String voltar() {
		return origem;
	}
	
	public void criarArquivoPdf(){

		tempFile = null;

		// Gera o PDF
		try {
			DocumentoJasper documento = gerarDocumento();

			if(this.colecao == null || this.colecao.size()==0){
				apresentarExcecaoNegocio(new BaseException(
						RelatorioResumoCobrancaAihExceptionCode.WARN_SEM_DADOS_CONTA_INFORMADA_PDF_RELATORIO));
			} else {				
				final File file = File.createTempFile(DominioNomeRelatorio.FATR_RESUMO_AIH.toString(),".pdf");
				final FileOutputStream out = new FileOutputStream(file);

				out.write(documento.getPdfByteArray(false));
				out.flush();
				out.close();

				tempFile = file;
			}

		} catch (JRException e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(new BaseException(
					RelatorioResumoAIHEmLotePdfExceptionCode.ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE));

		} catch (IOException e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(new BaseException(
					RelatorioResumoAIHEmLotePdfExceptionCode.ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (DocumentException e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(new BaseException(
					RelatorioResumoAIHEmLotePdfExceptionCode.ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE));
		}	
	}
	
	public void dispararDownload(){
		if(this.isGerouPdf()){
			try {
				download(tempFile, DominioMimeType.PDF.getContentType());
				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_PDF, e, e.getLocalizedMessage()));
			}
		}
		tempFile = null;
	}

	@Override
	public Collection<ResumoCobrancaAihVO> recuperarColecao()  {
		this.colecao = faturamentoFacade.gerarRelatorioResumoCobrancaAih(cthSeq, previa);

		// Atualiza o campo ind_impressao_espelho
		try {
			FatContasHospitalares conta = faturamentoFacade.obterContaHospitalar(cthSeq);

			if (conta != null && Boolean.FALSE.equals(conta.getIndImpressaoEspelho())) {
				FatContasHospitalares contaClone = faturamentoFacade.clonarContaHospitalar(conta);
				conta.setIndImpressaoEspelho(true);

				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					getLog().error("Exceção caputada:", e);
					apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
				}
				final Date dataFimVinculoServidor = new Date();
				faturamentoFacade.persistirContaHospitalar(conta, contaClone, nomeMicrocomputador, dataFimVinculoServidor);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioResumoCobrancaAih.jasper";
	}

	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String subReport = "br/gov/mec/aghu/faturamento/report/relatorioResumoCobrancaAihServicos.jasper";
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "FATR_RESUMO_AIH");
		params.put("subRelatorio", Thread.currentThread().getContextClassLoader().getResourceAsStream(subReport));

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("previa", false);

		if (previa) {
			params.put("imagemFundo", recuperaCaminhoImgBackground());
		}
		return params;
	}

	private String recuperaCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/report_previa.png");
		return path;
	}

	public Boolean isGerouPdf() {
		return this.tempFile != null && this.tempFile.exists();
	}
	
	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public String getIniciaisPaciente() {
		return iniciaisPaciente;
	}

	public void setIniciaisPaciente(String iniciaisPaciente) {
		this.iniciaisPaciente = iniciaisPaciente;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Boolean getAutorizada() {
		return autorizada;
	}

	public void setAutorizada(Boolean autorizada) {
		this.autorizada = autorizada;
	}

	public Boolean getDirectPrint() {
		return directPrint;
	}

	public void setDirectPrint(Boolean directPrint) {
		this.directPrint = directPrint;
	}
	
	public Boolean getVisualizarPDF() {
		return visualizarPDF;
	}

	public void setVisualizarPDF(Boolean visualizarPDF) {
		this.visualizarPDF = visualizarPDF;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}
}