package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioPacientesUnidade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.PacienteUnidadeFuncionalVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author tfelini
 */
public class RelatorioPacientesPorUnidadeController extends ActionReport {

	private static final long serialVersionUID = -4581914122246229630L;

	private static final Log LOG = LogFactory.getLog(RelatorioPacientesPorUnidadeController.class);

	private static final String RELATORIO_PACIENTES_POR_UNIDADE_PDF = "relatorioPacienteUnidadePdf";
	private static final String RELATORIO_PACIENTES_POR_UNIDADE = "relatorioPacienteUnidade";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	private AghUnidadesFuncionais unidadeFuncional;

	private DominioOrdenacaoRelatorioPacientesUnidade ordenacao;

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws ApplicationBusinessException, JRException, SystemException, IOException {
		return "relatorio";
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 */
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String imprimirRelatorio() {
		try {
			directPrint();
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}

	public String visualizarRelatorio() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		return RELATORIO_PACIENTES_POR_UNIDADE_PDF;
	}

	@Override
	public Collection<PacienteUnidadeFuncionalVO> recuperarColecao() throws ApplicationBusinessException {
		return internacaoFacade.pesquisaPacientesPorUnidadesFuncionais(this.unidadeFuncional, ordenacao);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioPacientesPorUnidadeFuncinal.jasper";
	}

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
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AINR_PAC_UNID_FUNC_T");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);

		return params;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String param) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorUnidEmergencia((String) param, true);
	}

	public String voltar() {
		return RELATORIO_PACIENTES_POR_UNIDADE;
	}

	// GETTERS e SETTERS
	public void limparUnidadeFuncional() {
		this.unidadeFuncional = null;
	}

	public boolean isMostrarLinkExcluirUnidadeFuncional() {
		return this.getUnidadeFuncional().getSeq() != null;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public DominioOrdenacaoRelatorioPacientesUnidade getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(DominioOrdenacaoRelatorioPacientesUnidade ordenacao) {
		this.ordenacao = ordenacao;
	}
}