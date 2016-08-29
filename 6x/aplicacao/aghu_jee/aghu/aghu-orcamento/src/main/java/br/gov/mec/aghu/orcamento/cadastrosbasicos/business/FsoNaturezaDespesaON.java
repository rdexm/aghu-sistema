package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.orcamento.dao.FsoNaturezaDespesaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @author amenegotto
 *
 */
@Stateless
public class FsoNaturezaDespesaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(FsoNaturezaDespesaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@Inject
	private FsoNaturezaDespesaDAO fsoNaturezaDespesaDAO;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	private static final long serialVersionUID = -7130595457653656617L;

	public enum FsoNaturezaDespesaONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_NATUREZA_DESPESA_M03, MENSAGEM_NATUREZA_DESPESA_M04, MENSAGEM_NATUREZA_DESPESA_M05, 
		MENSAGEM_NATUREZA_DESPESA_M09, MENSAGEM_NATUREZA_DESPESA_M10, MENSAGEM_NATUREZA_DESPESA_M11,
		MENSAGEM_NATUREZA_DESPESA_M12,MENSAGEM_NATUREZA_DESPESA_M13;
	}

	/**
	 * Retorna uma lista de naturezas de despesa por grupo de natureza, descricao ou situacao
	 */
	public List<FsoNaturezaDespesa> pesquisarListaNaturezaDespesa(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FsoGrupoNaturezaDespesa grupoNatureza,
			String descricaoNatureza, DominioSituacao indSituacao) {
		return this.getFsoNaturezaDespesaDAO().pesquisarListaNaturezaDespesa(
				firstResult, maxResults, orderProperty, asc, grupoNatureza,
				descricaoNatureza, indSituacao);
	}

	/**
	 * Retorna a quantidade natureza de despesa por grupo de natureza, descricao ou situacao  
	 */
	public Long countPesquisaListaNaturezaDespesa(
			FsoGrupoNaturezaDespesa grupoNatureza, String descricaoNatureza, DominioSituacao indSituacao) {
		return this.getFsoNaturezaDespesaDAO()
				.countPesquisaListaNaturezaDespesa(grupoNatureza,
						descricaoNatureza, indSituacao);
	}

	/**
	 * Insere a natureza de despesa
	 * @param naturezaDespesa
	 * @throws ApplicationBusinessException
	 */
	public void inserirNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa)
			throws ApplicationBusinessException {

		if (naturezaDespesa.getGrupoNaturezaDespesa().getIndSituacao() == DominioSituacao.I
				&& naturezaDespesa.getIndSituacao() == DominioSituacao.A) {
			throw new ApplicationBusinessException(
					FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M03);
		}

		if (this.getFsoNaturezaDespesaDAO()
				.verificarNaturezaDespesaEmGrupoNatureza(
						naturezaDespesa.getGrupoNaturezaDespesa(),
						naturezaDespesa.getId()) > 0) {
			throw new ApplicationBusinessException(
					FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M05);
		}

		this.getFsoNaturezaDespesaDAO().persistir(naturezaDespesa);
	}

	/**
	 * Altera a natureza de despesa
	 * @param naturezaDespesa
	 * @throws ApplicationBusinessException
	 */
	public void alterarNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa)
			throws ApplicationBusinessException {

		List<ScoSolicitacaoDeCompra> listaSolCompras = this.getSolicitacaoComprasFacade().
				buscarSolicitacaoCompraAssociadaAutorizacaoFornEfetivada(naturezaDespesa.getId());
				
		List<ScoSolicitacaoServico> listaSolServico = this.getSolicitacaoServicoFacade().
				buscarSolicitacaoServicoAssociadaAutorizacaoFornEfetivada(naturezaDespesa.getId()); 
		
		FsoNaturezaDespesa naturezaDespesaOriginal = this
				.getFsoNaturezaDespesaDAO().obterOriginal(naturezaDespesa);

		if (naturezaDespesaOriginal.getIndSituacao() == DominioSituacao.A
				&& naturezaDespesa.getIndSituacao() == DominioSituacao.I
				&& listaSolCompras.size() > 0) {
			throw new ApplicationBusinessException(
					FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M12, this.obterListaSolicitacoesComprasAssociadas(listaSolCompras));
		}
		
		if (naturezaDespesaOriginal.getIndSituacao() == DominioSituacao.A
				&& naturezaDespesa.getIndSituacao() == DominioSituacao.I
				&& listaSolServico.size() > 0) {
			throw new ApplicationBusinessException(
					FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M13, this.obterListaSolicitacoesServicoAssociadas(listaSolServico));
		}
		
		if (naturezaDespesaOriginal.getIndSituacao() == DominioSituacao.I
				&& naturezaDespesa.getIndSituacao() == DominioSituacao.A
				&& naturezaDespesa.getGrupoNaturezaDespesa().getIndSituacao() == DominioSituacao.I) {
			throw new ApplicationBusinessException(
					FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M04);
		}

		this.getFsoNaturezaDespesaDAO().merge(naturezaDespesa);
	}

	
	/**
	 * Exclui a natureza de Despesa
	 */
	public void excluirNaturezaDespesa(FsoNaturezaDespesaId id) throws ApplicationBusinessException {
		
		FsoNaturezaDespesa naturezaDespesa = fsoNaturezaDespesaDAO.obterPorChavePrimaria(id);
		
		if (naturezaDespesa == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
				
		List<ScoSolicitacaoDeCompra> listaSolCompras = this.getSolicitacaoComprasFacade().
				buscarSolicitacaoCompraAssociadaNaturezaDespesa(naturezaDespesa.getId(), false);
		
		List<ScoSolicitacaoServico> listaSolServico = this.getSolicitacaoServicoFacade().
				buscarSolicitacaoServicoAssociadaNaturezaDespesa(naturezaDespesa.getId(), false);
		
		if (listaSolCompras.size() > 0)	 {		
			throw new ApplicationBusinessException(
					FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M09, this.obterListaSolicitacoesComprasAssociadas(listaSolCompras));
		}

		if (listaSolServico.size() > 0) {
			throw new ApplicationBusinessException(
					FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M10, this.obterListaSolicitacoesServicoAssociadas(listaSolServico));
		}

		if (this.getFsoNaturezaDespesaDAO().verificarNaturezaDespesaRelacionadaNaturezaPlano(naturezaDespesa)) {
			throw new ApplicationBusinessException(FsoNaturezaDespesaONExceptionCode.MENSAGEM_NATUREZA_DESPESA_M11);
		}

		getFsoNaturezaDespesaDAO().remover(naturezaDespesa);
	}

	/**
	 * Pesquisa naturezas de despesa pelo Grupo e Código/Descrição da Natureza de Despesa
	 */
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(
			FsoGrupoNaturezaDespesa grupo, Object filter) {
		return getFsoNaturezaDespesaDAO().pesquisarNaturezasDespesa(grupo,
				filter);
	}

	/**
	 * Concatena os códigos de solicitação de compras da lista
	 * @param listaSolServico
	 * @return String
	 */
	private String obterListaSolicitacoesComprasAssociadas(List<ScoSolicitacaoDeCompra> listaSolCompras) {
		StringBuilder msg = new StringBuilder();
		
		for (ScoSolicitacaoDeCompra solicit : listaSolCompras) {
			msg.append(solicit.getNumero()).append(',');	
		}
		
		return msg.toString().substring(0, msg.toString().length()-1);
	}
	
	/**
	 * Concatena os códigos de solicitação de serviço da lista
	 * @param listaSolServico
	 * @return String
	 */
	private String obterListaSolicitacoesServicoAssociadas(List<ScoSolicitacaoServico> listaSolServico) {
		StringBuilder msg = new StringBuilder();
		
		for (ScoSolicitacaoServico solicit : listaSolServico) {
			msg.append(solicit.getNumero()).append(',');	
		}
		
		return msg.toString().substring(0, msg.toString().length()-1);
	}
	
	/**
	 * Desativa naturezas de despesa de um grupo.
	 * 
	 * @param codigo
	 *            ID do grupo.
	 */
	public void desativarPorGrupo(Integer codigo) {
		getFsoNaturezaDespesaDAO().desativarPorGrupo(codigo);
	}

	protected FsoNaturezaDespesaDAO getFsoNaturezaDespesaDAO() {
		return fsoNaturezaDespesaDAO;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return this.solicitacaoComprasFacade;
	}
	
	protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return this.solicitacaoServicoFacade;
	}
}