package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PdtDescricaoProcedimentoCirurgiaVO;
import net.sf.jasperreports.engine.JRException;

public class RelatorioPdtDescProcCirurgiaController extends ActionReport {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
		permiteImprimirDescricaoProcedimentosPOL = securityController
				.usuarioTemPermissao(
						"permiteImprimirDescricaoProcedimentosPOL", "imprimir");
	}

	private static final Log LOG = LogFactory
			.getLog(RelatorioPdtDescProcCirurgiaController.class);

	private static final long serialVersionUID = -6265776185617240893L;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;


	@Inject
	private SecurityController securityController;

	private List<PdtDescricaoProcedimentoCirurgiaVO> colecao = new ArrayList<PdtDescricaoProcedimentoCirurgiaVO>(
			0);

	private Integer seqPdtDescricao;

	private String voltarPara;

	private Boolean permiteImprimirDescricaoProcedimentosPOL;

	// public void impressaoDireta(Integer seqPdtDescricao) throws
	// BaseException, JRException, SystemException, IOException {
	// this.seqPdtDescricao = seqPdtDescricao;
	// directPrint();
	// }

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws ApplicationBusinessException {

		try {
		

			colecao = this.prontuarioOnlineFacade
					.recuperarPdtDescricaoProcedimentoCirurgiaVO(seqPdtDescricao);

			// if (colecao.isEmpty()) {
			// apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			// return;
			// }

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Collection<PdtDescricaoProcedimentoCirurgiaVO> recuperarColecao()
			throws ApplicationBusinessException {

		// seqPdtDescricao = 64437; //FIXME
		return colecao = this.prontuarioOnlineFacade
				.recuperarPdtDescricaoProcedimentoCirurgiaVO(seqPdtDescricao);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/relatorioPdtDescProcCirurgia.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
		params.put("SUBREPORT_DIR",
				"br/gov/mec/aghu/paciente/prontuarioonline/report/");

		return params;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws IOException,
			ApplicationBusinessException, JRException, SystemException,
			DocumentException {
		// colecao.clear();
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
		// Defeito em Qualidade #30595
		// if (getPermiteImprimirDescricaoProcedimentosPOL()) {
		// return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		// } else {
		// return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
		// }
	}

	// Getters e Setters

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public List<PdtDescricaoProcedimentoCirurgiaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<PdtDescricaoProcedimentoCirurgiaVO> colecao) {
		this.colecao = colecao;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getSeqPdtDescricao() {
		return seqPdtDescricao;
	}

	public void setSeqPdtDescricao(Integer seqPdtDescricao) {
		this.seqPdtDescricao = seqPdtDescricao;
	}

	public Boolean getPermiteImprimirDescricaoProcedimentosPOL() {
		return permiteImprimirDescricaoProcedimentosPOL;
	}

	public void setPermiteImprimirDescricaoProcedimentosPOL(
			Boolean permiteImprimirDescricaoProcedimentosPOL) {
		this.permiteImprimirDescricaoProcedimentosPOL = permiteImprimirDescricaoProcedimentosPOL;
	}
}
