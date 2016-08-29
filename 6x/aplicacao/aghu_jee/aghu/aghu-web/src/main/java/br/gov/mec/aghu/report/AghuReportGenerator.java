package br.gov.mec.aghu.report;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.AghDocumentoContingencia;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.vo.MensagemPendenciaAssinaturaVO;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.EmptyReportException;
import br.gov.mec.aghu.core.mail.ContatoEmail;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.report.ReportGenerator;

import com.itextpdf.text.DocumentException;

/**
 * Report Gernerator específico do aghu, que contem as regras a serem executadas
 * após a geração relatório.
 * 
 * @author geraldo
 * 
 */
public abstract class AghuReportGenerator extends ReportGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3177945104666545771L;

	private static final Log LOG = LogFactory.getLog(AghuReportGenerator.class);

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@EJB
	private IAghuFacade aghuFacade;

	public final String BLOQUEAR_GERACAO_PENDENCIA = "BLOQUEAR_GERACAO_PENDENCIA";

	public final String TIPO_DOCUMENTO = "TIPO_DOCUMENTO";

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/queue/pendenciaAssinaturaNivel1Queue")
	private Destination destination;

	private AghDocumentoCertificado documentoCertificado;

	private RapServidores servidorLogado;

	@Inject
	private EmailUtil emailUtil;

	private enum AghuReportGeneratorExceptionCode implements
			BusinessExceptionCode {
		CAMINHO_RELATORIO_OBRIGATORIO
	}

	/**
	 * Retorna um DocumentoJasper preenchido com os dados fornecidos pelos
	 * métodos sobrescritos. <br />
	 * Deste objeto pode ser obtidos JasperPrint, PDFs(protegidos ou não) e
	 * XLSs.
	 * 
	 * @see DocumentoJasper
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public final DocumentoJasper gerarDocumento(Boolean bloquearGeracaoPendencia)
			throws ApplicationBusinessException, EmptyReportException {

		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put(BLOQUEAR_GERACAO_PENDENCIA, bloquearGeracaoPendencia);

		return this.gerarDocumento(parametros);
	}

	/**
	 * Retorna um DocumentoJasper preenchido com os dados fornecidos pelos
	 * métodos sobrescritos e seta parâmetros para geração de pendência de
	 * assinatura. <br />
	 * Deste objeto pode ser obtidos JasperPrint, PDFs(protegidos ou não) e
	 * XLSs.
	 * 
	 * @see DocumentoJasper
	 * 
	 * @return
	 * @throws MECBaseException
	 */
	public final DocumentoJasper gerarDocumento(
			DominioTipoDocumento tipoDocumento)
			throws ApplicationBusinessException, EmptyReportException {

		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put(TIPO_DOCUMENTO, tipoDocumento);

		return this.gerarDocumento(parametros);
	}

	/**
	 * Retorna um DocumentoJasper preenchido com os dados fornecidos pelos
	 * métodos sobrescritos e seta parâmetros para geração de pendência de
	 * assinatura. <br />
	 * Deste objeto pode ser obtidos JasperPrint, PDFs(protegidos ou não) e
	 * XLSs.
	 * 
	 * @see DocumentoJasper
	 * @param tipoDocumento
	 * @param bloquearGeracaoPendencia
	 * @return
	 * @throws MECBaseException
	 */
	public final DocumentoJasper gerarDocumento(
			DominioTipoDocumento tipoDocumento, boolean bloquearGeracaoPendencia)
			throws ApplicationBusinessException, EmptyReportException {
		if (tipoDocumento == null) {
			throw new IllegalArgumentException("Argumentos obrigatórios");
		}

		Map<String, Object> parametros = new HashMap<String, Object>();

		parametros.put(TIPO_DOCUMENTO, tipoDocumento);
		parametros.put(BLOQUEAR_GERACAO_PENDENCIA, bloquearGeracaoPendencia);

		return this.gerarDocumento(parametros);
	}
	
	/**
	 * Método usado para gerar o relatório e criar uma cópia de segurança.
	 */
	public void imprimirRelatorioCopiaSeguranca(boolean protegido) throws BaseException, JRException, IOException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		this.gerarCopiaSeguranca(DominioMimeType.PDF, documento.getPdfByteArray(protegido));
	}
	
	/**
	 * Método usado para gerar o relatório e criar uma cópia de segurança.
	 */
	private void gerarCopiaSeguranca(DominioMimeType formato, byte[] documento) throws BaseException {
		AghDocumentoContingencia documentoContingencia = new AghDocumentoContingencia();
		documentoContingencia.setArquivo(documento);
		documentoContingencia.setFormato(formato);
		documentoContingencia.setNome(recuperarNomeArquivoRelatorio());

		aghuFacade.persistirDocumentoContigencia(documentoContingencia);
	}
	
	private String recuperarNomeArquivoRelatorio() {
		String arquivoRelatorio = this.recuperarArquivoRelatorio();

		int lastIndexBarra = arquivoRelatorio.lastIndexOf('/');
		int indexPonto = arquivoRelatorio.lastIndexOf('.');

		return arquivoRelatorio.substring(lastIndexBarra + 1, indexPonto);

	}
	
	
	
	/**
	 * Método que executa após a geração de um relatório. Na implementação
	 * padrão dos relatórios do AGHU, verifica e gera uma pendência de
	 * assinatura.
	 */
	@Override
	protected void executarPosGeracaoRelatorio(Map<String, Object> parametros)
			throws ApplicationBusinessException {
		if (verificarGeracaoPendenciaAssinatura(parametros)) {
			this.gerarPendenciaAssinatura();
		}

	}

	/**
	 * Verifica se é necessária a geração de pendencia de assinatura para o caso
	 * sendo avaliado.
	 * 
	 * @param parametros
	 * @return true - é preciso gerar a pendencia de assinatura.
	 * @throws ApplicationBusinessException
	 */
	protected boolean verificarGeracaoPendenciaAssinatura(
			Map<String, Object> parametros) throws ApplicationBusinessException {
		// verificando bloqueio dinâmico de geração de pendencia de assinatura.
		Boolean bloquearGeracaoPendencia = false;
		if (parametros != null
				&& parametros.containsKey(BLOQUEAR_GERACAO_PENDENCIA)) {
			bloquearGeracaoPendencia = (Boolean) parametros
					.get(BLOQUEAR_GERACAO_PENDENCIA);
		}

		if (bloquearGeracaoPendencia) {
			LOG.info("Flag de bloqueio de geração de pendência detectada. Geração de pendencia de assinatura abortada.");
			return false;
		}

		// verificando parâmetro global do sistema de geração de pendencia de
		// assinatura.
		Boolean certificacaoDigital = false;
		String valorParametroCertificacaoDigital = parametroFacade
				.buscarAghParametro(
						AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL)
				.getVlrTexto();

		if (valorParametroCertificacaoDigital != null) {
			certificacaoDigital = DominioSimNao.valueOf(
					valorParametroCertificacaoDigital).isSim();
		}

		if (!certificacaoDigital) {
			LOG.info("Geração de pendencia de assinatura desabilitada para este hospital");
			return false;
		}

		// verificando se a pendencia de assinatura digital está habilitada para
		// este relatorio.
		String nomeRelatorio = null;
		if (parametros != null
				&& parametros.containsKey(PARAMETRO_CAMINHO_RELATORIO)) {
			nomeRelatorio = (String) parametros
					.get(PARAMETRO_CAMINHO_RELATORIO);
		} else {
			throw new BaseRuntimeException(
					AghuReportGeneratorExceptionCode.CAMINHO_RELATORIO_OBRIGATORIO);
		}

		DominioTipoDocumento tipoDocumento = null;
		if (parametros.containsKey(TIPO_DOCUMENTO)) {
			tipoDocumento = (DominioTipoDocumento) parametros
					.get(TIPO_DOCUMENTO);
		}

		documentoCertificado = this.verificarRelatorioNecessitaAssinatura(
				nomeRelatorio, tipoDocumento);

		if (documentoCertificado == null) {
			LOG.info("Geração de pendencia de assinatura está desabilitada para este relatório");
			return false;
		}
		return true;
	}

	/**
	 * Gera uma mensagem jms para a fila de pendência de assinaturas. Essa
	 * mensagem será consumida posteriormente por um MDB que irá gerar o
	 * registro de banco em si.
	 */
	protected void gerarPendenciaAssinatura() {
		LOG.info("Gerando mensagem jms para fila de pendência de assinatura...");

		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			MessageProducer messageProducer = session
					.createProducer(destination);

			byte[] byteArray = documentoJasper.getPdfByteArray(false);

			MensagemPendenciaAssinaturaVO mensagemVO = new MensagemPendenciaAssinaturaVO();
			mensagemVO.setArquivoGerado(byteArray);
			mensagemVO.setEntidadePai(getEntidadePai());
			mensagemVO.setDocumentoCertificado(documentoCertificado);

			mensagemVO.setServidorLogado(this.getServidorLogado());

			Message mensagem = session.createObjectMessage(mensagemVO);
			messageProducer.send(mensagem);

		} catch (JMSException | JRException | IOException | DocumentException e) {
			LOG.error(e.getMessage(), e);
			this.enviarEmailErro(e.getMessage());			
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}

	}

	private void enviarEmailErro(String mensagemErro) {
		try {
			AghParametros pEmailEnvioAdmAGHU = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_EMAIL_ENVIO_ADM_AGHU);
			AghParametros pEmailErroGeracaoPendenciaAssinatura = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_EMAIL_ERRO_GERACAO_PENDENCIA_ASSINATURA);

			String emailEnvioAdmAGHU = pEmailEnvioAdmAGHU.getVlrTexto();

			String emailErroGeracaoPendenciaAssinatura = pEmailErroGeracaoPendenciaAssinatura
					.getVlrTexto();

			ContatoEmail remetente = new ContatoEmail("sistema AGHU",
					emailEnvioAdmAGHU);
			ContatoEmail destinatario = new ContatoEmail(
					emailErroGeracaoPendenciaAssinatura);

			emailUtil.enviaEmail(remetente, destinatario, null,
					"Erro ao gerar Pendencia de assinatura",
					"Aconteceu um problema ao gerar pendência de assinatura: "
							+ mensagemErro);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao enviar email de notificação de erro na geração de pendencia de assinatura");
			LOG.error(e.getMessage(), e);
		}

	}

	/**
	 * Método que identifica se a geração de um relatório implica na geração de
	 * uma pendencia de assinatura. A implementação padrão consulta a tabela
	 * Agh_Documento_Certificado e o parâmetro p_aghu_certificacao_digital.
	 * 
	 * @param nomeRelatorio
	 * @return Se o relatório necessita da geração da pendência de assinatura -
	 *         retornar um objeto AghDocumentoCertificado com as informações
	 *         necessárias <br/>
	 *         Se o relatório não necessita de geração de pendência de
	 *         assinatura - retorna null
	 * @throws MECBaseException
	 */
	protected AghDocumentoCertificado verificarRelatorioNecessitaAssinatura(
			String nomeRelatorio, DominioTipoDocumento tipoDocumento)
			throws ApplicationBusinessException {

		return this.certificacaoDigitalFacade
				.verificarRelatorioNecessitaAssinatura(nomeRelatorio,
						tipoDocumento);
	}

	protected String recuperarCaminhoLogo() throws ApplicationBusinessException {
		return parametroFacade.recuperarCaminhoLogo();
	}

	protected String recuperarCaminhoLogo2()
			throws ApplicationBusinessException {
		return parametroFacade.recuperarCaminhoLogo2();
	}

	/**
	 * Método que retorna a entidade pai do relatório, a ser usada na geração da
	 * pendência de assinatura. Deve ser sobrescrito nas controllers do
	 * relatórios que vão gerar pendência de assinatura.
	 * 
	 * @return
	 */
	protected BaseBean getEntidadePai() {
		return null;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidor) {
		this.servidorLogado = servidor;
	}

	public AghDocumentoCertificado getDocumentoCertificado() {
		return documentoCertificado;
	}

	public void setDocumentoCertificado(AghDocumentoCertificado documentoCertificado) {
		this.documentoCertificado = documentoCertificado;
	}

	
}
