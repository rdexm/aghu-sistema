package br.gov.mec.aghu.emergencia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.administracao.dao.AghMicrocomputadorDAO;
import br.gov.mec.aghu.administracao.vo.MicroComputador;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaEspecialidadeAlteradaRetornoVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaGradeAtendimentoVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaVO;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.dominio.DominioTipoPrevAtende;
import br.gov.mec.aghu.emergencia.dao.MamAgrupGrvEspsDAO;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamExtratoTriagemDAO;
import br.gov.mec.aghu.emergencia.dao.MamGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamMvtoTriagemDAO;
import br.gov.mec.aghu.emergencia.dao.MamRegistrosDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamTriagensJnDAO;
import br.gov.mec.aghu.emergencia.vo.MamPacientesAguardandoAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.OrdenacaoPorGravidadesVO;
import br.gov.mec.aghu.emergencia.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.emergencia.vo.PacientesAguardandoAtendimentoVO;
import br.gov.mec.aghu.emergencia.vo.TrgEncInternoVO;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.exames.vo.SolicitacaoExamesVO;
import br.gov.mec.aghu.internacao.service.IInternacaoService;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAgrupGrvEsps;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamMvtoTriagem;
import br.gov.mec.aghu.model.MamMvtoTriagemId;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.service.IPacienteService;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.prescricaoenfermagem.vo.EpePrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;
/**
 * Regras de negócio relacionadas ao Paciente.
 * 
 * @author ihaas
 * 
 */
@Stateless
public class PacientesEmergenciaON extends BaseBusiness {

	private static final String EM_ATE = " - Em até ";
	private static final String VLR_NUMERICO = "vlrNumerico"; 
	/**
	 * 
	 */
	private static final long serialVersionUID = -3423984755101821178L;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private IAmbulatorioService ambulatorioService;
	
	@Inject
	private IInternacaoService internacaoService;
	
	@Inject
	private IPacienteService pacienteService;
	
	@Inject
	private IConfiguracaoService configuracaoService;
	
	@Inject
	private IPrescricaoMedicaService prescricaoMedicaService;
	
	@Inject
	private IExamesService examesService;

	@Inject
	private AghMicrocomputadorDAO aghMicrocomputadorDAO;
	
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamMvtoTriagemDAO mamMvtoTriagemDAO;
	
	@Inject
	private MamTrgGravidadeDAO mamTrgGravidadeDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;
	
	@Inject
	private MamExtratoTriagemDAO mamExtratoTriagemDAO;
	
	@Inject
	private MamTriagensJnDAO mamTriagensJnDAO;
	
	@Inject
	private MamGravidadeDAO mamGravidadeDAO;
	
	@Inject
	private MamRegistrosDAO mamRegistrosDAO;
	
	@EJB
	private MamTriagensRN mamTriagensRN;
	
	@EJB
	private MamMvtoTriagemRN mamMvtoTriagemRN;
	
	@EJB
	private MarcarConsultaEmergenciaRN marcarConsultaEmergenciaRN;
	
	@Inject
	private MamAgrupGrvEspsDAO mamAgrupGrvEspsDAO;
	
	@EJB
	private IPermissionService permissionService;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum PacientesEmergenciaONExceptionCode implements BusinessExceptionCode {
		MAM_02765_1, MAM_02793_1, MAM_02776_1, MAM_04070, MAM_02707_1, ERRO_ACOLHIMENTO_NAO_PERTENCE_UNIDADE,
		MENSAGEM_SERVICO_INDISPONIVEL, MAM_02760, MAM_02836, MAM_02761, MAM_02755, MAM_02832, ERRO_SITUACAO_EMERGENCIA_ENC_TRIAGEM_VAZIO, MENSAGEM_ERRO_OBTENCAO_PARAMETRO,
		ERRO_UNIDADE_NAO_RECEBE_PACIENTES, ERRO_UNIDADE_NAO_PERMITE_ACOLHIMENTO, ERRO_APENAS_CO_RECEBE_ACOLHE_PACIENTES;
	}

	public MicroComputador obterMicroComputadorPorNomeOuIPException(String hostName) throws ApplicationBusinessException {
		
		MicroComputador computador = null;
		AghMicrocomputador aghMicro = aghMicrocomputadorDAO.obterAghMicroComputadorPorNomeOuIPException(hostName);
		if (aghMicro != null) {
			computador = new MicroComputador();
			computador.setNome(aghMicro.getNome());
			if (aghMicro.getAghUnidadesFuncionais() != null) {
				computador.setUnfSeq(aghMicro.getAghUnidadesFuncionais().getSeq());
			}
		}
		
		return computador;
	}
	
