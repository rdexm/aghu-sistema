package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoCuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticosSinaisSintomasVO;

public class EpeFatRelDiagnosticoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeFatRelDiagnostico> {

	private static final long serialVersionUID = -5953935728235220515L;

	public List<EpeFatRelDiagnostico> pesquisarFatRelDiagnosticoPorDiagnostico(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatRelDiagnostico.class);
		criteria.createAlias(EpeFatRelDiagnostico.Fields.FAT_RELACIONADO.toString(), EpeFatRelDiagnostico.Fields.FAT_RELACIONADO.toString());
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.FAT_RELACIONADO.toString()+"."+EpeFatRelacionado.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString(), snbGnbSeq));
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), snbSequencia));
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SEQUENCIA.toString(), sequencia));
		criteria.addOrder(Order.asc(EpeFatRelDiagnostico.Fields.FAT_RELACIONADO.toString()+"."+EpeFatRelacionado.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}	
	
	public List<EpeFatRelDiagnostico> pesquisarDiagnosticoPorSeq(Short seq, DominioSituacao dominio) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatRelDiagnostico.class);
		
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.FRE_SEQ.toString(), seq));
		
		if(dominio != null){
			criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.SITUACAO.toString(), dominio));
		}
		
		return executeCriteria(criteria);
	}
	
	public List<EpeFatRelDiagnostico> pesquisarFatRelDiagnosticoPorSubgrupoAtivo(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		DetachedCriteria criteria = montarCriteriaPesquisarFatRelDiagnosticoPorSubgrupo(
				snbGnbSeq, snbSequencia, sequencia);
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	public List<EpeFatRelDiagnostico> pesquisarFatRelDiagnosticoPorSubgrupo(Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		DetachedCriteria criteria = montarCriteriaPesquisarFatRelDiagnosticoPorSubgrupo(
				snbGnbSeq, snbSequencia, sequencia);
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarCriteriaPesquisarFatRelDiagnosticoPorSubgrupo(
			Short snbGnbSeq, Short snbSequencia, Short sequencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatRelDiagnostico.class);
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString(), snbGnbSeq));
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), snbSequencia));
		criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SEQUENCIA.toString(), sequencia));
		return criteria;
	}
		
	// #4960 (Manter diagnósticos x cuidados)
	// C5
	public List<DiagnosticoCuidadoVO> pesquisarDiagnosticos(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia, Short freSeq, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteriaDiagnosticoCuidado(snbGnbSeq, snbSequencia, dgnSequencia, freSeq);
		
		List<EpeFatRelDiagnostico> listaArrayObject = executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		List<DiagnosticoCuidadoVO> listaDiagnosticoCuidadoVO = new ArrayList<DiagnosticoCuidadoVO>();
		
		for (EpeFatRelDiagnostico arrayObject : listaArrayObject) {
			DiagnosticoCuidadoVO diagnosticoCuidadoVO = new DiagnosticoCuidadoVO();
			
			// diagnostico
			diagnosticoCuidadoVO.setDgnSequencia(arrayObject.getDiagnostico().getId().getSequencia());
			diagnosticoCuidadoVO.setDescricaoDiagnostico(arrayObject.getDiagnostico().getDescricao());
			// subgrupo
			diagnosticoCuidadoVO.setDgnSnbSequencia(arrayObject.getDiagnostico().getSubgrupoNecesBasica().getId().getSequencia());
			diagnosticoCuidadoVO.setDescricaoSubgrupo(arrayObject.getDiagnostico().getSubgrupoNecesBasica().getDescricao());
			// grupo
			diagnosticoCuidadoVO.setDgnSnbGnbSeq(arrayObject.getDiagnostico().getSubgrupoNecesBasica().getGrupoNecesBasica().getSeq());
			diagnosticoCuidadoVO.setDescricaoGrupo(arrayObject.getDiagnostico().getSubgrupoNecesBasica().getGrupoNecesBasica().getDescricao());
			// etiologia
			diagnosticoCuidadoVO.setFreSeq(arrayObject.getFatRelacionado().getSeq());
			diagnosticoCuidadoVO.setDescricaoEtiologia(arrayObject.getFatRelacionado().getDescricao());
			
			diagnosticoCuidadoVO.setIndSituacao(arrayObject.getSituacao().getDescricao());
			listaDiagnosticoCuidadoVO.add(diagnosticoCuidadoVO);
		}
		
		return listaDiagnosticoCuidadoVO;
	}
	
	// #4960 (Manter diagnósticos x cuidados)
	// C5 Count
	public Long pesquisarDiagnosticosCount(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = montarCriteriaDiagnosticoCuidado(snbGnbSeq, snbSequencia, dgnSequencia, freSeq);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarCriteriaDiagnosticoCuidado(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia, Short freSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatRelDiagnostico.class, "ref");

		criteria.createAlias("ref." + EpeFatRelDiagnostico.Fields.DIAGNOSTICO.toString(), "dgn");
		criteria.createAlias("dgn." + EpeDiagnostico.Fields.SUBGRUPO_NECES_BASICA.toString(), "snb");
		criteria.createAlias("snb." + EpeSubgrupoNecesBasica.Fields.GRUPO_NECES_BASICA.toString(), "gnb");
		criteria.createAlias("ref." + EpeFatRelDiagnostico.Fields.FAT_RELACIONADO.toString(), "fre");

		if (snbGnbSeq != null) {
			criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString(), snbGnbSeq));
		}
		if (snbSequencia != null) {
			criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), snbSequencia));
		}
		if (dgnSequencia != null) {
			criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SEQUENCIA.toString(), dgnSequencia));
		}
		if (freSeq != null) {
			criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.FRE_SEQ.toString(), freSeq));
		}

		return criteria;
	}

	@SuppressWarnings("unchecked")
	// Em HQL por ter relacionamento com chave parcial...
	public Query obterHQLDiagnosticosGrupoSubgrupoNecessidadesBasicas(Short gnbSeq, Short snbSequencia, Short dngSequencia, Boolean isCount) {
		StringBuffer hql = new StringBuffer(370);
		hql.append("select ");
		if (isCount) {
			hql.append("	count(*) ");
		} else {
			hql.append("	gnb.descricao as descricaoGrupo, ")
			   .append("	snb.descricao as descricaoSubgrupo, ")
			   .append("	dng.descricao as descricaoDiagnostico, ")
			   .append("	case rel.situacao ")
			   .append("		when DominioSituacao.A then 'Ativo' ")
			   .append("		when DominioSituacao.I then 'Inativo' end as indSituacao, ")
			   .append("	rel.id.dgnSnbGnbSeq as dgnSnbGnbSeq, ")
			   .append("	rel.id.dgnSnbSequencia as dgnSnbSequencia, ")
			   .append("	rel.id.dgnSequencia as dgnSequencia, ")
			   .append("	rel.id.freSeq as freSeq ");
		}
		hql.append("from ")
		   .append("	EpeFatRelDiagnostico rel, ")
		   .append("	EpeDiagnostico dng, ")
		   .append("	EpeGrupoNecesBasica gnb, ")
		   .append("	EpeSubgrupoNecesBasica snb ")
		   .append("where ")
		   .append("	rel.id.dgnSnbGnbSeq = gnb.seq ")
		   .append("	and rel.id.dgnSnbSequencia = snb.id.sequencia ")
		   .append("	and rel.id.dgnSequencia = dng.id.sequencia ");
		if (gnbSeq != null) {
			hql.append("	and gnb.seq = :gnbSeq ");
		}
		if (snbSequencia != null) {
			hql.append("	and snb.id.sequencia = :snbSequencia ");
		}
		if (dngSequencia != null) {
			hql.append("	and dng.id.sequencia = :dngSequencia ");
		}
		Query q = createHibernateQuery(hql.toString());
		if (gnbSeq != null) {
			q.setShort("gnbSeq",gnbSeq);
		}
		if (snbSequencia != null) {
			q.setShort("snbSequencia",snbSequencia);
		}
		if (dngSequencia != null) {
			q.setShort("dngSequencia",dngSequencia);
		}
		return q;
	}
	
	@SuppressWarnings("unchecked")
	public List<DiagnosticosSinaisSintomasVO> obterDiagnosticosGrupoSubgrupoNecessidadesBasicas(Short gnbSeq, Short snbSequencia, Short dngSequencia, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Query q = obterHQLDiagnosticosGrupoSubgrupoNecessidadesBasicas(gnbSeq, snbSequencia, dngSequencia, false);
		q.setFirstResult(firstResult);
		q.setMaxResults(maxResult);
		q.setResultTransformer(Transformers.aliasToBean(DiagnosticosSinaisSintomasVO.class));
		return q.list();
	}

	public Long obterDiagnosticosGrupoSubgrupoNecessidadesBasicasCount(Short gnbSeq, Short snbSequencia, Short dngSequencia) {
		Query q = obterHQLDiagnosticosGrupoSubgrupoNecessidadesBasicas(gnbSeq, snbSequencia, dngSequencia, true);
		return (Long) q.uniqueResult();
		// ...
		// O código a seguir é usado em outros lugares no AGHU pra solucionar
		// problemas com Oracle caso ocorram:
		// Integer count = 0;
		// if (isOracle()) {
		// count = ((BigDecimal) q.uniqueResult()).intValue();
		// } else if (isPostgreSQL()) {
		// count = ((BigInteger) q.uniqueResult()).intValue();
		// } else if (isHSQL()) {
		// count = (Integer) q.uniqueResult();
		// }
		// return count;
		// ...
	}	

	public List<EpeFatRelDiagnostico> pesquisarEtiologiasDiagnosticos(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = montarCriteriaDiagnosticosEtiologias(snbGnbSeq, snbSequencia, dgnSequencia);
		
		criteria.addOrder(Order.asc(EpeFatRelDiagnostico.Fields.FRE_SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarEtiologiasDiagnosticosCount(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia) {
		
		DetachedCriteria criteria = montarCriteriaDiagnosticosEtiologias(snbGnbSeq, snbSequencia, dgnSequencia);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaDiagnosticosEtiologias(Short snbGnbSeq, Short snbSequencia, Short dgnSequencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EpeFatRelDiagnostico.class, "ref");

		criteria.createAlias("ref." + EpeFatRelDiagnostico.Fields.DIAGNOSTICO.toString(), "dgn");
		criteria.createAlias("dgn." + EpeDiagnostico.Fields.SUBGRUPO_NECES_BASICA.toString(), "snb");
		criteria.createAlias("snb." + EpeSubgrupoNecesBasica.Fields.GRUPO_NECES_BASICA.toString(), "gnb");
		criteria.createAlias("ref." + EpeFatRelDiagnostico.Fields.FAT_RELACIONADO.toString(), "fat_rel", JoinType.LEFT_OUTER_JOIN);

		if (snbGnbSeq != null) {
			criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SNB_GNB_SEQ.toString(), snbGnbSeq));
		}
		if (snbSequencia != null) {
			criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SNB_SEQUENCIA.toString(), snbSequencia));
		}
		if (dgnSequencia != null) {
			criteria.add(Restrictions.eq(EpeFatRelDiagnostico.Fields.DGN_SEQUENCIA.toString(), dgnSequencia));
		}


		return criteria;
	}

}