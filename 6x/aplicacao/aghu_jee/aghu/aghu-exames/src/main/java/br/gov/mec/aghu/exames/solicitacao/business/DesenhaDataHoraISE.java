package br.gov.mec.aghu.exames.solicitacao.business;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public abstract class DesenhaDataHoraISE extends BaseBusiness {
	
	protected DesenhaDataHoraISE successor;
	private AelPermissaoUnidSolic aelPermissaoUnidSolic;
	
	public AelPermissaoUnidSolic getAelPermissaoUnidSolic() {
		return aelPermissaoUnidSolic;
	}
	
    public void setAelPermissaoUnidSolic(AelPermissaoUnidSolic aelPermissaoUnidSolic) {
		this.aelPermissaoUnidSolic = aelPermissaoUnidSolic;
	}

	public void setSuccessor(DesenhaDataHoraISE successor){
        this.successor = successor;
    }

    abstract public TipoCampoDataHoraISE processRequest(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws ApplicationBusinessException;
	
}
