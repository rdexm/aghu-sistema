package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCidUsualEquipeDAO;
import br.gov.mec.aghu.model.MbcCidUsualEquipe;
import br.gov.mec.aghu.model.MbcCidUsualEquipeId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcCidUsualEquipeRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcCidUsualEquipeRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCidUsualEquipeDAO mbcCidUsualEquipeDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 2792386332796173034L;

	
	public enum MbcCidUsualEquipeRNExceptionCode implements	BusinessExceptionCode {
		CONSTRAINT_CIDS_EQUIPE;
	}
	
	
	
	public void atualizar(MbcCidUsualEquipe elemento) throws BaseException {
		this.preAtualizar(elemento);
		this.getMbcCidUsualEquipeDAO().atualizar(elemento);
	}
	
	
	public void inserir(MbcCidUsualEquipe elemento) throws BaseException {
		this.preInserir(elemento);
		this.getMbcCidUsualEquipeDAO().persistir(elemento);
		this.getMbcCidUsualEquipeDAO().flush();
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_CUQ_BRU
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	private void preAtualizar(MbcCidUsualEquipe elemento) throws BaseException {
		elemento.setAlteradoEm(new Date());
	}
	
	
	/**
	 * ORADB TRIGGER MBCT_CUQ_BRI
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	private void preInserir(MbcCidUsualEquipe elemento) throws BaseException {
		this.executarRestricao(elemento);
		elemento.setCriadoEm(new Date());
	}
	
	
	public void executarRestricao(MbcCidUsualEquipe elemento) throws BaseException {
		MbcCidUsualEquipe cidUsualEquipe = 
			this.getMbcCidUsualEquipeDAO().obterPorChavePrimaria(
					new MbcCidUsualEquipeId(
					elemento.getAghEquipes().getSeq().shortValue(),
					elemento.getAghCid().getSeq()));
		
		if(cidUsualEquipe != null) {
			throw new ApplicationBusinessException(MbcCidUsualEquipeRNExceptionCode.CONSTRAINT_CIDS_EQUIPE);
		}
	}
	
	
	/** GET **/
	private MbcCidUsualEquipeDAO getMbcCidUsualEquipeDAO() {
		return mbcCidUsualEquipeDAO;
	}
	
	
}
