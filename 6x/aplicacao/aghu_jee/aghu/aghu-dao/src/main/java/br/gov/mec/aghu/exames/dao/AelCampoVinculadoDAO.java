package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelCampoVinculado;
import br.gov.mec.aghu.model.AelCampoVinculadoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;

public class AelCampoVinculadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCampoVinculado> {
		
	private static final long serialVersionUID = -3040156885500908459L;

	@Override
	protected void obterValorSequencialId(AelCampoVinculado elemento) {
		
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!");
		}
		if (elemento.getAelParametroCamposLaudoByAelCvcPclFk1() == null) {
			throw new IllegalArgumentException("Campo Laudo não possui Parâmetro Campo Laudo.");
		}
		if (elemento.getAelParametroCamposLaudoByAelCvcPclFk2() == null) {
			throw new IllegalArgumentException("Campo Laudo Vinculado não possui Parâmetro Campo Laudo.");
		}
		
		AelCampoVinculadoId id = new AelCampoVinculadoId();
		
		id.setPclVelEmaExaSigla(elemento.getAelParametroCamposLaudoByAelCvcPclFk1().getId().getVelEmaExaSigla());
		id.setPclVelEmaManSeq(elemento.getAelParametroCamposLaudoByAelCvcPclFk1().getId().getVelEmaManSeq());
		id.setPclVelSeqp(elemento.getAelParametroCamposLaudoByAelCvcPclFk1().getId().getVelSeqp());
		id.setPclCalSeq(elemento.getAelParametroCamposLaudoByAelCvcPclFk1().getId().getCalSeq());
		id.setPclSeqp(elemento.getAelParametroCamposLaudoByAelCvcPclFk1().getId().getSeqp());
		id.setPclVelEmaExaSiglaVinculad(elemento.getAelParametroCamposLaudoByAelCvcPclFk2().getId().getVelEmaExaSigla());
		id.setPclVelEmaManSeqVinculado(elemento.getAelParametroCamposLaudoByAelCvcPclFk2().getId().getVelEmaManSeq());
		id.setPclVelSeqpVinculado(elemento.getAelParametroCamposLaudoByAelCvcPclFk2().getId().getVelSeqp());
		id.setPclCalSeqVinculado(elemento.getAelParametroCamposLaudoByAelCvcPclFk2().getId().getCalSeq());
		id.setPclSeqpVinculado(elemento.getAelParametroCamposLaudoByAelCvcPclFk2().getId().getSeqp());
		
		elemento.setId(id);
	}
	
	
	/**
	 * Retorna uma lista de registros da tabela AEL_CAMPOS_VINCULADOS
	 * @param aelParametroCamposLaudoByAelCvcPclFk1
	 * @return
	 */
	public  List<AelCampoVinculado> pesquisarCampoVinculadoPorParametroCampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudoByAelCvcPclFk1) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoVinculado.class);
		criteria.add(Restrictions.eq(AelCampoVinculado.Fields.AEL_PARAMETRO_CAMPOS_LAUDO_BY_AEL_CVC_PCL_FK1.toString(), aelParametroCamposLaudoByAelCvcPclFk1));

		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de registros dos campos vinculadores databela AEL_CAMPOS_VINCULADOS
	 * @param aelParametroCamposLaudoByAelCvcPclFk2
	 * @return
	 */
	public  List<AelCampoVinculado> pesquisarCampoVinculadoresPorParametroCampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudoByAelCvcPclFk2) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoVinculado.class);
		criteria.add(Restrictions.eq(AelCampoVinculado.Fields.AEL_PARAMETRO_CAMPOS_LAUDO_BY_AEL_CVC_PCL_FK2.toString(), aelParametroCamposLaudoByAelCvcPclFk2));

		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de registros<br>
	 * da tabela AEL_CAMPOS_VINCULADOS<br>
	 * por parametro campo laudo.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @return
	 */
	public  List<AelCampoVinculado> pesquisarCampoVinculadoPorParametroCampoLaudo(String emaExaSigla, Integer emaManSeq, Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoVinculado.class, "PCL");

		criteria.createAlias(AelCampoVinculado.Fields.AEL_PARAMETRO_CAMPOS_LAUDO_BY_AEL_CVC_PCL_FK1.toString(), "CAL");
		
		criteria.add(Restrictions.eq("CAL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()), emaExaSigla));
		criteria.add(Restrictions.eq("CAL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()), emaManSeq));
		criteria.add(Restrictions.eq("CAL.".concat(AelParametroCamposLaudo.Fields.VEL_SEQP.toString()), seqp));

		return executeCriteria(criteria);
	}
	
}