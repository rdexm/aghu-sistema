package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.faturamento.vo.FatTabProcCidVO;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.FatProcedHospIntCidId;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatProcedHospIntCidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedHospIntCid> {
	
	private static final long serialVersionUID = -3433063570319013348L;
	
	private static final Log LOG = LogFactory.getLog(FatProcedHospIntCidDAO.class);

	public List<FatTabProcCidVO> buscarProcCid() {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, "pcid");
		criteria.createAlias(FatProcedHospIntCid.Fields.CID.toString(), "cids");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("pcid." + FatProcedHospIntCid.Fields.PHI_SEQ.toString()), "phiSeq")
				.add(Projections.property("cids." + AghCid.Fields.CODIGO.toString()), "cid"));
		criteria.setResultTransformer(Transformers.aliasToBean(FatTabProcCidVO.class));
		
		criteria.add(Restrictions.or(Restrictions.isNull("pcid."+FatProcedHospIntCid.Fields.VALIDADE.toString()),Restrictions.ne("pcid."+FatProcedHospIntCid.Fields.VALIDADE.toString(), DominioTipoPlano.I)));
		criteria.add(Restrictions.or(Restrictions.isNull("pcid."+FatProcedHospIntCid.Fields.PRINCIPAL.toString()),Restrictions.eq("pcid."+FatProcedHospIntCid.Fields.PRINCIPAL.toString(), Boolean.TRUE)));
		
		criteria.addOrder(Order.asc(FatProcedHospIntCid.Fields.PHI_SEQ.toString()));
		criteria.addOrder(Order.asc(FatProcedHospIntCid.Fields.CID_SEQ.toString()));
		
		final List<FatTabProcCidVO> retorno = executeCriteria(criteria);
		if(retorno == null || retorno.isEmpty()){
			return new ArrayList<FatTabProcCidVO>(0);
		}
		for (FatTabProcCidVO fatTabProcCidVO : retorno) {
			int i = fatTabProcCidVO.getCid().indexOf('.');
			if(i >= 0){
				fatTabProcCidVO.setCid(fatTabProcCidVO.getCid().substring(0,i) + fatTabProcCidVO.getCid().substring(i+1));
			}
		}
		
		return retorno;
		
