package br.gov.mec.aghu.exames.agendamento.business;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelHorarioGradeExameDAO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelHorarioGradeExame;
import br.gov.mec.aghu.model.AelTipoMarcacaoExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class HorarioGradeExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(HorarioGradeExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelHorarioGradeExameDAO aelHorarioGradeExameDAO;

	private static final long serialVersionUID = -1610631245766375350L;

	public enum HorarioGradeExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00703, AEL_00516, AEL_00517, AEL_00518, AEL_00519,AEL_00369,AEL_00702, MSG_ERRO_INFORMADO_NUM_HORARIO_E_HORA_FIM, AEL_HORARIO_GRADE_EXAME_CHAVE_EXISTE,
		MSG_ERRO_INFORMADO_NUM_HORARIO_ZERO,
		;
		
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
		
	}
	
	public void atualizarHorarioGradeExame(AelHorarioGradeExame horarioGrade) throws ApplicationBusinessException{
		AelHorarioGradeExame horarioGradeOriginal = this.getAelHorarioGradeExameDAO().obterOriginal(horarioGrade);
		
		this.preAtualizarGradeAgendaExame(horarioGrade, horarioGradeOriginal);
		this.getAelHorarioGradeExameDAO().merge(horarioGrade);
		this.getAelHorarioGradeExameDAO().flush();
	}
	
	
	/**
	 * @throws ApplicationBusinessException  
	 * @ORADB aelt_hge_bru
	 */
	private void preAtualizarGradeAgendaExame(AelHorarioGradeExame horarioGrade, AelHorarioGradeExame horarioGradeOriginal) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.verificaDataNumero(horarioGrade.getHoraFim(),horarioGrade.getNumHorario());
		if (CoreUtil.modificados(horarioGrade.getGradeAgendaExame().getId(), horarioGradeOriginal.getGradeAgendaExame().getId())){
			this.verificaGradeExamesAtiva(horarioGrade.getGradeAgendaExame());//A grade/agenda/exames está inativa
		}
		if (CoreUtil.modificados(horarioGrade.getTipoMarcacaoExame().getSeq(), horarioGradeOriginal.getTipoMarcacaoExame().getSeq())){
			this.verificaTipoMarcacaoAtivo(horarioGrade.getTipoMarcacaoExame());// Tipo marcação de exames está inativo
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm");
		if ( !(dateFormat.format(horarioGrade.getCriadoEm()).equals(dateFormat.format(horarioGradeOriginal.getCriadoEm())))
				|| CoreUtil.modificados(horarioGrade.getServidor().getId(), horarioGradeOriginal.getServidor().getId())){
			HorarioGradeExameRNExceptionCode.AEL_00369.throwException();
		}
		this.verificaSobreposicao(horarioGrade);
		
		horarioGrade.setServidorAlterado(servidorLogado);
		horarioGrade.setAlteradoEm(new Date());
	}
	
	public void inserirHorarioGradeExame(AelHorarioGradeExame horarioGrade) throws ApplicationBusinessException{
		this.preInserirHorarioGradeExame(horarioGrade);
		this.getAelHorarioGradeExameDAO().persistir(horarioGrade);
		this.getAelHorarioGradeExameDAO().flush();
	}
	
	/**
	 * @throws ApplicationBusinessException  
	 * @ORADB aelt_hge_bri
	 * 
	 * Deve ser >= a maior data gerada na disponibilidade.  
	 */
	private void preInserirHorarioGradeExame(AelHorarioGradeExame horarioGrade) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.verificaDataNumero(horarioGrade.getHoraFim(),horarioGrade.getNumHorario());
		this.verificaChavePrimaria(horarioGrade);
		this.verificaGradeExamesAtiva(horarioGrade.getGradeAgendaExame());
		this.verificaTipoMarcacaoAtivo(horarioGrade.getTipoMarcacaoExame());
		this.verificaSobreposicao(horarioGrade);
		
		Date dataCorrente = new Date();
		horarioGrade.setServidor(servidorLogado);
		horarioGrade.setServidorAlterado(servidorLogado);
		horarioGrade.setCriadoEm(dataCorrente);
		horarioGrade.setAlteradoEm(dataCorrente);
	}
	
	private void verificaChavePrimaria(AelHorarioGradeExame horarioGrade) throws ApplicationBusinessException{
		AelHorarioGradeExame horarioGradeExistente = this.getAelHorarioGradeExameDAO().obterPorChavePrimaria(horarioGrade.getId());
		if (horarioGradeExistente != null){
			HorarioGradeExameRNExceptionCode.AEL_HORARIO_GRADE_EXAME_CHAVE_EXISTE.throwException();
		}
	}

   /**
    * @ORADB: AELK_HGE_RN.RN_HGEP_VER_SOBREPOS
    *  Não permite horários se sobreporem para uma grade num mesmo dia da semana.
    *  
    *  @throws ApplicationBusinessException 
    */
	public void verificaSobreposicao(AelHorarioGradeExame horario) throws ApplicationBusinessException{
		
		Date ultimaHora = null;
		Date ultimaHoraCorrente = null;
		List<AelHorarioGradeExame> listaHorariosGrade = this.getAelHorarioGradeExameDAO()
			.pesquisarHorarioGradeExameAtivo(horario.getId().getGaeUnfSeq(), horario.getId().getGaeSeqp(), horario.getId().getDiaSemana());
		
		if (horario.getHoraFim() == null){
			ultimaHoraCorrente = obterUltimoHorario(horario);
		}else{
			ultimaHoraCorrente = (Date) horario.getHoraFim().clone();
		}
		
		for (AelHorarioGradeExame horarioGrade : listaHorariosGrade){
			if (horarioGrade.getHoraFim()==null){ // Carrega a última hora possível dos horários para o dia da semana, para cada horário da grade.
				ultimaHora = obterUltimoHorario(horarioGrade);
			}else{
				ultimaHora = (Date)horarioGrade.getHoraFim().clone();
			}
			
			if(!horarioGrade.getId().equals(horario.getId())){
				// Se horário possui hora fim preenchida, basta verificar se o horário novo encontra-se entre a hora inicio e hora fim
				if (verificaSobrepoeHorarios(horario, horarioGrade, ultimaHoraCorrente, ultimaHora)){
					HorarioGradeExameRNExceptionCode.AEL_00703.throwException();
					return;
				}
			}
		}			
	}
	
	private Boolean verificaSobrepoeHorarios(AelHorarioGradeExame horario, AelHorarioGradeExame horarioGrade, Date ultimaHoraCorrente, Date ultimaHora){
		
		Boolean retorno = Boolean.FALSE;
		
		Calendar calAux = Calendar.getInstance();
		Calendar calHoraInicioCorrente = Calendar.getInstance();
		Calendar calHoraInicioExistenteGrade = Calendar.getInstance();
		Calendar calHoraFimCorrente = Calendar.getInstance();
		Calendar calHoraFimExistenteGrade = Calendar.getInstance();
		
		calAux.setTime(horario.getId().getHoraInicio());
		calHoraInicioCorrente.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
		calHoraInicioCorrente.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
		calHoraInicioCorrente.set(Calendar.SECOND, 0);
		calHoraInicioCorrente.set(Calendar.MILLISECOND, 0);
		
		calAux.setTime(horarioGrade.getId().getHoraInicio());
		calHoraInicioExistenteGrade.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
		calHoraInicioExistenteGrade.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
		calHoraInicioExistenteGrade.set(Calendar.SECOND, 0);
		calHoraInicioExistenteGrade.set(Calendar.MILLISECOND, 0);
		
		calAux.setTime(ultimaHoraCorrente);
		calHoraFimCorrente.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
		calHoraFimCorrente.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
		calHoraFimCorrente.set(Calendar.SECOND, 0);
		calHoraFimCorrente.set(Calendar.MILLISECOND, 0);
		
		calAux.setTime(ultimaHora);
		calHoraFimExistenteGrade.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
		calHoraFimExistenteGrade.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
		calHoraFimExistenteGrade.set(Calendar.SECOND, 0);
		calHoraFimExistenteGrade.set(Calendar.MILLISECOND, 0);
		//Verifica se a hora inicio corrente está entre algum horario já existente na grade
		if (calHoraFimCorrente!=null 
				&& calHoraInicioCorrente.getTime().before(calHoraFimExistenteGrade.getTime()) 
					&& calHoraFimCorrente.getTime().after(calHoraInicioExistenteGrade.getTime())){
			retorno = Boolean.TRUE;
		}
		return retorno;
	}
   /**
    * @ORADB: AELK_HGE_RN.HGEC_VER_ULT_HR
    *  Verifica ultimo horario
    */
	public Date obterUltimoHorario(AelHorarioGradeExame horario) {
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

   /**
    * @ORADB: AELK_HGE_RN.RN_HGEP_VER_GRADE
    *  Verifica se a grade de exames está ativa
    *  
    * @throws ApplicationBusinessException 
    */
	public void verificaGradeExamesAtiva(AelGradeAgendaExame grade) throws ApplicationBusinessException{
		if (grade == null){
			HorarioGradeExameRNExceptionCode.AEL_00516.throwException();//Grade/agenda/exames não encontrada
		}else if(!grade.getSituacao().isAtivo()){
			HorarioGradeExameRNExceptionCode.AEL_00517.throwException();//Grade/agenda/exames está inativa
		}
	}

   /**
    * @ORADB: AELK_HGE_RN.RN_HGEP_VER_TP_MARCA
    *  Verifica se o tipo de marcação de exames está ativo
    *  
    * @throws ApplicationBusinessException 
    */
	private void verificaTipoMarcacaoAtivo(AelTipoMarcacaoExame tipoMarcacaoExame) throws ApplicationBusinessException{
		if (tipoMarcacaoExame == null){
			HorarioGradeExameRNExceptionCode.AEL_00518.throwException();//Tipos marcação exames não encontrado
		}else if(!tipoMarcacaoExame.getIndSituacao().isAtivo()){
			HorarioGradeExameRNExceptionCode.AEL_00519.throwException();//Tipos marcação exames está inativo
		}
	}
	
	/**
	 * ORADB: AACK_HGC_RN.RN_HGCP_VER_DT_NRO
	 */
	public void verificaDataNumero(Date horaFim, Short numHorarios)
			throws ApplicationBusinessException {

		//Data fim e nro de horários não podem ser informados ao mesmo tempo.
		if (horaFim != null && numHorarios != null) {
			HorarioGradeExameRNExceptionCode.MSG_ERRO_INFORMADO_NUM_HORARIO_E_HORA_FIM.throwException();
		}
		
		//deve ser informado obrigatoriamente hora fim ou nro de horarios para 
		//efetuar calculo de horarios
		if (horaFim == null && numHorarios == null) {
			HorarioGradeExameRNExceptionCode.AEL_00702.throwException();
		}
		
		if (numHorarios != null && numHorarios <= 0) {
			HorarioGradeExameRNExceptionCode.MSG_ERRO_INFORMADO_NUM_HORARIO_ZERO.throwException();
		}
	}
	
	protected AelHorarioGradeExameDAO getAelHorarioGradeExameDAO(){
		return aelHorarioGradeExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
