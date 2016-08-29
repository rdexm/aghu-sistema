package br.gov.mec.aghu.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.faturamento.vo.CidVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CidON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CidON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AghCidDAO aghCidDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7972605096964942398L;

	enum CidONExceptionCode implements BusinessExceptionCode {
		CODIGO_CID_NULO, CODIGO_CID_INVALIDO, MENSAGEM_NUMERO_MINIMO_CARACTERES_CID;
	}

	public String pesquisarDescricaoCidPrincipal(Integer seqAtendimento, Integer apaSeq, Short seqp) {
		return getAghCidDAO().pesquisarDescricaoCidPrincipal(seqAtendimento, apaSeq, seqp);
	}

	public List<String> pesquisarDescricaoCidSecundario(Integer seqAtendimento, Integer apaSeq, Short seqp) {
		return getAghCidDAO().pesquisarDescricaoCidSecundario(seqAtendimento, apaSeq, seqp);
	}

	/**
	 * Método para obter lista de <code>AghCids</code> a partir do codigo e
	 * descrição.
	 * 
	 * @param codigo
	 * @param descricao
	 * @return List de <code>AghCids</code>
	 */
	public List<AghCid> obterCids(String codDesc, boolean filtroSituacaoAtiva) {
		return getAghCidDAO().obterCids(codDesc, filtroSituacaoAtiva);
	}
	
	public Long obterCidsCount(String codDesc, boolean filtroSituacaoAtiva) {
		return getAghCidDAO().obterCidsCount(codDesc, filtroSituacaoAtiva);
	}	

	/**
	 * Método responsável por validar CID para pesquisa.
	 * 
	 * @param codCid
	 * @throws ApplicationBusinessException
	 */
	public void validarCodigoCid(String codCid) throws ApplicationBusinessException {
		if (!StringUtils.isNotBlank(codCid)) {
			throw new ApplicationBusinessException(CidONExceptionCode.CODIGO_CID_NULO);
		}
	}

	/**
	 * Método para obter a quantidade de Procedimento associados ao CID..
	 * 
	 * @param cidCodigo
	 * @return Integer
	 */
	public Long pesquisarProcedimentosParaCidCount(String cidCodigo) {
		return getAghCidDAO().pesquisarProcedimentosParaCidCount(cidCodigo);
	}

	/**
	 * Método que obtém a lista de Procedimentos associados ao CID como
	 * parâmetro.
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public List<CidVO> pesquisarProcedimentosParaCid(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			String codCid) {
		return getAghCidDAO().pesquisarProcedimentosParaCid(firstResult, maxResults, orderProperty, asc, codCid);
	}

	/**
	 * Método para buscar todos os Cids que estão auto-relacionaods (com
	 * CID_SEQ) através do seq do Cid.
	 * 
	 * @param seqCid
	 * @return lista de AghCids
	 */
	public List<AghCid> pesquisarCidsRelacionados(Integer seqCid) {
		if (seqCid == null) {
			return null;
		} else {
			return getAghCidDAO().pesquisarCidsRelacionados(seqCid);
		}
	}

	/**
	 * Método para buscar todos Cids que não tem registros auto-relacionados
	 * através do seq do GrupoCid.
	 * 
	 * @param seqGrupoCid
	 * @return lista de AghCids
	 */
	public List<AghCid> pesquisarCidsPorGrupo(Integer seqGrupoCid) {
		if (seqGrupoCid == null) {
			return null;
		} else {
			return getAghCidDAO().pesquisarCidsPorGrupo(seqGrupoCid);
		}
	}

	/**
	 * Método para buscar Cids através de sua descrição.
	 * 
	 * @param descricao
	 * @return
	 */
	public List<AghCid> pesquisarCidsPorDescricaoOuId(String descricao, Integer limiteRegistros) {
		return getAghCidDAO().pesquisarCidsPorDescricaoOuId(descricao, limiteRegistros);
	}

	/**
	 * Método para retornar todos os CIDs que não possuem sub-categorias. A
	 * pesquisa é feita através da descrição e ID da entidade AghCids.
	 * 
	 * @param descricao
	 * @return
	 */
	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuId(String descricao, Integer limiteRegistros)
			throws ApplicationBusinessException {
		try {
			return getAghCidDAO().pesquisarCidsSemSubCategoriaPorDescricaoOuId(descricao, limiteRegistros);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(CidONExceptionCode.CODIGO_CID_INVALIDO);
		}
	}

	/**
	 * Método para retornar todos os CIDs que não possuem sub-categorias. A
	 * pesquisa é feita através da descrição e ID da entidade AghCids.
	 * 
	 * Ordenado por descrição.
	 * 
	 * @param descricao
	 * @return
	 */
	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuIdOrdenadoPorDesc(String descricao, Integer limiteRegistros)
			throws ApplicationBusinessException {

		if (descricao.length() < 3) {
			throw new ApplicationBusinessException(CidONExceptionCode.MENSAGEM_NUMERO_MINIMO_CARACTERES_CID);
		}

		try {
			return getAghCidDAO().pesquisarCidsSemSubCategoriaPorDescricaoOuIdOrdenadoPorDesc(descricao, limiteRegistros);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(CidONExceptionCode.CODIGO_CID_INVALIDO);
		}
	}

	/**
	 * Método para pesquisar todos os cids de uma internação.
	 * 
	 * @param seqInternacao
	 * @return Lista de objetos AinCidsInternacao
	 */
	public List<AinCidsInternacao> pesquisarCidsInternacao(Integer seqInternacao) {
		if (seqInternacao == null) {
			return null;
		} else {
			return getAghCidDAO().pesquisarCidsInternacao(seqInternacao);
		}
	}

	/**
	 * Método para obter um objeto AghCids através do seu ID.
	 * 
	 * @param seq
	 * @return
	 */
	public AghCid obterCid(Integer seq) {
		if (seq == null) {
			return null;
		} else {
			return getAghCidDAO().obterPorChavePrimaria(seq);
		}
	}

	protected AghCidDAO getAghCidDAO() {
		return aghCidDAO;
	}
}
