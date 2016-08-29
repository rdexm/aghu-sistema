package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSalasExecutorasExamesDAO;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AelSalasExecutorasExamesId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterSalaExecutoraExameON extends BaseBusiness  {


@EJB
private ManterSalaExecutoraExameRN manterSalaExecutoraExameRN;

private static final Log LOG = LogFactory.getLog(ManterSalaExecutoraExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelSalasExecutorasExamesDAO aelSalasExecutorasExamesDAO;

	private static final long serialVersionUID = 6546550440811515371L;

	public void persistirSalaExecutoraExame(AelSalasExecutorasExames salaExecutora) throws ApplicationBusinessException{
		if (salaExecutora.getCodigo()!=null){
			this.getManterSalaExecutoraExameRN().atualizar(salaExecutora);
		}else{
			Short unfSeq = salaExecutora.getUnidadeFuncional().getSeq();
			Short seqp = this.getAelSalasExecutorasExamesDAO().obterMaxSeqpSalaExecutoraPorUnidadeFuncional(unfSeq);
			if (seqp == null){
				seqp = 0;
			}
			AelSalasExecutorasExamesId idSalaExecutora = new AelSalasExecutorasExamesId();
			idSalaExecutora.setUnfSeq(unfSeq);
			idSalaExecutora.setSeqp(++seqp);
			salaExecutora.setId(idSalaExecutora);
			this.getManterSalaExecutoraExameRN().inserir(salaExecutora);
		}
	}
	
	public void excluirSalaExecutoraExame(AelSalasExecutorasExamesId id) throws ApplicationBusinessException{
		this.getManterSalaExecutoraExameRN().excluir(id);
	}
	
	protected AelSalasExecutorasExamesDAO getAelSalasExecutorasExamesDAO(){
		return aelSalasExecutorasExamesDAO;
	}
	
	protected ManterSalaExecutoraExameRN getManterSalaExecutoraExameRN(){
		return manterSalaExecutoraExameRN;
	}
	
}
