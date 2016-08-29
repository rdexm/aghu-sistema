package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaGradeAtendimentoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.service.ICertificacaoDigitalService;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.OrigemPacAtendimentoVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamRegistrosDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.vo.MamPacientesAtendidosVO;
import br.gov.mec.aghu.emergencia.vo.PacientesAtendidosVO;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.exames.vo.SolicitacaoExamesValidaVO;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.prescricaomedica.service.IPrescricaoMedicaService;
import br.gov.mec.aghu.prescricaomedica.vo.AltaSumariaVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioConcluidoVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioServiceVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;
/**
 * Regras de negócio relacionadas ao Paciente Atendidos.
 * 
 * @author felipe.rocha
 * 
 */
@Stateless
public class PacientesEmergenciaAtendidosON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3423984755101821178L;
	
	private static final String TIPO_ALTA = "Alta";
	private static final String TIPO_OBITO = "Óbito";
	private static final String EMERGENCIA_OBSTETRICA = "EMERGENCIA_OBSTETRICA";
	private static final String ORIGEM_AMBULATORIO = "A";
	private static final Integer TIPO_MODAL_ESTORNADO = 1;
	private static final Integer TIPO_MODAL_OBITO = 2;
	private static final Integer TIPO_REDIRECIONA = 3;


	@EJB
	private IAghuFacade aghuFacade;		
	@EJB
	private IConfiguracaoService configuracaoService;

	@EJB
	private ICertificacaoDigitalService certificacaoDigitalService;
	
	@EJB
	private IPrescricaoMedicaService prescricaoMedicaService;
	
	@EJB
	private PacientesEmergenciaON pacientesEmergenciaON;
	
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;
	
	@Inject
	private MamRegistrosDAO mamRegistrosDAO;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IExamesService examesService;
	
	@EJB
	private IRegistroColaboradorService registroColaboradorService;

	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	private enum PacientesEmergenciaAtendidosONExceptionCode implements BusinessExceptionCode {MENSAGEM_ABA_ATENDIDOS_MAM_03686, 
		MENSAGEM_ABA_ATENDIDOS_MAM_03805_1, MENSAGEM_ABA_ATENDIDOS_MAM_03652, MENSAGEM_ABA_ATENDIDOS_MAM_03806, MENSAGEM_ABA_ATENDIDOS_ERRO_SITUACAO_EMERGENCIA_EM_ATEND_VAZIO,
		MENSAGEM_ABA_ATENDIDOS_ERRO_SITUACAO_EMERGENCIA_NO_CONSULTORIO_VAZIO, MENSAGEM_SERVICO_INDISPONIVEL;
	}
	/**
	 * #28986 - C1
	 * @param unfSeq
	 * @param espSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<MamPacientesAtendidosVO> listarPacientesAtendidos(Short unfSeq, Short espSeq) throws ApplicationBusinessException {	
		List<PacientesAtendidosVO> listVo = this.mamTriagensDAO.listarPacientesAtendidoPorUnfSeq(unfSeq);
		List<ConsultaGradeAtendimentoVO> listaConsultaGradeAtendimento = new ArrayList<ConsultaGradeAtendimentoVO>();	
		List<MamPacientesAtendidosVO> listaFinalPacientesAtendidos = new ArrayList<MamPacientesAtendidosVO>();
		if (listVo != null && !listVo.isEmpty()) {
			for (PacientesAtendidosVO pacientesAtendidosVO : listVo) {
				 listaConsultaGradeAtendimento   =  this.pacientesEmergenciaON.obterConsultaEspecialidade(pacientesAtendidosVO.getConNumero(), espSeq);
				  for (ConsultaGradeAtendimentoVO voGrade : listaConsultaGradeAtendimento ) {
					  	MamPacientesAtendidosVO mamVo = new MamPacientesAtendidosVO();
						Paciente paciente = this.pacientesEmergenciaON.obterPacientePorCodigo(pacientesAtendidosVO.getPacCodigo());
						mamVo.setPacProntuario(paciente.getProntuario());
						mamVo.setPacNome(paciente.getNome());
						mamVo.setPacIdade(CoreUtil.calculaIdade(paciente.getDtNascimento()));
						mamVo.setEspSeq(voGrade.getEspSeq());
						mamVo.setEspSigla(voGrade.getEspSigla());
						mamVo.setPacCodigo(pacientesAtendidosVO.getPacCodigo());
						mamVo.setTrgSeq(pacientesAtendidosVO.getTrgSeq());
						mamVo.setSegSeq(pacientesAtendidosVO.getSegSeq());
						mamVo.setUltTipoMvt(pacientesAtendidosVO.getUltTipoMvt());
						mamVo.setIndPacAtendimento(pacientesAtendidosVO.getIndPacAtendimento());
						mamVo.setUnfSeq(pacientesAtendidosVO.getUnfSeq());
						mamVo.setAtdSeq(voGrade.getAtdSeq());
						mamVo.setDtConsulta(voGrade.getDtConsulta());
						mamVo.setPacProntuarioFormatado(CoreUtil.formataProntuario(paciente.getProntuario()));
						mamVo.setSeqp(pacientesAtendidosVO.getSeqp());
						mamVo.setConNumero(pacientesAtendidosVO.getConNumero());
					if (pacientesAtendidosVO.getConNumero() != null) {
						mamVo.setLocalizacao(obterLocalizacaoPaciente(pacientesAtendidosVO.getConNumero(), pacientesAtendidosVO.getPacCodigo()));
					}
					if (pacientesAtendidosVO !=	null) {
						mamVo.setPendenciaAssinaturaDigital(existePendenciaAssinaturaDigital(voGrade.getAtdSeq()));
					}
					
					//RN02
					mamVo.setTooltipUltimoAtendimento(obterMedicoUltimoAtendimento(voGrade.getAtdSeq()));
					listaFinalPacientesAtendidos.add(mamVo);
				}
			}
		}
		
		Comparator<MamPacientesAtendidosVO> cp = MamPacientesAtendidosVO.getComparator(MamPacientesAtendidosVO.SortParameter.ID_DESCENDING, MamPacientesAtendidosVO.SortParameter.NAME_ASCENDING);
		Collections.sort(listaFinalPacientesAtendidos, cp);

		return listaFinalPacientesAtendidos;
	}

	/**
	 * #28986 - RN 01
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private String obterLocalizacaoPaciente(Integer conNumero, Integer pacCodigo) throws ApplicationBusinessException{
		AltaSumariaVO vo = obterAltaSumaria(conNumero);
		String localizacao = null;
		if (vo != null) {
			if (vo.getTipo() == TIPO_ALTA) {
				String dataAlta = DateUtil.obterDataFormatadaHoraMinutoSegundo(vo.getDthrAlta());
				localizacao = super.getResourceBundleValue("MENSAGEM_ABA_ATENDIDOS_ALTA", dataAlta);		
			} else if (vo.getTipo() == TIPO_OBITO) {
				String dataAlta = DateUtil.obterDataFormatadaHoraMinutoSegundo(vo.getDthrAlta());
				localizacao = super.getResourceBundleValue("MENSAGEM_ABA_ATENDIDOS_OBITO", dataAlta);
				
			} else {
				Date dataInicio = this.mcoLogImpressoesDAO.obterDataInicioLogImpressoes(pacCodigo);
				if (dataInicio != null) {
					localizacao = super.getResourceBundleValue("MENSAGEM_ABA_ATENDIDOS_OUTROS", DateUtil.obterDataFormatadaHoraMinutoSegundo(dataInicio));
				}
			}
		}
		return localizacao;
	}
	
	/**
	 * RN 03
	 * @param trgSeq
	 * @param atdSeq
	 * @param unfSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Map<Integer, String> reabrirPacienteAtendidos(Long trgSeq, Integer atdSeq, Short unfSeq, String pacNome, Short espSeq, String hostName) throws ApplicationBusinessException {
		MamTriagens triagem = this.mamTriagensDAO.verificarPacienteEmergencia(trgSeq);
		// C11
		OrigemPacAtendimentoVO atendimentoVO = obterAghAtendimentosPorSeq(atdSeq);
		validarPacienteAtendidos(atdSeq, triagem, atendimentoVO);
		HashMap<Integer, String> resultado = new HashMap<>();
		// C8
		if (existeCaractUnidFuncionaisPorSeqCaracteristica(unfSeq, EMERGENCIA_OBSTETRICA)) {
			resultado.put(TIPO_REDIRECIONA, null);
			return resultado;
		} else {
			validarUsuarioPermissaoPrescricaoMedica();
			// C12
			MpmAltaSumarioServiceVO altaSumarioVO = obterMpmAltaSumarioPorAtendimento(atdSeq);
			if (altaSumarioVO != null) {
				if (altaSumarioVO.getTipo() == TIPO_ALTA) {
					resultado.put(TIPO_MODAL_ESTORNADO, super.getResourceBundleValue("MODAL_ABA_ATENDIDOS_ESTORNADO", pacNome));
					return resultado;
				}
				
				if (altaSumarioVO.getTipo() == TIPO_OBITO) {
					resultado.put(TIPO_MODAL_OBITO, super.getResourceBundleValue("MODAL_ABA_ATENDIDOS_OBITO"));
					return resultado;
				}
			}else{
				atualizarInformacoesPacientesAtendidos(atdSeq, espSeq, hostName, triagem, atendimentoVO, altaSumarioVO);
			}
		
		}
		
		return null;

	}

	/**
	 * 
	 * @param atdSeq
	 * @param espSeq
	 * @param hostName
	 * @param triagem
	 * @param atendimentoVO
	 * @param altaSumarioVO
	 * @throws ApplicationBusinessException
	 */
	private void atualizarInformacoesPacientesAtendidos(Integer atdSeq, Short espSeq, String hostName, MamTriagens triagem, OrigemPacAtendimentoVO atendimentoVO, MpmAltaSumarioServiceVO altaSumarioVO)
			throws ApplicationBusinessException {
		if (atendimentoVO.getOrigem() == ORIGEM_AMBULATORIO) {
			atualizarSituacaoTriagem(triagem, DominioCaracteristicaEmergencia.NO_CONSULTORIO, hostName);
		} else {
			atualizarSituacaoTriagem(triagem, DominioCaracteristicaEmergencia.EM_ATEND, hostName);
		}
		// C14
		List<Integer> seqsDocs = buscarSeqDocumentosAtendidos(atdSeq);
		for (Integer seqDoc : seqsDocs) {
			try {
				this.certificacaoDigitalService.inativarVersaoDocumento(seqDoc);
			} catch (ServiceException e) {
				throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}
		}
		this.emergenciaFacade.atualizaPrevisaoAtendimento(espSeq);
		if (altaSumarioVO != null && altaSumarioVO.getRegistroSeq() != null) {
			// C15
			DominioTipoFormularioAlta tipoFormularioAlta = this.emergenciaFacade.obterTipoFormularioAlta(altaSumarioVO.getRegistroSeq());
			if (tipoFormularioAlta.getCodigo()== DominioTipoFormularioAlta.S.getCodigo()) {
				tipoFormularioAlta = this.emergenciaFacade.obterTipoFormularioAlta(altaSumarioVO.getRegistroSeq());
				//TODO deve ser chamado p_chama_registro_atend
			} else {
				tipoFormularioAlta = this.emergenciaFacade.obterTipoFormularioAlta(altaSumarioVO.getRegistroSeq());
				////TODO deve ser chamado  p_chama_registro_atend
			}
		}
	}
	

	/**
	 * 
	 * @param triagem
	 * @param caracteristicaEmergencia
	 * @param hostName
	 * @throws ApplicationBusinessException
	 */
	private void atualizarSituacaoTriagem(MamTriagens triagem,DominioCaracteristicaEmergencia caracteristicaEmergencia, String  hostName) throws ApplicationBusinessException {
		List<Short> segSeqs = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(caracteristicaEmergencia);
		if (segSeqs == null || segSeqs.isEmpty()) {
			if (caracteristicaEmergencia == DominioCaracteristicaEmergencia.NO_CONSULTORIO) {
				throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_ABA_ATENDIDOS_ERRO_SITUACAO_EMERGENCIA_NO_CONSULTORIO_VAZIO);
			}
			if (caracteristicaEmergencia == DominioCaracteristicaEmergencia.EM_ATEND) {
				throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_ABA_ATENDIDOS_ERRO_SITUACAO_EMERGENCIA_EM_ATEND_VAZIO);
			}
		}
		MamTriagens triagemOriginal = this.mamTriagensDAO.obterOriginal(triagem.getSeq());
		MamSituacaoEmergencia situacaoEmergencia =  mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeqs.get(0));
		triagem.setSituacaoEmergencia(situacaoEmergencia);
		this.emergenciaFacade.atualizarSituacaoTriagem(triagem, triagemOriginal, hostName);
	}
	
	
	/**
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void validarUsuarioPermissaoPrescricaoMedica() throws ApplicationBusinessException {
		Boolean temPermissaoPrescricao = getPermissionService().usuarioTemPermissao(
				obterLoginUsuarioLogado(), "estornarAltaPacienteEmergencia", "executar");
		if(!temPermissaoPrescricao) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_ABA_ATENDIDOS_MAM_03652);
		}
	}
	
	/**
	 * #28986 - RN 02
	 * @param atdSeq
	 * @param triagem
	 * @throws ApplicationBusinessException
	 */
	private void validarPacienteAtendidos(Integer atdSeq, MamTriagens triagem, OrigemPacAtendimentoVO atendimentoVO)
			throws ApplicationBusinessException {
		if (triagem == null) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_ABA_ATENDIDOS_MAM_03686);
		}
		if (!this.mamCaractSitEmergDAO.isExisteSituacaoEmerg(triagem.getSituacaoEmergencia().getSeq(), DominioCaracteristicaEmergencia.LISTA_ATENDIDO)) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_ABA_ATENDIDOS_MAM_03805_1);	
		}
		if (atendimentoVO != null && (StringUtils.isNotBlank(atendimentoVO.getIndPacAtendimento()) && atendimentoVO.getIndPacAtendimento().equalsIgnoreCase(DominioPacAtendimento.N.getDescricao())) &&  
		   (StringUtils.isNotBlank(atendimentoVO.getOrigem()) && (atendimentoVO.getOrigem().equalsIgnoreCase(DominioOrigemAtendimento.I.getDescricao()) || atendimentoVO.getOrigem().equalsIgnoreCase(DominioOrigemAtendimento.U.getDescricao())))){
		 	throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_ABA_ATENDIDOS_MAM_03806);	
		}
	}
	/**
	 * #28986 - RN 04
	 * @param atdSeq

	 * @param nomeMicromputador
	 * @throws ApplicationBusinessException
	 */
	public void desbloqueioSumarioAlta(Integer atdSeq, String nomeMicromputador) throws ApplicationBusinessException{
		try {
			this.prescricaoMedicaService.atualizarSumarioAltaApagarDadosAlta(atdSeq, nomeMicromputador);
			MpmAltaSumarioConcluidoVO sumarioConcluido = obterMpmAltaSumarioConcluidoPorAtendimento(atdSeq);
			if (sumarioConcluido != null) {
				this.prescricaoMedicaService.desbloquearSumarioAlta(atdSeq, sumarioConcluido.getPacienteSeq(), sumarioConcluido.getSeqp(), nomeMicromputador);
			}
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	
	/**
	 * 
	 * #28986 - RN 02
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	
	private String obterMedicoUltimoAtendimento(Integer atdSeq) throws ApplicationBusinessException {

		SolicitacaoExamesValidaVO examesVO;
		MpmPrescricaoMedicaVO prescricaoMedicaVO;
		MamRegistro registro;
		try {
			// C4
			registro = obterRegistroAtendimento(atdSeq);

			// C5
			prescricaoMedicaVO = this.prescricaoMedicaService.obterPrescricaoMedicaPorAtendimento(atdSeq);

			// C6
			examesVO = this.examesService.buscarUltimaSolicitacaoExames(atdSeq);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}

		if (examesVO != null && examesVO.getSerMatriculaValida() != null && prescricaoMedicaVO != null && prescricaoMedicaVO.getMatriculaValida() != null) {

			if (DateUtil.validaDataMaior(examesVO.getCriadoEm(), prescricaoMedicaVO.getCriadoEm())) {
				if (registro != null && DateUtil.validaDataMaior(registro.getCriadoEm(), examesVO.getCriadoEm())) {
					return obterMensagemMedicoUltimoAtendimento(registro.getServidor().getId().getMatricula(), registro.getServidor().getId().getVinCodigo(), registro.getCriadoEm());

				} else {
					// vai ser exame
					return obterMensagemMedicoUltimoAtendimento(examesVO.getSerMatriculaValida(), examesVO.getSerVinculoValida(), examesVO.getCriadoEm());
				}

			} else if (registro != null && DateUtil.validaDataMaior(registro.getCriadoEm(), prescricaoMedicaVO.getCriadoEm())) {
				return obterMensagemMedicoUltimoAtendimento(registro.getServidor().getId().getMatricula(), registro.getServidor().getId().getVinCodigo(), registro.getCriadoEm());

			} else {
				return obterMensagemMedicoUltimoAtendimento(prescricaoMedicaVO.getMatriculaValida(), prescricaoMedicaVO.getMatriculaVincValida().shortValue(), prescricaoMedicaVO.getCriadoEm());
			}
		} else if (examesVO != null && examesVO.getSerMatriculaValida() != null) {
			if (registro != null && DateUtil.validaDataMaior(registro.getCriadoEm(), examesVO.getCriadoEm())) {
				return obterMensagemMedicoUltimoAtendimento(registro.getServidor().getId().getMatricula(), registro.getServidor().getId().getVinCodigo(), registro.getCriadoEm());

			} else {
				// vai ser exame
				return obterMensagemMedicoUltimoAtendimento(examesVO.getSerMatriculaValida(), examesVO.getSerVinculoValida(), examesVO.getCriadoEm());
			}
		} else if (prescricaoMedicaVO != null && prescricaoMedicaVO.getMatriculaValida() != null) {
			if (registro != null && DateUtil.validaDataMaior(registro.getCriadoEm(), prescricaoMedicaVO.getCriadoEm())) {
				// vai ser registro
				return obterMensagemMedicoUltimoAtendimento(registro.getServidor().getId().getMatricula(), registro.getServidor().getId().getVinCodigo(), registro.getCriadoEm());
			} else {
				// vai ser prescricao
				return obterMensagemMedicoUltimoAtendimento(prescricaoMedicaVO.getMatriculaValida(), prescricaoMedicaVO.getMatriculaVincValida().shortValue(), prescricaoMedicaVO.getCriadoEm());
			}
		}

		return null;
	}
	
	
	private String obterMensagemMedicoUltimoAtendimento(Integer matricula, Short vincodigo, Date criadoEm) throws ApplicationBusinessException{
		
		try {
			Object[] resultado = this.prescricaoMedicaService.buscaConsProf(matricula, vincodigo);
			
			return super.getResourceBundleValue("MENSAGEM_ABA_ATENDIDOS_ULTIMO_ATENDIMENTO", resultado[0], criadoEm);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		
	}

	//Para cada registro verifica no servidor com matricula e vinculo	
	private MamRegistro obterRegistroAtendimento(Integer atdSeq) throws ApplicationBusinessException{
		List<MamRegistro> listaRegistro = this.mamRegistrosDAO.obterRegistroPorAtendimento(atdSeq);
		
		List<MamRegistro> retornoList = new ArrayList<MamRegistro>();
		
		for (MamRegistro registro : listaRegistro) {
			try {
				if(this.registroColaboradorService.existeServidorCategoriaProfMedico(registro.getServidor().getId().getMatricula(), registro.getServidor().getId().getVinCodigo())){
					retornoList.add(registro);
				}
			} catch (ServiceBusinessException e) {
				throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			} catch (ServiceException e) {
				throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
			}
		}
	
		if(!retornoList.isEmpty()){
//			Collections.sort(retornoList, new Comparator<MamRegistro>() {
//				@Override
//				public int compare(MamRegistro o1, MamRegistro o2) {
//					if (o1.getCriadoEm().compareTo(o2.getCriadoEm()) == 0) {
//						return o1.getCriadoEm().compareTo(o2.getCriadoEm());
//					} else {
//						return o1.getCriadoEm().compareTo(o2.getCriadoEm());
//					}
//				}
//			});
			
			return retornoList.get(0);
		}
		
		return null;
	}

	protected MpmPrescricaoMedicaVO obterPrescricaoMedicaPorAtendimento(Integer atdSeq) throws ApplicationBusinessException{
		MpmPrescricaoMedicaVO vo;
		try {
			vo = this.prescricaoMedicaService.obterPrescricaoMedicaPorAtendimento(atdSeq);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return vo;
	}
	
	protected OrigemPacAtendimentoVO obterAghAtendimentosPorSeq(Integer atdSeq) throws ApplicationBusinessException{
		OrigemPacAtendimentoVO vo;
		try {
			vo = this.configuracaoService.obterAghAtendimentosPorSeq(atdSeq);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return vo;
	}
	
	protected MpmAltaSumarioServiceVO obterMpmAltaSumarioPorAtendimento(Integer atdSeq) throws ApplicationBusinessException{
		MpmAltaSumarioServiceVO vo;
		try {
			vo = this.prescricaoMedicaService.obterMpmAltaSumarioPorAtendimento(atdSeq);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return vo;
	}
	protected AltaSumariaVO obterAltaSumaria(Integer conNumero) throws ApplicationBusinessException{
		AltaSumariaVO vo;
		try {
			vo = this.prescricaoMedicaService.pesquisarAltaSumariosPorConsultaNumero(conNumero);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return vo;
	}
	
	protected Boolean existeCaractUnidFuncionaisPorSeqCaracteristica(Short unfSeq, String caracteristica) throws ApplicationBusinessException{
		Boolean existeCaractUnidFuncionaisPorSeqCaracteristica = Boolean.FALSE;
		try {
			existeCaractUnidFuncionaisPorSeqCaracteristica = this.aghuFacade.existeCaractUnidFuncionaisPorSeqCaracteristica(unfSeq,ConstanteAghCaractUnidFuncionais.EMERGENCIA_OBSTETRICA);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return existeCaractUnidFuncionaisPorSeqCaracteristica;
	}
	
//	protected Boolean verificarAcaoQualificacaoMatricula(String descricao)  throws ApplicationBusinessException{
//		Boolean usuarioQualificado = Boolean.FALSE;
//		try {
//			usuarioQualificado = this.pacienteService.verificarAcaoQualificacaoMatricula(descricao);
//		} catch (ServiceException e) {
//			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
//		} catch (RuntimeException e) {
//			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
//		}
//		return usuarioQualificado;
//	}
	
	protected List<Integer> buscarSeqDocumentosAtendidos(Integer seqAtendimento)throws ApplicationBusinessException{
		List<Integer> seqs = new LinkedList<Integer>(); 
		try {
			seqs = this.certificacaoDigitalService.buscarSeqDocumentosAtendidos(seqAtendimento);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return seqs;
	}
	
	protected MpmAltaSumarioConcluidoVO obterMpmAltaSumarioConcluidoPorAtendimento(Integer atdSeq) throws ApplicationBusinessException{
		MpmAltaSumarioConcluidoVO vo;
		try {
			vo = this.prescricaoMedicaService.obterMpmAltaSumarioConcluidoPorAtendimento(atdSeq);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return vo;
	}
	
	protected Boolean existePendenciaAssinaturaDigital(Integer seqAtendimento) throws ApplicationBusinessException{
		try {
			return this.certificacaoDigitalService.existePendenciaAssinaturaDigital(seqAtendimento);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(PacientesEmergenciaAtendidosONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		
	}
}
