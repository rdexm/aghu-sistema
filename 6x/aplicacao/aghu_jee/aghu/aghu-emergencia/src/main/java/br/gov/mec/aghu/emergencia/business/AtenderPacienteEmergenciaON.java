package br.gov.mec.aghu.emergencia.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.vo.OrigemPacAtendimentoVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamMotivoAtendimentosDAO;
import br.gov.mec.aghu.emergencia.dao.MamRegistrosDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.IntegracaoEmergenciaAghuAGHWebVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamExtratoRegistro;
import br.gov.mec.aghu.model.MamMotivoAtendimento;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.service.ServiceException;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;

/**
 * Regras de negócio relacionadas ao início do atendimento do paciente em emergência.
 * 
 */
@Stateless
public class AtenderPacienteEmergenciaON extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;
	
	@Inject
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private IControlePacienteFacade controlePacienteFacade;

	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;

	@Inject
	private MamRegistrosDAO mamRegistroDAO;
	
	@Inject
	private MamMotivoAtendimentosDAO mamMotivoAtendimentosDAO;
	
	@EJB
	private MarcarConsultaEmergenciaRN marcarConsultaEmergenciaRN;
	
	@EJB
	private MamRegistroRN mamRegistroRN;
	
	@EJB
	private MamExtratoRegistroRN mamExtratoRegistroRN;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AghAtendimentoDAO atendimentoDAO;
	
	@Inject
	private AghEspecialidadesDAO especialidadeDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum AtenderPacienteEmergenciaONExceptionCode implements BusinessExceptionCode {
		MAM_03647, MAM_03109, MAM_03110, ERRO_OBTER_SITUACAO_EMERGENCIA, USUARIO_SEM_PERMISSAO_EVOLUCAO_AMBULATORIO,
		MENSAGEM_ERRO_SITUACAO_CONSULTA, ERRO_OBTER_PARAMETRO,ERRO_OBTER_SITUACAO_CHECKOUT_EMERGENCIA, MENSAGEM_SERVICO_INDISPONIVEL}
	
