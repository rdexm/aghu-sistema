package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.bancosangue.vo.BuscaJustificativaLaudoCsaVO;
import br.gov.mec.aghu.bancosangue.vo.BuscaJustificativaLaudoPheVO;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.FatProcedHospInternos;

public class AbsItemSolicitacaoHemoterapicaJustificativaDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsItemSolicitacaoHemoterapicaJustificativa> {

	
	private static final long serialVersionUID = -5563696768456494197L;


	public Object obterItemSolicitacaoHemoterapicaVO(Integer atdSeq, Integer seq, Short sequencia, Integer jSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsItemSolicitacaoHemoterapicaJustificativa.class);
		
		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.property(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.DESCRICAO_LIVRE.toString()))
		);

		criteria.add(Restrictions.eq(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.ISH_SHE_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.ISH_SHE_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.ISH_SEQUENCIA.toString(), sequencia));
		criteria.add(Restrictions.eq(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.JCS_SEQ.toString(), jSeq));
	
		return (Object)executeCriteriaUniqueResult(criteria);
	}

	
	@SuppressWarnings("unchecked")
	public List<BuscaJustificativaLaudoCsaVO> buscaJustificativaLaudoCsa(
			Integer atdSeq, Integer phiSeq) {
		StringBuffer hql = new StringBuffer(360);
		hql.append(" select new br.gov.mec.aghu.bancosangue.vo.BuscaJustificativaLaudoCsaVO (");
		hql.append(" 	ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString());
		hql.append(", ");
		hql.append(" 	jcs.");
		hql.append(AbsJustificativaComponenteSanguineo.Fields.DESCRICAO.toString());
		hql.append("||").append("isj.").append(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.DESCRICAO_LIVRE.toString());
		hql.append(" ) ");
		hql.append(" from AbsItemSolicitacaoHemoterapicaJustificativa isj ");
		hql.append(" 	join isj.");
		hql.append(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.ITEM_SOLUCAO_HEMOTERAPICA.toString());
		hql.append(" ish ");
		hql.append(" 	join ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SOLICITACOES_HEMOTERAPICAS.toString());
		hql.append(" as she ");
		hql.append(" 	join isj.");
		hql.append(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.JUSTIFICATIVA_COMPONENTE_SANGUINEO.toString());
		hql.append(" as jcs, ");
		hql.append(" 	FatProcedHospInternos phi ");
		hql.append(" where she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString());
		hql.append(" = :atdSeq ");
		hql.append(" 	and (phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" = :seq or phi.");
		hql.append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString());
		hql.append(" = :phiSeq) ");
		hql.append(" 	and isj.");
		hql.append(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.MARCADO.toString());
		hql.append(" = :marcado ");
		hql.append(" 	and ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS_CODIGO.toString());
		hql.append(" = phi.");
		hql.append(FatProcedHospInternos.Fields.CSA_CODIGO.toString());
		hql.append(" order by she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.DT_HR_CRIADO_EM.toString());
		hql.append(", she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.SEQ.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("atdSeq", atdSeq);
		query.setParameter("seq", phiSeq);
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("marcado", true);
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<BuscaJustificativaLaudoPheVO> buscaJustificativaLaudoPhe(
			Integer atdSeq, Integer phiSeq) {
		StringBuffer hql = new StringBuffer(350);
		hql.append(" select new br.gov.mec.aghu.bancosangue.vo.BuscaJustificativaLaudoPheVO (");
		hql.append(" 	ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString());
		hql.append(", ");
		hql.append(" 	jcs.");
		hql.append(AbsJustificativaComponenteSanguineo.Fields.DESCRICAO.toString());
		hql.append(" ) ");
		hql.append(" from AbsItemSolicitacaoHemoterapicaJustificativa isj ");
		hql.append(" 	join isj.");
		hql.append(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.ITEM_SOLUCAO_HEMOTERAPICA.toString());
		hql.append(" ish ");
		hql.append(" 	join ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.SOLICITACOES_HEMOTERAPICAS.toString());
		hql.append(" as she ");
		hql.append(" 	join isj.");
		hql.append(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.JUSTIFICATIVA_COMPONENTE_SANGUINEO.toString());
		hql.append(" as jcs, ");
		hql.append(" 	FatProcedHospInternos phi ");
		hql.append(" where she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString());
		hql.append(" = :atdSeq ");
		hql.append(" 	and (phi.");
		hql.append(FatProcedHospInternos.Fields.SEQ.toString());
		hql.append(" = :seq or phi.");
		hql.append(FatProcedHospInternos.Fields.FAT_PROCEDHOSP_INTERNOS_SEQ.toString());
		hql.append(" = :phiSeq) ");
		hql.append(" 	and isj.");
		hql.append(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.MARCADO.toString());
		hql.append(" = :marcado ");
		hql.append(" 	and ish.");
		hql.append(AbsItensSolHemoterapicas.Fields.ABS_PROCED_HEMOTERAPICOS_CODIGO.toString());
		hql.append(" = phi.");
		hql.append(FatProcedHospInternos.Fields.PHE_CODIGO.toString());
		hql.append(" order by she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.DT_HR_CRIADO_EM.toString());
		hql.append(", she.");
		hql.append(AbsSolicitacoesHemoterapicas.Fields.SEQ.toString());
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("atdSeq", atdSeq);
		query.setParameter("seq", phiSeq);
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("marcado", true);
		
		return query.list();
	}
	
	/**
	 * Método que pesquisa as justificativas de um item de hemoterapia
	 * @param sheAtdSeq
	 * @param sheSeq
	 * @param ishSequencia
	 * @return
	 */
	public List<AbsItemSolicitacaoHemoterapicaJustificativa> pesquisarJustificativasItemHemoterapia(Integer sheAtdSeq,
			Integer sheSeq, Short ishSequencia) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsItemSolicitacaoHemoterapicaJustificativa.class);
		
		criteria
				.createAlias(
						AbsItemSolicitacaoHemoterapicaJustificativa.Fields.JUSTIFICATIVA_COMPONENTE_SANGUINEO
								.toString(),
						AbsItemSolicitacaoHemoterapicaJustificativa.Fields.JUSTIFICATIVA_COMPONENTE_SANGUINEO
								.toString());
		
		criteria
				.createAlias(
						AbsItemSolicitacaoHemoterapicaJustificativa.Fields.JUSTIFICATIVA_COMPONENTE_SANGUINEO.toString() + "." 
								+ AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA
										.toString(), AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA.toString());

		criteria.add(Restrictions.eq(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.ISH_SHE_ATD_SEQ
				.toString(), sheAtdSeq));
		criteria.add(Restrictions.eq(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.ISH_SHE_SEQ
				.toString(), sheSeq));
		criteria.add(Restrictions.eq(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.ISH_SEQUENCIA
				.toString(), ishSequencia));
		
		criteria.add(Restrictions.eq(AbsItemSolicitacaoHemoterapicaJustificativa.Fields.MARCADO.toString(), true));
		
		criteria
				.addOrder(Order
						.asc(AbsJustificativaComponenteSanguineo.Fields.GRUPO_JUSTIFICATIVA
								.toString()
								+ "."
								+ AbsGrupoJustificativaComponenteSanguineo.Fields.DESCRICAO
										.toString()));

		return executeCriteria(criteria);
	}
	
	
	
}
