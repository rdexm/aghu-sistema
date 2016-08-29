package br.gov.mec.aghu.ambulatorio.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.DisponibilidadeHorariosVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * Controller da tela de pesquisa de disponibilidade de horarios por
 * grade/consulta
 */
public class DisponibilidadeHorariosPaginatorController extends ActionController implements ActionPaginator {

    private static final long serialVersionUID = -3744812806893136737L;

    @EJB
    private IAmbulatorioFacade ambulatorioFacade;

    @EJB
    private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;

    @EJB
    private IRegistroColaboradorFacade registroColaboradorFacade;

    @EJB
    private IAghuFacade aghuFacade;

    @EJB
    private IParametroFacade parametroFacade;
    
    @EJB
    private ICascaFacade cascaFacade;

    private Boolean voltar = false;

	private Boolean voltarInterconsultas = false;
	
	@Inject @Paginator
    private DynamicDataModel<DisponibilidadeHorariosVO> dataModel;

    private static final Log LOG = LogFactory.getLog(DisponibilidadeHorariosPaginatorController.class);

    private AacGradeAgendamenConsultas gradeAgendamenConsultas;

    // resultado da pesquisa na lista de valores
    private List<EspCrmVO> listaEspCrmVO = new ArrayList<EspCrmVO>();

	private static final String LISTAR_CONSULTAS_POR_GRADE= "ambulatorio-listarConsultasPorGrade";

	private AghEspecialidades aghEspecialidades;
	
	private static final String PAGE_GESTAO_INTERCONSULTAS = "ambulatorio-gestaoInterconsultas";
	
	private static final String  DISPONIBILIDADE_HORARIOS = "gradeagendamento-disponibilidadeHorarios";

	private static final String PAGE_MANTER_SUMARIO_ALTA = "prescricaomedica-manterSumarioAlta";
	
	@Inject
	private PesquisarInterconsultasPaginatorController pesquisarInterconsultasPaginatorController;
	
	@Inject
	private ListarConsultasPorGradeController listarConsultasPorGradeController;
	
	private List<DisponibilidadeHorariosVO> lista;
	
	
	
    // FILTRO
    private Integer seq;
    private AghEspecialidades especialidade;
    private AghEquipes equipe;
    private RapServidores profissional;

    private Date dtConsulta = null;
    private Date horaConsulta = null;

    private Date mesInicio = null;

    private Date mesFim = null;

    private DominioDiaSemana diaSemana = null;

    private List<AacPagador> pagadorList;
    private AacPagador pagador;

    private List<AacTipoAgendamento> autorizacaoList;
    private AacTipoAgendamento autorizacao;

    private List<AacCondicaoAtendimento> condicaoList;
    private AacCondicaoAtendimento condicao;

    private AacTipoAgendamento tipoAgendamento;

    /* CRM PROFESSOR */
    private ProfessorCrmInternacaoVO professorPesq;
    private VAacSiglaUnfSalaVO siglaUnfSalaVO;

    // SELEÇÃO
    private Integer seqSelected;
    private String labelZonaSala;

    private Boolean disponibilidade = false;

	private String cameFrom;

	private AghAtendimentos atendimento;

	private boolean origemSumario;
	private String labelZona;
	private String labelSala;
	private String titleZona;
	private String titleSala;
	private VAacSiglaUnfSalaVO zona;
	private VAacSiglaUnfSala zonaSala;
	private List<VAacSiglaUnfSala> zonaSalaList;
	private DominioTurno turno;
	private String prontuarioSumario;
	
	private Boolean visualizarPrimeirasConsultasSMS = false;
	
    @PostConstruct
    	protected void inicializar() {
    	this.begin(conversation);
		carregarParametros();
    }

