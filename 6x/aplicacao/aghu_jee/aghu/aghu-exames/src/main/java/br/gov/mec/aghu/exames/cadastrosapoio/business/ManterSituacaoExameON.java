package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterSituacaoExameON extends BaseBusiness {


	@EJB
	private ManterSituacaoExameRN manterSituacaoExameRN;
	
	private static final Log LOG = LogFactory.getLog(ManterSituacaoExameON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4890522564650077262L;

	public AelSitItemSolicitacoes persistirSituacaoExame(AelSitItemSolicitacoes situacao) throws ApplicationBusinessException {
		AelSitItemSolicitacoes retorno = null;
		
		if(situacao.getCriadoEm() == null) {
			//Realiza inserção
			retorno = getManterSituacaoExameRN().inserir(situacao);
		} else {
			//Realiza atualização
			retorno = getManterSituacaoExameRN().atualizar(situacao);
		}
		
		return retorno;
	}
	
	public List<AelSitItemSolicitacoes> listarSituacoesExame(String texto) {
		//Primeiro busca por chave-primária
		AelSitItemSolicitacoes elemento = new AelSitItemSolicitacoes();
		elemento.setIndSituacao(DominioSituacao.A);
		elemento.setCodigo(texto);
		List<AelSitItemSolicitacoes> resultado = getAelSitItemSolicitacoesDAO().listar(elemento);
		
		//Se nada foi encontrado, procura pela descrição
		if(resultado.isEmpty()) {
			elemento = new AelSitItemSolicitacoes();
			elemento.setIndSituacao(DominioSituacao.A);
			elemento.setDescricao(texto);
			resultado = getAelSitItemSolicitacoesDAO().listar(elemento);
		}
		
		return resultado;
	}
	
	//--------------------------------------------------
	//Getters
	
	protected ManterSituacaoExameRN getManterSituacaoExameRN() {
		return manterSituacaoExameRN;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	public AelSitItemSolicitacoes ativarDesativarSituacaoExame(AelSitItemSolicitacoes situacao, boolean ativar) throws ApplicationBusinessException {
		AelSitItemSolicitacoes entity = getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(situacao.getCodigo());
		
		if (ativar) {
			entity.setIndSituacao(DominioSituacao.A);
		} else {
			entity.setIndSituacao(DominioSituacao.I);
		}
		
		return persistirSituacaoExame(entity);
	}
	
	
}