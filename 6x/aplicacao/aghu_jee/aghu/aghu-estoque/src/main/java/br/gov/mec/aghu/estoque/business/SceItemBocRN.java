package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceItemBocDAO;
import br.gov.mec.aghu.model.SceItemBoc;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class SceItemBocRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SceItemBocRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemBocDAO sceItemBocDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -6072931275898661691L;

	private void preInserir(SceItemBoc itemBoletimOcorrencia) throws ApplicationBusinessException {
		//
	}
	
	private void posInserir(SceItemBoc itemBoletimOcorrencia) throws ApplicationBusinessException {
		//
	}
	
	@SuppressWarnings("ucd")
	public void inserir(SceItemBoc itemBoletimOcorrencia) throws ApplicationBusinessException {
		this.preInserir(itemBoletimOcorrencia);
		this.getSceItemBocDAO().persistir(itemBoletimOcorrencia);
		this.posInserir(itemBoletimOcorrencia);
	}
	
	private void preAtualizar(SceItemBoc itemBoletimOcorrencia) throws ApplicationBusinessException {
		//
	}

	private void posAtualizar(SceItemBoc itemBoletimOcorrencia) throws ApplicationBusinessException {
		//
	}

	public void atualizar(SceItemBoc itemBoletimOcorrencia) throws ApplicationBusinessException {
		this.preAtualizar(itemBoletimOcorrencia);
		this.getSceItemBocDAO().atualizar(itemBoletimOcorrencia);
		this.posAtualizar(itemBoletimOcorrencia);
	}

	@SuppressWarnings("ucd")
	public void remover(SceItemBoc itemBoletimOcorrencia) throws ApplicationBusinessException {
		this.getSceItemBocDAO().remover(itemBoletimOcorrencia);
	}
	
	private SceItemBocDAO getSceItemBocDAO() {
		return sceItemBocDAO;
	}	
}
