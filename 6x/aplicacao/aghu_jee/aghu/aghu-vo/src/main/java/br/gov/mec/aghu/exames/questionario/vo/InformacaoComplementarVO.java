package br.gov.mec.aghu.exames.questionario.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSexo;

public class InformacaoComplementarVO implements Serializable {

	/**
	 * 
	 */
    
   	private static final long serialVersionUID = -9183707563949087691L;
	private String identificacao;
	private Integer soeSeq;
	private String solicitacao;
	private String descricaoConvenio;
	private String nomePaciente;
	private Date dtNascimento;
	private Integer idade;
	private DominioSexo sexo;
	private String descricaoCpf;
	private String nomeMae;
	private String naturalidade;
	private BigDecimal cep;
	private String logradouro;
	private Long foneResidencial;
	private String foneRecado;
	private String endereco;
	private Integer prontuario;
	private String unfDescricao;
	private String local;
	private String descricaoExame;
	private String informacoesClinicas;
	private Date data;
	private String nomeServidor;
	private String prioridadeExecucao1;
	private String prioridadeExecucao2;
	private Short seqp;
	private String sigla;
	private Integer manSeq;
	private Integer qtnSeq1;
	private Integer qtnSeq;
	private Integer grupoSeq;
	private String descricaoGrupo;
	private Integer qaoSeq;
	private String qaoDescricao;
	private String descricaoCpfMedico;
	private String codigoProcedimento;
	private String rg;
	private BigInteger nroCartaoSaude;
	private String raca;
	private String responsavel;
	private String grauInstrucao;
	private String resposta;
	private String bairro;
	
