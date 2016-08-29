package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelServidorUnidAssinaElet;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;

public class AelServidorUnidAssinaEletDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelServidorUnidAssinaElet> {

	private static final long serialVersionUID = -2411611289395489893L;

	/**
	 * Obtém a DetachedCriteria para pesquisa de Servidor Unidade Assinatura Eletrônica através da Unidade Funcional
	 * @param unfSeq
	 * @return
	 */
	private DetachedCriteria getCriteriaPesquisarServidorUnidAssinaEletPorUnidadeFuncional(final Short unfSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelServidorUnidAssinaElet.class);
		
		criteria.createAlias(AelServidorUnidAssinaElet.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "unf");
		
		criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));
		criteria.add(Restrictions.eq(AelServidorUnidAssinaElet.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return criteria;
	}

	/**
	 * Pesquisa de Servidores de Unidade Assinatura Eletrônica através da Unidade Funcional
	 * @param unfSeq
	 * @return
	 */
	public List<AelServidorUnidAssinaElet> pesquisarServidorUnidAssinaEletPorUnidadeFuncional(Short unfSeq){
		return this.executeCriteria(this.getCriteriaPesquisarServidorUnidAssinaEletPorUnidadeFuncional(unfSeq));
	}


	/**
	 * Obtém a quantidades de registros da pesquisa de Servidores de Unidade Assinatura Eletrônica através da Unidade Funcional
	 * @param unfSeq
	 * @return
	 */
	public Long pesquisarServidorUnidAssinaEletPorUnidadeFuncionalCount(Short unfSeq){
		return this.executeCriteriaCount(this.getCriteriaPesquisarServidorUnidAssinaEletPorUnidadeFuncional(unfSeq));
	}

	public List<AelServidorUnidAssinaElet> pesquisarServidorUnidAssinaEletPorUnfSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelServidorUnidAssinaElet.class);

		criteria.createAlias(AelServidorUnidAssinaElet.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "unf");
		criteria.createAlias(AelServidorUnidAssinaElet.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PES", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("unf." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), seq));		
		return executeCriteria(criteria);
	}

}
