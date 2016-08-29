package br.gov.mec.aghu.sicon.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.model.ScoLogEnvioSicon;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.PdfUtil;

import com.itextpdf.text.DocumentException;


public class RetornoIntegracaoVisualizarPdfController extends ActionController{

	private static final long serialVersionUID = -3175971611083920391L;	
	
	
	private static final String PAGE_RETORNO_INTEGRACAO = "retornoIntegracao";


	@EJB
	private ISiconFacade siconFacade;	

	private Integer seqScoLogEnvioSicon;
	public static final String CONTENT_TYPE = "application/pdf;";
	
	

	
	private enum RetornoIntegracaoVisualizarPdfControllerExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_SEQ_NULO;
	}

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

		
	/**
	 * Renderiza o PDF.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, 
																JRException, SystemException,	DocumentException {
		
		try {

			if (this.seqScoLogEnvioSicon == null) {

				apresentarMsgNegocio(Severity.ERROR,
								RetornoIntegracaoVisualizarPdfControllerExceptionCode.MENSAGEM_SEQ_NULO
										.toString());
				return null;
			}  
			ScoLogEnvioSicon arquivo = this.siconFacade.obterLogEnvioSicon(this.seqScoLogEnvioSicon);
			

			 //this.downloadViaHttpServletResponse(arquivo);
		    //DocumentoJasper documento =  this.reportGenerator.gerarDocumento(false);
			 byte[] byteArray = arquivo.getArqRel();
		     ByteArrayOutputStream bos = PdfUtil.protectPdf(byteArray);				
				
		   //  Date date = new Date();			
			 return new DefaultStreamedContent(new ByteArrayInputStream(bos.toByteArray()), "application/pdf");
		    
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
		    
	}
	
	public String voltar(){
		return PAGE_RETORNO_INTEGRACAO;
	}

	public Integer getSeqScoLogEnvioSicon() {
		return seqScoLogEnvioSicon;
	}

	public void setSeqScoLogEnvioSicon(Integer seqScoLogEnvioSicon) {
		this.seqScoLogEnvioSicon = seqScoLogEnvioSicon;
	}

	
	

}
