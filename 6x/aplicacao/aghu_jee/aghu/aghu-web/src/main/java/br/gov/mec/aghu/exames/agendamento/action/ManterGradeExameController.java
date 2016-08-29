package br.gov.mec.aghu.exames.agendamento.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemanaColetaExames;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGradeAgendaExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioGradeExame;
import br.gov.mec.aghu.model.AelHorarioGradeExameId;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelUnfExecutaExames;
import br.gov.mec.aghu.model.VAelUnfExecutaExamesId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterGradeExameController extends ActionController  {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterGradeExameController.class);
	
	private static final long serialVersionUID = -5811540494287362022L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	//Variaveis aba GRADE
	private AelGradeAgendaExame gradeAgendamentoExame;
	private VAelUnfExecutaExames viewExame;
	private Short unfSeq;
	private Integer seqp;

	//Campos alimentados quando criada nova grade.
	private Short unfSeqGerado;
	private Integer seqpGerado;
	
	
	//Variaveis aba HORARIOS
	private AelHorarioGradeExame horarioGradeExame;
	private List<AelHorarioGradeExame> horarioGradeExameList;
	private Date horaInicial;
	private Date horaFinal;
	private DominioDiaSemanaColetaExames diaSemana;
	
	private Boolean ativo;
	private Boolean exclusivo;
	
	private Boolean existeExames;
	
	private Boolean emEdicao;
	
	private DominioDiaSemanaColetaExames[] listaDiaSemana = {DominioDiaSemanaColetaExames.SEG,
			DominioDiaSemanaColetaExames.TER, DominioDiaSemanaColetaExames.QUA,
			DominioDiaSemanaColetaExames.QUI, DominioDiaSemanaColetaExames.SEX,
			DominioDiaSemanaColetaExames.SAB, DominioDiaSemanaColetaExames.DOM};
	
    private enum CampoGradeAgendamentoExceptionCode implements BusinessExceptionCode{
        MENSAGEM_DATA_INICIAL_OBRIGATORIO,
        MENSAGEM_DATA_FINAL_OBRIGATORIO;
    }	
	
	//Variaveis geração de disponibilidade
	private Date dataInicial;
	private Date dataFinal;
	
	private AelHorarioGradeExame itemHorarioGradeExameSelecionado; 

	public void inicio() {
	 

		if (this.unfSeq != null && this.seqp != null){
			AelGradeAgendaExameId idGrade = new AelGradeAgendaExameId(this.unfSeq, this.seqp);
			this.gradeAgendamentoExame = this.agendamentoExamesFacade.obterGradeExamePorChavePrimaria(idGrade);
			if (this.gradeAgendamentoExame.getExame() != null){
				VAelUnfExecutaExamesId idViewExames = new VAelUnfExecutaExamesId();
				idViewExames.setManSeq(this.gradeAgendamentoExame.getExame().getId().getEmaManSeq());
				idViewExames.setSigla(this.gradeAgendamentoExame.getExame().getId().getEmaExaSigla());
				idViewExames.setUnfSeq(this.gradeAgendamentoExame.getExame().getId().getUnfSeq().getSeq());
				this.viewExame = this.agendamentoExamesFacade.obterVAelUnfExecutaExamesPorId(idViewExames);
			}
		}else{
			gradeAgendamentoExame = new AelGradeAgendaExame();
			gradeAgendamentoExame.setSituacao(DominioSituacao.A);
			horarioGradeExameList = new ArrayList<AelHorarioGradeExame>();
			existeExames = Boolean.FALSE;	
		}
		if (gradeAgendamentoExame.getId() != null){
			horarioGradeExameList = agendamentoExamesFacade.pesquisaHorariosPorGrade(gradeAgendamentoExame);
			if (horarioGradeExameList != null && !horarioGradeExameList.isEmpty()){
				existeExames = Boolean.TRUE;
			}else{
				existeExames = Boolean.FALSE;
			}
		}
		horarioGradeExame = new AelHorarioGradeExame();
		this.atribuirDefaults();
	
	}
	
	public void limparSuggestions(){
		this.gradeAgendamentoExame.setSalaExecutoraExames(null);
		this.gradeAgendamentoExame.setGrupoExame(null);
		this.gradeAgendamentoExame.setExame(null);
	}
	
	public void limparCamposModal(){
		this.dataInicial = null;
		this.dataFinal = null;
	}
	
	public void limparExame() {
		this.gradeAgendamentoExame.setExame(null);
	}
	
	public void atribuirDefaults(){
		this.horaInicial = null;
		this.horaFinal = null;
		this.diaSemana = null;
		this.ativo = Boolean.TRUE;
		this.exclusivo = Boolean.FALSE;
		this.emEdicao = Boolean.FALSE;
	}
	
	//--[ACTIONS]	
	public void salvar() {
		try {
			Boolean novo = Boolean.FALSE;
			if (this.gradeAgendamentoExame.getId() == null){
				novo = Boolean.TRUE;
			}
			if (this.viewExame != null){
				AelUnfExecutaExamesId exameId = new AelUnfExecutaExamesId();
				exameId.setEmaManSeq(this.viewExame.getId().getManSeq());
				exameId.setEmaExaSigla(this.viewExame.getId().getSigla());
				exameId.setUnfSeq(this.aghuFacade.obterAghUnidFuncionaisPeloId(this.viewExame.getId().getUnfSeq()));
				this.gradeAgendamentoExame.setExame(this.agendamentoExamesFacade.obterAelUnfExecutaExamesDAOPorId(exameId));
			}
			
			this.agendamentoExamesFacade.persistirGradeAgendaExame(this.gradeAgendamentoExame);
			if (novo){
				this.unfSeqGerado = this.gradeAgendamentoExame.getId().getUnfSeq();
				this.seqpGerado = this.gradeAgendamentoExame.getId().getSeqp();
			}
			this.apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_EXAME_SALVO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}		
	}
	
	public 	Date getDataHoraUltimaGrade() {    //novo método para pegar a data e hora do último agendamento

		if (gradeAgendamentoExame != null && gradeAgendamentoExame.getUnidadeFuncional() != null
				&& gradeAgendamentoExame.getId() != null) {
			return agendamentoExamesFacade.obterHorarioExameDataMaisRecentePorGrade(
					gradeAgendamentoExame.getUnidadeFuncional().getSeq()
					, gradeAgendamentoExame.getId().getSeqp());
		}
		return null;
	}

	
	public void gerarDisponibilidade(){
		try {				
			this.validarCampoObrigatorio();
			Integer qtdGerada = agendamentoExamesFacade.gerarDisponibilidadeHorarios(gradeAgendamentoExame, dataInicial, dataFinal, getDataHoraUltimaGrade());
			this.apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_EXAME_GERADO", qtdGerada);
			this.limparCamposModal();
		}  catch (ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);			
	     } catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	private void validarCampoObrigatorio() throws ApplicationBusinessException{
        if (this.dataInicial == null){
            throw new ApplicationBusinessException(CampoGradeAgendamentoExceptionCode.MENSAGEM_DATA_INICIAL_OBRIGATORIO);
        }		
        
        if (this.dataFinal == null){
            throw new ApplicationBusinessException(CampoGradeAgendamentoExceptionCode.MENSAGEM_DATA_FINAL_OBRIGATORIO);
        }	
	}
	
	public String voltarLista(){
		this.seqp = null;
		this.unfSeq = null;
		this.gradeAgendamentoExame = null;
		this.horarioGradeExameList = null;
		this.viewExame = null;
	//	this.dataModel.reiniciarPaginator(GradeExamePaginatorController.class);
		return "pesquisarGradeExame";
	}
	
	public String criar(){
		gradeAgendamentoExame = null;
		inicio();
		return "edit";
	}
	
	public String editar(AelGradeAgendaExame entity) throws ApplicationBusinessException {
		gradeAgendamentoExame = agendamentoExamesFacade.obterGradeExamePorChavePrimaria(entity.getId());
		inicio();
		horarioGradeExameList = agendamentoExamesFacade.pesquisaHorariosPorGrade(gradeAgendamentoExame);
		if (horarioGradeExameList != null && !horarioGradeExameList.isEmpty()){
			existeExames = Boolean.TRUE;
		}
		return "edit";
	}
	
	//--[SUGGESTIONS]	

	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorSeqDescricao(parametro);
	}
	
	public List<AelSalasExecutorasExames> pesquisarSala(String parametro) {
		return this.examesFacade.pesquisarSalasExecutorasExamesPorSeqOuNumeroEUnidadeAtivos(parametro, gradeAgendamentoExame.getUnidadeFuncional());
	}
	
	public List<AelGrupoExames> pesquisarGrupoExame(String parametro) {
		return this.examesFacade.pesquisarGrupoExamePorCodigoOuDescricaoEUnidadeAtivos(parametro, gradeAgendamentoExame.getUnidadeFuncional());
	}
	
	public List<VAelUnfExecutaExames> pesquisarExame(String parametro) {
		List<VAelUnfExecutaExames> lista = this.examesFacade.pesquisarExamePorDescricaoOuSigla(parametro);
		return lista;
	}
	
	public List<RapServidores> pesquisarServidor(String parametro) {
			return registroColaboradorFacade.pesquisarServidores(parametro);
	}
	
	public List<AelTipoMarcacaoExame> pesquisarTipoMarcacaoExame(String parametro) {
		return this.examesFacade.pesquisarTipoMarcacaoExame(parametro);
	}

	public AelGradeAgendaExame getGradeAgendamentoExame() {
		return gradeAgendamentoExame;
	}

	public void setGradeAgendamentoExame(AelGradeAgendaExame gradeAgendamentoExame) {
		this.gradeAgendamentoExame = gradeAgendamentoExame;
	}
	
	//--[ABA HORARIOS AGENDADOS]
	
	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	private void atribuirCampos(){
		this.horarioGradeExame.setGradeAgendaExame(this.gradeAgendamentoExame);
		      
		if(this.horaFinal != null){
			Calendar calendarFim = Calendar.getInstance();  
			calendarFim.setTime(new Date()); //colocando o objeto Date no Calendar  
			calendarFim.set(Calendar.HOUR_OF_DAY, this.horaFinal.getHours()); //zerando as horas, minuots e segundos..  
			calendarFim.set(Calendar.MINUTE, this.horaFinal.getMinutes());  
			calendarFim.set(Calendar.SECOND, this.horaFinal.getSeconds());  
			this.horarioGradeExame.setHoraFim(calendarFim.getTime());
		}else{
			this.horarioGradeExame.setHoraFim(null);
		}
		
		if (!emEdicao){
			AelHorarioGradeExameId id = new AelHorarioGradeExameId();
			id.setDiaSemana(this.diaSemana);
			id.setGaeUnfSeq(this.gradeAgendamentoExame.getId().getUnfSeq());
			id.setGaeSeqp(this.gradeAgendamentoExame.getId().getSeqp());
			Calendar calendarInicio = Calendar.getInstance();  
			calendarInicio.setTime(new Date()); //colocando o objeto Date no Calendar  
			calendarInicio.set(Calendar.HOUR_OF_DAY, this.horaInicial.getHours()); //zerando as horas, minuots e segundos..  
			calendarInicio.set(Calendar.MINUTE, this.horaInicial.getMinutes());  
			calendarInicio.set(Calendar.SECOND, this.horaInicial.getSeconds());  
			id.setHoraInicio(calendarInicio.getTime());
			this.horarioGradeExame.setId(id);
		}
		if(this.ativo){
			this.horarioGradeExame.setSituacao(DominioSituacao.A);
		}else{
			this.horarioGradeExame.setSituacao(DominioSituacao.I);
		}
		this.horarioGradeExame.setExclusivoExecutor(this.exclusivo);
	}
	
	public void gravarAgendamento() {
		this.atribuirCampos();
		if (horarioGradeExame.getHoraFim() != null && !horarioGradeExame.getHoraFim().after(horarioGradeExame.getId().getHoraInicio())){
			this.apresentarMsgNegocio(Severity.ERROR,"MSG_MANTER_GRADE_EXAME_HORARIO_FIM");
			return;
		}		
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(horarioGradeExame.getTempoEntreHorarios());
		if (calendario.get(Calendar.MINUTE) <= 0 && calendario.get(Calendar.HOUR_OF_DAY) <= 0) {
			this.apresentarMsgNegocio(Severity.ERROR,"MSG_MANTER_GRADE_EXAME_DURACAO");
			return;
		}		
		try {	
			
			if (this.emEdicao){
				agendamentoExamesFacade.atualizarHorarioGradeExame(horarioGradeExame);
			}else{
				agendamentoExamesFacade.inserirHorarioGradeExame(horarioGradeExame);
			}
			horarioGradeExameList = agendamentoExamesFacade.pesquisaHorariosPorGrade(gradeAgendamentoExame);
			this.montaMensagemSucesso(horarioGradeExame);
			horarioGradeExame = new AelHorarioGradeExame();
			this.atribuirDefaults();
		} catch (BaseException e) {
//			horarioGradeExame.setId(null);
			horarioGradeExameList = agendamentoExamesFacade.pesquisaHorariosPorGrade(gradeAgendamentoExame);
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}		
	}
	
	private void montaMensagemSucesso(AelHorarioGradeExame horarioGradeExame){
		String inicioUltimoHorario;
		String fimUltimoHorario;
		Date horaFim;
		String hora;
		String minuto;
		
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.setTime(horarioGradeExame.getId().getHoraInicio());
		
		hora = String.valueOf(dataInicio.get(Calendar.HOUR_OF_DAY));
		minuto = String.valueOf(dataInicio.get(Calendar.MINUTE));
		if (minuto.length() == 1) {
			minuto = "0" + dataInicio.get(Calendar.MINUTE);
		}

		if (horarioGradeExame.getHoraFim()!=null){
			horaFim = horarioGradeExame.getHoraFim();
		}else{
			horaFim = this.obtemDataFinal(horarioGradeExame);
		}
		inicioUltimoHorario = hora + ":" + minuto;
		
		Calendar dataFim = Calendar.getInstance();
		dataFim.setTime(horaFim);
		
		hora = String.valueOf(dataFim.get(Calendar.HOUR_OF_DAY));
		minuto = String.valueOf(dataFim.get(Calendar.MINUTE));
		if (minuto.length() == 1) {
			minuto = "0" + dataFim.get(Calendar.MINUTE);
		}
		fimUltimoHorario = hora+":"+minuto;
	
		this.apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_EXAME_HORARIO_SALVO", inicioUltimoHorario, fimUltimoHorario);
	}
	
	public void editarAgendamento() {
		AelHorarioGradeExame entity = itemHorarioGradeExameSelecionado;
		this.emEdicao = Boolean.TRUE;
		this.horarioGradeExame = entity;
		if (this.horarioGradeExame != null){
			this.exclusivo = this.horarioGradeExame.getExclusivoExecutor();
			this.ativo = this.horarioGradeExame.getSituacao().isAtivo();
			this.horaFinal = this.horarioGradeExame.getHoraFim();
			if (this.horarioGradeExame.getId() != null){
				this.diaSemana = this.horarioGradeExame.getId().getDiaSemana();
				this.horaInicial = this.horarioGradeExame.getId().getHoraInicio();
			}
		}
	}
	
	public void cancelarEdicao(){
		this.horarioGradeExame = new AelHorarioGradeExame();
		this.atribuirDefaults();
	}
	
	public void removerAgendamento() {
		AelHorarioGradeExame horarioGrade = getItemHorarioGradeExameSelecionado();
		agendamentoExamesFacade.excluirHorarioGrade(horarioGrade);
		this.apresentarMsgNegocio(Severity.INFO,"MSG_MANTER_GRADE_EXAME_HORARIO_REMOVIDO");
		horarioGradeExameList = agendamentoExamesFacade.pesquisaHorariosPorGrade(gradeAgendamentoExame);
		horarioGradeExame = new AelHorarioGradeExame();
		this.atribuirDefaults();
	}	
	
	public String getDescricaoGrade(){
		StringBuilder sb = new StringBuilder();		
		if (gradeAgendamentoExame != null && gradeAgendamentoExame.getId() != null){
			sb.append("<b>Grade de Exame: </b>");
			sb.append(gradeAgendamentoExame.getId().getSeqp());
			if (gradeAgendamentoExame.getUnidadeFuncional() != null){
				sb.append(" <b>Unidade Executora: </b>");
				sb.append(gradeAgendamentoExame.getUnidadeFuncional().getDescricao());
			}
			if (gradeAgendamentoExame.getSalaExecutoraExames() != null){
				sb.append(" <b>Sala: </b>");
				sb.append(gradeAgendamentoExame.getSalaExecutoraExames().getNumero());
			}
			if (gradeAgendamentoExame.getGrupoExame() != null){
				sb.append(" <b>Grupo: </b>");
				sb.append(gradeAgendamentoExame.getGrupoExame().getDescricao());
			}
			if (gradeAgendamentoExame.getServidor() != null){
				sb.append(" <b>Responsável: </b>");
				sb.append(gradeAgendamentoExame.getServidor().getPessoaFisica().getNome());
			}	
		}
		return sb.toString();
	}
	
	public Integer obtemNumeroHorarios(AelHorarioGradeExame horario){
		Integer numHorarios = 0;

		Calendar dataVariavel = Calendar.getInstance();
		dataVariavel.setTime(horario.getId().getHoraInicio());
		
		Calendar dataFinal = Calendar.getInstance();
		dataFinal.setTime(horario.getHoraFim()); 
		
		while (dataVariavel.before(dataFinal)){
			Calendar tempoDuracao = Calendar.getInstance();
			tempoDuracao.setTime(horario.getTempoEntreHorarios());

			dataVariavel.add(Calendar.HOUR_OF_DAY, tempoDuracao.get(Calendar.HOUR_OF_DAY));
			dataVariavel.add(Calendar.MINUTE, tempoDuracao.get(Calendar.MINUTE));
			numHorarios++;
		}
		return numHorarios;
	}
	
	public Date obtemDataFinal(AelHorarioGradeExame horario){
		Calendar dataVariavel = Calendar.getInstance();
		dataVariavel.setTime(horario.getId().getHoraInicio());
		
		for (Short i = 0; i < horario.getNumHorario();i++){
			Calendar tempoDuracao = Calendar.getInstance();
			tempoDuracao.setTime(horario.getTempoEntreHorarios());

			dataVariavel.add(Calendar.HOUR_OF_DAY, tempoDuracao.get(Calendar.HOUR_OF_DAY));
			dataVariavel.add(Calendar.MINUTE, tempoDuracao.get(Calendar.MINUTE));
		}
		return dataVariavel.getTime();
	}
	
	public AelHorarioGradeExame getHorarioGradeExame() {
		return horarioGradeExame;
	}

	public void setHorarioGradeExame(AelHorarioGradeExame horarioGradeExame) {
		this.horarioGradeExame = horarioGradeExame;
	}

	public List<AelHorarioGradeExame> getHorarioGradeExameList() {
		return horarioGradeExameList;
	}

	public void setHorarioGradeExameList(
			List<AelHorarioGradeExame> horarioGradeExameList) {
		this.horarioGradeExameList = horarioGradeExameList;
	}

	public Boolean getExisteExames() {
		return existeExames;
	}

	public void setExisteExames(Boolean existeExames) {
		this.existeExames = existeExames;
	}

	public Date getHoraInicial() {
		return horaInicial;
	}

	public void setHoraInicial(Date horaInicial) {
		this.horaInicial = horaInicial;
	}

	public Date getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(Date horaFinal) {
		this.horaFinal = horaFinal;
	}

	public void setDiaSemana(DominioDiaSemanaColetaExames diaSemana) {
		this.diaSemana = diaSemana;
	}

	public DominioDiaSemanaColetaExames getDiaSemana() {
		return diaSemana;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setExclusivo(Boolean exclusivo) {
		this.exclusivo = exclusivo;
	}

	public Boolean getExclusivo() {
		return exclusivo;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public Short getUnfSeqGerado() {
		return unfSeqGerado;
	}

	public void setUnfSeqGerado(Short unfSeqGerado) {
		this.unfSeqGerado = unfSeqGerado;
	}

	public Integer getSeqpGerado() {
		return seqpGerado;
	}

	public void setSeqpGerado(Integer seqpGerado) {
		this.seqpGerado = seqpGerado;
	}

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
	
	
	public void setViewExame(VAelUnfExecutaExames viewExame) {
		this.viewExame = viewExame;
	}

	public VAelUnfExecutaExames getViewExame() {
		return viewExame;
	}

	public DominioDiaSemanaColetaExames[] getListaDiaSemana() {
		return listaDiaSemana;
	}

	public void setListaDiaSemana(DominioDiaSemanaColetaExames[] listaDiaSemana) {
		this.listaDiaSemana = listaDiaSemana;
	}

	public AelHorarioGradeExame getItemHorarioGradeExameSelecionado() {
		return itemHorarioGradeExameSelecionado;
	}

	public void setItemHorarioGradeExameSelecionado(
			AelHorarioGradeExame itemHorarioGradeExameSelecionado) {
		this.itemHorarioGradeExameSelecionado = itemHorarioGradeExameSelecionado;
	}
}