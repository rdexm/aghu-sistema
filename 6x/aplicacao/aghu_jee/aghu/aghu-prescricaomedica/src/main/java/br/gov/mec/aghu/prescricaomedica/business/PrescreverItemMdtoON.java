package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTelaPrescreverItemMdto;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaTbDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotificacaoTbDAO;
import br.gov.mec.aghu.prescricaomedica.vo.JustificativaMedicamentoUsoGeralVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoNotificacaoTbVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificarDadosItensJustificativaPrescricaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author feoliveira
 *
 */
@Stateless
public class PrescreverItemMdtoON extends BaseBusiness {

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private MpmNotificacaoTbRN mpmNotificacaoTbRN;

	@Inject
	private MpmJustificativaTbDAO mpmJustificativaTbDAO;

	@Inject
	private MpmNotificacaoTbDAO mpmNotificacaoTbDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7738503480021380994L;
	private static final Log LOG = LogFactory.getLog(PrescreverItemMdtoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private enum PrescreverItemMdtoONExceptionCode implements BusinessExceptionCode {
		MPM_01569, MPM_03572, MPM_03573;
	}

	// c_view_justif list
	private List<JustificativaMedicamentoUsoGeralVO> listarPrescricaoPendenteVpp(Integer atdSeq) {
		return prescricaoMedicaFacade.obterMedicamentosIndicesRestritosPorAtendimento(atdSeq, null);
	}

	// c_view_justif
	private JustificativaMedicamentoUsoGeralVO obterPrescricaoPendenteVpp(Integer atdSeq, Short paramGupTb) {
		List<JustificativaMedicamentoUsoGeralVO> lista = prescricaoMedicaFacade.obterMedicamentosIndicesRestritosPorAtendimento(atdSeq, paramGupTb);
		return (lista != null && !lista.isEmpty() ? lista.get(0) : null);
	}

	// c_notif_tb
	private PrescricaoNotificacaoTbVO obterJustificativaTbUnionNotificacaoTbPorAtendimento(Integer atdSeq) {
		List<PrescricaoNotificacaoTbVO> lista = new ArrayList<PrescricaoNotificacaoTbVO>();
		lista.addAll(mpmNotificacaoTbDAO.listarNotificacoesTbPorAtendimento(atdSeq));

		List<PrescricaoNotificacaoTbVO> listaIndConcluido = mpmJustificativaTbDAO.listarJustificativasTbPorAtendimento(atdSeq);
		for (PrescricaoNotificacaoTbVO vo : listaIndConcluido) {
			vo.setIndConcluido(Boolean.TRUE);
		}

		lista.addAll(listaIndConcluido);
		return (lista != null && !lista.isEmpty() ? lista.get(0) : null);
	}

	/**
	 * @ORADB MPMP_VER_DADOS_ITENS
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public VerificarDadosItensJustificativaPrescricaoVO mpmpVerDadosItens(Integer atdSeq) throws ApplicationBusinessException {
		VerificarDadosItensJustificativaPrescricaoVO retorno = new VerificarDadosItensJustificativaPrescricaoVO();
		
		AghParametros paramGupTb = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRPO_USO_MDTO_TB);
		if (paramGupTb == null) {
			throw new ApplicationBusinessException(PrescreverItemMdtoONExceptionCode.MPM_01569);
		}

		List<JustificativaMedicamentoUsoGeralVO> listaCViewJustif = this.listarPrescricaoPendenteVpp(atdSeq);
		PrescricaoNotificacaoTbVO cNotifTb = this.obterJustificativaTbUnionNotificacaoTbPorAtendimento(atdSeq);

		for (JustificativaMedicamentoUsoGeralVO voCViewJustif : listaCViewJustif) {
			if (paramGupTb != null && voCViewJustif.getGupSeq() == Short.valueOf(paramGupTb.getVlrNumerico().toString())) {
				if (cNotifTb == null) {
					Integer ntbSeq = this.mpmNotificacaoTbRN.preInserirJustificativaUsoMedicamentosTb(atdSeq);
					retorno.setTela(DominioTelaPrescreverItemMdto.MPMF_NOTIFICACAO_TB);
					retorno.setSeqNotificacao(ntbSeq);
					return retorno;
				} else if (!Boolean.TRUE.equals(cNotifTb.getIndConcluido())) {
					retorno.setTela(DominioTelaPrescreverItemMdto.MPMF_NOTIFICACAO_TB);
					retorno.setSeqNotificacao(cNotifTb.getSeq());
					return retorno;
				}
			}
		}
		
		JustificativaMedicamentoUsoGeralVO cViewJustifVO = this.obterPrescricaoPendenteVpp(atdSeq, Short.valueOf(paramGupTb.getVlrNumerico().toString()));
		if (cViewJustifVO != null) {
			return this.selecionarJustificativa(cViewJustifVO, retorno);
		} else {
			JustificativaMedicamentoUsoGeralVO cViewJustifVOTB = this.obterPrescricaoPendenteVpp(atdSeq, null);
			if (cViewJustifVOTB != null) {
				return this.selecionarJustificativa(cViewJustifVOTB, retorno);
			}
		}
		return null;
	}
	
	private VerificarDadosItensJustificativaPrescricaoVO selecionarJustificativa(JustificativaMedicamentoUsoGeralVO cViewJustifVO,
			VerificarDadosItensJustificativaPrescricaoVO retorno) throws ApplicationBusinessException {
		
		AghParametros paramGupUr = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRPO_USO_MDTO_UR);
		if (paramGupUr == null) {
			throw new ApplicationBusinessException(PrescreverItemMdtoONExceptionCode.MPM_03572);
		}

		AghParametros paramGupNs = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRPO_USO_MDTO_NS);
		if (paramGupNs == null) {
			throw new ApplicationBusinessException(PrescreverItemMdtoONExceptionCode.MPM_03573);
		}
		
		if (DominioSimNao.S.equals(cViewJustifVO.getIndQuimioterapicoBoolean())) {
			retorno.setTela(DominioTelaPrescreverItemMdto.MPMF_JUST_QT);
			return retorno;
		} else if (paramGupUr != null && DominioSimNao.N.equals(cViewJustifVO.getIndAntimicrobianoBoolean()) && cViewJustifVO.getGupSeq() == Short.valueOf(paramGupUr.getVlrNumerico().toString())) {
			retorno.setTela(DominioTelaPrescreverItemMdto.MPMF_JUST_UR);
			return retorno;
		} else if (paramGupNs != null && DominioSimNao.N.equals(cViewJustifVO.getIndAntimicrobianoBoolean()) && cViewJustifVO.getGupSeq() == Short.valueOf(paramGupNs.getVlrNumerico().toString())) {
			retorno.setTela(DominioTelaPrescreverItemMdto.MPMF_JUST_NS);
			return retorno;
		} else if (paramGupUr != null && DominioSimNao.S.equals(cViewJustifVO.getIndAntimicrobianoBoolean()) && cViewJustifVO.getGupSeq() == Short.valueOf(paramGupUr.getVlrNumerico().toString())) {
			retorno.setTela(DominioTelaPrescreverItemMdto.MPMF_JUST_UR_MICROB);
			return retorno;
		} else if (paramGupNs != null && DominioSimNao.S.equals(cViewJustifVO.getIndAntimicrobianoBoolean()) && cViewJustifVO.getGupSeq() == Short.valueOf(paramGupNs.getVlrNumerico().toString())) {
			retorno.setTela(DominioTelaPrescreverItemMdto.MPMF_JUST_NS_MICROB);
			return retorno;
		} else {
			retorno.setTela(DominioTelaPrescreverItemMdto.MPMF_ATU_JUST_MDTO);
			return retorno;
		}
	}
}
