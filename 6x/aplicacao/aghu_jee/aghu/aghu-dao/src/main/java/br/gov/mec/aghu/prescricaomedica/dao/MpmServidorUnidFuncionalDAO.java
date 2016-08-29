package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmServidorUnidFuncional;
import br.gov.mec.aghu.model.RapServidores;

/**
 * @see MpmServidorUnidFuncional
 * 
 * @author cvagheti
 * 
 */
public class MpmServidorUnidFuncionalDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmServidorUnidFuncional> {

	private static final long serialVersionUID = 6678772935553114003L;

	/**
	 * Retorna criteria para pesquisa de unidades funcionais configuradas para o
	 * servidor fornecido.<br>
	 * 
	 * @param servidor
	 * @return
	 */
	protected DetachedCriteria criteriaConfiguradaPara(
			final RapServidores servidor) {
		if (servidor == null) {
			throw new IllegalArgumentException("Argumento obrigatório");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmServidorUnidFuncional.class);

		criteria.add(Restrictions.eq(MpmServidorUnidFuncional.Fields.SERVIDOR
				.toString(), servidor));

		return criteria;
	}

	/**
	 * Retorna criteria para pesquisa de unidades funcionais configuradas para o
	 * servidor fornecido.<br>
	 * Execução da criteria retorna lista de {@link AghUnidadesFuncionais}.
	 * 
	 * @param servidor
	 *            configurado para
	 * @return
	 */
	protected DetachedCriteria criteriaUnidadeFuncionalConfiguradaPara(
			RapServidores servidor) {
		return this
				.criteriaConfiguradaPara(servidor)
				.setProjection(
						Projections
								.property(MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL
										.toString()));
	}

	

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = criteriaUnidadeFuncionalConfiguradaPara(servidor);
		return executeCriteria(criteria);

	}

	public List<MpmServidorUnidFuncional> pesquisarAssociacoesPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = criteriaConfiguradaPara(servidor);
		return executeCriteria(criteria);
	}
	
	/**
	 * Método para pesquisar lista de MpmServidorUnidFuncional pelo servidor com
	 * restrições nas datas de final do seu vinculo
	 * 
	 * @param seqUnidadeFuncional
	 * @param dataFim
	 * @return
	 */
	public List<MpmServidorUnidFuncional> pesquisarServidorUnidadeFuncional(
			Short seqUnidadeFuncional, Date dataFim) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmServidorUnidFuncional.class);

		criteria.createAlias(
				MpmServidorUnidFuncional.Fields.SERVIDOR.toString(), "servidor");
		criteria.createAlias(
				MpmServidorUnidFuncional.Fields.UNIDADE_FUNCIONAL.toString(),
				"unidadefuncional");

		criteria.add(Restrictions.eq("unidadefuncional."
				+ AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(),
				seqUnidadeFuncional));

		Date data = new Date();
		if (dataFim != null) {
			data = dataFim;
		}

		criteria.add(Restrictions.le(
				MpmServidorUnidFuncional.Fields.CRIADO_EM.toString(), data));

		Criterion c1 = Restrictions.gt("servidor."
				+ RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());
		Criterion c2 = Restrictions.isNull("servidor."
				+ RapServidores.Fields.DATA_FIM_VINCULO.toString());

		criteria.add(Restrictions.or(c1, c2));

		return this.executeCriteria(criteria);
	}
}