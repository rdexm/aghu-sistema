package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.ByteArrayOutputStream;
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
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.vo.RelatorioParadaInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.AgrupadorRelatorioJasper;
import br.gov.mec.aghu.core.report.PdfUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioParadaInternacaoController extends ActionReport {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}

	private static final Log LOG = LogFactory
			.getLog(RelatorioParadaInternacaoController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;

	@Inject
	@Http
	private ConversationContext conversationContext;

	ByteArrayOutputStream outputStrem;

	private static final long serialVersionUID = -1391664920968228871L;

	private static final String TITULO_RELATORIO = "Sumário da Internação Atual";

	private String titlePdfView;

	private String nomeArquivo;

	private String origem;

	private Integer atdSeq;

	private Date dthrCriacao;

	private List<RelatorioParadaInternacaoVO> dadosRelatorio;

	@Inject
	protected RelatorioParadaInternacaoFolhaPaisagemController relatorioParadaInternacaoFolhaPaisagemController;

	@Inject
	private RelatorioParadaInternacaoFolhaNormalController relatorioParadaInternacaoFolhaNormalController;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	private String prontuario;

	private Integer seqInternacao;

	private String tipoInternacao;

	@Override
	public String recuperarArquivoRelatorio() {
		return null;
	}

	private void loadDadosRelatorio() {
		// setOrigem((String) Contexts.getConversationContext().get("origem"));
		// setAtdSeq((Integer) Contexts.getConversationContext().get("atdSeq"));
		// setNomeArquivo((String)
		// Contexts.getConversationContext().get("nomeArquivo"));
		// setDthrCriacao((Date)
		// Contexts.getConversationContext().get("dthrCriacao"));
		// if(Contexts.getConversationContext().get("seq") != null){
		// setSeqInternacao((Integer)
		// Contexts.getConversationContext().get("seq"));
		// }
		// if(Contexts.getConversationContext().get("tipo") != null){
		// setTipoInternacao((String)
		// Contexts.getConversationContext().get("tipo"));
		// }

		try {
			setDadosRelatorio(new ArrayList<RelatorioParadaInternacaoVO>());

			RelatorioParadaInternacaoVO vo = prontuarioOnlineFacade
					.obterRelatorioParadaInternacao(getAtdSeq(),
							getDthrCriacao());
			prontuario = vo.getProntuario();

			if (vo != null) {
				getDadosRelatorio().add(vo);
			}

			setTitlePdfView(TITULO_RELATORIO);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Renderiza o PDF
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws JRException
	 * @throws DocumentException
	 * @throws BaseException
	 */
	public StreamedContent getRenderPdf() throws IOException,
			JRException, DocumentException, ApplicationBusinessException {
		try {
			loadDadosRelatorio();

			AgrupadorRelatorioJasper agrupador = new AgrupadorRelatorioJasper();
			Map<String, Object> parametros = new HashMap<String, Object>();

			parametros.put("BLOQUEAR_GERACAO_PENDENCIA", true);
			

			relatorioParadaInternacaoFolhaNormalController.prepararRelatorio(
					getDadosRelatorio(), this.getTitlePdfView());
			agrupador.addReport(relatorioParadaInternacaoFolhaNormalController
					.getReportGenerator().gerarDocumento(parametros).getJasperPrint());

			if (getDadosRelatorio() != null && !getDadosRelatorio().isEmpty()
					&& !getDadosRelatorio().get(0).getControles().isEmpty()) {
				relatorioParadaInternacaoFolhaPaisagemController
						.prepararRelatorio(getDadosRelatorio());
				agrupador
						.addReport(relatorioParadaInternacaoFolhaPaisagemController
								.getReportGenerator().gerarDocumento(parametros).getJasperPrint());
			}

			outputStrem = agrupador.exportReportAsOutputStream();

			ByteArrayOutputStream outputStremProtegido = PdfUtil.protectPdf(outputStrem); // Protegido? = TRUE
			
			byte[] relatorioArrayBytes = outputStremProtegido.toByteArray();
			return this.criarStreamedContentPdf(relatorioArrayBytes);

		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(
					ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO,
					e, e.getLocalizedMessage()));
		} catch (JRException e) {
			apresentarExcecaoNegocio(new BaseException(
					ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO,
					e, e.getLocalizedMessage()));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(new BaseException(
					ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO,
					e, e.getLocalizedMessage()));
		}
		return null;
	}

	public void directPrint() {
		try {
			this.sistemaImpressao.imprimir(outputStrem,
					super.getEnderecoIPv4HostRemoto(),
					"Sumário de Internação Atual");
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String voltar() {
		limparDados();
		return getOrigem();
	}

	private void limparDados() {
		setAtdSeq(null);
		setDadosRelatorio(null);
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public Date getDthrCriacao() {
		return dthrCriacao;
	}

	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

	private List<RelatorioParadaInternacaoVO> getDadosRelatorio() {
		return dadosRelatorio;
	}

	private void setDadosRelatorio(
			List<RelatorioParadaInternacaoVO> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public void setTitlePdfView(String titlePdfView) {
		this.titlePdfView = titlePdfView;
	}

	public String getTitlePdfView() {
		return titlePdfView;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getProntuario() {
		return prontuario;
	}

	public Integer getSeqInternacao() {
		return seqInternacao;
	}

	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}

	public String getTipoInternacao() {
		return tipoInternacao;
	}

	public void setTipoInternacao(String tipoInternacao) {
		this.tipoInternacao = tipoInternacao;
	}

	@Override
	protected Collection<?> recuperarColecao()
			throws ApplicationBusinessException {
		// TODO Auto-generated method stub
		return null;
	}

}
