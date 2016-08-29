package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AipNacionalidadesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipNacionalidades> {
	
	private static final long serialVersionUID = 4311626672420821394L;

	public List<AipNacionalidades> pesquisaNacionalidades(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String sigla, String descricao,
			DominioSituacao situacao) {
		if (StringUtils.isBlank(orderProperty)) {
			orderProperty = AipNacionalidades.Fields.DESCRICAO.toString();
		}

		DetachedCriteria criteria = createPesquisaNacionalidadesCriteria(
				orderProperty, asc, codigo, sigla, descricao, situacao);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Long pesquisaNacionalidadesCount(Integer codigo, String sigla,
			String descricao, DominioSituacao situacao) {
		return executeCriteriaCount(createPesquisaNacionalidadesCriteria(null,
				null, codigo, sigla, descricao, situacao));
	}

	private DetachedCriteria createPesquisaNacionalidadesCriteria(
			String orderProperty, Boolean asc, Integer codigo, String sigla,
			String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipNacionalidades.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AipNacionalidades.Fields.CODIGO
					.toString(), codigo));
		}

		if (StringUtils.isNotBlank(sigla)) {
			criteria.add(Restrictions.ilike(AipNacionalidades.Fields.SIGLA
					.toString(), sigla, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AipNacionalidades.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(AipNacionalidades.Fields.IND_ATIVO
					.toString(), situacao));
		}

		if (StringUtils.isNotBlank(orderProperty)) {
			criteria.addOrder((asc || asc == null) ? Order.asc(orderProperty) : Order
					.desc(orderProperty));
		}

		return criteria;
	}
	
	public List<AipNacionalidades> getNacionalidadesComMesmaSigla(
			AipNacionalidades nacionalidade) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipNacionalidades.class);

		criteria.add(Restrictions.ilike(AipNacionalidades.Fields.SIGLA
				.toString(), nacionalidade.getSigla(), MatchMode.EXACT));

		// Garante que não será comparado a sigla da nacionalidade sendo
		// editada
		if (nacionalidade.getCodigo() != null) {
			criteria.add(Restrictions.ne(AipNacionalidades.Fields.CODIGO
					.toString(), nacionalidade.getCodigo()));
		}

		return this.executeCriteria(criteria);
	}

	public List<AipNacionalidades> getNacionalidadesComMesmoCodigo(
			AipNacionalidades nacionalidade) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipNacionalidades.class);

		criteria.add(Restrictions.eq(
				AipNacionalidades.Fields.CODIGO.toString(), nacionalidade
						.getCodigo()));

		return this.executeCriteria(criteria);
	}

	public List<AipNacionalidades> getNacionalidadesComMesmaDescricao(
			AipNacionalidades nacionalidade) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipNacionalidades.class);

		criteria.add(Restrictions.ilike(AipNacionalidades.Fields.DESCRICAO
				.toString(), nacionalidade.getDescricao(), MatchMode.EXACT));

		// Garante que não será comparado a sigla da nacionalidade sendo
		// editada
		if (nacionalidade.getCodigo() != null) {
			criteria.add(Restrictions.ne(AipNacionalidades.Fields.CODIGO
					.toString(), nacionalidade.getCodigo()));
		}

		return this.executeCriteria(criteria);
	}
	
	public Long pesquisarCountPorCodigoSiglaNome(Object paramPesquisa) {

		DetachedCriteria cri = DetachedCriteria
				.forClass(AipNacionalidades.class);

		// Pesquisar por nacionalidades que estejam em situacao ativa.
		cri.add(Restrictions.eq(AipNacionalidades.Fields.IND_ATIVO.toString(),
				DominioSituacao.A));
		//cri.addOrder(Order.asc(AipNacionalidades.Fields.DESCRICAO.toString()));

		if (StringUtils.isNotBlank((String) paramPesquisa)) {
			if (CoreUtil.isNumeroInteger(paramPesquisa)) {
				Integer integerPesquisa = Integer
						.valueOf((String) paramPesquisa);
				cri.add(Restrictions.eq(AipNacionalidades.Fields.CODIGO
						.toString(), integerPesquisa));
			} else {
				String strPesquisa = (String) paramPesquisa;
				cri.add(Restrictions.or(Restrictions.like(
						AipNacionalidades.Fields.SIGLA.toString(),
						((String) strPesquisa).toUpperCase(),
						MatchMode.ANYWHERE), Restrictions.like(
						AipNacionalidades.Fields.DESCRICAO.toString(),
						((String) strPesquisa).toUpperCase(),
						MatchMode.ANYWHERE)));
			}
		}

		return executeCriteriaCount(cri);
	}

	
	/**
	 * Método para listar as Nacionalidades em um combo de acordo com o
	 * parâmetro informado. O parâmetro pode ser tanto a sigla, parte da
	 * descrição da nacionalidade quanto o código da mesma.
	 * 
	 * @param paramPesquisa
	 * @return li
	 */
	public List<AipNacionalidades> pesquisarNacionalidadesInclusiveInativasPorCodigoSiglaNome(
			Object paramPesquisa) {

		DetachedCriteria cri = DetachedCriteria
				.forClass(AipNacionalidades.class);

		cri.addOrder(Order.asc(AipNacionalidades.Fields.DESCRICAO.toString()));

		if (StringUtils.isNotBlank((String) paramPesquisa)) {
			if (CoreUtil.isNumeroInteger(paramPesquisa)) {
				Integer integerPesquisa = Integer
						.valueOf((String) paramPesquisa);
				cri.add(Restrictions.eq(AipNacionalidades.Fields.CODIGO
						.toString(), integerPesquisa));
			} else {
				String strPesquisa = (String) paramPesquisa;
				cri.add(Restrictions.or(Restrictions.like(
						AipNacionalidades.Fields.SIGLA.toString(),
						((String) strPesquisa).toUpperCase(),
						MatchMode.ANYWHERE), Restrictions.like(
						AipNacionalidades.Fields.DESCRICAO.toString(),
						((String) strPesquisa).toUpperCase(),
						MatchMode.ANYWHERE)));
			}
		}

		List<AipNacionalidades> li = executeCriteria(cri);
		return li;
	}

	public List<AipNacionalidades> pesquisarNacionalidadesPessoaFisica(
			String valor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipNacionalidades.class);

		if (StringUtils.isNotBlank(valor)) {
			if (CoreUtil.isNumeroInteger(valor)) {
				Integer integerPesquisa = Integer.valueOf(valor);
				criteria.add(Restrictions.eq(AipNacionalidades.Fields.CODIGO.toString(), integerPesquisa));
			} else {
				criteria.add(Restrictions.ilike(AipNacionalidades.Fields.SIGLA.toString(), valor, MatchMode.EXACT));
				List<AipNacionalidades> resultPorSigla = executeCriteria(criteria);
				if (resultPorSigla.size() == 1) {
					return resultPorSigla;
				}
				criteria = DetachedCriteria.forClass(AipNacionalidades.class);
				criteria.add(Restrictions.ilike(AipNacionalidades.Fields.DESCRICAO.toString(), valor, MatchMode.ANYWHERE));
			}
		}
		
		List<AipNacionalidades> li = executeCriteria(criteria);
		return li;
	}
	
	/**
	 * Método para listar as Nacionalidades em um combo de acordo com o
	 * parâmetro informado. O parâmetro pode ser tanto a sigla, parte da
	 * descrição da nacionalidade quanto o código da mesma.
	 * 
	 * @param paramPesquisa
	 * @return li
	 */
	public List<AipNacionalidades> pesquisarPorCodigoSiglaNome(
			String paramPesquisa) {

		DetachedCriteria cri = DetachedCriteria
				.forClass(AipNacionalidades.class);

		// Pesquisar por nacionalidades que estejam em situacao ativa.
		cri.add(Restrictions.eq(AipNacionalidades.Fields.IND_ATIVO.toString(),
				DominioSituacao.A));
		cri.addOrder(Order.asc(AipNacionalidades.Fields.DESCRICAO.toString()));

		if (StringUtils.isNotBlank( paramPesquisa)) {
			if (CoreUtil.isNumeroInteger(paramPesquisa)) {
				Integer integerPesquisa = Integer
						.valueOf( paramPesquisa);
				cri.add(Restrictions.eq(AipNacionalidades.Fields.CODIGO
						.toString(), integerPesquisa));
			} else {
				cri.add(Restrictions.or(Restrictions.like(
						AipNacionalidades.Fields.SIGLA.toString(),
						(paramPesquisa).toUpperCase(),
						MatchMode.ANYWHERE), Restrictions.like(
						AipNacionalidades.Fields.DESCRICAO.toString(),
						(paramPesquisa).toUpperCase(),
						MatchMode.ANYWHERE)));
			}
		}

		List<AipNacionalidades> li = executeCriteria(cri, 0, 100, null, true);
		return li;
	}
	
	public AipNacionalidades obterNacionalidadePorCodigoAtiva(Integer codigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipNacionalidades.class);

		// Pesquisar por nacionalidades que estejam em situacao ativa.
		criteria.add(Restrictions.eq(AipNacionalidades.Fields.IND_ATIVO.toString(),
				DominioSituacao.A));
		criteria.add(Restrictions.eq(AipNacionalidades.Fields.CODIGO
				.toString(), codigo));

		return (AipNacionalidades)executeCriteriaUniqueResult(criteria);
	}

	/**
	 * #36463 C2
	 * Consulta para obter descrição de nacionalidade por código.
	 */
	public String obterDescricaoNacionalidadePorCodigo(Integer codigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipNacionalidades.class);
		
		criteria.setProjection(Projections.property(AipNacionalidades.Fields.DESCRICAO.toString()));
		criteria.add(Restrictions.eq(AipNacionalidades.Fields.CODIGO.toString(), codigo));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
}
