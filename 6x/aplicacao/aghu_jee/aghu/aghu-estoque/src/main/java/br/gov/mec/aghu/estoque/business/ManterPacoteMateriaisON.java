package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoGruposDAO;
import br.gov.mec.aghu.estoque.dao.SceItemPacoteMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.ScePacoteMateriaisDAO;
import br.gov.mec.aghu.estoque.vo.ConsumoMaterialVO;
import br.gov.mec.aghu.estoque.vo.ItemPacoteMateriaisVO;
import br.gov.mec.aghu.estoque.vo.PacoteMateriaisVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoGrupos;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemPacoteMateriais;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe negocial para Pacote Materiais. (estória #6623)
 * 
 * @author guilherme.finotti
 * 
 */
@Stateless
public class ManterPacoteMateriaisON extends BaseBusiness {

	@EJB
	private ScePacoteMateriaisRN scePacoteMateriaisRN;

	@EJB
	private ManterRequisicaoMaterialON manterRequisicaoMaterialON;

	private static final Log LOG = LogFactory.getLog(ManterPacoteMateriaisON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private SceItemPacoteMateriaisDAO sceItemPacoteMateriaisDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;

	@Inject
	private ScePacoteMateriaisDAO scePacoteMateriaisDAO;

	@Inject
	private SceAlmoxarifadoGruposDAO sceAlmoxarifadoGruposDAO;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	public enum ManterPacoteMateriaisONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MATERIAL_GRUPO_RM_PACOTE, MENSAGEM_MATERIAL_GRUPO_MISTURADO_RM_PACOTE, MENSAGEM_MATERIAL_DIRETO_SOLICITANTE_RM_PACOTE, MENSAGEM_MATERIAL_DIRETO_ALMOXARIFE_RM_PACOTE, MENSAGEM_MATERIAL_GRUPO_DIVERGENTE_RM_PACOTE;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4534673872760240489L;

	/**
	 * Retorna instância de ScePacoteMateriaisDAO
	 * 
	 * @return
	 */
	protected ScePacoteMateriaisDAO getScePacoteMateriaisDAO() {
		return scePacoteMateriaisDAO;
	}

	/**
	 * Retorna instância de SceItemPacoteMateriaisDAO
	 * 
	 * @return
	 */
	private SceItemPacoteMateriaisDAO getSceItemPacoteMateriaisDAO() {
		return sceItemPacoteMateriaisDAO;
	}

	/**
	 * Retorna instância de SceAlmoxarifadoDAO
	 * 
	 * @return
	 */
	private SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	/**
	 * Retorna instância de ManterRequisicaoMaterialON
	 * 
	 * @return
	 */
	protected ManterRequisicaoMaterialON getManterRequisicaoMaterialON() {
		return manterRequisicaoMaterialON;
	}

	/**
	 * Retorna instância de ScePacoteMateriaisRN
	 * 
	 * @return
	 */
	protected ScePacoteMateriaisRN getScePacoteMateriaisRN() {
		return scePacoteMateriaisRN;
	}

	/**
	 * Obtém pacote materiais com ítens, por chave primária
	 * 
	 * @param pacoteId
	 * @return
	 */
	public ScePacoteMateriais obterPacoteMateriaisComItensPorChavePrimaria(ScePacoteMateriaisId pacoteId) {
		return getScePacoteMateriaisDAO().obterPacoteMateriaisComItensPorChavePrimaria(pacoteId);
	}

	/**
	 * Remove o pacote de materiais
	 * 
	 * @param pacote
	 * @throws BaseListException
	 */
	public void removerPacoteMaterial(ScePacoteMateriais pacote) throws BaseListException {
		getScePacoteMateriaisRN().removerPacoteMateriais(pacote);
	}

	/**
	 * 
	 * @param codigo
	 * @return
	 */
	public FccCentroCustos obterFccCentroCustos(Integer codigo) {
		return getCentroCustoFacade().obterFccCentroCustos(codigo);
	}

	/**
	 * @ ORADB: SCEK_PMT_RN.RN_SCEP_VER_CCT_ATIV Verifica se o centro de custo
	 * informado está inativo.
	 * 
	 * @param codigoCentroCusto
	 * @return
	 */
	@SuppressWarnings("ucd")
	public boolean isCentroCustoInativo(Integer codigoCentroCusto) {
		FccCentroCustos centroCusto = getCentroCustoFacade().obterFccCentroCustos(codigoCentroCusto);
		if (centroCusto != null) {
			if (DominioSituacao.I.equals(centroCusto.getIndSituacao())) {
				return true;
			}
		}
		return false;
	}

	private Map<ConsumoMaterialVO.TipoConsumo, ConsumoMaterialVO> obterConsumosItemPacoteMaterial(Integer codMaterial, Integer codCCusto,
			Integer codGrpMat) throws ApplicationBusinessException {
		// obtem parâmetro de competencia
		AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		Map<ConsumoMaterialVO.TipoConsumo, ConsumoMaterialVO> mapConsumos = null;
		mapConsumos = getManterRequisicaoMaterialON().obterConsumos(param.getVlrData(), codMaterial, codCCusto, codGrpMat);
		return mapConsumos;

	}

	/**
	 * Obtém pacote de materiais como objeto de valor, com variáveis primitivas,
	 * necessárias para pesquisa
	 * 
	 * @param id
	 * @return
	 */
	public PacoteMateriaisVO obterPacoteMaterialVO(ScePacoteMateriaisId id) {
		return getScePacoteMateriaisDAO().obterPacoteMaterialVO(id);
	}

	/**
	 * 
	 * @param itensPacote
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @param seqEstoque
	 * @param quantidade
	 * @param codigoMaterial
	 * @param seqAlmoxarifadoPai
	 * @param seqAlmoxarifadoFilho
	 * @param situacaoAlmoxarifadoPai
	 * @throws BaseListException
	 * @throws ApplicationBusinessException
	 */
	public void verificarInclusaoItensPacoteMateriais(List<ItemPacoteMateriaisVO> itensPacote, Integer codigoCentroCustoProprietario,
			Integer codigoCentroCustoAplicacao, Integer numeroPacote, Integer seqEstoque, Integer quantidade, Integer codigoMaterial,
			Short seqAlmoxarifadoPai, Short seqAlmoxarifadoFilho, DominioSituacao situacaoAlmoxarifadoPai) throws BaseListException,
			ApplicationBusinessException {
		getScePacoteMateriaisRN().preInsercaoItensPacote(itensPacote, codigoCentroCustoProprietario, codigoCentroCustoAplicacao,
				numeroPacote, seqEstoque, quantidade, codigoMaterial, seqAlmoxarifadoPai, seqAlmoxarifadoFilho, situacaoAlmoxarifadoPai);
	}

	/**
	 * Pesquisa ítens de pacote de materiais
	 * 
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numero
	 * @return
	 */
	public List<SceItemPacoteMateriais> pesquisarItensPacoteMateriais(Integer codigoCentroCustoProprietario,
			Integer codigoCentroCustoAplicacao, Integer numero) {
		return getSceItemPacoteMateriaisDAO().pesquisarItensPacoteMateriais(codigoCentroCustoProprietario, codigoCentroCustoAplicacao,
				numero);

	}

	/**
	 * Método que valida se o último ítem da lista está sendo removido
	 * 
	 * @param quantidade
	 * @throws ApplicationBusinessException
	 */
	public void verificarUltimoItemPacoteMateriais(int quantidade) throws ApplicationBusinessException {
		getScePacoteMateriaisRN().verificarUltimoItemPacoteMateriais(quantidade);

	}

	/**
	 * Pesquisa PacoteMateriais de acordo com os parâmetros informados
	 * 
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @param numeroAlmoxarifado
	 * @param situacao
	 * @return
	 */
	public List<ScePacoteMateriais> pesquisarPacoteMateriais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacote, Short numeroAlmoxarifado,
			DominioSituacao situacao) {
		return getScePacoteMateriaisDAO().pesquisarPacoteMateriais(firstResult, maxResult, orderProperty, asc,
				codigoCentroCustoProprietario, codigoCentroCustoAplicacao, numeroPacote, numeroAlmoxarifado, situacao);
	}

	/**
	 * Retorna a quantidade de pacotes de materiais obtidos na pesquisa
	 */
	public Long pesquisarPacoteMateriaisCount(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao,
			Integer numeroPacote, Short numeroAlmoxarifado, DominioSituacao situacao) {
		return getScePacoteMateriaisDAO().pesquisarPacoteMateriaisCount(codigoCentroCustoProprietario, codigoCentroCustoAplicacao,
				numeroPacote, numeroAlmoxarifado, situacao);
	}

	/**
	 * Pesquisa os almoxarifados ativos, por código e descrição, ordenados pela
	 * descrição
	 * 
	 * @param parametro
	 * @return
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricaoOrdenadosPelaDescricao(Object parametro) {
		return getSceAlmoxarifadoDAO().pesquisarAlmoxarifadosAtivosPorCodigoDescricaoOrdenadosPelaDescricao(parametro);
	}

	/**
	 * Retorna ítens de pacote de materiais como VO's
	 * 
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemPacoteMateriaisVO> pesquisarItensPacoteMateriaisVO(Integer codigoCentroCustoProprietario,
			Integer codigoCentroCustoAplicacao, Integer numeroPacote) throws ApplicationBusinessException {
		List<ItemPacoteMateriaisVO> itensPacoteMateriaisVO = getSceItemPacoteMateriaisDAO().pesquisarItensPacoteMateriaisVO(
				codigoCentroCustoProprietario, codigoCentroCustoAplicacao, numeroPacote);

		for (ItemPacoteMateriaisVO itemPacoteMateriaisVO : itensPacoteMateriaisVO) {
			popularConsumosItemPacoteVO(itemPacoteMateriaisVO);
		}
		return itensPacoteMateriaisVO;
	}

	public void popularConsumosItemPacoteVO(ItemPacoteMateriaisVO itemPacoteMateriaisVO) throws ApplicationBusinessException {
		Map<ConsumoMaterialVO.TipoConsumo, ConsumoMaterialVO> mapConsumos = obterConsumosItemPacoteMaterial(
				itemPacoteMateriaisVO.getCodigoMaterial(), itemPacoteMateriaisVO.getCodigoCentroCustoAplicacaoPacote(),
				itemPacoteMateriaisVO.getCodigoGrupoMaterial());

		itemPacoteMateriaisVO.setConsumo30Dias(0.00);
		itemPacoteMateriaisVO.setMediaSemestre(0.00);
		if (mapConsumos != null && !mapConsumos.isEmpty()) {
			if (mapConsumos.containsKey(ConsumoMaterialVO.TipoConsumo.ULT_30_DIAS)) {
				itemPacoteMateriaisVO.setConsumo30Dias(mapConsumos.get(ConsumoMaterialVO.TipoConsumo.ULT_30_DIAS).getConsumo());
			}
			if (mapConsumos.containsKey(ConsumoMaterialVO.TipoConsumo.MEDIO_SEMESTRE)) {
				itemPacoteMateriaisVO.setMediaSemestre(mapConsumos.get(ConsumoMaterialVO.TipoConsumo.MEDIO_SEMESTRE).getConsumo());
			}
		}
	}

	/**
	 * Persiste (ou atualiza) no banco de dados o pacote de materiais, com ítens
	 * 
	 * @param pacote
	 * @throws BaseListException
	 */
	public void persistirPacoteMaterais(ScePacoteMateriais pacote) throws BaseListException {
		if (pacote.getId().getNumero() == null) {
			getScePacoteMateriaisRN().persistirPacoteMateriais(pacote);
		} else {
			getScePacoteMateriaisRN().atualizarPacoteMaterial(pacote);
		}
	}

	/**
	 * Obtém o ítem de pacote de material, a ser armazenado no grid, com campos
	 * necessários
	 * 
	 * @param seqEstoqueAlmoxarifado
	 * @param codigoMaterial
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public ItemPacoteMateriaisVO obterItemPacoteMateriaisVO(Integer seqEstoqueAlmoxarifado, Integer codigoMaterial)
			throws ApplicationBusinessException {

		ItemPacoteMateriaisVO itemPacoteMateriaisVO = getSceItemPacoteMateriaisDAO().obterItemPacoteMateriaisVO(seqEstoqueAlmoxarifado);
		itemPacoteMateriaisVO.setConsumo30Dias(0.00);
		itemPacoteMateriaisVO.setMediaSemestre(0.00);
		Map<ConsumoMaterialVO.TipoConsumo, ConsumoMaterialVO> mapConsumos = obterConsumosItemPacoteMaterial(
				itemPacoteMateriaisVO.getCodigoMaterial(), itemPacoteMateriaisVO.getCodigoCentroCustoAplicacaoPacote(),
				itemPacoteMateriaisVO.getCodigoGrupoMaterial());
		if (mapConsumos != null && !mapConsumos.isEmpty()) {
			if (mapConsumos.containsKey(ConsumoMaterialVO.TipoConsumo.ULT_30_DIAS)) {
				itemPacoteMateriaisVO.setConsumo30Dias(mapConsumos.get(ConsumoMaterialVO.TipoConsumo.ULT_30_DIAS).getConsumo());
			}
			if (mapConsumos.containsKey(ConsumoMaterialVO.TipoConsumo.MEDIO_SEMESTRE)) {
				itemPacoteMateriaisVO.setMediaSemestre(mapConsumos.get(ConsumoMaterialVO.TipoConsumo.MEDIO_SEMESTRE).getConsumo());
			}
		}
		return itemPacoteMateriaisVO;
	}

	public void validarInsercaoPacoteRequisicaoMaterial(SceReqMaterial reqMaterial, List<SceEstoqueAlmoxarifado> lstItemToAdd,
			Boolean isAlmoxarife) throws ApplicationBusinessException {
		List<SceAlmoxarifadoGrupos> listaAlmoxGrupos = new ArrayList<SceAlmoxarifadoGrupos>();

		if (reqMaterial != null && !reqMaterial.getAlmoxarifado().getIndMultiplosGrupos()) {
			listaAlmoxGrupos = getSceAlmoxarifadoGruposDAO().pesquisarGruposPorAlmoxarifado(reqMaterial.getAlmoxarifado());
		}

		Boolean primeiro = true;
		Boolean indEstocavel = false;
		for (SceEstoqueAlmoxarifado estoqueAlmoxarifado : lstItemToAdd) {
			// passa o 1o item do pacote
			if (primeiro) {
				// no 1o item testa se o usuario escolheu um grupo de material
				// somente
				if (reqMaterial != null && reqMaterial.getGrupoMaterial() != null) {
					// usuario nao eh almoxarife e escolheu um grupo de material
					// porem no pacote vem grupos diferentes
					if (!isAlmoxarife && !estoqueAlmoxarifado.getMaterial().getGrupoMaterial().equals(reqMaterial.getGrupoMaterial())) {
						throw new ApplicationBusinessException(ManterPacoteMateriaisONExceptionCode.MENSAGEM_MATERIAL_GRUPO_RM_PACOTE);
					}
				}
				indEstocavel = estoqueAlmoxarifado.getMaterial().getEstocavel();
				this.validarMateriaisDiretos(estoqueAlmoxarifado, isAlmoxarife, indEstocavel, reqMaterial, primeiro);
				primeiro = false;
			} else {
				// a partir do segundo item
				this.validarMateriaisDiretos(estoqueAlmoxarifado, isAlmoxarife, indEstocavel, reqMaterial, primeiro);
				// se o almoxarifado nao permite multiplos grupos
				if (reqMaterial != null && !reqMaterial.getAlmoxarifado().getIndMultiplosGrupos()) {
					// se a lista de excecoes esta vazia
					if (listaAlmoxGrupos == null || listaAlmoxGrupos.isEmpty()) {
						// se o usuario preencheu o grupo de material
						if (reqMaterial != null && reqMaterial.getGrupoMaterial() != null) {
							if (!isAlmoxarife
									&& !estoqueAlmoxarifado.getMaterial().getGrupoMaterial().equals(reqMaterial.getGrupoMaterial())) {
								throw new ApplicationBusinessException(
										ManterPacoteMateriaisONExceptionCode.MENSAGEM_MATERIAL_GRUPO_MISTURADO_RM_PACOTE);
							}
						} else {
							// compara o grupo de material deste eal com o do 1o
							// item da lista
							ScoMaterial mat = getComprasFacade().obterMaterialPorId(lstItemToAdd.get(0).getMaterial().getCodigo());
							if (mat != null) {
								if (!estoqueAlmoxarifado.getMaterial().getGrupoMaterial().equals(mat.getGrupoMaterial())) {
									throw new ApplicationBusinessException(
											ManterPacoteMateriaisONExceptionCode.MENSAGEM_MATERIAL_GRUPO_DIVERGENTE_RM_PACOTE);
								}
							}
						}
					} else {
						this.validarListaExcecoesGrupoAlmoxarifado(lstItemToAdd, listaAlmoxGrupos, estoqueAlmoxarifado);
					}
				}
			}
		}
	}

	private void validarMateriaisDiretos(SceEstoqueAlmoxarifado estoqueAlmoxarifado, Boolean isAlmoxarife, Boolean indEstocavel,
			SceReqMaterial reqMaterial, Boolean primeiraVez) throws ApplicationBusinessException {
		if (!isAlmoxarife && !primeiraVez) {
			if (!estoqueAlmoxarifado.getMaterial().getEstocavel().equals(indEstocavel)
					&& !reqMaterial.getAlmoxarifado().getIndMaterialDireto()
					&& (estoqueAlmoxarifado.getIndEstoqueTemporario() == null || !estoqueAlmoxarifado.getIndEstoqueTemporario())) {
				throw new ApplicationBusinessException(ManterPacoteMateriaisONExceptionCode.MENSAGEM_MATERIAL_DIRETO_ALMOXARIFE_RM_PACOTE);
			}
		}
	}

	private void validarListaExcecoesGrupoAlmoxarifado(List<SceEstoqueAlmoxarifado> lstItemToAdd,
			List<SceAlmoxarifadoGrupos> listaAlmoxGrupos, SceEstoqueAlmoxarifado estoqueAlmoxarifado) throws ApplicationBusinessException {
		// se tem lista de excecoes, tem que mandar a lista para a query
		ScoGrupoMaterial grupoReq = lstItemToAdd.get(0).getMaterial().getGrupoMaterial();

		// verifica se esta na lista de grupos que pode misturar
		Integer alcSeq = null;
		for (SceAlmoxarifadoGrupos grp : listaAlmoxGrupos) {
			if (grp.getGrupoMaterial().equals(grupoReq)) {
				alcSeq = grp.getComposicao().getSeq();
			}
		}

		Boolean inList = false;
		if (alcSeq != null) {
			// se estiver na lista, pega o alcSeq para comparar com o grupo que
			// veio do catalogo
			for (SceAlmoxarifadoGrupos grp : listaAlmoxGrupos) {
				if (grp.getComposicao().getSeq().equals(alcSeq)
						&& grp.getGrupoMaterial().equals(estoqueAlmoxarifado.getMaterial().getGrupoMaterial())) {
					inList = true;
				}
			}
		} else {
			// se nao estiver na lista, deixa somente o grupo que ja existe
			if (estoqueAlmoxarifado.getMaterial().getGrupoMaterial().equals(grupoReq)) {
				inList = true;
			}
		}

		// grupo do material vindo do pacote nao esta na lista permitida
		if (!inList) {
			throw new ApplicationBusinessException(ManterPacoteMateriaisONExceptionCode.MENSAGEM_MATERIAL_GRUPO_DIVERGENTE_RM_PACOTE);
		}
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	protected SceAlmoxarifadoGruposDAO getSceAlmoxarifadoGruposDAO() {
		return sceAlmoxarifadoGruposDAO;
	}

	protected void setSceAlmoxarifadoGruposDAO(SceAlmoxarifadoGruposDAO sceAlmoxarifadoGruposDAO) {
		this.sceAlmoxarifadoGruposDAO = sceAlmoxarifadoGruposDAO;
	}

}