//	RN16 - integracao com emergencia v2 - estoria
//	public Boolean verificaIntegracaoAghuAghWebEmergencia(Boolean isEmergenciaObstetrica) throws ApplicationBusinessException, ServiceException{
//		String parametroIntegracaoAghuAghWeb, parametroCaminhoAtenderAghEmer, parametroCaminhoAtenderAghEmerObst, parametroCaminhoAtenderAghuEmerObst;
//		
//		parametroCaminhoAtenderAghuEmerObst = (String) configuracaoService.obterAghParametroPorNome(EmergenciaParametrosEnum.P_CAMINHO_ATENDER_AGHU_EMER_OBST.toString(), "vlrTexto");
//		if (this.isHCPA()) {
//			parametroIntegracaoAghuAghWeb = (String) configuracaoService.obterAghParametroPorNome(EmergenciaParametrosEnum.P_AGHU_INTEGRACAO_AGH_ORACLE_WEBFORMS.toString(), "vlrTexto");
//			parametroCaminhoAtenderAghEmer = (String) configuracaoService.obterAghParametroPorNome(EmergenciaParametrosEnum.P_CAMINHO_ATENDER_AGH_EMER.toString(), "vlrTexto");
//			parametroCaminhoAtenderAghEmerObst = (String) configuracaoService.obterAghParametroPorNome(EmergenciaParametrosEnum.P_CAMINHO_ATENDER_AGH_EMER_OBST.toString(), "vlrTexto");
//			
//			if ((parametroIntegracaoAghuAghWeb != null && parametroIntegracaoAghuAghWeb.equalsIgnoreCase("S")) && 
//					(parametroCaminhoAtenderAghEmer != null && parametroCaminhoAtenderAghEmer.equalsIgnoreCase("S")) && isEmergenciaObstetrica){
//				return parametroCaminhoAtenderAghEmer;
//			} else if ((parametroIntegracaoAghuAghWeb != null && parametroIntegracaoAghuAghWeb.equalsIgnoreCase("S")) && 
//					(parametroCaminhoAtenderAghEmerObst != null && parametroCaminhoAtenderAghEmerObst.equalsIgnoreCase("S")) && isEmergenciaObstetrica){
//				return parametroCaminhoAtenderAghEmerObst;
//			} else if ((parametroIntegracaoAghuAghWeb != null && parametroIntegracaoAghuAghWeb.equalsIgnoreCase("N") && 
//					parametroCaminhoAtenderAghuEmerObst != null) && isEmergenciaObstetrica) {
//				return parametroCaminhoAtenderAghuEmerObst;
//			} else {
//				return null;
//			}
//		} else {
//			if (parametroCaminhoAtenderAghuEmerObst != null && !parametroCaminhoAtenderAghuEmerObst.isEmpty()) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
	
	public IntegracaoEmergenciaAghuAGHWebVO iniciarAtendimentoPaciente(Integer atdSeq, Integer conNumero, Long trgSeq, Date dataHoraInicio, Short unfSeq, Short espSeq, Integer pacCodigo, String hostName, Boolean atenderMedicoAghWeb) throws ServiceException, BaseException{
		boolean isEmergenciaObstetrica = this.verificarUnidadeEmergenciaObstetrica(unfSeq);
				
		Long rgtRetorno;
		
		if (atdSeq == null){
			atdSeq = this.ambulatorioFacade.obterAtendimentoPorConNumero(conNumero);
		}
					
		
		//ao clicar em atender executar RN15
		this.controlePacienteFacade.atualizarAtdSeqHorarioControlePaciente(atdSeq, trgSeq);
		
		//NÃO SERÁ UTILIZADO na V1, Utilizado na V2
		//executar RN03 - se for obstétrica
		if(isEmergenciaObstetrica) {
			//executar rn04
			rgtRetorno = iniciaAtendimento(trgSeq, conNumero, atdSeq, dataHoraInicio, espSeq, isEmergenciaObstetrica, hostName);
			//executar rn17
			atualizarSituacaoPacienteEmConsulta(trgSeq, hostName);
		} else {
//			validarSeUsuarioTemPerfilRegistroConsultorio();

			rgtRetorno = iniciaAtendimento(trgSeq, conNumero, atdSeq, dataHoraInicio, espSeq, isEmergenciaObstetrica, hostName);
		}

		IntegracaoEmergenciaAghuAGHWebVO integracaoRetorno = montarCaminhoIntegracaoAghuAghWeb(isEmergenciaObstetrica, pacCodigo, trgSeq, rgtRetorno, atdSeq, atenderMedicoAghWeb);
		
		//Esta parte será utilizada somente na versão V1. Foi retirado na V2
		//if (isEmergenciaObstetrica){
		//	pacienteService.ingressarPacienteEmergenciaObstetricaSalaObservacao(conNumero, pacCodigo, hostName);
		//}
		
		return integracaoRetorno;
	}
	
	//p_inicia_atendimento
	public Long iniciaAtendimento(Long trgSeq, Integer conNumero, Integer atdSeq, Date dataHoraInicio, Short espSeq, Boolean isEmergenciaObstetrica, String hostName) throws ServiceException, NumberFormatException, BaseException{
		Long retorno = null;
		MamTriagens mamTriagem = this.mamTriagensDAO.obterPorChavePrimaria(trgSeq);
		validarSePacienteEstaEmergencia(mamTriagem);
		
		if(!isEmergenciaObstetrica){
			//se paciente não estiver no consultório gera esta situação
			List<Short> segSeq = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.NO_CONSULTORIO);
			
			if (segSeq == null || segSeq.isEmpty()){
				throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.ERRO_OBTER_SITUACAO_EMERGENCIA);
			} else {
				mamTriagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeq.get(0)));
				MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(mamTriagem.getSeq());
				this.marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, hostName);
			}
			
		}
		
