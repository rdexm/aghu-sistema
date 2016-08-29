package br.gov.mec.aghu.procedimentoterapeutico.dao;


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloSessao;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.NovaVersaoProtocoloVO;
import br.gov.mec.aghu.protocolos.vo.InformacaoVersaoProtocoloVO;
import br.gov.mec.aghu.protocolos.vo.ProtocoloVO;
import br.gov.mec.aghu.protocolos.vo.SituacaoVersaoProtocoloVO;


public class MptVersaoProtocoloSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptVersaoProtocoloSessao> {

	private static final long serialVersionUID = 9069743268660153768L;
	
	private final String ALIAS_VPS = "VPS";
	private final String ALIAS_PSE = "PSE";
	private final String ALIAS_PTM = "PTM";
	private final String ALIAS_PIM = "PIM";
	private final String PONTO = ".";
	private static final String ALIAS_TPS = "TPS";
	
	/**
	 * Monta criteria de pesquisa de protocolos ativos.
	 * @param filtro
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarCriteriaPesquisarProtocolosAtivos(ProtocoloVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, ALIAS_VPS);		
		
		criteria.createAlias(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(), ALIAS_PSE, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.PROTOCOLO_MEDICAMENTOS.toString(), ALIAS_PTM, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_PTM + PONTO + MptProtocoloMedicamentos.Fields.PROTOCOLO_ITEM_MEDICAMENTOS.toString(), ALIAS_PIM, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TPS_SEQ.toString(), filtro.getSeqTipoSessao()));
		
		if (filtro.getCodMedicamento() != null) {
			criteria.add(Restrictions.eq(ALIAS_PIM + PONTO + MptProtocoloItemMedicamentos.Fields.MED_MAT_CODIGO, filtro.getCodMedicamento()));
		}
		
		if (filtro.getTituloProtocoloSessao() != null) {
			criteria.add(Restrictions.ilike(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TITULO.toString(), filtro.getTituloProtocoloSessao(), MatchMode.ANYWHERE));
		}
		
		if (filtro.getIndSituacaoVersaoProtocoloSessao().equals(DominioSituacaoProtocolo.A)) {
			criteria.add(Restrictions.ne(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoProtocolo.I));
		} else {
			criteria.add(Restrictions.eq(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString(), filtro.getIndSituacaoVersaoProtocoloSessao()));
		}
		
		return criteria;
	}

	/**
	 * Pesquisa protocolos ativos.
	 * @param filtro
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return List<ProtocoloVO>
	 */
	public List<ProtocoloVO> pesquisarProtocolosAtivos(ProtocoloVO filtro, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc) {
		
		DetachedCriteria criteria = montarCriteriaPesquisarProtocolosAtivos(filtro);
		
		criteria.addOrder(Order.asc(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TITULO.toString()));
		criteria.addOrder(Order.desc(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.VERSAO.toString()));
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TITULO.toString()), ProtocoloVO.Fields.TITULO_PROTOCOLO_SESSAO.toString())
				.add(Projections.property(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.SEQ.toString()), ProtocoloVO.Fields.SEQ_PROTOCOLO_SESSAO.toString())
				.add(Projections.property(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString()), ProtocoloVO.Fields.SEQ_VERSAO_PROTOCOLO_SESSAO.toString())
				.add(Projections.property(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.VERSAO.toString()), ProtocoloVO.Fields.VERSAO.toString())
				.add(Projections.property(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString()), ProtocoloVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.DIAS_TRATAMENTO.toString()), ProtocoloVO.Fields.DIAS_TRATAMENTO.toString())
			));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloVO.class));
				
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);			
	}
	
	/**
	 * Conta registros de pesquisar protocolos ativos.
	 * @param filtro
	 * @return Long
	 */
	public Long contarPesquisarProtocolosAtivos(ProtocoloVO filtro) {
		DetachedCriteria criteria = montarCriteriaPesquisarProtocolosAtivos(filtro);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TITULO.toString()), ProtocoloVO.Fields.TITULO_PROTOCOLO_SESSAO.toString())
				.add(Projections.property(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.SEQ.toString()), ProtocoloVO.Fields.SEQ_PROTOCOLO_SESSAO.toString())
				.add(Projections.property(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString()), ProtocoloVO.Fields.SEQ_VERSAO_PROTOCOLO_SESSAO.toString())
				.add(Projections.property(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.VERSAO.toString()), ProtocoloVO.Fields.VERSAO.toString())
				.add(Projections.property(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString()), ProtocoloVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.DIAS_TRATAMENTO.toString()), ProtocoloVO.Fields.DIAS_TRATAMENTO.toString())
			));
		
		List<ProtocoloVO> lista = executeCriteria(criteria);
		
		return Long.valueOf(lista.size());
	}
	
	/**
	 * Monta criteria de pesquisa de protocolos inativos.
	 * @param filtro
	 * @return DetachedCriteria
	 */
	private DetachedCriteria montarCriteriaPesquisarProtocolosInativos(ProtocoloVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, ALIAS_VPS);		
		
		criteria.createAlias(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(), ALIAS_PSE, JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.PROTOCOLO_MEDICAMENTOS.toString(), ALIAS_PTM, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_PTM + PONTO + MptProtocoloMedicamentos.Fields.PROTOCOLO_ITEM_MEDICAMENTOS.toString(), ALIAS_PIM, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TPS_SEQ.toString(), filtro.getSeqTipoSessao()));
		
		if (filtro.getCodMedicamento() != null) {
			criteria.add(Restrictions.eq(ALIAS_PIM + PONTO + MptProtocoloItemMedicamentos.Fields.MED_MAT_CODIGO, filtro.getCodMedicamento()));
		}
		
		if (filtro.getTituloProtocoloSessao() != null) {
			criteria.add(Restrictions.ilike(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TITULO.toString(), filtro.getTituloProtocoloSessao(), MatchMode.ANYWHERE));
		}
			
		criteria.add(Restrictions.eq(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoProtocolo.I));	
		
		return criteria;
	}
	
	/**
	 * Pesquisa protocolos inativos.
	 * @param filtro
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return List<ProtocoloVO>
	 */
	public List<ProtocoloVO> pesquisarProtocolosInativos(ProtocoloVO filtro, Integer firstResult, Integer maxResults, String orderProperty, Boolean asc) {
		
		DetachedCriteria criteria = montarCriteriaPesquisarProtocolosInativos(filtro);
		
		criteria.addOrder(Order.asc(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TITULO.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.groupProperty(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TITULO.toString()), ProtocoloVO.Fields.TITULO_PROTOCOLO_SESSAO.toString())
				.add(Projections.groupProperty(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.SEQ.toString()), ProtocoloVO.Fields.SEQ_PROTOCOLO_SESSAO.toString())				
				.add(Projections.max(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.VERSAO.toString()), ProtocoloVO.Fields.VERSAO.toString())
				.add(Projections.groupProperty(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString()), ProtocoloVO.Fields.IND_SITUACAO.toString())
				.add(Projections.groupProperty(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString()), ProtocoloVO.Fields.SEQ_VERSAO_PROTOCOLO_SESSAO.toString())
				.add(Projections.groupProperty(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.DIAS_TRATAMENTO.toString()), ProtocoloVO.Fields.DIAS_TRATAMENTO.toString())
			);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloVO.class));
				
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);			
	}
	
	/**
	 * Conta registros de pesquisar protocolos inativos.
	 * @param filtro
	 * @return quantidade
	 */
	public Long contarPesquisarProtocolosInativos(ProtocoloVO filtro) {
		DetachedCriteria criteria = montarCriteriaPesquisarProtocolosInativos(filtro);
		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa versões de protocolo.
	 * @param seqProtocoloSessao
	 * @return versões de protocolo
	 */
	public List<MptVersaoProtocoloSessao> pesquisarVersoesProtocolo(Integer seqProtocoloSessao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class);
		
		criteria.add(Restrictions.eq(MptVersaoProtocoloSessao.Fields.SEQ_PROTOCOLO_SESSAO.toString(), seqProtocoloSessao));
		
		return executeCriteria(criteria);		
	}

	public MptVersaoProtocoloSessao verificaSituacaoProtocoloPorSeq (Integer seqVersaoProtocoloSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class);
		
		criteria.add(Restrictions.eq(MptVersaoProtocoloSessao.Fields.SEQ.toString(), seqVersaoProtocoloSessao));
		
		return (MptVersaoProtocoloSessao) executeCriteriaUniqueResult(criteria);
		
	}
	
	/**
	 * @author thiago.cortes #44393 C1
	 * @param seqProtocolo
	 * @return
	 */
	public List<SituacaoVersaoProtocoloVO> obterSituacaoVersaoProtocoloSelecionado(Integer seqProtocolo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, "VPS");
		criteria.createAlias("VPS."+MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(),"PSE");
		criteria.createAlias("VPS."+MptVersaoProtocoloSessao.Fields.SERVIDOR.toString(),"RAP");
		criteria.createAlias("RAP."+RapServidores.Fields.PESSOA_FISICA.toString(),"RPF");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PSE."+MptProtocoloSessao.Fields.SEQ.toString()),
						SituacaoVersaoProtocoloVO.Fields.SEQ.toString())
				.add(Projections.property("PSE."+MptProtocoloSessao.Fields.TITULO.toString()),
						SituacaoVersaoProtocoloVO.Fields.TITULO_PROTOCOLO.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.CRIADO_EM.toString()),
						SituacaoVersaoProtocoloVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.VERSAO.toString()),
						SituacaoVersaoProtocoloVO.Fields.VERSAO.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString()),
						SituacaoVersaoProtocoloVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.SEQ.toString()),
						SituacaoVersaoProtocoloVO.Fields.SEQ_VERSAO_PROTOCOLO_SESSAO.toString())
				.add(Projections.property("RPF."+RapPessoasFisicas.Fields.NOME.toString()),
						SituacaoVersaoProtocoloVO.Fields.NOME.toString())
				);
		
		criteria.add(Restrictions.eq("PSE." + MptProtocoloSessao.Fields.SEQ.toString(), seqProtocolo));
		criteria.addOrder(Order.desc("VPS."+MptVersaoProtocoloSessao.Fields.VERSAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(SituacaoVersaoProtocoloVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * @author thiago.cortes #44393 C2
	 * @param seqProtocolo, versao
	 * @return
	 */
	public List<InformacaoVersaoProtocoloVO> obterInformacaoDetalhadaProcolocoVersaoSelecioanda(Integer seqProtocolo, Integer versao){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, "VPS");
		criteria.createAlias("VPS."+MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(),"PSE");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PSE."+MptProtocoloSessao.Fields.SEQ.toString()),
						InformacaoVersaoProtocoloVO.Fields.SEQ.toString())
				.add(Projections.property("PSE."+MptProtocoloSessao.Fields.TITULO.toString()),
						InformacaoVersaoProtocoloVO.Fields.TITULO_PROTOCOLO.toString())
				.add(Projections.property("PSE."+MptProtocoloSessao.Fields.TPS_SEQ.toString()),
						InformacaoVersaoProtocoloVO.Fields.TPS_SEQ.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.VERSAO.toString()),
						InformacaoVersaoProtocoloVO.Fields.VERSAO.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString()),
						InformacaoVersaoProtocoloVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("PSE."+MptProtocoloSessao.Fields.QTD_CICLO.toString()),
						InformacaoVersaoProtocoloVO.Fields.QTD_SEQ.toString())
				);
		
		criteria.add(Restrictions.eq("PSE." + MptProtocoloSessao.Fields.SEQ.toString(), seqProtocolo));
		criteria.add(Restrictions.eq("VPS." + MptVersaoProtocoloSessao.Fields.VERSAO.toString(), versao));
		criteria.addOrder(Order.desc("VPS."+MptVersaoProtocoloSessao.Fields.VERSAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(InformacaoVersaoProtocoloVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * @author thiago.cortes #44393 C4
	 * @param seqProtocolo, tpsSeq
	 * @return
	 */
	public List<ProtocoloVO> obterProtocolosAtivos(Integer seqProtocolo, Integer tpsSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, "VPS");
		criteria.createAlias("VPS."+MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(),"PSE");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PSE."+MptProtocoloSessao.Fields.TITULO.toString()),
						ProtocoloVO.Fields.TITULO_PROTOCOLO_SESSAO.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.SEQ.toString()),
						ProtocoloVO.Fields.SEQ_VERSAO_PROTOCOLO_SESSAO.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.VERSAO.toString()),
						ProtocoloVO.Fields.VERSAO.toString())
				.add(Projections.property("VPS."+MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString()),
						ProtocoloVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("PSE."+MptProtocoloSessao.Fields.SEQ.toString()),
						ProtocoloVO.Fields.SEQ_PROTOCOLO_SESSAO.toString())
				);
		
		criteria.add(Restrictions.ilike("PSE." + MptProtocoloSessao.Fields.TITULO.toString(), String.valueOf(seqProtocolo), MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq("PSE." + MptProtocoloSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		
		/**
			Se situação = Ativos
			VPS.IND_SITUACAO <> 'I'
			Senão VPS.IND_SITUACAO = <Situação>
		 */
		
		criteria.addOrder(Order.asc("PSE."+MptProtocoloSessao.Fields.TITULO.toString()))
				.addOrder(Order.asc("VPS."+MptVersaoProtocoloSessao.Fields.VERSAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProtocoloVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Integer carregarNumeroUltimaVersaoPorProtocoloSeq(Integer seqProtocoloSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class);
		criteria.add(Restrictions.eq(MptVersaoProtocoloSessao.Fields.SEQ_PROTOCOLO_SESSAO.toString(), seqProtocoloSessao));
		criteria.setProjection(Projections.max(MptVersaoProtocoloSessao.Fields.VERSAO.toString()));
		return (Integer)executeCriteriaUniqueResult(criteria);
	}
	
	public NovaVersaoProtocoloVO carregarItensVersaoProtocoloPorVpsSeq(Integer versaoProtocoloSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, ALIAS_VPS);
		criteria.createAlias(ALIAS_VPS + PONTO +MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(), ALIAS_PSE);
		criteria.createAlias(ALIAS_PSE + PONTO +MptProtocoloSessao.Fields.TIPO_SESSAO.toString(), ALIAS_TPS);
		criteria.add(Restrictions.eq(ALIAS_VPS + PONTO +MptVersaoProtocoloSessao.Fields.SEQ.toString(), versaoProtocoloSeq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_PSE + PONTO +MptProtocoloSessao.Fields.SEQ.toString()).as(NovaVersaoProtocoloVO.Fields.SEQ_PROTOCOLO.toString()))
				.add(Projections.property(ALIAS_PSE + PONTO +MptProtocoloSessao.Fields.TPS_SEQ.toString()).as(NovaVersaoProtocoloVO.Fields.TP_SESSAO.toString()))
				.add(Projections.property(ALIAS_PSE + PONTO +MptProtocoloSessao.Fields.TITULO.toString()).as(NovaVersaoProtocoloVO.Fields.TITULO.toString()))
				.add(Projections.property(ALIAS_VPS + PONTO +MptVersaoProtocoloSessao.Fields.QTD_CICLOS.toString()).as(NovaVersaoProtocoloVO.Fields.QTD_CICLOS.toString()))
				.add(Projections.property(ALIAS_VPS + PONTO +MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString()).as(NovaVersaoProtocoloVO.Fields.IND_SITUACAO.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NovaVersaoProtocoloVO.class));
		
		return (NovaVersaoProtocoloVO)executeCriteriaUniqueResult(criteria);
	}
	
	public boolean verificarExistenciaOutraVersaoProtocoloSessao(Integer protocoloSessaoSeq, Short tipoSessaoSeq, Integer vpsSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptVersaoProtocoloSessao.class, ALIAS_VPS);
		criteria.createAlias(ALIAS_VPS + PONTO +MptVersaoProtocoloSessao.Fields.PROTOCOLO_SESSAO.toString(), ALIAS_PSE);
		criteria.add(Restrictions.eq(ALIAS_PSE + PONTO + MptProtocoloSessao.Fields.TPS_SEQ.toString(), tipoSessaoSeq));
		criteria.add(Restrictions.eq(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.SEQ_PROTOCOLO_SESSAO.toString(), protocoloSessaoSeq));
		criteria.add(Restrictions.ne(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.SEQ.toString(), vpsSeq));
		criteria.add(Restrictions.ne(ALIAS_VPS + PONTO + MptVersaoProtocoloSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoProtocolo.I));
		
		return executeCriteriaExists(criteria);
	}
}