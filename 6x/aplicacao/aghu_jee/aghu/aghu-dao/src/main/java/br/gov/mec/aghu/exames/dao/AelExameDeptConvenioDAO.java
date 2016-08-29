package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExameDeptConvenio;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelExamesDependentesId;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;

public class AelExameDeptConvenioDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameDeptConvenio> {
	
	private static final long serialVersionUID = 1203443371793945075L;
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(),
				FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());
		
		montaCriteriaConvSaudePlano(filtro, criteria);
		criteria.addOrder(Order.asc(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString()+ "." + FatConvenioSaude.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc(FatConvenioSaudePlano.Fields.DESCRICAO.toString()));

		return this.executeCriteria(criteria, 0, 100, "descricao", true);
	}


	private void montaCriteriaConvSaudePlano(String filtro,
			DetachedCriteria criteria) {
		Criterion criSeq = Restrictions.sqlRestriction("cast(SEQ as varchar(50)) like '%" + filtro+ "%'");
		
		Criterion criCodigo = Restrictions.sqlRestriction("cast(CODIGO as varchar(50)) like '%" + filtro+ "%'");
		
		Criterion criDescricao = Restrictions.like(FatConvenioSaudePlano.Fields.DESCRICAO.toString(), filtro.toUpperCase(), MatchMode.ANYWHERE);
		
		Criterion criDescricaoConvenio = Restrictions.like(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
						+ FatConvenioSaude.Fields.DESCRICAO.toString(), filtro.toUpperCase(), MatchMode.ANYWHERE);
		
		// criteria2.add(criCodigo);
		criteria.add(Restrictions.or(criDescricao, Restrictions.or(criSeq,Restrictions.or(criCodigo, criDescricaoConvenio))));
	
	}
	
	public Long pesquisarConvenioSaudePlanosCount(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		criteria.createAlias(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(),
				FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());
		
		montaCriteriaConvSaudePlano(filtro, criteria);

		return this.executeCriteriaCount(criteria);
	}

	public List<AelExameDeptConvenio> obterConvenioPlanoDependentes(AelExamesDependentesId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameDeptConvenio.class);

		criteria.createAlias(AelExameDeptConvenio.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.EXD_EMA_EXA_SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.EXD_EMA_MAN_SEQ.toString(), id.getEmaManSeq()));

		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.EXD_EMA_EXA_SIGLA_EH_DEPEN.toString(), id.getEmaExaSiglaEhDependente()));
		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.EXD_EMA_EXA_SEQ_EH_DEPEN.toString(), id.getEmaManSeqEhDependente()));

		return executeCriteria(criteria);
	}

	public FatConvenioSaudePlano obterPlanoPorId(Byte seqConvenioSaudePlano, Short codConvenioSaude) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaudePlano.class);

		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.SEQ.toString(), seqConvenioSaudePlano));
		criteria.add(Restrictions.eq(FatConvenioSaudePlano.Fields.CODIGO.toString(), codConvenioSaude));

		FatConvenioSaudePlano convenioPlano = (FatConvenioSaudePlano) executeCriteriaUniqueResult(criteria);
		return convenioPlano;
    }
	
	public List<AelExameDeptConvenio> obterDependentesAtivosPorExame(String exdEmaExaSigla, int exdEmaManSeq, Short cspCnvCodigo, Byte cspSeq, boolean opcional) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameDeptConvenio.class);

		criteria.createCriteria(AelExameDeptConvenio.Fields.EXAME_DEPENDENTE.toString(), "exd", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.EXD_EMA_EXA_SIGLA.toString(), exdEmaExaSigla));
		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.EXD_EMA_MAN_SEQ.toString(), exdEmaManSeq));
		criteria.add(Restrictions.eq("exd." + AelExamesDependentes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("exd." + AelExamesDependentes.Fields.IND_OPCIONAL.toString(), DominioSimNao.getInstance(opcional)));
		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.CNV_CODIGO.toString(), cspCnvCodigo));
		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.SEQ.toString(), Short.valueOf(cspSeq.toString())));
		criteria.add(Restrictions.eq(AelExameDeptConvenio.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
    }
}