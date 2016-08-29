package br.gov.mec.aghu.exames.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface AnexarDocumentoLaudoBeanLocal {

	void anexarDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException;

	void removerDocumentoLaudo(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora) throws BaseException;

}
