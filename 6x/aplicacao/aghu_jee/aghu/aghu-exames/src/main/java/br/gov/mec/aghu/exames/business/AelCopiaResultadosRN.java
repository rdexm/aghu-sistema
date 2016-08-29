package br.gov.mec.aghu.exames.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.dao.AelCopiaResultadosDAO;
import br.gov.mec.aghu.model.AelCopiaResultados;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class AelCopiaResultadosRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelCopiaResultadosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelCopiaResultadosDAO aelCopiaResultadosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3026407304301660300L;

	public enum AelCopiaResultadosRNExceptionCode implements BusinessExceptionCode {
		AEL_01057, AEL_01058, AEL_00369, AEL_00251,MENSAGEM_ORIGEM_ATENDIMENTO_INVALIDA,NUMERO_COPIAS_CADASTRADA;
	}
	
	/**
	 * ORADB CONSTRAINTS (INSERT/UPDATE)
	 * @param aelCopiaResultados
	 * @throws BaseException
	 */
	protected void executarRestricoes(AelCopiaResultados aelCopiaResultados) throws ApplicationBusinessException{
		
		final Byte numero = aelCopiaResultados.getNumero();
		
		if(numero == null || (numero != null && numero < 0)){
			throw new ApplicationBusinessException (AelCopiaResultadosRNExceptionCode.AEL_00251);
		}
		
		if(!aelCopiaResultados.getId().getOrigemAtendimento().equals(DominioOrigemAtendimento.A) && !aelCopiaResultados.getId().getOrigemAtendimento().equals(DominioOrigemAtendimento.I) && !aelCopiaResultados.getId().getOrigemAtendimento().equals(DominioOrigemAtendimento.U) 
				&& !aelCopiaResultados.getId().getOrigemAtendimento().equals(DominioOrigemAtendimento.X) && !aelCopiaResultados.getId().getOrigemAtendimento().equals(DominioOrigemAtendimento.D) && !aelCopiaResultados.getId().getOrigemAtendimento().equals(DominioOrigemAtendimento.H)
				&& !aelCopiaResultados.getId().getOrigemAtendimento().equals(DominioOrigemAtendimento.T)){
			
			throw new ApplicationBusinessException (AelCopiaResultadosRNExceptionCode.MENSAGEM_ORIGEM_ATENDIMENTO_INVALIDA);
		}
		
		
	}
	
	/**
, 	 * ORADB TRIGGER AELT_CRE_BRI
	 * @param aelCopiaResultados
	 * @throws ApplicationBusinessException  
	 */
	public void inserirAelCopiaResultados(AelCopiaResultados aelCopiaResultados) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.executarRestricoes(aelCopiaResultados);
		
		AelCopiaResultados existe = this.getAelCopiaResultadosDAO().obterPorChavePrimaria(aelCopiaResultados.getId());
		if(existe != null){
			//Número de cópias do resultado do exame já cadastrada.
			throw new ApplicationBusinessException (AelCopiaResultadosRNExceptionCode.NUMERO_COPIAS_CADASTRADA);
		}
		
		this.verificarOrigemAtendimento(aelCopiaResultados);
		aelCopiaResultados.setServidor(servidorLogado);
		aelCopiaResultados.setServidorAlterado(servidorLogado);
		aelCopiaResultados.setCriadoEm(new Date());
		aelCopiaResultados.setAlteradoEm(new Date());
		
		this.getAelCopiaResultadosDAO().persistir(aelCopiaResultados);
		this.getAelCopiaResultadosDAO().flush();
	}
	
	/**
	 * ORADB TRIGGER AELT_CRE_BRU
	 * @param aelCopiaResultados
	 * @throws ApplicationBusinessException  
	 */
	public void atualizarAelCopiaResultados(AelCopiaResultados aelCopiaResultados) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.executarRestricoes(aelCopiaResultados);
		
		AelCopiaResultados elCopiaResultadosOriginal = getAelCopiaResultadosDAO().obterAelCopiaResultadoOriginal(aelCopiaResultados);
		this.validarAlteracao(elCopiaResultadosOriginal, aelCopiaResultados);
		aelCopiaResultados.setServidorAlterado(servidorLogado);
		aelCopiaResultados.setAlteradoEm(new Date());
		
		this.getAelCopiaResultadosDAO().merge(aelCopiaResultados);
		this.getAelCopiaResultadosDAO().flush();
		
	}
	
	/**
	 * ORADB aelk_cre_rn.rn_crep_ver_origem
	 * @param aelCopiaResultados
	 * @throws ApplicationBusinessException
	 */
	protected void verificarOrigemAtendimento(AelCopiaResultados aelCopiaResultados) throws ApplicationBusinessException {


		if (aelCopiaResultados.getId().getOrigemAtendimento().equals(DominioOrigemAtendimento.T)) {
						
			if (getAelCopiaResultadosDAO().existeItem(aelCopiaResultados.getId().getCnvCodigo(), aelCopiaResultados.getExamesMaterialAnalise().getId().getExaSigla(), aelCopiaResultados.getExamesMaterialAnalise().getId().getManSeq())) {
			
				throw new ApplicationBusinessException (AelCopiaResultadosRNExceptionCode.AEL_01057);
			
			}
			
		} else {
			
			if (getAelCopiaResultadosDAO().existeItemOrigemAtendimentoTodos(aelCopiaResultados.getId().getCnvCodigo(),  aelCopiaResultados.getExamesMaterialAnalise().getId().getExaSigla(), aelCopiaResultados.getExamesMaterialAnalise().getId().getManSeq())) {
				
				throw new ApplicationBusinessException (AelCopiaResultadosRNExceptionCode.AEL_01058);
			
			}
			
		}
		
	}
	
	/**
	 * Remove AelCopiaResultados
	 * @param aelCopiaResultados
	 * @throws BaseException
	 */
	public void remover(AelCopiaResultados aelCopiaResultados) throws BaseException{
		aelCopiaResultados = this.getAelCopiaResultadosDAO().obterPorChavePrimaria(aelCopiaResultados.getId());
		this.getAelCopiaResultadosDAO().remover(aelCopiaResultados);
		this.getAelCopiaResultadosDAO().flush();
	}
	
	
	/**
	 * Valida dados alterados
	 * @param aelCopiaResultados
	 * @param novoAelCopiaResultados
	 * @throws ApplicationBusinessException 
	 */
	protected void validarAlteracao(AelCopiaResultados aelCopiaResultados, AelCopiaResultados novoAelCopiaResultados) throws ApplicationBusinessException {
		
		if (aelCopiaResultados != null && novoAelCopiaResultados != null) {
			
			if (CoreUtil.modificados(aelCopiaResultados.getCriadoEm(), novoAelCopiaResultados.getCriadoEm()) || CoreUtil.modificados(aelCopiaResultados.getServidor().getId().getMatricula(), novoAelCopiaResultados.getServidor().getId().getMatricula()) || CoreUtil.modificados(aelCopiaResultados.getServidor().getId().getVinCodigo(), novoAelCopiaResultados.getServidor().getId().getVinCodigo())) {
				
				throw new ApplicationBusinessException (AelCopiaResultadosRNExceptionCode.AEL_00369);
				
			}
				
		}
		
	}

	protected AelCopiaResultadosDAO getAelCopiaResultadosDAO() {
		return aelCopiaResultadosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
