package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.AghProfissionaisEquipeId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ManterEquipesPorProfissionalRN extends BaseBusiness {
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private static final Log LOG = LogFactory.getLog(ManterEquipesPorProfissionalRN.class);
	
	private static final long serialVersionUID = -8563985508134574459L;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public void persistirEquipePorProfissional(RapServidores servidorSelecionado, AghEquipes equipe) {
		AghProfissionaisEquipeId idEquipePorProfissional = new AghProfissionaisEquipeId();
		idEquipePorProfissional.setEqpSeq(equipe.getSeq());
		idEquipePorProfissional.setSerMatricula(servidorSelecionado.getId().getMatricula());
		idEquipePorProfissional.setSerVinCodigo(servidorSelecionado.getId().getVinCodigo());
		
		AghProfissionaisEquipe equipePorProfissional = new AghProfissionaisEquipe();
		equipePorProfissional.setServidor(servidorSelecionado);
		equipePorProfissional.setId(idEquipePorProfissional);
		equipePorProfissional.setEquipe(equipe);
		
		getAghuFacade().persistirAghProfissionalEquipe(equipePorProfissional);
	}
	
	public void excluirEquipePorProfissional(AghProfissionaisEquipeId equipePorProfissionalId) {
		getAghuFacade().removerAghProfissionaisEquipe(getAghuFacade().obterAghProfissionaisEquipePorChavePrimaria(equipePorProfissionalId));
	}
	
	public List<AghProfissionaisEquipe> pesquisarListaEquipesPorProfissionalPorServidor(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, RapServidores servidor) {
		return getAghuFacade().pesquisarEquipesPorProfissionalPaginado(firstResult, maxResult, orderProperty, asc, servidor);
	}
	
	public Long pesquisarListaEquipesPorProfissionalPorServidorCount(RapServidores servidor) {
		return getAghuFacade().countPesquisarEquipesPorProfissionalPaginado(servidor);
	}
	
	public List<AghEquipes> pesquisarListaAghEquipes(String filtro) {
		return getAghuFacade().pesquisarListaEquipes(filtro);
	}

	public Long pesquisarListaAghEquipesCount(String filtro) {
		return getAghuFacade().pesquisarListaEquipesCount(filtro);
	}
	
	public IAghuFacade getAghuFacade(){
		return aghuFacade; 
	}
	
}
