package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelSinonimoExameDAO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelSinonimoExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelSinonimosExamesCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelSinonimosExamesCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelSinonimoExameDAO aelSinonimoExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8084077981229662783L;
	/**
	 * Seqp inválido para alterações e exclusão
	 */
	private final static String seqpInvalido = "1";

	public enum AelSinonimosExamesCRUDExceptionCode implements BusinessExceptionCode {

		AEL_00357, AEL_00369, AEL_00777, AEL_00777_2;

	}
	
	/**
	 * ORADB TRIGGER AELT_SIE_BRI
	 * @param aelSinonimoExame
	 * @throws ApplicationBusinessException 
	 */
	public void inserirAelSinonimosExames(AelSinonimoExame aelSinonimoExame) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.validaUnicoSinonimoInserir(aelSinonimoExame);
		aelSinonimoExame.setCriadoEm(new Date());
		aelSinonimoExame.setRapServidor(servidorLogado);
		this.getAelSinonimoExameDAO().persistir(aelSinonimoExame);
		this.getAelSinonimoExameDAO().flush();
		
	}
	
	/**
	 * ORADB TRIGGER AELT_SIE_BRU
	 * @param aelSinonimoExame
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void atualizarAelSinonimosExames(AelSinonimoExame aelSinonimoExame, boolean validaSeqp) throws ApplicationBusinessException {
		this.validaUnicoSinonimoAtualizar(aelSinonimoExame);
		this.preAtualizarAelSinonimosExames(aelSinonimoExame, validaSeqp);
		this.getAelSinonimoExameDAO().merge(aelSinonimoExame);
		this.getAelSinonimoExameDAO().flush();
	}
	
	/**
	 * ORADB AELT_SIE_BRD
	 * @param aelSinonimoExame
	 * @throws ApplicationBusinessException
	 */
	public void removerAelSinonimosExames(AelSinonimoExame aelSinonimoExame) throws ApplicationBusinessException {
		aelSinonimoExame = this.getAelSinonimoExameDAO().obterPorChavePrimaria(aelSinonimoExame.getId());
		this.validarSinonimoSeqp(aelSinonimoExame.getId().getSeqp());
		this.getAelSinonimoExameDAO().remover(aelSinonimoExame);
		this.getAelSinonimoExameDAO().flush();
		
	}
	
	/**
	 * Validações da trigger de update
	 * @param aelSinonimoExame
	 */
	public void preAtualizarAelSinonimosExames(AelSinonimoExame aelSinonimoExame, boolean validaSeqp) throws ApplicationBusinessException {
		
		AelSinonimoExame antigoSinonimoExame = this.getAelSinonimoExameDAO().obterAelSinonimoExamePorId(aelSinonimoExame.getId());

		if (validaSeqp) {
			
			this.validarSinonimoSeqp(aelSinonimoExame.getId().getSeqp());
			
		}
		
		this.validarAlteracao(aelSinonimoExame, antigoSinonimoExame);
		this.getAelSinonimoExameDAO().merge(aelSinonimoExame);
		this.getAelSinonimoExameDAO().flush();
		
	}
	
	public void validaUnicoSinonimoInserir(AelSinonimoExame aelSinonimoExame) throws ApplicationBusinessException{
		AelSinonimoExame sinonimoExame = this.getAelSinonimoExameDAO().validaUnicoSinonimoInserir(aelSinonimoExame);
		if(sinonimoExame != null){
			throw new ApplicationBusinessException(AelSinonimosExamesCRUDExceptionCode.AEL_00777_2);
		}
	}
	
	public void validaUnicoSinonimoAtualizar(AelSinonimoExame aelSinonimoExame) throws ApplicationBusinessException{
		AelSinonimoExame sinonimoExame = this.getAelSinonimoExameDAO().validaUnicoSinonimoAtualizar(aelSinonimoExame);
		if(sinonimoExame != null){
			throw new ApplicationBusinessException(AelSinonimosExamesCRUDExceptionCode.AEL_00777_2);
		}
	}
	
	/**
	 * ORADB aelk_sie_rn.rn_siep_ver_del_upd
	 * Não permite alterar sinônimo com seqp = 1
	 * @param seqp
	 * @throws ApplicationBusinessException
	 */
	public void validarSinonimoSeqp(Short seqp) throws ApplicationBusinessException {
		if (seqp.equals(Short.valueOf(seqpInvalido))) {
			throw new ApplicationBusinessException (AelSinonimosExamesCRUDExceptionCode.AEL_00357);
		}
	}

	/**
	 * Campos que não podem ser alterados
	 * @param aelSinonimoExame
	 * @throws ApplicationBusinessException
	 */
	public void validarAlteracao(AelSinonimoExame aelSinonimoExame, AelSinonimoExame antigoSinonimoExame) throws ApplicationBusinessException {
				
		if (aelSinonimoExame != null && antigoSinonimoExame != null) {
			
			if (CoreUtil.modificados(aelSinonimoExame.getCriadoEm(), antigoSinonimoExame.getCriadoEm()) || CoreUtil.modificados(aelSinonimoExame.getRapServidor().getId().getMatricula(), antigoSinonimoExame.getRapServidor().getId().getMatricula()) || CoreUtil.modificados(aelSinonimoExame.getRapServidor().getId().getVinCodigo(), antigoSinonimoExame.getRapServidor().getId().getVinCodigo())) {
				throw new ApplicationBusinessException (AelSinonimosExamesCRUDExceptionCode.AEL_00369);
			}
		}
	}

	public Long pesquisarSinonimosExamesCount(AelExames exame){
		return getAelSinonimoExameDAO().pesquisarSinonimosExamesCount(exame);
	}
	
	public List<AelSinonimoExame> pesquisarSinonimosExames(AelExames exame, Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		return getAelSinonimoExameDAO().pesquisarSinonimosExames(exame, firstResult, maxResult, orderProperty, asc);
	}
	
	protected AelSinonimoExameDAO getAelSinonimoExameDAO() {
		return aelSinonimoExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
