package br.gov.mec.aghu.nutricao.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuItemGrupoQuadroDieta;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.AnuTipoItemDietasJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AnuTipoItemDietaVO;

public interface INutricaoFacade extends Serializable {

	/**
	 * Lista AnuTipoItemDietaUnfs de acordo com a unidade funcional e o tipo de
	 * dieta.
	 * 
	 * @param tipoItemDieta
	 * @param unidadeFuncional
	 * @return
	 */
	List<AnuTipoItemDietaUnfs> listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(
			AnuTipoItemDieta tipoItemDieta,
			AghUnidadesFuncionais unidadeFuncional);

	public List<Date> executarCursorHaa(Integer atdSeq);
	
	public AnuTipoItemDieta obterAnuTipoItemDietaPorChavePrimaria(Integer seq);

	/**
	 * Retorna tipos de itens de dieta ativos com suas unidades funcionais com o
	 * id ou descrição fornecidas.
	 * 
	 * @param descricaoOuId
	 * @return
	 */
	public AnuTipoItemDieta obterTipoDietaComUnidadesFuncionais(final Integer idTipoDieta);
	
	/**
	 * Retorna tipos de itens de dieta com o id ou descrição fornecidas.
	 * 
	 * @param idTipoDieta
	 * @return
	 */
	public AnuTipoItemDieta obterTipoDieta(final Integer idTipoDieta, Enum... alias);
	
	public AnuTipoItemDieta obterTipoDietaEdicao(Integer idTipoDieta);
	
	/**
	 * Retorna itens de tipo de dieta com os argumentos fornecidos.
	 * 
	 * @param idOuDescricao
	 *            id ou descrição
	 * @param unidade
	 *            unidade funcional é opcional
	 * @param neonatologia
	 *            dietas para a unidade de neonatologia
	 * @param adulto
	 *            dietas para pacientes adultos
	 * @param pediatrico
	 *            dietas para pacientes pediatricos
	 * @return
	 */
	public List<AnuTipoItemDieta> obterTiposItemDieta(
			final Object idOuDescricao, final AghUnidadesFuncionais unidade,
			final boolean neonatologia, final boolean adulto,
			final boolean pediatrico);
	
	public Long obterTiposItemDietaCount(
			final Object idOuDescricao, final AghUnidadesFuncionais unidade,
			final boolean neonatologia, final boolean adulto,
			final boolean pediatrico);
	
	public void inserirAtualizarListaUnidadesTipoDieta(
			AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs);
	
	List<AnuTipoItemDietaUnfs> obterUnfsPorTipoDietaSeq(AnuTipoItemDieta tipoItemDieta);

	public Long obterTiposItemDietaCount(Object idOuDescricao);
	
	public List<AnuTipoItemDieta> obterTiposItemDieta(Object idOuDescricao);

	void inserirAnuTipoItemDieta(AnuTipoItemDieta tipoDieta);

	void atualizarAnuTipoItemDietaDepreciado(AnuTipoItemDieta tipoDieta);

	void desatacharAnuTipoItemDieta(AnuTipoItemDieta tipoDieta);

	void removerAnuTipoItemDieta(AnuTipoItemDieta tipoDieta);

	public boolean existeItemDieta(AnuTipoItemDieta tipoDieta, Class class1,
			Enum field);

	public List<AnuTipoItemDietaVO> pesquisarTipoItemDieta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao,
			DominioSimNao usoNutricao);

	public Long pesquisarTipoItemDietaCount(Integer codigo,
			String descricao, DominioSituacao situacao,
			DominioSimNao usuNutricao);

	void inserirAnuTipoItemDietasJn(AnuTipoItemDietasJn anuTipoItemDietasJn);
	
	public AnuItemGrupoQuadroDieta buscaGrupoDietaPorRefeicao(Short codigoRefeicao);
	
	public AnuItemGrupoQuadroDieta buscaGrupoDietaHabitoAlimentar(Short codigoHabitoAlimentar);
	
	void excluir(AnuTipoItemDietaUnfs anuTipoItemDietaUnfs);

	void inserirTodosTiposDietasUnf(RapServidores rapServidores)
			throws ApplicationBusinessException;

	void inserirTiposDietasUnf(AghUnidadesFuncionais unidadeFuncional,
			RapServidores rapServidores) throws ApplicationBusinessException;

	List<AnuTipoItemDietaUnfs> pesquisarTipoDieta(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncional);

	Integer pesquisarTipoDietaCount(AghUnidadesFuncionais unidadeFuncional);
	
	AnuTipoItemDietaUnfs obterAnuTipoItemDietaUnfsPorId(Integer seq);

	void inserirAtualizarListaUnidadesTipoDieta(AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas,
			List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc);
}