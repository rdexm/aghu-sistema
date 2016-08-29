package br.gov.mec.aghu.certificacaodigital.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAghVersaoDocumento;

public class VAghVersaoDocumentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAghVersaoDocumento> {

	private static final long serialVersionUID = -6060960123140912631L;

	public List<Object[]> countVersaoPacientes(List<Integer> sListaCodigosPacientes){
		DetachedCriteria criteria = DetachedCriteria
				.forClass(VAghVersaoDocumento.class);
		
		 ProjectionList projectionList = Projections.projectionList();
		    projectionList.add(Projections.groupProperty(VAghVersaoDocumento.Fields.CODIGO_PACIENTE.toString()));
		    projectionList.add(Projections.rowCount());
		    criteria.setProjection(projectionList);
		
		criteria.add(Restrictions.eq(VAghVersaoDocumento.Fields.SITUACAO.toString(), DominioSituacaoVersaoDocumento.P));
		criteria.add(Restrictions.in(VAghVersaoDocumento.Fields.CODIGO_PACIENTE.toString(),sListaCodigosPacientes));

		return executeCriteria(criteria);
	}
	
	/**
	 * Realizar a pesquisa através do paginator na view VAghVersaoDocumento
	 * Necessária a utilização de view para possibilitar a ordenação pelas
	 * colunas do paginator
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param paciente
	 * @param dataInicial
	 * @param dataFinal
	 * @param responsavel
	 * @param servico
	 * @param situacao
	 * @param tipoDocumento
	 * @return
	 */
	public List<VAghVersaoDocumento> pesquisarDocumentosPaciente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AipPacientes paciente, Date dataInicial,
			Date dataFinal, RapServidores responsavel, FccCentroCustos servico,
			DominioSituacaoVersaoDocumento situacao,
			DominioTipoDocumento tipoDocumento) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(VAghVersaoDocumento.class);

		if (paciente != null) {
			criteria.add(Restrictions.eq(
					VAghVersaoDocumento.Fields.CODIGO_PACIENTE.toString(),
					paciente.getCodigo()));
		}

		if (responsavel != null) {
			criteria.add(Restrictions.eq(
					VAghVersaoDocumento.Fields.MATRICULA_RESP.toString(),
					responsavel.getId().getMatricula()));
			criteria.add(Restrictions.eq(
					VAghVersaoDocumento.Fields.VINCULO_RESP.toString(),
					responsavel.getId().getVinCodigo()));
		}

		if (servico != null) {
			criteria.add(Restrictions.or(Restrictions.eq(
					VAghVersaoDocumento.Fields.CODIGO_CC_ATUACAO.toString(),
					servico.getCodigo()), Restrictions.and(Restrictions
					.isNull(VAghVersaoDocumento.Fields.CODIGO_CC_ATUACAO
							.toString()), Restrictions.eq(
					VAghVersaoDocumento.Fields.CODIGO_CC_LOTACAO.toString(),
					servico.getCodigo()))));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(
					VAghVersaoDocumento.Fields.SITUACAO.toString(), situacao));
		}

		if (tipoDocumento != null) {
			criteria.add(Restrictions.eq(
					VAghVersaoDocumento.Fields.TIPO.toString(), tipoDocumento));

		}

		// Datas inicial e final são obrigatórias
		criteria.add(Restrictions.between(
				VAghVersaoDocumento.Fields.CRIADO_EM.toString(), dataInicial,
				dataFinal));

		if (firstResult == 0 && maxResult == 0) {
			return this.executeCriteria(criteria);
		} else {
			if (orderProperty == null) {
				criteria.addOrder(Order
						.asc(VAghVersaoDocumento.Fields.NOME_PACIENTE
								.toString()));
				criteria.addOrder(Order
						.desc(VAghVersaoDocumento.Fields.CRIADO_EM.toString()));
			}
			return this.executeCriteria(criteria, firstResult, maxResult,
					orderProperty, asc);
		}

	}

	public boolean existeVersaoDocumentoParaProntuario(Integer prontuario){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAghVersaoDocumento.class);
		criteria.add(Restrictions.eq(VAghVersaoDocumento.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
		criteria.add(Restrictions.eq(VAghVersaoDocumento.Fields.SITUACAO.toString(), DominioSituacaoVersaoDocumento.P));
		return executeCriteriaExists(criteria);
	}
	
}

