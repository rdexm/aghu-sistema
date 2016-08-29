package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.perinatologia.vo.RelatorioSnappeVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioSnappe2Controller extends	ActionReport{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5426645393179037404L;

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject
	private RegistrarGestacaoAbaExtFisicoRNController registrarGestacaoAbaExtFisicoRNController;
	
	@Inject
	private RegistrarGestacaoController registrarGestacaoController;
		
	private String fileName;
	private Integer pacCodigoRecemNascido;
	private Short seqpRecemNascido;
	private String telaOrigem;
	private List<RelatorioSnappeVO> colecao = new ArrayList<RelatorioSnappeVO>();

	private static final Log LOG = LogFactory.getLog(RelatorioSnappe2Controller.class);
	
	private static final String RELATORIO_PDF = "relatorioSnappe2Pdf";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/perinatologia/report/relatorioSnappe2.jasper";
	}

	@Override
	public Collection<RelatorioSnappeVO> recuperarColecao() {
		if(this.colecao == null || this.colecao.isEmpty()){
			try {
				this.colecao = null;
			
				if (this.registrarGestacaoAbaExtFisicoRNController.getVo() != null) {
					this.colecao = perinatologiaFacade.listarRelatorioSnappe2(pacCodigoRecemNascido, getSeqpRecemNascido(), 
						this.registrarGestacaoController.getPacCodigo(), 
						this.registrarGestacaoController.getSeqp(),
						this.registrarGestacaoAbaExtFisicoRNController.getVo().getSeqp());
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}			
		}
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yy HH:mm"));
		return params;
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}

	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException {
		try {
			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));	
			return RELATORIO_PDF;
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String voltar() {
		limpar();
		return getTelaOrigem();
	}
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(
					Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(
					Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public void limpar() {
		this.colecao = null;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getPacCodigoRecemNascido() {
		return pacCodigoRecemNascido;
	}

	public void setPacCodigoRecemNascido(Integer pacCodigoRecemNascido) {
		this.pacCodigoRecemNascido = pacCodigoRecemNascido;
	}

	public Short getSeqpRecemNascido() {
		return seqpRecemNascido;
	}

	public void setSeqpRecemNascido(Short seqpRecemNascido) {
		this.seqpRecemNascido = seqpRecemNascido;
	}
	
	public String getTelaOrigem() {
		return telaOrigem;
	}
	
	public void setTelaOrigem(String telaOrigem) {
		this.telaOrigem = telaOrigem;
	}
}
