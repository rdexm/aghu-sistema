package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RelatorioMateriaisRecebidosNoDiaVO  implements Serializable{

	private static final long serialVersionUID = -4213654703271052771L;
	
	private Integer soeSeq;
	private Integer prontuario;
	private String leito;
	private String pacNome;
	//Campos formatados
	private Date dthrEventoFormat;
	private String exameMaterial;
	private String origemFormat;
	private String prontuarioFormat;
	
	//Campos auxiliares
	private Short seqP;
	private String indExigeDescricao;
	private String nomeUsual;
	private String descricaoExame; 
	private String materialAnalise;
	private Long numeroAp;
	private Date dthrEvento;
	private String origem;
	
	private String dthrEventoStringFormat;
		
	public Date getDthrEvento() {
		return dthrEvento;
	}
	public void setDthrEvento(Date dthrEvento) {
		this.dthrEvento = dthrEvento;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public String getExameMaterial() {
		return exameMaterial;
	}
	public void setExameMaterial(String exameMaterial) {
		this.exameMaterial = exameMaterial;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getPacNome() {
		return pacNome;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public void setSeqP(Short seqP) {
		this.seqP = seqP;
	}
	public Short getSeqP() {
		return seqP;
	}
	public String getIndExigeDescricao() {
		return indExigeDescricao;
	}
	public void setIndExigeDescricao(String indExigeDescricao) {
		this.indExigeDescricao = indExigeDescricao;
	}
	public String getNomeUsual() {
		return nomeUsual;
	}
	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}
	public String getDescricaoExame() {
		return descricaoExame;
	}
	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}
	public String getMaterialAnalise() {
		return materialAnalise;
	}
	public void setMaterialAnalise(String materialAnalise) {
		this.materialAnalise = materialAnalise;
	}
	public Long getNumeroAp() {
		return numeroAp;
	}
	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	} 
	public Date getDthrEventoFormat() {
		if (dthrEvento != null) {
			Calendar d1 = Calendar.getInstance();
			d1.setTime(dthrEvento);
			d1.set(Calendar.SECOND, 0);
			d1.set(Calendar.MILLISECOND, 0);
			dthrEventoFormat = d1.getTime(); 
			return dthrEventoFormat;
		}
		return null;
	}
	public void setDthrEventoFormat(Date dthrEventoFormat) {
		this.dthrEventoFormat = dthrEventoFormat;
	}
	public String getOrigemFormat() {
		return origemFormat;
	}
	public void setOrigemFormat(String origemFormat) {
		this.origemFormat = origemFormat;
	}
	public String getProntuarioFormat() {
		return prontuarioFormat;
	}
	public void setProntuarioFormat(String prontuarioFormat) {
		this.prontuarioFormat = prontuarioFormat;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		
		umHashCodeBuilder.append(this.getDescricaoExame());
		umHashCodeBuilder.append(this.getIndExigeDescricao());
		umHashCodeBuilder.append(this.getMaterialAnalise());
		umHashCodeBuilder.append(this.getNomeUsual());
		umHashCodeBuilder.append(this.getNumeroAp());
		umHashCodeBuilder.append(this.getOrigem());
		umHashCodeBuilder.append(this.getProntuario());
		umHashCodeBuilder.append(this.getLeito());
		umHashCodeBuilder.append(this.getSeqP());
		umHashCodeBuilder.append(this.getSoeSeq());
		umHashCodeBuilder.append(this.getDthrEventoFormat());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RelatorioMateriaisRecebidosNoDiaVO)) {
			return false;
		}
		RelatorioMateriaisRecebidosNoDiaVO other = (RelatorioMateriaisRecebidosNoDiaVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getDescricaoExame()		, other.getDescricaoExame());
		umEqualsBuilder.append(this.getIndExigeDescricao()	, other.getIndExigeDescricao());
		umEqualsBuilder.append(this.getMaterialAnalise()	, other.getMaterialAnalise());
		umEqualsBuilder.append(this.getNomeUsual()			, other.getNomeUsual());
		umEqualsBuilder.append(this.getNumeroAp()			, other.getNumeroAp());
		umEqualsBuilder.append(this.getOrigem()				, other.getOrigem());
		umEqualsBuilder.append(this.getProntuario()			, other.getProntuario());
		umEqualsBuilder.append(this.getLeito()				, other.getLeito());
		umEqualsBuilder.append(this.getSeqP()				, other.getSeqP());
		umEqualsBuilder.append(this.getSoeSeq()				, other.getSoeSeq());
		umEqualsBuilder.append(this.getDthrEventoFormat()	, other.getDthrEventoFormat());
		return umEqualsBuilder.isEquals();
		
	}
	
public enum Fields {
		
		DATA_HORA_EVENTO("dthrEvento"), 
		SOE_SEQ("soeSeq"),
		SEQP("seqP"),
		ATD_PRONTUARIO("prontuario"),
		PAC_NOME("pacNome"),
		ORIGEM("origem"),
		IND_EXIGE_DESC_MAT_ANLS("indExigeDescricao"),
		NOMEUSUALMATERIAL("nomeUsual"),
		DESCRICAO_EXAME("descricaoExame"),
		DESC_MATERIAL_ANALISE("materialAnalise"),
		NUMERO_AP("numeroAp"),
		DTHR_EVENTO_STRING_FORMAT("dthrEventoStringFormat"),
		LEITO("leito");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public String getDthrEventoStringFormat() {
		return dthrEventoStringFormat;
	}
	public void setDthrEventoStringFormat(String dthrEventoStringFormat) {
		this.dthrEventoStringFormat = dthrEventoStringFormat;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getLeito() {
		return leito;
	}
}
