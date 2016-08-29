package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioDestinoAltaMamAltaSumario;
import br.gov.mec.aghu.dominio.DominioRetornoAgenda;
import br.gov.mec.aghu.dominio.DominioSexo;


public class AltaAmbulatorialPolImpressaoVO {
	
	private Long seq;
	private Long alsSeq;
	private Integer conNumero;
	private String conNumeroAlsSeq;
	private String nome;
	private Integer prontuario;
	private Date dtNasc;
	private String dtNascimento;
	private Byte idadeDias;
	private Byte idadeMeses;
	private Short idadeAnos;
	private String idade;
	private DominioSexo sexo;
	private String descSexo;
	private String descAgenda;
	private String descEspecialidade;
	private String descEquipe;
	private DominioDestinoAltaMamAltaSumario destinoAlta;
	private String descDestinoAlta;
	private DominioRetornoAgenda retornoAgenda;
	private String descRetornoAgenda;
	private String descEspDestino;
	private String observacaoDestino;
	private String posto;
	private Integer matricula;
	private Short vinCodigo;
	private String assinatura;
	private Date data;
	private Long totalPrescricoes;	
	
	private List<AltaAmbulatorialPolDiagnosticoVO> altaAmbPolDiagnosticoList;
	private List<AltaAmbulatorialPolEvolucaoVO> altaAmbPolEvolucaoList;
	private List<AltaAmbulatorialPolPrescricaoVO> altaAmbPolPrescricaoList;
	
	
	// Métodos construtores
	public AltaAmbulatorialPolImpressaoVO() {
		
	}
	
	public AltaAmbulatorialPolImpressaoVO(List<AltaAmbulatorialPolDiagnosticoVO> altaAmbPolDiagnosticoList,List<AltaAmbulatorialPolEvolucaoVO> altaAmbPolEvolucaoList,List<AltaAmbulatorialPolPrescricaoVO> altaAmbPolPrescricaoList) {
		this.altaAmbPolDiagnosticoList = altaAmbPolDiagnosticoList;
		this.altaAmbPolEvolucaoList = altaAmbPolEvolucaoList;
		this.altaAmbPolPrescricaoList = altaAmbPolPrescricaoList;
	}
	
