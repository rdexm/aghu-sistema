package br.gov.mec.aghu.dominio;

import java.util.Arrays;
import java.util.List;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioListaOrigensAtendimentos implements Dominio {
	
	DISPENSACAO_MEDICAMENTOS(Arrays.asList(DominioOrigemAtendimento.I, DominioOrigemAtendimento.U, DominioOrigemAtendimento.H))
	,INTERNACAO(Arrays.asList(DominioOrigemAtendimento.I, DominioOrigemAtendimento.U, DominioOrigemAtendimento.N));
	
	DominioListaOrigensAtendimentos(List<DominioOrigemAtendimento> origensAtendimento){
		this.origensAtendimento = origensAtendimento;
	}
	
	private List<DominioOrigemAtendimento> origensAtendimento;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DISPENSACAO_MEDICAMENTOS:
			return "Lista de Origens de atendimento válidas para a dispensação de medicamentos";
		case INTERNACAO:
			return "Lista de Origens de atendimento para pacientes internados";
		default:
			return "";
		}
	}

	public List<DominioOrigemAtendimento> getOrigensAtendimento() {
		return origensAtendimento;
	}

	public void setOrigensAtendimento(
			List<DominioOrigemAtendimento> origensAtendimento) {
		this.origensAtendimento = origensAtendimento;
	}
	
}
