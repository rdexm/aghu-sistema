package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.PacientesComObitoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * Relatório de Pacientes com obito
 * 
 * @author lalegre
 * 
 */
public class RelatorioPacientesComObitoController extends ActionReport {

	private static final long serialVersionUID = -3236241415942691176L;

	private static final Log LOG = LogFactory.getLog(RelatorioPacientesComObitoController.class);

	private static final String RELATORIO_PACIENTES_COM_OBITO_PDF = "relatorioPacientesComObitoPDF";
	private static final String RELATORIO_PACIENTES_COM_OBITO = "relatorioPacientesComObito";

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
	 * Data inicial de referência
	 */
	private Date dtInicialReferencia = new Date();

	/**
	 * Data final de referência
	 */
	private Date dtFinalReferencia = new Date();

	/**
	 * Idade inicial do paciente para pesquisa
	 */
	private Integer idadeInicial;

	/**
	 * Idade final do paciente para pesquisa
	 */
	private Integer idadeFinal;

	/**
	 * Sexo do paciente
	 */
	private DominioSexo sexo;

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
		return RELATORIO_PACIENTES_COM_OBITO_PDF;
	}

	@Override
	public Collection<PacientesComObitoVO> recuperarColecao() throws ApplicationBusinessException {
		Date dataFinalReferenciaAjustada = dtFinalReferencia;
		// Ajuste nas horas, minutos e segundos da data final de referência
		if (dtFinalReferencia != null) {
			Calendar calendarDtFinalRef = Calendar.getInstance();
			calendarDtFinalRef.setTime(dtFinalReferencia);
			calendarDtFinalRef.set(Calendar.HOUR_OF_DAY, 23);
			calendarDtFinalRef.set(Calendar.MINUTE, 59);
			calendarDtFinalRef.set(Calendar.SECOND, 59);
			dataFinalReferenciaAjustada = calendarDtFinalRef.getTime();
		}

		return internacaoFacade.pesquisaPacientesComObito(dtInicialReferencia, dataFinalReferenciaAjustada, idadeInicial, idadeFinal, sexo);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioPacientesComObito.jasper";
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
		params.put("nomeRelatorio", "Relatório de Pacientes com Óbito");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		// params.put("hospital", "Hospital de Clínicas de Porto Alegre");

		return params;
	}

	public String voltar() {
		return RELATORIO_PACIENTES_COM_OBITO;
	}

	// GETs AND SETs
	public Date getDtInicialReferencia() {
		return dtInicialReferencia;
	}

	public void setDtInicialReferencia(Date dtInicialReferencia) {
		this.dtInicialReferencia = dtInicialReferencia;
	}

	public Date getDtFinalReferencia() {
		return dtFinalReferencia;
	}

	public void setDtFinalReferencia(Date dtFinalReferencia) {
		this.dtFinalReferencia = dtFinalReferencia;
	}

	public Integer getIdadeInicial() {
		return idadeInicial;
	}

	public void setIdadeInicial(Integer idadeInicial) {
		this.idadeInicial = idadeInicial;
	}

	public Integer getIdadeFinal() {
		return idadeFinal;
	}

	public void setIdadeFinal(Integer idadeFinal) {
		this.idadeFinal = idadeFinal;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}
}