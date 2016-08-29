package br.gov.mec.aghu.compras.parecer.business;


import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParecerAvaliacaoDAO;
import br.gov.mec.aghu.model.ScoParecerAvalConsul;
import br.gov.mec.aghu.model.ScoParecerAvalDesemp;
import br.gov.mec.aghu.model.ScoParecerAvalTecnica;
import br.gov.mec.aghu.model.ScoParecerAvaliacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.RapServidores;

@Stateless
public class ScoParecerAvaliacaoON extends BaseBusiness {
	
	@EJB
	private ScoParecerAvalTecnicaON scoParecerAvalTecnicaON;
	@EJB
	private ScoParecerAvalDesempON scoParecerAvalDesempON;
	@EJB
	private ScoParecerAvalConsulON scoParecerAvalConsulON;
	
	private static final Log LOG = LogFactory.getLog(ScoParecerAvaliacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoParecerAvaliacaoDAO scoParecerAvaliacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7518477434707318927L;
	
	public enum ScoParecerAvaliacaoONExceptionCode implements
			BusinessExceptionCode {

		MENSAGEM_PARAM_OBRIG;
	}

	public void persistirParecerAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao, ScoParecerAvalTecnica scoParecerAvalTecnica, 
                                          ScoParecerAvalConsul scoParecerAvalConsul, ScoParecerAvalDesemp scoParecerAvalDesemp) throws ApplicationBusinessException{
		
		if (scoParecerAvaliacao == null) {
			throw new ApplicationBusinessException(
					ScoParecerAvaliacaoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		if (scoParecerAvaliacao.getCodigo() == null){
			inserirParecerAvaliacao(scoParecerAvaliacao, scoParecerAvalTecnica, scoParecerAvalConsul, scoParecerAvalDesemp);
		}
		else {
			alterarParecerAvaliacao(scoParecerAvaliacao, scoParecerAvalTecnica, scoParecerAvalConsul, scoParecerAvalDesemp);
		}
		
	}
	
	public void inserirParecerAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao, ScoParecerAvalTecnica scoParecerAvalTecnica, 
			                            ScoParecerAvalConsul scoParecerAvalConsul, ScoParecerAvalDesemp scoParecerAvalDesemp) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
		scoParecerAvaliacao.setDtCriacao(new Date());
		scoParecerAvaliacao.setDtAlteracao(new Date());
		scoParecerAvaliacao.setServidorCriacao(servidorLogado);
		scoParecerAvaliacao.setServidorAlteracao(servidorLogado);
		this.getScoParecerAvaliacaoDAO().persistir(scoParecerAvaliacao);
        this.getScoParecerAvaliacaoDAO().flush();

		scoParecerAvalTecnica.setParecerAvaliacao(scoParecerAvaliacao);
        scoParecerAvalTecnica.setServidorCriacao(servidorLogado);

        scoParecerAvalConsul.setParecerAvaliacao(scoParecerAvaliacao);
        scoParecerAvalConsul.setServidorCriacao(servidorLogado);

        scoParecerAvalDesemp.setParecerAvaliacao(scoParecerAvaliacao);
        scoParecerAvalDesemp.setServidorCriacao(servidorLogado);

        this.gravarAvaliacoes(scoParecerAvalTecnica, scoParecerAvalConsul, scoParecerAvalDesemp);
		
	}
	
	public void alterarParecerAvaliacao(ScoParecerAvaliacao scoParecerAvaliacao, ScoParecerAvalTecnica scoParecerAvalTecnica, 
			                            ScoParecerAvalConsul scoParecerAvalConsul, ScoParecerAvalDesemp scoParecerAvalDesemp) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		scoParecerAvaliacao.setDtAlteracao(new Date());
		scoParecerAvaliacao.setServidorAlteracao(servidorLogado);
		this.getScoParecerAvaliacaoDAO().merge(scoParecerAvaliacao);  //persistir(scoParecerAvaliacao);
		scoParecerAvalTecnica.setParecerAvaliacao(scoParecerAvaliacao);
		if (scoParecerAvalTecnica.getServidorCriacao() == null){
			scoParecerAvalTecnica.setDtCriacao(new Date());
			scoParecerAvalTecnica.setServidorCriacao(servidorLogado);			
		}
		scoParecerAvalConsul.setParecerAvaliacao(scoParecerAvaliacao);
		if (scoParecerAvalConsul.getServidorCriacao() == null){
			scoParecerAvalConsul.setDtCriacao(new Date());
			scoParecerAvalConsul.setServidorCriacao(servidorLogado);			
		}
		
		scoParecerAvalDesemp.setParecerAvaliacao(scoParecerAvaliacao);
		if (scoParecerAvalDesemp.getServidorCriacao() == null){
			scoParecerAvalDesemp.setDtCriacao(new Date());
			scoParecerAvalDesemp.setServidorCriacao(servidorLogado);			
		}
		
		this.gravarAvaliacoes(scoParecerAvalTecnica, scoParecerAvalConsul, scoParecerAvalDesemp);			
		
		
	}	
	
	public void gravarAvaliacoes(ScoParecerAvalTecnica scoParecerAvalTecnica, ScoParecerAvalConsul scoParecerAvalConsul,
			                     ScoParecerAvalDesemp scoParecerAvalDesemp) throws ApplicationBusinessException{
		
		this.getScoParecerAvalTecnicaON().persistirParecerAvaliacaoTecnica(scoParecerAvalTecnica);
		this.getScoParecerAvalConsulON().persistirParecerAvaliacaoConsul(scoParecerAvalConsul);
		this.getScoParecerAvalDesempON().persistirParecerAvaliacaoDesempenho(scoParecerAvalDesemp);
		
	}

	public ScoParecerAvaliacao obterParecerAvaliacaoPorCodigo(Integer codigo) {
		ScoParecerAvaliacao parecerAvaliacao = this.getScoParecerAvaliacaoDAO().obterPorChavePrimaria(codigo);
		if(parecerAvaliacao.getServidorCriacao() != null && parecerAvaliacao.getServidorCriacao().getPessoaFisica() != null ){
			parecerAvaliacao.getServidorCriacao().getPessoaFisica().getNome(); 
		}
		return parecerAvaliacao;
	}

	protected ScoParecerAvaliacaoDAO getScoParecerAvaliacaoDAO() {
		return scoParecerAvaliacaoDAO;
	}	
	
	protected ScoParecerAvalTecnicaON getScoParecerAvalTecnicaON() {
		return scoParecerAvalTecnicaON;
	}	
	
	protected ScoParecerAvalDesempON getScoParecerAvalDesempON() {
		return scoParecerAvalDesempON;
	}	
	
	protected ScoParecerAvalConsulON getScoParecerAvalConsulON() {
		return scoParecerAvalConsulON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}	
}