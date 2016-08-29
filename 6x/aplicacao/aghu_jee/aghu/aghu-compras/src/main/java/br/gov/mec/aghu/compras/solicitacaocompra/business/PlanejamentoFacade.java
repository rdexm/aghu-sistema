package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.dao.ScoItemLoteReposicaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLoteReposicaoDAO;
import br.gov.mec.aghu.compras.vo.CriterioReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.FiltroReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.ItemReposicaoMaterialVO;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;

@Modulo(ModuloEnum.COMPRAS)
@Stateless
public class PlanejamentoFacade extends BaseFacade implements IPlanejamentoFacade {

	private static final long serialVersionUID = -5875255349708262627L;

	@EJB
	private ReposicaoScON reposicaoScON;

	@EJB
	private LoteReposicaoScON loteReposicaoScON;

	@Inject
	private ScoItemLoteReposicaoDAO scoItemLoteReposicaoDAO;

	@Inject
	private ScoLoteReposicaoDAO scoLoteReposicaoDAO;

	@Override
	public void excluirLote(ScoLoteReposicao loteReposicao) throws BaseException {
		this.getReposicaoScON().excluirLote(loteReposicao);
	}

	@Override
	public CriterioReposicaoMaterialVO montarCriterioReposicao(ScoLoteReposicao loteReposicao) {
		return getReposicaoScON().montarCriterioReposicao(loteReposicao);
	}

	@Override
	public String getScRelacionada(ItemReposicaoMaterialVO item) {
		return getReposicaoScON().getScRelacionada(item);
	}

	@Override
	public String getListaScs(ItemReposicaoMaterialVO item) {
		return getReposicaoScON().getListaScs(item);
	}

	@Override
	public String getSplited(final String descricao, final Integer tam) {
		return getReposicaoScON().getSplited(descricao, tam);
	}

	@Override
	public List<ScoLoteReposicao> pesquisarLoteReposicaoPorCodigoDescricao(Object param) {
		return this.getScoLoteReposicaoDAO().pesquisarLoteReposicaoPorCodigoDescricao(param);
	}

	@Override
	public Boolean verificarMaterialExistente(ScoLoteReposicao lote, ScoMaterial mat) {
		return this.getScoItemLoteReposicaoDAO().verificarMaterialExistente(lote, mat);
	}

	@Override
	public Long obterQtdScGerada(ScoLoteReposicao lote) {
		return this.getScoItemLoteReposicaoDAO().obterQtdScGerada(lote);
	}

	private ScoItemLoteReposicaoDAO getScoItemLoteReposicaoDAO() {
		return this.scoItemLoteReposicaoDAO;
	}

	private ScoLoteReposicaoDAO getScoLoteReposicaoDAO() {
		return this.scoLoteReposicaoDAO;
	}

	@Override
	public Long pesquisarMaterialReposicaoCount(ScoLoteReposicao lote) {
		return getScoItemLoteReposicaoDAO().pesquisarMaterialReposicaoCount(lote);
	}

	@Override
	public ScoLoteReposicao obterLoteReposicaoPorSeq(Integer seq) {
		return this.getScoLoteReposicaoDAO().obterLoteReposicaoPorSeq(seq);
	}

	@Override
	public ScoLoteReposicao criarLote(FiltroReposicaoMaterialVO filtro, CriterioReposicaoMaterialVO criterioGeracao,
			List<ItemReposicaoMaterialVO> listaAlteracoes, List<Integer> nroDesmarcados) throws BaseException {
		return this.loteReposicaoScON.criarLote(filtro, criterioGeracao, listaAlteracoes, nroDesmarcados);
	}

	@Override
	public void validarCamposObrigatorios(CriterioReposicaoMaterialVO criterioGeracao, Boolean simular) throws ApplicationBusinessException {
		this.getReposicaoScON().validarCamposObrigatorios(criterioGeracao, simular);
	}

	@Override
	public List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, FiltroReposicaoMaterialVO filtro, CriterioReposicaoMaterialVO criterioReposicao) {
		return getReposicaoScON().pesquisarMaterialReposicao(firstResult, maxResult, orderProperty, asc, filtro, criterioReposicao);
	}

	@Override
	public List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoLoteReposicao lote) {
		return this.getReposicaoScON().pesquisarMaterialReposicao(firstResult, maxResult, orderProperty, asc, lote);
	}

	@Override
	public ItemReposicaoMaterialVO montarItemReposicaoVO(ScoMaterial mat, ScoLoteReposicao loteReposicao, ScoSolicitacaoDeCompra slc,
			CriterioReposicaoMaterialVO criterioReposicao) {
		return this.getReposicaoScON().montarItemReposicaoVO(mat, loteReposicao, slc, criterioReposicao);
	}

	@Override
	public Long pesquisarLoteReposicaoCount(ScoLoteReposicao loteReposicao, ScoGrupoMaterial grupoMaterial, Date dataInicioGeracao,
			Date dataFimGeracao, RapServidores servidorGeracao, ScoMaterial material, String nomeLote, DominioTipoMaterial tipoMaterial) {
		return this.getScoLoteReposicaoDAO().pesquisarLoteReposicaoCount(loteReposicao, grupoMaterial, dataInicioGeracao, dataFimGeracao,
				servidorGeracao, material, nomeLote, tipoMaterial);
	}

	@Override
	public void inserirInclusaoManualLoteReposicao(ScoLoteReposicao lote, List<ItemReposicaoMaterialVO> listaInclusaoPontual)
			throws BaseException {
		this.getReposicaoScON().inserirInclusaoManualLoteReposicao(lote, listaInclusaoPontual);
	}

	@Override
	public void excluirItemLote(ScoLoteReposicao loteReposicao, Integer seq) throws BaseException {
		this.getReposicaoScON().excluirItemLote(loteReposicao, seq);
	}

	@Override
	public List<ScoLoteReposicao> pesquisarLoteReposicao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoLoteReposicao loteReposicao, ScoGrupoMaterial grupoMaterial, Date dataInicioGeracao, Date dataFimGeracao,
			RapServidores servidorGeracao, ScoMaterial material, String nomeLote, DominioTipoMaterial tipoMaterial) {
		return this.getScoLoteReposicaoDAO().pesquisarLoteReposicao(firstResult, maxResult, orderProperty, asc, loteReposicao,
				grupoMaterial, dataInicioGeracao, dataFimGeracao, servidorGeracao, material, nomeLote, tipoMaterial);
	}

	private ReposicaoScON getReposicaoScON() {
		return this.reposicaoScON;
	}
}