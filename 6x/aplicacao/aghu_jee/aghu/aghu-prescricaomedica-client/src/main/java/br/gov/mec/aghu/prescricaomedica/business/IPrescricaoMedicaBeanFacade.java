package br.gov.mec.aghu.prescricaomedica.business;

import java.io.Serializable;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.model.RapServidores;

public interface IPrescricaoMedicaBeanFacade extends Serializable {
    

    void geraDadosSumarioPrescricao(final Integer seqAtendimento,
                    final DominioTipoEmissaoSumario tipoEmissao, RapServidores servidorLogado)
                    throws ApplicationBusinessException;
	
}
