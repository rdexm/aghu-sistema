package br.gov.mec.aghu.bancosangue.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsAmostrasPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AbsAmostrasPacientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsAmostrasPacientes> {
	
	private static final long serialVersionUID = 7762780926293300223L;

	public List<AbsAmostrasPacientes> pesquisarAmostrasPaciente(
			Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsAmostrasPacientes.class);
		criteria.add(Restrictions.eq(
				AbsAmostrasPacientes.Fields.PACIENTE_CODIGO.toString(),
				pacCodigo));

		return executeCriteria(criteria);
	}

	public List<AbsAmostrasPacientes> amostrasPaciente(AipPacientes paciente, Date dthrInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsAmostrasPacientes.class);
		criteria.add(Restrictions.eq(AbsAmostrasPacientes.Fields.PACIENTE_CODIGO.toString(),
				paciente == null ? null : paciente.getCodigo()));
		criteria.add(Restrictions.ge(AbsAmostrasPacientes.Fields.DATA_HORA_AMOSTRA.toString(), dthrInicio));
		return executeCriteria(criteria);
	}

	/**
	 * Verificar se existe uma amostra válida no banco de sangue, ou seja,
	 *  ativa e com data de até <validade> horas anteriores à data prevista para cirurgia.
	 * @param atendimento
	 * @param cirurgia
	 * @return true se existe amostra
	 */
	public Boolean existeAmostraPendente(Integer seqAtendimento, Integer seqCirurgia, Integer validade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsAmostrasPacientes.class, "AMP");	

		criteria.createAlias("AMP." + AbsAmostrasPacientes.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("PAC." + AipPacientes.Fields.MBC_CIRURGIAS.toString(), "CRG", JoinType.INNER_JOIN);	
		
		criteria.add(Restrictions.eqProperty("AMP."
				+ AbsAmostrasPacientes.Fields.PACIENTE_CODIGO.toString(),
				"CRG." + MbcCirurgias.Fields.PAC_CODIGO.toString()));	

		criteria.add(Restrictions.eq("AMP." + AbsAmostrasPacientes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.ATENDIMENTO_SEQ.toString(), seqAtendimento));
		criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString(), seqCirurgia));	

		if(isOracle()) {
			criteria.add(Restrictions.sqlRestriction("DTHR_PREV_INICIO"
					+ " BETWEEN {alias}.dthr_amostra - " + validade/24 
					+ " AND {alias}.dthr_amostra + " + validade/24));
		} else {
			criteria.add(Restrictions.sqlRestriction("DTHR_PREV_INICIO" 
					+ " BETWEEN {alias}.dthr_amostra - INTERVAL '" + validade/24 + " days'" 
					+ " AND {alias}.dthr_amostra + INTERVAL '" + validade/24 + " days'"));
		}

		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<AbsAmostrasPacientes> pesquisarAmostrasPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsAmostrasPacientes.class);
		criteria.add(Restrictions.eq(AbsAmostrasPacientes.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.addOrder(Order.desc(AbsAmostrasPacientes.Fields.CRIADO_EM.toString()));
		return this.executeCriteria(criteria);
	}
	
	public List<AbsAmostrasPacientes> pesquisarAmostrasPorCodigoPacienteAtivos(Integer pacCodigo, Integer vDias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsAmostrasPacientes.class);
		criteria.add(Restrictions.eq(AbsAmostrasPacientes.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ge(AbsAmostrasPacientes.Fields.DATA_HORA_AMOSTRA.toString(), 
				DateUtil.adicionaDias(new Date(), -vDias)));
		criteria.add(Restrictions.eq(AbsAmostrasPacientes.Fields.SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria);
	}
	
	public AbsAmostrasPacientes obterAmostrasPorCodigoDthrAmostra(Integer pacCodigo, Date dthrAmosta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsAmostrasPacientes.class);
		criteria.add(Restrictions.eq(AbsAmostrasPacientes.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AbsAmostrasPacientes.Fields.DATA_HORA_AMOSTRA.toString(), dthrAmosta));
		return (AbsAmostrasPacientes) this.executeCriteriaUniqueResult(criteria);
	}
}
