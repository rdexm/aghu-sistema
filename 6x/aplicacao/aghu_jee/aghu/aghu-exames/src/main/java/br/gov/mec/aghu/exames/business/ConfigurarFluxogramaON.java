package br.gov.mec.aghu.exames.business;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelServidorCampoLaudoDAO;
import br.gov.mec.aghu.exames.vo.ExameDisponivelFluxogramaVO;
import br.gov.mec.aghu.exames.vo.ExameSelecionadoFluxogramaVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelServidorCampoLaudo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConfigurarFluxogramaON  extends BaseBusiness {
	
	@EJB
	private AelServidorCampoLaudoRN aelServidorCampoLaudoRN;
	
	private static final Log LOG = LogFactory.getLog(ConfigurarFluxogramaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;
	
	@Inject
	private AelServidorCampoLaudoDAO aelServidorCampoLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -505498404823415456L;
	
	public enum ConfigurarFluxogramaONExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_SERVIDOR_CAMPO_LAUDO;
	}
	
	/**
	 * Persiste Exames para um fluxograma através do servidor
	 * @param servidor
	 * @param listaExamesSelecionados
	 * @throws BaseException
	 */
	public void persistirExamesFluxogramaSelecionados(RapServidores servidor, List<ExameSelecionadoFluxogramaVO> listaExamesSelecionados) throws BaseException{

		try {
			
			// Remove registros antigos de Especialidade Campo Laudo
			List<AelServidorCampoLaudo> listaServidorCampoLaudoAntigos = this.getAelServidorCampoLaudoDAO().pesquisarServidorCampoLaudoPorServidor(servidor);
			for (AelServidorCampoLaudo servidorCampoLaudo : listaServidorCampoLaudoAntigos) {
				this.getAelServidorCampoLaudoRN().remover(servidorCampoLaudo);
			}
			this.getAelServidorCampoLaudoDAO().flush();
			
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(ConfigurarFluxogramaONExceptionCode.ERRO_PERSISTIR_SERVIDOR_CAMPO_LAUDO);
		}

		try {
			
			// Percorre a lista de Exames Selecionados e persiste os itens em Servidor Campo Laudo
			for (ExameSelecionadoFluxogramaVO vo : listaExamesSelecionados) {
				vo.setServidorCampoLaudo(this.getAelServidorCampoLaudoRN().persistirCampoLaudo(vo.getServidorCampoLaudo()));
			}
			this.getAelServidorCampoLaudoDAO().flush();
			
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(ConfigurarFluxogramaONExceptionCode.ERRO_PERSISTIR_SERVIDOR_CAMPO_LAUDO);
		}

	}
	
	/**
	 * Pesquisa Exames disponíveis para o Fluxograma
	 * @param servidor
	 * @return
	 */
	public List<ExameDisponivelFluxogramaVO> pesquisarExamesDisponiveisFluxograma() {
		
		List<ExameDisponivelFluxogramaVO> retorno = new LinkedList<ExameDisponivelFluxogramaVO>();
		
		// Pesquisa Campos Laudo com Fluxo Ativo
		List<AelCampoLaudo> listaCampoLaudosFluxoAtivo = this.getAelCampoLaudoDAO().pesquisarCampoLaudoFluxoAtivo();
		
		for (AelCampoLaudo campoLaudo : listaCampoLaudosFluxoAtivo) {
			
			ExameDisponivelFluxogramaVO vo = new ExameDisponivelFluxogramaVO();
			
			vo.setCampoLaudo(campoLaudo);
			vo.setSelecionado(false);
			
			retorno.add(vo);
		}
		
		return retorno;
		
	}
	
	public boolean pertenceAoFluxograma(final Integer campoLaudoSeq){
		AelCampoLaudo cLaudoOri = this.getAelCampoLaudoDAO().obterPorChavePrimaria(campoLaudoSeq);
		return cLaudoOri != null && cLaudoOri.getFluxo();
	}
	
	/**
	 * Pesquisa Exames Selecionados através do servidor logado
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public List<ExameSelecionadoFluxogramaVO> pesquisarExamesSelecionadosPorServidorLogado() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<ExameSelecionadoFluxogramaVO> retorno = new LinkedList<ExameSelecionadoFluxogramaVO>();

		List<AelServidorCampoLaudo> listaServidorCampoLaudo = this.getAelServidorCampoLaudoDAO().pesquisarServidorCampoLaudoPorServidor(servidorLogado);
		
		for (AelServidorCampoLaudo servidorCampoLaudo : listaServidorCampoLaudo) {
			
			ExameSelecionadoFluxogramaVO vo = new ExameSelecionadoFluxogramaVO();
			
			vo.setSeqCampoLaudo(servidorCampoLaudo.getCampoLaudo().getSeq());
			vo.setServidorCampoLaudo(servidorCampoLaudo);
			vo.setSelecionado(false);
			vo.setRemoverLista(false);
			
			retorno.add(vo);
			
		}
		
		return retorno;
	}
	
	/*
	 * Getters para RNs e DAOs
	 */	

	protected AelServidorCampoLaudoRN getAelServidorCampoLaudoRN() {
		return aelServidorCampoLaudoRN;
	}
	
	protected AelCampoLaudoDAO getAelCampoLaudoDAO(){
		return aelCampoLaudoDAO;
	}

	protected AelServidorCampoLaudoDAO getAelServidorCampoLaudoDAO(){
		return aelServidorCampoLaudoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