//		Query query = getSession()
//				.createSQLQuery(
//						" SELECT pcid.phi_seq as phiseq, RPAD(replace(cids.codigo, '.', ''),4,' ') cid FROM agh.agh_cids cids, agh.fat_proced_hosp_int_cid pcid WHERE CASE when pcid.ind_validade is null then ' ' else pcid.ind_validade end <> 'I' AND CASE when pcid.ind_principal is null then ' ' else pcid.ind_principal end IN (' ','S') AND cids.seq = pcid.cid_Seq ORDER BY 1,2 ");
//		query.setResultTransformer(Transformers.aliasToBean(FatTabProcCidVO.class));
//		return query.list();
	}
	
	public List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidPorPhiSeq(Integer phiSeq)  {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class);
		criteria.createAlias(FatProcedHospIntCid.Fields.CID.toString(), FatProcedHospIntCid.Fields.CID.toString(), JoinType.LEFT_OUTER_JOIN);
		if (phiSeq != null){
			criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		}
		return executeCriteria(criteria);

	}

	
	public Integer obterSeqPorFatProcedHospIntCidId(final FatProcedHospIntCidId id)  {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class);
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.CID_SEQ.toString(), id.getCidSeq()));
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), id.getPhiSeq()));
		
		criteria.setProjection(Projections.property(FatProcedHospIntCid.Fields.CID_SEQ.toString()));
		
		return (Integer) executeCriteriaUniqueResult(criteria);

	}
	
	public Long obterQtdeFatProcedHospIntCid(final Integer phiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class);
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		return executeCriteriaCount(criteria);
	}
	
	
	
	
	public List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidAtivosPorPhiSeq(Integer phiSeq) {
		String aliasProced = "proced";
		String separador = ".";
		String aliasCid = "cid";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, aliasProced);
		criteria.createAlias(FatProcedHospIntCid.Fields.CID.toString(), aliasCid);
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.eq(aliasProced + separador + FatProcedHospIntCid.Fields.PHI_SEQ, phiSeq));
		criteria.add(Restrictions.or(Restrictions.isNull(aliasProced + separador + FatProcedHospIntCid.Fields.VALIDADE.toString()), 
				Restrictions.eq(aliasProced + separador + FatProcedHospIntCid.Fields.VALIDADE.toString(), DominioTipoPlano.A)));
		criteria.add(Restrictions.eq(aliasCid + separador + AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
	public List<FatProcedHospIntCid> pesquisarFatProcedHospIntCidAtivosPorPhiSeqCidSeq(Integer phiSeq, Integer cidSeq) {
		String aliasProced = "proced";
		String separador = ".";
		String aliasCid = "cid";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, aliasProced);
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.createAlias(FatProcedHospIntCid.Fields.CID.toString(), aliasCid);
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.CID_SEQ.toString(), cidSeq));
		criteria.add(Restrictions.eq(aliasProced + separador + FatProcedHospIntCid.Fields.PHI_SEQ, phiSeq));
		criteria.add(Restrictions.or(Restrictions.isNull(aliasProced + separador + FatProcedHospIntCid.Fields.VALIDADE.toString()), 
				Restrictions.eq(aliasProced + separador + FatProcedHospIntCid.Fields.VALIDADE.toString(), DominioTipoPlano.A)));
		criteria.add(Restrictions.eq(aliasCid + separador + AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public void deleteByPhiSeqs(final Integer phiSeq, final String[] phiSeqs) {
		final StringBuffer hql = new StringBuffer().append(" delete from ").append(FatProcedHospIntCid.class.getName()).append(" as fph ");
		hql.append(" where fph.").append(FatProcedHospIntCid.Fields.PHI_SEQ.toString()).append(" = :phiSeq ");
		hql.append(" and fph.").append(FatProcedHospIntCid.Fields.PHI_SEQ.toString()).append(" not in (");
		for (String string : phiSeqs) {
			hql.append(string).append(',');
		}
		hql.deleteCharAt(hql.length() - 1 );
		hql.append(')');
		
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("phiSeq", phiSeq);
		
		int rowCount = query.executeUpdate();		
		LOG.info("Rows affected: " + rowCount);
	}	
	
	public  List<FatProcedHospIntCid> pesquisarProcedimentoHospitalarInternoCidCompativel(final Integer phiSeq, final Integer cidSeq, DominioOrigemPacienteCirurgia origem) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class);
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		if(cidSeq != null){
			criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.CID_SEQ.toString(), cidSeq));
		}
		
		List<FatProcedHospIntCid> resultado = new LinkedList<FatProcedHospIntCid>();
		List<FatProcedHospIntCid> resultadoParcial = executeCriteria(criteria);

		for (FatProcedHospIntCid item : resultadoParcial) {
			
			if(item.getValidade() != null){
				String origemStr = origem.toString();
				String origemValidade = item.getValidade().toString();
				if(CoreUtil.igual(origemStr,origemValidade)){
					resultado.add(item);
				}
				continue;
			}
			resultado.add(item);
			
		}

		return resultado;
	}
	
	public FatProcedHospIntCid pesquisarFatProcedHospIntCidPorPhiSeqValidade(Integer phiSeq, DominioTipoPlano validade) {		
		DetachedCriteria criteria = obterCriteriaPesquisarFatProcedHosp(phiSeq, validade);
		
		List<FatProcedHospIntCid> lista = executeCriteria(criteria, 0, 1, null, false);
		
		return lista != null && !lista.isEmpty() ? lista.get(0) : null;
	}
	
	public List<FatProcedHospIntCid> listarFatProcedHospIntCidPorPhiSeqValidade(
			Integer phiSeq, DominioTipoPlano validade, String filtro) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class, "fph");
		
		criteria.createAlias("fph." + FatProcedHospIntCid.Fields.CID.toString(), "cid");
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.or(Restrictions.isNull(FatProcedHospIntCid.Fields.VALIDADE.toString()),
				Restrictions.eq(FatProcedHospIntCid.Fields.VALIDADE.toString(),	validade)));
		
		if (filtro != null && !"".equals(filtro)) {
			criteria.add(Restrictions.ilike("cid." + AghCid.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
		}
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisarFatProcedHosp(Integer phiSeq, DominioTipoPlano validade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedHospIntCid.class);
		criteria.add(Restrictions.eq(FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		criteria.add(Restrictions.or(Restrictions.isNull(FatProcedHospIntCid.Fields.VALIDADE.toString()),
					Restrictions.eq(FatProcedHospIntCid.Fields.VALIDADE.toString(), validade)));
		return criteria;
	}
}
