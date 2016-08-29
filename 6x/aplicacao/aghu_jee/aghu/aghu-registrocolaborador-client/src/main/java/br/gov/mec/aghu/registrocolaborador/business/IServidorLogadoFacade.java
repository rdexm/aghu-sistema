package br.gov.mec.aghu.registrocolaborador.business;

import java.io.Serializable;

import javax.ejb.Local;

import br.gov.mec.aghu.model.RapServidores;

@Local
public interface IServidorLogadoFacade extends Serializable {
	
	
	RapServidores obterServidorLogado();
	
	RapServidores obterServidorLogadoSemCache();
	
	RapServidores obterServidorPorChavePrimaria(Integer matricula, Short vinCodigo);

}