	public Short pesquisarUnidadeFuncionalTriagemRecepcao(
			List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) throws ApplicationBusinessException {
		
		Short result = null;
		
		try {
			result = this.internacaoService
					.pesquisarUnidadeFuncionalTriagemRecepcao(listaUnfSeqTriagemRecepcao, unfSeqMicroComputador);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	public List<Paciente> obterPacientePorCodigoOuProntuario(PacienteFiltro filtro) throws ApplicationBusinessException {
		
		Paciente result = null;
		List<Paciente> listaRetorno = new ArrayList<Paciente>();
		try {
			result = this.pacienteService.obterPacientePorCodigoOuProntuario(filtro);
			if (result != null) {
				listaRetorno.add(result);
			}
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return listaRetorno;
	}
	
	public List<Paciente> pesquisarPorFonemas(PacienteFiltro filtro) throws ApplicationBusinessException {
		
		List<Paciente> listaRetorno = new ArrayList<Paciente>();
		
		try {
			listaRetorno = this.pacienteService.pesquisarPorFonemas(filtro);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return listaRetorno;
	}
	
	public Long pesquisarPorFonemasCount(PacienteFiltro filtro) throws ApplicationBusinessException {
		
		Long result = null;
		
		try {
			result = this.pacienteService.pesquisarPorFonemasCount(filtro);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	private Integer buscarAghParametro(final String param, final String valor) throws ApplicationBusinessException{
		try {
			Object parametro = parametroFacade.obterAghParametroPorNome(param, valor);		
			if (parametro.toString() == null){
				 throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_ERRO_OBTENCAO_PARAMETRO, param);
			}else{
				if (!CoreUtil.isNumeroInteger(parametro)){
					 throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_ERRO_OBTENCAO_PARAMETRO, param);

				 }else {
					 BigDecimal retorno = (BigDecimal) parametro;
					return retorno.intValue();
				 }
			}		
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_ERRO_OBTENCAO_PARAMETRO, param);
		}
	}

	
	/**
	 * Estoria #38825
	 * @param paciente
	 * @param unidade
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public boolean exibirModalPacienteMenor(Paciente paciente, MamUnidAtendem unidade) throws ApplicationBusinessException{
		if (unidade!= null && Boolean.TRUE.equals(unidade.getIndMenorResponsavel())) {
			Integer maioridadePenal = buscarAghParametro("P_MAIORIDADE_PENAL", VLR_NUMERICO);
			int idadePaciente = CoreUtil.calculaIdade(paciente.getDtNascimento());
			if (idadePaciente >= maioridadePenal.intValue()) {
			 return  false;
			}else{
				return true;
			}
		}
		return false;
	}
		
	/**
	 * Estória #29814
	 * @param paciente
	 * @param unidade
	 * @param computador
	 * @return Retorna true caso deva imprimir a pulseira de identificação do paciente.
	 * @throws ApplicationBusinessException
	 */
	public Boolean encaminharPacienteAcolhimento(Paciente paciente,
			MamUnidAtendem unidade, Short unfSeqMicroComputador, String hostName, String nomeResponsavel) throws ApplicationBusinessException {
		
		preEncaminharPacienteAcolhimento(paciente, unidade, unfSeqMicroComputador, hostName);
		
		Short seqUnidadeDestino = obterUnidadeDestinoPaciente(paciente, unidade);
		MamTriagens triagem = this.mamTriagensRN.inserirTriagem(paciente, seqUnidadeDestino, hostName, unidade, nomeResponsavel);
		MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(triagem.getSeq());
		// RN03
		this.marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(triagem, mamTriagemOriginal, hostName);
		// RN04
		atualizarTriagemPorTipoMovimento(triagem, mamTriagemOriginal, DominioTipoMovimento.E, hostName);
		
		BigDecimal valParametro = null;
		valParametro = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_UNID_FUNC_EMERGENCIA_OBSTETRICA.toString(), VLR_NUMERICO);
		
		if (!valParametro.equals(new BigDecimal(seqUnidadeDestino))) {
			return true;
		}
		return false;
	}
	
	private void atualizarTriagemPorTipoMovimento(MamTriagens triagem, MamTriagens mamTriagemOriginal, DominioTipoMovimento tipoMovimento,
			String nomeComputador) throws ApplicationBusinessException {
		this.mamTriagensRN.atualizarTriagemPorTipoMovimento(triagem, mamTriagemOriginal, tipoMovimento, nomeComputador);
		
		MamMvtoTriagemId mamMvtoTriagemId = new MamMvtoTriagemId(triagem.getSeq(), this.mamMvtoTriagemDAO.obterMaxSeqPMvtoTriagem(triagem.getSeq()));
		
		MamMvtoTriagem mamMvtoTriagem = new MamMvtoTriagem();
		mamMvtoTriagem.setId(mamMvtoTriagemId);
		mamMvtoTriagem.setDthrMvto(new Date());
		mamMvtoTriagem.setTipoMvto(tipoMovimento.toString());
		
		this.mamMvtoTriagemRN.inserir(mamMvtoTriagem);
	}

	private void preEncaminharPacienteAcolhimento(Paciente paciente,
			MamUnidAtendem unidade, Short unfSeqMicroComputador, String hostName) throws ApplicationBusinessException {
		exibirMensagemAbortar(unidade == null, PacientesEmergenciaONExceptionCode.MAM_02765_1);

		exibirMensagemAbortar( !permissionService.usuarioTemPermissao(
				obterLoginUsuarioLogado(), "encaminharPacientesParaAcolhimento", "executar"), PacientesEmergenciaONExceptionCode.MAM_02793_1);
		
		exibirMensagemAbortar(!unidade.getIndRecepcao(), PacientesEmergenciaONExceptionCode.ERRO_UNIDADE_NAO_RECEBE_PACIENTES);
		validarUnidadeRecebeAcolheAtendePacientesPeloAGHU(unidade.getSeq());
		
		if (unfSeqMicroComputador != null) {
			// Passado uma lista com o unfSeqMicroComputador para poder reaproveitar a consulta.
			List<Short> listUnfSeqMicroComputador = new ArrayList<Short>();
			listUnfSeqMicroComputador.add(unfSeqMicroComputador);
			
			Short unfResult = pesquisarUnidadeFuncionalTriagemRecepcao(listUnfSeqMicroComputador, unidade.getUnfSeq());
			
			exibirMensagemAbortar(unfResult == null, PacientesEmergenciaONExceptionCode.ERRO_ACOLHIMENTO_NAO_PERTENCE_UNIDADE);
		}
		List<MamTriagens> pacinteNaEmergencia = mamTriagensDAO.verificarExisteTriagemPorPaciente(paciente.getCodigo());
		if(pacinteNaEmergencia != null && !pacinteNaEmergencia.isEmpty()) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MAM_02707_1, pacinteNaEmergencia.get(0).getUnidadeFuncional().getDescricao());
		}
		
		exibirMensagemAbortar(paciente.getDtObito() != null, PacientesEmergenciaONExceptionCode.MAM_02776_1);
			
		exibirMensagemAbortar(verificarAtendimentoVigentePorCodigo(paciente.getCodigo()), PacientesEmergenciaONExceptionCode.MAM_04070);
		
		// Chama estória #29855
		if (paciente.getProntuario() == null && permissionService.usuarioTemPermissao(
				obterLoginUsuarioLogado(), "gerarNumeroProntuarioVirtualAcolhimentoEmergencia", "executar")) {
			Integer numeroProntuarioVirtual = null;
			try {
				numeroProntuarioVirtual = gerarNumeroProntuarioVirtualPacienteEmergencia(paciente.getCodigo(), hostName); 
				paciente.setProntuario(numeroProntuarioVirtual);
			} catch (ServiceException e) {
				throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}
		}
	}
	