//		MamTrgEncInterno mamTrgEncInterno = this.mamTrgEncInternoDAO.obterUltimoEncaminhamentoInterno(trgSeq);
		
		if (dataHoraInicio == null) {
			Date dataAtual = new Date();
			//busca data do primeiro exame realizado para o atendimento
			Date dataPrimeiroExame = this.examesFacade.obterDataPrimeiraSolicitacaoExamePeloNumConsulta(conNumero);
			if (dataPrimeiroExame != null && DateUtil.validaDataMenor(dataPrimeiroExame, dataAtual)){
				dataHoraInicio = dataPrimeiroExame;
			} else {
				dataHoraInicio = dataAtual;
			}

			this.atualizarConsultaEmergencia(conNumero,
					mamTriagem.getPaciente().getCodigo(), dataHoraInicio, null, null, null, hostName);
		}
		
		//p_chama_registro_atend
		if(!isEmergenciaObstetrica){
			//gera registro 
			retorno = geraRegistro(mamTriagem, atdSeq, hostName);
		}
		
		//atualiza a previsão de atendimento dos pacientes que estão aguardando consulta
		this.marcarConsultaEmergenciaRN.atualizaPrevisaoAtendimento(espSeq);
		return retorno;
	}
	
	public Integer atualizarConsultaEmergencia(Integer numeroConsulta, Integer pacCodigo, Date dataHoraInicio, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio,
			String indSitConsulta, String nomeMicrocomputador) throws ServiceException, NumberFormatException, BaseException {
		
		AacConsultas consulta = this.ambulatorioFacade.obterAacConsulta(numeroConsulta);
		consulta.setPaciente(this.pacienteFacade.buscaPaciente(pacCodigo));
		if (pConvenioSUSPadrao != null && pSusPlanoAmbulatorio != null) {
			consulta.setConvenioSaudePlano(this.faturamentoFacade.obterFatConvenioSaudePlano(pConvenioSUSPadrao, pSusPlanoAmbulatorio));
		}
		if (indSitConsulta != null){
			consulta.setSituacaoConsulta(this.ambulatorioFacade.pesquisarSituacaoConsultaPeloId(indSitConsulta));
		}
		
		if (dataHoraInicio != null){
			consulta.setDthrInicio(dataHoraInicio);
		}
		
		// #49832
		AacRetornos retorno = this.ambulatorioFacade.obterRetorno(DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
		consulta.setRetorno(retorno);
		
		AacConsultas consultaRetorno = null;
		
		consultaRetorno = this.ambulatorioFacade.atualizarConsulta(consulta, null, nomeMicrocomputador, new Date(), false, false, false, false);
		return (consultaRetorno != null) ? consultaRetorno.getNumero() : null;
	}
	
	/*private void atualizaSituacaoAtendimentoPacienteCheckout(
			MamTriagens mamTriagem, String hostName) throws ApplicationBusinessException{
		List<Short> segSeq = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.CHECK_OUT);
		
		if (segSeq == null || segSeq.isEmpty()){
			throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.ERRO_OBTER_SITUACAO_CHECKOUT_EMERGENCIA);
		} else {
			MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(mamTriagem.getSeq());

			mamTriagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeq.get(0)));
			mamTriagem.setUltTipoMvto(DominioTipoMovimento.C);
			mamTriagem.setIndPacEmergencia(false);
			this.marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, hostName);
		}		
	}*/

	public IntegracaoEmergenciaAghuAGHWebVO montarCaminhoIntegracaoAghuAghWeb(Boolean isEmergenciaObstetrica, Integer pacCodigo, Long trgSeq, Long rgtSeq, Integer atdSeq, Boolean atenderMedicoAghWeb) throws ServiceException, ApplicationBusinessException{
		IntegracaoEmergenciaAghuAGHWebVO vo = new IntegracaoEmergenciaAghuAGHWebVO();
		StringBuilder parametros = new StringBuilder();
		String url = (String) this.parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_URL_BASE_AGH_ORACLE_WEBFORMS.toString(), "vlrTexto");
		String banco = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_BANCO_AGHU_AGHWEB);
		
		if (url != null) {
			vo.setUrlAghWeb(url);
		}

		if(isEmergenciaObstetrica){
			if (atenderMedicoAghWeb){			
				vo.setForm("mcof_gestacoes.fmx");
				parametros.append("p_pac_codigo=").append(pacCodigo.toString());
				parametros.append("+p_atd_seq=").append(atdSeq.toString());
				parametros.append("+p_banco=").append(banco);
			}
			else {
				vo.setRedirecionaPesquisaGestacao(Boolean.TRUE);
			}
			
		}else{
			vo.setForm("mamf_atu_evo_emerg.fmx");
			parametros = parametros.append("p_trg_seq=" );
			parametros = parametros.append(trgSeq.toString());
			parametros = parametros.append("+p_rgt_seq=");
			parametros = parametros.append(rgtSeq.toString());
			parametros = parametros.append("+p_asu_apa_atd_seq=");
			parametros = parametros.append("+p_asu_apa_seq=");
			parametros = parametros.append("+p_asu_seqp=");
			parametros = parametros.append("+p_dthr_alta=" + new Date().toString());
		}
		vo.setParametros(parametros.toString());
		
		return vo;
	}
	
	public Long geraRegistroAtendimento(Long trgSeq, Integer atdSeq, String hostName) throws ApplicationBusinessException, ServiceException {
		MamTriagens mamTriagem = this.mamTriagensDAO.obterPorChavePrimaria(trgSeq);
		return this.geraRegistro(mamTriagem, atdSeq, hostName);
	}
	
	/**
	 * @ORADB mamk_emg_generica.mamp_emg_gera_reg_2 - Gera uma nova linha na tabela de MAM_REGISTROS 
	 */
	public Long geraRegistro(MamTriagens mamTriagem, Integer atdSeq, String hostName) throws ServiceException, ApplicationBusinessException {
		Boolean indReaproveitaRegistro = true, indNoConsultorio = true, 
				indPedeMotivo = true, permiteRegistrarMoeEmrg = false, indAchouMoe = true; 
		DominioTipoFormularioAlta indTipoFormularioAlta = DominioTipoFormularioAlta.S; 
		MamRegistro mamRegistro = new MamRegistro();
		OrigemPacAtendimentoVO origem = obterAghAtendimentosPorSeq(atdSeq);
		BigDecimal parametroMoeEmg = null;
		
		parametroMoeEmg = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SEQ_PROC_MOE_EMG.toString(), "vlrNumerico");
		
		permiteRegistrarMoeEmrg = this.ambulatorioFacade.validarProcesso(usuario.getVinculo(), usuario.getMatricula(), Short.valueOf(parametroMoeEmg.toString())); 
		if (origem.getOrigem().equals(DominioOrigemAtendimento.A)) {
			if (this.mamCaractSitEmergDAO.isExisteSituacaoEmerg(mamTriagem.getSituacaoEmergencia().getSeq(), DominioCaracteristicaEmergencia.ENC_INTERNO) ||
					this.mamCaractSitEmergDAO.isExisteSituacaoEmerg(mamTriagem.getSituacaoEmergencia().getSeq(), DominioCaracteristicaEmergencia.VOLTAR_TRIAGEM)){
				indReaproveitaRegistro = false;
				indPedeMotivo = permiteRegistrarMoeEmrg;
			} else if (this.mamCaractSitEmergDAO.isExisteSituacaoEmerg(mamTriagem.getSituacaoEmergencia().getSeq(), DominioCaracteristicaEmergencia.NO_CONSULTORIO)) {
				indNoConsultorio = true;
				mamRegistro = this.mamRegistroDAO.obterRegistroComSituacaoPendentePorTriagem(mamTriagem.getSeq());
				if (mamRegistro != null) {
					indReaproveitaRegistro = true;
				} else {
					indReaproveitaRegistro = false;
				}
				
				verificaMotivoAtendimento(mamTriagem.getSeq(), indAchouMoe, indPedeMotivo, permiteRegistrarMoeEmrg);
			} else if (this.mamCaractSitEmergDAO.isExisteSituacaoEmerg(mamTriagem.getSituacaoEmergencia().getSeq(), DominioCaracteristicaEmergencia.EM_ATEND)) {
				indReaproveitaRegistro = false;
				indNoConsultorio = false;
				indTipoFormularioAlta = DominioTipoFormularioAlta.C;
				
				verificaMotivoAtendimento(mamTriagem.getSeq(), indAchouMoe, indPedeMotivo, permiteRegistrarMoeEmrg);
			} else {
				indReaproveitaRegistro = false;
				indNoConsultorio = false;
				indTipoFormularioAlta = DominioTipoFormularioAlta.C;
				indPedeMotivo = false;
			}
		} else {
			if (this.mamCaractSitEmergDAO.isExisteSituacaoEmerg(mamTriagem.getSituacaoEmergencia().getSeq(), DominioCaracteristicaEmergencia.EM_ATEND)) {
				indReaproveitaRegistro = false;
				indNoConsultorio = false;
				indTipoFormularioAlta = DominioTipoFormularioAlta.C;	
				
				verificaMotivoAtendimento(mamTriagem.getSeq(), indAchouMoe, indPedeMotivo, permiteRegistrarMoeEmrg);
			} else {
				indReaproveitaRegistro = false;
				indNoConsultorio = false;
				indTipoFormularioAlta = DominioTipoFormularioAlta.C;
				indPedeMotivo = false;
			}
		}
		
		return gerarRegistroAtualParaElaboracao(indReaproveitaRegistro, indNoConsultorio, indTipoFormularioAlta, indPedeMotivo, atdSeq, mamRegistro, mamTriagem, hostName);
	}
	
	public OrigemPacAtendimentoVO obterAghAtendimentosPorSeq(Integer seq) {
		
		AghAtendimentos atendimento = this.aghuFacade.obterAghAtendimentosPorSeq(seq);
		OrigemPacAtendimentoVO pacAtendimentoVO = new OrigemPacAtendimentoVO();
		if (atendimento != null) {
			pacAtendimentoVO.setIndPacAtendimento(atendimento.getIndPacAtendimento() == null ? null : atendimento.getIndPacAtendimento().getDescricao());
			pacAtendimentoVO.setOrigem(atendimento.getOrigem() == null ? null : atendimento.getOrigem().getDescricao());
			pacAtendimentoVO.setEspSeq(atendimento.getEspecialidade() == null ? null : atendimento.getEspecialidade().getSeq());
		}
		return pacAtendimentoVO;
	}
	
	private Long gerarRegistroAtualParaElaboracao(Boolean indReaproveitaRegistro, Boolean indNoConsultorio, DominioTipoFormularioAlta indTipoFormularioAlta, 
			Boolean indPedeMotivo, Integer atdSeq, MamRegistro mamRegistro, MamTriagens mamTriagem, String micNome) throws ServiceException {
		if (indReaproveitaRegistro && mamRegistro != null) {
			atualizaRegistro(mamRegistro, micNome);
		} else { 
			//gera novo registro
			mamRegistro = new MamRegistro();
			mamRegistro.setTriagem(mamTriagem);
			mamRegistro.setIndNoConsultorio(indNoConsultorio);
			mamRegistro.setIndSituacao(DominioSituacaoRegistro.EE);
			mamRegistro.setIndPedeMotivo(indPedeMotivo);
			mamRegistro.setAtendimento(atendimentoDAO.buscarAtendimentoPorSeq(atdSeq));
			mamRegistro.setTipoFormularioAlta(indTipoFormularioAlta);
			
			OrigemPacAtendimentoVO origem = obterAghAtendimentosPorSeq(atdSeq);
			mamRegistro.setEspecialidade((origem != null ? especialidadeDAO.buscarEspecialidadesPorSeq(origem.getEspSeq()): null));
			mamRegistro.setUnidadeFuncional(mamTriagem.getUnidadeFuncional());
			this.mamRegistroRN.persistir(mamRegistro, micNome);
			
			//insere extrato para o registro
			MamExtratoRegistro mamExtratoRegistro = new MamExtratoRegistro();
			mamExtratoRegistro.setIndSituacao(DominioSituacaoRegistro.EE);	
			this.mamExtratoRegistroRN.persistir(mamExtratoRegistro, mamRegistro.getSeq(), micNome);
		}
		
		return mamRegistro.getSeq();
	}
	
	/**
	 *  @ORADB mamk_emg_generica.mamp_emg_atlz_reg;
	 *  @param mamRegistro
	 *  @param micNome
	 */
	private void atualizaRegistro(MamRegistro mamRegistro, String micNome){
		mamRegistro.setIndSituacao(DominioSituacaoRegistro.EE);
		this.mamRegistroRN.persistir(mamRegistro, micNome);
		
		//gera extrato para o registro
		MamExtratoRegistro mamExtratoRegistro = new MamExtratoRegistro();
		mamExtratoRegistro.setIndSituacao(DominioSituacaoRegistro.EE);	
		this.mamExtratoRegistroRN.persistir(mamExtratoRegistro, mamRegistro.getSeq(), micNome);
	}
	
	public void atualizarSituacaoPacienteEmConsulta(Long trgSeq, String micNome) throws ServiceException, ApplicationBusinessException {
		BigDecimal situacaoConsulta = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SIT_CONSULTA.toString(), "vlrNumerico");
		
		if (situacaoConsulta == null) {
			throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.MENSAGEM_ERRO_SITUACAO_CONSULTA);
		} else {
			MamTriagens mamTriagem = this.mamTriagensDAO.obterPorChavePrimaria(trgSeq);
			MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(trgSeq);
			MamSituacaoEmergencia situacao = this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(Short.valueOf(situacaoConsulta.toString()));
			
			mamTriagem.setSituacaoEmergencia(situacao);
			
			this.marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, micNome);
		}
	}
	
	public void verificaMotivoAtendimento(Long trgSeq, Boolean indAchouMoe, Boolean indPedeMotivo, Boolean permiteRegistrarMoeEmrg){
		MamMotivoAtendimento motivoAtendimento = this.mamMotivoAtendimentosDAO.obterRegistroPendenteValidadoPorTriagem(trgSeq);
		if (motivoAtendimento == null) {
			indAchouMoe = false;
		}
		
		if (indAchouMoe && !permiteRegistrarMoeEmrg){
			indPedeMotivo = false;
		} 
	}

	public boolean verificarUnidadeEmergenciaObstetrica(Short unfSeq) throws ServiceException {
		if (unfSeq != null) {
			 return verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.EMERGENCIA_OBSTETRICA.toString());
		 } else {
			 return Boolean.FALSE;
		 }
	}
	
	public Boolean verificarCaracteristicaUnidadeFuncional(Short unfSeq, String caracteristicaUnidadeFuncional) throws ServiceException {
		ConstanteAghCaractUnidFuncionais caracteristica = ConstanteAghCaractUnidFuncionais.getEnum(caracteristicaUnidadeFuncional);
		if (caracteristica == null) {
			throw new ServiceException("caracteristicaUnidadeFuncional não encontrada");
		}
		
		return this.aghuFacade.verificarCaracteristicaUnidadeFuncional(unfSeq,	caracteristica);
	}
	
