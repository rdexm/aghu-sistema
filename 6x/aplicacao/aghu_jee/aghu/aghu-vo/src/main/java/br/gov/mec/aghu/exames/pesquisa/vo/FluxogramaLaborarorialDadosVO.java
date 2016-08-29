package br.gov.mec.aghu.exames.pesquisa.vo;

import java.util.Date;
import java.util.Map;



public class FluxogramaLaborarorialDadosVO implements java.io.Serializable{

	private static final long serialVersionUID = 3492443181944463696L;
	private String nomeSumario;
	private Integer calSeq;
	private Short ordem;
	private Boolean indDividePorMil;
	private Long valor;
	private Byte quantidadeCasasDecimais;
	private Date dthrProgramada;
	private Date dtNascimento;
	private Integer idade;
	private Date dthrLiberada;
	private Date dthrEvento;
	private String sexo;
	private Integer soeSeq;
	private Short seqp;
	private String codSituacao;
	private Integer ntcSeqp;
	private Map<String, FluxogramaLaborarorialDadosDataValorVO> datasValores;
	
	/*Variáveis para impressão relatório*/
	private String valor0;
	private String valor1;
	private String valor2;
	private String valor3;
	private String valor4;
	private String valor5;
	private String valor6;
	private String valor7;
	private String valor8;
	private String valor9;
	private String valor10;
	private String valor11;
	private String valor12;	

	public String getNomeSumario() {
		return nomeSumario;
	}
	public void setNomeSumario(String nomeSumario) {
		this.nomeSumario = nomeSumario;
	}
	public Integer getCalSeq() {
		return calSeq;
	}
	public void setCalSeq(Integer calSeq) {
		this.calSeq = calSeq;
	}
	public Boolean getIndDividePorMil() {
		return indDividePorMil;
	}
	public void setIndDividePorMil(Boolean indDividePorMil) {
		this.indDividePorMil = indDividePorMil;
	}
	public Long getValor() {
		return valor;
	}
	public void setValor(Long valor) {
		this.valor = valor;
	}
	public Byte getQuantidadeCasasDecimais() {
		return quantidadeCasasDecimais;
	}
	public void setQuantidadeCasasDecimais(Byte quantidadeCasasDecimais) {
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
	}
	public Date getDthrProgramada() {
		return dthrProgramada;
	}
	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public Date getDthrLiberada() {
		return dthrLiberada;
	}
	public void setDthrLiberada(Date dthrLiberada) {
		this.dthrLiberada = dthrLiberada;
	}
	public Date getDthrEvento() {
		return dthrEvento;
	}
	public void setDthrEvento(Date dthrEvento) {
		this.dthrEvento = dthrEvento;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public String getCodSituacao() {
		return codSituacao;
	}
	public void setCodSituacao(String codSituacao) {
		this.codSituacao = codSituacao;
	}
	public Integer getNtcSeqp() {
		return ntcSeqp;
	}
	public void setNtcSeqp(Integer ntcSeqp) {
		this.ntcSeqp = ntcSeqp;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public Map<String, FluxogramaLaborarorialDadosDataValorVO> getDatasValores() {
		return datasValores;
	}
	public void setDatasValores(Map<String, FluxogramaLaborarorialDadosDataValorVO> datasValores) {
		this.datasValores = datasValores;
	}
	public String getValor0() {
		return valor0;
	}
	public void setValor0(String valor0) {
		this.valor0 = valor0;
	}
	public String getValor1() {
		return valor1;
	}
	public void setValor1(String valor1) {
		this.valor1 = valor1;
	}
	public String getValor2() {
		return valor2;
	}
	public void setValor2(String valor2) {
		this.valor2 = valor2;
	}
	public String getValor3() {
		return valor3;
	}
	public void setValor3(String valor3) {
		this.valor3 = valor3;
	}
	public String getValor4() {
		return valor4;
	}
	public void setValor4(String valor4) {
		this.valor4 = valor4;
	}
	public String getValor5() {
		return valor5;
	}
	public void setValor5(String valor5) {
		this.valor5 = valor5;
	}
	public String getValor6() {
		return valor6;
	}
	public void setValor6(String valor6) {
		this.valor6 = valor6;
	}
	public String getValor7() {
		return valor7;
	}
	public void setValor7(String valor7) {
		this.valor7 = valor7;
	}
	public String getValor8() {
		return valor8;
	}
	public void setValor8(String valor8) {
		this.valor8 = valor8;
	}
	public String getValor9() {
		return valor9;
	}
	public void setValor9(String valor9) {
		this.valor9 = valor9;
	}
	public String getValor10() {
		return valor10;
	}
	public void setValor10(String valor10) {
		this.valor10 = valor10;
	}
	public String getValor11() {
		return valor11;
	}
	public void setValor11(String valor11) {
		this.valor11 = valor11;
	}
	public String getValor12() {
		return valor12;
	}
	public void setValor12(String valor12) {
		this.valor12 = valor12;
	}
}