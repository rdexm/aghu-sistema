package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoImpressaoRelatorio;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class RelatorioSumarioTransferenciaVO implements Serializable, Cloneable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8748011919260845591L;
	
	//Chave do sumario
	private Integer pAsuAtdSeq;
	private Integer pAsuApaSeq;
	private Integer pAsuSeq;
	
	//Identificação
	private String nomePaciente; 
	private String idade; 
	private String sexo; 
	private Date dataInternacao; 
	private Date dataTransferencia; //
	private String prontuario; 
	private String leito; 
	private String permanencia; 
	private String convenio; 
	private String equipeResponsavel; 
	private String servicoResponsavel;
	private String indetificador;	
	private DominioTipoImpressaoRelatorio tipoImpressao;
	
	private String equipeDestino;
	private String especialidadeDestino;
	private String unidadeDestino;
	private String instituicaoDestino;
	
	// Outra Equipe
	private List<LinhaReportVO> outrasEquipes;
	
	//Diagnóstico
	private String motivoTransferencia;
	private List<LinhaReportVO>  diagnosticosPrincipais;
	private String diagnosticoSecundario;
	
	//Procedimentos Diagnósticos ou Terapeuticos e Cirurgias
	private List<LinhaReportVO> cirurgiasRealizadas;
	private List<LinhaReportVO> outrosProcedimentos;
	private List<LinhaReportVO> consultorias;
	private List<LinhaReportVO> principaisFarmacos;
	private List<LinhaReportVO> complementoFarmacos;
	
	//Evolução
	private String evolucao;
	
	//Plano Terapeutico
	private String motivoAlta;
	private String planoPosAlta;
	private List<LinhaReportVO> recomendacoes;
	
	
	//Dados Relatório
	private String nomeSiglaRegistro;

	//Identificação do paciente no rodape
	private String nomeRodape;
	private String localRodape;
	private String prontuarioRodape;
	
	//Indices Details
	private String indiceDiagnostico;
	private String indiceProcedTerapeutico;
	private String indiceEvolucao;
	private String indicePlanoTerapeutico;
	
	//Campo que delimita as vias do report
	private Integer ordemTela;
	
	private String destinoConcatenado;
	
	private String planoTerapeuticoConcatenado;
	
	private Date dataRopape;
	
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}


	public String getIdade() {
		return idade;
	}


	public void setIdade(String idade) {
		this.idade = idade;
	}


	public String getSexo() {
		return sexo;
	}


	public void setSexo(String sexo) {
		this.sexo = sexo;
	}


	public Date getDataInternacao() {
		return dataInternacao;
	}


	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}


	public Date getDataTransferencia() {
		return dataTransferencia;
	}


	public void setDataTransferencia(Date dataTransferencia) {
		this.dataTransferencia = dataTransferencia;
	}


	public String getProntuario() {
		return prontuario;
	}


	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}


	public String getLeito() {
		return leito;
	}


	public void setLeito(String leito) {
		this.leito = leito;
	}


	public String getPermanencia() {
		return permanencia;
	}


	public void setPermanencia(String permanencia) {
		this.permanencia = permanencia;
	}


	public String getConvenio() {
		return convenio;
	}


	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}


	public String getEquipeResponsavel() {
		return equipeResponsavel;
	}


	public void setEquipeResponsavel(String equipeResponsavel) {
		this.equipeResponsavel = equipeResponsavel;
	}


	public String getServicoResponsavel() {
		return servicoResponsavel;
	}


	public void setServicoResponsavel(String servicoResponsavel) {
		this.servicoResponsavel = servicoResponsavel;
	}


	public String getIndetificador() {
		return indetificador;
	}


	public void setIndetificador(String indetificador) {
		this.indetificador = indetificador;
	}


	public List<LinhaReportVO> getOutrasEquipes() {
		return outrasEquipes;
	}


	public void setOutrasEquipes(List<LinhaReportVO> outrasEquipes) {
		this.outrasEquipes = outrasEquipes;
	}


	public String getMotivoTransferencia() {
		return motivoTransferencia;
	}

	public void setMotivoTransferencia(String motivoTransferencia) {
		this.motivoTransferencia = motivoTransferencia;
	}

	public String getDiagnosticoSecundario() {
		return diagnosticoSecundario;
	}
 

	public void setDiagnosticoSecundario(String diagnosticoSecundario) {
		this.diagnosticoSecundario = diagnosticoSecundario;
	}


	public List<LinhaReportVO> getDiagnosticosPrincipais() {
		return diagnosticosPrincipais;
	}


	public void setDiagnosticosPrincipais(List<LinhaReportVO> diagnosticosPrincipais) {
		this.diagnosticosPrincipais = diagnosticosPrincipais;
	}



	public List<LinhaReportVO> getOutrosProcedimentos() {
		return outrosProcedimentos;
	}


	public void setOutrosProcedimentos(List<LinhaReportVO> outrosProcedimentos) {
		this.outrosProcedimentos = outrosProcedimentos;
	}


	public List<LinhaReportVO> getConsultorias() {
		return consultorias;
	}


	public void setConsultorias(List<LinhaReportVO> consultorias) {
		this.consultorias = consultorias;
	}


	public List<LinhaReportVO> getPrincipaisFarmacos() {
		return principaisFarmacos;
	}


	public void setPrincipaisFarmacos(List<LinhaReportVO> principaisFarmacos) {
		this.principaisFarmacos = principaisFarmacos;
	}


	public List<LinhaReportVO> getComplementoFarmacos() {
		return complementoFarmacos;
	}


	public void setComplementoFarmacos(List<LinhaReportVO> complementoFarmacos) {
		this.complementoFarmacos = complementoFarmacos;
	}


	public String getEvolucao() {
		return evolucao;
	}


	public void setEvolucao(String evolucao) {
		this.evolucao = evolucao;
	}



	public List<LinhaReportVO> getCirurgiasRealizadas() {
		return cirurgiasRealizadas;
	}


	public void setCirurgiasRealizadas(List<LinhaReportVO> cirurgiasRealizadas) {
		this.cirurgiasRealizadas = cirurgiasRealizadas;
	}
		
	public DominioTipoImpressaoRelatorio getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(DominioTipoImpressaoRelatorio tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public String getNomeSiglaRegistro() {
		return nomeSiglaRegistro;
	}

	public void setNomeSiglaRegistro(String nomeSiglaRegistro) {
		this.nomeSiglaRegistro = nomeSiglaRegistro;
	}

	public String getNomeRodape() {
		return nomeRodape;
	}

	public void setNomeRodape(String nomeRodape) {
		this.nomeRodape = nomeRodape;
	}

	public String getLocalRodape() {
		return localRodape;
	}

	public void setLocalRodape(String localRodape) {
		this.localRodape = localRodape;
	}

	public String getProntuarioRodape() {
		return prontuarioRodape;
	}

	public void setProntuarioRodape(String prontuarioRodape) {
		this.prontuarioRodape = prontuarioRodape;
	}

	public String getIndiceDiagnostico() {
		return indiceDiagnostico;
	}

	public void setIndiceDiagnostico(String indiceDiagnostico) {
		this.indiceDiagnostico = indiceDiagnostico;
	}

	public String getIndiceProcedTerapeutico() {
		return indiceProcedTerapeutico;
	}

	public void setIndiceProcedTerapeutico(String indiceProcedTerapeutico) {
		this.indiceProcedTerapeutico = indiceProcedTerapeutico;
	}

	public String getIndiceEvolucao() {
		return indiceEvolucao;
	}

	public void setIndiceEvolucao(String indiceEvolucao) {
		this.indiceEvolucao = indiceEvolucao;
	}

	public String getIndicePlanoTerapeutico() {
		return indicePlanoTerapeutico;
	}

	public void setIndicePlanoTerapeutico(String indicePlanoTerapeutico) {
		this.indicePlanoTerapeutico = indicePlanoTerapeutico;
	}

	public Integer getOrdemTela() {
		return ordemTela;
	}

	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}
	
	public String getEquipeDestino() {
		return equipeDestino;
	}

	public void setEquipeDestino(String equipeDestino) {
		this.equipeDestino = equipeDestino;
	}

	public String getEspecialidadeDestino() {
		return especialidadeDestino;
	}

	public void setEspecialidadeDestino(String especialidadeDestino) {
		this.especialidadeDestino = especialidadeDestino;
	}

	public String getUnidadeDestino() {
		return unidadeDestino;
	}

	public void setUnidadeDestino(String unidadeDestino) {
		this.unidadeDestino = unidadeDestino;
	}

	public String getInstituicaoDestino() {
		return instituicaoDestino;
	}

	public void setInstituicaoDestino(String instituicaoDestino) {
		this.instituicaoDestino = instituicaoDestino;
	}
	
	public RelatorioSumarioTransferenciaVO copiar() {
		try {
			return (RelatorioSumarioTransferenciaVO) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
	}

	public String getMotivoAlta() {
		return motivoAlta;
	}

	public void setMotivoAlta(String motivoAlta) {
		this.motivoAlta = motivoAlta;
	}

	public String getPlanoPosAlta() {
		return planoPosAlta;
	}

	public void setPlanoPosAlta(String planoPosAlta) {
		this.planoPosAlta = planoPosAlta;
	}

	public List<LinhaReportVO> getRecomendacoes() {
		return recomendacoes;
	}

	public void setRecomendacoes(List<LinhaReportVO> recomendacoes) {
		this.recomendacoes = recomendacoes;
	}

	public String getDestinoConcatenado() {
		return destinoConcatenado;
	}

	public void setDestinoConcatenado(String destinoConcatenado) {
		this.destinoConcatenado = destinoConcatenado;
	}

	public String getPlanoTerapeuticoConcatenado() {
		return planoTerapeuticoConcatenado;
	}

	public void setPlanoTerapeuticoConcatenado(String planoTerapeuticoConcatenado) {
		this.planoTerapeuticoConcatenado = planoTerapeuticoConcatenado;
	}

	public Integer getpAsuAtdSeq() {
		return pAsuAtdSeq;
	}

	public void setpAsuAtdSeq(Integer pAsuAtdSeq) {
		this.pAsuAtdSeq = pAsuAtdSeq;
	}

	public Integer getpAsuApaSeq() {
		return pAsuApaSeq;
	}

	public void setpAsuApaSeq(Integer pAsuApaSeq) {
		this.pAsuApaSeq = pAsuApaSeq;
	}

	public Integer getpAsuSeq() {
		return pAsuSeq;
	}

	public void setpAsuSeq(Integer pAsuSeq) {
		this.pAsuSeq = pAsuSeq;
	}

	public Date getDataRopape() {
		return dataRopape;
	}

	public void setDataRopape(Date dataRopape) {
		this.dataRopape = dataRopape;
	}


	
}

