package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAnamneseEvolucaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import net.sf.jasperreports.engine.JRException;


public class ConsultarAmbulatorioPOLRelatorioController extends ActionReport {


	private static final Log LOG = LogFactory.getLog(ConsultarAmbulatorioPOLRelatorioController.class);

	private static final long serialVersionUID = -3744812806595136737L;
	
	private String voltarPara;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	private List<RelatorioAnamneseEvolucaoVO> colecao = new ArrayList<RelatorioAnamneseEvolucaoVO>(0);
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioAnamneseEvolucao.jasper";
	}

	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {
		try {

			recuperarColecao();
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}

	@Override
	public List<Object> recuperarColecao() throws ApplicationBusinessException {

		List<Object> lista = new ArrayList<Object>();
		lista.addAll(colecao);
		return lista;
	} 

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual", new Date());
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
		return params;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		final DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}
	
	public String voltar(){
		return getVoltarPara();
	}

	public List<RelatorioAnamneseEvolucaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioAnamneseEvolucaoVO> colecao) {
		this.colecao = colecao;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}
}
