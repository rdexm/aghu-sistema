package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MpmAltaMotivoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaMotivo> {

	private static final long serialVersionUID = 1166104485897421626L;

	@Override
	protected void obterValorSequencialId(MpmAltaMotivo elemento) {
		if (elemento.getAltaSumario() == null) {
			throw new IllegalArgumentException("MpmAltaSumario nao esta associado corretamente a MpmAltaMotivo.");
		}
		
		elemento.setId(elemento.getAltaSumario().getId());
	}
	
	/**
	 * Retorna uma nova referência ao registro informado por parâmetro com os
	 * valores atuais do banco, utilizando o comando evict para não buscar do
	 * cache.
	 * 
	 * @param {MpmAltaMotivo} altaMotivo
	 * @return
	 */
	public MpmAltaMotivo obterAltaMotivoOriginal(MpmAltaMotivo altaMotivo) {
		this.desatachar(altaMotivo);
		return obterPorChavePrimaria(altaMotivo.getId());
	}
	
	/**
	 * Busca alta motivo do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param isObito TRUE se é pra busca óbito, FALSE se não Óbito e NULL para ambos
	 * @return
	 */
	public MpmAltaMotivo obterMpmAltaMotivo(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp, Boolean isObito) throws ApplicationBusinessException {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaMotivo.class);
		criteria.createAlias(MpmAltaMotivo.Fields.MOTIVO_ALTA_MEDICAS.toString(), MpmAltaMotivo.Fields.MOTIVO_ALTA_MEDICAS.toString());
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.SEQP.toString(), altanAsuSeqp));
		if (isObito != null) {
			criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.MOTIVO_ALTA_MEDICAS.toString() + "." + MpmMotivoAltaMedica.Fields.IND_OBITO.toString(), isObito.booleanValue()));
		}
		return (MpmAltaMotivo) executeCriteriaUniqueResult(criteria);
	}

	public List<MpmAltaMotivo> buscaAltaMotivoPorAltaSumario(MpmAltaSumario altaSumario) {
		if (altaSumario == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaMotivo.class);
		
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.ALTA_SUMARIO_ID.toString(), altaSumario.getId()));
		
		return super.executeCriteria(criteria);
	}
	
	public String obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaMotivo.class);
		
		criteria.setProjection(Projections.sqlProjection(
				"coalesce(DESC_MOTIVO, '')||' '||coalesce(COMPL_MOTIVO,'')" +				
				" as descricao",					
				new String[]{"descricao"},
				new Type[]{StringType.INSTANCE}));
				
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.SEQP.toString(), asuSeqp));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Método responsável pela validação
	 * da alta do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 *  
	 */
	public List<Long> listMotivoAltaPaciente(MpmAltaSumarioId altaSumarioId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaMotivo.class);
		
		criteria.setProjection(Projections.rowCount()).add(Restrictions.idEq(altaSumarioId));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #39018
	 * Busca alta motivo do sumário ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaMotivo obterMpmAltaMotivoPorId(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaMotivo.class);
		criteria.createAlias(MpmAltaMotivo.Fields.MOTIVO_ALTA_MEDICAS.toString(), MpmAltaMotivo.Fields.MOTIVO_ALTA_MEDICAS.toString());
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaMotivo.Fields.SEQP.toString(), altanAsuSeqp));
		return (MpmAltaMotivo) executeCriteriaUniqueResult(criteria);
	}
}
