package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ORADB PROCEDURE IMPRIME_MOV_MED_FARMACIA_DISP
 * 
 * @author aghu
 *
 */
@Stateless
public class ImprimeMovimentoMedicamentoFarmaciaDispensacaoRN extends BaseBusiness {

	private static final long serialVersionUID = -2046211624930972432L;

	private static final Log LOG = LogFactory.getLog(ImprimeMovimentoMedicamentoFarmaciaDispensacaoRN.class);

	public static final Byte NRO_VIAS_PME_PADRAO = 2;

	public enum ImprimeMovimentoMedicamentoFarmaciaDispensacaoRNRNExceptionCode implements BusinessExceptionCode {
		MPM_02364;
	}

	//@EJB
	//private IParametroFacade parametroFacade;

	//@EJB
	//private IAghuFacade aghuFacade;

	/**
	 * ORADB PROCEDURE IMPRIME_MOV_MED_FARMACIA_DISP
	 * 
	 * @param pme
	 * @param dthrMovimento
	 * @throws ApplicationBusinessException
	 */
//	public void imprimirMovimentoMedicamentoFarmaciaDispensacao(final MpmPrescricaoMedica pme, final Date dthrMovimento) throws ApplicationBusinessException {
//
//		final AghParametros parametroUnfFarmDisp = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UNF_FARM_DISP);
//		final Short farmPadrao = parametroUnfFarmDisp.getVlrNumerico().shortValue();
//
//		// Cursor C1
//		List<AghUnidadesFuncionais> listaUnidadesFarmaciasAtivas = this.aghuFacade.obterListaUnidadesFuncionaisAtivasPorCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_FARMACIA);
//
//		for (AghUnidadesFuncionais unf : listaUnidadesFarmaciasAtivas) {
//			// NVL(NRO_VIAS_PME, 2)
//			final Byte nroViasPme = unf.getNroViasPme() != null ? unf.getNroViasPme() : NRO_VIAS_PME_PADRAO;
//
//			DominioSimNao movimento = null; // TODO CHAMAR mpmc_ver_farm_mov_me
//											// do Fernando
//
//			if (DominioSimNao.S.equals(movimento)) {
//
//				if (unf.getSeq().equals(farmPadrao)) {
//					// TODO
//					// Chama_report_mov_med_farm_disp (v_unf_seq, p_dthr_movimento, 
//					//               v_nro_vias_pme,                'S' 
//					//               );
//				} else {
//					// TODO
//					// Chama_report_mov_med_farm_disp (v_unf_seq, p_dthr_movimento, 
//					//               v_nro_vias_pme,                'N' 
//					//               );
//
//				}
//
//			}
//
//		}
//
//	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

}
