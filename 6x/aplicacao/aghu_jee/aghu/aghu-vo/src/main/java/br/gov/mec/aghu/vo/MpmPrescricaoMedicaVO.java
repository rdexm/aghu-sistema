package br.gov.mec.aghu.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class MpmPrescricaoMedicaVO implements BaseBean  {

	private static final long serialVersionUID = -1630392576782832937L;
	
	private String origem;
	private Date dthrInicio;
	private Date dthrFim;
	private Integer pacProntuario;
	private Integer pacCodigo;
	private String pacNome;
	private Integer mpmSeq;
	private String prescricao;
	private Integer prescricaoInt;
	private Long afaSeq;
	private Integer atdSeq;
	private String leito;
	private Boolean indPmNaoEletronica;

	public MpmPrescricaoMedicaVO() {
		
	}
	
	public String getProntuarioNomePaciente(Integer size) {
		String retorno;
		if(pacProntuario != null){
			retorno = formataProntuario(pacProntuario) + " - " + pacNome;
		}else{
			retorno = pacNome;
		}
		if(size != null && size != 0 && retorno.length() > size) {
			retorno = retorno.substring(0, size - 2) + "...";
		}
		return retorno;
	}
	
	public static String formataProntuario(Object valor) {
		if (valor == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder(valor.toString());
		while (sb.length() < 8) {
			sb.insert(0, '0');
		}
		sb.insert(7, '/');
		return sb.toString();
	}
	
	public String getPacProntuarioFormatado() {
		return formataProntuario(this.pacProntuario);
	}		

	public enum Fields {
		ORIGEM("origem"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		PAC_PRONTUARIO("pacProntuario"),
		PAC_CODIGO("pacCodigo"),
		PAC_NOME("pacNome"),
		MPM_SEQ("mpmSeq"),
		AFA_SEQ("afaSeq"),
		ATD_SEQ("atdSeq"),
		LEITO("leito"),
		IND_PM_NAO_ELETRONICA("indPmNaoEletronica"),
		PRESCRICAO("prescricao"),
		PRESCRICAO_INT("prescricaoInt")
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public String getPacNome() {
		return pacNome;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Integer getMpmSeq() {
		return mpmSeq;
	}

	public void setMpmSeq(Integer mpmSeq) {
		this.mpmSeq = mpmSeq;
	}

	public Boolean getIndPmNaoEletronica() {
		return indPmNaoEletronica;
	}

	public void setIndPmNaoEletronica(Boolean indPmNaoEletronica) {
		this.indPmNaoEletronica = indPmNaoEletronica;
	}

	public Long getAfaSeq() {
		return afaSeq;
	}

	public void setAfaSeq(Long afaSeq) {
		this.afaSeq = afaSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getPrescricao() {
		if(getPrescricaoInt() !=null){
			return getPrescricaoInt().toString();
		}
		return prescricao;
	}

	public void setPrescricao(String prescricao) {
		this.prescricao = prescricao;
	}

	public Integer getPrescricaoInt() {
		return prescricaoInt;
	}

	public void setPrescricaoInt(Integer prescricaoInt) {
		this.prescricaoInt = prescricaoInt;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
}
