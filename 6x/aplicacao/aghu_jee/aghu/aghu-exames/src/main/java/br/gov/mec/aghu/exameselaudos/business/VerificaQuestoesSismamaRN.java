package br.gov.mec.aghu.exameselaudos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResHistDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;

/**
 * Utilizada pela estoria #5868 – Verificar questões do SISMAMA
 *
 */
@Stateless
public class VerificaQuestoesSismamaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(VerificaQuestoesSismamaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSismamaMamoResHistDAO aelSismamaMamoResHistDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AelSismamaMamoResDAO aelSismamaMamoResDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116321235468809689L;
	
	/**
	 * @ORADB - AELP_CHAMA_QUT_SISMAMA_VESPE
	 * 
	 * Migradas as Linhas 72 á 86 para atender a #5868
	 * 
	 * Metodo irá retornar um Object  onde as keys são v_histo, v_cito, v_mamo. O valor correspondente será false para 0 e true para 1.
	 *  
	 * @author daniel.silva
	 * @since  27/12/12
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param unfSeq
	 * @return
	 */
	public Boolean verificarQuestaoSismama(Integer iseSoeSeq, Short iseSeqp, Short unfSeq, boolean isBuscaHisto) {
		//verifica a necessidade de chamar a tela de quetionário da mamografia

		Boolean hasCaracteristicaExSismaMamo =
			getAghuFacade().verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.EXAME_SISMAMA_MAMO);
		//if (getAghCaractUnidFuncionaisDAO().verificarCaracteristicaDaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.EXAME_SISMAMA_MAMO).isSim()) {
		if (hasCaracteristicaExSismaMamo) {
			Long con; 
			if (isBuscaHisto) {
				con = getAelSismamaMamoResHistDAO().obterRespostaMamografiaCountHist(iseSoeSeq, iseSeqp);
			} else {
				con = getAelSismamaMamoResDAO().obterRespostaMamografiaCount(iseSoeSeq, iseSeqp);
			}

			if (con > 0) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}

	
	private AelSismamaMamoResHistDAO getAelSismamaMamoResHistDAO() {
		return aelSismamaMamoResHistDAO;
	}

	protected AelSismamaMamoResDAO getAelSismamaMamoResDAO() {
		return aelSismamaMamoResDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}