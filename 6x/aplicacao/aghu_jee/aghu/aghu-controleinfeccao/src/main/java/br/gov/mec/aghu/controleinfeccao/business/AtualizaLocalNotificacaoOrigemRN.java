package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamRespostaNotifInfeccaoDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.controleinfeccao.LocalNotificacaoOrigemRetornoVO;
import br.gov.mec.aghu.controleinfeccao.VMciMvtoPacsVO;
import br.gov.mec.aghu.controleinfeccao.dao.MciEtiologiaInfeccaoDAO;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoLocalNotificacao;
import br.gov.mec.aghu.dominio.DominioTipoTratamentoAtendimento;
import br.gov.mec.aghu.internacao.dao.AinMovimentosAtendUrgenciaDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MamRespostaNotifInfeccao;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ORADB PROCEDURE MCIK_RN.RN_MCIP_ATU_LOCAL - Atualiza o local de notificação e
 * de origem de uma notificação
 * 
 * @author aghu
 *
 */
@Stateless
public class AtualizaLocalNotificacaoOrigemRN extends BaseBusiness {

	private static final long serialVersionUID = 1255929698660675925L;

	private static final Log LOG = LogFactory.getLog(AtualizaLocalNotificacaoOrigemRN.class);

	private enum AtualizaLocalNotificacaoOrigemRNExceptionCode implements BusinessExceptionCode {
		MCI_00480, MCI_00793;
	}

	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;

	@Inject
	private MciEtiologiaInfeccaoDAO mciEtiologiaInfeccaoDAO;

	@Inject
	private MamRespostaNotifInfeccaoDAO mamRespostaNotifInfeccaoDAO;
	
	@Inject
	private AinMovimentosAtendUrgenciaDAO ainMovimentosAtendUrgenciaDAO;

