package br.gov.mec.aghu.estoque.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface MovimentoMaterialBeanLocal {

	public void gravarMovimentoMaterial(SceMovimentoMaterial movimento, String nomeMicrocomputador) throws BaseException;
}