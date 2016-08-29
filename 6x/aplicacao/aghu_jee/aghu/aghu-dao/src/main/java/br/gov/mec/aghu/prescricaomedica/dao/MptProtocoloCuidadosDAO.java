package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MptProtocoloCuidados;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
/**
 * 
 * @modulo prescricaomedica.cadastrosbasicos
 *
 */
public class MptProtocoloCuidadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptProtocoloCuidados> {
	
	private static final long serialVersionUID = -7487173167971232377L;
	
	private final String ALIAS_CDU_PONTO = "CDU.";
	private final String ALIAS_PCU_PONTO = "PCU.";
	private final String ALIAS_TFS_PONTO = "TFS.";
	private final String ALIAS_VPS_PONTO = "VPS.";
	
	/**
	 * #44283 - C3
	 * @param seqVersaoProtocolo
	 * @return List<ProtocoloMedicamentoSolucaoCuidadoVO>
	 */
	public List<ProtocoloMedicamentoSolucaoCuidadoVO> consultaCuidadosPorVersaoProtocolo(Integer seqVersaoProtocolo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCuidados.class, "PCU");
		
		criteria.createAlias(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.VERSAO_PROTOCOLO_SESSAO.toString(), "VPS", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.TIPO_FREQUENCIA_APRAZAMENTO.toString(), "TFS", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.CUIDADO_USUAL.toString(), "CDU", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(ALIAS_VPS_PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString(), seqVersaoProtocolo));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.PCU_SEQ.toString())
				.add(Projections.property(ALIAS_VPS_PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.VPS_SEQ.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TFQ_SEQ.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DESCRICAO_APRAZAMENTO.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_DIGITA_FREQUENCIA.toString())
				.add(Projections.property(ALIAS_CDU_PONTO + MpmCuidadoUsual.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.CDU_SEQ.toString())
				.add(Projections.property(ALIAS_CDU_PONTO + MpmCuidadoUsual.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DESCRICAO_CUIDADO_USUAL.toString())
				.add(Projections.property(ALIAS_CDU_PONTO + MpmCuidadoUsual.Fields.IND_DIGITA_COMPLEMENTO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_DIGITA_COMPLEMENTO.toString())
				.add(Projections.property(ALIAS_CDU_PONTO + MpmCuidadoUsual.Fields.FREQUENCIA.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.FREQUENCIA_CUIDADO.toString())
				.add(Projections.property(ALIAS_CDU_PONTO + MpmCuidadoUsual.Fields.TFQ_SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TFQ_CUIDADO.toString())
				.add(Projections.property(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.FREQUENCIA.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.FREQUENCIA_PROTOCOLO_CUIDADO.toString())
				.add(Projections.property(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.COMPLEMENTO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.COMPLEMENTO.toString())
				.add(Projections.property(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.ORDEM.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.ORDEM.toString())
				.add(Projections.property(ALIAS_PCU_PONTO + MptProtocoloCuidados.Fields.TEMPO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TEMPO.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloMedicamentoSolucaoCuidadoVO.class));
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Obter protocolo cuidados por ordem, seqVersaoProtocolo
	 * @param ordem
	 * @param seqVersaoProtocoloSessao
	 * @return MptProtocoloCuidados
	 */
	public MptProtocoloCuidados obterProtocoloCuidadoPorOrdemSeqVersaoProtocolo(Short ordem, Integer seqVersaoProtocoloSessao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloCuidados.class);
		
		criteria.add(Restrictions.eq(MptProtocoloCuidados.Fields.ORDEM.toString(), ordem));
		criteria.add(Restrictions.eq(MptProtocoloCuidados.Fields.VPS_SEQ.toString(), seqVersaoProtocoloSessao));
		
		return (MptProtocoloCuidados) executeCriteriaUniqueResult(criteria);		
	}	
}