    /**
     * Método executado ao iniciar a controller
     */
    public void iniciar() {
		this.populaCombo1();
		this.populaCombo2();
		this.populaCombo3();
		voltar = false;
		origemSumario = false;
		if (cameFrom != null && cameFrom.equals(PAGE_GESTAO_INTERCONSULTAS)){
			voltarInterconsultas = true;			
		}else if (PAGE_MANTER_SUMARIO_ALTA.equals(cameFrom)){
			origemSumario = true;
			if(atendimento != null && atendimento.getPaciente() != null && atendimento.getPaciente().getProntuario() != null){
				prontuarioSumario = atendimento.getPaciente().getProntuario().toString();
			}
		}

	}
	
	private void carregarParametros() {
		try {
			this.labelZona = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			this.labelSala = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();

			if (this.labelZona==null){
				this.labelZona="Zona";
			}
			if(this.labelSala == null) {
				this.labelSala = "Sala";
			}
			this.labelZonaSala = labelZona + "/" + labelSala;
			this.titleZona = WebUtil.initLocalizedMessage("TITLE_ZONA_GRADE_AGENDAMENTO", null, this.labelZona);
			this.titleSala = WebUtil.initLocalizedMessage("TITLE_PESQUISAR_PACIENTES_AGENDADOS_SALA", null, this.labelSala);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método para Suggestion Box de Zona
	 */	
	public List<VAacSiglaUnfSalaVO> obterZona(String objPesquisa) throws BaseException  {
		if (objPesquisa!=null && objPesquisa instanceof String){
			objPesquisa = ((String) objPesquisa).trim();
		}
		return ambulatorioFacade.pesquisarTodasZonas(objPesquisa);
	}
	
	public void obterZonaSala()  {	
		zonaSalaList = new ArrayList<VAacSiglaUnfSala>();
		if (zona!=null) {
			List<AghUnidadesFuncionais> undFuncionais = aghuFacade.listarUnidadeFuncionalPorFuncionalSala(zona.getDescricao());	
			if (!undFuncionais.isEmpty()){
				zonaSalaList =  this.aghuFacade.pesquisarSalasUnidadesFuncionais(undFuncionais, DominioSituacao.A);
			}
		}else{
			zonaSala=null;
		}
	}

    /**
     * Método executado ao clicar no botão pesquisar
     */
    public void pesquisar() {
		try {

			Short unfSeq = null;
			List<AghEspecialidades> listEspecialidade = null;

			this.seqSelected = null;

			if (dtConsulta != null) {
				this.setMesInicio(null);
				this.setMesFim(null);
			}

			if (siglaUnfSalaVO != null) {
				unfSeq = siglaUnfSalaVO.getUnfSeq();
			}

			if (origemSumario && especialidade == null) {
				listEspecialidade = obterEspecialidade(null);
			}

			this.lista = ambulatorioFacade.listarDisponibilidadeHorarios(seq,
					unfSeq, especialidade, equipe, profissional, pagador,
					autorizacao, condicao, dtConsulta, horaConsulta, mesInicio,
					mesFim, diaSemana, disponibilidade, zona, zonaSala,
					ambulatorioFacade.definePeriodoTurno(turno),
					listEspecialidade, visualizarPrimeirasConsultasSMS);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		    LOG.error(e.getMessage(), e);
		}
	    
	    visualizarPrimeirasConsultasSMS = cascaFacade.usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarPrimeirasConsultasSMS");

	    dataModel.reiniciarPaginator();
	} 

    /**
     * Método que controla a exibição da modal de pesquisa sem filtro
     */
	public void validarPesquisa() {
		try {
			this.ambulatorioFacade.validarPesquisaDisponibilidadeHorarios(seq,
					especialidade, horaConsulta, dtConsulta, mesInicio, mesFim,
					diaSemana, zona, turno);
			
			if (dataModel.getPesquisaAtiva()) {
				dataModel.limparPesquisa();
			}

			if (dtConsulta == null && mesInicio == null && mesFim == null
					&& !voltar) {
				this.openDialog("modalPesquisaSemFiltroWG");
				return;
			} else {
				this.pesquisar();
			}

	    } catch (ApplicationBusinessException exception) {
			apresentarExcecaoNegocio(exception);
		    LOG.error(exception.getMessage(), exception);
		}
	}

    public void limparPesquisa() {
	seq = null;
	siglaUnfSalaVO = null;
	especialidade = null;
	equipe = null;
	profissional = null;
	seqSelected = null;
	dtConsulta = null;
	horaConsulta = null;
	diaSemana = null;
	pagador = null;
	autorizacao = null;
	condicao = null;
	mesInicio = null;
	mesFim = null;
	disponibilidade = null;
	zona = null;
	equipe = null;
	zonaSalaList=new ArrayList<VAacSiglaUnfSala>();
	turno=null;
	dataModel.limparPesquisa();
    }

    @Override
    public Long recuperarCount() {
		Short unfSeq = null;
	
		mesFim = (mesFim == null) ? null : DateUtil.truncaDataFim(DateUtil.obterUltimoDiaDoMes(mesFim));
	
		if (siglaUnfSalaVO != null) {
		    unfSeq = siglaUnfSalaVO.getUnfSeq();
		}

		Long result = null;
		List<AghEspecialidades> listEspecialidade = null;
		try {
		
			if(origemSumario && especialidade == null){
				listEspecialidade = obterEspecialidade(null);
			}
			result = ambulatorioFacade.listarDisponibilidadeHorariosCount(seq, unfSeq, especialidade, equipe, profissional, pagador,
				autorizacao, condicao, dtConsulta, horaConsulta, mesInicio, mesFim, diaSemana, disponibilidade,
				zona, zonaSala, ambulatorioFacade.definePeriodoTurno(turno), listEspecialidade, visualizarPrimeirasConsultasSMS);
		} catch (ApplicationBusinessException e) {
		    apresentarExcecaoNegocio(e);
		    LOG.error(e.getMessage(), e);
		}

		if (result == null) {
		    return 0L;
		} else {
		    return Long.valueOf(result);
		}
    }

    @Override
    public List<DisponibilidadeHorariosVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
    	
		if (lista == null) {
		    return new ArrayList<DisponibilidadeHorariosVO>();
		} else if (lista.size() > (firstResult + maxResult)) {
		    return lista.subList(firstResult, firstResult + maxResult);
		} else {
			return lista.subList(firstResult, lista.size());
		}
    }

    public String listarConsultasPorGrade() {
    	listarConsultasPorGradeController.setConsultaExcedente(false);
    	listarConsultasPorGradeController.setFormaAgendamentoExcedenteParametro(null);
    	listarConsultasPorGradeController.setDtConsultaExcedenteParametro(null);
    	return LISTAR_CONSULTAS_POR_GRADE;
    }
    
    public String listarConsultasPorGradeExcedente() {
    	if (pagador != null && autorizacao != null && condicao != null){
    		Enum[] fields = { AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO, AacFormaAgendamento.Fields.TIPO_AGENDAMENTO, AacFormaAgendamento.Fields.PAGADOR};
    		AacFormaAgendamentoId formaAgendamentoId = new AacFormaAgendamentoId(condicao.getSeq(), autorizacao.getSeq(), pagador.getSeq());
    		AacFormaAgendamento formaAgendamento = ambulatorioFacade.obterAacFormaAgendamentoPorChavePrimaria(formaAgendamentoId, fields, null);
    		listarConsultasPorGradeController.setFormaAgendamentoExcedenteParametro(formaAgendamento);
    	}
    	else{
    		listarConsultasPorGradeController.setFormaAgendamentoExcedenteParametro(null);
    	}
    	if (dtConsulta != null){
    		Calendar calendarData = Calendar.getInstance();
    		calendarData.setTime(dtConsulta);
    		if (horaConsulta != null){
    			Calendar calendarHora = Calendar.getInstance();
    			calendarHora.setTime(horaConsulta);
    			calendarData.set(Calendar.HOUR_OF_DAY, calendarHora.get(Calendar.HOUR_OF_DAY));
    			calendarData.set(Calendar.MINUTE, calendarHora.get(Calendar.MINUTE));  
    			calendarData.set(Calendar.SECOND, calendarHora.get(Calendar.SECOND));
    		}else{
    			calendarData.set(Calendar.HOUR_OF_DAY, 0);
    			calendarData.set(Calendar.MINUTE, 0);
    			calendarData.set(Calendar.SECOND, 0);    			
    		}
    		listarConsultasPorGradeController.setDtConsultaExcedenteParametro(calendarData.getTime());
    	}
    	else{
    		listarConsultasPorGradeController.setDtConsultaExcedenteParametro(null);
    	}
    	listarConsultasPorGradeController.setConsultaExcedente(true); 
    	return LISTAR_CONSULTAS_POR_GRADE;
    }

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

    public List<AghEspecialidades> obterEspecialidade(String parametro) {
    	if(this.origemSumario){
    		RapServidores servidorLogado = null;
    		try {
    			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
    							new Date());
    		} catch (ApplicationBusinessException e) {
    			apresentarExcecaoNegocio(e);
    		}
    		return aghuFacade.pesquisarEspecialidadesAtivasOrigemSumario((String) parametro, atendimento.getSeq(), servidorLogado);
    	}else {
    		return aghuFacade.pesquisarEspecialidadesAtivas((String) parametro);
    	}
    }

