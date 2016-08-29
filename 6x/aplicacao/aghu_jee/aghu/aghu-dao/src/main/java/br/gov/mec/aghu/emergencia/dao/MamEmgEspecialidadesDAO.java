package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamUnidAtendeEsp;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamEmgEspecialidadesDAO extends BaseDao<MamEmgEspecialidades> {

	private static final long serialVersionUID = 824892726430400298L;

	/**
	 * Executa a pesquisa de situações de emergência
	 * 
	 * @param codigo
	 * @param indSituacao
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<MamEmgEspecialidades> pesquisarEspecialidadesEmergencia(Short codigo, DominioSituacao indSituacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		final DetachedCriteria criteria = this.montarPesquisaEspecialidadesEmergencia(codigo, indSituacao);

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Executa a pesquisa de situações de emergência
	 * 
	 * @param codigo
	 * @param indSituacao
	 * @return
	 */
	public List<MamEmgEspecialidades> pesquisarEspecialidadesEmergencia(Short codigo, DominioSituacao indSituacao) {

		final DetachedCriteria criteria = this.montarPesquisaEspecialidadesEmergencia(codigo, indSituacao);

		return this.executeCriteria(criteria);
	}

	public List<Short> pesquisarSeqsEspecialidadesEmergencia(Short codigo, DominioSituacao indSituacao) {
		final DetachedCriteria criteria = this.montarPesquisaEspecialidadesEmergencia(codigo, indSituacao);
		criteria.setProjection(Projections.property(MamEmgEspecialidades.Fields.ESP_SEQ.toString()));
		return this.executeCriteria(criteria);
	}

	/**
	 * Executa o count da pesquisa de situações de emergência
	 * 
	 * @param codigo
	 * @param indSituacao
	 * @return
	 */
	public Long pesquisarEspecialidadesEmergenciaCount(Short codigo, DominioSituacao indSituacao) {

		final DetachedCriteria criteria = this.montarPesquisaEspecialidadesEmergencia(codigo, indSituacao);

		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Monta a pesquisa de especialidades de emergência
	 * 
	 * @param codigo
	 * @param indSituacao
	 * @return
	 */
	private DetachedCriteria montarPesquisaEspecialidadesEmergencia(Short codigo, DominioSituacao indSituacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgEspecialidades.class, "MamEmgEspecialidades");

		if (codigo != null) {
			criteria.add(Restrictions.eq(MamEmgEspecialidades.Fields.ESP_SEQ.toString(), codigo));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(MamEmgEspecialidades.Fields.IND_SITUACAO.toString(), indSituacao));

		}

		return criteria;
	}

	/***
	 * C11
	 * 
	 * @return
	 */
	public List<MamEmgEspecialidades> pesquisarEspecialidadesAtivas() {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgEspecialidades.class, "MamEmgEspecialidades");

		criteria.add(Restrictions.eq("MamEmgEspecialidades." + MamEmgEspecialidades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return this.executeCriteria(criteria);

	}

	public List<Short> pesquisarAtivosPorSeq(Short seq, Integer maxResults) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgEspecialidades.class);
		criteria.add(Restrictions.eq(MamEmgEspecialidades.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if (seq != null) {
			criteria.add(Restrictions.eq(MamEmgEspecialidades.Fields.ESP_SEQ.toString(), seq));
		}
		criteria.setProjection(Projections.property(MamEmgEspecialidades.Fields.ESP_SEQ.toString()));
		if (maxResults != null) {
			return executeCriteria(criteria, 0, maxResults, MamEmgEspecialidades.Fields.ESP_SEQ.toString(), true);
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa especialidades associadas a uma unidade da emergencia
	 * @param unfSeq
	 * @return
	 */
	public List<Short> pesquisarEspecialidadesEmergenciaAssociadasUnidades (Short unfSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgEspecialidades.class, "MEE"); 	
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamUnidAtendeEsp.class,"MUAE");
		subCriteria.setProjection(Projections.projectionList().add(Projections.property("MUAE."+ MamUnidAtendeEsp.Fields.ESP_SEQ.toString())));
												   subCriteria.add(Restrictions.eq("MUAE."+  MamUnidAtendeEsp.Fields.MAM_UNID_ATENDEM_UNF_SEQ.toString(), unfSeq));
												   subCriteria.add(Restrictions.eqProperty("MUAE."+  MamUnidAtendeEsp.Fields.ESP_SEQ.toString(), "MEE." + MamEmgEspecialidades.Fields.ESP_SEQ));
		criteria.add(Subqueries.exists(subCriteria));
		criteria.setProjection(Projections.distinct(Projections.projectionList().add(Projections.property("MEE."+ MamEmgEspecialidades.Fields.ESP_SEQ.toString()))));		
		return executeCriteria(criteria);
		
	}
}
