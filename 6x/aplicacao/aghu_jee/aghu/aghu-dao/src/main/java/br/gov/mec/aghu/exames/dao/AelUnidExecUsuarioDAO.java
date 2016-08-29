package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.RapServidoresId;


public class AelUnidExecUsuarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelUnidExecUsuario> {

	private static final long serialVersionUID = 1646741436948629587L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelUnidExecUsuario.class);
		return criteria;
    }
	
	private DetachedCriteria criarCriteria(AelUnidExecUsuario elemento) {
    	DetachedCriteria criteria = obterCriteria();
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		//Código
    		if(elemento.getId() != null) {
    			criteria.add(Restrictions.eq(AelUnidExecUsuario.Fields.ID.toString(), elemento.getId()));
    		}
    		//Descrição
			if(elemento.getUnfSeq() != null) {
				criteria.add(Restrictions.eq(AelUnidExecUsuario.Fields.UNF_SEQ.toString(), elemento.getUnfSeq()));
			}
			
			criteria.setFetchMode(AelUnidExecUsuario.Fields.UNF_SEQ.toString(), FetchMode.JOIN);
    	}
    	return criteria;
    }

	
	public List<AelUnidExecUsuario> obterAelUnidExecUsuario(RapServidoresId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelUnidExecUsuario.class);
		criteria.add(Restrictions.eq(AelUnidExecUsuario.Fields.ID.toString(), id));
		return executeCriteria(criteria);
	}
	
	public AelUnidExecUsuario obterPeloId(RapServidoresId id) {
		AelUnidExecUsuario elemento = new AelUnidExecUsuario();
		elemento.setId(id);
		DetachedCriteria criteria = criarCriteria(elemento);
		
		return (AelUnidExecUsuario) executeCriteriaUniqueResult(criteria);
	}
	
}
