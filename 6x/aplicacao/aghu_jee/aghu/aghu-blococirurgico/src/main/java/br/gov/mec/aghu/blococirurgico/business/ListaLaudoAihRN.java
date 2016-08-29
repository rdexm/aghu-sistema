package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ListaLaudoAihRN  extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ListaLaudoAihRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8882216894957385729L;
	
	/**
	 * Procedure
	 * 
	 * ORADB: p_visualizar_laudo
	 * 
	 */
	public MamLaudoAih gravarMamLaudoAih(Long seq) throws ApplicationBusinessException{

		MamLaudoAih mamLaudoAih = getAmbulatorioFacade().obterMamLaudoAihPorChavePrimaria(seq);
		
		if (DominioIndPendenteLaudoAih.R.equals(mamLaudoAih.getIndPendente())) {
			if (mamLaudoAih.getMamLaudoAihs().getSeq() == null) {
				mamLaudoAih.setIndPendente(DominioIndPendenteLaudoAih.P);
				getAmbulatorioFacade().gravarMamLaudoAih(mamLaudoAih);
			}
		}		
		
		return mamLaudoAih;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return iAmbulatorioFacade;
	}
}
