package br.gov.mec.aghu.exames.agendamento.action;

import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorSalaVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.SolicitacaoInternacaoUnidadesFechadasController;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;


public class RelatorioAgendasPorSalaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 1585906080710567133L;

	private static final String RELATORIO_AGENDAS_POR_SALA = "relatorioAgendasPorSala";
	private static final String RELATORIO_AGENDAS_POR_SALA_PDF = "relatorioAgendasPorSalaPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private SolicitacaoInternacaoUnidadesFechadasController solicitacaoInternacaoUnidadesFechadasController;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@Inject
	private EtiquetaEnvelopePacienteController etiquetaEnvelopePacienteController;
	
	private AelUnidExecUsuario usuarioUnidadeExecutora;

	/**
	 * Filtro de pesquisa
	 */
	private AghUnidadesFuncionais unidadeExecutora;
	private Date dtAgenda;
	private AelSalasExecutorasExames sala;
	private Boolean impHorariosLivres = true;
	private Boolean impEtiquetas;
	private Boolean impTickets;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
		if (this.usuarioUnidadeExecutora == null){ 
			// Obtem o usuario da unidade executora
			try {
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				usuarioUnidadeExecutora=null;
			}
	
			// Resgata a unidade executora associada ao usuario
			if (this.usuarioUnidadeExecutora != null) {
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}
       }	
	}
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioAgendaPorSalaVO> colecao = new ArrayList<RelatorioAgendaPorSalaVO>();
	
	/**
	 * Método responsável pela visualização do relatório.
	 */
	public String visualizarImpressao() throws BaseException, JRException, SystemException, IOException {
		this.colecao = agendamentoExamesFacade.obterAgendasPorSala(unidadeExecutora, dtAgenda, sala, impHorariosLivres, impEtiquetas, impTickets);
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_AGENDA_SALA_VAZIA");
			return null;
		}
		return RELATORIO_AGENDAS_POR_SALA_PDF;
	}

	public String voltar(){
		return RELATORIO_AGENDAS_POR_SALA;
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			this.colecao = agendamentoExamesFacade.obterAgendasPorSala(unidadeExecutora, dtAgenda, sala, impHorariosLivres, impEtiquetas, impTickets);

		} catch (Exception e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,
					e.getLocalizedMessage());
			return;
		}
		
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_AGENDA_SALA_VAZIA");
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "'Agenda Por Sala'");
		
			//IMP_TICKETS
			if(agendamentoExamesFacade.isImprimeTicketsAgendas(unidadeExecutora, impTickets)) {
				List<Integer> solicitacoes = agendamentoExamesFacade.obterSolicitacoesExame(colecao);
				if(solicitacoes.isEmpty()) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SOLICITACOES_EXAMES_TICKET_VAZIA");
					
				} else {
					solicitacaoInternacaoUnidadesFechadasController.imprimirSolicitacoes(unidadeExecutora, solicitacoes);
				}
			}
			
			//IMP_ETIQUETAS
			//Se impressoraEtiquetas == null então não imprimir etiquetas 
			ImpImpressora impressoraEtiquetas = agendamentoExamesFacade.isImprimeEtiquetasAgendas(unidadeExecutora, impEtiquetas);
			if(impressoraEtiquetas != null) {
				List<Integer> listaSolic = new ArrayList<Integer>();
				List<AelSolicitacaoExames> solicitacoes = agendamentoExamesFacade.obterSolicitacoesExamePorSeq(colecao);
				if(solicitacoes.isEmpty()) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SOLICITACOES_EXAMES_ETIQUETAS_VAZIA");
				} else {
					for(AelSolicitacaoExames solicitacao : solicitacoes) {
						listaSolic.add(solicitacao.getSeq());
					}

					for(Integer nroSolicitacao : listaSolic) {
						etiquetaEnvelopePacienteController.imprimirEnvelopePaciente(impressoraEtiquetas, nroSolicitacao, this.unidadeExecutora);
					}
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<RelatorioAgendaPorSalaVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	public List<RelatorioAgendaPorSalaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioAgendaPorSalaVO> colecao) {
		this.colecao = colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("tituloRelatorio", "Agenda da "+unidadeExecutora.getDescricao()+" em "+DateFormatUtil.obterDataFormatada(dtAgenda, DateConstants.DATE_PATTERN_DDMMYYYY));
		params.put("nomeRelatorio", "AELR_AGENDA_SALA");

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/agendamento/report/relatorioAgendasPorSala.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public void limparSuggestions(){
		this.sala = null;
	}
	
	public void limparPesquisa(){
		this.sala = null;
		this.unidadeExecutora = null;
		this.dtAgenda = null;
		this.impEtiquetas = null;
		this.impHorariosLivres = null;
		this.impTickets = null;
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}
	
	public List<AelSalasExecutorasExames> pesquisarSala(String parametro) {
		return this.examesFacade.pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidade(parametro, unidadeExecutora);
	}
	
	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Date getDtAgenda() {
		return dtAgenda;
	}

	public void setDtAgenda(Date dtAgenda) {
		this.dtAgenda = dtAgenda;
	}

	public AelSalasExecutorasExames getSala() {
		return sala;
	}

	public void setSala(AelSalasExecutorasExames sala) {
		this.sala = sala;
	}

	public Boolean getImpHorariosLivres() {
		return impHorariosLivres;
	}

	public void setImpHorariosLivres(Boolean impHorariosLivres) {
		this.impHorariosLivres = impHorariosLivres;
	}

	public Boolean getImpEtiquetas() {
		return impEtiquetas;
	}

	public void setImpEtiquetas(Boolean impEtiquetas) {
		this.impEtiquetas = impEtiquetas;
	}

	public Boolean getImpTickets() {
		return impTickets;
	}

	public void setImpTickets(Boolean impTickets) {
		this.impTickets = impTickets;
	}
			
}
