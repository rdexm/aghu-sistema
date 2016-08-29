package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface ManterMaterialBeanLocal {
	
	void manterMaterial(ScoMaterial scoMaterial, String nomeMicrocomputador) throws BaseException;
	
	void manterMaterialSemFlush(ScoMaterial material,String nomeMicrocomputador) throws BaseException;
}
