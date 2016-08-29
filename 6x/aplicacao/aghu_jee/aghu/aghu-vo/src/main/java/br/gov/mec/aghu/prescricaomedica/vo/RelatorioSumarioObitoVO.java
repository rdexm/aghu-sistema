package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoImpressaoRelatorio;


public class RelatorioSumarioObitoVO implements Serializable, Cloneable  {

	private static final long serialVersionUID = 8095682654010532934L;
	
	//Chave do sumario
	private Integer pAsuAtdSeq;
	private Integer pAsuApaSeq;
	private Integer pAsuSeq;
	
	//Identificação
	private String nomePaciente; 
	private String idade; 
	private String sexo; 
	private Date dataInternacao; 
	private Date dataObito; //
	private String prontuario; 
	private String leito; 
	private String permanencia; 
	private String convenio; 
	private String equipeResponsavel; 
	private String servicoResponsavel;
	private String indetificador;	
	private DominioTipoImpressaoRelatorio tipoImpressao;
	
	// Outra Equipe
	private List<LinhaReportVO> outrasEquipes;
	
	//Diagnóstico
	private List<LinhaReportVO> motivoInternacao;
	private String diagnosticoPrincipalObito;
	private List<LinhaReportVO>  diagnosticosSecundarios;
	
	//Procedimentos Terapeuticos
	private List<LinhaReportVO> cirurgiasRealizadas;
	private List<LinhaReportVO> outrosProcedimentos;
	private List<LinhaReportVO> consultorias;
	private List<LinhaReportVO> principaisFarmacos;
	private List<LinhaReportVO> complementoFarmacos;
	
	//Evolução
	private String evolucao;
	
	//Obito
	private List<LinhaReportVO> obtCausaAntecedentes;
	private String obtCausaDiretas;
	private String obtGravidezAnteriores;
	private String obtNecropsias;
	private List<LinhaReportVO> obtOutrasCausas;
	
	//Dados Relatório
	private Date dthrElaboracaoObito;
	private String nomeSiglaRegistro;

	//Identificação do paciente no rodape
	private String nomeRodape;
	private String localRodape;
	private String prontuarioRodape;
	
	//Indices Details
	private String indiceDiagnostico;
	private String indiceProcedTerapeutico;
	private String indiceEvolucao;
	private String indiceInfObito;
	
	//Campo que delimita as vias do report
	private Integer ordemTela;
	
	// #46268 – Óbito e sumário de óbito – situação de saída do paciente
	private String situacaoSaidaPaciente;
	private String indiceSituacaoSaidaPaciente;
	
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


	public Date getDataObito() {
		return dataObito;
	}


	public void setDataObito(Date dataObito) {
		this.dataObito = dataObito;
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


	public List<LinhaReportVO> getMotivosInternacao() {
		return motivoInternacao;
	}


	public void setMotivosInternacao(List<LinhaReportVO> motivosInternacao) {
		this.motivoInternacao = motivosInternacao;
	}


	public String getDiagnosticoPrincipalObito() {
		return diagnosticoPrincipalObito;
	}
 

	public void setDiagnosticoPrincipalObito(String diagnosticoPrincipalObito) {
		this.diagnosticoPrincipalObito = diagnosticoPrincipalObito;
	}


	public List<LinhaReportVO> getDiagnosticosSecundarios() {
		return diagnosticosSecundarios;
	}


	public void setDiagnosticosSecundarios(List<LinhaReportVO> diagnosticosSecundarios) {
		this.diagnosticosSecundarios = diagnosticosSecundarios;
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
	
	public List<LinhaReportVO> getObtCausaAntecedentes() {
		return obtCausaAntecedentes;
	}


	public void setObtCausaAntecedentes(List<LinhaReportVO> obtCausaAntecedentes) {
		this.obtCausaAntecedentes = obtCausaAntecedentes;
	}


	public String getObtCausaDiretas() {
		return obtCausaDiretas;
	}


	public void setObtCausaDiretas(String obtCausaDiretas) {
		this.obtCausaDiretas = obtCausaDiretas;
	}


	public String getObtGravidezAnteriores() {
		return obtGravidezAnteriores;
	}


	public void setObtGravidezAnteriores(String obtGradivezAnteriores) {
		this.obtGravidezAnteriores = obtGradivezAnteriores;
	}


	public String getObtNecropsias() {
		return obtNecropsias;
	}


	public void setObtNecropsias(String obtNecropsias) {
		this.obtNecropsias = obtNecropsias;
	}


	public List<LinhaReportVO> getObtOutrasCausas() {
		return obtOutrasCausas;
	}


	public void setObtOutrasCausas(List<LinhaReportVO> obtOutrasCausas) {
		this.obtOutrasCausas = obtOutrasCausas;
	}

	public DominioTipoImpressaoRelatorio getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(DominioTipoImpressaoRelatorio tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public List<LinhaReportVO> getMotivoInternacao() {
		return motivoInternacao;
	}

	public void setMotivoInternacao(List<LinhaReportVO> motivoInternacao) {
		this.motivoInternacao = motivoInternacao;
	}

	public Date getDthrElaboracaoObito() {
		return dthrElaboracaoObito;
	}

	public void setDthrElaboracaoObito(Date dthrElaboracaoObito) {
		this.dthrElaboracaoObito = dthrElaboracaoObito;
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

	public String getIndiceInfObito() {
		return indiceInfObito;
	}

	public void setIndiceInfObito(String indiceInfObito) {
		this.indiceInfObito = indiceInfObito;
	}

	public Integer getOrdemTela() {
		return ordemTela;
	}

	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}
	
	public RelatorioSumarioObitoVO copiar() {
		try {
			return (RelatorioSumarioObitoVO) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
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
	
	public String getSituacaoSaidaPaciente() {
		return situacaoSaidaPaciente;
	}
	
	public void setSituacaoSaidaPaciente(String situacaoSaidaPaciente) {
		this.situacaoSaidaPaciente = situacaoSaidaPaciente;
	}
	
	public String getIndiceSituacaoSaidaPaciente() {
		return indiceSituacaoSaidaPaciente;
	}
	
	public void setIndiceSituacaoSaidaPaciente(String indiceSituacaoSaidaPaciente) {
		this.indiceSituacaoSaidaPaciente = indiceSituacaoSaidaPaciente;
	}
}
