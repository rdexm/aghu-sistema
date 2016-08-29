package br.gov.mec.aghu.nutricao.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuItemGrupoQuadroDieta;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.AnuTipoItemDietasJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.nutricao.dao.AnuHabitoAlimAtendimentoDAO;
import br.gov.mec.aghu.nutricao.dao.AnuItemGrupoQuadroDietaDAO;
import br.gov.mec.aghu.nutricao.dao.AnuTipoItemDietaDAO;
import br.gov.mec.aghu.nutricao.dao.AnuTipoItemDietaUnfsDAO;
import br.gov.mec.aghu.nutricao.dao.AnuTipoItemDietasJnDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AnuTipoItemDietaVO;

/**
 * Porta de entrada para a camada de negócio do módulo de nutrição.
 * 
 * @author gmneto
 * 
 */

@Modulo(ModuloEnum.NUTRICAO)
@Stateless
public class NutricaoFacade extends BaseFacade implements INutricaoFacade {


	@EJB
	private TipoItemDietaUnidadeFuncionalRN tipoItemDietaUnidadeFuncionalRN;
	
	@Inject
	private AnuTipoItemDietaDAO anuTipoItemDietaDAO;
	
	@Inject
	private AnuHabitoAlimAtendimentoDAO anuHabitoAlimAtendimentoDAO;
	
	@Inject
	private AnuTipoItemDietaUnfsDAO anuTipoItemDietaUnfsDAO;
	
	@Inject
	private AnuTipoItemDietasJnDAO anuTipoItemDietasJnDAO;
	
	@Inject
	private AnuItemGrupoQuadroDietaDAO anuItemGrupoQuadroDietaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5725178932363425721L;



	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.nutricao.business.INutricaoFacade#listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(br.gov.mec.aghu.model.AnuTipoItemDieta, br.gov.mec.aghu.model.AghUnidadesFuncionais)
	 */
	@Override
	@BypassInactiveModule
	public List<AnuTipoItemDietaUnfs> listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(
			AnuTipoItemDieta tipoItemDieta,
			AghUnidadesFuncionais unidadeFuncional) {

		return this.getTipoItemDietaUnidadeFuncionalRN()
				.listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(
						tipoItemDieta, unidadeFuncional);

	}

	@BypassInactiveModule
	public List<Date> executarCursorHaa(Integer atdSeq) {
		return this.getAnuHabitoAlimAtendimentoDAO().executarCursorHaa(atdSeq);
	}
	
	protected AnuHabitoAlimAtendimentoDAO getAnuHabitoAlimAtendimentoDAO() {
		return anuHabitoAlimAtendimentoDAO;
	}
	
	protected TipoItemDietaUnidadeFuncionalRN getTipoItemDietaUnidadeFuncionalRN() {
		return tipoItemDietaUnidadeFuncionalRN;
	}

	protected AnuTipoItemDietaDAO getAnuTipoItemDietaDAO() {
		return anuTipoItemDietaDAO;
	}

	@Override
	@BypassInactiveModule
	public AnuTipoItemDieta obterAnuTipoItemDietaPorChavePrimaria(Integer seq) {
		return getAnuTipoItemDietaDAO().obterPorChavePrimaria(seq);
	}
	
	/**
	 * Retorna tipos de itens de dieta ativos com suas unidades funcionais com o
	 * id ou descrição fornecidas.
	 * 
	 * @param descricaoOuId
	 * @return
	 */
	@Override
	public AnuTipoItemDieta obterTipoDietaComUnidadesFuncionais(
			final Integer idTipoDieta) {
		return this.getAnuTipoItemDietaDAO()
				.obterTipoDietaComUnidadesFuncionais(idTipoDieta);
	}
	
	/**
	 * Retorna tipos de itens de dieta ativos com o id ou descrição fornecidas.
	 * 
	 * @param descricaoOuId
	 * @return
	 */
	@Override
	public AnuTipoItemDieta obterTipoDieta(final Integer idTipoDieta, Enum... alias) {
		return this.getAnuTipoItemDietaDAO().obterTipoDieta(idTipoDieta, alias);
	}
	
