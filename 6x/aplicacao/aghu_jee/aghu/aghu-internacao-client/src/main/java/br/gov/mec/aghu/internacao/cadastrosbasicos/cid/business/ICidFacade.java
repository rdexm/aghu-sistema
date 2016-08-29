package br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghGrupoCids;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface ICidFacade extends Serializable {

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param numero
	 * @param descricao
	 * @param indExigeCidSecundario
	 * @param indSituacao
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#pesquisar(java.lang.Integer, java.lang.Integer, java.lang.Short, java.lang.String, br.gov.mec.aghu.dominio.DominioSimNao, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	
	public List<AghCapitulosCid> pesquisar(Integer firstResult,
			Integer maxResults, Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao);

	/**
	 * @param numero
	 * @param descricao
	 * @param indExigeCidSecundario
	 * @param indSituacao
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#obterCapituloCidCount(java.lang.Short, java.lang.String, br.gov.mec.aghu.dominio.DominioSimNao, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	public Long obterCapituloCidCount(Short numero, String descricao,
			DominioSimNao indExigeCidSecundario, DominioSituacao indSituacao);

	/**
	 * @param capituloCid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#persistirCapituloCid(br.gov.mec.aghu.model.AghCapitulosCid)
	 */
	
	public void persistirCapituloCid(AghCapitulosCid capituloCid)
			throws ApplicationBusinessException;

	/**
	 * @param seq
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#obterCapituloCid(java.lang.Integer)
	 */
	
	public AghCapitulosCid obterCapituloCid(Integer seq);

	/**
	 * @param capituloCidSeq
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#removerCapituloCid(br.gov.mec.aghu.model.AghCapitulosCid)
	 */	
	public void removerCapituloCid(Integer capituloCidSeq)
			throws ApplicationBusinessException;

	/**
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CapitulosCidCRUD#pesquisarCapitulosCidsAtivo()
	 */
	
	public List<AghCapitulosCid> pesquisarCapitulosCidsAtivo();

	/**
	 * @param codigo
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#obterCid(java.lang.String)
	 */
	
	public AghCid obterCid(String codigo);

	/**
	 * @param seq
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#obterCidporSeq(java.lang.Integer)
	 */
	
	public AghCid obterCidporSeq(Integer seq);

	/**
	 * @param seq
	 * @param codigo
	 * @param descricao
	 * @param situacaoPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#pesquisaCount(java.lang.Integer, java.lang.String, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	public Long pesquisaCount(Integer seq, String codigo, String descricao,
			DominioSituacao situacaoPesquisa);

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param seq
	 * @param codigo
	 * @param descricao
	 * @param situacaoPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#pesquisa(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, java.lang.Integer, java.lang.String, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	
	public List<AghCid> pesquisa(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, Integer seq, String codigo,
			String descricao, DominioSituacao situacaoPesquisa);

	/**
	 * @param cid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#excluirCid(br.gov.mec.aghu.model.AghCid)
	 */
	
	public void excluirCid(AghCid cid) throws ApplicationBusinessException;

	/**
	 * @param cid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#incluirCid(br.gov.mec.aghu.model.AghCid)
	 */
	
	public void incluirCid(AghCid cid) throws ApplicationBusinessException;

	/**
	 * @param cid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#atualizarCid(br.gov.mec.aghu.model.AghCid)
	 */
	
	public void atualizarCid(AghCid cid) throws ApplicationBusinessException;

	/**
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#pesquisarCidsSemSubCategoriaPorDescricaoOuId(java.lang.String, java.lang.Integer)
	 */
	
	public List<AghCid> pesquisarCidsSemSubCategoriaPorDescricaoOuId(
			String descricao, Integer limiteRegistros);

	/**
	 * @param descricao
	 * @param limiteRegistros
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#pesquisarCidsComSubCategoriaPorDescricaoOuId(java.lang.String, java.lang.Integer)
	 */
	
	public List<AghCid> pesquisarCidsComSubCategoriaPorDescricaoOuId(
			String descricao, Integer limiteRegistros);

	/**
	 * @return
	 *  
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.CidsON#nova()
	 */
	public AghCid nova() throws ApplicationBusinessException;

	/**
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param capitulo
	 * @param codigoGrupo
	 * @param siglaGrupo
	 * @param descricaoGrupo
	 * @param situacaoGrupo
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGruposCids(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, br.gov.mec.aghu.model.AghCapitulosCid, java.lang.Integer, java.lang.String, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	
	public List<AghGrupoCids> pesquisarGruposCids(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			AghCapitulosCid capitulo, Integer codigoGrupo, String siglaGrupo,
			String descricaoGrupo, DominioSituacao situacaoGrupo);

	/**
	 * @param capitulo
	 * @param codigoGrupo
	 * @param siglaGrupo
	 * @param descricaoGrupo
	 * @param situacaoGrupo
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGruposCidsCount(br.gov.mec.aghu.model.AghCapitulosCid, java.lang.Integer, java.lang.String, java.lang.String, br.gov.mec.aghu.dominio.DominioSituacao)
	 */
	public Long pesquisarGruposCidsCount(AghCapitulosCid capitulo,
			Integer codigoGrupo, String siglaGrupo, String descricaoGrupo,
			DominioSituacao situacaoGrupo);

	/**
	 * @param cpcSeq
	 * @param seq
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#obterGrupoCidPorId(java.lang.Integer, java.lang.Integer)
	 */
	
	public AghGrupoCids obterGrupoCidPorId(Integer cpcSeq, Integer seq);

	/**
	 * @param aghGrupoCid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#persistirGrupoCid(br.gov.mec.aghu.model.AghGrupoCids)
	 */
	
	
	public void persistirGrupoCid(AghGrupoCids aghGrupoCid)
			throws ApplicationBusinessException;

	/**
	 * @param aghGrupoCid
	 * @throws ApplicationBusinessException
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#removerGrupoCid(br.gov.mec.aghu.model.AghGrupoCids)
	 */
	
	
	public void removerGrupoCid(AghGrupoCids aghGrupoCid)
			throws ApplicationBusinessException;

	/**
	 * @param seqCapituloCid
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGrupoCidPorCapituloCid(java.lang.Integer)
	 */
	
	public List<AghGrupoCids> pesquisarGrupoCidPorCapituloCid(
			Integer seqCapituloCid);

	/**
	 * @param paramPesquisa
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#listarPorSigla(java.lang.Object)
	 */
	
	public List<AghGrupoCids> listarPorSigla(Object paramPesquisa);

	/**
	 * @param sigla
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGrupoCidSIGLA(java.lang.String)
	 */
	
	public List<AghGrupoCids> pesquisarGrupoCidSIGLA(String sigla);

	/**
	 * @param sigla
	 * @return
	 * @see br.gov.mec.aghu.internacao.cadastrosbasicos.cid.business.GrupoCidCRUD#pesquisarGrupoCidSIGLAS(java.lang.String)
	 */
	
	public List<AghGrupoCids> pesquisarGrupoCidSIGLAS(String sigla);

}