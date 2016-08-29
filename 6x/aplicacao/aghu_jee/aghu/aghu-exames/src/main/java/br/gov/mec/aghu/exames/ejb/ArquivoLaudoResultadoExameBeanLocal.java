package br.gov.mec.aghu.exames.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface ArquivoLaudoResultadoExameBeanLocal {

	void inserirAelDocResultadoExame(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador) throws BaseException;

	void removerAelDocResultadoExame(AelDocResultadoExame doc, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador) throws BaseException;

}
