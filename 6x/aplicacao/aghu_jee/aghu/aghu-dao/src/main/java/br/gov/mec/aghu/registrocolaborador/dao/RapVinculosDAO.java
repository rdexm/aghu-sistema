package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.CsePerfisVinculos;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapVinculosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapVinculos> {
	
	private static final long serialVersionUID = -3030681267292710452L;

	private enum VinculoDAOExceptionCode implements BusinessExceptionCode {
		MENSAGEM_VINCULO_NAO_INFORMADO,
		MENSAGEM_ERRO_DESCRICAO_VINCULO_JA_EXISTE,
		MENSAGEM_ERRO_CODIGO_VINCULO_JA_EXISTE,
		MENSAGEM_PARAMETRO_NAO_INFORMADO;
	}
	
	protected RapVinculosDAO() {
	}
	
	/**
	 * Método para buscar um vínculo na base de dados
	 */
	public RapVinculos obterVinculo(Short codigoVinculo)
			throws ApplicationBusinessException {
		if (codigoVinculo == null) {
			throw new ApplicationBusinessException(
					VinculoDAOExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		return obterPorChavePrimaria(codigoVinculo);
	}	

	/**
	 * Método para pesquisar vínculos na base de dados
	 */
	public List<RapVinculos> pesquisarVinculos(Short codigo, String descricao,
			DominioSituacao indSituacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		DetachedCriteria criteria = montarCriteria(codigo, descricao,
				indSituacao);

		return executeCriteria(criteria, firstResult, maxResult,
				RapVinculos.Fields.CODIGO.toString(), true);
	}
	
	/**
	 * Método que monta as restrições presentes na criteria
	 */
	private DetachedCriteria montarCriteria(Short codigo, String descricao,
			DominioSituacao indSituacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapVinculos.class);

		// código
		if (codigo != null) {
			criteria.add(Restrictions.eq(RapVinculos.Fields.CODIGO.toString(),
					codigo));
		}

		// descrição
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.like(
					RapVinculos.Fields.DESCRICAO.toString(), descricao,
					MatchMode.ANYWHERE));
		}

		// situação (Ativo/Inativo)
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(
					RapVinculos.Fields.IND_SITUACAO.toString(), indSituacao));
		}

		return criteria;
	}
	
	/**
	 * Método para contar a quantidade de vínculos pesquisados
	 */
	public Long pesquisarVinculosCount(Short codigo, String descricao,
			DominioSituacao indSituacao) {
		DetachedCriteria criteria = montarCriteria(codigo, descricao,
				indSituacao);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Método para alterar um vínculo
	 */
	
	public void alterar(RapVinculos vinculo) throws ApplicationBusinessException {
		if (vinculo == null) {
			throw new ApplicationBusinessException(
					VinculoDAOExceptionCode.MENSAGEM_VINCULO_NAO_INFORMADO);
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapVinculos.class);
		criteria.add(Restrictions.eq(RapVinculos.Fields.DESCRICAO.toString(),
				vinculo.getDescricao()));
		criteria.add(Restrictions.not(Restrictions.eq(
				RapVinculos.Fields.CODIGO.toString(), vinculo.getCodigo())));

		if (executeCriteriaCount(criteria) > 0) {
			throw new ApplicationBusinessException(
					VinculoDAOExceptionCode.MENSAGEM_ERRO_DESCRICAO_VINCULO_JA_EXISTE);
		}

		merge(vinculo);
		flush();
	}
	
	/**
	 * Método para salvar um novo vínculo
	 */
	
	public void salvar(RapVinculos vinculo) throws ApplicationBusinessException {

		if (vinculo == null) {
			throw new ApplicationBusinessException(
					VinculoDAOExceptionCode.MENSAGEM_VINCULO_NAO_INFORMADO);
		}

		if (obterVinculo(vinculo.getCodigo()) != null) {
			throw new ApplicationBusinessException(
					VinculoDAOExceptionCode.MENSAGEM_ERRO_CODIGO_VINCULO_JA_EXISTE);
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapVinculos.class);
		criteria.add(Restrictions.eq(RapVinculos.Fields.DESCRICAO.toString(),
				vinculo.getDescricao()));

		if (executeCriteriaCount(criteria) > 0) {
			throw new ApplicationBusinessException(
					VinculoDAOExceptionCode.MENSAGEM_ERRO_DESCRICAO_VINCULO_JA_EXISTE);
		}

		persistir(vinculo);
		flush();
	}
	
	/**
	 * Retorna vínculos de acrodo com o código ou descrição informados por ordem
	 * de codigo
	 * 
	 * @dbtables RapVinculos select
	 * 
	 * @param vinculo
	 *            ou descricao
	 * @return vínculos encontrados lista vazia se não encontrado
	 */
	public List<RapVinculos> pesquisarVinculoPorCodigoDescricao(Object vinculo,
			boolean somenteAtivo) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapVinculos.class);

		String stParametro = (String) vinculo;
		Short codigo = null;

		if (CoreUtil.isNumeroShort(stParametro)) {
			codigo = Short.valueOf(stParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(RapVinculos.Fields.CODIGO.toString(),
					codigo));
		} else {
			criteria.add(Restrictions.ilike(
					RapVinculos.Fields.DESCRICAO.toString(), stParametro,
					MatchMode.ANYWHERE));
		}

		if (somenteAtivo) {
			criteria.add(Restrictions.eq(
					RapVinculos.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));
		}

		criteria.addOrder(Order.asc(RapVinculos.Fields.CODIGO.toString()));

		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	/**
	 * Obtem os vínculos cadastrados para o usuario.
	 * 
	 * @param username
	 *            login do usuario
	 * @return Lista de perfis que este usuario possui
	 */
	public List<RapVinculos> obterPerfisVinculos(Collection<String> perfisUsuario) {
		DetachedCriteria criteriaPerfisVinculos = DetachedCriteria.forClass(CsePerfisVinculos.class);

		criteriaPerfisVinculos.add(Restrictions.like(CsePerfisVinculos.Fields.ID_PER_NOME.toString(), "RAPG", MatchMode.START));

		if (perfisUsuario != null && !perfisUsuario.isEmpty()) {
			String[] perfis = perfisUsuario.toArray(new String[perfisUsuario.size()]);
			criteriaPerfisVinculos.add(Restrictions.in(CsePerfisVinculos.Fields.CSE_PERFIS.toString(),perfis));
		}

		criteriaPerfisVinculos.setProjection(Projections
				.property(CsePerfisVinculos.Fields.RAP_VINCULOS.toString()));

		return executeCriteria(criteriaPerfisVinculos);
	}
	
	public List<RapVinculos> pesquisarVinculosFuncionario(Object vinculo){
		DetachedCriteria criteria = montarQueryVinculosFuncionario(vinculo);
		criteria.addOrder(Order.asc(RapVinculos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria); 
	}
	
	public Long pesquisarVinculosFuncionarioCount(Object vinculo) {
		return executeCriteriaCount(montarQueryVinculosFuncionario(vinculo));
	}
	
	private DetachedCriteria montarQueryVinculosFuncionario(Object vinculo){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(RapVinculos.class);

		String stParametro = (String) vinculo;
		Short codigo = null;

		if (CoreUtil.isNumeroShort(stParametro)) {
			codigo = Short.valueOf(stParametro);
		}

		if (codigo != null) {
			criteria.add(Restrictions.eq(RapVinculos.Fields.CODIGO.toString(),
					Short.valueOf(codigo)));
		} else {
			criteria.add(Restrictions.ilike(
					RapVinculos.Fields.DESCRICAO.toString(), stParametro,
					MatchMode.ANYWHERE));
		}		
		
		return criteria;
	}
	
	/**
     * <p>{@code Estoria} <b>#8674</b></p> <p>Obtem lista de vinculos para suggestion box.</p> 
	 * 
	 * @param parametro utilizado na consulta.
	 * @return Lista de {@link RapVinculos}.
	 */
	public List<RapVinculos> obterListaVinculoPorCodigoOuDescricao(String parametro) {
		DetachedCriteria criteria = obterCriteriaVinculoPorCodigoOuDescricao(parametro);
		return executeCriteria(criteria, 0, 100, RapVinculos.Fields.DESCRICAO.toString(), true);
	}

	/**
     * <p>{@code Estoria} <b>#8674</b></p> <p>Obtem count de vinculos para suggestion box.</p> 
	 * 
	 * @param parametro utilizado na consulta.
	 * @return Quantidade de Registros retornados.
	 */
	public Long obterCountVinculoPorCodigoOuDescricao(String parametro) {
		DetachedCriteria criteria = obterCriteriaVinculoPorCodigoOuDescricao(parametro);
		return executeCriteriaCount(criteria);
	}

	

	private DetachedCriteria obterCriteriaVinculoPorCodigoOuDescricao(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapVinculos.class);		
		if (StringUtils.isNotBlank(parametro)) {			
			if (CoreUtil.isNumeroShort(parametro)) {				
				criteria.add(Restrictions.eq(RapVinculos.Fields.CODIGO.toString(), Short.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(RapVinculos.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

}
