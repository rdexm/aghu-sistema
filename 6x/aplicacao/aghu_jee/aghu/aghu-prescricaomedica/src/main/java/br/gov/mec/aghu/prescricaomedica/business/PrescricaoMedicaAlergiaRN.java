package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEstadoPacienteDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoRegistroDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamMotivoAtendimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRegistroDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.blococirurgico.vo.CurTeiVO;
import br.gov.mec.aghu.casca.dao.CseProcessosDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaractUnidFuncionaisDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MamExtratoRegistro;
import br.gov.mec.aghu.model.MamExtratoRegistroId;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MamEstadoPacienteMantidoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ParametrosProcedureVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class PrescricaoMedicaAlergiaRN extends BaseBusiness {


	private static final String P_SEQ_PROC_MOE_EMG = "P_SEQ_PROC_MOE_EMG";

	private static final String CONFIRMAR_PRESCRICAO = "prescricaoMedica";

	private static final String CONFIRMAR = "confirmar";

	/**
	 * 
	 */
	private static final long serialVersionUID = -2376994824850355477L;
	
	private static final String STRING_S = "S";
	private static final String STRING_N = "N";

	private static final String P_UNF_CTI_PAI_ADULTO = "P_UNF_CTI_PAI_ADULTO";
	private static final String P_EXIGE_ESTADO_PAC = "P_EXIGE_ESTADO_PAC";
	private static final String P_HR_LIM_CONTROL_PREV_ALTA = "P_HR_LIM_CONTROL_PREV_ALTA";
	private static final String P_DIAS_PADRAO_CONTROL_PREV_ALTA = "P_DIAS_PADRAO_CONTROL_PREV_ALTA";
	private static final String P_HORAS_CONTROLE_PREV_ALTA = "P_HORAS_CONTROLE_PREV_ALTA";
	private static final String P_USA_CONTROLE_PREV_ALTA = "P_USA_CONTROLE_PREV_ALTA";
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;
	
	@Inject
	private AghCaractUnidFuncionaisDAO aghCaractUnidFuncionaisDAO;
	
	@Inject
	private MamEstadoPacienteDAO mamEstadoPacienteDAO;
	
	@Inject
	private MamEstadoPacienteMantidoDAO mamEstadoPacienteMantidoDAO;
	
	@Inject
	private MamRegistroDAO mamRegistroDAO;
	
	@Inject
	private MamExtratoRegistroDAO mamExtratoRegistroDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamMotivoAtendimentoDAO mamMotivoAtendimentoDAO; 
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO unidadeFuncionalDAO;
	
	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO; 

	@EJB
    private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
    private CseProcessosDAO cseProcessosDAO;
	
//	@Inject
//	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	

	public enum PrescricaoMedicaAlergiaRNExceptionCode implements BusinessExceptionCode {
		MAM_03547, MAM_03548, MAM_03546, MAM_03545, MS01_ERRO_ATUALIZACAO_IND_PEDIATRICO; 
	}
	
	private static final Log LOG = LogFactory.getLog(PrescricaoMedicaAlergiaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * #44179 - RF01
	 * @param prescricaoMedicaVO
	 * @param checkPacientePediatrico
	 */
	public void atualizarIndPacientePediatrico(PrescricaoMedicaVO prescricaoMedicaVO) throws ApplicationBusinessException{
		AghAtendimentos atendimento = aghAtendimentoDAO.obterAghAtendimentoPorSeq(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getSeq());
		
		atendimento.setIndPacPediatrico(prescricaoMedicaVO.getIndPacPediatrico());
		
		try {
			this.aghAtendimentoDAO.atualizar(atendimento);
		} catch (Exception e) {
			throw new ApplicationBusinessException(PrescricaoMedicaAlergiaRNExceptionCode.MS01_ERRO_ATUALIZACAO_IND_PEDIATRICO);
		}
	}
	
	/**
	 * #44179 - P01
	 * @ORADB - RN_PMEP_VER_EST_PAC2  
	 */
	public String consultarPreenchimentoEstadoPaciente(Integer pAtdSeq, Integer pSeq, Long pRgtSeq, String usuarioLogado){
		
		//retorno
		String pFaltaEstPac = null;
		
		String vSisExigeEstadoPaciente;
		String vElaborarPme = null;
//		String vValidarPme;
		String vUnidEmergTerreo;
//		String vTemEstPac = null;
		String vFaltaEstPac;
		Short vUnfSeq = null;
		BigDecimal vUnfCtiPaiAdulto = null;
		
		Short curAtd = aghAtendimentoDAO.obterUnfSeqPorAtendimentoSeq(pAtdSeq);
		
		vFaltaEstPac = STRING_N;
		
		AghParametros pExigeEstadoPac = parametroFacade.obterAghParametroPorNome(P_EXIGE_ESTADO_PAC);
		
		if(pExigeEstadoPac != null){
			vSisExigeEstadoPaciente = pExigeEstadoPac.getVlrTexto();
		} else {
			vSisExigeEstadoPaciente = STRING_N;
		}
		
		if(STRING_S.equals(vSisExigeEstadoPaciente)){
//			boolean vValidarPmePermissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), CONFIRMAR_PRESCRICAO, CONFIRMAR);
			boolean vElaborarPmePermissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), CONFIRMAR_PRESCRICAO, CONFIRMAR);
//			boolean vElaborarPmePermissao = cascaFacade.temPermissao(usuarioLogado,  CONFIRMAR_PRESCRICAO, CONFIRMAR);
//			if(vValidarPmePermissao){
//				vValidarPme = STRING_S;
//			}
			if(vElaborarPmePermissao){
				vElaborarPme = STRING_S;
			}
			
			if(STRING_S.equals(vElaborarPme)){
				vUnfSeq = curAtd;
				
				boolean verificaUnidEmergTerreo = aghCaractUnidFuncionaisDAO.verificarCaracteristicaUnidadeFuncional(vUnfSeq, ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO);
				if(verificaUnidEmergTerreo){
					vUnidEmergTerreo = STRING_S;
				} else {
					vUnidEmergTerreo = STRING_N;
				}
				
				if(STRING_N.equals(vUnidEmergTerreo)){
					AghParametros pUnfCtiPaiAdulto = parametroFacade.obterAghParametroPorNome(P_UNF_CTI_PAI_ADULTO);
					
					if(pUnfCtiPaiAdulto != null){
						vUnfCtiPaiAdulto = pUnfCtiPaiAdulto.getVlrNumerico();
					}
					
					vUnidEmergTerreo = validacaoPM01ParteUm(vUnidEmergTerreo,vUnfSeq, vUnfCtiPaiAdulto);
				}
				
				vFaltaEstPac = validacaoPM01ParteDois(pAtdSeq, pSeq, pRgtSeq,vUnidEmergTerreo, vFaltaEstPac);
			}
		}
		pFaltaEstPac = vFaltaEstPac;
		
		return pFaltaEstPac;
	}

	private String validacaoPM01ParteDois(Integer pAtdSeq, Integer pSeq,
			Long pRgtSeq, String vUnidEmergTerreo, String vFaltaEstPac) {
		String vTemEstPac;
		if(STRING_S.equals(vUnidEmergTerreo)){
			vTemEstPac = mamEstadoPacienteDAO.listarCursorMamEstadoPacientes(pAtdSeq, pSeq, pRgtSeq.longValue());	
			if(vTemEstPac == null){
				vTemEstPac = STRING_N;
			}
			
			if(STRING_N.equals(vTemEstPac)){
				vTemEstPac = mamEstadoPacienteDAO.listarCursorMamEstadoPacientes2(pRgtSeq.longValue());
				if(vTemEstPac == null){
					vTemEstPac = STRING_N;
				}
			}
			
			if(STRING_N.equals(vTemEstPac)){
				vTemEstPac = mamEstadoPacienteMantidoDAO.obterCursorMamEstadoPacienteMantido(pRgtSeq.longValue());
				if(vTemEstPac == null){
					vTemEstPac = STRING_N;
				}
			}
			
			if(STRING_S.equals(vTemEstPac)){
				vFaltaEstPac = STRING_N;
			} else if(STRING_N.equals(vTemEstPac)){
				vFaltaEstPac = STRING_S;
			}
		}
		return vFaltaEstPac;
	}

	private String validacaoPM01ParteUm(String vUnidEmergTerreo, Short vUnfSeq,
			BigDecimal vUnfCtiPaiAdulto) {
		if(vUnfCtiPaiAdulto != null){
			List<Short> curUnfCtiFilhos = aghUnidadesFuncionaisDAO.listarCursorUnfCtiFilhos(vUnfCtiPaiAdulto.intValue());
			
			if(!curUnfCtiFilhos.isEmpty()){
				for(Short obj : curUnfCtiFilhos){
					if(obj.equals(vUnfSeq)){
						vUnidEmergTerreo = STRING_S;
					}
				}
			}
		}
		return vUnidEmergTerreo;
	}
	
	//#44179
	public ParametrosProcedureVO validarParametros(String usuarioLogado, Integer codAtendimento, Integer seqPrescricao, ParametrosProcedureVO parametrosProcedureVO) throws ApplicationBusinessException{
		
//		Boolean retornoValidacao = Boolean.FALSE;
		
		parametrosProcedureVO.setvElaborarPrcrMedPermissao(this.permissionService.usuarioTemPermissao(usuarioLogado, CONFIRMAR_PRESCRICAO, CONFIRMAR));
		
		if(parametrosProcedureVO.isvElaborarPrcrMedPermissao()){
			parametrosProcedureVO.setvElaborarPrcrMed(STRING_S);
		} else {
			parametrosProcedureVO.setvElaborarPrcrMed(STRING_N);
		}
		
		//RF02 - 1
		AghParametros pUsaControlePrevAlta = parametroFacade.obterAghParametroPorNome(P_USA_CONTROLE_PREV_ALTA);
		
		parametrosProcedureVO.setvUsaControlePrevAlta(validacaoRF02ParteUm(pUsaControlePrevAlta));
		
		//RF02 - 2
		AghParametros pHorasControlePrevAlta = parametroFacade.obterAghParametroPorNome(P_HORAS_CONTROLE_PREV_ALTA);
		
		parametrosProcedureVO.setvHorasControlePrevAlta(validacaoRF02ParteDois(pHorasControlePrevAlta));
		
		//RF02 - 3
		AghParametros pDiasPadraoControlePrevAlta = parametroFacade.obterAghParametroPorNome(P_DIAS_PADRAO_CONTROL_PREV_ALTA);
		
		if(pDiasPadraoControlePrevAlta == null){
			parametrosProcedureVO.setvDiasPadraoCtrlPrevAlta(new BigDecimal(3));
		} else {
			parametrosProcedureVO.setvDiasPadraoCtrlPrevAlta(pDiasPadraoControlePrevAlta.getVlrNumerico());
		}
		
		//RF02 - 4
		AghParametros pHrLimControlePrevAlta = parametroFacade.obterAghParametroPorNome(P_HR_LIM_CONTROL_PREV_ALTA);
		
		if(pHrLimControlePrevAlta == null){
			Calendar dataPrevAlta = GregorianCalendar.getInstance();
			dataPrevAlta.set(Calendar.YEAR, 2012);
			dataPrevAlta.set(Calendar.MONTH, 02);
			dataPrevAlta.set(Calendar.DAY_OF_MONTH, 01);
			dataPrevAlta.set(Calendar.HOUR, 16);
			dataPrevAlta.set(Calendar.MINUTE, 00);
			dataPrevAlta.set(Calendar.SECOND, 00);
			parametrosProcedureVO.setvHrLimControlPrevAlta(dataPrevAlta.getTime());
		} else {
			parametrosProcedureVO.setvHrLimControlPrevAlta(pHrLimControlePrevAlta.getVlrData());
		}
		
		//RF02 - 5
//		Boolean edicao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), CONFIRMAR_PRESCRICAO, CONFIRMAR);

		//RF02 - 6
		AghParametros pExigeEstadoPac = parametroFacade.obterAghParametroPorNome(P_EXIGE_ESTADO_PAC);
		
		parametrosProcedureVO.setvSisExigeEstadoPaciente(validacaoRF02ParteUm(pExigeEstadoPac));
		AghAtendimentos atendimento;
		atendimento = aghAtendimentoDAO.buscarAtendimentoPorSeq(codAtendimento);
//		Variável v_unf_seq = atd.unf_seq
		if(atendimento != null){
			if(atendimento.getUnidadeFuncional() != null && atendimento.getUnidadeFuncional().getUnfSeq() != null){
				parametrosProcedureVO.setvUnfSeq(atendimento.getUnidadeFuncional().getUnfSeq().getSeq().intValue());
			}
		} else {
			parametrosProcedureVO.setvUnfSeq(0);
		}

		//RF02 - 7 
//		validacaoRF02ParteSete(codAtendimento, parametrosProcedureVO);
		
		//RF02 - 8
//		parametrosProcedureVO.setvMostraEstPac(validacaoRF02ParteOito(codAtendimento,retornoValidacao, edicao, parametrosProcedureVO.getvSisExigeEstadoPaciente(),parametrosProcedureVO.getvTemTriagem()));
		
		//RF02 - 9
		parametrosProcedureVO.setvUnidCti(STRING_N);
		
//		//RF02 - 10
//		AghParametros pUnfCtiPaiAdulto = parametroFacade.obterAghParametroPorNome(P_UNF_CTI_PAI_ADULTO);
//		
//		if(STRING_N.equals(parametrosProcedureVO.getvMostraEstPac())){
//			//10.1
//			if(pUnfCtiPaiAdulto != null){
//				parametrosProcedureVO.setvUnfCtiPaiAdulto(pUnfCtiPaiAdulto.getVlrNumerico());
//				if(parametrosProcedureVO.getvUnfSeq() != null){
//					if(parametrosProcedureVO.getvUnfSeq().equals(parametrosProcedureVO.getvUnfCtiPaiAdulto().intValue())){
//						parametrosProcedureVO.setvUnidCti(STRING_S);
//					}
//				}
//			}
//			//10.2
//			if(STRING_S.equals(parametrosProcedureVO.getvElaborarPrcrMed()) && STRING_S.equals(parametrosProcedureVO.getvSisExigeEstadoPaciente()) && STRING_S.equals(parametrosProcedureVO.getvUnidCti())){
//				parametrosProcedureVO.setvMostraEstPac(STRING_S);
//			}
//		}
		
		buscarEstadoPaciente(parametrosProcedureVO);
		
		return parametrosProcedureVO;
	}
	
    //#44179 - #49186
    public void buscarEstadoPaciente(ParametrosProcedureVO parametrosProcedureVO) {
    	String vMostraEstPac = StringUtils.EMPTY;
//    	   - O sistema deve exibir o estado do paciente de acordo com as regras abaixo:
//			
//			- O sistema deve buscar o parâmetro na tabela AGH_PARAMETROS, de acordo com a C03:
    	AghParametros pExigeEstadoPac = parametroFacade.obterAghParametroPorNome(P_EXIGE_ESTADO_PAC);
    	AghParametros pUnfCtiPaiAdulto = parametroFacade.obterAghParametroPorNome(P_UNF_CTI_PAI_ADULTO);
//      Se campo AGH_PARAMETROS.VLR_TEXTO = vazio (nulo), então
    	if(pExigeEstadoPac != null){
	    	if(pExigeEstadoPac.getVlrTexto() == null){
	//		   Variável v_sis_exige_estado_paciente = N,
	    		parametrosProcedureVO.setvSisExigeEstadoPaciente(STRING_N);
	// 			Se não
	    	} else {
	// 			Variável v_sis_exige_estado_paciente = AGH_PARAMETROS.VLR_TEXTO
	    		parametrosProcedureVO.setvSisExigeEstadoPaciente(pExigeEstadoPac.getVlrTexto());
	    	}
    	}

//			Executa consulta C07 para buscar o parâmetro.
    		regraBuscaEstadoPaciente(vMostraEstPac, parametrosProcedureVO, pUnfCtiPaiAdulto);

//    		Se variável v_unid_cti = ‘S’    
    		if(STRING_S.equals(parametrosProcedureVO.getvUnidCti()) 
//    				e variável v_sis_exige_estado_paciente = ‘S’
    				&& STRING_S.equals(parametrosProcedureVO.getvSisExigeEstadoPaciente())
//    				e v_elaborar_prcr_med = ‘S’
    				&& STRING_S.equals(parametrosProcedureVO.getvElaborarPrcrMed())){
//			Variável v_mostra_est_pac = ‘S’
    			parametrosProcedureVO.setvMostraEstPac(STRING_S);
    		} else {
    			parametrosProcedureVO.setvMostraEstPac(STRING_N);
    		}
    		
    }

	public void regraBuscaEstadoPaciente(String vMostraEstPac, ParametrosProcedureVO parametrosProcedureVO, AghParametros pUnfCtiPaiAdulto) {
		BigDecimal vUnfCtiPaiAdulto;
		List<Short> curUnfCtiFilhos;
//		Executa consulta C07 para buscar o parâmetro.
		if(pUnfCtiPaiAdulto != null){
//			Variável v_unf_cti_pai_adulto = AGH_PARAMETROS.VLR_NUMERICO
			vUnfCtiPaiAdulto = pUnfCtiPaiAdulto.getVlrNumerico();
//			Se variável v_unf_cti_pai_adulto for diferente de vazio (nulo), então
			if(vUnfCtiPaiAdulto != null){
//				Executa a consulta C08
				curUnfCtiFilhos = aghUnidadesFuncionaisDAO.listarCursorUnfCtiFilhos(vUnfCtiPaiAdulto.intValue());
				if(!curUnfCtiFilhos.isEmpty()){
//					Para cada registro da consulta deve fazer a validação abaixo:
					for(Short obj : curUnfCtiFilhos){
//						Se seq (da consulta C08) for igual a variável v_unf_seq, então
						if(obj.equals(parametrosProcedureVO.getvUnfSeq().shortValue())){
//							Variável v_unid_cti= ‘S’
							parametrosProcedureVO.setvUnidCti(STRING_S);
						}
					}
				}
			}
		}
	}

	public void validacaoRF02ParteSete(Integer codAtendimento, ParametrosProcedureVO parametrosProcedureVO) {
		AghAtendimentos atendimento;
		if(codAtendimento == null){
			parametrosProcedureVO.setvTemTriagem(STRING_N);
			parametrosProcedureVO.setvUnfSeq(0);
		} else {

			atendimento = aghAtendimentoDAO.buscarAtendimentoPorSeq(codAtendimento);
//			Variável v_unf_seq = atd.unf_seq
			if(atendimento.getUnidadeFuncional() != null && atendimento.getUnidadeFuncional().getUnfSeq() != null){
				parametrosProcedureVO.setvUnfSeq(atendimento.getUnidadeFuncional().getUnfSeq().getSeq().intValue());
			}
			
			AacConsultas consulta = prescricaoMedicaFacade.obterConsultaPorAtendimentoSeq(codAtendimento);
			if(consulta == null){
				parametrosProcedureVO.setvTemTriagem(STRING_N);
			} else {
				parametrosProcedureVO.setvTemTriagem(STRING_S); 
			}
		}
	}

	private BigDecimal validacaoRF02ParteDois(AghParametros pHorasControlePrevAlta) {
		BigDecimal v_horas_controle_prev_alta;
		if(pHorasControlePrevAlta == null){
			v_horas_controle_prev_alta = new BigDecimal(48);
		} else {
			v_horas_controle_prev_alta = pHorasControlePrevAlta.getVlrNumerico();
		}
		return v_horas_controle_prev_alta;
	}

	private String validacaoRF02ParteUm(AghParametros pUsaControlePrevAlta) {
		String v_usa_controle_prev_alta;
		if(pUsaControlePrevAlta == null){
			v_usa_controle_prev_alta = STRING_N;
		} else {
			v_usa_controle_prev_alta = pUsaControlePrevAlta.getVlrTexto();
		}
		return v_usa_controle_prev_alta;
	}

