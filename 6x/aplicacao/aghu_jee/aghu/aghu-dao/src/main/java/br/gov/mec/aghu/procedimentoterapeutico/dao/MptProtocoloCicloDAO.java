package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MptPrescricaoCiclo;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImpressaoTicketAgendamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptProtocoloVersaoAssistencialVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloCicloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloPrescricaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.RegistroIntercorrenciaVO;

public class MptProtocoloCicloDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptProtocoloCiclo> {

	private static final long serialVersionUID = 6082584819984406090L;

	/**
	 * Consulta de protocolos.
	 * @param codigo
	 * @return MptProtocoloCiclo
	 */
	public List<MptProtocoloCiclo> buscarProtocoloCiclo(Integer codigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCiclo.class, "PTC");
		criteria.createAlias("PTC." + MptProtocoloCiclo.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PTC." + MptProtocoloCiclo.Fields.PROTOCOLO_ASSISTENCIAL.toString(), "PAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTV", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("PTC." + MptProtocoloCiclo.Fields.CICLO.toString(), codigo));
		
		return executeCriteria(criteria);
	}
	
	
public List<ProtocoloCicloVO> obterProtocolo(final Integer cloSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCiclo.class);

		// Aliases padrão da C6
		criteria.createAlias(MptProtocoloCiclo.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MptProtocoloCiclo.Fields.MPT_PRESCRICAO_CICLO.toString(), "CLO", JoinType.INNER_JOIN);

		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property(MptProtocoloCiclo.Fields.DESCRICAO.toString()), ProtocoloCicloVO.Fields.DESCRICAO.toString());
		pList.add(Projections.property("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString()), ProtocoloCicloVO.Fields.TITULO.toString());
		criteria.setProjection(Projections.distinct(pList)); 

criteria.add(Restrictions.eq("CLO." + MptPrescricaoCiclo.Fields.SEQ.toString(), cloSeq));

		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloCicloVO.class));
		return executeCriteria(criteria);
	}

	/**
	 * #42292 - C3 - Consulta carrega protocolo(s) do ciclo informado.
	 * @param codPaciente
	 * @return
	 */
	public List<ImpressaoTicketAgendamentoVO> obterProtocolosCicloInformado(List<Integer> listCloSeq) {
		DetachedCriteria criteria = montarCriteriaProtocolosCicloInformado(listCloSeq);
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarCriteriaProtocolosCicloInformado(List<Integer> listCloSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCiclo.class, "PTC");
		criteria.createAlias("PTC." + MptProtocoloCiclo.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PTC." + MptProtocoloCiclo.Fields.DESCRICAO.toString()), ImpressaoTicketAgendamentoVO.Fields.PTC_DESCRICAO.toString())
				.add(Projections.property("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString()), ImpressaoTicketAgendamentoVO.Fields.PTA_TITULO.toString()));
		
		if (listCloSeq != null && !listCloSeq.isEmpty()) {
			criteria.add(Restrictions.in("PTC." + MptProtocoloCiclo.Fields.CICLO.toString(), listCloSeq));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ImpressaoTicketAgendamentoVO.class));
		return criteria;
	}

	/**
	 * #44249 C6
	 * @param cloSeq
	 * @return
	 */
	public List<ProtocoloPrescricaoVO> obterProtocoloPrescricaoVOPorCloSeq(Integer cloSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCiclo.class, "PTC");
		criteria.createAlias("PTC."+MptProtocoloCiclo.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VPA."+MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PTC."+MptProtocoloCiclo.Fields.DESCRICAO.toString()), ProtocoloPrescricaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("PTA."+MpaProtocoloAssistencial.Fields.TITULO.toString()), ProtocoloPrescricaoVO.Fields.TITULO.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloPrescricaoVO.class));
		
		criteria.add(Restrictions.eq("PTC."+MptProtocoloCiclo.Fields.CLO_SEQ.toString(), cloSeq));
		
		return executeCriteria(criteria);
	}	


	public List<MptProtocoloVersaoAssistencialVO> pesquisarProtocoloGrid(Integer cloSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCiclo.class, "PTC");
		
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("PTC." + MptProtocoloCiclo.Fields.DESCRICAO.toString()), "descricao");
		pList.add(Projections.property("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString()), "titulo");
		criteria.setProjection(pList);
		
		criteria.createAlias("PTC." + MptProtocoloCiclo.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("PTC." + MptProtocoloCiclo.Fields.CICLO.toString(), cloSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MptProtocoloVersaoAssistencialVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<RegistroIntercorrenciaVO> obterProtocolosSessao(Integer seqSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCiclo.class, "PTC");
		criteria.createAlias("PTC." + MptProtocoloCiclo.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(),"VPA",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PTC." + MptProtocoloCiclo.Fields.PROTOCOLO_ASSISTENCIAL.toString(),"PTA",JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PTC." + MptProtocoloCiclo.Fields.DESCRICAO.toString()),RegistroIntercorrenciaVO.Fields.PROTOCOLO_DESCRICAO.toString())
				.add(Projections.property("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString()), RegistroIntercorrenciaVO.Fields.PROTOCOLO_TITULO.toString()));

		if(seqSessao != null){
			criteria.add(Restrictions.eq(MptProtocoloCiclo.Fields.CLO_SEQ.toString(),seqSessao));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(RegistroIntercorrenciaVO.class));
		return executeCriteria(criteria);
	}
}