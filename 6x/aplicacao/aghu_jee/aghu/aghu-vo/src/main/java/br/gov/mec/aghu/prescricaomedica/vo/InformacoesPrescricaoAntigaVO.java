package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.model.RapServidores;

public class InformacoesPrescricaoAntigaVO {

	
	private Integer seq;
	private Date dataHoraFimNPTS;
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;
	private Date dataHoraFimPM;
	private RapServidores servidorValidacao;
	
	public enum Fields {
		SEQ("seq"),
		DATA_HORA_FIM_NPTS("dataHoraFimNPTS"),
		SER_MAT_VALIDA("serMatriculaValida"),
		SER_VIN_COD_VALIDA("serVinCodigoValida"),
		DATA_HORA_FIM_PM("dataHoraFimPM"),
		SERVIDOR_VALIDACAO("servidorValidacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Date getDataHoraFimNPTS() {
		return dataHoraFimNPTS;
	}
	public void setDataHoraFimNPTS(Date dataHoraFimNPTS) {
		this.dataHoraFimNPTS = dataHoraFimNPTS;
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
	public Date getDataHoraFimPM() {
		return dataHoraFimPM;
	}
	public void setDataHoraFimPM(Date dataHoraFimPM) {
		this.dataHoraFimPM = dataHoraFimPM;
	}
	public RapServidores getServidorValidacao() {
		return servidorValidacao;
	}
	public void setServidorValidacao(RapServidores servidorValidacao) {
		this.servidorValidacao = servidorValidacao;
	}
	
}
