package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class AceiteTecnicoPendenteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5737162244329037311L;
	private Long irpSeq;
	private Integer receb;
	private Integer item;
	private Integer status;
	private Date dataUltAtualizacao;
	private Integer seqPtmStatusTicket;
	private Integer seqPtmAreaTecAvaliacao;
	private String nomeAreaTecAvaliacao;
	
	// Dados responsáveis por cada item de recebimento
	
	private Set<ResponsavelAceiteTecnicoPendenteVO> listaResponsaveisItemRecebimento = new HashSet<ResponsavelAceiteTecnicoPendenteVO>();
	
	// Dados responsável área técnica
	
	private ResponsavelAreaTecAceiteTecnicoPendenteVO ResponsavelAreaTecnica = new ResponsavelAreaTecAceiteTecnicoPendenteVO();
	
	
	//aproveitando VO para estória 44297
	
	private Integer matricula;
	private Short vinCodigo;
	private Integer ticSeq;
	
	public enum Fields {
		
		IRP_SEQ("irpSeq"),
		RECEB("receb"),
		ITEM("item"),
		STATUS("status"),
		DATA_ULT_ATUALIZACAO("dataUltAtualizacao"),
		SEQ_PTM_STATUS_TICKET("seqPtmStatusTicket"),
		SEQ_PTM_AREA_TEC_AVALIACAO("seqPtmAreaTecAvaliacao"),
		NOME_AREA_TEC_AVALIACAO("nomeAreaTecAvaliacao"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		TIC_SEQ("ticSeq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getIrpSeq() {
		return irpSeq;
	}

	public void setIrpSeq(Long irpSeq) {
		this.irpSeq = irpSeq;
	}

	public Integer getReceb() {
		return receb;
	}

	public void setReceb(Integer receb) {
		this.receb = receb;
	}

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getDataUltAtualizacao() {
		return dataUltAtualizacao;
	}

	public void setDataUltAtualizacao(Date dataUltAtualizacao) {
		this.dataUltAtualizacao = dataUltAtualizacao;
	}

	public Integer getSeqPtmStatusTicket() {
		return seqPtmStatusTicket;
	}

	public void setSeqPtmStatusTicket(Integer seqPtmStatusTicket) {
		this.seqPtmStatusTicket = seqPtmStatusTicket;
	}

	public Integer getSeqPtmAreaTecAvaliacao() {
		return seqPtmAreaTecAvaliacao;
	}

	public void setSeqPtmAreaTecAvaliacao(Integer seqPtmAreaTecAvaliacao) {
		this.seqPtmAreaTecAvaliacao = seqPtmAreaTecAvaliacao;
	}

	public String getNomeAreaTecAvaliacao() {
		return nomeAreaTecAvaliacao;
	}

	public void setNomeAreaTecAvaliacao(String nomeAreaTecAvaliacao) {
		this.nomeAreaTecAvaliacao = nomeAreaTecAvaliacao;
	}

	public ResponsavelAreaTecAceiteTecnicoPendenteVO getResponsavelAreaTecnica() {
		return ResponsavelAreaTecnica;
	}

	public void setResponsavelAreaTecnica(
			ResponsavelAreaTecAceiteTecnicoPendenteVO responsavelAreaTecnica) {
		ResponsavelAreaTecnica = responsavelAreaTecnica;
	}

	public Set<ResponsavelAceiteTecnicoPendenteVO> getListaResponsaveisItemRecebimento() {
		return listaResponsaveisItemRecebimento;
	}

	public void setListaResponsaveisItemRecebimento(
			Set<ResponsavelAceiteTecnicoPendenteVO> listaResponsaveisItemRecebimento) {
		this.listaResponsaveisItemRecebimento = listaResponsaveisItemRecebimento;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getTicSeq() {
		return ticSeq;
	}

	public void setTicSeq(Integer ticSeq) {
		this.ticSeq = ticSeq;
	}
	

}
