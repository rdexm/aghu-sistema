package br.gov.mec.aghu.estoque.dao;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceMotivoProblema;
import br.gov.mec.aghu.model.ScoFornecedor;

/**
 * 
 * @author lalegre
 *
 */
public class SceHistoricoProblemaMaterialDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceHistoricoProblemaMaterial> {

	private static final long serialVersionUID = 6064777822897373624L;

	public SceHistoricoProblemaMaterial obterSceHistoricoProblemaMaterial(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "est_almo", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("est_almo." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "almo", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("est_almo." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "material", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("est_almo." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "forn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.SERVIDOR.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv." + RapServidores.Fields.PESSOA_FISICA.toString(), "serv_pf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv." + RapServidores.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.MOTIVO_PROBLEMA.toString(), "mot_pro", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.FORNECEDOR_ENTREGA.toString(), "for_ent", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.SEQ.toString(), seq));
		return (SceHistoricoProblemaMaterial) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Busca o Documento RCSAF pendente para o Material (  SCEP_MMT_ATU_BLPROBL )
	 * @param matCodigo
	 * @return
	 */
	public SceHistoricoProblemaMaterial obterDocumentoRCSAFPendente(Integer matCodigo,AghParametros parametroRCSAF) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class,"HPM");
		criteria.createAlias("HPM." + SceHistoricoProblemaMaterial.Fields.ENTRADA_SAIDA_SEM_LICIACAO, "ESL", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ESL."+SceEntradaSaidaSemLicitacao.Fields.CODIGO_MATERIAL.toString(), matCodigo));
		criteria.add(Restrictions.eq("ESL."+SceEntradaSaidaSemLicitacao.Fields.TIPO_MOVIMENTO_SEQ.toString(), parametroRCSAF));
		criteria.add(Restrictions.eq("ESL."+SceEntradaSaidaSemLicitacao.Fields.IND_ENCERRADO.toString(), Boolean.FALSE));

		Integer subtr = Integer.valueOf("HPM." + SceHistoricoProblemaMaterial.Fields.QTDE_PROBLEMA.toString()) - Integer.valueOf("HPM."+SceHistoricoProblemaMaterial.Fields.QTDE_DESBLOQUEADA.toString()) - Integer.valueOf("HPM."+SceHistoricoProblemaMaterial.Fields.QTDE_DF.toString()); 
		criteria.add(Restrictions.ge(subtr.toString(),0));

		return (SceHistoricoProblemaMaterial) executeCriteriaUniqueResult(criteria);

	}

	/**
	 * Pesquisa se há material com saldo bloqueado por problema no almoxarifado
	 * @param codigoMaterial
	 * @return
	 */
	public List<SceHistoricoProblemaMaterial> pesquisarMaterialSaldoBloqueadoProblemaAlmoxarifado(Integer codigoMaterial) {

		if (codigoMaterial == null) {
			throw new IllegalArgumentException("Parâmetro codigoMaterial não foi informado.");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class,"HPM");

		criteria.createAlias("HPM." + SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO, "EAL", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));

		List<SceHistoricoProblemaMaterial> lista = executeCriteria(criteria);
		List<SceHistoricoProblemaMaterial> resultado = new LinkedList<SceHistoricoProblemaMaterial>();

		if (lista != null && !lista.isEmpty()) {

			for (SceHistoricoProblemaMaterial historicoProblemaMaterial : lista) {

				final Integer qtdeProblema = historicoProblemaMaterial.getQtdeProblema() == null ? 0 : historicoProblemaMaterial.getQtdeProblema();
				final Integer qtdeDesbloqueada = historicoProblemaMaterial.getQtdeDesbloqueada() == null ? 0 : historicoProblemaMaterial.getQtdeDesbloqueada();
				final Integer qtdeDf = historicoProblemaMaterial.getQtdeDf() == null ? 0 : historicoProblemaMaterial.getQtdeDf();

				final Integer subtracao = qtdeProblema - qtdeDesbloqueada - qtdeDf;

				if (subtracao > 0){
					resultado.add(historicoProblemaMaterial);
				}

			}
		}

		return resultado;
	}

