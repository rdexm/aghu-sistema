package br.gov.mec.aghu.faturamento.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ProcedHospInternoBean extends BaseBusiness implements ProcedHospInternoBeanLocal {

private static final Log LOG = LogFactory.getLog(ProcedHospInternoBean.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade iFaturamentoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2908391216543469152L;
	
	@Resource
	protected SessionContext ctx;
	
	@Override
	public void inserirProcedimentoHospitalarInterno(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, String descricao,
			DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		try {
			getFaturamentoFacade().inserirProcedimentoHospitalarInterno(matCodigo, procedimentoCirurgico,
					procedEspecialDiverso, csaCodigo, pheCodigo, descricao, indSituacao, euuSeq, cduSeq, cuiSeq, tidSeq);			
		} catch (ApplicationBusinessException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public void atualizarProcedimentoHospitalarInternoSituacao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, DominioSituacao indSituacao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		try {
			getFaturamentoFacade().atualizarProcedimentoHospitalarInternoSituacao(matCodigo, procedimentoCirurgico, procedEspecialDiverso, 
					csaCodigo, pheCodigo, indSituacao, euuSeq, cduSeq, cuiSeq, tidSeq);	
		} catch (ApplicationBusinessException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public void atualizarProcedimentoHospitalarInternoDescricao(ScoMaterial matCodigo,
			MbcProcedimentoCirurgicos procedimentoCirurgico, MpmProcedEspecialDiversos procedEspecialDiverso,
			String csaCodigo, String pheCodigo, String descricao, Short euuSeq, Integer cduSeq,
			Short cuiSeq, Integer tidSeq) throws ApplicationBusinessException {
		
		try {
			getFaturamentoFacade().atualizarProcedimentoHospitalarInternoDescricao(matCodigo, procedimentoCirurgico, procedEspecialDiverso,
					csaCodigo, pheCodigo, descricao, euuSeq, cduSeq, cuiSeq, tidSeq);
		} catch (ApplicationBusinessException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		}
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.iFaturamentoFacade;
	}

}
