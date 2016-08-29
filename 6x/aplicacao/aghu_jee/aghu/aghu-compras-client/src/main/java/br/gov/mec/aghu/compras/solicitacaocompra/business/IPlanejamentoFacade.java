package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.compras.vo.CriterioReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.FiltroReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.ItemReposicaoMaterialVO;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IPlanejamentoFacade extends Serializable {

	void excluirLote(ScoLoteReposicao loteReposicao) throws BaseException;

	CriterioReposicaoMaterialVO montarCriterioReposicao(ScoLoteReposicao loteReposicao);

	String getScRelacionada(ItemReposicaoMaterialVO item);

	String getListaScs(ItemReposicaoMaterialVO item);

	String getSplited(final String descricao, final Integer tam);

	List<ScoLoteReposicao> pesquisarLoteReposicaoPorCodigoDescricao(Object param);

	Boolean verificarMaterialExistente(ScoLoteReposicao lote, ScoMaterial mat);

	Long obterQtdScGerada(ScoLoteReposicao lote);

	ScoLoteReposicao obterLoteReposicaoPorSeq(Integer seq);

	public ItemReposicaoMaterialVO montarItemReposicaoVO(ScoMaterial mat, ScoLoteReposicao loteReposicao, ScoSolicitacaoDeCompra slc,
			CriterioReposicaoMaterialVO criterioReposicao);

	ScoLoteReposicao criarLote(FiltroReposicaoMaterialVO filtro, CriterioReposicaoMaterialVO criterioGeracao,
			List<ItemReposicaoMaterialVO> listaAlteracoes, List<Integer> nroDesmarcados) throws BaseException;

	void validarCamposObrigatorios(CriterioReposicaoMaterialVO criterioGeracao, Boolean simular) throws ApplicationBusinessException;

	List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			FiltroReposicaoMaterialVO filtro, CriterioReposicaoMaterialVO criterioReposicao);

	List<ScoLoteReposicao> pesquisarLoteReposicao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoLoteReposicao loteReposicao, ScoGrupoMaterial grupoMaterial, Date dataInicioGeracao, Date dataFimGeracao,
			RapServidores servidorGeracao, ScoMaterial material, String nomeLote, DominioTipoMaterial tipoMaterial);

	List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			ScoLoteReposicao lote);

	Long pesquisarLoteReposicaoCount(ScoLoteReposicao loteReposicao, ScoGrupoMaterial grupoMaterial, Date dataInicioGeracao,
			Date dataFimGeracao, RapServidores servidorGeracao, ScoMaterial material, String nomeLote, DominioTipoMaterial tipoMaterial);

	void inserirInclusaoManualLoteReposicao(ScoLoteReposicao lote, List<ItemReposicaoMaterialVO> listaInclusaoPontual) throws BaseException;

	void excluirItemLote(ScoLoteReposicao loteReposicao, Integer seq) throws BaseException;

	Long pesquisarMaterialReposicaoCount(ScoLoteReposicao lote);
}