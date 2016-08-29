package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoLocalizacaoProcessoON extends BaseBusiness {

@EJB
private ScoLocalizacaoProcessoRN scoLocalizacaoProcessoRN;

private static final Log LOG = LogFactory.getLog(ScoLocalizacaoProcessoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = 3503634542478784864L;

	public enum ScoLocalizacaoProcessoONExceptionCode implements
			BusinessExceptionCode { MENSAGEM_PARAM_OBRIG; }

	/**
	 * Insere local do processo pelo código
	 * @param ScoLocalizacaoProcesso
	 * @author dilceia.alves
	 * @since 18/01/2013
	 * @throws ApplicationBusinessException 
	 */
	public void inserirLocalizacaoProcesso(ScoLocalizacaoProcesso scoLocalizacaoProcesso)
			throws ApplicationBusinessException {

		if (scoLocalizacaoProcesso == null) {
			throw new ApplicationBusinessException(ScoLocalizacaoProcessoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoLocalizacaoProcessoRN().persistir(scoLocalizacaoProcesso);
	}

	/**
	 * Altera local do processo pelo código
	 * @param ScoLocalizacaoProcesso
	 * @author dilceia.alves
	 * @since 18/01/2013
	 * @throws ApplicationBusinessException 
	 */
	public void alterarLocalizacaoProcesso(ScoLocalizacaoProcesso scoLocalizacaoProcesso)
			throws ApplicationBusinessException {

		if (scoLocalizacaoProcesso == null) {
			throw new ApplicationBusinessException(ScoLocalizacaoProcessoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoLocalizacaoProcessoRN().atualizar(scoLocalizacaoProcesso);
	}

	/**
	 * Exclui local do processo pelo código
	 */
	public void excluirLocalizacaoProcesso(Short codigo) throws ApplicationBusinessException {
		this.getScoLocalizacaoProcessoRN().remover(codigo);
	}
	
	protected ScoLocalizacaoProcessoRN getScoLocalizacaoProcessoRN(){
		return scoLocalizacaoProcessoRN;
	}
}