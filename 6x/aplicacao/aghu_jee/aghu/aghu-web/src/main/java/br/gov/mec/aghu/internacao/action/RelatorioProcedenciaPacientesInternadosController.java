package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import br.gov.mec.aghu.dominio.DominioLocalizacaoPaciente;
import br.gov.mec.aghu.dominio.DominioOrdenacaoProcedenciaPacientes;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProcedenciaPacientesInternadosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * Relatório de Procedência de Pacientes Internados (SUS)
 * 
 * @author lalegre
 * 
 */
public class RelatorioProcedenciaPacientesInternadosController extends ActionReport {

	private static final long serialVersionUID = 1374343411865865286L;

	private static final Log LOG = LogFactory.getLog(RelatorioProcedenciaPacientesInternadosController.class);

	private static final String RELATORIO_PROCEDENCIA_PACIENTES_INTERNADOS_PDF = "relatorioProcedenciaPacientesInternadosPDF";
	private static final String RELATORIO_PROCEDENCIA_PACIENTES_INTERNADOS = "relatorioProcedenciaPacientesInternados";

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

	/**
	 * Combo Localização
	 */
	private DominioLocalizacaoPaciente localizacaoPaciente = DominioLocalizacaoPaciente.T;

	/**
	 * Combo Ordenação
	 */
	private DominioOrdenacaoProcedenciaPacientes ordenacaoProcedenciaPacientes = DominioOrdenacaoProcedenciaPacientes.C;

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioProcedenciaPacientesInternados.jasper";
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws SistemaImpressaoException, ApplicationBusinessException, JRException, SystemException, IOException {
		return "relatorio";
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
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
		return RELATORIO_PROCEDENCIA_PACIENTES_INTERNADOS_PDF;
	}

	@Override
	public Collection<ProcedenciaPacientesInternadosVO> recuperarColecao() throws ApplicationBusinessException {
		return internacaoFacade.pesquisaProcedenciaPacientesInternados(localizacaoPaciente, ordenacaoProcedenciaPacientes);
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
		params.put("nomeRelatorio", "AINR_CONTRA_REFERENC");
		params.put("tituloRelatorio", "Procedência dos Pacientes Internados Convênio SUS para Contra-Referência");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		// params.put("hospital", "Hospital de Clínicas de Porto Alegre");

		if (this.localizacaoPaciente == DominioLocalizacaoPaciente.E) {
			params.put("localizacao", " - EMERGÊNCIA");
		} else if (this.localizacaoPaciente == DominioLocalizacaoPaciente.I) {
			params.put("localizacao", " - INTERNAÇÃO");
		} else {
			params.put("localizacao", "");
		}

		return params;
	}

	public String voltar() {
		return RELATORIO_PROCEDENCIA_PACIENTES_INTERNADOS;
	}

	// GETS AND SETS
	public DominioLocalizacaoPaciente getLocalizacaoPaciente() {
		return localizacaoPaciente;
	}

	public void setLocalizacaoPaciente(DominioLocalizacaoPaciente localizacaoPaciente) {
		this.localizacaoPaciente = localizacaoPaciente;
	}

	public DominioOrdenacaoProcedenciaPacientes getOrdenacaoProcedenciaPacientes() {
		return ordenacaoProcedenciaPacientes;
	}

	public void setOrdenacaoProcedenciaPacientes(DominioOrdenacaoProcedenciaPacientes ordenacaoProcedenciaPacientes) {
		this.ordenacaoProcedenciaPacientes = ordenacaoProcedenciaPacientes;
	}
}