	@Override
	@BypassInactiveModule
	public AnuTipoItemDieta obterTipoDietaEdicao(Integer idTipoDieta){
		return this.getAnuTipoItemDietaDAO().obterTipoDietaEdicao(idTipoDieta);
	}
	
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
	@Override
	@BypassInactiveModule
	public List<AnuTipoItemDieta> obterTiposItemDieta(final Object idOuDescricao,
			final AghUnidadesFuncionais unidade, final boolean neonatologia,
			final boolean adulto, final boolean pediatrico) {
		return this.getAnuTipoItemDietaDAO().obterTiposItemDieta(idOuDescricao,
				unidade, neonatologia, adulto, pediatrico);
	}
	
	/**
	 * Retorna count de itens de tipo de dieta com os argumentos fornecidos.
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
	@Override
	@BypassInactiveModule
	public Long obterTiposItemDietaCount(final Object idOuDescricao,
			final AghUnidadesFuncionais unidade, final boolean neonatologia,
			final boolean adulto, final boolean pediatrico) {
		return this.getAnuTipoItemDietaDAO().obterTiposItemDietaCount(idOuDescricao,
				unidade, neonatologia, adulto, pediatrico);
	}

	@Override
	@BypassInactiveModule
	public void inserirAtualizarListaUnidadesTipoDieta(
			AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs) {
		this.getAnuTipoItemDietaUnfsDAO()
				.inserirAtualizarListaUnidadesTipoDieta(tipoDieta,
						listaAnuTipoItemDietaUnfs);
	}
	
	@Override
	@BypassInactiveModule
	public void inserirAtualizarListaUnidadesTipoDieta(
			AnuTipoItemDieta tipoDieta,
			List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas,
			List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc) {
		this.getAnuTipoItemDietaUnfsDAO()
				.inserirAtualizarListaUnidadesTipoDieta(tipoDieta,
						listaUnidadeFuncAdicionadas, listaExcluiUnidadeFunc);
	}
	
	@Override
	@BypassInactiveModule
	public List<AnuTipoItemDietaUnfs> obterUnfsPorTipoDietaSeq(AnuTipoItemDieta tipoItemDieta) {
		return this.getAnuTipoItemDietaUnfsDAO().obterUnfsPorTipoDietaSeq(tipoItemDieta);
	}

	@Override
	@BypassInactiveModule
	public List<AnuTipoItemDieta> obterTiposItemDieta(Object idOuDescricao) {
		return this.getAnuTipoItemDietaDAO().obterTiposItemDieta(idOuDescricao);
	}
	
	@Override
	@BypassInactiveModule
	public AnuTipoItemDietaUnfs obterAnuTipoItemDietaUnfsPorId(Integer seq) {
		return this.getAnuTipoItemDietaUnfsDAO().obterPorChavePrimaria(seq);
	}

	@Override
	@BypassInactiveModule
	public Long obterTiposItemDietaCount(Object idOuDescricao) {
		return this.getAnuTipoItemDietaDAO().obterTiposItemDietaCount(idOuDescricao);
	}
	
	@Override
	@BypassInactiveModule		
	public void inserirAnuTipoItemDieta(AnuTipoItemDieta tipoDieta) {
		this.getAnuTipoItemDietaDAO().persistir(tipoDieta);
		this.getAnuTipoItemDietaDAO().flush();
	}

	@Override
	@BypassInactiveModule	
	public void atualizarAnuTipoItemDietaDepreciado(AnuTipoItemDieta tipoDieta) {
		this.getAnuTipoItemDietaDAO().merge(tipoDieta);
		this.getAnuTipoItemDietaDAO().flush();
	}

	@Override
	@BypassInactiveModule	
	public void desatacharAnuTipoItemDieta(AnuTipoItemDieta tipoDieta) {
		this.getAnuTipoItemDietaDAO().desatachar(tipoDieta);
	}

	@Override
	public void removerAnuTipoItemDieta(AnuTipoItemDieta tipoDieta) {
		this.getAnuTipoItemDietaDAO().remover(tipoDieta);
		this.getAnuTipoItemDietaDAO().flush();
	}

	@Override
	public boolean existeItemDieta(AnuTipoItemDieta tipoDieta, Class class1,
			Enum field) {
		return this.getAnuTipoItemDietaDAO().existeItemDieta(tipoDieta, class1,
				field);
	}

	@Override
	@BypassInactiveModule
	public List<AnuTipoItemDietaVO> pesquisarTipoItemDieta(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao,
			DominioSimNao usoNutricao) {
		return this.getAnuTipoItemDietaDAO().pesquisarTipoItemDieta(
				firstResult, maxResult, orderProperty, asc, codigo, descricao,
				situacao, usoNutricao);
	}

	@Override
	@BypassInactiveModule
	public Long pesquisarTipoItemDietaCount(Integer codigo,
			String descricao, DominioSituacao situacao,
			DominioSimNao usuNutricao) {
		return this.getAnuTipoItemDietaDAO().pesquisarTipoItemDietaCount(
				codigo, descricao, situacao, usuNutricao);
	}

	@Override
	@BypassInactiveModule
	public void inserirAnuTipoItemDietasJn(AnuTipoItemDietasJn anuTipoItemDietasJn) {
		this.getAnuTipoItemDietasJnDAO().persistir(anuTipoItemDietasJn);
		this.getAnuTipoItemDietasJnDAO().flush();
	}

	protected AnuTipoItemDietaUnfsDAO getAnuTipoItemDietaUnfsDAO() {
		return anuTipoItemDietaUnfsDAO;
	}
	
	protected AnuTipoItemDietasJnDAO getAnuTipoItemDietasJnDAO() {
		return anuTipoItemDietasJnDAO;
	}

	@Override
	@BypassInactiveModule
	public AnuItemGrupoQuadroDieta buscaGrupoDietaPorRefeicao(Short codigoRefeicao){
		return anuItemGrupoQuadroDietaDAO.buscaGrupoDietaPorRefeicao(codigoRefeicao);
	}
	
	@Override
	@BypassInactiveModule
	public AnuItemGrupoQuadroDieta buscaGrupoDietaHabitoAlimentar(Short codigoHabitoAlimentar){
		return anuItemGrupoQuadroDietaDAO.buscaGrupoDietaHabitoAlimentar(codigoHabitoAlimentar);
	}
	
	/**
	 * Exclui os vinculos de tipos de dietas com unidades.
	 * 
	 * @param anuTipoItemDietaUnfs
	 */
	@Override
	@BypassInactiveModule
	public void excluir(AnuTipoItemDietaUnfs anuTipoItemDietaUnfs) {
		getAnuTipoItemDietaUnfsDAO().apagar(anuTipoItemDietaUnfs);
	}

