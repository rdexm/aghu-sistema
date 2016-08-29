package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class EstornarNotaRecebimentoBean extends BaseBusiness implements EstornarNotaRecebimentoBeanLocal {

	private static final long serialVersionUID = -1483757745105211696L;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return null;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	public void estornarNotaRecebimento(Integer seqNotaRecebimento, String nomeMicrocomputador) throws BaseException {
		getEstoqueFacade().estornarNotaRecebimento(seqNotaRecebimento, nomeMicrocomputador);
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
}
