package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRException;

import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.HistoricoPacientePolVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author tfelini
 */


public class RelatorioHistoricoPacienteController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6176911321970282368L;

	private Integer prontuario;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject @Http 
	private ConversationContext conversationContext;



	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<HistoricoPacientePolVO> colecao;

	private String paginaOrigem = null;

	public void observarGeracaoRelatorioHistoricoPaciente(Integer numeroProntuario) throws BaseException {
		this.setProntuario(numeroProntuario);

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

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws IOException
	 */
	@Override
	public Collection<HistoricoPacientePolVO> recuperarColecao() throws ApplicationBusinessException {
		this.colecao = pacienteFacade.pesquisaHistoricoPaciente(this.prontuario);
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/report/relatorioHistoricoPacientePol.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws ApplicationBusinessException 
	 * @throws IOException
	 * @throws JRException
	 * @throws BaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, IOException, JRException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}

	public void directPrint() {
		try {
			this.observarGeracaoRelatorioHistoricoPaciente(prontuario);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		params.put("logoPath", servletContext.getRealPath("/resources/img/logoSus.jpg"));

		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		params.put("nomePaciente", pacienteFacade.pesquisarNomePaciente(this.prontuario));
		params.put("prontuarioPaciente", CoreUtil.formataProntuario(this.prontuario));
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AIPR_POL_HIST_PAC");
		params.put("subHistorico", Thread.currentThread().getContextClassLoader().getResourceAsStream("br/gov/mec/aghu/paciente/report/subRelatorioHistoricoPacientePol.jasper"));
		params.put("subHistoricoDataFim", Thread.currentThread().getContextClassLoader().getResourceAsStream("br/gov/mec/aghu/paciente/report/subRelatorioHistoricoPacientePolDataFim.jasper"));

		return params;
	}

	// GETTERS e SETTERS
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public List<HistoricoPacientePolVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<HistoricoPacientePolVO> colecao) {
		this.colecao = colecao;
	}

	public String getPaginaOrigem() {
		return paginaOrigem;
	}

	public void setPaginaOrigem(String paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}
}
