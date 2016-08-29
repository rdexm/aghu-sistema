package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.CursorMotivoCadastroSugestaoDesdobramentoCirurgicoVO;
import br.gov.mec.aghu.faturamento.vo.MotivoDesdobramentoCadastroSugestaoDesdobramentoVO;
import br.gov.mec.aghu.model.FatMotivoDesdobrClinica;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatMotivoDesdobramentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMotivoDesdobramento> {

	private static final long serialVersionUID = -7302966411266001956L;

	public List<FatMotivoDesdobramento> listarMotivosDesdobramentos(String filtro, Byte seqTipoAih) {
		DetachedCriteria criteria = createCriteriaListarMotivosDesdobramentos(filtro, seqTipoAih);
		return executeCriteria(criteria, 0, 25, null, false);
	}

	public Long listarMotivosDesdobramentosCount(String filtro, Byte seqTipoAih) {
		DetachedCriteria criteria = createCriteriaListarMotivosDesdobramentos(filtro, seqTipoAih);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createCriteriaListarMotivosDesdobramentos(String filtro, Byte seqTipoAih) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoDesdobramento.class);

		criteria.createAlias(FatMotivoDesdobramento.Fields.TIPO_AIH.toString(), FatMotivoDesdobramento.Fields.TIPO_AIH.toString());

		if (CoreUtil.isNumeroByte(filtro)) {
			Byte aux = Byte.valueOf(filtro);

			criteria.add(Restrictions.or(
					Restrictions.ilike(FatMotivoDesdobramento.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE),
					Restrictions.or(Restrictions.eq(FatMotivoDesdobramento.Fields.SEQ.toString(), aux),
							Restrictions.eq(FatMotivoDesdobramento.Fields.CODIGO_SUS.toString(), aux))));
		} else {
			criteria.add(Restrictions.ilike(FatMotivoDesdobramento.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		}

		if (seqTipoAih != null) {
			criteria.add(Restrictions.eq(FatMotivoDesdobramento.Fields.TIPO_AIH.toString() + "." + FatTipoAih.Fields.SEQ.toString(),
					seqTipoAih));
		}

		criteria.add(Restrictions.eq(FatMotivoDesdobramento.Fields.SITUACAO_REGISTRO.toString(), DominioSituacao.A));

		return criteria;
	}

	@SuppressWarnings("unchecked")
	public List<MotivoDesdobramentoCadastroSugestaoDesdobramentoVO> listaMotivosDesdobramentoCadastroSugestaoDesdobramento(Byte tahSeq, Byte clcCodigo,
			Byte mdsSeq, DominioSituacao situacaoRegistro) {
		StringBuffer hql = new StringBuffer(300);
		
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.MotivoDesdobramentoCadastroSugestaoDesdobramentoVO(");
		hql.append("	  md.").append(FatMotivoDesdobramento.Fields.SEQ.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.DIAS_APOS_INTERNACAO.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.QTD_MINIMA.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.DIAS_APOS_PROCEDIMENTO.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.TIPO_DESDOBRAMENTO.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.CODIGO_SUS.toString());
		hql.append(')');
		hql.append(" from ").append(FatMotivoDesdobramento.class.getSimpleName()).append(" as md ");
		hql.append(" where md.").append(FatMotivoDesdobramento.Fields.TAH_SEQ.toString()).append(" = :tahSeq ");
		hql.append(" 	and md.").append(FatMotivoDesdobramento.Fields.SITUACAO_REGISTRO.toString()).append(" = :situacaoRegistro ");
		hql.append(" 	and exists (");
		hql.append(" 		select 1 ");
		hql.append(" 		from ").append(FatMotivoDesdobrClinica.class.getSimpleName()).append(" as mdc ");
		hql.append(" 		where mdc.").append(FatMotivoDesdobrClinica.Fields.MDS_SEQ.toString()).append(" = md.").append(FatMotivoDesdobramento.Fields.SEQ.toString());
		hql.append(" 			and mdc.").append(FatMotivoDesdobrClinica.Fields.CLC_CODIGO.toString()).append(" = :clcCodigo ");
		hql.append(" 	)");
		
		if (mdsSeq != null) {
			hql.append(" 	and md.").append(FatMotivoDesdobramento.Fields.SEQ.toString()).append(" = :mdsSeq ");
		}

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("tahSeq", tahSeq);
		query.setParameter("clcCodigo", clcCodigo.intValue());
		query.setParameter("situacaoRegistro", situacaoRegistro);
		if (mdsSeq != null) {
			query.setParameter("mdsSeq", mdsSeq);
		}

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<CursorMotivoCadastroSugestaoDesdobramentoCirurgicoVO> listarMotivosCadastroSugestaoDesdobramentoCirurgico(Byte tahSeq,
			Integer clcCodigo, DominioSituacao situacaoRegistro, Byte[] codigosSUS) {
		StringBuffer hql = new StringBuffer(300);
		
		hql.append(" select new br.gov.mec.aghu.faturamento.vo.CursorMotivoCadastroSugestaoDesdobramentoCirurgicoVO(");
		hql.append("	  md.").append(FatMotivoDesdobramento.Fields.SEQ.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.DIAS_APOS_INTERNACAO.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.QTD_MINIMA.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.DIAS_APOS_PROCEDIMENTO.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.TIPO_DESDOBRAMENTO.toString());
		hql.append("	, md.").append(FatMotivoDesdobramento.Fields.CODIGO_SUS.toString());
		hql.append(')');
		hql.append(" from ").append(FatMotivoDesdobramento.class.getSimpleName()).append(" as md ");
		hql.append(" 	join md.").append(FatMotivoDesdobramento.Fields.MOTIVOS_DESDOBR_CLINICAS.toString()).append(" as mdc ");
		hql.append(" 	join md.").append(FatMotivoDesdobramento.Fields.TIPO_AIH.toString()).append(" as tah ");
		hql.append(" where tah.").append(FatTipoAih.Fields.SEQ.toString()).append(" = :tahSeq ");
		hql.append(" 	and md.").append(FatMotivoDesdobramento.Fields.SITUACAO_REGISTRO.toString()).append(" = :situacaoRegistro ");
		hql.append(" 	and md.").append(FatMotivoDesdobramento.Fields.CODIGO_SUS.toString()).append(" in (:codigosSUS) ");
		hql.append(" 	and mdc.").append(FatMotivoDesdobrClinica.Fields.CLC_CODIGO.toString()).append(" = :clcCodigo ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("tahSeq", tahSeq);
		query.setParameter("clcCodigo", clcCodigo);
		query.setParameter("situacaoRegistro", situacaoRegistro);
		query.setParameterList("codigosSUS", codigosSUS);
		
		return query.list();
	}
	
	
	public List<FatMotivoDesdobramento> listarMovimentoDesdobramento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FatMotivoDesdobramento motivoDesdobramento) {
		DetachedCriteria criteria = createMotivoDesdobramentoCriteria(motivoDesdobramento, true);

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarMotivoDesdobramentoCount(FatMotivoDesdobramento motivoDesdobramento) {
		DetachedCriteria criteria = createMotivoDesdobramentoCriteria(motivoDesdobramento, false);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createMotivoDesdobramentoCriteria(FatMotivoDesdobramento motivoDesdobramento, boolean order) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoDesdobramento .class);

		criteria.createAlias(FatMotivoDesdobramento.Fields.TIPO_AIH.toString(),
				FatMotivoDesdobramento.Fields.TIPO_AIH.toString(), JoinType.INNER_JOIN);
		
		if (motivoDesdobramento.getSeq() != null) {
			criteria.add(Restrictions.eq(FatMotivoDesdobramento.Fields.SEQ.toString(), motivoDesdobramento.getSeq()));
		}
		
		if (motivoDesdobramento.getCodigoSus() != null) {
			criteria.add(Restrictions.eq(FatMotivoDesdobramento.Fields.CODIGO_SUS.toString(), motivoDesdobramento.getCodigoSus()));
		}

		if (StringUtils.isNotBlank(motivoDesdobramento.getDescricao())) {
			criteria.add(Restrictions.ilike(FatMotivoDesdobramento.Fields.DESCRICAO.toString(), motivoDesdobramento.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (motivoDesdobramento.getTipoDesdobramento() != null) {
			criteria.add(Restrictions.eq(FatMotivoDesdobramento.Fields.TIPO_DESDOBRAMENTO.toString(), motivoDesdobramento.getTipoDesdobramento()));
		}

		if (motivoDesdobramento.getTipoAih() != null) {
			criteria.add(Restrictions.eq(FatMotivoDesdobramento.Fields.TIPO_AIH.toString(), motivoDesdobramento.getTipoAih()));
		}
		
		if (motivoDesdobramento.getSituacaoRegistro() != null) {
			criteria.add(Restrictions.eq(FatMotivoDesdobramento.Fields.SITUACAO_REGISTRO.toString(), motivoDesdobramento.getSituacaoRegistro()));
		}
		
		if (order) {
			criteria.addOrder(Order.asc(FatMotivoDesdobramento.Fields.SEQ.toString()));
		}

		return criteria;
	}

}