	public enum Fields {
		SEQ("seq"),
		ALS_SEQ("alsSeq"),
		CON_NUMERO("conNumero"),
		NOME("nome"),
		PRONTUARIO("prontuario"),
		DT_NASCIMENTO("dtNascimento"),
		DT_NASC("dtNasc"),
		IDADE_DIAS("idadeDias"),
		IDADE_MESES("idadeMeses"),
		IDADE_ANOS("idadeAnos"),
		IDADE("idade"),
		SEXO("sexo"),
		DESC_AGENDA("descAgenda"),
		DESC_ESPECIALIDADE("descEspecialidade"),
		DESC_EQUIPE("descEquipe"),
		DESTINO_ALTA("destinoAlta"),
		RETORNO_AGENDA("retornoAgenda"),
		DESC_ESP_DESTINO("descEspDestino"),
		OBSERVACAO_DESTINO("observacaoDestino"),
		POSTO("posto"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		ASSINATURA("assinatura"),
		DATA("data"),
		TOTAL_PRESCRICOES("totalPrescricoes");
	
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	
	// GETTERS and SETTERS
	
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Long getAlsSeq() {
		return alsSeq;
	}

	public void setAlsSeq(Long alsSeq) {
		this.alsSeq = alsSeq;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(String dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Byte getIdadeDias() {
		return idadeDias;
	}

	public void setIdadeDias(Byte idadeDias) {
		this.idadeDias = idadeDias;
	}

	public Byte getIdadeMeses() {
		return idadeMeses;
	}

	public void setIdadeMeses(Byte idadeMeses) {
		this.idadeMeses = idadeMeses;
	}

	public Short getIdadeAnos() {
		return idadeAnos;
	}

	public void setIdadeAnos(Short idadeAnos) {
		this.idadeAnos = idadeAnos;
	}

	public String getDescEquipe() {
		return descEquipe;
	}

	public void setDescEquipe(String descEquipe) {
		this.descEquipe = descEquipe;
	}

	public DominioDestinoAltaMamAltaSumario getDestinoAlta() {
		return destinoAlta;
	}

	public void setDestinoAlta(DominioDestinoAltaMamAltaSumario destinoAlta) {
		this.destinoAlta = destinoAlta;
	}

	public DominioRetornoAgenda getRetornoAgenda() {
		return retornoAgenda;
	}

	public void setRetornoAgenda(DominioRetornoAgenda retornoAgenda) {
		this.retornoAgenda = retornoAgenda;
	}

	public String getDescEspDestino() {
		return descEspDestino;
	}

	public void setDescEspDestino(String descEspDestino) {
		this.descEspDestino = descEspDestino;
	}

	public String getObservacaoDestino() {
		return observacaoDestino;
	}

	public void setObservacaoDestino(String observacaoDestino) {
		this.observacaoDestino = observacaoDestino;
	}

	public String getPosto() {
		return posto;
	}

	public void setPosto(String posto) {
		this.posto = posto;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getAssinatura() {
		return assinatura;
	}

	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Long getTotalPrescricoes() {
		return totalPrescricoes;
	}

	public void setTotalPrescricoes(Long totalPrescricoes) {
		this.totalPrescricoes = totalPrescricoes;
	}

	public List<AltaAmbulatorialPolDiagnosticoVO> getAltaAmbPolDiagnosticoList() {
		return altaAmbPolDiagnosticoList;
	}

	public void setAltaAmbPolDiagnosticoList(
			List<AltaAmbulatorialPolDiagnosticoVO> altaAmbPolDiagnosticoList) {
		this.altaAmbPolDiagnosticoList = altaAmbPolDiagnosticoList;
	}

	public List<AltaAmbulatorialPolEvolucaoVO> getAltaAmbPolEvolucaoList() {
		return altaAmbPolEvolucaoList;
	}

	public void setAltaAmbPolEvolucaoList(
			List<AltaAmbulatorialPolEvolucaoVO> altaAmbPolEvolucaoList) {
		this.altaAmbPolEvolucaoList = altaAmbPolEvolucaoList;
	}

	public List<AltaAmbulatorialPolPrescricaoVO> getAltaAmbPolPrescricaoList() {
		return altaAmbPolPrescricaoList;
	}

	public void setAltaAmbPolPrescricaoList(
			List<AltaAmbulatorialPolPrescricaoVO> altaAmbPolPrescricaoList) {
		this.altaAmbPolPrescricaoList = altaAmbPolPrescricaoList;
	}

	public String getDescAgenda() {
		return descAgenda;
	}

	public void setDescAgenda(String descAgenda) {
		this.descAgenda = descAgenda;
	}

	public String getDescEspecialidade() {
		return descEspecialidade;
	}

	public void setDescEspecialidade(String descEspecialidade) {
		this.descEspecialidade = descEspecialidade;
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

	public String getDescSexo() {
		return descSexo;
	}

	public void setDescSexo(String descSexo) {
		this.descSexo = descSexo;
	}

	public String getDescDestinoAlta() {
		return descDestinoAlta;
	}

	public void setDescDestinoAlta(String descDestinoAlta) {
		this.descDestinoAlta = descDestinoAlta;
	}

	public String getDescRetornoAgenda() {
		return descRetornoAgenda;
	}

	public void setDescRetornoAgenda(String descRetornoAgenda) {
		this.descRetornoAgenda = descRetornoAgenda;
	}

	public Date getDtNasc() {
		return dtNasc;
	}

	public void setDtNasc(Date dtNasc) {
		this.dtNasc = dtNasc;
	}

	public String getConNumeroAlsSeq() {
		return conNumeroAlsSeq;
	}

	public void setConNumeroAlsSeq(String conNumeroAlsSeq) {
		this.conNumeroAlsSeq = conNumeroAlsSeq;
	}	
	
		
}
