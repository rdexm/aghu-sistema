package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatProcedimentoModalidade;

public class FatProcedimentoModalidadeDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedimentoModalidade> {

	private static final long serialVersionUID = -3420171272742112705L;

	public List<FatProcedimentoModalidade> listarProcedimentosModalidade(
			Short phoSeq, Integer iphSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatProcedimentoModalidade.class);

		criteria.add(Restrictions
				.eq(FatProcedimentoModalidade.Fields.IPH_PHO_SEQ.toString(),
						phoSeq));

		criteria.add(Restrictions.eq(FatProcedimentoModalidade.Fields.IPH_SEQ
				.toString(), iphSeq));

		criteria.add(Restrictions.in(
				FatProcedimentoModalidade.Fields.MOT_CODIGO.toString(),
				new Short[] { 2, 3 }));

		return executeCriteria(criteria);
	}
	
public List<Short> obterListaMotCodigo(Integer p_iph_seq, Short p_pho_seq){
		
		final Short[] values = {2,3};
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedimentoModalidade.class);
		
		criteria.setProjection(Projections.property(FatProcedimentoModalidade.Fields.MOT_CODIGO.toString()));
		criteria.add(Restrictions.eq(FatProcedimentoModalidade.Fields.IPH_PHO_SEQ.toString(), p_pho_seq));
		criteria.add(Restrictions.eq(FatProcedimentoModalidade.Fields.IPH_SEQ.toString(), p_iph_seq));
		criteria.add(Restrictions.in(FatProcedimentoModalidade.Fields.MOT_CODIGO.toString(), values));
		
		List<Short> listaRetornoModalidade = new ArrayList<Short>();
		
		listaRetornoModalidade = executeCriteria(criteria);
        
        return listaRetornoModalidade;
	}

}
