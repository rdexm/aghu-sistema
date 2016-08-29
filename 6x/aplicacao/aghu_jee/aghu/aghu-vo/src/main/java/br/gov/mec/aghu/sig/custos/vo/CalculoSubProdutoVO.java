package br.gov.mec.aghu.sig.custos.vo;

import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.transform.ResultTransformer;

import br.gov.mec.aghu.model.SigCalculoComponente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;


public class CalculoSubProdutoVO implements ResultTransformer {
	
	private static final long serialVersionUID = 7422850478952799693L;
	private SigCalculoComponente calculoComponente;
	private SigCalculoObjetoCusto calculoObjetoCustoSubProduto;
	
	public CalculoSubProdutoVO(){
		
	}
	
	public SigCalculoComponente getCalculoComponente() {
		return calculoComponente;
	}
	
	public void setCalculoComponente(SigCalculoComponente calculoComponente) {
		this.calculoComponente = calculoComponente;
	}
	
	public SigCalculoObjetoCusto getCalculoObjetoCustoSubProduto() {
		return calculoObjetoCustoSubProduto;
	}
	
	public void setCalculoObjetoCustoSubProduto(SigCalculoObjetoCusto calculoObjetoCustoSubProduto) {
		this.calculoObjetoCustoSubProduto = calculoObjetoCustoSubProduto;
	}
	
	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		
		CalculoSubProdutoVO vo = new CalculoSubProdutoVO();
		if (tuple[0] != null) {
			vo.setCalculoComponente((SigCalculoComponente) tuple[0]);
		}
		if(tuple[1] != null){
			vo.setCalculoObjetoCustoSubProduto((SigCalculoObjetoCusto) tuple[1]);
		}
		return vo;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List collection) {
		return collection;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof CalculoSubProdutoVO)) {
			return false;
		}
		
		CalculoSubProdutoVO other = (CalculoSubProdutoVO)obj;
		return this.getCalculoComponente().equals(other.getCalculoComponente()) && this.getCalculoObjetoCustoSubProduto().equals(other.getCalculoObjetoCustoSubProduto());
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(calculoComponente).append(calculoObjetoCustoSubProduto).toHashCode();
	}
	
}
