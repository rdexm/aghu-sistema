package br.gov.mec.aghu.certificacaodigital.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;

public class DocumentosPendentesVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4518004227532345984L;
	private Integer seq;
	private Integer prontuario;
	private String nome;
	private DominioTipoDocumento documento;
	private DominioSituacaoVersaoDocumento situacao;
	private Date criadoEm;
	private String responsavel;
	private byte[] original;
	private Boolean selecionado = Boolean.FALSE;

	public DocumentosPendentesVO() {

	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioTipoDocumento getDocumento() {
		return documento;
	}

	public void setDocumento(DominioTipoDocumento documento) {
		this.documento = documento;
	}

	public DominioSituacaoVersaoDocumento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoVersaoDocumento situacao) {
		this.situacao = situacao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public byte[] getOriginal() {
		return original;
	}

	public void setOriginal(byte[] original) {
		this.original = original;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean equals(Object other) {
		if (!(other instanceof DocumentosPendentesVO)){
			return false;
		}
		DocumentosPendentesVO castOther = (DocumentosPendentesVO) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
