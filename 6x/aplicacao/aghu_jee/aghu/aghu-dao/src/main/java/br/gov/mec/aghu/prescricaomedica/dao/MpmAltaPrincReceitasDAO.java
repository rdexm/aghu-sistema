package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaPrincReceitas;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.vo.AltaPrincReceitasVO;



public class MpmAltaPrincReceitasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaPrincReceitas> {

	private static final long serialVersionUID = 2880437058654674167L;

	
	public List<AltaPrincReceitasVO> obterListaMedicamentosPrescritosAlta(Integer apaAtdSeq, Integer apaSeq){
			
		//subQuery
		
		DetachedCriteria subQuery1 = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
		subQuery1.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		subQuery1.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
		subQuery1.setProjection(Projections.projectionList().add(Projections.max(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString()));

		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
				
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
		criteria.add(Subqueries.propertiesEq(new String[] {MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()}, subQuery1));
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString()), AltaPrincReceitasVO.Fields.DESC_RECEITA.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.IND_CARGA.toString()), AltaPrincReceitasVO.Fields.IND_CARGA.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString()), AltaPrincReceitasVO.Fields.ASU_APA_ATD_SEQ.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString()), AltaPrincReceitasVO.Fields.ASU_APA_SEQ.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString())
				
				));
 
	    criteria.setResultTransformer(Transformers.aliasToBean(AltaPrincReceitasVO.class));
	
		return  executeCriteria(criteria);
	}
			
	public List<AltaPrincReceitasVO> obterMedicamentosPrescritosAlta(String descricao, MpmAltaSumario altoSumario){
		        
		Integer apaAtdSeq  = altoSumario.getId().getApaAtdSeq();
		Integer apaSeq = altoSumario.getId().getApaSeq();
				 
		//subQuery
		
		DetachedCriteria subQuery1 = DetachedCriteria.forClass(MpmAltaPrincReceitas.class, "PRC");
		subQuery1.add(Restrictions.eq("PRC."+MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		subQuery1.add(Restrictions.eq("PRC."+MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
		subQuery1.add(Restrictions.eq("PRC."+MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString(), descricao));
		subQuery1.setProjection(Projections.projectionList().add(Projections.max("PRC."+MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString()));
		 	
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
		
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString(), descricao));
		criteria.add(Subqueries.propertiesEq(new String[] {MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()}, subQuery1));
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString()), AltaPrincReceitasVO.Fields.DESC_RECEITA.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.IND_CARGA.toString()), AltaPrincReceitasVO.Fields.IND_CARGA.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString()), AltaPrincReceitasVO.Fields.ASU_APA_ATD_SEQ.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString()), AltaPrincReceitasVO.Fields.ASU_APA_SEQ.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString())
				.add(Projections.property(MpmAltaPrincReceitas.Fields.SEQP.toString()), AltaPrincReceitasVO.Fields.SEQP.toString())
				);
 
	    criteria.setResultTransformer(Transformers.aliasToBean(AltaPrincReceitasVO.class));
	
		return executeCriteria(criteria);
	}
	
	
public List<AltaPrincReceitasVO> obterListaMedicamentosPrescritosAltaAux(Integer apaAtdSeq, Integer apaSeq){
		
	//subQuery
	
	DetachedCriteria subQuery1 = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
	subQuery1.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
	subQuery1.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
	subQuery1.setProjection(Projections.projectionList().add(Projections.max(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString()));

	
	DetachedCriteria subQuery2 = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
			
	subQuery2.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
	subQuery2.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
	subQuery2.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
	subQuery2.add(Subqueries.propertiesEq(new String[] {MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()}, subQuery1));
	
	StringBuilder descricao = new StringBuilder(100);
	if(isOracle()){
		descricao.append("SUBSTR({alias}.DESC_RECEITA,0,INSTR({alias}.DESC_RECEITA,'-', 1, 1)-1)");
	}else{
		descricao.append("SUBSTRING({alias}.DESC_RECEITA, 0, POSITION('-' IN {alias}.DESC_RECEITA)-1)");
	}
	subQuery2.setProjection(Projections.projectionList().add(Projections.sqlProjection(descricao.toString(), new String [] {"DESC_RECEITA"}, new Type[] {new StringType()})));

	
	DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceituario.class);
	
	criteria.createAlias(MamItemReceituario.Fields.RECEITUARIO.toString(), "RCT", JoinType.LEFT_OUTER_JOIN);
	
	criteria.setProjection(Projections.distinct(Projections.projectionList()
			.add(Projections.property(MamItemReceituario.Fields.DESCRICAO.toString()), AltaPrincReceitasVO.Fields.DESCRICAO.toString())
			.add(Projections.property(MamItemReceituario.Fields.QUANTIDADE.toString()), AltaPrincReceitasVO.Fields.QUANTIDADE.toString())
			.add(Projections.property(MamItemReceituario.Fields.IND_INTERNO.toString()), AltaPrincReceitasVO.Fields.IND_INTERNO.toString())
			.add(Projections.property(MamItemReceituario.Fields.IND_USO_CONTINUO.toString()), AltaPrincReceitasVO.Fields.IND_USO_CONTINUO.toString())
			.add(Projections.property(MamItemReceituario.Fields.IND_SITUACAO.toString()), AltaPrincReceitasVO.Fields.IND_SITUACAO.toString())
			.add(Projections.property(MamItemReceituario.Fields.SEQP.toString()), AltaPrincReceitasVO.Fields.SEQP.toString())));
			
	criteria.add(Restrictions.and(Restrictions.eq("RCT."+MamReceituarios.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq),
								 Restrictions.eq("RCT."+MamReceituarios.Fields.ASU_APA_SEQ.toString(), apaSeq)));
	criteria.add(Restrictions.not(Subqueries.propertiesIn(new String[] {MamItemReceituario.Fields.DESCRICAO.toString()}, subQuery2)));

   criteria.setResultTransformer(Transformers.aliasToBean(AltaPrincReceitasVO.class));

	return  executeCriteria(criteria);
	}


