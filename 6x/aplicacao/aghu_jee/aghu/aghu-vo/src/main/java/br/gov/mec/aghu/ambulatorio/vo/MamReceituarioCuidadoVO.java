package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.estoque.vo.MaterialMDAFVO;

/**
 */
public class MamReceituarioCuidadoVO implements Serializable {

	private static final long serialVersionUID = -4195817103912553934L;

	private Long seq;
	private Boolean impresso;
	private DominioIndPendenteAmbulatorio pendente;
	private Byte nroVias;
	private String texto;
	private Short vinCodigo;
	private Integer matricula;
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;
	private String especialidade;
	private String data;
	private String DataHora;
	private String tituloFormatado;
	private String titulo;
	private String nome;
	private List<MaterialMDAFVO> descricao;
	private String codigoCid;
	private String descricaoCid;
	private String cidFormatado;
	private String complementoEnderecoFormatado;
	private String nomeMedico;
	private String siglaConselho;
	private String numeroRegistroConselho;
	private String assinaturaFormatado;

	
	
	private String enderecoHospital;
	private BigDecimal cepHospital;
	private String cidadeHospital;
	private String ufHospital;
	private String telefoneHospital;
	
	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}
	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}
	public Short getSerVinCodigoValida() {
		return serVinCodigoValida;
	}
	public void setSerVinCodigoValida(Short serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
	}
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public Boolean getImpresso() {
		return impresso;
	}
	public void setImpresso(Boolean impresso) {
		this.impresso = impresso;
	}
	public DominioIndPendenteAmbulatorio getPendente() {
		return pendente;
	}
	public void setPendente(DominioIndPendenteAmbulatorio pendente) {
		this.pendente = pendente;
	}
	public Byte getNroVias() {
		return nroVias;
	}
	public void setNroVias(Byte nroVias) {
		this.nroVias = nroVias;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getTituloFormatado() {
		return tituloFormatado;
	}
	public void setTituloFormatado(String tituloFormatado) {
		this.tituloFormatado = tituloFormatado;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getEnderecoHospital() {
		return enderecoHospital;
	}
	public void setEnderecoHospital(String enderecoHospital) {
		this.enderecoHospital = enderecoHospital;
	}
	public BigDecimal getCepHospital() {
		return cepHospital;
	}
	public void setCepHospital(BigDecimal cepHospital) {
		this.cepHospital = cepHospital;
	}
	public String getCidadeHospital() {
		return cidadeHospital;
	}
	public void setCidadeHospital(String cidadeHospital) {
		this.cidadeHospital = cidadeHospital;
	}
	public String getUfHospital() {
		return ufHospital;
	}
	public void setUfHospital(String ufHospital) {
		this.ufHospital = ufHospital;
	}
	public String getTelefoneHospital() {
		return telefoneHospital;
	}
	public void setTelefoneHospital(String telefoneHospital) {
		this.telefoneHospital = telefoneHospital;
	}
	public List<MaterialMDAFVO> getDescricao() {
		return descricao;
	}
	public void setDescricao(List<MaterialMDAFVO> descricao) {
		this.descricao = descricao;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDataHora() {
		return DataHora;
	}
	public void setDataHora(String dataHora) {
		DataHora = dataHora;
	}
	public String getCodigoCid() {
		return codigoCid;
	}
	public void setCodigoCid(String codigoCid) {
		this.codigoCid = codigoCid;
	}
	public String getDescricaoCid() {
		return descricaoCid;
	}
	public void setDescricaoCid(String descricaoCid) {
		this.descricaoCid = descricaoCid;
	}
	public String getCidFormatado() {
		return cidFormatado;
	}
	public void setCidFormatado(String cidFormatado) {
		this.cidFormatado = cidFormatado;
	}
	public String getComplementoEnderecoFormatado() {
		return complementoEnderecoFormatado;
	}
	public void setComplementoEnderecoFormatado(String complementoEnderecoFormatado) {
		this.complementoEnderecoFormatado = complementoEnderecoFormatado;
	}
	public String getNomeMedico() {
		return nomeMedico;
	}
	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}
	public String getSiglaConselho() {
		return siglaConselho;
	}
	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = siglaConselho;
	}
	public String getNumeroRegistroConselho() {
		return numeroRegistroConselho;
	}
	public void setNumeroRegistroConselho(String numeroRegistroConselho) {
		this.numeroRegistroConselho = numeroRegistroConselho;
	}
	public String getAssinaturaFormatado() {
		return assinaturaFormatado;
	}
	public void setAssinaturaFormatado(String assinaturaFormatado) {
		this.assinaturaFormatado = assinaturaFormatado;
	}

	public enum Fields {
		
		SEQ("seq"),
		IMPRESSO("impresso"),
		NRO_VIAS("nroVias"),
		PACIENTE("paciente"),
		PENDENTE("pendente"),
		SER_MATRICULA_VALIDA("serMatriculaValida"),
		DESCRICAO("descricao"),
		NOME("nome"),
		SER_VIN_CODIGO_VALIDA("serVinCodigoValida");
	

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
				
}