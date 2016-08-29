package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioControleSumarioAltaPendente;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelItemPedidoExame;
import br.gov.mec.aghu.model.AelPedidoExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmAltaEstadoPaciente;
import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmObitoNecropsia;
import br.gov.mec.aghu.model.MpmObtCausaAntecedente;
import br.gov.mec.aghu.model.MpmObtCausaDireta;
import br.gov.mec.aghu.model.MpmObtOutraCausa;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaMotivoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoAltaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObitoNecropsiaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaAntecedenteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaDiretaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtOutraCausaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPlanoPosAltaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSumarioAltaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class SumarioAltaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(SumarioAltaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmMotivoAltaMedicaDAO mpmMotivoAltaMedicaDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private MpmSumarioAltaDAO mpmSumarioAltaDAO;
	
	@Inject
	private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;
	
	@Inject
	private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;
	
	@Inject
	private MpmPlanoPosAltaDAO mpmPlanoPosAltaDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;
	
	@Inject
	private MpmAltaMotivoDAO mpmAltaMotivoDAO;

	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmAltaEstadoPacienteDAO mpmAltaEstadoPacienteDAO;
	
	@EJB
	private ManterAltaEstadoPacienteRN manterAltaEstadoPacienteRN;
	
	@EJB
	private ManterObtCausaDiretaRN manterObtCausaDiretaRN;
	
	@Inject
	private MpmObtCausaDiretaDAO mpmObtCausaDiretaDAO;
	
	@Inject
	private MpmObtCausaAntecedenteDAO  mpmObtCausaAntecedenteDAO;
	
	@EJB
	private ManterObtCausaAntecedenteRN manterObtCausaAntecedenteRN;
	
	@Inject
	private MpmObtOutraCausaDAO  mpmObtOutraCausaDAO;
	
	@EJB
	private ManterObtOutraCausaRN manterObtOutraCausaRN;
	
	@Inject
	private MpmObitoNecropsiaDAO mpmObitoNecropsiaDAO;
	
	@EJB
	private ManterObitoNecropsiaRN manterObitoNecropsiaRN;
	
	@EJB
	private ManterPrescricaoDietaRN manterPrescricaoDietaRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8364533729879694817L;

	private enum SumarioAltaExceptionCode implements
	BusinessExceptionCode {
	MPM_00382, MPM_00946, MPM_00383, MPM_00951, MPM_00957, MPM_00950, MPM_00960,  
	MPM_01102, MPM_02404, RAP_00175, MPM_01767, MPM_02246, MPM_02247, MPM_01721, MPM_01761,
	MPM_XXX, ERRO_GENERICO;
	}

	
	/**
	 * ORADB Trigger MPMT_SAL_BRU (operacao UPD)
	 * 
	 */
	@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.NPathComplexity"})
	public void preAtualizar(MpmSumarioAlta sumarioAlta, String nomeMicrocomputador, final Date dataFimVinculoServidor)
	throws BaseException, Exception {
		
		sumarioAlta.setAlteradoEm(new Date());

		SumarioAltaVO sumarioAltaVO = (SumarioAltaVO)getMpmSumarioAltaDAO().obtemSumarioAltaVO(sumarioAlta.getAtdSeq());
		
		if(CoreUtil.modificados(sumarioAlta.getPlaSeq(), sumarioAltaVO.getPlaSeq())) {
			this.verPlanoSumarioAlta(sumarioAlta.getPlaSeq());
		}

		if(CoreUtil.modificados((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAltaVO.getMamSeq())) {
			this.verMotivoAltaMedicaSumarioAlta((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null);
		}

		if(CoreUtil.modificados((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAltaVO.getMamSeq()) ||
				CoreUtil.modificados(sumarioAlta.getComplMotivoAlta(), sumarioAltaVO.getComplMotivoAlta())) {
			this.verMotivoAltaMedicaComplementoSumarioAlta((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAlta.getComplMotivoAlta());
		}

		if(CoreUtil.modificados((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAltaVO.getMamSeq()) ||
				CoreUtil.modificados(sumarioAlta.getIndNecropsia(), sumarioAltaVO.getIndNecropsia())) {
			this.verNecropsiaSumarioAlta((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAlta.getIndNecropsia());
		}

		if(CoreUtil.modificados(sumarioAlta.getPlaSeq(), sumarioAltaVO.getPlaSeq()) ||
				CoreUtil.modificados(sumarioAlta.getComplPlanoPosAlta(), sumarioAltaVO.getComplPlanoPosAlta())) {
			this.verMotivoAltaMedicaComplementoPosSumarioAlta(sumarioAlta.getPlaSeq(), sumarioAlta.getComplPlanoPosAlta());
		}

		if(CoreUtil.modificados((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAltaVO.getMamSeq()) ||
				CoreUtil.modificados(sumarioAlta.getEstadoPacienteAlta(), sumarioAltaVO.getEstadoPacienteAlta())) {
			this.verExigeEstadoPacienteSumarioAlta((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAlta.getEstadoPacienteAlta());
		}

		if(CoreUtil.modificados((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAltaVO.getMamSeq()) &&
				sumarioAlta.getMotivoAltaMedica()!=null) {
			this.atualizarServidorValida(sumarioAlta);
		}

		if (CoreUtil.modificados((sumarioAlta.getMotivoAltaMedica() != null) ? sumarioAlta.getMotivoAltaMedica().getSeq() : null,
				sumarioAltaVO.getMamSeq()) && sumarioAlta.getMotivoAltaMedica() == null) {
			this.atualizarPrescricaoSumarioAlta(sumarioAlta, nomeMicrocomputador);
		}

		/* Verifica data/hora  alta  */
		if (CoreUtil.modificados(sumarioAlta.getDthrAlta(), sumarioAltaVO.getDthrAlta()) && sumarioAlta.getDthrAlta() != null) {
			this.verDataAlta(sumarioAlta);
			this.verAltaFutura(sumarioAlta);
		}
		
		if(CoreUtil.modificados((sumarioAlta.getMotivoAltaMedica()!=null)?sumarioAlta.getMotivoAltaMedica().getSeq():null, sumarioAltaVO.getMamSeq()) ||
			(DateUtil.isDatasIguais(sumarioAltaVO.getDthrAlta(),sumarioAlta.getDthrAlta()))) {
			this.atualizarAtendimento(sumarioAlta, sumarioAltaVO, nomeMicrocomputador, dataFimVinculoServidor);
		}
		
	}

	
	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_ATU_ATEND
	 * 
	 */
	public void atualizarAtendimento(MpmSumarioAlta sumarioAlta, SumarioAltaVO sumarioAltaVO, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		if((sumarioAlta.getDthrAlta() != null && sumarioAltaVO.getDthrAlta() == null) &&
			(sumarioAlta.getMotivoAltaMedica() != null && sumarioAltaVO.getMamSeq() == null)) {
			AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(sumarioAlta.getAghAtendimento().getSeq());
			AghAtendimentos atendimentoOld = getPacienteFacade().clonarAtendimento(atendimento);
			atendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
			atendimento.setCtrlSumrAltaPendente(DominioControleSumarioAltaPendente.E);
			getPacienteFacade().persistirAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}
	



	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_VER_ALTA_FUT
	 * 
	 */
	public void verAltaFutura(MpmSumarioAlta sumarioAlta) throws BaseException {
		/* Alta futura só p/paciente internado em unid. funcional com
		limite dias de adiantamento  > 0
		*/	
		if(sumarioAlta.getAghAtendimento() != null && sumarioAlta.getAghAtendimento().getUnidadeFuncional() != null) {
			if(sumarioAlta.getAghAtendimento().getUnidadeFuncional().getNroUnidTempoPenAdiantadas() > 0) {
				if(DominioUnidTempo.D.equals(sumarioAlta.getAghAtendimento().getUnidadeFuncional().getIndUnidTempoPenAdiantada())) {
					if((DateUtil.diffInDaysInteger(DateUtil.truncaData(sumarioAlta.getDthrAlta()), DateUtil.truncaData(new Date())) + 1) > sumarioAlta.getAghAtendimento().getUnidadeFuncional().getNroUnidTempoPenAdiantadas()) {
						throw new ApplicationBusinessException(
								SumarioAltaExceptionCode.MPM_XXX, sumarioAlta.getAghAtendimento().getUnidadeFuncional().getNroUnidTempoPenAdiantadas());											
					}
				} else if (DominioUnidTempo.H.equals(sumarioAlta.getAghAtendimento().getUnidadeFuncional()
						.getIndUnidTempoPenAdiantada())
						&& ((DateUtil.diffInDaysInteger(DateUtil.truncaData(sumarioAlta.getDthrAlta()),
								DateUtil.truncaData(new Date())) * 24) > sumarioAlta.getAghAtendimento().getUnidadeFuncional()
								.getNroUnidTempoPenAdiantadas())) {
					throw new ApplicationBusinessException(SumarioAltaExceptionCode.MPM_XXX, sumarioAlta.getAghAtendimento()
							.getUnidadeFuncional().getNroUnidTempoPenAdiantadas());
				}
			}
			if((sumarioAlta.getAghAtendimento().getUnidadeFuncional().getNroUnidTempoPenAdiantadas() == null || sumarioAlta.getAghAtendimento().getUnidadeFuncional().getNroUnidTempoPenAdiantadas() == 0)
					&& (DateUtil.validaDataMaior(sumarioAlta.getDthrAlta(), new Date()))) {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_01761);									
			}
		}		
	}

	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_VER_DT_ALTA
	 * 
	 */
	public void verDataAlta(MpmSumarioAlta sumarioAlta) throws BaseException {
		if(sumarioAlta.getAghAtendimento() != null) {
			if(DominioPacAtendimento.N.equals(sumarioAlta.getAghAtendimento().getIndPacAtendimento())) {
				if(DateValidator.validaDataMenor(sumarioAlta.getDthrAlta(), DateUtil.adicionaDias(sumarioAlta.getAghAtendimento().getDthrFim(), 1))) {
					throw new ApplicationBusinessException(
							SumarioAltaExceptionCode.MPM_02246);					
				}
				else if(DateUtil.validaDataMaior(sumarioAlta.getDthrAlta(), sumarioAlta.getAghAtendimento().getDthrFim())){
					throw new ApplicationBusinessException(
							SumarioAltaExceptionCode.MPM_02247);										
				}
			}
			else if(DateValidator.validaDataMenor(DateUtil.truncaData(sumarioAlta.getDthrAlta()), DateUtil.truncaData(new Date()))) {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_01721);
			}
		}
	}

	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_VER_PLA
	 * 
	 */
	public void verPlanoSumarioAlta(Short seq) throws BaseException {
		if(seq != null) {
			MpmPlanoPosAlta planoPosAlta = getMpmPlanoPosAltaDAO().obterPlanoPosAltaPeloId(Integer.valueOf(seq));
			if(planoPosAlta == null) {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00382);
			}
			else if(!DominioSituacao.A.equals(planoPosAlta.getIndSituacao())){
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00946);				
			}
		}
	}
	
	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_VER_MTV_ALTM
	 * 
	 */
	public void verMotivoAltaMedicaSumarioAlta(Short seq) throws BaseException {
		if(seq != null) {
			MpmMotivoAltaMedica motivoAlta = getMpmMotivoAltaMedicaDAO().obterMotivoAltaMedicaPeloId(seq);
			if(motivoAlta == null) {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00383);
			}
			else if(!DominioSituacao.A.equals(motivoAlta.getIndSituacao())){
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00951);				
			}
		}
	}

	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_VER_COMPL
	 * 
	 */
	public void verMotivoAltaMedicaComplementoSumarioAlta(Short seq, String complementoMotivoAlta) throws BaseException {
		if(seq != null && complementoMotivoAlta == null) {
			MpmMotivoAltaMedica motivoAlta = getMpmMotivoAltaMedicaDAO().obterMotivoAltaMedicaPeloId(seq);
			if(motivoAlta == null) {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00383);
			}
			else if(motivoAlta.getIndExigeComplemento()){
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00957);				
			}
		}
	}

	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_VER_NECROPS
	 * 
	 */
	public void verNecropsiaSumarioAlta(Short seq, String indNecropsia) throws BaseException {
		if("S".equals(indNecropsia)) {
			if(seq != null) {
				MpmMotivoAltaMedica motivoAlta = getMpmMotivoAltaMedicaDAO().obterMotivoAltaMedicaPeloId(seq);
				if(motivoAlta == null) {
					throw new ApplicationBusinessException(
							SumarioAltaExceptionCode.MPM_00383);
				}
				else if(!motivoAlta.getIndObito()){
					throw new ApplicationBusinessException(
							SumarioAltaExceptionCode.MPM_00950);				
				}
			}
			else {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00950);								
			}
		}
	}

	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_VER_COMPL_PL
	 * 
	 */
	public void verMotivoAltaMedicaComplementoPosSumarioAlta(Short seq, String complementoPlanPosAlta) throws BaseException {
		if(seq != null && complementoPlanPosAlta == null) {
			MpmMotivoAltaMedica motivoAlta = getMpmMotivoAltaMedicaDAO().obterMotivoAltaMedicaPeloId(seq);
			if(motivoAlta == null) {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00382);
			}
			else if(motivoAlta.getIndExigeComplemento()){
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00960);				
			}
		}
	}

	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_VER_EXIG_EST
	 * 
	 */
	public void verExigeEstadoPacienteSumarioAlta(Short seq, String estadoPacienteAlta) throws BaseException {
		if(seq != null) {
			MpmMotivoAltaMedica motivoAlta = getMpmMotivoAltaMedicaDAO().obterMotivoAltaMedicaPeloId(seq);
			if(motivoAlta == null) {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.MPM_00383);
			}
			else {
				if(motivoAlta.getIndExigeEstadoPaciente() && StringUtils.isEmpty(estadoPacienteAlta)) {
					throw new ApplicationBusinessException(
							SumarioAltaExceptionCode.MPM_01102);				
				}
				if(!motivoAlta.getIndExigeEstadoPaciente() && !StringUtils.isEmpty(estadoPacienteAlta)) {
					throw new ApplicationBusinessException(
							SumarioAltaExceptionCode.MPM_02404);				
				}
			}			
		}
	}

	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_ATU_SERV_VAL
	 * 
	 */
	public void atualizarServidorValida(MpmSumarioAlta sumarioAlta) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(sumarioAlta.getMotivoAltaMedica() != null) {
			if(servidorLogado == null) {
				throw new ApplicationBusinessException(
						SumarioAltaExceptionCode.RAP_00175);
			}
			else {
				sumarioAlta.setServidorValido(servidorLogado);
				sumarioAlta.setDthrElaboracaoAlta(new Date());
			}			
		} else {
			sumarioAlta.setServidorValido(null);
			sumarioAlta.setDthrElaboracaoAlta(null);
		}
	}

	/**
	 * ORADB Trigger MPMK_SAL_RN.RN_SALP_ATU_PRESCR
	 * 
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void atualizarPrescricaoSumarioAlta(MpmSumarioAlta sumarioAlta, String nomeMicrocomputador) throws BaseException, Exception {
		sumarioAlta.setServidorValido(null);
		sumarioAlta.setDthrElaboracaoAlta(null);

		List<MpmPrescricaoMdto> medicamentosOriginal = getMpmPrescricaoMdtoDAO().buscarPrescricaoMedicamentosSemItemRecomendaAltaPeloAtendimento(sumarioAlta.getAtdSeq());
		List<MpmPrescricaoMdto> medicamentos = getMpmPrescricaoMdtoDAO().buscarPrescricaoMedicamentosSemItemRecomendaAltaPeloAtendimento(sumarioAlta.getAtdSeq());
		for(int i =0; i< medicamentos.size();i++){//for(MpmPrescricaoMdto medicamento : medicamentos) {
			MpmPrescricaoMdto medicamentoOriginal = medicamentosOriginal.get(i);
			getMpmPrescricaoMdtoDAO().desatachar(medicamentoOriginal);
			MpmPrescricaoMdto medicamento = medicamentos.get(i);
			
			medicamento.setIndItemRecomendadoAlta(false);
			getPrescricaoMedicaFacade().persistirPrescricaoMedicamento(medicamento, nomeMicrocomputador,medicamentoOriginal);
		}
		
		List<MpmPrescricaoDieta> dietas = getMpmPrescricaoDietaDAO().buscarPrescricaoDietasSemItemRecomendaAltaPeloAtendimento(sumarioAlta.getAtdSeq());
		for(MpmPrescricaoDieta dieta : dietas) {
			dieta.setIndItemRecomendadoAlta(false);
			getPrescricaoMedicaFacade().gravar(dieta, null, null, null, nomeMicrocomputador);
		}

		List<MpmPrescricaoCuidado> cuidados = getMpmPrescricaoCuidadoDAO().buscarPrescricaoCuidadosSemItemRecomendaAltaPeloAtendimento(sumarioAlta.getAtdSeq());
		final Date dataFimVinculoServidor = new Date();
		for(MpmPrescricaoCuidado cuidado : cuidados) {
			cuidado.setIndItemRecomendadoAlta(false);
			getPrescricaoMedicaFacade().alterarPrescricaoCuidado(cuidado, nomeMicrocomputador, dataFimVinculoServidor);
		}
		
		for(AelPedidoExame pedidoExame : getExamesFacade().buscarPedidosExamePeloAtendimento(sumarioAlta.getAtdSeq())) {
			for(AelItemPedidoExame item : pedidoExame.getAelItensPedidosExameses()) {
				getExamesFacade().removerAelItemPedidoExame(item, true);
			}
			getExamesFacade().removerAelPedidoExame(pedidoExame, true);
		}
	}

	/**
	 * ORADB TRIGGER MPMT_SAL_ARU
	 * 
	 */
	public void executarAposAtualizarSumarioAlta(MpmSumarioAlta sumarioAlta) throws ApplicationBusinessException{
		
		SumarioAltaVO sumarioAltaVO = (SumarioAltaVO)getMpmSumarioAltaDAO().obtemSumarioAltaVO(sumarioAlta.getAtdSeq());

		
		if(sumarioAlta.getMotivoAltaMedica() != null && CoreUtil.modificados(sumarioAlta.getMotivoAltaMedica().getSeq(), sumarioAltaVO.getMamSeq())){
			verificarCidAtendimentoDoSumarioAlta(sumarioAlta.getMotivoAltaMedica().getSeq());
		}
		
	}

	/**
	 * ORADB MPMK_SAL_RN RN_SALP_VER_CID_ATEN
	 */
	public void verificarCidAtendimentoDoSumarioAlta(Short newAtdSeq) throws ApplicationBusinessException{
		 List<MpmCidAtendimento> listaMpmCidAtendimentos = null;
		MpmCidAtendimentoDAO mpmCidAtendimentoDAO = getMpmCidAtendimentoDAO();
		
		listaMpmCidAtendimentos = mpmCidAtendimentoDAO.listarCidAtendimentosPorSeqAtendimento(Integer.valueOf(newAtdSeq));
		if(listaMpmCidAtendimentos == null || listaMpmCidAtendimentos.isEmpty()){
			throw new ApplicationBusinessException(SumarioAltaExceptionCode.MPM_01767);
		}
		
	}
	/**
	 * #39018  #39019 #39020 #39021 #39022 #39023 #39014 #39015 #39016
	 * Serviço que desbloqueia sumario alta
	 * @param atdSeq
	 * @param apaSeq
	 * @param seqp
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	public void desbloquearSumarioAlta(Integer atdSeq, Integer apaSeq, Short seqp, String nomeMicrocomputador) throws ApplicationBusinessException{
		getPrescricaoMedicaFacade().atualizarAltaSumarioEstorno(seqp, atdSeq, apaSeq, nomeMicrocomputador);
		removerInformacoesDesbloqueioSumarioAlta(atdSeq, apaSeq, seqp);
		atualizarInformacoesDesbloqueioSumarioAlta(atdSeq, nomeMicrocomputador);
	}


	private void atualizarInformacoesDesbloqueioSumarioAlta(Integer atdSeq,
			String nomeMicrocomputador) {
		List<MpmPrescricaoDieta> listaPrescricaoDieta = new LinkedList<MpmPrescricaoDieta>();
		listaPrescricaoDieta = getMpmPrescricaoDietaDAO().obterPrescricaoDietaPorAtendimentoRecomendaAlta(atdSeq);
		for (MpmPrescricaoDieta mpmPrescricaoDieta : listaPrescricaoDieta) {
			mpmPrescricaoDieta.setIndItemRecomendadoAlta(Boolean.TRUE);
			try {
				getManterPrescricaoDietaRN().atualizarPrescricaoDieta(mpmPrescricaoDieta, nomeMicrocomputador);
			} catch (BaseException e) {
				new ApplicationBusinessException(e);
			}
		}
		List<MpmPrescricaoCuidado> cuidados = getMpmPrescricaoCuidadoDAO().buscarPrescricaoCuidadosSemItemRecomendaAltaPeloAtendimento(atdSeq);
		final Date dataFimVinculoServidor = new Date();
		for(MpmPrescricaoCuidado cuidado : cuidados) {
			cuidado.setIndItemRecomendadoAlta(false);
			try {
				getPrescricaoMedicaFacade().alterarPrescricaoCuidado(cuidado, nomeMicrocomputador, dataFimVinculoServidor);
			} catch (BaseException e) {
				new ApplicationBusinessException(e);
			}
		}
		
		List<MpmPrescricaoMdto> medicamentosOriginal = getMpmPrescricaoMdtoDAO().buscarPrescricaoMedicamentosSemItemRecomendaAltaPeloAtendimento(atdSeq);
		List<MpmPrescricaoMdto> medicamentos = getMpmPrescricaoMdtoDAO().buscarPrescricaoMedicamentosSemItemRecomendaAltaPeloAtendimento(atdSeq);
		for(int i =0; i< medicamentos.size();i++){//for(MpmPrescricaoMdto medicamento : medicamentos) {
			MpmPrescricaoMdto medicamentoOriginal = medicamentosOriginal.get(i);
			getMpmPrescricaoMdtoDAO().desatachar(medicamentoOriginal);
			MpmPrescricaoMdto medicamento = medicamentos.get(i);
			
			medicamento.setIndItemRecomendadoAlta(false);
			try {
				getPrescricaoMedicaFacade().persistirPrescricaoMedicamento(medicamento, nomeMicrocomputador,medicamentoOriginal);
			} catch (BaseException e) {
				new ApplicationBusinessException(e);
			}
		}
	}


	private void removerInformacoesDesbloqueioSumarioAlta(Integer atdSeq,
			Integer apaSeq, Short seqp) throws ApplicationBusinessException {
		MpmAltaMotivo altaMotivo = this.getMpmAltaMotivoDAO().obterMpmAltaMotivoPorId(atdSeq, apaSeq, seqp);

		if (altaMotivo != null) {
			try {
				getPrescricaoMedicaFacade().removerAltaMotivo(altaMotivo);
			} catch (BaseException e) {
				new ApplicationBusinessException(e);
			}
		}
		
		MpmAltaEstadoPaciente altaEstadoPaciente = this.getMpmAltaEstadoPacienteDAO().obterMpmAltaEstadoPaciente(atdSeq, apaSeq, seqp);
		if (altaEstadoPaciente != null ) {
			
			altaEstadoPaciente.getAltaSumario();
			if(altaEstadoPaciente.getAltaSumario() != null){
				altaEstadoPaciente.getAltaSumario().setAltaEstadoPaciente(null);
			}
			getManterAltaEstadoPacienteRN().removerAltaEstadoPaciente(altaEstadoPaciente);
		}
		
		MpmObtCausaDireta obtCausaDireta = this.getMpmObtCausaDiretaDAO().obterObtCausaDireta(atdSeq, apaSeq, seqp);
		if (obtCausaDireta != null ) {
			getManterObtCausaDiretaRN().removerObtCausaDireta(obtCausaDireta);
		}
		
		List<MpmObtCausaAntecedente> listaObtCausaAntecedente =  new LinkedList<MpmObtCausaAntecedente>();		
		listaObtCausaAntecedente = this.getMpmObtCausaAntecedenteDAO().obterMpmObtCausaAntecedente(atdSeq, apaSeq, seqp);
		for (MpmObtCausaAntecedente obtCausaAntecedente : listaObtCausaAntecedente) {
			this.getManterObtCausaAntecedenteRN().removerObtCausaAntecedente(obtCausaAntecedente);
		}
		List<MpmObtOutraCausa> listaObtOutraCausa =  new LinkedList<MpmObtOutraCausa>();		
		listaObtOutraCausa  = this.getMpmObtOutraCausaDAO().obterMpmObtOutraCausa(atdSeq, apaSeq, seqp);
		for (MpmObtOutraCausa obtOutraCausa : listaObtOutraCausa) {
			this.getManterObtOutraCausaRN().removerObtOutraCausa(obtOutraCausa);
		}
		
		MpmObitoNecropsia mpmObitoNecropsia = this.getMpmObitoNecropsiaDAO().obterMpmObitoNecropsia(atdSeq, apaSeq, seqp);
		if (mpmObitoNecropsia != null) {
			this.getManterObitoNecropsiaRN().removerObitoNecropsia(mpmObitoNecropsia);
		}
	}
	
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected MpmPrescricaoDietaDAO getMpmPrescricaoDietaDAO(){
		return mpmPrescricaoDietaDAO;
	}

	protected MpmPrescricaoCuidadoDAO getMpmPrescricaoCuidadoDAO(){
		return mpmPrescricaoCuidadoDAO;
	}

	protected MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO(){
		return mpmPrescricaoMdtoDAO;
	}

	protected MpmCidAtendimentoDAO getMpmCidAtendimentoDAO(){
		return mpmCidAtendimentoDAO;
	}

	protected MpmSumarioAltaDAO getMpmSumarioAltaDAO(){
		return mpmSumarioAltaDAO;
	}

	protected MpmPlanoPosAltaDAO getMpmPlanoPosAltaDAO(){
		return mpmPlanoPosAltaDAO;
	}

	protected MpmMotivoAltaMedicaDAO getMpmMotivoAltaMedicaDAO(){
		return mpmMotivoAltaMedicaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected ManterPrescricaoDietaRN getManterPrescricaoDietaRN() {
		return this.manterPrescricaoDietaRN;
		
	}
	
	protected ManterAltaEstadoPacienteRN getManterAltaEstadoPacienteRN(){
		return this.manterAltaEstadoPacienteRN;
	}
	
	protected ManterObtCausaDiretaRN getManterObtCausaDiretaRN(){
		return this.manterObtCausaDiretaRN;
	}
	
	
	protected MpmAltaMotivoDAO getMpmAltaMotivoDAO(){
		return this.mpmAltaMotivoDAO;
		
	}
	
	protected MpmAltaEstadoPacienteDAO getMpmAltaEstadoPacienteDAO(){
		return this.mpmAltaEstadoPacienteDAO;
	}
	
	protected MpmObtCausaDiretaDAO getMpmObtCausaDiretaDAO(){
		return this.mpmObtCausaDiretaDAO;
	}
	
	
	protected MpmObtCausaAntecedenteDAO getMpmObtCausaAntecedenteDAO(){
		return this.mpmObtCausaAntecedenteDAO;
	}
	
	
	protected ManterObtCausaAntecedenteRN getManterObtCausaAntecedenteRN(){
		return this.manterObtCausaAntecedenteRN;
	}
	protected MpmObtOutraCausaDAO getMpmObtOutraCausaDAO(){
		return this.mpmObtOutraCausaDAO;
	}
	protected ManterObtOutraCausaRN getManterObtOutraCausaRN(){
		return this.manterObtOutraCausaRN;
	}
	
	protected MpmObitoNecropsiaDAO getMpmObitoNecropsiaDAO(){
		return this.mpmObitoNecropsiaDAO;
	}
	
	protected ManterObitoNecropsiaRN getManterObitoNecropsiaRN(){
		return this.manterObitoNecropsiaRN;
	}
}
