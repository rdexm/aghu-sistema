package br.gov.mec.aghu.exames.solicitacao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.model.AelExameApItemSolic;

@Modulo(ModuloEnum.EXAMES_LAUDOS)
@Stateless
public class SolicitacaoExame2Facade extends BaseFacade implements ISolicitacaoExame2Facade {
	private static final long serialVersionUID = 3761809856583509275L;

	@EJB
	private AelExameApItemSolicRN aelExameApItemSolicRN;
	
	@Override
	public void inserirAelExameApItemSolic(final AelExameApItemSolic elemento) throws BaseException {
		this.getAelExameApItemSolicRN().inserir(elemento);
	}

	protected ItemSolicitacaoExameEnforceRN getItemSolicitacaoExameEnforceRN() {
		return new ItemSolicitacaoExameEnforceRN();
	}

	protected AelExameApItemSolicRN getAelExameApItemSolicRN() {
		return aelExameApItemSolicRN;
	}

}
