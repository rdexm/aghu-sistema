package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatExcCnvGrpItemProcDAO;
import br.gov.mec.aghu.model.FatExcCnvGrpItemProc;
import br.gov.mec.aghu.core.business.BaseBusiness;



@Stateless
public class FatExcCnvGrpItemProcON extends BaseBusiness implements	Serializable {


@EJB
private FatExcCnvGrpItemProcRN fatExcCnvGrpItemProcRN;

private static final Log LOG = LogFactory.getLog(FatExcCnvGrpItemProcON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatExcCnvGrpItemProcDAO fatExcCnvGrpItemProcDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5720370863773670670L;

	/**
	 * Persiste uma entidade FatExcCnvGrpItemProc, executando as respectivas triggers.
	 * 
	 * No caso de inserção, deve-se passar o segundo parâmetro nulo.
	 * 
	 * @param excCnvGrpItemProcNew
	 * @param excCnvGrpItemProcOld
	 */
	public void persistirExcCnvGrpItemProc(FatExcCnvGrpItemProc excCnvGrpItemProcNew){
		FatExcCnvGrpItemProcRN fatExcCnvGrpItemProcRN = getFatExcCnvGrpItemProcRN();
		FatExcCnvGrpItemProcDAO fatExcCnvGrpItemProcDAO = getFatExcCnvGrpItemProcDAO();
		
		if(excCnvGrpItemProcNew.getSeq() == null){
			fatExcCnvGrpItemProcRN.executarAntesDeInserir(excCnvGrpItemProcNew);
			fatExcCnvGrpItemProcDAO.persistir(excCnvGrpItemProcNew);
			
		}else{
			fatExcCnvGrpItemProcRN.executarAntesDeAtualizar(excCnvGrpItemProcNew);
			//fatExcCnvGrpItemProcDAO.atualizar(excCnvGrpItemProcNew);//Comentado pois nao deve executar flush nesse momento.
			FatExcCnvGrpItemProc excCnvGrpItemProcOld = fatExcCnvGrpItemProcDAO.obterOriginal(excCnvGrpItemProcNew);
            fatExcCnvGrpItemProcDAO.merge(excCnvGrpItemProcNew);
			fatExcCnvGrpItemProcRN.executarDepoisDeAtualizar(excCnvGrpItemProcNew, excCnvGrpItemProcOld);
		}
		
	}
	
	/**
	 * Metodo para remover uma entidade FatExcCnvGrpItemProc. 
	 * @param excCnvGrpItemProc
	 */
	public void removerExcCnvGrpItemProc(FatExcCnvGrpItemProc excCnvGrpItemProc){
		FatExcCnvGrpItemProcRN fatExcCnvGrpItemProcRN = getFatExcCnvGrpItemProcRN();
		FatExcCnvGrpItemProcDAO fatExcCnvGrpItemProcDAO = getFatExcCnvGrpItemProcDAO();
		
        fatExcCnvGrpItemProcDAO.removerPorId(excCnvGrpItemProc.getSeq());
		fatExcCnvGrpItemProcRN.executarDepoisDeRemover(excCnvGrpItemProc);
	}

	protected FatExcCnvGrpItemProcRN getFatExcCnvGrpItemProcRN() {
		return fatExcCnvGrpItemProcRN;
	}

	protected FatExcCnvGrpItemProcDAO getFatExcCnvGrpItemProcDAO() {
		return fatExcCnvGrpItemProcDAO;
	}
}
