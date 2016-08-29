package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaItemNptPadrao;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class VMpmDosagemDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMpmDosagem> {


	private static final long serialVersionUID = 6106594990485819073L;

	public VMpmDosagem obterVMpmDosagem(Integer medMatCodigo, Integer seqDosagem) {
		DetachedCriteria cri = DetachedCriteria.forClass(VMpmDosagem.class);
		
		cri.add(Restrictions.eq(VMpmDosagem.Fields.SEQ_MEDICAMENTO.toString(), medMatCodigo));
		cri.add(Restrictions.eq(VMpmDosagem.Fields.SEQ_DOSAGEM.toString(),seqDosagem));

		
		VMpmDosagem vMpmDosagem = (VMpmDosagem)executeCriteriaUniqueResult(cri);
		
		return vMpmDosagem;
	}
	
	public VMpmDosagem obterVMpmDosagemPorAfaItem(AfaItemNptPadrao afaItemNptPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmDosagem.class, "vd");
		
		criteria.createAlias("vd."+ AfaItemNptPadrao.Fields.ID.toString(), "ain", JoinType.INNER_JOIN);
		
		
//		criteria.add(Restrictions.eq(VMpmDosagem.Fields.ID.toString(),AfaItemNptPadrao.Fields.ID.toString()));
		
		VMpmDosagem vMpmDosagem = (VMpmDosagem)executeCriteriaUniqueResult(criteria);
		
		return vMpmDosagem;

	}
	
	/**
	 * consulta C10
	 * suggestionbox SB05 estoria #3435
	 * @param medMatCodigo
	 * @return
	 */
	public List<VMpmDosagem> pesquisarVMpmDosagemPorfiltro(Integer medMatCodigo,Object objeto) {
		DetachedCriteria cri = criaCriteriacomFiltros(medMatCodigo,objeto);			
		return  executeCriteria(cri, 0, 100, "formaDosagem", true);
	}
	
	public long pesquisarVMpmDosagemPorfiltroCount(Integer medMatCodigo,Object objeto) {
		DetachedCriteria cri = criaCriteriacomFiltros(medMatCodigo,objeto);
		return  executeCriteriaCount(cri);
	}
	
	public DetachedCriteria criaCriteriacomFiltros(Integer medMatCodigo,Object objPesquisa){
		DetachedCriteria cri = DetachedCriteria.forClass(VMpmDosagem.class);
		cri.add(Restrictions.eq(VMpmDosagem.Fields.SEQ_MEDICAMENTO.toString(), medMatCodigo));		
		String strPesquisa = (String) objPesquisa;
		if (CoreUtil.isNumeroShort(strPesquisa)) { 
			cri.add(Restrictions.or(Restrictions.eq(VMpmDosagem.Fields.SEQ_DOSAGEM.toString(),Short.valueOf(strPesquisa))));
		}
		
		return cri;
	}
	
	/**
	 * @author marcelo.deus
	 * #44281 - C2
	 */
	public List<VMpmDosagem> listarUnidadeDosagemMedicamento(Integer codMedicamento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmDosagem.class, "VDS");
		
		criteria.add(Restrictions.eq("VDS." + VMpmDosagem.Fields.IND_SITUACAO.toString(), DominioSituacao.A))
				.add(Restrictions.eq("VDS." + VMpmDosagem.Fields.SEQ_MEDICAMENTO.toString(), codMedicamento));
		
		criteria.addOrder(Order.asc("VDS." + VMpmDosagem.Fields.SEQ_UNIDADE.toString()));
	
		return executeCriteria(criteria);
	}
	
	/**
	 * @author marcelo.deus
	 * #44281 - P1 - cur_fds
	 */
	public VMpmDosagem buscarMpmDosagemPorSeqMedicamentoSeqDosagem(Integer seqMedicamento, Integer seqDosagem){
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmDosagem.class);
		
		criteria.add(Restrictions.eq(VMpmDosagem.Fields.SEQ_MEDICAMENTO.toString(), seqMedicamento))
				.add(Restrictions.eq(VMpmDosagem.Fields.SEQ_DOSAGEM.toString(), seqDosagem));
		
		return (VMpmDosagem) executeCriteriaUniqueResult(criteria);		
	}
	
	/**
	 * @author marcelo.deus
	 * #44281
	 */
	public VMpmDosagem buscarMpmDosagemPorSeqDosagem(Integer seqDosagem){
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmDosagem.class);
		
		criteria.add(Restrictions.eq(VMpmDosagem.Fields.SEQ_DOSAGEM.toString(), seqDosagem))
				.add(Restrictions.eq(VMpmDosagem.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return (VMpmDosagem) executeCriteriaUniqueResult(criteria);		
	}
}
