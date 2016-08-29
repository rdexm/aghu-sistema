package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.MpmTextoPadraoParecer;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTextoPadraoParecerDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmTextoPadraoParecerON extends BaseBusiness{

	private static final long serialVersionUID = -102977986710675538L;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	@Inject
	private MpmTextoPadraoParecerDAO mpmTextoPadraoParecerDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	public enum MpmTextoPadraoParecerRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MPM_TEXTO_PADRAO_PARECER, MS01;
	}
	
	public void adicionarMpmTextoPadraoParecer(String sigla, String descricao) throws BaseException{
		MpmTextoPadraoParecer mpmTextoPadraoParecer = mpmTextoPadraoParecerDAO.obterOriginal(sigla);
		if(mpmTextoPadraoParecer == null){
			mpmTextoPadraoParecer = new MpmTextoPadraoParecer(sigla.trim(), descricao.trim());
			mpmTextoPadraoParecerDAO.persistir(mpmTextoPadraoParecer);
		}
	}
	
	public void removerMpmTextoPadraoParecer(MpmTextoPadraoParecer mpmTextoPadraoParecer) throws BaseException{
		MpmTextoPadraoParecer entidade = mpmTextoPadraoParecerDAO.obterPorChavePrimaria(mpmTextoPadraoParecer.getSigla());
		mpmTextoPadraoParecerDAO.remover(entidade);
		mpmTextoPadraoParecerDAO.flush();
	}
	
	public void editarMpmTextoPadraoParecer(MpmTextoPadraoParecer mpmTextoPadraoParecer, String siglaNova, String descricao) throws BaseException{
		removerMpmTextoPadraoParecer(mpmTextoPadraoParecer);
		adicionarMpmTextoPadraoParecer(siglaNova, descricao);			
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
