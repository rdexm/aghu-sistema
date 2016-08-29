package br.gov.mec.aghu.business.bancosangue;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 */

@Stateless
public class JustificativaComponenteSanguineoRN extends	BaseBusiness implements Serializable {


	@Inject
	private AbsJustificativaComponenteSanguineoDAO absJustificativaComponenteSanguineoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5901972767954341308L;

	private static final Log LOG = LogFactory.getLog(JustificativaComponenteSanguineoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private enum JustificativaComponenteSanguineoRNExceptionCode implements BusinessExceptionCode {
			ABS_00166;
	}
	
	protected AbsJustificativaComponenteSanguineoDAO getAbsJustificativaComponenteSanguineoDAO() {
		return absJustificativaComponenteSanguineoDAO;
	}

	public void excluirJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo)	
	throws ApplicationBusinessException {
		
		this.getAbsJustificativaComponenteSanguineoDAO().remover(justificativaComponenteSanguineo);
	}
	
	/**
	 * ORADB ABST_JCS_BRI
	 * 
	 * Implementação da trigger ABST_JCS_BRI
	 * da tabela ABS_JUSTIF_COMPONENTES_SANG
	 * 
	 * @param justificativaComponenteSanguineo
	 * @throws ApplicationBusinessException
	 */
	public void preInserirJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo)	
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date dataAtual = new Date();
		justificativaComponenteSanguineo.setCriadoEm(dataAtual);
		justificativaComponenteSanguineo.setAlteradoEm(dataAtual);
		
		if (servidorLogado.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(
					JustificativaComponenteSanguineoRNExceptionCode	.ABS_00166);
		}
		
		justificativaComponenteSanguineo.setServidor(servidorLogado);
	}

	/**
	 * ORADB ABST_JCS_BRU
	 * 
	 * Implementação da trigger de before UPDATE da tabela
	 * ABS_JUSTIF_COMPONENTES_SANG
	 * 
	 * @param justificativaComponenteSanguineo
	 * @throws ApplicationBusinessException
	 */
	public void preAtualizarJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		justificativaComponenteSanguineo.setAlteradoEm(new Date());
		
		if (servidorLogado.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(
					JustificativaComponenteSanguineoRNExceptionCode	.ABS_00166);
		}
		
		justificativaComponenteSanguineo.setServidorAlterado(servidorLogado);
	}

	public void gravarJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo) throws ApplicationBusinessException{
		if(justificativaComponenteSanguineo != null && justificativaComponenteSanguineo.getSeq() == null){
			this.preInserirJustificativaComponenteSanguineo(justificativaComponenteSanguineo);
			this.getAbsJustificativaComponenteSanguineoDAO().persistir(justificativaComponenteSanguineo);
		}else{
			this.preAtualizarJustificativaComponenteSanguineo(justificativaComponenteSanguineo);
			if(this.getAbsJustificativaComponenteSanguineoDAO().contains(justificativaComponenteSanguineo)) {
				this.getAbsJustificativaComponenteSanguineoDAO().merge(justificativaComponenteSanguineo);
			}
		}
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
