/**
 * 
 */
package br.gov.mec.aghu.estoque.dao;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioTransferenciaMaterial;
import br.gov.mec.aghu.estoque.vo.RelatorioTransferenciaMaterialVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @author lessandro.lucas
 * 
 */

public class SceTransferenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceTransferencia> {
	
	/**
	 * Retorna SceTransferencia original
	 * @param elementoModificado
	 * @return
	 */
	/*public SceTransferencia obterOriginal(Integer seq) {

		//Integer id = elementoModificado.getSeq().intValue();

		StringBuilder hql = new StringBuilder();

		hql.append("select o.").append(SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString());

		//hql.append(", o.").append(SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString());
		hql.append(", o.").append(SceTransferencia.Fields.DT_EFETIVACAO.toString());
		hql.append(", o.").append(SceTransferencia.Fields.DT_ESTORNO.toString());
		hql.append(", o.").append(SceTransferencia.Fields.DT_GERACAO.toString());
		hql.append(", o.").append(SceTransferencia.Fields.EFETIVADA.toString());
		hql.append(", o.").append(SceTransferencia.Fields.ESTORNO.toString());
		hql.append(", o.").append(SceTransferencia.Fields.TRANSFERENCIA_AUTOMATICA.toString());
		hql.append(", o.").append(SceTransferencia.Fields.PMT_CCT_CODIGO.toString());
		hql.append(", o.").append(SceTransferencia.Fields.PMT_CCT_CODIGO_REFERE.toString());
		hql.append(", o.").append(SceTransferencia.Fields.PMT_NUMERO.toString());
		//hql.append(", o.").append(SceTransferencia.Fields.ITEM_TRANSFERENCIA.toString());
		hql.append(", o.").append(SceTransferencia.Fields.SCE_ALMOX.toString());
		hql.append(", o.").append(SceTransferencia.Fields.SCE_ALMOX_RECEB.toString());
		hql.append(", o.").append(SceTransferencia.Fields.TIPO_MOVIMENTO.toString());
		hql.append(", o.").append(SceTransferencia.Fields.SERVIDOR.toString());
		hql.append(", o.").append(SceTransferencia.Fields.SERVIDOR_EFETIVADO.toString());
		hql.append(", o.").append(SceTransferencia.Fields.SERVIDOR_ESTORNADO.toString());
		
		hql.append(" from ").append(SceTransferencia.class.getSimpleName()).append(" o ");

		hql.append(" left outer join o.").append(SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString());
		hql.append(" left outer join o.").append(SceTransferencia.Fields.SCE_ALMOX.toString());
		hql.append(" left outer join o.").append(SceTransferencia.Fields.SCE_ALMOX_RECEB.toString());
		hql.append(" left outer join o.").append(SceTransferencia.Fields.TIPO_MOVIMENTO.toString());
		hql.append(" left outer join o.").append(SceTransferencia.Fields.SERVIDOR.toString());
		hql.append(" left outer join o.").append(SceTransferencia.Fields.SERVIDOR_EFETIVADO.toString());
		hql.append(" left outer join o.").append(SceTransferencia.Fields.SERVIDOR_ESTORNADO.toString());
		
		hql.append(" where o.").append(SceTransferencia.Fields.SEQ.toString()).append(" = :entityId ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", seq);

		SceTransferencia original = null;
		@SuppressWarnings("unchecked")
		List<Object[]> camposLst = (List<Object[]>) query.getResultList();

		if(camposLst != null && camposLst.size()>0) {

			Object[] campos = camposLst.get(0);

			original = new SceTransferencia();

			original.setSeq(seq);
			original.setClassifMatNiv5((ScoClassifMatNiv5)campos[0]);
			original.setDtEfetivacao((Date)campos[1]);
			original.setDtEstorno((Date)campos[2]);
			original.setDtGeracao((Date)campos[3]);
			original.setEfetivada((Boolean)campos[4]);
			original.setEstorno((Boolean)campos[5]);
			original.setTransferenciaAutomatica((Boolean)campos[6]);
			original.setPmtCctCodigo((Integer)campos[7]);
			original.setPmtCctCodigoRefere((Integer)campos[8]);
			original.setPmtNumero((Integer)campos[9]);
		//	original.setItensTransferencia((Set<SceItemTransferencia>)campos[11]);
			original.setAlmoxarifado((SceAlmoxarifado)campos[10]);
			original.setAlmoxarifadoRecebimento((SceAlmoxarifado)campos[11]);
			original.setTipoMovimento((SceTipoMovimento)campos[12]);
			original.setServidor((RapServidores)campos[13]);
			original.setServidorEfetivado((RapServidores)campos[14]);
			original.setServidorEstornado((RapServidores)campos[15]);
			
		}

		return original;

	}*/
	
	
	private static final long serialVersionUID = -9010939160175295923L;



