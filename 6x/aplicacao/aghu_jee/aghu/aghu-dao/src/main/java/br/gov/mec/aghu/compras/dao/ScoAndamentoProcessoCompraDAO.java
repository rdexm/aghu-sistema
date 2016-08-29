package br.gov.mec.aghu.compras.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.compras.pac.vo.RelatorioApVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraDataVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraVO;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.suprimentos.vo.ScoLocalizacaoProcessoComprasVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoAndamentoProcessoCompraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAndamentoProcessoCompra>{

	private static final long serialVersionUID = -4896194344759000477L;

	public Long obterQuantidadeRegistrosPeloCodigoLocProc(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAndamentoProcessoCompra.class);
		
		criteria.add(Restrictions.eq(ScoAndamentoProcessoCompra.Fields.CODIGO_LOCALIZACAO.toString(), codigo));
		
		return executeCriteriaCount(criteria);
	}

	/**
	 * Conta itens do PAC de uma liticação.
	 * 
	 * @param numero Número da licitação.
	 * @return Número de itens do PAC.
	 */
	public Long contarPacLicitacao(Integer numero) {
		DetachedCriteria criteria = getPacLicitacaoCriteria(numero);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Pesquisa itens do PAC de uma licitação.
	 * 
	 * @param numero Número da licitação.
	 * @param first Primeiro registro.
	 * @param max Número máximo de registros.
	 * @param order Ordenamento.
	 * @param asc Direção do ordenamento.
	 * @return Itens do PAC.
	 */
	public List<ScoAndamentoProcessoCompra> pesquisarPacLicitacao(
			Integer numero, Integer first, Integer max, String order,
			boolean asc) {
		DetachedCriteria criteria = getPacLicitacaoCriteria(numero);
		
		criteria.addOrder(Order.desc(path(criteria.getAlias(), 
				ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA)));
		
		criteria.addOrder(Order.desc(path(criteria.getAlias(), 
				ScoAndamentoProcessoCompra.Fields.DATA_SAIDA)));
		
		return executeCriteria(criteria, first, max, order, asc);
	}

	/**
	 * Busca lista de locais de um PAC
	 * 
	 * @param numeroPAC
	 * @return
	 */
	public List<ScoAndamentoProcessoCompra> pesquisarPacLicitacao(Integer numeroPAC) {
		DetachedCriteria criteria = getPacLicitacaoCriteria(numeroPAC);
		
		criteria.addOrder(Order.desc(path(criteria.getAlias(), 
				ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA)));
		
		criteria.addOrder(Order.desc(path(criteria.getAlias(), 
				ScoAndamentoProcessoCompra.Fields.DATA_SAIDA)));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Obtem item do PAC sem data de saída.
	 * 
	 * @param numero Número da licitação.
	 * @return Item do PAC
	 */
	public ScoAndamentoProcessoCompra obterAndamentoSemDataSaida(Integer numero) {
		final String AP = "AP", LC = "LC";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAndamentoProcessoCompra.class, AP);
		
		criteria.createAlias(path(AP, ScoAndamentoProcessoCompra.Fields.LICITACAO), LC);
		
		criteria.add(Restrictions.eq(path(LC, ScoLicitacao.Fields.NUMERO), numero));
		criteria.add(Restrictions.isNull(path(AP, ScoAndamentoProcessoCompra.Fields.DATA_SAIDA)));
		
		return (ScoAndamentoProcessoCompra) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem último item do PAC.
	 * 
	 * @param numero Número da licitação.
	 */
	public ScoAndamentoProcessoCompra obterUltimoAndamento(Integer numero) {
		final String AP = "AP", LC = "LC";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAndamentoProcessoCompra.class, AP);
		
		criteria.createAlias(path(AP, ScoAndamentoProcessoCompra.Fields.LICITACAO), LC);
		
		criteria.add(Restrictions.eq(path(LC, ScoLicitacao.Fields.NUMERO), numero));
		criteria.addOrder(Order.desc(path(AP, ScoAndamentoProcessoCompra.Fields.SEQ)));
		
		List<ScoAndamentoProcessoCompra> result = executeCriteria(criteria);
		return result.isEmpty() ? null : result.get(0);
	}

	private DetachedCriteria getPacLicitacaoCriteria(Integer numero) {
		final String LC = "LC", ML = "ML", AP = "AP", LP = "LP", PS = "PS",
				SR = "SR", PF = "PF", PR = "PR";		
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAndamentoProcessoCompra.class, AP);
		
		criteria.createAlias(path(AP, ScoAndamentoProcessoCompra.Fields.LICITACAO), LC);
		criteria.createAlias(path(LC, ScoLicitacao.Fields.MODALIDADE_LICITACAO), ML);
		criteria.createAlias(path(AP, ScoAndamentoProcessoCompra.Fields.LOCALIZACAO), LP);
		criteria.createAlias(path(AP, ScoAndamentoProcessoCompra.Fields.SERVIDOR), PS);
		criteria.createAlias(path(PS, RapServidores.Fields.PESSOA_FISICA), PF);
		criteria.createAlias(path(LP, ScoLocalizacaoProcesso.Fields.SERVIDOR_RESPONSAVEL), SR, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(path(SR, RapServidores.Fields.PESSOA_FISICA), PR, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(path(LC, ScoLicitacao.Fields.NUMERO), numero));
		
		return criteria;
	}
	
	public List<ScoLocalizacaoProcessoComprasVO> pesquisarLocalizacaoProcessoCompra(Integer first, Integer max, String order, 
			boolean asc, Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento, 
			ScoModalidadeLicitacao modalidadeCompra, Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel){
		String APC = "APC";
		DetachedCriteria criteria = createPesquisarLocalizacaoProcessoCompra(first, max,  order, asc, protocolo, local, nroPac, complemento, modalidadeCompra,
		nroAF,dtEntrada, servidorResponsavel);
		criteria.addOrder(Order.asc(path(APC, ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA)));
		return executeCriteria(criteria, first, max, order, asc);
	}
	
	public Long pesquisarLocalizacaoProcessoCompraCount(Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento,
			ScoModalidadeLicitacao modalidadeCompra, Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel){
		DetachedCriteria criteria = createPesquisarLocalizacaoProcessoCompra(0, null,  null, Boolean.FALSE, protocolo, local, nroPac, complemento, modalidadeCompra,
		nroAF,dtEntrada, servidorResponsavel);
		return executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria createPesquisarLocalizacaoProcessoCompra(Integer first, Integer max, String order, 
			boolean asc, Integer protocolo, ScoLocalizacaoProcesso local, Integer nroPac, Short complemento, ScoModalidadeLicitacao modalidadeCompra,
			Integer nroAF, Date dtEntrada, RapServidores servidorResponsavel){
	
		final String APC = "APC", AFN = "AFN", LIC = "LIC", MLI = "MLI",  LCP = "LCP",
				PS="PS", PF = "PF", RAM ="RAM";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAndamentoProcessoCompra.class, APC);
		criteria.createAlias(path(APC, ScoAndamentoProcessoCompra.Fields.AUTORIZACAO), AFN,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(path(APC, ScoAndamentoProcessoCompra.Fields.LICITACAO), LIC, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(path(LIC, ScoLicitacao.Fields.MODALIDADE_LICITACAO), MLI, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(path(APC, ScoAndamentoProcessoCompra.Fields.LOCALIZACAO), LCP);
		criteria.createAlias(path(APC, ScoAndamentoProcessoCompra.Fields.SERVIDOR), PS);
		criteria.createAlias(path(PS, RapServidores.Fields.PESSOA_FISICA), PF);
		criteria.createAlias(path(LCP, ScoLocalizacaoProcesso.Fields.RAMAL), RAM,JoinType.LEFT_OUTER_JOIN);
		
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property(path(APC, ScoAndamentoProcessoCompra.Fields.SEQ)),ScoLocalizacaoProcessoComprasVO.Fields.PROTOCOLO.toString())
				.add(Projections.property(path(LCP, ScoLocalizacaoProcesso.Fields.CODIGO)),ScoLocalizacaoProcessoComprasVO.Fields.LOCALIZACAO_SEQ.toString())
				.add(Projections.property(path(LCP, ScoLocalizacaoProcesso.Fields.DESCRICAO)),ScoLocalizacaoProcessoComprasVO.Fields.LOCALIZACAO_DESC.toString())
				.add(Projections.property(path(LIC, ScoLicitacao.Fields.NUMERO)),ScoLocalizacaoProcessoComprasVO.Fields.NRO_PAC.toString())
				.add(Projections.property(path(LIC, ScoLicitacao.Fields.DESCRICAO)),ScoLocalizacaoProcessoComprasVO.Fields.DESCRICAO.toString())
				.add(Projections.property(path(MLI, ScoModalidadeLicitacao.Fields.CODIGO)),ScoLocalizacaoProcessoComprasVO.Fields.MODALIDADE.toString())
				.add(Projections.property(path(MLI, ScoModalidadeLicitacao.Fields.DESCRICAO)),ScoLocalizacaoProcessoComprasVO.Fields.MODALIDADE_DESC.toString())
				.add(Projections.property(path(AFN, ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO)),ScoLocalizacaoProcessoComprasVO.Fields.NRO_AF.toString())
				.add(Projections.property(path(AFN, ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO)),ScoLocalizacaoProcessoComprasVO.Fields.CP.toString())
				.add(Projections.property(path(AFN, ScoAutorizacaoForn.Fields.SITUACAO)),ScoLocalizacaoProcessoComprasVO.Fields.SITUACAO.toString())
				.add(Projections.property(path(APC, ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA)),ScoLocalizacaoProcessoComprasVO.Fields.DT_ENTRADA.toString())
				.add(Projections.property(path(PF, RapPessoasFisicas.Fields.NOME)),ScoLocalizacaoProcessoComprasVO.Fields.RESPONSAVEL.toString())
				.add(Projections.property(path(RAM, RapRamalTelefonico.Fields.NUMERORAMAL)),ScoLocalizacaoProcessoComprasVO.Fields.RAMAL.toString());
		criteria.setProjection(projection);	
		criteria.setResultTransformer(Transformers.aliasToBean(ScoLocalizacaoProcessoComprasVO.class));
		
		criteria.add(Restrictions.isNull(path(APC, ScoAndamentoProcessoCompra.Fields.DATA_SAIDA)));
		
		
		//aplicar filtro 
		if(protocolo!=null){
			criteria.add(Restrictions.eq(path(APC, ScoAndamentoProcessoCompra.Fields.SEQ),protocolo));
		}
		
		if(local!=null){
			criteria.add(Restrictions.eq(path(APC, ScoAndamentoProcessoCompra.Fields.LOCALIZACAO), local));
		}

		if(nroPac!=null){
			criteria.add(Restrictions.eq(path(LIC, ScoLicitacao.Fields.NUMERO), nroPac));
		}
		
		if(complemento!=null){
			criteria.add(Restrictions.eq(path(AFN, ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO), complemento));
		}
		
		if(modalidadeCompra!=null){
			criteria.add(Restrictions.eq(path(LIC, ScoLicitacao.Fields.MODALIDADE_LICITACAO), modalidadeCompra));
		}
		
		if(nroAF!=null){
			criteria.add(Restrictions.eq(path(AFN, ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO), nroAF));
		}
		
		if(dtEntrada!=null){
			criteria.add(Restrictions.between(path(APC, ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA), DateUtil.obterDataComHoraInical(dtEntrada),DateUtil.obterDataComHoraFinal(dtEntrada)));
		}
		
		if(servidorResponsavel!=null){
			criteria.add(Restrictions.eq(path(APC, ScoAndamentoProcessoCompra.Fields.SERVIDOR), servidorResponsavel));
		}
		
		
		
		return criteria;
	}
	

	private static String path(Object... parts) {
		Iterator<Object> iterator = Arrays.asList(parts).iterator();
		
		StringBuffer path = new StringBuffer(1024)
			.append(iterator.next().toString());

		if (iterator.hasNext()) {
			final String SEPARATOR = ".";
			
			do {
				path.append(SEPARATOR)
					.append(iterator.next().toString());
			} while (iterator.hasNext());
		}

		return path.toString();
	}
	
	public List<RelatorioApVO> buscaLocalizacaoProcessoRelatorioAP(Integer numeroPac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAndamentoProcessoCompra.class, "APC");
		
		criteria.createAlias("APC."+ScoAndamentoProcessoCompra.Fields.LICITACAO.toString(), "LCT");
		criteria.createAlias("APC."+ScoAndamentoProcessoCompra.Fields.LOCALIZACAO.toString(), "LCP");
		
		criteria.setProjection(Projections.projectionList().
				add(Projections.property("LCT."+ScoLicitacao.Fields.NUMERO.toString()), RelatorioApVO.Fields.PROCESSO.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.DT_DIGITACAO.toString()), RelatorioApVO.Fields.GERACAO.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.MLC_CODIGO.toString()), RelatorioApVO.Fields.MODALIDADE.toString()).
				add(Projections.property("LCP."+ScoLocalizacaoProcesso.Fields.DESCRICAO.toString()), RelatorioApVO.Fields.LOCALIZACAO.toString()).
				add(Projections.property("LCP."+ScoLocalizacaoProcesso.Fields.CODIGO.toString()), RelatorioApVO.Fields.CODIGO.toString()).
				add(Projections.property("APC."+ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA.toString()), RelatorioApVO.Fields.DT_ENTRADA.toString()).
				add(Projections.property("APC."+ScoAndamentoProcessoCompra.Fields.DATA_SAIDA.toString()), RelatorioApVO.Fields.DT_SAIDA.toString()));
		
		criteria.add(Restrictions.gt("APC."+ScoAndamentoProcessoCompra.Fields.CODIGO_LOCALIZACAO.toString(), Short.valueOf("1")));
		criteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.NUMERO.toString(), numeroPac));
		criteria.addOrder(Order.asc("APC."+ScoAndamentoProcessoCompra.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioApVO.class));
		
		return executeCriteria(criteria);
	}

	public ScoAndamentoProcessoCompra obterAndamentoPac(Integer seq) {
		ScoAndamentoProcessoCompra andamentoProcessoCompra = this.obterPorChavePrimaria(seq, false, ScoAndamentoProcessoCompra.Fields.SERVIDOR, ScoAndamentoProcessoCompra.Fields.LOCALIZACAO  );
		if(andamentoProcessoCompra.getServidor() != null){
			Hibernate.initialize(andamentoProcessoCompra.getServidor().getPessoaFisica());
		}
		return andamentoProcessoCompra;
	}
	
	public List<ConsultarAndamentoProcessoCompraDataVO> consultarAndamentoProcessoCompra(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, ConsultarAndamentoProcessoCompraVO filtro) {
		
		ConsultarAndamentoProcessoCompraQueryProvider builder = new ConsultarAndamentoProcessoCompraQueryProvider(filtro);

		Query query = createSQLQuery(builder.buildQuery());
				query.setFirstResult(firstResult);
				query.setMaxResults(maxResults);
				return  ((SQLQuery) query).addScalar("pacSeq", IntegerType.INSTANCE).
						addScalar("numeroPac",IntegerType.INSTANCE).
						addScalar("dtEntrada",DateType.INSTANCE).
						addScalar("dtSaida",DateType.INSTANCE).
						addScalar("lcpCodigo",ShortType.INSTANCE).
						setResultTransformer(Transformers.aliasToBean(ConsultarAndamentoProcessoCompraDataVO.class)).list();
	}
			
	public Long consultarAndamentoProcessoCompraCount(ConsultarAndamentoProcessoCompraVO filtro) {
		ConsultarAndamentoProcessoCompraQueryProvider builder = new ConsultarAndamentoProcessoCompraQueryProvider(filtro);
		SQLQuery query = createSQLQuery(builder.buildQueryCount());
		Number count = (Number) query.uniqueResult();
		return count.longValue();
	}
	
			
	// C2 - #22068
	public Object obterLocalidadeAtual(Integer numeroLicitacao){
			
			StringBuilder hql = new StringBuilder(300);
			
			hql.append("SELECT ");
			hql.append("LOC." ).append( ScoLocalizacaoProcesso.Fields.DESCRICAO.toString());
			hql.append(" FROM ");
			hql.append(ScoAndamentoProcessoCompra.class.getSimpleName() ).append( " APC1, ");
			hql.append(ScoLocalizacaoProcesso.class.getSimpleName() ).append( " LOC ");
			hql.append(" WHERE");
			hql.append(" APC1." ).append( ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString() ).append( " = :numeroLicitacao");
			hql.append(" AND APC1." ).append( ScoAndamentoProcessoCompra.Fields.CODIGO_LOCALIZACAO.toString() ).append( " = LOC." ).append( ScoLocalizacaoProcesso.Fields.CODIGO.toString());
			hql.append(" AND APC1." ).append( ScoAndamentoProcessoCompra.Fields.DATA_SAIDA.toString() ).append( " is null");
			
			hql.append(" AND APC1." ).append( ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA.toString() ).append( " = ("); 
											hql.append(" SELECT MAX(APC2." ).append( ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA.toString() ).append( ')');
											hql.append(" FROM " ).append( ScoAndamentoProcessoCompra.class.getSimpleName() ).append( " APC2 ");
											hql.append(" WHERE APC2." ).append( ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString() ).append( " =");
											hql.append(" APC1." ).append( ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString());
											hql.append(" ) ");
			hql.append(" AND APC1." ).append( ScoAndamentoProcessoCompra.Fields.SEQ.toString() ).append( " = (");
											hql.append(" SELECT MAX(APC3." ).append( ScoAndamentoProcessoCompra.Fields.SEQ.toString() ).append( ')');
											hql.append(" FROM " ).append( ScoAndamentoProcessoCompra.class.getSimpleName() ).append( " APC3 ");
											hql.append(" WHERE APC3." ).append( ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString() ).append( " =");
											hql.append(" APC1." ).append( ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString());
											hql.append(" ) ");
											
			org.hibernate.Query query = createHibernateQuery(hql.toString());
			query.setInteger("numeroLicitacao", numeroLicitacao);
			return query.uniqueResult();
		}
		
		public String obterResponsavelPeloProcesso(Integer pacSeq) {
			ConsultarAndamentoProcessoCompraQueryProvider builder = new ConsultarAndamentoProcessoCompraQueryProvider();
			SQLQuery query = createSQLQuery(builder.buildQueryObterResponsavelPeloProcesso(pacSeq));
			return  (String) query.addScalar("nome",StringType.INSTANCE).uniqueResult();
		}
		
		public List<ConsultarAndamentoProcessoCompraDataVO> consultarAndamentoProcessoCompraForCSV(ConsultarAndamentoProcessoCompraVO filtro) {
					ConsultarAndamentoProcessoCompraQueryProvider builder = new ConsultarAndamentoProcessoCompraQueryProvider(filtro);
					Query query = createSQLQuery(builder.buildQuery());
					return  ((SQLQuery) query).addScalar("pacSeq",IntegerType.INSTANCE).
							addScalar("numeroPac",IntegerType.INSTANCE).
							addScalar("dtEntrada",DateType.INSTANCE).
							addScalar("dtSaida",DateType.INSTANCE).
							addScalar("lcpCodigo",ShortType.INSTANCE).
							setResultTransformer(Transformers.aliasToBean(ConsultarAndamentoProcessoCompraDataVO.class)).list();
				}
		
		public Object obterCodigoLocalidadeAtual(Integer numeroLicitacao){
			
			StringBuilder hql = new StringBuilder(300);
			
			hql.append("SELECT ");
			hql.append("LOC.").append(ScoLocalizacaoProcesso.Fields.CODIGO.toString());
			
			hql.append(" FROM ");
			hql.append(ScoAndamentoProcessoCompra.class.getSimpleName()).append(" APC1, ");
			hql.append(ScoLocalizacaoProcesso.class.getSimpleName()).append(" LOC ");
			
			hql.append(" WHERE");
			hql.append(" APC1.").append(ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString()).append(" = :numeroLicitacao");
			hql.append(" AND APC1.").append(ScoAndamentoProcessoCompra.Fields.CODIGO_LOCALIZACAO.toString()).append( " = LOC.").append(ScoLocalizacaoProcesso.Fields.CODIGO.toString());
			hql.append(" AND APC1.").append(ScoAndamentoProcessoCompra.Fields.DATA_SAIDA.toString()).append(" is null");
			
			hql.append(" AND APC1.").append(ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA.toString()).append(" = ("); 
			                                hql.append(" SELECT MAX(APC2." ).append( ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA.toString() ).append( ')');											
											hql.append(" FROM ").append(ScoAndamentoProcessoCompra.class.getSimpleName()).append(" APC2 ");
											hql.append(" WHERE APC2.").append( ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString()).append(" =");
											hql.append(" APC1.").append(ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString());
											hql.append(" ) ");
			hql.append(" AND APC1.").append(ScoAndamentoProcessoCompra.Fields.SEQ.toString()).append(" = (");
											hql.append(" SELECT MAX(APC3.").append(ScoAndamentoProcessoCompra.Fields.SEQ.toString()).append(')');
											hql.append(" FROM ").append(ScoAndamentoProcessoCompra.class.getSimpleName()).append(" APC3 ");
											hql.append(" WHERE APC3.").append(ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString()).append(" =");
											hql.append(" APC1.").append(ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString());
											hql.append(" ) ");
											
			Query query = createHibernateQuery(hql.toString());
			query.setInteger("numeroLicitacao", numeroLicitacao);

			return query.uniqueResult();
		}
		
		public List<RelatorioApVO> listarEntradaSaidaAndamentoProcessoCompras(Integer lctNumero, Short lcpCodigo){
			DetachedCriteria criteria = DetachedCriteria.forClass(ScoAndamentoProcessoCompra.class);
			
			criteria.setProjection(Projections.projectionList().
					add(Projections.property(ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA.toString()), RelatorioApVO.Fields.DT_ENTRADA.toString()).
					add(Projections.property(ScoAndamentoProcessoCompra.Fields.DATA_SAIDA.toString()), RelatorioApVO.Fields.DT_SAIDA.toString()));
			
			criteria.add(Restrictions.eq(ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO.toString(), lctNumero));
			criteria.add(Restrictions.eq(ScoAndamentoProcessoCompra.Fields.CODIGO_LOCALIZACAO.toString(), lcpCodigo));
			criteria.addOrder(Order.desc(ScoAndamentoProcessoCompra.Fields.SEQ.toString()));
			
			criteria.setResultTransformer(Transformers.aliasToBean(RelatorioApVO.class));
			
			return executeCriteria(criteria);
		}		
	
}