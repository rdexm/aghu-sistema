package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;



public class DescricaoCirurgiaVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6508174055826604701L;
	
	private String titulo; 					//F_7
	private String undDescricao;			//F_1
	private Date dataCirurgia;				//F_6
	private String nomePac;					//F_NOME
	private String prontuario;				//F_PRONTUARIO
	private String idade;					//F_IDADE
	private String sexo;					//F_SEXO
	private String leito;					//F_10
	private String convenio;				//F_CONVENIO
	private Boolean labelProjeto;			//Projeto
	private String projeto;					//F_19
	private Boolean labelResp;				//Resp:
	private String nomeResp;				//F_14
	private String nomeEsp;					//F_8
	private List<LinhaReportVO> equipeList; //F_11,F_12,F_13
	private String descricaoAsa;			//F_15
	private List<LinhaReportVO> diagnosticoList;//F_3, //F_16, //F_17, //Destacar
	private Date dtHrInicioCirurgia;		//F_20
	private Date dtHrFimCirurgia;			//F_21
	private String carater;					//F_22
	private List<MbcProcDescricoes> procedimentosList;		//F_23
	//private String indComtaminacao;			//F_32
	private String descricaoAnestesia;		//F_24 
	private String observacao;				//F_25
	private String achadosOperatorios;		//F_26
	private String intercorrenciaClinica;	//F_27
	private String descricaoTecnica;		//F_28
	private String numeroDesenho;
	private Boolean camposDesenho;			//DESENHO
	private String texto;					//F_29
	private byte[] imagem;					//F_30
	private Date dataConclui;				//F_34
	private String responsavel;				//F_35
	private String numeroNotasAdicionais;
	private Boolean camposNotasAdicionais;	//NOTAS ADICIONAIS
	private String notasAdicionais;			//F_18
	private String espProf;					//F_31
	private Boolean camposIdentificacao;
	private Date criadoEm;					//F_38
	private String responsavel1;			//F_39
	private String caminhoLogo;				//F_LOGO1
	private String nomePac1;				//F_PACIENTE
	private String leitoRodape;				//F_LEITO
	private String prontuario1;				//F_2
	private Integer pacCodigo;				//F_9
	private Date currentDate;				//F_current_date3
	private Boolean previa;
	private String perdaSangue;
	
	private String descViaAereas;
	private DominioAsa asa;
	private String avaliacaoClinica;
	private String comorbidades;
	private String exameFisico;
	private String nroRegConselho;	
	private String nome;
	

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setUndDescricao(String unfDescricao) {
		this.undDescricao = unfDescricao;
	}

	public String getUndDescricao() {
		return undDescricao;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}

	public String getNomePac() {
		return nomePac;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getIdade() {
		return idade;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getSexo() {
		return sexo;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getLeito() {
		return leito;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getConvenio() {
		return convenio;
	}

	public void setLabelProjeto(Boolean labelProjeto) {
		this.labelProjeto = labelProjeto;
	}

	public Boolean getLabelProjeto() {
		return labelProjeto;
	}

	public void setProjeto(String projeto) {
		this.projeto = projeto;
	}

	public String getProjeto() {
		return projeto;
	}

	public void setLabelResp(Boolean labelResp) {
		this.labelResp = labelResp;
	}

	public Boolean getLabelResp() {
		return labelResp;
	}

	public void setNomeResp(String nomeResp) {
		this.nomeResp = nomeResp;
	}

	public String getNomeResp() {
		return nomeResp;
	}

	public void setNomeEsp(String nomeEsp) {
		this.nomeEsp = nomeEsp;
	}

	public String getNomeEsp() {
		return nomeEsp;
	}

	public void setDescricaoAsa(String descricaoAsa) {
		this.descricaoAsa = descricaoAsa;
	}

	public String getDescricaoAsa() {
		return descricaoAsa;
	}

	public void setDtHrInicioCirurgia(Date dtHrInicioCirurgia) {
		this.dtHrInicioCirurgia = dtHrInicioCirurgia;
	}

	public Date getDtHrInicioCirurgia() {
		return dtHrInicioCirurgia;
	}

	public void setDtHrFimCirurgia(Date dtHrFimCirurgia) {
		this.dtHrFimCirurgia = dtHrFimCirurgia;
	}

	public Date getDtHrFimCirurgia() {
		return dtHrFimCirurgia;
	}

	public void setCarater(String carater) {
		this.carater = carater;
	}

	public String getCarater() {
		return carater;
	}

	public void setDescricaoAnestesia(String descricaoAnestesia) {
		this.descricaoAnestesia = descricaoAnestesia;
	}

	public String getDescricaoAnestesia() {
		return descricaoAnestesia;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setAchadosOperatorios(String achadosOperatorios) {
		this.achadosOperatorios = achadosOperatorios;
	}

	public String getAchadosOperatorios() {
		return achadosOperatorios;
	}

	public void setIntercorrenciaClinica(String intercorrenciaClinica) {
		this.intercorrenciaClinica = intercorrenciaClinica;
	}

	public String getIntercorrenciaClinica() {
		return intercorrenciaClinica;
	}

	public void setDescricaoTecnica(String descricaoTecnica) {
		this.descricaoTecnica = descricaoTecnica;
	}

	public String getDescricaoTecnica() {
		return descricaoTecnica;
	}

	public void setNumeroDesenho(String numeroDesenho) {
		this.numeroDesenho = numeroDesenho;
	}

	public String getNumeroDesenho() {
		return numeroDesenho;
	}

	public void setCamposDesenho(Boolean camposDesenho) {
		this.camposDesenho = camposDesenho;
	}

	public Boolean getCamposDesenho() {
		return camposDesenho;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getTexto() {
		return texto;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setDataConclui(Date dataConclui) {
		this.dataConclui = dataConclui;
	}

	public Date getDataConclui() {
		return dataConclui;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setNumeroNotasAdicionais(String numeroNotasAdicionais) {
		this.numeroNotasAdicionais = numeroNotasAdicionais;
	}

	public String getNumeroNotasAdicionais() {
		return numeroNotasAdicionais;
	}

	public void setCamposNotasAdicionais(Boolean camposNotasAdicionais) {
		this.camposNotasAdicionais = camposNotasAdicionais;
	}

	public Boolean getCamposNotasAdicionais() {
		return camposNotasAdicionais;
	}

	public void setNotasAdicionais(String notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}

	public String getNotasAdicionais() {
		return notasAdicionais;
	}

	public void setEspProf(String espProf) {
		this.espProf = espProf;
	}

	public String getEspProf() {
		return espProf;
	}

	public void setCamposIdentificacao(Boolean camposIdentificacao) {
		this.camposIdentificacao = camposIdentificacao;
	}

	public Boolean getCamposIdentificacao() {
		return camposIdentificacao;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setResponsavel1(String responsavel1) {
		this.responsavel1 = responsavel1;
	}

	public String getResponsavel1() {
		return responsavel1;
	}

	public void setCaminhoLogo(String caminhoLogo) {
		this.caminhoLogo = caminhoLogo;
	}

	public String getCaminhoLogo() {
		return caminhoLogo;
	}

	public void setNomePac1(String nomePac1) {
		this.nomePac1 = nomePac1;
	}

	public String getNomePac1() {
		return nomePac1;
	}

	public void setLeitoRodape(String leitoRodape) {
		this.leitoRodape = leitoRodape;
	}

	public String getLeitoRodape() {
		return leitoRodape;
	}

	public void setProntuario1(String prontuario1) {
		this.prontuario1 = prontuario1;
	}

	public String getProntuario1() {
		return prontuario1;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setPrevia(Boolean previa) {
		this.previa = previa;
	}

	public Boolean getPrevia() {
		return previa;
	}

	public void setEquipeList(List<LinhaReportVO> equipeList) {
		this.equipeList = equipeList;
	}

	public List<LinhaReportVO> getEquipeList() {
		return equipeList;
	}

	public void setDiagnosticoList(List<LinhaReportVO> diagnosticoList) {
		this.diagnosticoList = diagnosticoList;
	}

	public List<LinhaReportVO> getDiagnosticoList() {
		return diagnosticoList;
	}

	public String getPerdaSangue() {
		return perdaSangue;
	}

	public void setPerdaSangue(String perdaSangue) {
		this.perdaSangue = perdaSangue;
	}

	public List<MbcProcDescricoes> getProcedimentosList() {
		return procedimentosList;
	}

	public void setProcedimentosList(List<MbcProcDescricoes> procedimentosList) {
		this.procedimentosList = procedimentosList;
	}

	public String getDescViaAereas() {
		return descViaAereas;
	}

	public void setDescViaAereas(String descViaAereas) {
		this.descViaAereas = descViaAereas;
	}

	public DominioAsa getAsa() {
		return asa;
	}

	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}

	public String getAvaliacaoClinica() {
		return avaliacaoClinica;
	}

	public void setAvaliacaoClinica(String avaliacaoClinica) {
		this.avaliacaoClinica = avaliacaoClinica;
	}

	public String getComorbidades() {
		return comorbidades;
	}

	public void setComorbidades(String comorbidades) {
		this.comorbidades = comorbidades;
	}

	public String getExameFisico() {
		return exameFisico;
	}

	public void setExameFisico(String exameFisico) {
		this.exameFisico = exameFisico;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
