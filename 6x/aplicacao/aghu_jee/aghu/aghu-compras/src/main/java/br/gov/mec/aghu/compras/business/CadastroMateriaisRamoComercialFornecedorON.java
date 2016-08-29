package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoClassifMatNiv5DAO;
import br.gov.mec.aghu.compras.vo.ClassificacaoVO;
import br.gov.mec.aghu.compras.vo.ClassificacaoVOComparator;
import br.gov.mec.aghu.compras.vo.ConsultaClassificacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class CadastroMateriaisRamoComercialFornecedorON extends BaseBusiness{
	
	private static final long serialVersionUID = -2035774151279595685L;
	private static final Log LOG = LogFactory.getLog(CadastroMateriaisRamoComercialFornecedorON.class);
	
	@Inject
	private ScoClassifMatNiv5DAO scoClassifMatNiv5DAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public List<ClassificacaoVO> obterClassificacoes(Object param){
		
		List<ConsultaClassificacaoVO> resultado = scoClassifMatNiv5DAO.pesquisarClassificacoes(param);
		return this.verificarClassificacao(resultado, ClassificacaoVO.Fields.DESCRICAO.toString(), true);
	}
	
	public List<ClassificacaoVO> listarClassificacoes(Integer numero, Short rcmCodigo, String orderProperty, boolean asc) throws BaseException{
		
		List<ConsultaClassificacaoVO> resultado = scoClassifMatNiv5DAO.listarClassificacoes(numero, rcmCodigo);
		return this.verificarClassificacao(resultado, orderProperty, asc);		
	}	
	
	private List<ClassificacaoVO> verificarClassificacao(List<ConsultaClassificacaoVO> resultado, String orderProperty, boolean asc){
		
		List<ClassificacaoVO> listaClassificacoesVO = new ArrayList<ClassificacaoVO>();
		
		for(ConsultaClassificacaoVO consultaClassificacoesVO : resultado){
			
			ClassificacaoVO vo = new ClassificacaoVO();
			
			vo.setCodigo(consultaClassificacoesVO.getCodigo());
			vo.setCodGrupoMaterial(consultaClassificacoesVO.getCodigoGrupoMaterial());
			
			if(consultaClassificacoesVO.getClassificacao5() != 0){
				vo.setDescricao(consultaClassificacoesVO.getDescricao5());
			} else if(consultaClassificacoesVO.getClassificacao4() != 0){
				vo.setDescricao(consultaClassificacoesVO.getDescricao4());
			} else if(consultaClassificacoesVO.getClassificacao3() != 0){
				vo.setDescricao(consultaClassificacoesVO.getDescricao3());
			} else if(consultaClassificacoesVO.getClassificacao2() != 0){
				vo.setDescricao(consultaClassificacoesVO.getDescricao2());
			} else if(consultaClassificacoesVO.getClassificacao1() != 0){
				vo.setDescricao(consultaClassificacoesVO.getDescricao1());
			} else {
				vo.setDescricao(consultaClassificacoesVO.getDescricao0());
			}
			
			listaClassificacoesVO.add(vo);
		}
		
		if(listaClassificacoesVO.size() > 0){
			if(orderProperty.equalsIgnoreCase(ClassificacaoVO.Fields.DESCRICAO.toString())){
				if(asc){
					Collections.sort(listaClassificacoesVO, new ClassificacaoVOComparator.OrderByDescricao());
				} else { 
					Collections.sort(listaClassificacoesVO, Collections.reverseOrder(new ClassificacaoVOComparator.OrderByDescricao()));
				}
			} else {
				if(asc){
					Collections.sort(listaClassificacoesVO, new ClassificacaoVOComparator.OrderByCodigo());
				} else { 
					Collections.sort(listaClassificacoesVO, Collections.reverseOrder(new ClassificacaoVOComparator.OrderByCodigo()));
				}
			} 			
		}
		
		return listaClassificacoesVO;
	}
	
	
}