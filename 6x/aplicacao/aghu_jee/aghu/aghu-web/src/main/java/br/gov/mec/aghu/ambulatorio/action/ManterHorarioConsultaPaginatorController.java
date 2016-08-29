package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.ConverterConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.HorarioConsultaVO;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller da tela de Manter Horários de Consulta
 */
public class ManterHorarioConsultaPaginatorController extends ActionController implements ActionPaginator {

	private static final Log LOG = LogFactory.getLog(ManterHorarioConsultaPaginatorController.class);

	private static final long serialVersionUID = -3744812806893136737L;

	private static final String CONSULTAR_GRADE_AGENDAMENTO = "ambulatorio-consultarGradeAgendamento";
	private static final String MANTER_HORARIOS_CONSULTA = "ambulatorio-manterHorariosConsulta";
	private static final String ATENDIMENTO_AMBULATORIAL_ABA_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";


	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private AacGradeAgendamenConsultas gradeAgendamenConsulta;

	@Inject @Paginator
	private DynamicDataModel<HorarioConsultaVO> dataModel;
	

	@Inject
	PesquisarPacientesAgendadosController pesquisarPacientesAgendadosController;

	// FILTRO
	private Integer seq;
	private DominioDiaSemana diaSemana;
	private Date horaConsulta;
	private List<AacPagador> pagadorList;
	private AacPagador pagador;
	private List<AacTipoAgendamento> autorizacaoList;
	private AacTipoAgendamento autorizacao;
	private List<AacCondicaoAtendimento> condicaoList;
	private AacCondicaoAtendimento condicao;
	private AacSituacaoConsultas situacao;
	private Date dtInicio;
	private Date dtFim;
	private Integer nroConsulta;
	private List<HorarioConsultaVO> listaHorarioConsulta;
	private List<Integer> consultasSelecionadas = new ArrayList<Integer>();
	private String parametroZona;
	private String pacienteNome;
	private Integer prontuario;
	
	// SELEÇÃO
	private Integer seqSelected;
	

	// Atributo para a modal Alterar disponibilidade
	private AacSituacaoConsultas novaSituacao;
	
	private ConverterConsultasVO consultaPagadores;
	private ConsultaAmbulatorioVO consultaSelecionada;


	private AacConsultas horarioConsulta;
	private Integer numeroAac;

	private Boolean desabilitaBotao;
	private boolean allChecked;
	private Boolean ambulatorioAgendados;


	public enum ManterHorarioConsultaPaginatorControllerExceptionCode implements BusinessExceptionCode {
		ERRO_SELECIONAR_CAMPO_SITUACAO;
	}
	
	public void guardaConsulta(Integer numero){
		this.consultaPagadores = null;
		this.numeroAac = numero;
	}
	
	@PostConstruct
	protected void inicializar() {	
		if(consultaSelecionada != null && consultaSelecionada.getNumero() != null){
			this.nroConsulta  =  consultaSelecionada.getNumero();
			this.filtrar();
		}
			
		this.begin(conversation);
	
		try {
			String zona = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_ZONA);
			String sala = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_SALA);
			parametroZona = zona + "/" + sala;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Método executado ao clicar no botão filtraro
	 */
	public void filtrar() {
		consultasSelecionadas = new ArrayList<>();
		limparListaHorarioConsulta();
		allChecked = false;
		this.seqSelected = null;
	
		dataModel.reiniciarPaginator();
	}

	private void limparListaHorarioConsulta() {
		listaHorarioConsulta = new LinkedList<>();
	}

	public void limparPesquisa() {
		seq = null;
		diaSemana = null;
		horaConsulta = null;
		pagador = null;
		autorizacao = null;
		condicao = null;
		situacao = null;
		seqSelected = null;
		
		dtInicio = null;
		dtFim = null;
		
		nroConsulta = null;
		limparListaHorarioConsulta();
		consultasSelecionadas = new ArrayList<>();
		desabilitaBotao = true;
		novaSituacao = null;
		consultaPagadores = null;
		//consultaPagadores = null; 
		allChecked = false;
		dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return ambulatorioFacade.listarHorariosConsultasCount(situacao, nroConsulta, diaSemana, horaConsulta, pagador, autorizacao, condicao, dtInicio, dtFim, gradeAgendamenConsulta.getSeq());
	}

	private List<Integer> obterAllNrosConsulta() {
		return ambulatorioFacade.listarHorariosConsultasSeqs(situacao, nroConsulta, diaSemana, horaConsulta, pagador, autorizacao, condicao, dtInicio, dtFim, gradeAgendamenConsulta.getSeq());
	}

