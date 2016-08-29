package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.vo.ScoFornecedorVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class CertificadoRegistroCadastroON  extends BaseBusiness{

	private static final long serialVersionUID = 3245097817315170368L;

	public ScoFornecedorVO bindValoresLista(List<ScoFornecedorVO> listVO) {
		ScoFornecedorVO retorno = null;
		if(listVO != null && listVO.size() > 0){
			if(listVO.size() == 1){
				return listVO.get(0);
			} else {
				for(ScoFornecedorVO vo : listVO){
					if(retorno == null){
						retorno = vo;
					} else {
						retorno.setDescricao(retorno.getDescricao()+"\n  "+vo.getDescricao());
					}
				}
			}
		}
		
		return retorno;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return null;
	}	
}