	/**
	 * ORADB PROCEDURE MCIK_RN.RN_MCIP_ATU_LOCAL
	 * Rotina generica para determinar o local de notificacao e o local de
	 * origem
	 * 
	 * MÉTODO DIVIDIDO PARA EVITAR NPATH
	 * 
	 * @param pAtdSeq
	 * @param pEinTipoMvto
	 * @param pTipoMvto 
	 * @param pDtInicioMvto
	 * @param pRniPnnSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public LocalNotificacaoOrigemRetornoVO atualizarLocalNotificacaoOrigem(final Integer pAtdSeq, final String pEinTipoMvto, final DominioTipoMovimentoLocalNotificacao pTipoMvto, final Date pDtInicioMvto, MamRespostaNotifInfeccao pRniPnnSeq) throws ApplicationBusinessException {

		final LocalNotificacaoOrigemRetornoVO retornoVO = new LocalNotificacaoOrigemRetornoVO();

		// RN1 CURSOR c_atd
		final AghAtendimentos vAtd = this.aghAtendimentoDAO.obterPorChavePrimaria(pAtdSeq);
		if (vAtd == null) {
			throw new ApplicationBusinessException(AtualizaLocalNotificacaoOrigemRNExceptionCode.MCI_00480);
		}

		Integer v_pac_codigo_real;
		Date v_dt_inicio_mvto_real;

		// RN2 PARAM P_RNI_PNN_SEQ E P_RNI_SEQP
		if (pRniPnnSeq == null || pRniPnnSeq.getId() == null) {
			v_pac_codigo_real = vAtd.getPaciente().getCodigo();
			v_dt_inicio_mvto_real = pDtInicioMvto;
		} else {

			if (vAtd.getGsoPacCodigo() == null) { // Mãe
				v_pac_codigo_real = vAtd.getPaciente().getCodigo();
			} else {
				v_pac_codigo_real = vAtd.getGsoPacCodigo();
			}

			// CURSOR C_RNI
			MamRespostaNotifInfeccao rni = this.mamRespostaNotifInfeccaoDAO.obterAtualizaLocalNotificacaoOrigem(pRniPnnSeq.getId());
			if (rni == null) {
				throw new ApplicationBusinessException(AtualizaLocalNotificacaoOrigemRNExceptionCode.MCI_00793);
			}

			v_dt_inicio_mvto_real = rni.getMamPistaNotifInfeccao().getDthrEvento();
		}

		// RN3
		if (vAtd.getHospitalDia() != null) {
			// CURSOR c_ein
			popularRetornoPatologiaInfeccaoHospitalDia(retornoVO, pEinTipoMvto, vAtd);
			this.popularParametrosNotificadosRetorno(retornoVO, pRniPnnSeq, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
			return retornoVO;

		}

		// RN4
		if (vAtd.getConsulta() != null && vAtd.getInternacao() != null && vAtd.getAtendimentoUrgencia() != null && vAtd.getHospitalDia() != null) {
			this.popularParametrosRetorno(retornoVO, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
			this.popularParametrosNotificadosRetorno(retornoVO, pRniPnnSeq, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
			return retornoVO;
		}

		// RN5
		if (DominioTipoTratamentoAtendimento.VALOR_27.equals(vAtd.getIndTipoTratamento()) && (DominioTipoMovimentoLocalNotificacao.MFP.equals(pTipoMvto) || DominioTipoMovimentoLocalNotificacao.MRI.equals(pTipoMvto) || (DominioTipoMovimentoLocalNotificacao.MMP.equals(pTipoMvto) && StringUtils.isBlank(pEinTipoMvto)))) {
			this.popularParametrosRetorno(retornoVO, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
			this.popularParametrosNotificadosRetorno(retornoVO, pRniPnnSeq, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
			return retornoVO;
		}
		
		return atualizarLocalNotificacaoOrigemParte2(vAtd, retornoVO, pEinTipoMvto, pTipoMvto, pRniPnnSeq, v_pac_codigo_real, v_dt_inicio_mvto_real);

	}
	
	public LocalNotificacaoOrigemRetornoVO atualizarLocalNotificacaoOrigemParte2(final AghAtendimentos vAtd, LocalNotificacaoOrigemRetornoVO retornoVO, 
			final String p_ein_tipo_mvto, final DominioTipoMovimentoLocalNotificacao pTipoMvto, MamRespostaNotifInfeccao pRniPnnSeq,
			Integer v_pac_codigo_real, Date v_dt_inicio_mvto_real) throws ApplicationBusinessException {
		
			// RN6
				if (DominioOrigemAtendimento.C.equals(vAtd.getOrigem()) && StringUtils.isBlank(p_ein_tipo_mvto)) {
					this.popularParametrosRetorno(retornoVO, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
					this.popularParametrosNotificadosRetorno(retornoVO, pRniPnnSeq, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
					return retornoVO;
				}

				// RN7 CURSOR c_lcal
				List<VMciMvtoPacsVO> lCal = ainMovimentosAtendUrgenciaDAO.buscarMovimentoPaciente(v_pac_codigo_real, v_dt_inicio_mvto_real, null);
				if (lCal.isEmpty()) {
					this.popularParametrosRetorno(retornoVO, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
					this.popularParametrosNotificadosRetorno(retornoVO, pRniPnnSeq, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
					return retornoVO;
				}

				// RN 8
				VMciMvtoPacsVO vLcal = lCal.get(0);
				popularParametrosNotificadosRetorno(retornoVO, pRniPnnSeq, vLcal.getLtoLtoId(), vLcal.getQrtNumero(), vLcal.getUnfSeq());

				// RN9
				if (DominioTipoMovimentoLocalNotificacao.MFP.equals(pTipoMvto) || DominioTipoMovimentoLocalNotificacao.MRI.equals(pTipoMvto)) {
					this.popularParametrosRetorno(retornoVO, vLcal.getLtoLtoId(), vLcal.getQrtNumero(), vLcal.getUnfSeq());
					return retornoVO;
				}

				// RN10 CURSOR c_vmo
				List<VMciMvtoPacsVO> cVmo = ainMovimentosAtendUrgenciaDAO.buscarMovimentoPaciente(v_pac_codigo_real, v_dt_inicio_mvto_real, p_ein_tipo_mvto);
				VMciMvtoPacsVO vVmo = lCal.get(0);

				if (cVmo.isEmpty()) {
					popularRetornoPatologiaInfeccao(retornoVO, p_ein_tipo_mvto, vAtd, vVmo);
				} else {
					popularParametrosRetorno(retornoVO, vVmo.getLtoLtoId(), vVmo.getQrtNumero(), vVmo.getUnfSeq());
				}

				return retornoVO;		
		
	}

	/*
	 * Métodos reutilizáveis
	 */

	/**
	 * Popula retorno da procedure com hospital dia
	 * 
	 * @param retornoVO
	 * @param pEinTipoMvto
	 * @param vAtd
	 */
	private void popularRetornoPatologiaInfeccaoHospitalDia(LocalNotificacaoOrigemRetornoVO retornoVO, String pEinTipoMvto, AghAtendimentos vAtd) {
		popularRetornoPatologiaInfeccao(retornoVO, pEinTipoMvto, vAtd, null);
	}

