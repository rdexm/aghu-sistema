package br.gov.mec.aghu.exames.agendamento.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.HorarioExameVO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para Manter Disponibilidade de Horários
 * 
 * @author diego.pacheco
 *
 */


public class ManutencaoDisponibilidadeHorarioPaginatorController extends ActionController implements ActionPaginator {

	private static final String END_B = ": </b>";

	private static final String B = "<b>";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<HorarioExameVO> dataModel;

	private static final Log LOG = LogFactory.getLog(ManutencaoDisponibilidadeHorarioPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3098703023030664423L;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;

	
	private AelGradeAgendaExame gradeAgendaExame;
	
	// Filtros utilizados na pesquisa
	private DominioSituacaoHorario situacaoHorario;
	private Date dtInicio;
	private Date dtFim;
	private Boolean filtraHorariosFuturos = Boolean.TRUE;
	private DominioDiaSemana diaSemana;
	private Date hora;
	private AelTipoMarcacaoExame tipoMarcacao;
	private DominioSimNao extra;
	private DominioSimNao exclusivo;
	
	private List<HorarioExameVO> listaHorarioExameVO;
	private List<AelHorarioExameDisp> listaHorarioExameSelecionados;
	
	// Atributos para controle de seleção
	// e habilitar/desabilitar botões
	private Boolean todosHorariosSelecionados = Boolean.FALSE;
	private Boolean todosHorariosSelecionadosOld = Boolean.FALSE;
	private Boolean desabilitaBotoesSelecao = Boolean.TRUE;
	private Boolean clicouCheckboxTodos = Boolean.FALSE;
	
	// Atributos para as modais "Alterar disponibilidade" e
	// "Adicionar novo horario"
	private DominioSituacaoHorario novaSituacaoHorario;
	private AelTipoMarcacaoExame novoTipoMarcacao;
	private DominioSimNao novoExtra;
	private DominioSimNao novoExclusivo;
	private Date novaDataHora;

	private boolean novoHorarioAdicionado;
	
	public enum ManutencaoDisponibilidadeHorarioPaginatorControllerExceptionCode implements BusinessExceptionCode {
		ERRO_DISPONIBILIDADE_HORARIO_EXAME_SITUACAO_NAO_INFORMADA;
	}

	@Override
	public Long recuperarCount() {
		Boolean filtroExtra = null;
		Boolean filtroExclusivo = null;
		
		if (this.extra != null) {
			filtroExtra = this.extra.isSim();
		}
		
		if (this.exclusivo != null) {
			filtroExclusivo = this.exclusivo.isSim();					
		}
		
		return agendamentoExamesFacade.pesquisarHorarioExameDisponibilidadeCount(situacaoHorario, dtInicio, dtFim, 
				filtraHorariosFuturos, diaSemana, hora, tipoMarcacao, filtroExtra, filtroExclusivo, 
				gradeAgendaExame.getId().getUnfSeq(), gradeAgendaExame.getId().getSeqp());
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public List<HorarioExameVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		Boolean filtroExtra = null;
		Boolean filtroExclusivo = null;
		
		if (this.extra != null) {
			filtroExtra = this.extra.isSim();
		}
		
		if (this.exclusivo != null) {
			filtroExclusivo = this.exclusivo.isSim();					
		}
		
		List<AelHorarioExameDisp> lista = agendamentoExamesFacade.pesquisarHorarioExameDisponibilidade(
				firstResult, maxResult, orderProperty, asc, situacaoHorario, dtInicio, dtFim, filtraHorariosFuturos, 
				diaSemana, hora, tipoMarcacao, filtroExtra, filtroExclusivo, gradeAgendaExame.getId().getUnfSeq(),
				gradeAgendaExame.getId().getSeqp());
		
		if (lista == null || lista.isEmpty()) {
			return new ArrayList<HorarioExameVO>();
		}
		
		if (listaHorarioExameSelecionados == null) {
			listaHorarioExameSelecionados = new ArrayList<AelHorarioExameDisp>();
		}
		
		if (listaHorarioExameVO != null && !listaHorarioExameVO.isEmpty()) {
			if (todosHorariosSelecionados && clicouCheckboxTodos) {
				/*
				 *  Horários marcados e executados não podem
				 *  ser selecionados ao clicar em checkbox Selecionar todos
				 */
				listaHorarioExameSelecionados = 
						agendamentoExamesFacade.pesquisarHorarioExameDisponibilidadeExcetoMarcadoExecutado(situacaoHorario, dtInicio, dtFim, filtraHorariosFuturos, 
								diaSemana, hora, tipoMarcacao, filtroExtra, filtroExclusivo, 
								gradeAgendaExame.getId().getUnfSeq(),
								gradeAgendaExame.getId().getSeqp());
				
				clicouCheckboxTodos = false;
			}
			else {
				// Carrega/atualiza a lista de horários selecionados
				listaHorarioExameSelecionados = obterListaHorarioExameSelecionados();				
			}
		}

		listaHorarioExameVO = new ArrayList<HorarioExameVO>();
		
		for (AelHorarioExameDisp horarioExame : lista) {
			HorarioExameVO horarioExameVO = new HorarioExameVO(horarioExame);
			listaHorarioExameVO.add(horarioExameVO);
			LOG.debug("SEQP=" + horarioExame.getId().getGaeSeqp() 
				+ "  SITUACAO=" + horarioExame.getSituacaoHorario().getDescricao());
		}
		
		// Atualiza na lista de resultados (VOs) os horários que foram selecionados 
		if (listaHorarioExameSelecionados != null && !listaHorarioExameSelecionados.isEmpty()) {
			listaHorarioExameVO = obterListaAtualizadaRegistroSelecionados();
		}
		
		desabilitaBotoesSelecao = listaHorarioExameSelecionados == null || listaHorarioExameSelecionados.isEmpty();

		return listaHorarioExameVO;
	}
	
	/**
	 * Retorna uma nova lista de horarios selecionados considerando
	 * a lista de horários que já estão selecionados.
	 * 
	 * @return List<AelHorarioExameDisp>
	 */
	private List<AelHorarioExameDisp> obterListaHorarioExameSelecionados() {
		List<AelHorarioExameDisp> lista = listaHorarioExameSelecionados;

		// Adiciona os horários selecionados
		for (HorarioExameVO horarioExameVO : listaHorarioExameVO) {
			AelHorarioExameDisp horarioExame = horarioExameVO.getHorarioExame();
			if (horarioExameVO.getSelecionado()
					&& !lista.contains(horarioExame)) {
				lista.add(horarioExame);
			}
		}
		
		if (!lista.isEmpty()) {
			// Remove os horários que não estão mais selecionados
			for (Iterator<AelHorarioExameDisp> it = lista.iterator(); it.hasNext();) {
				AelHorarioExameDisp horarioExameSelecionado = it.next();
				for (HorarioExameVO horarioExameVO : listaHorarioExameVO) {
					if (horarioExameVO.getHorarioExame().getId().equals(horarioExameSelecionado.getId()) 
							&& !horarioExameVO.getSelecionado()) {
						it.remove();
					}
				}				
			}
		}

		return lista;		
	}
	
	/**
	 * Retorna a lista dos registros exibidos na pesquisa
	 * para manter a seleção quando é realizada a paginação.
	 * Os registros (VOs) retornados na pesquisa são atualizados como selecionados
	 * de acordo com os registros que estão em <b>listaHorarioExameSelecionados</b>.
	 * 
	 */
	private List<HorarioExameVO> obterListaAtualizadaRegistroSelecionados() {
		List<HorarioExameVO> lista = new ArrayList<HorarioExameVO>();
		
		for (AelHorarioExameDisp horarioSelecionado : listaHorarioExameSelecionados) {
			
			for (int i = 0; i < listaHorarioExameVO.size(); i++) {
				HorarioExameVO consultaVO = (HorarioExameVO) listaHorarioExameVO.get(i);
				AelHorarioExameDisp horarioExame = consultaVO.getHorarioExame();
				
				if (horarioExame.equals(horarioSelecionado)) {
					consultaVO.setSelecionado(Boolean.TRUE);
				}
				
				if (!verificarListaHorarioVOContemHorario(horarioExame, lista)) {
					lista.add(consultaVO);
				}
			}
		}
		
		return lista;
	}
	
	private Boolean verificarListaHorarioVOContemHorario(AelHorarioExameDisp horarioExame,
			List<HorarioExameVO> listaHorarioExameVO) {
		for (HorarioExameVO horarioExameVO : listaHorarioExameVO) {
			if (horarioExame.equals(horarioExameVO.getHorarioExame())) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public void inicializarModalAlteracao() {
		refazerPesquisa();
		inicializarAtributosModal();
	}
	
	/**
	 * Refaz pesquisa sem reiniciar o paginator (faz a pesquisa
	 * mas continua na mesma pagina).
	 */
	public void refazerPesquisa() {
		this.dataModel.reiniciarPaginator();
		
	}
	
	/**
	 * Seta a grade passada pela controller da tela 
	 * de pesquisa de grades.
	 * 
	 * @param grade
	 * @return
	 */
	public String editarHorarioExameGrade(AelGradeAgendaExame grade) {
		listaHorarioExameVO = new ArrayList<HorarioExameVO>();
		limparPesquisa();
		gradeAgendaExame = grade;
		filtrar();
		return "manterDisponibilidadeHorarios";
	}
	
	public String voltar() {
		return "pesquisarGradeExame";
	}	
	
	/**
	 * Método executado ao clicar no botão filtrar
	 */
	public void filtrar() {
		limparSelecao();
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Limpa os atributos de pesquisa.
	 */
	public void limparPesquisa() {
		situacaoHorario = null;
		dtInicio = null;
		dtFim = null;
		filtraHorariosFuturos = Boolean.TRUE;
		diaSemana = null;
		hora = null;
		tipoMarcacao = null;
		extra = null;
		exclusivo = null;
		this.dataModel.setPesquisaAtiva(Boolean.FALSE);
		limparSelecao();
		this.dataModel.reiniciarPaginator();
	}
	
	/**
	 * Limpa os horarios selecionados, 
	 * atributos das modais e reinicia a pesquisa.
	 */
	private void limparSelecao() {
		if (listaHorarioExameVO != null && !listaHorarioExameVO.isEmpty()) {
			for (HorarioExameVO horarioExameVO : listaHorarioExameVO) {
				horarioExameVO.setSelecionado(Boolean.FALSE);
			}
		}
		listaHorarioExameSelecionados = null;
		todosHorariosSelecionados = Boolean.FALSE;
		todosHorariosSelecionadosOld = Boolean.FALSE;
		clicouCheckboxTodos = Boolean.FALSE;
	}
	
	/**
	 * Método responsável pela pesquisa da suggestion Tipo de marcação
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacao(String parametro) {
		return agendamentoExamesFacade.pesquisarTipoMarcacaoExameAtivoPorSeqOuDescricao(parametro);
	}
	
	/**
	 * Altera a disponibilidade dos horários selecionados
	 */
	public void alterarDisponibilidade() {
		
		try {
			if (novaSituacaoHorario == null) {
				throw new ApplicationBusinessException(
						ManutencaoDisponibilidadeHorarioPaginatorControllerExceptionCode.ERRO_DISPONIBILIDADE_HORARIO_EXAME_SITUACAO_NAO_INFORMADA);
			}
		} catch (BaseException exception) {
			apresentarExcecaoNegocio(exception);
			LOG.error(exception.getMessage(),exception);
			return;
		}
		
		Boolean ehExtra = this.novoExtra.isSim();
		Boolean ehExclusivo = this.novoExclusivo.isSim();

		try {
			/*
			 * Altera a situação, tipo de marcação, atributo indicando horario extra e atributo 
			 * indicando horário exclusivo da unid. executora dos horários selecionados 
			 */
			agendamentoExamesFacade.atualizarListaHorarioExameDisp(listaHorarioExameSelecionados, novaSituacaoHorario, 
					novoTipoMarcacao, ehExtra, ehExclusivo);
			
			this.apresentarMsgNegocio(Severity.INFO, 
					"MENSAGEM_SUCESSO_ALTERACAO_DISPONIBILIDADE_HORARIOS_EXAME", 
					listaHorarioExameSelecionados.size());
			
		} catch (BaseException exception) {
			for (AelHorarioExameDisp horarioExame : listaHorarioExameSelecionados) {
				agendamentoExamesFacade.refreshHorarioExameDisp(horarioExame);
			}
			novaSituacaoHorario = null;
			apresentarExcecaoNegocio(exception);
			LOG.error(exception.getMessage(),exception);
			this.dataModel.reiniciarPaginator();
			return;
		}
		
		limparSelecao();
		refazerPesquisa();
	}
	
	/**
	 * Verifica se desabilitará o checkbox para alterar disponibilidade
	 * ou excluir horários.
	 * 
	 * @param horarioExameDisp
	 * @return boolean
	 */
	public Boolean verificarSituacaoHorarioIndisponivel(AelHorarioExameDisp horarioExameDisp) {
		return agendamentoExamesFacade.verificarSituacaoHorarioIndisponivel(horarioExameDisp);
	}
	
	public String getDescricaoGrade(){
		StringBuilder sb = new StringBuilder(70);	
		
		sb.append(B).append(WebUtil.initLocalizedMessage("LABEL_MANTER_DISPONIBILIDADE_HORARIO_EXAME_GRADE_DE_EXAME",null)).append(END_B)
		.append(gradeAgendaExame.getId().getSeqp());
		if (gradeAgendaExame.getUnidadeFuncional() != null){
			sb.append(" <b>").append(WebUtil.initLocalizedMessage("LABEL_MANTER_DISPONIBILIDADE_HORARIO_EXAME_UNIDADE_EXECUTORA",null)).append(END_B)
			.append(gradeAgendaExame.getUnidadeFuncional().getDescricao());
		}
		if (gradeAgendaExame.getSalaExecutoraExames() != null){
			sb.append(" <b>").append(WebUtil.initLocalizedMessage("LABEL_MANTER_DISPONIBILIDADE_HORARIO_EXAME_SALA",null)).append(END_B)
			.append(gradeAgendaExame.getSalaExecutoraExames().getNumero());
		}
		if (gradeAgendaExame.getGrupoExame() != null){
			sb.append(" <b>").append(WebUtil.initLocalizedMessage("LABEL_MANTER_DISPONIBILIDADE_HORARIO_EXAME_GRUPO",null)).append(END_B)
			.append(gradeAgendaExame.getGrupoExame().getDescricao());
		}
		if (gradeAgendaExame.getServidor() != null){
			sb.append(" <b> ").append(WebUtil.initLocalizedMessage("LABEL_MANTER_DISPONIBILIDADE_HORARIO_EXAME_RESPONSAVEL",null)).append(END_B)
			.append(gradeAgendaExame.getServidor().getPessoaFisica().getNome());
		}

		return sb.toString();
	}
	
	/**
	 * Método que retorna o dia da semana por extenso.
	 */
	public String obterDiaSemana(Date data) {
		DominioDiaSemana diaSemana = CoreUtil.retornaDiaSemana(data);
		return diaSemana.getDescricao();
	}	
	
	public String obterDataHoraFormatada(Date dtHrExame) {
		if (dtHrExame != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			return sdf.format(dtHrExame);
		} else {
			return "";
		}
	}

	public void verificarTodosHorariosSelecionados() {
		// Se desmarcou a seleção de todas os horários
		if (!this.todosHorariosSelecionados && this.todosHorariosSelecionadosOld) {
			listaHorarioExameVO = null;
			listaHorarioExameSelecionados = null;
		}
		
		// Se clicou no checkbox (mudou o valor boolean)
		if (this.todosHorariosSelecionados != this.todosHorariosSelecionadosOld) {
			clicouCheckboxTodos = Boolean.TRUE;
		}
		
		// Pega o antigo valor que indica se todos horarios estavam selecionados ou não
		this.todosHorariosSelecionadosOld = this.todosHorariosSelecionados.booleanValue();
		
		refazerPesquisa();
	}
	
	public void adicionarNovoHorario() {
		Boolean ehExtra = this.novoExtra.isSim();
		Boolean ehExclusivo = this.novoExclusivo.isSim();
		
		try {	
			
			agendamentoExamesFacade.inserirHorarioExameDisp(novaSituacaoHorario, novoTipoMarcacao, ehExtra, 
					ehExclusivo, gradeAgendaExame.getId().getUnfSeq(), gradeAgendaExame.getId().getSeqp(), novaDataHora);
		
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_DISPONIBILIDADE_HORARIOS_EXAME");
			novoHorarioAdicionado = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			return;
		}
		
		limparSelecao();
		this.dataModel.reiniciarPaginator();
	}
	
	public void excluir() {
		listaHorarioExameSelecionados = obterListaHorarioExameSelecionados();
		try {
			if (listaHorarioExameSelecionados != null && !listaHorarioExameSelecionados.isEmpty()) {
				Boolean removeu = agendamentoExamesFacade.removerListaHorarioExameDisp(listaHorarioExameSelecionados);
		
				if (removeu) {
					this.apresentarMsgNegocio(Severity.INFO, 
							"MENSAGEM_SUCESSO_EXCLUSAO_DISPONIBILIDADE_HORARIO_EXAME",
							listaHorarioExameSelecionados.size());
					listaHorarioExameSelecionados = null;
					listaHorarioExameVO = null;
					refazerPesquisa();							
				}
			}
			else {
				this.apresentarMsgNegocio(Severity.INFO, 
						"MENSAGEM_ERRO_EXCLUIR_DISPONIBILIDADE_HORARIO_EXAME");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	public void inicializarAtributosModal() {
		novoHorarioAdicionado = false;
		novaDataHora = null;
		novaSituacaoHorario = null;
		novoTipoMarcacao = null;
		novoExtra = DominioSimNao.N;
		novoExclusivo = DominioSimNao.N;
		listaHorarioExameSelecionados = obterListaHorarioExameSelecionados();
	}

	public IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return agendamentoExamesFacade;
	}

	public void setAgendamentoExamesFacade(
			IAgendamentoExamesFacade agendamentoExamesFacade) {
		this.agendamentoExamesFacade = agendamentoExamesFacade;
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

	public Boolean getFiltraHorariosFuturos() {
		return filtraHorariosFuturos;
	}

	public void setFiltraHorariosFuturos(Boolean filtraHorariosFuturos) {
		this.filtraHorariosFuturos = filtraHorariosFuturos;
	}

	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public Date getHora() {
		return hora;
	}

	public void setHora(Date hora) {
		this.hora = hora;
	}

	public AelTipoMarcacaoExame getTipoMarcacao() {
		return tipoMarcacao;
	}

	public void setTipoMarcacao(AelTipoMarcacaoExame tipoMarcacao) {
		this.tipoMarcacao = tipoMarcacao;
	}

	public DominioSimNao getExtra() {
		return extra;
	}

	public void setExtra(DominioSimNao extra) {
		this.extra = extra;
	}

	public DominioSimNao getExclusivo() {
		return exclusivo;
	}

	public void setExclusivo(DominioSimNao exclusivo) {
		this.exclusivo = exclusivo;
	}

	public Boolean getTodosHorariosSelecionados() {
		return todosHorariosSelecionados;
	}

	public void setTodosHorariosSelecionados(Boolean todosHorariosSelecionados) {
		this.todosHorariosSelecionados = todosHorariosSelecionados;
	}

	public Boolean getDesabilitaBotoesSelecao() {
		return desabilitaBotoesSelecao;
	}

	public void setDesabilitaBotoesSelecao(Boolean desabilitaBotoesSelecao) {
		this.desabilitaBotoesSelecao = desabilitaBotoesSelecao;
	}

	public Boolean getClicouCheckboxTodos() {
		return clicouCheckboxTodos;
	}

	public void setClicouCheckboxTodos(Boolean clicouCheckboxTodos) {
		this.clicouCheckboxTodos = clicouCheckboxTodos;
	}

	public List<HorarioExameVO> getListaHorarioExameVO() {
		return listaHorarioExameVO;
	}

	public void setListaHorarioExameVO(List<HorarioExameVO> listaHorarioExameVO) {
		this.listaHorarioExameVO = listaHorarioExameVO;
	}

	public List<AelHorarioExameDisp> getListaHorarioExameSelecionados() {
		return listaHorarioExameSelecionados;
	}

	public void setListaHorarioExameSelecionados(
			List<AelHorarioExameDisp> listaHorarioExameSelecionados) {
		this.listaHorarioExameSelecionados = listaHorarioExameSelecionados;
	}

	public DominioSituacaoHorario getSituacaoHorario() {
		return situacaoHorario;
	}

	public void setSituacaoHorario(DominioSituacaoHorario situacaoHorario) {
		this.situacaoHorario = situacaoHorario;
	}

	public AelGradeAgendaExame getGradeAgendaExame() {
		return gradeAgendaExame;
	}

	public void setGradeAgendaExame(AelGradeAgendaExame gradeAgendaExame) {
		this.gradeAgendaExame = gradeAgendaExame;
	}

	public Boolean getTodosHorariosSelecionadosOld() {
		return todosHorariosSelecionadosOld;
	}

	public void setTodosHorariosSelecionadosOld(Boolean todosHorariosSelecionadosOld) {
		this.todosHorariosSelecionadosOld = todosHorariosSelecionadosOld;
	}

	public AelTipoMarcacaoExame getNovoTipoMarcacao() {
		return novoTipoMarcacao;
	}

	public void setNovoTipoMarcacao(AelTipoMarcacaoExame novoTipoMarcacao) {
		this.novoTipoMarcacao = novoTipoMarcacao;
	}

	public DominioSituacaoHorario getNovaSituacaoHorario() {
		return novaSituacaoHorario;
	}

	public void setNovaSituacaoHorario(DominioSituacaoHorario novaSituacaoHorario) {
		this.novaSituacaoHorario = novaSituacaoHorario;
	}

	public Date getNovaDataHora() {
		return novaDataHora;
	}

	public void setNovaDataHora(Date novaDataHora) {
		this.novaDataHora = novaDataHora;
	}

	public DominioSimNao getNovoExtra() {
		return novoExtra;
	}

	public void setNovoExtra(DominioSimNao novoExtra) {
		this.novoExtra = novoExtra;
	}

	public DominioSimNao getNovoExclusivo() {
		return novoExclusivo;
	}

	public void setNovoExclusivo(DominioSimNao novoExclusivo) {
		this.novoExclusivo = novoExclusivo;
	}
	 
	public boolean isNovoHorarioAdicionado() {
		return novoHorarioAdicionado;
	}

	public void setNovoHorarioAdicionado(boolean novoHorarioAdicionado) {
		this.novoHorarioAdicionado = novoHorarioAdicionado;
	}

	public DynamicDataModel<HorarioExameVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<HorarioExameVO> dataModel) {
	 this.dataModel = dataModel;
	}
}
