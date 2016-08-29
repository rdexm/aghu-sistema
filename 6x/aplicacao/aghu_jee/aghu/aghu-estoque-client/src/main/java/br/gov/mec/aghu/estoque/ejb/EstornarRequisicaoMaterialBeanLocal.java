package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface EstornarRequisicaoMaterialBeanLocal {

	void estornarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException;

}
