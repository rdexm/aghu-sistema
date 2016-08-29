package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;

public class SceLoteDocumentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceLoteDocumento> {
	
	private static final long serialVersionUID = -3757950877617354161L;
	
	private static final Log LOG = LogFactory.getLog(SceLoteDocumentoDAO.class);

	@Override
	protected void obterValorSequencialId(SceLoteDocumento elemento) {
		
		if (elemento == null) {			
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		//elemento.setSeq(getNextVal(SequenceID.SCE_LDC_SQ1));		
		
	}
	
	
	/*public SceLoteDocumento obterOriginal(SceLoteDocumento elementoModificado) {
		SceLoteDocumento b = null;
		
		try {
			Field[] fields = elementoModificado.getClass().getDeclaredFields();

			Method[] methods = elementoModificado.getClass().getMethods();
			Method methodId = null;
			for (Method method : methods) {
				Annotation[] annotations = method.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation.annotationType().getName().equals("javax.persistence.Id")) {
						methodId = method;
						break;
					}
				}
			}
			Field fieldId = null;
			for (Field field : fields) {
				if (methodId.getName().toUpperCase().contains(field.getName().toUpperCase())) {
					fieldId = field;
					fieldId.setAccessible(true);
					break;
				}
			}

			StringBuilder hql = new StringBuilder();
			hql.append("select o.");
			int count = 0;
			for (Field field : fields) {
				field.setAccessible(true);
				if(field.getName().toUpperCase().equals("serialVersionUID".toUpperCase())){
					continue;
				}
				if(count == 0){
					hql.append(field.getName());
				}else{
					hql.append(", o."+field.getName());
				}
				count++;
			}
			hql.append(" from ").append(elementoModificado.getClass().getSimpleName()).append(" o ");
			
			for (Field field : fields) {
				field.setAccessible(true);
				if(field.getName().toUpperCase().equals("serialVersionUID".toUpperCase())){
					continue;
				}
				Class<?> typeField = field.getType();
				if (typeField.getName().toUpperCase().contains("br.gov.mec.aghu.model.".toUpperCase())) {
					hql.append(" left outer join o.").append(
							field.getName()).append(' ');
				}
			}

			hql.append(" where o.").append(fieldId.getName())
			.append(" = :entityId ");

			Query query = this.createQuery(hql.toString());

			query.setParameter("entityId", fieldId.get(elementoModificado));

			//SceItemRms original = null;
			List<Object[]> camposLst = (List<Object[]>) query.getResultList();

			b = elementoModificado.getClass().newInstance();

			if (camposLst != null && camposLst.size() > 0) {
				Integer countAux = 0;
				Object[] campos = camposLst.get(0);
				for (Field field : fields) {
					field.setAccessible(true);
					if(field.getName().toUpperCase().equals("serialVersionUID".toUpperCase())){
						continue;
					}
					field.set(b, campos[countAux]);
					countAux++;
				}
			}
			
		} catch(Exception e) {
			super.logError(e.getMessage(), e);
			b = null;
		}
		
		return b;
	}*/

	public Boolean pesquisarLoteDocumentoPorInrNrsSeq(Integer seq) {
		
		Boolean acessaLdcpAtuDelecao = true;
		
		StringBuilder hql = new StringBuilder(150);
		hql.append("select count(*) ")
		.append(" from ").append(SceLoteDocumento.class.getSimpleName()).append(" ldc ,")
		.append(  SceLoteFornecedor.class.getSimpleName()).append(" ltf ")
		.append(" where ldc.").append(SceLoteDocumento.Fields.INR_NRS_SEQ.toString()).append(".seq").append(" = :pSeq ")
		.append("   and ltf.").append(SceLoteFornecedor.Fields.LOT_CODIGO.toString()).append(" = ").append("ldc.").append(SceLoteDocumento.Fields.LOT_CODIGO.toString())
		.append("   and ltf.").append(SceLoteFornecedor.Fields.LOT_MCM_CODIGO.toString()).append(" = ").append("ldc.").append(SceLoteDocumento.Fields.LOT_MCM_CODIGO.toString())
		.append("   and ltf.").append(SceLoteFornecedor.Fields.LOT_MAT_CODIGO.toString()).append(" = ").append("ldc.").append(SceLoteDocumento.Fields.LOT_MAT_CODIGO.toString());
		
		
		LOG.info(hql.toString());
		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("pSeq", seq);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.getResultList();
		
		if (lista != null && !lista.isEmpty()) {
			
			acessaLdcpAtuDelecao = false;
			
		}
		
		return acessaLdcpAtuDelecao;
	
	}

	public List<SceLoteDocumento> pesquisarLoteDocumentoPorNotaItemAutorizacao(
			SceNotaRecebimento notaRecebimento,
			ScoItemAutorizacaoForn itemAutorizacaoForn) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteDocumento.class,"lod");
		
		criteria.add(Restrictions.eq(SceLoteDocumento.Fields.INR_NRS_SEQ.toString(), notaRecebimento.getSeq()));
		criteria.add(Restrictions.eq(SceLoteDocumento.Fields.INR_IAF_AFN_NUMERO.toString(), itemAutorizacaoForn.getAutorizacoesForn().getNumero()));
		criteria.add(Restrictions.eq(SceLoteDocumento.Fields.INR_IAF_NUMERO.toString(), itemAutorizacaoForn.getId().getNumero()));
		
		return executeCriteria(criteria);
	}

	public List<SceLoteDocumento> pesquisarLoteDocumentoPorEstoqueAlmoxarifadoMaterial(Integer estoqueAlmoxarifado, Integer codMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteDocumento.class,"lod");
		
		criteria.createAlias(SceLoteDocumento.Fields.ENTRADA_SAIDA_SEM_LICITACAO.toString(), "es_s_lic", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("es_s_lic." + SceEntradaSaidaSemLicitacao.Fields.MARCA_COMERCIAL.toString(), "esslic_marca", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceLoteDocumento.Fields.ITEM_NOTA_RECEBIMENTO.toString(), "it_nt_rec", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("it_nt_rec." + SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString(), "item_auto_forn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("item_auto_forn." + ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "item_marca_comercial", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(SceLoteDocumento.Fields.LOT_MAT_CODIGO.toString(), codMaterial));
		criteria.add(Restrictions.gt(SceLoteDocumento.Fields.DATA_VALIDADE.toString(), new Date()));
		criteria.add(Restrictions.sqlRestriction("({alias}.inr_nrs_seq is not null or {alias}.esl_seq is not null)"));

		String sql = "exists ( 	select 	1 from agh.sce_validades val " +
				"				where   val.eal_seq = ? " +
				"				and val.data = {alias}.dt_validade" +
				"				and 	coalesce(val.qtde_disponivel,0) > 0)";
		criteria.add(Restrictions.sqlRestriction(sql, new Object[]{estoqueAlmoxarifado}, new Type[]{IntegerType.INSTANCE}));
		
		criteria.addOrder(Order.desc(SceLoteDocumento.Fields.DATA_VALIDADE.toString()));
		return executeCriteria(criteria);
	}
	
	public List<SceLoteDocumento> pesquisarLoteDocumentoPorDalSeqEEalSeq(Integer dalSeq, Integer ealSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteDocumento.class);
		criteria.setFetchMode(SceLoteDocumento.Fields.FORNECEDOR.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(SceLoteDocumento.Fields.IDA_DAL_SEQ.toString(), dalSeq));
		criteria.add(Restrictions.eq(SceLoteDocumento.Fields.IDA_EAL_SEQ.toString(), ealSeq));
		return executeCriteria(criteria);
	}
}