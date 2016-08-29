package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.OpcoesImpressao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.ufrgs.hcpa.crypto.CMSReader;
import net.sf.jasperreports.engine.JRException;


public class ConsultarDocumentosPrescricaoMedicaController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(ConsultarDocumentosPrescricaoMedicaController.class);

	private static final long serialVersionUID = -8698651764281103788L;

	private Integer atdSeq;
	private String voltarPara;
	private Integer seqDocCertificado;
	
	private AghVersaoDocumento docCertificado;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@PostConstruct
	protected void iniciarConversacao(){
		this.begin(conversation);
	}
	
	private StreamedContent arquivo;
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException {
		
		try{
			docCertificado = certificacaoDigitalFacade.pesquisarAghVersaoDocumentoPorAtendimento(atdSeq, DominioTipoDocumento.PM, seqDocCertificado).get(0);
			
			byte[] byteArray = null;
			if(docCertificado.getEnvelope() == null){
				byteArray = docCertificado.getOriginal();
			}else{
				byte[] is = docCertificado.getEnvelope();
	            CMSSignedData signedData = CMSReader.read(is);
				byteArray = (byte[])signedData.getSignedContent().getContent();
			}
			
			arquivo = criarStreamedContentPdf(byteArray);
			return arquivo;
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		} 
	}

	private byte[] obterByteArray() throws CMSException {
		byte[] byteArray = null;
		if(docCertificado.getEnvelope() == null){
			byteArray = docCertificado.getOriginal();
		}else{
			byte[] is = docCertificado.getEnvelope();
		    CMSSignedData signedData = CMSReader.read(is);
			byteArray = (byte[])signedData.getSignedContent().getContent();
		}
		return byteArray;
	}
	
	public String voltar(){
		return voltarPara;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return null;
	}

	@Override
	protected Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return null;
	}
	
	public void directPrint() throws SistemaImpressaoException ,ApplicationBusinessException {
		
		try {
			byte[] byteArray = obterByteArray();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(byteArray);

			OpcoesImpressao fitToPage = new OpcoesImpressao();
			fitToPage.setFitToPage(true); 
			
			sistemaImpressao.imprimir(out, super.getEnderecoIPv4HostRemoto(), fitToPage);

		} catch(SistemaImpressaoException e){
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			
		} catch (CMSException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public Integer getSeqDocCertificado() {
		return seqDocCertificado;
	}

	public AghVersaoDocumento getDocCertificado() {
		return docCertificado;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setSeqDocCertificado(Integer seqDocCertificado) {
		this.seqDocCertificado = seqDocCertificado;
	}

	public void setDocCertificado(AghVersaoDocumento docCertificado) {
		this.docCertificado = docCertificado;
	}

	public StreamedContent getArquivo() {
		return arquivo;
	}

	public void setArquivo(StreamedContent arquivo) {
		this.arquivo = arquivo;
	}
}
