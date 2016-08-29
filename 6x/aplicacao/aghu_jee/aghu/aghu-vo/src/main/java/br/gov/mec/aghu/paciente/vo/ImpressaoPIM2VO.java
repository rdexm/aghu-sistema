package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioDiagAltoRisco;
import br.gov.mec.aghu.dominio.DominioDiagBaixoRisco;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapQualificacao;

public class ImpressaoPIM2VO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5992066055870779078L;

	private Integer atdSeq;
	private BigDecimal escorePim2;
	private Date dthrIngressoUnidade;
	private DominioSimNao admissaoEletiva;
	private DominioSimNao admissaoRecuperaCirProc;
	private DominioSimNao admissaoPosBypass;
	private DominioDiagAltoRisco diagAltoRisco;
	private DominioDiagBaixoRisco diagBaixoRisco;
	private DominioSimNao faltaRespostaPupilar;
	private DominioSimNao ventilacaoMecanica;
	private String excessoBase;
	private String pao2;
	private BigDecimal fio2;
	private Short pressaoSistolica;
	private String probabilidadeMorte;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date dthrRealizacao;
	private Integer seq;
	private String nome;
	private Integer codigo;
	private String idade;
	private Integer prontuario;
	private DominioSexo sexo;
	private String quarto;
	private List<RapQualificacao> qualificacoesProf;
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public BigDecimal getEscorePim2() {
		return escorePim2;
	}
	public void setEscorePim2(BigDecimal escorePim2) {
		this.escorePim2 = escorePim2;
	}
	public Date getDthrIngressoUnidade() {
		return dthrIngressoUnidade;
	}
	public void setDthrIngressoUnidade(Date dthrIngressoUnidade) {
		this.dthrIngressoUnidade = dthrIngressoUnidade;
	}
	public DominioSimNao getAdmissaoEletiva() {
		return admissaoEletiva;
	}
	public void setAdmissaoEletiva(DominioSimNao admissaoEletiva) {
		this.admissaoEletiva = admissaoEletiva;
	}
	public DominioSimNao getAdmissaoRecuperaCirProc() {
		return admissaoRecuperaCirProc;
	}
	public void setAdmissaoRecuperaCirProc(DominioSimNao admissaoRecuperaCirProc) {
		this.admissaoRecuperaCirProc = admissaoRecuperaCirProc;
	}
	public DominioSimNao getAdmissaoPosBypass() {
		return admissaoPosBypass;
	}
	public void setAdmissaoPosBypass(DominioSimNao admissaoPosBypass) {
		this.admissaoPosBypass = admissaoPosBypass;
	}
	public DominioDiagAltoRisco getDiagAltoRisco() {
		return diagAltoRisco;
	}
	public void setDiagAltoRisco(DominioDiagAltoRisco diagAltoRisco) {
		this.diagAltoRisco = diagAltoRisco;
	}
	public DominioDiagBaixoRisco getDiagBaixoRisco() {
		return diagBaixoRisco;
	}
	public void setDiagBaixoRisco(DominioDiagBaixoRisco diagBaixoRisco) {
		this.diagBaixoRisco = diagBaixoRisco;
	}
	public DominioSimNao getFaltaRespostaPupilar() {
		return faltaRespostaPupilar;
	}
	public void setFaltaRespostaPupilar(DominioSimNao faltaRespostaPupilar) {
		this.faltaRespostaPupilar = faltaRespostaPupilar;
	}
	public DominioSimNao getVentilacaoMecanica() {
		return ventilacaoMecanica;
	}
	public void setVentilacaoMecanica(DominioSimNao ventilacaoMecanica) {
		this.ventilacaoMecanica = ventilacaoMecanica;
	}
	public String getExcessoBase() {
		return excessoBase;
	}
	public void setExcessoBase(String excessoBase) {
		this.excessoBase = excessoBase;
	}
	public String getPao2() {
		return pao2;
	}
	public void setPao2(String pao2) {
		this.pao2 = pao2;
	}
	public BigDecimal getFio2() {
		return fio2;
	}
	public void setFio2(BigDecimal fio2) {
		this.fio2 = fio2;
	}
	public Short getPressaoSistolica() {
		return pressaoSistolica;
	}
	public void setPressaoSistolica(Short pressaoSistolica) {
		this.pressaoSistolica = pressaoSistolica;
	}
	public String getProbabilidadeMorte() {
		return probabilidadeMorte;
	}
	public void setProbabilidadeMorte(String probabilidadeMorte) {
		this.probabilidadeMorte = probabilidadeMorte;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Date getDthrRealizacao() {
		return dthrRealizacao;
	}
	public void setDthrRealizacao(Date dthrRealizacao) {
		this.dthrRealizacao = dthrRealizacao;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getIdade() {
		return idade;
	}
	public void setIdade(String idade) {
		this.idade = idade;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public DominioSexo getSexo() {
		return sexo;
	}
	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}
	public String getQuarto() {
		return quarto;
	}
	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}
	public List<RapQualificacao> getQualificacoesProf() {
		return qualificacoesProf;
	}
	public void setQualificacoesProf(List<RapQualificacao> qualificacoesProf) {
		this.qualificacoesProf = qualificacoesProf;
	}
}