	/**
	 * Obtem a SceTransferencia por seq e almoxarifado
	 * @param material
	 * @return
	 */
	public SceTransferencia obterTransferenciaPorSeqAlmoxarifado(Integer seqTransferencia,  Short seqAlmoxarifado){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class);
		
		criteria.createAlias(SceTransferencia.Fields.SCE_ALMOX.toString(),"ALM", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(SceTransferencia.Fields.SEQ.toString(), seqTransferencia));
		criteria.add(Restrictions.eq("ALM." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifado));
		
		return (SceTransferencia) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Obtém a DetachedCriteria para pesquisa de transferência automática NÃO efetivadas no almoxarifado de destino
	 * @param seqAlmoxarifado
	 * @param seqAlmoxarifadoRecebimento
	 * @param numeroClassifMatNiv5
	 * @return
	 */
	private DetachedCriteria obterCriteriaTransferenciaAutomaticaNaoEfetivadaDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento, Long numeroClassifMatNiv5){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class);
		
		criteria.createAlias(SceTransferencia.Fields.SCE_ALMOX.toString(),"ALM", JoinType.INNER_JOIN);
		criteria.createAlias(SceTransferencia.Fields.SCE_ALMOX_RECEB.toString(),"ALMRECEB", JoinType.INNER_JOIN);
		criteria.createAlias(SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString(),"CLASSIF5", JoinType.INNER_JOIN);
		
		//criteria.add(Restrictions.eq(SceTransferencia.Fields.SEQ.toString(), seqTransferencia));
		criteria.add(Restrictions.eq("ALM." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifado));
		criteria.add(Restrictions.eq("ALMRECEB." + SceAlmoxarifado.Fields.SEQ.toString(), seqAlmoxarifadoRecebimento));
		
		if(numeroClassifMatNiv5 != null){
			criteria.add(Restrictions.eq("CLASSIF5." + ScoClassifMatNiv5.Fields.NUMERO.toString(), numeroClassifMatNiv5));
		}
			
		criteria.add(Restrictions.eq(SceTransferencia.Fields.TRANSFERENCIA_AUTOMATICA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(SceTransferencia.Fields.EFETIVADA.toString(), Boolean.FALSE));
		
		return criteria;
		
	}
	
	/**
	 * Pesquisa transferências automáticas NÃO efetivadas no almoxarifado de destino
	 * @param seqAlmoxarifado
	 * @param seqAlmoxarifadoRecebimento
	 * @param numeroClassifMatNiv5
	 * @return
	 */
	public List<SceTransferencia> pesquisarTransferenciaAutomaticaNaoEfetivadaDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento, Long numeroClassifMatNiv5){
		return executeCriteria(this.obterCriteriaTransferenciaAutomaticaNaoEfetivadaDestino(seqAlmoxarifado, seqAlmoxarifadoRecebimento, numeroClassifMatNiv5));
	}
	
	/**
	 * Verifica a existência de transferências automáticas NÃO efetivadas no almoxarifado de destino
	 * @param seqAlmoxarifado
	 * @param seqAlmoxarifadoRecebimento
	 * @param numeroClassifMatNiv5
	 * @return
	 */
	public Boolean existeTransferenciaAutomaticaNaoEfetivadaDestino(Short seqAlmoxarifado, Short seqAlmoxarifadoRecebimento, Long numeroClassifMatNiv5){
		return executeCriteriaCount(this.obterCriteriaTransferenciaAutomaticaNaoEfetivadaDestino(seqAlmoxarifado, seqAlmoxarifadoRecebimento, numeroClassifMatNiv5)) > 0;
	}

	
	/**
	 * Obtem a SceTransferencia por seq
	 * @param material
	 * @return
	 */
	public SceTransferencia obterTransferenciaPorSeq(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class);
		criteria.add(Restrictions.eq(SceTransferencia.Fields.SEQ.toString(), seq));
		return (SceTransferencia) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtem a SceTransferencia por seq com situaçao efetivada
	 * @param material
	 * @return
	 */
	public SceTransferencia obterTransferenciaPorSeqEfetivada(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class);
		criteria.add(Restrictions.eq(SceTransferencia.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(SceTransferencia.Fields.EFETIVADA.toString(), Boolean.TRUE));
		return (SceTransferencia) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param seq
	 * @param dtGeracao
	 * @param indTransfAutomatica
	 * @param sceAlmoxarifadoSeq
	 * @param sceAlmoxarifadoRecebSeq
	 * @param numero
	 * @param descricao
	 * @param nome
	 * @return
	 */
	public List<SceTransferencia> pesquisarTransferenciaAutomatica(Integer firstResult, 
															       Integer maxResult, 
															       String orderProperty,
															       boolean asc,
															       Integer seq, 
															       Date dtGeracao,
															       Boolean indTransfAutomatica,
															       Short sceAlmoxarifadoSeq,
															       Short sceAlmoxarifadoRecebSeq, 
															       Long numero,
															       String descricao,
															       String nome,
															       Boolean estorno) {

		DetachedCriteria criteria = obterCriteriaTransferenciaAutomatica(
				seq, dtGeracao, indTransfAutomatica, sceAlmoxarifadoSeq, sceAlmoxarifadoRecebSeq, numero, descricao, nome, estorno);

		if (!StringUtils.isEmpty(orderProperty)) {
			Order order = null;
			if (asc) {
				order = Order.asc(orderProperty);
			} else {
				order = Order.desc(orderProperty);
			}
			criteria.addOrder(order);
		}
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	
	/**
	 * 
	 * @param seq
	 * @param dtGeracao
	 * @param indTransfAutomatica
	 * @param sceAlmoxarifadoSeq
	 * @param sceAlmoxarifadoRecebSeq
	 * @param numero
	 * @param descricao
	 * @param nome
	 * @param estorno
	 * @return
	 */
	public Long pesquisarTransferenciaAutomaticaCount(Integer seq, 
														 Date dtGeracao,
														 Boolean indTransfAutomatica,
														 Short sceAlmoxarifadoSeq,
														 Short sceAlmoxarifadoRecebSeq, 
														 Long numero,
														 String descricao,
														 String nome, 
														 Boolean estorno) {
		DetachedCriteria criteria = obterCriteriaTransferenciaAutomatica(seq, dtGeracao,indTransfAutomatica, sceAlmoxarifadoSeq,sceAlmoxarifadoRecebSeq,numero,descricao,nome, estorno);
		return this.executeCriteriaCount(criteria);
	}
	
	
	
	/**
	 * 
	 * @param seq
	 * @param dtGeracao
	 * @param indTransfAutomatica
	 * @param sceAlmoxarifadoSeq
	 * @param sceAlmoxarifadoRecebSeq
	 * @param numero
	 * @param descricao
	 * @param nome
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria obterCriteriaTransferenciaAutomatica(Integer seq, 
																  Date dtGeracao,
																  Boolean indTransfAutomatica,
																  Short sceAlmoxarifadoSeq,
																  Short sceAlmoxarifadoRecebSeq, 
																  Long numero,
																  String descricao,
																  String nome,
																  Boolean estorno) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class, "SCE");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("SCE." + SceTransferencia.Fields.SEQ.toString()));
		p.add(Projections.property("SCE." +SceTransferencia.Fields.DT_GERACAO.toString()));
		p.add(Projections.property("SCE." +SceTransferencia.Fields.TRANSFERENCIA_AUTOMATICA.toString()));
		p.add(Projections.property("SCE." +SceTransferencia.Fields.SCE_ALMOX.toString()));
		p.add(Projections.property("SCE." +SceTransferencia.Fields.SCE_ALMOX_RECEB.toString()));
		p.add(Projections.property("SCE." +SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString() +"."+ScoClassifMatNiv5.Fields.DESCRICAO.toString()));
		
		criteria.createAlias("SCE."	+ SceTransferencia.Fields.SCE_ALMOX.toString(),"ALM", JoinType.INNER_JOIN);
		criteria.createAlias("SCE."	+ SceTransferencia.Fields.SCE_ALMOX_RECEB.toString(),"ALM1", JoinType.INNER_JOIN);
		criteria.createAlias("SCE."	+ SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString(),"CN5", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SCE."	+ SceTransferencia.Fields.SERVIDOR.toString(),"SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER."	+ RapServidores.Fields.PESSOA_FISICA.toString(),"PES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("SCE." +SceTransferencia.Fields.EFETIVADA.toString(), Boolean.FALSE));
		
		if(estorno == Boolean.TRUE) {
			criteria.add(Restrictions.eq("SCE." +SceTransferencia.Fields.ESTORNO.toString(), Boolean.TRUE));
		} else {
			criteria.add(Restrictions.eq("SCE." +SceTransferencia.Fields.ESTORNO.toString(), Boolean.FALSE));
		}
		
		if(seq != null){
			criteria.add(Restrictions.eq("SCE." + SceTransferencia.Fields.SEQ.toString(), seq));			
		}
		
		if(dtGeracao!=null){
			Calendar dtGeracaoInicio = Calendar.getInstance();
			dtGeracaoInicio.setTime(dtGeracao);
			dtGeracaoInicio.add(Calendar.HOUR,0);
			dtGeracaoInicio.add(Calendar.MINUTE,0);
			dtGeracaoInicio.add(Calendar.SECOND,0);
			
			Calendar dtGeracaoFim = Calendar.getInstance();
			dtGeracaoFim.setTime(dtGeracao);
			dtGeracaoFim.add(Calendar.HOUR,23);
			dtGeracaoFim.add(Calendar.MINUTE,59);
			dtGeracaoFim.add(Calendar.SECOND,59);
			criteria.add(Restrictions.between("SCE."+ SceTransferencia.Fields.DT_GERACAO.toString(), dtGeracaoInicio.getTime(), dtGeracaoFim.getTime()));
		}
		
		if (indTransfAutomatica != null) {
			criteria.add(Restrictions.eq("SCE."+ SceTransferencia.Fields.TRANSFERENCIA_AUTOMATICA.toString(), indTransfAutomatica));
		}
		
		if(sceAlmoxarifadoSeq != null){
			criteria.add(Restrictions.eq(
					"SCE." + SceTransferencia.Fields.SCE_ALMOX.toString()+
					"."+SceAlmoxarifado.Fields.SEQ.toString(), sceAlmoxarifadoSeq));
		}
		if(sceAlmoxarifadoRecebSeq != null){
			criteria.add(Restrictions.eq(
					"SCE." + SceTransferencia.Fields.SCE_ALMOX_RECEB.toString()+
					"."+SceAlmoxarifado.Fields.SEQ.toString(), sceAlmoxarifadoRecebSeq));
		}
		if(numero!= null){
			criteria.add(Restrictions.eq(
					"SCE." + SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString()+
					"."+ScoClassifMatNiv5.Fields.NUMERO.toString(), numero));
		}
		if(descricao!= null){
			criteria.add(Restrictions.eq(
					"SCE." + SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString()+
					"."+ScoClassifMatNiv5.Fields.DESCRICAO.toString(), descricao));
		}
		if(nome!= null){
			criteria.add(Restrictions.eq(
					"VPS1." +RapServidores .Fields.NOME_PESSOA_FISICA.toString(),nome));
		}
		return criteria;
	}
	
	/**
	 * Obtem a criteria necessária para a consulta da estória
	 * @param material
	 * @return
	 */
	public DetachedCriteria getCriteriaPesquisarTransferenciaAutoAlmoxarifado(SceTransferencia transferencia){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class);
		
		// Número (Seq)
		if(transferencia.getSeq() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SEQ.toString(), transferencia.getSeq()));
		}
		
		// Classificação do material
		if(transferencia.getClassifMatNiv5() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString(), transferencia.getClassifMatNiv5()));
		}		
		
		// Almoxariado de origem
		if(transferencia.getAlmoxarifado() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SCE_ALMOX.toString(), transferencia.getAlmoxarifado()));
		}		
		
		// Almoxariado de destino
		if(transferencia.getAlmoxarifadoRecebimento() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SCE_ALMOX_RECEB.toString(), transferencia.getAlmoxarifadoRecebimento()));
		}	

		// Restrições padrão
		criteria.add(Restrictions.eq(SceTransferencia.Fields.EFETIVADA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(SceTransferencia.Fields.TRANSFERENCIA_AUTOMATICA.toString(), Boolean.TRUE));
		
		return criteria;
		
	}
	
	/**
	 * Obtem a criteria necessária para a consulta da estória
	 * @param material
	 * @return
	 */
	public DetachedCriteria getCriteriaPesquisarEstornoTransferenciaAutoAlmoxarifado(SceTransferencia transferencia, Boolean ordena){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class);
		
		// Número (Seq)
		if (transferencia.getSeq() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SEQ.toString(), transferencia.getSeq()));
		}
		
		// Classificação do material
		if (transferencia.getClassifMatNiv5() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString(), transferencia.getClassifMatNiv5()));
		}		
		
		// Almoxariado de origem
		if (transferencia.getAlmoxarifado() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SCE_ALMOX.toString(), transferencia.getAlmoxarifado()));
		}		
		
		// Almoxariado de destino
		if (transferencia.getAlmoxarifadoRecebimento() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SCE_ALMOX_RECEB.toString(), transferencia.getAlmoxarifadoRecebimento()));
		}	

		// Restrições padrão
		criteria.add(Restrictions.eq(SceTransferencia.Fields.EFETIVADA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(SceTransferencia.Fields.ESTORNO.toString(), Boolean.FALSE));
		
		/*if (ordena) {
			
			criteria.addOrder(Order.asc(SceTransferencia.Fields.DT_GERACAO.toString()));
			criteria.addOrder(Order.asc(SceTransferencia.Fields.SEQ.toString()));
			
		}*/
		
		
		return criteria;
		
	}
	
	
	public Long pesquisarTransferenciaAutoAlmoxarifadoCount(SceTransferencia transferencia){
		DetachedCriteria criteria = this.getCriteriaPesquisarTransferenciaAutoAlmoxarifado(transferencia);
		return this.executeCriteriaCount(criteria);
	}
	
	public Long pesquisarEstornoTransferenciaAutoAlmoxarifadoCount(SceTransferencia transferencia){
		DetachedCriteria criteria = this.getCriteriaPesquisarEstornoTransferenciaAutoAlmoxarifado(transferencia, false);
		return this.executeCriteriaCount(criteria);
	}

	public List<SceTransferencia> pesquisarTransferenciaAutoAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SceTransferencia transferencia){
		DetachedCriteria criteria = this.getCriteriaPesquisarTransferenciaAutoAlmoxarifado(transferencia);
		criteria.createAlias(SceTransferencia.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		
		return this.executeCriteria(criteria, firstResult, maxResult, SceTransferencia.Fields.DT_GERACAO.toString(), asc);
	}
	
	public List<SceTransferencia> pesquisarEstornoTransferenciaAutoAlmoxarifado(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SceTransferencia transferencia){
		DetachedCriteria criteria = this.getCriteriaPesquisarEstornoTransferenciaAutoAlmoxarifado(transferencia, true);
		
		criteria.createAlias(SceTransferencia.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(SceTransferencia.Fields.SERVIDOR_EFETIVADO.toString(), "SERV_EF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_EF."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF_EF", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(SceTransferencia.Fields.SERVIDOR_ESTORNADO.toString(), "SERV_ES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_ES."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF_ES", JoinType.LEFT_OUTER_JOIN);
		
		return this.executeCriteria(criteria, firstResult, maxResult, SceTransferencia.Fields.DT_GERACAO.toString(), false);
	}
	
	/**
	 * Busca o objeto pelo seu id.
	 * @param {Integer} seq
	 * @return {SceTransferencia}
	 */
	public SceTransferencia obterTransferenciaPorId(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class);
		criteria.createAlias(SceTransferencia.Fields.TIPO_MOVIMENTO.toString(), "TM", JoinType.INNER_JOIN);
		
		criteria.createAlias(SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString(), "CMN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceTransferencia.Fields.SCE_ALMOX.toString(), "AM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceTransferencia.Fields.SCE_ALMOX_RECEB.toString(), "AMR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceTransferencia.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.INNER_JOIN);
		

		criteria.createAlias(SceTransferencia.Fields.SERVIDOR_EFETIVADO.toString(), "SERV_EF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_EF."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF_EF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(SceTransferencia.Fields.SEQ.toString(), seq));
		// #33263
		List<SceTransferencia> lista = executeCriteria(criteria);
		
		if(lista != null && !lista.isEmpty()){
			return lista.get(0);
		}
		return null; 
	}
	
	/**
	 * Efetua a pesquisa para obter os dados do relatório transferencia de  material
	 * @param numTransferenciaMaterial
	 * @param campoOrdenacao
	 * @return
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<RelatorioTransferenciaMaterialVO>  pesquisarDadosRelatorioTransferenciaMaterialItens(Integer numTransferenciaMaterial, 
			DominioOrdenacaoRelatorioTransferenciaMaterial campoOrdenacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class, "TRF");

		//PROJECTIONS
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.SEQ.toString()),RelatorioTransferenciaMaterialVO.Fields.NUM_TRANFERENCIA_MATERIAL.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.TRANSFERENCIA_AUTOMATICA.toString()),RelatorioTransferenciaMaterialVO.Fields.IND_TRANF_AUTOMATICA.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.DT_GERACAO.toString()),RelatorioTransferenciaMaterialVO.Fields.DT_GERACAO.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.EFETIVADA.toString()),RelatorioTransferenciaMaterialVO.Fields.IND_EFETIVADA.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.DT_EFETIVACAO.toString()),RelatorioTransferenciaMaterialVO.Fields.DT_EFETIVACAO.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.ESTORNO.toString()),RelatorioTransferenciaMaterialVO.Fields.IND_ESTORNO.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.DT_ESTORNO.toString()),RelatorioTransferenciaMaterialVO.Fields.DT_ESTORNO.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.SCE_ALMOX.toString()+"."+SceAlmoxarifado.Fields.SEQ.toString()),RelatorioTransferenciaMaterialVO.Fields.ALMOX_ORIGEM.toString());
		p.add(Projections.property("ALM."  + SceAlmoxarifado.Fields.DESCRICAO.toString()),RelatorioTransferenciaMaterialVO.Fields.ALMOX_ORIGEM_DESCRICAO.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.SCE_ALMOX_RECEB.toString()+"."+SceAlmoxarifado.Fields.SEQ.toString()),RelatorioTransferenciaMaterialVO.Fields.ALMOX_DESTINO.toString());
		p.add(Projections.property("ALM1." + SceAlmoxarifado.Fields.DESCRICAO.toString()),RelatorioTransferenciaMaterialVO.Fields.ALMOX_DESTINO_DESCRICAO.toString());
		p.add(Projections.property("TRF."  + SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString()),RelatorioTransferenciaMaterialVO.Fields.CN5.toString());
		// V_SCO_CLAS_MATERIAL é de view 
		p.add(Projections.property("CN5."  + ScoClassifMatNiv5.Fields.DESCRICAO.toString()),RelatorioTransferenciaMaterialVO.Fields.CN5_DESCRICAO.toString());
		p.add(Projections.property("EAL."  + SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString()),RelatorioTransferenciaMaterialVO.Fields.MAT_CODIGO.toString());
		p.add(Projections.property("MAT."  + ScoMaterial.Fields.NOME.toString()),RelatorioTransferenciaMaterialVO.Fields.NOME.toString());
		p.add(Projections.property("EAL."  + SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString()),RelatorioTransferenciaMaterialVO.Fields.UMD_CODIGO.toString());
		p.add(Projections.property("UND."  + ScoUnidadeMedida.Fields.DESCRICAO.toString()),RelatorioTransferenciaMaterialVO.Fields.UMD_DESCRICAO.toString());
		// cálculo realizado na VO
		p.add(Projections.property("ALM1." + SceAlmoxarifado.Fields.DIAS_ESTOQUE_MINIMO.toString()),RelatorioTransferenciaMaterialVO.Fields.DIAS_ESTOQUE_MINIMO.toString());
		p.add(Projections.property("EAL."  + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_EST_MIN.toString()),RelatorioTransferenciaMaterialVO.Fields.QTD_ESTOQ_MIN.toString());
		p.add(Projections.property("EAL."  + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString()),
				RelatorioTransferenciaMaterialVO.Fields.QTD_DISPONIVEL_DESTINO.toString());
		p.add(Projections.property("EAL."  + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_BLOQUEADA.toString()),RelatorioTransferenciaMaterialVO.Fields.QTD_BLOQUEADA.toString());
		p.add(Projections.property("EAL1." + SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString()),
				RelatorioTransferenciaMaterialVO.Fields.QTD_DISPONIVEL_ORIGEM.toString());
		p.add(Projections.property("EAL1." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()),
				RelatorioTransferenciaMaterialVO.Fields.ENDERECO_ORIGEM.toString());
		p.add(Projections.property("EAL2." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString()),
				RelatorioTransferenciaMaterialVO.Fields.ENDERECO_DESTINO.toString());
		p.add(Projections.property("ITR."  + SceItemTransferencia.Fields.QTDE_ENVIADA.toString()),RelatorioTransferenciaMaterialVO.Fields.QTDE_ENVIADA.toString());

		String nomeCampoDiasEstoqueMinimo = CoreUtil.obterNomeCampoAtributo(SceAlmoxarifado.class,SceAlmoxarifado.Fields.DIAS_ESTOQUE_MINIMO.toString());
		String nomeCampoQtdeEstoqueMinimo = CoreUtil.obterNomeCampoAtributo(SceEstoqueAlmoxarifado.class,SceEstoqueAlmoxarifado.Fields.QUANTIDADE_EST_MIN.toString());
		String nomeCampoQtdeDisponivel = CoreUtil.obterNomeCampoAtributo(SceEstoqueAlmoxarifado.class,SceEstoqueAlmoxarifado.Fields.QUANTIDADE_DISPONIVEL.toString());
		String nomeCampoQtdeBloqueada = CoreUtil.obterNomeCampoAtributo(SceEstoqueAlmoxarifado.class,SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString());
		String nomeCampoQtdeBloqueadaEntrTransf = CoreUtil.obterNomeCampoAtributo(SceEstoqueAlmoxarifado.class,SceEstoqueAlmoxarifado.Fields.QTDE_BLOQ_ENTR_TRANSF.toString());

		/*,DECODE(NVL(ALM1.DIAS_ESTQ_MINIMO,0),0,EAL.QTDE_ESTQ_MIN,(EAL.QTDE_ESTQ_MIN - (EAL.QTDE_DISPONIVEL + EAL.QTDE_BLOQUEADA + EAL.QTDE_BLOQ_ENTR_TRANSF))) TRNSF_DEST*/
		String sql = "(CASE WHEN coalesce(" + "alm1x2_." + nomeCampoDiasEstoqueMinimo 
		+ ",0) = 0 THEN "  
		+ "eal4_." + nomeCampoQtdeEstoqueMinimo 
		+ " ELSE " 
		+ "eal4_." + nomeCampoQtdeEstoqueMinimo
		+ " - ("
		+ "eal4_." + nomeCampoQtdeDisponivel
		+ " + "
		+ "eal4_." + nomeCampoQtdeBloqueada
		+ " + "
		+ "eal4_." + nomeCampoQtdeBloqueadaEntrTransf
		+ ") END ) as transferenciaDestino";
		p.add(Projections.sqlProjection(sql, new String[]{"transferenciaDestino"}, new Type[]{IntegerType.INSTANCE}));

		// CARREGAR CAMPOS DESCRIÇÃO 
		p.add(Projections.property("CN5." + ScoClassifMatNiv5.Fields.DESCRICAO.toString()),RelatorioTransferenciaMaterialVO.Fields.CN5_DESCRICAO.toString());
		p.add(Projections.property("CN5." + ScoClassifMatNiv5.Fields.CODIGO.toString()),RelatorioTransferenciaMaterialVO.Fields.CN5_CODIGO.toString());

		criteria.setProjection(p);
		
		// JOINS
		criteria.createAlias("TRF." + SceTransferencia.Fields.SCE_ALMOX.toString(), "ALM", JoinType.INNER_JOIN);
		criteria.createAlias("TRF." + SceTransferencia.Fields.SCE_ALMOX_RECEB.toString(), "ALM1", JoinType.INNER_JOIN);
		criteria.createAlias("TRF." + SceTransferencia.Fields.ITEM_TRANSFERENCIA.toString(), "ITR", JoinType.INNER_JOIN);
		criteria.createAlias("ITR." + SceItemTransferencia.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL, "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA, "UND", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR, "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("TRF." + SceTransferencia.Fields.CLASSIF_MAT_NIV5.toString(), "CN5", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ALM." + SceAlmoxarifado.Fields.SCE_ESTOQUE_ALMOXARIFADO, "EAL1", JoinType.INNER_JOIN);
		criteria.createAlias("ALM1." + SceAlmoxarifado.Fields.SCE_ESTOQUE_ALMOXARIFADO, "EAL2", JoinType.INNER_JOIN);

		//RESTRICTIONS
		criteria.add(Restrictions.eq("TRF." +SceTransferencia.Fields.SEQ.toString(),
				numTransferenciaMaterial));		

		criteria.add(Restrictions.eqProperty("EAL1." +SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(),
				"EAL." +SceEstoqueAlmoxarifado.Fields.MATERIAL.toString()));
		criteria.add(Restrictions.eqProperty("EAL1." +SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(),
				"EAL." +SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()));
		criteria.add(Restrictions.eqProperty("EAL2." +SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(),
				"EAL." +SceEstoqueAlmoxarifado.Fields.MATERIAL.toString()));
		criteria.add(Restrictions.eqProperty("EAL2." +SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(),
				"EAL." +SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()));
		
		// ORDER BY
		if(campoOrdenacao != null) {
			Order order = null;
			if(DominioOrdenacaoRelatorioTransferenciaMaterial.ENDERECO_ORIGEM.equals(campoOrdenacao)) {
				//order = Order.asc("EAL." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString());
				order = Order.asc("EAL1." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString());
			}
			if(DominioOrdenacaoRelatorioTransferenciaMaterial.CODIGO.equals(campoOrdenacao)) {
				order = Order.asc("MAT." +ScoMaterial.Fields.CODIGO.toString());
			}
			if(DominioOrdenacaoRelatorioTransferenciaMaterial.ENDERECO_DESTINO.equals(campoOrdenacao)) {
				//order = Order.asc("EAL1." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString());
				order = Order.asc("EAL." + SceEstoqueAlmoxarifado.Fields.ENDERECO.toString());
			}
			if(DominioOrdenacaoRelatorioTransferenciaMaterial.DESCRICAO.equals(campoOrdenacao)) {
				order = Order.asc("MAT."  + ScoMaterial.Fields.NOME.toString());	
			}
			criteria.addOrder(order);	
		}
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioTransferenciaMaterialVO.class));
		return executeCriteria(criteria);
	}
	/**
	 * Retorna a quantidade de transferências dependentes do pacote de materiais
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 */
	public Long obterQuantidadeTransferenciasPorCentrosCustosNumeroPacoteMaterial(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacote){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class, "TRS");
		
		criteria.add(Restrictions.eq("TRS." + SceTransferencia.Fields.CENTRO_CUSTO_PROPRIETARIO_PACOTE.toString(),
				codigoCentroCustoProprietario));
		
		criteria.add(Restrictions.eq("TRS." + SceTransferencia.Fields.CENTRO_CUSTO_APLICACAO_PACOTE.toString(),
				codigoCentroCustoAplicacao));
		
		criteria.add(Restrictions.eq("TRS." + SceTransferencia.Fields.NUMERO_PACOTE.toString(),
				numeroPacote));
		
		return executeCriteriaCount(criteria);
	}
	
	
	
	public Long pesquisarTransferenciaMateriaisEventualCount(SceTransferencia transferencia){
		DetachedCriteria criteria = this.montaCriteriaTransferenciaMateriaisEventual(transferencia);
		return this.executeCriteriaCount(criteria);
	}

	public List<SceTransferencia> pesquisarTransferenciaMateriaisEventual(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SceTransferencia transferencia){
		DetachedCriteria criteria = this.montaCriteriaTransferenciaMateriaisEventual(transferencia);
		criteria.createAlias(SceTransferencia.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(SceTransferencia.Fields.SERVIDOR_EFETIVADO.toString(), "SERV_EF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_EF."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF_EF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(SceTransferencia.Fields.SERVIDOR_ESTORNADO.toString(), "SERV_ES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_ES."+RapServidores.Fields.PESSOA_FISICA.toString(), "PF_ES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc(SceTransferencia.Fields.DT_GERACAO.toString()));
		criteria.addOrder(Order.asc(SceTransferencia.Fields.SEQ.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, null, false);
	}
	
	
	
	public DetachedCriteria montaCriteriaTransferenciaMateriaisEventual(SceTransferencia transferencia){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceTransferencia.class, "TRS");
		
		// Número (Seq)
		if (transferencia.getSeq() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SEQ.toString(), transferencia.getSeq()));
		}
		
		// Almoxariado de origem
		if (transferencia.getAlmoxarifado() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SCE_ALMOX.toString(), transferencia.getAlmoxarifado()));
		}		
		
		// Almoxariado de destino
		if (transferencia.getAlmoxarifadoRecebimento() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.SCE_ALMOX_RECEB.toString(), transferencia.getAlmoxarifadoRecebimento()));
		}	
		
		// Gerado Em
		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
		if (transferencia.getDtGeracao() != null) {
			final String sqlRestrictionToChar = "TO_CHAR(this_.DT_GERACAO,'dd/MM/yyyy') = ?";
			criteria.add(Restrictions.sqlRestriction(sqlRestrictionToChar,formatador.format(transferencia.getDtGeracao()),StringType.INSTANCE));
		}
		
		// Efetivado Em
		if (transferencia.getDtEfetivacao() != null) {
			final String sqlRestrictionToChar = "TO_CHAR(this_.DT_EFETIVACAO,'dd/MM/yyyy') = ?";
			criteria.add(Restrictions.sqlRestriction(sqlRestrictionToChar,formatador.format(transferencia.getDtEfetivacao()),StringType.INSTANCE));
		}
	
		
		// Efetivado 
		if (transferencia.getEfetivada() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.EFETIVADA.toString(), transferencia.getEfetivada()));
		}
	
		// Estornado
		if (transferencia.getEstorno() != null) {
			criteria.add(Restrictions.eq(SceTransferencia.Fields.ESTORNO.toString(), transferencia.getEstorno()));
		}
		
		
		criteria.add(Restrictions.eq(SceTransferencia.Fields.TRANSFERENCIA_AUTOMATICA.toString(), Boolean.FALSE));
		
		

		
		return criteria;
	}
	
	
}
