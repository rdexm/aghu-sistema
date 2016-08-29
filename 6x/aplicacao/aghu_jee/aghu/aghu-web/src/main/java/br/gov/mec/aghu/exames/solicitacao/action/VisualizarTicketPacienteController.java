package br.gov.mec.aghu.exames.solicitacao.action;

import java.io.ByteArrayInputStream;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.ambulatorio.action.AtenderPacientesEvolucaoController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.TicketExamesPacienteVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


/**
 * Controller para visualização do ticket do paciente após gravar a soliticação.
 * Baseada no relatório relatorioTicketExamesPaciente.
 * 
 */

public class VisualizarTicketPacienteController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(VisualizarTicketPacienteController.class);
	
	private static final long serialVersionUID = -7064786505600458656L;
	
	private static final String PAGE_VOLTAR_PARA_LISTA_SOLICITACAO="exames-voltarParaListaSolicitacao";
	
	/**
	 * Filtro de pesquisa
	 */
	private Integer codSolicitacao;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private String paginaChamadora;
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<TicketExamesPacienteVO> colecao = new ArrayList<TicketExamesPacienteVO>();
	
	@Inject 
	private AtenderPacientesEvolucaoController atenderPacientesEvolucaoController;

	
	@PostConstruct
	protected void inicializar() {
		LOG.info("VisualizarTicketPacienteController.inicializar...");
		
		this.begin(conversation);
	}

	
	public String voltar() {
		if(paginaChamadora != null && !paginaChamadora.equals("")) {
			atenderPacientesEvolucaoController.setAcao(null);
			return paginaChamadora;
		}
		
		return PAGE_VOLTAR_PARA_LISTA_SOLICITACAO;
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public void print() throws BaseException, JRException, SystemException, IOException, DocumentException {
	 

		if (codSolicitacao == null ) {
			this.apresentarMsgNegocio(Severity.ERROR,
			"MENSAGEM_CAMPO_BLANK");
		}
		
		this.colecao = examesFacade.pesquisarRelatorioTicketExamesPaciente(this.codSolicitacao, null);

		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
	
	}
	
	@Override
	public Collection<TicketExamesPacienteVO> recuperarColecao() throws ApplicationBusinessException {
	
		return this.colecao;
	}
	
	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		//SimpleDateFormat sdf_2 = new SimpleDateFormat("dd/MM/yyyy");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_TICKET_PACIENTE");
		params.put("tituloRelatorio", "Ticket de exames a realizar");
		params.put("totalRegistros", colecao.size()-1);


		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return	"br/gov/mec/aghu/exames/report/relatorioTicketExamesPaciente.jasper";
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
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
			

	public Integer getCodSolicitacao() {
		return codSolicitacao;
	}

	public void setCodSolicitacao(Integer codSolicitacao) {
		this.codSolicitacao = codSolicitacao;
	}
	
	public String getPaginaChamadora() {
		return paginaChamadora;
	}
	
	public void setPaginaChamadora(String paginaChamadora) {
		this.paginaChamadora = paginaChamadora;
	}

}
