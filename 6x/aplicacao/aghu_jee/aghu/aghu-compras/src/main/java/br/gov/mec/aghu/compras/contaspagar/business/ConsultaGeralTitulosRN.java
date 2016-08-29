package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloSolicitacoesDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.ConsultaGeralTituloVO;
import br.gov.mec.aghu.model.FcpTitulo;
import br.gov.mec.aghu.model.FcpTituloSolicitacoes;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Classe responsável por prover os metodos de negócio genéricos para a entidade
 * de Título.
 *
 */
@Stateless
public class ConsultaGeralTitulosRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8347455837466718892L;

	private static final Log LOG = LogFactory.getLog(ConsultaGeralTitulosRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/** Injeção do objeto de título da camada de dados */
	@Inject
	private FcpTituloDAO tituloDAO;
	
	@Inject
	private FcpTituloSolicitacoesDAO tituloSolicitacoesDAO;


//	private enum ConsultarTituloRNException implements BusinessExceptionCode {
//		DATA_PAGAMENTO_INVALIDA,;
//	
//	}
	
	private void excluirTituloSolicitacoes(FcpTitulo titulo){
		
		List<FcpTituloSolicitacoes> listaTitulosSolicitacoes = tituloSolicitacoesDAO.listarTitulosPorTtlSeq(titulo.getSeq());
		tituloDAO.atualizar(titulo);
		for(FcpTituloSolicitacoes item:listaTitulosSolicitacoes){
			desvincularTituloSotlicitacao(item, titulo);
			tituloSolicitacoesDAO.remover(tituloSolicitacoesDAO.obterPorChavePrimaria(item.getSeq()));
		}
	}
	
	public void desvincularTituloSotlicitacao(FcpTituloSolicitacoes item,FcpTitulo titulo){

		if(item.getTtlSeq()!=null && titulo.getSeq().equals(item.getTtlSeq())){
			item.setTitulo(null);
			tituloSolicitacoesDAO.atualizar(item);
		}
	}
	
	public void excluirTitulo(ConsultaGeralTituloVO item){
		FcpTitulo titulo = tituloDAO.obterPorChavePrimaria(item.getTtlSeq());
		excluirTituloSolicitacoes(titulo);
		tituloDAO.remover(tituloDAO.obterPorChavePrimaria(item.getTtlSeq()));
	}
	
}