	public SceHistoricoProblemaMaterial obterHistProbMaterialPorEstoqueEsl(Integer vnroDoc, Integer ealSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class,"HPM");
		criteria.createAlias("HPM." + SceHistoricoProblemaMaterial.Fields.ENTRADA_SAIDA_SEM_LICIACAO, "ESL", Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq("ESL." + SceEntradaSaidaSemLicitacao.Fields.SEQ.toString(), vnroDoc));
		criteria.add(Restrictions.eq("HPM." + SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO_SEQ.toString(), ealSeq));

		return (SceHistoricoProblemaMaterial) executeCriteriaUniqueResult(criteria);
	}

	public SceHistoricoProblemaMaterial pesquisarQtdeBloqueadaPorProblema(Integer ealSeq) {

		SceHistoricoProblemaMaterial retorno = null;

		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class,"HPM");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty(SceHistoricoProblemaMaterial.Fields.SEQ.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_PROBLEMA.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_DF.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_DESBLOQUEADA.toString()));

		criteria.setProjection(p);

		criteria.add(Restrictions.eq("HPM." + SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO_SEQ.toString(), ealSeq));
		criteria.add(Restrictions.eq("HPM." + SceHistoricoProblemaMaterial.Fields.IND_EFETIVADO.toString(), Boolean.FALSE));

		List<Object[]> lista = executeCriteria(criteria);
		if(lista != null) {
			for(Object[] obj : lista) {
				retorno = new SceHistoricoProblemaMaterial();
				retorno.setSeq(Integer.valueOf(obj[0].toString()));
				if(obj[1]!=null){
					retorno.setQtdeProblema(Integer.valueOf(obj[1].toString()));
				}
				if(obj[2]!=null){
					retorno.setQtdeDf(Integer.valueOf(obj[2].toString()));
				}
				if(obj[3]!=null){
					retorno.setQtdeDesbloqueada(Integer.valueOf(obj[3].toString()));
				}

			}

		}

		return retorno;
	}


	public SceHistoricoProblemaMaterial pesquisarQtdeBloqueadaPorMaterialFornecedor(Integer matCodigo, Integer numFornecedor) {

		SceHistoricoProblemaMaterial retorno = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class,"HPM");

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty(SceHistoricoProblemaMaterial.Fields.SEQ.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_PROBLEMA.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_DF.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_DESBLOQUEADA.toString()));

		criteria.setProjection(p);

		criteria.createAlias("HPM." + SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO, "EAL", Criteria.LEFT_JOIN);

		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), numFornecedor));

		criteria.add(Restrictions.eq("HPM." + SceHistoricoProblemaMaterial.Fields.IND_EFETIVADO.toString(), Boolean.FALSE));

		List<Object[]> lista = executeCriteria(criteria);
		if(lista != null) {
			for(Object[] obj : lista) {

				retorno = new SceHistoricoProblemaMaterial();
				retorno.setSeq(Integer.valueOf(obj[0].toString()));
				retorno.setQtdeProblema(Integer.valueOf(obj[1].toString()));
				retorno.setQtdeDf(Integer.valueOf(obj[2].toString()));
				retorno.setQtdeDesbloqueada(Integer.valueOf(obj[3].toString()));

			}

		}

		return retorno;
	}

	/**
	 * ORADB FUNCTION CALCULA_QTDE_PROBLEMA
	 * @param estoqueAlmoxSeq
	 * @return
	 * @author bruno.mourao
	 * @changed Fábio Winck - Samurais Porto Alegre
	 * @since 12/11/2011
	 */
	public Integer obterQtdeProblemaPorCodAlmoxarifado(Integer estoqueAlmoxSeq){

		Integer result = 0;

		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class,"HPM");

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_PROBLEMA.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_DF.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_DESBLOQUEADA.toString()));

		criteria.setProjection(p);

		criteria.createAlias("HPM." + SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO, "EAL", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.SEQ.toString(), estoqueAlmoxSeq));
		criteria.add(Restrictions.eq("HPM." + SceHistoricoProblemaMaterial.Fields.IND_EFETIVADO.toString(), false));

		List<Object[]> lista = executeCriteria(criteria);
		if(lista != null) {

			Integer qtde_problema = 0; 
			Integer qtde_df = 0;
			Integer qtde_desbloqueada = 0;

			for (Object[] valores : lista) {
				qtde_problema += (valores[0] == null)?0:Integer.parseInt(valores[0].toString());
				qtde_df += (valores[1] == null)?0:Integer.parseInt(valores[1].toString());
				qtde_desbloqueada += (valores[2] == null)?0:Integer.parseInt(valores[2].toString());

			}
			result = qtde_problema - qtde_df - qtde_desbloqueada;
		}		
		return result;
	}
	
	private Integer calcularQtdeProblema(DetachedCriteria criteria) {
		Integer result = 0;
		List<Object[]> lista = executeCriteria(criteria);
		if(lista != null) {

			Integer qtde_problema = 0; 
			Integer qtde_df = 0;
			Integer qtde_desbloqueada = 0;

			for (Object[] valores : lista) {
				qtde_problema += (valores[0] == null)?0:Integer.parseInt(valores[0].toString());
				qtde_df += (valores[1] == null)?0:Integer.parseInt(valores[1].toString());
				qtde_desbloqueada += (valores[2] == null)?0:Integer.parseInt(valores[2].toString());

			}
			
			result = qtde_problema - qtde_df - qtde_desbloqueada;
		}
		return result;
	}
	
	
	/**
	 * ORADB FUNCTION CALCULA_QTDE_PROBLEMA
	 * @param estoqueAlmoxSeq
	 * @return
	 * @author bruno.mourao
	 * @changed Fábio Winck - Samurais Porto Alegre
	 * @changed amenegotto - TNT
	 * @since 12/11/2011
	 */
	public Integer obterQtdeProblemaPorCodMaterial(Integer codMaterial, Integer ealSeq, Integer seqHistorico){

		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class,"HPM");

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_PROBLEMA.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_DF.toString()));
		p.add(Projections.sum(SceHistoricoProblemaMaterial.Fields.QTDE_DESBLOQUEADA.toString()));

		criteria.setProjection(p);

		criteria.createAlias("HPM." + SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO, "EAL", Criteria.INNER_JOIN);

		if (codMaterial != null) {
			criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codMaterial));
		}
		
		if (ealSeq != null) {
			criteria.add(Restrictions.eq("HPM." + SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO_SEQ.toString(), ealSeq));
		}
		
		if (seqHistorico != null) {
			criteria.add(Restrictions.eq("HPM." + SceHistoricoProblemaMaterial.Fields.SEQ.toString(), seqHistorico));
		}
			
		criteria.add(Restrictions.eq("HPM." + SceHistoricoProblemaMaterial.Fields.IND_EFETIVADO.toString(), false));
			
		return this.calcularQtdeProblema(criteria);
	}


	/**
	 * Pesquisa historico de materiais com problema
	 * @param codigoMaterial
	 * @return
	 */
	public Long pesquisarHistoricosProblemaPorFiltroCount(Integer codigoMaterial, Short almoxSeq, Integer codFornecedor, Short motivoProblema, Integer fornecedorEntrega) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "eal", Criteria.LEFT_JOIN);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.MOTIVO_PROBLEMA.toString(), "mtv", Criteria.LEFT_JOIN);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.FORNECEDOR_ENTREGA.toString(), "frn", Criteria.LEFT_JOIN);

		if(codigoMaterial!=null){
			criteria.add(Restrictions.eq("eal." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		}
		if(almoxSeq!=null){
			criteria.add(Restrictions.eq("eal." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeq));
		}
		if(codFornecedor!=null){
			criteria.add(Restrictions.eq("eal." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), codFornecedor));
		}
		if(motivoProblema!=null){
			criteria.add(Restrictions.eq("mtv." + SceMotivoProblema.Fields.SEQ.toString(), motivoProblema));
		}
		if(fornecedorEntrega!=null){
			criteria.add(Restrictions.eq("frn."+ScoFornecedor.Fields.NUMERO.toString(), fornecedorEntrega));
		}
		return executeCriteriaCount(criteria);
	}


	/**
	 * Pesquisa historico de materiais com problema
	 * @param codigoMaterial
	 * @return
	 */
	public List<SceHistoricoProblemaMaterial> pesquisarHistoricosProblemaPorFiltro(Integer firstResult, Integer maxResult, Integer codigoMaterial, Short almoxSeq, Integer codFornecedor, Short motivoProblema, Integer fornecedorEntrega) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "eal", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("eal." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "eal_alm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("eal." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "eal_mat", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("eal." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "eal_frn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.MOTIVO_PROBLEMA.toString(), "mtv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceHistoricoProblemaMaterial.Fields.FORNECEDOR_ENTREGA.toString(), "frn", JoinType.LEFT_OUTER_JOIN);

		if(codigoMaterial!=null){
			criteria.add(Restrictions.eq("eal." + SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		}

		if(almoxSeq!=null){
			criteria.add(Restrictions.eq("eal." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almoxSeq));
		}

		if(codFornecedor!=null){
			criteria.add(Restrictions.eq("eal." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), codFornecedor));
		}

		if(motivoProblema!=null){
			criteria.add(Restrictions.eq("mtv." + SceMotivoProblema.Fields.SEQ.toString(), motivoProblema));
		}

		if(fornecedorEntrega!=null){
			criteria.add(Restrictions.eq("frn."+ScoFornecedor.Fields.NUMERO.toString(), fornecedorEntrega));
		}

		return executeCriteria(criteria, firstResult, maxResult, "seq", true);
	}
	
	public List<SceHistoricoProblemaMaterial> pesquisarHistoricoProblemaPorFornecedorAlmox(Integer nroFornecedor, Integer ealSeq, Date dtGeracao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class);
		

		if (ealSeq != null) {
			criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO_SEQ.toString(), ealSeq));
		}
		
		if (nroFornecedor != null) {
			criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.FORNECEDOR_NUMERO.toString(), nroFornecedor));
		}
		
		if (dtGeracao != null) {
			SimpleDateFormat dateMask = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			criteria.add(Restrictions.sqlRestriction("TO_CHAR("+"{alias}."+SceHistoricoProblemaMaterial.Fields.DT_GERACAO.name()+",'DD/MM/YYYY HH24:MI') = '"+dateMask.format(dtGeracao)+"'"));
		}
		
		criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.IND_EFETIVADO.toString(), Boolean.FALSE));
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de historico de problemas de material por fornecedor, estoque almoxarifado, quantidade de problema e motivo do problema
	 * @param nroFornecedor
	 * @param ealSeq
	 * @param qtdProblema
	 * @param motivoProblema
	 * @return
	 */
	public List<SceHistoricoProblemaMaterial> pesquisarHistoricoProblemaMaterialPorFornecedorQtdProblema(
			Integer nroFornecedor, Integer ealSeq, Integer qtdProblema, SceMotivoProblema motivoProblema) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoProblemaMaterial.class);
		
		if (ealSeq != null) {
			criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.ESTOQUE_ALMOXARIFADO_SEQ.toString(), ealSeq));
		}
		
		if (nroFornecedor != null) {
			criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.FORNECEDOR_NUMERO.toString(), nroFornecedor));
		}
		
		if (qtdProblema != null) {
			criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.QTDE_PROBLEMA.toString(), qtdProblema));
		}
		
		if (motivoProblema != null) {
			criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.MOTIVO_PROBLEMA.toString(), motivoProblema));
		}
		
		criteria.add(Restrictions.eq(SceHistoricoProblemaMaterial.Fields.IND_EFETIVADO.toString(), Boolean.FALSE));
				
		return executeCriteria(criteria);
	}
}