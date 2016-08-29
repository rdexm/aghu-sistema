package br.gov.mec.aghu.exames.agendamento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelGradeAgendaExameDAO;
import br.gov.mec.aghu.exames.dao.AelHorarioGradeExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.model.AelHorarioExameDispId;
import br.gov.mec.aghu.model.AelHorarioGradeExame;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class GradeAgendaExameRN  extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GradeAgendaExameRN.class);
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	private AelGradeAgendaExame gradeAgendamentoExame;

	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGradeAgendaExameDAO aelGradeAgendaExameDAO;
	
	@Inject
	private AelHorarioGradeExameDAO aelHorarioGradeExameDAO;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private static final long serialVersionUID = 4114244438569371372L;

	public enum GradeAgendaExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00370, AEL_00371, AEL_00678, AEL_00679, AEL_00555, AEL_00831,
		AEL_00564, AEL_00504, AEL_00563, AEL_00369, MSG_ERRO_SOBREPOSICAO_INDEVIDA_DE_HORARIOS,
		ERRO_AEL_00506, ERRO_AEL_00506_DATA, ERRO_AEL_00506_MOTIVO, ERRO_AEL_00506_DATA_MOTIVO;
		
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	public void inserirGradeAgendaExame(AelGradeAgendaExame grade) throws ApplicationBusinessException{
		this.preInserirGradeAgendaExame(grade);
		this.getAelGradeAgendaExameDAO().persistir(grade);
		this.getAelGradeAgendaExameDAO().flush();
	}
	
	public void atualizarGradeAgendaExame(AelGradeAgendaExame grade) throws ApplicationBusinessException{
		AelGradeAgendaExame gradeOriginal = getAelGradeAgendaExameDAO().obterOriginal(grade);
		this.preAtualizarGradeAgendaExame(grade, gradeOriginal);
		this.getAelGradeAgendaExameDAO().merge(grade);
		this.getAelGradeAgendaExameDAO().flush();
	}
	
	/**
	 * @throws ApplicationBusinessException  
	 * @ORADB AELT_GAE_BRI
	 * 
	 * Deve ser >= a maior data gerada na disponibilidade.  
	 */
	private void preInserirGradeAgendaExame(AelGradeAgendaExame grade) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		grade.setServidorDigitado(servidorLogado);
		
		grade.setUnidadeFuncional(aghuFacade.obterAghUnidFuncionaisPeloId(grade.getUnidadeFuncional().getSeq()));
		
		this.verificarUnidadeFuncional(grade.getUnidadeFuncional());
		this.verificarSalaAtivaUnidade(grade.getSalaExecutoraExames(),grade.getUnidadeFuncional());
		this.verificarGrupoExame(grade.getGrupoExame(), grade.getUnidadeFuncional());
		this.verificarUnidadeExecutaExames(grade.getExame(), grade.getUnidadeFuncional());
		
		grade.setCriadoEm(new Date());
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB AELT_GAE_BRU
	 * 
	 * Deve ser >= a maior data gerada na disponibilidade.  
	 */
	private void preAtualizarGradeAgendaExame(AelGradeAgendaExame grade, AelGradeAgendaExame gradeOriginal) throws ApplicationBusinessException{
		
		if (CoreUtil.modificados(grade.getId().getUnfSeq(), gradeOriginal.getId().getUnfSeq())){
			this.verificarUnidadeFuncional(grade.getUnidadeFuncional());
		}
		if (CoreUtil.modificados(grade.getSalaExecutoraExames(), gradeOriginal.getSalaExecutoraExames())){
			this.verificarSalaAtivaUnidade(grade.getSalaExecutoraExames(),grade.getUnidadeFuncional());
		}
		
		if (CoreUtil.modificados(grade.getUnidadeFuncional(), gradeOriginal.getUnidadeFuncional())
				||(CoreUtil.modificados(grade.getSituacao(), gradeOriginal.getSituacao())
						&& grade.getSituacao().isAtivo())){

			if (grade.getGrupoExame()!=null){
				if (gradeOriginal.getGrupoExame()==null || CoreUtil.modificados(grade.getGrupoExame(), gradeOriginal.getGrupoExame())){
					this.verificarGrupoExame(grade.getGrupoExame(), grade.getUnidadeFuncional());
				}
			}
			
			if (grade.getExame() !=null){
				if ((gradeOriginal.getExame() == null)
						|| CoreUtil.modificados(grade.getExame().getId(),gradeOriginal.getExame().getId())) {
					this.verificarUnidadeExecutaExames(grade.getExame(), grade.getUnidadeFuncional());
				}
			}
		}
		
		if (CoreUtil.modificados(new Date(grade.getCriadoEm().getTime()).toString(), new Date(gradeOriginal.getCriadoEm().getTime()).toString())
				||CoreUtil.modificados(grade.getServidorDigitado().getId(), gradeOriginal.getServidorDigitado().getId())){
			GradeAgendaExameRNExceptionCode.AEL_00369.throwException();
		}
	}
	
	/**
	 * @throws BaseException 
	 * @ORADB AELK_GAE_RN.RN_GAEP_GERA_DISP_HR
	 * 
	 * Gera disponibilidade de Horarios 
	 * 
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	//@TransactionTimeout(1800)
	public Integer gerarDisponibilidadeHorarios(AelGradeAgendaExame grade, Date dataInicio, Date dataFim) throws BaseException{
		Calendar dataAtualCal = Calendar.getInstance();
		dataAtualCal.setTime(DateUtils.truncate(dataInicio, Calendar.DATE));
		
		Calendar dataBaseCal = Calendar.getInstance();
		dataBaseCal.setTime(DateUtils.truncate(dataInicio, Calendar.DATE));

		Integer qtdGerada=0;
		Integer count = 0;
		
		while (!dataBaseCal.getTime().after(dataFim) && count < 7){
			List<AelHorarioGradeExame> horarioGradeList = getAelHorarioGradeExameDAO()
				.pesquisarHorarioGradeExameAtivo(grade.getId().getUnfSeq(),grade.getId().getSeqp(), dataBaseCal.getTime());
		
			for(AelHorarioGradeExame horario : horarioGradeList){
				while (!dataAtualCal.getTime().after(dataFim)){
					qtdGerada = this.criarHoras(horario, dataAtualCal.getTime(), dataInicio, dataFim, qtdGerada);
					dataAtualCal.add(Calendar.DATE, 7);
				}
				dataAtualCal.setTime(dataBaseCal.getTime());
			}
			dataBaseCal.add(Calendar.DATE, 1);
			dataAtualCal.setTime(dataBaseCal.getTime());
			count++;
		}
		
		if (grade.getDataUltimaGeracao() == null 
				|| DateUtils.truncate(dataFim, Calendar.DATE).after(DateUtils.truncate(grade.getDataUltimaGeracao(), Calendar.DATE))){
			grade.setDataUltimaGeracao(DateUtils.truncate(dataFim, Calendar.DATE));
			this.atualizarGradeAgendaExame(grade);
		}
		return qtdGerada;
	}
	
	/**

	 * @throws 

	 * @ORADB AEL_HORARIO_EXAME_DISPS. AELK_GAE_RN.RN_GAEP_CRIA_HORAS

	 * 

	 * Recupera a data da ultima geração de consulta.  

	 */

	public 	Date dataHoraUltimaGrade() {    //

		if (gradeAgendamentoExame != null && gradeAgendamentoExame.getUnidadeFuncional() != null
			&& gradeAgendamentoExame.getId() != null) {
			
			return agendamentoExamesFacade.obterHorarioExameDataMaisRecentePorGrade(
					gradeAgendamentoExame.getUnidadeFuncional().getSeq()
					, gradeAgendamentoExame.getId().getSeqp());
		}

		return null;
	}

		   
	/**
	 * @throws BaseException 
	 * @ORADB AELK_GAE_RN.RN_GAEP_CRIA_HORAS
	 * 
	 * Gerar a disponibilidade de horários de uma grade de exames  
	 */
	private Integer criarHoras(AelHorarioGradeExame horario, Date dataAtual,
			Date dataInicio, Date dataFim, Integer quantidadeGerada) throws BaseException {
		
		Calendar dthrAtualCal = Calendar.getInstance();
		dthrAtualCal.setTime(dataAtual);
		
		Calendar hrAuxCal = Calendar.getInstance();
		hrAuxCal.setTime(horario.getId().getHoraInicio());
		dthrAtualCal.set(Calendar.HOUR_OF_DAY, hrAuxCal.get(Calendar.HOUR_OF_DAY));
		dthrAtualCal.set(Calendar.MINUTE, hrAuxCal.get(Calendar.MINUTE));
		dthrAtualCal.set(Calendar.SECOND, hrAuxCal.get(Calendar.SECOND));
		dthrAtualCal.set(Calendar.MILLISECOND, hrAuxCal.get(Calendar.MILLISECOND));
		
		Calendar dthrAnteriorCal = (Calendar)dthrAtualCal.clone();
		//Verificar porque foi tirada essa regra do outro. v_dthr_atual := v_dthr_atual + v_horas_dias;
		//dthrAtualCal.add(Calendar.DAY_OF_MONTH, 1);
		
		Calendar duracao = Calendar.getInstance();
		duracao.setTime(horario.getTempoEntreHorarios());
		
		if (horario.getHoraFim() != null){
			Calendar dthrFimCal = Calendar.getInstance();
			dthrFimCal.setTime(dataAtual);
			
			hrAuxCal.setTime(horario.getHoraFim());
			dthrFimCal.set(Calendar.HOUR_OF_DAY, hrAuxCal.get(Calendar.HOUR_OF_DAY));
			dthrFimCal.set(Calendar.MINUTE, hrAuxCal.get(Calendar.MINUTE));
			dthrFimCal.set(Calendar.SECOND, hrAuxCal.get(Calendar.SECOND));
			dthrFimCal.set(Calendar.MILLISECOND, hrAuxCal.get(Calendar.MILLISECOND));

			while(dthrAtualCal.before(dthrFimCal)){
				quantidadeGerada = this.criarHorarioExameDisponivel(dthrAtualCal, dthrAnteriorCal,horario,quantidadeGerada, duracao);
			}
		}else{
			for (Integer i = 1; i<=horario.getNumHorario(); i++){
				quantidadeGerada = this.criarHorarioExameDisponivel(dthrAtualCal, dthrAnteriorCal,horario,quantidadeGerada, duracao);
			}
		}
		return quantidadeGerada;
	}
		
	private Integer criarHorarioExameDisponivel(Calendar dthrAtualCal,
			Calendar dthrAnteriorCal, AelHorarioGradeExame horario,
			Integer quantidadeGerada, Calendar duracao) throws BaseException{
				
		Boolean indGeraTurno = false;
		
		AghFeriados feriado = getAghuFacade().obterFeriado(dthrAtualCal.getTime()); 
		
		if (feriado!= null && feriado.getTurno()!=null){
			if(feriado.getTurno() == DominioTurno.T && (dthrAtualCal.get(Calendar.AM_PM) == Calendar.AM)){
				indGeraTurno = true;
			}else if (feriado.getTurno() == DominioTurno.M && (dthrAtualCal.get(Calendar.AM_PM) == Calendar.PM)){
				indGeraTurno = true;
			}
		}

		if (feriado == null || indGeraTurno){
			
			AelHorarioExameDispId horarioExameDisponivelId = new AelHorarioExameDispId();
			horarioExameDisponivelId.setGaeUnfSeq(horario.getId().getGaeUnfSeq());
			horarioExameDisponivelId.setGaeSeqp(horario.getId().getGaeSeqp());
			horarioExameDisponivelId.setDthrAgenda(dthrAtualCal.getTime());
			
			AelHorarioExameDisp horarioExameDisponivel = new AelHorarioExameDisp();
			horarioExameDisponivel.setId(horarioExameDisponivelId);
			horarioExameDisponivel.setTipoMarcacaoExame(horario.getTipoMarcacaoExame());
			horarioExameDisponivel.setSituacaoHorario(DominioSituacaoHorario.G);
			horarioExameDisponivel.setIndHorarioExtra(Boolean.FALSE);
			horarioExameDisponivel.setExclusivoExecutor(horario.getExclusivoExecutor());
			horarioExameDisponivel.setGradeAgendaExame(horario.getGradeAgendaExame());
			try{
				getExamesFacade().inserirHorarioExameSemFlush(horarioExameDisponivel);
			}catch (PersistenceException e){
				if (e.getCause() instanceof NonUniqueObjectException){
					GradeAgendaExameRNExceptionCode.MSG_ERRO_SOBREPOSICAO_INDEVIDA_DE_HORARIOS.throwException(); 
				}
			}
			
			quantidadeGerada++;
		}
		
		dthrAnteriorCal = (Calendar) dthrAtualCal.clone();
		dthrAtualCal.add(Calendar.HOUR_OF_DAY, duracao.get(Calendar.HOUR_OF_DAY));
		dthrAtualCal.add(Calendar.MINUTE, duracao.get(Calendar.MINUTE));
		
		return quantidadeGerada;
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB AELK_GAE_RN.RN_GAEP_VER_EXEC_EXM
	 * 
	 * Verifica se a unidade executa de exames está ativa 
	 * As fk's do arco, deverão conter a unidade funcional da pk da grade, exceto se grade for por exame e unid da grade for de coleta  
	 * O exame da grade deve permitir agendamento prévio de internação ou agendamento prévio de ambulatório para que se possa gerar uma grade com ele.
	 */
	private void verificarUnidadeExecutaExames(AelUnfExecutaExames exame, AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException{
		
		
		if (exame != null){
			
			AelUnfExecutaExames newExame = aelUnfExecutaExamesDAO.obterPorChavePrimaria(exame.getId(), true, AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE);
			
			if(!DominioSituacao.A.equals(newExame.getIndSituacao())) {
				if(newExame.getDthrReativaTemp() != null && newExame.getMotivoDesativacao() != null) {
					throw new ApplicationBusinessException(
							GradeAgendaExameRNExceptionCode.ERRO_AEL_00506_DATA_MOTIVO,
							newExame.getNomeUsualMaterialUnidade(),
							DateUtil.obterDataFormatada(
									newExame.getDthrReativaTemp(),
									"dd/MM/yyyy"), newExame
									.getMotivoDesativacao());
				} else if (newExame.getDthrReativaTemp() == null
						&& newExame.getMotivoDesativacao() != null) {
					throw new ApplicationBusinessException(
							GradeAgendaExameRNExceptionCode.ERRO_AEL_00506_MOTIVO,
							newExame.getNomeUsualMaterialUnidade(),
							newExame.getMotivoDesativacao());
				} else if (newExame.getDthrReativaTemp() != null
						&& newExame.getMotivoDesativacao() == null) {
					throw new ApplicationBusinessException(
							GradeAgendaExameRNExceptionCode.ERRO_AEL_00506_DATA,
							newExame.getNomeUsualMaterialUnidade(),
							DateUtil.obterDataFormatada(
									newExame.getDthrReativaTemp(),
									"dd/MM/yyyy"));
				} else {
					throw new ApplicationBusinessException(
							GradeAgendaExameRNExceptionCode.ERRO_AEL_00506,
							newExame.getNomeUsualMaterialUnidade());
				}

			}
			if (!verificarCamposAgendaExame(newExame)) {
				//Regra: if v_ind_ag_int not in ('S','R') and v_ind_ag_n_int not in ('S','R') then
				GradeAgendaExameRNExceptionCode.AEL_00831.throwException();//Exame não permite agendamento
			}
			
			boolean possuiCaracteristica = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA);
			if (!unidadeFuncional.getSeq().equals(exame.getId().getUnfSeq().getSeq()) && !possuiCaracteristica){
				GradeAgendaExameRNExceptionCode.AEL_00564.throwException();//O exame deve pertencer a mesma unidade funcional da grade ou unidade funcional de coleta
			}
		}
	}
	
	private Boolean verificarCamposAgendaExame(AelUnfExecutaExames unidadeExecutaExame){
		Boolean agendaExame = Boolean.FALSE;
		if(unidadeExecutaExame.getIndAgendamPrevioInt().equals(DominioSimNaoRestritoAreaExecutora.S) 
				|| unidadeExecutaExame.getIndAgendamPrevioInt().equals(DominioSimNaoRestritoAreaExecutora.R) 
				|| (unidadeExecutaExame.getIndAgendamPrevioNaoInt().equals(DominioSimNaoRestritoAreaExecutora.S)
						|| unidadeExecutaExame.getIndAgendamPrevioNaoInt().equals(DominioSimNaoRestritoAreaExecutora.R))){
			agendaExame = Boolean.TRUE;
		}
		return agendaExame;
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB AELK_GAE_RN.RN_GAEP_VER_GRUPO_EX
	 * 
	 * Verifica se o grupo exame está ativo
	 * As fk's do arco, deverão conter a unidade funcional da pk da grade.  
	 */
	private void verificarGrupoExame(AelGrupoExames grupoExame, AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException{
		if (grupoExame!=null){
			if (!grupoExame.getSituacao().isAtivo()){
				GradeAgendaExameRNExceptionCode.AEL_00504.throwException(); //Grupo de exames está inativo	
			}
			if (!grupoExame.getUnidadeFuncional().getSeq().equals(unidadeFuncional.getSeq())){
				GradeAgendaExameRNExceptionCode.AEL_00563.throwException(); //O grupo deve pertencer a mesma unidade funcional da grade
			}
		}
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB AELK_GAE_RN.RN_GAEP_VER_SALA
	 * 
	 * Verifica se a sala, quando informada, está ativa e se pertence à mesma unidade funcional da grade. 
	 */
	private void verificarSalaAtivaUnidade(AelSalasExecutorasExames salaExecutora,
			AghUnidadesFuncionais unidadeFuncionalGrade) throws ApplicationBusinessException {
	
		if (salaExecutora != null && salaExecutora.getId()!=null && salaExecutora.getId().getUnfSeq()!=null){
			if (!salaExecutora.getUnidadeFuncional().getSeq().equals(unidadeFuncionalGrade.getSeq())){//  A unidade funcional da sala deve ser a mesma da grade
				GradeAgendaExameRNExceptionCode.AEL_00679.throwException();
			}
			if (!salaExecutora.getSituacao().isAtivo()){
				GradeAgendaExameRNExceptionCode.AEL_00555.throwException(salaExecutora.getNumero());// Sala #1 está inativa
			}
		}
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * 
	 * @ORADB AELK_GAE_RN.RN_GAEP_VER_UNID_FUN
	 * 
	 * Verifica se a unidade funcional está ativa e se é unidade executora de exames ou se é unidade de coleta 
	 */
	private void verificarUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException{
		if (unidadeFuncional == null){
			GradeAgendaExameRNExceptionCode.AEL_00370.throwException();
		}else if (!unidadeFuncional.getIndSitUnidFunc().isAtivo()){
			GradeAgendaExameRNExceptionCode.AEL_00371.throwException();
		}
		
		boolean possuiCaracteristica1 = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES);
		boolean possuiCaracteristica2 = aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA);
		
		if (!(possuiCaracteristica1 || possuiCaracteristica2)){
			GradeAgendaExameRNExceptionCode.AEL_00678.throwException();//  Unidade funcional não é unidade executora ou de coleta
		}
	}
	
	protected AelHorarioGradeExameDAO getAelHorarioGradeExameDAO(){
		return aelHorarioGradeExameDAO;
	}
	
	protected AelGradeAgendaExameDAO getAelGradeAgendaExameDAO(){
		return aelGradeAgendaExameDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
