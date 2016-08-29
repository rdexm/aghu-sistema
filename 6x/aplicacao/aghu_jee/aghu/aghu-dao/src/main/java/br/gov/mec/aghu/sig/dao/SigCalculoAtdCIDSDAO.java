package br.gov.mec.aghu.sig.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.model.FatCidContaHospitalar;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.SigCalculoAtdCIDS;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CidFaturamentoVO;

public class SigCalculoAtdCIDSDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtdCIDS> {

	private static final long serialVersionUID = 3781971960889992522L;

	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(100);
		sql.append(" DELETE ").append(SigCalculoAtdCIDS.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigCalculoAtdCIDS.Fields.CALCULO_ATD_PACIENTE.toString()).append('.').append(SigCalculoAtdPaciente.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT c.").append(SigCalculoAtdPaciente.Fields.SEQ.toString());
			sql.append(" FROM ").append( SigCalculoAtdPaciente.class.getSimpleName()).append(" c ");
			sql.append(" WHERE c.").append(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
	
	public List<CidFaturamentoVO> buscarCidsFaturamento(Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class, "cth");
		
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("cch." + FatCidContaHospitalar.Fields.CID.toString()), CidFaturamentoVO.Fields.CID.toString())
			.add(Projections.property("cch." + FatCidContaHospitalar.Fields.PRIORIDADE_CID.toString()), CidFaturamentoVO.Fields.PRIORIDADE_CID.toString())
		);
		
		criteria.createAlias("cth." + FatContasHospitalares.Fields.CID_CONTAS_HOSPITALARES.toString(), "cch", JoinType.INNER_JOIN);
		criteria.createAlias("cch." + FatCidContaHospitalar.Fields.CID.toString(), "cid", JoinType.INNER_JOIN);
		criteria.createAlias("cth." + FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "coi", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("coi." + FatContasInternacao.Fields.INT_SEQ.toString(), intSeq));
		criteria.add(Restrictions.ne("cch." + FatCidContaHospitalar.Fields.PRIORIDADE_CID.toString(), DominioPrioridadeCid.N));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CidFaturamentoVO.class));
		
		return executeCriteria(criteria);
	}
}
