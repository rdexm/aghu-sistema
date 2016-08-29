package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class RelatorioFichaTrabalhoPatologiaVO implements Serializable {

	private static final long serialVersionUID = 4062266481609587577L;

	private Short unfSeq;
	private Integer soeSeq;
	private String origem;
	private String convenioPlano;
	private Short cspCnvCodigo;
	private Short cspSeq;
	private Short unfSeqSolicitante;
	private String unfDescricao;
	private String andar;
	private AghAla ala;
	private Date criadoEm;
	private String nomePaciente;
	private String localizacao;
	private Integer matriculaResponsavel;
	private Short vinCodigoResponsavel;
	private String nomeResponsavel;
	private Integer pacCodigo;
	private Integer prontuario;
	private Date dtNascimento;
	private String idade;
	private DominioSexo sexo;
	private String leitoID;
	private String informacoesClinicas;
	private Date dtSolic;
	private AelAmostras amostra;
	private BigDecimal tempoIntervaloColeta;
	private DominioUnidadeMedidaTempo unidTempoIntervaloColeta;
	private String recemNascido;
	private Integer atdConNumero;
	private Short amostraSeqP;
	private Date dtNumeroUnico;
	private Integer nroUnico;

	
	private List<RelatorioFichaTrabalhoPatologiaExameVO> listaExame;
	
	public AghAla getAla() {
		return ala;
	}
	
	public void setAla(AghAla ala) {
		this.ala = ala;
	}
	
	public String getDescAla() {
		return (this.ala != null)?this.ala.getDescricao():null;
	}

	public String getCodigoAla() {
		return (this.ala != null) ? this.ala.getCodigo() : null;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getConvenioPlano() {
		return convenioPlano;
	}

	public void setConvenioPlano(String convenioPlano) {
		this.convenioPlano = convenioPlano;
	}

	public Short getUnfSeqSolicitante() {
		return unfSeqSolicitante;
	}

	public void setUnfSeqSolicitante(Short unfSeqSolicitante) {
		this.unfSeqSolicitante = unfSeqSolicitante;
	}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public String getAndar() {
		return andar;
	}

	public void setAndar(String andar) {
		this.andar = andar;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Integer getMatriculaResponsavel() {
		return matriculaResponsavel;
	}

	public void setMatriculaResponsavel(Integer matriculaResponsavel) {
		this.matriculaResponsavel = matriculaResponsavel;
	}

	public Short getVinCodigoResponsavel() {
		return vinCodigoResponsavel;
	}

	public void setVinCodigoResponsavel(Short vinCodigoResponsavel) {
		this.vinCodigoResponsavel = vinCodigoResponsavel;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getProntuarioFormat() {
		return (this.prontuario!=null)?CoreUtil.formataProntuarioRelatorio(this.prontuario):null;
	}
	
	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}

	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}

	public AelAmostras getAmostra() {
		return amostra;
	}

	public void setAmostra(AelAmostras amostra) {
		this.amostra = amostra;
	}

	public BigDecimal getTempoIntervaloColeta() {
		return tempoIntervaloColeta;
	}

	public void setTempoIntervaloColeta(BigDecimal tempoIntervaloColeta) {
		this.tempoIntervaloColeta = tempoIntervaloColeta;
	}

	public DominioUnidadeMedidaTempo getUnidTempoIntervaloColeta() {
		return unidTempoIntervaloColeta;
	}

	public void setUnidTempoIntervaloColeta(
			DominioUnidadeMedidaTempo unidTempoIntervaloColeta) {
		this.unidTempoIntervaloColeta = unidTempoIntervaloColeta;
	}

	public String getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(String recemNascido) {
		this.recemNascido = recemNascido;
	}

	public Integer getAtdConNumero() {
		return atdConNumero;
	}

	public void setAtdConNumero(Integer atdConNumero) {
		this.atdConNumero = atdConNumero;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Date getDtSolic() {
		return dtSolic;
	}

	public void setDtSolic(Date dtSolic) {
		this.dtSolic = dtSolic;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Short getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Short cspSeq) {
		this.cspSeq = cspSeq;
	}

	public List<RelatorioFichaTrabalhoPatologiaExameVO> getListaExame() {
		return listaExame;
	}

	public void setListaExame(
			List<RelatorioFichaTrabalhoPatologiaExameVO> listaExame) {
		this.listaExame = listaExame;
	}

	public Short getAmostraSeqP() {
		return amostraSeqP;
	}

	public void setAmostraSeqP(Short amostraSeqP) {
		this.amostraSeqP = amostraSeqP;
	}

	public Date getDtNumeroUnico() {
		return dtNumeroUnico;
	}

	public void setDtNumeroUnico(Date dtNumeroUnico) {
		this.dtNumeroUnico = dtNumeroUnico;
	}

	public Integer getNroUnico() {
		return nroUnico;
	}

	public void setNroUnico(Integer nroUnico) {
		this.nroUnico = nroUnico;
	}
	
	public String getTempoIntervaloColetaFormat() {
		return (((tempoIntervaloColeta != null && tempoIntervaloColeta.compareTo(BigDecimal.ZERO) > 0) && (unidTempoIntervaloColeta != null)))?tempoIntervaloColeta.toString() + " " + unidTempoIntervaloColeta:null;
	}

}
