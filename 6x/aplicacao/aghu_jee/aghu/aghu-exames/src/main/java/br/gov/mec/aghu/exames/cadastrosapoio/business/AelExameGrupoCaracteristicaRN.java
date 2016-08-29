package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelExameGrupoCaracteristicaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelExameGrupoCaracteristicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExameGrupoCaracteristicaDAO aelExameGrupoCaracteristicaDAO;

	private static final long serialVersionUID = -7094168329684772452L;

	protected enum AelExameGrupoCaracteristicaRNExceptionCode implements BusinessExceptionCode {
		AEL_00046,//
		ERRO_REMOVER_EXAME_GRUPO_CARACTERISTICA;//
	}

	public void atualizar(AelExameGrupoCaracteristica elemento) throws BaseException {
		this.getAelExameGrupoCaracteristicaDAO().merge(elemento);
	}

	public void persistir(AelExameGrupoCaracteristica elemento) throws BaseException {
		this.inserir(elemento);
	}

	private void inserir(AelExameGrupoCaracteristica elemento) throws BaseException {
		this.preInserir(elemento);

		AelExameGrupoCaracteristicaId id = new AelExameGrupoCaracteristicaId();
		id.setEmaExaSigla(elemento.getExameMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExameMaterialAnalise().getId().getManSeq());
		id.setCacSeq(elemento.getResultadoCaracteristica().getSeq());
		id.setGcaSeq(elemento.getGrupoResultadoCaracteristica().getSeq());
		elemento.setId(id);
		
		this.getAelExameGrupoCaracteristicaDAO().persistir(elemento);
	}
	
	public void remover(AelExameGrupoCaracteristicaId id) throws BaseException {
		this.getAelExameGrupoCaracteristicaDAO().removerPorId(id);
	}

	/**
	 * ORADB TRIGGER AELT_EGC_BRI
	 */
	private void preInserir(AelExameGrupoCaracteristica elemento) throws BaseException {
		this.obterServidorLogado(elemento);
		this.executarRestricoes(elemento);
	}

	private void obterServidorLogado(AelExameGrupoCaracteristica elemento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		elemento.setServidor(servidorLogado);
	}

	/**
	 * ORADB CONSTRAINTS (INSERT/UPDATE)
	 */
	protected void executarRestricoes(AelExameGrupoCaracteristica elemento) throws ApplicationBusinessException {
		
		AelExameGrupoCaracteristicaId id = new AelExameGrupoCaracteristicaId();
		id.setEmaExaSigla(elemento.getExameMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExameMaterialAnalise().getId().getManSeq());
		id.setCacSeq(elemento.getResultadoCaracteristica().getSeq());
		id.setGcaSeq(elemento.getGrupoResultadoCaracteristica().getSeq());
		
		AelExameGrupoCaracteristica exameGrupoCaract = this.getAelExameGrupoCaracteristicaDAO().obterOriginal(id);
		
		if(exameGrupoCaract != null) {
			throw new ApplicationBusinessException(AelExameGrupoCaracteristicaRNExceptionCode.AEL_00046);
		}
		
	}
	
	/** GET **/
	protected AelExameGrupoCaracteristicaDAO getAelExameGrupoCaracteristicaDAO() {
		return aelExameGrupoCaracteristicaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
