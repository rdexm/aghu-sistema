package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatMotivoRejeicaoContaDAO;
import br.gov.mec.aghu.model.FatMotivoRejeicaoConta;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FatMotivoRejeicaoContaON extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 3422279483453546291L;

	private static final Log LOG = LogFactory.getLog(FatMotivoRejeicaoContaON.class);
	
	@Inject
	private FatMotivoRejeicaoContaDAO fatMotivoRejeicaoContaDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Método responsável pela persistencia da entidade. 
	 * 
	 * @param fatMotivoRejeicaoConta {@link FatMotivoRejeicaoConta}
	 */
	public void gravarMotivoRejeicaoConta(FatMotivoRejeicaoConta fatMotivoRejeicaoConta) throws ApplicationBusinessException {
		
		if (fatMotivoRejeicaoConta.getSeq() != null) {
			
			this.fatMotivoRejeicaoContaDAO.atualizar(fatMotivoRejeicaoConta);
			
		} else {
			
			this.fatMotivoRejeicaoContaDAO.persistir(fatMotivoRejeicaoConta);
		}
	}
}
