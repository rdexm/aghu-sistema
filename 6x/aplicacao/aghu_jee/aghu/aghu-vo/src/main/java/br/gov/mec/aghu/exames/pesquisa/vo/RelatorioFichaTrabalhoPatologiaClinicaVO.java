package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class RelatorioFichaTrabalhoPatologiaClinicaVO implements Serializable {

	private static final long serialVersionUID = 4062266481609587577L;

	private final String QUEBRA_LINHA = "\n";

	private Short unfSeq;
	private Integer soeSeq;
	private String origem;
	private String convenioPlano;
	private Short cspCnvCodigo;
	private Byte cspSeq;
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
	private Boolean recemNascido;
	private Integer atdConNumero;
	private Boolean usaAntimicrobianos;

	private Short amostraSeqP;
	private Short itemSolExSeqP;
	private String nroFrascoFabricante;
	private Date dtNumeroUnico;
	private Integer nroUnico;
	private String nomeUsualMaterial;
	private String descIntervaloColeta;
	private Byte prioridadeExecucao;
	private Byte nroAmostras;
	private DominioTipoColeta tipoColeta;
	private String descMatAnalise;
	private String itemRegiaoAnatomicaDescricao;
	private String regiaoAnatoDescricao;

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
		return this.ala.getCodigo();
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
		return (Boolean.TRUE.equals(usaAntimicrobianos))?"** Paciente em uso de antimicrobianos **" + QUEBRA_LINHA + informacoesClinicas:informacoesClinicas;
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

	public String getTempoIntervaloColetaFormat() {
		return (((tempoIntervaloColeta != null && tempoIntervaloColeta.compareTo(BigDecimal.ZERO) > 0) && (unidTempoIntervaloColeta != null)))?tempoIntervaloColeta.toString() + " " + unidTempoIntervaloColeta:null;
	}
	
	public DominioUnidadeMedidaTempo getUnidTempoIntervaloColeta() {
		return unidTempoIntervaloColeta;
	}

	public void setUnidTempoIntervaloColeta(
			DominioUnidadeMedidaTempo unidTempoIntervaloColeta) {
		this.unidTempoIntervaloColeta = unidTempoIntervaloColeta;
	}

	public Boolean getRecemNascido() {
		return recemNascido;
	}

	public void setRecemNascido(Boolean recemNascido) {
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

	public Byte getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}
	
	public Short getAmostraSeqP() {
		return amostraSeqP;
	}
	
	public void setAmostraSeqP(Short amostraSeqP) {
		this.amostraSeqP = amostraSeqP;
	}

	public Short getItemSolExSeqP() {
		return itemSolExSeqP;
	}

	public void setItemSolExSeqP(Short itemSolExSeqP) {
		this.itemSolExSeqP = itemSolExSeqP;
	}

	public String getNomeUsualMaterial() {
		return nomeUsualMaterial;
	}

	public void setNomeUsualMaterial(String nomeUsualMaterial) {
		this.nomeUsualMaterial = nomeUsualMaterial;
	}

	public String getDescIntervaloColeta() {
		return descIntervaloColeta;
	}

	public void setDescIntervaloColeta(String descIntervaloColeta) {
		this.descIntervaloColeta = descIntervaloColeta;
	}

	public Byte getPrioridadeExecucao() {
		return prioridadeExecucao;
	}

	public void setPrioridadeExecucao(Byte prioridadeExecucao) {
		this.prioridadeExecucao = prioridadeExecucao;
	}

	public DominioTipoColeta getTipoColeta() {
		return tipoColeta;
	}

	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.tipoColeta = tipoColeta;
	}

	public String getTipoColetaDescricao() {
		return (tipoColeta!=null)?tipoColeta.getDescricao():null;
	}

	public String getDescMatAnalise() {
		return (this.descMatAnalise != null)?"DESC. MATERIAL: "+descMatAnalise:null;
	}

	public void setDescMatAnalise(String descMatAnalise) {
		this.descMatAnalise = descMatAnalise;
	}

	public String getDescRegiaoAnatomica() {
		return (this.itemRegiaoAnatomicaDescricao!=null)?"Desc. Região Anatômica: " + this.itemRegiaoAnatomicaDescricao:((this.regiaoAnatoDescricao !=null)?"Desc. Região Anatômica: " + this.regiaoAnatoDescricao:null);
	}

	public String getItemRegiaoAnatomicaDescricao() {
		return itemRegiaoAnatomicaDescricao;
	}

	public void setItemRegiaoAnatomicaDescricao(String itemRegiaoAnatomicaDescricao) {
		this.itemRegiaoAnatomicaDescricao = itemRegiaoAnatomicaDescricao;
	}

	public String getRegiaoAnatoDescricao() {
		return regiaoAnatoDescricao;
	}

	public void setRegiaoAnatoDescricao(String regiaoAnatoDescricao) {
		this.regiaoAnatoDescricao = regiaoAnatoDescricao;
	}

	public String getNroFrascoFabricante() {
		return nroFrascoFabricante;
	}

	public void setNroFrascoFabricante(String nroFrascoFabricante) {
		this.nroFrascoFabricante = nroFrascoFabricante;
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

	public String getNroAmostraVtlEd() {
		return (nroAmostras != null)?nroAmostras.toString() + "ª amostra":"";
	}

	public Byte getNroAmostras() {
		return nroAmostras;
	}

	public void setNroAmostras(Byte nroAmostras) {
		this.nroAmostras = nroAmostras;
	}

	public Boolean getUsaAntimicrobianos() {
		return usaAntimicrobianos;
	}

	public void setUsaAntimicrobianos(Boolean usaAntimicrobianos) {
		this.usaAntimicrobianos = usaAntimicrobianos;
	}
}