public List<AltaPrincReceitasVO> obterListaMedicamentosPrescritosAltaAux2(Integer apaAtdSeq, Integer apaSeq, Short asuSeqp){
		
	//subQuery
	
	DetachedCriteria subQuery1 = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
	subQuery1.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
	subQuery1.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
	subQuery1.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString(), asuSeqp));
	subQuery1.setProjection(Projections.projectionList().add(Projections.max(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString()));

	
	DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
			
	criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
	criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
	criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString(), asuSeqp));
	criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.IND_SITUACAO.toString(), DominioSituacao.A.toString()));
	criteria.add(Subqueries.propertiesEq(new String[] {MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()}, subQuery1));
	criteria.setProjection(Projections.projectionList()
			.add(Projections.property(MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString()), AltaPrincReceitasVO.Fields.DESC_RECEITA.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.IND_CARGA.toString()), AltaPrincReceitasVO.Fields.IND_CARGA.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString()), AltaPrincReceitasVO.Fields.ASU_APA_ATD_SEQ.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString()), AltaPrincReceitasVO.Fields.ASU_APA_SEQ.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.SEQP.toString()), AltaPrincReceitasVO.Fields.SEQP.toString())
			);

    criteria.setResultTransformer(Transformers.aliasToBean(AltaPrincReceitasVO.class));

	return  executeCriteria(criteria);
	}

public List<AltaPrincReceitasVO> obterMedicamentosPrescritosAltaAux(Integer apaAtdSeq, Integer apaSeq, String descReceita){
    			 
	//subQuery
	
	DetachedCriteria subQuery1 = DetachedCriteria.forClass(MpmAltaPrincReceitas.class, "PRC");
	subQuery1.add(Restrictions.eq("PRC."+MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
	subQuery1.add(Restrictions.eq("PRC."+MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
	subQuery1.add(Restrictions.eq("PRC."+MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString(), descReceita));
	subQuery1.setProjection(Projections.projectionList().add(Projections.max("PRC."+MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString()));
	 	
	DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
	
	criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
	criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
	criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString(), descReceita));
	criteria.add(Subqueries.propertiesEq(new String[] {MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()}, subQuery1));
	criteria.setProjection(Projections.projectionList()
			.add(Projections.property(MpmAltaPrincReceitas.Fields.DESC_RECEITA.toString()), AltaPrincReceitasVO.Fields.DESC_RECEITA.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.IND_CARGA.toString()), AltaPrincReceitasVO.Fields.IND_CARGA.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString()), AltaPrincReceitasVO.Fields.ASU_APA_ATD_SEQ.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString()), AltaPrincReceitasVO.Fields.ASU_APA_SEQ.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString()), AltaPrincReceitasVO.Fields.ASU_SEQP.toString())
			.add(Projections.property(MpmAltaPrincReceitas.Fields.SEQP.toString()), AltaPrincReceitasVO.Fields.SEQP.toString())
			);

    criteria.setResultTransformer(Transformers.aliasToBean(AltaPrincReceitasVO.class));

	return executeCriteria(criteria);
	}

	public void deleteEmLoteByID(Integer apaAtdSeq, Integer apaSeq, Short asuSeqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), apaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), apaSeq));
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString(), asuSeqp));
		List<MpmAltaPrincReceitas> listAltaPrincReceita = executeCriteria(criteria);
		if(listAltaPrincReceita != null && listAltaPrincReceita.size() > 0){
			for (MpmAltaPrincReceitas mpmAltaPrincReceitas : listAltaPrincReceita) {
				this.remover(mpmAltaPrincReceitas);
			}
		}
	}
	
	public List<MpmAltaPrincReceitas> obterAltaPrincReceitasPorAltaSumario(MpmAltaSumario altaSumario){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPrincReceitas.class);
		
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_ATD_SEQ.toString(), altaSumario.getId().getApaAtdSeq()));
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_APA_SEQ.toString(), altaSumario.getId().getApaSeq()));
		criteria.add(Restrictions.eq(MpmAltaPrincReceitas.Fields.ASU_SEQP.toString(), altaSumario.getId().getSeqp().shortValue()));
		
		return executeCriteria(criteria);
		
	}

}