    public List<AghEquipes> obterEquipe(String parametro) {
	return aghuFacade.getListaEquipes((String) parametro);
    }

    public List<EspCrmVO> pesquisarMedico(Object descricao) throws ApplicationBusinessException {
	if (especialidade != null) {
	    listaEspCrmVO = solicitacaoInternacaoFacade.pesquisarEspCrmVO(descricao, especialidade);
	}
	return listaEspCrmVO;
    }

    public AacGradeAgendamenConsultas getGradeAgendamenConsultas() {
	return gradeAgendamenConsultas;
    }

    public void setGradeAgendamenConsultas(AacGradeAgendamenConsultas gradeAgendamenConsultas) {
	this.gradeAgendamenConsultas = gradeAgendamenConsultas;
    }

    public void setEspecialidade(AghEspecialidades especialidade) {
	this.especialidade = especialidade;
    }

    public AghEspecialidades getEspecialidade() {
	return especialidade;
    }

    public void setEquipe(AghEquipes equipe) {
	this.equipe = equipe;
    }

    public AghEquipes getEquipe() {
	return equipe;
    }

    public List<EspCrmVO> getListaEspCrmVO() {
	return listaEspCrmVO;
    }

    public void setListaEspCrmVO(List<EspCrmVO> listaEspCrmVO) {
	this.listaEspCrmVO = listaEspCrmVO;
    }

