package br.gov.mec.aghu.dominio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a origem de um atendimento.
 * 
 */
public enum DominioOrigemAtendimento implements Dominio {
	N("Nascimento", true),
	A("Ambulatório", true),
	I("Internação", true),
	U("Urgência", true),
	X("Paciente Externo", true),
	D("Doação de sangue", false), 
	H("Hospital dia", true),
	C("Cirurgia", true),
	T("Todas as origens", false),
	E("Emergência", false);
		
	private String descricao;
	private boolean permitePrescricao;
	
	private DominioOrigemAtendimento(String descricao, boolean permitePrescricao) {
		this.descricao = descricao;
		this.permitePrescricao = permitePrescricao;
	}	

	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		return descricao;
	}
	
	public String getDescricaoAtdTipo() {
		switch (this) {	
		case A:
			return "Consulta";
		default:
			return getDescricao();
		}			
	}

	public boolean permitePrescricao() {
		return permitePrescricao;
	}
	
	
	public static DominioOrigemAtendimento getInstance(String valor) {
		if(valor != null){
			return DominioOrigemAtendimento.valueOf(valor.toUpperCase());
		}
		return null;
	}	
	
	/**
	 * Gap #34801
	 * Retorna as origens de atendimento que fazem parte da prescrição ambulatorial
	 * @return
	 */
	public static List<DominioOrigemAtendimento> getOrigensDePrescricaoAmbulatorial(){
		return Arrays.asList(
					DominioOrigemAtendimento.A,
					DominioOrigemAtendimento.C,
					DominioOrigemAtendimento.X
					);
	}
	
	public static List<DominioOrigemAtendimento> getOrigensPermitemPrescricaoMedica(){
		List<DominioOrigemAtendimento> listaOrigens = new ArrayList<DominioOrigemAtendimento>();
		for(DominioOrigemAtendimento dominio : DominioOrigemAtendimento.values()){
			if(dominio.permitePrescricao){
				listaOrigens.add(dominio);
			}
		}
		return listaOrigens;
	}
	
	public static List<DominioOrigemAtendimento> getOrigensPrescricaoInternacao(){
		return Arrays.asList(
					DominioOrigemAtendimento.I,
					DominioOrigemAtendimento.H,
					DominioOrigemAtendimento.U,
					DominioOrigemAtendimento.N
					);
	}
	
	public static List<DominioOrigemAtendimento> getOrigensAtendimentoInternacaoNascimento(){
		return Arrays.asList(
					DominioOrigemAtendimento.I,
					DominioOrigemAtendimento.N
					);
	}
}
