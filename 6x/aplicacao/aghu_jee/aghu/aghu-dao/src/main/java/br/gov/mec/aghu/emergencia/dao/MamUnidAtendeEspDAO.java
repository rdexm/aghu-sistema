package br.gov.mec.aghu.emergencia.dao;



import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamEmgEspecialidades;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.MamUnidAtendeEsp;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamUnidAtendeEspDAO extends BaseDao<MamUnidAtendeEsp> {

	private static final long serialVersionUID = 8027343146470265096L;
	
	private static final String MAM_UNID_ATENDE_ESP = "MamUnidAtendeEsp.";

	public Boolean existeUnidAtendeEspPorUnidadeAtendem(Short unfSeq){	
		return (this.executeCriteriaCount(this.obterCriteriaPorUnidadeAtendem(unfSeq)) > 0);		
	}	
	
	public Boolean existeUnidAtendeEspPorUnidadeEspecialidade(Short unfSeq, Short espSeq){	
		
		final DetachedCriteria criteria = this.obterCriteriaPorUnidadeAtendem(unfSeq);			
		criteria.add(Restrictions.eq(MamUnidAtendeEsp.Fields.ESP_SEQ.toString(), espSeq));			
		return (this.executeCriteriaCount(criteria) > 0);
	}	
	
    public List<MamUnidAtendeEsp> pesquisarUnidAtendeEspPorUnidadeAtendem(	Integer firstResult,
			Integer maxResult,
			String orderProperty,
			boolean asc,
			Short unfSeq) {		
		return this.executeCriteria(this.obterCriteriaPorUnidadeAtendem(unfSeq), firstResult, maxResult,
				orderProperty, asc);		
	}	
    
    public Long pesquisarUnidAtendeEspPorUnidadeAtendemCount(Short unfSeq) {
		return this.executeCriteriaCount(this.obterCriteriaPorUnidadeAtendem(unfSeq));		
	}	
    
    private DetachedCriteria obterCriteriaPorUnidadeAtendem(Short unfSeq){
    	
        final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidAtendeEsp.class, "MamUnidAtendeEsp");
		
		criteria.add(Restrictions.eq(MamUnidAtendeEsp.Fields.MAM_UNID_ATENDEM_UNF_SEQ.toString(), unfSeq));
		
		return criteria;
    }   
    
    /***
	 *    
	 * @param seqTriagem
	 * @return
	 */
	public List<Short> pesquisarEspecialidadesTriagem(Long seqTriagem){
	    	
	  	final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidAtendeEsp.class, "MamUnidAtendeEsp"); 	
	  	criteria.createAlias(MAM_UNID_ATENDE_ESP + MamUnidAtendeEsp.Fields.MAM_UNID_ATENDEM.toString() , "MamUnidAtendem");
	  	  	
	  	criteria.add(Restrictions.eq(MAM_UNID_ATENDE_ESP + MamUnidAtendeEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	  	
	  	criteria.add(Restrictions.eqProperty(MAM_UNID_ATENDE_ESP + MamUnidAtendeEsp.Fields.IND_SITUACAO.toString(), "MamUnidAtendem." + MamUnidAtendem.Fields.IND_SITUACAO.toString()));
	  		  	
	  	/**criteria.add(Restrictions.and(Restrictions.ge(MAM_UNID_ATENDE_ESP + MamUnidAtendeEsp.Fields.HORA_INICIO_MARCA_CONS.toString(), new Date()),
	  			                     (Restrictions.le(MAM_UNID_ATENDE_ESP + MamUnidAtendeEsp.Fields.HORA_FIM_MARCA_CONS.toString(), new Date()))));
	  **/
	  	
	  	criteria.add(Restrictions.sqlRestriction(" TO_CHAR(current_date, 'hh24mi') BETWEEN TO_CHAR(this_.hora_inicio_marca_cons, 'hh24mi') AND TO_CHAR(this_.hora_fim_marca_cons, 'hh24mi')"));
	  	
	  	DetachedCriteria subQueryTriagens = DetachedCriteria.forClass(MamTriagens.class,"MamTriagens");
	  	subQueryTriagens.setProjection(Projections.projectionList()
				                .add(Projections.property("MamTriagens."+ MamTriagens.Fields.UNF_SEQ.toString())));

	  	subQueryTriagens.add(Restrictions.eqProperty("MamTriagens."+ MamTriagens.Fields.UNF_SEQ.toString(), "MamUnidAtendem."+ MamUnidAtendem.Fields.UNF_SEQ.toString()));
	  	subQueryTriagens.add(Restrictions.eq("MamTriagens."+ MamTriagens.Fields.SEQ.toString(), seqTriagem));
	  	
	  	criteria.add(Subqueries.exists(subQueryTriagens));
	  	
		criteria.setProjection(Projections.distinct(Projections.projectionList()
                .add(Projections.property(MAM_UNID_ATENDE_ESP+ MamUnidAtendeEsp.Fields.ESP_SEQ.toString()))));
		
		return executeCriteria(criteria);
	 }
	
	public List<Boolean> obterListaSituacaoMarcacao(Long trgSeq, Short espSeq) {
		final String UAE = "UAE.";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidAtendeEsp.class, "UAE"); 	
	  	criteria.createAlias(UAE + MamUnidAtendeEsp.Fields.MAM_UNID_ATENDEM.toString() , "UAN");
	  	
	  	criteria.setProjection(Projections.property(UAE + MamUnidAtendeEsp.Fields.IND_SO_MARCA_CONS_DIA.toString()));
	  	  	
	  	criteria.add(Restrictions.eq(UAE + MamUnidAtendeEsp.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	  	criteria.add(Restrictions.eq("UAN." + MamUnidAtendem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	  	criteria.add(Restrictions.eq(UAE + MamUnidAtendeEsp.Fields.ESP_SEQ.toString(), espSeq));
	  	
	  	DetachedCriteria subQueryTriagens = DetachedCriteria.forClass(MamTriagens.class,"TRG");
	  	subQueryTriagens.setProjection(Projections.projectionList()
				                .add(Projections.property("TRG."+ MamTriagens.Fields.SEQ.toString())));

	  	subQueryTriagens.add(Restrictions.eqProperty("TRG."+ MamTriagens.Fields.UNF_SEQ.toString(), "UAN."+ MamUnidAtendem.Fields.UNF_SEQ.toString()));
	  	subQueryTriagens.add(Restrictions.eq("TRG."+ MamTriagens.Fields.SEQ.toString(), trgSeq));
	  	
	  	criteria.add(Subqueries.exists(subQueryTriagens));
	  	
	  	return executeCriteria(criteria);
	}
	
	/**
	 * Pesquisar especialidades da emergencia filtrando por unidade
	 * @param seqUnidade
	 * @return
	 */
	public List<Short> pesquisarEspecialidadesEmergenciaPorUnidade(Long seqUnidade){
	  	final DetachedCriteria criteria = DetachedCriteria.forClass(MamEmgEspecialidades.class ,"MES"); 	
	  	
		DetachedCriteria subQuery = DetachedCriteria.forClass(MamUnidAtendeEsp.class, "MUAE");
	  	subQuery.setProjection(Projections.projectionList()
				                .add(Projections.property("MUAE."+ MamUnidAtendeEsp.Fields.ESP_SEQ.toString())));

	  	subQuery.add(Restrictions.eqProperty("MUAE."+ MamUnidAtendeEsp.Fields.ESP_SEQ.toString(), "MES."+ MamEmgEspecialidades.Fields.ESP_SEQ.toString()));
	  	subQuery.add(Restrictions.eq("MUAE."+ MamUnidAtendeEsp.Fields.MAM_UNID_ATENDEM_UNF_SEQ.toString(), seqUnidade));
	  	
	  	criteria.add(Subqueries.exists(subQuery));
		criteria.setProjection(Projections.distinct(Projections.projectionList()
                .add(Projections.property("MES."+ MamEmgEspecialidades.Fields.ESP_SEQ.toString()))));
		
		return executeCriteria(criteria);
		}
}