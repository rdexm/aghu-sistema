package br.gov.mec.aghu.controlepaciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.EcpServidorUnidFuncional;
import br.gov.mec.aghu.model.RapServidores;
/**
 * 
 * @modulo controlepaciente
 *
 */
public class EcpServidorUnidFuncionalDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<EcpServidorUnidFuncional> {

	private static final long serialVersionUID = 8742474963627185405L;

	/**
	 * Retorna criteria para pesquisa de unidades funcionais configuradas para o
	 * servidor fornecido.<br>
	 * 
	 * @param servidor
	 * @return
	 */
	protected DetachedCriteria criteriaConfiguradaPara(
			final RapServidores servidor) {
		if (servidor == null) {
			throw new IllegalArgumentException("Argumento obrigatório");
		}
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpServidorUnidFuncional.class);

		criteria.add(Restrictions.eq(EcpServidorUnidFuncional.Fields.RAP_SERVIDORES
				.toString(), servidor));

		return criteria;
	}
	
	
	public DetachedCriteria criteriaUnidadeFuncionalConfiguradaPara(
			RapServidores servidor) {
		
		DetachedCriteria criteria = this.criteriaConfiguradaPara(servidor)
				.setProjection(Projections.property(
						EcpServidorUnidFuncional.Fields.AGH_UNIDADES_FUNCIONAIS.toString()));
		return criteria;

	}
	


	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = criteriaUnidadeFuncionalConfiguradaPara(servidor);
		return executeCriteria(criteria);
	}

	public List<EcpServidorUnidFuncional> pesquisarAssociacoesPorServidor(
			RapServidores servidor) {
		DetachedCriteria criteria = criteriaConfiguradaPara(servidor);
		return executeCriteria(criteria);
	}
	
}
