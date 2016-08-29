package br.gov.mec.aghu.parametrosistema.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Responsavel por motar a busca de sistemas por nome.<br>
 * Usando o valor de busca para filtar nos campos:<br>
 * <ol>
 * 	<li>SIGLA: MatchMode.EXACT</li>
 * 	<li>ou NOME: MatchMode.ANYWHERE</li>
 * </ol><br>
 * 
 * Classe concretas de build devem sempre ter modificador de acesso Default.<br>
 * 
 * <p>Exemplo de uso do QueryBuilder para org.hibernate.criterion.DetachedCriteria.
 * Com passagem dos filtros no proprio metodo build.</p>
 * 
 */
class SistemaPorNomeQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	private String nomeSistema;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(AghSistemas.class);
	}

	@Override
	protected void doBuild(DetachedCriteria aProduct) {
		
		if (StringUtils.isNotBlank(this.nomeSistema)) {
			aProduct.add(Restrictions.or(Restrictions.ilike(AghSistemas.Fields.SIGLA.toString(), this.nomeSistema, MatchMode.EXACT)
					, Restrictions.ilike(AghSistemas.Fields.NOME.toString(), this.nomeSistema,MatchMode.ANYWHERE)));
		}
		
	}
	
	public DetachedCriteria build(String umNomeSistema) {
		
		this.nomeSistema = umNomeSistema;
		
		return super.build();
	}

}
