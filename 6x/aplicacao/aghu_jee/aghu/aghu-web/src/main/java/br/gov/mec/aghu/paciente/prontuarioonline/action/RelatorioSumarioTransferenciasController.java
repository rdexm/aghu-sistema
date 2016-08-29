package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelatorioSumarioTransferenciaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioSumarioTransferenciasController extends ActionReport {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioSumarioTransferenciasController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	private static final long serialVersionUID = 9143747578292350584L;

	private Integer seqAtendimento;
	private Short seqpAltaSumario;


	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	IProntuarioOnlineFacade prontuarioOnlineFacade;


	private String tipoImpressao;

	private String voltarPara;

	@Override
	public Collection<RelatorioSumarioTransferenciaVO> recuperarColecao()
			throws ApplicationBusinessException {

		AghAtendimentos atendimento = this.aghuFacade
				.obterAghAtendimentoPorChavePrimaria(seqAtendimento);

		/*
		 * listaAltasSumarios = atendimento.getAltasSumario(); if
		 * (listaAltasSumarios.isEmpty()) { return null; }return
		 * "br/gov/mec/aghu/paciente/prontuarioonline/report/relatorioSumarioTransferencia.jasper"
		 * ;
		 * 
		 * MpmAltaSumarioId id = this.listaAltasSumarios.get(
		 * this.listaAltasSumarios.size() - 1).getId();
		 */
		return Arrays.asList(this.buscarColecao(atendimento));
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws BaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws IOException,
			ApplicationBusinessException, JRException, SystemException, DocumentException {
		if (voltarPara == null) {
			voltarPara = "/paciente/prontuarioonline/consultarInternacoes.xhtml";
		}
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
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/relatorioSumarioTransferencia.jasper";

		// alterar a chamada para o metodo da controller.(recuperarColecao())
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
		params.put("previaUrlImagem", recuperarCaminhoImgBackground());

		return params;
	}

	private String recuperarCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext
				.getRealPath("/resources/img/report_previa.png");
		return path;
	}

	private RelatorioSumarioTransferenciaVO buscarColecao(
			AghAtendimentos atendimento) throws ApplicationBusinessException {
		return this.prontuarioOnlineFacade.criarRelatorioSumarioTransferencia(
				atendimento, this.tipoImpressao, seqpAltaSumario);
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public String getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(String tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public Short getSeqpAltaSumario() {
		return seqpAltaSumario;
	}

	public void setSeqpAltaSumario(Short seqpAltaSumario) {
		this.seqpAltaSumario = seqpAltaSumario;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

}
