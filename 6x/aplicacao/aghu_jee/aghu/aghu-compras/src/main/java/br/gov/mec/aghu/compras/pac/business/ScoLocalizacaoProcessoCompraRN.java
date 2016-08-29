package br.gov.mec.aghu.compras.pac.business;

import java.util.Date;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Serviço de Localizacao 
 * 
 * Responsável pelas regras de negócio da Localizacao da solicitacao.
 * 
 * @author rpanassolo
 */

@Stateless
public class ScoLocalizacaoProcessoCompraRN extends BaseBusiness {
	private static final long serialVersionUID = 7473022992282148283L;
	
	private static final Log LOG = LogFactory.getLog(ScoLocalizacaoProcessoCompraRN.class);
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	public Boolean validarCamposObrigatorioLocalizacao(Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac,
			Short complemento, ScoModalidadeLicitacao modalidadeCompra, Integer nroAF,
			Date dtEntrada, RapServidores servidorResponsavel) throws ApplicationBusinessException {
			if(protocolo!=null){
				return Boolean.TRUE;
			}
			if(local!=null){
				return Boolean.TRUE;
			}
			if(nroPac!=null){
				return Boolean.TRUE;
			}
			if(complemento!=null){
				return Boolean.TRUE;
			}
			if(modalidadeCompra!=null){
				return Boolean.TRUE;
			}
			if(nroAF!=null){
				return Boolean.TRUE;
			}
			if(dtEntrada!=null){
				return Boolean.TRUE;
			}
			if(servidorResponsavel!=null){
				return Boolean.TRUE;
			}
			ExceptionCode.MENSAGEM_LOCALIZACAO_PROCESSO_FILTRO_PESQUISA
					.throwException();
			return Boolean.FALSE;
		
	}	
	
	
	public enum ExceptionCode implements BusinessExceptionCode {
		MENSAGEM_LOCALIZACAO_PROCESSO_FILTRO_PESQUISA;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
}
