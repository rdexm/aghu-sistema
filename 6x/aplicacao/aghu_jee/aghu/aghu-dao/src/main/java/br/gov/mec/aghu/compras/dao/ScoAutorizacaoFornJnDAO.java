package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroPesquisaAssinarAFVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfEmpenho;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.suprimentos.vo.RelAutorizacaoFornecimentoSaldoEntregaVO;

/**
 * 
 * @modulo compras
 *
 */
public class ScoAutorizacaoFornJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAutorizacaoFornJn> {
	
	private static final String AFJN2 = "AFJN.";
	private static final String AFJN = "AFJN";
	private static final String FSC = "FSC.";
	private static final long serialVersionUID = -3346456799528460332L;

	public List<RelAutorizacaoFornecimentoSaldoEntregaVO> obterListaAutorizacaoFornecimentoSaldoEntrega(final Integer lctNumero, final Integer sequenciaAlteracao, final Short nroComplemento){
		List<RelAutorizacaoFornecimentoSaldoEntregaVO> lista = null;
		return lista;
	}
	
	public List<ScoAutorizacaoFornJn> pesquisarScoAutorizacaoFornPorNroAF(Integer afnNumero) {
		if(afnNumero == null){
			return null;
		}
		List<DominioAprovadaAutorizacaoForn> listaAprovadas = new ArrayList<DominioAprovadaAutorizacaoForn>();
		listaAprovadas.add(DominioAprovadaAutorizacaoForn.A);
		listaAprovadas.add(DominioAprovadaAutorizacaoForn.C);
		listaAprovadas.add(DominioAprovadaAutorizacaoForn.E);
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class);
		criteria.add(Restrictions.eq(ScoAutorizacaoFornJn.Fields.NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.in(ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), listaAprovadas));
		return this.executeCriteria(criteria);
	}
	
	public ScoAutorizacaoFornJn obterScoAutorizacaoFornJnPorAFSequencia(Integer afnNumero, Short sequenciaAlteracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class);
		criteria.add(Restrictions.eq(ScoAutorizacaoFornJn.Fields.NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq(ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		return (ScoAutorizacaoFornJn) this.executeCriteriaUniqueResult(criteria);
	}
	
	public ScoAutorizacaoFornJn buscarUltimaScoAutorizacaoFornJnPorNroAF(Integer afnNumero, Short sequenciaAlteracao) {
		if(afnNumero == null){
			return null;
		}
		
		ScoAutorizacaoFornJn autJn = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class);
		criteria.createAlias(ScoAutorizacaoFornJn.Fields.MOTIVO_ALTERACAO.toString(), "MTV", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoAutorizacaoFornJn.Fields.NUMERO.toString(), afnNumero));
		
		if (sequenciaAlteracao != null) {
			criteria.add(Restrictions.eq(ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		} else {
			criteria.addOrder(Order.desc(ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()));	
		}
		
		List<ScoAutorizacaoFornJn> listaAfJn = this.executeCriteria(criteria);
		
		if (listaAfJn != null && !listaAfJn.isEmpty()) {
			autJn = listaAfJn.get(0);
		}
		return autJn;
	}
	
	public List<ScoAutorizacaoFornJn> pesquisarAFsPorLicitacaoComplSeqAlteracao(Integer pacNumero, Short nroComplemento, Short sequenciaAlteracao, int espEmpenho){
		
		String hql ="select jn from  "+ ScoAutorizacaoFornJn.class.getName() + " jn  , "+ScoAfEmpenho.class.getName()+ " afe " 
						+" where jn."+ScoAutorizacaoFornJn.Fields.NUMERO.toString()+"=  afe."+ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString()
						+" and jn."+ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString()+"= :afnNumero  " 
						+" and jn."+ScoAutorizacaoFornJn.Fields.NUM_COMPLEMENTO.toString()+"= :nroComplemento "
						+" and jn." +ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()+ "= :seqAlteracao "
						+" and (afe."+ScoAfEmpenho.Fields.SEQ.toString()+ " in (select max(AFO."+ScoAfEmpenho.Fields.SEQ.toString() +") as y0_ " 
																		+" from "+ScoAfEmpenho.class.getName()+ " AFO "
																		+" where AFO."+ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString()+"=jn."+ScoAutorizacaoFornJn.Fields.NUMERO.toString()
																		+"  and AFO."+ScoAfEmpenho.Fields.ESPECIE.toString() +" =:pEspecie  ) "
						+" or jn."+ScoAutorizacaoFornJn.Fields.NUMERO.toString()+ " not in " +
								"( select EMP."+ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString()+" as y0_ from "
																				+ScoAfEmpenho.class.getName()+ " EMP "
												+" where EMP."+ScoAfEmpenho.Fields.AUTORIZACAO_FORN_NUMERO.toString()+"=jn."+ScoAutorizacaoFornJn.Fields.NUMERO.toString()+
												" and EMP."+ScoAfEmpenho.Fields.ESPECIE.toString() +" = :pEspecie)) " ;
		
		Query createQuery = this.createHibernateQuery(hql);
		
		createQuery.setParameter("pEspecie", espEmpenho);
		createQuery.setParameter("afnNumero", pacNumero);
		createQuery.setParameter("nroComplemento" , nroComplemento);
		createQuery.setParameter("seqAlteracao" , sequenciaAlteracao);
		
		List<ScoAutorizacaoFornJn> list = createQuery.list();
						
		return list;
	}
 	
	public List<PesquisaAutorizacaoFornecimentoVO> pesquisarListaJnAfs(final FiltroPesquisaAssinarAFVO filtroPesquisaAssinarAFVO) {
		/*
		 * SELECT NUMERO, PFR_LCT_NUMERO, NRO_COMPLEMENTO, SEQUENCIA_ALTERACAO,
		 * DT_ALTERACAO, IND_SITUACAO, DT_GERACAO, MAA_CODIGO, DT_PREV_ENTREGA,
		 * PFR_FRN_NUMERO, SER_VIN_CODIGO_GESTOR, SER_MATRICULA_GESTOR,
		 * DT_VENCTO_CONTRATO, NTD_GND_CODIGO, 'N' AFP FROM SCO_AF_JN
		 */
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, "AFJN");
		criteria.createAlias("AFJN."+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("AFJN."+ScoAutorizacaoFornJn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROP."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.ID.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.SEQ_AF_JN.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.NUMERO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.AFN_NUMERO.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.NUM_COMPLEMENTO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.NUMERO_COMPLEMENTO.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.SEQUENCIA_ALTERACAO.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.OBSERVACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.OBSERVACAO.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.NRO_CONTRATO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.NUMERO_CONTRATO.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.DT_ALTERACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.DT_ALTERACAO.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.SITUACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.SITUACAO_AF.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.DT_PREV_ENTREGA.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.DT_PREV_ENTREGA.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.VENCIMENTO_CONTRATO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.DT_VENCIMENTO_CONTRATO.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.DT_GERACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.DT_GERACAO.toString());			
		p.add(Projections.property("PROP." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.LCT_NUMERO.toString());
		p.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.RAZAO_SOCIAL.toString());
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.SITUACAO_AFJN_AF.toString());
		p.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.SEQUENCIA_ALTERACAO_AF.toString());
		p.add(Projections.property("AFJN." + ScoAutorizacaoFornJn.Fields.PROPOSTA_FORNECEDOR.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.PROPOSTA_FORNECEDOR.toString());
		p.add(Projections.property("PROP." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString()), PesquisaAutorizacaoFornecimentoVO.Fields.FORNECEDOR.toString());
		
		
		p.add(Projections.sqlProjection(" {alias}.CDP_NUMERO as " + PesquisaAutorizacaoFornecimentoVO.Fields.CDP_NUMERO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.CDP_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" {alias}.NTD_GND_CODIGO as " + PesquisaAutorizacaoFornecimentoVO.Fields.CODIGO_GRUPO_NATUREZA.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.CODIGO_GRUPO_NATUREZA.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" {alias}.SER_MATRICULA_GESTOR as " + PesquisaAutorizacaoFornecimentoVO.Fields.MATRICULA_GESTOR.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.MATRICULA_GESTOR.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" {alias}.SER_VIN_CODIGO_GESTOR as " + PesquisaAutorizacaoFornecimentoVO.Fields.VINCULO_SERVIDOR_GESTOR.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VINCULO_SERVIDOR_GESTOR.toString()}, new Type[]{ShortType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT rap1.usuario FROM rap_servidores rap1 where matricula = {alias}.SER_MATRICULA_GESTOR and vin_codigo = {alias}.SER_VIN_CODIGO_GESTOR) as " + PesquisaAutorizacaoFornecimentoVO.Fields.LOGIN_GESTOR.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.LOGIN_GESTOR.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT nome FROM rap_servidores rap2, AGH.RAP_PESSOAS_FISICAS pes where rap2.pes_codigo = pes.codigo AND rap2.matricula = {alias}.SER_MATRICULA_GESTOR and rap2.vin_codigo = {alias}.SER_VIN_CODIGO_GESTOR) as " + PesquisaAutorizacaoFornecimentoVO.Fields.NOME_GESTOR.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.NOME_GESTOR.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT descricao FROM AGH.SCO_MOTIVOS_ALTERACAO_AF where codigo = {alias}.maa_codigo) as " + PesquisaAutorizacaoFornecimentoVO.Fields.DESCRICAO_MOTIVO_ALTERACAO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.DESCRICAO_MOTIVO_ALTERACAO.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT MIN(FSC2.TIPO) FROM AGH.SCO_FASES_SOLICITACOES FSC2 WHERE FSC2.IAF_AFN_NUMERO = {alias}.NUMERO) as " + PesquisaAutorizacaoFornecimentoVO.Fields.TIPO_SOLICITACAO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.TIPO_SOLICITACAO.toString()}, new Type[]{StringType.INSTANCE}));		
		p.add(Projections.sqlProjection(" (SELECT COUNT(scocumxpro0_.seq) FROM AGH.SCO_CUM_X_PROGR_ENTREGAS scocumxpro0_, AGH.SCO_PROGR_ENTREGA_ITENS_AF scoprogent1_ WHERE scocumxpro0_.PEA_IAF_AFN_NUMERO=scoprogent1_.IAF_AFN_NUMERO and scocumxpro0_.PEA_IAF_NUMERO=scoprogent1_.IAF_NUMERO AND SCOCUMXPRO0_.PEA_PARCELA=SCOPROGENT1_.PARCELA AND scoprogent1_.IAF_AFN_NUMERO = {alias}.NUMERO) as " + PesquisaAutorizacaoFornecimentoVO.Fields.QTD_CUM.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.QTD_CUM.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COALESCE(SUM(fso2.VALOR_ORCADO),0) FROM AGH.FSO_PREVISOES_ORCAMENTARIAS fso2 inner join AGH.FSO_EXERCICIOS_ORCAMENTARIOS tab1x1_ on fso2.EXO_EXERCICIO=tab1x1_.EXERCICIO WHERE TAB1X1_.IND_EXERCICIO_CORRENTE='S' and fso2.GND_CODIGO={alias}.NTD_GND_CODIGO   ) as " + PesquisaAutorizacaoFornecimentoVO.Fields.VLR_ORCAMENTO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VLR_ORCAMENTO.toString()}, new Type[]{BigDecimalType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COALESCE(SUM(fso3.VALOR_COMPROMETIDO),0) FROM AGH.FSO_PREVISOES_ORCAMENTARIAS fso3 inner join AGH.FSO_EXERCICIOS_ORCAMENTARIOS TAB1X1_ on fso3.EXO_EXERCICIO=tab1x1_.EXERCICIO WHERE TAB1X1_.IND_EXERCICIO_CORRENTE='S' and fso3.GND_CODIGO={alias}.NTD_GND_CODIGO ) as " + PesquisaAutorizacaoFornecimentoVO.Fields.VLR_COMPROMETIDO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VLR_COMPROMETIDO.toString()}, new Type[]{BigDecimalType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COALESCE(CGC, CPF) from AGH.SCO_FORNECEDORES WHERE NUMERO = {alias}.PFR_FRN_NUMERO) as " + PesquisaAutorizacaoFornecimentoVO.Fields.CNPJ_CPF_FORN.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.CNPJ_CPF_FORN.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT COUNT(nr_contrato) FROM AGH.SCO_CONTRATOS WHERE nr_contrato = {alias}.nro_contrato) as " + PesquisaAutorizacaoFornecimentoVO.Fields.EXISTE_CONTRATO_SICON.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.EXISTE_CONTRATO_SICON.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT SUM( case when AFJN2.SEQUENCIA_ALTERACAO = 0 then 0 else  (case when FSC.TIPO = 'C' then ( COALESCE(IAFJ.QTDE_SOLICITADA, 0) - (case  when AFJN2.SEQUENCIA_ALTERACAO = 0 then 0  else COALESCE(IAFJA.QTDE_SOLICITADA , 0)  end) ) * COALESCE(IAFJ.VALOR_UNITARIO , 0) else (COALESCE(IAFJ.VALOR_UNITARIO, 0) - (case when AFJN2.SEQUENCIA_ALTERACAO = 0 then 0 else COALESCE(IAFJA.VALOR_UNITARIO,  0)  end) ) end ) end ) as soma  FROM agh.SCO_FASES_SOLICITACOES FSC LEFT JOIN agh.sco_iaf_jn IAFJ ON (FSC.IAF_AFN_NUMERO = IAFJ.AFN_NUMERO AND FSC.IAF_NUMERO = IAFJ.NUMERO ) LEFT JOIN agh.sco_af_jn AFJN2 ON ( IAFJ.AFN_NUMERO = AFJN2.NUMERO ) INNER JOIN agh.sco_iaf_jn IAFJA ON (IAFJA.AFN_NUMERO = AFJN2.NUMERO AND IAFJA.NUMERO = IAFJ.NUMERO ) WHERE AFJN2.NUMERO = {alias}.numero AND AFJN2.SEQUENCIA_ALTERACAO = {alias}.SEQUENCIA_ALTERACAO AND IAFJ.IND_EXCLUSAO = 'N' AND IAFJ.SEQUENCIA_ALTERACAO = AFJN2.SEQUENCIA_ALTERACAO AND IAFJA.SEQUENCIA_ALTERACAO <= AFJN2.SEQUENCIA_ALTERACAO AND IAFJA.SEQUENCIA_ALTERACAO IN ( SELECT MAX(iafjn4.SEQUENCIA_ALTERACAO) FROM agh.sco_iaf_jn iafjn4 WHERE iafjn4.AFN_NUMERO = IAFJ.AFN_NUMERO AND iafjn4.NUMERO = IAFJ.NUMERO AND ( ( iafjn4.SEQUENCIA_ALTERACAO < AFJN2.SEQUENCIA_ALTERACAO ) OR ( iafjn4.SEQUENCIA_ALTERACAO = AFJN2.SEQUENCIA_ALTERACAO AND AFJN2.SEQUENCIA_ALTERACAO = 0 )))) as " + PesquisaAutorizacaoFornecimentoVO.Fields.VALOR_REFORCO.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VALOR_REFORCO.toString()}, new Type[]{DoubleType.INSTANCE}));
		p.add(Projections.sqlProjection(" (SELECT SUM(( CASE when FSC3.TIPO = 'C' then ((IAFJN3.QTDE_SOLICITADA - COALESCE(IAFJN3.QTDE_RECEBIDA, 0)) * IAFJN3.VALOR_UNITARIO) + COALESCE(IAFJN3.VALOR_EFETIVADO, 0) else IAFJN3.VALOR_UNITARIO end)) as soma FROM agh.SCO_FASES_SOLICITACOES FSC3, agh.sco_iaf_jn IAFJN3 WHERE IAFJN3.AFN_NUMERO = {alias}.numero AND IAFJN3.IND_SITUACAO <> 'EX' AND IAFJN3.SEQUENCIA_ALTERACAO IN (SELECT MAX(iafjn5.SEQUENCIA_ALTERACAO) FROM agh.SCO_IAF_JN iafjn5 WHERE iafjn5.AFN_NUMERO = IAFJN3.AFN_NUMERO AND iafjn5.NUMERO = IAFJN3.NUMERO AND iafjn5.SEQUENCIA_ALTERACAO <= {alias}.sequencia_alteracao ) AND FSC3.IAF_AFN_NUMERO = IAFJN3.AFN_NUMERO AND FSC3.IAF_NUMERO = IAFJN3.NUMERO ) as " + PesquisaAutorizacaoFornecimentoVO.Fields.VALOR_TOTAL.toString(), new String[]{PesquisaAutorizacaoFornecimentoVO.Fields.VALOR_TOTAL.toString()}, new Type[]{DoubleType.INSTANCE}));
		
		criteria.setProjection(p);
		
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisaAutorizacaoFornecimentoVO.class));		
		
		criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), DominioAprovadaAutorizacaoForn.C));
		if (filtroPesquisaAssinarAFVO != null) {
			if (filtroPesquisaAssinarAFVO.getNumeroAf() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), filtroPesquisaAssinarAFVO.getNumeroAf()));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroComplemento() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.NUM_COMPLEMENTO.toString(), filtroPesquisaAssinarAFVO.getNumeroComplemento()));
			}
			if (filtroPesquisaAssinarAFVO.getIndContrato() != null && filtroPesquisaAssinarAFVO.getIndContrato()) {
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
				subCriteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), filtroPesquisaAssinarAFVO.getIndContrato()));
				subCriteria.setProjection(Projections.property("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
				criteria.add(Subqueries.propertyIn("AFJN."+ScoAutorizacaoFornJn.Fields.NUMERO.toString(), subCriteria));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroContrato() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.NRO_CONTRATO.toString(), filtroPesquisaAssinarAFVO.getNumeroContrato()));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroFornecedor() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.PFR_FRN_NUMERO.toString(), filtroPesquisaAssinarAFVO.getNumeroFornecedor()));
			}
			if (filtroPesquisaAssinarAFVO.getVinCodigoGestor() != null && filtroPesquisaAssinarAFVO.getMatriculaGestor() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.SER_VIN_CODIGO_GESTOR.toString(), filtroPesquisaAssinarAFVO.getVinCodigoGestor()));
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.SER_MATRICULA_GESTOR.toString(), filtroPesquisaAssinarAFVO.getMatriculaGestor()));
			}
			if (StringUtils.isNotEmpty(filtroPesquisaAssinarAFVO.getCodigoModalidadeCompra())) {
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
				subCriteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(),
						filtroPesquisaAssinarAFVO.getCodigoModalidadeCompra()));
				subCriteria.setProjection(Projections.property("LCT."+ScoLicitacao.Fields.NUMERO.toString()));
				criteria.add(Subqueries.propertyIn("AFJN."+ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), subCriteria));
			}
			if (filtroPesquisaAssinarAFVO.getTipoCompra() != null) {
				this.addSubCriteriaTipoCompra(criteria, filtroPesquisaAssinarAFVO);
			}
		}
		criteria.addOrder(Order.desc(ScoAutorizacaoFornJn.Fields.DT_ALTERACAO.toString()));
		criteria.addOrder(Order.desc(ScoAutorizacaoFornJn.Fields.NUMERO.toString()));
		return this.executeCriteria(criteria, true);
	}

	public Long pesquisarListaJnAfsCount(final FiltroPesquisaAssinarAFVO filtroPesquisaAssinarAFVO) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, "AFJN");
		criteria.createAlias("AFJN."+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("AFJN."+ScoAutorizacaoFornJn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.count("AFJN." + ScoAutorizacaoFornJn.Fields.NUMERO.toString()));
				
		criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), DominioAprovadaAutorizacaoForn.C));
		if (filtroPesquisaAssinarAFVO != null) {
			if (filtroPesquisaAssinarAFVO.getNumeroAf() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), filtroPesquisaAssinarAFVO.getNumeroAf()));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroComplemento() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.NUM_COMPLEMENTO.toString(), filtroPesquisaAssinarAFVO.getNumeroComplemento()));
			}
			if (filtroPesquisaAssinarAFVO.getIndContrato() != null && filtroPesquisaAssinarAFVO.getIndContrato()) {
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
				subCriteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), filtroPesquisaAssinarAFVO.getIndContrato()));
				subCriteria.setProjection(Projections.property("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
				criteria.add(Subqueries.propertyIn("AFJN."+ScoAutorizacaoFornJn.Fields.NUMERO.toString(), subCriteria));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroContrato() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.NRO_CONTRATO.toString(), filtroPesquisaAssinarAFVO.getNumeroContrato()));
			}
			if (filtroPesquisaAssinarAFVO.getNumeroFornecedor() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.PFR_FRN_NUMERO.toString(), filtroPesquisaAssinarAFVO.getNumeroFornecedor()));
			}
			if (filtroPesquisaAssinarAFVO.getVinCodigoGestor() != null && filtroPesquisaAssinarAFVO.getMatriculaGestor() != null) {
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.SER_VIN_CODIGO_GESTOR.toString(), filtroPesquisaAssinarAFVO.getVinCodigoGestor()));
				criteria.add(Restrictions.eq("AFJN."+ScoAutorizacaoFornJn.Fields.SER_MATRICULA_GESTOR.toString(), filtroPesquisaAssinarAFVO.getMatriculaGestor()));
			}
			if (StringUtils.isNotEmpty(filtroPesquisaAssinarAFVO.getCodigoModalidadeCompra())) {
				final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
				subCriteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString(),
						filtroPesquisaAssinarAFVO.getCodigoModalidadeCompra()));
				subCriteria.setProjection(Projections.property("LCT."+ScoLicitacao.Fields.NUMERO.toString()));
				criteria.add(Subqueries.propertyIn("AFJN."+ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), subCriteria));
			}
			if (filtroPesquisaAssinarAFVO.getTipoCompra() != null) {
				this.addSubCriteriaTipoCompra(criteria, filtroPesquisaAssinarAFVO);
			}
		}
		return (Long) this.executeCriteriaUniqueResult(criteria);
	}

	private void addSubCriteriaTipoCompra(final DetachedCriteria criteria, final FiltroPesquisaAssinarAFVO filtroPesquisaAssinarAFVO) {
		switch (filtroPesquisaAssinarAFVO.getTipoCompra()) {

		case M: {
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subCriteria.createAlias(FSC + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
			if (filtroPesquisaAssinarAFVO.getCodigoGrupoMaterial() != null) {
				subCriteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
			}

			subCriteria.add(Restrictions.isNotNull(FSC + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			if (filtroPesquisaAssinarAFVO.getCodigoGrupoMaterial() != null) {
				subCriteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(),
						filtroPesquisaAssinarAFVO.getCodigoGrupoMaterial()));
			}
			this.addFiltroNatureza(subCriteria, filtroPesquisaAssinarAFVO.getCodigoGrupoNaturezaDespesa(), filtroPesquisaAssinarAFVO.getCodigoNaturezaDespesa(), filtroPesquisaAssinarAFVO.getSeqVerbaGestao(), "SLC");
			subCriteria.setProjection(Projections.property(FSC + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			criteria.add(Subqueries.propertyIn("AFJN."+ScoAutorizacaoFornJn.Fields.NUMERO.toString(), subCriteria));
			break;
		}
		case S: {
			final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
			subCriteria.createAlias(FSC + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS");
			if (filtroPesquisaAssinarAFVO.getCodigoGrupoServico() != null) {
				subCriteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SVR");
			}
			subCriteria.add(Restrictions.isNotNull(FSC + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			if (filtroPesquisaAssinarAFVO.getCodigoGrupoServico() != null) {
				subCriteria.add(Restrictions.eq("SVR." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString(), filtroPesquisaAssinarAFVO.getCodigoGrupoServico()));
			}
			this.addFiltroNatureza(subCriteria, filtroPesquisaAssinarAFVO.getCodigoGrupoNaturezaDespesa(), filtroPesquisaAssinarAFVO.getCodigoNaturezaDespesa(), filtroPesquisaAssinarAFVO.getSeqVerbaGestao(), "SLS");
			subCriteria.setProjection(Projections.property(FSC + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
			criteria.add(Subqueries.propertyIn("AFJN."+ScoAutorizacaoFornJn.Fields.NUMERO.toString(), subCriteria));
			break;
		}
		default:
			break;
		}
	}

	public Short obterMaxSequenciaAlteracaoAfJn(Integer numAf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class,AFJN);
		criteria.createAlias(AFJN2+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString(), "AF");
		criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), numAf));
		criteria.setProjection(Projections.max(AFJN2+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public Short obterMaxSequenciaAlteracaoAnteriorAfJn(Integer numAf, Short sequenciaAlteracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class,AFJN);
		criteria.createAlias(AFJN2+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString(), "AF");
		criteria.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.NUMERO.toString(), Integer.valueOf(numAf)));
		criteria.add(Restrictions.lt(AFJN2+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		criteria.setProjection(Projections.max(AFJN2+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	private void addFiltroNatureza(final DetachedCriteria subCriteria, final Integer codigoGrupoNaturezaDespesa, final Byte codigoNaturezaDespesa, final Integer seqVerbaGestao,
			String prefixo) {
		if (codigoGrupoNaturezaDespesa != null) {
			subCriteria.add(Restrictions.eq(prefixo + "." + ScoSolicitacaoServico.Fields.NTD_GND_CODIGO.toString(), codigoGrupoNaturezaDespesa));
		}
		if (codigoNaturezaDespesa != null) {
			subCriteria.add(Restrictions.eq(prefixo + "." + ScoSolicitacaoDeCompra.Fields.NTD_CODIGO.toString(),
					codigoNaturezaDespesa));
		}
		if (seqVerbaGestao != null) {
			subCriteria.add(Restrictions.eq(prefixo + "." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString(), seqVerbaGestao));
		}
	}
	
	/**
	 * Obtem versões anteriores de uma AF.
	 * 
	 * @param numPac Número do PAC
	 * @param nroComplemento Número do Complemento
	 * @param first 
	 * @param max 
	 * @return Versões Anteriores
	 */
	public List<ScoAutorizacaoFornJn> buscarAutFornJNPorNumPacNumCompl(
			Integer numPac, Short nroComplemento, int first, int max) {
		DetachedCriteria criteria = obterCriteriaBuscaAutFornJNPorNumPacNumCompl(
				numPac, nroComplemento);
		
		String order = ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString();
		
		return executeCriteria(criteria, first, max, order, false);
	}
	
	/**
	 * Conta versões anteriores de uma AF.
	 * 
	 * @param numPac Número do PAC
	 * @param nroComplemento Número do Complemento
	 * @return Versões Anteriores
	 */
	public Long contarAutFornJNPorNumPacNumCompl(Integer numPac, Short nroComplemento) {
		return executeCriteriaCount(obterCriteriaBuscaAutFornJNPorNumPacNumCompl(numPac, nroComplemento));
	}
	
	/**
	 * Obtem criteria de consulta às versões anteriores de uma AF.
	 * 
	 * @param numPac Número do PAC
	 * @param nroComplemento Número do Complemento
	 * @return Versões Anteriores
	 */
	private DetachedCriteria obterCriteriaBuscaAutFornJNPorNumPacNumCompl(Integer numPac, Short nroComplemento) {
		final String SER = "SER", PES = "PES", MAA = "MAA";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, AFJN);
		
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR_CONTROLADO.toString(), SER, JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias(SER + "." + RapServidores.Fields.PESSOA_FISICA.toString(), PES, JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.MOTIVO_ALTERACAO.toString(), MAA, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), numPac));		
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.NUM_COMPLEMENTO.toString(), nroComplemento));
		
		return criteria;
	}

	/**
	 * Obtem versão de uma AF.
	 * 
	 * @param numeroAf Número da AF
	 * @param complementoAf Complemento da AF
	 * @param sequenciaAlteracao Seq. Alteração
	 * @return Versão
	 */
	public ScoAutorizacaoFornJn obterScoAutorizacaoFornJn(
			Integer numeroAf, Short complementoAf, Short sequenciaAlteracao) {
		final String AF = "AF", PRF = "PRF", LCT = "LCT", MLC = "MLC",
				FRN = "FRN", SGR = "SGR", GRP = "GRP", SGT = "SGT", GTP = "GTP", 
				MAA = "MAA", CDP = "CDP", MDA = "MDA", VBG = "VBG", 
				NTD = "NTD", GND = "GND";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, AFJN);
		
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN.toString(), AF);
		criteria.createAlias(AF + "." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), PRF);
		criteria.createAlias(PRF + "." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), LCT);
		criteria.createAlias(LCT + "." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), MLC);
		criteria.createAlias(PRF + "." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), FRN);
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR.toString(), SGR, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SGR + "." + RapServidores.Fields.PESSOA_FISICA.toString(), GRP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR_GESTOR.toString(), SGT, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SGT + "." + RapServidores.Fields.PESSOA_FISICA.toString(), GTP, JoinType.LEFT_OUTER_JOIN);	
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.MOTIVO_ALTERACAO.toString(), MAA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.CONDICAO_PAGAMENTO.toString(), CDP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.MOEDA.toString(), MDA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.VERBA_GESTAO.toString(), VBG, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.NATUREZA_DESPESA.toString(), NTD, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AFJN + "." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), GND, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.NUM_COMPLEMENTO.toString(), complementoAf));
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		
		return (ScoAutorizacaoFornJn) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem responsáveis a partir da versão de uma AF.
	 * 
	 * @param numeroAf Número da AF
	 * @param complementoAf Complemento da AF
	 * @param sequenciaAlteracao Seq. Alteração
	 * @return Responsáveis
	 */
	public ScoAutorizacaoFornJn obterResponsaveisAutorizacaoFornJn(
			Integer numeroAf, Short complementoAf, Short sequenciaAlteracao) {
		final String SGR = "SGR", GRP = "GRP",  
				SCT = "SCT", SCP = "SCP", SCE = "SCE", SEP = "SEP",
				SES = "SES", SSP = "SSP", SAS = "SAS", SAP = "SAP",
				SAU = "SAU", STP = "STP";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, AFJN);
		
		// Geração
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR.toString(), SGR, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SGR + "." + RapServidores.Fields.PESSOA_FISICA.toString(), GRP, JoinType.LEFT_OUTER_JOIN);
		
		// Alteração
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR_CONTROLADO.toString(), SCT, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SCT + "." + RapServidores.Fields.PESSOA_FISICA.toString(), SCP, JoinType.LEFT_OUTER_JOIN);
		
		// Exclusão
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR_EXCLUIDO.toString(), SCE, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SCE + "." + RapServidores.Fields.PESSOA_FISICA.toString(), SEP, JoinType.LEFT_OUTER_JOIN);
		
		// Estorno
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR_ESTORNO.toString(), SES, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SES + "." + RapServidores.Fields.PESSOA_FISICA.toString(), SSP, JoinType.LEFT_OUTER_JOIN);
		
		// Assinatura
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR_ASSINATURA.toString(), SAS, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SAS + "." + RapServidores.Fields.PESSOA_FISICA.toString(), SAP, JoinType.LEFT_OUTER_JOIN);
		
		// Chefia Compras
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.SERVIDOR_AUTORIZADO.toString(), SAU, Criteria.LEFT_JOIN);
		criteria.createAlias(SAU + "." + RapServidores.Fields.PESSOA_FISICA.toString(), STP, Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.NUM_COMPLEMENTO.toString(), complementoAf));
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		
		return (ScoAutorizacaoFornJn) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem condição de pagamento a partir da versão de uma AF.
	 * 
	 * @param numeroAf Número da AF
	 * @param complementoAf Complemento da AF
	 * @param sequenciaAlteracao Seq. Alteração
	 * @return Condição de pagamento.
	 */
	public ScoCondicaoPagamentoPropos obterCondPgtoAutorizacaoFornJn(
			Integer numeroAf, Short complementoAf, Short sequenciaAlteracao) {
		final String CDP = "CDP";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, AFJN);
		
		criteria.createAlias(AFJN + "." + ScoAutorizacaoFornJn.Fields.CONDICAO_PAGAMENTO.toString(), CDP, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString(), numeroAf));
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.NUM_COMPLEMENTO.toString(), complementoAf));
		criteria.add(Restrictions.eq(AFJN + "." + ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), sequenciaAlteracao));
		
		ScoAutorizacaoFornJn jn = (ScoAutorizacaoFornJn) executeCriteriaUniqueResult(criteria);
		return jn.getCondicaoPagamentoPropos();
	}
	
	public Boolean verificarAutorizacaoFornecimentoJnPendenteAssinatura(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, AFJN);
		
		criteria.add(Restrictions.eq(ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq(ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), DominioAprovadaAutorizacaoForn.N));
		
		return executeCriteriaCount(criteria) > 1;		
	}
	
	public List<ScoAutorizacaoFornJn> pesquisarAutorizacaoFornecimentoJnPendenteAssinatura(Integer afnNumero, Boolean sequenciaAnterior) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class, AFJN);
		
		criteria.add(Restrictions.eq(AFJN2+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));
				
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoAutorizacaoFornJn.class,"AFJN1");		
		subQuery.setProjection(Projections.max("AFJN1."+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()));
		subQuery.add(Restrictions.eq("AFJN1."+ScoAutorizacaoFornJn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));		
		
		if (!sequenciaAnterior) {
			criteria.add(Subqueries.propertyIn(AFJN2+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), subQuery));
			
			List<DominioAprovadaAutorizacaoForn> listaFiltroDominioAprovada = new ArrayList<DominioAprovadaAutorizacaoForn>();						
			listaFiltroDominioAprovada.add(DominioAprovadaAutorizacaoForn.C);
			listaFiltroDominioAprovada.add(DominioAprovadaAutorizacaoForn.A);
			listaFiltroDominioAprovada.add(DominioAprovadaAutorizacaoForn.E);
			criteria.add(Restrictions.not(Restrictions.in(ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), listaFiltroDominioAprovada)));
		} else {
			criteria.add(Subqueries.propertyNotIn(AFJN2+ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString(), subQuery));
			criteria.add(Restrictions.eq(ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString(), DominioAprovadaAutorizacaoForn.N));			
		}
		return executeCriteria(criteria);		
	}
}
	