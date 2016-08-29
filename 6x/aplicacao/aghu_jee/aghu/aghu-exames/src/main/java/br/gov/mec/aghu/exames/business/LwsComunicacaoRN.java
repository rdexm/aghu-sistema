package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioLwsTipoComunicacao;
import br.gov.mec.aghu.dominio.DominioLwsTipoStatusTransacao;
import br.gov.mec.aghu.exames.dao.LwsComunicacaoDAO;
import br.gov.mec.aghu.model.LwsComunicacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class LwsComunicacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(LwsComunicacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private LwsComunicacaoDAO lwsComunicacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5709321355634749453L;

	
	/*
	 * Métodos para Inserir LwsComunicacao
	 */

	/**
	 * ORADB TRIGGER LWST_LWF_BRI (INSERT)
	 * @param lwsComunicacao
	 */
	private void preInserir(LwsComunicacao lwsComunicacao) throws BaseException {
		
		if(lwsComunicacao != null 
				&& DominioLwsTipoComunicacao.PROPOSTA_PEDIDO_CARGA_EXAMES.equals(lwsComunicacao.getTipoComunicacao())
				&& DominioLwsTipoStatusTransacao.NAO_PROCESSADA.equals(lwsComunicacao.getStatus())
				&& Short.valueOf("99").equals(lwsComunicacao.getIdOrigem())
				&& Short.valueOf("90").equals(lwsComunicacao.getIdDestino())){
			lwsComunicacao.setStatus(DominioLwsTipoStatusTransacao.PROCESSADA);
		}
		
	}
	
	/**
	 * Persiste LwsComunicacao
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	public void persistir(LwsComunicacao lwsComunicacao) throws BaseException {
		if(lwsComunicacao.getSeqComunicacao() != null){
			this.atualizar(lwsComunicacao);
		} else{
			this.inserir(lwsComunicacao);
		}
	}
	

	/**
	 * Inserir LwsComunicacao
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	public void inserir(LwsComunicacao lwsComunicacao) throws BaseException {
		this.preInserir(lwsComunicacao);
		this.getLwsComunicacaoDAO().persistir(lwsComunicacao);
	}
	
	/**
	 * Atualizar LwsComunicacao
	 * @param lwsComunicacao
	 * @throws BaseException
	 */
	public void atualizar(LwsComunicacao lwsComunicacao) throws BaseException {
		this.getLwsComunicacaoDAO().merge(lwsComunicacao);
	}
	

	/**
	 * Getters para RNs e DAOs
	 */
	
	protected LwsComunicacaoDAO getLwsComunicacaoDAO(){
		return lwsComunicacaoDAO;
	}
	
}