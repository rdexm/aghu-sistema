

package br.gov.mec.aghu.compras.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoDireitoAutorizacaoTempDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoParadaServidorDAO;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoPontoServidorId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author aghu
 * 
 */
@Stateless
public class ManterPontoParadaServidorON extends BaseBusiness implements Serializable {

	private static final Log LOG = LogFactory.getLog(ManterPontoParadaServidorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private ScoPontoParadaServidorDAO scoPontoParadaServidorDAO;

	@Inject
	private ScoDireitoAutorizacaoTempDAO scoDireitoAutorizacaoTempDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = 2672651430122798649L;

	public enum ManterPontoParadaServidorONExceptionCode implements BusinessExceptionCode{
		ERRO_GRAVACAO_REGISTRO_DUPLICADO,ERRO_DEPENDENCIA_DADOS;
	}
	
	/**
	 * Retorna instÃ¢ncia de ScoPontoParadaServidorDAO
	 * 
	 * @return
	 */
	protected ScoPontoParadaServidorDAO getScoPontoParadaServidorDAO() {
		return scoPontoParadaServidorDAO;
	}

	protected ScoDireitoAutorizacaoTempDAO getScoDireitoAutorizacaoTempDAO() {
		return scoDireitoAutorizacaoTempDAO;
	}

	/**
	 * Insere novo registro de ponto de parada de servidor
	 * 
	 * @param entity
	 * @throws BaseListException 
	 */
	public void inserirPontoParadaServidor(ScoPontoServidor entity) throws BaseListException {
		BaseListException listaExcept = new BaseListException();
		// verifica se ja existe
		if(verificarPontoParadaServidorExistente(entity)) {
			listaExcept.add(new ApplicationBusinessException(ManterPontoParadaServidorONExceptionCode.ERRO_GRAVACAO_REGISTRO_DUPLICADO, new Object[]{}));
		}
		if(listaExcept.hasException()){
			throw listaExcept;
		}
		getScoPontoParadaServidorDAO().persistir(entity);
	}

	/**
	 * Remove registro
	 * 
	 * @param entity
	 */
	public void removerPontoParadaServidor(ScoPontoServidorId idPonto) throws ApplicationBusinessException{
		ScoPontoServidor entity = getScoPontoParadaServidorDAO().obterPorChavePrimaria(idPonto);
		
		if (entity == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		if (this.getScoDireitoAutorizacaoTempDAO().pesquisarScoDireitoAutorizacaoTempCount(entity, null) > 0) {
			throw new ApplicationBusinessException(
					ManterPontoParadaServidorONExceptionCode.ERRO_DEPENDENCIA_DADOS);
		}
		
		getScoPontoParadaServidorDAO().remover(entity);
	}

	/**
	 * 
	 * @param pontoServidor
	 * @return
	 */
	private boolean verificarPontoParadaServidorExistente(ScoPontoServidor pontoServidor) {
		List<ScoPontoServidor> resultado = null;
		if(pontoServidor != null) {
			resultado = getScoPontoParadaServidorDAO().pesquisarPontoParadaServidorCodigoMatriculaVinculo(0,100,null,false,
			    																						  pontoServidor.getId().getCodigoPontoParadaSolicitacao(), 
																										  pontoServidor.getServidor().getId().getMatricula(), 
																										  pontoServidor.getServidor().getId().getVinCodigo());
		}
		if(resultado != null && resultado.size() > 0) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

}