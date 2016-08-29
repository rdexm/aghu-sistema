package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class AtosAnestesicosPolVO {
	
	private BigDecimal ficSeq;
	private String origem;
	private String local;
	private String pacProntuario;
	private String pacNome;
	private String pacIdade;
	private String pacSexo;
	private String pacCor;
	private String leito;
	private String carater;
	private BigDecimal sala;
	private String porte;
	private Date inicioAnestesia;
	private Date fimAnestesia;
	private String inicioCirurgia;
	private String fimCirurgia;
	private String duracaoAnestesia;
	private String duracaoCirurgia;
	private String asa;
	private String justAsa;
	private String tempoJejum;
	private String peso;
	private String altura;
	private BigDecimal imc;
	private String superficieCorporal;
	private String condPsicoSensorial;
	private String viaArea;
	private String observacao;
	private String tipoAnestesia;
	private String definicaoMonitorizacao;
	private String farmacos;
	private String tecnicas;
	private String numLinhasVenosas;
	private String condPsicoRecuperacao;
	private String viaAereaRecuperacao;
	private String destinoPaciente;
	private Date dthrValida;
	private String assinatura;
	private BigDecimal gsoPacCodigo;
	private BigDecimal gsoSeqp;
	private BigDecimal totNeonato;
	private BigDecimal totGrandePorte;
	private Date dataAtual;
	
	private List<LinhaReportVO> mbcrFichaEquipe;
	private List<LinhaReportVO> mbcrFichaProcedimento;
	private List<LinhaReportVO> mbcrFichaExame;
	private List<LinhaReportVO> mbcrFichaMedicamentoPre;
	private List<LinhaReportVO> mbcrFichaInducao;
	
	private List<LinhaReportVO> mbcrFichaGrafico;
	private List<LinhaReportVO> mbcrFichaMatrizFarmaco;
	
	private List<LinhaReportVO> mbcrFichaNeonatologia;
	private List<LinhaReportVO> mbcrFichaRegional;
	private List<LinhaReportVO> mbcrFicha5viaVentPos;
	private List<LinhaReportVO> mbcrFichaFluido;
	private List<LinhaReportVO> mbcrFichaEvento;
	private List<LinhaReportVO> mbcrFichaGrandePorte;
	private List<LinhaReportVO> mbcrFichaObstetricia;
	private List<LinhaReportVO> mbcrFichaManutencao;
	
	public AtosAnestesicosPolVO(){}

	@SuppressWarnings("PMD.ExcessiveParameterList")
	public AtosAnestesicosPolVO(BigDecimal ficSeq, String origem, String local,
			String pacProntuario, String pacNome, String pacIdade,
			String pacSexo, String pacCor, String leito, String carater,
			BigDecimal sala, String porte, Date inicioAnestesia,
			Date fimAnestesia, String inicioCirurgia, String fimCirurgia,
			String duracaoAnestesia, String duracaoCirurgia, String asa,
			String justAsa, String tempoJejum, String peso, String altura,
			BigDecimal imc, String superficieCorporal,
			String condPsicoSensorial, String viaArea, String observacao,
			String tipoAnestesia, String definicaoMonitorizacao,
			String farmacos, String tecnicas, String numLinhasVenosas,
			String condPsicoRecuperacao, String viaAereaRecuperacao,
			String destinoPaciente, Date dthrValida, String assinatura,
			BigDecimal gsoPacCodigo, BigDecimal gsoSeqp, BigDecimal totNeonato,
			BigDecimal totGrandePorte, Date dataAtual) {
		super();
		this.ficSeq = ficSeq;
		this.origem = origem;
		this.local = local;
		this.pacProntuario = pacProntuario;
		this.pacNome = pacNome;
		this.pacIdade = pacIdade;
		this.pacSexo = pacSexo;
		this.pacCor = pacCor;
		this.leito = leito;
		this.carater = carater;
		this.sala = sala;
		this.porte = porte;
		this.inicioAnestesia = inicioAnestesia;
		this.fimAnestesia = fimAnestesia;
		this.inicioCirurgia = inicioCirurgia;
		this.fimCirurgia = fimCirurgia;
		this.duracaoAnestesia = duracaoAnestesia;
		this.duracaoCirurgia = duracaoCirurgia;
		this.asa = asa;
		this.justAsa = justAsa;
		this.tempoJejum = tempoJejum;
		this.peso = peso;
		this.altura = altura;
		this.imc = imc;
		this.superficieCorporal = superficieCorporal;
		this.condPsicoSensorial = condPsicoSensorial;
		this.viaArea = viaArea;
		this.observacao = observacao;
		this.tipoAnestesia = tipoAnestesia;
		this.definicaoMonitorizacao = definicaoMonitorizacao;
		this.farmacos = farmacos;
		this.tecnicas = tecnicas;
		this.numLinhasVenosas = numLinhasVenosas;
		this.condPsicoRecuperacao = condPsicoRecuperacao;
		this.viaAereaRecuperacao = viaAereaRecuperacao;
		this.destinoPaciente = destinoPaciente;
		this.dthrValida = dthrValida;
		this.assinatura = assinatura;
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.totNeonato = totNeonato;
		this.totGrandePorte = totGrandePorte;
		this.dataAtual = dataAtual;
	}
	
	public BigDecimal getFicSeq() {
		return ficSeq;
	}
	public String getOrigem() {
		return origem;
	}
	public String getLocal() {
		return local;
	}
	public String getPacProntuario() {
		return pacProntuario;
	}
	public String getPacNome() {
		return pacNome;
	}
	public String getPacIdade() {
		return pacIdade;
	}
	public String getPacSexo() {
		return pacSexo;
	}
	public String getPacCor() {
		return pacCor;
	}
	public String getLeito() {
		return leito;
	}
	public String getCarater() {
		return carater;
	}
	public BigDecimal getSala() {
		return sala;
	}
	public String getPorte() {
		return porte;
	}
	public Date getInicioAnestesia() {
		return inicioAnestesia;
	}
	public Date getFimAnestesia() {
		return fimAnestesia;
	}
	public String getInicioCirurgia() {
		return inicioCirurgia;
	}
	public String getFimCirurgia() {
		return fimCirurgia;
	}
	public String getDuracaoAnestesia() {
		return duracaoAnestesia;
	}
	public String getDuracaoCirurgia() {
		return duracaoCirurgia;
	}
	public String getAsa() {
		return asa;
	}
	public String getJustAsa() {
		return justAsa;
	}
	public String getTempoJejum() {
		return tempoJejum;
	}
	public String getPeso() {
		return peso;
	}
	public String getAltura() {
		return altura;
	}
	public BigDecimal getImc() {
		return imc;
	}
	public String getSuperficieCorporal() {
		return superficieCorporal;
	}
	public String getCondPsicoSensorial() {
		return condPsicoSensorial;
	}
	public String getViaArea() {
		return viaArea;
	}
	public String getObservacao() {
		return observacao;
	}
	public String getTipoAnestesia() {
		return tipoAnestesia;
	}
	public String getDefinicaoMonitorizacao() {
		return definicaoMonitorizacao;
	}
	public String getFarmacos() {
		return farmacos;
	}
	public String getTecnicas() {
		return tecnicas;
	}
	public String getNumLinhasVenosas() {
		return numLinhasVenosas;
	}
	public String getCondPsicoRecuperacao() {
		return condPsicoRecuperacao;
	}
	public String getDestinoPaciente() {
		return destinoPaciente;
	}
	public Date getDthrValida() {
		return dthrValida;
	}
	public String getAssinatura() {
		return assinatura;
	}
	public BigDecimal getGsoPacCodigo() {
		return gsoPacCodigo;
	}
	public BigDecimal getGsoSeqp() {
		return gsoSeqp;
	}
	public BigDecimal getTotNeonato() {
		return totNeonato;
	}
	public BigDecimal getTotGrandePorte() {
		return totGrandePorte;
	}
	public void setFicSeq(BigDecimal ficSeq) {
		this.ficSeq = ficSeq;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public void setPacProntuario(String pacProntuario) {
		this.pacProntuario = pacProntuario;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	public void setPacIdade(String pacIdade) {
		this.pacIdade = pacIdade;
	}
	public void setPacSexo(String pacSexo) {
		this.pacSexo = pacSexo;
	}
	public void setPacCor(String pacCor) {
		this.pacCor = pacCor;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public void setCarater(String carater) {
		this.carater = carater;
	}
	public void setSala(BigDecimal sala) {
		this.sala = sala;
	}
	public void setPorte(String porte) {
		this.porte = porte;
	}
	public void setInicioAnestesia(Date inicioAnestesia) {
		this.inicioAnestesia = inicioAnestesia;
	}
	public void setFimAnestesia(Date fimAnestesia) {
		this.fimAnestesia = fimAnestesia;
	}
	public void setInicioCirurgia(String inicioCirurgia) {
		this.inicioCirurgia = inicioCirurgia;
	}
	public void setFimCirurgia(String fimCirurgia) {
		this.fimCirurgia = fimCirurgia;
	}
	public void setDuracaoAnestesia(String duracaoAnestesia) {
		this.duracaoAnestesia = duracaoAnestesia;
	}
	public void setDuracaoCirurgia(String duracaoCirurgia) {
		this.duracaoCirurgia = duracaoCirurgia;
	}
	public void setAsa(String asa) {
		this.asa = asa;
	}
	public void setJustAsa(String justAsa) {
		this.justAsa = justAsa;
	}
	public void setTempoJejum(String tempoJejum) {
		this.tempoJejum = tempoJejum;
	}
	public void setPeso(String peso) {
		this.peso = peso;
	}
	public void setAltura(String altura) {
		this.altura = altura;
	}
	public void setImc(BigDecimal imc) {
		this.imc = imc;
	}
	public void setSuperficieCorporal(String superficieCorporal) {
		this.superficieCorporal = superficieCorporal;
	}
	public void setCondPsicoSensorial(String condPsicoSensorial) {
		this.condPsicoSensorial = condPsicoSensorial;
	}
	public void setViaArea(String viaArea) {
		this.viaArea = viaArea;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public void setTipoAnestesia(String tipoAnestesia) {
		this.tipoAnestesia = tipoAnestesia;
	}
	public void setDefinicaoMonitorizacao(String definicaoMonitorizacao) {
		this.definicaoMonitorizacao = definicaoMonitorizacao;
	}
	public void setFarmacos(String farmacos) {
		this.farmacos = farmacos;
	}
	public void setTecnicas(String tecnicas) {
		this.tecnicas = tecnicas;
	}
	public void setNumLinhasVenosas(String numLinhasVenosas) {
		this.numLinhasVenosas = numLinhasVenosas;
	}
	public void setCondPsicoRecuperacao(String condPsicoRecuperacao) {
		this.condPsicoRecuperacao = condPsicoRecuperacao;
	}
	public void setDestinoPaciente(String destinoPaciente) {
		this.destinoPaciente = destinoPaciente;
	}
	public void setDthrValida(Date dthrValida) {
		this.dthrValida = dthrValida;
	}
	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}
	public void setGsoPacCodigo(BigDecimal gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}
	public void setGsoSeqp(BigDecimal gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}
	public void setTotNeonato(BigDecimal totNeonato) {
		this.totNeonato = totNeonato;
	}
	public void setTotGrandePorte(BigDecimal totGrandePorte) {
		this.totGrandePorte = totGrandePorte;
	}

	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}

	public Date getDataAtual() {
		return dataAtual;
	}

	public String getViaAereaRecuperacao() {
		return viaAereaRecuperacao;
	}

	public void setViaAereaRecuperacao(String viaAereaRecuperacao) {
		this.viaAereaRecuperacao = viaAereaRecuperacao;
	}

	public void setMbcrFichaEquipe(List<LinhaReportVO> mbcrFichaEquipe) {
		this.mbcrFichaEquipe = mbcrFichaEquipe;
	}

	public List<LinhaReportVO> getMbcrFichaEquipe() {
		return mbcrFichaEquipe;
	}

	public void setMbcrFichaExame(List<LinhaReportVO> mbcrFichaExame) {
		this.mbcrFichaExame = mbcrFichaExame;
	}

	public List<LinhaReportVO> getMbcrFichaExame() {
		return mbcrFichaExame;
	}

	public void setMbcrFichaMedicamentoPre(List<LinhaReportVO> mbcrFichaMedicamentoPre) {
		this.mbcrFichaMedicamentoPre = mbcrFichaMedicamentoPre;
	}

	public List<LinhaReportVO> getMbcrFichaMedicamentoPre() {
		return mbcrFichaMedicamentoPre;
	}

	public void setMbcrFichaInducao(List<LinhaReportVO> mbcrFichaInducao) {
		this.mbcrFichaInducao = mbcrFichaInducao;
	}

	public List<LinhaReportVO> getMbcrFichaInducao() {
		return mbcrFichaInducao;
	}

	public void setMbcrFichaGrafico(List<LinhaReportVO> mbcrFichaGrafico) {
		this.mbcrFichaGrafico = mbcrFichaGrafico;
	}

	public List<LinhaReportVO> getMbcrFichaGrafico() {
		return mbcrFichaGrafico;
	}

	public void setMbcrFichaMatrizFarmaco(List<LinhaReportVO> mbcrFichaMatrizFarmaco) {
		this.mbcrFichaMatrizFarmaco = mbcrFichaMatrizFarmaco;
	}

	public List<LinhaReportVO> getMbcrFichaMatrizFarmaco() {
		return mbcrFichaMatrizFarmaco;
	}

	public void setMbcrFichaNeonatologia(List<LinhaReportVO> mbcrFichaNeonatologia) {
		this.mbcrFichaNeonatologia = mbcrFichaNeonatologia;
	}

	public List<LinhaReportVO> getMbcrFichaNeonatologia() {
		return mbcrFichaNeonatologia;
	}

	public void setMbcrFichaRegional(List<LinhaReportVO> mbcrFichaRegional) {
		this.mbcrFichaRegional = mbcrFichaRegional;
	}

	public List<LinhaReportVO> getMbcrFichaRegional() {
		return mbcrFichaRegional;
	}

	public void setMbcrFicha5viaVentPos(List<LinhaReportVO> mbcrFicha5viaVentPos) {
		this.mbcrFicha5viaVentPos = mbcrFicha5viaVentPos;
	}

	public List<LinhaReportVO> getMbcrFicha5viaVentPos() {
		return mbcrFicha5viaVentPos;
	}

	public void setMbcrFichaFluido(List<LinhaReportVO> mbcrFichaFluido) {
		this.mbcrFichaFluido = mbcrFichaFluido;
	}

	public List<LinhaReportVO> getMbcrFichaFluido() {
		return mbcrFichaFluido;
	}

	public void setMbcrFichaEvento(List<LinhaReportVO> mbcrFichaEvento) {
		this.mbcrFichaEvento = mbcrFichaEvento;
	}

	public List<LinhaReportVO> getMbcrFichaEvento() {
		return mbcrFichaEvento;
	}

	public void setMbcrFichaGrandePorte(List<LinhaReportVO> mbcrFichaGrandePorte) {
		this.mbcrFichaGrandePorte = mbcrFichaGrandePorte;
	}

	public List<LinhaReportVO> getMbcrFichaGrandePorte() {
		return mbcrFichaGrandePorte;
	}

	public void setMbcrFichaObstetricia(List<LinhaReportVO> mbcrFichaObstetricia) {
		this.mbcrFichaObstetricia = mbcrFichaObstetricia;
	}

	public List<LinhaReportVO> getMbcrFichaObstetricia() {
		return mbcrFichaObstetricia;
	}

	public void setMbcrFichaManutencao(List<LinhaReportVO> mbcrFichaManutencao) {
		this.mbcrFichaManutencao = mbcrFichaManutencao;
	}

	public List<LinhaReportVO> getMbcrFichaManutencao() {
		return mbcrFichaManutencao;
	}

	public List<LinhaReportVO> getMbcrFichaProcedimento() {
		return mbcrFichaProcedimento;
	}

	public void setMbcrFichaProcedimento(List<LinhaReportVO> mbcrFichaProcedimento) {
		this.mbcrFichaProcedimento = mbcrFichaProcedimento;
	}

}