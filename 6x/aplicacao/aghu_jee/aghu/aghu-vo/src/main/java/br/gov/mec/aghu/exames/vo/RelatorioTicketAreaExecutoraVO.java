package br.gov.mec.aghu.exames.vo;

import java.util.Date;



public class RelatorioTicketAreaExecutoraVO {

	private Integer soeSeq;
	private String infClinicas;
	private String criadoEm; //soe
	private String descConvenio;
	private String localizacaoPac;
	private String prontuario;
	private String nome;
	private String idade;   //ex: 74 anos e 6 meses
	private Short seqp;
	private String tipoColeta;
	private String dthrProgramada;
	private Boolean indUso02;
	private String tipoTransporte;
	private String descExame;
	private String indObjSolic;
	private Integer seqoIndObjSolic;
	private Integer codigoPac;
	private String sexo;
	private String leito;
	private String dataNascimento;
	private String descricaoMaterialAnalise;
	private String origem;
	private Short serVinCodigo;
	private Integer serMatricula;
	private String nomeServidorResponsavel;
	private String descUnidadeFuncional;
	private String andar;
	private String ala;
	private String descRegiaoAnatomica;
	private Date dtNascimento;
	private String descricaoMaterial;
	
	public String getAndar() {
		return andar;
	}
	public void setAndar(String andar) {
		this.andar = andar;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public String getInfClinicas() {
		return infClinicas;
	}
	public void setInfClinicas(String infClinicas) {
		this.infClinicas = infClinicas;
	}
	public String getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getDescConvenio() {
		return descConvenio;
	}
	public void setDescConvenio(String descConvenio) {
		this.descConvenio = descConvenio;
	}
	public String getLocalizacaoPac() {
		return localizacaoPac;
	}
	public void setLocalizacaoPac(String localizacaoPac) {
		this.localizacaoPac = localizacaoPac;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getIdade() {
		return idade;
	}
	public void setIdade(String idade) {
		this.idade = idade;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public String getTipoColeta() {
		return tipoColeta;
	}
	public void setTipoColeta(String tipoColeta) {
		this.tipoColeta = tipoColeta;
	}
	public String getDthrProgramada() {
		return dthrProgramada;
	}
	public void setDthrProgramada(String dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}
	public Boolean getIndUso02() {
		return indUso02;
	}
	public void setIndUso02(Boolean indUso02) {
		this.indUso02 = indUso02;
	}
	public String getTipoTransporte() {
		return tipoTransporte;
	}
	public void setTipoTransporte(String tipoTransporte) {
		this.tipoTransporte = tipoTransporte;
	}
	public String getDescExame() {
		return descExame;
	}
	public void setDescExame(String descExame) {
		this.descExame = descExame;
	}
	public String getIndObjSolic() {
		return indObjSolic;
	}
	public void setIndObjSolic(String indObjSolic) {
		this.indObjSolic = indObjSolic;
	}
	public Integer getSeqoIndObjSolic() {
		return seqoIndObjSolic;
	}
	public void setSeqoIndObjSolic(Integer seqoIndObjSolic) {
		this.seqoIndObjSolic = seqoIndObjSolic;
	}
	public Integer getCodigoPac() {
		return codigoPac;
	}
	public void setCodigoPac(Integer codigoPac) {
		this.codigoPac = codigoPac;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getDescricaoMaterialAnalise() {
		return descricaoMaterialAnalise;
	}
	public void setDescricaoMaterialAnalise(String descricaoMaterialAnalise) {
		this.descricaoMaterialAnalise = descricaoMaterialAnalise;
	}
	public String getOrigem() {
		return origem;
	}
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public String getNomeServidorResponsavel() {
		return nomeServidorResponsavel;
	}
	public void setNomeServidorResponsavel(String nomeServidorResponsavel) {
		this.nomeServidorResponsavel = nomeServidorResponsavel;
	}
	public String getDescUnidadeFuncional() {
		return descUnidadeFuncional;
	}
	public void setDescUnidadeFuncional(String descUnidadeFuncional) {
		this.descUnidadeFuncional = descUnidadeFuncional;
	}
	public String getAla() {
		return ala;
	}
	public void setAla(String ala) {
		this.ala = ala;
	}
	public String getDescRegiaoAnatomica() {
		return descRegiaoAnatomica;
	}
	public void setDescRegiaoAnatomica(String descRegiaoAnatomica) {
		this.descRegiaoAnatomica = descRegiaoAnatomica;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	
	
	
}
