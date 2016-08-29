package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioCumpriuJejumColeta;
import br.gov.mec.aghu.dominio.DominioLocalColetaAmostra;
import br.gov.mec.aghu.model.AelInformacaoColeta;
import br.gov.mec.aghu.model.AelInformacaoMdtoColeta;

public class AelInformacaoColetaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelInformacaoColeta> {
	
	private static final long serialVersionUID = 9171548370242587976L;

	public Long obterCountInformacaoColetaPorSoeSeq(Integer soeSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelInformacaoColeta.class);
		criteria.add(Restrictions.eq(AelInformacaoColeta.Fields.SOE_SEQ.toString(), soeSeq));
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Lista Informações de coleta por soe_seq
	 * @param parametro
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AelInformacaoColeta> listarInformacoesPorSoeSeq(Integer soeSeq) {
		StringBuilder hql = new StringBuilder(400);
		
		hql.append(" select icl ");
		hql.append(" from ").append(AelInformacaoColeta.class.getName()).append(" as icl ");
		hql.append(" where ");
		hql.append(" 	icl.").append(AelInformacaoColeta.Fields.SOE_SEQ).append(" = :soeSeq ");
		hql.append(" 	and (");
		hql.append(" 		icl.").append(AelInformacaoColeta.Fields.CUMPRIU_JEJUM).append(" in (:cumpriuJejumList) ");
		hql.append(" 		or icl.").append(AelInformacaoColeta.Fields.DOCUMENTO).append(" = :documento ");
		hql.append(" 		or icl.").append(AelInformacaoColeta.Fields.LOCAL_COLETA).append(" <> :localColeta ");
		hql.append(" 		or icl.").append(AelInformacaoColeta.Fields.INFORMACOES_ADICIONAIS).append(" is not null ");
		hql.append(" 		or icl.").append(AelInformacaoColeta.Fields.DT_ULT_MENSTRUACAO).append(" is not null ");
		hql.append(" 		or icl.").append(AelInformacaoColeta.Fields.INF_MENSTRUACAO).append(" = :infMenstruacao ");
		hql.append(" 		or icl.").append(AelInformacaoColeta.Fields.INF_MEDICACAO).append(" = :infMedicacao ");
		hql.append(" 		or exists ( ");
		hql.append(" 			select imc.id ");
		hql.append(" 			from ").append(AelInformacaoMdtoColeta.class.getName()).append(" as imc ");
		hql.append(" 			where ");
		hql.append(" 				icl.").append(AelInformacaoColeta.Fields.SOE_SEQ).append(" = imc.").append(AelInformacaoMdtoColeta.Fields.ICL_SOE_SEQ);
		hql.append(" 				and icl.").append(AelInformacaoColeta.Fields.SEQP).append(" = imc.").append(AelInformacaoMdtoColeta.Fields.ICL_SEQP);
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
