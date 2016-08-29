package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacaoRarCandidatoPrograma;
import br.gov.mec.aghu.dominio.DominioTipoRarPrograma;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RarAssociaPrograma;
import br.gov.mec.aghu.model.RarCandidatoPrograma;
import br.gov.mec.aghu.model.RarCandidatos;
import br.gov.mec.aghu.model.RarPrograma;

public class RarCandidatoProgramaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RarCandidatoPrograma> {

	private static final long serialVersionUID = -1048628229492587596L;

	protected RarCandidatoProgramaDAO() {
	}
	
	public RarCandidatoPrograma obterRarCandidatoProgramaPorServidorData(RapServidores rapServidor, Date data) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "CPM");

		dc.createAlias("CPM.".concat(RarCandidatoPrograma.Fields.RAR_CANDIDATOS.toString()), "CND");
		dc.createAlias("CPM.".concat(RarCandidatoPrograma.Fields.RAR_PROGRAMAS_BY_PGA_SEQ.toString()), "PGA");
		
		dc.add(Restrictions.eq("CND.".concat(RarCandidatos.Fields.PESSOA_FISICA.toString()), rapServidor.getPessoaFisica()));
		dc.add(Restrictions.eq("CPM.".concat(RarCandidatoPrograma.Fields.SITUACAO.toString()), DominioSituacaoRarCandidatoPrograma.A));		
		dc.add(criarRestrictionRarCandidatoProgramaParametroEntreDtInicioDtFim(data));
		
		return (RarCandidatoPrograma)executeCriteriaUniqueResult(dc);
	}
	
	private Criterion criarRestrictionRarCandidatoProgramaParametroEntreDtInicioDtFim(Date data) {

		Criterion restrict1 = Restrictions.le("CPM.".concat(RarCandidatoPrograma.Fields.DT_INICIO.toString()), data);
		Criterion restrict2 = Restrictions.ge("CPM.".concat(RarCandidatoPrograma.Fields.DT_FIM.toString()), data);
		Criterion restrict3 = Restrictions.and(restrict1, restrict2);
		
		Criterion restrict4 = Restrictions.le("CPM.".concat(RarCandidatoPrograma.Fields.DT_INICIO.toString()), data);
		Criterion restrict5 = Restrictions.isNull("CPM.".concat(RarCandidatoPrograma.Fields.DT_FIM.toString()));
		Criterion restrict6 = Restrictions.and(restrict4, restrict5);
		
		return Restrictions.or(restrict3, restrict6);
	}
	
	public RarCandidatoPrograma obterRarCandidatoProgramaPorCandidatoData(RarCandidatos candidatos, Date data) {
		
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "CPM");

		dc.add(Restrictions.eq("CPM.".concat(RarCandidatoPrograma.Fields.RAR_CANDIDATOS.toString()), candidatos));		
		dc.add(criarRestrictionRarCandidatoProgramaParametroEntreDtInicioDtFim(data));
		
		return (RarCandidatoPrograma)executeCriteriaUniqueResult(dc);
	}
	
	public RarCandidatoPrograma obterRarCandidatoProgramaPorProgramaCandidato(RarCandidatos candidato, RarPrograma programa) {
		
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "CPM");
		
		dc.createAlias("CPM".concat(RarCandidatoPrograma.Fields.RAR_PROGRAMAS_BY_PGA_SEQ.toString()), "PGA");
		
		dc.add(Restrictions.eq("CPM.".concat(RarCandidatoPrograma.Fields.RAR_CANDIDATOS.toString()), candidato));
		dc.add(Restrictions.eq("CPM.".concat(RarCandidatoPrograma.Fields.SITUACAO.toString()), DominioSituacaoRarCandidatoPrograma.A));
		dc.add(Restrictions.eq("PGA.".concat(RarPrograma.Fields.TIPO.toString()), DominioTipoRarPrograma.PP));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(RarAssociaPrograma.class, "ASP");
		subCriteria.add(Restrictions.eqProperty(
				"ASP.".concat(RarAssociaPrograma.Fields.SEQ_RAR_PROGRAMAS_BY_PGA_SUPERIOR.toString()),
				"PGA.".concat(RarPrograma.Fields.SEQ.toString())));
		subCriteria.add(Restrictions.eq("ASP.".concat(RarAssociaPrograma.Fields.RAR_PROGRAMAS_BY_PGA_AREA_ATUACAO.toString()), programa));
		subCriteria.setProjection(Projections.rowCount());
		
		dc.add(Subqueries.exists(subCriteria));
		
		return (RarCandidatoPrograma) executeCriteriaUniqueResult(dc);
	}
	
	public RarCandidatoPrograma obterUltimoRarCandidatoProgramaPorCandidato(RarCandidatos candidatos) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "CPM");
		
		dc.add(Restrictions.eq("CPM.".concat(RarCandidatoPrograma.Fields.RAR_CANDIDATOS.toString()), candidatos));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(getClazz(), "CPM2");
		subQuery.add(Restrictions.eqProperty(
				"CPM2.".concat(RarCandidatoPrograma.Fields.RAR_CANDIDATOS.toString()),
				"CPM.".concat(RarCandidatoPrograma.Fields.RAR_CANDIDATOS.toString())));
		subQuery.add(Restrictions.eq("CPM2.".concat(RarCandidatoPrograma.Fields.SITUACAO.toString()), DominioSituacaoRarCandidatoPrograma.A));
		subQuery.setProjection(Projections.max("CPM2.".concat(RarCandidatoPrograma.Fields.DT_FIM.toString())));
		
		dc.add(Subqueries.propertyIn("CPM.".concat(RarCandidatoPrograma.Fields.DT_FIM.toString()), subQuery));
		
		return (RarCandidatoPrograma) executeCriteriaUniqueResult(dc);
	}
}