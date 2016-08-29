package br.gov.mec.aghu.procedimentoterapeutico.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoProtocolo;
import br.gov.mec.aghu.core.commons.BaseBean;

public class NovaVersaoProtocoloVO implements BaseBean {

	/**
	 * #44287
	 */
	private static final long serialVersionUID = 7314678386935852737L;
	
	private Integer seqProtocolo;
	private Short tipoSessaoSeq;
	private String titulo;
	private Integer qtdCiclos;
	private DominioSituacaoProtocolo indSituacao;
	
	public enum Fields {
		
		SEQ_PROTOCOLO("seqProtocolo"),
		TP_SESSAO("tipoSessaoSeq"),
		TITULO("titulo"),
		QTD_CICLOS("qtdCiclos"),
		IND_SITUACAO("indSituacao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	//Getters e Setters
	public Integer getSeqProtocolo() {
		return seqProtocolo;
	}

	public void setSeqProtocolo(Integer seqProtocolo) {
		this.seqProtocolo = seqProtocolo;
	}

	public Short getTipoSessaoSeq() {
		return tipoSessaoSeq;
	}

	public void setTipoSessaoSeq(Short tipoSessaoSeq) {
		this.tipoSessaoSeq = tipoSessaoSeq;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Integer getQtdCiclos() {
		return qtdCiclos;
	}

	public void setQtdCiclos(Integer qtdCiclos) {
		this.qtdCiclos = qtdCiclos;
	}

	public DominioSituacaoProtocolo getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoProtocolo indSituacao) {
		this.indSituacao = indSituacao;
	}

}
