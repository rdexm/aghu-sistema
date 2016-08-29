package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatProcedHospIntCidDAO;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatProcedHospIntCidId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FatProcedHospIntCidON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FatProcedHospIntCidON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatProcedHospIntCidDAO fatProcedHospIntCidDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8508026861884472891L;

	/**
	 * Persiste Relacionar CID com PHI 
	 * 
	 * @param fatProcedHospIntCid
	 * @throws BaseException
	 */
	public void persistirFatProcedHospIntCid(FatProcedHospIntCid fatProcedHospIntCid, Boolean flush) throws BaseException {
		
		FatProcedHospIntCidDAO dao = getFatProcedHospIntCidDAO();
		
		FatProcedHospIntCid existe = dao.obterPorChavePrimaria(fatProcedHospIntCid.getId());
		
		if (existe == null) {//insert
			dao.persistir(fatProcedHospIntCid);
			if (flush){
				dao.flush();
			}
		}
		else { //update
			dao.atualizar(fatProcedHospIntCid);
			if (flush){
				dao.flush();
			}
		}
		
	}
	
	/**
	 * Remove Relacionar CID com PHI 
	 * 
	 * @param phiSeq
	 * @param cidSeq
	 * @throws BaseException
	 */
	public void removerFatProcedHospIntCid(Integer phiSeq, Integer cidSeq, Boolean flush) throws BaseException {
		
		FatProcedHospIntCidDAO dao = getFatProcedHospIntCidDAO();
		
		FatProcedHospIntCid elemento = dao.obterPorChavePrimaria(new FatProcedHospIntCidId(phiSeq, cidSeq));
		
		if (elemento != null) {
			dao.remover(elemento);
			if (flush){
				dao.flush();
			}
		}
		
	}	
	
	protected FatProcedHospIntCidDAO getFatProcedHospIntCidDAO() {
		return fatProcedHospIntCidDAO;
	}	
	
}
