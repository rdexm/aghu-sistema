package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;

public class SceLoteFornecedorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceLoteFornecedor> {
	
	private static final long serialVersionUID = 975251630484898593L;


	@Override
	protected void obterValorSequencialId(SceLoteFornecedor elemento) {
		
		if (elemento == null) {			
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		//elemento.setSeq(getNextVal(SequenceID.SCE_LTF_SQ1));		
		
	}

	public List<SceLoteFornecedor> pesquisarLoteFornecedorPorLotCodigoLotMatCodigoLotMcmCodigo(String lotCodigo,Integer lotMatCodigo,Integer lotMcmCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteFornecedor.class,"lod");
		
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_CODIGO.toString(), lotCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_MAT_CODIGO.toString(), lotMatCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_MCM_CODIGO.toString(), lotMcmCodigo));
		
		return executeCriteria(criteria);
	}
	
	public SceLoteFornecedor obterLoteFornecedorPorLotCodigoLotMatCodigoLotDtValidade(String lotCodigo,Integer lotMatCodigo, Date dtValidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteFornecedor.class,"lod");
		
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_CODIGO.toString(), lotCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_MAT_CODIGO.toString(), lotMatCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.DATA_VALIDADE.toString(), dtValidade));
		
		return (SceLoteFornecedor)executeCriteriaUniqueResult(criteria);
	}
	
	public SceLoteFornecedor obterLoteFornecedorPorLotCodigoLotMatCodigoLotMcmCodigoDtValidade(String oldLotCodigo,Integer novoLotMatCodigo, Date dtValidade) {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteFornecedor.class,"lod");
		
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_CODIGO.toString(), oldLotCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_MAT_CODIGO.toString(), novoLotMatCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.DATA_VALIDADE.toString(), dtValidade));

		return (SceLoteFornecedor)executeCriteriaUniqueResult(criteria);
	}

	public SceLoteFornecedor obterLoteFornecedorIgualDocPorLoteDtValidadeFornFornEventual(
			SceLote lote, Date dtValidade, ScoFornecedor fornecedor,
			SceFornecedorEventual fornecedorEventual) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteFornecedor.class,"lod");
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOTE.toString(), lote));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.DATA_VALIDADE.toString(), dtValidade));
		if(fornecedor != null){
			criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.FORNECEDOR.toString(), fornecedor));
			criteria.add(Restrictions.isNull(SceLoteFornecedor.Fields.FORNECEDOR_EVENTUAL.toString()));		
		}else if(fornecedorEventual != null){
			criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.FORNECEDOR_EVENTUAL.toString(), fornecedorEventual));	
			criteria.add(Restrictions.isNull(SceLoteFornecedor.Fields.FORNECEDOR.toString()));	
		}
		return (SceLoteFornecedor)executeCriteriaUniqueResult(criteria);
	}
	
	public SceLoteFornecedor obterLoteFornecedorDiferenteDocPorLoteDtValidadeFornFornEventual(
			SceLote lote, Date dtValidade, ScoFornecedor fornecedor,
			SceFornecedorEventual fornecedorEventual) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteFornecedor.class,"lod");
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOTE.toString(), lote));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.DATA_VALIDADE.toString(), dtValidade));
		if(fornecedor != null){
			criteria.add(Restrictions.ne(SceLoteFornecedor.Fields.FORNECEDOR.toString(), fornecedor));
			criteria.add(Restrictions.isNull(SceLoteFornecedor.Fields.FORNECEDOR_EVENTUAL.toString()));		
		}else if(fornecedorEventual != null){
			criteria.add(Restrictions.ne(SceLoteFornecedor.Fields.FORNECEDOR_EVENTUAL.toString(), fornecedorEventual));	
			criteria.add(Restrictions.isNull(SceLoteFornecedor.Fields.FORNECEDOR.toString()));	
		}
		return (SceLoteFornecedor)executeCriteriaUniqueResult(criteria);
	}
	

	public List<SceLoteFornecedor> pesquisarLoteFornecedorPorMaterialValidade(Integer lotMatCodigo, Date dtValidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteFornecedor.class,"lod");
		
		criteria.createAlias(SceLoteFornecedor.Fields.LOTE.toString(), SceLoteFornecedor.Fields.LOTE.toString(),
				JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SceLoteFornecedor.Fields.LOTE.toString() + "." + SceLote.Fields.MARCA_COMERCIAL.toString(),
				SceLote.Fields.MARCA_COMERCIAL.toString(), JoinType.LEFT_OUTER_JOIN);		
		
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_MAT_CODIGO.toString(), lotMatCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.DATA_VALIDADE.toString(), dtValidade));
		
		return executeCriteria(criteria);
	}
	
	public SceLoteFornecedor pesquisarLoteFornecedorPorLoteMarcaMaterialDtValidadeFornecedorEFornEvent(String lotCodigo, Integer lotMcmCodigo, Integer lotMatCodigo, Date dtValidade, Integer fornecedor, Integer fornEventual) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteFornecedor.class,"lod");

		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_MCM_CODIGO.toString(), lotMcmCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.LOT_MAT_CODIGO.toString(), lotMatCodigo));
		criteria.add(Restrictions.eq(SceLoteFornecedor.Fields.DATA_VALIDADE.toString(), dtValidade));

		String sql = "(({alias}.frn_numero is not null and {alias}.frn_numero = ? and {alias}.fev_seq is null)" +
					" or ({alias}.fev_seq is not null and {alias}.fev_seq = ? and  {alias}.frn_numero is null))";

		Object[] valores = {fornecedor, fornEventual};
		Type[] tipos = {IntegerType.INSTANCE, IntegerType.INSTANCE};
		criteria.add(Restrictions.sqlRestriction(sql, valores, tipos));
		
		List<SceLoteFornecedor> lst = executeCriteria(criteria);
		if(lst.size() > 0){
			return lst.get(0);
		}else{
			return null;
		}

	}
	

	public List<SceLoteFornecedor> pesquisarLoteFornecedorPorEalSeqLotDataValidade(SceEstoqueAlmoxarifado estoqueAlmoxarifado, Date dataValidade ) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SceLoteFornecedor.class,"LTF"); //SceLoteFornecedor
		
		criteria.add(Restrictions.eq("LTF."+SceLoteFornecedor.Fields.LOT_MAT_CODIGO.toString(), estoqueAlmoxarifado.getMaterial().getCodigo()));
		criteria.add(Restrictions.eq("LTF."+SceLoteFornecedor.Fields.DATA_VALIDADE.toString(), dataValidade));
		//criteria.add(Restrictions.lt("LTF."+SceLoteFornecedor.Fields.QUANTIDADE_SAIDA.toString(), "LTF."+SceLoteFornecedor.Fields.QUANTIDADE.toString()));
		criteria.addOrder(Order.asc("LTF."+SceLoteFornecedor.Fields.DATA_GERACAO.toString()));
		
		return executeCriteria(criteria);
	}	
	
	
	

}
