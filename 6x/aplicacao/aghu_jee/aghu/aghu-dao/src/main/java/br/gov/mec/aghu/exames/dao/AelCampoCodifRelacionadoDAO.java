package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelCampoCodifRelacionado;
import br.gov.mec.aghu.model.AelCampoCodifRelacionadoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;

public class AelCampoCodifRelacionadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCampoCodifRelacionado> {

	
	
	private static final long serialVersionUID = -4095579935979873856L;

	@Override
	protected void obterValorSequencialId(AelCampoCodifRelacionado elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		AelCampoCodifRelacionadoId id = new AelCampoCodifRelacionadoId();
		id.setPclVelEmaExaSigla(elemento.getAelParametroCamposLaudoByAelCcrPclFk1().getId().getVelEmaExaSigla());
		id.setPclVelEmaManSeq(elemento.getAelParametroCamposLaudoByAelCcrPclFk1().getId().getVelEmaManSeq());
		id.setPclVelSeqp(elemento.getAelParametroCamposLaudoByAelCcrPclFk1().getId().getVelSeqp());
		id.setPclCalSeq(elemento.getAelParametroCamposLaudoByAelCcrPclFk1().getId().getCalSeq());
		id.setPclSeqp(elemento.getAelParametroCamposLaudoByAelCcrPclFk1().getId().getSeqp());
		id.setPclVelEmaExaSiglaVinculad(elemento.getAelParametroCamposLaudoByAelCcrPclFk2().getId().getVelEmaExaSigla());
		id.setPclVelEmaManSeqVinculado(elemento.getAelParametroCamposLaudoByAelCcrPclFk2().getId().getVelEmaManSeq());
		id.setPclVelSeqpVinculado(elemento.getAelParametroCamposLaudoByAelCcrPclFk2().getId().getVelSeqp());
		id.setPclCalSeqVinculado(elemento.getAelParametroCamposLaudoByAelCcrPclFk2().getId().getCalSeq());
		id.setPclSeqpVinculado(elemento.getAelParametroCamposLaudoByAelCcrPclFk2().getId().getSeqp());
		
		elemento.setId(id);
	}
	
	
	/**
	 * Lista registros da tabela <br>
	 * AEL_CAMPOS_CODIF_RELACIONADOS <br>
	 * por Exame Material.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @return
	 */
	public List<AelCampoCodifRelacionado> listarCamposCodifRelacionadosPorExameMaterial(String emaExaSigla, Integer emaManSeq, Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoCodifRelacionado.class);

		criteria.createAlias(AelCampoCodifRelacionado.Fields.AEL_PARAMETRO_CAMPOS_LAUDO_BY_AEL_CCR_PCL_FK1.toString(), "PCL");
		
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()), emaExaSigla));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()), emaManSeq));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_SEQP.toString()), seqp));
				
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de registros da tabela AEL_CAMPOS_CODIF_RELACIONADOS
	 * filtrado por paramentroCampoLaudo e campo laudo
	 * @param aelParametroCamposLaudo
	 * @return
	 */
	public  List<AelCampoCodifRelacionado> pesquisarCampoCodificadoPorParametroCampoLaudoECampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoCodifRelacionado.class);

		criteria.createAlias(AelCampoCodifRelacionado.Fields.AEL_PARAMETRO_CAMPOS_LAUDO_BY_AEL_CCR_PCL_FK1.toString(), "PCL");
		
		criteria.add(Restrictions.eq("PCL."+AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), aelParametroCamposLaudo.getCampoLaudo()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de registros da tabela AEL_CAMPOS_CODIF_RELACIONADOS
	 * filtrado só por parametro campo laudo
	 * @param aelParametroCamposLaudo
	 * @return
	 */
	public  List<AelCampoCodifRelacionado> pesquisarCampoCodificadoPorParametroCampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoCodifRelacionado.class);
		criteria.add(Restrictions.eq(AelCampoCodifRelacionado.Fields.AEL_PARAMETRO_CAMPOS_LAUDO_BY_AEL_CCR_PCL_FK1.toString(), aelParametroCamposLaudo));
		return executeCriteria(criteria);
	}	
}