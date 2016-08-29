package br.gov.mec.aghu.certificacaodigital.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSSignedData;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.PdfUtil;
import br.ufrgs.hcpa.crypto.CMSReader;


public class VisualizarDocumentoController extends ActionController{

	private static final Log LOG = LogFactory.getLog(VisualizarDocumentoController.class);

	private static final long serialVersionUID = -1102029236969130649L;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@Inject
	private SecurityController securityController;

	private Integer seqAghVersaoDocumento;
	private String origem;
	private boolean imprimirDocumentoOriginal;

	private StreamedContent media;
	
	public static final String CONTENT_TYPE = "application/pdf;";
	
	private enum VisualizarDocumentoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO;
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		imprimirDocumentoOriginal = securityController.usuarioTemPermissao("samisAssinaturaDigital", "imprimirDocumentoOriginal");
	}
	
	public StreamedContent getRenderPdf() {
		
		try {
			AghVersaoDocumento arquivo = null;
			if (this.seqAghVersaoDocumento == null) {
				apresentarMsgNegocio(
						Severity.ERROR,
						VisualizarDocumentoControllerExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO
								.toString());
			}
			
			arquivo = certificacaoDigitalFacade
					.visualizarDocumentoOriginal(seqAghVersaoDocumento);

			return new DefaultStreamedContent(new ByteArrayInputStream(
					arquivo.getOriginal()), "application/pdf", "relatorio.pdf");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

    public StreamedContent getRenderPdfAssinado() {

        try {
            AghVersaoDocumento arquivo = null;
            if (this.seqAghVersaoDocumento == null) {
                apresentarMsgNegocio(
                        Severity.ERROR,
                        VisualizarDocumentoControllerExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO
                                .toString());
            }

            arquivo = certificacaoDigitalFacade
                    .visualizarDocumentoOriginal(seqAghVersaoDocumento);

            byte[] is = arquivo.getEnvelope();
            CMSSignedData signedData = CMSReader.read(is);
            byte[] pdfAssinado = (byte[])signedData.getSignedContent().getContent();
            return new DefaultStreamedContent(new ByteArrayInputStream(pdfAssinado), "application/pdf", "relatorio.pdf");

        } catch (ApplicationBusinessException e) {
            apresentarExcecaoNegocio(e);
            return null;
        } catch (Exception e) {
            apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
            return null;
        }

    }
	
	/**
	 * Download do arquivo Utilizado na lista de pendências da certificacao digital
	 */
	public String visualizarDocumentoOriginal(OutputStream out, Object data) {

		try {

			if (this.seqAghVersaoDocumento == null) {
				apresentarMsgNegocio(Severity.ERROR,VisualizarDocumentoControllerExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO.toString());
				return null;
			}

			AghVersaoDocumento arquivo = certificacaoDigitalFacade.visualizarDocumentoOriginal(seqAghVersaoDocumento);

			return this.downloadViaHttpServletResponse(arquivo, "original", false);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Download do arquivo
	 */
	public String visualizarDocumentoAssinado(OutputStream out, Object data) {

		try {

			if (this.seqAghVersaoDocumento == null) {
				apresentarMsgNegocio(Severity.ERROR,VisualizarDocumentoControllerExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO.toString());
				return null;
			}

			AghVersaoDocumento arquivo = certificacaoDigitalFacade.visualizarDocumentoAssinado(seqAghVersaoDocumento);

			return downloadViaHttpServletResponse(arquivo, "assinado", false);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Busca o envelope certificado 
	 */
	public String downloadEnvelope(){
		
		try {
		
			if (this.seqAghVersaoDocumento == null) {
				apresentarMsgNegocio(Severity.ERROR, VisualizarDocumentoControllerExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO.toString());
				return null;
			}
	
			AghVersaoDocumento arquivo = certificacaoDigitalFacade.visualizarDocumentoAssinado(this.seqAghVersaoDocumento);
			
			return this.downloadViaHttpServletResponse(arquivo, "assinado", true);
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;

	}

	/**
	 * Método responsável pelo download do arquivo. O arquivo pode ser do tipo Original, Assinado ou ainda o Envelope criptografado.
	 */
	private String downloadViaHttpServletResponse(AghVersaoDocumento documento, String tipo, boolean downloadEnvelope) {

		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		if(downloadEnvelope){
			response.setContentType("application/x-msdownload");
			response.addHeader("Content-disposition", "attachment;filename=envelope.p7s");
			
		}else{
			response.setContentType(CONTENT_TYPE);
			response.addHeader("Content-disposition", "inline");
		}

		ServletOutputStream os = null;
		try {
			
			//testar permissão de impressão
			if(!imprimirDocumentoOriginal){
				
				byte[] byteArray = null;
				if("original".equals(tipo)){
					byteArray = documento.getOriginal();
				}else{
					byte[] is = documento.getEnvelope();
		            CMSSignedData signedData = CMSReader.read(is);
					byteArray = (byte[])signedData.getSignedContent().getContent();
				}
				
				ByteArrayOutputStream bos = PdfUtil.protectPdf(byteArray);
				
				os = response.getOutputStream();
				os.write(bos.toByteArray());
				
				
			}else{
				os = response.getOutputStream();
				if("original".equals(tipo)){
					os.write(documento.getOriginal());
				}else{
					
					byte[] is = documento.getEnvelope();
		            
		            if(downloadEnvelope){
		            	
		            	os.write(is);
		            	
		            }else{
		            	
		            CMSSignedData signedData = CMSReader.read(is);
		            byte[] pdfAssinado = (byte[])signedData.getSignedContent().getContent();
					os.write(pdfAssinado);
						
		            }
		            
				}
			}
			
			os.flush();
			FacesContext.getCurrentInstance().responseComplete();
			
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		} finally {
			IOUtils.closeQuietly(os);
		}
		return null;
	}
	
	public String voltar(){
		return origem;
		/*
		 
		 Migrado parcial para POL, confirmar demais....
		
		}else if(this.origem.equals("listaPendenciasAssinatura")){
			return "listaPendenciasAssinatura";
			
		}else {
			return "voltarDocumentosPaciente";
		}
 
 */
	}

	public Integer getSeqAghVersaoDocumento() {
		return seqAghVersaoDocumento;
	}

	public void setSeqAghVersaoDocumento(Integer seqAghVersaoDocumento) {
		this.seqAghVersaoDocumento = seqAghVersaoDocumento;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	} 

	public boolean isImprimirDocumentoOriginal() {
		return imprimirDocumentoOriginal;
	}

	public void setImprimirDocumentoOriginal(boolean imprimirDocumentoOriginal) {
		this.imprimirDocumentoOriginal = imprimirDocumentoOriginal;
	}

	public StreamedContent getMedia() {		
			return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
}