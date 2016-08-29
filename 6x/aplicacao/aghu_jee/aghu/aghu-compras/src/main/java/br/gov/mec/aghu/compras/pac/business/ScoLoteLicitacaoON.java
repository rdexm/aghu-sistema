package br.gov.mec.aghu.compras.pac.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLoteLicitacaoDAO;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacaoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * RN responsável por manter condição de pagamento de licitação.
 * 
 */
@Stateless
public class ScoLoteLicitacaoON extends BaseBusiness {

	private static final long serialVersionUID = -5180375676145620896L;
	private static final Log LOG = LogFactory.getLog(ScoLoteLicitacaoON.class);

	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

	@Inject
	private ScoLoteLicitacaoDAO scoLoteLicitacaoDAO;

	@EJB
	private ScoLoteLicitacaoRN scoLoteLicitacaoRN;

	protected enum ScoLoteLicitacaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_GERAR_LOTES_NAO_EXCLUI_MATERIAIS_ASSOCIADOS, MENSAGEM_GERAR_LOTES_LOTE_NAO_ENCONTRADO;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public void gravarLoteSolicitacao(ScoLoteLicitacao loteLicitacao) throws ApplicationBusinessException {
		getScoLoteLicitacaoRN().persistir(loteLicitacao);
	}

	public void excluirLoteSolicitacao(ScoLoteLicitacaoId idLoteDelecao) throws ApplicationBusinessException {
		ScoLoteLicitacao loteLicitacao = getScoLoteLicitacaoDAO().obterPorChavePrimaria(idLoteDelecao);
		getScoLoteLicitacaoRN().remover(loteLicitacao);
	}
	
	public void excluirTodosLoteSolicitacao(List<ScoLoteLicitacao> lotesSolicitacao, Integer nroPac) throws ApplicationBusinessException{
		for(ScoLoteLicitacao scoLoteLicitacao :lotesSolicitacao){
			excluirLoteSolicitacao(scoLoteLicitacao.getId());
			
		}
		desfazerAssociacao(nroPac); 
	}
	

	public void desfazerExcluirLotes(List<ScoLoteLicitacao> lotesLicitcao, Integer nroPac) throws ApplicationBusinessException {
		desfazerAssociacao(nroPac);
		for (ScoLoteLicitacao scoLoteLicitacao : lotesLicitcao) {
			getScoLoteLicitacaoDAO().removerPorId(scoLoteLicitacao.getId());
		}
		gerarLotesByPacItens(nroPac);
	}

	public void desfazerAssociacao(Integer nroPac) {
		List<ScoItemLicitacao> itens = getScoItemLicitacaoDAO().pesquisarItemLicitacaoPorNumLicitacaoNumItens(nroPac,
				new ArrayList<Integer>());
		for (ScoItemLicitacao scoItemLicitacao : itens) {
			scoItemLicitacao.setLoteLicitacao(null);
			getScoItemLicitacaoDAO().atualizar(scoItemLicitacao);
		}

	}

	//RN02
	public Boolean verificarExisteItensAssociados(Integer lctNumero, Short numero)throws ApplicationBusinessException{
		List<ScoItemLicitacao> item = getScoItemLicitacaoDAO().obterItensPorLote(lctNumero,numero);
		if(item!=null && item.size()>0){
			throw new ApplicationBusinessException(ScoLoteLicitacaoONExceptionCode.MENSAGEM_GERAR_LOTES_NAO_EXCLUI_MATERIAIS_ASSOCIADOS);
		}
		return Boolean.TRUE;
	}
	

	public Boolean verificarExisteLote(Integer nroPac, Short nroLote) throws ApplicationBusinessException {
		Boolean existe = getScoLoteLicitacaoDAO().verificarExisteLote(nroPac, nroLote);
		if (!existe) {
			throw new ApplicationBusinessException(ScoLoteLicitacaoONExceptionCode.MENSAGEM_GERAR_LOTES_LOTE_NAO_ENCONTRADO);
		}
		return existe;
	}

	public void associarItensLote(List<ItensPACVO> itensSolicitacao) throws ApplicationBusinessException {
		for (ItensPACVO itemVO : itensSolicitacao) {
			associarItemLote(itemVO);
		}
	}

	public void associarItemLote(ItensPACVO itemVO) {
		ScoItemLicitacaoId id = new ScoItemLicitacaoId(itemVO.getNumeroLicitacao(), itemVO.getNumeroItem());
		ScoItemLicitacao item = getScoItemLicitacaoDAO().obterPorChavePrimaria(id);
		ScoLoteLicitacaoId loteId = new ScoLoteLicitacaoId(itemVO.getNumeroLicitacao(), itemVO.getNumeroLote());
		ScoLoteLicitacao lote = getScoLoteLicitacaoDAO().obterPorChavePrimaria(loteId);
		item.setLoteLicitacao(lote);
		getScoItemLicitacaoDAO().atualizar(item);
	}

	public void gerarLotesByPacItens(Integer nroPac) throws ApplicationBusinessException {
		List<ItensPACVO> itensLicitacao = getScoItemLicitacaoDAO().listarItensPorNroPac(nroPac);
		for (ItensPACVO itemVO : itensLicitacao) {
			ScoLoteLicitacao lote = gerarLote(itemVO);
			itemVO.setNumeroLote(lote.getId().getNumero());
			associarItemLote(itemVO);
		}
	}

	private ScoLoteLicitacao gerarLote(ItensPACVO itemVO) {
		return getScoLoteLicitacaoRN().gerarLoteByItem(itemVO);
	}

	private ScoLoteLicitacaoRN getScoLoteLicitacaoRN() {
		return this.scoLoteLicitacaoRN;
	}

	private ScoLoteLicitacaoDAO getScoLoteLicitacaoDAO() {
		return this.scoLoteLicitacaoDAO;
	}

	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return this.scoItemLicitacaoDAO;
	}

}
