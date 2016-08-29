package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FatCompetenciaON extends BaseBusiness {


@EJB
private FatCompetenciaRN fatCompetenciaRN;

private static final Log LOG = LogFactory.getLog(FatCompetenciaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 4610979435131058424L;

	protected FatCompetenciaRN getFatCompetenciaRN(){
		return fatCompetenciaRN;
	}
	
	protected void atualizarFatCompetencia(final FatCompetencia fatCompetencia) throws ApplicationBusinessException {
		getFatCompetenciaRN().atualizarFatCompetencia(fatCompetencia);
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public FatCompetencia clonarFatCompetencia(final FatCompetencia competencia) throws Exception {
		final FatCompetencia clone = (FatCompetencia) BeanUtils.cloneBean(competencia);
		
		if(competencia.getId() != null){
			clone.setId(competencia.getId());
		}
		
		if(competencia.getFatArqEspelhoProcedAmbs() != null){
			clone.setFatArqEspelhoProcedAmbs(competencia.getFatArqEspelhoProcedAmbs());
		}
		
		if(competencia.getFatDocumentoCobrancaAihses() != null){
			clone.setFatDocumentoCobrancaAihses(competencia.getFatDocumentoCobrancaAihses());
		}
		
		if(competencia.getFatEspelhoProcedAmbs() != null){
			clone.setFatEspelhoProcedAmbs(competencia.getFatEspelhoProcedAmbs());
		}
		
		if(competencia.getFatEspelhoProcedSiscoloses() != null){
			clone.setFatEspelhoProcedSiscoloses(competencia.getFatEspelhoProcedSiscoloses());
		}
		
		if(competencia.getFatEspelhosContaApacsForFatEcaCpeFk1() != null){
			clone.setFatEspelhosContaApacsForFatEcaCpeFk1(competencia.getFatEspelhosContaApacsForFatEcaCpeFk1());
		}
		
		if(competencia.getFatEspelhosContaApacsForFatEcaCpeFk2() != null){
			clone.setFatEspelhosContaApacsForFatEcaCpeFk2(competencia.getFatEspelhosContaApacsForFatEcaCpeFk2());
		}
		
		if(competencia.getFatEspelhoSismamas() != null){
			clone.setFatEspelhoSismamas(competencia.getFatEspelhoSismamas());
		}
		
		return clone;
	}
}