	/**
	 * inserir todos tipos de dietas em todas unidades.
	 * 
	 * @param rapServidores
	 * @throws AGHUNegocioException
	 */
	@Override
	@BypassInactiveModule
	public void inserirTodosTiposDietasUnf(RapServidores rapServidores) throws ApplicationBusinessException {
		this.getAnuTipoItemDietaUnfsDAO().inserirTodosTiposDietasUnf(rapServidores);
	}	

	/**
	 * inserir tipos de dietas em todas unidades.
	 * 
	 * @param unidadeFuncional
	 * @param rapServidores
	 * @throws AGHUNegocioException
	 */
	@Override
	@BypassInactiveModule
	public void inserirTiposDietasUnf(AghUnidadesFuncionais unidadeFuncional, RapServidores rapServidores) throws ApplicationBusinessException {
		this.getAnuTipoItemDietaUnfsDAO().inserirTiposDietasUnf(unidadeFuncional, rapServidores);
	}	

	@Override
	@BypassInactiveModule
	public List<AnuTipoItemDietaUnfs> pesquisarTipoDieta(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			AghUnidadesFuncionais unidadeFuncional) {
		return this.getAnuTipoItemDietaUnfsDAO()
				.listarAnuTipoItemDietaUnfsPorUnidadeFuncional(firstResult,
						maxResults, orderProperty, asc, unidadeFuncional);
	}

	@Override
	@BypassInactiveModule
	public Integer pesquisarTipoDietaCount(
			AghUnidadesFuncionais unidadeFuncional) {
		return this.getAnuTipoItemDietaUnfsDAO().listarAnuTipoItemDietaUnfsCount(
				unidadeFuncional);
	}
	

}
