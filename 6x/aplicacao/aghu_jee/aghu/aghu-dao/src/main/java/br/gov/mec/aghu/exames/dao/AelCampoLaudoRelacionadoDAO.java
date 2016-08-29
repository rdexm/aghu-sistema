package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelCampoLaudoRelacionado;
import br.gov.mec.aghu.model.AelCampoLaudoRelacionadoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;

public class AelCampoLaudoRelacionadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelCampoLaudoRelacionado> {
	
	
	private static final long serialVersionUID = -278512636144265989L;


	@Override
	protected void obterValorSequencialId(AelCampoLaudoRelacionado elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		AelCampoLaudoRelacionadoId id = new AelCampoLaudoRelacionadoId();
		id.setPclVelEmaExaSigla(elemento.getAelParametroCamposLaudoByAelClvPclFk1().getId().getVelEmaExaSigla());
		id.setPclVelEmaManSeq(elemento.getAelParametroCamposLaudoByAelClvPclFk1().getId().getVelEmaManSeq());
		id.setPclVelSeqp(elemento.getAelParametroCamposLaudoByAelClvPclFk1().getId().getVelSeqp());
		id.setPclCalSeq(elemento.getAelParametroCamposLaudoByAelClvPclFk1().getId().getCalSeq());
		id.setPclSeqp(elemento.getAelParametroCamposLaudoByAelClvPclFk1().getId().getSeqp());
		id.setPclVelEmaExaSiglaPertence(elemento.getAelParametroCamposLaudoByAelClvPclFk2().getId().getVelEmaExaSigla());
		id.setPclVelEmaManSeqPertence(elemento.getAelParametroCamposLaudoByAelClvPclFk2().getId().getVelEmaManSeq());
		id.setPclVelSeqpPertence(elemento.getAelParametroCamposLaudoByAelClvPclFk2().getId().getVelSeqp());
		id.setPclCalSeqPertence(elemento.getAelParametroCamposLaudoByAelClvPclFk2().getId().getCalSeq());
		id.setPclSeqpPertence(elemento.getAelParametroCamposLaudoByAelClvPclFk2().getId().getSeqp());
		
		elemento.setId(id);
	}
	
	
	public List<AelCampoLaudoRelacionado> pesquisarAelCampoLaudoRelacionadoDefineOrdemDroga(String exaSigla, Integer manSeq, Integer versaoAtivaAntibiograma, Integer campoLaudoGerme, Integer ordemGerme, String codDrogaLis){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudoRelacionado.class);

		criteria.add(Restrictions.eq(AelCampoLaudoRelacionado.Fields.ID_PCL_VEL_EMA_EXA_SIGLA_PERTENCE.toString(), exaSigla));
		criteria.add(Restrictions.eq(AelCampoLaudoRelacionado.Fields.ID_PCL_VEL_EMA_MAN_SEQ_PERTENCE.toString(), manSeq));
		criteria.add(Restrictions.eq(AelCampoLaudoRelacionado.Fields.ID_PCL_VEL_SEQP_PERTENCE.toString(), versaoAtivaAntibiograma));
		criteria.add(Restrictions.eq(AelCampoLaudoRelacionado.Fields.ID_PCL_CAL_SEQ_PERTENCE.toString(), campoLaudoGerme));
		criteria.add(Restrictions.eq(AelCampoLaudoRelacionado.Fields.ID_PCL_SEQP_PERTENCE.toString(), ordemGerme));
		criteria.add(Restrictions.eq(AelCampoLaudoRelacionado.Fields.ID_PCL_CAL_SEQ.toString(), Integer.valueOf(codDrogaLis)));

		return executeCriteria(criteria);
	}
	

	/**
	 * Lista registros da tabela <br>
	 * AEL_CAMPOS_LAUDO_RELACIONADOS
	 * por exame material.
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @return
	 */
	public List<AelCampoLaudoRelacionado> listarCampoLaudoRelacionadoPorExameMaterial(String emaExaSigla, Integer emaManSeq, Integer seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudoRelacionado.class);
		
		criteria.createAlias(AelCampoLaudoRelacionado.Fields.AEL_PARAMETRO_CAMPOS_LAUDO_BY_AEL_CLV_PCL_FK1.toString(), "PCL");
		
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()), emaExaSigla));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()), emaManSeq));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_SEQP.toString()), seqp));
				
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Retorna uma lista de registros da tabela AEL_CAMPOS_LAUDO_RELACIONADOS
	 * @param aelParametroCamposLaudo
	 * @return
	 */
	public  List<AelCampoLaudoRelacionado> pesquisarCampoLaudoPorParametroCampoLaudo(AelParametroCamposLaudo aelParametroCamposLaudo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelCampoLaudoRelacionado.class);
		criteria.createAlias(AelCampoLaudoRelacionado.Fields.AEL_PARAMETRO_CAMPOS_LAUDO_BY_AEL_CLV_PCL_FK1.toString(), "PCL");
		return executeCriteria(criteria);
	}
	
}