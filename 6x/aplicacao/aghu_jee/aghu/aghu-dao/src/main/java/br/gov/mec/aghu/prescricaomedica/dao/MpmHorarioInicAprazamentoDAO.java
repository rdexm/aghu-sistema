package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmHorarioInicAprazamento;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;

public class MpmHorarioInicAprazamentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmHorarioInicAprazamento> {

	private static final long serialVersionUID = 7424544025665508176L;

	public Date obterHoraInicialPorTipoFrequenciaAprazamento(
			AghUnidadesFuncionais unidade,
			MpmTipoFrequenciaAprazamento tipoFrequencia, Short frequencia) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmHorarioInicAprazamento.class);

		criteria.add(Restrictions.eq(
				MpmHorarioInicAprazamento.Fields.ID_UNIDADE_FUNCIONAL
						.toString(), unidade.getSeq()));

		criteria.add(Restrictions.eq(
				MpmHorarioInicAprazamento.Fields.TIPO_FREQ_APRAZAMENTO
						.toString(), tipoFrequencia));

		criteria.add(Restrictions.eq(
				MpmHorarioInicAprazamento.Fields.FREQUENCIA.toString()
						.toString(), frequencia));

		criteria.add(Restrictions.eq(
				MpmHorarioInicAprazamento.Fields.IND_SITUACAO.toString()
						.toString(), DominioSituacao.A));

		criteria.setProjection(Projections
				.property(MpmHorarioInicAprazamento.Fields.HORARIO_INICIO
						.toString()));

		return (Date) this.executeCriteriaUniqueResult(criteria);

	}

	/**
	 * Efetua a busca de horários de início de aprazamento
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param unfSeq
	 * @param situacao
	 * @return Lista de horários de início de aprazamento
	 */
	public List<MpmHorarioInicAprazamento> pesquisarHorariosInicioAprazamentos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short unfSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteriaObterListaHorariosInicioAprazamentos(unfSeq, situacao);
		
		return executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);		
	}

	/**
	 * Efetua o count para a busca de horários de início de aprazamento
	 * @param unfSeq
	 * @param situacao
	 * @return Count
	 */
	public Long pesquisarHorariosInicioAprazamentosCount(Short unfSeq, DominioSituacao situacao) {
		DetachedCriteria criteria = montarCriteriaObterListaHorariosInicioAprazamentos(unfSeq, situacao);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Monta critéria para obtenção de horários de início de aprazamento.
	 * @param unfSeq
	 * @param situacao
	 * @return
	 */
	private DetachedCriteria montarCriteriaObterListaHorariosInicioAprazamentos(Short unfSeq, DominioSituacao situacao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmHorarioInicAprazamento.class, "HAP");
		criteria.createAlias("HAP."	+ MpmHorarioInicAprazamento.Fields.TIPO_FREQ_APRAZAMENTO.toString(), "TFA", JoinType.INNER_JOIN);
		criteria.createAlias("HAP."	+ MpmHorarioInicAprazamento.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias("HAP."	+ MpmHorarioInicAprazamento.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER."	+ RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);

		if (unfSeq != null) {
			criteria.add(Restrictions.eq("HAP."+ MpmHorarioInicAprazamento.Fields.ID_UNIDADE_FUNCIONAL.toString(), unfSeq));
		}		
		if(situacao!=null){
			criteria.add(Restrictions.eq("HAP."+ MpmHorarioInicAprazamento.Fields.IND_SITUACAO, situacao));
		}
		return criteria;
	}
}