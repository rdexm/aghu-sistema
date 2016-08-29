package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class RelExameFisicoRecemNascidoVO {
	
	private String notaCabecalho;			//03
	private String nome;					//04, 46
	private String prontuario;				//05
	private String nascimento;				//06
	private String sexo;					//07
	private String nomeMae;					//08
	private String prontuarioMae;			//09
	private String idadeMae;				//10
	private String alturaMae;				//11
	private Date dtAtendimentoMae;			//12
	private String pesoMae;					//13
	private String convenioMae;				//14
	private String peso;					//15
	private BigDecimal altura;					//16
	private BigDecimal perCefalico;			//17
	private BigDecimal perToraxico;			//18
	private BigDecimal cirAbdominal;		//19
	private String aspectoGeral;			//20
	private String adequacaoPeso;			//21
	private Byte igFinal;					//22
	private String temperatura;				//23
	private Short freqCardiaca;				//24
	private Short freqRespiratoria;			//25
	private String descricao;				//26
	private String particularidades;		//27
	private List<LinhaReportVO> subReport1;  		//28 29 30
	private String moro;					//31
	private String fugaAsfixia;				//32
	private String reptacao;				//33
	private String marcha;					//34
	private String succao;					//35
	private String tempoRetornoMae;			//36
	private String tempoRetornoMin;			//37
	private String crede;					//38
	private String reflexoVermelho;			//39
	private String observacao;				//40
	private String dtHrMovimento;			//41
	private String consProf;				//42
	private String notaAdicional;			//43
	private String criadoEm;				//44
	private String nomeProf;				//45
	//private String nome;					//46				
	private String leito;					//47
	private String prontuarioProvisorio;	//48
	private String prontuarioProvisorioMae;	//49 50
	private Date dataAtual;					//51
	
	private Byte seqp;
	private Integer rnaPacCodigo;
	private Date dataNascimentoMae;
	private Integer sindromeSeq;
	
	public String getNotaCabecalho() {
		return notaCabecalho;
	}
	public void setNotaCabecalho(String notaCabecalho) {
		this.notaCabecalho = notaCabecalho;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNascimento() {
		return nascimento;
	}
	public void setNascimento(String nascimento) {
		this.nascimento = nascimento;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getNomeMae() {
		return nomeMae;
	}
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	public String getProntuarioMae() {
		return prontuarioMae;
	}
	public void setProntuarioMae(String prontuarioMae) {
		this.prontuarioMae = prontuarioMae;
	}
	public String getIdadeMae() {
		return idadeMae;
	}
	public void setIdadeMae(String idadeMae) {
		this.idadeMae = idadeMae;
	}
	public String getAlturaMae() {
		return alturaMae;
	}
	public void setAlturaMae(String alturaMae) {
		this.alturaMae = alturaMae;
	}
	public Date getDtAtendimentoMae() {
		return dtAtendimentoMae;
	}
	public void setDtAtendimentoMae(Date dtAtendimentoMae) {
		this.dtAtendimentoMae = dtAtendimentoMae;
	}
	public String getPesoMae() {
		return pesoMae;
	}
	public void setPesoMae(String pesoMae) {
		this.pesoMae = pesoMae;
	}
	public String getConvenioMae() {
		return convenioMae;
	}
	public void setConvenioMae(String convenioMae) {
		this.convenioMae = convenioMae;
	}
	public String getPeso() {
		return peso;
	}
	public void setPeso(String peso) {
		this.peso = peso;
	}
	public BigDecimal getAltura() {
		return altura;
	}
	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}
	public BigDecimal getPerCefalico() {
		return perCefalico;
	}
	public void setPerCefalico(BigDecimal perCefalico) {
		this.perCefalico = perCefalico;
	}
	public BigDecimal getPerToraxico() {
		return perToraxico;
	}
	public void setPerToraxico(BigDecimal perToraxico) {
		this.perToraxico = perToraxico;
	}
	public BigDecimal getCirAbdominal() {
		return cirAbdominal;
	}
	public void setCirAbdominal(BigDecimal cirAbdominal) {
		this.cirAbdominal = cirAbdominal;
	}
	public String getAspectoGeral() {
		return aspectoGeral;
	}
	public void setAspectoGeral(String aspectoGeral) {
		this.aspectoGeral = aspectoGeral;
	}
	public String getAdequacaoPeso() {
		return adequacaoPeso;
	}
	public void setAdequacaoPeso(String adequacaoPeso) {
		this.adequacaoPeso = adequacaoPeso;
	}
	public Byte getIgFinal() {
		return igFinal;
	}
	public void setIgFinal(Byte igFinal) {
		this.igFinal = igFinal;
	}
	public String getTemperatura() {
		return temperatura;
	}
	public void setTemperatura(String temperatura) {
		this.temperatura = temperatura;
	}
	public Short getFreqCardiaca() {
		return freqCardiaca;
	}
	public void setFreqCardiaca(Short freqCardiaca) {
		this.freqCardiaca = freqCardiaca;
	}
	public Short getFreqRespiratoria() {
		return freqRespiratoria;
	}
	public void setFreqRespiratoria(Short freqRespiratoria) {
		this.freqRespiratoria = freqRespiratoria;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getParticularidades() {
		return particularidades;
	}
	public void setParticularidades(String particularidades) {
		this.particularidades = particularidades;
	}
	public String getMoro() {
		return moro;
	}
	public void setMoro(String moro) {
		this.moro = moro;
	}
	public String getFugaAsfixia() {
		return fugaAsfixia;
	}
	public void setFugaAsfixia(String fugaAsfixia) {
		this.fugaAsfixia = fugaAsfixia;
	}
	public String getReptacao() {
		return reptacao;
	}
	public void setReptacao(String reptacao) {
		this.reptacao = reptacao;
	}
	public String getMarcha() {
		return marcha;
	}
	public void setMarcha(String marcha) {
		this.marcha = marcha;
	}
	public String getSuccao() {
		return succao;
	}
	public void setSuccao(String succao) {
		this.succao = succao;
	}
	public String getTempoRetornoMae() {
		return tempoRetornoMae;
	}
	public void setTempoRetornoMae(String tempoRetornoMae) {
		this.tempoRetornoMae = tempoRetornoMae;
	}
	public String getTempoRetornoMin() {
		return tempoRetornoMin;
	}
	public void setTempoRetornoMin(String tempoRetornoMin) {
		this.tempoRetornoMin = tempoRetornoMin;
	}
	public String getCrede() {
		return crede;
	}
	public void setCrede(String crede) {
		this.crede = crede;
	}
	public String getReflexoVermelho() {
		return reflexoVermelho;
	}
	public void setReflexoVermelho(String reflexoVermelho) {
		this.reflexoVermelho = reflexoVermelho;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getDtHrMovimento() {
		return dtHrMovimento;
	}
	public void setDtHrMovimento(String dtHrMovimento) {
		this.dtHrMovimento = dtHrMovimento;
	}
	public String getConsProf() {
		return consProf;
	}
	public void setConsProf(String consProf) {
		this.consProf = consProf;
	}
	public String getNotaAdicional() {
		return notaAdicional;
	}
	public void setNotaAdicional(String notaAdicional) {
		this.notaAdicional = notaAdicional;
	}
	public String getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getNomeProf() {
		return nomeProf;
	}
	public void setNomeProf(String nomeProf) {
		this.nomeProf = nomeProf;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getProntuarioProvisorio() {
		return prontuarioProvisorio;
	}
	public void setProntuarioProvisorio(String prontuarioProvisorio) {
		this.prontuarioProvisorio = prontuarioProvisorio;
	}
	public String getProntuarioProvisorioMae() {
		return prontuarioProvisorioMae;
	}
	public void setProntuarioProvisorioMae(String prontuarioProvisorioMae) {
		this.prontuarioProvisorioMae = prontuarioProvisorioMae;
	}
	public Date getDataAtual() {
		return dataAtual;
	}
	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}
	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}
	public Byte getSeqp() {
		return seqp;
	}
	public void setRnaPacCodigo(Integer rnaPacCodigo) {
		this.rnaPacCodigo = rnaPacCodigo;
	}
	public Integer getRnaPacCodigo() {
		return rnaPacCodigo;
	}
	public void setDataNascimentoMae(Date dataNascimentoMae) {
		this.dataNascimentoMae = dataNascimentoMae;
	}
	public Date getDataNascimentoMae() {
		return dataNascimentoMae;
	}
	public void setSindromeSeq(Integer sindromeSeq) {
		this.sindromeSeq = sindromeSeq;
	}
	public Integer getSindromeSeq() {
		return sindromeSeq;
	}
	public void setSubReport1(List<LinhaReportVO> subReport1) {
		this.subReport1 = subReport1;
	}
	public List<LinhaReportVO> getSubReport1() {
		return subReport1;
	}
}