package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExameEquipCampoLaud;
import br.gov.mec.aghu.model.AelExameEquipCampoLaudId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;

public class AelExameEquipCampoLaudDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameEquipCampoLaud> {

	
	
	private static final long serialVersionUID = -6855878138669627003L;


	@Override
	protected void obterValorSequencialId(AelExameEquipCampoLaud elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		AelExameEquipCampoLaudId id = new AelExameEquipCampoLaudId();
		id.setPclVelEmaExaSigla(elemento.getAelParametroCamposLaudo().getId().getVelEmaExaSigla());
		id.setPclVelEmaManSeq(elemento.getAelParametroCamposLaudo().getId().getVelEmaManSeq());
		id.setPclVelSeqp(elemento.getAelParametroCamposLaudo().getId().getVelSeqp());
		id.setPclCalSeq(elemento.getAelParametroCamposLaudo().getId().getCalSeq());
		id.setPclSeqp(elemento.getAelParametroCamposLaudo().getId().getSeqp());
		id.setEeqEemEmaExaSigla(elemento.getAelExameEquipamento().getId().getEemEmaExaSigla());
		id.setEeqEemEmaManSeq(elemento.getAelExameEquipamento().getId().getEemEmaManSeq());
		id.setEeqEemEquSeq(elemento.getAelExameEquipamento().getId().getEemEquSeq());
		id.setEeqEemProgramacao(elemento.getAelExameEquipamento().getId().getEemProgramacao().toString());
		id.setEeqCodigo(elemento.getAelExameEquipamento().getId().getCodigo());
		id.setEeqNumero(elemento.getAelExameEquipamento().getId().getNumero());
		
		elemento.setId(id);
	}
	
	
	/**
	 * Lista registros da tabela<br>
	 * AEL_EXAME_EQUIP_CAMPOS_LAUD<br>
	 * por exame material. 
	 * 
	 * @param emaExaSigla
	 * @param emaManSeq
	 * @param seqp
	 * @return
	 */
	public List<AelExameEquipCampoLaud> listarExameEquipamentoLaudoPorMaterialExame(String emaExaSigla, Integer emaManSeq, Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameEquipCampoLaud.class);
		criteria.createAlias(AelExameEquipCampoLaud.Fields.AEL_PARAMETRO_CAMPOS_LAUDO.toString(), "PCL");
		
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_EXA_SIGLA.toString()), emaExaSigla));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_EMA_MAN_SEQ.toString()), emaManSeq));
		criteria.add(Restrictions.eq("PCL.".concat(AelParametroCamposLaudo.Fields.VEL_SEQP.toString()), seqp));
		
		return this.executeCriteria(criteria);
	}
	
}