	private Short obterUnidadeDestinoPaciente(Paciente paciente, MamUnidAtendem unidade) throws ApplicationBusinessException {
		
		Short seqUnidadeDestino = unidade.getUnfSeq();
		if (unidade.getIndDivideIdade().equals(Boolean.TRUE)) {
			int idade = CoreUtil.calculaIdade(paciente.getDtNascimento());
			
			
			BigDecimal paramIdade=null;
			paramIdade = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_IDADE_TRG_PEDIATRICA.toString(), VLR_NUMERICO);
			
			if (CoreUtil.menorOuIgual(idade, paramIdade)) {
				
				BigDecimal paramUnidPediatrica=null;
				paramUnidPediatrica = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_UNF_EME_PEDIATRICA.toString(), VLR_NUMERICO);
				
				seqUnidadeDestino = paramUnidPediatrica.shortValue();
				
			} else {
				
				BigDecimal paramUnidAdulto=null;
				paramUnidAdulto = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_UNF_EME_ADULTO.toString(), VLR_NUMERICO);				
				seqUnidadeDestino = paramUnidAdulto.shortValue();
			}
		}
		return seqUnidadeDestino;
	}
	
	private void exibirMensagemAbortar(Boolean flag, PacientesEmergenciaONExceptionCode mensagemErro) throws ApplicationBusinessException{
		if (flag){
		    throw new ApplicationBusinessException(mensagemErro);
		}
	}
	
	public Boolean verificarAtendimentoVigentePorCodigo(final Integer codigo) throws ApplicationBusinessException {
		Boolean result = false;
		
		try {
			result = this.configuracaoService.verificarAtendimentoVigentePorCodigo(codigo);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	public String gerarEtiquetaPulseira(
			Integer pacCodigo, Integer atdSeq) throws ApplicationBusinessException {
		String zpl = null;
		
		try {
			zpl = this.pacienteService.gerarEtiquetaPulseira(pacCodigo,
					null);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return zpl;
	}
	
	public List<PacienteAcolhimentoVO> listarPacientesAcolhimento(Short unfSeq) throws ApplicationBusinessException {
		
		List<PacienteAcolhimentoVO> listVo = new ArrayList<PacienteAcolhimentoVO>();
		List<MamTriagens> listaTriagens = mamTriagensDAO.listarPacientesTriagemPorUnfSeq(unfSeq);
		
		if (listaTriagens != null && !listaTriagens.isEmpty()) {
			for (MamTriagens triagem : listaTriagens) {
				PacienteAcolhimentoVO vo = new PacienteAcolhimentoVO();
				vo.setTrgSeq(triagem.getSeq());
				vo.setCodigo(triagem.getPaciente().getCodigo());
				vo.setQueixaPrincipal(triagem.getQueixaPrincipal());
				
				Paciente paciente = obterPacientePorCodigo(triagem.getPaciente().getCodigo());
				vo.setProntuario(paciente.getProntuario());
				vo.setNome(paciente.getNome());
				vo.setIdade(CoreUtil.calculaIdade(paciente.getDtNascimento()));
				preencherChegada(triagem.getSeq(), vo);
				preencherGravidade(triagem.getSeq(), triagem.getUnidadeFuncional().getSeq(), vo);
				vo.setPacProntuarioFormatado(CoreUtil.formataProntuario(vo.getProntuario()));
				
				listVo.add(vo);
			}
			CoreUtil.ordenarLista(listVo, PacienteAcolhimentoVO.Fields.DT_HR_SITUACAO.toString(), true);
		}
		return listVo;
	}
	
	public Paciente obterPacientePorCodigo(Integer pacCodigo) throws ApplicationBusinessException {
		Paciente result = null;
		PacienteFiltro filtro = new PacienteFiltro();
		filtro.setCodigo(pacCodigo);
		
		try {
			result = this.pacienteService.obterPacientePorCodigoOuProntuario(filtro);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	private void preencherChegada(Long trgSeq, PacienteAcolhimentoVO vo) throws ApplicationBusinessException {
		List<Short> segSeqs = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.ENC_TRIAGEM);
		
		if(segSeqs == null || segSeqs.isEmpty()) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.ERRO_SITUACAO_EMERGENCIA_ENC_TRIAGEM_VAZIO);
		}
		
		List<Date> listDtHrSituacao = mamExtratoTriagemDAO.obterDtHrSituacaoExtratoTriagem(trgSeq, segSeqs.get(0));
		
		if (listDtHrSituacao != null && !listDtHrSituacao.isEmpty()) {
			vo.setDtHrSituacao(listDtHrSituacao.get(0));
		}
	}
	
	private void preencherGravidade(Long trgSeq, Short unfSeqTriagem, PacienteAcolhimentoVO vo) throws ApplicationBusinessException {
		Short grvSeq = mamTrgGravidadeDAO.obterGrvSeqPorTriagem(trgSeq);
		
		if (grvSeq != null) {
			MamGravidade gravidade = mamGravidadeDAO.obterPorChavePrimaria(grvSeq);
			
			vo.setOrdem(gravidade.getOrdem());
			vo.setCodCor(gravidade.getCodCor());
			
			StringBuffer descricaoGravidade = new StringBuffer(80);
			
			descricaoGravidade.append(gravidade.getDescricao());
			
			if (gravidade.getTempoEspera() != null) {
				Integer horas = DateUtil.getHoras(gravidade.getTempoEspera());
				Double minutos = DateUtil.getMinutos(gravidade.getTempoEspera());
				
				if (horas > 0) {
					descricaoGravidade.append(EM_ATE).append(horas).append(" hora(s)");
				}
				
				if (minutos > 0 && horas > 0) {
					descricaoGravidade.append(" e ").append(minutos.intValue()).append(" minutos");
					
				} else if (minutos > 0) {
					descricaoGravidade.append(EM_ATE).append(minutos.intValue()).append(" minuto(s)");
				}
			}
			vo.setDescricaoGravidade(descricaoGravidade.toString());
		} else{
			vo.setOrdem((short)-1);
		}
		
		List<Short> unfSeqTriagensJn = mamTriagensJnDAO.obterUnidadeTriagensJnPorTrgSeq(trgSeq);
		vo.setIndTransferido(Boolean.FALSE);
		vo.setIndGermesMulti(Boolean.FALSE);
		if (unfSeqTriagensJn != null && !unfSeqTriagensJn.isEmpty()) {
			if (!unfSeqTriagensJn.get(0).equals(unfSeqTriagem)) {
				vo.setCodCor("");
			}
			verificarCorLinhas(unfSeqTriagensJn, unfSeqTriagem, vo);
		}
	}
	
	private void verificarCorLinhas(List<Short> unfSeqTriagensJn, Short unfSeqTriagem, PacienteAcolhimentoVO vo) throws ApplicationBusinessException {
		
		if (verificarNotificacaoGmrPorCodigo(vo.getCodigo()).equals(Boolean.TRUE)) {
			vo.setIndGermesMulti(Boolean.TRUE);
		}
		
		for (Short unfSeqTriagemJn : unfSeqTriagensJn) {
			if (!unfSeqTriagemJn.equals(unfSeqTriagem)) {
				vo.setIndTransferido(Boolean.TRUE);
				vo.setIndGermesMulti(Boolean.FALSE);
				break;
			}
		}
	}
	
	public Boolean verificarNotificacaoGmrPorCodigo(Integer pacCodigo) throws ApplicationBusinessException {
		return controleInfeccaoFacade.verificarNotificacaoGmrPorCodigo(pacCodigo);
	}
	
	public void validarAcolhimentoPaciente(Long trgSeq, String hostName, MamUnidAtendem mamUnidAtendem) throws ApplicationBusinessException {
		MamTriagens triagem = mamTriagensDAO.obterPorChavePrimaria(trgSeq);
		MamTriagens mamTriagemOriginal = mamTriagensDAO.obterOriginal(triagem.getSeq());
		
		validarUnidadeRecebeAcolheAtendePacientesPeloAGHU(mamUnidAtendem.getSeq());
		
		exibirMensagemAbortar(mamCaractSitEmergDAO.isExisteSituacaoEmerg(triagem.getSituacaoEmergencia().getSeq(),
				DominioCaracteristicaEmergencia.CHECK_OUT), PacientesEmergenciaONExceptionCode.MAM_02760);
		
		exibirMensagemAbortar(triagem.getIndPacAtendimento().equals(DominioPacAtendimento.N), PacientesEmergenciaONExceptionCode.MAM_02761);
		
		exibirMensagemAbortar(!mamUnidAtendem.getIndTriagem(), PacientesEmergenciaONExceptionCode.ERRO_UNIDADE_NAO_PERMITE_ACOLHIMENTO);
		
		List<TrgEncInternoVO> trgEncInternoList = obterListaTrgEncInternoOrdenada(trgSeq);
		
		if (!trgEncInternoList.isEmpty()) {
			exibirMensagemAbortar(trgEncInternoList.get(0).getDthrInicio() != null, PacientesEmergenciaONExceptionCode.MAM_02755);
		}
		
		String triagemPaciente = verificarTriagemPaciente(triagem.getSituacaoEmergencia().getSeq());
		if (triagemPaciente.equalsIgnoreCase("S")) {
			atualizarTriagemPaciente(triagem, mamTriagemOriginal, hostName,  false);
		} else if (triagemPaciente.equalsIgnoreCase("R")) {
			atualizarTriagemPaciente(triagem, mamTriagemOriginal, hostName, true);
		} else {
			exibirMensagemAbortar(true, PacientesEmergenciaONExceptionCode.MAM_02832);
		}
	}
	
	/**
	 * Realiza a ordenação manual da lista
	 * @param trgSeq
	 * @return mtmc
	 * @throws ApplicationBusinessException
	 */
	private List<TrgEncInternoVO> obterListaTrgEncInternoOrdenada(Long trgSeq) throws ApplicationBusinessException {
		List<TrgEncInternoVO> listRetorno = mamTrgEncInternoDAO.obterListaTrgEncInternoOrdenada(trgSeq);
		
		List<Integer> numeros = new ArrayList<Integer>();
		for (TrgEncInternoVO vo : listRetorno) {
			numeros.add(vo.getConsultaNumero());
		}
		
		if (!numeros.isEmpty()) {
			List<ConsultaVO> listaConsulta = obterConsultasPorNumero(numeros);
			
			for (TrgEncInternoVO vo : listRetorno) {
				for (ConsultaVO consulta : listaConsulta) {
					if (vo.getConsultaNumero().equals(consulta.getNumero())) {
						vo.setDthrInicio(consulta.getDthrInicio());
					}
				}
			}
			CoreUtil.ordenarLista(listRetorno, TrgEncInternoVO.Fields.SEQP.toString(), false);
		}
		return listRetorno;
	}
	
	private List<ConsultaVO> obterConsultasPorNumero(List<Integer> numeros) throws ApplicationBusinessException {
		
		List<ConsultaVO> listaRetorno = new ArrayList<ConsultaVO>();
		
		try {
			listaRetorno = this.ambulatorioService.obterConsultasPorNumero(numeros);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return listaRetorno;
	}
	
	private String verificarTriagemPaciente(Short segSeq) {
		if (mamCaractSitEmergDAO.isExisteSituacaoEmerg(segSeq, DominioCaracteristicaEmergencia.LISTA_TRIAGEM)) {
			return "S";
		} else if (mamCaractSitEmergDAO.isExisteSituacaoEmerg(segSeq, DominioCaracteristicaEmergencia.ENC_INTERNO)
				|| mamCaractSitEmergDAO.isExisteSituacaoEmerg(segSeq, DominioCaracteristicaEmergencia.ENC_EXTERNO)) {
			return "R";
		}
		return "N";
	}
	
	/**
	 * TODO Por enquanto este método não irá retriar. Será desenvolvido em outra estória.
	 * @param trgSeq
	 * @param retriar
	 * @throws ApplicationBusinessException 
	 */
	private void atualizarTriagemPaciente(MamTriagens triagem, MamTriagens mamTriagemOriginal, String hostName,
			Boolean retriar) throws ApplicationBusinessException {
		if (retriar.equals(Boolean.FALSE)) {
			List<Short> segSeq = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.EM_TRIAGEM);
			triagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeq.get(0)));
			this.marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(triagem, mamTriagemOriginal, hostName);
		}
	}
	
	
	
	
	/**
	 * #28985 - C1 - Retorna a lista de pacientes aguardando atendimento emergencia
	 * @param unfSeq - seq da unidade
	 * @param espSeq - seq da especialidade
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MamPacientesAguardandoAtendimentoVO> listarPacientesAguardandoAtendimentoEmergencia(Short unfSeq, Short espSeq) throws ApplicationBusinessException{
		List<PacientesAguardandoAtendimentoVO> listaPacAguardandoAtend = new ArrayList<PacientesAguardandoAtendimentoVO>();
		List<MamPacientesAguardandoAtendimentoVO> listaFinalPacAguardandoAtend = new ArrayList<MamPacientesAguardandoAtendimentoVO>();
		List<ConsultaGradeAtendimentoVO> listaConsultaGradeAtendimento = new ArrayList<ConsultaGradeAtendimentoVO>();	
		listaPacAguardandoAtend = mamTriagensDAO.listarPacientesAguardandoPorUnfSeq(unfSeq);
		if (listaPacAguardandoAtend  != null && !listaPacAguardandoAtend.isEmpty()) {
			for (PacientesAguardandoAtendimentoVO pacientesAguardandoAtendimentoVO : listaPacAguardandoAtend) {
				listaConsultaGradeAtendimento   =  obterConsultaEspecialidade(pacientesAguardandoAtendimentoVO.getConNumero(), espSeq);
					if (listaPacAguardandoAtend  != null && !listaPacAguardandoAtend.isEmpty()) {
						for (ConsultaGradeAtendimentoVO vo : listaConsultaGradeAtendimento ) {
							MamPacientesAguardandoAtendimentoVO mamVo = new MamPacientesAguardandoAtendimentoVO();
							Paciente paciente = obterPacientePorCodigo(pacientesAguardandoAtendimentoVO.getPacCodigo());
							mamVo.setPacProntuario(paciente.getProntuario());
							mamVo.setPacNome(paciente.getNome());
							mamVo.setPacIdade(CoreUtil.calculaIdade(paciente.getDtNascimento()));
							mamVo.setEspSeq(vo.getEspSeq());
							mamVo.setEspSigla(vo.getEspSigla());
							mamVo.setDthrInicio(vo.getDthrInicio());
							mamVo.setAtdSeq(vo.getAtdSeq());
							mamVo.setGrdSeq(vo.getGrdSeq());
							mamVo.setTrgSeq(pacientesAguardandoAtendimentoVO.getTrgSeq());
							mamVo.setPacCodigo(pacientesAguardandoAtendimentoVO.getPacCodigo());
							mamVo.setSegSeq(pacientesAguardandoAtendimentoVO.getSegSeq());
							mamVo.setIndPacAtendimento(pacientesAguardandoAtendimentoVO.getIndPacAtendimento());
							mamVo.setIndPacEmergencia(pacientesAguardandoAtendimentoVO.getIndPacEmergencia());
							mamVo.setUnfSeq(pacientesAguardandoAtendimentoVO.getUnfSeq());
							mamVo.setUltTipoMvt(pacientesAguardandoAtendimentoVO.getUltTipoMvt());
							mamVo.setDthrUltMvto(pacientesAguardandoAtendimentoVO.getDthrUltMvto());
							mamVo.setIndImediato(pacientesAguardandoAtendimentoVO.getIndImediato());
							mamVo.setDthrPrevAtend(pacientesAguardandoAtendimentoVO.getDthrPrevAtend());
							mamVo.setSeqp(pacientesAguardandoAtendimentoVO.getSeqp());
							mamVo.setConNumero(pacientesAguardandoAtendimentoVO.getConNumero());
							mamVo.setDthrEstorno(pacientesAguardandoAtendimentoVO.getDthrEstorno());
							mamVo.setCor(obterCorTabelaAguardandoAtendimento(mamVo));
							mamVo.setOrdemGravidade(obterOrdemGravidade(mamVo.getTrgSeq()));
							preencherGravidadeAguardandoAtendimento(mamVo);
							preencherChegadaPacienteAguardandoAtendimento(mamVo);
							listaFinalPacAguardandoAtend.add(mamVo);
						}
					}
			}
		}
		CoreUtil.ordenarLista(listaFinalPacAguardandoAtend, MamPacientesAguardandoAtendimentoVO.Fields.ORDEM_GRAVIDADE.toString(), true);
		return listaFinalPacAguardandoAtend;
	}
	
	/**
	 * @ORADB MAMK_EMG_GENERICA_4
	 * @param trgSeq
	 * @return ordemGravidade
	 */
	private String obterOrdemGravidade(Long trgSeq) {
		StringBuilder ordem = new StringBuilder();
		
		OrdenacaoPorGravidadesVO ordemVO = this.mamTrgGravidadeDAO.obterOrdenacaoPorTrgSeq(trgSeq);
		if(ordemVO != null) {
			Short espSeq = this.ambulatorioService.obtemEspecialidadeDaGradePeloNumeroConsulta(ordemVO.getConNumero());
			MamAgrupGrvEsps gravEsps = this.mamAgrupGrvEspsDAO.obterPrevAtendAcolhimentoGravEsp(ordemVO.getAgrSeq(), espSeq);
			if(gravEsps != null && gravEsps.getIndPrevAtend() != null) {
				ordemVO.setIndPrevAtend(gravEsps.getIndPrevAtend());
			}
			
			if(DominioTipoPrevAtende.I.equals(ordemVO.getIndPrevAtend())) {
				ordem.append('1');
			} else {
				ordem.append('2');
			}
			ordem.append(String.format("%05d", ordemVO.getAgrOrdem()));
			if(DominioTipoPrevAtende.I.equals(ordemVO.getIndPrevAtend())) {
				ordem.append("0501010000");
			} else if(DominioTipoPrevAtende.C.equals(ordemVO.getIndPrevAtend())) {
				if(ordemVO.getDthrPrevAtend() != null) {
					ordem.append(DateUtil.obterDataFormatada(ordemVO.getDthrPrevAtend(), "yyMMddHHmm"));
				} else {
					ordem.append("9912312359");
				}
			} else {
				ordem.append("9912312359");
			}
			ordem.append(String.format("%014d", ordemVO.getTrgSeq()));
		}
		
		return ordem.toString();
	}
	
	
	/**
	 * Obter detalhamento das especialidades
	 * @param conNumero
	 * @param espSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ConsultaGradeAtendimentoVO> obterConsultaEspecialidade(Integer conNumero, Short espSeq ) throws ApplicationBusinessException {
		List<ConsultaGradeAtendimentoVO> result = null;
		try {
			List<Integer>  listaConNumero = new LinkedList<Integer>();
			listaConNumero.add(conNumero);			
			result = this.ambulatorioService.pesquisarPorConsultaPorNumeroConsultaEspecialidade(listaConNumero, espSeq);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	
	/**
	 * Preenche gravidade da listagem da aba Aguardando
	 * MAMK_SITUACAO_EMERG.MAMC_DT_ENC_TRIAGEM
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void preencherGravidadeAguardandoAtendimento(MamPacientesAguardandoAtendimentoVO vo) throws ApplicationBusinessException {
		Short grvSeq = mamTrgGravidadeDAO.obterGrvSeqPorTriagem(vo.getTrgSeq());
		
		if (grvSeq != null) {
			MamGravidade gravidade = mamGravidadeDAO.obterPorChavePrimaria(grvSeq);
			
			vo.setCorGravidade(gravidade.getCodCor());
			vo.setOrdem(gravidade.getOrdem());
			StringBuffer descricaoGravidade = new StringBuffer(80);
			
			descricaoGravidade.append(gravidade.getDescricao());
			
			if (gravidade.getTempoEspera() != null) {
				Integer horas = DateUtil.getHoras(gravidade.getTempoEspera());
				Double minutos = DateUtil.getMinutos(gravidade.getTempoEspera());
				
				if (horas > 0) {
					descricaoGravidade.append(EM_ATE).append(horas).append(" hora(s)");
				}
				
				if (minutos > 0 && horas > 0) {
					descricaoGravidade.append(" e ").append(minutos.intValue()).append(" minutos");
					
				} else if (minutos > 0) {
					descricaoGravidade.append(EM_ATE).append(minutos.intValue()).append(" minuto(s)");
				}
			}
			vo.setDescricaoGravidade(descricaoGravidade.toString());
		}
	}
	
	/**
	 * Preenche cor  da listagem da aba Aguardando
	 * MAMK_SITUACAO_EMERG.MAMC_DT_ENC_TRIAGEM
	 * @param vo
	 * @return
	 * @throws ApplicationBusinessException
	 */

	private boolean obterCorTabelaAguardandoAtendimento(MamPacientesAguardandoAtendimentoVO vo) throws ApplicationBusinessException{
		List<ConsultaEspecialidadeAlteradaRetornoVO> listaRetorno = obterConsultasEspecialidadeAlterada(vo.getConNumero(), vo.getEspSeq(), vo.getGrdSeq());	
		List<MamRegistro> registros = mamRegistrosDAO.obterRegistroPorTriagem(vo.getTrgSeq());
		MamRegistro registro = null;
		if(!registros.isEmpty()) {
			registro = registros.get(0);
		}
		MpmPrescricaoMedicaVO prescricaoMedicaVO = obterUltimaPrescricaoMedica(vo.getAtdSeq());
		EpePrescricaoEnfermagemVO prescricaoEnfermagemVO = obterUltimaPrescricaoEnfermagem(vo.getAtdSeq());
		SolicitacaoExamesVO solicitacaoExames = obterSolicitacaoExames(vo.getAtdSeq());
		//Se a consulta C5 não retornar nenhum registro, não deve pintar o a linha do grid de azul.
		if(listaRetorno != null && listaRetorno.size()	==	0){
			return false;
		//Senão se a consulta C6 retornar algum registro
		}else if (registro != null) {
			for (ConsultaEspecialidadeAlteradaRetornoVO consultaEspecialidadeAlteradaRetornoVO : listaRetorno) {
				//1. Se o esp_seq obtido na C6 for igual ao esp_seq obtido na C5 e a criado_em da C6 for maior que criado_em da C5, então não deve pintar a linha de azul.
				if(registro.getEspecialidade().getSeq() == consultaEspecialidadeAlteradaRetornoVO.getEspSeq() && consultaEspecialidadeAlteradaRetornoVO.getJnDateTime().before(registro.getCriadoEm())){
					return false;
				}
			}
			
			if(prescricaoMedicaVO != null && prescricaoMedicaVO.getCriadoEm().after(registro.getCriadoEm())){
				return false;
				//Senão se a consulta C8 retornar algum registro e a data de criado_em do primeiro retornado for maior que a criado_em da consulta C5, então não deve pintar a linha de azul.
				}else if(prescricaoEnfermagemVO != null && prescricaoEnfermagemVO.getCriadoEm().after(registro.getCriadoEm())){
					return false;
				//Senão se a consulta C9 retornar algum registro e a data de criado_em do primeiro retornado for maior que a criado_em da consulta C5, então não deve pintar a linha de azul.	
				}else if(solicitacaoExames != null && solicitacaoExames.getCriadoEm().after(registro.getCriadoEm())){
					return false;
			}
		} else {
		  return true;
		}
		return true;
			
	}

	
	/**
	 * 
	 * @param conNumero
	 * @param espSeq
	 * @param grdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<ConsultaEspecialidadeAlteradaRetornoVO> obterConsultasEspecialidadeAlterada(Integer conNumero, Short espSeq, Integer grdSeq) throws ApplicationBusinessException {
		List<ConsultaEspecialidadeAlteradaRetornoVO> result = null;
		try {		
			result = this.ambulatorioService.obterConsultasEspecialidadeAlterada(espSeq, grdSeq, conNumero);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
	}
	
	
	
	/**
	 * 
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmPrescricaoMedicaVO obterUltimaPrescricaoMedica(Integer atdSeq) throws ApplicationBusinessException {
		List<MpmPrescricaoMedicaVO> listResult = new ArrayList<MpmPrescricaoMedicaVO>();
		MpmPrescricaoMedicaVO prescricaoMedicaVO = null;
		try {			
			listResult = this.prescricaoMedicaService.obterPrescricaoTecnicaPorAtendimentoOrderCriadoEm(atdSeq);
			if (listResult.size() > 0) {
				prescricaoMedicaVO = listResult.get(0);
			}
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return prescricaoMedicaVO;
	}
	
	
	private EpePrescricaoEnfermagemVO obterUltimaPrescricaoEnfermagem(Integer atdSeq) throws ApplicationBusinessException {
		List<EpePrescricaoEnfermagemVO> listResult = new ArrayList<EpePrescricaoEnfermagemVO>();
		EpePrescricaoEnfermagemVO prescricaoEnfermagemVO = null;
		
		listResult = prescricaoEnfermagemFacade.pesquisarPrescricaoEnfermagemPorAtendimentoEmergencia(atdSeq);
		if (listResult != null && listResult.size() > 0) {
			prescricaoEnfermagemVO = listResult.get(0);
		}
		
		return prescricaoEnfermagemVO;
	}
	
	/**
	 * Consulta solicitação de exames
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private SolicitacaoExamesVO obterSolicitacaoExames(Integer atdSeq) throws ApplicationBusinessException {
		List<SolicitacaoExamesVO> listResult = new ArrayList<SolicitacaoExamesVO>();
		SolicitacaoExamesVO solicitacaoExames = null;
		try {			
			listResult = this.examesService.obterSolicitacaoExamesPorAtendimento(atdSeq);
			if (listResult!= null && listResult.size() > 0) {
				solicitacaoExames = listResult.get(0);
			}
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return solicitacaoExames;
	}
	
	/**
	 * Preenche informações da chegada do paciente na aba aguardando
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void preencherChegadaPacienteAguardandoAtendimento(MamPacientesAguardandoAtendimentoVO vo) throws ApplicationBusinessException {
		List<Short> segSeqs = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.ENC_TRIAGEM);
		
		if(segSeqs == null || segSeqs.isEmpty()) {
			throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.ERRO_SITUACAO_EMERGENCIA_ENC_TRIAGEM_VAZIO);
		}
		
		List<Date> listDtHrSituacao = mamExtratoTriagemDAO.obterDtHrSituacaoExtratoTriagem(vo.getTrgSeq(), segSeqs.get(0));
		
		if (listDtHrSituacao != null && !listDtHrSituacao.isEmpty()) {
			vo.setDthrSituacao(listDtHrSituacao.get(0));
		}
	}

	public Paciente reimpressaoPulseiraPacEmergencia(Integer pacCodigo, MamUnidAtendem unidade) throws ApplicationBusinessException {

		Paciente paciente = obterPacientePorCodigo(pacCodigo);

		exibirMensagemAbortar(paciente.getDtObito() != null, PacientesEmergenciaONExceptionCode.MAM_02776_1);

		return paciente;
	}
	
	public void validarUnidadeRecebeAcolheAtendePacientesPeloAGHU(
            Short unfSeq) throws ApplicationBusinessException {
        String apenasPerinatoRecebePacientes = null;
        apenasPerinatoRecebePacientes = (String) parametroFacade.obterAghParametroPorNome(AghuParametrosEnum.P_APENAS_PERINATOLOGIA_RECEBE_CLASSIFICA_PACIENTES.toString(), "vlrTexto");
        
        if(apenasPerinatoRecebePacientes.equals(DominioSimNao.S.toString())) {
            BigDecimal valParametro = new BigDecimal(124);
            
            valParametro = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_UNID_FUNC_EMERGENCIA_OBSTETRICA.toString(), "vlrNumerico");
            
            if (!valParametro.equals(new BigDecimal(unfSeq))) {
                throw new ApplicationBusinessException(PacientesEmergenciaONExceptionCode.ERRO_APENAS_CO_RECEBE_ACOLHE_PACIENTES);
            }
        }
    }
	
	public Integer gerarNumeroProntuarioVirtualPacienteEmergencia(Integer pacCodigo, String nomeMicrocomputador)
			throws ServiceException, ApplicationBusinessException {
		
		Integer prontuarioVirtual = null;
		
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
		
		prontuarioVirtual = this.cadastroPacienteFacade.gerarNumeroProntuarioVirtualPacienteEmergencia(paciente, nomeMicrocomputador);
		
		return prontuarioVirtual;
	}
}
