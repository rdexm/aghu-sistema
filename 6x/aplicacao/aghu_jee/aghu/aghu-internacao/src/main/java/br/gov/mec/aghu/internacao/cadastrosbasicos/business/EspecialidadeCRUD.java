package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioEspecialidadeInterna;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghEspecialidadesJn;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de especialidades.
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity"})
@Stateless
public class EspecialidadeCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EspecialidadeCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;
	
	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4997574445615935587L;
	private static final Integer MAX_RESULTS = 200;

	private enum EspecialidadesCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERSISTIR_ESPECIALIDADE, 
		ERRO_ATUALIZAR_ESPECIALIDADE, 
		ERRO_REMOVER_ESPECIALIDADE,
		ERRO_NOME_ESPECIALIDADE_ALTERADO,
		ERRO_ESPECIALIDADE_CENTRO_CUSTO,
		ERRO_ESPECIALIDADE_CLINICA,
		ERRO_ESPECIALIDADE_CHEFIA,
		ERRO_ESPECIALIDADE_POSSUI_AGENDAS, //AGH-00561
		ERRO_ESPECIALIDADE_POSSUI_GRADE_AGENDAMENTO, //AGH-00563
		ERRO_ESPECIALIDADE_GENERICA_INATIVA, //AGH-00562
		ERRO_ESPECIALIDADE_GENERICA_GENERICA, //AGH-00570
		ERRO_ESPECIALIDADE_INATIVA, //AGH-00560
		ERRO_ESPECIALIDADE_IDADES_MIN_MAX, // AGH_ESP_CK3, AGH_ESP_CK4, AGH_ESP_CK8
		ERRO_ESPECIALIDADE_SOLIC_CONSULTORIA, // AGH_ESP_CK19
		ERRO_ESPECIALIDADE_CAPAC_REFERENCIAL, // AGH_ESP_CK7, AGH-00116
		ERRO_ESPECIALIDADE_CAPAC_REFERENCIAL_NEG, // AGH_ESP_CK6, AGH-00115
		ERRO_ESPECIALIDADE_SIGLA_JA_EXISTENTE // AGH_ESP_UK1, AGH-00025
		;
	}
	
	// Associa constraints que podem ser lançadas pelo banco aos erros de negócio
	private enum Constraint {
		AGH_ESP_UK1(EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_SIGLA_JA_EXISTENTE);
		
		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private final EspecialidadesCRUDExceptionCode exception_code;
		
		Constraint(EspecialidadesCRUDExceptionCode exception_code) {
			this.exception_code = exception_code;
		}		
		public EspecialidadesCRUDExceptionCode getExceptionCode() { return this.exception_code; }
	}
	
	/**
	 * @dbtables AghEspecialidades select
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigoEspecialidade
	 * @param nomeEspecialidade
	 * @param siglaEspecialidade
	 * @param codigoEspGenerica
	 * @param centroCusto
	 * @param clinica
	 * @param situacao
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidades(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Short codigoEspecialidade, String nomeEspecialidade, String siglaEspecialidade, Short codigoEspGenerica,
			Integer centroCusto, Integer clinica, DominioSituacao situacao) {
		return this.getAghuFacade().pesquisarEspecialidades(firstResult, maxResult, orderProperty, asc, codigoEspecialidade,
				nomeEspecialidade, siglaEspecialidade, codigoEspGenerica, centroCusto, clinica, situacao);
	}

	/**
	 * @dbtables AghEspecialidades select
	 * @param codigo
	 * @param nomeEspecialidade
	 * @param siglaEspecialidade
	 * @param codigoEspGenerica
	 * @param centroCusto
	 * @param clinica
	 * @param situacao
	 * @return
	 */
	public Long pesquisarEspecialidadesCount(Short codigo, String nomeEspecialidade, String siglaEspecialidade,
			Short codigoEspGenerica, Integer centroCusto, Integer clinica, DominioSituacao situacao) {
		return this.getAghuFacade().pesquisarEspecialidadesCount(codigo, nomeEspecialidade, siglaEspecialidade,
				codigoEspGenerica, centroCusto, clinica, situacao);
	}
	
	// Retorna uma nova especialidade com valores default preenchidos
	public AghEspecialidades gerarNovaEspecialidade() {
		
		AghEspecialidades especialidade = new AghEspecialidades();
		
		// seta valores default
		especialidade.setIndSituacao(DominioSituacao.A);
		especialidade.setIndConsultoria(DominioSimNao.N);
		especialidade.setIndAtendHospDia(DominioSimNao.N);
		especialidade.setIndSugereProfInternacao(DominioSimNao.N);
		especialidade.setIndHoraDispFeriado(DominioSimNao.N);
		especialidade.setIndImprimeAgenda(DominioSimNao.S);
		especialidade.setIndEmiteBoletimAtendimento(DominioSimNao.S);
		especialidade.setIndEmiteTicket(DominioSimNao.S);
		especialidade.setIndEnviaMensagem(DominioSimNao.S);
		especialidade.setIndAcompPosTransplante(DominioSimNao.N);
		especialidade.setIndProntoAtendimento(DominioSimNao.N);
		especialidade.setIndImpSoServico(DominioSimNao.N);
		especialidade.setIndImpSolicConsultoria(false);
		especialidade.setIndEspPediatrica(false);
		
		return especialidade;

	}
	
	/**
	 * Pesquisa de especialidade genérica por nome ou código
	 * 
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 */
	public List<AghEspecialidades> pesquisarEspecialidadeGenerica(
			String strPesquisa) {
		return this.getAghuFacade().pesquisarEspecialidadeGenerica(strPesquisa, MAX_RESULTS);
	}
	
	public Long pesquisarEspecialidadeGenericaCount(String strPesquisa) {
		return this.getAghuFacade().pesquisarEspecialidadeGenericaCount(strPesquisa);
	}
	
	
	public List<AghEspecialidades> pesquisarTodasEspecialidades(String strPesquisa) {
		return this.getAghuFacade().pesquisarTodasEspecialidades(strPesquisa);
	}
	
	public Long pesquisarTodasEspecialidadesCount(String strPesquisa) {
		return this.getAghuFacade().pesquisarTodasEspecialidadesCount(strPesquisa);
	}
	
	/**
	 * Pesquisa de especialidade genérica por nome ou código levando em conta
	 * a idade do paciente internado e o índice de especialidade interna
	 * 
	 * Pesquisa utilizada na tela de internação.
	 * 
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa, idadePaciente
	 */
	public List<AghEspecialidades> pesquisarEspecialidadeInternacao(
			String strPesquisa, Short idadePaciente, DominioSimNao indUnidadeEmergencia) {
		return this.getAghuFacade().pesquisarEspecialidadeInternacao(strPesquisa, idadePaciente, indUnidadeEmergencia, MAX_RESULTS);
	}
	
	/**
	 * @dbtables AghEspecialidades select
	 * @param strPesquisa
	 * @param idadePaciente
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadeSolicitacaoInternacao(
			String strPesquisa, Short idadePaciente) {
		return this.getAghuFacade().pesquisarEspecialidadeSolicitacaoInternacao(strPesquisa, idadePaciente, MAX_RESULTS);
	}
	
	private void ajustaCodigoEspDasCaracteristicas(AghEspecialidades especialidade) {
		// seta código da especialidade nas caracteristicas que ainda não tem
		if (especialidade.getCaracteristicas() != null) {
			for (AghCaractEspecialidades ace : especialidade.getCaracteristicas()) {
				if (ace.getId().getEspSeq() == null) {
					ace.getId().setEspSeq(especialidade.getSeq());
				}
			}
		}		
	}
	
	
	/**
	 * Método responsável pela persistência de uma especialidade.
	 * 
	 * @dbtables AghEspecialidades insert,update
	 * @param especialidade
	 * @throws ApplicationBusinessException
	 */
	public void persistirEspecialidade(AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		especialidade.setServidor(servidorLogado);
		if (especialidade.getSeq() == null) {
			// inclusão
			this.incluirEspecialidade(especialidade);
		} else {
			// edição
			this.atualizarEspecialidade(especialidade);
		}
	}
	
	private boolean possuiAgenda(AghEspecialidades especialidade) {
		List<AghEspecialidades> agendas = this.pesquisarEspecialidades(
			0, 1, null, true, null, null, null,
			especialidade.getSeq(), null, null, null
		);
		return !agendas.isEmpty();
	}

	private boolean possuiAgendaAtiva(AghEspecialidades especialidade) {
		List<AghEspecialidades> agendas = this.pesquisarEspecialidades(
			0, 1, null, true, null, null, null,
			especialidade.getSeq(), null, null, DominioSituacao.A
		);
		return !agendas.isEmpty();
	}
	
	// ORADB: AGHK_ESP_RN.RN_ESPP_VER_DESATIVA cursor C_GRADE
	private boolean possuiGradeAgendamento(AghEspecialidades especialidade) throws ApplicationBusinessException {
		/*
		CURSOR c_grade (c_esp agh_especialidades.seq%type) IS
		SELECT  NVL(COUNT(*),0)
		FROM   aac_grade_agendamen_consultas grd
		WHERE  grd.esp_seq = c_esp
		AND   aacc_ver_sit_grade(grd.seq,SYSDATE) = 'A';
		*/
		List<AacGradeAgendamenConsultas> gradeList = this.getAmbulatorioFacade().listarGradesAgendamentoConsultaPorEspecialidade(especialidade);
		
		Date hoje = new Date();
		for (AacGradeAgendamenConsultas grade : gradeList) {
			String retorno = this.getAmbulatorioFacade().verificaSituacaoGrade(grade, hoje);
			if (retorno.equals("A")) {
				return true;
			}
		}
		return false;
	}
	
	// ORADB: Trigger AGHT_ESP_ARU
	private void journalUpdate(AghEspecialidades velho, AghEspecialidades novo) {
		
		if (
			!CoreUtil.igual(velho.getSigla(), novo.getSigla()) ||
			!CoreUtil.igual(velho.getNomeEspecialidade(), novo.getNomeEspecialidade()) ||
			!CoreUtil.igual(velho.getNomeReduzido(), novo.getNomeReduzido()) ||
			!CoreUtil.igual(velho.getIdadeMinPacAmbulatorio(), novo.getIdadeMinPacAmbulatorio()) ||
			!CoreUtil.igual(velho.getIdadeMaxPacAmbulatorio(), novo.getIdadeMaxPacAmbulatorio()) ||
			!CoreUtil.igual(velho.getIdadeMinPacInternacao(), novo.getIdadeMinPacInternacao()) ||
			!CoreUtil.igual(velho.getIdadeMaxPacInternacao(), novo.getIdadeMaxPacInternacao()) ||
			!CoreUtil.igual(velho.getIndEspInterna(), novo.getIndEspInterna()) ||
			!CoreUtil.igual(velho.getIndConsultoria(), novo.getIndConsultoria()) ||
			!CoreUtil.igual(velho.getClinica(), novo.getClinica()) ||
			!CoreUtil.igual(velho.getServidor(), novo.getServidor()) ||
			!CoreUtil.igual(velho.getEspecialidade(), novo.getEspecialidade()) ||
			!CoreUtil.igual(velho.getCentroCusto(), novo.getCentroCusto()) ||
			!CoreUtil.igual(velho.getServidorChefe(), novo.getServidorChefe()) ||
			!CoreUtil.igual(velho.getIndSituacao(), novo.getIndSituacao()) ||
			!CoreUtil.igual(velho.getCapacReferencial(), novo.getCapacReferencial()) ||
			!CoreUtil.igual(velho.getIndAtendHospDia(), novo.getIndAtendHospDia()) ||
			!CoreUtil.igual(velho.getEspecialidadeAgrupaLoteExame(), novo.getEspecialidadeAgrupaLoteExame()) ||
			!CoreUtil.igual(velho.getIndSugereProfInternacao(), novo.getIndSugereProfInternacao()) ||
			!CoreUtil.igual(velho.getIndHoraDispFeriado(), novo.getIndHoraDispFeriado()) ||
			!CoreUtil.igual(velho.getEpcSeq(), novo.getEpcSeq()) ||
			!CoreUtil.igual(velho.getIndImprimeAgenda(), novo.getIndImprimeAgenda()) ||
			!CoreUtil.igual(velho.getIndEmiteBoletimAtendimento(), novo.getIndEmiteBoletimAtendimento()) ||
			!CoreUtil.igual(velho.getIndEmiteTicket(), novo.getIndEmiteTicket()) ||
			!CoreUtil.igual(velho.getIndEnviaMensagem(), novo.getIndEnviaMensagem()) ||
			!CoreUtil.igual(velho.getIndAcompPosTransplante(), novo.getIndAcompPosTransplante()) ||
			!CoreUtil.igual(velho.getMediaPermanencia(), novo.getMediaPermanencia()) ||
			!CoreUtil.igual(velho.getIndProntoAtendimento(), novo.getIndProntoAtendimento()) ||
			!CoreUtil.igual(velho.getIndImpSoServico(), novo.getIndImpSoServico()) ||
			!CoreUtil.igual(velho.getMaxQuantFaltas(), novo.getMaxQuantFaltas())
		)
		{
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AghEspecialidadesJn especialidadeJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AghEspecialidadesJn.class, servidorLogado.getUsuario());
			
			especialidadeJn.setSeq(velho.getSeq());
			especialidadeJn.setSigla(velho.getSigla());
			especialidadeJn.setNomeEspecialidade(velho.getNomeEspecialidade());
			especialidadeJn.setNomeReduzido(velho.getNomeReduzido());
			especialidadeJn.setIdadeMinPacAmbulatorio(velho.getIdadeMinPacAmbulatorio());
			especialidadeJn.setIdadeMaxPacAmbulatorio(velho.getIdadeMaxPacAmbulatorio());
			especialidadeJn.setIdadeMinPacInternacao(velho.getIdadeMinPacInternacao());
			especialidadeJn.setIdadeMaxPacInternacao(velho.getIdadeMaxPacInternacao());
			especialidadeJn.setIndEspInterna(velho.getIndEspInterna());
			especialidadeJn.setIndConsultoria(velho.getIndConsultoria());
			especialidadeJn.setClinica(velho.getClinica());
			especialidadeJn.setServidor(velho.getServidor());
			especialidadeJn.setEspecialidade(velho.getEspecialidade());
			especialidadeJn.setCentroCusto(velho.getCentroCusto());
			especialidadeJn.setServidorChefe(velho.getServidorChefe());
			especialidadeJn.setIndSituacao(velho.getIndSituacao());
			especialidadeJn.setCapacReferencial(velho.getCapacReferencial());
			especialidadeJn.setIndAtendHospDia(velho.getIndAtendHospDia());
			especialidadeJn.setEspecialidadeAgrupaLoteExame(velho.getEspecialidadeAgrupaLoteExame());
			especialidadeJn.setIndSugereProfInternacao(velho.getIndSugereProfInternacao());
			especialidadeJn.setIndHoraDispFeriado(velho.getIndHoraDispFeriado());
			especialidadeJn.setEpcSeq(velho.getEpcSeq());
			especialidadeJn.setIndImprimeAgenda(velho.getIndImprimeAgenda());
			especialidadeJn.setIndEmiteBoletimAtendimento(velho.getIndEmiteBoletimAtendimento());
			especialidadeJn.setIndEmiteTicket(velho.getIndEmiteTicket());
			especialidadeJn.setIndEnviaMensagem(velho.getIndEnviaMensagem());
			especialidadeJn.setIndAcompPosTransplante(velho.getIndAcompPosTransplante());
			especialidadeJn.setMediaPermanencia(velho.getMediaPermanencia());
			especialidadeJn.setIndProntoAtendimento(velho.getIndProntoAtendimento());
			especialidadeJn.setIndImpSoServico(velho.getIndImpSoServico());
			especialidadeJn.setMaxQuantFaltas(velho.getMaxQuantFaltas());
			this.getAghuFacade().inserirAghEspecialidadesJn(especialidadeJn, false);
		}
		
	}
	private void atualizarEspecialidade(AghEspecialidades especialidade)
	throws ApplicationBusinessException {
		try {
			IAghuFacade aghuFacade = this.getAghuFacade();
			
			this.ajustaCodigoEspDasCaracteristicas(especialidade);
			this.validarDados(especialidade);
			
			AghEspecialidades espOriginal = aghuFacade.obterEspecialidade(especialidade.getSeq());
			this.validarDadosAlteracao(espOriginal, especialidade);
			this.journalUpdate(espOriginal, especialidade);			
			aghuFacade.desatacharAghEspecialidades(espOriginal);
			aghuFacade.atualizarAghEspecialidades(especialidade, true);
		} catch (PersistenceException | BaseException e ) {
			LOG.error("Erro ao atualizar a especialidade.", e);
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ATUALIZAR_ESPECIALIDADE
			);
		} 
	}
	
	// Identifica constraint que falhou no banco de dados e lança exceção de 
	// negócio correspondente (para exibir mensagem amigável ao usuário)
	private void lancaErroConstraint(PersistenceException pe) throws ApplicationBusinessException {
		Throwable cause = pe.getCause();
		if (cause instanceof ConstraintViolationException) {
			ConstraintViolationException cve = (ConstraintViolationException) cause;
			try {
				if (cve.getConstraintName() == null) {
					throw new IllegalArgumentException();
				}
				String constraint_name = cve.getConstraintName().substring(cve.getConstraintName().indexOf(".")+1);
				Constraint constraint = Constraint.valueOf(constraint_name); // lança IllegalArgumentException se não existir
				throw new ApplicationBusinessException(constraint.getExceptionCode());
			} catch (IllegalArgumentException iae) {
				logError("ConstraintViolationException - Constraint não identificada", pe);
				throw new ApplicationBusinessException(EspecialidadesCRUDExceptionCode.ERRO_PERSISTIR_ESPECIALIDADE);
			}
		}
	}
	
	private void incluirEspecialidade(AghEspecialidades especialidade)
	throws ApplicationBusinessException {
		try {
			this.validarDados(especialidade);
			this.getAghuFacade().persistirAghEspecialidades(especialidade);
			flush();
		} catch (PersistenceException e) {
			especialidade.setSeq(null);
			lancaErroConstraint(e);
			logError("Erro ao incluir a especialidade.", e);
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_PERSISTIR_ESPECIALIDADE
			);
		}
	}

	/**
	 * Método responsável pelas validações dos dados de especialidade
	 * Método utilizado para inclusão e atualização de
	 * especialidade.
	 * 
	 * @param especialidade
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void validarDados(AghEspecialidades especialidade)
	throws ApplicationBusinessException {
		
		// Centro de custo obrigatório. Deve ser ativo e possuir chefia
		FccCentroCustos cc = especialidade.getCentroCusto();
		
		//Verifica se sigla já existe
		AghEspecialidades especialidadeExistente = obterEspecialidadePorSigla(especialidade.getSigla());
		if (especialidadeExistente != null && !especialidadeExistente.getSeq().equals(especialidade.getSeq())){
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_SIGLA_JA_EXISTENTE
			);
		}
		
		if (cc == null || !cc.isAtivo() || cc.getRapServidor() == null) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_CENTRO_CUSTO
			);
		}
		
		// Clínica obrigatório.
		if (especialidade.getClinica() == null || especialidade.getClinica().getCodigo() == null) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_CLINICA
			);
		}
		
		
		// Chefia deve ser igual a chefia do Centro de Custo
		if (especialidade.getServidorChefe() == null || !especialidade.getServidorChefe().equals(cc.getRapServidor())) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_CHEFIA
			);				
		}
		
		// AGH_ESP_CK19 (ind_imp_solic_consultoria = 'N' or (ind_imp_solic_consultoria = 'S' and  ind_consultoria = 'S')
		if (especialidade.getIndImpSolicConsultoria() && !especialidade.isIndicConsultoria()) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_SOLIC_CONSULTORIA
			);				
		}
		
		// Idades min/max obrigatórias, maiores que zero e min menor ou igual a max.
		// AGH_ESP_CK3
		// AGH_ESP_CK4
		// AGH_ESP_CK8
		if (
			especialidade.getIdadeMinPacAmbulatorio() == null ||
			especialidade.getIdadeMaxPacAmbulatorio() == null ||
			especialidade.getIdadeMinPacInternacao() == null ||
			especialidade.getIdadeMaxPacInternacao() == null ||
			especialidade.getIdadeMinPacAmbulatorio() < 0 ||
			especialidade.getIdadeMaxPacAmbulatorio() < 0 ||
			especialidade.getIdadeMinPacInternacao() < 0 ||
			especialidade.getIdadeMaxPacInternacao() < 0 ||
			especialidade.getIdadeMinPacAmbulatorio() > especialidade.getIdadeMaxPacAmbulatorio() || 
			especialidade.getIdadeMinPacInternacao() > especialidade.getIdadeMaxPacInternacao() 
		) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_IDADES_MIN_MAX
			);
		}
		
		// Capacidade referencial da especialidade na internação deve ser maior que zero (AGH-00115)
		// AGH_ESP_CK6 (nvl(capac_referencial,0) >= 0)
		if (
			especialidade.getCapacReferencial() != null &&
			especialidade.getCapacReferencial() < 0
		) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_CAPAC_REFERENCIAL_NEG
			);				
		}
		
		// Capacidade referencial só deve ser informada para especialidades que internam (AGH-00116)
		// AGH_ESP_CK7 (ind_esp_interna = 'N' and nvl(capac_referencial,0) = 0) or (ind_esp_interna in ('I','U') and (capac_referencial is null or capac_referencial is not null))
		if (
			especialidade.getIndEspInterna().equals(DominioEspecialidadeInterna.N) && 
			especialidade.getCapacReferencial() != null &&
			especialidade.getCapacReferencial() > 0
		) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_CAPAC_REFERENCIAL
			);
		}
		
	}
	
	/**
	 * Método responsável pelas validações dos dados de especialidade
	 * para atualização de especialidade.
	 * 
	 * @param especialidade
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void validarDadosAlteracao(AghEspecialidades espOriginal, AghEspecialidades especialidade)
	throws ApplicationBusinessException {
		
		// Nome da especialidade não pode ser alterado
		if (especialidade.getEspecialidade() == null && !espOriginal.getNomeEspecialidade().trim().equals(especialidade.getNomeEspecialidade().trim())) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_NOME_ESPECIALIDADE_ALTERADO
			);
		}
		
		// Validações ao desativar
		if (espOriginal.isAtivo() && !especialidade.isAtivo()) {
			if (espOriginal.getEspecialidade() == null) { 
				// Só pode desativar especialidade genérica se não existem agendas ativas vinculadas.
				if (possuiAgendaAtiva(espOriginal)) {
					throw new ApplicationBusinessException(
						EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_POSSUI_AGENDAS
					);
				}
			}
			// Só pode desativar especialidade/agenda se não existem grades de agendamento ativas
			if (possuiGradeAgendamento(espOriginal)) { 
				throw new ApplicationBusinessException(
					EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_POSSUI_GRADE_AGENDAMENTO
				);					
			}
		}
		
		// Só pode ativar uma agenda se a especialidade genérica 
		// vinculada estiver ativa
		if (!espOriginal.isAtivo() && especialidade.isAtivo()) {
			if (espOriginal.getEspecialidade() != null) { // Agenda
				if (!espOriginal.getEspecialidade().isAtivo()) { 
					throw new ApplicationBusinessException(
						EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_GENERICA_INATIVA
					);
				}
			}
		}
		
		// Se a especialidade atual possuir agendas vinculadas então ela não pode ser 
		// vinculada a uma especialidade genérica (tornar-se-ia agenda e genérica ao mesmo tempo)
		if (possuiAgenda(espOriginal) && especialidade.getEspecialidade() != null) {
			throw new ApplicationBusinessException(
				EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_GENERICA_GENERICA
			);
		}
		
		// Não pode adicionar características a uma especialidade inativa
		if (!especialidade.isAtivo() && especialidade.getCaracteristicas() != null) {
			for (AghCaractEspecialidades ace : especialidade.getCaracteristicas()) {
				if (!espOriginal.possuiCaracteristica(ace.getId().getCaracteristica())) {
					throw new ApplicationBusinessException(
						EspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_INATIVA
					);						
				}
			}
		}		

	}
	
	/**
	 * @dbtables AghEspecialidades select
	 * @param seq
	 * @return
	 */
	public AghEspecialidades obterEspecialidade(Short seq) {
		return this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seq);
	}
	
	public AghEspecialidades obterAghEspecialidadesPorChavePrimaria(Short chavePrimaria, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin) {
		return this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(chavePrimaria, fetchArgsInnerJoin, fetchArgsLeftJoin);
	}
	
	
	/***
	 * 
	 * Obtém uma especialidade baseado na sigla
	 * @dbtables AghEspecialidades select
	 * @author Stanley Araujo
	 * @param sigla - Sigla da especialidade
	 * @return Uma instância de AghEspecialidades
	 * */
	public AghEspecialidades obterEspecialidadePorSigla(String sigla) {
		return this.getAghuFacade().obterEspecialidadePorSigla(sigla);
	}
	
	/**
	 * @dbtables AghEspecialidades select
	 * @param seq
	 * @return
	 */
	public AghEspecialidades obterEspecialidadeAtiva(short seq) {
		AghEspecialidades esp = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seq);		
		
		if (isAtiva(esp)) {
			return esp;
		}
		else {
			return null;
		}
	}
	
	public boolean isAtiva(AghEspecialidades esp) {
		return (esp != null && esp.isAtivo());
	}
	
	/**
	 * Obtem uma lista de especialidades de pediatria sem à ocorrência de especialidade genérica
	 * @return lista de especialidades
	 */
	public List<AghEspecialidades> obterEspecialidadePediatria() {
		return this.getAghuFacade().obterEspecialidadePediatria();
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.iAmbulatorioFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public List<AghEspecialidades> obterEspecialidadePorSiglas(List<String> siglas) {
		return this.getAghuFacade().obterEspecialidadePorSiglas(siglas);
	}
	
}