    public ProfessorCrmInternacaoVO getProfessorPesq() {
	return professorPesq;
    }

    public void setProfessorPesq(ProfessorCrmInternacaoVO professorPesq) {
	this.professorPesq = professorPesq;
    }

    public IAmbulatorioFacade getAmbulatorioFacade() {
	return ambulatorioFacade;
    }

    public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
	this.ambulatorioFacade = ambulatorioFacade;
    }

    public void populaCombo1() {
	this.pagadorList = ambulatorioFacade.pesquisaPagadoresComAgendamento();
    }

    public void populaCombo2() {
	this.autorizacaoList = ambulatorioFacade.pesquisaTipoAgendamentoComAgendamentoEPagador(this.getPagador());
	if (autorizacao != null && !autorizacaoList.contains(autorizacao)) {
	    autorizacao = null;
	}
    }

    public void populaCombo3() {
	this.condicaoList = ambulatorioFacade.pesquisaCondicaoAtendimentoComAgendamentoEPagadorETipo(this.getPagador(),
		this.getAutorizacao());
	if (condicao != null && !condicaoList.contains(condicao)) {
	    condicao = null;
	}
    }

    private void limparPeriodoConsulta() {
	mesInicio = null;
	mesFim = null;
    }

    public void verificarPeriodo() {
	if (dtConsulta != null) {
	    limparPeriodoConsulta();
	}
    }

    /**
     * Verifica botão de excluir, só aparecerá se não tiver agendamento para a
     * grade.
     * 
     * @param gradeAgendamenConsulta
     */
    public boolean verificarConsultas(AacGradeAgendamenConsultas gradeAgendamenConsulta) {
	return ambulatorioFacade.verificarGradeConsulta(gradeAgendamenConsulta.getSeq());
    }

    public VAacSiglaUnfSalaVO getSiglaUnfSalaVO() {
	return siglaUnfSalaVO;
    }

    public void setSiglaUnfSalaVO(VAacSiglaUnfSalaVO siglaUnfSalaVO) {
	this.siglaUnfSalaVO = siglaUnfSalaVO;
    }

    public void removerGradeAgendamento(AacGradeAgendamenConsultas gradeAgendamenConsulta) {
	try {
	    ambulatorioFacade.removerGradeConsulta(gradeAgendamenConsulta.getSeq());
	    dataModel.reiniciarPaginator();
	} catch (ApplicationBusinessException e) {
	    apresentarExcecaoNegocio(e);
	    LOG.error(e.getMessage(), e);
	}
    }

    public List<RapServidores> obterProfissionais(String parametro) {
	return registroColaboradorFacade.listarServidoresComPessoaFisicaPorNome((String) parametro);
    }

    public String getCriadoEm(Date dataCriacao) {
	if (dataCriacao != null) {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    return sdf.format(dataCriacao);
	} else {
	    return "-";
	}
    }

    public String getAlteradoEm(Date dataAlteracao) {
		if (dataAlteracao != null) {
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		    return sdf.format(dataAlteracao);
		} else {
		    return "-";
		}
    }

	
	public String voltar() {
		if(this.origemSumario){
			origemSumario = false;
			return PAGE_MANTER_SUMARIO_ALTA;
		}else {
			voltarInterconsultas = false;
			pesquisarInterconsultasPaginatorController.setCameFrom(DISPONIBILIDADE_HORARIOS);
			return PAGE_GESTAO_INTERCONSULTAS;
		}
	}

    public Date getDtConsulta() {
	return dtConsulta;
    }

    public void setDtConsulta(Date dtConsulta) {
	this.dtConsulta = dtConsulta;
    }

    public Date getHoraConsulta() {
	return horaConsulta;
    }

    public void setHoraConsulta(Date horaConsulta) {
	this.horaConsulta = horaConsulta;
    }

    public DominioDiaSemana getDiaSemana() {
	return diaSemana;
    }

    public void setDiaSemana(DominioDiaSemana diaSemana) {
	this.diaSemana = diaSemana;
    }

    public String getLabelZonaSala() {
	return labelZonaSala;
    }

    public void setLabelZonaSala(String labelZonaSala) {
	this.labelZonaSala = labelZonaSala;
    }

    public RapServidores getProfissional() {
	return profissional;
    }

    public void setProfissional(RapServidores profissional) {
	this.profissional = profissional;
    }

    public Boolean getDisponibilidade() {
	return disponibilidade;
    }

    public void setDisponibilidade(Boolean disponibilidade) {
	this.disponibilidade = disponibilidade;
    }

    public DynamicDataModel<DisponibilidadeHorariosVO> getDataModel() {
	return dataModel;
    }

    public void setDataModel(DynamicDataModel<DisponibilidadeHorariosVO> dataModel) {
	this.dataModel = dataModel;
    }

    public List<AacPagador> getPagadorList() {
	return pagadorList;
    }

    public void setPagadorList(List<AacPagador> pagadorList) {
	this.pagadorList = pagadorList;
    }

    public AacPagador getPagador() {
	return pagador;
    }

    public void setPagador(AacPagador pagador) {
	this.pagador = pagador;
    }

    public AacTipoAgendamento getTipoAgendamento() {
	return tipoAgendamento;
    }

    public void setTipoAgendamento(AacTipoAgendamento tipoAgendamento) {
	this.tipoAgendamento = tipoAgendamento;
    }

    public List<AacTipoAgendamento> getAutorizacaoList() {
	return autorizacaoList;
    }

    public void setAutorizacaoList(List<AacTipoAgendamento> autorizacaoList) {
	this.autorizacaoList = autorizacaoList;
    }

    public AacTipoAgendamento getAutorizacao() {
	return autorizacao;
    }

    public void setAutorizacao(AacTipoAgendamento autorizacao) {
	this.autorizacao = autorizacao;
    }

    public List<AacCondicaoAtendimento> getCondicaoList() {
	return condicaoList;
    }

    public void setCondicaoList(List<AacCondicaoAtendimento> condicaoList) {
	this.condicaoList = condicaoList;
    }

    public AacCondicaoAtendimento getCondicao() {
	return condicao;
    }

    public void setCondicao(AacCondicaoAtendimento condicao) {
	this.condicao = condicao;
    }

    public Date getMesInicio() {
	return mesInicio;
    }

    public void setMesInicio(Date mesInicio) {
	this.mesInicio = mesInicio;
    }

    public Date getMesFim() {
	return mesFim;
    }

    public void setMesFim(Date mesFim) {
	this.mesFim = mesFim;
    }

    public Boolean getVoltar() {
	return voltar;
    }

    public void setVoltar(boolean isVoltar) {
	this.voltar = isVoltar;
    }
	
	
	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Boolean getVoltarInterconsultas() {
		return voltarInterconsultas;
	}

	public void setVoltarInterconsultas(Boolean voltarInterconsultas) {
		this.voltarInterconsultas = voltarInterconsultas;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public boolean isOrigemSumario() {
		return origemSumario;
	}

	public void setOrigemSumario(boolean origemSumario) {
		this.origemSumario = origemSumario;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public String getLabelSala() {
		return labelSala;
	}

	public void setLabelSala(String labelSala) {
		this.labelSala = labelSala;
	}

	public String getTitleZona() {
		return titleZona;
	}

	public void setTitleZona(String titleZona) {
		this.titleZona = titleZona;
	}

	public String getTitleSala() {
		return titleSala;
	}

	public void setTitleSala(String titleSala) {
		this.titleSala = titleSala;
	}

	public VAacSiglaUnfSalaVO getZona() {
		return zona;
	}

	public void setZona(VAacSiglaUnfSalaVO zona) {
		this.zona = zona;
	}

	public VAacSiglaUnfSala getZonaSala() {
		return zonaSala;
	}

	public void setZonaSala(VAacSiglaUnfSala zonaSala) {
		this.zonaSala = zonaSala;
	}

	public List<VAacSiglaUnfSala> getZonaSalaList() {
		return zonaSalaList;
	}

	public void setZonaSalaList(List<VAacSiglaUnfSala> zonaSalaList) {
		this.zonaSalaList = zonaSalaList;
	}

	public DominioTurno getTurno() {
		return turno;
	}

	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}

	public String getProntuarioSumario() {
		return prontuarioSumario;
	}

	public void setProntuarioSumario(String prontuarioSumario) {
		this.prontuarioSumario = prontuarioSumario;
	}

	public Boolean getVisualizarPrimeirasConsultasSMS() {
		return visualizarPrimeirasConsultasSMS;
	}

	public void setVisualizarPrimeirasConsultasSMS(
			Boolean visualizarPrimeirasConsultasSMS) {
		this.visualizarPrimeirasConsultasSMS = visualizarPrimeirasConsultasSMS;
	}
	
	public List<RapServidores> obterProfissionaisPorEquipe(String parametro) {
		return registroColaboradorFacade.listarServidoresComPessoaFisicaPorEquipe((String) parametro, equipe);
	}
	
}
