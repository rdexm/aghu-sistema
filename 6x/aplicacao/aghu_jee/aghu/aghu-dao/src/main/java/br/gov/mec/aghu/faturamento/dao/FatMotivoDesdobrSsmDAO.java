package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.CursorMotivosSsmCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.model.FatMotivoDesdobrSsm;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;

public class FatMotivoDesdobrSsmDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMotivoDesdobrSsm> {

	private static final long serialVersionUID = 8260302465070007399L;

	@SuppressWarnings("unchecked")
	public List<CursorMotivosSsmCadastroSugestaoDesdobramentoVO> listarMotivosSsmCadastroSugestaoDesdobramento(Short phoSeq,
			Integer iphSeq, DominioSituacao indSituacao) {
		StringBuffer hql = new StringBuffer(240);
		
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.CursorMotivosSsmCadastroSugestaoDesdobramentoVO(");
		hql.append(" 	mss.").append(FatMotivoDesdobrSsm.Fields.MDS_SEQ.toString());
		hql.append(" 	, mss.").append(FatMotivoDesdobrSsm.Fields.DIAS_INTERNACAO.toString());
		hql.append(" 	, mss.").append(FatMotivoDesdobrSsm.Fields.IPH_PHO_SEQ.toString());
		hql.append(" 	, mss.").append(FatMotivoDesdobrSsm.Fields.IPH_SEQ.toString());
		hql.append(" 	, mds.").append(FatMotivoDesdobramento.Fields.TIPO_DESDOBRAMENTO.toString());
		hql.append(" 	, mds.").append(FatMotivoDesdobramento.Fields.CODIGO_SUS.toString());
		hql.append(')');
		hql.append(" from ").append(FatMotivoDesdobrSsm.class.getSimpleName()).append(" as mss ");
		hql.append(" 	join mss.").append(FatMotivoDesdobrSsm.Fields.MOTIVO_DESDOBRAMENTO.toString()).append(" as mds ");
		hql.append(" where mss.").append(FatMotivoDesdobrSsm.Fields.IPH_PHO_SEQ.toString()).append(" = :phoSeq ");
		hql.append(" 	and mss.").append(FatMotivoDesdobrSsm.Fields.IPH_SEQ.toString()).append(" = :iphSeq ");
		hql.append(" 	and mss.").append(FatMotivoDesdobrSsm.Fields.IND_SITUACAO.toString()).append(" = :indSituacao ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("phoSeq", phoSeq);
		query.setParameter("iphSeq", iphSeq);
		query.setParameter("indSituacao", indSituacao);
		
		return query.list();
	}
	
	public List<FatMotivoDesdobrSsm> pesquisarMotivosDesdobramentoSSM(Short seqMotivoDesdobramento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoDesdobrSsm.class, "SSM");
		
		criteria.createAlias("SSM."+FatMotivoDesdobrSsm.Fields.ITEM_PROCEDIMENTO_HOSPITALAR.toString(), "IPH", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("SSM."+FatMotivoDesdobrSsm.Fields.MDS_SEQ.toString(), seqMotivoDesdobramento));
		
		return executeCriteria(criteria);
		
	}

}
