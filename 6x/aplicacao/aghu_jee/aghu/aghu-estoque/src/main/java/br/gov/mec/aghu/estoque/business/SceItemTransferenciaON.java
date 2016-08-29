package br.gov.mec.aghu.estoque.business;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemTransferenciaDAO;
import br.gov.mec.aghu.estoque.vo.ItemTransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class SceItemTransferenciaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SceItemTransferenciaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemTransferenciaDAO sceItemTransferenciaDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6207771292876096620L;

	/**
	 * ORADB PROCEDURE BUSCA_ESTQ_ALMOX_ORIG
	 * @param almSeq
	 * @param matCodigo
	 * @param frnNumero
	 * @return
	 *  
	 */
	public SceEstoqueAlmoxarifado obterEstoqueAlmoxarifadoOrigem(Short almSeq, Integer matCodigo, Integer frnNumero) throws ApplicationBusinessException {
		
		AghParametros parametroFornecedor = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		
		if (parametroFornecedor != null) {
			
			frnNumero = parametroFornecedor.getVlrNumerico().intValue();
			
		}
		
		return getSceEstoqueAlmoxarifadoDAO().buscarSceEstoqueAlmoxarifadoPorAlmoxarifado(almSeq, matCodigo, frnNumero);
		
	}
	
	
	/**
	 * 
	 * @param seqTransferencia
	 * @return
	 */
	public List<ItemTransferenciaAutomaticaVO> pesquisarListaItensTransferenciaVOPorTransferencia(Integer seqTransferencia) {

		List<SceItemTransferencia> listaItemTransferencia = this.getSceItemTransferenciaDAO().pesquisarListaItensTransferenciaPorTransferencia(seqTransferencia);
		List<ItemTransferenciaAutomaticaVO>  retorno =  new LinkedList<ItemTransferenciaAutomaticaVO>();
		
		if(listaItemTransferencia != null &&  !listaItemTransferencia.isEmpty()){
			
			for (SceItemTransferencia itemTransferencia : listaItemTransferencia) {
				
				ItemTransferenciaAutomaticaVO vo = new ItemTransferenciaAutomaticaVO();
				
				vo.setId(itemTransferencia.getId());
				vo.setTransferencia(itemTransferencia.getTransferencia());
				vo.setQuantidade(itemTransferencia.getQuantidade());
				vo.setQtdeEnviada(itemTransferencia.getQtdeEnviada());
				vo.setEstoqueAlmoxarifadoOrigem(itemTransferencia.getEstoqueAlmoxarifado());
				vo.setEstoqueAlmoxarifado(itemTransferencia.getEstoqueAlmoxarifado());
				
				retorno.add(vo);
				
			}
		}

		return retorno;

	}
	
	protected SceItemTransferenciaDAO getSceItemTransferenciaDAO() {
		return sceItemTransferenciaDAO;
	}
	
	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
}
