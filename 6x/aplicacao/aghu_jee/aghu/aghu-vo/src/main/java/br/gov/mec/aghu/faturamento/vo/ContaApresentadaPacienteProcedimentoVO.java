package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ContaApresentadaPacienteProcedimentoVO {

	private String especialidade;
	private Short seqEspecialidade;
	private String nome;
	private String procedimento;
	private Integer cthSeq;
	private Integer prontuario;
	private Long codSus;
	private Date dataInternacao;
	private BigDecimal valor;
	private Long nroAIh;
	private String codSusRelatorio;
	private Long quantidade;
	private BigDecimal total;
	private BigDecimal valorSadt;
	private BigDecimal valorServHosp;
	private BigDecimal valorServProf;
	private Integer eaiSeq;
	
	private List<ContaApresentadaPacienteProcedimentoVO> procedimentos;
	
	public ContaApresentadaPacienteProcedimentoVO() {
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public BigDecimal getValorSadt() {
		return valorSadt;
	}

	public BigDecimal getValorServHosp() {
		return valorServHosp;
	}

	public BigDecimal getValorServProf() {
		return valorServProf;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public void setValorSadt(BigDecimal valorSadt) {
		this.valorSadt = valorSadt;
	}

	public void setValorServHosp(BigDecimal valorServHosp) {
		this.valorServHosp = valorServHosp;
	}

	public void setValorServProf(BigDecimal valorServProf) {
		this.valorServProf = valorServProf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Long getCodSus() {
		return codSus;
	}

	public void setCodSus(Long codSus) {
		this.codSus = codSus;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public BigDecimal getValor() {
		if (valor == null) {
			valor	=	BigDecimal.ZERO;
			if (valorSadt != null) {
				valor = valor.add(valorSadt);
			}
			if (valorServHosp != null) {
				valor = valor.add(valorServHosp);
			}
			if (valorServProf != null) {
				valor = valor.add(valorServProf);
			
			}
		}
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Long getNroAIh() {
		return nroAIh;
	}

	public void setNroAIh(Long nroAIh) {
		this.nroAIh = nroAIh;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public String getCodSusRelatorio() {
		return codSusRelatorio == null ? "    " + getCodSus() : codSusRelatorio;
	}

	public void setCodSusRelatorio(String codSusRelatorio) {
		this.codSusRelatorio = codSusRelatorio;
	}

	public Integer getEaiSeq() {
		return eaiSeq;
	}

	public void setEaiSeq(Integer eaiSeq) {
		this.eaiSeq = eaiSeq;
	}

	public BigDecimal getTotal() {
		return total == null ? (BigDecimal.ZERO.add(valorSadt != null ? valorSadt : BigDecimal.ZERO)).
				add(valorServHosp != null ? valorServHosp : BigDecimal.ZERO).add(valorServProf != null ? valorServProf : BigDecimal.ZERO) : total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public String getDataInternacaoFormatada() {
		if (dataInternacao != null) {
			return DateUtil.dataToString(dataInternacao,  "dd/MM/yyyy");
		}
	return null;
	}

	public String getValorFormatado() {
		 return AghuNumberFormat.formatarNumeroMoeda(valor); 
	}

	public String getTotalFormatado() {
		 return AghuNumberFormat.formatarNumeroMoeda(total); 
	}
	
	public List<ContaApresentadaPacienteProcedimentoVO> getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(
			List<ContaApresentadaPacienteProcedimentoVO> procedimentos) {
		this.procedimentos = procedimentos;
	}

	public enum Fields {
		ESPECIALIDADE("especialidade"), ESP_SEQ("seqEspecialidade"), NOME("nome"), PROCEDIMENTO("procedimento"), CTH_SEQ(
				"cthSeq"), QUANTIDADE("quantidade"), VALOR_SADT("valorSadt"), VALOR_SERV_HOSP(
				"valorServHosp"), VALOR_SERV_PROF("valorServProf"), TOTAL("total"), VALOR(
				"valor"), DATA_INTERNACAO("dataInternacao"), COD_SUS("codSus"), PRONTUARIO(
				"prontuario"), NRO_AIH("nroAIh"), EAI_SEQ("eaiSeq");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}