package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.LockOptions;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.model.AipProntuarioLiberados;

public class AipProntuarioLiberadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipProntuarioLiberados> {

	private static final long serialVersionUID = -2465531801466967423L;

	@SuppressWarnings("unchecked")
	public Integer obterNumeroProntuariosLiberados(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipProntuarioLiberados.class);
		criteria.add(Restrictions.eq(
				AipProntuarioLiberados.Fields.PRONTUARIO.toString(), prontuario));

		List<AipProntuarioLiberados> listaProntuariosLiberatos = (List) this
				.executeCriteria(criteria);
		return listaProntuariosLiberatos.size();
	}

	public AipProntuarioLiberados obterProntuarioLiberadoComMaiorNumeroProntuario() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipProntuarioLiberados.class);

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AipProntuarioLiberados.class).setProjection(
				Projections.max(AipProntuarioLiberados.Fields.PRONTUARIO.toString()));

		criteria.add(Subqueries.propertyEq(AipProntuarioLiberados.Fields.PRONTUARIO.toString(), subCriteria));

		return (AipProntuarioLiberados) this.executeCriteriaUniqueResult(criteria);
	}
	
	//Melhoria em Produção #26862
	
	public void limpaProntuariosLiberados(){
		String hql = "delete from AipProntuarioLiberados pl where exists (select p.prontuario from AipPacientes p where p.prontuario = pl.prontuario)"; 
		createQuery(hql).executeUpdate();
	}
	
	
	public AipProntuarioLiberados obterProntuarioLiberado(Integer prontuario, LockOptions lockMode) {
		return (AipProntuarioLiberados) getAndLock( prontuario, lockMode);
	}

	
	public List<AipProntuarioLiberados> pesquisarProntuariosLiberados(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipProntuarioLiberados.class);
		if (prontuario != null) {
			criteria.add(Restrictions.eq(AipProntuarioLiberados.Fields.PRONTUARIO.toString(), prontuario));
		}
		return executeCriteria(criteria);
	}
	
}
