package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface TransferenciaEventualMaterialBeanLocal {

	public void gravarTransferenciaEventualMaterial(SceTransferencia sceTransferencia) throws BaseException ;
}