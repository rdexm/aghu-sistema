package br.gov.mec.aghu.model;

/**
 * 
 * @author cvagheti
 *
 */
public interface IAelSolicitacaoExames {

	AghAtendimentos getAtendimento();

	Integer getSeq();

	FatConvenioSaude getConvenioSaude();

	RapServidores getServidorResponsabilidade();

	AghUnidadesFuncionais getUnidadeFuncional();
	
	Boolean getRecemNascido();
	
	AelAtendimentoDiversos getAtendimentoDiverso();

}
