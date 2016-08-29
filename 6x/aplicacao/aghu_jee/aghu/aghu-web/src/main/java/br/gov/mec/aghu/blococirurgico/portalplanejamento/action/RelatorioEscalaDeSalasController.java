package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.RelatorioEscalaDeSalasVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioEscalaDeSalasController extends ActionReport {

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

	private static final Log LOG = LogFactory.getLog(RelatorioEscalaDeSalasController.class);

	private static final String CONSULTA_ESCALA = "consultaEscalaSalas";
	
	private static final long serialVersionUID = -4997391332261628111L;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject
	private IParametroFacade parametroFacade;
	
	private Short seqUnidade;
	
	private String urlRetornoEscala;
	

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/portalplanejamento/report/relatorioEscalaDeSalas.jasper";
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
			params.put("caminhoLogo", recuperaLogo() );
			params.put("SUBREPORT_DIR","br/gov/mec/aghu/blococirurgico/portalplanejamento/report/"); 
		return params;
	}
	
	private String recuperaLogo(){
		return parametroFacade.recuperarCaminhoLogo2Relativo();
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioEscalaDeSalasVO> recuperarColecao() throws ApplicationBusinessException {
		return blocoCirurgicoPortalPlanejamentoFacade.listarEquipeSalas(seqUnidade);
	}
	
	public String voltar() {
		return CONSULTA_ESCALA;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
	
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public Short getSeqUnidade() {
		return seqUnidade;
	}

	public void setSeqUnidade(Short seqUnidade) {
		this.seqUnidade = seqUnidade;
	}

	public void setUrlRetornoEscala(String urlRetornoEscala) {
		this.urlRetornoEscala = urlRetornoEscala;
	}

	public String getUrlRetornoEscala() {
		return urlRetornoEscala;
	}
}
