package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.model.MciNotasCCIH;
import br.gov.mec.aghu.model.RapServidores;

public class MciNotasCCIHDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MciNotasCCIH> {

	private static final long serialVersionUID = -2735645798452711615L;

	
	public List<NotificacoesGeraisVO> listarNotasCCIH(final Integer codigoPaciente) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq("NTC." + MciNotasCCIH.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.addOrder(Order.desc("NTC." + MciNotasCCIH.Fields.DT_HR_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		return executeCriteria(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotasCCIHNaoEncerradas(final Integer codigoPaciente) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.isNull("NTC." + MciNotasCCIH.Fields.DT_HR_ENCERRAMENTO.toString()));
		
		criteria.add(Restrictions.eq("NTC." + MciNotasCCIH.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.addOrder(Order.desc("NTC." + MciNotasCCIH.Fields.DT_HR_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotasCCIH.class, "NTC");
		criteria.createAlias("NTC." + MciNotasCCIH.Fields.SERVIDOR.toString(), "SER1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER1." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NTC." + MciNotasCCIH.Fields.SERVIDOR_MOVIMENTADO.toString(), "SER2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER2." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF2", JoinType.LEFT_OUTER_JOIN);
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("(CASE WHEN pf2x4_.NOME IS NULL ");
		sqlProjection.append(" THEN pf1x2_.NOME ");
		sqlProjection.append(" ELSE pf2x4_.NOME ");
		sqlProjection.append(" END) nomeServidor");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{NotificacoesGeraisVO.Fields.NOME_SERVIDOR.toString()}, new Type[]{StringType.INSTANCE})
						, NotificacoesGeraisVO.Fields.NOME_SERVIDOR.toString())
				.add(Projections.property("NTC." + MciNotasCCIH.Fields.SEQ.toString())
						, NotificacoesGeraisVO.Fields.SEQ.toString())
				.add(Projections.property("NTC." + MciNotasCCIH.Fields.DESCRICAO.toString())
						, NotificacoesGeraisVO.Fields.DESCRICAO.toString())
				.add(Projections.property("NTC." + MciNotasCCIH.Fields.DT_HR_INICIO.toString())
						, NotificacoesGeraisVO.Fields.DT_INICIO.toString()));
		
		return criteria;
	}
		
	public List<MciNotasCCIH> bucarNotasCCIHPorPacCodigo(final Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotasCCIH.class, "NTC");
		criteria.createAlias("NTC." + MciNotasCCIH.Fields.SERVIDOR.toString(), "SER1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("NTC." + MciNotasCCIH.Fields.SERVIDOR_MOVIMENTADO.toString(), "SER2", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("NTC." + MciNotasCCIH.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.addOrder(Order.desc("NTC." + MciNotasCCIH.Fields.DT_HR_INICIO.toString()));
		return executeCriteria(criteria);
	}
	
}