//	public void validarSeUsuarioTemPerfilRegistroConsultorio() throws ServiceException, ApplicationBusinessException {
//		BigDecimal parametroProcessoConsulta, parametroProcessoEvoAmb;
//		
//		parametroProcessoConsulta = (BigDecimal) configuracaoService.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SEQ_PROC_RG_CONS.toString(), "vlrNumerico");
//		parametroProcessoEvoAmb = (BigDecimal) configuracaoService.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SEQ_PROC_EVO_AMB.toString(), "vlrNumerico");
//		
//		if (parametroProcessoConsulta == null) {	
//			throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.ERRO_OBTER_PARAMETRO, EmergenciaParametrosEnum.P_SEQ_PROC_RG_CONS.toString());
//		}
//				
//		if (parametroProcessoEvoAmb == null) {
//			throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.ERRO_OBTER_PARAMETRO, EmergenciaParametrosEnum.P_SEQ_PROC_EVO_AMB.toString());
//		}
//		
//		if (!validarUsuarioPermissaoExecucaoProcesso(usuario.getLogin(), parametroProcessoConsulta != null ? Short.valueOf(parametroProcessoConsulta.toString()) : null) || 
//				!validarUsuarioPermissaoExecucaoProcesso(usuario.getLogin(), parametroProcessoEvoAmb != null ? Short.valueOf(parametroProcessoEvoAmb.toString()) : null)) {
//			throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.MAM_03647);
//		}
//	}
	
	/**
	 * @ORADB MAMK_EMG_SEGURANCA.MAMC_EMG_EXEC_PROC - verifica se usuário tem permissão para execução de determinado processo. Valida se o mesmo pode 
	 * fazer registro de um paciente que está no consultório.
	 * */
