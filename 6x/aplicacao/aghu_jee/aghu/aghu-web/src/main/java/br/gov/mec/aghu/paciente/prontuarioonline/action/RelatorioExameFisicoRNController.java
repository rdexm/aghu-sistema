package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelExameFisicoRecemNascidoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioExameFisicoRNController extends ActionReport {


	private static final Log LOG = LogFactory.getLog(RelatorioExameFisicoRNController.class);
	
	private static final long serialVersionUID = 7795985660223407856L;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	private List<RelExameFisicoRecemNascidoVO> colecao = new ArrayList<RelExameFisicoRecemNascidoVO>(0);

	private Integer pacCodigo;
	
	private Short gsoSeqp;
	
	private Byte seqp;
	
	private Integer conNumero;

	private String voltarPara;
	
	private String indImpPrevia;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}
	
	public void directPrint() {
		try {
			colecao = this.prontuarioOnlineFacade.recuperarRelExameFisicoRecemNascidoVO(pacCodigo, gsoSeqp, seqp, conNumero);			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	} 

	@Override
	public Collection<RelExameFisicoRecemNascidoVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao = this.prontuarioOnlineFacade.recuperarRelExameFisicoRecemNascidoVO(pacCodigo, gsoSeqp, seqp, conNumero);		
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/exameFisicoRN/relatorioExameFisicoRN.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		
			Map<String, Object> params = new HashMap<String, Object>();
			
			try {
				params.put("caminhoLogo", recuperarCaminhoLogo());
			} catch (BaseException e) {
				LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
			}				
			params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/exameFisicoRN/");
			params.put("P_IND_IMP_PREVIA", getIndImpPrevia());
			// TODO: Deve ser ajustado posteriormente para virar um parametro do sistema assim como o logo
			//params.put("previaUrlImagem", recuperarCaminhoImgBackground());
			
			return params;		
	}
	
	// TODO: Descomentar depois de criar o parametro
	/*private String recuperarCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/img/report_previa.png");
		return path;
	}*/

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		// colecao.clear();
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}
	
	public String voltar(){
		return voltarPara;
	}
	

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}
	
	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public List<RelExameFisicoRecemNascidoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelExameFisicoRecemNascidoVO> colecao) {
		this.colecao = colecao;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Byte getSeqp() {
		return seqp;
	}

	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}

	public void setIndImpPrevia(String indImpPrevia) {
		this.indImpPrevia = indImpPrevia;
	}

	public String getIndImpPrevia() {
		return indImpPrevia;
	}	
}