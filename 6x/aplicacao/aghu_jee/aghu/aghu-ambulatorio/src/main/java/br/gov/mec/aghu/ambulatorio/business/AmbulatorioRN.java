package br.gov.mec.aghu.ambulatorio.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaEspecialidadeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacFormaEspecialidadeJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacNivelBuscaDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAtestadosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamInterconsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLogEmUsosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituarioCuidadoDAO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.ObjetosOracleException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaEspecialidade;
import br.gov.mec.aghu.model.AacFormaEspecialidadeId;
import br.gov.mec.aghu.model.AacFormaEspecialidadeJn;
import br.gov.mec.aghu.model.AacNivelBusca;
import br.gov.mec.aghu.model.AacNivelBuscaId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamExtratoControlesId;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.MamLogEmUsos;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MamReceituarioCuidado;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptPrescricaoPacienteId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptPrescricaoPacienteDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * Migração da package genérica de ambulatórios e de procedures de uso geral deste módulo
 * 
 *  ORADB: AACK_AAC_RN
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class AmbulatorioRN extends BaseBusiness {
	
	private static final Integer NACIONALIDADE_EIRE = 99;
	private static final Integer NACIONALIDADE_BRASILEIRO = 10;
	private static final String _HIFEN_ = " - ";
	
	@EJB
	private AtendimentoPacientesAgendadosRN atendimentoPacientesAgendadosRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	@EJB
	private PesquisarPacientesAgendadosON pacientesAgendadosON;
	
	private static final Log LOG = LogFactory.getLog(AmbulatorioRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@Inject
	private AacNivelBuscaDAO aacNivelBuscaDAO;
	
	@Inject
	private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	@Inject
	private AacFormaEspecialidadeJnDAO aacFormaEspecialidadeJnDAO;
	
	@Inject
	private MamControlesDAO mamControlesDAO;
	
	@Inject
	private MamExtratoControlesDAO mamExtratoControlesDAO;
	
	@Inject
	private MamProcedimentoDAO mamProcedimentoDAO;
	
	@Inject
	private MamLogEmUsosDAO mamLogEmUsosDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AacFormaEspecialidadeDAO aacFormaEspecialidadeDAO;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ProcedimentoRN procedimentoRN;	
	
	@Inject
	private MamAtestadosDAO mamAtestadosDAO;
	
	@Inject
	private MamReceituarioCuidadoDAO mamReceituarioCuidadoDAO;
	
	@Inject
	MamInterconsultasDAO mamInterconsultasDAO;
	
	@Inject
	MptPrescricaoPacienteDAO mptPrescricaoPacienteDAO;
	
	@Inject
	private FatProcedHospInternosDAO fatProcedHospInternosDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1431334637452812962L;

	private static final String NEWLINE = System.getProperty("line.separator");
	
	public enum AmbulatorioRNExceptionCode implements BusinessExceptionCode {

		AAC_00070, AAC_00064, AAC_00679, AIP_00514, MSG_PAC_1, MSG_PAC_2, MSG_PAC_4, MAM_01959, MAM_01160, MSG_PACIENTE_OUTRA_CONSULTA;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	/**
	 * Atualiza impressão tratamento fisiátrico.
	 * @param atdSeq
	 * @param pteSeq
	 */
	public void atualizarIndPrcrImpressaoTratamentoFisiatrico(Integer atdSeq, Integer pteSeq) {
		MptPrescricaoPacienteId mptPrescricaoPacienteId = new MptPrescricaoPacienteId();
		mptPrescricaoPacienteId.setAtdSeq(atdSeq);
		mptPrescricaoPacienteId.setSeq(pteSeq);
		
		MptPrescricaoPaciente prescricaoPaciente = mptPrescricaoPacienteDAO.obterPorChavePrimaria(mptPrescricaoPacienteId);
		prescricaoPaciente.setPrescricaoImpressao(Boolean.TRUE);
		mptPrescricaoPacienteDAO.atualizar(prescricaoPaciente);		
	}
	
	/**
	 * Atualiza atestado indicando que foi impresso.
	 * @param seq
	 */
	public void atualizarIndImpressaoAtestado(Long seq) {		
		MamAtestados atestado = mamAtestadosDAO.obterPorChavePrimaria(seq);
		atestado.setIndImpresso(Boolean.TRUE);
		mamAtestadosDAO.atualizar(atestado);		
	}
	
	/**
	 * Atualiza interconsulta indicando que foi impresso.
	 * @param seq
	 */		
	public void atualizarIndImpressaoInterconsultas(Long seq) {		
		MamInterconsultas interconsultas = mamInterconsultasDAO.obterPorChavePrimaria(seq);
		interconsultas.setIndImpresso("S");
		mamInterconsultasDAO.atualizar(interconsultas);		
	}
	
	/**
	 * Atualiza receituario cuidado indicando que foi impresso.
	 * @param seq
	 */
	
	public void atualizarIndImpressaoReceituarioCuidado(Long seq) {		
		MamReceituarioCuidado receituarioCuidado = mamReceituarioCuidadoDAO.obterPorChavePrimaria(seq);
		receituarioCuidado.setImpresso(Boolean.TRUE);
		mamReceituarioCuidadoDAO.atualizar(receituarioCuidado);		
	}
	
	/**
	 * ORADB: RN_AACP_ATU_SERVIDOR
	 * @param: RapServidores servidor
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public RapServidores atualizaServidor(RapServidores servidor)throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado != null){
			servidor = servidorLogado;
		}else{
			AmbulatorioRNExceptionCode.AAC_00064.throwException();
		}
		return servidor;
	}
	
	/**
	 * ORADB: RN_AACP_VER_ESP_ATI
	 * @param: AghEspecialidades especialidade
	 * @throws ApplicationBusinessException
	 */
	public void verificaSituacaoEspecialidade(AghEspecialidades especialidade)
			throws ApplicationBusinessException {

		if (especialidade.getIndSituacao() != DominioSituacao.A){
			AmbulatorioRNExceptionCode.AAC_00070.throwException();
		}
	}
	
	/**
	 * ORADB: RN_AACP_ENVIA_EMAIL
	 * @param: String mensagem
	 * @throws ApplicationBusinessException
	 *  
	 */
	public void enviaEmail(String mensagem)throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		String remetente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();
		List<String> destinatariosOcultos = null;
		String assunto = "Criada nova Grade";
		
		List<String> destinatarios = new ArrayList<String>();
		if (servidorLogado !=null && servidorLogado.getEmail() != null){
			destinatarios.add(servidorLogado.getEmail());
		}
		
		AghParametros emailGrade = getParametroFacade().buscarAghParametro( AghuParametrosEnum.P_AGHU_EMAIL_GRADE_CRIADA);
		
		if (emailGrade != null && StringUtils.isNotBlank(emailGrade.getVlrTexto())) {
			StringTokenizer emailPara = new StringTokenizer(emailGrade.getVlrTexto(), ";");
			while (emailPara.hasMoreTokens()) {
				destinatarios.add(emailPara.nextToken().trim().toLowerCase());
			}
			getEmailUtil().enviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, mensagem);

		}
		
	}
	
	
	/**
	 * ORADB: Procedure MAMP_CHECK_OUT_INT
	 */
	public void gerarCheckOut(final Integer seq, final Integer pacCodigo,
			final String tipoAltaMedicaCodigoOld,
			final String tipoAltaMedicaCodigo, final Short unfSeqOld,
			final Short unfSeq, final Boolean pacienteInternadoOld,
			final Boolean pacienteInternacao) throws ApplicationBusinessException {

			try {
				getAghuFacade().gerarCheckOut(seq, pacCodigo,
						tipoAltaMedicaCodigoOld, tipoAltaMedicaCodigo, unfSeqOld,
						unfSeq, pacienteInternadoOld, pacienteInternacao);
			} catch (ObjetosOracleException e) {
				throw new ApplicationBusinessException(e.getCode(), e);
			}
		
	}

	/**
	 * ORADB: Procedure MAMP_ATU_TRG_EMATEND
	 */
	public void atualizarSituacaoTriagem(final Integer pacCodigo)
			throws ApplicationBusinessException {
		try {
			getAghuFacade().atualizarSituacaoTriagem(pacCodigo);
		} catch (ObjetosOracleException e) {
			throw new ApplicationBusinessException(e.getCode(), e);
		}
	}	
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	/**
	 * @return the emailUtil
	 */
	public EmailUtil getEmailUtil() {
		return emailUtil;
	}
	
	/**
	 * ORADB: Procedure MAMP_INTEGRA_PROCED
	 */	
	public void integraProcedimento(final Integer seq) throws BaseException {
		List<MpmProcedEspecialDiversos> lista =  getPrescricaoMedicaFacade().listarProcedimentosEspeciaisAtivosComProcedimentoIntAtivoPorPhiSeq(seq);
		for(MpmProcedEspecialDiversos proc : lista) {
			if(proc.getSeq() != null) {
				if(!getMamProcedimentoDAO().existeProcedimentoPorPedSeq(proc.getSeq())) {
					try {
						//INSERE
						/*
				           INSERT INTO mam_procedimentos
				           (descricao,
				            ped_seq,
				            ind_situacao,
				            ind_generico)
				           VALUES
				           (v_desc,
				            v_seq,
				            'A',
				            'N');
						 * */						
						MamProcedimento procedimento = new MamProcedimento();
						procedimento.setDescricao(proc.getDescricao());
						procedimento.setProcedEspecialDiverso(proc);
						procedimento.setSituacao(DominioSituacao.A);
						procedimento.setGenerico(false);
						getProcedimentoRN().inserir(procedimento);
					} catch(BaseException e) {
						logError("Exceção BaseException capturada, lançada para cima.");
						throw e;
					} catch(Exception e) {
						logError("Exceção capturada: ", e);
						throw new ApplicationBusinessException(AmbulatorioRNExceptionCode.MAM_01959);
					}
				}
			}
		}
	}
	
	
	/**
	 * ORADB: Procedure p_ver_outra_consulta
	 */	
	public void verificaPacienteOutraConsulta(Integer numeroConsulta) throws BaseException {
		AacConsultas consulta = aacConsultasDAO.obterPorChavePrimaria(numeroConsulta);
		DataInicioFimVO turno = consulta.getGradeAgendamenConsulta().getIndAvisaConsultaTurno() ? pacientesAgendadosON.definePeriodoTurno(consulta.getDtConsulta()) : null;
		
		String labelZona;
		String labelSala;
		
		try {
			labelZona = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			labelSala = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			labelZona = "Zona";
			labelSala = "Sala";
		}
		
		List<AacConsultas> consultas =getAacConsultasDAO().obterConsultasPorPacienteUnfSalaData(consulta.getPaciente().getCodigo(), consulta.getDtConsulta(), 
				consulta.getGradeAgendamenConsulta(), turno);
		
		BaseListException listaException = new BaseListException();
		
		for (AacConsultas c : consultas){
			if (numeroConsulta==null || !numeroConsulta.equals(c.getNumero())) {
				listaException.add(new ApplicationBusinessException(AmbulatorioRNExceptionCode.MSG_PACIENTE_OUTRA_CONSULTA, Severity.WARN, 
						labelZona, labelSala,
						c.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getUnidadeFuncional().getSigla(),
						c.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala(),
						DateUtil.dataToString(c.getDtConsulta(), "HH:mm")));
			}	
		}	
		
		if(listaException.hasException()) {
			throw listaException;
		}
	}
	
	
	
	/**
	 * ORADB: P_ATUALIZA_CONTROLE
	 */	
	public void atualizaControleConsultaSituacao(AacConsultas consulta, MamSituacaoAtendimentos situacao, String nomeMicrocomputador) throws ApplicationBusinessException{
		
		MamControles controle = null;		
		if (consulta.getControle()==null){
			controle = new MamControles();
			controle.setCriadoEm(new Date());
			controle.setConsulta(consulta);
			controle.setPaciente(consulta.getPaciente());
		}else{
			controle=consulta.getControle();
		}
		controle.setDthrMovimento(new Date());
		controle.setSituacao(DominioSituacaoControle.L);
		controle.setSituacaoAtendimento(situacao);
		if (controle.getSeq()==null){
			getMarcacaoConsultaRN().inserirControles(controle, nomeMicrocomputador);
		}else{
			getMarcacaoConsultaRN().atualizarControles(controle, nomeMicrocomputador);
		}
		MamExtratoControles extrato = new MamExtratoControles();
		extrato.setControle(controle);
		extrato.setDthrMovimento(new Date());
		extrato.setId(new MamExtratoControlesId(controle.getSeq(), getMamExtratoControlesDAO().nextSeqp(controle)));
		extrato.setSituacaoAtendimento(situacao);
		getMarcacaoConsultaRN().inserirExtratoControles(extrato, nomeMicrocomputador);
		if (controle.getExtratoControles()==null){
			controle.setExtratoControles(new HashSet<MamExtratoControles>());
		}
		controle.getExtratoControles().add(extrato);
		consulta.setControle(controle);
	}
	
	
	/**
	 * ORADB: AIPC_VER_DADOS_CNS
	 */		
	public List<String> validaDadosPaciente(Integer codigo){
		
		List<String> retorno = new ArrayList<String>();
		AipPacientes paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codigo);
	
		if (paciente.getAipPacientesDadosCns()==null){
			retorno.add(AmbulatorioRNExceptionCode.MSG_PAC_1.name());
		}
		if (StringUtils.isBlank(paciente.getRg())) {
			retorno.add(AmbulatorioRNExceptionCode.MSG_PAC_2.name());
		}

		if (paciente.getAipNacionalidades() != null && !nacionalidadeBrasileira(paciente)
				&& !nacionalidadeEire(paciente)) {
			AipPacientesDadosCns dadosCns = paciente.getAipPacientesDadosCns();
			if (dadosCns != null && (dadosCns.getDataEntradaBr() == null || dadosCns.getDataNaturalizacao() == null
					|| dadosCns.getPortariaNatural() == null)) {
				retorno.add(AmbulatorioRNExceptionCode.MSG_PAC_4.name());
			}
		}
		return retorno;
	}
	
	private Boolean nacionalidadeEire(AipPacientes paciente) {
		return paciente.getAipNacionalidades().getCodigo().equals(NACIONALIDADE_EIRE);
	}
	private Boolean nacionalidadeBrasileira(AipPacientes paciente) {
		return paciente.getAipNacionalidades().getCodigo().equals(NACIONALIDADE_BRASILEIRO);
	}
	
	
	public boolean validarDadosPacienteAmbulatorio(Integer codigo){
		AipPacientes paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codigo);
	
		boolean validar=false;
		
		if(!paciente.isApresentarCadConfirmado()) {
			validar = true;
		}
		
		return validar;
	}	
		
	public Boolean verificarTrocaPacienteConsulta(Integer codigo){
		Boolean retorno = false;
		AipPacientes paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codigo);
		if (paciente.getProntuario() == null){
			retorno = true;
		}
		return retorno;
	}


	public void salvarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException {
		this.preInserirFormaAgendamento(formaAgendamento);
		this.getAacFormaAgendamentoDAO().persistir(formaAgendamento);
		this.getAacFormaAgendamentoDAO().flush();
	}

	/**
	 * ORADB AACT_FAG_BRI
	 * 
	 * @param formaAgendamento
	 */
	private void preInserirFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException {
		formaAgendamento.setCriadoEm(new Date());
		formaAgendamento.setAlteradoEm(new Date());
		formaAgendamento.setServidor(this.atualizaServidor(formaAgendamento.getServidor()));
	}

	public void alterarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException {
		this.preAtualizarFormaAgendamento(formaAgendamento);
		this.getAacFormaAgendamentoDAO().atualizar(formaAgendamento);
		this.getAacFormaAgendamentoDAO().flush();
	}

	/**
	 * ORADB AACT_FAG_BRU
	 * 
	 * @param formaAgendamento
	 */
	private void preAtualizarFormaAgendamento(AacFormaAgendamento formaAgendamento) throws BaseException {
		formaAgendamento.setAlteradoEm(new Date());
		formaAgendamento.setServidorAlterado(this.atualizaServidor(
				formaAgendamento.getServidorAlterado()));
	}

	public void salvarNivelBusca(AacNivelBusca nivelBusca) throws BaseException {
		this.preInserirNivelBusca(nivelBusca);
		getAacNivelBuscaDAO().persistir(nivelBusca);
		getAacNivelBuscaDAO().flush();
	}

	/**
	 * ORADB AACT_NBC_BRI
	 * 
	 * @param nivelBusca
	 * @throws BaseException
	 */
	private void preInserirNivelBusca(AacNivelBusca nivelBusca) throws BaseException {
		nivelBusca.setCriadoEm(new Date());
		nivelBusca.setAlteradoEm(new Date());
		nivelBusca.setServidor(this.atualizaServidor(nivelBusca.getServidor()));
	}

	public void alterarNivelBusca(AacNivelBusca nivelBusca) throws BaseException {
		this.preAtualizarNivelBusca(nivelBusca);
		aacNivelBuscaDAO.merge(nivelBusca);
	}

	/**
	 * ORADB AACT_NBC_BRU
	 */
	private void preAtualizarNivelBusca(AacNivelBusca nivelBusca) throws BaseException {
		nivelBusca.setAlteradoEm(new Date());

		if (nivelBusca.getServidorAlterado() == null) {
			nivelBusca.setServidorAlterado(this.atualizaServidor(nivelBusca.getServidorAlterado()));
		}
	}

	public void removerNivelBusca(AacNivelBuscaId id) {
		final AacNivelBusca nivelBusca = aacNivelBuscaDAO.obterPorChavePrimaria(id); 
		aacNivelBuscaDAO.remover(nivelBusca);
	}
	
	/**
	 * ORADB: AIPC_VER_RECADASTRO
	 */		
	private static final int DIFERENCA_MAXIMA_DATA_RECADASTRO = 24;
	private static final int DIFERENCA_MAXIMA_ULTIMA_CONSULTA = 24;
	public Boolean necessitaRecadastramentoPaciente(Integer pacienteCodigo, Date dtRecadastroPaciente, Date dtConsulta){
		Date dtUltimaConsulta = getAacConsultasDAO().ultimaDataConsultaPaciente(pacienteCodigo, dtConsulta);
		if (dtRecadastroPaciente!=null 
				&& DateUtil.difMeses(dtConsulta, dtRecadastroPaciente)>=DIFERENCA_MAXIMA_DATA_RECADASTRO){
			return true;
		}else if (dtUltimaConsulta!=null
				&& DateUtil.difMeses(dtConsulta, dtUltimaConsulta)>=DIFERENCA_MAXIMA_ULTIMA_CONSULTA){
			return true;
		}
		return false;
	}
	
	public void salvarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException {
		this.preInserirFormaEspecialidade(formaEspecialidade);
		getAacFormaEspecialidadeDAO().persistir(formaEspecialidade);
		getAacFormaEspecialidadeDAO().flush();
	}

	/**
	 * AACT_FGE_BRI
	 * 
	 * @param formaEspecialidade
	 * @throws BaseException
	 */
	private void preInserirFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException {
		if (formaEspecialidade.getDtInicio() != null
				&& DateUtil.validaDataTruncadaMaior(new Date(), formaEspecialidade.getDtInicio())) {
			throw new ApplicationBusinessException(AmbulatorioRNExceptionCode.AAC_00679);
		}

		formaEspecialidade.setCriadoEm(new Date());

		formaEspecialidade.setServidor(this.atualizaServidor(formaEspecialidade.getServidor()));
	}

	public void alterarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws BaseException {
		
		if (DateUtil.validaDataTruncadaMaior(new Date(), formaEspecialidade.getDtInicio())) {
			throw new ApplicationBusinessException(AmbulatorioRNExceptionCode.AAC_00679);
		}

		AacFormaEspecialidade oldFormaEspecialidade = aacFormaEspecialidadeDAO.obterOriginal(formaEspecialidade.getId());
		this.preAtualizarFormaEspecialidade(formaEspecialidade, oldFormaEspecialidade);
		formaEspecialidade = aacFormaEspecialidadeDAO.merge(formaEspecialidade);
		this.posAtualizarFormaEspecialidade(formaEspecialidade, oldFormaEspecialidade);
		
	}
	
	/**
	 * ORADB AACT_FGE_BRU
	 * 
	 * @param formaEspecialidade
	 * @param oldFormaEspecialidade
	 * @throws BaseException
	 */
	private void preAtualizarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade, AacFormaEspecialidade oldFormaEspecialidade)
			throws BaseException {
		if (!CoreUtil.igual(formaEspecialidade.getDtInicio(), oldFormaEspecialidade.getDtInicio())
				&& DateUtil.validaDataTruncadaMaior(new Date(), formaEspecialidade.getDtInicio())) {
			throw new ApplicationBusinessException(AmbulatorioRNExceptionCode.AAC_00679);
		}

		if (!CoreUtil.igual(formaEspecialidade.getDtFinal(), oldFormaEspecialidade.getDtFinal())
				&& DateUtil.validaDataTruncadaMaior(new Date(), formaEspecialidade.getDtFinal())) {
			throw new ApplicationBusinessException(AmbulatorioRNExceptionCode.AAC_00679);
		}
	}
	
	/**
	 * ORADB AACT_FGE_ARU
	 * 
	 * @param formaEspecialidade
	 * @param oldFormaEspecialidade
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizarFormaEspecialidade(AacFormaEspecialidade formaEspecialidade, AacFormaEspecialidade oldFormaEspecialidade) throws ApplicationBusinessException {
		if (CoreUtil.modificados(formaEspecialidade.getServidor(), oldFormaEspecialidade.getServidor())
				|| CoreUtil.modificados(formaEspecialidade.getId(), oldFormaEspecialidade.getId())
				|| CoreUtil.modificados(formaEspecialidade.getDtInicio(), oldFormaEspecialidade.getDtInicio())
				|| CoreUtil.modificados(formaEspecialidade.getDtFinal(), oldFormaEspecialidade.getDtFinal())
				|| CoreUtil.modificados(formaEspecialidade.getCriadoEm(), oldFormaEspecialidade.getCriadoEm())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AacFormaEspecialidadeJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD,
					AacFormaEspecialidadeJn.class, servidorLogado.getUsuario());

			jn.setSerVinCodigo(oldFormaEspecialidade.getServidor() != null ? oldFormaEspecialidade.getServidor().getId()
					.getVinCodigo() : null);
			jn.setFagCaaSeq(oldFormaEspecialidade.getId().getFagCaaSeq());
			jn.setFagTagSeq(oldFormaEspecialidade.getId().getFagTagSeq());
			jn.setFagPgdSeq(oldFormaEspecialidade.getId().getFagPgdSeq());
			jn.setEspSeq(oldFormaEspecialidade.getId().getEspSeq());
			jn.setDtInicio(oldFormaEspecialidade.getDtInicio());
			jn.setDtFinal(oldFormaEspecialidade.getDtFinal());
			jn.setCriadoEm(oldFormaEspecialidade.getCriadoEm());
			jn.setSerMatricula(oldFormaEspecialidade.getServidor() != null ? oldFormaEspecialidade.getServidor().getId()
					.getMatricula() : null);

			this.getAacFormaEspecialidadeJnDAO().persistir(jn);
			this.getAacFormaEspecialidadeJnDAO().flush();
		}
	}

	public void removerFormaEspecialidade(AacFormaEspecialidadeId id) throws ApplicationBusinessException {
		final AacFormaEspecialidade formaEspecialidade = aacFormaEspecialidadeDAO.obterPorChavePrimaria(id);
		preRemoverFormaEspecialidade(formaEspecialidade);
		aacFormaEspecialidadeDAO.remover(formaEspecialidade);
	}

	/**
	 * ORADB AACT_FGE_ARD
	 * 
	 * @param formaEspecialidade
	 * @param flush
	 * @throws ApplicationBusinessException 
	 */
	private void preRemoverFormaEspecialidade(AacFormaEspecialidade formaEspecialidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AacFormaEspecialidadeJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL,
				AacFormaEspecialidadeJn.class, servidorLogado.getUsuario());

		jn.setSerVinCodigo(formaEspecialidade.getServidor() != null ? formaEspecialidade.getServidor().getId().getVinCodigo() : null);
		jn.setFagCaaSeq(formaEspecialidade.getId().getFagCaaSeq());
		jn.setFagTagSeq(formaEspecialidade.getId().getFagTagSeq());
		jn.setFagPgdSeq(formaEspecialidade.getId().getFagPgdSeq());
		jn.setEspSeq(formaEspecialidade.getId().getEspSeq());
		jn.setDtInicio(formaEspecialidade.getDtInicio());
		jn.setDtFinal(formaEspecialidade.getDtFinal());
		jn.setCriadoEm(formaEspecialidade.getCriadoEm());
		jn.setSerMatricula(formaEspecialidade.getServidor() != null ? formaEspecialidade.getServidor().getId().getMatricula() : null);

		aacFormaEspecialidadeJnDAO.persistir(jn);
		aacFormaEspecialidadeJnDAO.flush();
	}

	/**
	 * ORADB P_VISUALIZA_CONSULTA
     *    
     *    DIVERSOS CURSORES FORA DO ESCOPO DE AMBULATORIO V1 ANTES DE USAR ESTE 
     *    METÓDO VERIFICAR SE ELE COBRE TODAS AS NECESSIDADES
     *    
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public StringBuffer visualizaConsulta(Integer numero, Integer codigo) throws BaseException {
		
		StringBuffer vAnamnese = new StringBuffer();
		StringBuffer vEvolucao = new StringBuffer();
		StringBuffer vNotasAdicionaisAnamnese = new StringBuffer();
		StringBuffer vNotasAdicionaisEvolucao = new StringBuffer();
		StringBuffer vConsulta = new StringBuffer();
		StringBuffer retorno = new StringBuffer();
		
		// NÃO IMPLEMENTADO
		// v_triagem := mamk_emg_visualiza.mamc_emg_vis_trg (v_trg_seq, 'CS');
		// NÃO IMPLEMENTADO
		// medicamentos ativos
		// NÃO IMPLEMENTADO
		// diagnósticos

		// anamnese
		List<MamAnamneses> anamneses = getMamAnamnesesDAO().obterAnamnesesPelaConsulta(numero);
		for(MamAnamneses anamnese : anamneses) {
			if(DominioIndPendenteAmbulatorio.P.equals(anamnese.getPendente())) {
				String cabPendente = getCabPendente(numero);
				vAnamnese.append(cabPendente);
				vAnamnese.append(NEWLINE);
				vAnamnese.append(NEWLINE);
				vAnamnese.append(getAtendimentoPacientesAgendadosRN().obtemDescricaoAnamnese(anamnese, false, null, false, false));
			}
			else {
				vAnamnese.append(getAtendimentoPacientesAgendadosRN().obtemDescricaoAnamnese(anamnese, false, null, true, false));
			}
		}
		// notas adicionais anamnese
		if(vAnamnese == null || vAnamnese.length() == 0) {
			AacConsultas consulta = getAacConsultasDAO().obterPorChavePrimaria(numero);
			vNotasAdicionaisAnamnese.append(getAtendimentoPacientesAgendadosRN().obtemDescricaoNotaAdicionalAna(consulta, null, false));
			if(vNotasAdicionaisAnamnese != null && vNotasAdicionaisAnamnese.length() > 0) {
				vAnamnese.append(NEWLINE);
				vAnamnese.append(vNotasAdicionaisAnamnese);
			}
		}
		// evolução
		List<MamEvolucoes> evolucoes = getMamEvolucoesDAO().obterEvolucoesPelaConsulta(numero);
		for(MamEvolucoes evolucao : evolucoes) {
			if(DominioIndPendenteAmbulatorio.P.equals(evolucao.getPendente())) {
				String cabPendente = getCabPendente(numero);
				vEvolucao.append(cabPendente);
				vEvolucao.append(NEWLINE);
				vEvolucao.append(NEWLINE);
				vEvolucao.append(getAtendimentoPacientesAgendadosRN().obtemDescricaoEvolucao(evolucao, false, null, false, false));
			}
			else {
				vEvolucao.append(getAtendimentoPacientesAgendadosRN().obtemDescricaoEvolucao(evolucao, false, null, true, false));
			}
		}
		
		AacConsultas consulta = getAacConsultasDAO().obterPorChavePrimaria(numero);
		
		// notas adicionais evolucao
		if(vEvolucao == null || vEvolucao.length() == 0) {
			vNotasAdicionaisEvolucao.append(getAtendimentoPacientesAgendadosRN().obtemDescricaoNotaAdicionalEvo(consulta, null, false));
			if(vNotasAdicionaisEvolucao != null && vNotasAdicionaisEvolucao.length() > 0) {
				vEvolucao.append(NEWLINE);
				vEvolucao.append(vNotasAdicionaisAnamnese);
			}
		}
		
		if(vAnamnese.length() == 0 && vEvolucao.length() == 0) {
			vConsulta.append(getAtendimentoPacientesAgendadosRN().obterDadosBasicosConsulta(consulta));
		}
		
		if(vConsulta.length() != 0) {
			retorno.append(vConsulta);
			retorno.append(NEWLINE);
		}
		if(vAnamnese.length() != 0) {
			retorno.append(vAnamnese);
			retorno.append(NEWLINE);
			retorno.append(obtemDescricaoProcedimentos(consulta));
		}
		if(vEvolucao.length() != 0) {
			retorno.append(vEvolucao);
			retorno.append(NEWLINE);
			retorno.append(obtemDescricaoProcedimentos(consulta));
		}
		
		return retorno;
	}
	
	public StringBuilder obtemDescricaoProcedimentos(AacConsultas consulta)
			throws ApplicationBusinessException {
		StringBuilder texto = new StringBuilder();

		Set<MamProcedimentoRealizado> procedimentosRealizados = getMamProcedimentoRealizadoDAO()
				.pesquisarProcedimentoPorNumeroConsulta(consulta.getNumero());

		if (procedimentosRealizados != null
				&& !procedimentosRealizados.isEmpty()) {
			texto.append("Procedimentos Realizados:");
			texto.append(NEWLINE);
		}
		
		for (MamProcedimentoRealizado procedimento : procedimentosRealizados) {
			String textoCid = "";
			if (procedimento.getCid() != null) {
				textoCid = montaTextoCid(procedimento.getCid());
			}
			texto.append(montaTextoDescricao(procedimento, textoCid));
		}
		return texto;
	}
	
	private String montaTextoCid(AghCid cid) {

		StringBuffer textoCid = new StringBuffer(", CID: ")
				.append(cid.getCodigo())
				.append(_HIFEN_)
				.append(cid.getDescricaoEditada() != null ? cid
						.getDescricaoEditada() : cid.getDescricao());

		return textoCid.toString();
	}
	
	private StringBuilder montaTextoDescricao(MamProcedimentoRealizado proc,
			String textoCid) {
		StringBuilder texto = new StringBuilder();

		if (proc.getQuantidade() == null || proc.getQuantidade().equals(0)) {
			texto.append(proc.getProcedimento().getDescricao());
		} else {
			texto.append(proc.getProcedimento().getDescricao())
					.append(", quantidade: ").append(proc.getQuantidade());
		}
		texto.append(textoCid)
		.append(NEWLINE);
		if (!proc.getPendente().equals(DominioIndPendenteAmbulatorio.V)
				&& proc.getSituacao().equals(DominioSituacao.I)) {
			texto.append("<<< EXCLUÍDO >>>");
		}

		return texto;
	}
	
	/**
	 * ORADB MAMC_GET_CAB_PEND
	 * @throws ApplicationBusinessException  
	 * 
	 */
	public String getCabPendente(Integer numero) throws ApplicationBusinessException {
		StringBuffer str = new StringBuffer(100);
		Object[] prof = new Object[4];
		String equipeNome = null;
		String agendaNome = null;
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		
		AacConsultas consulta = getAacConsultasDAO().obterPorChavePrimaria(numero);
		prof = getPrescricaoMedicaFacade().buscaConsProf(consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel());
		if(prof[1] != null) {
			equipeNome = " - Equipe: " + (String)prof[1];
		}
		
		if(consulta.getGradeAgendamenConsulta().getProfServidor() != null) {
			prof = getPrescricaoMedicaFacade().buscaConsProf(consulta.getGradeAgendamenConsulta().getProfServidor());
			if(prof[1] != null) {
				agendaNome = _HIFEN_ + (String)prof[1];
			}
		}
		
		str.append(consulta.getNumero())
		.append(_HIFEN_).append(fmt.format(consulta.getDtConsulta()))
		.append(_HIFEN_).append(consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade());
		if(equipeNome != null) {
			str.append(equipeNome);
		}
		if(agendaNome != null) {
			str.append(agendaNome);
		}
		str.append("/n >>>>>> Esta consulta não foi assinada no sistema e permanece na situação pendente <<<<<<");
		
		return str.toString();
	}
	
	public void validarSelecaoImpressaoConsultaAmbulatorio(Integer numero, Boolean selecionar) throws BaseException {
		Long quantidadeAnm = getMamAnamnesesDAO().obterQuantidadeAnamnesesPeloNumeroConsultaEIndPendente(numero, new DominioIndPendenteAmbulatorio[]{DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.V});
		Long quantidadeEvl = getMamEvolucoesDAO().obterQuantidadeEvolucoesPeloNumeroConsultaEIndPendente(numero, new DominioIndPendenteAmbulatorio[]{DominioIndPendenteAmbulatorio.A, DominioIndPendenteAmbulatorio.E, DominioIndPendenteAmbulatorio.V});
		if(selecionar) {
			Boolean assinado = false;
			if(quantidadeAnm > 0 || quantidadeEvl > 0) {
				assinado = true;
			}
			DominioIndAbsenteismo absenteismo = DominioIndAbsenteismo.N;
			AacConsultas consulta = (getAacConsultasDAO().obterConsulta(numero));
			if(consulta.getRetorno() != null) {
				absenteismo = consulta.getRetorno().getAbsenteismo();
			}
			
			if(!DominioIndAbsenteismo.R.equals(absenteismo) && !assinado) {
				throw new ApplicationBusinessException(AmbulatorioRNExceptionCode.AIP_00514);
			}
		}
	}

	public void removerFormaAgendamento(AacFormaAgendamento formaAgendamento) throws ApplicationBusinessException {
		aacFormaAgendamentoDAO.remover(formaAgendamento);
	}

	public void removerLogEmUso(MamLogEmUsos logEmUso, boolean flush) {
		this.getMamLogEmUsosDAO().remover(logEmUso);
		if (flush){
			this.getMamLogEmUsosDAO().flush();
		}
	}

	public AghAtendimentos obterAghAtendimentosPorSeq(Integer seq) {
		AghAtendimentos aghAtendimentos = aghAtendimentoDAO.obterAghAtendimentosPorSeq(seq);
		if (aghAtendimentos != null) {
			Hibernate.initialize(aghAtendimentos.getUnidadeFuncional());
		}
		return aghAtendimentos;
	}

	public FatProcedHospInternos obterCodigoDescricaoProcedimentoProTransplante(Integer codigoPaciente) throws ApplicationBusinessException{
		Long ps7 = (parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_AGHU_ACOMP_POS_CORNEA)).longValue();
		Long ps8 = (parametroFacade.buscarValorNumerico(AghuParametrosEnum.P_AGHU_ACOMP_POS)).longValue();
		
		return fatProcedHospInternosDAO.obterCodigoDescricaoProcedimentoProTransplante(ps7, ps8, codigoPaciente);
	}
	
	public List<AghEspecialidades> obterEspecialidadesPorSiglaOUNomeEspecialidade(String parametro) {
		List<AghEspecialidades> retorno = aghEspecialidadesDAO.obterEspecialidadesPorSiglaOUNomeEspecialidade(parametro, true);
		if(retorno != null && retorno.isEmpty()){
			retorno = aghEspecialidadesDAO.obterEspecialidadesPorSiglaOUNomeEspecialidade(parametro, false);
		}
		return retorno;
	}
	
	public Long obterEspecialidadesPorSiglaOUNomeEspecialidadeCount(String parametro){
		Long size = aghEspecialidadesDAO.obterEspecialidadesPorSiglaOUNomeEspecialidadeCount(parametro, true);
		if(size == null || size == 0){
			size =  aghEspecialidadesDAO.obterEspecialidadesPorSiglaOUNomeEspecialidadeCount(parametro, false);
		}
		return size;
	}

	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}
	
	protected MamControlesDAO getMamControlesDAO(){
		return mamControlesDAO;
	}

	protected MamExtratoControlesDAO getMamExtratoControlesDAO(){
		return mamExtratoControlesDAO;
	}	
	
	protected MarcacaoConsultaRN getMarcacaoConsultaRN(){
		return marcacaoConsultaRN;
	}
	
	private ProcedimentoRN getProcedimentoRN() {
		return procedimentoRN;
	}

	protected AacNivelBuscaDAO getAacNivelBuscaDAO() {
		return aacNivelBuscaDAO;
	}

	protected AacFormaEspecialidadeDAO getAacFormaEspecialidadeDAO() {
		return aacFormaEspecialidadeDAO;
	}
	
	protected AacFormaAgendamentoDAO getAacFormaAgendamentoDAO() {
		return aacFormaAgendamentoDAO;
	}
	
	protected AacFormaEspecialidadeJnDAO getAacFormaEspecialidadeJnDAO() {
		return aacFormaEspecialidadeJnDAO;
	}
	
	protected MamLogEmUsosDAO getMamLogEmUsosDAO() {
		return mamLogEmUsosDAO;
	}

	private MamProcedimentoDAO getMamProcedimentoDAO() {
		return mamProcedimentoDAO;
	}
	protected MamAnamnesesDAO getMamAnamnesesDAO() {
		return mamAnamnesesDAO;
	}

	private MamEvolucoesDAO getMamEvolucoesDAO() {
		return mamEvolucoesDAO;
	}

	private AtendimentoPacientesAgendadosRN getAtendimentoPacientesAgendadosRN() {
		return atendimentoPacientesAgendadosRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public AghEspecialidadesDAO getAghEspecialidadesDAO() {
		return aghEspecialidadesDAO;
	}

	public void setAghEspecialidadesDAO(AghEspecialidadesDAO aghEspecialidadesDAO) {
		this.aghEspecialidadesDAO = aghEspecialidadesDAO;
	}
	
	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO() {
		return mamProcedimentoRealizadoDAO;
	}
	
}
