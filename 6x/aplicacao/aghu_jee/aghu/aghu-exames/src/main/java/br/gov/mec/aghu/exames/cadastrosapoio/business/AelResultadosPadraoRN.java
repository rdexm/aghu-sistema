package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelResultadosPadraoDAO;
import br.gov.mec.aghu.exames.dao.AelVersaoLaudoDAO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelResultadosPadraoRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(AelResultadosPadraoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelVersaoLaudoDAO aelVersaoLaudoDAO;
	
	@Inject
	private AelResultadosPadraoDAO aelResultadosPadraoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1897725935712974622L;
	
	public enum AelResultadosPadraoRNExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_RESULTADO_PADRAO,AEL_01552,AEL_01553;
	}
	
	/**
	 * Persiste AelResultadosPadrao
	 * @param resultadosPadrao
	 * @throws BaseException
	 */
	public void persistirResultadoPadrao(AelResultadosPadrao resultadoPadrao) throws BaseException{
		
		if (resultadoPadrao.getSeq() == null) {
			this.inserir(resultadoPadrao);
		} else {
			this.atualizar(resultadoPadrao);
		}

		try {
			// Caso nenhum erro ocorra faz o flush das alterações
			this.getAelResultadosPadraoDAO().flush();
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(AelResultadosPadraoRNExceptionCode.ERRO_PERSISTIR_RESULTADO_PADRAO);
		}
	}
	
	/**
	 * ORADB TRIGGER AELT_RPA_BRI (INSERT)
	 * @param resultadosPadrao
	 * @throws BaseException
	 */
	private void preInserir(AelResultadosPadrao resultadoPadrao) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		resultadoPadrao.setCriadoEm(new Date()); // RN1
		resultadoPadrao.setServidor(servidorLogado); // RN2
		this.verificarVersaoExame(resultadoPadrao); // RN3
	}

	/**
	 * Inserir AelResultadosPadrao
	 * @param resultadosPadrao
	 * @throws BaseException
	 */
	public void inserir(AelResultadosPadrao resultadoPadrao) throws BaseException{
		this.preInserir(resultadoPadrao);
		this.getAelResultadosPadraoDAO().persistir(resultadoPadrao);
	}
	

	/**
	 * ORADB TRIGGER AELT_RPA_BRU (UPDATE)
	 * @param resultadosPadrao
	 * @throws BaseException
	 */
	private void preAtualizar(AelResultadosPadrao resultadoPadrao) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		resultadoPadrao.setServidor(servidorLogado); // RN1
		
		// RN3
		final AelResultadosPadrao old = this.getAelResultadosPadraoDAO().obterOriginal(resultadoPadrao.getSeq());
		
		if((old.getExameMaterialAnalise() != null && !old.getExameMaterialAnalise().equals(resultadoPadrao.getExameMaterialAnalise())) 
				|| (resultadoPadrao.getExameMaterialAnalise() != null && !resultadoPadrao.getExameMaterialAnalise().equals(old.getExameMaterialAnalise()))){ 
			this.verificarVersaoExame(resultadoPadrao);
		}
	}
	
	/**
	 * Atualizar AelResultadosPadrao
	 * @param resultadosPadrao
	 * @throws BaseException
	 */
	public void atualizar(AelResultadosPadrao resultadoPadrao) throws BaseException{
		this.preAtualizar(resultadoPadrao);
		this.getAelResultadosPadraoDAO().merge(resultadoPadrao);
	}
	
	/*
	 * RNs Inserir
	 */
	
	/**
	 * ORADB PROCEDURE AELK_RPA_RN.RN_RPAP_VER_EXME_VER
	 * Verifica se o Exame/Material do resultado está ativo e se a Versão da máscara foi criada.
	 * @param campoLaudo
	 */
	protected void verificarVersaoExame(AelResultadosPadrao resultadoPadrao) throws ApplicationBusinessException{
	
		final AelExamesMaterialAnalise examesMaterialAnalise = resultadoPadrao.getExameMaterialAnalise();
		
		if(examesMaterialAnalise != null){
			
			final String exaSigla = examesMaterialAnalise.getId().getExaSigla();
			final Integer manSeq = examesMaterialAnalise.getId().getManSeq();

			// Verifica se o Exame/Material do resultado está ativo
			if(DominioSituacao.I.equals(examesMaterialAnalise.getIndSituacao())){
				throw new ApplicationBusinessException(AelResultadosPadraoRNExceptionCode.AEL_01552);
			} else{

				final List<AelVersaoLaudo> listaVersaoLaudo = this.getAelVersaoLaudoDAO().pesquisarVersaoLaudoPorExameMaterialAnalise(exaSigla, manSeq);
				
				// Verifica se Versões de Máscara foram criadas
				if(listaVersaoLaudo != null && listaVersaoLaudo.isEmpty()){
					throw new ApplicationBusinessException(AelResultadosPadraoRNExceptionCode.AEL_01553);
				}
			}
			
		} 
		
	}
	
	/**
	 * Getters para RNs e DAOs
	 */
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}	
	
	protected AelResultadosPadraoDAO getAelResultadosPadraoDAO() {
		return aelResultadosPadraoDAO;
	}
	
	protected AelVersaoLaudoDAO getAelVersaoLaudoDAO() {
		return aelVersaoLaudoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
