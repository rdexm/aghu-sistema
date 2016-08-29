package br.gov.mec.aghu.internacao.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaractUnidFuncionaisDAO;
import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.configuracao.dao.AghClinicasDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudeDAO;
import br.gov.mec.aghu.faturamento.dao.FatConvenioSaudePlanoDAO;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposAltaMedicaDAO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * @author gmneto
 * @ORADB AINK_ATU_RN
 * 
 */
@Stateless
public class AtendimentoUrgenciaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AtendimentoUrgenciaRN.class);

	@EJB
	IParametroFacade parametroFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	InternacaoRN internacaoRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;	
	
	@EJB
	private IInternacaoFacade internacaoFacade;	
	
	@EJB
	private IPacienteFacade pacienteFacade;	
	
	@Inject
	AghCidDAO aghCidDAO;

	@Inject
	FatConvenioSaudePlanoDAO convenioSaudePlanoDAO;

	@Inject
	AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;
	
	@Inject
	AinInternacaoDAO ainInternacaoDAO;
	
	@Inject
	AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	AghCaractUnidFuncionaisDAO aghCaractUnidFuncionaisDAO;
	
	@Inject
	AacConsultasDAO aacConsultasDAO;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@Inject
	AghClinicasDAO aghClinicasDAO;
	
	@Inject
	AinQuartosDAO ainQuartosDAO;
	
	@Inject 
	AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	AipPacientesDAO aipPacientesDAO;
	
	@Inject
	FatConvenioSaudeDAO fatConvenioSaudeDAO;
	
	@Inject
	AinTiposAltaMedicaDAO ainTiposAltaMedicaDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 16321050292891796L;

	private enum AtendimentoUrgenciaRNExceptionCode implements BusinessExceptionCode {
		AIN_00801, AIN_00131, AIN_00300_1, AIN_00200, AIN_00242, AAC_00195,
		MENSAGEM_ERRO_MODULO_INATIVO,
		MENSAGEM_ERRO_NECESSARIO_INTERNAR_PACIENTE,
		MENSAGEM_SERVICO_INDISPONIVEL_FINALIZAR_CONSULTA,
		MESSAGEM_GENERICA_ERRO_SERVICO,
		MENSAGEM_PACIENTE_NAO_ESTA_EM_ATENDIMENTO_URGENCIA,		
		MENSAGEM_PARAMETRO_NAO_CADASTRADO;
	}

	/**
	 * 
	 * @param atuSeq
	 * @param novaDataAtendimento
	 * @param antigaDataAtendimento
	 * 
	 * @ORADB RN_ATUP_ATU_LCTO_EXL
	 * 
	 */
	public void atualizarDataLancamentoExtratoLeito(
			Integer atuSeq, Date novaDataAtendimento,
			Date antigaDataAtendimento) {

		//TODO: ESTE MÉTODO DEVERÁ SER IMPLEMENTADO NO MÓDULO DE EMERGÊNCIA
	}

	/**
	 * 
	 * @param paciente
	 * @param dataAlta
	 * @throws BaseException 
	 * 
	 * @ORADB RN_ATUP_ALTA_PAC
	 */
	public void atualizarUltimaAltaPaciente(AipPacientes paciente, String nomeMicrocomputador, Date dataAlta) throws BaseException {
		cadastroPacienteFacade.atualizarPaciente(paciente, nomeMicrocomputador, dataAlta);
	}

	/**
	 * 
	 * @param paciente
	 * @param dataUltimaInternacao
	 * @param dataUltimaInternacaoAnterior
	 * 
	 * @ORADB RN_ATUP_DATA_PAC
	 * 
	 */
	public void atualizarUltimaInternacaoPaciente(AipPacientes paciente,
			Date dataUltimaInternacao, Date dataUltimaInternacaoAnterior) {
		
		//TODO: ESTE MÉTODO DEVERÁ SER IMPLEMENTADO NO MÓDULO DE EMERGÊNCIA
	}

	/**
	 * RN07 #26857
	 * 
	 * @param paciente
	 * @param dataInternacao
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @throws ApplicationBusinessException 
	 * 
	 * @ORADB RN_ATUP_ATLZ_PAC
	 */
	public void atualizarUltimaInternacaoPaciente(AipPacientes paciente,
			Date dataInternacao, AinLeitos leito, AinQuartos quarto,
			AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		
		paciente.setDtUltInternacao(dataInternacao);
		paciente.setLeito(leito);
		paciente.setQuarto(quarto);
		paciente.setUnidadeFuncional(unidadeFuncional);
		
		cadastroPacienteFacade.atualizarPacienteParcial(paciente, null, new Date());
	}

	/**
	 * 
	 * @param atendimentoUrgencia
	 * @param atendimentoUrgenciaAntigo
	 * @param nomeMicrocomputador
	 * @param dataFimVinculoServidor
	 * 
	 * @ORADB RN_ATUP_ATU_LCAL_ATD
	 */
	public void atualizarLeitoNoAtendimento(AinAtendimentosUrgencia atendimentoUrgencia, 
			AinAtendimentosUrgencia atendimentoUrgenciaAntigo, String nomeMicrocomputador, Date dataFimVinculoServidor) throws ApplicationBusinessException, BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		AinLeitos leito = atendimentoUrgencia.getLeito();
		AinQuartos quarto = atendimentoUrgencia.getQuarto(); 

		AghAtendimentos atendimento = atendimentoUrgencia.getAtendimento();
		AghAtendimentos atendimentoAntigo = atendimentoUrgenciaAntigo.getAtendimento();

		if(leito != null){
			atendimento.setUnidadeFuncional(leito.getUnidadeFuncional());
			atendimento.setQuarto(leito.getQuarto());			
		} else if(quarto != null){
			atendimento.setUnidadeFuncional(quarto.getUnidadeFuncional());			
		}
		pacienteFacade.atualizarAtendimento(atendimento, atendimentoAntigo, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);		
	}

	/**
	 * 
	 * @param atendimentoUrgencia
	 * @param dataAlta
	 * @throws BaseException 
	 * 
	 * @ORADB RN_ATUP_ATU_FIM_ATD
	 */
	public void atualizarFimAtendimento(AinAtendimentosUrgencia atendimentoUrgencia, String nomeMicrocomputador, Date dataAlta) throws BaseException {
		pacienteFacade.atualizarFimAtendimento(null, null, atendimentoUrgencia, new Date(), nomeMicrocomputador, atendimentoUrgencia.getDtAltaAtendimento());		
	}

	/**
	 * 
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 *  
	 * 
	 * @ORADB RN_ATUP_VER_ARCO
	 * 
	 *        Verifica se esta informado leito OU quarto OU Unidade Funcional
	 */
	public void validarLocalInternacao(AinLeitos leito, AinQuartos quarto,
			AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		
		if (leito == null && unidadeFuncional == null && quarto == null){
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00801);
		}
		
		if (leito != null){
			if (quarto != null || unidadeFuncional != null){
				throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00131);
			}
		}
		
		if (quarto != null){
			if (leito != null || unidadeFuncional != null){
				throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00131);
			}
		}
		
		if (unidadeFuncional != null){
			if (quarto != null || leito != null){
				throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00131);
			}
		}
	}
	
	public AinAtendimentosUrgencia persistirAtendimentoUrgente(Integer numeroConsulta,Integer codigoPaciente, String microComputador) throws NumberFormatException, BaseException{
		AinAtendimentosUrgencia urgencia = this.montarAtendimentoUrgencia(numeroConsulta, codigoPaciente);
		urgencia = this.prePersistirAtendimentoUrgencia(urgencia, microComputador);
		
		ainAtendimentosUrgenciaDAO.persistir(urgencia);
		
		posInsertAtendimentoUrgencia(microComputador, urgencia, null);
		
		return urgencia;	
	}
	
	private void posInsertAtendimentoUrgencia(String nomeMicrocomputador, AinAtendimentosUrgencia atendimentoUrgencia,
			AinAtendimentosUrgencia atendimentoUrgenciaOriginal) throws ApplicationBusinessException, BaseException {
		String leitoId = atendimentoUrgencia.getLeito() != null ? atendimentoUrgencia.getLeito().getLeitoID() : null;
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		Short seqUnidadeFuncional = null;
		Short numeroQuarto = null;
		Integer codPaciente = null;
		String codigoTipoAlaMedica = null;

		if (atendimentoUrgencia.getUnidadeFuncional() != null && atendimentoUrgencia.getUnidadeFuncional().getSeq() != null) {
			seqUnidadeFuncional = atendimentoUrgencia.getUnidadeFuncional().getSeq();
		}

		if (atendimentoUrgencia.getQuarto() != null && atendimentoUrgencia.getQuarto().getNumero() != null) {
			numeroQuarto = atendimentoUrgencia.getQuarto().getNumero();
		}

		if (atendimentoUrgencia.getPaciente() != null && atendimentoUrgencia.getPaciente().getCodigo() != null) {
			codPaciente = atendimentoUrgencia.getPaciente().getCodigo();
		}

		if (atendimentoUrgencia.getTipoAltaMedica() != null && atendimentoUrgencia.getTipoAltaMedica().getCodigo() != null) {
			codigoTipoAlaMedica = atendimentoUrgencia.getTipoAltaMedica().getCodigo();
		}

		this.internacaoRN.gerarCheckOutAtu(seqUnidadeFuncional, numeroQuarto, leitoId, null, null, null, atendimentoUrgencia.getSeq(),
				codPaciente, null, codigoTipoAlaMedica, Boolean.FALSE, atendimentoUrgencia.getIndPacienteEmAtendimento(), servidorLogado.getUsuario());

		this.getAmbulatorioFacade().atualizarSituacaoTriagem(codPaciente);

		this.internacaoRN.processarEnforceAtendimentoUrgencia(atendimentoUrgencia, atendimentoUrgenciaOriginal, TipoOperacaoEnum.INSERT,
				nomeMicrocomputador, new Date());
	}
	
	private AinAtendimentosUrgencia montarAtendimentoUrgencia(Integer numeroConsulta, Integer codigoPaciente) throws ApplicationBusinessException {
		AinAtendimentosUrgencia ainAtendimentosUrgencia = new AinAtendimentosUrgencia();

		ainAtendimentosUrgencia.setServidor(getServidorLogadoFacade().obterServidorLogado());
		ainAtendimentosUrgencia.setCid(aghCidDAO.pesquisarPorNomeCodigoAtivaPaginadoUnico(parametroFacade
				.buscarValorInteiro(AghuParametrosEnum.P_CID_PADRAO_INGRESSO_SO)));

		Short cspConvenioCodigo = parametroFacade.buscarValorShort(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
		Byte cspSeq = parametroFacade.buscarValorByte(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
		
		FatConvenioSaudePlano convenio = convenioSaudePlanoDAO.obterConvenioSaudePlano(cspConvenioCodigo, cspSeq);

		ainAtendimentosUrgencia.setConvenioSaudePlano(convenio);
		
		FatConvenioSaude convenioSaude = fatConvenioSaudeDAO.obterConvenioSaude(cspConvenioCodigo);
		
		ainAtendimentosUrgencia.setConvenioSaude(convenioSaude);
		ainAtendimentosUrgencia.setCspSeq(cspSeq);
		ainAtendimentosUrgencia.setDtAtendimento(new Date());

		AipPacientes paciente = aipPacientesDAO.obterPorChavePrimaria(codigoPaciente);

		ainAtendimentosUrgencia.setPaciente(paciente);
		ainAtendimentosUrgencia.setIndInternacao(Boolean.TRUE);
		ainAtendimentosUrgencia.setIndPacienteEmAtendimento(Boolean.TRUE);
		ainAtendimentosUrgencia.setIndLocalPaciente(DominioLocalPaciente.L);

		AacConsultas consultas =  aacConsultasDAO.obterConsulta(numeroConsulta);

		ainAtendimentosUrgencia.setConsulta(consultas);

		AghEspecialidades especialidade = aghEspecialidadesDAO.buscarEspecialidadesPorSeq(parametroFacade
				.buscarValorShort(AghuParametrosEnum.P_ESPECIALIDADE_URGENCIA_OBSTETRICA));
		
		ainAtendimentosUrgencia.setEspecialidade(especialidade);
		ainAtendimentosUrgencia.setLeito(null);

		AinQuartos quarto = ainQuartosDAO.obterQuartosLeitosPorId(parametroFacade.buscarValorShort(AghuParametrosEnum.P_NUMERO_SALA_OBSERVACAO));

		ainAtendimentosUrgencia.setQuarto(quarto);
		ainAtendimentosUrgencia.setUnidadeFuncional(null);

		AghClinicas clinicas = aghClinicasDAO.obterClinica(parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_CLINICA_OBSTETRICA));

		ainAtendimentosUrgencia.setClinica(clinicas);
		ainAtendimentosUrgencia.setTipoAltaMedica(null);
		ainAtendimentosUrgencia.setDtAltaAtendimento(null);
		ainAtendimentosUrgencia.setEnvProntUnidInt(DominioSimNao.N.toString());
		ainAtendimentosUrgencia.setDataAvisoSamis(null);

		return ainAtendimentosUrgencia;
	}

	/**
	 * Estória #26857 RN02
	 * @ORADB AINT_ATU_BRI before insert AIN_ATENDIMENTOS_URGENCIA
	 * 
	 * @param urgencia
	 * @return AinAtendimentosUrgencia
	 * @throws BaseException 
	 * @throws NumberFormatException 
	 */
	private AinAtendimentosUrgencia prePersistirAtendimentoUrgencia(AinAtendimentosUrgencia urgencia, String microComputador) throws NumberFormatException, BaseException {

		if (ainAtendimentosUrgenciaDAO.obterPacienteAtendimentoUrgencia(urgencia.getPaciente().getCodigo()) != null) {
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00200);
		}

		if (ainInternacaoDAO.obrterInternacaoPorPacienteInternado(urgencia.getPaciente().getCodigo()) != null) {
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00242);
		}

		Integer diferencaDataAtendimento = DateUtil.calcularDiasEntreDatas(urgencia.getDtAtendimento(), new Date());
		Integer diasMinimoVoltaInternacao = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_QTD_DIAS_VOLTA_INT);
		if (diferencaDataAtendimento > diasMinimoVoltaInternacao || DateUtil.validaDataMaior(urgencia.getDtAtendimento(), new Date())) {
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00300_1);
		}

		verificaPreeximentoLeitoQuartoUnidadeFuncional(urgencia);

		defineLocalPaciente(urgencia);
		
		AghAtendimentos atendimento = aghAtendimentoDAO.buscarAtendimentoPorConNumero(urgencia.getConsulta().getNumero());
		if(atendimento == null){
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AAC_00195);
		}
		
		if (aghCaractUnidFuncionaisDAO.verificarCaracteristicaUnidadeFuncional(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.CONTROLA_TRAT_AMBULATORIAL)) {
			if (atendimento.getConsulta().getDthrInicio() == null) {
				urgencia.getConsulta().setDthrInicio(urgencia.getDtAtendimento());
				urgencia.getConsulta().setDthrFim(urgencia.getDtAtendimento());
				this.getAmbulatorioFacade().atualizarConsulta(atendimento.getConsulta(), urgencia.getConsulta(), Boolean.FALSE, microComputador, new Date(), Boolean.FALSE);	
			}
			else if (atendimento.getConsulta().getDthrFim() == null) {
				urgencia.getConsulta().setDthrFim(urgencia.getDtAtendimento());
				this.getAmbulatorioFacade().atualizarConsulta(atendimento.getConsulta(), urgencia.getConsulta(), Boolean.FALSE, microComputador, new Date(), Boolean.FALSE);
			}
		}

		return urgencia;
	}

	private void defineLocalPaciente(AinAtendimentosUrgencia urgencia) {
		if (urgencia.getLeito() != null) {
			urgencia.setIndLocalPaciente(DominioLocalPaciente.L);
		}

		else if (urgencia.getQuarto() != null) {
			urgencia.setIndLocalPaciente(DominioLocalPaciente.Q);
		}

		else if (urgencia.getUnidadeFuncional() != null) {
			urgencia.setIndLocalPaciente(DominioLocalPaciente.U);
		}
	}

	private void verificaPreeximentoLeitoQuartoUnidadeFuncional(AinAtendimentosUrgencia urgencia) throws ApplicationBusinessException {
		if (urgencia.getLeito() == null && urgencia.getQuarto() == null && urgencia.getUnidadeFuncional() == null) {
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00801);
		}

		else if (urgencia.getLeito() != null && (urgencia.getQuarto() != null || urgencia.getUnidadeFuncional() != null)) {
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00131);
		}

		else if (urgencia.getQuarto() != null && (urgencia.getLeito() != null || urgencia.getUnidadeFuncional() != null)) {
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00131);
		}

		else if (urgencia.getUnidadeFuncional() != null && (urgencia.getQuarto() != null || urgencia.getLeito() != null)) {
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.AIN_00131);
		}
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	protected void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	protected void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
	
	public void atualizarAtendimentoUrgencia(Integer seq, String codigoTipoAltaMedicaNormal){
		
		AinAtendimentosUrgencia atendimentosUrgencia = ainAtendimentosUrgenciaDAO.obterPorChavePrimaria(seq);
		AinTiposAltaMedica tipoAltaMedica = ainTiposAltaMedicaDAO.obterTipoAltaMedica(codigoTipoAltaMedicaNormal);	
		atendimentosUrgencia.setDtAltaAtendimento(new Date());		
		atendimentosUrgencia.setTipoAltaMedica(tipoAltaMedica);
		
		ainAtendimentosUrgenciaDAO.merge(atendimentosUrgencia);		
	}
	
	public void atualizarAtendimentoUrgenciaSemDataAte(Integer seq, String codigoTipoAltaMedicaNormal){
		
		AinAtendimentosUrgencia atendimentosUrgencia = ainAtendimentosUrgenciaDAO.obterPorChavePrimaria(seq);
		atendimentosUrgencia.setDtAltaAtendimento(new Date());		
		AinTiposAltaMedica tipoAltaMedica = ainTiposAltaMedicaDAO.obterTipoAltaMedica(codigoTipoAltaMedicaNormal);	
		atendimentosUrgencia.setTipoAltaMedica(tipoAltaMedica);
		
		ainAtendimentosUrgenciaDAO.merge(atendimentosUrgencia);		
	}

	public void finalizarConsulta(Integer numeroConsulta, Integer codigoPaciente, Integer matricula, Short vinCodigo, String hostName) throws ApplicationBusinessException, BaseException{
		
		verificaSeModuloEstaAtivo(ModuloEnum.INTERNACAO.getDescricao());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AinAtendimentosUrgencia atendimentoUrgencia = ainAtendimentosUrgenciaDAO.obterPacienteEmAtendimentoUrgencia(numeroConsulta, codigoPaciente);
		
		if(atendimentoUrgencia != null) {
			
			BigDecimal quantidadeHorasInternarPacEmSO = obtemParametroQuantidadeHorasInternarPacEmSO();
			
			verificarTempoPacienteEstaEmSO(atendimentoUrgencia.getDtAtendimento(), quantidadeHorasInternarPacEmSO);	
			
			AinAtendimentosUrgencia atendimentoUrgenciaAntigo = ainAtendimentosUrgenciaDAO.obterOriginal(atendimentoUrgencia.getSeq());
//			AghAtendimentos atendimento = aghAtendimentoDAO.obterAtendimentoPorAtendimentoUrgenciaVigente(atendimentoUrgencia.getSeq());
			Integer seqAtendimento = aghAtendimentoDAO.obterAtendimentoPorConNumero(numeroConsulta);
			AghAtendimentos atendimento = aghAtendimentoDAO.obterAtendimentoPeloSeq(seqAtendimento);
			atendimentoUrgenciaAntigo.setAtendimento(atendimento);
			
			
			internacaoFacade.validarAtendimentoUrgenciaAntesAtualizar(atendimentoUrgencia, atendimentoUrgenciaAntigo, servidorLogado.getUsuario());
			
			String codigoTipoAltaMedicaNormal = obtemParametroTipoAltaMedica();		
			atualizarAtendimentoUrgencia(atendimentoUrgencia.getSeq(), codigoTipoAltaMedicaNormal);
			
			internacaoFacade.processarEnforceAtendimentoUrgencia(atendimentoUrgencia, atendimentoUrgenciaAntigo, TipoOperacaoEnum.UPDATE, hostName, new Date());
			
			Integer codigoMovimentacao = internacaoFacade.obtemCodigoMovimento(AghuParametrosEnum.P_COD_MVTO_INT_ALTA.toString());
			internacaoFacade.geraMovimentoAtendimento(atendimentoUrgencia, codigoMovimentacao, atendimentoUrgencia.getDtAltaAtendimento());
			
			AipPacientes paciente = aipPacientesDAO.obterPorChavePrimaria(codigoPaciente);
			atualizarUltimaAltaPaciente(paciente, hostName, atendimentoUrgencia.getDtAltaAtendimento());
			
			atualizarFimAtendimento(atendimentoUrgencia, hostName, atendimentoUrgencia.getDtAltaAtendimento());
			
			internacaoFacade.atualizaMovimentoInternacao(atendimentoUrgencia, atendimentoUrgenciaAntigo, codigoMovimentacao, atendimentoUrgenciaAntigo.getDtAltaAtendimento());
			
			atualizarLeitoNoAtendimento(atendimentoUrgencia, atendimentoUrgenciaAntigo, hostName, new Date());
						
		}
	}

	private String obtemParametroTipoAltaMedica() throws ApplicationBusinessException {
		AghParametros parametroTipoAltaMedica = parametroFacade.obterAghParametroPorNome(AghuParametrosEnum.P_COD_TIPO_ALTA_NORMAL.toString());
		if(parametroTipoAltaMedica == null || StringUtils.isBlank(parametroTipoAltaMedica.getVlrTexto())){
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.MENSAGEM_PARAMETRO_NAO_CADASTRADO, AghuParametrosEnum.P_QTD_HORAS_INTERNAR_PAC_EM_SO.toString());
		}
		return parametroTipoAltaMedica.getVlrTexto();
	}

	private void verificarTempoPacienteEstaEmSO(Date dataAtendimento, BigDecimal quantidadeHorasInternarPacEmSO) throws ApplicationBusinessException {
		Date dataAtual =  new Date();
		Integer quantidadeDeMinutos =  DateUtil.obterQtdMinutosEntreDuasDatas(dataAtendimento, dataAtual);
		Integer quantidadeDeHoras = quantidadeDeMinutos / 60;
		quantidadeDeMinutos = quantidadeDeMinutos % 60; 

		if(quantidadeDeHoras > quantidadeHorasInternarPacEmSO.intValue()){
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.MENSAGEM_ERRO_NECESSARIO_INTERNAR_PACIENTE, quantidadeDeHoras, quantidadeDeMinutos);
		}
	}

	private BigDecimal obtemParametroQuantidadeHorasInternarPacEmSO() throws ApplicationBusinessException {
		Object quantidadeHorasInternarPacEmSO = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_QTD_HORAS_INTERNAR_PAC_EM_SO.toString());
		if(quantidadeHorasInternarPacEmSO == null){
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.MENSAGEM_PARAMETRO_NAO_CADASTRADO, AghuParametrosEnum.P_QTD_HORAS_INTERNAR_PAC_EM_SO.toString());
		}
		return (BigDecimal) quantidadeHorasInternarPacEmSO;
	}

	private void verificaSeModuloEstaAtivo(String modulo) throws ApplicationBusinessException {
		boolean moduloAtivo = cascaFacade.verificarSeModuloEstaAtivo(modulo);
		if(!moduloAtivo){
			throw new ApplicationBusinessException(AtendimentoUrgenciaRNExceptionCode.MENSAGEM_ERRO_MODULO_INATIVO, "Internação");
		}
	}

}
