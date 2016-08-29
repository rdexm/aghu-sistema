package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoPlanoIniciais;

public class McoPlanoIniciaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoPlanoIniciais> {

	private static final long serialVersionUID = 7132022206087024640L;
	
	@Inject
	private ListarMcoPlanoIniciaisCondutaQueryBuilder iniciaisCondutaQueryBuilder;

	public List<McoPlanoIniciais> listarPlanosIniciais(Integer codigoPaciente, Short sequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoPlanoIniciais.class);

		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.SEQUENCE.toString(), sequence));

		return executeCriteria(criteria);
	}

	public List<McoPlanoIniciais> listarPlanosIniciaisPorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoPlanoIniciais.class);

		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}
	
	public List<McoPlanoIniciais> listarPlanosIniciaisPorPacienteSequenceNumeroConsulta(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoPlanoIniciais.class);

		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.CON_NUMERO.toString(), conNumero));
		
		return executeCriteria(criteria);
	}
	
	public List<McoPlanoIniciais> listarMcoPlanoIniciaisConduta(Integer efiConNumero, Short efiGsoSeqp, Integer efiGsoPacCodigo) {
		final DetachedCriteria criteria = iniciaisCondutaQueryBuilder.build(efiConNumero, efiGsoSeqp, efiGsoPacCodigo);
		return executeCriteria(criteria);
	}

	public boolean verificaSeExisteCondutaSemComplemento(Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoPlanoIniciais.class);
		
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.isNull(McoPlanoIniciais.Fields.COMPLEMENTO.toString()));
		
		return executeCriteriaExists(criteria);	
	}
	
	public List<McoPlanoIniciais> listarMcoPlanoIniciaisCondutaPorConsultaCodigoSeqp(Integer efiConNumero, Short efiGsoSeqp, Integer efiGsoPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoPlanoIniciais.class);
		
		criteria.createAlias(McoPlanoIniciais.Fields.CONDUTA.toString(), "MCO_CONDUTAS");
		
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.CODIGO_PACIENTE.toString(), efiGsoPacCodigo));
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.SEQUENCE.toString(), efiGsoSeqp));
		criteria.add(Restrictions.eq(McoPlanoIniciais.Fields.CON_NUMERO.toString(), efiConNumero));
	
		return executeCriteria(criteria);
	}

}
