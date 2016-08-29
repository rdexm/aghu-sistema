package br.gov.mec.aghu.blococirurgico.dao;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.VAinServInterna;
import br.gov.mec.aghu.core.utils.DateUtil;

public class VAinServInternaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAinServInterna>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2736849143257023016L;

	public List<String> obterVAinServInternaPorId(Integer matricula, Short vinCodigo) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(VAinServInterna.class);
	    criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(VAinServInterna.Fields.NOME.toString()))));
	    criteria.add(Restrictions.eq(VAinServInterna.Fields.MATRICULA.toString(), matricula));
	    criteria.add(Restrictions.eq(VAinServInterna.Fields.VIN_CODIGO.toString(), vinCodigo));
	    
		return executeCriteria(criteria);
	}
	
	public List<Object> pesquisarVAinServInternaMatriculaVinculoEsp(Integer matricula, Short vinCodigo, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAinServInterna.class, "VSC");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("VSC."+VAinServInterna.Fields.NOME.toString())),VAinServInterna.Fields.NOME.toString())
				.add(Projections.property("VSC."+VAinServInterna.Fields.NRO_REG_CONSELHO.toString()),VAinServInterna.Fields.NRO_REG_CONSELHO.toString())
				.add(Projections.property("VSC."+VAinServInterna.Fields.MATRICULA.toString()),VAinServInterna.Fields.MATRICULA.toString())
				.add(Projections.property("VSC."+VAinServInterna.Fields.VIN_CODIGO.toString()),VAinServInterna.Fields.VIN_CODIGO.toString())
		  );	
		
		criteria.add(Restrictions.eq("VSC."+VAinServInterna.Fields.MATRICULA.toString(), matricula));
	    criteria.add(Restrictions.eq("VSC."+VAinServInterna.Fields.VIN_CODIGO.toString(), vinCodigo));
	    criteria.add(Restrictions.eq("VSC."+VAinServInterna.Fields.ESP_SEQ.toString(), espSeq));
	    return executeCriteria(criteria); 
	}
	
	public List<Object> pesquisarVAinServInternaLaudoAih(Object pesquisa, Short espSeq) {
		DetachedCriteria criteria = montarCriteriaPesquisarVAinServInternaLaudoAih(pesquisa, espSeq);
		criteria.addOrder(Order.asc("VSC." + VAinServInterna.Fields.NOME.toString()));
		return this.executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarVAinServInternaLaudoAihCount(Object pesquisa, Short espSeq) {
		DetachedCriteria criteria = montarCriteriaPesquisarVAinServInternaLaudoAih(pesquisa, espSeq);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaPesquisarVAinServInternaLaudoAih(Object pesquisa, Short espSeq) {
		
		final String stParametro = (String) pesquisa;
		DetachedCriteria criteria = DetachedCriteria.forClass(VAinServInterna.class, "VSC");
		
		if (StringUtils.isNotBlank(stParametro)){
			criteria.add(Restrictions.or(Restrictions.ilike("VSC." + VAinServInterna.Fields.NRO_REG_CONSELHO.toString(), stParametro, MatchMode.ANYWHERE), 
					Restrictions.ilike("VSC." + VAinServInterna.Fields.NOME.toString(), stParametro, MatchMode.ANYWHERE))); 
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("VSC."+VAinServInterna.Fields.NOME.toString())),VAinServInterna.Fields.NOME.toString())
				.add(Projections.property("VSC."+VAinServInterna.Fields.NRO_REG_CONSELHO.toString()),VAinServInterna.Fields.NRO_REG_CONSELHO.toString())
				.add(Projections.property("VSC."+VAinServInterna.Fields.MATRICULA.toString()),VAinServInterna.Fields.MATRICULA.toString())
				.add(Projections.property("VSC."+VAinServInterna.Fields.VIN_CODIGO.toString()),VAinServInterna.Fields.VIN_CODIGO.toString())
		  );		
		
		//SUBQUERY
		final DetachedCriteria subQuery = DetachedCriteria.forClass(AghProfEspecialidades.class, "PRE");
		subQuery.setProjection(Projections.property("PRE." + AghProfEspecialidades.Fields.SER_MATRICULA.toString()));
		subQuery.setProjection(Projections.property("PRE." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString()));
		subQuery.add(Property.forName("PRE." + AghProfEspecialidades.Fields.SER_MATRICULA.toString()).eqProperty("VSC." + VAinServInterna.Fields.MATRICULA.toString()));
		subQuery.add(Property.forName("PRE." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString()).eqProperty("VSC." + VAinServInterna.Fields.VIN_CODIGO.toString()));
		subQuery.add(Property.forName("PRE." + AghProfEspecialidades.Fields.ESP_SEQ.toString()).eqProperty("VSC." + VAinServInterna.Fields.ESP_SEQ.toString()));
		
		subQuery.createAlias("PRE." + AghProfEspecialidades.Fields.PROFISSIONAIS_ESP_CONVENIO.toString(), "PEC");
		subQuery.createAlias("PRE." + AghProfEspecialidades.Fields.ESCALAS_PROFISSIONAIS_INT.toString(), "EPI");
		subQuery.createAlias("PEC." + AghProfissionaisEspConvenio.Fields.FAT_CONVENIO_SAUDE.toString(), "CNV");
		
		subQuery.add(Restrictions.eq("PRE." + AghProfEspecialidades.Fields.IND_INTERNA.toString(), DominioSimNao.S));
		subQuery.add(Restrictions.eq("PEC." + AghProfissionaisEspConvenio.Fields.IND_INTERNA.toString(), Boolean.TRUE));
		subQuery.add(Restrictions.eq("CNV." + FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
			
		subQuery.add(Restrictions.and(Restrictions.le("EPI." + AinEscalasProfissionalInt.Fields.DATA_INICIO.toString(), new Date()), 
				Restrictions.or(Restrictions.isNull("EPI." + AinEscalasProfissionalInt.Fields.DATA_FIM.toString()), 
						Restrictions.ge("EPI." + AinEscalasProfissionalInt.Fields.DATA_FIM.toString(), DateUtil.truncaData(new Date())))));
		
		subQuery.add(Property.forName("CNV." + FatConvenioSaude.Fields.CODIGO.toString()).eqProperty("PEC." + AghProfissionaisEspConvenio.Fields.CONVENIO.toString()));

		criteria.add(Subqueries.exists(subQuery));

		criteria.add(Restrictions.eq("VSC." + VAinServInterna.Fields.ESP_SEQ.toString(), espSeq));
				
		return criteria;
	}

}
