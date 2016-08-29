package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.RapServidores;

public class AelApXPatologistaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelApXPatologista> {

	private static final long serialVersionUID = 8625353895049676150L;

	private DetachedCriteria obterDetachedCriteriaAelApXPatologista(final Long lumSeq, final Integer luiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelApXPatologista.class);

		if (lumSeq != null) {
			criteria.add(Restrictions.eq(AelApXPatologista.Fields.LUM_SEQ.toString(), lumSeq));
		}
		if (luiSeq != null) {
			criteria.add(Restrictions.eq(AelApXPatologista.Fields.LUI_SEQ.toString(), luiSeq));
		}
		return criteria;
	}

	public AelApXPatologista obterAelApXPatologistaPorSeqAnatoPatologicoMatriculaEFuncao(final Long lumSeq, final Integer matricula,
			final DominioFuncaoPatologista[] funcao, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelApXPatologista.class);
		criteria.createAlias(AelApXPatologista.Fields.AEL_PATOLOGISTAS.toString(), "pat");
		criteria.createAlias("pat." + AelPatologista.Fields.SERVIDOR.toString(), "ser");

		criteria.add(Restrictions.eq(AelApXPatologista.Fields.LUM_SEQ.toString(), lumSeq));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.in("pat." + AelPatologista.Fields.FUNCAO.toString(), funcao));
		if (situacao != null) {
			criteria.add(Restrictions.eq("pat." + AelPatologista.Fields.SITUCAO.toString(), situacao));
		}		
		
		return (AelApXPatologista)executeCriteriaUniqueResult(criteria);
	}
	
	public List<AelApXPatologista> listarAelApXPatologistaAtivos(final Long lumSeq, final Integer luiSeq) {
		final DetachedCriteria criteria = this.obterDetachedCriteriaAelApXPatologista(lumSeq, luiSeq);
		criteria.createAlias(AelApXPatologista.Fields.AEL_PATOLOGISTAS.toString(), "pat");
		criteria.add(Restrictions.eq("pat." + AelPatologista.Fields.SITUCAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<AelApXPatologista> listarAelApXPatologistaPorLumSeq(final Long lumSeq) {
		final DetachedCriteria criteria = this.obterDetachedCriteriaAelApXPatologista(lumSeq, null);
		return executeCriteria(criteria);
	}	
	
	public Long contarPatologistaAtivoPorFuncaoPatologista(final Long codigoAnatomoPatologico, final Integer codigoPatologista,
			final DominioFuncaoPatologista... funcao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelApXPatologista.class);
		criteria.createAlias(AelApXPatologista.Fields.AEL_PATOLOGISTAS.toString(), "pat");
		
		criteria.add(Restrictions.eq(AelApXPatologista.Fields.LUM_SEQ.toString(), codigoAnatomoPatologico));
		criteria.add(Restrictions.ne(AelApXPatologista.Fields.LUI_SEQ.toString(), codigoPatologista));
		criteria.add(Restrictions.in("pat." + AelPatologista.Fields.FUNCAO.toString(), funcao));
		criteria.add(Restrictions.eq("pat." + AelPatologista.Fields.SITUCAO.toString(),
				DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}

	public String obterResidenteResponsavel(Long numeroAp, Integer lu2Seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelApXPatologista.class, "LO5");
		criteria.createAlias("LO5." + AelApXPatologista.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "LUM");
		criteria.createAlias("LO5." + AelApXPatologista.Fields.AEL_PATOLOGISTAS.toString(), "LUI");
		criteria.createAlias("LUI." + AelPatologista.Fields.AEL_EXAME_AP.toString(), "LUX");
		
		criteria.setProjection(Projections.property("LUI." + AelPatologista.Fields.NOME.toString()));
		
		criteria.add(Restrictions.eq("LUM." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("LUM." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		criteria.add(Restrictions.eq("LUI." + AelPatologista.Fields.FUNCAO.toString(), DominioFuncaoPatologista.R));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
}
