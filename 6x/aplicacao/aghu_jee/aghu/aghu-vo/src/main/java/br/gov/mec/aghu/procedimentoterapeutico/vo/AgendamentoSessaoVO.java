package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AgendamentoSessaoVO implements BaseBean {

	private static final long serialVersionUID = 3817489990318316160L;
	
	private Short loaSeq;
	private String descricaoSala;
	private DominioTipoAcomodacao tipoLocal;
	private String descricaoLocal;
	private Date dataInicio;
	private Date dataFim;
	
	public Short getLoaSeq() {
		return loaSeq;
	}

	public void setLoaSeq(Short loaSeq) {
		this.loaSeq = loaSeq;
	}

	public String getDescricaoSala() {
		return descricaoSala;
	}

	public void setDescricaoSala(String descricaoSala) {
		this.descricaoSala = descricaoSala;
	}

	public DominioTipoAcomodacao getTipoLocal() {
		return tipoLocal;
	}

	public void setTipoLocal(DominioTipoAcomodacao tipoLocal) {
		this.tipoLocal = tipoLocal;
	}

	public String getDescricaoLocal() {
		return descricaoLocal;
	}

	public void setDescricaoLocal(String descricaoLocal) {
		this.descricaoLocal = descricaoLocal;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public enum Fields {

		LOA_SEQ("loaSeq"), 
		DESCRICAO_SALA("descricaoSala"),
		TIPO_LOCAL("tipoLocal"),
		DESCRICAO_LOCAL("descricaoLocal"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim");

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