	/**
	 * Popula retorno da procedure com patologia infecção
	 * 
	 * @param retornoVO
	 * @param pEinTipoMvto
	 * @param vAtd
	 * @param vLcal
	 */
	private void popularRetornoPatologiaInfeccao(LocalNotificacaoOrigemRetornoVO retornoVO, String pEinTipoMvto, AghAtendimentos vAtd, VMciMvtoPacsVO vLcal) {

		// CURSOR c_ein
		MciEtiologiaInfeccao cEin = null;
		if(StringUtils.isNotBlank(pEinTipoMvto)){
			cEin = this.mciEtiologiaInfeccaoDAO.obterPorChavePrimaria(pEinTipoMvto);
		}
			
		if (cEin != null) {

			Short vUnfSeqDefault = null;
			if (cEin.getUnidadeFuncional() != null) {
				vUnfSeqDefault = cEin.getUnidadeFuncional().getSeq();
			}

			if (CoreUtil.maior(vUnfSeqDefault, 0)) {
				retornoVO.setUnfSeq(vUnfSeqDefault);
			} else {
				this.popularParametrosRetorno(retornoVO, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());
			}

		} else {
			// Regra criada para a reutilização do método
			if (vLcal != null) {
				this.popularParametrosRetorno(retornoVO, vLcal.getLtoLtoId(), vLcal.getQrtNumero(), vLcal.getUnfSeq());
			} else {
				this.popularParametrosRetorno(retornoVO, vAtd.getLeito(), vAtd.getQuarto(), vAtd.getUnidadeFuncional());

			}

		}

	}

	/**
	 * Popula retorno da procedure com atendimento
	 * 
	 * @param retornoVO
	 * @param vAtdLtoLto
	 * @param vAtdQrt
	 * @param vAtdUnf
	 */
	private void popularParametrosRetorno(LocalNotificacaoOrigemRetornoVO retornoVO, AinLeitos vAtdLtoLto, AinQuartos vAtdQrt, AghUnidadesFuncionais vAtdUnf) {
		String vAtdLtoLtoId = vAtdLtoLto != null ? vAtdLtoLto.getLeitoID() : null;
		Short vAtdQrtNumero = vAtdQrt != null ? vAtdQrt.getNumero() : null;
		Short vAtdUnfSeq = vAtdUnf != null ? vAtdUnf.getSeq() : null;
		popularParametrosRetorno(retornoVO, vAtdLtoLtoId, vAtdQrtNumero, vAtdUnfSeq);
	}

	/**
	 * Popula retorno da procedure com atendimento
	 * 
	 * @param retornoVO
	 * @param vAtdLtoLtoId
	 * @param vAtdQrtNumero
	 * @param vAtdUnfSeq
	 */
	private void popularParametrosRetorno(LocalNotificacaoOrigemRetornoVO retornoVO, String vAtdLtoLtoId, Short vAtdQrtNumero, Short vAtdUnfSeq) {
		retornoVO.setLtoLtoId(vAtdLtoLtoId);
		retornoVO.setQrtNumero(vAtdQrtNumero);
		retornoVO.setUnfSeq(vAtdUnfSeq);
	}

	/**
	 * Popula retorno da procedure com atendimento com notificação
	 * 
	 * @param retorno
	 * @param pRni
	 * @param vAtdLtoLto
	 * @param vAtdQrt
	 * @param vAtdUnf
	 */
	private void popularParametrosNotificadosRetorno(LocalNotificacaoOrigemRetornoVO retorno, MamRespostaNotifInfeccao pRni, AinLeitos vAtdLtoLto, AinQuartos vAtdQrt, AghUnidadesFuncionais vAtdUnf) {
		String vAtdLtoLtoId = vAtdLtoLto != null ? vAtdLtoLto.getLeitoID() : null;
		Short vAtdQrtNumero = vAtdQrt != null ? vAtdQrt.getNumero() : null;
		Short vAtdUnfSeq = vAtdUnf != null ? vAtdUnf.getSeq() : null;
		popularParametrosNotificadosRetorno(retorno, pRni, vAtdLtoLtoId, vAtdQrtNumero, vAtdUnfSeq);
	}

	/**
	 * Popula retorno da procedure com atendimento com notificação
	 * 
	 * @param retornoVO
	 * @param pRni
	 * @param vAtdLtoLtoId
	 * @param vAtdQrtNumero
	 * @param vAtdUnfSeq
	 */
	private void popularParametrosNotificadosRetorno(LocalNotificacaoOrigemRetornoVO retornoVO, MamRespostaNotifInfeccao pRni, String vAtdLtoLtoId, Short vAtdQrtNumero, Short vAtdUnfSeq) {
		if (pRni == null) {
			retornoVO.setLtoLtoIdNotificado(vAtdLtoLtoId);
			retornoVO.setQrtNumeroNotificado(vAtdQrtNumero);
			retornoVO.setUnfSeqNotificado(vAtdUnfSeq);
		} else {
			if (pRni != null) {
				retornoVO.setUnfSeqNotificado(pRni.getConsulta().getGradeAgendamenConsulta().getUnidadeFuncional().getSeq());
			}
		}
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
