package br.gov.mec.aghu.procedimentoterapeutico.dao;



import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HomologarProtocoloVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptProtocoloMedicamentosDAO extends BaseDao<MptProtocoloMedicamentos> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6689671667221112921L;
	
	private final String ALIAS_MED_PONTO = "MED.";
	private final String ALIAS_PIM_PONTO = "PIM.";
	private final String ALIAS_PTM_PONTO = "PTM.";
	private final String ALIAS_TFS_PONTO = "TFS.";
	private final String ALIAS_TVA_PONTO = "TVA.";
	private final String ALIAS_VAD_PONTO = "VAD.";
	private final String ALIAS_VIA_PONTO = "VIA.";
	private final String ALIAS_VPS_PONTO = "VPS.";
	
	public List<ProtocoloMedicamentoSolucaoCuidadoVO> pesquisarListaTratamento(Integer seqVersaoProtocoloSessao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloMedicamentos.class, "PTM");		
		
		criteria.createAlias(MptProtocoloMedicamentos.Fields.PROTOCOLO_ITEM_MEDICAMENTOS.toString(), "PIM", JoinType.INNER_JOIN);
		criteria.createAlias(MptProtocoloMedicamentos.Fields.VERSAO_PROTOCOLO_SESSAO.toString(), "VPS", JoinType.INNER_JOIN);	
		criteria.createAlias(MptProtocoloMedicamentos.Fields.TIPO_FREQUENCIA_APRAZAMENTO.toString(), "TFS", JoinType.INNER_JOIN);	
		criteria.createAlias(ALIAS_PIM_PONTO + MptProtocoloItemMedicamentos.Fields.AFA_MEDICAMENTO.toString(), "MED", JoinType.INNER_JOIN);	
		criteria.createAlias(MptProtocoloMedicamentos.Fields.VIA_ADMINISTRACAO.toString(), "VIA", JoinType.INNER_JOIN);	
			
		criteria.add(Restrictions.eq(ALIAS_VPS_PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString(), seqVersaoProtocoloSessao));
		criteria.add(Restrictions.eq(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.IND_SOLUCAO.toString(), false));

	
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MptProtocoloMedicamentos.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.PTM_SEQ.toString())
				.add(Projections.property(ALIAS_PIM_PONTO + MptProtocoloItemMedicamentos.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.SEQ_ITEM_PROTOCOLO_MDTOS.toString())
				.add(Projections.property(ALIAS_VPS_PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.VPS_SEQ.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TFQ_SEQ.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.PTM_DESCRICAO.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.IND_INFUSOR_PORTATIL.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_INFUSOR_PORTATIL.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.IND_BOMBA_INFUSAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_BOMBA_INFUSAO.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.GOTEJO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.GOTEJO.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DESCRICAO_APRAZAMENTO.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_DIGITA_FREQUENCIA.toString())
				.add(Projections.property(ALIAS_MED_PONTO + AfaMedicamento.Fields.MAT_CODIGO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.MED_MAT_CODIGO.toString())
				.add(Projections.property(ALIAS_PIM_PONTO + MptProtocoloItemMedicamentos.Fields.DOSE.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DOSE.toString())
				.add(Projections.property(ALIAS_MED_PONTO + AfaMedicamento.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DESCRICAO_MEDICAMENTO.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.QTDE_HORAS_CORRER.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.QTD_HORAS_CORRER.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.UNID_HORAS_CORRER.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.UNIDADE_HORAS_CORRER.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.FREQUENCIA.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.PTM_FREQUENCIA.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.COMPLEMENTO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.COMPLEMENTO.toString())
				.add(Projections.property(ALIAS_VIA_PONTO + AfaViaAdministracao.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DESCRICAO_VIA.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.ORDEM.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.ORDEM.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.TEMPO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TEMPO.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.IND_SOLUCAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_SOLUCAO.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.OBSERVACAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.OBSERVACAO.toString())
				.add(Projections.property(MptProtocoloMedicamentos.Fields.MED_MAT_CODIGO_DILUENTE.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.MED_MAT_CODIGO_DILUENTE.toString())

				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloMedicamentoSolucaoCuidadoVO.class));
		
		return  executeCriteria(criteria); 
	}
	
	/**
	 * #44283 - C2
	 * @param seqVersaoProtocoloSessao
	 * @return List<ProtocoloMedicamentoSolucaoCuidadoVO>
	 */
	public List<ProtocoloMedicamentoSolucaoCuidadoVO> pesquisarSolucoesPorVersaoProtocolo(Integer seqVersaoProtocoloSessao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloMedicamentos.class, "PTM");
		
		criteria.createAlias(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.VERSAO_PROTOCOLO_SESSAO.toString(), "VPS", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.TIPO_FREQUENCIA_APRAZAMENTO.toString(), "TFS", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.VIA_ADMINISTRACAO.toString(), "VAD", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.TIPO_VELOC_ADMINISTRACOES.toString(), "TVA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ALIAS_VPS_PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString(), seqVersaoProtocoloSessao));
		criteria.add(Restrictions.eq(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.IND_SOLUCAO.toString(), Boolean.TRUE));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.PTM_SEQ.toString())
				.add(Projections.property(ALIAS_VPS_PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.VPS_SEQ.toString())
				.add(Projections.property(ALIAS_TVA_PONTO + AfaTipoVelocAdministracoes.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TVA_SEQ.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.PTM_DESCRICAO.toString())
				.add(Projections.property(ALIAS_VAD_PONTO + AfaViaAdministracao.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DESCRICAO_VIA.toString())
				.add(Projections.property(ALIAS_VAD_PONTO + AfaViaAdministracao.Fields.SIGLA.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.SIGLA_VIA.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.FREQUENCIA.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.PTM_FREQUENCIA.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.SEQ.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TFQ_SEQ.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DESCRICAO_APRAZAMENTO.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_DIGITA_FREQUENCIA.toString())
				.add(Projections.property(ALIAS_TFS_PONTO + MpmTipoFrequenciaAprazamento.Fields.SINTAXE.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TFS_SINTAXE.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.QTDE_HORAS_CORRER.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.QTD_HORAS_CORRER.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.UNID_HORAS_CORRER.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.UNIDADE_HORAS_CORRER.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.IND_BOMBA_INFUSAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_BOMBA_INFUSAO.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.IND_DOMICILIAR.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_USO_DOMICILIAR.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.DIAS_DE_USO_DOMICILIAR.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DIAS_USO_DOMICILIAR.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.IND_SE_NECESSARIO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_SE_NECESSARIO.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.GOTEJO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.GOTEJO.toString())
				.add(Projections.property(ALIAS_TVA_PONTO + AfaTipoVelocAdministracoes.Fields.DESCRICAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.DESCRICAO_VELOC.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.OBSERVACAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.OBSERVACAO.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.ORDEM.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.ORDEM.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.TEMPO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.TEMPO.toString())
				.add(Projections.property(ALIAS_PTM_PONTO + MptProtocoloMedicamentos.Fields.IND_SOLUCAO.toString()), ProtocoloMedicamentoSolucaoCuidadoVO.Fields.IND_SOLUCAO.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloMedicamentoSolucaoCuidadoVO.class));
		
		return executeCriteria(criteria); 
		
	}
	
	/**
	 * Obter protocolo medicamento por ordem, seqVersaoProtocolo 
	 * @param ordem
	 * @param seqVersaoProtocoloSessao
	 * @return MptProtocoloMedicamentos
	 */
	public MptProtocoloMedicamentos obterProtocoloMedicamentoPorOrdemSeqVersaoProtocolo(Short ordem, Integer seqVersaoProtocoloSessao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloMedicamentos.class);
		
		criteria.add(Restrictions.eq(MptProtocoloMedicamentos.Fields.ORDEM.toString(), ordem));
		criteria.add(Restrictions.eq(MptProtocoloMedicamentos.Fields.VPS_SEQ.toString(), seqVersaoProtocoloSessao));
		criteria.add(Restrictions.eq(MptProtocoloMedicamentos.Fields.IND_SOLUCAO.toString(), Boolean.FALSE));
		
		return (MptProtocoloMedicamentos) executeCriteriaUniqueResult(criteria);		
	}
	
	/**
	 * Obter protocolo solucao por ordem, seqVersaoProtocolo 
	 * @param ordem
	 * @param seqVersaoProtocoloSessao
	 * @return MptProtocoloMedicamentos
	 */
	public MptProtocoloMedicamentos obterProtocoloSolucaoPorOrdemSeqVersaoProtocolo(Short ordem, Integer seqVersaoProtocoloSessao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloMedicamentos.class);
		
		criteria.add(Restrictions.eq(MptProtocoloMedicamentos.Fields.ORDEM.toString(), ordem));
		criteria.add(Restrictions.eq(MptProtocoloMedicamentos.Fields.VPS_SEQ.toString(), seqVersaoProtocoloSessao));
		criteria.add(Restrictions.eq(MptProtocoloMedicamentos.Fields.IND_SOLUCAO.toString(), Boolean.TRUE));
		
		return (MptProtocoloMedicamentos) executeCriteriaUniqueResult(criteria);		
	}

	public void persistirSolucao(MptProtocoloMedicamentos solucao) {
		persistir(solucao);
		flush();
	}
	
	/**#44279 C1**/
	public List<HomologarProtocoloVO> obterListaMedicamentosProtocolo(Integer seqVersaoProtocoloSessao){
		DetachedCriteria criteria = criteriaProtocoloMedicamentos(seqVersaoProtocoloSessao);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.SEQ.toString()).as(HomologarProtocoloVO.Fields.PTM_SEQ.toString()))
				.add(Projections.property(ALIAS_VPS_PONTO+MptVersaoProtocoloSessao.Fields.SEQ.toString()).as(HomologarProtocoloVO.Fields.VPS_SEQ.toString()))
				.add(Projections.property(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.TFQ_SEQ.toString()).as(HomologarProtocoloVO.Fields.TFQ_SEQ.toString()))
				.add(Projections.property(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.ORDEM.toString()).as(HomologarProtocoloVO.Fields.ORDEM.toString()))
				.add(Projections.property(ALIAS_MED_PONTO+AfaMedicamento.Fields.DESCRICAO.toString()).as(HomologarProtocoloVO.Fields.MEDICAMENTO.toString()))
				.add(Projections.property(ALIAS_PIM_PONTO+MptProtocoloItemMedicamentos.Fields.DOSE.toString()).as(HomologarProtocoloVO.Fields.DOSE.toString()))
				.add(Projections.property(ALIAS_VIA_PONTO+AfaViaAdministracao.Fields.DESCRICAO.toString()).as(HomologarProtocoloVO.Fields.VIA.toString()))
				.add(Projections.property(ALIAS_TFS_PONTO+MpmTipoFrequenciaAprazamento.Fields.IND_DIGITA_FREQUENCIA.toString()).as(HomologarProtocoloVO.Fields.INDICADOR_FREQUENCIA.toString()))
				.add(Projections.property(ALIAS_TFS_PONTO+MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()).as(HomologarProtocoloVO.Fields.DESCRICAO_APRAZAMENTO.toString()))
				.add(Projections.property(ALIAS_TFS_PONTO+MpmTipoFrequenciaAprazamento.Fields.SINTAXE.toString()).as(HomologarProtocoloVO.Fields.SINTAXE_FREQUENCIA.toString()))
				.add(Projections.property(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.TEMPO.toString()).as(HomologarProtocoloVO.Fields.TEMPO.toString()))
				.add(Projections.property(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.FREQUENCIA.toString()).as(HomologarProtocoloVO.Fields.FREQUENCIA.toString()))
				.add(Projections.property(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.IND_NAO_PERMITE_ALTERACAO.toString()).as(HomologarProtocoloVO.Fields.IND_PERMITE_ALTERACAO.toString())));
		
		criteria.addOrder(Order.asc(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.ORDEM.toString()))
				.addOrder(Order.asc((ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.SEQ.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(HomologarProtocoloVO.class));
		return executeCriteria(criteria);
	}
	
	//#44279 C1 - Monta a consulta
	private DetachedCriteria criteriaProtocoloMedicamentos(Integer seqVersaoProtocoloSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptProtocoloMedicamentos.class, "PTM");
		criteria.createAlias(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.PROTOCOLO_ITEM_MEDICAMENTOS.toString(), "PIM");
		criteria.createAlias(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.VERSAO_PROTOCOLO_SESSAO.toString(), "VPS");	
		criteria.createAlias(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.TIPO_FREQUENCIA_APRAZAMENTO.toString(), "TFS");	
		criteria.createAlias(ALIAS_PIM_PONTO+MptProtocoloItemMedicamentos.Fields.AFA_MEDICAMENTO.toString(), "MED");	
		criteria.createAlias(ALIAS_PTM_PONTO+MptProtocoloMedicamentos.Fields.VIA_ADMINISTRACAO.toString(), "VIA");
		
		criteria.add(Restrictions.eq(ALIAS_VPS_PONTO+MptVersaoProtocoloSessao.Fields.SEQ.toString(), seqVersaoProtocoloSessao));
		criteria.add(Restrictions.or(Restrictions.eq(ALIAS_MED_PONTO+AfaMedicamento.Fields.IND_PADRONIZACAO.toString(), Boolean.FALSE), 
				Restrictions.eq(ALIAS_VIA_PONTO+AfaViaAdministracao.Fields.IND_USO_QUIMIOTERAPIA.toString(), Boolean.TRUE)));
		
		return criteria;
	}

}

