package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.PdfUtil;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfEncryptor;
import com.itextpdf.text.pdf.PdfReader;


public class VisualizacaoResultadoExameController extends ActionController {

	private static final Log LOG = LogFactory.getLog(VisualizacaoResultadoExameController.class);
	
	private static final long serialVersionUID = -7750163009287676358L;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private Integer iseSoeSeq;
	private Short iseSeqp;	
	private String voltarPara;
	private Integer currentTabIndex;
	
	private String data;
	private Short unidadeExame;
	private Integer prontuario;
	private int firstResult;
	private boolean ativo;

	private Boolean origemPOL;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}	
	
	
	/**
	 * Chamado no inicio de cada conversacao
	 */
	public void persistirVisualizacaoDownloadAnexo() throws ApplicationBusinessException, ApplicationBusinessException {
		// Persiste o download ou visualizacao do documento anexado
		this.examesFacade.persistirVisualizacaoDownloadAnexo(this.iseSoeSeq, this.iseSeqp);
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws DocumentException, IOException {
		
		StreamedContent stremedContent = null;
		try {
			ByteArrayOutputStream byteArrayOS = this.prontuarioOnlineFacade.buscarArquivoResultadoExame(iseSoeSeq, iseSeqp);

			if (Boolean.TRUE.equals(origemPOL)) {
				// Se origem for POL, então impressão é feita somente via botão
				ByteArrayOutputStream protectPdf = PdfUtil.protectPdf(byteArrayOS.toByteArray());
				stremedContent = ActionReport.criarStreamedContentPdfPorByteArray(protectPdf.toByteArray());
			} else {
				stremedContent = ActionReport.criarStreamedContentPdfPorByteArray(byteArrayOS.toByteArray());
			}

			//TODO MIGRAÇÂO: Verificar se é realmente necessário 
			//IOUtils.copy(input, out);
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
		}
		return stremedContent;
	}
	
	/**
	 * Imprime o laudo do exame para a impressora default.
	 */
	public void imprimir() {
		try {
			ByteArrayOutputStream output = this.prontuarioOnlineFacade.buscarArquivoResultadoExame(iseSoeSeq, iseSeqp);

			PdfReader reader = new PdfReader(output.toByteArray());

			// se criptografado e nao permite impressao
			if (reader.isEncrypted() && !PdfEncryptor.isPrintingAllowed(reader.getPermissions())) {
				apresentarMsgNegocio(Severity.ERROR, "ERRO_IMPRESSAO_RELATORIO_PROTEGIDO");
				return;
			}
			
			this.sistemaImpressao.imprimir(output,super.getEnderecoIPv4HostRemoto(), "ArquivoResultadoExame");

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_IMPRESSAO_RELATORIO");
		}
	}
	public String voltar() {
		return voltarPara;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public void setUnidadeExame(Short unidadeExame) {
		this.unidadeExame = unidadeExame;
	}

	public Short getUnidadeExame() {
		return unidadeExame;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}


	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}


	public Boolean getOrigemPOL() {
		return origemPOL;
	}

	public void setOrigemPOL(Boolean origemPOL) {
		this.origemPOL = origemPOL;
	}


	
	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}


	
	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}
}