package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.Set;

import br.gov.mec.aghu.dominio.DominioCumpriuJejumColeta;
import br.gov.mec.aghu.dominio.DominioLocalColetaAmostra;

public interface IAelInformacaoColeta {

	DominioCumpriuJejumColeta getCumpriuJejum();

	String getJejumRealizado();

	Boolean getDocumento();

	DominioLocalColetaAmostra getLocalColeta();

	Boolean getInfMenstruacao();

	Date getDtUltMenstruacao();

	Boolean getInfMedicacao();

	Set<? extends IAelInformacaoMdtoColeta> getInformacaoMdtoColetaes();

	String getInformacoesAdicionais();

}