//	private boolean validarUsuarioPermissaoExecucaoProcesso(String loginUsuario, Short processo) throws ApplicationBusinessException{
//		if (processo == null) {
//			return false;
//		} else {
//			return this.ambulatorioService.validarProcesso(usuario.getVinculo(), usuario.getMatricula(), processo);
//		}
//	}

	public void validarSePacienteEstaEmergencia(MamTriagens triagem) throws ApplicationBusinessException {
		if(!mamCaractSitEmergDAO.isExisteSituacaoEmerg(triagem.getSituacaoEmergencia().getSeq(), DominioCaracteristicaEmergencia.LISTA_AGUARDANDO)){
			throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.MAM_03109);
		}
		
		if (triagem.getUltTipoMvto().equals(DominioTipoMovimento.C) || triagem.getUltTipoMvto().equals(DominioTipoMovimento.S)){
			throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.MAM_03110);
		}
	}
	
	public Boolean verificaIntegracaoAghuAghWebEmergencia() throws ApplicationBusinessException {
		if (super.isHCPA()){
			String parametroIntegracaoAghuAghWeb = (String) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_AGHU_INTEGRACAO_AGH_ORACLE_WEBFORMS.toString(), "vlrTexto");
			
			if (parametroIntegracaoAghuAghWeb.equalsIgnoreCase("S")) {
				return true;
			}
		}			
		return false;
	}	
	
	public void validarSeTemPermissaoRealizarEvolucaoAmbulatorio() throws ApplicationBusinessException {
		Boolean permRealizarEvolucaoAmbulatorio = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(),
				"realizarEvolucaoAmbulatorio","salvar");
		if (!permRealizarEvolucaoAmbulatorio) {
			throw new ApplicationBusinessException(AtenderPacienteEmergenciaONExceptionCode.USUARIO_SEM_PERMISSAO_EVOLUCAO_AMBULATORIO);
		}
	}
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
}
