package br.gov.mec.aghu.administracao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.dao.AghCaractMicrocomputadorDAO;
import br.gov.mec.aghu.administracao.dao.AghMicrocomputadorDAO;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.model.AghCaractMicrocomputador;
import br.gov.mec.aghu.model.AghCaractMicrocomputadorId;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException.BaseOptimisticLockExceptionCode;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MicrocomputadorRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MicrocomputadorRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AghMicrocomputadorDAO aghMicrocomputadorDAO;
	
	@Inject
	private AghCaractMicrocomputadorDAO aghCaractMicrocomputadorDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3840784200794870975L;
	
	public enum MicrocomputadorRNExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUIR_CARACT_MICROCOMPUTADOR, ERRO_INCLUIR_CARACT_MICROCOMPUTADOR, MSG_MICROCOMPUTADOR_EXISTENTE, MSG_MICROCOMPUTADOR_IP_EXISTENTE, MSG_MICROCOMPUTADOR_PONTO_EXISTENTE;
	}

	/**
	 * ORADB 
	 * 
	 * Verifica se o microcomputador tem referência em outras tabelas antes de deletar.
	 * As verificações nas tabelas: AGH_MICRO_SOFTWARES, AGH_MICRO_GRUPOS, AGH_CONEXOES
	 * e AGH_MICRO_APLICS não foram implementadas. Serão implementadas de acordo com
	 * necessidades futuras.
	 * 
	 * @param nomeMicromputador
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public void excluirMicrocomputador(String nomeMicrocomputador) throws ApplicationBusinessException {
		AghMicrocomputadorDAO dao = getAghMicrocomputadorDAO();
		
		Boolean existeCaractMicrocomputador = getAghCaractMicrocomputadorDAO().existeCaractMicrocomputadorPorMicroNome(nomeMicrocomputador);
		
		if(existeCaractMicrocomputador) {
			throw new ApplicationBusinessException(MicrocomputadorRNExceptionCode.ERRO_EXCLUIR_CARACT_MICROCOMPUTADOR);
		}
		else {
			AghMicrocomputador microcomputador = getAghMicrocomputadorDAO().obterPorChavePrimaria(nomeMicrocomputador);
			dao.remover(microcomputador);
			dao.flush();
		}
		
	}
	
	/**
	 * ORADB: AGHT_MIC_BRI
	 * @throws BaseException
	 */
	public void persistirMicrocomputador(AghMicrocomputador microcomputador) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		microcomputador.setServidor(servidorLogado);
		microcomputador.setCriadoEm(new Date());
		
		// O valor default para a coluna VERSAO_AGH é 'AGH_6.0' (tabela AGH_MICROCOMPUTADORES). 
		// Esta coluna é nullable false.
		microcomputador.setVersaoAgh("AGH_6.0");
		validaDadosInformadosMicroComputador(microcomputador);
		this.getAghMicrocomputadorDAO().persistir(microcomputador);
		this.getAghMicrocomputadorDAO().flush();
	
	}
	
	/**
	 * ORADB: AGHT_MIC_BRU
	 * @throws BaseException
	 */
	public void atualizarMicrocomputador(AghMicrocomputador microcomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		microcomputador.setServidor(servidorLogado);
		validaComputadorAtualizado(microcomputador);
		this.getAghMicrocomputadorDAO().merge(microcomputador);
		this.getAghMicrocomputadorDAO().flush();
	}

	/**
	 * ORADB: AGHT_CMI_BRI
	 */
	public void persistirCaracteristicaMicrocomputador(AghMicrocomputador microcomputador,
			DominioCaracteristicaMicrocomputador dominioCaracteristica) throws BaseException {
		try{
			if (this.getAghMicrocomputadorDAO().verificarCaracteristicaMicrocomputador(microcomputador, dominioCaracteristica)) {
				throw new ApplicationBusinessException(MicrocomputadorRNExceptionCode.ERRO_INCLUIR_CARACT_MICROCOMPUTADOR); 
			}
			
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AghCaractMicrocomputador caracteristica = new AghCaractMicrocomputador();
			AghCaractMicrocomputadorId caracteristicaId = new AghCaractMicrocomputadorId();
			
			caracteristicaId.setMicNome(microcomputador.getNome());
			caracteristicaId.setCaracteristica(dominioCaracteristica);
			
			caracteristica.setId(caracteristicaId);
			caracteristica.setMicrocomputador(microcomputador);
			caracteristica.setServidor(servidorLogado);
			caracteristica.setCriadoEm(new Date());
			
			this.getAghCaractMicrocomputadorDAO().persistir(caracteristica);
			getAghCaractMicrocomputadorDAO().flush();
		} catch (PersistenceException e){
			throw new BaseOptimisticLockException(BaseOptimisticLockExceptionCode.OPTIMISTIC_LOCK);    	
		}
	}
	
	/**
	 * Excluir Caracteristica do Microcomputador
	 */
	public void excluirCaracteristica(AghCaractMicrocomputadorId caracteristicaId) throws ApplicationBusinessException {
		AghCaractMicrocomputador caracteristica = this.getAghCaractMicrocomputadorDAO().obterPorChavePrimaria(caracteristicaId);
		this.getAghCaractMicrocomputadorDAO().remover(caracteristica);
		this.getAghCaractMicrocomputadorDAO().flush();
	}
	
	public void validaDadosInformadosMicroComputador(AghMicrocomputador microcomputador) throws ApplicationBusinessException {
		
		   if  (validaCadastroMicrocomputador(microcomputador)) {
			   throw new ApplicationBusinessException(MicrocomputadorRNExceptionCode.MSG_MICROCOMPUTADOR_EXISTENTE);
		   }
		   
		   if (validaCadastroIp(microcomputador)) {
				throw new ApplicationBusinessException(MicrocomputadorRNExceptionCode.MSG_MICROCOMPUTADOR_IP_EXISTENTE);
		   }
			
		   if (validaCadastroPonto(microcomputador)) { 
			   throw new ApplicationBusinessException(MicrocomputadorRNExceptionCode.MSG_MICROCOMPUTADOR_PONTO_EXISTENTE);
		   }

	}
	
	public boolean validaCadastroMicrocomputador(AghMicrocomputador microcomputador) throws ApplicationBusinessException {		
		if (!StringUtils.isEmpty(microcomputador.getNome())) {
			return  aghMicrocomputadorDAO.validaDadosInformadosMicrocomputador(microcomputador.getNome(), null, null);
			}
		return  Boolean.FALSE;
		}	
	
	public boolean validaCadastroIp(AghMicrocomputador microcomputador) throws ApplicationBusinessException {		
		if (!StringUtils.isEmpty(microcomputador.getIp())) {
			return aghMicrocomputadorDAO.validaDadosInformadosMicrocomputador(null, microcomputador.getIp(),null);
			}
		return Boolean.FALSE;
		}
	
	public boolean validaCadastroPonto(AghMicrocomputador microcomputador) throws ApplicationBusinessException {		
		if (!StringUtils.isEmpty(microcomputador.getPonto())) {
			return aghMicrocomputadorDAO.validaDadosInformadosMicrocomputador(null, null, microcomputador.getPonto());
			}
		return Boolean.FALSE;
		}
	
	
	public void validaComputadorAtualizado(AghMicrocomputador microcomputador) throws ApplicationBusinessException {	
		
		if (validaCadastroIp(microcomputador) && aghMicrocomputadorDAO.verificaQtdComputador(microcomputador.getNome(), null, microcomputador.getIp()) > 0) {
			throw new ApplicationBusinessException(MicrocomputadorRNExceptionCode.MSG_MICROCOMPUTADOR_IP_EXISTENTE);
		}
		
		if (validaCadastroPonto(microcomputador) && aghMicrocomputadorDAO.verificaQtdComputador(microcomputador.getNome(), microcomputador.getPonto(), null ) > 0) {
			throw new ApplicationBusinessException(MicrocomputadorRNExceptionCode.MSG_MICROCOMPUTADOR_PONTO_EXISTENTE);
		
		}
		
	}
	
	protected AghCaractMicrocomputadorDAO getAghCaractMicrocomputadorDAO() {
		return aghCaractMicrocomputadorDAO;
	}
	
	protected AghMicrocomputadorDAO getAghMicrocomputadorDAO() {
		return aghMicrocomputadorDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
