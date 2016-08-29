package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptProtocoloItemMedicamentosDAO extends BaseDao<MptProtocoloItemMedicamentos> {

	private static final String ORDER_BY = " ORDER BY";
//	private static final String AND_FDS = "		AND FDS.";
	private static final String AND_PIM = "		AND PIM.";
	private static final String PIM = "			PIM.";
	private static final String WHERE = "	WHERE	";
	private static final String FROM = "  FROM ";
	private static final String AS_PIM = " AS PIM, ";
	private static final String AS_VDM = " AS VDM, ";
	private static final String AS_FDS = " AS FDS ";
//	private static final String AS_UMM = " AS UMM ";
	private static final String ESPACO = "	    ";
	private static final String AS = " as ";
	private static final String VIRGULA = " , ";
	/**
	 * 
	 */
	private static final long serialVersionUID = 3461757181837578606L;

	
	/**
	 * @author marcelo.deus
	 * #44281 - C7
	 */
	@SuppressWarnings("unchecked")
	public List<ProtocoloItensMedicamentoVO> listarItensMedicamento(Long codSolucao){
			
		StringBuffer hql = new StringBuffer(750);
		
		hql.append(" SELECT DISTINCT PIM." + MptProtocoloItemMedicamentos.Fields.SEQ.toString()+ AS).append(ProtocoloItensMedicamentoVO.Fields.PIM_SEQ.toString() + VIRGULA)
			.append("		VDM." + VAfaDescrMdto.Fields.DESCRICAO.toString()+ AS).append(ProtocoloItensMedicamentoVO.Fields.VDM_DESCRICAO.toString() + VIRGULA)
			.append("		PIM." + MptProtocoloItemMedicamentos.Fields.OBSERVACAO.toString()+ AS).append(ProtocoloItensMedicamentoVO.Fields.PIM_OBSERVACAO.toString() + VIRGULA)
			.append("		PIM." + MptProtocoloItemMedicamentos.Fields.DOSE.toString()+ AS).append(ProtocoloItensMedicamentoVO.Fields.PIM_DOSE.toString() + VIRGULA)
			.append("		PIM." + MptProtocoloItemMedicamentos.Fields.MED_MAT_CODIGO.toString()+ AS).append(ProtocoloItensMedicamentoVO.Fields.MED_MAT_CODIGO.toString() + VIRGULA)
//			.append("		UMM." + MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()+ AS).append(ProtocoloItensMedicamentoVO.Fields.UMM.toString())
//			.append("		FDS." + AfaFormaDosagem.Fields.UNIDADE_MEDICAS_SEQ.toString()+ AS).append(ProtocoloItensMedicamentoVO.Fields.UMM_SEQ.toString())
			.append("		FDS." + AfaFormaDosagem.Fields.SEQ.toString()+ AS).append(ProtocoloItensMedicamentoVO.Fields.FDS_SEQ.toString())
			
			.append(FROM).append(MptProtocoloItemMedicamentos.class.getSimpleName()).append(AS_PIM)
			.append(ESPACO).append(VAfaDescrMdto.class.getSimpleName()).append(AS_VDM)
			.append(ESPACO).append(AfaFormaDosagem.class.getSimpleName()).append(AS_FDS)
//			.append(ESPACO).append(MpmUnidadeMedidaMedica.class.getSimpleName()).append(AS_UMM)
			
			.append(WHERE)
			.append(PIM + MptProtocoloItemMedicamentos.Fields.MED_MAT_CODIGO.toString()+ " = VDM." + VAfaDescrMdto.Fields.MAT_CODIGO.toString())
			.append(AND_PIM + MptProtocoloItemMedicamentos.Fields.FDS_SEQ.toString()+ " = FDS." + AfaFormaDosagem.Fields.SEQ.toString())
//			.append(AND_FDS + AfaFormaDosagem.Fields.UNIDADE_MEDICAS_SEQ.toString()+ " = UMM." + MpmUnidadeMedidaMedica.Fields.SEQ.toString())
			.append(AND_PIM + MptProtocoloItemMedicamentos.Fields.PROTOCOLO_MEDICAMENTOS_SEQ.toString()+ " = :codSolucao")
			
			.append(ORDER_BY)
			.append(PIM + MptProtocoloItemMedicamentos.Fields.SEQ.toString());
			
		Query q = createHibernateQuery(hql.toString());
		q.setLong("codSolucao", codSolucao);
		q.setResultTransformer(Transformers.aliasToBean(ProtocoloItensMedicamentoVO.class));
		
		return q.list();
	}
	
	public List<MptProtocoloItemMedicamentos> obterItensMedicamentoPorSeqProtocolo(Long seqProtocolo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloItemMedicamentos.class);
		criteria.add(Restrictions.eq(MptProtocoloItemMedicamentos.Fields.PROTOCOLO_MEDICAMENTOS_SEQ.toString(), seqProtocolo));
		return executeCriteria(criteria);
	}
}
