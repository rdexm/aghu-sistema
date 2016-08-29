package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmListaServEspecialidade;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 * @see MpmListaServEspecialidade
 * 
 * @author cvagheti
 * 
 */
public class MpmListaServEspecialidadeDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmListaServEspecialidade> {
	private static final long serialVersionUID = -434151080233776019L;

	/**
	 * Retorna criteria para pesquisa de especialidades configuradas para o
	 * servidor fornecido.<br>
	 * Execução da criteria retorna lista de {@link MpmListaServEspecialidade}.
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
				.forClass(MpmListaServEspecialidade.class);

		criteria.add(Restrictions.eq(MpmListaServEspecialidade.Fields.SERVIDOR
				.toString(), servidor));

		return criteria;
	}
	
	
	/**
	 * Retorna criteria para pesquisa de especialidades configuradas para o
	 * servidor fornecido.<br>
	 * Execução da criteria retorna lista de {@link AghEspecialidades}.
	 * 
	 * @param servidor
	 *            configurado para
	 * @return
	 */
	protected DetachedCriteria criteriaEspecialidadeConfiguradaPara(
			RapServidores servidor) {
		return this.criteriaConfiguradaPara(servidor).setProjection(
				Projections.property("especialidade"));
	}

	

	public List<AghEspecialidades> pesquisarEspecialidadesPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = criteriaEspecialidadeConfiguradaPara(servidor);
		return executeCriteria(criteria);
	}
	
	public List<MpmListaServEspecialidade> pesquisarAssociacoesPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = criteriaConfiguradaPara(servidor);
		return executeCriteria(criteria);
	}

	/**
	 * Método para pesquisar lista de MpmServidorEspecialidade pelo seq da
	 * especialidade e com restrições nas datas de final do vinculo do servidor
	 * 
	 * @param seqEspecialidade
	 * @param dataCriacao
	 * @return
	 */
	public List<MpmListaServEspecialidade> pesquisarListaServidorEspecialidadePorEspecialiade(
			Short seqEspecialidade, Date dataFim) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmListaServEspecialidade.class);
		
		criteria.createAlias(
				MpmListaServEspecialidade.Fields.ESPECIALIDADE.toString(),
				"especialidade");
		criteria.createAlias(
				MpmListaServEspecialidade.Fields.SERVIDOR.toString(),
				"servidor");
		
		criteria.add(Restrictions.eq("especialidade."
				+ AghEspecialidades.Fields.SEQ.toString(), seqEspecialidade));
		
		Date data = new Date();
		if (dataFim != null) {
			data = dataFim;
		}

		criteria.add(Restrictions.le(
				MpmListaServEspecialidade.Fields.CRIADO_EM.toString(), data));

		Criterion c1 = Restrictions.gt("servidor."
				+ RapServidores.Fields.DATA_FIM_VINCULO.toString(), new Date());
		Criterion c2 = Restrictions.isNull("servidor."
				+ RapServidores.Fields.DATA_FIM_VINCULO.toString());

		criteria.add(Restrictions.or(c1, c2));

		return this.executeCriteria(criteria);
	}
}