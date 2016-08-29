package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface GerarRequisicaoMaterialBeanLocal {

	public void gravarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException ;
	
	public void gravarItensRequisicaoMaterial(SceItemRms sceItemRms, String nomeMicrocomputador) throws BaseException;

	public void excluirItemRequisicaoMaterial(SceItemRms sceItemRms, Integer countItensLista, Boolean estorno) throws BaseException;

	public void gravarSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmox, String nomeMicrocomputador) throws BaseException;

}
