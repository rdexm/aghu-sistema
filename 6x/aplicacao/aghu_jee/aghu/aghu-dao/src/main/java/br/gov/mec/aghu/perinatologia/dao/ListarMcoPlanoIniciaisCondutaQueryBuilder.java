package br.gov.mec.aghu.perinatologia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class ListarMcoPlanoIniciaisCondutaQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = -6407815851713653512L;
	private static final String PONTO = ".";
	private static final String MCO_CONDUTAS = "CON";
	private static final String MCO_PLANO_INICIAIS = "PLI";
	
	private DetachedCriteria criteria;
	private Integer efiConNumero;
	private Short efiGsoSeqp;
	private Integer efiGsoPacCodigo;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(McoPlanoIniciais.class , MCO_PLANO_INICIAIS);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFilter();
	}
	
	private void setJoin() {
		criteria.createAlias(MCO_PLANO_INICIAIS + PONTO + McoPlanoIniciais.Fields.CONDUTA.toString(), MCO_CONDUTAS);
	}
	
	private void setFilter() {
		criteria.add(Restrictions.eq(MCO_PLANO_INICIAIS + PONTO + McoPlanoIniciais.Fields.CON_NUMERO.toString(), efiConNumero));
		criteria.add(Restrictions.eq(MCO_PLANO_INICIAIS + PONTO + McoPlanoIniciais.Fields.SEQUENCE.toString(), efiGsoSeqp));
		criteria.add(Restrictions.eq(MCO_PLANO_INICIAIS + PONTO + McoPlanoIniciais.Fields.CODIGO_PACIENTE.toString(), efiGsoPacCodigo));
	}

	/**
	 * 
	 * @param efiConNumero 		CON_NUMERO
	 * @param efiGsoSeqp  		SEQUENCE
	 * @param efiGsoPacCodigo	CODIGO_PACIENTE
	 * @return
	 */
	public DetachedCriteria build(Integer efiConNumero, Short efiGsoSeqp, Integer efiGsoPacCodigo) {
		this.efiConNumero = efiConNumero;
		this.efiGsoSeqp = efiGsoSeqp;
		this.efiGsoPacCodigo = efiGsoPacCodigo;
		return super.build();
	}
}
