package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RelatorioBoletimInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioBoletimInternacaoController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7456775144517829944L;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	// Parametro recebido pela URL
	private Integer codigoInternacao = null;

	private Integer pacCodigo;

	// Atributo estático para controle do parametro codigoInternacao recebido
	// por URL. Como os relatórios chamam 2 vezes o método rederPdf(), na
	// segunda vez o parametro vindo da URL é anulada, assim teve que ser
	// colocado esse atributo estático para armazenar esse valor. Implementado
	// método "setCodigoInternacao()" com atribuição a esse atributo.
	@SuppressWarnings("unused")
	private static Integer codigoInternacao2 = null;

	private RelatorioBoletimInternacaoVO dadosRelatorio = new RelatorioBoletimInternacaoVO();

	private static final String CADASTRO_INTERNACAO = "cadastroInternacao";

	private static final Log LOG = LogFactory.getLog(RelatorioBoletimInternacaoController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<Object> colecao = new ArrayList<Object>(0);

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException,
			DocumentException {

		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioBoletimInternacao.jasper";
	}

	/**
	 * @author jccouto (Jean Couto) - Charruas
	 * @param seqInternacao
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * 
	 *             Issue #28740 - Alteração do método de impressão para que seja
	 *             enviado direto para impressora, sem necessidade de
	 *             visualização.
	 */
	public void print(Integer seqInternacao, Short convenioId) throws ApplicationBusinessException, JRException, SystemException,
			IOException {
		try {

			internacaoFacade.verificarNecessidadeInformarDiaria(seqInternacao, convenioId);

			this.dadosRelatorio = internacaoFacade.imprimirRelatorioBoletimInternacao(seqInternacao);
			colecao = new ArrayList<Object>();
			colecao.add(dadosRelatorio);

			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (OptimisticLockException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		params.put("footerDataHoraGeracao", sdf.format(dataAtual));
		params.put("footerAbreveacaoNomeRelatorio", "AINR_BOLETIM_INT");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("footerNomeHospital", hospital);
		params.put("footerNomeRelatorio", "Boletim de Identificação / Internação");
		params.put("footerCaminhoLogo", parametroFacade.recuperarCaminhoLogo2Relativo());
		
		return params;
	}

	@Override
	public Collection<Object> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	// retornar para tela de cadastro de Internacao
	public String cancelar() {
		LOG.info("Cancelado");
		return CADASTRO_INTERNACAO;
	}

	public Integer getCodigoInternacao() {
		return codigoInternacao;
	}

	public void setCodigoInternacao(Integer codigoInternacao) {
		this.codigoInternacao = codigoInternacao;

		if (codigoInternacao != null) {
			codigoInternacao2 = codigoInternacao;
		}
	}

	public RelatorioBoletimInternacaoVO getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(RelatorioBoletimInternacaoVO dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

}
