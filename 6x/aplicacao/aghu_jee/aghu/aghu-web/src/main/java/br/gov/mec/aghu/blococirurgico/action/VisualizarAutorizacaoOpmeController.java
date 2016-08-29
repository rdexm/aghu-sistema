package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.VisualizarAutorizacaoOpmeVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class VisualizarAutorizacaoOpmeController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {			
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(VisualizarAutorizacaoOpmeController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3271748420754549284L;
	
	private static final String URL_DOCUMENTO_JASPER = "br/gov/mec/aghu/blococirurgico/report/imprimirRelatorioAutorizacaoOPME.jasper";
	private static final String URL_SUBREPORT_JASPER = "br/gov/mec/aghu/blococirurgico/report/";
	private static final String NOME_RELATORIO = "ORTESES_PROTESES_MATERIAIS_ESPECIAIS";
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
		
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	private List<VisualizarAutorizacaoOpmeVO> autorizacaoOpmeVO;
	private Short seqRequisicao;
	private String voltarParaUrl;
	
	public void inicio(){
		try {
			this.carregarColecao();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return URL_DOCUMENTO_JASPER;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {	
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {	
			
			// #35483 - C01_NOME_HOSP
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			parametros.put("nomeHospital", parametroRazaoSocial.getVlrTexto());
			parametros.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
			parametros.put("nomeRelatorio", NOME_RELATORIO);
			
			parametros.put("SUBREPORT_DIR", URL_SUBREPORT_JASPER);
			
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return parametros;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<VisualizarAutorizacaoOpmeVO> recuperarColecao() {		
		return autorizacaoOpmeVO; 
	}
	
	public void carregarColecao() throws ApplicationBusinessException {		
		autorizacaoOpmeVO = this.blocoCirurgicoOpmesFacade.carregarVizualizacaoAutorizacaoOpme(this.seqRequisicao);
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	public void directPrint() {
		try {
				carregarColecao();
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public List<VisualizarAutorizacaoOpmeVO> getAutorizacaoOpmeVO() {
		return autorizacaoOpmeVO;
	}

	public void setAutorizacaoOpmeVO(List<VisualizarAutorizacaoOpmeVO> autorizacaoOpmeVO) {
		this.autorizacaoOpmeVO = autorizacaoOpmeVO;
	}

	public Short getSeqRequisicao() {
		return seqRequisicao;
	}

	public void setSeqRequisicao(Short seqRequisicao) {
		this.seqRequisicao = seqRequisicao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	
	public String voltar(){
		return voltarParaUrl;
	}
}
