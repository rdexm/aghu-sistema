package br.gov.mec.aghu.protocolos.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;

public class ProtocoloVO implements Serializable {

	private static final long serialVersionUID = -7815275833514772016L;

	private Integer seqProtocoloSessao;
	
	private String tituloProtocoloSessao;
	
	private Integer seqVersaoProtocoloSessao;
	
	private Integer versao;
	
	private DominioSituacaoProtocolo indSituacaoVersaoProtocoloSessao;
	
	private Short seqTipoSessao;
	
	private Boolean repetido;
	
	private Integer codMedicamento;
	
	private Short diasTratamento;

	public Integer getSeqProtocoloSessao() {
		return seqProtocoloSessao;
	}

	public void setSeqProtocoloSessao(Integer seqProtocoloSessao) {
		this.seqProtocoloSessao = seqProtocoloSessao;
	}

	public String getTituloProtocoloSessao() {
		return tituloProtocoloSessao;
	}

	public void setTituloProtocoloSessao(String tituloProtocoloSessao) {
		this.tituloProtocoloSessao = tituloProtocoloSessao;
	}

	public Integer getSeqVersaoProtocoloSessao() {
		return seqVersaoProtocoloSessao;
	}

	public void setSeqVersaoProtocoloSessao(Integer seqVersaoProtocoloSessao) {
		this.seqVersaoProtocoloSessao = seqVersaoProtocoloSessao;
	}

	public Integer getVersao() {
		return versao;
	}

	public void setVersao(Integer versao) {
		this.versao = versao;
	}

	public DominioSituacaoProtocolo getIndSituacaoVersaoProtocoloSessao() {
		return indSituacaoVersaoProtocoloSessao;
	}

	public void setIndSituacaoVersaoProtocoloSessao(
			DominioSituacaoProtocolo indSituacaoVersaoProtocoloSessao) {
		this.indSituacaoVersaoProtocoloSessao = indSituacaoVersaoProtocoloSessao;
	}

	public Short getSeqTipoSessao() {
		return seqTipoSessao;
	}

	public void setSeqTipoSessao(Short seqTipoSessao) {
		this.seqTipoSessao = seqTipoSessao;
	}
	
	public Boolean getRepetido() {
		return repetido;
	}

	public void setRepetido(Boolean repetido) {
		this.repetido = repetido;
	}
	
	public Integer getCodMedicamento() {
		return codMedicamento;
	}

	public void setCodMedicamento(Integer codMedicamento) {
		this.codMedicamento = codMedicamento;
	}

	public Short getDiasTratamento() {
		return diasTratamento;
	}

	public void setDiasTratamento(Short diasTratamento) {
		this.diasTratamento = diasTratamento;
	}

	public enum Fields {	
		SEQ_PROTOCOLO_SESSAO("seqProtocoloSessao"),
		TITULO_PROTOCOLO_SESSAO("tituloProtocoloSessao"),
		SEQ_VERSAO_PROTOCOLO_SESSAO("seqVersaoProtocoloSessao"),
		VERSAO("versao"),
		IND_SITUACAO("indSituacaoVersaoProtocoloSessao"),
		DIAS_TRATAMENTO("diasTratamento");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
}
