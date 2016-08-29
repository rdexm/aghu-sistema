package br.gov.mec.aghu.estoque.controleestoque.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IControleEstoqueFacade extends Serializable{

	/** #6618 **/
	public abstract SceAlmoxarifado obterAlmoxarifado(Short seq);

	public abstract List<SceAlmoxarifado> pesquisarAlmoxarifadoMovimentoMaterialPorSeqDescricao(
			String objPesquisa);

	public abstract void persistirSceMovimentoMaterial(
			SceMovimentoMaterial movimentoMaterial, String nomeMicrocomputador) throws BaseException;
	/** FIM #6618 **/

}