package br.gov.mec.aghu.exames.business;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.business.ConfigurarFluxogramaON.ConfigurarFluxogramaONExceptionCode;
import br.gov.mec.aghu.exames.dao.AelEspecialidadeCampoLaudoDAO;
import br.gov.mec.aghu.exames.vo.ExameEspecialidadeSelecionadoFluxogramaVO;
import br.gov.mec.aghu.model.AelEspecialidadeCampoLaudo;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ConfigurarFluxogramaEspecialidadeON  extends BaseBusiness {


@EJB
private AelEspecialidadeCampoLaudoRN aelEspecialidadeCampoLaudoRN;

private static final Log LOG = LogFactory.getLog(ConfigurarFluxogramaEspecialidadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelEspecialidadeCampoLaudoDAO aelEspecialidadeCampoLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4411820807048878553L;
	
	/**
	 * Persiste Exames para um fluxograma por Especialidade
	 * @param especialidade
	 * @param listaExamesSelecionados
	 * @param listaExamesRemovidos 
	 * @throws BaseException
	 */
	public void persistirExamesFluxogramaSelecionadosPorEspecialidade(AghEspecialidades especialidade, 
			List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesSelecionados, 
			List<ExameEspecialidadeSelecionadoFluxogramaVO> listaExamesRemovidos) throws BaseException{

		try {
			
			// Remove registros antigos de Servidor Campo Laudo
			for (ExameEspecialidadeSelecionadoFluxogramaVO especialidadeCampoLaudo : listaExamesRemovidos) {
				if (!listaExamesSelecionados.contains(especialidadeCampoLaudo)) {
					AelEspecialidadeCampoLaudo remover = getAelEspecialidadeCampoLaudoDAO().carregarPorChavePrimaria(especialidadeCampoLaudo.getEspecialidadeCampoLaudo().getId());
					this.getAelEspecialidadeCampoLaudoRN().remover(remover);
				}
			}
			this.getAelEspecialidadeCampoLaudoDAO().flush();
			
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(ConfigurarFluxogramaONExceptionCode.ERRO_PERSISTIR_SERVIDOR_CAMPO_LAUDO);
		}

		try {
			
			// Percorre a lista de Exames Selecionados e persiste os itens em Especialidade Campo Laudo
			for (ExameEspecialidadeSelecionadoFluxogramaVO vo : listaExamesSelecionados) {
				this.getAelEspecialidadeCampoLaudoRN().persistirEspecialidadeCampoLaudo(vo.getEspecialidadeCampoLaudo());
			}
			this.getAelEspecialidadeCampoLaudoDAO().flush();
			
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(ConfigurarFluxogramaONExceptionCode.ERRO_PERSISTIR_SERVIDOR_CAMPO_LAUDO);
		}

	}
	
	/**
	 * Pesquisa Exames Especialidade Selecionados através da especialidade
	 * @param servidor
	 * @return
	 */
	public List<ExameEspecialidadeSelecionadoFluxogramaVO> pesquisarExamesEspeciadadeSelecionadosPorEspecialidade(AghEspecialidades especialidade) {
		
		List<ExameEspecialidadeSelecionadoFluxogramaVO> retorno = new LinkedList<ExameEspecialidadeSelecionadoFluxogramaVO>();

		List<AelEspecialidadeCampoLaudo> listaServidorCampoLaudo = this.getAelEspecialidadeCampoLaudoDAO().pesquisarEspecialidadeCampoLaudoPorEspecialidade(especialidade.getSeq());
		
		for (AelEspecialidadeCampoLaudo especialidadeCampoLaudo : listaServidorCampoLaudo) {
			
			ExameEspecialidadeSelecionadoFluxogramaVO vo = new ExameEspecialidadeSelecionadoFluxogramaVO();
			
			vo.setSeqCampoLaudo(especialidadeCampoLaudo.getCampoLaudo().getSeq());
			vo.setEspecialidadeCampoLaudo(especialidadeCampoLaudo);
			vo.setSelecionado(false);
			
			retorno.add(vo);
			
		}
		
		return retorno;
	}
	
	/*
	 * Getters para RNs e DAOs
	 */	

	protected AelEspecialidadeCampoLaudoRN getAelEspecialidadeCampoLaudoRN() {
		return aelEspecialidadeCampoLaudoRN;
	}
	
	protected AelEspecialidadeCampoLaudoDAO getAelEspecialidadeCampoLaudoDAO(){
		return aelEspecialidadeCampoLaudoDAO;
	}
	
}
