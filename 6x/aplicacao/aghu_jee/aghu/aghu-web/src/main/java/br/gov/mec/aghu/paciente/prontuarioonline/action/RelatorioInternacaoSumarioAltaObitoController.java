package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioSumarioAltaController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



@SuppressWarnings({"PMD.HierarquiaControllerIncorreta"})
public class RelatorioInternacaoSumarioAltaObitoController extends RelatorioSumarioAltaController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioInternacaoSumarioAltaObitoController.class);
	
	private static final long serialVersionUID = -193816866164472606L;

	private static final String DETALHE_INTERNACAO = "detalheinternacao";
	private static final String CONSULTA_DETALHE_INTERNACAO = "pol-detalheInternacao";
	private static final String INTERNACAO = "internacao";
	private static final String CONSULTAR_INTERNACOES = "pol-internacao";
	
	private static final String PERINATAIS = "pol-informacoesPerinatais";
	private static final String INFORMACOES_PERINATAIS = "pol-informacoesPerinatais";

	private static final String ATENDIMENTOS_EMERGENCIA_POL = "atendimentosEmergenciaPOL";
	private static final String POL_EMERGENCIA = "pol-emergencia";




	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private Integer atdSeq;	//seq do atendimento recebido via parâmetro
	private String sumarioObito; //indicador se é sumário de óbito para apresentar o label da tela dinamicamente	
	private String voltarPara;
		
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;
	
	public void verificaTipoRelatorio() {
		setAltaSumario(this.prescricaoMedicaFacade.obterAltaSumariosAtivoConcluido(atdSeq));
		
		if (getAltaSumario() != null) {
			super.setSeqAtendimento(getAltaSumario().getId().getApaAtdSeq());

			if (getAltaSumario().getTipo().equals(DominioIndTipoAltaSumarios.OBT)) {
				setTipoImpressao("R");
				setObito(true);
			}
		}
	}
	
	@Override
	public void directPrint() {
		try {
			DominioTipoDocumento tipoDocumento = DominioTipoDocumento.SA; 
			if (getAltaSumario().getTipo().equals(DominioIndTipoAltaSumarios.OBT)) {
				tipoDocumento = DominioTipoDocumento.SO; 
			}
			DocumentoJasper documento = gerarDocumento(tipoDocumento, Boolean.TRUE);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException	 * 
	 */
	@Override
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {

		setAltaSumario(this.prescricaoMedicaFacade.obterAltaSumariosAtivoConcluido(atdSeq));
		
		DominioTipoDocumento tipoDocumento = DominioTipoDocumento.SA; 
		
		if (getAltaSumario() != null) {
			super.setSeqAtendimento(getAltaSumario().getId().getApaAtdSeq());

			if (getAltaSumario().getTipo().equals(DominioIndTipoAltaSumarios.OBT)) {
				setTipoImpressao("R");
				setObito(true);
				tipoDocumento = DominioTipoDocumento.SO; 
			} else {
				setObito(false);
			}
			populaRelatorioAltaObito();
			//Boolean permiteImpressao =  getIdentity().hasPermission("permiteImprimirSumarioAltaObitoPOL", "imprimir");
			DocumentoJasper documento = gerarDocumento(tipoDocumento, Boolean.TRUE);
			return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
		}
		return null;
	}
	
	public Integer getAtdSeq() {
		if(atdSeq == null){
			atdSeq = 0;
		}
		return atdSeq;
	}
	
	public String voltar(){
		if(DETALHE_INTERNACAO.equals(voltarPara)){
			return CONSULTA_DETALHE_INTERNACAO;
			
		} else if(INTERNACAO.equals(voltarPara)){
			return CONSULTAR_INTERNACOES;
			
		} else if(PERINATAIS.equals(voltarPara)){
			return INFORMACOES_PERINATAIS;
			
		} else if(ATENDIMENTOS_EMERGENCIA_POL.equals(voltarPara)){
			return POL_EMERGENCIA;
			
		} else {
			return null;
		}
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getSumarioObito() {
		return sumarioObito;
	}

	public void setSumarioObito(String sumarioObito) {
		this.sumarioObito = sumarioObito;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
}
