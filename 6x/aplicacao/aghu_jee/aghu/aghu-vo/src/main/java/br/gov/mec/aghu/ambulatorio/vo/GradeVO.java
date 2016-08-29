package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class GradeVO implements Serializable {
	private static final String hifen = " - ";

	/**
	 * 
	 */
	private static final long serialVersionUID = -1895083454728018368L;
	
	private Integer seq;
	private Short espSeq;
	private Integer eqpSeq;
	private Boolean indProcedimento;
	private String sigla;
	private Byte sala;
	private String nomeEspecialidade;
	private String nomeEquipe;
	private String nomeProfissional;
	private Integer matriculaProfissional;
	private Short vinculoProfissional;
	private DominioSimNao indProcedimentoString;
	
	public enum Fields {
		SEQ("seq"),
		ESP_SEQ("espSeq"),
		EQP_SEQ("eqpSeq"),
		IND_PROCEDIMENTO("indProcedimento"),
		SIGLA("sigla"),
		SALA("sala"),
		MATRICULA_PROFISSIONAL("matriculaProfissional"),
		VINCULO_PROFISSIONAL("vinculoProfissional"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		NOME_EQUIPE("nomeEquipe"),
		NOME_PROFISSIONAL("nomeProfissional");
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	public Short getEspSeq() {
		return espSeq;
	}
	
	public String getProfissionalFormatado(){
		StringBuilder profissionalFormat = new StringBuilder();
		if(getVinculoProfissional() != null){
			profissionalFormat.append(getVinculoProfissional()+hifen);
		}
		if(getMatriculaProfissional() != null){
			profissionalFormat.append(getMatriculaProfissional()+hifen);
		}
		if(getNomeProfissional() != null){
			if(getNomeProfissional().length() > 16){
				profissionalFormat.append(getNomeProfissional().substring(0, 13).concat("..."));
			}
			else{
				profissionalFormat.append(getNomeProfissional());
			}
		}
		return profissionalFormat.toString();
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Byte getSala() {
		return sala;
	}

	public void setSala(Byte sala) {
		this.sala = sala;
	}

	public Boolean getIndProcedimento() {
		return indProcedimento;
	}

	public void setIndProcedimento(Boolean indProcedimento) {
		this.indProcedimento = indProcedimento;
	}

	public String getNomeEquipe() {
		return nomeEquipe;
	}

	public void setNomeEquipe(String nomeEquipe) {
		this.nomeEquipe = nomeEquipe;
	}

	public String getNomeProfissional() {
		return nomeProfissional;
	}

	public void setNomeProfissional(String nomeProfissional) {
		this.nomeProfissional = nomeProfissional;
	}

	public Integer getEqpSeq() {
		return eqpSeq;
	}

	public void setEqpSeq(Integer eqpSeq) {
		this.eqpSeq = eqpSeq;
	}

	public DominioSimNao getIndProcedimentoString() {
		if(indProcedimento){
			indProcedimentoString = DominioSimNao.S;
		}else{
			indProcedimentoString = DominioSimNao.N;
		}
		return indProcedimentoString;
	}

	public Integer getMatriculaProfissional() {
		return matriculaProfissional;
	}

	public void setMatriculaProfissional(Integer matriculaProfissional) {
		this.matriculaProfissional = matriculaProfissional;
	}

	public Short getVinculoProfissional() {
		return vinculoProfissional;
	}

	public void setVinculoProfissional(Short vinculoProfissional) {
		this.vinculoProfissional = vinculoProfissional;
	}

	public void setIndProcedimentoString(DominioSimNao indProcedimentoString) {
		this.indProcedimentoString = indProcedimentoString;
	}
}