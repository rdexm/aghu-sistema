package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtosAnestesicosPolVO;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioAtosAnestesicosController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioAtosAnestesicosController.class);
	private static final long serialVersionUID = 1831213012198796318L;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;


	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@Inject
	private SecurityController securityController;

	private Long seqMbcFichaAnestesia;
	private String vSessao;
	private String voltarPara;
	private Boolean permiteImpressao = Boolean.FALSE;

	private Boolean permiteImprimirRelatorioAtoAnestesicoPOL;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
		permiteImprimirRelatorioAtoAnestesicoPOL = securityController.usuarioTemPermissao("permiteImprimirRelatorioAtoAnestesicoPOL", "imprimir");
	}	
	

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/prontuarioonline/report/mbcrFicha/mbcr_ficha.jasper";
	}

	public StreamedContent getRenderPdf() throws IOException,
			ApplicationBusinessException, JRException, SystemException,
			DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}

	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SUBREPORT_DIR",	"br/gov/mec/aghu/paciente/prontuarioonline/report/mbcrFicha/");
		
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}
		params.put("P_FIC_SEQ",	Integer.valueOf(seqMbcFichaAnestesia.toString()));

		return params;
	}
	
	public String voltar(){
		return voltarPara;
	}

	@Override
	public Collection<AtosAnestesicosPolVO> recuperarColecao() throws ApplicationBusinessException {
		return prontuarioOnlineFacade.pesquisarRelatorioAtosAnestesicos(seqMbcFichaAnestesia, vSessao);
	}

	public Long getSeqMbcFichaAnestesia() {
		return seqMbcFichaAnestesia;
	}

	public void setSeqMbcFichaAnestesia(Long seqMbcFichaAnestesia) {
		this.seqMbcFichaAnestesia = seqMbcFichaAnestesia;
	}

	public void setvSessao(String vSessao) {
		this.vSessao = vSessao;
	}

	public String getvSessao() {
		return vSessao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getPermiteImpressao() {
		return permiteImpressao;
	}

	public void setPermiteImpressao(Boolean permiteImpressao) {
		this.permiteImpressao = permiteImpressao;
	}

	public boolean verificarPermissaoParaImprimir() {
		/*
		 * permiteImpressao = Boolean.FALSE; if (origem != null) { if
		 * (origem.equals(EnumTargetOrigem.CONSULTA_AMBULATORIO.toString()) ||
		 * origem
		 * .equals(EnumTargetOrigem.CONSULTA_HISTORIA_OBSTETRICA.toString())) {
		 * permiteImpressao = permiteImprimirAtoAnestesicoAmbulatorioPOL; } else
		 * if (origem.equals(EnumTargetOrigem.CONSULTA_CIRURGIAS.toString()) ||
		 * origem.equals(EnumTargetOrigem.CONSULTA_PROCEDIMENTOS.toString())) {
		 * permiteImpressao =
		 * permiteImprimirAtoAnestesicoProcedimentosCirurgiasPOL; }else if
		 * (origem.equals(EnumTargetOrigem.EXAMES_POL.toString())) {
		 * permiteImpressao =
		 * Boolean.TRUE;//https://apus.hcpa.ufrgs.br/issues/25447#note-5 } }
		 * return permiteImpressao;
		 */
		return permiteImprimirRelatorioAtoAnestesicoPOL;
	}

	

}