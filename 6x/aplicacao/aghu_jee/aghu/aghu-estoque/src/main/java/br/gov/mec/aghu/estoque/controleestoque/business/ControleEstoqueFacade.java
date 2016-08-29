package br.gov.mec.aghu.estoque.controleestoque.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceMovimentoMaterial;


@Modulo(ModuloEnum.ESTOQUE)
@Stateless
public class ControleEstoqueFacade extends BaseFacade implements IControleEstoqueFacade {

@EJB
private IncluirSaldoEstoqueON incluirSaldoEstoqueON;

@Inject
private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1064497882279668227L;

	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.estoque.controleestoque.business.IControleEstoqueFacade#obterAlmoxarifado(java.lang.Short)
	 */
	@Override
	public SceAlmoxarifado obterAlmoxarifado(Short seq) {
		return this.getSceAlmoxarifadoDAO().obterAlmoxarifadoPorSeq(seq);
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.estoque.controleestoque.business.IControleEstoqueFacade#pesquisarAlmoxarifadoMovimentoMaterialPorSeqDescricao(java.lang.String)
	 */
	@Override
	public List<SceAlmoxarifado> pesquisarAlmoxarifadoMovimentoMaterialPorSeqDescricao(String objPesquisa) {
		return this.getSceAlmoxarifadoDAO().pesquisarAlmoxarifadoPorSeqDescricao(objPesquisa);
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.estoque.controleestoque.business.IControleEstoqueFacade#persistirSceMovimentoMaterial(br.gov.mec.aghu.model.SceMovimentoMaterial)
	 */
	@Override
	public void persistirSceMovimentoMaterial(SceMovimentoMaterial movimentoMaterial, String nomeMicrocomputador) 
		throws BaseException {
		
		this.getIncluirSaldoEstoqueON().persistir(movimentoMaterial, nomeMicrocomputador);
	}
	/** FIM #6618 **/
	
	
	/**
	 * get de RNs, ONs e DAOs
	 */
	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO(){
		return sceAlmoxarifadoDAO;
	}

	protected IncluirSaldoEstoqueON getIncluirSaldoEstoqueON() {
		return incluirSaldoEstoqueON;
	}
	
}
