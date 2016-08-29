package br.gov.mec.aghu.paciente.action;

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
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.VAipPacientesExcluidos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * Controller para geração do relatório 'Prontuários Excluídos'.
 * 
 * @author lalegre
 */


public class RelatorioProntuariosExcluidosController extends ActionReport {

	
	private static final long serialVersionUID = 4178825939566871925L;
	private static final Log LOG = LogFactory.getLog(RelatorioProntuariosExcluidosController.class);
	private static final String PACIENTE_RELATORIO_PRONTUARIOS_EXCLUIDOS = "paciente-relatorioProntuariosExcluidos";
	private static final String PACIENTE_RELATORIO_PRONTUARIOS_EXCLUIDOS_PDF = "paciente-relatorioProntuariosExcluidosPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB 
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Data Inicial
	 */
	private Date dataInicial;

	private Date dataFinal = new Date();

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<VAipPacientesExcluidos> colecao = new ArrayList<VAipPacientesExcluidos>(0);
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	/**
	 * Busca os dados para geração do relatório.
	 */
	public String print() throws  JRException {
		if(geraDadosColecao()){
				return PACIENTE_RELATORIO_PRONTUARIOS_EXCLUIDOS_PDF;
		}
		return null;
	}

	private boolean geraDadosColecao() {
		try {
			
			this.pacienteFacade.validaDatas(getDataInicial(), getDataFinal());
			this.colecao = pacienteFacade.pesquisaPacientesExcluidos(getDataInicial(), getDataFinal());
			
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return false;
		}

		return true;
	}
	
	public String voltar(){
		this.dataInicial = null;
		this.dataFinal = new Date();
		return PACIENTE_RELATORIO_PRONTUARIOS_EXCLUIDOS;
	}

	/**
	 * Impressao direta via CUPS.
	 */
	public void directPrint() {

		// Valida dados de entrada e prepara dados para geração do relatório.
		if(geraDadosColecao()){
			try {
				
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);

			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
				
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}	
		}

	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException,
			JRException, SystemException, DocumentException {
		DocumentoJasper documento = this.gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Collection<VAipPacientesExcluidos> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/prontuario/report/relatorioProntuariosExcluidos.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AIPR_PRONT_EXCLUIDOS");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);

		return params;
	}

	// GETs AND SETs

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}
