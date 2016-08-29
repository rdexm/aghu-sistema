package br.gov.mec.aghu.administracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.model.AghCaractMicrocomputador;

public class AghCaractMicrocomputadorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCaractMicrocomputador> {
	
	private static final long serialVersionUID = -799906826945452172L;

	public Boolean existeCaractMicrocomputadorPorMicroNome(String nomeMicrocomputador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractMicrocomputador.class);
		
		criteria.add(Restrictions.eq(AghCaractMicrocomputador.Fields.MIC_NOME.toString(),nomeMicrocomputador).ignoreCase());
	
		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<AghCaractMicrocomputador> pesquisaCaractMicrocomputadorPorNome(String nome){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractMicrocomputador.class);
		
		criteria.add(Restrictions.eq(AghCaractMicrocomputador.Fields.MIC_NOME.toString(),nome).ignoreCase());

		return executeCriteria(criteria);
	}	
	
	public List<AghCaractMicrocomputador> pesquisaCaractMicrocomputadorPorNome(String nome, DominioCaracteristicaMicrocomputador caracteristica){
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaractMicrocomputador.class);
		
		criteria.add(Restrictions.eq(AghCaractMicrocomputador.Fields.MIC_NOME.toString(), StringUtils.upperCase(nome)));
		if(caracteristica != null){
			criteria.add(Restrictions.eq(AghCaractMicrocomputador.Fields.CARACTERISTICA.toString(), caracteristica));
		}

		return executeCriteria(criteria);
	}
}
