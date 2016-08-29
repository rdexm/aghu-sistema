package br.gov.mec.aghu.mensageiro;

import java.io.Serializable;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AghJobDetail;

public interface IMensageiroFacade extends Serializable {

	boolean enviarWhatsappDeInternacoesExcedentes(AghJobDetail job) throws ApplicationBusinessException;

}