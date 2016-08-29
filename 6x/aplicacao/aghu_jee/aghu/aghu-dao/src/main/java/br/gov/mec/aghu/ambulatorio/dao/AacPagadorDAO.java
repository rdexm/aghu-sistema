/**
 * 
 */
package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @author marcelofilho
 *
 */
public class AacPagadorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacPagador> {

	private static final long serialVersionUID = -4386251224915938729L;

	/**
	 * @param codigo
	 * @return
	 */
	public AacPagador obterPagador(Short codigo) {
		if (codigo == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		DetachedCriteria criteria = createPesquisaPorCodigoCriteria(codigo);

		return (AacPagador) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método auxiliar que cria DetachedCriteria a partir do codigo. Código é
	 * AK da tabela AAC_PAGADORES.
	 * 
	 * @param codigo
	 * @return DetachedCriteria.
	 */
	private DetachedCriteria createPesquisaPorCodigoCriteria(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPagador.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AacPagador.Fields.SEQ.toString(),
					codigo));
		}

		return criteria;
	}

	public List<AacPagador> pesquisarPagadores(String filtro) {
		DetachedCriteria criteria = createPesquisaPagadoresCriteria(filtro);
		return executeCriteria(criteria, 0, 25, null, false);
	}

	/**
	 * Ordenação por descrição e quantidade máxima de resgistro 100
	 * @param filtro
	 * @return
	 */
	public List<AacPagador> obterListaPagadores(String filtro) {
		DetachedCriteria criteria = createPesquisaPagadoresCriteria(filtro);
		return executeCriteria(criteria, 0, 100, AacPagador.Fields.DESCRICAO.toString(), true);
	}

	public Long pesquisarPagadoresCount(String filtro) {
		DetachedCriteria criteria = createPesquisaPagadoresCriteria(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createPesquisaPagadoresCriteria(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPagador.class);

		if (StringUtils.isNotBlank(filtro)) {
			Criterion descricaoCriterion = Restrictions.ilike(AacPagador.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.or(Restrictions.eq(AacPagador.Fields.SEQ.toString(), Short.valueOf(filtro)),
						descricaoCriterion));
			} else {
				criteria.add(descricaoCriterion);
			}
		}

		return criteria;
	}

	public List<AacPagador> pesquisarPagadorPaginado(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Short codigoOrgaoPagador, String descricaoOrgaoPagador,
			DominioSituacao situacaoOrgaoPagador,
			DominioGrupoConvenio convenioOrgaoPagador) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacPagador.class);
		criteria = this.obterCriteriaPesquisaPagadorPaginado(
				codigoOrgaoPagador, descricaoOrgaoPagador,
				situacaoOrgaoPagador, convenioOrgaoPagador, criteria);

		criteria.addOrder(Order.asc(AacPagador.Fields.SEQ.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	public Long countPesquisarPagadorPaginado(Short filtroSeq,
			String filtroDescricao, DominioSituacao filtroSituacao,
			DominioGrupoConvenio filtroConvenio) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacPagador.class);
		criteria = this.obterCriteriaPesquisaPagadorPaginado(filtroSeq,
				filtroDescricao, filtroSituacao, filtroConvenio, criteria);

		return executeCriteriaCount(criteria);
	}

	protected DetachedCriteria obterCriteriaPesquisaPagadorPaginado(
			Short filtroSeq, String filtroDescricao,
			DominioSituacao filtroSituacao,
			DominioGrupoConvenio filtroConvenio, DetachedCriteria criteria) {

		if (filtroSeq != null) {
			criteria.add(Restrictions.eq(AacPagador.Fields.SEQ.toString(),
					filtroSeq));
		}
		if (filtroDescricao != null) {
			criteria.add(Restrictions.ilike(
					AacPagador.Fields.DESCRICAO.toString(), filtroDescricao,
					MatchMode.ANYWHERE));
		}
		if (filtroSituacao != null) {
			criteria.add(Restrictions.eq(AacPagador.Fields.SITUACAO.toString(),
					filtroSituacao));
		}
		if (filtroConvenio != null) {
			criteria.add(Restrictions.eq(
					AacPagador.Fields.GRUPO_CONVENIO.toString(), filtroConvenio));
		}
		return criteria;
	}

	/**
	 * Verifica se existe ao menos um registro com os códigos informados.
	 * 
	 * @param seqPagador - Código do Pagador
	 * @param seqPagadorSus - Código do Pagador SUS
	 * @return Flag indicando a existência do registro solicitado
	 */
	public DominioSimNao verificarExistenciaPagadorSus(Short seqPagador, Short seqPagadorSus) {

		DominioSimNao retorno = DominioSimNao.N;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPagador.class);
		
		criteria.add(Restrictions.eq(AacPagador.Fields.SEQ.toString(), seqPagador));
		criteria.add(Restrictions.eq(AacPagador.Fields.SEQ.toString(), seqPagadorSus));
		
		if (executeCriteriaExists(criteria)) {
			retorno = DominioSimNao.S;
		}
		
		return retorno;
	}
	
	public List<AacPagador> listarPagadoresAtivos(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacPagador.class);
		criteria.add(Restrictions.eq(AacPagador.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AacPagador.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
}
