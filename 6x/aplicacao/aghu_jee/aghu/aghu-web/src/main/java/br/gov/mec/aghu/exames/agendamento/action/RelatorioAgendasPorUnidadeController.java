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
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioAgendaUnidade;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteAgenda;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.RelatorioAgendaPorUnidadeVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.SolicitacaoInternacaoUnidadesFechadasController;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioAgendasPorUnidadeController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -6034758665975842999L;

	private static final String RELATORIO_AGENDAS_POR_UNIDADE = "relatorioAgendasPorUnidade";
	private static final String RELATORIO_AGENDAS_POR_UNIDADE_PDF = "relatorioAgendasPorUnidadePdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private SolicitacaoInternacaoUnidadesFechadasController solicitacaoInternacaoUnidadesFechadasController;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
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
	private DominioOrigemPacienteAgenda origem;
	private DominioOrdenacaoRelatorioAgendaUnidade ordenacao;
	private Date dtInicio;
	private Date dtFim;
	private Boolean impEtiquetas;
	private Boolean impTickets;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if(unidadeExecutora==null) {
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
	private List<RelatorioAgendaPorUnidadeVO> colecao = new ArrayList<RelatorioAgendaPorUnidadeVO>();
	
	/**
	 * Método responsável pela visualização do relatório.
	 */
	public String visualizarImpressao() throws BaseException, JRException, SystemException, IOException {
		try {
			this.colecao = agendamentoExamesFacade.obterAgendasPorUnidade(unidadeExecutora, dtInicio, dtFim, origem, ordenacao);
		} catch (Exception e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
			return null;
		}
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_AGENDA_UNIDADE_VAZIA");
			return null;
		}
		return RELATORIO_AGENDAS_POR_UNIDADE_PDF;
	}

	public String voltar(){
		return RELATORIO_AGENDAS_POR_UNIDADE;
	}
	
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			this.colecao = agendamentoExamesFacade.obterAgendasPorUnidade(unidadeExecutora, dtInicio, dtFim, origem, ordenacao);

		} catch (Exception e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,
					e.getLocalizedMessage());
			return;
		}
		
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_AGENDA_UNIDADE_VAZIA");
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "'Agenda Por Unidade'");
		
		
			//IMP_TICKETS
			impTickets = agendamentoExamesFacade.isImprimeTicketsAgendas(this.unidadeExecutora, impTickets);
			
			if(impTickets) {
				List<Integer> solicitacoes = agendamentoExamesFacade.obterSolicitacoesExameUnidade(colecao);
				if(solicitacoes.isEmpty()) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SOLICITACOES_EXAMES_TICKET_VAZIA");
				} else {
					solicitacaoInternacaoUnidadesFechadasController.imprimirSolicitacoes(unidadeExecutora, solicitacoes);
				}
			}
			//IMP_ETIQUETAS
			//Se impressoraEtiquetas == null então não imprimir etiquetas 
			ImpImpressora impressoraEtiquetas = agendamentoExamesFacade.isImprimeEtiquetasAgendas(this.unidadeExecutora, impEtiquetas);
			if(impressoraEtiquetas != null) {
				List<Integer> solicitacoes = agendamentoExamesFacade.obterSolicitacoesExameUnidade(colecao);
				if(solicitacoes.isEmpty()) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SOLICITACOES_EXAMES_ETIQUETAS_VAZIA");
				} else {
					for(Integer solicitacao : solicitacoes) {
						etiquetaEnvelopePacienteController.imprimirEnvelopePaciente(impressoraEtiquetas, solicitacao, this.unidadeExecutora);
					}
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<RelatorioAgendaPorUnidadeVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	public List<RelatorioAgendaPorUnidadeVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioAgendaPorUnidadeVO> colecao) {
		this.colecao = colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		
		if(dtFim==null) {
			params.put("tituloRelatorio", "Agenda da "+unidadeExecutora.getDescricao()+" em "+DateFormatUtil.obterDataFormatada(dtInicio, DateConstants.DATE_PATTERN_DDMMYYYY));
		} else {
			params.put("tituloRelatorio", "Agenda da "+unidadeExecutora.getDescricao()+" de "+DateFormatUtil.obterDataFormatada(dtInicio, DateConstants.DATE_PATTERN_DDMMYYYY)+" até "+
					DateFormatUtil.obterDataFormatada(dtFim, DateConstants.DATE_PATTERN_DDMMYYYY));
		}
		params.put("nomeRelatorio", "AELR_AGENDA_UNID");

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/agendamento/report/relatorioAgendasPorUnidade.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public void limparPesquisa(){
		this.unidadeExecutora = null;
		this.dtInicio= null;
		this.dtFim= null;
		this.impEtiquetas = false;
		this.impTickets = false;
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}
	
	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public DominioOrigemPacienteAgenda getOrigem() {
		return origem;
	}

	public void setOrigem(DominioOrigemPacienteAgenda origem) {
		this.origem = origem;
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

	public void setOrdenacao(DominioOrdenacaoRelatorioAgendaUnidade ordenacao) {
		this.ordenacao = ordenacao;
	}

	public DominioOrdenacaoRelatorioAgendaUnidade getOrdenacao() {
		return ordenacao;
	}
			
}
