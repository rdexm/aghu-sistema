package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @see AghAtendimentosPacExtern
 * br.gov.mec.aghu.core.persistence.dao.BaseDao
 */
public class AghResponsavelDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghResponsavel> {

	

	private static final long serialVersionUID = 5774660240922174354L;
	
	
	public List<AghResponsavel> listarResponsavel(String parametro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghResponsavel.class);
		
		criteria.createAlias(AghResponsavel.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		 
		if (CoreUtil.isNumeroLong(parametro)) {
			Long documento = Long.valueOf(parametro);
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.eq(AghResponsavel.Fields.CPF_CGC.toString(), documento),
					                                     Restrictions.eq(AghResponsavel.Fields.NRO_DOC_EXTERIOR.toString(), parametro )),
					                                     Restrictions.eq("PAC." + AipPacientes.Fields.CPF.toString(), documento)));
			
		} else {
			if (!StringUtils.isEmpty((String) parametro)) {
				criteria.add(Restrictions.or(Restrictions.or(Restrictions.ilike(AghResponsavel.Fields.NOME.toString(), (String) parametro, MatchMode.ANYWHERE),
						                     Restrictions.ilike(AghResponsavel.Fields.NRO_DOC_EXTERIOR.toString(), (String) parametro, MatchMode.ANYWHERE)),
						                     Restrictions.ilike("PAC." + AipPacientes.Fields.NOME.toString(), (String) parametro, MatchMode.ANYWHERE)));
			}
		}		
		
				
		return executeCriteria(criteria);
	}
	
	public Boolean verificaDocumentoDuplicado(Integer seq, Long cpfCgc, String nroDocExterior) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghResponsavel.class);
		
		if (seq !=null){
			criteria.add(Restrictions.ne(AghResponsavel.Fields.SEQ.toString(), seq));
		}
		
		if (cpfCgc != null && CoreUtil.isNumeroLong(cpfCgc)) {
			
			criteria.add(Restrictions.eq(AghResponsavel.Fields.CPF_CGC.toString(), cpfCgc));
			
		} else {
			if (!StringUtils.isEmpty(nroDocExterior)) {
				criteria.add(Restrictions.eq(AghResponsavel.Fields.NRO_DOC_EXTERIOR.toString(), nroDocExterior));
						
			}
		}
				
		return (this.executeCriteriaCount(criteria) > 0);
	}	
	
	public AghResponsavel obterResponsavelPorPaciente(AipPacientes paciente) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghResponsavel.class);
		
		criteria.add(Restrictions.eq(AghResponsavel.Fields.PACIENTE.toString(),paciente));
				
		return (AghResponsavel) this.executeCriteriaUniqueResult(criteria);
		
	}
	
    public AghResponsavel obterResponsavelPorSeq(Integer seq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghResponsavel.class, "RESP");
		
		criteria.createAlias("RESP." + AghResponsavel.Fields.PAIS_BCB.toString(), "PAIS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RESP." + AghResponsavel.Fields.AIP_BAIRROS_CEP_LOGRADOURO.toString(), "AIP_BAIRROS_CEP_LOGRADOURO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RESP." + AghResponsavel.Fields.AIP_CIDADE.toString(), "AIP_CIDADE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RESP." + AghResponsavel.Fields.AIP_LOGRADOURO.toString(), "AIP_LOGRADOURO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RESP." + AghResponsavel.Fields.AIP_UF.toString(), "AIP_UF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RESP." + AghResponsavel.Fields.AIP_ORGAO_EMISSOR.toString(), "AIP_ORGAO_EMISSOR", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AghResponsavel.Fields.SEQ.toString(), seq));
				
		return (AghResponsavel) this.executeCriteriaUniqueResult(criteria);
		
	}
		
}