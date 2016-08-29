package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioCumpriuJejumColeta;
import br.gov.mec.aghu.dominio.DominioLocalColetaAmostra;
import br.gov.mec.aghu.model.AelInformacaoColetaHist;
import br.gov.mec.aghu.model.AelInformacaoMdtoColetaHist;

public class AelInformacaoColetaHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelInformacaoColetaHist> {
	
	private static final long serialVersionUID = -4769508555201092735L;

	public Long obterCountInformacaoColetaPorSoeSeq(Integer soeSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelInformacaoColetaHist.class);
		criteria.add(Restrictions.eq(AelInformacaoColetaHist.Fields.SOE_SEQ.toString(), soeSeq));
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Lista Informações de coleta por soe_seq
	 * @param parametro
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AelInformacaoColetaHist> listarInformacoesPorSoeSeq(Integer soeSeq) {
		StringBuilder hql = new StringBuilder(400);
		
		hql.append(" select icl ");
		hql.append(" from ").append(AelInformacaoColetaHist.class.getName()).append(" as icl ");
		hql.append(" where ");
		hql.append(" 	icl.").append(AelInformacaoColetaHist.Fields.SOE_SEQ).append(" = :soeSeq ");
		hql.append(" 	and (");
		hql.append(" 		icl.").append(AelInformacaoColetaHist.Fields.CUMPRIU_JEJUM).append(" in (:cumpriuJejumList) ");
		hql.append(" 		or icl.").append(AelInformacaoColetaHist.Fields.DOCUMENTO).append(" = :documento ");
		hql.append(" 		or icl.").append(AelInformacaoColetaHist.Fields.LOCAL_COLETA).append(" <> :localColeta ");
		hql.append(" 		or icl.").append(AelInformacaoColetaHist.Fields.INFORMACOES_ADICIONAIS).append(" is not null ");
		hql.append(" 		or icl.").append(AelInformacaoColetaHist.Fields.DT_ULT_MENSTRUACAO).append(" is not null ");
		hql.append(" 		or icl.").append(AelInformacaoColetaHist.Fields.INF_MENSTRUACAO).append(" = :infMenstruacao ");
		hql.append(" 		or icl.").append(AelInformacaoColetaHist.Fields.INF_MEDICACAO).append(" = :infMedicacao ");
		hql.append(" 		or exists ( ");
		hql.append(" 			select imc.id ");
		hql.append(" 			from ").append(AelInformacaoMdtoColetaHist.class.getName()).append(" as imc ");
		hql.append(" 			where ");
		hql.append(" 				icl.").append(AelInformacaoColetaHist.Fields.SOE_SEQ).append(" = imc.").append(AelInformacaoMdtoColetaHist.Fields.ICL_SOE_SEQ);
		hql.append(" 				and icl.").append(AelInformacaoColetaHist.Fields.SEQP).append(" = imc.").append(AelInformacaoMdtoColetaHist.Fields.ICL_SEQP);
		hql.append(" 		)");
		hql.append(" 	)");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("soeSeq", soeSeq);
		query.setParameter("documento", Boolean.FALSE);
		query.setParameter("localColeta", DominioLocalColetaAmostra.C);
		query.setParameter("infMenstruacao", Boolean.TRUE);
		query.setParameter("infMedicacao", Boolean.TRUE);
		query.setParameterList("cumpriuJejumList", new DominioCumpriuJejumColeta[] {DominioCumpriuJejumColeta.N, DominioCumpriuJejumColeta.P});
		
		return query.list();
	}

}
