package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class MamRelatorioVO implements Serializable {

	private static final long serialVersionUID = 6078952405130384434L;

	//#43029 c1
	private Long seq;
	private String indPendente;

	//#43029 c2
	private String descricao;
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;
	private String nomePac;
	
	//#43029 P1 CUR_REL
	private String texto = "2";
	private String indImpresso;
	private Short nroVias;
	
	private String nomeMedico;
	private String usuario;
	private String especialidade;
	
	public enum Fields {
		SEQ("seq"),
		IND_PENDENTE("indPendente"),
		DESCRICAO("descricao"),
		SER_MATRICULA_VALIDA("serMatriculaValida"),
		SER_VIN_CODIGO_VALIDA("serVinCodigoValida"),
		NOME_PAC("nomePac"),
		TEXTO("texto"),
		IND_IMPRESSO("indImpresso"),
		NRO_VIAS("nroVias")
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public String getIndPendente() {
		return indPendente;
	}

	public void setIndPendente(String indPendente) {
		this.indPendente = indPendente;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}

	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}

	public Short getSerVinCodigoValida() {
		return serVinCodigoValida;
	}

	public void setSerVinCodigoValida(Short serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
	}

	public String getNomePac() {
		return nomePac;
	}

	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getIndImpresso() {
		return indImpresso;
	}

	public void setIndImpresso(String indImpresso) {
		this.indImpresso = indImpresso;
	}

	public Short getNroVias() {
		return nroVias;
	}

	public void setNroVias(Short nroVias) {
		this.nroVias = nroVias;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}