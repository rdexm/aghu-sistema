package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.exames.agendamento.vo.AgendamentoExameVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.TicketExamesPacienteVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioTicketExamesPacienteController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioTicketExamesPacienteController.class);

	private static final long serialVersionUID = 7210844403149103307L;

	private static final String RELATORIO_TICKET_EXAMES_PACIENTE = "relatorioTicketExamesPaciente";
	private static final String RELATORIO_TICKET_EXAMES_PACIENTE_PDF = "relatorioTicketExamesPacientePdf";


	@Inject
	private SistemaImpressao sistemaImpressao;

	private Integer codSolicitacao;
	private Short unfSeq;
	private Set<Short> listaUnfSeq;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	
	private List<TicketExamesPacienteVO> colecao = new ArrayList<TicketExamesPacienteVO>();

	private List<AgendamentoExameVO> examesMarcadosParaImprimir;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		if(codSolicitacao == null ) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_BLANK");
		}
		
		this.colecao = examesFacade.pesquisarRelatorioTicketExamesPaciente(this.codSolicitacao, unfSeq);
		
		if (colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		
	
		return null;
		}
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_TICKET_EXAMES_PACIENTE_PDF;
		
		
	}
	
	public String voltar(){
		codSolicitacao = null;
		return RELATORIO_TICKET_EXAMES_PACIENTE;
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			if(examesMarcadosParaImprimir !=null && !examesMarcadosParaImprimir.isEmpty()){
				colecao = converterListaParaImprimir();
				examesMarcadosParaImprimir = new ArrayList<AgendamentoExameVO>();
			} else {
				colecao = examesFacade.pesquisarRelatorioTicketExamesPaciente(this.codSolicitacao, this.unfSeq, this.listaUnfSeq);
			}
			if (colecao.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}
			
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,
					e.getLocalizedMessage());
			return;
		}


		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}
	
	private List<TicketExamesPacienteVO> converterListaParaImprimir() {
		List<TicketExamesPacienteVO> retorno = new ArrayList<TicketExamesPacienteVO>();
		for (AgendamentoExameVO toPrint : examesMarcadosParaImprimir) {
			toPrint.getItemExame();
			TicketExamesPacienteVO vo = null;
			try {
				vo = examesFacade.geraTicketExamesPacienteVO(toPrint.getItemExame());
			} catch (Exception e) {
				LOG.error("Erro ao Converter Item Exame para TicketExamesPacienteVO.",e);
				vo = null;
			}
			if (vo!=null){
				retorno.add(vo);
			}
		}
		examesFacade.verificarSetarMaiorTempoJejum(retorno);
		return retorno;
	}

	@Override
	public Collection<TicketExamesPacienteVO> recuperarColecao() {
		return this.colecao;
	}
	
	protected void apresentarExcecaoNegocio(ApplicationBusinessException e) {
		// Apenas apresenta a mensagem de erro negocial para o cliente
		this.apresentarExcecaoNegocio(e);
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_TICKET_PACIENTE");
		params.put("tituloRelatorio", "Ticket de exames a realizar");
		params.put("totalRegistros", colecao.size()-1);

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioTicketExamesPaciente.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException,
			 JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param codSolicitacao
	 */
	//@Observer("imprimirTicketExamesPaciente")
	public void directPrint(Integer codSolicitacao, Short unfSeq, Set<Short> listaUnfSeq, Boolean imprimiu) throws BaseException,
			JRException, SystemException, IOException {
		setCodSolicitacao(codSolicitacao);
		setUnfSeq(unfSeq);
		setListaUnfSeq(listaUnfSeq);
		directPrint();
		if (!imprimiu) {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}		
			solicitacaoExameFacade.atualizarIndImpressaoSolicitacaoExames(codSolicitacao, nomeMicrocomputador);
		}
	}
			

	public Integer getCodSolicitacao() {
		return codSolicitacao;
	}

	public void setCodSolicitacao(Integer codSolicitacao) {
		this.codSolicitacao = codSolicitacao;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Set<Short> getListaUnfSeq() {
		return listaUnfSeq;
	}

	public void setListaUnfSeq(Set<Short> listaUnfSeq) {
		this.listaUnfSeq = listaUnfSeq;
	}

	public void setListaExamesImpressao(List<AgendamentoExameVO> examesMarcadosParaImprimir) {
		this.examesMarcadosParaImprimir = examesMarcadosParaImprimir;
	}

}
