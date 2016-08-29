package br.gov.mec.aghu.perinatologia.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoNascIndicacoes;

public class McoNascIndicacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoNascIndicacoes> {

	private static final long serialVersionUID = -2073411945605036572L;
	
	private static final String NAI = "NAI";
	private static final String FCP = "FCP";
	private static final String CSR = "CSR";

	public List<McoNascIndicacoes> listarNascIndicacoesPorForcipes(Integer forcipesCodigoPaciente, Short forcipesSequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascIndicacoes.class);

		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.FORCIPES_CODIGO_PACIENTE.toString(), forcipesCodigoPaciente));
		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.FORCIPES_SEQUENCE.toString(), forcipesSequence));

		return executeCriteria(criteria);
	}

	public List<McoNascIndicacoes> listarNascIndicacoesPorCesariana(Integer cesarianaCodigoPaciente, Short cesarianaSequence) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascIndicacoes.class);

		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.CESARIANA_CODIGO_PACIENTE.toString(), cesarianaCodigoPaciente));
		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.CESARIANA_SEQUENCE.toString(), cesarianaSequence));


		return executeCriteria(criteria);
	}
	
	public List<McoNascIndicacoes> pesquisarNascIndicacoesPorForcipes(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascIndicacoes.class);
		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.FORCIPES_CODIGO_PACIENTE.toString(), gsoPacCodigo));
		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.FORCIPES_SEQUENCE.toString(), gsoSeqp));
		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.FORCIPES_SEQP.toString(), seqp));
		return executeCriteria(criteria);
	}
	
	/**
	 * Q1
	 * @param cesPacCod
	 * @param cesSeq
	 * @param cesGsoSeqp
	 * @return List<McoNascIndicacoes>
	 */
	public List<McoNascIndicacoes> listarNascIndicacoesPorCesariana(Integer cesPacCod, Integer cesSeq, Short cesGsoSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascIndicacoes.class);

		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.CESARIANA_CODIGO_PACIENTE.toString(), cesPacCod));
		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.CESARIANA_SEQUENCE.toString(), cesGsoSeqp));
		criteria.add(Restrictions.eq(McoNascIndicacoes.Fields.CESARIANA_SEQP.toString(), cesSeq));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Prefixar um field
	 * 
	 * @param prefixo
	 * @param field
	 * @return
	 */
	private String prefixar(String prefixo, String field) {
		return new StringBuffer(prefixo).append(".").append(field).toString();
	}
	
	@Override
	protected void obterValorSequencialId(McoNascIndicacoes mcoNascIndicacoes) {
		StringBuffer select = new StringBuffer();
		if (isOracle()) {
			select.append("select ").append("AGH.MCO_NAI_SQ1").append(".NEXTVAL from dual");
		} else if (isPostgreSQL()) {
			select.append("select ").append(" nextval('").append("AGH.MCO_NAI_SQ1").append("')");
		} else if (isHSQL()) {
			select.append("CALL NEXT VALUE FOR ").append("AGH.MCO_NAI_SQ1");
		}
		
		List<?> listNextVal = createNativeQuery(select.toString()).getResultList();
		
		Number nextVal = null;
		if (isOracle()) {
			nextVal = (BigDecimal) listNextVal.get(0);			
		} else if (isPostgreSQL()) {
			nextVal = (BigInteger) listNextVal.get(0);
		} else if (isHSQL()) {
			nextVal = (Integer) listNextVal.get(0);
		}
		mcoNascIndicacoes.setSeq(nextVal.toString());
	}
	
	/**
	 * Pesquisar indica��es para parto instrumentado
	 * 
	 * C2 de #37857
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	public List<McoNascIndicacoes> pesquisarIndicacoesPartoInstrumentado(final Integer gsoPacCodigo, final Short gsoSeqp, final Integer seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoNascIndicacoes.class, NAI);
		criteria.createAlias(this.prefixar(NAI, McoNascIndicacoes.Fields.FORCIPES.toString()), FCP);
		if (gsoPacCodigo != null) {
			criteria.add(Restrictions.eq(this.prefixar(FCP, McoForcipes.Fields.CODIGO_PACIENTE.toString()), gsoPacCodigo));
		}
		if (gsoSeqp != null) {
			criteria.add(Restrictions.eq(this.prefixar(FCP, McoForcipes.Fields.SEQUENCE.toString()), gsoSeqp));
		}
		if (seqp != null) {
			criteria.add(Restrictions.eq(this.prefixar(FCP, McoForcipes.Fields.SEQP.toString()), seqp));
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Pesquisar indica��es para parto Cesariana
	 * 
	 * C3 de #37857
	 * 
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param seqp
	 * @return
	 */
	public List<McoNascIndicacoes> pesquisarIndicacoesCesariana(final Integer gsoPacCodigo, final Short gsoSeqp, final Integer seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(McoNascIndicacoes.class, NAI);
		
		criteria.createAlias(this.prefixar(NAI, McoNascIndicacoes.Fields.CESARIANA.toString()), CSR);
		if (gsoPacCodigo != null) {
			criteria.add(Restrictions.eq(this.prefixar(CSR, McoCesarianas.Fields.CODIGO_PACIENTE.toString()), gsoPacCodigo));
		}
		if (gsoSeqp != null) {
			criteria.add(Restrictions.eq(this.prefixar(CSR, McoCesarianas.Fields.SEQUENCE.toString()), gsoSeqp));
		}
		if (seqp != null) {
			criteria.add(Restrictions.eq(this.prefixar(CSR, McoCesarianas.Fields.SEQP.toString()), seqp));
		}
		return super.executeCriteria(criteria);
	}
	
	public Boolean verificaExisteMcoNascIndicacoesForcipe(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascIndicacoes.class);
		criteria.createAlias((McoNascIndicacoes.Fields.FORCIPES.toString()), FCP);
		if (gsoPacCodigo != null) {
			criteria.add(Restrictions.eq(("FCP." + McoForcipes.Fields.CODIGO_PACIENTE.toString()), gsoPacCodigo));
		}
		if (gsoSeqp != null) {
			criteria.add(Restrictions.eq(("FCP." + McoForcipes.Fields.SEQUENCE.toString()), gsoSeqp));
		}
		if (pSeqp != null) {
			criteria.add(Restrictions.eq(("FCP." +McoForcipes.Fields.SEQP.toString()), pSeqp));
		}
		
		return this.executeCriteriaExists(criteria);
	}
	
	public Boolean verificaExisteMcoNascIndicacoesCesarea(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(McoNascIndicacoes.class);
		criteria.createAlias(McoNascIndicacoes.Fields.CESARIANA.toString(), CSR);
		if (gsoPacCodigo != null) {
			criteria.add(Restrictions.eq("CSR." + McoCesarianas.Fields.CODIGO_PACIENTE.toString(), gsoPacCodigo));
		}
		if (gsoSeqp != null) {
			criteria.add(Restrictions.eq("CSR." + McoCesarianas.Fields.SEQUENCE.toString(), gsoSeqp));
		}
		if (pSeqp != null) {
			criteria.add(Restrictions.eq("CSR." + McoCesarianas.Fields.SEQP.toString(), pSeqp));
		}
		
		return this.executeCriteriaExists(criteria);
	}
}
