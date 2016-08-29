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

import org.apache.commons.lang3.StringUtils;
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
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorGradeVO;
import br.gov.mec.aghu.exames.cadastrosapoio.action.SolicitacaoInternacaoUnidadesFechadasController;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import net.sf.jasperreports.engine.JRException;


public class RelatorioAgendasPorGradeController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -6604519028031907098L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@Inject
	private SolicitacaoInternacaoUnidadesFechadasController solicitacaoInternacaoUnidadesFechadasController;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private EtiquetaEnvelopePacienteController etiquetaEnvelopePacienteController;


	private AghUnidadesFuncionais unidadeFuncional;
	private AelGradeAgendaExame gradeAgendaExame;
	private Date dtInicio;
	private Date dtFim;
	private Boolean imprimeHorariosLivres;
	private Boolean imprimeEtiquetas;
	private Boolean imprimeTickets;
	
	private String caminhoArquivo;
	private Boolean realizarDownload;
	
	private static final String FILTRO_DT_INICIO = "filtroDtInicio";
	private static final String FILTRO_DT_FIM = "filtroDtFim";
	private static final String FILTRO_GAE_SEQP = "filtroGaeSeqp";

	private static final String RELATORIO_AGENDAS_POR_GRADE = "relatorioAgendasPorGrade";
	private static final String RELATORIO_AGENDAS_POR_GRADE_PDF = "relatorioAgendasPorGradePdf";
	
	/**
	 * Dados que serão impressos no PDF.
	 */
	private List<RelatorioAgendaPorGradeVO> colecao = new ArrayList<RelatorioAgendaPorGradeVO>();	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.imprimeHorariosLivres = Boolean.TRUE;
	
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}
	
	public List<AelGradeAgendaExame> pesquisarGradeAgendaExame(String parametro) {
		Short unfSeq = null;
		if (this.unidadeFuncional != null) {
			unfSeq = unidadeFuncional.getSeq();
		} else {
			unfSeq = -1;
		}
		return this.agendamentoExamesFacade.pesquisarGradeAgendaExamePorSeqpUnfSeq(parametro, unfSeq);
	}
	
	/**
	 * Método responsável pela visualização do relatório.
	 */
	public String visualizarImpressao() {
		Map<String, Object> mapFiltros = obterFiltrosPesquisa();
		Date filtroDtInicio = (Date) mapFiltros.get(FILTRO_DT_INICIO);
		Date filtroDtFim = (Date) mapFiltros.get(FILTRO_DT_FIM);
		Integer filtroGaeSeqp = (Integer) mapFiltros.get(FILTRO_GAE_SEQP);
		
		try {
			agendamentoExamesFacade.validarFiltrosRelatorioAgendas(dtInicio, dtFim, gradeAgendaExame);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		this.colecao = agendamentoExamesFacade.obterAgendasPorGrade(unidadeFuncional.getSeq(), filtroGaeSeqp, filtroDtInicio, filtroDtFim, imprimeHorariosLivres, true);
		
		if (colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRIMIR_AGENDAS_GRADE_NAO_ENCONTROU_AGENDAS");
			return null;
		}

		return RELATORIO_AGENDAS_POR_GRADE_PDF;
	}

	public String voltar(){
		//limparPesquisa();
		return RELATORIO_AGENDAS_POR_GRADE;
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {
		Map<String, Object> mapFiltros = obterFiltrosPesquisa();
		Date filtroDtInicio = (Date) mapFiltros.get(FILTRO_DT_INICIO);
		Date filtroDtFim = (Date) mapFiltros.get(FILTRO_DT_FIM);
		Integer filtroGaeSeqp = (Integer) mapFiltros.get(FILTRO_GAE_SEQP);				
		
		try {
			agendamentoExamesFacade.validarFiltrosRelatorioAgendas(dtInicio, dtFim, gradeAgendaExame);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		
		this.colecao = agendamentoExamesFacade.obterAgendasPorGrade(unidadeFuncional.getSeq(), filtroGaeSeqp, filtroDtInicio, filtroDtFim, imprimeHorariosLivres, true);
		
		if (colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRIMIR_AGENDAS_GRADE_NAO_ENCONTROU_AGENDAS");
			return;
		}		
		
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "'Agenda Por Grade'");
		
			//IMP_TICKETS
			imprimeTickets = agendamentoExamesFacade.isImprimeTicketsAgendas(this.unidadeFuncional, imprimeTickets);
			if(imprimeTickets) {
				List<Integer> solicitacoes = agendamentoExamesFacade.obterListaSoeSeqGradeAgenda(colecao);
				if(solicitacoes.isEmpty()) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SOLICITACOES_EXAMES_TICKET_VAZIA");
					
				} else {
					solicitacaoInternacaoUnidadesFechadasController.imprimirSolicitacoes(unidadeFuncional, solicitacoes);
				}
			}
			
			//IMP_ETIQUETAS
			//Se impressoraEtiquetas == null então não imprimir etiquetas 
			ImpImpressora impressoraEtiquetas = agendamentoExamesFacade.isImprimeEtiquetasAgendas(this.unidadeFuncional, imprimeEtiquetas);
			if(impressoraEtiquetas != null) {
				List<Integer> solicitacoes = agendamentoExamesFacade.obterListaSoeSeqGradeAgenda(colecao);
				if(solicitacoes.isEmpty()) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SOLICITACOES_EXAMES_ETIQUETAS_VAZIA");
				} else {
					for(Integer solicitacao : solicitacoes) {
						etiquetaEnvelopePacienteController.imprimirEnvelopePaciente(impressoraEtiquetas, solicitacao, this.unidadeFuncional);
					}
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}
	
	private Map<String, Object> obterFiltrosPesquisa() {
		Map<String, Object> mapFiltros = new HashMap<String, Object>();
		Date filtroDtInicio = (Date) dtInicio.clone();
		Date filtroDtFim = null;
		
		filtroDtInicio = DateUtil.obterDataComHoraInical(filtroDtInicio);
		
		if (dtFim != null) {
			filtroDtFim = DateUtil.obterDataComHoraFinal(dtFim);
		} else {
			// Período de 1 dia apenas
			filtroDtFim = DateUtil.obterDataComHoraFinal(filtroDtInicio);
		}	
		
		Integer filtroGaeSeqp = null;
		if (gradeAgendaExame != null) {
			filtroGaeSeqp = gradeAgendaExame.getId().getSeqp();
		}
		
		mapFiltros.put(FILTRO_DT_INICIO, filtroDtInicio);
		mapFiltros.put(FILTRO_DT_FIM, filtroDtFim);
		mapFiltros.put(FILTRO_GAE_SEQP, filtroGaeSeqp);
		return mapFiltros;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = null;
		try {
			documento = gerarDocumento();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		try {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_IMPRIMIR_AGENDAS_GRADE_PDF");
			return null;
		}
	}
	
	public void gerarArquivoCsv() {
		Map<String, Object> mapFiltros = obterFiltrosPesquisa();
		Date filtroDtInicio = (Date) mapFiltros.get(FILTRO_DT_INICIO);
		Date filtroDtFim = (Date) mapFiltros.get(FILTRO_DT_FIM);
		Integer filtroGaeSeqp = (Integer) mapFiltros.get(FILTRO_GAE_SEQP);
		
		try {
			agendamentoExamesFacade.validarPeriodoRelatorioAgendas(dtInicio, dtFim);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		
		this.colecao = agendamentoExamesFacade.obterAgendasPorGrade(unidadeFuncional.getSeq(), filtroGaeSeqp, filtroDtInicio, filtroDtFim, imprimeHorariosLivres, false);		
		
		if (colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRIMIR_AGENDAS_GRADE_NAO_ENCONTROU_AGENDAS");
			return;
		}
		
		try {
			this.caminhoArquivo = agendamentoExamesFacade.gerarArquivoAgendas(colecao);
			this.realizarDownload = Boolean.TRUE;
		} catch (IOException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_IMPRIMIR_AGENDAS_GRADE_ARQUIVO_CSV");
		}
	}

	public void dispararDownload() {
		try {
			if (StringUtils.isNotBlank(this.caminhoArquivo)) {
				super.download(caminhoArquivo);
			}
		} catch (IOException e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_IMPRIMIR_AGENDAS_GRADE_ARQUIVO_CSV");
		}
		
		this.caminhoArquivo = null;
		this.realizarDownload = Boolean.FALSE;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/agendamento/report/relatorioAgendasPorGrade.jasper";
	}
	
	@Override
	public Collection<RelatorioAgendaPorGradeVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		
		if (dtFim == null) {
			params.put("tituloRelatorio", "Agenda da " + unidadeFuncional.getDescricao() + " em " + DateFormatUtil.obterDataFormatada(dtInicio, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		} else {
			params.put("tituloRelatorio", "Agenda da " + unidadeFuncional.getDescricao() 
					+ " de " + DateFormatUtil.obterDataFormatada(dtInicio, DateConstants.DATE_PATTERN_DDMMYYYY) + " até " + 
					DateFormatUtil.obterDataFormatada(dtFim, DateConstants.DATE_PATTERN_DDMMYYYY));
		}
		
		if (!colecao.isEmpty()) {
			RelatorioAgendaPorGradeVO relAgendaPorGradeVO = colecao.get(0);
			StringBuffer descricaoGrade = new StringBuffer(relAgendaPorGradeVO.getGaeSeqp().toString());
			String descricaoGrupoExame = relAgendaPorGradeVO.getDescricaoGrupoExames();
			
			if (descricaoGrupoExame != null) {
				descricaoGrade.append(" - ").append(descricaoGrupoExame);
			}
			params.put("descricaoGrade", descricaoGrade);
		}
		
		params.put("responsavel", gradeAgendaExame.getServidor() == null ? "" : gradeAgendaExame.getServidor().getPessoaFisica().getNome());
		params.put("nomeRelatorio", "AELR_AGENDA");
		params.put("subReport1", "br/gov/mec/aghu/exames/agendamento/report/subRelatorioAgendasPorGradeExames.jasper");

		return params;
	}	
	
	public void limparPesquisa() {
		this.unidadeFuncional = null;
		this.gradeAgendaExame = null;
		this.dtInicio = null;
		this.dtFim = null;
		this.imprimeHorariosLivres = Boolean.FALSE;
		this.imprimeEtiquetas = Boolean.FALSE;
		this.imprimeTickets = Boolean.FALSE;
		iniciar();
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AelGradeAgendaExame getGradeAgendaExame() {
		return gradeAgendaExame;
	}

	public void setGradeAgendaExame(AelGradeAgendaExame gradeAgendaExame) {
		this.gradeAgendaExame = gradeAgendaExame;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Boolean getImprimeHorariosLivres() {
		return imprimeHorariosLivres;
	}

	public void setImprimeHorariosLivres(Boolean imprimeHorariosLivres) {
		this.imprimeHorariosLivres = imprimeHorariosLivres;
	}

	public Boolean getImprimeEtiquetas() {
		return imprimeEtiquetas;
	}

	public void setImprimeEtiquetas(Boolean imprimeEtiquetas) {
		this.imprimeEtiquetas = imprimeEtiquetas;
	}

	public Boolean getImprimeTickets() {
		return imprimeTickets;
	}

	public void setImprimeTickets(Boolean imprimeTickets) {
		this.imprimeTickets = imprimeTickets;
	}

	public Boolean getRealizarDownload() {
		return realizarDownload;
	}

	public void setRealizarDownload(Boolean realizarDownload) {
		this.realizarDownload = realizarDownload;
	}

	public String getCaminhoArquivo() {
		return caminhoArquivo;
	}

	public void setCaminhoArquivo(String caminhoArquivo) {
		this.caminhoArquivo = caminhoArquivo;
	}
	
}
