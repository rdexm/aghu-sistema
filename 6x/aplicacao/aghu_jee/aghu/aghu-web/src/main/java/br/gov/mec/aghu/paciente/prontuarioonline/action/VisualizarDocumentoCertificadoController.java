package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSSignedData;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfBoolean;
import com.itextpdf.text.pdf.PdfEncryptor;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.PdfUtil;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.ufrgs.hcpa.crypto.CMSReader;
import net.sf.jasperreports.engine.JRException;


public class VisualizarDocumentoCertificadoController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(VisualizarDocumentoCertificadoController.class);

	private static final long serialVersionUID = 1929352823465820813L;

	private static final String ORIGINAL = "original", ASSINADO = "assinado";

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	private Integer seqVersaoDocumento;
	private String voltarPara;
	private String situacaoDocumento;
	private String descricaoTipoDocumento;
	
	private StreamedContent arquivo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		arquivo = null;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException {
		try {

			if (this.seqVersaoDocumento == null) {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO");
			}
			
			if(arquivo == null){
				byte[] byteArray = null;
				AghVersaoDocumento documento = obterDocumento();
				
				if(ORIGINAL.equals(situacaoDocumento)){
					byteArray = documento.getOriginal();
					
				} else if(ASSINADO.equals(situacaoDocumento)){
					byte[] is = documento.getEnvelope();
					CMSSignedData signedData = CMSReader.read(is);
					byteArray = (byte[]) signedData.getSignedContent().getContent();
				}
	
				arquivo = criarStreamedContentPdf(byteArray);
			}
			
			return arquivo;

		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
		}
		
		return null;
	}

	private AghVersaoDocumento obterDocumento() throws ApplicationBusinessException {
		
		if(ORIGINAL.equals(situacaoDocumento)){
			return this.certificacaoDigitalFacade.visualizarDocumentoOriginal(this.seqVersaoDocumento);
			
		} else if(ASSINADO.equals(situacaoDocumento)){
			return certificacaoDigitalFacade.visualizarDocumentoAssinado(this.seqVersaoDocumento);
		}
		
		return null;
	}

	public void directPrint() throws SistemaImpressaoException ,ApplicationBusinessException {
		try {

			AghVersaoDocumento documento = obterDocumento();
			byte[] byteArray = null;
			ByteArrayOutputStream bos = null;
			
			if(ORIGINAL.equals(situacaoDocumento)){
				byteArray = documento.getOriginal();
	            
	            bos = this.protectPdf(byteArray);
				this.sistemaImpressao.imprimir(bos, super.getEnderecoIPv4HostRemoto(), "documentoOriginal");
				
			} else if(ASSINADO.equals(situacaoDocumento)){
				byte[] is = documento.getEnvelope();
	            CMSSignedData signedData = CMSReader.read(is);
	            byteArray = (byte[])signedData.getSignedContent().getContent();
	            
	            bos = this.protectPdf(byteArray);
				sistemaImpressao.imprimir(bos, super.getEnderecoIPv4HostRemoto(), "documentoCertificado");
			}

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Proteje o PDF retirando todas as permissões e ocultando barra de menu e
	 * barra de ferramentas. <br />
	 * Retirando as permissões o botão de impressão do leitor de PDF fica
	 * protegido.<br />
	 * Se o PDF já estiver criptografado e protegido de impressão retorna sem
	 * modificações.
	 * 
	 * @param byteArray
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public ByteArrayOutputStream protectPdf(byte[] byteArray)
			throws IOException, DocumentException {
		if (byteArray == null) {
			throw new IllegalArgumentException("byteArray não pode ser null.");
		}

		if (PdfUtil.isImpressaoProtegida(byteArray)) {
			// como já está protegido, retorna o próprio recebido
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			IOUtils.write(byteArray, outputStream);
			return outputStream;
		}

		PdfReader reader = new PdfReader(byteArray);
		// permissão para imprimir
		int permissions = PdfWriter.ALLOW_PRINTING;
		// oculta a menu bar
		reader.addViewerPreference(PdfName.HIDEMENUBAR, PdfBoolean.PDFTRUE);
		// oculta a tool bar
		reader.addViewerPreference(PdfName.HIDETOOLBAR, PdfBoolean.PDFTRUE);

		reader.addViewerPreference(PdfName.HIDE, PdfBoolean.PDFFALSE);
		reader.addViewerPreference(PdfName.HIDEWINDOWUI, PdfBoolean.PDFFALSE);

		// encrypt e sai para byte array
		ByteArrayOutputStream outputStreamModified = new ByteArrayOutputStream();
		PdfEncryptor.encrypt(reader, outputStreamModified, null, null, permissions, true);
		reader.close();

		return outputStreamModified;
	}
	
	public String converterString(String string) {
		try {
			byte[] iso88591 = string.getBytes("ISO-8859-1");
			String utf8 = new String(iso88591, "UTF-8");
			return utf8;
		} catch (Exception e) {
			return null;
		}
	}

	public String voltar(){
		arquivo = null;
		return voltarPara;
	}
	
	@Override
	protected Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return null;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return null;
	}


	public Integer getSeqVersaoDocumento() {
		return seqVersaoDocumento;
	}

	public void setSeqVersaoDocumento(Integer seqVersaoDocumento) {
		this.seqVersaoDocumento = seqVersaoDocumento;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getSituacaoDocumento() {
		return situacaoDocumento;
	}

	public void setSituacaoDocumento(String situacaoDocumento) {
		this.situacaoDocumento = situacaoDocumento;
	}

	public String getDescricaoTipoDocumento() {
		return descricaoTipoDocumento;
	}

	public String getDescricaoTipoDocumentoConvertida() {
		return this.converterString(descricaoTipoDocumento);
	}

	public void setDescricaoTipoDocumento(String descricaoTipoDocumento) {
		this.descricaoTipoDocumento = descricaoTipoDocumento;
	}

	public StreamedContent getArquivo() {
		return arquivo;
	}

	public void setArquivo(StreamedContent arquivo) {
		this.arquivo = arquivo;
	}
}