//	private String validacaoRF02ParteOito(Integer codAtendimento, Boolean retornoValidacao, Boolean edicao,	String v_sis_exige_estado_paciente, String v_tem_triagem) {
//		//RF02 - 8
//		AghAtendimentos aghAtendimento = prescricaoMedicaFacade.obterAghUnidadeFuncionalPorAtendimentoSeq(codAtendimento);
//		Boolean possuiCaracterisca = prescricaoMedicaFacade.obterCaracteristicaPorUnfSeq(aghAtendimento.getUnidadeFuncional().getSeq(), 
//						ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO);
//		String v_mostra_est_pac = StringUtils.EMPTY;
//		
//		if(possuiCaracterisca && edicao	&& STRING_S.equals(v_sis_exige_estado_paciente)	&& STRING_S.equals(v_tem_triagem) && codAtendimento == null){
//			v_mostra_est_pac = STRING_S;
//		} else {
//			v_mostra_est_pac = STRING_N;
//		}
//
//		return v_mostra_est_pac;
//	}
	
	public void validarCIDAtendimento(ParametrosProcedureVO parametrosProcedureVO,Integer codAtendimento,Integer seqPrescricao) throws ApplicationBusinessException{
		
		Long trgSeq = prescricaoMedicaFacade.buscarTriagemEstadoPaciente(codAtendimento, seqPrescricao);
		Long pRgtSeq = mampEmgGeraReg(trgSeq);
		parametrosProcedureVO.setRgtSeq(pRgtSeq);

		if(parametrosProcedureVO != null && STRING_S.equals(parametrosProcedureVO.getvMostraEstPac())){
			String v_falta_est_pac =  consultarPreenchimentoEstadoPaciente(codAtendimento, seqPrescricao, pRgtSeq, servidorLogadoFacade.obterServidorLogado().getUsuario());
			if(v_falta_est_pac.equals(STRING_N)){
				return;
			}
			//if(v_unid_cti.equals(STRING_N)){
				//TODO CALL DA ESTORIA 1378, A SER CONSTRUIDA NO SPRINT 102, COM PARAMETROS.
				/*
				•	Variavel v_trg_seq;
				•	Variavel v_rgt_seq –Procedure PC02;
				•	Código de atendimento do paciente que está no contexto;
				•	Código da prescrição médica que está no contexto;
				 */
//			} else 
//				if(parametrosProcedureVO != null && STRING_S.equals(parametrosProcedureVO.getvUnidCti())){
				//TODO CALL DA ESTORIA 1378, A SER CONSTRUIDA NO SPRINT 102, COM PARAMETROS.
				/*
				•	Código de atendimento do paciente que está no contexto;
				•	Variável v_rgt_seq;
				•	Código da prescrição médica que está no contexto;
				 */
//			}
		}	
	}

	/**
	 * #44179
	 * @ORADB MAMP_EMG_ATLZ_REG
	 * @author marcelo.deus
	 * @param pRgtSeq
	 * @param pIndSituacao
	 * @throws ApplicationBusinessException
	 */
	public void atualizaExtratoRegistro(Long pRgtSeq, String pIndSituacao) throws ApplicationBusinessException{
		
		Long vRgtSeqp;
		MamRegistro mamRegistro = mamRegistroDAO.obterPorChavePrimaria(pRgtSeq);
		mamRegistro.setIndSituacao(DominioSituacaoRegistro.valueOf(pIndSituacao));
		if(mamRegistroDAO.atualizar(mamRegistro) == null){
			throw new ApplicationBusinessException(PrescricaoMedicaAlergiaRNExceptionCode.MAM_03547);
		}
		vRgtSeqp = mamExtratoRegistroDAO.obterValorMaximoExtratoRegistro(pRgtSeq);
		MamExtratoRegistro mamExtratoRegistro = new MamExtratoRegistro();
		mamExtratoRegistro.setId(new MamExtratoRegistroId(pRgtSeq, vRgtSeqp.shortValue()));
		mamExtratoRegistro.setIndSituacao(DominioSituacaoRegistro.valueOf(pIndSituacao));
		mamExtratoRegistroDAO.persistir(mamExtratoRegistro);
		if(mamExtratoRegistro.getId().getSeqp() == null){
			throw new ApplicationBusinessException(PrescricaoMedicaAlergiaRNExceptionCode.MAM_03548, Severity.ERROR);
		}
	}
	
	/**
	 * #44179 - P02
	 * @ORADB MAMP_EMG_GERA_REG
	 * @author thiago.cortes
	 * @param pTrgSeq
	 * @throws ApplicationBusinessException 
	 */
	public Long mampEmgGeraReg(Long pTrgSeq) throws ApplicationBusinessException{
		Integer vAtdSeq;
		String vOrigem;
		Boolean vIndNoConsultorio;
		DominioTipoFormularioAlta vTipoFormularioAlta;
		Boolean vIndPedeMotivo;
		Integer vEspSeq = 0;
		Short vUnfSeq = 0;
		Short vSegSeqAtual;
//		String vIndGeraRegistro = StringUtils.EMPTY;
		String vIndReaproveitaRegistro = StringUtils.EMPTY;
		String vPodeRegistrarMoeEmg = StringUtils.EMPTY;
		String vAchouMoe = StringUtils.EMPTY;
	    MamRegistro mamRegistro = new MamRegistro();

	    vAtdSeq = mamcEmgGetAtdSeq(pTrgSeq);
		vOrigem = aghAtendimentoDAO.obterOrigemPorSeq(vAtdSeq);
		vSegSeqAtual = mamTriagensDAO.obterSegSeqPorSeq(pTrgSeq);
		
//		se o paciente ainda esta sendo atendido a nível ambulatorial
//		v_pode_registrar_moe_emg := mamk_perfil.mamc_executa_proc(NULL,NULL,mamk_processos.mamc_get_proc_moeemg);
		vPodeRegistrarMoeEmg = validarUsuarioExecutarProcesso(null, null, obterParametroPExigeEstadoPac().shortValue());
		if("A".equalsIgnoreCase(vOrigem)){
//			se o paciente está com encaminhamento interno (BA)
//			IF mamk_situacao_emerg.mamc_sit_tem_caract (v_seg_seq_atual, 'Enc Interno') = 'S'
			if(STRING_S.equalsIgnoreCase(mamcSitTemCaract(vSegSeqAtual,"Enc Interno"))){
//				vIndGeraRegistro = STRING_S;
				vIndReaproveitaRegistro = STRING_N;
				vIndNoConsultorio = Boolean.TRUE;
				vTipoFormularioAlta = DominioTipoFormularioAlta.S;
				if(STRING_S.equalsIgnoreCase(vPodeRegistrarMoeEmg)){
					vIndPedeMotivo = Boolean.TRUE;
				}
				else{
					vIndPedeMotivo = Boolean.FALSE;
				}
			}
//			se o paciente foi mandado de volta para a triagem
//			ELSIF mamk_situacao_emerg.mamc_sit_tem_caract (v_seg_seq_atual, 'Voltar Triagem') = 'S'
			else if(STRING_S.equalsIgnoreCase(mamcSitTemCaract(vSegSeqAtual,"Voltar Triagem"))){
//				vIndGeraRegistro = STRING_S;
				vIndReaproveitaRegistro = STRING_N;
				vIndNoConsultorio = Boolean.TRUE;
				vTipoFormularioAlta = DominioTipoFormularioAlta.S;
				if(STRING_S.equalsIgnoreCase(vPodeRegistrarMoeEmg)){
					vIndPedeMotivo = Boolean.TRUE;
				}else{
					vIndPedeMotivo = Boolean.FALSE;
				}
			}
//			   se o paciente está no consultório
//			   ELSIF mamk_situacao_emerg.mamc_sit_tem_caract (v_seg_seq_atual, 'No Consultorio') = 'S'
			else if(STRING_S.equalsIgnoreCase(mamcSitTemCaract(vSegSeqAtual,"No Consultorio"))){
				vIndNoConsultorio = Boolean.TRUE;
				vTipoFormularioAlta = DominioTipoFormularioAlta.S;
				
				mamRegistro.setSeq(mamRegistroDAO.obterSeqPorTrgSeq(pTrgSeq));
				if(mamRegistro.getSeq() > 0L){
//					vIndGeraRegistro = STRING_N;
					vIndReaproveitaRegistro = STRING_S;
				}
				else{
//					vIndGeraRegistro = STRING_S;
					vIndReaproveitaRegistro = STRING_N;
				}
				
				vAchouMoe = mamMotivoAtendimentoDAO.curMoe(pTrgSeq);
				if(StringUtils.EMPTY.equals(vAchouMoe)){
					vAchouMoe = STRING_N;
				}
				
				if((STRING_N.equalsIgnoreCase(vAchouMoe)) && (STRING_S.equalsIgnoreCase(vPodeRegistrarMoeEmg))){
					vIndPedeMotivo = Boolean.TRUE;
				}
				else{
					vIndPedeMotivo = Boolean.FALSE;
				}
			}
//			   se o paciente em atendimento (o paciente foi ingressado e depois estornado o ingresso)
//			   ELSIF mamk_situacao_emerg.mamc_sit_tem_caract (v_seg_seq_atual, 'Em Atend') = 'S'
			else if(STRING_S.equalsIgnoreCase(mamcSitTemCaract(vSegSeqAtual,"Em Atend"))){
//				vIndGeraRegistro = STRING_S;
				vIndReaproveitaRegistro = STRING_N;
				vIndNoConsultorio = Boolean.FALSE;
				vTipoFormularioAlta = DominioTipoFormularioAlta.C;
				
				vAchouMoe = mamMotivoAtendimentoDAO.curMoe(pTrgSeq);
				if(StringUtils.EMPTY.equals(vAchouMoe)){
					vAchouMoe = STRING_N;
				}
				if((STRING_N.equalsIgnoreCase(vAchouMoe)) && (STRING_S.equalsIgnoreCase(vPodeRegistrarMoeEmg))){
					vIndPedeMotivo = Boolean.TRUE;
				}
				else{
					vIndPedeMotivo = Boolean.FALSE;
				}
			}
			else{
//				vIndGeraRegistro = STRING_S;
				vIndReaproveitaRegistro = STRING_N;
				vIndNoConsultorio = Boolean.FALSE;
				vTipoFormularioAlta = DominioTipoFormularioAlta.C;
				vIndPedeMotivo = Boolean.FALSE;
			}
		}
//		se o paciente não esta mais sendo atendido a nível ambulatorial
		else{
//		   se o paciente em atendimento
//		   IF mamk_situacao_emerg.mamc_sit_tem_caract (v_seg_seq_atual, 'Em Atend') = 'S'
			if(STRING_S.equalsIgnoreCase(mamcSitTemCaract(vSegSeqAtual,"Em Atend"))){
//				vIndGeraRegistro = STRING_S;
				vIndReaproveitaRegistro = STRING_N;
				vIndNoConsultorio = Boolean.FALSE;
				vTipoFormularioAlta = DominioTipoFormularioAlta.C;
				
				vAchouMoe = mamMotivoAtendimentoDAO.curMoe(pTrgSeq);
				if(StringUtils.EMPTY.equals(vAchouMoe)){
					vAchouMoe = STRING_N;
				}
				if((STRING_N.equalsIgnoreCase(vAchouMoe)) && (STRING_S.equalsIgnoreCase(vPodeRegistrarMoeEmg))){
					vIndPedeMotivo = Boolean.TRUE;
				}
				else{
					vIndPedeMotivo = Boolean.FALSE;
				}
			}
			else{
//				vIndGeraRegistro = STRING_S;
				vIndReaproveitaRegistro = STRING_N;
				vIndNoConsultorio = Boolean.FALSE;
				vTipoFormularioAlta = DominioTipoFormularioAlta.C;
				vIndPedeMotivo = Boolean.FALSE;
			}
		}
		
//		faz a geração do registro conforme situação do paciente
		if(STRING_S.equalsIgnoreCase(vIndReaproveitaRegistro )){
//			   atualiza o registro atual para em elaboração
//			   mamk_emg_generica.mamp_emg_atlz_reg (v_rgt_seq_retorno, 'EE');
			atualizaExtratoRegistro(mamRegistro.getSeq(), "EE");
		}
		else{
//			gera um novo registro com siutacao igual a em elaboração
//		    Integer nextVal = mamRegistroDAO.getNextVal(SequenceID.MAM_RGT_SQ1); 
//		    vRgtSeqRetorno = vRgtSeqRetorno.parseLong(String.valueOf(nextVal)); 
		    vEspSeq	= mamcEmgGetEsp(pTrgSeq);
		    vUnfSeq = mamcEmgGetUnf(pTrgSeq);
		    
		    preInsertRegistros(pTrgSeq, mamRegistro, vAtdSeq, vIndNoConsultorio, vTipoFormularioAlta, vIndPedeMotivo, vEspSeq, vUnfSeq);
		    mamRegistroDAO.persistir(mamRegistro);
		    MamExtratoRegistro mamExtratoRegistro = new MamExtratoRegistro();
		    preInsertExtratoRegistro(mamExtratoRegistro, mamRegistro.getSeq());
		    mamExtratoRegistroDAO.persistir(mamExtratoRegistro);
		}
		return mamRegistro.getSeq();
	}

	private void preInsertExtratoRegistro(MamExtratoRegistro mamExtratoRegistro, Long vRgtSeqRetorno) throws ApplicationBusinessException {
		if(mamExtratoRegistro == null){
			throw new ApplicationBusinessException(PrescricaoMedicaAlergiaRNExceptionCode.MAM_03546);
		}
		else{
			MamExtratoRegistroId id = new MamExtratoRegistroId();
			id.setRgtSeq(vRgtSeqRetorno);
			id.setSeqp(Short.valueOf("1"));
			mamExtratoRegistro.setId(id);
			mamExtratoRegistro.setIndSituacao(DominioSituacaoRegistro.EE);
			mamExtratoRegistro.setCriadoEm(new Date());
			mamExtratoRegistro.setServidor(servidorLogadoFacade.obterServidorLogadoSemCache());
		}
	}

	private void preInsertRegistros(Long pTrgSeq, MamRegistro mamRegistro, Integer vAtdSeq, Boolean vIndNoConsultorio,
			DominioTipoFormularioAlta vTipoFormularioAlta, Boolean vIndPedeMotivo, Integer vEspSeq, Short vUnfSeq) throws ApplicationBusinessException {
		
		MamTriagens triagem = null; 
		AghEspecialidades especialidade = null;
		AghAtendimentos AghAtendimentos = null;
		AghUnidadesFuncionais unidadeFuncional = null; 
		
		if(pTrgSeq != null){
			triagem = mamTriagensDAO.obterPorChavePrimaria(pTrgSeq);
		}
		
		if(vEspSeq != null){
			especialidade = aghEspecialidadesDAO.obterPorChavePrimaria(vEspSeq);
		}
		
		if(vAtdSeq != null){
			AghAtendimentos = aghAtendimentoDAO.obterPorChavePrimaria(vAtdSeq);
		}

		if(vUnfSeq != null){
			unidadeFuncional = unidadeFuncionalDAO.obterPorChavePrimaria(vUnfSeq);
		}
		
	
		if(triagem == null){
			mamRegistro.setTriagem(null);	
		} else {
			mamRegistro.setTriagem(triagem);
		}
		
		if(especialidade == null){
			mamRegistro.setEspecialidade(null);	
		} else {
			mamRegistro.setEspecialidade(especialidade);
		}
		
		if(unidadeFuncional == null){
			mamRegistro.setUnidadeFuncional(null);
		} else {
			mamRegistro.setUnidadeFuncional(unidadeFuncional);
		}
		if(AghAtendimentos == null){
			mamRegistro.setAtendimento(null);
		} else {
			mamRegistro.setAtendimento(AghAtendimentos);
		}
		mamRegistro.setIndNoConsultorio(vIndNoConsultorio);
		mamRegistro.setIndSituacao(DominioSituacaoRegistro.EE);
		mamRegistro.setTipoFormularioAlta(vTipoFormularioAlta);
		mamRegistro.setIndPedeMotivo(vIndPedeMotivo);
		mamRegistro.setCriadoEm(new Date());
		mamRegistro.setServidor(servidorLogadoFacade.obterServidorLogadoSemCache());

	}
	
	/**
	 * #44179 
	 * @ORADB MAMC_SIT_TEM_CARACT
	 * @author thiago.cortes
	 * @param segSeq, caracteristica
	 * @return 'S'
	 */
	public String mamcSitTemCaract(Short segSeq,String caracteristica){
		String vExiste = StringUtils.EMPTY;
		if(segSeq != null && caracteristica != null){
			vExiste = mamCaractSitEmergDAO.cCsi(segSeq,caracteristica);
		}
		if(StringUtils.EMPTY.equals(vExiste)){
			vExiste = STRING_N;
		}
		return vExiste;
	}
	
	/**
	 * #44179
	 * @ORADB FUNCTION MAMC_EMG_GET_ATD_SEQ
	 * @author thiago.cortes
	 * @param pTrgSeq
	 * @return
	 */
	public Integer mamcEmgGetAtdSeq(Long pTrgSeq){
		CurTeiVO curTei = null;
		if(pTrgSeq != null){
			curTei = aghAtendimentoDAO.curTei(pTrgSeq);
		}
		//Short vSeqP;
		//Integer vConNumero;
		Integer vAtdSeq = null;
		if(curTei != null){
			if(curTei.getAtdSeq() != null){
				vAtdSeq = curTei.getAtdSeq();
			}
//			if(curTei.getSeqP() != null){
//				vSeqP = curTei.getSeqP(); 
//			}
//			if(curTei.getConNumero() != null){
//				vConNumero = curTei.getConNumero();
//			}
		}
		return vAtdSeq;
	}
	
	/**#44179
	 * @ORADB FUNCTION MAMC_EMG_GET_ESP
	 * @author thiago.cortes
	 * @param pTrgSeq
	 * @return espSeq
	 */
	public Integer mamcEmgGetEsp(Long pTrgSeq){
		CurTeiVO curTei= null;
		if(pTrgSeq != null){
			curTei = aacGradeAgendamenConsultasDAO.obterEspPorTriagem(pTrgSeq);
		}
//		Short vTeiSeqP;
//		Integer vConNumero;
		Integer vEspSeq = null;
		if(curTei != null){
			if(curTei.getAtdSeq() != null){
				vEspSeq = curTei.getEspSeq();
			}
//			if(curTei.getSeqP() != null){
//				vTeiSeqP = curTei.getSeqP(); 
//			}
//			if(curTei.getConNumero() != null){
//				vConNumero = curTei.getConNumero();
//			}
		}
		return vEspSeq;
	}
	
	/**#44179
	 * @ORADB FUNCTION MAMC_EMG_GET_UNF
	 * @author thiago.cortes
	 * @param pTrgSeq
	 * @return unfSeq
	 */
	public Short mamcEmgGetUnf(Long pTrgSeq){
		CurTeiVO curTei=null;
		if(pTrgSeq != null){
			curTei = aghAtendimentoDAO.obterUnidadePorTriagem(pTrgSeq);
		}
//		Short vTeiSeqP;
//		Integer vConNumero;
		Short vUnfSeq = null;
		if(curTei != null){
			if(curTei.getAtdSeq() != null){
				vUnfSeq = curTei.getUnfSeq();
			}
//			if(curTei.getSeqP() != null){
//				vTeiSeqP = curTei.getSeqP(); 
//			}
//			if(curTei.getConNumero() != null){
//				vConNumero = curTei.getConNumero();
//			}
		}
		return vUnfSeq;
	}
	
    /**
     *  #44179
     * @ORADB MAMC_EXECUTA_PROC
     * @author thiago.cortes
     * @param pSerVinCodigo, pSerMatricula, pProcSeq
     * @return String
     */
    private String validarUsuarioExecutarProcesso(Short pSerVinCodigo, Integer pSerMatricula, Short pProcSeq){
       boolean vExecute = false;
       Short vSerVinCodigo = null;
       Integer vSerMatricula = null;
       
       if(pSerVinCodigo == null || pSerMatricula == null){
             RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
             if(servidorLogado != null){
            	 vSerVinCodigo = servidorLogado.getId().getVinCodigo();
            	 vSerMatricula = servidorLogado.getId().getMatricula(); 
             }
       }else{
             vSerVinCodigo = pSerVinCodigo;
             vSerMatricula = pSerMatricula;
       }
       
       if(vSerVinCodigo != null && vSerMatricula != null){
    	   vExecute = cseProcessosDAO.validarPermissaoPorServidorESeqProcesso(servidorLogadoFacade.obterServidorLogadoSemCache().getUsuario(), pProcSeq);
       }
       if(vExecute){
             return STRING_S;
       }
       return STRING_N;
    }

    private BigDecimal obterParametroPExigeEstadoPac(){
       BigDecimal vVlrNumero;
       AghParametros pExigeEstadoPac = parametroFacade.obterAghParametroPorNome(P_SEQ_PROC_MOE_EMG);
       if(pExigeEstadoPac == null){
             vVlrNumero = BigDecimal.ZERO;
       } else {
             vVlrNumero = pExigeEstadoPac.getVlrNumerico();
       }
             return vVlrNumero;
    }
}