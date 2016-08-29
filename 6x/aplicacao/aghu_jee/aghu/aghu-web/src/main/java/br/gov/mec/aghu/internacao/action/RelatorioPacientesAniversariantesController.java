package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.net.UnknownHostException;
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
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.PacientesAniversariantesVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * 
 * Retatório de Pacientes Aniversáriantes
 * 
 * @author bsoliveira
 * 
 */
public class RelatorioPacientesAniversariantesController extends ActionReport {

	private static final long serialVersionUID = -5552316203689401420L;

	private static final Log LOG = LogFactory.getLog(RelatorioPacientesAniversariantesController.class);

	private static final String RELATORIO_PACIENTES_ANIVERSARIANTES_PDF = "relatorioPacientesAniversariantesPDF";
	private static final String RELATORIO_PACIENTES_ANIVERSARIANTES = "relatorioPacientesAniversariantes";
	
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
	 * FILTRO RELATÓRIO Data de referência
	 */
	private Date dtReferencia = new Date();

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws SistemaImpressaoException, ApplicationBusinessException, UnknownHostException, JRException, SystemException, IOException {
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
			LOG.error("Exceção capturada: ", e);
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
		return RELATORIO_PACIENTES_ANIVERSARIANTES_PDF;
	}

	@Override
	public Collection<PacientesAniversariantesVO> recuperarColecao() throws ApplicationBusinessException {
		return internacaoFacade.pesquisaPacientesAniversariantes(dtReferencia);
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
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

		params.put("nomeRelatorio", "Pacientes Internados que estão Aniversariando em " + sdf1.format(dtReferencia));
		params.put("nomeResumidoRelatorio", "AINR ANIVERSARIANTES");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		// params.put("hospital",
		// ResourceBundle.instance().getString("NOME_HOSPITAL"));

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioPacientesAniversariantes.jasper";
	}

	public String voltar() {
		return RELATORIO_PACIENTES_ANIVERSARIANTES;
	}

	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public Date getDtReferencia() {
		return dtReferencia;
	}

}
