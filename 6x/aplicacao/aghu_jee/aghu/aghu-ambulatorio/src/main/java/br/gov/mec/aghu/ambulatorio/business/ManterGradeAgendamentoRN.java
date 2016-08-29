package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.GerarDisponibilidadeHorarioRN.GerarDisponibilidadeHorarioRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.AacCaracteristicaGradeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacCaracteristicaGradeJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeSituacaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacHorarioGradeConsultaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacHorarioGradeJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacProcedHospEspecialidadesDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacCaracteristicaGrade;
import br.gov.mec.aghu.model.AacCaracteristicaGradeJn;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsJn;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeProcedHospitalar;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacGradeSituacaoId;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AacHorarioGradeJn;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;


/**
 * Implementação das packages e triggers de Manter Grade Agendamento
 * 
 * Tiago Felini
 */

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class ManterGradeAgendamentoRN extends BaseBusiness{

	@EJB
	private AmbulatorioRN ambulatorioRN;
	
	private static final Log LOG = LogFactory.getLog(ManterGradeAgendamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private AacCaracteristicaGradeDAO aacCaracteristicaGradeDAO;
	
	@Inject
	private AacCaracteristicaGradeJnDAO aacCaracteristicaGradeJnDAO;
	
	@Inject
	private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;
	
	@Inject
	private AacGradeAgendamenConsJnDAO aacGradeAgendamenConsJnDAO;
	
	@Inject
	private AacGradeProcedHospitalarDAO aacGradeProcedHospitalarDAO;
	
	@Inject
	private AacProcedHospEspecialidadesDAO aacProcedHospEspecialidadesDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AacHorarioGradeConsultaDAO aacHorarioGradeConsultaDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AacHorarioGradeJnDAO aacHorarioGradeJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;
	
	@Inject
	private AacGradeSituacaoDAO aacGradeSituacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3959718752092474488L;

	public enum ManterGradeAgendamentoRNExceptionCode implements
	BusinessExceptionCode {

		AAC_00069, AAC_00077, AAC_00071, AAC_00074, 
		AAC_00137, AAC_00126, AAC_00217, AAC_00066, 
		AAC_00131, AAC_00075, AAC_00076, AAC_00142, 
		AAC_00613,
		PROCED_HOSP_INTERNO_INATIVO, 
		NAO_EXISTE_PERIODO_ATIVO,
		AAC_00546,
		AAC_00138,
		AAC_00541,
		AAC_00542,
		AAC_00553,
		AAC_00554,
		AAC_00558;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_GRD_ARI
	 * @param: AacGradeAgendamenConsultas gradeAgendamentoConsulta
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void posInserirAacGradeAgendamenConsultas(
			AacGradeAgendamenConsultas gradeAgendamentoConsulta)
			throws ApplicationBusinessException {
		
		if (!gradeAgendamentoConsulta.getProcedimento()){
			verificaProcedimentoEspecialidade(gradeAgendamentoConsulta.getEspecialidade());
		}
	}

	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_GRD_BRI
	 * @param: AacGradeAgendamenConsultas gradeAgendamentoConsulta
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preInserirAacGradeAgendamenConsultas(
			AacGradeAgendamenConsultas gradeAgendamentoConsulta)
			throws ApplicationBusinessException {
		
		if (gradeAgendamentoConsulta.getProfEspecialidade()!=null){
			verificaEspecialidadeProfissional(gradeAgendamentoConsulta
					.getEspecialidade().getSeq(), gradeAgendamentoConsulta
					.getProfEspecialidade().getAghEspecialidade().getSeq());
		}	
		
		verificaUnidadeFuncionalSala(gradeAgendamentoConsulta);
		
		//verifica se Especialidade esta ativa
		verificaSituacaoEspecialidade(gradeAgendamentoConsulta.getEspecialidade());
		
		// verifica se equipe esta ativa
		verificaSituacaoEquipe(gradeAgendamentoConsulta.getEquipe());
		
		// verifica se profissional atua no ambulatorio
		if (gradeAgendamentoConsulta.getProfEspecialidade() != null
				&& gradeAgendamentoConsulta.getProfEspecialidade().getAghEspecialidade() != null) {

			verificaSituacaoEquipeEspecialidade(gradeAgendamentoConsulta
					.getProfEspecialidade().getAghEspecialidade().getSeq(),
					gradeAgendamentoConsulta.getProfEspecialidade().getRapServidor());
		} else {
			verificaEquipeEspecialidade(gradeAgendamentoConsulta.getEquipe(),
					gradeAgendamentoConsulta.getEspecialidade());
		}

		// atualiza servidor
		gradeAgendamentoConsulta
				.setServidor(this.getAmbulatorioRN().atualizaServidor(gradeAgendamentoConsulta
						.getServidor()));
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_GRD_BRU
	 * @param: AacGradeAgendamenConsultas newGradeAgendamentoConsulta, AacGradeAgendamenConsultas oldGradeAgendamentoConsulta
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */

	@SuppressWarnings("PMD.NPathComplexity")
	public void preAtualizarAacGradeAgendamenConsultas(
			AacGradeAgendamenConsultas newGradeAgendamentoConsulta,
			AacGradeAgendamenConsultas oldGradeAgendamentoConsulta)
			throws ApplicationBusinessException {

/*		if (newGradeAgendamentoConsulta.getDtUltimaGeracao() != null
				&& oldGradeAgendamentoConsulta.getDtUltimaGeracao() != null
				&& CoreUtil.modificados(newGradeAgendamentoConsulta
						.getDtUltimaGeracao(), oldGradeAgendamentoConsulta
						.getDtUltimaGeracao())) {

			verificaSituacaoGradeData(newGradeAgendamentoConsulta, new Date());
		}*/

		// verifica esp - profissional
		if (newGradeAgendamentoConsulta.getProfEspecialidade() != null
				&& oldGradeAgendamentoConsulta.getProfEspecialidade() != null
				&& (CoreUtil.modificados(newGradeAgendamentoConsulta
						.getProfEspecialidade().getId().getEspSeq(),
						oldGradeAgendamentoConsulta.getProfEspecialidade()
								.getId().getEspSeq()) || newGradeAgendamentoConsulta
						.getEspecialidade().getSeq() != oldGradeAgendamentoConsulta
						.getEspecialidade().getSeq())) {

			verificaEspecialidadeProfissional(newGradeAgendamentoConsulta
					.getProfEspecialidade().getId().getEspSeq(),
					newGradeAgendamentoConsulta.getEspecialidade().getSeq());
		}

		// verifica se sala esta ativa
		if (newGradeAgendamentoConsulta.getAacUnidFuncionalSala() != null
				&& oldGradeAgendamentoConsulta.getAacUnidFuncionalSala() != null
				&& !newGradeAgendamentoConsulta.getAacUnidFuncionalSala()
						.getId().equals(
								oldGradeAgendamentoConsulta
										.getAacUnidFuncionalSala().getId())) {

			verificaUnidadeFuncionalSala(newGradeAgendamentoConsulta);
		}

		// verifica se especialidade esta ativa
		if (newGradeAgendamentoConsulta.getEspecialidade().getSeq() != oldGradeAgendamentoConsulta
				.getEspecialidade().getSeq()) {
			verificaSituacaoEspecialidade(newGradeAgendamentoConsulta
					.getEspecialidade());
		}

		// verifica se equipe esta ativa
		if (newGradeAgendamentoConsulta.getEquipe().getSeq() != oldGradeAgendamentoConsulta
				.getEquipe().getSeq()) {
			verificaSituacaoEquipe(newGradeAgendamentoConsulta.getEquipe());
		}

		// verifica se profissional atua no ambulatorio

		if (newGradeAgendamentoConsulta.getProfEspecialidade()!=null && CoreUtil.modificados(newGradeAgendamentoConsulta
				.getProfEspecialidade(), oldGradeAgendamentoConsulta
				.getProfEspecialidade())) {
			verificaSituacaoEquipeEspecialidade(newGradeAgendamentoConsulta
					.getProfEspecialidade().getId().getEspSeq(),
					newGradeAgendamentoConsulta.getProfEspecialidade()
							.getRapServidor());
		}

		// verifica se chefe equipe atende especialidade genérica
		if (newGradeAgendamentoConsulta.getEquipe().getSeq() != oldGradeAgendamentoConsulta
				.getEquipe().getSeq()
				|| newGradeAgendamentoConsulta.getEspecialidade().getSeq() != newGradeAgendamentoConsulta
						.getEspecialidade().getSeq()) {

			verificaEquipeEspecialidade(
					newGradeAgendamentoConsulta.getEquipe(),
					newGradeAgendamentoConsulta.getEspecialidade());
		}
		
		if (newGradeAgendamentoConsulta.getEquipe().getSeq() != oldGradeAgendamentoConsulta
				.getEquipe().getSeq()
				|| (newGradeAgendamentoConsulta.getAacUnidFuncionalSala() != null
						&& newGradeAgendamentoConsulta
								.getAacUnidFuncionalSala() != null && newGradeAgendamentoConsulta
						.getAacUnidFuncionalSala().getId().equals(
								oldGradeAgendamentoConsulta
										.getAacUnidFuncionalSala().getId()))
				|| newGradeAgendamentoConsulta.getEspecialidade().getSeq() != newGradeAgendamentoConsulta
						.getEspecialidade().getSeq()
				|| CoreUtil.modificados(newGradeAgendamentoConsulta
						.getProfEspecialidade(), oldGradeAgendamentoConsulta
						.getProfEspecialidade())) {

		
			verificaSituacaoGradeAlteracao(newGradeAgendamentoConsulta);
		}
		
		newGradeAgendamentoConsulta
				.setServidorAlterado(this.getAmbulatorioRN().atualizaServidor(newGradeAgendamentoConsulta
						.getServidor()));
		
		newGradeAgendamentoConsulta.setAlteradoEm(new Date());

	}
			
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_GRD_ASI
	 * @param: AacGradeAgendamenConsultas gradeAgendamentoConsulta
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void posInserirAacGradeAgendamenConsultasEnforce(
			AacGradeAgendamenConsultas gradeAgendamentoConsulta) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(!gradeAgendamentoConsulta.getProgramada()){
			verificaAtendimentoProgramado(gradeAgendamentoConsulta.getAacUnidFuncionalSala().getId().getUnfSeq(), gradeAgendamentoConsulta.getEspecialidade().getSeq());
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		AacGradeSituacao gradeSituacao = new AacGradeSituacao();
		
		AacGradeSituacaoId id = new AacGradeSituacaoId();
		
		id.setGrdSeq(gradeAgendamentoConsulta.getSeq());
		id.setDtInicioSituacao(cal.getTime());
		
		gradeSituacao.setId(id);
		gradeSituacao.setSituacao(DominioSituacao.A);
		gradeSituacao.setGradeAgendamentoConsulta(gradeAgendamentoConsulta);
		gradeSituacao.setCriadoEm(new Date());
		gradeSituacao.setServidor(servidorLogado);
		
		getAmbulatorioFacade().persistirSituacao(gradeSituacao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_HGC_BRI
	 * @param: AacHorarioGradeConsulta horarioGradeConsulta
	 * @throws ApplicationBusinessException  
	 */
	public void preInserirAacHorarioGradeConsulta(
			AacHorarioGradeConsulta horarioGradeConsulta) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		horarioGradeConsulta.setServidor(servidorLogado);
				
		horarioGradeConsulta.setCriadoEm(new Date());
		
		verificaDataNumero(horarioGradeConsulta.getHoraFim(), horarioGradeConsulta.getNumHorario());

		verificaDuracaoZero(horarioGradeConsulta.getNumHorario(), horarioGradeConsulta.getDuracao());

		horarioGradeConsulta
				.setServidor(this.getAmbulatorioRN().atualizaServidor(horarioGradeConsulta
						.getServidor()));
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_HGC_BRU
	 * @param: AacHorarioGradeConsulta newHorarioGradeConsulta, AacHorarioGradeConsulta oldHorarioGradeConsulta
	 * @throws ApplicationBusinessException  
	 */
	public void preAtualzarAacHorarioGradeConsulta(
			AacHorarioGradeConsulta newHorarioGradeConsulta,
			AacHorarioGradeConsulta oldHorarioGradeConsulta)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		newHorarioGradeConsulta.setAlteradoEm(new Date());
		newHorarioGradeConsulta.setServidor(servidorLogado);
		
		if (newHorarioGradeConsulta.getDiaSemana() != oldHorarioGradeConsulta.getDiaSemana()){
			newHorarioGradeConsulta.setDtUltimaGeracao(null);
		}
		verificaDataNumero(newHorarioGradeConsulta.getHoraFim(), newHorarioGradeConsulta.getNumHorario());

		if (newHorarioGradeConsulta.getDuracao() != oldHorarioGradeConsulta.getDuracao()
				|| CoreUtil.modificados(newHorarioGradeConsulta.getDuracao(),
										oldHorarioGradeConsulta.getDuracao())) {
			
			verificaDuracaoZero(newHorarioGradeConsulta.getNumHorario(),
					newHorarioGradeConsulta.getDuracao());
		}
		
		if (newHorarioGradeConsulta.getFormaAgendamento() != null) {
			verificaFormaAgendamento(newHorarioGradeConsulta.getFormaAgendamento());
		}
		
		newHorarioGradeConsulta
				.setServidorAlterado(this.getAmbulatorioRN().atualizaServidor(newHorarioGradeConsulta
						.getServidorAlterado()));
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_GRD_ARU
	 * @param: AacGradeAgendamenConsultas newGrAgendConsulta,AacGradeAgendamenConsultas oldGrAgendConsulta,
	 * @throws ApplicationBusinessException 
	 */
	public void posAtualizarAacGradeAgendamenConsultasJournal(
			AacGradeAgendamenConsultas newGrAgendConsulta,
			AacGradeAgendamenConsultas oldGrAgendConsulta) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (!newGrAgendConsulta.getProgramada().equals(oldGrAgendConsulta.getProgramada())
				|| (newGrAgendConsulta.getConvenioSus()!=null && oldGrAgendConsulta.getConvenioSus()!=null && !newGrAgendConsulta.getConvenioSus().equals(oldGrAgendConsulta.getConvenioSus()))
				|| !newGrAgendConsulta.getProcedimento().equals(oldGrAgendConsulta.getProcedimento())
				|| !newGrAgendConsulta.getEquipe().getSeq().equals(oldGrAgendConsulta.getEquipe().getSeq())
				|| !newGrAgendConsulta.getEspecialidade().getSeq().equals(oldGrAgendConsulta.getEspecialidade().getSeq())
				|| (newGrAgendConsulta.getAacUnidFuncionalSala().getId()!=null && !newGrAgendConsulta.getAacUnidFuncionalSala().getId().equals(oldGrAgendConsulta.getAacUnidFuncionalSala().getId()))
				|| (newGrAgendConsulta.getProfEspecialidade()!=null && (oldGrAgendConsulta.getProfEspecialidade()==null || !newGrAgendConsulta.getProfEspecialidade().getId().equals(oldGrAgendConsulta.getProfEspecialidade().getId())))
				|| !newGrAgendConsulta.getSeq().equals(oldGrAgendConsulta.getSeq())
				|| !newGrAgendConsulta.getCriadoEm().equals(oldGrAgendConsulta.getCriadoEm())
				|| !newGrAgendConsulta.getExigeProntuario().equals(oldGrAgendConsulta.getExigeProntuario())
				|| !newGrAgendConsulta.getEnviaSamis().equals(oldGrAgendConsulta.getEnviaSamis())
				|| !newGrAgendConsulta.getEmiteTicket().equals(oldGrAgendConsulta.getEmiteTicket())
				|| !newGrAgendConsulta.getEmiteBa().equals(oldGrAgendConsulta.getEmiteBa())
				|| !newGrAgendConsulta.getIndAvisaConsultaTurno().equals(oldGrAgendConsulta.getIndAvisaConsultaTurno())
				|| !newGrAgendConsulta.getServidor().getId().equals(oldGrAgendConsulta.getServidor().getId())
				|| (newGrAgendConsulta.getServidorAlterado()!=null && (oldGrAgendConsulta.getServidorAlterado()==null || !newGrAgendConsulta.getServidorAlterado().getId().equals(oldGrAgendConsulta.getServidorAlterado().getId())))
				|| !newGrAgendConsulta.getAlteradoEm().equals(oldGrAgendConsulta.getAlteradoEm())
		){
			AacGradeAgendamenConsJn gradeJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AacGradeAgendamenConsJn.class, servidorLogado.getUsuario());
			
			gradeJn.setSeq(oldGrAgendConsulta.getSeq());
			gradeJn.setCriadoEm(oldGrAgendConsulta.getCriadoEm());
			gradeJn.setProgramada(oldGrAgendConsulta.getProgramada());
			gradeJn.setExigeProntuario(oldGrAgendConsulta.getExigeProntuario());
			gradeJn.setConvenioSus(oldGrAgendConsulta.getConvenioSus());
			gradeJn.setEnviaSamis(oldGrAgendConsulta.getEnviaSamis());
			gradeJn.setEmiteTicket(oldGrAgendConsulta.getEmiteTicket());
			gradeJn.setEmiteBa(oldGrAgendConsulta.getEmiteBa());
			gradeJn.setProcedimento(oldGrAgendConsulta.getProcedimento());
			gradeJn.setIndAvisaConsultaTurno(oldGrAgendConsulta.getIndAvisaConsultaTurno());
			gradeJn.setEspecialidade(oldGrAgendConsulta.getEspecialidade());
			gradeJn.setEquipe(oldGrAgendConsulta.getEquipe());
			gradeJn.setServidor(oldGrAgendConsulta.getServidor());
			gradeJn.setDtUltimaGeracao(oldGrAgendConsulta.getDtUltimaGeracao());
			gradeJn.setProfEspecialidade(oldGrAgendConsulta.getProfEspecialidade());
			gradeJn.setAacUnidFuncionalSala(oldGrAgendConsulta.getAacUnidFuncionalSala());
			gradeJn.setServidorAlterado(oldGrAgendConsulta.getServidorAlterado());
			gradeJn.setAlteradoEm(oldGrAgendConsulta.getAlteradoEm());
			gradeJn.setPagador(oldGrAgendConsulta.getPagador());
			gradeJn.setTipoAgendamento(oldGrAgendConsulta.getTipoAgendamento());
			gradeJn.setCondicaoAtendimento(oldGrAgendConsulta.getCondicaoAtendimento());
			gradeJn.setTempoAtendAnterior(oldGrAgendConsulta.getTempoAtendAnterior());

			getAacGradeAgendamenConsJnDAO().persistirGradeAgendamenConsJn(gradeJn);
		}
	}
	
	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_HGC_ARD
	 * @param: AacHorarioGradeConsulta horarioGrade
	 * @throws ApplicationBusinessException 
	 */
	
	public void posDeleteAacHorarioGradeConsultaJournal(
			AacHorarioGradeConsulta horarioGrade)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AacHorarioGradeJn horarioGradeJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AacHorarioGradeJn.class, servidorLogado.getUsuario());
		
		horarioGradeJn.setCriadoEm(horarioGrade.getCriadoEm());
		horarioGradeJn.setSituacao(horarioGrade.getSituacao());
		horarioGradeJn.setServidor(horarioGrade.getServidor());
		horarioGradeJn.setServidorAlterado(horarioGrade.getServidorAlterado());
		horarioGradeJn.setAlteradoEm(horarioGrade.getAlteradoEm());
		horarioGradeJn.setFormaAgendamento(horarioGrade.getFormaAgendamento());
		horarioGradeJn.setDiaSemana(horarioGrade.getDiaSemana());
		horarioGradeJn.setGrdSeq(horarioGrade.getId().getGrdSeq());
		horarioGradeJn.setSeqp(horarioGrade.getId().getSeqp());
		horarioGradeJn.setHoraInicio(horarioGrade.getHoraInicio());
		horarioGradeJn.setTpsTipo(horarioGrade.getTpsTipo());
		horarioGradeJn.setDuracao(horarioGrade.getDuracao());
		horarioGradeJn.setNumHorario(horarioGrade.getNumHorario());
		
		getAacHorarioGradeJnDAO().persistirHorarioGradeJn(horarioGradeJn);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_HGC_ARU
	 * @param: AacHorarioGradeConsulta newHorarioGrade, AacHorarioGradeConsulta oldHorarioGrade
	 * @throws ApplicationBusinessException 
	 */
	
	public void posUpdateAacHorarioGradeConsultaJournal(
			AacHorarioGradeConsulta newHorarioGrade, AacHorarioGradeConsulta oldHorarioGrade)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (!newHorarioGrade.getId().equals(oldHorarioGrade.getId())
				|| !newHorarioGrade.getDiaSemana().equals(oldHorarioGrade.getDiaSemana())
				|| !newHorarioGrade.getHoraInicio().equals(oldHorarioGrade.getHoraInicio())
				|| !newHorarioGrade.getSituacao().equals(oldHorarioGrade.getSituacao())
				|| !newHorarioGrade.getCriadoEm().equals(oldHorarioGrade.getCriadoEm())
				|| !newHorarioGrade.getServidor().getId().equals(oldHorarioGrade.getServidor().getId())
				|| !newHorarioGrade.getDuracao().equals(oldHorarioGrade.getDuracao())
				|| (newHorarioGrade.getNumHorario()!=null && !newHorarioGrade.getNumHorario().equals(oldHorarioGrade.getNumHorario()))
				|| (newHorarioGrade.getHoraFim()!= null && !newHorarioGrade.getHoraFim().equals(oldHorarioGrade.getHoraFim()))
				|| !newHorarioGrade.getAlteradoEm().equals(oldHorarioGrade.getAlteradoEm())
				|| !newHorarioGrade.getServidorAlterado().equals(oldHorarioGrade.getServidorAlterado())
				|| !newHorarioGrade.getFormaAgendamento().getId().equals(oldHorarioGrade.getFormaAgendamento().getId())
		){
			AacHorarioGradeJn horarioGradeJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AacHorarioGradeJn.class, (servidorLogado != null ? servidorLogado
					.getUsuario() : null));
			horarioGradeJn.setCriadoEm(oldHorarioGrade.getCriadoEm());
			horarioGradeJn.setSituacao(oldHorarioGrade.getSituacao());
			horarioGradeJn.setServidor(oldHorarioGrade.getServidor());
			horarioGradeJn.setServidorAlterado(oldHorarioGrade.getServidorAlterado());
			horarioGradeJn.setAlteradoEm(oldHorarioGrade.getAlteradoEm());
			horarioGradeJn.setFormaAgendamento(oldHorarioGrade.getFormaAgendamento());
			horarioGradeJn.setDiaSemana(oldHorarioGrade.getDiaSemana());
			horarioGradeJn.setGrdSeq(oldHorarioGrade.getId().getGrdSeq());
			horarioGradeJn.setSeqp(oldHorarioGrade.getId().getSeqp());
			horarioGradeJn.setHoraInicio(oldHorarioGrade.getHoraInicio());
			horarioGradeJn.setTpsTipo(oldHorarioGrade.getTpsTipo());
			horarioGradeJn.setDuracao(oldHorarioGrade.getDuracao());
			horarioGradeJn.setNumHorario(oldHorarioGrade.getNumHorario());
			
			getAacHorarioGradeJnDAO().persistirHorarioGradeJn(horarioGradeJn);
		}
	}
	
	
	
	
	/**
	 * Trigger
	 * 
	 * ORADB: AACT_GST_BRI
	 * @throws ApplicationBusinessException 
	 */
	public void preInserirGradeSituacoes(AacGradeSituacao gradeSituacao)
			throws ApplicationBusinessException {
		this.getAmbulatorioRN().atualizaServidor(gradeSituacao.getServidor());
		if (gradeSituacao.getSituacao().equals(DominioSituacao.I)) {
			this.verificarConsultaMarcadaNovoPeriodo(gradeSituacao);
		}
		this.ativarInativarGrade(gradeSituacao);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: AACK_GST_RN.RN_GSTP_VER_GRD_DUPL
	 */
	public void posInserirGradeSituacoes(AacGradeSituacao gradeSituacao)
			throws ApplicationBusinessException {
		Long conta = 0l;
		AacGradeAgendamenConsultas grade = gradeSituacao.getGradeAgendamentoConsulta();
		if(grade.getServidor().getId().getMatricula()!=null){
			List<AacGradeAgendamenConsultas> listaAgendamento = this.getAacGradeAgendamenConsultasDAO().listarAgendamentoConsultasSituacao(grade);
			for(AacGradeAgendamenConsultas gradeAgendamento:listaAgendamento){
				if(this.verificarSituacaoGrade(gradeAgendamento.getSeq(), gradeSituacao.getId().getDtInicioSituacao()).equals(DominioSituacao.A)){
					conta++;
				}
			}
			//este if esta diferente da procedure devido ao java persistir o objeto apos o flush
			if(conta>0){
				throw new ApplicationBusinessException(ManterGradeAgendamentoRNExceptionCode.AAC_00138);
			}
			conta = 0l;
			if(gradeSituacao.getDtFimSituacao()==null){
				conta = this.getAacGradeSituacaoDAO().listarGradeSituacaoComProfCount(gradeSituacao);
				if(conta>0){
					throw new ApplicationBusinessException(ManterGradeAgendamentoRNExceptionCode.AAC_00558);
				}
			}
		}
		else {
			List<AacGradeAgendamenConsultas> listaGradeEquipe = this.getAacGradeAgendamenConsultasDAO().listarAgendamentoConsultasEquipe(grade);
			for(AacGradeAgendamenConsultas gradeAgendamento:listaGradeEquipe){
				if(this.verificarSituacaoGrade(gradeAgendamento.getSeq(), gradeSituacao.getId().getDtInicioSituacao()).equals(DominioSituacao.A)){
					conta++;
				}
			}
			//este if esta diferente da procedure devido ao java persistir o objeto apos o flush
			if(conta>0){
				throw new ApplicationBusinessException(ManterGradeAgendamentoRNExceptionCode.AAC_00138);
			}
			if(gradeSituacao.getDtFimSituacao()==null){
				conta = this.getAacGradeSituacaoDAO().listarGradeSituacaoSemProfCount(gradeSituacao);
				if(conta>0){
					throw new ApplicationBusinessException(ManterGradeAgendamentoRNExceptionCode.AAC_00558);
				}
			}
		}

	}
	
	/**
	 * FUNCTION
	 * 
	 * ORADB: AACC_VER_SIT_GRADE
	 *
	 */
	public DominioSituacao verificarSituacaoGrade(Integer grdSeq, Date data){
		List<AacGradeSituacao> lista = this.getAacGradeSituacaoDAO().listarVerificaSituacaoGrade(grdSeq, data);
		if(lista.size()==0){
			return DominioSituacao.I;
		} else {
			return DominioSituacao.A;
		}
	}


	/**
	 * 
	 * ORADB: AACK_GST_RN.RN_GSTP_VER_DELECAO
	 * @param gradeSituacao
	 * @throws ApplicationBusinessException
	 * 
	 *  Exclui periodo de uma grade 
	 */
	public void verificarRemocaoGradeSituacao(
			AacGradeSituacao gradeSituacao)
			throws ApplicationBusinessException {
		List<AacGradeSituacao> listaGradeSituacao =  this.getAacGradeSituacaoDAO().listarSituacaoGradeDataMaior(gradeSituacao.getGradeAgendamentoConsulta().getSeq(), gradeSituacao.getId().getDtInicioSituacao());
		if(listaGradeSituacao.size()>0){
			throw new ApplicationBusinessException(
					ManterGradeAgendamentoRNExceptionCode.AAC_00553);
		}
		Long count = this.getAacGradeSituacaoDAO().obterCountListaSituacaoGrade(gradeSituacao.getGradeAgendamentoConsulta().getSeq());
		if(count<=1){
			throw new ApplicationBusinessException(
					ManterGradeAgendamentoRNExceptionCode.AAC_00554);
		}
	}
	
	
	/**
	 * ORADB: AACK_GST_RN.RN_GSTP_EXCLUI_PER
	 * @param gradeSituacao
	 * @throws ApplicationBusinessException
	 * 
	 * Exclui um periodo de uma grade
	 */
	public void removerPeriodoGrade(
			AacGradeSituacao gradeSituacao)
			throws ApplicationBusinessException {
		List<AacGradeSituacao> listaGrade = this.getAacGradeSituacaoDAO().listarSituacaoGradePorSituacaoDiferente(gradeSituacao.getGradeAgendamentoConsulta().getSeq(), gradeSituacao.getSituacao());
		for(AacGradeSituacao grade: listaGrade){
			Date dataAux = DateUtil.adicionaDias(grade.getDtFimSituacao(), 1);
			if(DateValidator.validarMesmoDia(gradeSituacao.getId().getDtInicioSituacao(), dataAux)){
				this.atualizarPeriodoGrade(grade);
			}
		}
	}
	
	public void atualizarPeriodoGrade(
			AacGradeSituacao gradeSituacao)
			throws ApplicationBusinessException {
		List<AacGradeSituacao> listaGrade = this.getAacGradeSituacaoDAO().listarSituacaoGradePorDataESituacao(gradeSituacao.getGradeAgendamentoConsulta().getSeq(), gradeSituacao.getId().getDtInicioSituacao(), gradeSituacao.getSituacao());
		for(AacGradeSituacao grade: listaGrade){
			grade.setDtFimSituacao(null);
			this.getAacGradeSituacaoDAO().atualizar(grade);
			this.getAacGradeSituacaoDAO().flush();
		}
		
	}
	
	
	/**
	 * Verifica se existe consulta marcada no novo período
	 * 
	 * ORADB: AACK_GST_RN.RN_GSTP_VER_PER
	 * 
	 */
	public void verificarConsultaMarcadaNovoPeriodo(AacGradeSituacao gradeSituacao) throws ApplicationBusinessException {
		List<AacConsultas> listaConsultas = this.getAacConsultasDAO().listarConsultasMarcadasNovoPeriodo(gradeSituacao.getGradeAgendamentoConsulta().getSeq(), gradeSituacao.getId().getDtInicioSituacao());
		if(listaConsultas.size()!=0){
			throw new ApplicationBusinessException(
					ManterGradeAgendamentoRNExceptionCode.AAC_00546);
		}
	}
	
	/**
	 * Ativar/Inativar a grade
	 * 
	 * ORADB: AACK_GST_RN.RN_GSTP_ATU_GRD
	 * 
	 */
	public void ativarInativarGrade(AacGradeSituacao gradeSituacao) throws ApplicationBusinessException {
		List<AacGradeSituacao> listaGradeSituacao = new ArrayList<AacGradeSituacao>(); 
		listaGradeSituacao = this.getAacGradeSituacaoDAO().listarSituacaoGradePorDataDecrescente(gradeSituacao.getGradeAgendamentoConsulta().getSeq());
		if(listaGradeSituacao.size()>0){
			AacGradeSituacao gradeSituacaoRetorno=listaGradeSituacao.get(0);
			if(DateUtil.validaDataMaiorIgual(gradeSituacaoRetorno.getId().getDtInicioSituacao(), gradeSituacao.getId().getDtInicioSituacao())){
				throw new ApplicationBusinessException(
						ManterGradeAgendamentoRNExceptionCode.AAC_00541);	
			} else if (gradeSituacaoRetorno.getSituacao().equals(gradeSituacao.getSituacao())){
				throw new ApplicationBusinessException(
						ManterGradeAgendamentoRNExceptionCode.AAC_00542);
			} else if (gradeSituacaoRetorno.getDtFimSituacao()==null){
				Date dataFim = DateUtil.adicionaDias(gradeSituacao.getId().getDtInicioSituacao(), -1);
				dataFim = DateUtil.adicionaHoras(dataFim, 23);
				dataFim = DateUtil.adicionaMinutos(dataFim, 59);
				dataFim = DateUtil.adicionaSegundos(dataFim, 59);
				gradeSituacaoRetorno.setDtFimSituacao(dataFim);
				this.getAacGradeSituacaoDAO().atualizar(gradeSituacaoRetorno);
				this.getAacGradeSituacaoDAO().flush();
			}
		}
	}
	
	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_UFSA_AT
	 * @param Short unfSeq, Byte sala
	 * @throws ApplicationBusinessException 
	 */
	public void verificaUnidadeFuncionalSala(
			AacGradeAgendamenConsultas gradeAgendamentoConsulta)
			throws ApplicationBusinessException {

		Map<String, String> parametrosZonaSala = this.obterParametroZonaSala();
		
		if (gradeAgendamentoConsulta.getAacUnidFuncionalSala() != null
				&& gradeAgendamentoConsulta.getAacUnidFuncionalSala()
						.getSituacao() != DominioSituacao.A) {
			ManterGradeAgendamentoRNExceptionCode.AAC_00069.throwException(
					parametrosZonaSala.get("zona"), parametrosZonaSala.get("sala"));
		}
	}

	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_ESP_PROF
	 * @param Short espSeq, Short preEspSeq
	 * @throws ApplicationBusinessException	
	 */
	public void verificaEspecialidadeProfissional(Short espSeq, Short preEspSeq)
			throws ApplicationBusinessException {
		
        //Especialidade do Profissional deve ser a mesma especialidade da grade
		if (preEspSeq != null && ! (espSeq.equals(preEspSeq))){
			ManterGradeAgendamentoRNExceptionCode.AAC_00077.throwException();
		}
	}
	
	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_GRD_N_PR
	 * @param Short UnfSeq, Short espSeq
	 * @throws ApplicationBusinessException	
	 */
	public void verificaAtendimentoProgramado(Short UnfSeq, Short espSeq)
	throws ApplicationBusinessException {
		if (getAacGradeAgendamenConsultasDAO()
				.listarConsultasProgramadas(UnfSeq, espSeq).isEmpty()) {
			ManterGradeAgendamentoRNExceptionCode.AAC_00217.throwException();
		}
	}

	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_EQP_ESP
	 * @param Short eqpSeq, Short espSeq
	 * @throws ApplicationBusinessException	
	 */
	public void verificaEquipeEspecialidade(AghEquipes equipe, AghEspecialidades especialidade)
	throws ApplicationBusinessException {
		if(getAghuFacade().listaProfEspecialidadesEquipe(especialidade,
				equipe.getProfissionalResponsavel()).isEmpty()){
			ManterGradeAgendamentoRNExceptionCode.AAC_00137.throwException();
		}
	}
	
	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_PROF_ATU
	 * @param RapServidores servidor, Short espSeq
	 * @throws ApplicationBusinessException	
	 */
	public void verificaSituacaoEquipeEspecialidade(Short espSeq, RapServidores servidor)
	throws ApplicationBusinessException {
		
		if (getAghuFacade().listaProfEspecialidades(servidor,
				espSeq).isEmpty()) {
			ManterGradeAgendamentoRNExceptionCode.AAC_00074.throwException();
		}
	}
	
	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_EQP_ATI
	 * @param AghEquipes equipe
	 * @throws ApplicationBusinessException	
	 */
	public void verificaSituacaoEquipe(AghEquipes equipe)
	throws ApplicationBusinessException {

		if (equipe == null || equipe.getIndSituacao() != DominioSituacao.A){
			ManterGradeAgendamentoRNExceptionCode.AAC_00071.throwException();
		}
	}
	
	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_ESP_ATI
	 * @param AghEspecialidades especialidade
	 * @throws ApplicationBusinessException	
	 */
	public void verificaSituacaoEspecialidade(AghEspecialidades especialidade)
	throws ApplicationBusinessException {
		getAmbulatorioRN().verificaSituacaoEspecialidade(especialidade);
	}

	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_ALTERAC
	 * @param Integer gradeSeq
	 * @throws ApplicationBusinessException	
	 */
	public void verificaSituacaoGradeAlteracao(AacGradeAgendamenConsultas gradeAgendamentoConsultas) throws ApplicationBusinessException {
		List <AacConsultas> listaConsultas = getAacConsultasDAO().pesquisarConsultasPorGrade(gradeAgendamentoConsultas.getSeq(), null);
		if (!listaConsultas.isEmpty()) {
			for (AacConsultas consulta : listaConsultas) {
				if (consulta.getSituacaoConsulta().getSituacao().equals('M')) {
					// Não é permitido alterar grade que possui consulta Marcada
					ManterGradeAgendamentoRNExceptionCode.AAC_00126.throwException();
				}
			}
		}
	}
	
	/**
	 * ORADB: AACK_GRD_RN.RN_GRDP_VER_PROC_ESP
	 * @param Short espSeq
	 * @throws ApplicationBusinessException	
	 *  
	 */
	public void verificaProcedimentoEspecialidade(AghEspecialidades especialidade)
		throws ApplicationBusinessException {

		if (getAacProcedHospEspDao().listarProcedimentosEspecialidadesConsulta(especialidade.getSeq()).isEmpty()) {
			String mensagem = super.getResourceBundleValue("NAO_EXISTE_PROCEDIMENTO_ESPECIALIDADE", especialidade.getSigla());
			getAmbulatorioRN().enviaEmail(mensagem);
		}
	}
	
	/**
	 * ORADB: AACK_HGC_RN.RN_HGCP_VER_DUR_ZERO
	 */
	public void verificaDuracaoZero(Short numHorario, Date duracao)
			throws ApplicationBusinessException {
		
		//Número de horários deve ser informado quando duração for = zero
		Calendar calDuracao = Calendar.getInstance();
		if(duracao!=null){
			calDuracao.setTime(duracao);
			if (calDuracao.get(Calendar.HOUR_OF_DAY) == 0
					&& calDuracao.get(Calendar.MINUTE) == 0
					&& numHorario == null) {
				ManterGradeAgendamentoRNExceptionCode.AAC_00142.throwException();
			}
		}
	}
	/**
	 * ORADB: AACK_HGC_RN.RN_HGCP_VER_DT_NRO
	 */
	public void verificaDataNumero(Date horaFim, Short numHorarios)
			throws ApplicationBusinessException {

		//Data fim e nro de horários não podem ser informados ao mesmo tempo.
		if (horaFim != null && numHorarios != null) {
			ManterGradeAgendamentoRNExceptionCode.AAC_00075.throwException();
		}
		
		//deve ser informado obrigatoriamente hora fim ou nro de horarios para 
		//efetuar calculo de horarios
		if (horaFim == null && numHorarios == null) {
			ManterGradeAgendamentoRNExceptionCode.AAC_00076.throwException();
		}
	}

	/**
	 * ORADB: AACK_HGC_RN.RN_HGCP_VER_HR_IG_AC
	 * 
	 * Não permite horários se sobreporem para uma grade
	 * num mesmo dia da semana e zona/sala.
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void verificaDataHoraIgualAC(
			AacHorarioGradeConsulta horarioGradeConsulta,AacGradeAgendamenConsultas grade)
			throws ApplicationBusinessException {
		
		if (horarioGradeConsulta.getHoraFim() == null && horarioGradeConsulta.getNumHorario() == null) {
			return;
		}
		
		Date horaFimCorrente = null;
		Date horaFimExistenteGrade = null;
		
		if(grade==null){
			ManterGradeAgendamentoRNExceptionCode.AAC_00066.throwException();
		}
		
		// Grades da emergencia sobrepõem, não há consistência carrega a última hora possível 
		// dos horários para o dia da semana, para o horário sendo inserido / atualizado. 
		if (!getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(
				grade.getAacUnidFuncionalSala().getId().getUnfSeq(),
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)) {
			
			if (horarioGradeConsulta.getHoraFim()!=null){
				horaFimCorrente = horarioGradeConsulta.getHoraFim(); 
			}else{
				horaFimCorrente = verificaUltimoHorario(horarioGradeConsulta
						.getHoraInicio(), horarioGradeConsulta.getNumHorario(),
						horarioGradeConsulta.getDuracao());
			}
		
			List<AacHorarioGradeConsulta> horarioGradeConsultas = getAacHorarioGradeConsultaDAO()
					.buscaHorarioGradeConsulta(
							horarioGradeConsulta.getId()!=null?horarioGradeConsulta.getId().getSeqp():null,
							horarioGradeConsulta.getId()!=null?horarioGradeConsulta.getId().getGrdSeq():null,
							horarioGradeConsulta.getDiaSemana(),
							grade.getAacUnidFuncionalSala().getId().getUnfSeq(),
							grade.getAacUnidFuncionalSala().getId().getSala());
			
			for(AacHorarioGradeConsulta horarioGrade : horarioGradeConsultas){
				if (horarioGrade.getHoraFim()!=null){
					horaFimExistenteGrade = horarioGrade.getHoraFim(); 
				}else{
					horaFimExistenteGrade = verificaUltimoHorario(horarioGrade
							.getHoraInicio(), horarioGrade.getNumHorario(),
							horarioGrade.getDuracao());
				}
				
				Calendar calAux = Calendar.getInstance();
				Calendar calHoraInicioCorrente = Calendar.getInstance();
				Calendar calHoraInicioExistenteGrade = Calendar.getInstance();
				Calendar calHoraFimCorrente = Calendar.getInstance();
				Calendar calHoraFimExistenteGrade = Calendar.getInstance();
				
				calAux.setTime(horarioGradeConsulta.getHoraInicio());
				calHoraInicioCorrente.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
				calHoraInicioCorrente.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
				calHoraInicioCorrente.set(Calendar.SECOND, 0);
				calHoraInicioCorrente.set(Calendar.MILLISECOND, 0);
				
				calAux.setTime(horarioGrade.getHoraInicio());
				calHoraInicioExistenteGrade.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
				calHoraInicioExistenteGrade.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
				calHoraInicioExistenteGrade.set(Calendar.SECOND, 0);
				calHoraInicioExistenteGrade.set(Calendar.MILLISECOND, 0);
				
				calAux.setTime(horaFimCorrente);
				calHoraFimCorrente.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
				calHoraFimCorrente.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
				calHoraFimCorrente.set(Calendar.SECOND, 0);
				calHoraFimCorrente.set(Calendar.MILLISECOND, 0);
				
				calAux.setTime(horaFimExistenteGrade);
				calHoraFimExistenteGrade.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
				calHoraFimExistenteGrade.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
				calHoraFimExistenteGrade.set(Calendar.SECOND, 0);
				calHoraFimExistenteGrade.set(Calendar.MILLISECOND, 0);
				
							
				
				if (calHoraFimCorrente!=null ){
					Boolean horaInicioCorrenteEntreHorarioExistente = false;
					Boolean horaFimCorrenteEntreHorarioExistente = false;
					Boolean horaFimOuHoraInicioIguais = false;
	
					
					//Verifica se a hora inicio corrente está entre algum horario já existente na grade
					if (calHoraInicioCorrente.getTime().after(calHoraInicioExistenteGrade.getTime())
							&& calHoraInicioCorrente.getTime().before(calHoraFimExistenteGrade.getTime())){
						horaInicioCorrenteEntreHorarioExistente=true;
					}
					
					//Verifica se a hora fim corrente está entre algum horario já existente na grade
					if (calHoraFimCorrente.getTime().after(calHoraInicioExistenteGrade.getTime()) 
							&& calHoraFimCorrente.getTime().before(calHoraFimExistenteGrade.getTime())){
						horaFimCorrenteEntreHorarioExistente=true;
					}
					
					//Verifica se a hora fim ou hora inicio corrente é igual a hora fim ou hora fim existentes (respectivamente)
					if ((calHoraFimCorrente.get(Calendar.HOUR_OF_DAY)==calHoraFimExistenteGrade.get(Calendar.HOUR_OF_DAY)
							&&	calHoraFimCorrente.get(Calendar.MINUTE)==calHoraFimExistenteGrade.get(Calendar.MINUTE))
							||(calHoraInicioCorrente.get(Calendar.HOUR_OF_DAY)==calHoraInicioExistenteGrade.get(Calendar.HOUR_OF_DAY)
									&&	calHoraInicioCorrente.get(Calendar.MINUTE)==calHoraInicioExistenteGrade.get(Calendar.MINUTE))){
						horaFimOuHoraInicioIguais = true;
					}
					
					
					// Se horário possui hora fim preenchida, basta verificar se o horário
			        // novo encontra-se entre a hora inicio e hora fim
					if (horaInicioCorrenteEntreHorarioExistente || horaFimCorrenteEntreHorarioExistente || horaFimOuHoraInicioIguais) {
						//verifica se é o mesmo equipe/profissional
						//pois sendo para os mesmos, pode haver sobreposição
						if(!horarioGrade.getGradeAgendamentoConsulta().getEquipe().getSeq().equals(grade.getEquipe().getSeq())
							|| (grade.getProfEspecialidade()!=null && horarioGrade.getGradeAgendamentoConsulta().getProfEspecialidade()!=null &&
								!horarioGrade.getGradeAgendamentoConsulta().getProfEspecialidade().getId().equals(grade.getProfEspecialidade().getId()))){
							
				            //O intervalo informado implicará em sobreposição de horários para a mesma Zona/Sala
							if(horarioGradeConsulta.getFormaAgendamento()!=null && horarioGrade.getFormaAgendamento()!=null  
									&& horarioGrade.getFormaAgendamento().getId().getCaaSeq()
									.equals(horarioGradeConsulta.getFormaAgendamento().getId().getCaaSeq())){
								
								Map<String, String> parametrosZonaSala = this.obterParametroZonaSala();
								
								ManterGradeAgendamentoRNExceptionCode.AAC_00131.throwException(
										parametrosZonaSala.get("zona"), parametrosZonaSala.get("sala"), horarioGrade.getGradeAgendamentoConsulta().getSeq());
							}
						}
					}
				}
			}
		}
	}

	/**
	 * ORADB: AACK_HGC_RN.HGCC_VER_ULT_HR
	 */
	private Date verificaUltimoHorario(Date horaInicio, Short numeroHorarios,
			Date tempo) {
		
		Calendar calDuracao = Calendar.getInstance();
		if (tempo!=null){
			calDuracao.setTime(tempo);
		}
		Integer duracaoMinutos = ((calDuracao.get(Calendar.HOUR_OF_DAY) * 60) + calDuracao.get(Calendar.MINUTE));
		
		Calendar calHoraInicio = Calendar.getInstance();
		if (horaInicio!=null){
			calHoraInicio.setTime(horaInicio);
		}
		
		for(int i=0; i<numeroHorarios ;i++){
			calHoraInicio.add(Calendar.MINUTE, duracaoMinutos);
		}
		
		return calHoraInicio.getTime();
	}

	/**
	 * ORADB: AACK_HGC_RN.RN_HGCP_VER_FORMA
	 */
	public void verificaFormaAgendamento(AacFormaAgendamento formaAgendamento)
			throws ApplicationBusinessException {
		
		if(getAacFormaAgendamentoDAO().findByID(formaAgendamento.getId())==null){
			ManterGradeAgendamentoRNExceptionCode.AAC_00613.throwException();
		}
	}
	
	/**
	 * ORADB: AACK_GPR_RN.RN_GPRP_VER_PROC_ATI
	 */
	public void verificaProcedimentoHospitalarInternoAtivo(
			AacGradeProcedHospitalar procedimento)
			throws ApplicationBusinessException {
		if (!procedimento.getProcedHospInterno().getSituacao().equals(
				DominioSituacao.A)) {
			throw new ApplicationBusinessException(
					ManterGradeAgendamentoRNExceptionCode.PROCED_HOSP_INTERNO_INATIVO);
		}

	}
	
	/**
	 * ORADB: AACK_GST_RN.RN_GSTP_VER_SIT_GRD
	 * 
	 * Verifica se a grade está ativa numa determinada data 
	 */
	public void verificaSituacaoGradeData(
			AacGradeAgendamenConsultas gradeAgendamentoConsulta,
			Date dataReferencia) throws ApplicationBusinessException {
		
		AacGradeSituacao gradePeriodo = getAacGradeSituacaoDAO().obterGradePeriodoInformado(gradeAgendamentoConsulta.getSeq(),
				dataReferencia,null);
		
		if(gradePeriodo==null) {
			GerarDisponibilidadeHorarioRNExceptionCode.NAO_EXISTE_PERIODO_ATIVO.throwException();
		}
	}
	
	/**
	 * Trigger
	 * ORADB: AACT_CGA_ARD 
	 * @param caracteristica
	 * @throws ApplicationBusinessException
	 */
	public void persistirCaracteristicaJn(AacCaracteristicaGrade caracteristica)
	throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AacCaracteristicaGradeJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AacCaracteristicaGradeJn.class, servidorLogado.getUsuario());
		jn.setCaracteristica(caracteristica.getId().getCaracteristica());
		jn.setCriadoEm(caracteristica.getCriadoEm());
		jn.setGrdSeq(caracteristica.getGradeAgendamentoConsulta().getSeq());
		jn.setSerMatricula(caracteristica.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(caracteristica.getServidor().getId().getVinCodigo());
		this.getCaracteristicaGradeJnDAO().persistir(jn);
		this.getCaracteristicaGradeJnDAO().flush();
	}
	
	public void inserirSituacao(AacGradeSituacao gradeSituacao)
	throws ApplicationBusinessException {
		this.preInserirGradeSituacoes(gradeSituacao);
		this.posInserirGradeSituacoes(gradeSituacao);
		this.getAacGradeSituacaoDAO().persistir(gradeSituacao);
		this.getAacGradeSituacaoDAO().flush();
	}
	
	protected AacProcedHospEspecialidadesDAO getAacProcedHospEspDao(){
		return aacProcedHospEspecialidadesDAO;
	}
	
	protected AacHorarioGradeConsultaDAO getAacHorarioGradeConsultaDAO(){
		return aacHorarioGradeConsultaDAO;
	}
	
	protected AacCaracteristicaGradeDAO getAacCaracteristicaGradeDAO(){
		return aacCaracteristicaGradeDAO;
	}
	
	protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO(){
		return aacGradeAgendamenConsultasDAO;
	}
	
	protected AacGradeSituacaoDAO getAacGradeSituacaoDAO(){
		return aacGradeSituacaoDAO;
	}
	
	protected AacGradeProcedHospitalarDAO getAacGradeProcedHospitalarDAO(){
		return aacGradeProcedHospitalarDAO;
	}
	
	protected AacGradeAgendamenConsJnDAO getAacGradeAgendamenConsJnDAO(){
		return aacGradeAgendamenConsJnDAO;
	}
	
	protected AacHorarioGradeJnDAO getAacHorarioGradeJnDAO(){
		return aacHorarioGradeJnDAO;
	}
	
	protected AacFormaAgendamentoDAO getAacFormaAgendamentoDAO() {
		return aacFormaAgendamentoDAO;
	}
	
	protected AmbulatorioRN getAmbulatorioRN(){
		return ambulatorioRN;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected AacConsultasDAO getAacConsultasDAO(){
		return aacConsultasDAO;
	}
	
	protected AacCaracteristicaGradeJnDAO getCaracteristicaGradeJnDAO() {
		return aacCaracteristicaGradeJnDAO;
	}
	
	/**
	 * Método para implementar regras da trigger executada na exclusão de uma
	 * grade de Agendamento.
	 * 
	 * ORADB Trigger AACT_GRD_ARD
	 * 
	 * @param consultaOld
	 * @param operacaoJn
	 * @throws ApplicationBusinessException 
	 */
	public void inserirGradeAgendamenConsultaJournal(AacGradeAgendamenConsultas gradeAgedamenConsultaOld) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AacGradeAgendamenConsJn jn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.DEL, AacGradeAgendamenConsJn.class, servidorLogado.getUsuario());
	
		jn.setSeq(gradeAgedamenConsultaOld.getSeq());
		jn.setCriadoEm(gradeAgedamenConsultaOld.getCriadoEm());
		jn.setProgramada(gradeAgedamenConsultaOld.getProgramada());
		jn.setExigeProntuario(gradeAgedamenConsultaOld.getExigeProntuario());
		jn.setConvenioSus(gradeAgedamenConsultaOld.getConvenioSus());
		jn.setEnviaSamis(gradeAgedamenConsultaOld.getEnviaSamis());
		jn.setEmiteTicket(gradeAgedamenConsultaOld.getEmiteTicket());
		jn.setEmiteBa(gradeAgedamenConsultaOld.getEmiteBa());
		jn.setProcedimento(gradeAgedamenConsultaOld.getProcedimento());
		jn.setIndAvisaConsultaTurno(gradeAgedamenConsultaOld.getIndAvisaConsultaTurno());
		jn.setEquipe(gradeAgedamenConsultaOld.getEquipe());
		jn.setEspecialidade(gradeAgedamenConsultaOld.getEspecialidade());
		jn.setAacUnidFuncionalSala(gradeAgedamenConsultaOld.getAacUnidFuncionalSala());
		jn.setServidor(gradeAgedamenConsultaOld.getServidor());
		jn.setDtUltimaGeracao(gradeAgedamenConsultaOld.getDtUltimaGeracao());
		jn.setProfEspecialidade(gradeAgedamenConsultaOld.getProfEspecialidade());
		jn.setServidorAlterado(gradeAgedamenConsultaOld.getServidorAlterado());
		jn.setAlteradoEm(gradeAgedamenConsultaOld.getAlteradoEm());
		jn.setTipoAgendamento(gradeAgedamenConsultaOld.getTipoAgendamento());
		jn.setCondicaoAtendimento(gradeAgedamenConsultaOld.getCondicaoAtendimento());
		jn.setPagador(gradeAgedamenConsultaOld.getPagador());
		jn.setTempoAtendAnterior(gradeAgedamenConsultaOld.getTempoAtendAnterior());
		
		getAacGradeAgendamenConsJnDAO().persistir(jn);
		getAacGradeAgendamenConsJnDAO().flush();
	}
	
	public void removerGradeConsulta(Integer seqGrade) throws ApplicationBusinessException{
		
		AacGradeAgendamenConsultas gradeAgendamenConsulta =getAacGradeAgendamenConsultasDAO().obterPorChavePrimaria(seqGrade);
		
		Set<AacGradeSituacao> situacoes = gradeAgendamenConsulta.getGradeSituacao();
		Set<AacHorarioGradeConsulta> horarios = gradeAgendamenConsulta.getHorarioGradeConsulta();
		Set<AacGradeProcedHospitalar> gradesProcedimentos = gradeAgendamenConsulta.getGradeProcedimentosHospitalar();
		Set<AacCaracteristicaGrade> gradesCaracteristicas = gradeAgendamenConsulta.getCaracteristicaGrade();
		
		this.removerSituacoes(situacoes);
		this.removerProcedimentos(gradesProcedimentos);
		this.removerHorarios(horarios);
		this.removerCaracteristica(gradesCaracteristicas);
		this.removerGradeAgendamenConsulta(gradeAgendamenConsulta);
		
	}

	/**
	 * Remove os objetos situação da grade de agendamento consulta
	 * @param situacoes
	 */
	private void removerSituacoes(Set<AacGradeSituacao> situacoes) {
		AacGradeSituacaoDAO aacGradeSituacaoDAO = getAacGradeSituacaoDAO();
		for (AacGradeSituacao situacao: situacoes){
			aacGradeSituacaoDAO.remover(situacao);
			aacGradeSituacaoDAO.flush();
		}
	}
	
	/**
	 * Remove os objetos procedimentos da grade de agendamento consulta
	 * @param gradesProcedimentos
	 */
	private void removerProcedimentos(Set<AacGradeProcedHospitalar> gradesProcedimentos) {
		AacGradeProcedHospitalarDAO aacGradeProcedHospitalarDAO = getAacGradeProcedHospitalarDAO();
		for (AacGradeProcedHospitalar gradesProcedimento: gradesProcedimentos){
			aacGradeProcedHospitalarDAO.remover(gradesProcedimento);
			aacGradeProcedHospitalarDAO.flush();
		}
	}
	
	/**
	 * Remove os objetos horário da grade de agendamento consulta
	 * @param horarios
	 */
	private void removerHorarios(Set<AacHorarioGradeConsulta> horarios) throws ApplicationBusinessException {
		for (AacHorarioGradeConsulta horario: horarios){
			getAacHorarioGradeConsultaDAO().remover(horario);
			getAacHorarioGradeConsultaDAO().flush();
			posDeleteAacHorarioGradeConsultaJournal(horario);
		}
	}
	
	/**
	 * Remove os objetos caracteristica da grade de agendamento consulta
	 * @param caracteristicas
	 */
	private void removerCaracteristica(Set<AacCaracteristicaGrade> caracteristicas) throws ApplicationBusinessException {
		for (AacCaracteristicaGrade caracteristica: caracteristicas){
			getAacCaracteristicaGradeDAO().remover(caracteristica);
			getAacCaracteristicaGradeDAO().flush();
			persistirCaracteristicaJn(caracteristica);
		}
	}
	
	/**
	 * Remove a grade de agendamento consulta
	 * @param gradeAgendamenConsulta
	 */
	private void removerGradeAgendamenConsulta(AacGradeAgendamenConsultas gradeAgendamenConsulta) throws ApplicationBusinessException {
			AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO = getAacGradeAgendamenConsultasDAO();
			aacGradeAgendamenConsultasDAO.remover(gradeAgendamenConsulta);
			aacGradeAgendamenConsultasDAO.flush();
			inserirGradeAgendamenConsultaJournal(gradeAgendamenConsulta);
	}

	private Map<String, String> obterParametroZonaSala() {
		
		String labelZona;
		String labelSala;
		
		try {
			labelZona = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			labelSala = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			labelZona = "(Zona)";
			labelSala = "Sala";
		}
		
		Map<String, String> values = new HashMap<String, String>();
		values.put("zona", labelZona);
		values.put("sala", labelSala);
		
		return values;
	}

	public boolean validaGradeAgendamentoExistentePorSetorEspecialidadeEquipe(AacGradeAgendamenConsultas grade) {
		boolean retorno = false;
		if (getAacGradeAgendamenConsultasDAO().buscarGradeAgendamentoExistentePorSetorEspecialidadeEquipe(grade) > 0L) {
			retorno = true;
		}
		return retorno;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