	@Override
	public List<HorarioConsultaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<AacConsultas> lista = ambulatorioFacade.listarHorariosConsultas(firstResult, maxResult, orderProperty, asc, situacao, nroConsulta, diaSemana, horaConsulta, pagador, autorizacao,
				condicao, dtInicio, dtFim, gradeAgendamenConsulta.getSeq());

		if (lista == null || lista.isEmpty()) {
			desabilitaBotao = true;
			return new ArrayList<HorarioConsultaVO>();
		}
		this.listaHorarioConsulta = new ArrayList<HorarioConsultaVO>();
		for (AacConsultas consulta : lista) {
			HorarioConsultaVO hcVO = new HorarioConsultaVO(consulta);
			
			hcVO.setHabilitaCheck(verificarSituacaoHorariosConsulta(consulta));

			if (consultasSelecionadas.contains(consulta.getNumero())) {
				hcVO.setSelecionado(true);
			} else {
				hcVO.setSelecionado(false);
			}
			
			LOG.info("SITUACAO:" + consulta.getSituacaoConsulta().getSituacao());
			listaHorarioConsulta.add(hcVO);
		}

		if (!listaHorarioConsulta.isEmpty()) {
			desabilitaBotao = false;
		}

		return listaHorarioConsulta;
	}

	public void obterAutorizacoesAtivas() {
		autorizacaoList = ambulatorioFacade.obterListaAutorizacoesAtivas();
	}

	/**
	 * Método para Suggestion Situação
	 */
	public List<AacSituacaoConsultas> obterSituacao(String objPesquisa) throws BaseException {
		return ambulatorioFacade.pesquisarSituacao((String) objPesquisa);
	}

	public List<AacSituacaoConsultas> obterSituacaoSemMarcada(String objPesquisa) throws BaseException {
		return ambulatorioFacade.pesquisarSituacaoSemMarcada((String) objPesquisa);
	}

	public void obterPagadoresComAgendamento() {
		pagadorList = ambulatorioFacade.obterListaPagadoresComAgendamento();
	}

	public void obterCondicaoAtentimentoAtivas() {
		condicaoList = ambulatorioFacade.listarCondicaoAtendimento();
	}

	/**
	 * Verifica se desabilitará o checkbox para alterar disponibilidade ou excluir consulta.
	 */
	public boolean verificarSituacaoHorariosConsulta(AacConsultas aacConsulta) {
		if (aacConsulta.getSituacaoConsulta().getSituacao().equals("M")) {
			return true;
		} else {
			return false;
		}
	}

	public String editarHorarioConsultaGrade(AacGradeAgendamenConsultas grade) {
		
		ambulatorioAgendados = false;

				
		//limparListaHorarioConsulta();
		limparPesquisa();
		obterPagadoresComAgendamento();
		obterAutorizacoesAtivas();
		obterCondicaoAtentimentoAtivas();
		gradeAgendamenConsulta = grade;
		
			if(consultaSelecionada != null){
				
				if(consultaSelecionada.getNumero() != null){
				this.nroConsulta = consultaSelecionada.getNumero();
					ambulatorioAgendados = true;
				}
				
				if(consultaSelecionada.getPacienteNome() != null && consultaSelecionada.getProntuario() != null){
					pacienteNome = consultaSelecionada.getPacienteNome();
					prontuario = consultaSelecionada.getProntuario();
				}
			}	
		
		if(consultaSelecionada != null){
			
			if(consultaSelecionada.getNumero() != null){
				this.nroConsulta = consultaSelecionada.getNumero();
				ambulatorioAgendados = true;
			}
						
			if(consultaSelecionada.getPacienteNome() != null && consultaSelecionada.getProntuario() != null){
				pacienteNome = consultaSelecionada.getPacienteNome();
				prontuario = consultaSelecionada.getProntuario();
			}
		}
		filtrar();
		return MANTER_HORARIOS_CONSULTA;
	}

	public String voltar() {
		
		if(consultaSelecionada != null){
			if(consultaSelecionada.getGradeSeq() != null){
				pesquisarPacientesAgendadosController.setSelectedTab(0);
				return ATENDIMENTO_AMBULATORIAL_ABA_AGENDADOS;
		}
	}	
		return CONSULTAR_GRADE_AGENDAMENTO;
	}

	/**
	 * Altera a disponibilidade das consultas selecionadas
	 */
	public void alterarDisponibilidade() {
		try {

			if (novaSituacao == null) {
				apresentarMsgNegocio(Severity.ERROR, ManterHorarioConsultaPaginatorControllerExceptionCode.ERRO_SELECIONAR_CAMPO_SITUACAO.toString());
				return;
			}
 
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e1) {
				LOG.error("Exceção capturada:", e1);
			}

			ambulatorioFacade.alterarDisponibilidadeConsultas(consultasSelecionadas, novaSituacao, nomeMicrocomputador);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_DISPONIBILIDADE",  consultasSelecionadas.size());
			dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public String getTipoDeGrade() {
		StringBuilder sb = new StringBuilder(25);

		if (gradeAgendamenConsulta != null) {
			if (gradeAgendamenConsulta.getProcedimento()) {
				sb.append("Grade de Procedimento");
			} else {
				sb.append("Grade de Consulta");
			}
		}

		return sb.toString();
	}

	public void excluir() {
		try {
			if (consultasSelecionadas != null && !consultasSelecionadas.isEmpty()) {

				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e1) {
					LOG.error("Exceção capturada:", e1);
				}

				boolean consultasExcluidas = ambulatorioFacade.excluirListaConsultas(consultasSelecionadas, gradeAgendamenConsulta.getSeq(), nomeMicrocomputador, false);
				if (consultasExcluidas) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_HORARIO_CONSULTA", consultasSelecionadas.size());
				}

				dataModel.reiniciarPaginator();
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_EXCLUIR_HORARIO_CONSULTA");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public void checkItem(Integer numero) {
		if (consultasSelecionadas.contains(numero)) {
			consultasSelecionadas.remove(numero);
		} else {
			consultasSelecionadas.add(numero);
		}
	}

	public void checkAll() {
		// Selecionou opção
		if (allChecked) {
			// consultasSelecionadas.clear();
			consultasSelecionadas = obterAllNrosConsulta();

			for (HorarioConsultaVO vo : listaHorarioConsulta) {
				if (this.consultasSelecionadas.contains(vo.getConsulta().getNumero())) {
					vo.setSelecionado(true);
				} else {
					vo.setSelecionado(false);
				}
			}
			// Desmarcou opção
		} else {
			limparSelecao();
		}
	}

	/**
	 * Limpa as consultas selecionadas e reinicia a pesquisa
	 */
	public void limparSelecao() {
		for (HorarioConsultaVO horarioConsultaVO : listaHorarioConsulta) {
			horarioConsultaVO.setSelecionado(false);
		}
		consultasSelecionadas.clear();
		novaSituacao = null;
		consultaPagadores = null;
		//consultaPagadores = null;
		allChecked = false;
	}

	public String getDescricaoGrade() {
		StringBuilder sb = new StringBuilder();
		if (gradeAgendamenConsulta != null) {
			if (gradeAgendamenConsulta.getProcedimento()) {
				sb.append("<b>Grade de Procedimento: </b>");
			} else {
				sb.append("<b>Grade de Consulta: </b>");
			}
			sb.append(gradeAgendamenConsulta.getSeq() ).append(' ')
			.append("<b>" ).append( parametroZona ).append( ": </b>")
			.append(gradeAgendamenConsulta.getUnidadeFuncional().getSigla() ).append('/').append( gradeAgendamenConsulta.getAacUnidFuncionalSala().getId().getSala() ).append(' ')
			.append("<b>Esp/Agenda: </b>")
			.append(gradeAgendamenConsulta.getEspecialidade().getNomeReduzido().replaceAll("\\<.*?>", "") ).append(' ')
			.append("<b>Equipe: </b>");
			if (gradeAgendamenConsulta.getEquipe() != null) {
				sb.append(gradeAgendamenConsulta.getEquipe().getNome().replaceAll("\\<.*?>", "") ).append(' ');
			}
			if (gradeAgendamenConsulta.getProfEspecialidade() != null) {
				sb.append("<b>Profissional: </b>");
				sb.append(gradeAgendamenConsulta.getProfEspecialidade().getRapServidor().getPessoaFisica().getNome().replaceAll("\\<.*?>", ""));
			}
		}
		return sb.toString();
	}

	public void salvarPagadores(){
		
		this.ambulatorioFacade.salvarPagadores(consultaPagadores, numeroAac);
		limparSelecao();
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CONVERSAO_ATUALIZADA");
		dataModel.reiniciarPaginator();
	}
	
	public List<ConverterConsultasVO> obterConsultaPagadoresCadastrados(String sbPagadores){ 
		return returnSGWithCount(this.ambulatorioFacade.obterConsultaPagadoresCadastrados(sbPagadores),
								this.ambulatorioFacade.obterConsultaPagadoresCadastradosCount(sbPagadores));
	}
	
	/*public void guardaConsulta(Integer numero){
		this.consultaPagadores = null;
		this.numeroAac = numero;
	}
	*/
		
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeqSelected() {
		return seqSelected;
	}

	public void setSeqSelected(Integer seqSelected) {
		this.seqSelected = seqSelected;
	}

	public void setGradeAgendamenConsulta(AacGradeAgendamenConsultas gradeAgendamenConsulta) {
		this.gradeAgendamenConsulta = gradeAgendamenConsulta;
	}

	public AacGradeAgendamenConsultas getGradeAgendamenConsulta() {
		return gradeAgendamenConsulta;
	}

	public void setSituacao(AacSituacaoConsultas situacao) {
		this.situacao = situacao;
	}

	public AacSituacaoConsultas getSituacao() {
		return situacao;
	}

	public void setDiaSemana(DominioDiaSemana diaSemana) {
		this.diaSemana = diaSemana;
	}

	public DominioDiaSemana getDiaSemana() {
		return diaSemana;
	}

	public void setHoraConsulta(Date horaConsulta) {
		this.horaConsulta = horaConsulta;
	}

	public Date getHoraConsulta() {
		return horaConsulta;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public AacPagador getPagador() {
		return pagador;
	}

	public List<AacPagador> getPagadorList() {
		return pagadorList;
	}

	public void setPagadorList(List<AacPagador> pagadorList) {
		this.pagadorList = pagadorList;
	}

	public void setAutorizacao(AacTipoAgendamento autorizacao) {
		this.autorizacao = autorizacao;
	}

	public AacTipoAgendamento getAutorizacao() {
		return autorizacao;
	}

	public void setAutorizacaoList(List<AacTipoAgendamento> autorizacaoList) {
		this.autorizacaoList = autorizacaoList;
	}

	public List<AacTipoAgendamento> getAutorizacaoList() {
		return autorizacaoList;
	}

	public void setCondicaoList(List<AacCondicaoAtendimento> condicaoList) {
		this.condicaoList = condicaoList;
	}

	public List<AacCondicaoAtendimento> getCondicaoList() {
		return condicaoList;
	}

	public void setCondicao(AacCondicaoAtendimento condicao) {
		this.condicao = condicao;
	}

	public AacCondicaoAtendimento getCondicao() {
		return condicao;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setNroConsulta(Integer nroConsulta) {
		this.nroConsulta = nroConsulta;
	}

	public Integer getNroConsulta() {
		return nroConsulta;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public DynamicDataModel<HorarioConsultaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<HorarioConsultaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getDesabilitaBotao() {
		return desabilitaBotao;
	}

	public void setDesabilitaBotao(Boolean desabilitaBotao) {
		this.desabilitaBotao = desabilitaBotao;
	}

	public AacSituacaoConsultas getNovaSituacao() {
		return novaSituacao;
	}

	public void setNovaSituacao(AacSituacaoConsultas novaSituacao) {
		this.novaSituacao = novaSituacao;
	}

	public List<Integer> getConsultasSelecionadas() {
		return consultasSelecionadas;
	}

	public void setConsultasSelecionadas(List<Integer> consultasSelecionadas) {
		this.consultasSelecionadas = consultasSelecionadas;
	}

	public ConverterConsultasVO getConsultaPagadores() {
		return consultaPagadores;
	}

	public void setConsultaPagadores(ConverterConsultasVO consultaPagadores) {
		this.consultaPagadores = consultaPagadores;
	}

	public boolean isAllChecked() {
		return allChecked;
	}

	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}

	public AacConsultas getHorarioConsulta() {
		return horarioConsulta;
	}

	public void setHorarioConsulta(AacConsultas horarioConsulta) {
		this.horarioConsulta = horarioConsulta;
	}
	
	public ConsultaAmbulatorioVO getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(ConsultaAmbulatorioVO consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}
	
	public Boolean getAmbulatorioAgendados() {
		return ambulatorioAgendados;
	}

	public void setAmbulatorioAgendados(Boolean ambulatorioAgendados) {
		this.ambulatorioAgendados = ambulatorioAgendados;
	}
	
	public String getPacienteNome() {
		return pacienteNome;
	}

	public void setPacienteNome(String pacienteNome) {
		this.pacienteNome = pacienteNome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

}