	private Long cpfMedico;
	private Integer codTabela;
	private Long cpf;
	private Short unfSeq;
	private String unf2Descricao;
	private Integer pesCodigo;
	private Integer matricula;
	private Short vinCodigo;
	private Integer atdSeq;
	private DominioGrupoConvenio grupoConvenio;
	private String foneResidencialPacComDDD;
	private String foneRecadoPacComDDD;
	
	
	public Integer getPesCodigo() {
		return pesCodigo;
	}

	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}

	
	@SuppressWarnings("PMD.ExcessiveParameterList")
	public InformacaoComplementarVO(String identificacao, Integer soeSeq, String descricaoConvenio, String nomePaciente, Date dtNascimento,
			DominioSexo sexo, String nomeMae, Long foneResidencial,
			String foneRecado,Integer prontuario, String unfDescricao, 
			String descricaoExame, Short seqp, String sigla,
			Integer manSeq, Integer qtnSeq1, Integer qtnSeq,
			Integer grupoSeq, String descricaoGrupo,
			Integer qaoSeq, String qaoDescricao,
			String rg, BigInteger nroCartaoSaude, Short vinCodigo, Integer matricula, 
			Integer atdSeq, DominioGrupoConvenio grupoConvenio, Short unfSeq,
			String unf2Descricao, String informacoesClinicas) {
		super();
		this.identificacao = identificacao;
		this.soeSeq = soeSeq;
		this.descricaoConvenio = descricaoConvenio;
		this.nomePaciente = nomePaciente;
		this.dtNascimento = dtNascimento;
		this.sexo = sexo;
		this.nomeMae = nomeMae;
		this.foneResidencial = foneResidencial;
		this.foneRecado = foneRecado;
		this.prontuario = prontuario;
		this.unfDescricao = unfDescricao;
		this.descricaoExame = descricaoExame;
		this.seqp = seqp;
		this.sigla = sigla;
		this.manSeq = manSeq;
		this.qtnSeq1 = qtnSeq1;
		this.qtnSeq = qtnSeq;
		this.grupoSeq = grupoSeq;
		this.descricaoGrupo = descricaoGrupo;
		this.qaoSeq = qaoSeq;
		this.qaoDescricao = qaoDescricao;
		this.rg = rg;
		this.nroCartaoSaude = nroCartaoSaude;
		this.matricula = matricula;
		this.vinCodigo = vinCodigo;
		this.atdSeq = atdSeq;
		this.grupoConvenio = grupoConvenio;
		this.unfSeq = unfSeq;
		this.unf2Descricao = unf2Descricao;
		this.informacoesClinicas = informacoesClinicas;
	}
	
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public String getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(String solicitacao) {
		this.solicitacao = solicitacao;
	}
	public String getDescricaoConvenio() {
		return descricaoConvenio;
	}
	public void setDescricaoConvenio(String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public Integer getIdade() {
		return idade;
	}
	public void setIdade(Integer idade) {
		this.idade = idade;
	}
	public DominioSexo getSexo() {
		return sexo;
	}
	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}
	public Long getCpf() {
		return cpf;
	}
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	public String getNomeMae() {
		return nomeMae;
	}
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	public String getNaturalidade() {
		return naturalidade;
	}
	public void setNaturalidade(String naturalidade) {
		this.naturalidade = naturalidade;
	}
	public BigDecimal getCep() {
		return cep;
	}
	public void setCep(BigDecimal cep) {
		this.cep = cep;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getUnfDescricao() {
		return unfDescricao;
	}
	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getDescricaoExame() {
		return descricaoExame;
	}
	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}
	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}
	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getNomeServidor() {
		return nomeServidor;
	}
	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}
	public String getPrioridadeExecucao1() {
		return prioridadeExecucao1;
	}
	public void setPrioridadeExecucao1(String prioridadeExecucao1) {
		this.prioridadeExecucao1 = prioridadeExecucao1;
	}
	public String getPrioridadeExecucao2() {
		return prioridadeExecucao2;
	}
	public void setPrioridadeExecucao2(String prioridadeExecucao2) {
		this.prioridadeExecucao2 = prioridadeExecucao2;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public Integer getManSeq() {
		return manSeq;
	}
	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}
	public Integer getQtnSeq1() {
		return qtnSeq1;
	}
	public void setQtnSeq1(Integer qtnSeq1) {
		this.qtnSeq1 = qtnSeq1;
	}
	public Integer getQtnSeq() {
		return qtnSeq;
	}
	public void setQtnSeq(Integer qtnSeq) {
		this.qtnSeq = qtnSeq;
	}
	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}
	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}
	public Integer getQaoSeq() {
		return qaoSeq;
	}
	public void setQaoSeq(Integer qaoSeq) {
		this.qaoSeq = qaoSeq;
	}
	public String getQaoDescricao() {
		return qaoDescricao;
	}
	public void setQaoDescricao(String qaoDescricao) {
		this.qaoDescricao = qaoDescricao;
	}
	public Long getCpfMedico() {
		return cpfMedico;
	}
	public void setCpfMedico(Long cpfMedico) {
		this.cpfMedico = cpfMedico;
	}
	public String getCodigoProcedimento() {
		return codigoProcedimento;
	}
	public void setCodigoProcedimento(String codigoProcedimento) {
		this.codigoProcedimento = codigoProcedimento;
	}
	public Integer getCodTabela() {
		return codTabela;
	}
	public void setCodTabela(Integer codTabela) {
		this.codTabela = codTabela;
	}
	public String getRg() {
		return rg;
	}
	public void setRg(String rg) {
		this.rg = rg;
	}
	public BigInteger getNroCartaoSaude() {
		return nroCartaoSaude;
	}
	public void setNroCartaoSaude(BigInteger nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}
	public String getRaca() {
		return raca;
	}
	public void setRaca(String raca) {
		this.raca = raca;
	}
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}
	public String getGrauInstrucao() {
		return grauInstrucao;
	}
	public void setGrauInstrucao(String grauInstrucao) {
		this.grauInstrucao = grauInstrucao;
	}
	public Long getFoneResidencial() {
		return foneResidencial;
	}
	public void setFoneResidencial(Long foneResidencial) {
		this.foneResidencial = foneResidencial;
	}
	public String getFoneRecado() {
		return foneRecado;
	}
	public void setFoneRecado(String foneRecado) {
		this.foneRecado = foneRecado;
	}
	public Integer getGrupoSeq() {
		return grupoSeq;
	}
	public void setGrupoSeq(Integer grupoSeq) {
		this.grupoSeq = grupoSeq;
	}
	
	public String getIdentificacao() {
		return identificacao;
	}
	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}
	public String getDescricaoCpf() {
		return descricaoCpf;
	}
	public void setDescricaoCpf(String descricaoCpf) {
		this.descricaoCpf = descricaoCpf;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getDescricaoCpfMedico() {
		return descricaoCpfMedico;
	}
	public void setDescricaoCpfMedico(String descricaoCpfMedico) {
		this.descricaoCpfMedico = descricaoCpfMedico;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getUnf2Descricao() {
		return unf2Descricao;
	}

	public void setUnf2Descricao(String unf2Descricao) {
		this.unf2Descricao = unf2Descricao;
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

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public DominioGrupoConvenio getGrupoConvenio() {
		return grupoConvenio;
	}

	public void setGrupoConvenio(DominioGrupoConvenio grupoConvenio) {
		this.grupoConvenio = grupoConvenio;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getFoneResidencialPacComDDD() {
		return foneResidencialPacComDDD;
	}

	public void setFoneResidencialPacComDDD(String foneResidencialPacComDDD) {
		this.foneResidencialPacComDDD = foneResidencialPacComDDD;
	}

	public String getFoneRecadoPacComDDD() {
		return foneRecadoPacComDDD;
	}

	public void setFoneRecadoPacComDDD(String foneRecadoPacComDDD) {
		this.foneRecadoPacComDDD = foneRecadoPacComDDD;
	}
	
	
}
