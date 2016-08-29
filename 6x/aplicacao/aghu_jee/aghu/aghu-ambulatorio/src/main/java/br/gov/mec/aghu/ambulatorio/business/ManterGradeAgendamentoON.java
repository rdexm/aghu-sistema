package br.gov.mec.aghu.ambulatorio.business;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacCaracteristicaGradeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeSituacaoDAO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaGrade;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.AacCaracteristicaGrade;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeProcedHospitalar;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacGradeSituacaoId;
import br.gov.mec.aghu.model.AacHorarioGradeConsulta;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterGradeAgendamentoON extends BaseBusiness {
	
	@EJB
	private ManterGradeAgendamentoRN manterGradeAgendamentoRN;
	
	@EJB
	private GerarDisponibilidadeHorarioRN gerarDisponibilidadeHorarioRN;
	
	private static final Log LOG = LogFactory.getLog(ManterGradeAgendamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AacCaracteristicaGradeDAO aacCaracteristicaGradeDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AacGradeProcedHospitalarDAO aacGradeProcedHospitalarDAO;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;
	
	@Inject
	private AacGradeSituacaoDAO aacGradeSituacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6849393471248958980L;

	private enum ManterGradeAgendamentoONExceptionCode implements
			BusinessExceptionCode {
		AAC_00728, PROCEDIMENTO_JA_ADICIONADO_GRADE, CARACTERISTICA_JA_ADICIONADA_GRADE, AAC_00146, AAC_00145,
		MSG_MANTER_GRADE_AGENDAMENTO_CRM_INATIVO, MSG_EQUIPE_EXISTENTE_EM_GRADE_AGENDAMENTO,
		MSG_MANTER_GRADE_AGENDAMENTO_COPIAR_GRADE_VALIDAR_GRADE_EXISTENTE,
		MSG_MANTER_GRADE_AGENDAMENTO_COPIAR_GRADE_VALIDAR_GRADE_INALTERADA,
		MSG_MANTER_GRADE_AGENDAMENTO_HORARIO_FIM;
	
		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}	
	
	public AacGradeAgendamenConsultas salvar(AacGradeAgendamenConsultas entity, AacGradeAgendamenConsultas oldEntity)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (entity.getSeq() == null) {
			entity.setExigeProntuario(false);
			entity.setProgramada(true);
			entity.setCriadoEm(new Date());
			getManterGradeAgendamentoRN().preInserirAacGradeAgendamenConsultas(entity);
			getManterGradeAgendamentoRN().posInserirAacGradeAgendamenConsultas(entity);
			getAacGradeAgendamenConsultasDAO().persistir(entity);
			entity.setCaracteristicaGrade(null);
			entity.setEmgEspecialidades(null);
			entity.setGradeProcedimentosHospitalar(null);
			entity.setGradeProcedimentosHospitalar(null);
			entity.setHorarioGradeConsulta(null);
			getManterGradeAgendamentoRN().posInserirAacGradeAgendamenConsultasEnforce(entity);
		} else {
			entity.setServidorAlterado(servidorLogado);
			entity.setAlteradoEm(new Date());
			getManterGradeAgendamentoRN().preAtualizarAacGradeAgendamenConsultas(entity, oldEntity);
			//getManterGradeAgendamentoRN().verificaSituacaoGradeData(entity, new Date());			
			entity = getAacGradeAgendamenConsultasDAO().atualizar(entity);
			getAacGradeAgendamenConsultasDAO().flush();
			getManterGradeAgendamentoRN().posAtualizarAacGradeAgendamenConsultasJournal(entity, oldEntity);
		}
		return entity;
	}
	
	
	public int gerarDisponibilidade(AacGradeAgendamenConsultas entity, Date dataInicial, Date dataFinal, String nomeMicrocomputador) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NumberFormatException, BaseException {
		if (dataInicial==null || DateUtils.truncate(dataInicial,Calendar.DATE).before(DateUtils.truncate(new Date(),Calendar.DATE))){
			throw new ApplicationBusinessException(ManterGradeAgendamentoONExceptionCode.AAC_00146);
		}else if (dataFinal==null || DateUtils.truncate(dataFinal,Calendar.DATE).before(DateUtils.truncate(dataInicial,Calendar.DATE))){
			throw new ApplicationBusinessException(ManterGradeAgendamentoONExceptionCode.AAC_00145);
		}
	
		return getGerarDisponibilidadeHorarioRN().gerarHorarioConsulta(entity, dataInicial, dataFinal, nomeMicrocomputador);
		
	}
		
	
	
	public void eventoPreInserirUpdate(
			AacHorarioGradeConsulta horarioGradeConsulta)
			throws ApplicationBusinessException {
		
		AghParametros p3 = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_VALIDA_PROJETO_PESQUISA);
		Short parametroProjetoPesquisa = p3 != null ? p3.getVlrNumerico()
				.shortValue() : null;

		if (horarioGradeConsulta != null && horarioGradeConsulta.getGradeAgendamentoConsulta()!=null
				&& horarioGradeConsulta.getGradeAgendamentoConsulta().getProjetoPesquisa()!=null
				&& horarioGradeConsulta.getFormaAgendamento() != null
				&& horarioGradeConsulta.getFormaAgendamento().getPagador() != null
				&& !horarioGradeConsulta.getFormaAgendamento().getPagador().getSeq().equals(parametroProjetoPesquisa)) {
			ManterGradeAgendamentoONExceptionCode.AAC_00728.throwException();
		}
	}
	
	public void  verificaDataHoraIgualAC(AacHorarioGradeConsulta horarioGradeConsulta, 
			AacGradeAgendamenConsultas grade) throws ApplicationBusinessException{
		getManterGradeAgendamentoRN().verificaDataHoraIgualAC(horarioGradeConsulta, grade);
	}
	

	/**
	 *
	 */
	public void eventoPreRemoverGradeSituacao(
			AacGradeSituacao gradeSituacao)
			throws ApplicationBusinessException {
		this.getManterGradeAgendamentoRN().verificarRemocaoGradeSituacao(gradeSituacao);
		this.getManterGradeAgendamentoRN().verificarConsultaMarcadaNovoPeriodo(gradeSituacao);
		this.getManterGradeAgendamentoRN().removerPeriodoGrade(gradeSituacao);
		gradeSituacao = this.getGradeSituacaoDAO().merge(gradeSituacao);
		this.getGradeSituacaoDAO().remover(gradeSituacao);
		this.getGradeSituacaoDAO().flush();
	}

	public void persistirProcedimento(AacGradeProcedHospitalar procedimento) throws ApplicationBusinessException {
		this.validarDescricaoProcedimento(procedimento);
		this.getManterGradeAgendamentoRN().verificaProcedimentoHospitalarInternoAtivo(procedimento);
		this.getGradeProcedHospitalarDAO().persistir(procedimento);
		this.getGradeProcedHospitalarDAO().flush();
	}

	public void persistirCaracteristica(AacCaracteristicaGrade caracteristica)
			throws ApplicationBusinessException {
		this.validarCaracteristica(caracteristica);
		this.getCaracteristicaGradeDAO().persistir(caracteristica);
		this.getCaracteristicaGradeDAO().flush();
	}

	public void validarDescricaoProcedimento(
			AacGradeProcedHospitalar procedimento)
			throws ApplicationBusinessException {
		List<AacGradeProcedHospitalar> listaGradeProcedHospitalar = this
				.getGradeProcedHospitalarDAO().listarProcedimentosGrade(
						procedimento.getGradeAgendamentoConsulta().getSeq());
		for (AacGradeProcedHospitalar gradeProcedHospitalar : listaGradeProcedHospitalar) {
			if (gradeProcedHospitalar.getProcedHospInterno().getDescricao()
					.equals(procedimento.getProcedHospInterno().getDescricao())) {
				throw new ApplicationBusinessException(
						ManterGradeAgendamentoONExceptionCode.PROCEDIMENTO_JA_ADICIONADO_GRADE, procedimento.getProcedHospInterno().getDescricao());
			}
		}
	}
	
	public void validarCaracteristica(
			AacCaracteristicaGrade caracteristica)
			throws ApplicationBusinessException {
		List<AacCaracteristicaGrade> listaCaracteristicaGrade = this
				.getCaracteristicaGradeDAO().listarCaracteristicaGrade(
						caracteristica.getGradeAgendamentoConsulta().getSeq());
		for (AacCaracteristicaGrade caracteristicaGrade : listaCaracteristicaGrade) {
			if (caracteristica.getId().getCaracteristica()
					.equals(caracteristicaGrade.getId().getCaracteristica())) {
				throw new ApplicationBusinessException(
						ManterGradeAgendamentoONExceptionCode.CARACTERISTICA_JA_ADICIONADA_GRADE, caracteristica.getId().getCaracteristica());
			}
		}
	}

	public List<String> listarCaracteristicas() {
		DominioCaracteristicaGrade [] caracteristicas = DominioCaracteristicaGrade.values();
		List<String> listaCaracteristicasGrade = new ArrayList<String>();
		for (DominioCaracteristicaGrade dominioCaracteristicaGrade : caracteristicas) {
			listaCaracteristicasGrade.add(dominioCaracteristicaGrade.getDescricao());
		}
		return listaCaracteristicasGrade;
	}	
	
	protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}

	protected ManterGradeAgendamentoRN getManterGradeAgendamentoRN() {
		return manterGradeAgendamentoRN;
	}

	protected AacGradeProcedHospitalarDAO getGradeProcedHospitalarDAO() {
		return aacGradeProcedHospitalarDAO;
	}

	protected AacCaracteristicaGradeDAO getCaracteristicaGradeDAO() {
		return aacCaracteristicaGradeDAO;
	}
	
	protected AacGradeSituacaoDAO getGradeSituacaoDAO() {
		return aacGradeSituacaoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected GerarDisponibilidadeHorarioRN getGerarDisponibilidadeHorarioRN(){
		return gerarDisponibilidadeHorarioRN;
	}
	
	public void validaCRMAmbulatorio(AghProfEspecialidades prof, AacGradeAgendamenConsultas grade) throws ApplicationBusinessException{
		Date dataAtual = new Date();
		if (prof!=null){
			if ((prof.getRapServidor().getIndSituacao().equals(DominioSituacaoVinculo.A)) || 
				(prof.getRapServidor().getIndSituacao().equals(DominioSituacaoVinculo.P) && !prof.getRapServidor().getDtFimVinculo().before(dataAtual))) { 
				grade.setProfEspecialidade(prof);
			}else{
			throw new ApplicationBusinessException(
					ManterGradeAgendamentoONExceptionCode.MSG_MANTER_GRADE_AGENDAMENTO_CRM_INATIVO);
			}		
		}else{
		throw new ApplicationBusinessException(
				ManterGradeAgendamentoONExceptionCode.MSG_MANTER_GRADE_AGENDAMENTO_CRM_INATIVO);
		}		
	}
	
	public void existeGradeAgendamentoConsultaComEquipe (AghEquipes equipe) throws BaseException{
		if(getAacGradeAgendamenConsultasDAO().listarGradesAgendamentoConsultaPorEquipe(equipe)) {
			throw new ApplicationBusinessException(
					ManterGradeAgendamentoONExceptionCode.MSG_EQUIPE_EXISTENTE_EM_GRADE_AGENDAMENTO);
			}		
	}
	
	public void validarHorariosAgendados(AacHorarioGradeConsulta horarioGradeConsulta)throws ApplicationBusinessException{
		if (horarioGradeConsulta.getHoraFim()!=null && !horarioGradeConsulta.getHoraFim().after(horarioGradeConsulta.getHoraInicio())) {
			throw new ApplicationBusinessException(ManterGradeAgendamentoONExceptionCode.MSG_MANTER_GRADE_AGENDAMENTO_HORARIO_FIM);
		}
		
	}
	
	private void tratarCampoDataNula() {
		Date defaultValue = null; 
		DateConverter converter = new DateConverter (defaultValue); 
		ConvertUtils.register (converter, java.util.Date.class);
	}
	
	public AacHorarioGradeConsulta clonarHorarioGradeConsulta(AacHorarioGradeConsulta horario, AacGradeAgendamenConsultas gradeAgendamenConsultaCopia) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
		AacHorarioGradeConsulta clone = new AacHorarioGradeConsulta();
		tratarCampoDataNula();
		BeanUtils.copyProperties(clone, horario);
		clone.setGradeAgendamentoConsulta(gradeAgendamenConsultaCopia);
		clone.setId(null);
		
		if (horario.getNumHorario() == null) {
			clone.setNumHorario(null);
		}
		return clone;
	}
	public AacGradeProcedHospitalar clonarListaProcedimentosHospitalarConsulta(AacGradeProcedHospitalar procedimento) throws IllegalAccessException, InvocationTargetException {
		AacGradeProcedHospitalar clone = new AacGradeProcedHospitalar();
		tratarCampoDataNula(); 
		BeanUtils.copyProperties(clone, procedimento);
		return clone;
	}
	public AacCaracteristicaGrade clonarHorarioGradeConsulta(AacCaracteristicaGrade caracteristica) throws IllegalAccessException, InvocationTargetException {
		AacCaracteristicaGrade clone = new AacCaracteristicaGrade();
		tratarCampoDataNula(); 
		clone.setCriadoEm(new Date());
		BeanUtils.copyProperties(clone, caracteristica);
		return clone;
	}
	public AacGradeSituacao clonarSituacaoGradeConsulta(AacGradeSituacao situacao, AacGradeAgendamenConsultas gradeAgendamenConsultasCopia) throws IllegalAccessException, InvocationTargetException {
		AacGradeSituacao clone = new AacGradeSituacao();
		AacGradeSituacaoId idClone = new AacGradeSituacaoId();
		tratarCampoDataNula(); 
		BeanUtils.copyProperties(clone, situacao);
		clone.setCriadoEm(new Date());
		idClone.setGrdSeq(gradeAgendamenConsultasCopia.getSeq());
		idClone.setDtInicioSituacao(situacao.getId().getDtInicioSituacao());
		clone.setId(idClone);
		return clone;
	}

	
	public AacGradeAgendamenConsultas clonarAgendamenConsultaCopia(AacGradeAgendamenConsultas gradeAgendamenConsultaOriginal) throws IllegalAccessException, InvocationTargetException {
		AacGradeAgendamenConsultas clone = new AacGradeAgendamenConsultas();
		tratarCampoDataNula(); 
		BeanUtils.copyProperties(clone, gradeAgendamenConsultaOriginal);
		clone.setConvenioSus(null);
		clone.setCriadoEm(new Date());
		clone.setAlteradoEm(new Date());
		clone.setHorarioGradeConsulta(null);
		clone.setGradeProcedimentosHospitalar(null);
		clone.setCaracteristicaGrade(null);
		clone.setGradeSituacao(null);
		clone.setSeq(null);
		clone.setDtUltimaGeracao(null);
		clone.setAacConsultas(null);
		clone.setEmgEspecialidades(null);
		clone.setVersion(null); 
		return clone;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
